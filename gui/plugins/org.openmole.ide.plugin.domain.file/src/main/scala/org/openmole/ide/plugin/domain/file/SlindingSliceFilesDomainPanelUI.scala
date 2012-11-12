/*
 * Copyright (C) 2012 Mathieu Leclaire 
 * < mathieu.leclaire at openmole.org >
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
package org.openmole.ide.plugin.domain.file

import org.openmole.ide.misc.widget.{ Help, URL, Helper, PluginPanel }
import org.openmole.ide.core.model.panel.IDomainPanelUI
import swing.{ CheckBox, TextField }
import java.util.{ Locale, ResourceBundle }

class SlindingSliceFilesDomainPanelUI(val dataUI: SlindingSliceFilesDomainDataUI) extends PluginPanel("wrap") with IDomainPanelUI with FileDomainPanelUI {

  val i18n = ResourceBundle.getBundle("help", new Locale("en", "EN"))
  val dirTField = directoryTextField(dataUI.directoryPath)
  val numberPatternTextField = new TextField(8) { text = dataUI.numberPattern }
  val sliceSizeTextField = new TextField(8) { text = dataUI.sliceSize.toString }

  contents += FileDomainPanelUI.panel(List((dirTField, "Directory"), (numberPatternTextField, "Number pattern"), (sliceSizeTextField, "Slice size")))

  override def toString = dataUI.name

  def saveContent = new SlindingSliceFilesDomainDataUI(dirTField.text, numberPatternTextField.text, sliceSizeTextField.text.toInt)

  override val help = new Helper(List(new URL(i18n.getString("permalinkText"), i18n.getString("permalink")))) {
    add(dirTField, new Help(i18n.getString("dir"), i18n.getString("dirEx")))
    add(numberPatternTextField, new Help(i18n.getString("selectionPattern"), i18n.getString("selectionPatternEx")))
    add(sliceSizeTextField, new Help(i18n.getString("sliceSize"), i18n.getString("sliceSizeEx")))
  }
}