package org.supla.launcher.extensions

import android.content.res.Resources
import android.util.TypedValue
import androidx.compose.ui.unit.Dp

fun Dp.toPx(): Float {
  return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value, Resources.getSystem().displayMetrics)
}
