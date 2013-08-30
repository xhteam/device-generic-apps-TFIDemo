#ifdef __cplusplus
extern "C" {
#endif

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <jni.h>

#include "log.h"
#include "winscard.h"
#include "wintypes.h"
#include "pcsclite.h"

#ifdef __cplusplus
}
#endif

static char className[] = "com/quester/demo/scard/SCardManager";

static SCARDCONTEXT hContext;
static SCARDHANDLE hCard;
static DWORD dwActiveProtocol;

jint
jni_establish_scard_context(JNIEnv* env, jobject thiz, jint dwScope)
{
	LONG ret;
	ret = SCardEstablishContext((DWORD)dwScope, NULL, NULL, &hContext);
	return ret;
}

jint
jni_release_scard_context(JNIEnv* env, jobject thiz)
{
	LONG ret;
	ret = SCardReleaseContext(hContext);
	return ret;
}

jint
jni_is_valid_scard_context(JNIEnv* env, jobject thiz)
{
	LONG ret;
	ret = SCardIsValidContext(hContext);
	return ret;
}

jint
jni_connect_scard(JNIEnv* env, jobject thiz, jstring reader, jint dwShareMode,
		jint dwPreferredProtocols)
{
	LONG ret;
	LPCSTR szReader;

	szReader = env->GetStringUTFChars(reader, NULL);
	dwActiveProtocol = -1;

	ret = SCardConnect(hContext, szReader, (DWORD)dwShareMode, (DWORD)dwPreferredProtocols,
			&hCard, &dwActiveProtocol);
	if (szReader) {
		env->ReleaseStringUTFChars(reader, szReader);
	}

	return ret;
}

jint
jni_reconnect_scard(JNIEnv* env, jobject thiz, jint dwShareMode, jint dwPreferredProtocols,
		jint dwInitialization)
{
	LONG ret;
	ret = SCardReconnect(hCard, (DWORD)dwShareMode, (DWORD)dwPreferredProtocols,
		(DWORD)dwInitialization, &dwActiveProtocol);
	return ret;
}

jint
jni_disconnect_scard(JNIEnv* env, jobject thiz, jint dwDisposition)
{
	LONG ret;
	ret = SCardDisconnect(hCard, (DWORD)dwDisposition);
	return ret;
}

jint
jni_begin_scard_transaction(JNIEnv* env, jobject thiz)
{
	LONG ret;
	ret = SCardBeginTransaction(hCard);
	return ret;
}

jint
jni_end_scard_transaction(JNIEnv* env, jobject thiz, jint dwDisposition)
{
	LONG ret;
	ret = SCardEndTransaction(hCard, (DWORD)dwDisposition);
	return ret;
}

jint
jni_get_scard_status(JNIEnv* env, jobject thiz, jbyteArray reader, jobject readerLen,
		jobject state, jobject prot, jbyteArray atr, jobject atrLen)
{
	LONG ret;
	DWORD pcchReaderLen, dwState, dwProt, cbAtrLen;
	jclass clazzInt;
	jfieldID fieldId;

	clazzInt = env->FindClass("java/lang/Integer");
	if (NULL == clazzInt) {
		LOGD("SCardGetStatus: FindClass failed");
		return -1;
	}
	fieldId = env->GetFieldID(clazzInt, "value", "I");
	if (NULL == fieldId) {
		LOGD("SCardGetStatus: GetFieldID failed");
		return -1;
	}

	LPSTR mszReaderName = (LPSTR)env->GetByteArrayElements(reader, NULL);
	LPBYTE pbAtr = (LPBYTE)env->GetByteArrayElements(atr, NULL);
	pcchReaderLen = (DWORD)env->GetIntField(readerLen, fieldId);
	cbAtrLen = (DWORD)env->GetIntField(atrLen, fieldId);

	ret = SCardStatus(hCard, mszReaderName, &pcchReaderLen, &dwState, &dwProt, pbAtr, &cbAtrLen);
	env->ReleaseByteArrayElements(reader, (jbyte*)mszReaderName, JNI_COMMIT);
	env->SetIntField(readerLen, fieldId, pcchReaderLen);
	env->SetIntField(state, fieldId, dwState);
	env->SetIntField(prot, fieldId, dwProt);
	env->SetIntField(atrLen, fieldId, cbAtrLen);
	env->ReleaseByteArrayElements(atr, (jbyte*)pbAtr, JNI_COMMIT);

	return ret;
}

