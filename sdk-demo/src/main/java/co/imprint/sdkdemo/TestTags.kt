package co.imprint.sdkdemo

object TestTags {
    const val CLIENT_SECRET_INPUT = "client_secret_input"
    const val TAB_STAGING = "tab_staging"
    const val TAB_SANDBOX = "tab_sandbox"
    const val TAB_PRODUCTION = "tab_production"
    const val START_APPLICATION_BUTTON = "start_application_button"
    const val COMPLETION_STATE = "completion_state"

    fun environmentTab(env: Environment): String = "tab_${env.name.lowercase()}"
}
