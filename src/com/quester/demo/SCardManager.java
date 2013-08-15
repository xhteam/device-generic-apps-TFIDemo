package com.quester.demo;

public class SCardManager {
	
	/* Smart Card Native Methods */
	
	/**
	 * See winscard.h SCardEstablishCotext(...)
	 */
	private native int nativeEstablishSCardContext(int dwScope);
	
	/**
	 * See winscard.h SCardReleaseContext(...)
	 */
	private native int nativeReleaseSCardContext();
	
	/**
	 * See winscard.h SCardIsValidContext(...)
	 */
	private native int nativeIsValidSCardContext();
	
	/**
	 * See winscard.h SCardConnect(...)
	 */
	private native int nativeConnectSCard(String reader, int dwShareMode, int dwPreferredProtocols);
	
	/**
	 * See winscard.h SCardReconnect(...)
	 */
	private native int nativeReconnectSCard(int dwShareMode, int dwPreferredProtocols, int dwInitialization);
	
	/**
	 * See winscard.h SCardDisconnect(...)
	 */
	private native int nativeDisconnectSCard(int dwDisposition);
	
	/**
	 * See winscard.h SCardBeginTransaction(...)
	 */
	private native int nativeBeginSCardTransaction();
	
	/**
	 * See winscard.h SCardEndTransaction(...)
	 */
	private native int nativeEndSCardTransaction(int dwDisposition);
	
	/**
	 * See winscard.h SCardGetStatus(...)
	 */
	private native int nativeGetSCardStatus(byte[] reader, Integer readerLen, Integer state, 
			Integer prot, byte[] atr, Integer atrLen);
	
	
	/**
	 * See winscard.h SCardGetStatusChange(...)
	 */
	private native int nativeGetSCardStatusChange(int dwTimeout, String reader, Integer eventState, 
			byte[] atr, Integer atrLen);
	
	/**
	 * See winscard.h SCardControl(...)
	 */
	private native int nativeControlSCard(int ctrlCode, byte[] sendBuf, int sendLen, 
			byte[] recvBuf, int recvLen, Integer lpBytesReturned);
	
	/**
	 * See winscard.h SCardTransmit(...)
	 */
	private native int nativeTransmitSCard(byte[] sendBuf, int sendLen, byte[] recvBuf, Integer recvLen);
	
	/**
	 * See winscard.h SCardListReaderGroups(...)
	 */
	private native String[] nativeListSCardReaderGroups(int hGroups);
	
	/**
	 * See winscard.h SCardListReaders(...)
	 */
	private native String[] nativeListSCardReaders(String groups, int hReaders);
	
	/**
	 * See winscard.h SCardCancel(...)
	 */
	private native int nativeCancelSCard();
	
	/**
	 * See winscard.h SCardGetAttrib(...)
	 */
	private native int nativeGetSCardAttrib(int attrId, byte[] attr, Integer attrLen);
	
	/**
	 * See winscard.h SCardSetAttrib(...)
	 */
	private native int nativeSetScardAttrib(int attrId, byte[] attr, int attrLen);
	
	private native String nativeGetPcscIfyError(int ret);
	/*-----------------------------------------------------------------------------------------*/
	
	public int establishSCardContext(int dwScope) {
		return nativeEstablishSCardContext(dwScope);
	}
	
	public int releaseSCardContext() {
		return nativeReleaseSCardContext();
	}
	
	public int isValidSCardContext() {
		return nativeIsValidSCardContext();
	}
	
	public int connectSCard(String reader, int dwShareMode, int dwPreferredProtocols) {
		return nativeConnectSCard(reader, dwShareMode, dwPreferredProtocols);
	}
	
	public int reconnectSCard(int dwShareMode, int dwPreferredProtocols, int dwInitialization) {
		return nativeReconnectSCard(dwShareMode, dwPreferredProtocols, dwInitialization);
	}
	
	public int disconnectSCard(int dwDisposition) {
		return nativeDisconnectSCard(dwDisposition);
	}
	
	public int beginSCardTransaction() {
		return nativeBeginSCardTransaction();
	}
	
	public int endSCardTransaction(int dwDisposition) {
		return nativeEndSCardTransaction(dwDisposition);
	}
	
	public int getSCardStatus(byte[] reader, Integer readerLen, Integer state, 
			Integer prot, byte[] atr, Integer atrLen) {
		return nativeGetSCardStatus(reader, readerLen, state, prot, atr, atrLen);
	}
	
	public int getSCardStatusChange(int dwTimeout, String reader, Integer eventState, 
			byte[] atr, Integer atrLen) {
		return nativeGetSCardStatusChange(dwTimeout, reader, eventState, atr, atrLen);
	}
	
	public int controlSCard(int ctrlCode, byte[] sendBuf, int sendLen, 
			byte[] recvBuf, int recvLen, Integer lpBytesReturned) {
		return nativeControlSCard(ctrlCode, sendBuf, sendLen, recvBuf, recvLen, lpBytesReturned);
	}
	
	public int transmitSCard(byte[] sendBuf, int sendLen, byte[] recvBuf, Integer recvLen) {
		return nativeTransmitSCard(sendBuf, sendLen, recvBuf, recvLen);
	}
	
	public String[] listSCardReaderGroups(int hGroups) {
		return nativeListSCardReaderGroups(hGroups);
	}
	
	public String[] listSCardReaders(String groups, int hReaders) {
		return nativeListSCardReaders(groups, hReaders);
	}
	
	public int cancelSCard() {
		return nativeCancelSCard();
	}
	
	public int getSCardAttrib(int attrId, byte[] attr, Integer attrLen) {
		return nativeGetSCardAttrib(attrId, attr, attrLen);
	}
	
	public int setSCardAttrib(int attrId, byte[] attr, int attrLen) {
		return nativeSetScardAttrib(attrId, attr, attrLen);
	}
	
	public String getPcscIfyError(int ret) {
		return nativeGetPcscIfyError(ret);
	}
	/*-----------------------------------------------------------------------------------------*/
	
	static {
		System.loadLibrary("scard");
	}
	
}