jint
jni_get_scard_status_change(JNIEnv* env, jobject thiz, jint dwTimeout, jstring reader,
		jobject eventState, jbyteArray atr, jobject atrLen)
{
	LONG ret;
	SCARD_READERSTATE rgReaderStates[1];
	jclass clazzInt;
	jfieldID fieldId;

	clazzInt = env->FindClass("java/lang/Integer");
	if (NULL == clazzInt) {
		LOGD("SCardGetStatusChange: FindClass failed");
		return -1;
	}
	fieldId = env->GetFieldID(clazzInt, "value", "I");
	if (NULL == fieldId) {
		LOGD("SCardGetStatusChange: GetFieldID failed");
		return -1;
	}

	LPCSTR szReader = (LPCSTR)env->GetStringUTFChars(reader, NULL);
	LPBYTE rgbAtr = (LPBYTE)env->GetByteArrayElements(atr, NULL);
	rgReaderStates[0].szReader = szReader;
	rgReaderStates[0].dwCurrentState = SCARD_STATE_UNAWARE;

	ret = SCardGetStatusChange(hContext, (DWORD)dwTimeout, rgReaderStates, 1);
	if (szReader) {
		env->ReleaseStringUTFChars(reader, szReader);
	}
	env->SetIntField(eventState, fieldId, rgReaderStates[0].dwEventState);
	env->ReleaseByteArrayElements(atr, (jbyte*)rgbAtr, JNI_COMMIT);
	env->SetIntField(atrLen, fieldId, rgReaderStates[0].cbAtr);

	return ret;
}

jint
jni_control_scard(JNIEnv* env, jobject thiz, jint dwControlCode, jbyteArray sendBuffer,
		jint sendLen, jbyteArray recvBuffer, jint recvLen, jobject lpBytesReturned)
{
	LONG ret;
	DWORD lp;
	jclass clazzInt;
	jfieldID fieldId;

	clazzInt = env->FindClass("java/lang/Integer");
	if (NULL == clazzInt) {
		LOGD("SCardControl: FindClass failed");
		return -1;
	}
	fieldId = env->GetFieldID(clazzInt, "value", "I");
	if (NULL == fieldId) {
		LOGD("SCardControl: GetFieldID failed");
		return -1;
	}

	LPBYTE pbSendBuffer = (LPBYTE)env->GetByteArrayElements(sendBuffer, NULL);
	LPBYTE pbRecvBuffer = (LPBYTE)env->GetByteArrayElements(recvBuffer, NULL);

	ret = SCardControl(hCard, (DWORD)dwControlCode, pbSendBuffer, (DWORD)sendLen,
		pbRecvBuffer, (DWORD)recvLen, &lp);
	env->ReleaseByteArrayElements(sendBuffer, (jbyte*)pbSendBuffer, JNI_ABORT);
	env->ReleaseByteArrayElements(recvBuffer, (jbyte*)pbRecvBuffer, JNI_COMMIT);
	env->SetIntField(lpBytesReturned, fieldId, lp);

	return ret;
}

