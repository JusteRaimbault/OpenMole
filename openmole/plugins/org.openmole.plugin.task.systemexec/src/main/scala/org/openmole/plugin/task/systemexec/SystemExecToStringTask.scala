/*
 * Copyright (C) 2010 mathieu leclaire <mathieu.leclaire@openmole.org>
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

package org.openmole.plugin.task.systemexec

import org.openmole.plugin.tools.utils.ProcessUtils._
import org.openmole.commons.tools.io.StringBuilderOutputStream
import org.openmole.core.implementation.data.Prototype
import org.openmole.core.model.job.IContext
import java.io.PrintStream
import java.lang.StringBuilder

class SystemExecToStringTask(name: String, 
                             cmd: String, 
                             returnValue: Prototype[Integer], 
                             relativeDir: String,
                             val outString: Prototype[String]) extends AbstractSystemExecTask(name,cmd,returnValue,relativeDir) {
  
  val stringBuilder = new StringBuilder()
  val stringBuilderOutputStream = new StringBuilderOutputStream(stringBuilder)
    
  override protected def execute(process: Process, context: IContext):Int = {    
    val ret = executeProcess(process,new PrintStream(stringBuilderOutputStream),System.err)
    if(!stringBuilder.toString.isEmpty()) context.setValue[String](outString,stringBuilder.toString)
    return ret
  }
}
