package co.imprint.sdk.presentation

import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import co.imprint.sdk.presentation.components.AppBar
import co.imprint.sdk.presentation.components.WebViewWrapper

@Composable
internal fun ApplicationView(viewModel: ApplicationViewModel) {
  val context = LocalContext.current
  val activity = context as? Activity
  val bitmap = viewModel.logoBitmap.collectAsState().value

  Scaffold(
    modifier = Modifier
      .fillMaxSize()
      .background(Color.White),
    containerColor = Color.White,
    topBar = {
      AppBar(
        bitmap = bitmap,
        onDismiss = {
          viewModel.onDismiss()
          activity?.finish()
        },
      )
    }
  ) { innerPadding ->
    WebViewWrapper(viewModel = viewModel, modifier = Modifier.padding(innerPadding))
  }
}
