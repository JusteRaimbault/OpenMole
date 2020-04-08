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

package org.openmole.plugin.task.netlogo6

import java.io.File

import monocle.macros.Lenses
import org.openmole.core.context.Val
import org.openmole.core.workflow.builder._
import org.openmole.core.workflow.dsl._
import org.openmole.plugin.task.external._
import org.openmole.plugin.task.netlogo.NetLogoTask.Workspace
import org.openmole.plugin.task.netlogo._
import org.openmole.plugin.tool.netlogo6._
import org.openmole.core.expansion._

object NetLogo6Task {

  def factory = new NetLogoFactory {
    def apply = new NetLogo6
  }

  implicit def isTask: InputOutputBuilder[NetLogo6Task] = InputOutputBuilder(NetLogo6Task.config)
  implicit def isExternal: ExternalBuilder[NetLogo6Task] = ExternalBuilder(NetLogo6Task.external)
  implicit def isInfo = InfoBuilder(info)
  implicit def isMapped = MappedInputOutputBuilder(NetLogo6Task.mapped)

  def workspace(
    workspace:            File,
    script:               String,
    go:                   Seq[FromContext[String]],
    setup:                Seq[FromContext[String]],
    seed:                 OptionalArgument[Val[Int]],
    ignoreError:          Boolean,
    reuseWorkspace:       Boolean,
    ignoreErrorOnDispose: Boolean,
    switch3d:             Boolean
  )(implicit name: sourcecode.Name, definitionScope: DefinitionScope): NetLogo6Task =
    withDefaultArgs(
      workspace = Workspace.Directory(directory = workspace, script = script, name = workspace.getName),
      go = go,
      setup = setup,
      seed = seed,
      ignoreError = ignoreError,
      reuseWorkspace = reuseWorkspace,
      ignoreErrorOnDispose = ignoreErrorOnDispose,
      switch3d = switch3d
    ) set (
        inputs += (seed.option.toSeq: _*)
      )

  def file(
    script:               File,
    go:                   Seq[FromContext[String]],
    setup:                Seq[FromContext[String]],
    seed:                 OptionalArgument[Val[Int]],
    ignoreError:          Boolean,
    reuseWorkspace:       Boolean,
    ignoreErrorOnDispose: Boolean,
    switch3d:             Boolean
  )(implicit name: sourcecode.Name, definitionScope: DefinitionScope): NetLogo6Task =
    withDefaultArgs(
      workspace = Workspace.Script(script = script, name = script.getName),
      go = go,
      setup = setup,
      seed = seed,
      ignoreError = ignoreError,
      reuseWorkspace = reuseWorkspace,
      ignoreErrorOnDispose = ignoreErrorOnDispose,
      switch3d = switch3d
    ) set (
        inputs += (seed.option.toSeq: _*)
      )

  def apply(
    script:               File,
    go:                   Seq[FromContext[String]],
    setup:                Seq[FromContext[String]]   = Seq(),
    embedWorkspace:       Boolean                    = false,
    seed:                 OptionalArgument[Val[Int]] = None,
    ignoreError:          Boolean                    = false,
    reuseWorkspace:       Boolean                    = false,
    ignoreErrorOnDispose: Boolean                    = false,
    switch3d:             Boolean                    = false
  )(implicit name: sourcecode.Name, definitionScope: DefinitionScope): NetLogo6Task =
    if (embedWorkspace) workspace(script.getCanonicalFile.getParentFile, script.getName, go = go, setup = setup, seed = seed, ignoreError = ignoreError, reuseWorkspace = reuseWorkspace, ignoreErrorOnDispose = ignoreErrorOnDispose, switch3d = switch3d)
    else file(script, go = go, setup = setup, seed = seed, ignoreError = ignoreError, reuseWorkspace = reuseWorkspace, ignoreErrorOnDispose = ignoreErrorOnDispose, switch3d = switch3d)

  private def withDefaultArgs(
    workspace:            NetLogoTask.Workspace,
    go:                   Seq[FromContext[String]],
    setup:                Seq[FromContext[String]],
    seed:                 Option[Val[Int]],
    ignoreError:          Boolean,
    reuseWorkspace:       Boolean,
    ignoreErrorOnDispose: Boolean,
    switch3d:             Boolean
  )(implicit name: sourcecode.Name, definitionScope: DefinitionScope) =
    NetLogo6Task(
      config = InputOutputConfig(),
      external = External(),
      info = InfoConfig(),
      mapped = MappedInputOutputConfig(),
      workspace = workspace,
      go = go,
      setup = setup,
      seed = seed,
      ignoreError = ignoreError,
      reuseWorkspace = reuseWorkspace,
      ignoreErrorOnDispose = ignoreErrorOnDispose,
      switch3d = switch3d
    )

}

@Lenses case class NetLogo6Task(
  config:               InputOutputConfig,
  external:             External,
  info:                 InfoConfig,
  mapped:               MappedInputOutputConfig,
  workspace:            NetLogoTask.Workspace,
  go:                   Seq[FromContext[String]],
  setup:                Seq[FromContext[String]],
  seed:                 Option[Val[Int]],
  ignoreError:          Boolean,
  reuseWorkspace:       Boolean,
  ignoreErrorOnDispose: Boolean,
  switch3d:             Boolean
) extends NetLogoTask {
  override def netLogoFactory: NetLogoFactory = NetLogo6Task.factory
}

