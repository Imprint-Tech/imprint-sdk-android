package co.imprint.imprintsdk.application

import android.util.Log
import android.webkit.JavascriptInterface
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import org.json.JSONObject

@Composable
internal fun WebViewWrapper(
  viewModel: ApplicationViewModel,
  modifier: Modifier = Modifier,
) {
  AndroidView(
    modifier = modifier.fillMaxSize(),
    factory = { context ->
      WebView.setWebContentsDebuggingEnabled(true)
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

                val completionData = dataJson?.let { jsonToMap(it) }
                val state = ImprintConfiguration.CompletionState.fromString(eventName)
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
    update = { webView ->
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

private fun jsonToMap(jsonObject: JSONObject): Map<String, String?> {
  val map = mutableMapOf<String, String?>()
  val keys = jsonObject.keys()
  while (keys.hasNext()) {
    val key = keys.next()
    val value = jsonObject.optString(key) // Safely get the string value
    map[key] = value
  }
  return map
}