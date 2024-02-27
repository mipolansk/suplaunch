package org.supla.launcher.data.source.local

import android.content.Context
import android.os.Environment
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.FileOutputStream
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocalFileStorage @Inject constructor(@ApplicationContext private val context: Context) {

  fun getLocalFileOutputStream(fileName: String): FileOutputStream {
    Environment.getExternalStorageDirectory()
    return context.openFileOutput(fileName, Context.MODE_PRIVATE)
  }
}