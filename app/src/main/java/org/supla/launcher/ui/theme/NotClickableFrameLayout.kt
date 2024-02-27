package org.supla.launcher.ui.theme

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.FrameLayout

class NotClickableFrameLayout : FrameLayout {

  constructor(context: Context) : super(context, null, 0) {
    isClickable = false
  }

  constructor(context: Context, attrs: AttributeSet?) : super(context, attrs, 0) {
    isClickable = false
  }

  constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
    isClickable = false
  }

  override fun onTouchEvent(event: MotionEvent?): Boolean {
    return false
  }

  override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
    return false
  }
}