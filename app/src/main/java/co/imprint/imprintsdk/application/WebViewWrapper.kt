package co.imprint.imprintsdk.application

import android.util.Log
import android.webkit.JavascriptInterface
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import org.json.JSONObject

@Composable
fun WebViewWrapper(viewModel: ApplicationViewModel) {
  AndroidView(
    modifier = Modifier.fillMaxSize(),
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
              Log.d("Kaichang", "Received message: $data")
              try {
                // Parse the incoming JSON data
                val jsonObject = JSONObject(data)
                val eventName = jsonObject.optString(Constants.EVENT_NAME)
                val metadataJson = jsonObject.optJSONObject(Constants.METADATA)
                val logoURL = jsonObject.optString(Constants.LOGO_URL)
                Log.d("Kaichang", "logoUrl: $logoURL")
                viewModel.updateLogoUrl(url = logoURL)

                // Convert metadata JSON to Map<String, String>
                val metadata = metadataJson?.let { jsonToMap(it) }
                Log.d("Kaichang", "metaData: $metadata")

                // Update ViewModel or handle the received data
                val state = ImprintConfiguration.CompletionState.fromString(eventName)
                viewModel.updateCompletionState(state, metadata)
              } catch (e: Exception) {

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

object Constants {
  const val CALLBACK_HANDLER_NAME = "AndroidInterface"
  const val LOGO_URL = "logoUrl"
  const val EVENT_NAME = "eventName"
  const val METADATA = "metadata"
}

fun jsonToMap(jsonObject: JSONObject): Map<String, String> {
  val map = mutableMapOf<String, String>()
  val keys = jsonObject.keys()
  while (keys.hasNext()) {
    val key = keys.next()
    val value = jsonObject.optString(key) // Safely get the string value
    map[key] = value
  }
  return map
}