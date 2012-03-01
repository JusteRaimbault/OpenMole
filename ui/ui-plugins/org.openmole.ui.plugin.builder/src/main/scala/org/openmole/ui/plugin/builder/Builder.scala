/*
 * Copyright (C) 2010 reuillon
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

package org.openmole.ui.plugin.builder

/**
 *
 * Builder is a class offering factories to build complex OpenMOLE objects.
 *
 * @author nicolas.dumoulin@openmole.org
 */

import org.openmole.misc.exception.UserBadDataError
import org.openmole.core.implementation.mole.Capsule
import org.openmole.core.implementation.mole.StrainerCapsule
import org.openmole.core.implementation.data.Data
import org.openmole.core.implementation.data.DataChannel
import org.openmole.core.implementation.data.DataSet
import org.openmole.core.implementation.data.Prototype
import org.openmole.core.implementation.data.Variable
import org.openmole.core.implementation.mole.FixedEnvironmentSelection
import org.openmole.core.implementation.mole.Mole
import org.openmole.core.implementation.mole.MoleExecution
import org.openmole.core.implementation.sampling.Factor
import org.openmole.core.implementation.task.ExplorationTask
import org.openmole.core.implementation.task.MoleTask
import org.openmole.core.implementation.task.Task
import org.openmole.core.implementation.transition.Transition
import org.openmole.core.implementation.transition.Slot
import org.openmole.core.model.mole.ICapsule
import org.openmole.core.model.data.IContext
import org.openmole.core.model.data.IDataSet
import org.openmole.core.model.data.IPrototype
import org.openmole.core.model.data.IVariable
import org.openmole.core.model.domain.IDomain
import org.openmole.core.model.mole.IEnvironmentSelection
import org.openmole.core.model.mole.IMole
import org.openmole.core.model.mole.IMoleExecution
import org.openmole.core.model.sampling.IFactor
import org.openmole.core.model.sampling.ISampling
import org.openmole.core.model.task.IExplorationTask
import org.openmole.core.model.task.ITask
import org.openmole.core.model.task.IMoleTask
import org.openmole.core.model.task.ITask
import org.openmole.core.model.transition.ITransition
import org.openmole.ui.plugin.transitionfactory.IPuzzleFirstAndLast
import org.openmole.ui.plugin.transitionfactory.PuzzleFirstAndLast
import org.openmole.ui.plugin.transitionfactory.TransitionFactory
import org.openmole.ui.plugin.transitionfactory.TransitionFactory._

object Builder {

  /**
   * Builds an OpenMOLE prototype. A prototype is composed of a name and a type.
   *
   * @param name, the name of the protoype,
   * @param type, the class name of the type.
   * @return an instance of Prototype.
   */
  def prototype[T](name: String, `type`: Class[T]): IPrototype[T] =  new Prototype[T](name, `type`)
    

  /**
   * Builds an OpenMOLE variable.
   * @param name
   * @param value
   * @return an instance of Variable.
   */
  def variable[T](name: String, value: T): IVariable[T] = new Variable[T](name, value)
    

  /**
   * Builds a dataSet, which is a collection of prototypes.
   *
   * @param prototypes, the prototypes to be grouped.
   * @return a DataSet
   */
  def dataSet(head: IPrototype[_], prototypes: Array[IPrototype[_]]): IDataSet = new DataSet(head, prototypes)
    

  /**
   * Builds a dataSet, from a collection of dataset. In other words, it composes
   * datasets.
   *
   * @param dataSets, the dataSet to be composed.
   * @return the composed dataSet.
   */
  def dataSet(head: IDataSet, dataSets: Array[IDataSet]): IDataSet = new DataSet(head, dataSets)
    

  /**
   * Builds a Capsule object.
   *
   * @param task, the task to be encapsulated
   * @return an instance of Capsule
   */
  def capsule(task: ITask): ICapsule = new Capsule(task)

