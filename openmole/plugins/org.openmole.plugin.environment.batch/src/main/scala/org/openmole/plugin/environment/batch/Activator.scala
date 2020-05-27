/*
 * Copyright (C) 2015 Romain Reuillon
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

package org.openmole.plugin.environment.batch

import org.openmole.core.pluginmanager.PluginInfo
import org.openmole.core.preference.ConfigurationLocationRegistry
import org.openmole.core.replication.ReplicaCatalog
import org.openmole.plugin.environment.batch.environment.BatchEnvironment
import org.openmole.plugin.environment.batch.storage.{ StorageInterface, StorageService, StorageSpace }
import org.osgi.framework.{ BundleActivator, BundleContext }

class Activator extends BundleActivator {
  override def stop(context: BundleContext): Unit = {
    PluginInfo.unregister(this)
    ConfigurationLocationRegistry.unregister(this)
  }

  override def start(context: BundleContext): Unit = {
    PluginInfo.register(this, Vector(this.getClass.getPackage))
    ConfigurationLocationRegistry.register(
      this.getClass,
      ConfigurationLocationRegistry.list(BatchEnvironment) ++
        ConfigurationLocationRegistry.list(ReplicaCatalog) ++
        ConfigurationLocationRegistry.list(StorageService) ++
        ConfigurationLocationRegistry.list(StorageSpace)
    )
  }
}