jint
jni_transmit_scard(JNIEnv* env, jobject thiz, jbyteArray sendBuffer, jint sendLen,
		jbyteArray recvBuffer, jobject recvLen)
{
	LONG ret;
	DWORD cbRecvLen;
	const SCARD_IO_REQUEST* pioSendPci;
	SCARD_IO_REQUEST pioRecvPci;
	jclass clazzInt;
	jfieldID fieldId;

	clazzInt = env->FindClass("java/lang/Integer");
	if (NULL == clazzInt) {
		LOGD("SCardTransmit: FindClass failed");
		return -1;
	}
	fieldId = env->GetFieldID(clazzInt, "value", "I");
	if (NULL == fieldId) {
		LOGD("SCardTransmit: GetFieldID failed");
		return -1;
	}

	switch (dwActiveProtocol) {
	case SCARD_PROTOCOL_T0:
		pioSendPci = SCARD_PCI_T0;
		break;
	case SCARD_PROTOCOL_T1:
		pioSendPci = SCARD_PCI_T1;
		break;
	default:
		return -1;
	}

	LPBYTE pbSendBuffer = (LPBYTE)env->GetByteArrayElements(sendBuffer, NULL);
	LPBYTE pbRecvBuffer = (LPBYTE)env->GetByteArrayElements(recvBuffer, NULL);
	cbRecvLen = (DWORD)env->GetIntField(recvLen, fieldId);

	ret = SCardTransmit(hCard, pioSendPci, pbSendBuffer, (DWORD)sendLen,
			&pioRecvPci, pbRecvBuffer, &cbRecvLen);
	env->ReleaseByteArrayElements(sendBuffer, (jbyte*)pbSendBuffer, JNI_ABORT);
	env->ReleaseByteArrayElements(recvBuffer, (jbyte*)pbRecvBuffer, JNI_COMMIT);
	env->SetIntField(recvLen, fieldId, cbRecvLen);

	return ret;
}

jobjectArray
jni_list_scard_reader_groups(JNIEnv* env, jobject thiz, jint hGroups)
{
	LONG ret;
	DWORD cchGroups;
	LPSTR ptr;
	LPSTR mszGroups;
	int nbGroups;

	jclass clazzString;
	jmethodID methodId;
	jobject jgroup;
	jobjectArray jgroups;

	clazzString = env->FindClass("java/lang/String");
	if (NULL == clazzString) {
		LOGD("SCardListReaderGroups: FindClass failed");
		return NULL;
	}
	methodId = env->GetMethodID(clazzString, "<init>", "()V");
	if(NULL == methodId) {
		LOGD("SCardListReaderGroups: GetMethodID failed");
		return NULL;
	}

	cchGroups = (DWORD)hGroups;

	ret = SCardListReaderGroups(hContext, (LPSTR)&mszGroups, &cchGroups);
	if (ret != SCARD_S_SUCCESS) {
		LOGD("SCardListReaderGroups: %s (0x%lX)", pcsc_stringify_error(ret), ret);
		if (mszGroups) {
			SCardFreeMemory(hContext, mszGroups);
		}
		return NULL;
	}

	nbGroups = 0;
	ptr = mszGroups;
	while (*ptr != '\0') {
		ptr += strlen(ptr) + 1;
		nbGroups++;
	}
	if (nbGroups == 0) {
		LOGD("SCardListReaderGroups: No reader found");
		if (mszGroups) {
			SCardFreeMemory(hContext, mszGroups);
		}
		return NULL;
	}

	jgroup = env->NewObject(clazzString, methodId);
	jgroups = env->NewObjectArray(nbGroups, clazzString, jgroup);

	nbGroups = 0;
	ptr = mszGroups;
	while (*ptr != '\0')
	{
		env->SetObjectArrayElement(jgroups, nbGroups, (jobject)env->NewStringUTF(ptr));
		ptr += strlen(ptr)+1;
		nbGroups++;
	}

	SCardFreeMemory(hContext, mszGroups);
	return jgroups;
}

