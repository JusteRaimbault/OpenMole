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

package org.openmole.ide.plugin.groupingstrategy.batch

import org.openmole.core.model.mole.ICapsule
import org.openmole.plugin.grouping.batch.NumberOfMoleJobsGroupingStrategy
import org.openmole.ide.core.model.data.IGroupingStrategyDataUI
import org.openmole.ide.core.model.control.IExecutionManager

class NumberOfMoleJobsGroupingStrategyDataUI(executionManager: IExecutionManager,
                        toBeGrouped: (ICapsule,Int)) extends IGroupingStrategyDataUI{
    
  override def coreObject = (new NumberOfMoleJobsGroupingStrategy(toBeGrouped._2),toBeGrouped._1)
}
