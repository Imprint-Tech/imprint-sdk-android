package co.imprint.imprintsdk.application

internal object ImprintCallbackHolder {
  var onApplicationCompletion: ((ImprintConfiguration.CompletionState, Map<String, String>?) -> Unit)? = null
}