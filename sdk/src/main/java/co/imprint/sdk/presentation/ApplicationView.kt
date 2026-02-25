package co.imprint.sdk.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.material3.MaterialTheme
import co.imprint.sdk.presentation.components.AppBar
import co.imprint.sdk.presentation.components.WebViewWrapper

@Composable
internal fun ApplicationView(viewModel: ApplicationViewModel) {
  val bitmap = viewModel.logoBitmap.collectAsState().value

  Scaffold(
    modifier = Modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.surface),
    containerColor = MaterialTheme.colorScheme.surface,
    topBar = {
      AppBar(
        bitmap = bitmap,
        onDismiss = {
          viewModel.onDismiss()
        },
        modifier = Modifier.statusBarsPadding()
      )
    },
    contentWindowInsets = WindowInsets.systemBars,
  ) { innerPadding ->
    WebViewWrapper(viewModel = viewModel, modifier = Modifier.padding(innerPadding))
  }
}
