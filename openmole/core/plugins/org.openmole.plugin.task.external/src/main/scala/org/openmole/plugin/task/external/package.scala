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

package org.openmole.plugin.task

import org.openmole.misc.tools.service.OS

package external {

  import java.io._
  import org.openmole.core.workflow.data._
  import org.openmole.core.workflow.tools._

  trait ExternalPackage {
    implicit def inputsFileDecorator(i: org.openmole.core.workflow.builder.Inputs) = new {
      def +=(p: Prototype[File], name: String, link: Boolean = false) =
        (_: ExternalTaskBuilder).addInput(p, name, link)
    }

    implicit def outputsFileDecorator(o: org.openmole.core.workflow.builder.Outputs) = new {
      def +=(name: String, p: Prototype[File]) =
        (_: ExternalTaskBuilder).addOutput(name, p)
    }

    lazy val resources =
      new {
        def +=(file: File, name: Option[ExpandedString] = None, link: Boolean = false, os: OS = OS()) =
          (_: ExternalTaskBuilder).addResource(file, name, link, os)
      }
  }
}

package object external extends ExternalPackage