package co.imprint.sdk.domain.model

enum class ImprintProcessState {
  INITIALIZED,
  APPLICATION_STARTED,
  OFFER_PRESENTED,
  OFFER_ACCEPTED,
  REJECTED,
  APPLICATION_REVIEW,
  CREDIT_FROZEN,
  CUSTOMER_CLOSED,
  IMPRINT_CLOSED,
  ERROR;

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