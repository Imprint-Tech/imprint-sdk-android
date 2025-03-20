package co.imprint.sdk.data.repository

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import co.imprint.sdk.di.IsolatedKoinComponent
import co.imprint.sdk.domain.repository.ImageRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL

internal class ImageRepositoryImpl(
  private val ioDispatcher: CoroutineDispatcher
) : ImageRepository, IsolatedKoinComponent {

  override suspend fun getImageBitmap(url: String): Bitmap = withContext(ioDispatcher) {
    val connection = URL(url).openConnection() as HttpURLConnection
    connection.doInput = true
    connection.connect()
    val inputStream: InputStream = connection.inputStream
    BitmapFactory.decodeStream(inputStream)
  }
}