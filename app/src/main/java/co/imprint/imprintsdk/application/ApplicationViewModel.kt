package co.imprint.imprintsdk.application

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL

internal class ApplicationViewModel(private val configuration: ImprintConfiguration) : ViewModel() {
  private val host = when (configuration.environment) {
    ImprintConfiguration.Environment.STAGING -> "https://apply.stg.imprintapi.co"
    ImprintConfiguration.Environment.SANDBOX -> "https://apply.sbx.imprint.co"
    ImprintConfiguration.Environment.PRODUCTION -> "https://apply.imprint.co"
  }
  val webUrl = "$host/start?client_secret=${configuration.clientSecret}&partner_reference=${configuration.partnerReference}"

  private val _logoBitmap = MutableStateFlow<Bitmap?>(null)
  val logoBitmap: StateFlow<Bitmap?> = _logoBitmap.asStateFlow()

  private var completionState: ImprintConfiguration.CompletionState =
    ImprintConfiguration.CompletionState.ABANDONED
  private var completionData: Map<String, String?>? = null

  fun updateLogoUrl(url: String) {
   loadImageBitmap(url = url)
  }

  fun updateCompletionState(
    state: ImprintConfiguration.CompletionState,
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
    viewModelScope.launch(Dispatchers.IO) {
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