  /**
   * Builds a generic MOLE Task, that is to say without any exploration.
   *
   * @param taskName, the task name,
   * @param puzzle, the puzzle to be executed in the Mole task.
   * @return a instance of MoleTask
   * @throws InternalProcessingError
   * @throws UserBadDataError
   * @throws InterruptedException
   */
  def moleTask(taskName: String, puzzle: IPuzzleFirstAndLast) = 
    new MoleTask(taskName, new Mole(puzzle.first), puzzle.last)


  /**
   * Builds a Mole.
   *
   * @param tasks, a list of tasks to be chained inside the Mole. Task capsules
   * are previously generated.
   * @return an instance of Mole.
   * @throws UserBadDataError
   * @throws InternalProcessingError
   * @throws InterruptedException
   */
  def mole(head: ITask, tasks: Array[ITask]): IMole = new Mole(chain(head, tasks).first)
    
  /**
   * Builds a Mole.
   *
   * @param Capsule, a list of task capsules to be chained inside the Mole.
   * @return an instance of Mole.
   * @throws UserBadDataError
   * @throws InternalProcessingError
   * @throws InterruptedException
   */
  def mole(head: ICapsule, capsules: Array[ICapsule]): IMole = new Mole(chain(head, capsules).first)
  def mole(capsule: ICapsule): IMole = new Mole(capsule)

  /**
   * Builds a Mole.
   *
   * @param puzzle, the puzzle to be executed inside the Mole.
   * @return an instance of Mole.
   * @throws UserBadDataError
   * @throws InternalProcessingError
   * @throws InterruptedException
   */
  def mole(puzzle: IPuzzleFirstAndLast): IMole = new Mole(puzzle.first)
  /**
   * Builds a Mole execution, that is to say a Mole ready to be run.
   *
   * @param tasks, a list of tasks to be chained inside the Mole.
   * @return an instance of MoleExecution
   * @throws UserBadDataError
   * @throws InternalProcessingError
   * @throws InterruptedException
   */
  def moleExecution(head: ITask, tasks: Array[ITask]): IMoleExecution = new MoleExecution(mole(head, tasks))
    

  /**
   * Builds a Mole execution, that is to say a Mole ready to be run.
   *
   * @param Capsule, a list of task capsules to be chained inside the Mole.
   * @return an instance of MoleExecution
   * @throws UserBadDataError
   * @throws InternalProcessingError
   * @throws InterruptedException
   */
  def moleExecution(capsule: ICapsule): IMoleExecution = new MoleExecution(new Mole(capsule))

  /**
   * Builds a Mole execution, that is to say a Mole ready to be run.
   *
   * @param puzzle,  the puzzle to be executed inside the Mole.
   * @return an instance of MoleExecution
   * @throws UserBadDataError
   * @throws InternalProcessingError
   * @throws InterruptedException
   */
  def moleExecution(puzzle: IPuzzleFirstAndLast): IMoleExecution = new MoleExecution(mole(puzzle))

  /**
   * Builds a Mole execution, that is to say a Mole ready to be run.
   *
   * @param mole, the mole to be executed.
   * @return an instance of MoleExecution.
   * @throws InternalProcessingError
   * @throws UserBadDataError
   */
  def moleExecution(mole: IMole): IMoleExecution = new MoleExecution(mole)
    

  /**
   * Builds a Mole execution, with a specific environment strategy.
   *
   * @param mole, the puzzle to be executed inside the Mole.
   * @return an instance of MoleExecution.
   * @throws InternalProcessingError
   * @throws UserBadDataError
   */
  def moleExecution(puzzle: IPuzzleFirstAndLast, strategy: IEnvironmentSelection): IMoleExecution = new MoleExecution(mole(puzzle), strategy)

  /**
   * Builds an environment selection object.
   * @return an instance of FixedEnvironmentSelection
   * @throws InternalProcessingError
   */
  def fixedEnvironmentSelection: FixedEnvironmentSelection = new FixedEnvironmentSelection


