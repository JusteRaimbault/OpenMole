/*
 * Copyright (C) 2017 Romain Reuillon
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.openmole.core.workflow.tools

import org.openmole.core.context._
import org.openmole.core.expansion._
import org.openmole.core.fileservice._
import org.openmole.core.workflow.domain._
import org.openmole.core.workflow.sampling._
import org.openmole.core.workspace._
import org.openmole.tool.random._
import cats.implicits._

import scala.annotation.tailrec
import org.openmole.core.tools.math._

import scala.reflect.ClassTag

object Scalable {

  object Scaled {
    def toVariable(s: Scaled, values: List[Double], inputSize: Int) =
      s match {
        case s: Scalable.ScaledScalar[_]   ⇒ s.toVariable → values.tail
        case s: Scalable.ScaledSequence[_] ⇒ s.toVariable → values.drop(inputSize)
      }
  }

  sealed trait Scaled

  case class ScaledSequence[T: ClassTag](prototype: Val[Array[T]], s: Array[T]) extends Scaled {
    def toVariable = Variable(prototype, s)
  }
  case class ScaledScalar[T](prototype: Val[T], v: T) extends Scaled {
    def toVariable = Variable(prototype, v)
  }

  object ScalableType {
    implicit def doubleIsScalable = new ScalableType[Double] {
      def scale(v: Double, min: Double, max: Double) = v.scale(min, max)
      def convert(v: Double) = v
    }

    implicit def intIsScalable = new ScalableType[Int] {
      def scale(v: Double, min: Int, max: Int) = v.scale(min.toDouble, max.toDouble + 1).toInt
      def convert(v: Double) = v.toInt
    }

    implicit def longIsScalable = new ScalableType[Long] {
      def scale(v: Double, min: Long, max: Long) = v.scale(min.toDouble, max.toDouble + 1).toLong
      def convert(v: Double) = v.toLong
    }

  }

  trait ScalableType[T] {
    def scale(v: Double, min: T, max: T): T
    def convert(v: Double): T
  }

  implicit def factorOfDoubleIsScalable[D, T: ScalableType](implicit bounded: BoundsFromContext[D, T]) = new Scalable[Factor[D, T]] {
    def isScalar(t: Factor[D, T]) = true
    override def inputs(t: Factor[D, T]) = Seq()
    override def prototype(t: Factor[D, T]): Val[_] = t.value
    override def size(t: Factor[D, T]): FromContext[Int] = 1

    override def unflatten(s: Factor[D, T])(values: Seq[Double], scale: Boolean): FromContext[Scaled] = {
      val g = values.head
      assert(!g.isNaN)

      (bounded.min(s.domain) map2 bounded.max(s.domain)) { (min, max) ⇒
        val sc = if (scale) implicitly[ScalableType[T]].scale(g, min, max) else implicitly[ScalableType[T]].convert(g)
        ScaledScalar(s.value, sc)
      }
    }
  }

  implicit def factorOfSequenceIsScalable[D, T: ScalableType: ClassTag](implicit bounded: BoundsFromContext[D, Array[T]]) = new Scalable[Factor[D, Array[T]]] {

    def isScalar(t: Factor[D, Array[T]]) = false
    override def inputs(t: Factor[D, Array[T]]) = Seq()
    override def prototype(t: Factor[D, Array[T]]): Val[_] = t.value

    override def size(t: Factor[D, Array[T]]): FromContext[Int] =
      (bounded.min(t.domain) map2 bounded.max(t.domain)) { case (min, max) ⇒ math.min(min.size, max.size) }

    override def unflatten(t: Factor[D, Array[T]])(values: Seq[Double], scale: Boolean): FromContext[Scaled] = {

      def scaled =
        (bounded.min(t.domain) map2 bounded.max(t.domain)) {
          case (min, max) ⇒
            if (scale) (values zip (min zip max)).map { case (g, (min, max)) ⇒ implicitly[ScalableType[T]].scale(g, min, max) }
            else values.map(implicitly[ScalableType[T]].convert)
        }

      scaled.map { sc ⇒ ScaledSequence(t.value, sc.toArray) }
    }

  }

}

trait Scalable[T] {
  def isScalar(t: T): Boolean
  def inputs(t: T): PrototypeSet
  def prototype(t: T): Val[_]
  def size(t: T): FromContext[Int]
  def unflatten(t: T)(values: Seq[Double], scale: Boolean): FromContext[Scalable.Scaled]
}

object ScalarOrSequenceOfDouble {

  def prototypes(scales: Seq[ScalarOrSequenceOfDouble[_]]) = scales.map(_.prototype)

  def unflatten(scales: Seq[ScalarOrSequenceOfDouble[_]], values: Seq[Double], scale: Boolean = true): FromContext[List[Variable[_]]] = {
    @tailrec def scaled0(scales: List[ScalarOrSequenceOfDouble[_]], values: List[Double], acc: List[Variable[_]] = Nil)(context: ⇒ Context, rng: RandomProvider, newFile: TmpDirectory, fileService: FileService): List[Variable[_]] =
      if (scales.isEmpty || values.isEmpty) acc.reverse
      else {
        val input = scales.head
        val (variable, tail) =
          input.unflatten(values, scale).map { Scalable.Scaled.toVariable(_, values, input.size(context)(rng, newFile, fileService)) }.from(context)(rng, newFile, fileService)

        scaled0(scales.tail, tail, variable :: acc)({ context + variable }, rng, newFile, fileService)
      }

    FromContext { p ⇒ scaled0(scales.toList, values.toList)(p.context, p.random, p.newFile, p.fileService) }
  }

  implicit def fromScalable[T: Scalable](t: T): ScalarOrSequenceOfDouble[T] = new ScalarOrSequenceOfDouble(t, implicitly[Scalable[T]])
}

class ScalarOrSequenceOfDouble[T](t: T, scalable: Scalable[T]) {
  def isScalar = scalable.isScalar(t)
  def inputs = scalable.inputs(t)
  def prototype = scalable.prototype(t)
  def size = scalable.size(t)
  def unflatten(values: Seq[Double], scale: Boolean = true) = scalable.unflatten(t)(values, scale)
}
