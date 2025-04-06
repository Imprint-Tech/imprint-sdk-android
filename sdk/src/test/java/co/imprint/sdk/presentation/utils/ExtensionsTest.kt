package co.imprint.sdk.presentation.utils

import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNull
import org.json.JSONObject
import org.junit.Test

class ExtensionsTest {

  @Test
  fun `toMapOrNull should return empty map when JSONObject is empty`() {
    val jsonObject = JSONObject()
    val result = jsonObject.toMap()
    assertEquals(emptyMap<String, Any?>(), result)
  }

  @Test
  fun `toMapOrNull should return correct map when JSONObject has key-value pairs`() {
    val jsonObject = JSONObject().apply {
      put("key1", "value1")
      put("key2", "value2")
    }

    val expectedMap = mapOf(
      "key1" to "value1",
      "key2" to "value2"
    )

    val result = jsonObject.toMap()
    assertEquals(expectedMap, result)
  }

  @Test
  fun `toMapOrNull should handle null values correctly`() {
    val jsonObject = JSONObject().apply {
      put("key1", JSONObject.NULL) // JSON stores null as JSONObject.NULL
      put("key2", "value2")
    }

    val expectedMap = mapOf(
      "key1" to "", //Gets mapped as empty string
      "key2" to "value2"
    )

    val result = jsonObject.toMap()
    assertEquals(expectedMap, result)
  }

}