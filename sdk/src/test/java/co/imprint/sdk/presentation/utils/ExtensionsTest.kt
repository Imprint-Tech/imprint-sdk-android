package co.imprint.sdk.presentation.utils

import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNull
import org.json.JSONObject
import org.junit.Test

class ExtensionsTest {

  @Test
  fun `toMap should return empty map when JSONObject is empty`() {
    val jsonObject = JSONObject()
    val result = jsonObject.toMap()
    assertEquals(emptyMap<String, Any?>(), result)
  }

  @Test
  fun `toMap should return correct map when JSONObject has key-value pairs`() {
    val jsonObject = JSONObject().apply {
      put("key1", "value1")
      put("key2", "value2")
      put("key3", 1)
    }

    val expectedMap = mapOf(
      "key1" to "value1",
      "key2" to "value2",
      "key3" to 1,
    )

    val result = jsonObject.toMap()
    assertEquals(expectedMap, result)
  }

  @Test
  fun `toMap should handle null values correctly`() {
    val jsonObject = JSONObject().apply {
      put("key1", JSONObject.NULL) // JSON stores null as JSONObject.NULL
      put("key2", "value2")
    }

    val expectedMap = mapOf(
      "key1" to null, //Gets mapped as empty string
      "key2" to "value2"
    )

    val result = jsonObject.toMap()
    assertEquals(expectedMap, result)
  }

}