package com.margelo.nitro.nitrosimpletoast

import android.os.Build
import android.os.Handler
import android.os.Looper
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.widget.Toast
import com.facebook.proguard.annotations.DoNotStrip

@DoNotStrip
class NitroSimpleToast : HybridNitroSimpleToastSpec() {

    override fun show(options: ToastOptions): Unit {
        val context = NitroSimpleToastPackage.appContext ?: return

        Handler(Looper.getMainLooper()).post {
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

            val haptic = options.haptic ?: ToastHaptic.SUCCESS
            performHaptic(haptic)
        }
    }

    private fun performHaptic(haptic: ToastHaptic) {
        if (haptic == ToastHaptic.NONE) return

        val context = NitroSimpleToastPackage.appContext ?: return

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
}
