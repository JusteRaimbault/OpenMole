package org.openmole.plugin.task.python

import org.openmole.core.highlight.HighLight
import org.openmole.core.pluginregistry.PluginRegistry
import org.osgi.framework.{ BundleActivator, BundleContext }

class Activator extends BundleActivator {

  override def stop(context: BundleContext): Unit =
    PluginRegistry.unregister(this)

  override def start(context: BundleContext): Unit = {
    import org.openmole.core.highlight.HighLight._

    val keyWords: Vector[HighLight] =
      Vector(
        TaskHighLight(objectName(PythonTask))
      )

    PluginRegistry.register(this, Vector(this.getClass.getPackage), highLight = keyWords)
  }
}