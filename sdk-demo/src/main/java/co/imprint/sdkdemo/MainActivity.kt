package co.imprint.sdkdemo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.viewmodel.compose.viewModel
import co.imprint.sdkdemo.ui.theme.ImprintSDKDemoTheme

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContent {
      ImprintSDKDemoTheme {
        val viewModel: MainViewModel = viewModel()
        MainScreen(viewModel = viewModel)
      }
    }
  }
}