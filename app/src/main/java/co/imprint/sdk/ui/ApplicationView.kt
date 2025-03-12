package co.imprint.sdk.ui

import android.app.Activity
import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import co.imprint.sdk.viewmodel.ApplicationViewModel

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

@Composable
private fun AppBar(
  bitmap: Bitmap?,
  onDismiss: () -> Unit,
  modifier: Modifier = Modifier,
) {
  Row(
    modifier = modifier
      .fillMaxWidth()
      .height(56.dp)
      .padding(horizontal = 16.dp),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically,
  ) {
    LogoImage(bitmap)
    CloseButton(onDismiss)
  }
}

@Composable
private fun LogoImage(bitmap: Bitmap?) {
  if (bitmap != null) {
    Image(
      bitmap = bitmap.asImageBitmap(),
      contentDescription = null,
      modifier = Modifier
        .wrapContentWidth()
        .padding(16.dp),
    )
  }
  else Box(modifier = Modifier.size(56.dp))
}

@Composable
private fun CloseButton(onDismiss: () -> Unit) {
  IconButton(
    onClick = onDismiss,
    modifier = Modifier.size(24.dp),
  ) {
    Icon(
      imageVector = Icons.Default.Close,
      contentDescription = "Close",
      tint = Color.Black,
    )
  }
}