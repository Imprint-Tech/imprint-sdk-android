package co.imprint.imprintsdk.application

internal object ImprintCallbackHolder {
  var onApplicationCompletion: ((CompletionState, Map<String, String?>?) -> Unit)? =
    null
}