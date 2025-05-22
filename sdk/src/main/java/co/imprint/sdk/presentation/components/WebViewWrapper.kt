package co.imprint.sdk.presentation.components

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Message
import android.util.Log
import android.webkit.JavascriptInterface
import android.webkit.WebChromeClient
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import co.imprint.sdk.presentation.ApplicationViewModel
import co.imprint.sdk.presentation.Constants
import org.json.JSONObject
import androidx.core.net.toUri

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

          javaScriptCanOpenWindowsAutomatically = true
          setSupportMultipleWindows(true)
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
                viewModel.processEventData(jsonObject)
              } catch (e: Exception) {
                Log.e("WebViewWrapper", "onMessage: Error parsing data from Web view")
              }
            }
          },
          Constants.CALLBACK_HANDLER_NAME,
        )

        webViewClient = WebViewClient()

        webChromeClient = object : WebChromeClient() {
          override fun onCreateWindow(
            view: WebView?,
            isDialog: Boolean,
            isUserGesture: Boolean,
            resultMsg: Message?
          ): Boolean {
            if (view == null) return false
            val newWebView = WebView(view.context)

            newWebView.webViewClient = object : WebViewClient() {
              override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                val url = request?.url?.toString()
                if (!url.isNullOrEmpty()) {
                  try {
                    val intent = Intent(Intent.ACTION_VIEW, url.toUri())
                    context.startActivity(intent)
                    return true
                  } catch (e: Exception) {
                    Log.e("WebViewWrapper", "Cannot open URL: $url", e)
                  }
                }
                return false
              }
            }

            val transport = resultMsg?.obj as? WebView.WebViewTransport
            transport?.webView = newWebView
            resultMsg?.sendToTarget()

            return true
          }
        }
        loadUrl(viewModel.webUrl)
      }
    },
    update = { _ ->
      // Optional: Additional WebView updates if needed
    }
  )
}