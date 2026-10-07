package co.imprint.sdk

import android.graphics.Bitmap
import android.os.Handler
import android.os.Looper
import android.webkit.WebView
import androidx.lifecycle.SavedStateHandle
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import co.imprint.sdk.domain.ImprintCallbackHolder
import co.imprint.sdk.domain.model.ImprintCompletionState
import co.imprint.sdk.domain.model.ImprintConfiguration
import co.imprint.sdk.domain.model.ImprintEnvironment
import co.imprint.sdk.domain.repository.ImageRepository
import co.imprint.sdk.presentation.ApplicationActivity
import co.imprint.sdk.presentation.ApplicationViewModel
import co.imprint.sdk.presentation.components.ImprintJavascriptBridge
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

@RunWith(AndroidJUnit4::class)
class TieredWebViewBridgeTest {
  @Test
  fun tieredEventsReachPartnerCallbacksThroughAndroidWebView() {
    val instrumentation = InstrumentationRegistry.getInstrumentation()
    val mainHandler = Handler(Looper.getMainLooper())
    val config = ImprintConfiguration("test-secret", ImprintEnvironment.SANDBOX)
    val state = SavedStateHandle(mapOf(ApplicationActivity.APPLICATION_CONFIGURATION to config))
    val imageRepository = object : ImageRepository {
      override suspend fun getImageBitmap(url: String): Bitmap =
        Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)
    }
    val viewModel = ApplicationViewModel(imageRepository, state)
    val eventLatch = CountDownLatch(3)
    val completionLatch = CountDownLatch(1)
    val receivedEvents = CopyOnWriteArrayList<String>()
    val completions = CopyOnWriteArrayList<ImprintCompletionState>()
    ImprintCallbackHolder.onApplicationEvent = { name, data ->
      receivedEvents.add("$name [${data?.get("tier")}]")
      eventLatch.countDown()
    }
    ImprintCallbackHolder.onApplicationCompletion = { state, _ ->
      completions.add(state)
      completionLatch.countDown()
    }

    var webView: WebView? = null
    try {
      instrumentation.runOnMainSync {
        webView = WebView(instrumentation.targetContext).apply {
          settings.javaScriptEnabled = true
          addJavascriptInterface(
            ImprintJavascriptBridge(viewModel) { action -> mainHandler.post(action) },
            "androidInterface",
          )
          loadDataWithBaseURL(
            "https://example.test/",
            """<html><script>
              if (window.androidInterface.supportsEventTiers()) {
                const send = (name, source, tier) =>
                  window.androidInterface.onMessage(JSON.stringify({event_name: name, source: source, tier: tier}));
                send('OFFER_ACCEPTED', 'imprint_web_app', 'outcome');
                send('ACCOUNT_LINK_RESULT', 'imprint_web_app', 'intermediate');
                send('CLOSED', 'imprint_web_app', 'terminal');
                send('CLOSED', 'imprint_internal_event', 'terminal');
              }
            </script></html>""",
            "text/html", "UTF-8", null,
          )
        }
      }

      assertTrue("Partner events did not arrive", eventLatch.await(10, TimeUnit.SECONDS))
      assertTrue("Completion did not arrive", completionLatch.await(10, TimeUnit.SECONDS))
      assertEquals(
        listOf("OFFER_ACCEPTED [outcome]", "ACCOUNT_LINK_RESULT [intermediate]", "CLOSED [terminal]"),
        receivedEvents.toList(),
      )
      assertEquals(listOf(ImprintCompletionState.OFFER_ACCEPTED), completions.toList())
    } finally {
      instrumentation.runOnMainSync { webView?.destroy() }
      ImprintCallbackHolder.onApplicationCompletion = null
      ImprintCallbackHolder.onApplicationEvent = null
    }
  }
}
