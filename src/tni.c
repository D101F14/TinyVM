#include <jni.h>
#include "tni.h"
#include <windows.h>

typedef int(CALLBACK* NativeFunctionType)(int *params, int size);

JNIEXPORT jint JNICALL Java_dk_aau_d101f14_tinyvm_TinyNativeInterface_execute
  (JNIEnv * env, jobject obj, jstring library_path, jstring function_name, jintArray parameters) {
	HINSTANCE library_dll = NULL;
	NativeFunctionType library_func = NULL;
	char* library_path_string = (*env)->GetStringUTFChars(env,library_path,0); 
	char* function_name_string = (*env)->GetStringUTFChars(env,function_name,0);
	library_dll = LoadLibrary(library_path_string);

	if (library_dll != NULL) {
		(*env)->ReleaseStringUTFChars(env, library_path, library_path_string);
		library_func = (NativeFunctionType)GetProcAddress(library_dll, function_name_string);

		if (library_func != NULL) {
			(*env)->ReleaseStringUTFChars(env, function_name, function_name_string);
			int params_length = (*env)->GetArrayLength(env, parameters);
			int *native_params = (int*)malloc(params_length * sizeof(int));
			jint *param_elements = (*env)->GetIntArrayElements(env, parameters, 0);
			for(int i = 0; i < params_length; i++) {
				native_params[i] = param_elements[i];
			}
			return library_func(native_params, params_length);
		}
		else {
			FreeLibrary(library_dll);
			return 0;
		}
	}
	else {
		FreeLibrary(library_dll);
		return 0;
	}

	FreeLibrary(library_dll);
  }
