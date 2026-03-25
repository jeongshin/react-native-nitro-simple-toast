#include <jni.h>
#include "nitrosimpletoastOnLoad.hpp"

JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM* vm, void*) {
  return margelo::nitro::nitrosimpletoast::initialize(vm);
}
