package co.imprint.sdk.presentation.utils

import org.json.JSONObject

internal fun JSONObject?.toMapOrNull(): Map<String, String?>? {
  if (this == null) return null
  val mutableMap = mutableMapOf<String, String?>()
  val keys = this.keys()
  while (keys.hasNext()) {
    val key = keys.next()
    val value = this.optString(key) // Safely get the string value
    mutableMap[key] = value
  }
  return mutableMap.toMap()
}