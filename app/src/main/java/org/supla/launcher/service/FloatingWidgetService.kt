package org.supla.launcher.service

import android.animation.Animator
import android.animation.ValueAnimator
import android.app.Service
import android.content.Intent
import android.graphics.PixelFormat
import android.os.IBinder
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.ImageButton
import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.compose.ui.unit.dp
import dagger.hilt.android.AndroidEntryPoint
import org.supla.launcher.R
import org.supla.launcher.extensions.toPx
import org.supla.launcher.extensions.visibleIf
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject

private const val OPEN_CLOSE_ANIMATION_TIME = 300L // in ms
private val MENU_CLOSED_POSITION = (-72).dp.toPx().toInt()
private val MENU_OPENED_POSITION = 0.dp.toPx().toInt()
private val BUTTON_CLOSED_POSITION = 0.dp.toPx().toInt()
private val BUTTON_OPENED_POSITION = 72.dp.toPx().toInt()

@AndroidEntryPoint
class FloatingWidgetService : Service() {

  @Inject
  lateinit var launchAppService: LaunchAppService

  private lateinit var windowManager: WindowManager
  private var floatingView: View? = null
  private var floatingButton: View? = null
  private var floatingButtonArrow: ImageView? = null
  private var openCloseAnimator: ValueAnimator? = null
  private val isOpened = AtomicBoolean(false)

  override fun onCreate() {
    super.onCreate()

    windowManager = getSystemService(WINDOW_SERVICE) as WindowManager


    addOverflowMenu()
  }


  override fun onBind(p0: Intent?): IBinder? {
    return null
  }

  override fun onDestroy() {
    super.onDestroy()

    floatingView?.let { windowManager.removeView(it) }
  }

  private fun addOverflowMenu() {
    val inflater = getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
    addOverflowView(inflater)
    addOverflowButton(inflater)
  }

  private fun addOverflowView(inflater: LayoutInflater) {
    floatingView = inflater.inflate(R.layout.view_floating_home, null)

    val viewParams = WindowManager.LayoutParams(
      WindowManager.LayoutParams.MATCH_PARENT,
      WindowManager.LayoutParams.WRAP_CONTENT,
      WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
      WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
      PixelFormat.TRANSLUCENT
    )
    viewParams.gravity = Gravity.TOP
    viewParams.x = 0
    viewParams.y = MENU_CLOSED_POSITION

    floatingView?.let { view ->
      view.findViewById<ImageButton>(R.id.floating_home_button)?.setOnClickListener {
        launchAppService.launchSuplaunch()
        closeMenu()
      }
      view.findViewById<ImageButton>(R.id.floating_supla_button)?.setOnClickListener {
        launchAppService.launchSupla()
        closeMenu()
      }
      view.findViewById<ImageButton>(R.id.floating_ewelink_button)?.setOnClickListener {
        launchAppService.launchHome()
        closeMenu()
      }
      view.findViewById<ImageButton>(R.id.floating_android_button)?.let{
        val ultraSmallLauncher = packageManager.getLaunchIntentForPackage("l.l")
        it.visibleIf(ultraSmallLauncher != null)
        it.setOnClickListener {
          launchAppService.launchIntent(ultraSmallLauncher)
          closeMenu()
        }
      }

      windowManager.addView(view, viewParams)
    }
  }

  private fun addOverflowButton(inflater: LayoutInflater) {
    floatingButton = inflater.inflate(R.layout.view_floating_button, null)

    val buttonParams = WindowManager.LayoutParams(
      WindowManager.LayoutParams.WRAP_CONTENT,
      WindowManager.LayoutParams.WRAP_CONTENT,
      WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
      WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
      PixelFormat.TRANSLUCENT
    )
    buttonParams.gravity = Gravity.TOP or Gravity.START
    buttonParams.x = 95.dp.toPx().toInt()
    buttonParams.y = BUTTON_CLOSED_POSITION

    floatingButton?.let { view ->
      floatingButtonArrow = view.findViewById(R.id.floating_home_arrow_icon)
      view.setOnClickListener { toggleMenu() }

      windowManager.addView(view, buttonParams)
    }
  }

  private fun openMenu() {
    if (isOpened.compareAndSet(false, true)) {
      runOpenAnimation()
    }
  }

  private fun closeMenu() {
    if (isOpened.compareAndSet(true, false)) {
      runCloseAnimation()
    }
  }

  private fun toggleMenu() {
    if (isOpened.compareAndSet(true, false)) {
      runCloseAnimation()
    } else if (isOpened.compareAndSet(false, true)) {
      runOpenAnimation()
    }
  }

  private fun runOpenAnimation() {
    openCloseAnimator?.cancel()
    openCloseAnimator = animation(BUTTON_CLOSED_POSITION, BUTTON_OPENED_POSITION, R.drawable.ic_arrow_up)
  }

  private fun runCloseAnimation() {
    openCloseAnimator?.cancel()
    openCloseAnimator = animation(BUTTON_OPENED_POSITION, BUTTON_CLOSED_POSITION, R.drawable.ic_arrow_down)
  }

  private fun animation(startValue: Int, endValue: Int, @DrawableRes finalArrowIcon: Int) =
    ValueAnimator.ofInt(startValue, endValue).apply {
      duration = OPEN_CLOSE_ANIMATION_TIME
      addUpdateListener {
        moveViewToPosition(floatingView, MENU_CLOSED_POSITION + it.animatedValue as Int)
        moveViewToPosition(floatingButton, it.animatedValue as Int)
      }
      addListener(EndAnimationListener {
        floatingButtonArrow?.let { it.setImageDrawable(it.context.getDrawable(finalArrowIcon)) }
      })
      start()
    }

  private fun moveViewToPosition(optionalView: View?, position: Int) {
    optionalView?.let { view ->
      val params = view.layoutParams as WindowManager.LayoutParams
      params.y = position
      windowManager.updateViewLayout(view, params)
    }
  }
}

private class EndAnimationListener(private val onEnd: () -> Unit) : Animator.AnimatorListener {
  override fun onAnimationStart(p0: Animator) {
    // Intentionally left empty
  }

  override fun onAnimationEnd(p0: Animator) {
    onEnd()
  }

  override fun onAnimationCancel(p0: Animator) {
    onEnd()
  }

  override fun onAnimationRepeat(p0: Animator) {
    // Intentionally left empty
  }
}