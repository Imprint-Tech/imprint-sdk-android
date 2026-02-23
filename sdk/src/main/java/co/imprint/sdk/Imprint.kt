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
  ) {
    if (ImprintCallbackHolder.onApplicationCompletion != null) {
      return
    }
    ImprintCallbackHolder.onApplicationCompletion = onCompletion
    val intent = Intent(context, ApplicationActivity::class.java).apply {
      putExtra(APPLICATION_CONFIGURATION, configuration)
    }
    context.startActivity(intent)
  }
}