package co.imprint.sdk.presentation

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.imprint.sdk.di.IoDispatcher
import co.imprint.sdk.domain.ImprintCallbackHolder
import co.imprint.sdk.domain.model.CompletionState
import co.imprint.sdk.domain.model.ImprintConfiguration
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import javax.inject.Inject

@HiltViewModel
internal class ApplicationViewModel @Inject constructor(
  @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
  state: SavedStateHandle,
) : ViewModel() {

  private val configuration: ImprintConfiguration =
    state[ApplicationActivity.APPLICATION_CONFIGURATION]
      ?: throw IllegalStateException("Imprint configuration is required to initialize the SDK")

  val webUrl: String = configuration.webUrl

  private val _logoBitmap = MutableStateFlow<Bitmap?>(null)
  val logoBitmap: StateFlow<Bitmap?> = _logoBitmap.asStateFlow()

  private var completionState: CompletionState = CompletionState.ABANDONED
  private var completionData: Map<String, String?>? = null

  fun updateLogoUrl(url: String) {
    loadImageBitmap(url = url)
  }

  fun updateCompletionState(
    state: CompletionState,
    data: Map<String, String?>?,
  ) {
    completionState = state
    completionData = data
  }

  fun onDismiss() {
    val onCompletion = ImprintCallbackHolder.onApplicationCompletion
    onCompletion?.invoke(completionState, completionData)
  }

  private fun loadImageBitmap(url: String) {
    viewModelScope.launch(ioDispatcher) {
      try {
        val connection = URL(url).openConnection() as HttpURLConnection
        connection.doInput = true
        connection.connect()
        val inputStream: InputStream = connection.inputStream
        val loadedBitmap = BitmapFactory.decodeStream(inputStream)
        _logoBitmap.value = loadedBitmap
      } catch (e: Exception) {
        e.printStackTrace()
        _logoBitmap.value = null
      }
    }
  }
}
