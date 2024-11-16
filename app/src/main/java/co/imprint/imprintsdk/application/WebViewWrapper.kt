package co.imprint.imprintsdk.application

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
              // Parse JSON data from JavaScript callback
              val jsonObject = JSONObject(data)
              val logoUrl = jsonObject.optString(Constants.LOGO_URL)
              val eventName = jsonObject.optString(Constants.EVENT_NAME)
              val metadata = jsonObject.optJSONObject(Constants.METADATA)

              // Update ViewModel or handle data
              logoUrl?.let { viewModel.updateLogoUrl(it) }
              eventName?.let { event ->
                when (event) {
                }
              }
            }
          },
          Constants.CALLBACK_HANDLER_NAME // Name of the JavaScript object
        )

        webViewClient = object : WebViewClient() {
          override fun shouldOverrideUrlLoading(
            view: WebView,
            request: WebResourceRequest
          ): Boolean {
            return false
          }

          override fun onPageFinished(view: WebView, url: String) {
            // Inject JavaScript for callback handling
            view.evaluateJavascript(
              """
                            window.${Constants.CALLBACK_HANDLER_NAME} = function(data) {
                                Android.onMessage(JSON.stringify(data));
                            }
                            """.trimIndent(), null)
          }
        }
        loadUrl(viewModel.webUrl)
      }
    },
    update = { webView ->
      // Optional: Additional WebView updates if needed
    }
  )
}

object Constants {
  const val CALLBACK_HANDLER_NAME = "imprintWebCallback"
  const val LOGO_URL = "logoUrl"
  const val EVENT_NAME = "eventName"
  const val METADATA = "metadata"
}