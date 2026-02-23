package co.imprint.sdk

import android.graphics.Bitmap
import android.view.View
import android.view.ViewGroup
import android.webkit.JavascriptInterface
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.SavedStateHandle
import androidx.test.ext.junit.runners.AndroidJUnit4
import co.imprint.sdk.domain.model.ImprintConfiguration
import co.imprint.sdk.domain.model.ImprintEnvironment
import co.imprint.sdk.domain.repository.ImageRepository
import co.imprint.sdk.presentation.ApplicationActivity
import co.imprint.sdk.presentation.ApplicationViewModel
import co.imprint.sdk.presentation.components.WebViewWrapper
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

/**
 * Tests for [WebViewWrapper].
 *
 * Covers:
 * 1. layoutParams = MATCH_PARENT — the fix that prevents height:100% containers from collapsing.
 * 2. WebView settings — javaScriptEnabled, domStorageEnabled, cacheMode, viewport flags.
 * 3. JavaScript bridge — verifies that JS calling window.androidInterface.onMessage() reaches Kotlin.
 */
@RunWith(AndroidJUnit4::class)
class WebViewWrapperTest {

  @get:Rule
  val composeTestRule = createAndroidComposeRule<ComponentActivity>()

  // ---------------------------------------------------------------------------
  // Helpers
  // ---------------------------------------------------------------------------

