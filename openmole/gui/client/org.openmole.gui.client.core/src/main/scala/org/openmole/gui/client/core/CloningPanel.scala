package org.openmole.gui.client.core

import org.openmole.gui.ext.data._
import scaladget.bootstrapnative.bsn._
import org.openmole.gui.ext.tool.client._
import scalatags.JsDom.all._
import scaladget.tools._
import rx._
import scaladget.bootstrapnative.Selector.Options
import org.openmole.gui.client.core.files.{ TreeNodePanel, treenodemanager }

class CloningPanel {

  val versioningSelector: Options[VersioningPluginFactory] = Plugins.versioningFactories.now.options(0, btn_primary, (a: VersioningPluginFactory) ⇒ a.name,
    onclose = () ⇒ currentPanel() = versioningSelector.content.now.map {
      _.clonePanel(treenodemanager.instance.current.now)
    }
  )

  def onCloned = () ⇒ {
    TreeNodePanel.refreshAndDraw
    dialog.hide
  }

  val currentPanel: Var[Option[VersioningGUIPlugin]] = Var(versioningSelector.content.now.map { _.clonePanel(treenodemanager.instance.current.now, onCloned) })
  val dialog = ModalDialog(omsheet.panelWidth(52))

  dialog header (b("Clone a repository"))

  dialog body (hForm()(
    versioningSelector.selector,
    Rx {
      currentPanel().map { _.panel }.getOrElse(div())
    }
  ))
}