#ifndef QST_LOG_H
#define QST_LOG_H

#ifndef LOG_TAG
#define LOG_TAG "scard"
#endif

#include <android/log.h>
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG,LOG_TAG,__VA_ARGS__)
#define ALWAYS(...) __android_log_print(ANDROID_LOG_DEBUG,LOG_TAG,__VA_ARGS__)

#endif //QST_LOG_H