  /**
   * Builds a Factor according to a prototype
   *
   * @param prototype, the prototype to be
   * @param domain, to domain on which making the exploration
   * @return an instance of Factor
   */
  def factor[T, D <: IDomain[T]](prototype: IPrototype[T], domain: D): IFactor[T, D] = new Factor(prototype, domain)      

  /**
   * Builds an exploration task, according to a Design of Experiment.
   *
   * @param name, the name of the task,
   * @param sampler, the sampler to be explored.
   * @return an instance of ExplorationTask
   * @throws UserBadDataError
   * @throws InternalProcessingError
   */
  def explorationTask(name: String, sampler: ISampling): IExplorationTask = new ExplorationTask(name, sampler)
        

  /**
   * Builds an exploration task, according to a Design of Experiment and input
   * prototypes.
   *
   * @param name, the name of the task,
   * @param sampler, the sampler to be explored.
   * @param input, a set of prototypes to be set as input of the task
   * @return an instance of ExplorationTask
   * @throws UserBadDataError
   * @throws InternalProcessingError
   */
  def  explorationTask(name: String, sampler: ISampling, input: IDataSet): IExplorationTask = {
    val explo = new ExplorationTask(name, sampler)
    explo.addInput(input)
    explo
  }


  /**
   * Builds an transitin exploration from a exploration task capsule and a
   * task to be explored.
   *
   * @param explorationCapsule, the exploration task capsule.
   * @param exploredTask, the task to be explored.
   * @return an instance of Capsule
   */
  def explorationTransition(explorationCapsule: ICapsule, exploredTask: ITask): ITransition = {
    val exploredCapsule = new Capsule(exploredTask)
    new Transition(explorationCapsule, exploredCapsule)
  }
    
  /**
   * Builds a MoleTask containing an exploration. The output of this task are the
   * the puzzle output as arrays.
   *
   * @param taskName, the name of the task,
   * @param explo, the exploration task,
   * @param puzzle, the puzzle.
   * @return a instance of MoleTask
   * @throws InternalProcessingError
   * @throws UserBadDataError
   * @throws InterruptedException
   */ 
  def explorationMoleTask(taskName: String, explo: IExplorationTask, puzzle: IPuzzleFirstAndLast) = {
        
    val ft = puzzle.last.task.getOrElse(throw new UserBadDataError("Task unasigned for first capsule of the puzzle"))
    
    // the final task making possible the retrieving of output
    //val inputToGlobalTask = new InputToGlobalTask(taskName + "InputToGlobalTask", ft.userOutputs.map{ Data.toArray(_)} )
    val exploPuz = exploration(explo, puzzle)
    
    // builds a mole containing a exploration, a puzzle, and an aggregation on the inputToGlobalTask
    new MoleTask(taskName, new Mole(exploPuz.first), puzzle.last)
  }
  
  
  def iterative(iterationName: String, nb: Int, head: ICapsule, capsules: Array[ICapsule] = Array.empty): IPuzzleFirstAndLast =
    iterative(iterationName, nb, chain(head, capsules))
  
    
  def iterative(iterationName: String, nb: Int, puzzle: IPuzzleFirstAndLast) = {
    val prototype = new Prototype(iterationName, classOf[Int])
    
    val loopOnCapsule = new StrainerCapsule(new Task(iterationName + "_loopOn") {
      addParameter(prototype, 0)
      addInput(prototype)
      addOutput(prototype)
        
      override def process(context: IContext) = context
    })
    
    val decrementCapsule = new StrainerCapsule(new Task(iterationName + "_decrement") {
      addInput(prototype)
      addOutput(prototype)
      
      override def process(context: IContext) =
        context + (prototype, context.value(prototype).get + 1)
      
    })
     
    new Transition(loopOnCapsule, puzzle.first)
    new Transition(puzzle.last, decrementCapsule)
    new Transition(decrementCapsule, new Slot(loopOnCapsule), prototype.name + "<=" + nb)
    new DataChannel(loopOnCapsule, decrementCapsule, prototype)
    new PuzzleFirstAndLast(loopOnCapsule, decrementCapsule)
  }
      
}