jobjectArray
jni_list_scard_readers(JNIEnv* env, jobject thiz, jstring groups, jint hReaders)
{
	LONG ret;
	LPCSTR mszGroups;
	DWORD cchReaders;
	LPSTR ptr;
	LPSTR mszReaders;
	int nbReaders;

	jclass clazzString;
	jmethodID methodId;
	jobject jreader;
	jobjectArray jreaders;

	clazzString = env->FindClass("java/lang/String");
	if (NULL == clazzString) {
		LOGD("SCardListReaders: FindClass failed");
		return NULL;
	}
	methodId = env->GetMethodID(clazzString, "<init>", "()V");
	if(NULL == methodId) {
		LOGD("SCardListReaders: GetMethodID failed");
		return NULL;
	}

	mszGroups = (LPCSTR)env->GetStringUTFChars(groups, NULL);
	cchReaders = (DWORD)hReaders;

	ret = SCardListReaders(hContext, mszGroups, (LPSTR)&mszReaders, &cchReaders);
	if (mszGroups) {
		env->ReleaseStringUTFChars(groups, mszGroups);
	}
	if (ret != SCARD_S_SUCCESS) {
		LOGD("SCardListReaders: %s (0x%lX)", pcsc_stringify_error(ret), ret);
		if (mszReaders) {
			SCardFreeMemory(hContext, mszReaders);
		}
		return NULL;
	}

	nbReaders = 0;
	ptr = mszReaders;
	while (*ptr != '\0') {
		ptr += strlen(ptr) + 1;
		nbReaders++;
	}
	if (nbReaders == 0) {
		LOGD("SCardListReaders: No reader found");
		if (mszReaders) {
			SCardFreeMemory(hContext, mszReaders);
		}
		return NULL;
	}

	jreader = env->NewObject(clazzString, methodId);
	jreaders = env->NewObjectArray(nbReaders, clazzString, jreader);

	nbReaders = 0;
	ptr = mszReaders;
	while (*ptr != '\0')
	{
		env->SetObjectArrayElement(jreaders, nbReaders, (jobject)env->NewStringUTF(ptr));
		ptr += strlen(ptr)+1;
		nbReaders++;
	}

	SCardFreeMemory(hContext, mszReaders);
	return jreaders;
}

jint
jni_cancel_scard(JNIEnv* env, jobject thiz)
{
	LONG ret;
	ret = SCardCancel(hContext);
	return ret;
}

jint
jni_get_scard_attrib(JNIEnv* env, jobject thiz, jint attrId, jbyteArray attr, jobject attrLen)
{
	LONG ret;
	DWORD cbAttrLen;
	jclass clazzInt;
	jfieldID fieldId;

	clazzInt = env->FindClass("java/lang/Integer");
	if (clazzInt == NULL) {
		LOGD("SCardGetAttrib: FindClass failed");
		return -1;
	}
	fieldId = env->GetFieldID(clazzInt, "value", "I");
	if (fieldId == NULL) {
		LOGD("SCardGetAttrib: GetFieldID failed");
		return -1;
	}

	LPBYTE pbAttr = (LPBYTE)env->GetByteArrayElements(attr, NULL);
	cbAttrLen = (DWORD)env->GetIntField(attrLen, fieldId);

	ret = SCardGetAttrib(hCard, attrId, pbAttr, &cbAttrLen);
	env->ReleaseByteArrayElements(attr, (jbyte*)pbAttr, JNI_COMMIT);
	env->SetIntField(attrLen, fieldId, cbAttrLen);

	return ret;
}

jint
jni_set_scard_attrib(JNIEnv* env, jobject thiz, jint attrId, jbyteArray attr, jint attrLen)
{
	LONG ret;
	LPCBYTE pbAttr = (LPCBYTE)env->GetByteArrayElements(attr, NULL);
	ret = SCardSetAttrib(hCard, attrId, pbAttr, attrLen);
	env->ReleaseByteArrayElements(attr, (jbyte*)pbAttr, JNI_ABORT);
	return ret;
}

jstring
jni_get_pcsc_ify_error(JNIEnv* env, jobject thiz, jint ret)
{
	LPSTR stringify;
	stringify = pcsc_stringify_error(ret);
	return (env->NewStringUTF(stringify));
}

