package co.imprint.sdkdemo

object TestTags {
    const val CLIENT_SECRET_INPUT = "client_secret_input"
    const val SELECT_STAGING = "select_staging"
    const val SELECT_SANDBOX = "select_sandbox"
    const val SELECT_PRODUCTION = "select_production"
    const val START_APPLICATION_BUTTON = "start_application_button"
    const val COMPLETION_STATE = "completion_state"

    fun environmentTab(env: Environment): String = "select_${env.name.lowercase()}"
}
