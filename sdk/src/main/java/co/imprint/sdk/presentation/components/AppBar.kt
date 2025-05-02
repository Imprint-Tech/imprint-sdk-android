package co.imprint.sdk.presentation.components

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp

@Composable
internal fun AppBar(
  bitmap: Bitmap?,
  onDismiss: () -> Unit,
  modifier: Modifier = Modifier,
) {
  Box(
    modifier = modifier
      .fillMaxWidth()
      .height(56.dp)
      .padding(horizontal = 16.dp),
  ) {
    Box(
      modifier = Modifier.align(Alignment.Center)
    ) {
      LogoImage(bitmap)
    }

    Box(
      modifier = Modifier.align(Alignment.CenterEnd)
    ) {
      CloseButton(onDismiss)
    }
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
  } else Box(modifier = Modifier.size(56.dp))
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