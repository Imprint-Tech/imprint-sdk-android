package co.imprint.imprintsdk.application

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest

@Composable
fun ApplicationView(viewModel: ApplicationViewModel) {
  val context = LocalContext.current
  val logoUrl by viewModel.logoUrl.collectAsState()

  Column(modifier = Modifier.fillMaxSize().background(Color.White)) {
    Spacer(modifier = Modifier.weight(1f))

    Row(
      modifier = Modifier
        .fillMaxWidth()
        .height(56.dp)
        .padding(horizontal = 16.dp),
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      logoUrl?.let { url ->
        AsyncImage(
          model = ImageRequest.Builder(context).data(url).build(),
          contentDescription = null,
          modifier = Modifier
            .size(48.dp)
            .padding(16.dp)
        )
      }

      IconButton(
        onClick = {
          viewModel.onDismiss()
        },
        modifier = Modifier.size(48.dp)
      ) {
        Icon(
          imageVector = Icons.Default.Close,
          contentDescription = "Close",
          tint = Color(0xFF232323)
        )
      }
    }

    WebViewWrapper(viewModel)
  }
}