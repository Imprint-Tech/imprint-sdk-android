package co.imprint.sdksample

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import co.imprint.sdksample.ui.theme.ImprintSDKTheme

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      val viewModel: MainViewModel = viewModel()
      val context = LocalContext.current
      val statusText by viewModel.statusText.collectAsState()
      ImprintSDKTheme {
        Scaffold(modifier = Modifier.fillMaxSize(), containerColor = Color.White) { innerPadding ->
          Content(
            modifier = Modifier
              .padding(innerPadding)
              .padding(vertical = 80.dp, horizontal = 16.dp),
            onStartApplication = { viewModel.startApplication(context = context) },
            statusText = statusText,
          )
        }
      }
    }
  }
}

@Composable
private fun Content(
  onStartApplication: () -> Unit,
  statusText: String,
  modifier: Modifier = Modifier,
) {
  Column(
    modifier = modifier,
    horizontalAlignment = Alignment.CenterHorizontally,
  ) {
    Button(onClick = { onStartApplication() }) {
      Text(
        text = "Start application",
      )
    }

    Spacer(modifier = Modifier.height(48.dp))

    Text(
      text = statusText,
      modifier = Modifier
        .fillMaxWidth()
        .wrapContentHeight(),
      textAlign = TextAlign.Center,
    )
  }
}