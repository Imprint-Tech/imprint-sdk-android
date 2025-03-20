package co.imprint.sdk.presentation

import android.graphics.Bitmap
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.imprint.sdk.di.IsolatedKoinComponent
import co.imprint.sdk.domain.ImprintCallbackHolder
import co.imprint.sdk.domain.model.ImprintCompletionState
import co.imprint.sdk.domain.model.ImprintConfiguration
import co.imprint.sdk.domain.repository.ImageRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

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

  private var completionState: ImprintCompletionState = ImprintCompletionState.ABANDONED
  private var completionData: Map<String, String?>? = null

  fun updateLogoUrl(url: String) {
    loadImageBitmap(url = url)
  }

  fun updateCompletionState(
    state: ImprintCompletionState,
    data: Map<String, String?>?,
  ) {
    completionState = state
    completionData = data
  }

  fun onDismiss() {
    val onCompletion = ImprintCallbackHolder.onApplicationCompletion
    onCompletion?.invoke(completionState, completionData)
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
}
