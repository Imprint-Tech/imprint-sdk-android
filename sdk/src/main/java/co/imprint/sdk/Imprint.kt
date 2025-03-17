package co.imprint.sdk

import android.content.Context
import android.content.Intent
import co.imprint.sdk.domain.model.ImprintCompletionState
import co.imprint.sdk.domain.ImprintCallbackHolder
import co.imprint.sdk.domain.model.ImprintConfiguration
import co.imprint.sdk.presentation.ApplicationActivity
import co.imprint.sdk.presentation.ApplicationActivity.Companion.APPLICATION_CONFIGURATION

object Imprint {

  /**
   * Starts the application process with the specified configuration.
   * @param context The context from which the application process will be presented.
   * @param configuration The configuration settings for the application process.
   * @param onCompletion Callback to be invoked when the application process completes.
   */
  fun startApplication(
    context: Context,
    configuration: ImprintConfiguration,
    onCompletion: (ImprintCompletionState, Map<String, String?>?) -> Unit,
  ) {
    ImprintCallbackHolder.onApplicationCompletion = onCompletion
    val intent = Intent(context, ApplicationActivity::class.java).apply {
      putExtra(APPLICATION_CONFIGURATION, configuration)
    }
    context.startActivity(intent)
  }
}