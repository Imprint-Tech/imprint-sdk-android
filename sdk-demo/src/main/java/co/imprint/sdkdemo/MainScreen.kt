package co.imprint.sdkdemo

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTagsAsResourceId
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun MainScreen(viewModel: MainViewModel = viewModel()) {
  val clientSecret by viewModel.clientSecret.collectAsState()
  val selectedEnvironment by viewModel.selectedEnvironment.collectAsState()
  val completionState by viewModel.completionState.collectAsState()
  val context = LocalContext.current

  Scaffold(
    contentWindowInsets = WindowInsets.systemBars,
  ) { innerPadding ->
    Column(
      modifier = Modifier
        .semantics { testTagsAsResourceId = true }
        .fillMaxSize()
        .padding(innerPadding)
        .padding(16.dp)
        .verticalScroll(rememberScrollState())
    ) {
      InputField(
        label = "Client Secret:",
        value = clientSecret,
        onValueChange = viewModel::updateClientSecret,
        maxLines = 6
      )

      Spacer(modifier = Modifier.height(16.dp))

      EnvironmentTabs(
        selectedEnv = selectedEnvironment,
        onEnvSelected = viewModel::selectEnvironment
      )

      Spacer(modifier = Modifier.height(32.dp))

      StartButton(onClick = { viewModel.startApplication(context) })

      Spacer(modifier = Modifier.height(64.dp))

      CompletionStateBox(completionState)
    }
  }
}

@Composable
fun InputField(label: String, value: String, onValueChange: (String) -> Unit, maxLines: Int) {
  Column {
    Text(label)
    OutlinedTextField(
      value = value,
      onValueChange = onValueChange,
      modifier = Modifier
        .fillMaxWidth()
        .height((maxLines * 24).dp)
        .testTag(TestTags.CLIENT_SECRET_INPUT),
      maxLines = maxLines,
      keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Text)
    )
  }
}

@Composable
fun EnvironmentTabs(selectedEnv: Environment, onEnvSelected: (Environment) -> Unit) {
  val environments = Environment.entries.toTypedArray()

  TabRow(
    selectedTabIndex = Environment.toIndex(selectedEnv)
  ) {
    environments.forEach { env ->
      Tab(
        selected = env == selectedEnv,
        onClick = { onEnvSelected(env) },
        modifier = Modifier.testTag(TestTags.environmentTab(env)),
        text = { Text(env.displayName) }
      )
    }
  }
}

@Composable
fun StartButton(onClick: () -> Unit) {
  Button(
    onClick = onClick,
    modifier = Modifier
      .fillMaxWidth()
      .testTag(TestTags.START_APPLICATION_BUTTON)
  ) {
    Text("Start Application")
  }
}

@Composable
fun CompletionStateBox(completionState: String) {
  Text(
    text = "Completion State: $completionState",
    modifier = Modifier
      .fillMaxWidth()
      .wrapContentHeight()
      .background(Color.LightGray)
      .padding(8.dp)
      .testTag(TestTags.COMPLETION_STATE),
    style = MaterialTheme.typography.bodyLarge
  )
}

@Composable
@Preview(showBackground = true)
fun MainScreenPreview() {
  MaterialTheme {
    MainScreen(viewModel = MainViewModel())
  }
}
