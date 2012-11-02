/*
 * Copyright (C) 2010 Romain Reuillon
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

package org.openmole.core.batch.control

import concurrent.stm._

trait LimitedAccess extends UsageControl {

  def nbTokens: Int

  private lazy val tokens: Ref[List[AccessToken]] = Ref((0 until nbTokens).map { i ⇒ new AccessToken }.toList)

  def add(token: AccessToken)(implicit txn: InTxn) = { tokens() = token :: tokens() }

  def releaseToken(token: AccessToken)(implicit txn: InTxn) = add(token)

  def tryGetToken: Option[AccessToken](implicit txn: InTxn) = {
    tokens() match {
      case head :: tail ⇒
        tokens() = tail
        Some(head)
      case _ ⇒ None
    }
  }

  def waitAToken = atomic { implicit txn ⇒
    tryGetToken.getOrElse(retry)
  }

  def available = tokens.single().size
}
