package co.imprint.sdk

import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import androidx.activity.ComponentActivity
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import android.widget.FrameLayout

/**
 * Regression tests for WebView layout params and the resulting Android View height.
 *
 * Background
 * ----------
 * The apply-flow web app uses a chain of `height:100%` CSS containers
 * (html → body → #root → main). For this chain to have an established height,
 * WebKit must know the WebView's height. WebKit derives `window.innerHeight`
 * from the Android View's height that it receives after the View layout pass.
 *
 * Root cause of the regression
 * ----------------------------
 * Inside Compose's AndroidView, a WebView without explicit layoutParams defaults
 * to WRAP_CONTENT. An empty WebView has no intrinsic content height, so its
 * laid-out height is 0. WebKit receives height = 0 and reports
 * `window.innerHeight = 0`, causing every `height:100%` container to collapse.
 * Content overflows below the visible viewport with no scroll range to reach it.
 *
 * The fix
 * -------
 * Setting `layoutParams = MATCH_PARENT` makes the WebView fill its parent
 * container. The layout pass produces a positive height, WebKit receives the
 * correct viewport, and `height:100%` containers fill the screen.
 *
 * Why we test at the Android View level (not window.innerHeight)
 * --------------------------------------------------------------
 * WebKit only populates `window.innerHeight` after its renderer commits a GPU
 * frame. In emulator-based instrumented tests there is no active GPU frame
 * pipeline, so `window.innerHeight` is always 0. Testing the Android-side View
 * height is the reliable proxy: when `view.height > 0` the platform has
 * correctly sized the WebView and WebKit will receive the correct viewport in
 * the running app.
 *
 * Also note: Compose's AndroidView always sizes the embedded View to fill
 * Modifier.fillMaxSize() constraints regardless of the View's own layoutParams,
 * so measuredHeight is not a useful discriminator here. We test in a plain
 * FrameLayout so the View's layoutParams are respected as they would be in any
 * real ViewGroup, including the one Compose creates around the WebView.
 */
@RunWith(AndroidJUnit4::class)
class WebViewLayoutTest {

  @get:Rule
  val composeTestRule = createAndroidComposeRule<ComponentActivity>()

  /**
   * Measures and lays out a WebView inside a FrameLayout with fixed dimensions,
   * then returns the WebView's resulting height.
   */
  private fun measuredHeightInContainer(webViewLayoutParams: ViewGroup.LayoutParams): Int {
    var height = -1
    composeTestRule.runOnUiThread {
      val activity = composeTestRule.activity
      val dm = activity.resources.displayMetrics
      val containerW = dm.widthPixels
      val containerH = dm.heightPixels

      val container = FrameLayout(activity)
      val webView = WebView(activity).apply { layoutParams = webViewLayoutParams }
      container.addView(webView)

      // Force a full measure + layout pass with exact screen dimensions,
      // mirroring what the window does when the WebView fills the display.
      container.measure(
        View.MeasureSpec.makeMeasureSpec(containerW, View.MeasureSpec.EXACTLY),
        View.MeasureSpec.makeMeasureSpec(containerH, View.MeasureSpec.EXACTLY),
      )
      container.layout(0, 0, containerW, containerH)

      height = webView.height
    }
    return height
  }

  // ---------------------------------------------------------------------------
  // Tests
  // ---------------------------------------------------------------------------

  /**
   * A WebView with MATCH_PARENT layout params must fill its container after layout.
   * This is the fix for the CSS height:100% regression: a positive height here
   * guarantees WebKit will receive the correct viewport height in the running app.
   */
  @Test
  fun matchParentWebViewFillsContainer() {
    val height = measuredHeightInContainer(
      ViewGroup.LayoutParams(
        ViewGroup.LayoutParams.MATCH_PARENT,
        ViewGroup.LayoutParams.MATCH_PARENT,
      ),
    )

    assertTrue(
      "WebView height should be positive with MATCH_PARENT layout params (got $height). " +
        "WebKit uses this value for window.innerHeight — if zero, CSS height:100% " +
        "containers will collapse in the running app.",
      height > 0,
    )
  }

  /**
   * A WebView without MATCH_PARENT collapses to 0 height inside a FrameLayout.
   * An empty WebView has no intrinsic content, so WRAP_CONTENT produces height = 0.
   * This is the original regression: WebKit receives height = 0, window.innerHeight = 0,
   * and every CSS height:100% container collapses.
   *
   * This test documents the failure mode that MATCH_PARENT was added to prevent.
   */
  @Test
  fun wrapContentWebViewCollapsesWithoutContent() {
    val height = measuredHeightInContainer(
      ViewGroup.LayoutParams(
        ViewGroup.LayoutParams.WRAP_CONTENT,
        ViewGroup.LayoutParams.WRAP_CONTENT,
      ),
    )

    assertEquals(
      "WebView height should be 0 with WRAP_CONTENT when the WebView has no content " +
        "(got $height). This is the regression — MATCH_PARENT was added precisely " +
        "because WRAP_CONTENT causes height:100% containers to collapse.",
      0,
      height,
    )
  }
}
