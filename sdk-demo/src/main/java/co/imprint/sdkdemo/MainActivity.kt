package co.imprint.sdkdemo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.viewmodel.compose.viewModel
import co.imprint.sdkdemo.ui.theme.ImprintSDKDemoTheme
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.GlobalContext.startKoin

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    //Starting Koin instance to test the Koin context isolation of the SDK
    startKoin {
      androidLogger()
      androidContext(applicationContext)
      //No modules loaded
    }

    setContent {
      ImprintSDKDemoTheme {
        val viewModel: MainViewModel = viewModel()
        MainScreen(viewModel = viewModel)
      }
    }
  }
}