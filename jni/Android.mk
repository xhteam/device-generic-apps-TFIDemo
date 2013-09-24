LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_SRC_FILES := \
    com_quester_demo_scard_SCardManager.cpp

LOCAL_C_INCLUDES += \
    $(JNI_H_INCLUDE) \

LOCAL_SHARED_LIBRARIES := \
    libandroid_runtime \
    libnativehelper \
    libutils \
    libpcsclite

LOCAL_MODULE := libscard_jni
LOCAL_PRELINK_MODULE := false

include $(BUILD_SHARED_LIBRARY)
