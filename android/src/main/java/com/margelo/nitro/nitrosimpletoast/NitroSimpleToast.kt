package com.margelo.nitro.nitrosimpletoast

import android.os.Build
import android.os.Handler
import android.os.Looper
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.util.Log
import android.widget.Toast
import com.facebook.proguard.annotations.DoNotStrip
import com.margelo.nitro.NitroModules

@DoNotStrip
class NitroSimpleToast : HybridNitroSimpleToastSpec() {

    override fun show(options: ToastOptions): Unit {
        val reactContext = NitroModules.applicationContext
        if (reactContext == null) {
            Log.e(TAG, "NitroModules.applicationContext is null")
            return
        }
        val context = reactContext.applicationContext

        Handler(Looper.getMainLooper()).post {
            try {
                val text = if (options.message != null) {
                    "${options.title}\n${options.message}"
                } else {
                    options.title
                }

                val toastDuration = if (options.duration != null && options.duration!! > 2.0) {
                    Toast.LENGTH_LONG
                } else {
                    Toast.LENGTH_SHORT
                }

                Toast.makeText(context, text, toastDuration).show()
                Log.d(TAG, "Toast shown: ${options.title}")

                val haptic = options.haptic ?: ToastHaptic.SUCCESS
                performHaptic(haptic)
            } catch (e: Exception) {
                Log.e(TAG, "Failed to show toast", e)
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
