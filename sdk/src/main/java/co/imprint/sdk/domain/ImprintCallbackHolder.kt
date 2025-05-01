package co.imprint.sdk.domain

import co.imprint.sdk.domain.model.ImprintCompletionState

internal object ImprintCallbackHolder {
  var onApplicationCompletion: ((ImprintCompletionState, Map<String, Any?>?) -> Unit)? =
    null
}