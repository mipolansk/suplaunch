package org.supla.launcher.extensions

import android.view.View

fun View.visibleIf(condition: Boolean) {
  visibility = if (condition) {
    View.VISIBLE
  } else {
    View.GONE
  }
}