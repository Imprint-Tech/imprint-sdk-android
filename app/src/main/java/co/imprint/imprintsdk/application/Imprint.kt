package co.imprint.imprintsdk.application

import android.content.Context
import android.content.Intent

/**
 * Main class for managing the Imprint application process.
 */
object Imprint {

  /**
   * Starts the application process with the specified configuration.
   * @param context The context from which the application process will be presented.
   * @param configuration The configuration settings for the application process.
   */
  fun startApplication(
    context: Context,
    configuration: ImprintConfiguration,
    onCompletion: (ImprintConfiguration.CompletionState, Map<String, String>?) -> Unit,
  ) {
    ImprintCallbackHolder.onApplicationCompletion = onCompletion
    val intent = Intent(context, ApplicationActivity::class.java).apply {
      putExtra("config", configuration)
    }
    context.startActivity(intent)
  }
}