package co.imprint.sdk.domain.model

enum class ImprintProcessState {
  INITIALIZED,
  APPLICATION_STARTED,
  OFFER_PRESENTED,
  OFFER_ACCEPTED,
  OFFER_DECLINED,
  REJECTED,
  APPLICATION_REVIEW,
  CREDIT_FROZEN,
  ABANDONED,
  ERROR;

  companion object {
    fun fromString(value: String?): ImprintProcessState {
      return try {
        value?.let { enumValueOf<ImprintProcessState>(it) } ?: ABANDONED
      } catch (e: IllegalArgumentException) {
        ABANDONED
      }
    }
  }
}

fun ImprintProcessState.toCompletionState(): ImprintCompletionState {
  return when (this) {
    ImprintProcessState.OFFER_ACCEPTED -> ImprintCompletionState.OFFER_ACCEPTED
    ImprintProcessState.REJECTED -> ImprintCompletionState.REJECTED
    ImprintProcessState.ERROR -> ImprintCompletionState.ERROR
    else -> ImprintCompletionState.IN_PROGRESS
  }
}