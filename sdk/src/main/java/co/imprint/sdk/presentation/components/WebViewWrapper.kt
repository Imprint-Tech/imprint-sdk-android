package co.imprint.sdk.presentation.components

import android.annotation.SuppressLint
import android.util.Log
import android.webkit.JavascriptInterface
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import co.imprint.sdk.domain.model.ImprintProcessState
import co.imprint.sdk.presentation.utils.toMapOrNull
import co.imprint.sdk.presentation.ApplicationViewModel
import org.json.JSONObject

@SuppressLint("SetJavaScriptEnabled")
@Composable
internal fun WebViewWrapper(
  viewModel: ApplicationViewModel,
  modifier: Modifier = Modifier,
) {
  AndroidView(
    modifier = modifier.fillMaxSize(),
    factory = { context ->
      WebView(context).apply {
        // Configure WebView settings
        settings.apply {
          javaScriptEnabled = true
          domStorageEnabled = true
          cacheMode = WebSettings.LOAD_NO_CACHE
          useWideViewPort = true
          loadWithOverviewMode = true
        }

        clearCache(true)
        clearHistory()

        // Add JavaScript interface
        addJavascriptInterface(
          object {
            @JavascriptInterface
            fun onMessage(data: String) {
              try {
                val jsonObject = JSONObject(data)
                val eventName = jsonObject.optString(Constants.EVENT_NAME)
                val dataJson = jsonObject.optJSONObject(Constants.DATA)
                val logoURL = jsonObject.optString(Constants.LOGO_URL)
                viewModel.updateLogoUrl(url = logoURL)

                val completionData = dataJson.toMapOrNull()
                val state = ImprintProcessState.fromString(eventName)
                viewModel.updateCompletionState(state, completionData)
              } catch (e: Exception) {
                Log.e("WebViewWrapper", "onMessage: Error parsing data from Web view")
              }
            }
          },
          Constants.CALLBACK_HANDLER_NAME,
        )

        webViewClient = WebViewClient()
        loadUrl(viewModel.webUrl)
      }
    },
    update = { _ ->
      // Optional: Additional WebView updates if needed
    }
  )
}

internal object Constants {
  const val CALLBACK_HANDLER_NAME = "androidInterface"
  const val LOGO_URL = "logoUrl"
  const val EVENT_NAME = "eventName"
  const val DATA = "data"
}