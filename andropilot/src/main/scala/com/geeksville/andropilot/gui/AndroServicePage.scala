package com.geeksville.andropilot.gui

import android.os.Bundle
import com.ridemission.scandroid.AndroidLogger
import scala.collection.JavaConverters._
import android.os.Handler
import com.geeksville.util.ThreadTools._
import android.support.v4.app.Fragment
import com.ridemission.scandroid.PagerPage
import com.geeksville.andropilot.service.AndroServiceClient
import com.geeksville.andropilot.FlurryClient

/**
 * Mixin for common behavior for all our fragments that depend on data from the andropilot service.
 * This variant is careful to only start using the service when our page is shown (being careful to only start it once
 * and to only stop it when once
 */
trait AndroServicePage extends Fragment with AndroidLogger with AndroServiceClient with PagerPage with FlurryClient {

  implicit def acontext = getActivity

  /**
   * Does work in the GUIs thread
   */
  protected final var handler: Handler = null

  private var serviceBound = false

  override def onCreate(saved: Bundle) {
    super.onCreate(saved)

    debug("androPage onCreate")
    handler = new Handler
  }

  override def onResume() {
    debug("androPage onResume")
    super.onResume()

    if (isShown) // Only do this we we were already the selected page
      bind()
  }

  override def onPause() {
    debug("androPage onPause")

    unbind()

    super.onPause()
  }

  /**
   * Don't bother sending us anything if we are not visible...
   */
  override def isInterested(evt: Any) = isShown && super.isInterested(evt)

  private def unbind() {
    if (serviceBound) {
      serviceOnPause()
      serviceBound = false
    }
  }

  private def bind() {
    serviceOnResume()
    serviceBound = true
  }

  override def onPageShown() {
    super.onPageShown()
    beginTimedEvent("show_" + getClass.getSimpleName)
    bind()
  }

  override def onPageHidden() {
    endTimedEvent("show_" + getClass.getSimpleName)
    unbind()

    super.onPageHidden()
  }
}
