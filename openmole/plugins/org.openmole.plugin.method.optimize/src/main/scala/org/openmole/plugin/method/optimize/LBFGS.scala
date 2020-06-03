package org.openmole.plugin.method.optimize

object LBFGS {

}

/**
  * LBFGS optimization (reduced memory - dynamic programming approach to gradient descent)
  *   https://en.wikipedia.org/wiki/Broyden%E2%80%93Fletcher%E2%80%93Goldfarb%E2%80%93Shanno_algorithm
  *
  *  Parallelisation
  *   Najafabadi, M. M., Khoshgoftaar, T. M., Villanustre, F., & Holt, J. (2017). Large-scale distributed L-BFGS. Journal of Big Data, 4(1), 22.
  *   => parallelize the dimensions, not the sequential algorithm
  *
  *   Combine with multi-start? (done in mgo-benchmark) "A multi-start LBFGS distributed on computation grids"
  *   Marti, R. (2003). Handbook of Metaheuristics, vol. 57, chap. Multi-Start Methods.
  *
  *   Use breeze implementation https://github.com/scalanlp/breeze/wiki/Quickstart
  *
  *  - Sobol sampling of start space -> rather regular
  *
  * Berahas, A. S., & Takáč, M. (2019). A robust multi-batch l-bfgs method for machine learning. Optimization Methods and Software, 1-29.
  *
  */
object LBFGSOptimization {

  import org.openmole.core.dsl.DSL

  val abcNamespace = Namespace("abc")

  /**
    * Alternative constructor with a given aggregation of objectives
    *
    * @param evaluation
    * @param variables
    * @param objectives
    * @param aggregation
    * @return
    */
  def apply(
             evaluation:           DSL,
             variables:           Seq[Val[Double]],
             objectives: Seq[Val[Double]],
             aggregation: DSL
           ) = {
      val objective = Val[Double] // should be as output of the aggreg task ?
      aggregation set (outputs += Seq(objective))
      LBFGSOptimization(
        evaluation = MapReduce(evaluation = evaluation,aggregation=aggregation)
        variables = variables,
        objective = objective
      )
    }
  }


  /**
    *
    * @param evaluation
    * @param variables restricted in type of variables, contrary to GA
    * @param objective the single objective to be optimized
    * @param scope
    * @return
    */
  def apply(
             evaluation:           DSL,
             variables:           Seq[Val[Double]],
             objective:           Val[Double]
             scope:       DefinitionScope                 = "lbfgs optimization"
           ) = {

    //val step = Val[Int]("step")
    val stop = Val[Boolean]


    //val fitness: DenseVector[Double] => Double = {x => problem.fitness(x.toScalaVector()) sum}

    val wrapped = pattern.wrap(evaluation, evolution.inputPrototypes, evolution.outputPrototypes,false)

    val boundaries = problem.boundaries

    // define gradient function
    //val gradient = new ApproximateGradientFunction(fitness)
    val gradient = new StochasticApproximateGradient(fitness,gradientDescent.stochastic_iterations,gradientDescent.epsilon)

    // define the solver
    //println("Running LBFGS on problem "+problem.toString)
    val lbfgs = if(gradientDescent.tolerance < 0){
      //println("GD : no cv check")
      new LBFGS[DenseVector[Double]](FirstOrderMinimizer.maxIterationsReached[DenseVector[Double]](gradientDescent.iterations)||FirstOrderMinimizer.searchFailed,gradientDescent.m)
    } else {
      new LBFGS[DenseVector[Double]](maxIter=gradientDescent.iterations, m=gradientDescent.m,tolerance=gradientDescent.tolerance)
    }

    //println(lbfgs.convergenceCheck.initialInfo)

    //val res = lbfgs.minimize(gradient,new DenseVector(gradientDescent.x0.toArray))
    //val minstate = lbfgs.minimizeAndReturnState(gradient,new DenseVector(gradientDescent.x0(problem)))
    Iterator[State] iterations = lbfgs.iterations(gradient,new DenseVector(gradientDescent.x0(problem)))

    // abc pattern
    val mapReduce = MapReduce(sampler = preStepTask,evaluation = evaluation,aggregation = postStepTask)
    val loop = While(evaluation = mapReduce, condition = !(stop: Condition))

    // sobol sampling generation of starts
    val initialValuesTask = EmptyTask()


    // evolution: master-slave
    val masterTask = MoleTask(master) set (exploredOutputs += evolution.genomePrototype.toArray)

    val masterSlave = MasterSlave(
        initialValuesTask,
        master = masterTask,
        slave = wrapped,
        state = Seq(evolution.populationPrototype, evolution.statePrototype),
        slaves = parallelism,
        stop = evolution.terminatedPrototype
    )

    DSLContainerExtension[ABCParameters](DSLContainer(loop), output = Some(postStepTask), delegate = mapReduce.delegate, data = ABCParameters(state, step, prior))
  }



}