static JNINativeMethod methods[] = {
		{"nativeEstablishSCardContext", "(I)I", (void*) jni_establish_scard_context },
		{"nativeReleaseSCardContext", "()I", 	(void*) jni_release_scard_context },
		{"nativeIsValidSCardContext", "()I", 	(void*) jni_is_valid_scard_context },
		{"nativeConnectSCard", "(Ljava/lang/String;II)I",
												(void*) jni_connect_scard },
		{"nativeReconnectSCard", "(III)I", 		(void*) jni_reconnect_scard },
		{"nativeDisconnectSCard", "(I)I", 		(void*) jni_disconnect_scard },
		{"nativeBeginSCardTransaction", "()I", 	(void*) jni_begin_scard_transaction },
		{"nativeEndSCardTransaction", "(I)I", 	(void*) jni_end_scard_transaction },
		{"nativeGetSCardStatus", "([BLjava/lang/Integer;Ljava/lang/Integer;Ljava/lang/Integer;[BLjava/lang/Integer;)I",
												(void*) jni_get_scard_status },
		{"nativeGetSCardStatusChange", "(ILjava/lang/String;Ljava/lang/Integer;[BLjava/lang/Integer;)I",
												(void*) jni_get_scard_status_change },
		{"nativeControlSCard", "(I[BI[BILjava/lang/Integer;)I",
												(void*) jni_control_scard },
		{"nativeTransmitSCard", "([BI[BLjava/lang/Integer;)I",
												(void*) jni_transmit_scard },
		{"nativeListSCardReaderGroups", "(I)[Ljava/lang/String;",
												(void*) jni_list_scard_reader_groups },
		{"nativeListSCardReaders", "(Ljava/lang/String;I)[Ljava/lang/String;",
												(void*) jni_list_scard_readers },
		{"nativeCancelSCard", "()I", 			(void*) jni_cancel_scard },
		{"nativeGetSCardAttrib", "(I[BLjava/lang/Integer;)I",
												(void*) jni_get_scard_attrib },
		{"nativeSetScardAttrib", "(I[BI)I", 	(void*) jni_set_scard_attrib },
		{"nativeGetPcscIfyError", "(I)Ljava/lang/String;",
												(void*) jni_get_pcsc_ify_error },
};

/*
 * Register several native methods for one class.
 */
static int registerNativeMethods(JNIEnv* env, const char* className,
		JNINativeMethod* gMethods, int numMethods)
{
	jclass clazz;

	clazz = env->FindClass(className);
	if (clazz == NULL) {
		LOGD("Native registration unable to find class '%s'", className);
		return JNI_FALSE;
	}
	if (env->RegisterNatives(clazz, gMethods, numMethods) < 0) {
		LOGD("RegisterNatives failed for '%s'", className);
		return JNI_FALSE;
	}

	return JNI_TRUE;
}

/*
 * Register native methods for all classes we know about.
 *
 * return JNI_TRUE on success.
 */
static int registerNatives(JNIEnv* env)
{
	if (!registerNativeMethods(env, className, methods,
			sizeof(methods) / sizeof(methods[0]))) {
		return JNI_FALSE;
	}

	return JNI_TRUE;
}

// ----------------------------------------------------------------------------------------------

/*
 * This is called by the VM when the shared library is first loaded.
 */

typedef union {
	JNIEnv* env;
	void* venv;
} UnionJNIEnvToVoid;

jint JNI_OnLoad(JavaVM* vm, void* reserved)
{
	UnionJNIEnvToVoid uenv;
	uenv.venv = NULL;
	jint result = -1;
	JNIEnv* env = NULL;

	LOGD("JNI_OnLoad");

	if (vm->GetEnv(&uenv.venv, JNI_VERSION_1_4) != JNI_OK) {
		LOGD("ERROR: GetEnv failed");
		goto bail;
	}
	env = uenv.env;

	if (registerNatives(env) != JNI_TRUE) {
		LOGD("ERROR: registerNatives failed");
		goto bail;
	}

	result = JNI_VERSION_1_4;

bail:
	return result;
}
