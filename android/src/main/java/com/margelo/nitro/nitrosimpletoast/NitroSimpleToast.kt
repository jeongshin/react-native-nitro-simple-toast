package com.margelo.nitro.nitrosimpletoast
  
import com.facebook.proguard.annotations.DoNotStrip

@DoNotStrip
class NitroSimpleToast : HybridNitroSimpleToastSpec() {
  override fun multiply(a: Double, b: Double): Double {
    return a * b
  }
}
