/*
 * Copyright (C) 2012 Romain Reuillon
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

package org.openmole.core.workflow

import org.openmole.core.expansion.{ FromContext, Validate }

/**
 * Sampling aims at associating prototypes with values.
 */
package sampling {

  import org.openmole.core.context._
  import org.openmole.core.expansion._
  import org.openmole.tool.types._
  import org.openmole.core.workflow.domain._
  import cats.implicits._
  import org.openmole.core.workflow.validation.{ ExpectedValidation, RequiredInput }

  trait SamplingPackage {

    implicit class PrototypeFactorDecorator[T](p: Val[T]) {
      def is(d: FromContext[T]) = Factor(p, d)
    }

    implicit def fromContextIsDiscrete[T] = new DiscreteFromContextDomain[FromContext[T], T] {
      override def iterator(domain: FromContext[T]): FromContext[Iterator[T]] = domain.map(v ⇒ Vector(v).iterator)
    }

    implicit def fromIsSampling[T](t: T)(implicit isSampling: IsSampling[T], samplingInputs: RequiredInput[T], samplingValidate: ExpectedValidation[T]) =
      new Sampling {
        override def validate = isSampling.validate(t) ++ samplingValidate(t)
        override def inputs = isSampling.inputs(t) ++ samplingInputs(t)
        override def outputs: Iterable[Val[_]] = isSampling.outputs(t)
        override def apply(): FromContext[Iterator[Iterable[Variable[_]]]] = isSampling.apply(t)
      }

    implicit def factorIsSampling[D, T](implicit domain: DiscreteFromContextDomain[D, T], domainInputs: RequiredInput[D], domainValidate: ExpectedValidation[D]) = new IsSampling[Factor[D, T]] {
      def validate(f: Factor[D, T]): Validate = domain.iterator(f.domain).validate ++ domainValidate(f.domain)
      def inputs(f: Factor[D, T]) = domain.iterator(f.domain).inputs ++ domainInputs.apply(f.domain)

      def outputs(f: Factor[D, T]) = List(f.value)
      override def apply(f: Factor[D, T]): FromContext[Iterator[collection.Iterable[Variable[T]]]] =
        domain.iterator(f.domain).map(_.map { v ⇒ List(Variable(f.value, v)) })
    }

    type Sampling = sampling.Sampling

    def EmptySampling() = Sampling { _ ⇒ Iterator.empty }
  }
}

package object sampling {
  import org.openmole.core.context._
  import org.openmole.core.keyword._

  /**
   * The factor type associates a Val to a domain through the keyword In
   * @tparam D
   * @tparam T
   */
  type Factor[D, T] = In[Val[T], D]

  /**
   * Construct a [[Factor]] from a prototype and its domain
   * @param p
   * @param d
   * @return
   */
  def Factor[D, T](p: Val[T], d: D) = In(p, d)

  object IsSampling {
    implicit def samplingIsSampling = new IsSampling[Sampling] {
      override def validate(s: Sampling) = s.validate
      override def inputs(s: Sampling): PrototypeSet = s.inputs
      override def outputs(s: Sampling): Iterable[Val[_]] = s.outputs
      override def apply(s: Sampling): FromContext[Iterator[Iterable[Variable[_]]]] = s()
    }
  }

  trait IsSampling[-S] {

    def validate(s: S): Validate

    /**
     * Prototypes of the variables required by this sampling.
     *
     * @return the data
     */
    def inputs(s: S): PrototypeSet

    /**
     * Prototypes of the factors generated by this sampling.
     *
     * @return the factors prototypes
     */
    def outputs(s: S): Iterable[Val[_]]

    /**
     * This method builds the explored plan in the given {@code context}.
     */
    def apply(s: S): FromContext[Iterator[Iterable[Variable[_]]]]
  }

}