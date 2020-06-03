package org.openmole.plugin.method

package object optimize {


  object OptimizationPattern {

  }

  implicit def workflowIntegration = WorkflowIntegration[DSLContainer[EvolutionWorkflow]](_.data)


}
