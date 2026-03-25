package com.margelo.nitro.nitrosimpletoast

import android.app.Activity
import android.content.res.Configuration
import android.graphics.Color
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.facebook.proguard.annotations.DoNotStrip
import com.hjq.window.EasyWindow
import com.margelo.nitro.NitroModules

@DoNotStrip
class NitroSimpleToast : HybridNitroSimpleToastSpec() {

    private var toastWindow: EasyWindow<*>? = null

    override fun show(options: ToastOptions): Unit {
        val activity = NitroModules.applicationContext?.currentActivity
        if (activity == null) {
            Log.e(TAG, "currentActivity is null, cannot show toast")
            return
        }

        activity.runOnUiThread {
            try {
                showToast(activity, options)
            } catch (e: Exception) {
                Log.e(TAG, "Failed to show toast", e)
            }
        }
    }

    private fun showToast(activity: Activity, options: ToastOptions) {
        // Dismiss existing toast
        toastWindow?.cancel()

        // Inflate layout
        val container = LayoutInflater.from(activity)
            .inflate(R.layout.toast_layout, null) as LinearLayout

        // Resolve theme
        val isDark = resolveIsDark(activity, options.theme)

        // Apply background
        container.setBackgroundResource(
            if (isDark) R.drawable.toast_bg_dark else R.drawable.toast_bg_light
        )

        // Text colors
        val textColor = if (isDark) Color.parseColor("#F5F5F6") else Color.parseColor("#16171A")

        // Title
        val titleView = container.findViewById<TextView>(R.id.toast_title)
        titleView.text = options.title
        titleView.setTextColor(textColor)

        // Message
        val messageView = container.findViewById<TextView>(R.id.toast_message)
        if (options.message != null) {
            messageView.text = options.message
            messageView.setTextColor(textColor)
            messageView.visibility = View.VISIBLE
        }

        // Icon based on preset
        val iconView = container.findViewById<ImageView>(R.id.toast_icon)
        when (options.preset ?: ToastPreset.DONE) {
            ToastPreset.DONE -> {
                iconView.setImageResource(R.drawable.ic_toast_done)
                iconView.visibility = View.VISIBLE
            }
            ToastPreset.ERROR -> {
                iconView.setImageResource(R.drawable.ic_toast_error)
                iconView.visibility = View.VISIBLE
            }
            ToastPreset.NONE -> {
                iconView.visibility = View.GONE
            }
        }

        // Position
        val isBottom = options.from == ToastFrom.BOTTOM
        val gravity = if (isBottom) Gravity.BOTTOM else Gravity.TOP
        val animStyle = if (isBottom) R.style.ToastAnimBottom else R.style.ToastAnimTop

        // Duration in milliseconds (default 3 seconds)
        val durationMs = ((options.duration ?: 3.0) * 1000).toInt()

        // Show with EasyWindow
        toastWindow = EasyWindow<EasyWindow<*>>(activity).apply {
            setDuration(durationMs)
            setContentView(container)
            setGravity(gravity)
            setYOffset(48)
            setAnimStyle(animStyle)
            setOutsideTouchable(true)
        }
        toastWindow?.show()

        Log.d(TAG, "Toast shown: ${options.title}")

        // Haptic
        performHaptic(options.haptic ?: ToastHaptic.SUCCESS)
    }

    private fun resolveIsDark(activity: Activity, theme: ToastTheme?): Boolean {
        return when (theme ?: ToastTheme.SYSTEM) {
            ToastTheme.DARK -> true
            ToastTheme.LIGHT -> false
            ToastTheme.SYSTEM -> {
                val nightMode = activity.resources.configuration.uiMode and
                    Configuration.UI_MODE_NIGHT_MASK
                nightMode == Configuration.UI_MODE_NIGHT_YES
            }
        }
    }

    private fun performHaptic(haptic: ToastHaptic) {
        if (haptic == ToastHaptic.NONE) return

        val reactContext = NitroModules.applicationContext ?: return
        val context = reactContext.applicationContext

        val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = context.getSystemService(
                android.content.Context.VIBRATOR_MANAGER_SERVICE
            ) as VibratorManager
            vibratorManager.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(android.content.Context.VIBRATOR_SERVICE) as Vibrator
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val effect = when (haptic) {
                ToastHaptic.SUCCESS -> VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE)
                ToastHaptic.WARNING -> VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE)
                ToastHaptic.ERROR -> VibrationEffect.createWaveform(longArrayOf(0, 50, 50, 50), -1)
                ToastHaptic.NONE -> return
            }
            vibrator.vibrate(effect)
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(50)
        }
    }

    companion object {
        private const val TAG = "NitroSimpleToast"
    }
}
