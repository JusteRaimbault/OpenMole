package org.openmole.plugin.domain.modifier

/*
 * Copyright (C) 2021 Romain Reuillon
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

import org.openmole.core.dsl._
import org.openmole.core.dsl.extension._
import org.openmole.plugin.domain.range._

import org.scalatest._

class ModifierDomainSpec extends FlatSpec with Matchers {

  "inputs of modified domain" should "be as expected" in {
    val size = Val[Int]
    val range = RangeDomain[Int](0, 10)
    val take = range.take(size)
    implicitly[RequiredInput[take.type]].apply(take) should contain(size)
  }

  "range" should "work with modifiers" in {
    RangeDomain[Double](0.0, 10.0, 0.1).map(x ⇒ x * x)
    RangeDomain[Int](0, 10).map(x ⇒ x * x)
  }

}
