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

package org.openmole.core.workflow.task

import java.io.File

import org.openmole.core.context._
import org.openmole.core.expansion.FromContext
import org.openmole.core.fileservice.FileService
import org.openmole.core.preference.Preference
import org.openmole.core.serializer.SerializerService
import org.openmole.core.threadprovider.ThreadProvider
import org.openmole.core.tools.obj.Id
import org.openmole.core.workflow.builder.{ DefinitionScope, InfoConfig, InputOutputConfig }
import org.openmole.core.workflow.execution._
import org.openmole.core.workflow.mole.MoleExecution
import org.openmole.core.workflow.tools._
import org.openmole.core.workspace.{ TmpDirectory, Workspace }
import org.openmole.tool.cache._
import org.openmole.tool.lock._
import org.openmole.tool.logger.LoggerService
import org.openmole.tool.outputredirection.OutputRedirection
import org.openmole.tool.random

/**
 * Execution context for a task
 *
 * @param moleExecutionDirectory tmp dir cleaned at the end of the mole execution
 * @param taskExecutionDirectory tmp dir cleaned at the end of the task execution
 * @param applicationExecutionDirectory tmp dir cleaned at the end of the application
 * @param localEnvironment local environment
 * @param preference
 * @param threadProvider
 * @param fileService
 * @param workspace
 * @param outputRedirection
 * @param cache
 * @param lockRepository
 * @param moleExecution
 */
case class TaskExecutionContext(
  moleExecutionDirectory:         File,
  taskExecutionDirectory:         File,
  applicationExecutionDirectory:  File,
  localEnvironment:               LocalEnvironment,
  implicit val preference:        Preference,
  implicit val threadProvider:    ThreadProvider,
  fileService:                    FileService,
  implicit val workspace:         Workspace,
  implicit val outputRedirection: OutputRedirection,
  implicit val loggerService:     LoggerService,
  implicit val serializerService: SerializerService,
  cache:                          KeyValueCache,
  lockRepository:                 LockRepository[LockKey],
  moleExecution:                  Option[MoleExecution]   = None)

object Task {

  /**
   * Construct a Random Number Generator for the task. The rng is constructed by [[org.openmole.tool.random.Random]] with the seed provided from the context (seed being defined as an OpenMOLE variable)
   *
   * @param context
   * @return
   */
  def buildRNG(context: Context): scala.util.Random = random.Random(context(Variable.openMOLESeed)).toScala
  def definitionScope(t: Task) = t.info.definitionScope

  def apply(className: String)(fromContext: FromContextTask.Parameters ⇒ Context)(implicit name: sourcecode.Name, definitionScope: DefinitionScope): FromContextTask =
    FromContextTask.apply(className)(fromContext)

}

/**
 * A Task is a fundamental unit for the execution of a workflow.
 */
trait Task <: Name with Id {

  /**
   * Perform this task.
   *
   * @param context the context in which the task will be executed
   * @param executionContext context of the environment in which the Task is executed
   * @return
   */
  def perform(context: Context, executionContext: TaskExecutionContext): Context = {
    lazy val rng = Lazy(Task.buildRNG(context))
    InputOutputCheck.perform(this, inputs, outputs, defaults, process(executionContext))(executionContext.preference).from(context)(rng, TmpDirectory(executionContext.moleExecutionDirectory), executionContext.fileService)
  }

  /**
   * The actual processing of the Task, wrapped by the [[perform]] method
   * @param executionContext
   * @return
   */
  protected def process(executionContext: TaskExecutionContext): FromContext[Context]

  /**
   * Configuration for inputs/outputs
   * @return
   */
  def config: InputOutputConfig

  /**
   * Information on the task (name, scope)
   * @return
   */
  def info: InfoConfig

  def inputs = config.inputs
  def outputs = config.outputs
  def defaults = config.defaults
  def name = info.name

  /**
   * Make sure 2 tasks with the same content are not equal in the java sense:
   * as Task inherits of the trait Id, hashconsing is done through this id, and creating a unique object here will ensure unicity of tasks
   * (this trick allows to still benefit of the power of case classes while staying in a standard object oriented scheme)
   */
  lazy val id = new Object {}

}

