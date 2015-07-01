package org.openmole.gui.client.core

/*
 * Copyright (C) 17/05/15 // mathieu.leclaire@openmole.org
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
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

import org.openmole.core.workspace.{ AuthenticationProvider, Workspace }
import org.openmole.gui.misc.utils.Utils
import org.openmole.gui.shared.Api
import org.scalajs.dom.raw.HTMLDivElement
import org.scalajs.jquery
import scala.scalajs.js.Date
import scalatags.JsDom.all._
import org.openmole.gui.misc.js.Expander
import org.openmole.gui.misc.js.Expander._
import org.openmole.gui.misc.js.{ BootstrapTags ⇒ bs }
import scalatags.JsDom.{ tags ⇒ tags }
import org.openmole.gui.misc.js.JsRxTags._
import scala.scalajs.js.timers._
import scala.scalajs.concurrent.JSExecutionContext.Implicits.runNow
import autowire._
import org.openmole.gui.ext.data._
import bs._
import rx._

class AuthenticationPanel extends ModalPanel {
  val modalID = "authenticationsPanelID"
  val setting: Var[Option[_ <: AuthenticationData]] = Var(None)

  def onOpen = () ⇒ {
    println("open authen")
  }

  def onClose = () ⇒ {
    println("close authen")
  }

  private val auths: Var[Seq[AuthenticationData]] = Var(Seq())

  def getAuthentications = {
    OMPost[Api].authentications.call().foreach { a ⇒
      auths() = a
    }
  }

  lazy val authenticationTable = {

    bs.table(striped)(
      thead,
      Rx {
        tbody({
          setting() match {
            case Some(d: AuthenticationData) ⇒ ClientService.panelUI(d).view
            case _ ⇒
              for (a ← auths()) yield {
                //ClientService.authenticationUI(a)
                Seq(bs.tr(row)(
                  tags.span(a.synthetic, cursor := "pointer", onclick := { () ⇒
                    setting() = Some(a)
                  })
                )
                )
              }
          }
        }
        )
      }
    )
  }

  val closeButton = bs.button("Close", btn_test)(data("dismiss") := "modal", onclick := {
    () ⇒
      println("Close")
  }
  )

  val dialog = modalDialog(modalID,
    headerDialog(
      tags.div("Authentications"
      ),
      bodyDialog(`class` := "executionTable")(
        authenticationTable
      ),
      footerDialog(
        closeButton
      )
    )
  )

  jquery.jQuery(org.scalajs.dom.document).on("hide.bs.modal", "#" + modalID, () ⇒ {
    onClose()
  })
}