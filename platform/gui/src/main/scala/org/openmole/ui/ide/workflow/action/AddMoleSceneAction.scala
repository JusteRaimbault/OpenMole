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

package org.openmole.ui.ide.workflow.action

import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import org.openmole.ui.ide.control.MoleScenesManager

class AddMoleSceneAction extends ActionListener{

  override def actionPerformed(ae: ActionEvent)= MoleScenesManager.display(MoleScenesManager.addMoleScene)
}
//import java.awt.event.ActionEvent;
//import java.awt.event.ActionListener;
//import org.openmole.ui.ide.control.MoleScenesManager;
//import org.openmole.ui.ide.workflow.implementation.MoleScene;
//
///**
// *
// * @author Mathieu Leclaire <mathieu.leclaire@openmole.org>
// */
//public class AddMoleSceneAction implements ActionListener{
//
//    @Override
//    public void actionPerformed(ActionEvent ae) {
//        MoleScenesManager.getInstance().display(MoleScenesManager.getInstance().addMoleScene());
//    }
//}