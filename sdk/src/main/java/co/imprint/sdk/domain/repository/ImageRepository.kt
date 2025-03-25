package co.imprint.sdk.domain.repository

import android.graphics.Bitmap

internal interface ImageRepository {

  suspend fun getImageBitmap(url: String): Bitmap

}