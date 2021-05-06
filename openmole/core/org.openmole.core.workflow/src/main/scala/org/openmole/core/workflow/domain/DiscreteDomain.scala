/*
 * Copyright (C) 2012 Romain Reuillon
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.openmole.core.workflow.domain

import org.openmole.core.expansion._
import scala.annotation.implicitNotFound

/**
 * Property of being discrete for a domain
 * @tparam D
 * @tparam T
 */
@implicitNotFound("${D} is not a discrete variation domain of type ${T}")
trait DiscreteDomain[-D, +T] {
  def iterator(domain: D): Iterator[T]
}

object DiscreteFromContextDomain {
  implicit def discreteIsContextDiscrete[D, T](implicit d: DiscreteDomain[D, T]): DiscreteFromContextDomain[D, T] =
    domain ⇒ FromContext.value(d.iterator(domain))
}

@implicitNotFound("${D} is not a discrete variation domain of type T | FromContext[${T}]")
trait DiscreteFromContextDomain[-D, +T] {
  def iterator(domain: D): FromContext[Iterator[T]]
}