  /**
   * Builds a minimal [ApplicationViewModel] suitable for tests. Uses SANDBOX environment
   * so the webUrl is a real HTTPS URL but no actual network traffic affects the assertions —
   * we only inspect the WebView's Java-side configuration, not page load results.
   */
  private fun makeTestViewModel(): ApplicationViewModel {
    val config = ImprintConfiguration(
      clientSecret = "test-secret",
      environment = ImprintEnvironment.SANDBOX,
    )
    val handle = SavedStateHandle(
      mapOf(ApplicationActivity.APPLICATION_CONFIGURATION to config),
    )
    val imageRepo = object : ImageRepository {
      override suspend fun getImageBitmap(url: String): Bitmap =
        Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)
    }
    return ApplicationViewModel(imageRepo, handle)
  }

  /** Recursively finds the first [WebView] in a View tree. */
  private fun View.findFirstWebView(): WebView? {
    if (this is WebView) return this
    if (this is ViewGroup) {
      for (i in 0 until childCount) {
        getChildAt(i).findFirstWebView()?.let { return it }
      }
    }
    return null
  }

  /**
   * Composes [WebViewWrapper] with a minimal ViewModel and returns the [WebView] it creates,
   * found by traversing the activity's View hierarchy after Compose has settled.
   */
  private fun composeWebViewWrapper(): WebView? {
    composeTestRule.setContent {
      WebViewWrapper(viewModel = makeTestViewModel())
    }
    composeTestRule.waitForIdle()

    var webView: WebView? = null
    composeTestRule.runOnUiThread {
      webView = composeTestRule.activity.window.decorView.findFirstWebView()
    }
    return webView
  }

  // ---------------------------------------------------------------------------
  // Tests
  // ---------------------------------------------------------------------------

  /**
   * Regression test: WebView must have MATCH_PARENT layout params.
   *
   * Without them the browser engine doesn't receive the correct viewport dimensions, so any
   * CSS container using height:100% collapses to zero height and its content falls below
   * the visible fold with no scroll range to reach it.
   */
  @Test
  fun webViewHasMatchParentLayoutParams() {
    val webView = composeWebViewWrapper()

    assertNotNull("WebView was not found in the view hierarchy after composition", webView)
    assertEquals(
      "WebView layoutParams.width must be MATCH_PARENT — " +
        "removing it causes height:100% containers to collapse",
      ViewGroup.LayoutParams.MATCH_PARENT,
      webView!!.layoutParams.width,
    )
    assertEquals(
      "WebView layoutParams.height must be MATCH_PARENT — " +
        "removing it causes height:100% containers to collapse",
      ViewGroup.LayoutParams.MATCH_PARENT,
      webView.layoutParams.height,
    )
  }

  /**
   * Verifies every WebView setting that the SDK relies on.
   * A regression in any of these would silently break SDK functionality.
   *
   * Settings are read on the main thread (WebView enforces this via checkThread())
   * and the captured values are asserted on the test thread afterward.
   */
  @Test
  fun webViewSettingsMatchProductionConfiguration() {
    val webView = composeWebViewWrapper()
    assertNotNull(webView)

    var jsEnabled = false
    var domStorage = false
    var cacheMode = -1
    var wideViewPort = false
    var overviewMode = false

    composeTestRule.runOnUiThread {
      webView!!.settings.also {
        jsEnabled = it.javaScriptEnabled
        domStorage = it.domStorageEnabled
        cacheMode = it.cacheMode
        wideViewPort = it.useWideViewPort
        overviewMode = it.loadWithOverviewMode
      }
    }

    assertTrue(
      "javaScriptEnabled must be true — the SDK's entire web UI depends on it",
      jsEnabled,
    )
    assertTrue(
      "domStorageEnabled must be true — web app uses localStorage/sessionStorage",
      domStorage,
    )
    assertEquals(
      "cacheMode must be LOAD_NO_CACHE to ensure the latest web content is always loaded",
      WebSettings.LOAD_NO_CACHE,
      cacheMode,
    )
    assertTrue(
      "useWideViewPort must be true for correct viewport meta-tag handling",
      wideViewPort,
    )
    assertTrue(
      "loadWithOverviewMode must be true — required alongside useWideViewPort",
      overviewMode,
    )
  }

  /**
   * Verifies that JavaScript calling `window.androidInterface.onMessage(json)` reaches Kotlin.
   *
   * This tests the pattern used in [WebViewWrapper] in isolation: a WebView is configured with
   * javaScriptEnabled and the same interface name ("androidInterface"), then HTML with an inline
   * script is loaded to trigger the call. The test asserts the Kotlin callback fires with the
   * expected JSON string.
   */
  @Test
  fun javascriptInterfaceIsCallableFromJs() {
    val pageLoaded = CountDownLatch(1)
    val bridgeCalled = CountDownLatch(1)
    var receivedJson: String? = null
    var webView: WebView? = null

    composeTestRule.setContent {
      AndroidView(
        factory = { ctx ->
          WebView(ctx).apply {
            layoutParams = ViewGroup.LayoutParams(
              ViewGroup.LayoutParams.MATCH_PARENT,
              ViewGroup.LayoutParams.MATCH_PARENT,
            )
            settings.javaScriptEnabled = true

            addJavascriptInterface(
              object {
                @JavascriptInterface
                fun onMessage(data: String) {
                  receivedJson = data
                  bridgeCalled.countDown()
                }
              },
              "androidInterface",
            )

            webViewClient = object : WebViewClient() {
              override fun onPageFinished(view: WebView?, url: String?) {
                pageLoaded.countDown()
              }
            }

            webView = this
          }
        },
        modifier = Modifier.fillMaxSize(),
      )
    }

    composeTestRule.waitForIdle()

    composeTestRule.runOnUiThread {
      // The inline script runs synchronously during HTML parsing, before onPageFinished.
      // loadData() uses a null origin; newer WebView blocks JS interface access from null-origin
      // pages. loadDataWithBaseURL gives the page a real origin so androidInterface is reachable.
      webView?.loadDataWithBaseURL(
        "file:///android_asset/",
        """<html><body><script>
          window.androidInterface.onMessage('{"event_name":"open","source":"imprint"}');
        </script></body></html>""",
        "text/html",
        "UTF-8",
        null,
      )
    }

    assertTrue("Page did not load within 5 seconds", pageLoaded.await(5, TimeUnit.SECONDS))
    assertTrue(
      "androidInterface.onMessage was not called from JavaScript within 3 seconds",
      bridgeCalled.await(3, TimeUnit.SECONDS),
    )
    assertEquals(
      """{"event_name":"open","source":"imprint"}""",
      receivedJson,
    )
  }
}
