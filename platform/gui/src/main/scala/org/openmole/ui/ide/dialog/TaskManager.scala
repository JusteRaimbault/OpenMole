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

package org.openmole.ui.ide.dialog

import org.openmole.ui.ide.implementation.Preferences
import org.openmole.ui.ide.workflow.implementation.TaskUI
import org.openmole.ui.ide.workflow.implementation.TasksUI

class TaskManager extends IManager{

  override def entityInstance(name: String,t: Class[_])= {
    new TaskUI(name, t)
  }
  
  override def container= TasksUI
  
  override def classTypes= Preferences.coreTaskClasses
}

//public class TaskManager implements IManager{
//
//    @Override
//    public IEntityUI getEntityInstance(String name, Class type) {
//        return new TaskUI(name, type);
//    }
//
//    @Override
//    public IContainerUI getContainer() {
//        return TasksUI.getInstance();
//    }
//
//    @Override
//    public Set<Class<?>> getClassTypes() {
//        return Preferences.getInstance().getCoreTaskClasses();
//    }
//
//}