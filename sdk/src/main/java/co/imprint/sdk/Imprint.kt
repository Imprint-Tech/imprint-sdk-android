package co.imprint.sdk

import android.content.Context
import android.content.Intent
import android.util.Log
import co.imprint.sdk.domain.model.ImprintCompletionState
import co.imprint.sdk.domain.ImprintCallbackHolder
import co.imprint.sdk.domain.model.ImprintConfiguration
import co.imprint.sdk.presentation.ApplicationActivity
import co.imprint.sdk.presentation.ApplicationActivity.Companion.APPLICATION_CONFIGURATION

object Imprint {

  /**
   * Starts the application process with the specified configuration.
   *
   * Must be called from the main thread. The [onCompletion] callback is always invoked on the
   * main thread, regardless of how the flow ends (completion, error, or user dismissal).
   *
   * Only one application session can be active at a time. Calling this while a session is already
   * in progress will be ignored.
   *
   * @param context The context from which the application process will be presented.
   * @param configuration The configuration settings for the application process.
   * @param onCompletion Callback invoked exactly once when the application process ends.
   * The first parameter is the terminal [ImprintCompletionState]. The second parameter is a
   * metadata map whose contents vary by state — see [ImprintCompletionState] for details.
   */
  fun startApplication(
    context: Context,
    configuration: ImprintConfiguration,
    onCompletion: (ImprintCompletionState, Map<String, Any?>?) -> Unit,
  ) = startApplicationSession(context, configuration, null, onCompletion)

  /**
   * Starts the application process and observes every partner-visible WebView event.
   *
   * [onEvent] receives the event name and its metadata without changing when [onCompletion]
   * runs or which final state it reports.
   */
  fun startApplication(
    context: Context,
    configuration: ImprintConfiguration,
    onEvent: (String, Map<String, Any?>?) -> Unit,
    onCompletion: (ImprintCompletionState, Map<String, Any?>?) -> Unit,
  ) = startApplicationSession(context, configuration, onEvent, onCompletion)

  private fun startApplicationSession(
    context: Context,
    configuration: ImprintConfiguration,
    onEvent: ((String, Map<String, Any?>?) -> Unit)?,
    onCompletion: (ImprintCompletionState, Map<String, Any?>?) -> Unit,
  ) {
    if (ImprintCallbackHolder.onApplicationCompletion != null) {
      Log.w("Imprint", "startApplication called while a session is already active. Ignoring.")
      return
    }
    ImprintCallbackHolder.onApplicationEvent = onEvent
    ImprintCallbackHolder.onApplicationCompletion = onCompletion
    val intent = Intent(context, ApplicationActivity::class.java).apply {
      putExtra(APPLICATION_CONFIGURATION, configuration)
    }
    context.startActivity(intent)
  }
}
