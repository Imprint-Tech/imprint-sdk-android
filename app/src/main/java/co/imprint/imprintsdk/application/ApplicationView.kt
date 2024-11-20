package co.imprint.imprintsdk.application

import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImagePainter
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import coil.request.ImageRequest

@Composable
fun ApplicationView(viewModel: ApplicationViewModel) {
  val context = LocalContext.current
  val activity = context as? Activity
  val logoUrl by viewModel.logoUrl.collectAsState()

  Column(
    modifier = Modifier
      .fillMaxSize()
      .background(Color.White)
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .height(56.dp)
        .padding(horizontal = 16.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically,
    ) {
      SubcomposeAsyncImage(
        model = ImageRequest.Builder(context).data(logoUrl).build(),
        contentDescription = null,
        modifier = Modifier
          .wrapContentWidth()
          .padding(16.dp),
      ) {
        when (painter.state) {
          is AsyncImagePainter.State.Success ->
            SubcomposeAsyncImageContent(contentScale = ContentScale.Fit)

          else -> Box(
            modifier = Modifier
              .width(56.dp)
              .fillMaxHeight()
          )
        }
      }

      IconButton(
        onClick = {
          viewModel.onDismiss()
          activity?.finish()
        },
        modifier = Modifier.size(24.dp),
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