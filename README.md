## Installation

### Step 1: Add to app-level `build.gradle` and sync project to gradle
[![Maven Central](https://img.shields.io/maven-central/v/co.imprint.sdk/imprint-sdk.svg)](https://maven-badges.herokuapp.com/maven-central/co.imprint.sdk/imprint-sdk)

```
repositories {     
    ...     
    mavenCentral() 
    ...
}

dependencies {     
    implementation 'co.imprint.sdk:imprint-sdk:<version>'
}
```

## Implementation
### 1. Import the SDK
```Kotlin
import co.imprint.sdk.Imprint
```

### 2. Configuration
Create an instance of `ImprintConfiguration` with your `clientSecret` and `environment`, then assign additional optional fields as needed.

```Kotlin
val configuration = ImprintConfiguration(
  clientSecret = "client_secret",
  environment = ImprintEnvironment.SANDBOX,
)
```

### 3. Define the completion handler
Define the completion handler `onCompletion` to manage the terminal states when the application flow ends.

```Kotlin
val onCompletion =
      { state: ImprintCompletionState, metadata: Map<String, Any?>? ->
        val metadataInfo = metadata?.toString() ?: "No metadata"
        val result = when (state) {
          ImprintCompletionState.OFFER_ACCEPTED -> {
            "Offer accepted\n$metadataInfo"
          }
          ImprintCompletionState.REJECTED -> {
            "Application rejected\n$metadataInfo"
          }
          ImprintCompletionState.ERROR -> {
            "Error occurred\n$metadataInfo"
          }
          else -> {
            "Application interrupted\n$metadataInfo"
          }
        }
        Log.d("Application result:", result)
      }
```

### 4. Start the Application flow
Once you have configured the `ImprintConfiguration`, initiate the application flow by calling `ImprintApp.startApplication`. This will start within a new Activity.

```Kotlin
fun startApplication(
    context: Context,
    configuration: ImprintConfiguration,
    onCompletion: (ImprintCompletionState, Map<String, Any?>?) -> Unit,
  )
```


`context`: The context from which the application process will be presented

`configuration`: The previously created ImprintConfiguration object containing your API key and completion handler

`onCompletion`: The completion handler of the flow


## Sample APP

- Change the `imprintSDK` version value in `gradle/wrapper/libs.versions.toml`
- Build the demo APP from the `sdk-demo` module
