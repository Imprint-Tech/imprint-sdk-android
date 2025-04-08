package co.imprint.sdk.presentation.utils

import org.json.JSONObject

internal fun JSONObject.toMap(): MutableMap<String, Any?> {
  val mutableMap = mutableMapOf<String, Any?>()
  val keys = this.keys()
  while (keys.hasNext()) {
    val key = keys.next()
    val value = this.get(key)
    mutableMap[key] = value
  }

  return mutableMap
}