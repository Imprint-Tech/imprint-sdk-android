package co.imprint.sdk.domain

import co.imprint.sdk.domain.model.CompletionState

internal object ImprintCallbackHolder {
  var onApplicationCompletion: ((CompletionState, Map<String, String?>?) -> Unit)? =
    null
}