package co.imprint.sdk.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import co.imprint.sdk.presentation.components.AppBar
import co.imprint.sdk.presentation.components.WebViewWrapper

@Composable
internal fun ApplicationView(viewModel: ApplicationViewModel) {
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
        },
      )
    }
  ) { innerPadding ->
    WebViewWrapper(viewModel = viewModel, modifier = Modifier.padding(innerPadding))
  }
}
