package co.imprint.sdk.domain.model

enum class ImprintProcessState {
  OFFER_ACCEPTED,
  REJECTED,
  IN_PROGRESS,
  ERROR,
  CLOSED;

  companion object {
    fun fromString(value: String?): ImprintProcessState? {
      return try {
        value?.let { enumValueOf<ImprintProcessState>(it) }
      } catch (e: IllegalArgumentException) {
        null
      }
    }
  }
}

fun ImprintProcessState?.toCompletionState(): ImprintCompletionState {
  return when (this) {
    ImprintProcessState.OFFER_ACCEPTED -> ImprintCompletionState.OFFER_ACCEPTED
    ImprintProcessState.REJECTED -> ImprintCompletionState.REJECTED
    ImprintProcessState.ERROR -> ImprintCompletionState.ERROR
    else -> ImprintCompletionState.IN_PROGRESS
  }
}