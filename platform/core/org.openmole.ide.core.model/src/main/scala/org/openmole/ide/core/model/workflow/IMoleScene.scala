/*
 * Copyright (C) 2011 Mathieu leclaire <mathieu.leclaire at openmole.org>
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

package org.openmole.ide.core.model.workflow

import org.netbeans.api.visual.graph.GraphScene
import org.openmole.ide.core.model.commons.MoleSceneType

trait IMoleScene { 
  def manager: IMoleSceneManager
  
  def setLayout
  
  def refresh
  
  def validate
  
  def initCapsuleAdd(w: ICapsuleUI)
  
  def moleSceneType: MoleSceneType.Value
  
  def isBuildScene: Boolean = moleSceneType == MoleSceneType.BUILD
  
  def graphScene: GraphScene[String,String]
  
  def copy: IMoleScene
  
  def createEdge(sourceNodeID:String, targetNodeID: String)
}
