/*
 * Copyright (C) 24/10/13 Romain Reuillon
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
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.openmole.plugin.domain.range

import org.openmole.core.dsl._
import org.openmole.core.dsl.extension._

import cats.implicits._

object StepRangeDomain {

  implicit def isDiscrete[T] = new DiscreteFromContextDomain[StepRangeDomain[T], T] with BoundedFromContextDomain[StepRangeDomain[T], T] with CenterFromContextDomain[StepRangeDomain[T], T] {
    override def iterator(domain: StepRangeDomain[T]) = domain.iterator
    override def max(domain: StepRangeDomain[T]) = domain.max
    override def min(domain: StepRangeDomain[T]) = domain.min
    override def center(domain: StepRangeDomain[T]) = RangeDomain.rangeCenter(domain.range)
  }

  implicit def inputs[T]: RequiredInput[StepRangeDomain[T]] = domain ⇒ RangeDomain.inputs.apply(domain.range) ++ domain.steps.inputs
  implicit def validate[T]: ExpectedValidation[StepRangeDomain[T]] = domain ⇒ RangeDomain.validate.apply(domain.range) ++ domain.steps.validate

  def apply[T](range: RangeDomain[T], step: FromContext[T]) = new StepRangeDomain[T](range, step)
}

class StepRangeDomain[T](val range: RangeDomain[T], val steps: FromContext[T]) extends SizeStep[T] {
  import range._

  def stepAndSize(minValue: T, maxValue: T) = steps.map { step ⇒
    import ops._
    val size = (maxValue - minValue) / step
    (step, if (size.toInt < 0) 0 else size.toInt)
  }

  def min = range.min
  def max = range.max
}
