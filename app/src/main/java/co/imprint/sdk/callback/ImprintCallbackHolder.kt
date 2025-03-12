package co.imprint.sdk.callback

import co.imprint.sdk.api.CompletionState

internal object ImprintCallbackHolder {
  var onApplicationCompletion: ((CompletionState, Map<String, String?>?) -> Unit)? =
    null
}