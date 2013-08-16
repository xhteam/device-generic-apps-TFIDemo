package com.quester.demo.scard;

import com.quester.demo.R;
import com.quester.demo.SCardManager;
import com.quester.demo.SCardPcscLite;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;

public class SCardActivity extends Activity implements SCardPcscLite {
	
	@SuppressWarnings("unused")
	private static final String TAG = "scard";
	
	private static final int ESTABLISH = 0x1;
	private static final int RELEASE = 0x2;
	private static final int ISVALID = 0x3;
	private static final int CONNECT = 0x4;
	private static final int RECONNECT = 0x5;
	private static final int DISCONNECT = 0x6;
	private static final int BEGIN = 0x7;
	private static final int END = 0x8;
	private static final int STATUS = 0x9;
	private static final int CHANGE = 0xa;
	private static final int TRANSMIT = 0xb;
	private static final int GROUPS = 0xc;
	private static final int READERS = 0xd;
	
	private SCardManager mSCardManager;
	private TextView mTextView;
	private ImageButton mButton;
	private String[] mGroups;
	private String[] mReaders;
	
	private static byte[] reader;
	private static Integer readerLen;
	private static Integer state;
	private static Integer prot;
	private static byte[] atr;
	private static Integer atrLen;
	private static byte[] sendBuf;
	private static byte[] recvBuf;
	private static Integer recvLen;
	private static Integer eventState;
	
	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			int ret = msg.arg1;
			switch (msg.what) {
			case ESTABLISH:
				if (responsed(ret)) {
					mTextView.setText(getDetails(R.string.scard_establish_context, 
							R.string.scard_complete));
				} else {
					mTextView.setText(getDetails(R.string.scard_establish_context, 
							R.string.scard_error));
					mTextView.append(getErrors(ret));
				}
				break;
			case ISVALID:
				if (responsed(ret)) {
					mTextView.append(getDetails(R.string.scard_is_valid_context, 
							R.string.scard_valid));
				} else {
					mTextView.append(getDetails(R.string.scard_is_valid_context, 
							R.string.scard_invalid));
					mTextView.append(getErrors(ret));
				}
				break;
			case GROUPS:
				if (responsed(ret)) {
					mTextView.append(getDetails(R.string.scard_groups, R.string.scard_valid));
					int nbGroups = mGroups.length;
					for (int i = 0; i < nbGroups; i++) {
						mTextView.append("-> " + getString(R.string.scard_groupname) + 
								mGroups[i] + "\n");
					}
				} else {
					mTextView.append(getDetails(R.string.scard_groups, R.string.scard_invalid));
				}
				break;
			case READERS:
				if (responsed(ret)) {
					mTextView.append(getDetails(R.string.scard_readers, R.string.scard_valid));
					int nbReaders = mReaders.length;
					for (int i = 0; i < nbReaders; i++) {
						mTextView.append("-> " + getString(R.string.scard_readername) + 
								mReaders[i] + "\n");
					}
					mTextView.append(getString(R.string.scard_connecting) + mReaders[0] + "\n");
				} else {
					mTextView.append(getDetails(R.string.scard_readers, R.string.scard_invalid));
				}
				break;
			case CONNECT:
				if (responsed(ret)) {
					mTextView.append(getDetails(R.string.scard_connect_state, 
							R.string.scard_connect));
				} else {
					mTextView.append(getDetails(R.string.scard_connect_state, 
							R.string.scard_error));
					mTextView.append(getErrors(ret));
				}
				break;
			case STATUS:
				if (responsed(ret)) {
					mTextView.append(getDetails(R.string.scard_statues, R.string.scard_complete));
					mTextView.append("-> Reader: " + new String(reader, 0, readerLen.intValue()) + "\n");
					mTextView.append("-> State: " + String.format("0x%04X", state.intValue()) + "\n");
					mTextView.append("-> Protocal: " + whichProtocol(prot.intValue()) + "\n");
					mTextView.append("-> ATR: " + byteArrayToString(atr, atrLen.intValue()) + "\n");
				} else {
					mTextView.append(getDetails(R.string.scard_statues, R.string.scard_error));
					mTextView.append(getErrors(ret));
				}
				break;
			case BEGIN:
				if (responsed(ret)) {
					mTextView.append(getDetails(R.string.scard_begin_transaction, 
							R.string.scard_complete));
				} else {
					mTextView.append(getDetails(R.string.scard_begin_transaction, 
							R.string.scard_error));
					mTextView.append(getErrors(ret));
				}
				break;
			case TRANSMIT:
				if (responsed(ret)) {
					mTextView.append(getDetails(R.string.scard_transmit, R.string.scard_complete));
					mTextView.append("-> " + getString(R.string.scard_sending) + 
							byteArrayToString(sendBuf, sendBuf.length) + "\n");
					mTextView.append("-> " + getString(R.string.scard_received) + 
							byteArrayToString(recvBuf, recvLen.intValue()) + "\n");
				} else {
					mTextView.append(getDetails(R.string.scard_transmit, R.string.scard_error));
					mTextView.append(getErrors(ret));
				}
				break;
			case END:
				if (responsed(ret)) {
					mTextView.append(getDetails(R.string.scard_end_transaction, 
							R.string.scard_complete));
				} else {
					mTextView.append(getDetails(R.string.scard_end_transaction, 
							R.string.scard_error));
					mTextView.append(getErrors(ret));
				}
				break;
			case RECONNECT:
				if (responsed(ret)) {
					mTextView.append(getDetails(R.string.scard_connect_state, 
							R.string.scard_reconnect));
				} else {
					mTextView.append(getDetails(R.string.scard_connect_state, 
							R.string.scard_error));
					mTextView.append(getErrors(ret));
				}
				break;
			case CHANGE:
				if (responsed(ret)) {
					mTextView.append(getDetails(R.string.scard_statues_change, 
							R.string.scard_complete));
					mTextView.append("Reader: " + new String(reader, 0, readerLen.intValue()) + "\n");
					mTextView.append("State: " + String.format("0x%04X", eventState.intValue()) + "\n");
					mTextView.append("ATR: " + byteArrayToString(atr, atrLen.intValue()) + "\n");
				} else {
					mTextView.append(getDetails(R.string.scard_statues_change, 
							R.string.scard_error));
					mTextView.append(getErrors(ret));
				}
				break;
			case DISCONNECT:
				if (responsed(ret)) {
					mTextView.append(getDetails(R.string.scard_connect_state, 
							R.string.scard_disconnect));
				} else {
					mTextView.append(getDetails(R.string.scard_connect_state, 
							R.string.scard_error));
					mTextView.append(getErrors(ret));
				}
				break;
			case RELEASE:
				if (responsed(ret)) {
					mTextView.append(getDetails(R.string.scard_release_context, 
							R.string.scard_complete));
				} else {
					mTextView.append(getDetails(R.string.scard_release_context, 
							R.string.scard_error));
					mTextView.append(getErrors(ret));
				}
				break;
			default:
				break;
			}
			mTextView.append("\n");
		}
	};
	
	private String getDetails(int target, int result) {
		String details = getString(target) + getString(result) + "\n";
		return details;
	}
	
	private String getErrors(int ret) {
		String errors = getString(R.string.scard_reason) + 
				mSCardManager.getPcscIfyError(ret) + "\n";
		return errors;
	}
	
	private String byteArrayToString(byte[] data, int lens) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < lens; i++) {
			sb.append(String.format("%02X ", data[i]));
		}
		return sb.toString();
	}
	
	private String whichProtocol(int prot) {
		String protocol;
		if (prot == SCARD_PROTOCOL_T0) {
			protocol = "T0";
		} else if (prot == SCARD_PROTOCOL_T1) {
			protocol = "T1";
		} else if (prot == SCARD_PROTOCOL_ANY) {
			protocol = "T0 | T1";
		} else if (prot == SCARD_PROTOCOL_T15) {
			protocol = "T15";
		} else if (prot == SCARD_PROTOCOL_RAW) {
			protocol = "RAW";
		} else {
			protocol = "UNDEFINED";
		}
		return protocol;
	}
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activitiy_scard);
		mTextView = (TextView)findViewById(R.id.scard_info);
		mButton = (ImageButton)findViewById(R.id.scard_refresh);
		mButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				mTextView.setText("");
				processing();
			}
		});
		mSCardManager = new SCardManager();
		processing();
	}
	
	private void initDatas() {
		reader = new byte[MAX_READERNAME];
		readerLen = Integer.valueOf(MAX_READERNAME);
		state = Integer.valueOf(0);
		prot = Integer.valueOf(0);
		atr = new byte[MAX_ATR_SIZE];
		atrLen = Integer.valueOf(MAX_ATR_SIZE);
	}
	
	private void initTransmitBuffers() {
		byte[] prepSend = {0x00, (byte)0xa4, 0x00, 0x00, 0x02, 0x3f, 0x00};
		sendBuf = prepSend;
		recvBuf = new byte[10];
		recvLen = Integer.valueOf(recvBuf.length);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
	}
	
	private void processing() {
		new Thread(new Runnable() {
			public void run() {
				int ret = -1;
				/* establish context */
				ret = mSCardManager.establishSCardContext(SCARD_SCOPY_SYSTEM);
				mHandler.sendMessage(mHandler.obtainMessage(ESTABLISH, ret, ret));
				if (!responsed(ret)) {
					return;
				}
				
				/* is valid context */
				ret = mSCardManager.isValidSCardContext();
				mHandler.sendMessage(mHandler.obtainMessage(ISVALID, ret, ret));
				if (!responsed(ret)) {
					endProcessing();
					return;
				}
				
				/* list reader groups  */
				mGroups = mSCardManager.listSCardReaderGroups(SCARD_AUTOALLOCATE);
				if (mGroups == null) {
					mHandler.sendMessage(mHandler.obtainMessage(GROUPS, -1, -1));
				} else {
					mHandler.sendMessage(mHandler.obtainMessage(GROUPS, SCARD_S_SUCCESS, 
							SCARD_S_SUCCESS));
				}
				
				/* list readers */
				mReaders = mSCardManager.listSCardReaders(null, SCARD_AUTOALLOCATE);
				if (mReaders == null) {
					mHandler.sendMessage(mHandler.obtainMessage(READERS, -1, -1));
					endProcessing();
					return;
				}
				mHandler.sendMessage(mHandler.obtainMessage(READERS, SCARD_S_SUCCESS, 
						SCARD_S_SUCCESS));
				
				/* connect */
				ret = mSCardManager.connectSCard(mReaders[0], SCARD_SHARE_SHARED, 
						SCARD_PROTOCOL_ANY);
				mHandler.sendMessage(mHandler.obtainMessage(CONNECT, ret, ret));
				if (!responsed(ret)) {
					endProcessing();
					return;
				}
				
				/* get status */
				initDatas();
				ret = mSCardManager.getSCardStatus(reader, readerLen, state, prot, atr, atrLen);
				mHandler.sendMessage(mHandler.obtainMessage(STATUS, ret, ret));
				
				/* begin transaction */
				ret = mSCardManager.beginSCardTransaction();
				mHandler.sendMessage(mHandler.obtainMessage(BEGIN, ret, ret));
				
				/* transmit, exchange APDU */
				initTransmitBuffers();
				ret = mSCardManager.transmitSCard(sendBuf, sendBuf.length, recvBuf, recvLen);
				mHandler.sendMessage(mHandler.obtainMessage(TRANSMIT, ret, ret));
				
				/* end transaction */
				ret = mSCardManager.endSCardTransaction(SCARD_LEAVE_CARD);
				mHandler.sendMessage(mHandler.obtainMessage(END, ret, ret));
				
				/* reconnect */
				ret = mSCardManager.reconnectSCard(SCARD_SHARE_SHARED, 
						SCARD_PROTOCOL_ANY, SCARD_LEAVE_CARD);
				mHandler.sendMessage(mHandler.obtainMessage(RECONNECT, ret, ret));
				
				/* get status change */
				eventState = Integer.valueOf(0);
				ret = mSCardManager.getSCardStatusChange(0, mReaders[0], eventState, atr, atrLen);
				mHandler.sendMessage(mHandler.obtainMessage(CHANGE, ret, ret));
				
				/* disconnect */
				ret = mSCardManager.disconnectSCard(SCARD_UNPOWER_CARD);
				mHandler.sendMessage(mHandler.obtainMessage(DISCONNECT, ret, ret));
				
				endProcessing();
			}
			
			private void endProcessing() {
				int ret = -1;
				ret = mSCardManager.releaseSCardContext();
				mHandler.sendMessage(mHandler.obtainMessage(RELEASE, ret, ret));
			}
		}).start();
	}
	
	
	private boolean responsed(int ret) {
		return (ret == SCARD_S_SUCCESS);
	}
	
}
