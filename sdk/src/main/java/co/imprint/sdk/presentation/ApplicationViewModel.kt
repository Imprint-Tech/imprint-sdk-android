package co.imprint.sdk.presentation

import android.graphics.Bitmap
import androidx.annotation.VisibleForTesting
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.imprint.sdk.di.IsolatedKoinComponent
import co.imprint.sdk.domain.ImprintCallbackHolder
import co.imprint.sdk.domain.model.ImprintCompletionState
import co.imprint.sdk.domain.model.ImprintConfiguration
import co.imprint.sdk.domain.model.ImprintErrorCode
import co.imprint.sdk.domain.model.ImprintProcessState
import co.imprint.sdk.domain.model.toCompletionState
import co.imprint.sdk.domain.repository.ImageRepository
import co.imprint.sdk.presentation.utils.toMap
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
) : ViewModel(), IsolatedKoinComponent {

  private val configuration: ImprintConfiguration =
    state[ApplicationActivity.APPLICATION_CONFIGURATION]
      ?: throw IllegalStateException("Imprint configuration is required to initialize the SDK")

  val webUrl: String = configuration.webUrl

  private val _logoBitmap = MutableStateFlow<Bitmap?>(null)
  val logoBitmap: StateFlow<Bitmap?> = _logoBitmap.asStateFlow()

  private var completionState: ImprintCompletionState = ImprintCompletionState.IN_PROGRESS
  @VisibleForTesting
  private var processState: ImprintProcessState? = null
  @VisibleForTesting
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
    onCompletion?.invoke(completionState, completionData)
    finishActivity()
  }

  private fun loadImageBitmap(url: String) = viewModelScope.launch {
    runCatching {
      imageRepository.getImageBitmap(url)
    }.onSuccess { image ->
      _logoBitmap.value = image
    }.onFailure {
      it.printStackTrace()
      _logoBitmap.value = null
    }
  }

  fun processEventData(eventData: JSONObject?) {
    eventData?.let {
      val logoURL = eventData.optString(Constants.LOGO_URL)
      updateLogoUrl(url = logoURL)

      val resultMap = it.toMap()

      val eventName = eventData.optString(Constants.EVENT_NAME)
      val state = ImprintProcessState.fromString(eventName)

      if (state == ImprintProcessState.ERROR) {
        val errorCode = eventData.optString(Constants.ERROR_CODE)
        resultMap["error_code"] = ImprintErrorCode.fromString(errorCode)
      }
      completionData = resultMap

      if (state == ImprintProcessState.CLOSED) {
        onDismiss()
      } else {
        processState = state
      }
    }
  }
}

sealed class NavigationEvent {
  object Finish : NavigationEvent()
}