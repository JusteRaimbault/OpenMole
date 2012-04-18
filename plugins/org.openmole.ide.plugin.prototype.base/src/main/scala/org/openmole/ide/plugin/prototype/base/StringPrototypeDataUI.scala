/*
 * Copyright (C) 2011 <mathieu.leclaire at openmole.org>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.openmole.ide.plugin.prototype.base

import org.openmole.ide.core.model.data.IPrototypeDataUI
import org.openmole.core.model.data.IPrototype
import org.openmole.core.implementation.data.Prototype
import org.openmole.core.implementation.data.Prototype._

class StringPrototypeDataUI(val name: String="", d: Int=0) extends GenericPrototypeDataUI[String](d){
  def displayTypedName = displayName + " : String"
  
  def coreClass = classOf[IPrototype[String]]
  
  def coreObject = new Prototype[String](name).toArray(dim).asInstanceOf[IPrototype[String]]
  
  def fatImagePath = "img/string_fat.png"
  
  def buildPanelUI = new StringPrototypePanelUI(this)
}
