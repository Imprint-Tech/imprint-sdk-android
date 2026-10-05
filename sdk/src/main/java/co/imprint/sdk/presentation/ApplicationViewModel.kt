package co.imprint.sdk.presentation

import android.graphics.Bitmap
import androidx.annotation.VisibleForTesting
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.imprint.sdk.domain.ImprintCallbackHolder
import co.imprint.sdk.domain.model.ImprintCompletionState
import co.imprint.sdk.domain.model.ImprintConfiguration
import co.imprint.sdk.domain.model.ImprintErrorCode
import co.imprint.sdk.domain.model.ImprintProcessState
import co.imprint.sdk.domain.model.toCompletionState
import co.imprint.sdk.domain.repository.ImageRepository
import co.imprint.sdk.presentation.utils.toMap
import android.util.Log
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.json.JSONObject

internal class ApplicationViewModel(
  private val imageRepository: ImageRepository,
  state: SavedStateHandle,
) : ViewModel() {

  private val configuration: ImprintConfiguration =
    state[ApplicationActivity.APPLICATION_CONFIGURATION]
      ?: throw IllegalStateException("Imprint configuration is required to initialize the SDK")

  val webUrl: String = configuration.webUrl

  private val _logoBitmap = MutableStateFlow<Bitmap?>(null)
  val logoBitmap: StateFlow<Bitmap?> = _logoBitmap.asStateFlow()

  private var completionState: ImprintCompletionState = ImprintCompletionState.IN_PROGRESS
  private var processState: ImprintProcessState? = null
  private var completionData: Map<String, Any?>? = null

  private val _navigationEvents = MutableSharedFlow<NavigationEvent>()
  val navigationEvents = _navigationEvents.asSharedFlow()

  private fun finishActivity() {
    viewModelScope.launch {
      _navigationEvents.emit(NavigationEvent.Finish)
    }
  }

  fun updateLogoUrl(url: String) {
    if (url.isNotEmpty()) {
      loadImageBitmap(url = url)
    }
  }

  fun onDismiss() {
    completionState = processState.toCompletionState()
    val onCompletion = ImprintCallbackHolder.onApplicationCompletion
    ImprintCallbackHolder.onApplicationCompletion = null
    ImprintCallbackHolder.onApplicationEvent = null
    onCompletion?.invoke(completionState, completionData)
    finishActivity()
  }

  private fun loadImageBitmap(url: String) = viewModelScope.launch {
    runCatching {
      imageRepository.getImageBitmap(url)
    }.onSuccess { image ->
      _logoBitmap.value = image
    }.onFailure {
      Log.e("Imprint", "Failed to load logo image", it)
      _logoBitmap.value = null
    }
  }

  fun processEventData(eventData: JSONObject?) {
    if (eventData == null) return

    val logoURL = eventData.optString(Constants.LOGO_URL)
    if (logoURL.isNotEmpty()) {
      updateLogoUrl(url = logoURL)
      return
    }

    val eventName = eventData.optString(Constants.EVENT_NAME)
    if (eventName.isEmpty()) return

    val source = if (eventData.has(Constants.SOURCE)) {
      eventData.optString(Constants.SOURCE)
    } else {
      Constants.PARTNER_SOURCE
    }

    when (source) {
      Constants.PARTNER_SOURCE -> handlePartnerEvent(eventName, eventData)
      Constants.INTERNAL_SOURCE -> handleInternalEvent(eventName, eventData)
    }
  }

  private fun handlePartnerEvent(eventName: String, eventData: JSONObject) {
    val state = ImprintProcessState.fromString(eventName)
    val resultData = processResultData(eventData, state)
    notifyEvent(eventName, resultData)
    handleEventLifecycle(eventData, state, resultData)
  }

  private fun notifyEvent(eventName: String, resultData: Map<String, Any?>) {
    runCatching {
      ImprintCallbackHolder.onApplicationEvent?.invoke(eventName, resultData)
    }.onFailure {
      Log.e("Imprint", "onEvent callback failed for $eventName", it)
    }
  }

  private fun handleInternalEvent(eventName: String, eventData: JSONObject) {
    val state = ImprintProcessState.fromString(eventName)
    val resultData = processResultData(eventData, state)
    handleEventLifecycle(eventData, state, resultData)
  }

  private fun handleEventLifecycle(
    eventData: JSONObject,
    state: ImprintProcessState?,
    resultData: Map<String, Any?>,
  ) {

    if (!eventData.has(Constants.TIER)) {
      updateLegacyOutcome(state, resultData)
      return
    }

    when (EventTier.fromString(eventData.optString(Constants.TIER))) {
      EventTier.INTERMEDIATE -> Unit
      EventTier.OUTCOME -> updateOutcome(state, resultData)
      EventTier.TERMINAL -> if (state == ImprintProcessState.CLOSED) onDismiss()
      null -> Unit
    }
  }

  private fun updateLegacyOutcome(
    state: ImprintProcessState?,
    resultData: Map<String, Any?>,
  ) {
    if (state == ImprintProcessState.CLOSED) {
      onDismiss()
      return
    }
    updateOutcome(state, resultData)
  }

  private fun updateOutcome(
    state: ImprintProcessState?,
    resultData: Map<String, Any?>,
  ) {
    if (state == null || state == ImprintProcessState.CLOSED) return
    processState = state
    completionData = resultData
  }

  @VisibleForTesting
  internal fun processResultData(eventData: JSONObject, state: ImprintProcessState?): MutableMap<String, Any?> {
    val resultData = eventData.toMap().apply {
      // Exclude fields from the result data
      remove(Constants.EVENT_NAME)
      remove(Constants.SOURCE)
    }

    // Add error code if in ERROR state
    if (state == ImprintProcessState.ERROR) {
      val errorCode = eventData.optString(Constants.ERROR_CODE)
      resultData["error_code"] = ImprintErrorCode.fromString(errorCode)
    }

    return resultData
  }
}

private enum class EventTier {
  INTERMEDIATE,
  OUTCOME,
  TERMINAL;

  companion object {
    fun fromString(value: String?): EventTier? =
      entries.firstOrNull { it.name.equals(value, ignoreCase = true) }
  }
}

sealed class NavigationEvent {
  object Finish : NavigationEvent()
}
