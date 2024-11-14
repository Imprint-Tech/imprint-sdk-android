package co.imprint.imprintsdk.application

import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView

@Composable
fun WebViewWrapper(viewModel: ApplicationViewModel) {
  AndroidView(
    modifier = Modifier.fillMaxSize(),
    factory = { context ->
      WebView(context).apply {
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