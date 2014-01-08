package com.quester.demo.nfc;

import java.io.IOException;

//import org.xmlpull.v1.XmlPullParserException;

import com.quester.demo.R;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
//import android.content.res.XmlResourceParser;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcEvent;
import android.nfc.Tag;
import android.nfc.NfcAdapter.CreateNdefMessageCallback;
import android.nfc.tech.IsoDep;
import android.nfc.tech.MifareClassic;
import android.nfc.tech.MifareUltralight;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
//import android.nfc.tech.NdefFormatable;
import android.nfc.tech.NfcA;
import android.nfc.tech.NfcB;
import android.nfc.tech.NfcF;
import android.nfc.tech.NfcV;
//import android.nfc.tech.NfcBarcode;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.util.Log;
import android.widget.TextView;

/**
 * Foreground activity that handling the NFC Tag
 * @author John.Jian
 */
public class NfcActivity extends Activity implements CreateNdefMessageCallback {
	
	private static final String TAG = "nfc";
	private static final int TECH_ISODEP = 0;
	private static final int TECH_NFCA = 1;
	private static final int TECH_NFCB = 2;
	private static final int TECH_NFCF = 3;
	private static final int TECH_NFCV = 4;
	private static final int TECH_NDEF = 5;
	private static final int TECH_NDEFFORMATABLE = 6;
	private static final int TECH_MIFARECLASSIC = 7;
	private static final int TECH_MIFAREULTRALIGHT = 8;
//	private static final int TECH_NFCBARCODE = 9;
	
	private TextView mTextView;
	private NfcAdapter mNfcAdapter;
	private PendingIntent mIntent;
	private IntentFilter[] mFilters;
	private String[][] mTechLists;
	private boolean mPause = true;
	
	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case TECH_MIFARECLASSIC:
				mTextView.append((String)msg.obj);
				break;
			case TECH_MIFAREULTRALIGHT:
				mTextView.append((String)msg.obj);
				break;
			default:
				break;
			}
		}
	};
	
	@Override
	protected void onCreate(Bundle saveInstanceState) {
		super.onCreate(saveInstanceState);
		setContentView(R.layout.activity_nfc);
		mTextView = (TextView)findViewById(R.id.nfc_info);
		//check for available NFC adapter
		mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
		if (mNfcAdapter == null) {
			mTextView.setText("NFC is not available");
		} else {
			//initialize foreground dispatch attr
			mIntent = PendingIntent.getActivity(
					this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
			IntentFilter discovered = new IntentFilter();
			discovered.addAction(NfcAdapter.ACTION_NDEF_DISCOVERED);
			discovered.addAction(NfcAdapter.ACTION_TECH_DISCOVERED);
			discovered.addAction(NfcAdapter.ACTION_TAG_DISCOVERED);
			mFilters = new IntentFilter[] {discovered};
//			getPersistTechs();
			mTechLists = new String[][] {
					new String[] {IsoDep.class.getName()}, 
					new String[] {NfcA.class.getName()}, 
					new String[] {NfcB.class.getName()}, 
					new String[] {NfcF.class.getName()}, 
					new String[] {NfcV.class.getName()}, 
					new String[] {Ndef.class.getName()}, 
					new String[] {NdefFormatable.class.getName()}, 
					new String[] {MifareClassic.class.getName()}, 
					new String[] {MifareUltralight.class.getName()}, 
					/*new String[] {NfcBarcode.class.getName()}*/ };
			
			mNfcAdapter.setNdefPushMessageCallback(this, this);
			if (!mNfcAdapter.isEnabled()) {
				mTextView.setText("nfc is disable");
			}
		}
	}
	
	/*private void getPersistTechs() {
		XmlResourceParser xrp = getResources().getXml(R.xml.nfc_tech_filter);
		StringBuilder sb = new StringBuilder();
		try {
			int eventType = xrp.getEventType();
			while (eventType != XmlResourceParser.END_DOCUMENT) {
				if (eventType == XmlResourceParser.START_TAG) {
					if (xrp.getName().equals("tech")) {
						sb.append(xrp.nextText() + ",");
					}
				}
				eventType = xrp.next();
			}
		} catch (XmlPullParserException e) {
			Log.i(TAG, "parseXml, " + e.getMessage());
		} catch (IOException e) {
			Log.i(TAG, "parseXml, " + e.getMessage());
		}
		
		if(sb.length() > 0) {
			String[] mTechs = sb.toString().split(",");
			for (int i = 0; i < mTechs.length; i++) {
				mTechLists[i] = new String[] {mTechs[i]};
			}
		}
	}*/
	
	private void handleIntent(Intent intent) {
		String action = intent.getAction();
		if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {
			mTextView.setText("\"Android NFC data exchange format\"\n\n");
			mTextView.append(processIntentNdef(intent));
		} else if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)) {
			mTextView.setText("\"Standard NFC tag technology\"\n\n");
			processIntentTech(intent);
		} else if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(action)) {
			mTextView.setText("\"Unknown NFC tag technology\"\n\n");
		} else {
			mTextView.setText("Standby..");
		}
	}
	
	@Override
	protected void onNewIntent(Intent intent) {
		//onResume gets called after this to handle the intent
		setIntent(intent);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		if (mNfcAdapter == null) {
			return;
		}
		if (mPause) {
			mPause = false;
			mNfcAdapter.enableForegroundDispatch(this, mIntent, mFilters, mTechLists);
		}
		handleIntent(getIntent());
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		mPause = true;
		if (mNfcAdapter != null && mNfcAdapter.isEnabled()) {
			mNfcAdapter.disableForegroundDispatch(this);
		}
	}
	
	/**
	 * Parse the NDEF message from the intent
	 */
	private String processIntentNdef(Intent intent) {
		StringBuilder sb = new StringBuilder();
		sb.append("NFC technology: Ndef\n");
		sb.append("Tag id: " + byteArrayToString(intent.getByteArrayExtra(NfcAdapter.EXTRA_ID)) + "\n");
		Parcelable[] rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
		if (rawMsgs != null) {
			int lens = rawMsgs.length;
			NdefMessage[] mMsgs = new NdefMessage[lens];
			for (int i = 0; i < lens; i++) {
				mMsgs[i] = (NdefMessage)rawMsgs[i];
				NdefRecord[] mRecords = mMsgs[i].getRecords();
				for (int j = 0; j < mRecords.length; j++) {
					//the variable length payload, there just handle text type
					byte[] payload= mRecords[j].getPayload();
					sb.append(new String(payload) + "\n");
				}
			}
		}
		
		return sb.toString();
	}
	
	/**
	 * Parses the tag technology message from the intent
	 */
	private void processIntentTech(Intent intent) {
		Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
		String id = byteArrayToString(intent.getByteArrayExtra(NfcAdapter.EXTRA_ID));
		for (String tech : tag.getTechList()) {
			switch (availableTech(tech)) {
			case TECH_ISODEP:
				mTextView.append(processIsoDep(tag, id));
				break;
			case TECH_NFCA:
				mTextView.append(processNfcA(tag, id));
				break;
			case TECH_NFCB:
				mTextView.append(processNfcB(tag, id));
				break;
			case TECH_NFCF:
				mTextView.append(processNfcF(tag, id));
				break;
			case TECH_NFCV:
				mTextView.append(processNfcV(tag, id));
				break;
			case TECH_NDEF:
				mTextView.append(processNdef(tag, id));
				break;
			case TECH_NDEFFORMATABLE:
				mTextView.append(processNdefFormatable(tag, id));
				break;
			case TECH_MIFARECLASSIC:
				processMifareClassic(tag, id);
				break;
			case TECH_MIFAREULTRALIGHT:
				processMifareUltralight(tag, id);
				break;
//			case TECH_NFCBARCODE:
//				mTextView.append(processNfcBarcode(tag, id));
//				break;
			default:
				mTextView.append("NFC technology: unknown\n");
				break;
			}
		}
	}
	
	private int availableTech(String tech) {
		int ret = -1;
		if (tech.equals(mTechLists[0][0])) {
			ret = TECH_ISODEP;
		} else if (tech.equals(mTechLists[1][0])) {
			ret = TECH_NFCA;
		} else if (tech.equals(mTechLists[2][0])) {
			ret = TECH_NFCB;
		} else if (tech.equals(mTechLists[3][0])) {
			ret = TECH_NFCF;
		} else if (tech.equals(mTechLists[4][0])) {
			ret = TECH_NFCV;
		} else if (tech.equals(mTechLists[5][0])) {
			ret = TECH_NDEF;
		} else if (tech.equals(mTechLists[6][0])) {
			ret = TECH_NDEFFORMATABLE;
		} else if (tech.equals(mTechLists[7][0])) {
			ret = TECH_MIFARECLASSIC;
		} else if (tech.equals(mTechLists[8][0])) {
			ret = TECH_MIFAREULTRALIGHT;
//		} else if (tech.equals(mTechLists[9][0])) {
//			ret = TECH_NFCBARCODE;
		}
		return ret;
	}
	
	/*
	 * IsoDep
	 * 
	 * Provides access to ISO-DEP (ISO 14443-4) properties and I/O operations on a Tag.
	 * 
	 * Tags that enumerate the IsoDep technology in getTechList() will also enumerate 
	 * NfcA or NfcB (since IsoDep builds on top of either of these)
	 */
	private String processIsoDep(Tag tag, String id) {
		StringBuilder sb = new StringBuilder();
		sb.append("NFC technology: IsoDep (ISO 14443-4)\n");
		sb.append("Tag id: " + id + "\n");
		try {
			IsoDep idep = IsoDep.get(tag);
			idep.connect();
			byte[] nfca = idep.getHistoricalBytes();
			if (nfca != null) {
				sb.append("|-- NfcA: " + byteArrayToString(nfca) + "\n");
			}
			byte[] nfcb = idep.getHiLayerResponse();
			if (nfcb != null) {
				sb.append("|-- NfcB: " + byteArrayToString(nfcb) + "\n");
			}
			// Use idep.getMaxTransceiveLength() to retrieve the maximum number of bytes 
			// that can be sent with idep.transceive(byte[]). transceive is an I/O opration 
			// and will block until complete. It must not be called from the main application 
			// thread.
			idep.close();
		} catch (IOException e) {
			Log.i(TAG, "IsoDep, " + e.getMessage());
		}
		
		return sb.toString();
	}
	
	/*
	 * NfcA
	 * 
	 * Provides access to NFC-A (ISO 14443-3A) properties and I/O operations on a Tag.
	 */
	private String processNfcA(Tag tag, String id) {
		StringBuilder sb = new StringBuilder();
		sb.append("NFC technology: NfcA (ISO 14443-3A)\n");
		sb.append("Tag id: " + id + "\n");
		try {
			NfcA na = NfcA.get(tag);
			na.connect();
			int sak = na.getSak();
			sb.append("|-- SAK: " + sak + "\n");
			sb.append("|-- ATQA: " + byteArrayToString(na.getAtqa()) + "\n");
			// Use na.getMaxTransceiveLength() to retrieve the maximum number of bytes 
			// that can be sent with na.transceive(byte[]). transceive is an I/O opration 
			// and will block until complete. It must not be called from the main application 
			// thread.
			na.close();
		} catch (IOException e) {
			Log.i(TAG, "NfcA, " + e.getMessage());
		}
		
		return sb.toString();
	}
	
	/*
	 * NfcB
	 * 
	 * Provides access to NFC-B (IOS 14443-3B) properties and I/O operations on a Tag.
	 */
	private String processNfcB(Tag tag, String id) {
		StringBuilder sb = new StringBuilder();
		sb.append("NFC technology: NfcB (ISO 14443-3B)\n");
		sb.append("Tag id: " + id + "\n");
		try {
			NfcB nb = NfcB.get(tag);
			nb.connect();
			sb.append("|-- Protocol info: " + byteArrayToString(nb.getProtocolInfo()) + "\n");
			sb.append("|-- App data: " + byteArrayToString(nb.getApplicationData()) + "\n");
			// Use nb.getMaxTransceiveLength() to retrieve the maximum number of bytes 
			// that can be sent with nb.transceive(byte[]). transceive is an I/O opration 
			// and will block until complete. It must not be called from the main application 
			// thread.
			nb.close();
		} catch (IOException e) {
			Log.i(TAG, "NfcB, " + e.getMessage());
		}
		
		return sb.toString();
	}
	
	/*
	 * NfcF
	 * 
	 * Provides access to NFC-F (JIS 6319-4) properties and I/O operations on a Tag.
	 */
	private String processNfcF(Tag tag, String id) {
		StringBuilder sb = new StringBuilder();
		sb.append("NFC technology: NfcF (JIS 6319-4)\n");
		sb.append("Tag id: " + id + "\n");
		try {
			NfcF nf = NfcF.get(tag);
			nf.connect();
			sb.append("|-- Manufacturer: " + byteArrayToString(nf.getManufacturer()) + "\n");
			sb.append("|-- System code: " + byteArrayToString(nf.getSystemCode()) + "\n");
			// Use nf.getMaxTransceiveLength() to retrieve the maximum number of bytes 
			// that can be sent with nf.transceive(byte[]). transceive is an I/O opration 
			// and will block until complete. It must not be called from the main application 
			// thread.
			nf.close();
		} catch (IOException e) {
			Log.i(TAG, "NfcF, " + e.getMessage());
		}
		
		return sb.toString();
	}
	
	/*
	 * NfcV
	 * 
	 * Provides access to NFC-V (ISO 15693) properties and I/O operations on a Tag.
	 */
	private String processNfcV(Tag tag, String id) {
		StringBuilder sb = new StringBuilder();
		sb.append("NFC technology: NfcV (ISO 15693)\n");
		sb.append("Tag id: " + id + "\n");
		try {
		NfcV nv = NfcV.get(tag);
			nv.connect();
			sb.append("|-- DSF id: " + String.format("0x%02X", nv.getDsfId()) + "\n");
			sb.append("|-- Response flag: " + String.format("0x%02X", nv.getResponseFlags()) + "\n");
			// Use nv.getMaxTransceiveLength() to retrieve the maximum number of bytes 
			// that can be sent with nv.transceive(byte[]). transceive is an I/O opration 
			// and will block until complete. It must not be called from the main application 
			// thread.
			nv.close();
		} catch (IOException e) {
			Log.i(TAG, "NfcV, " + e.getMessage());
		}
		
		return sb.toString();
	}
	
	/*
	 * NDEF
	 * 
	 * Provides access to NDEF content and operations on a Tag.
	 * 
	 * NDEF is an NFC Forum data format. The data formats are implemented in NdefMessage 
	 * and NdefRecord. This class provides methods to retrieve and modify the NdefMessage 
	 * on a tag.
	 * 
	 * There are currently four NFC Forum standardized tag types that can ben formatted 
	 * to contain NDEF data.
	 * - Innovision Topaz
	 * - NXP MIFARE Ultralight
	 * - Sony Felica
	 * - NXP MIFARE Desfire
	 */
	private String processNdef(Tag tag, String id) {
		StringBuilder sb = new StringBuilder();
		sb.append("NFC technology: Ndef\n");
		sb.append("Tag id: " + id + "\n");
		try {
			Ndef ndef = Ndef.get(tag);
			ndef.connect();
			sb.append("|-- Type: " + ndef.getType() + "\n");
			NdefMessage msg = ndef.getCachedNdefMessage();
			if (msg != null) {
				NdefRecord[] mRecords = msg.getRecords();
				for (int i = 0; i < mRecords.length; i++) {
					//the variable length payload, there just handle text type
					byte[] payload= mRecords[i].getPayload();
					sb.append("|-- Record " + i + ": " + new String(payload) + "\n");
				}
			}
			ndef.close();
		} catch (IOException e) {
			Log.i(TAG, "Ndef, " + e.getMessage());
		}
		
		return sb.toString();
	}
	
	/*
	 * NdefFormatable
	 * 
	 * Provide access to NDEF format operations on a Tag.
	 * 
	 * Android devices with NFC must only enumerate and implement this class for 
	 * which it can format to NDEF.
	 * 
	 * Unfortunately the procedures to convert unformated tags to NDEF formatted tags 
	 * are not specified by NFC Forum, and are not generally well-known. So there is 
	 * no mandatory set of tags for which all Android devices with NFC must support it.
	 */
	private String processNdefFormatable(Tag tag, String id) {
		StringBuilder sb = new StringBuilder();
		sb.append("NFC technology: NdefFormatable\n");
		sb.append("Tag id: " + id + "\n");
//		try {
//			NdefFormatable nformat = NdefFormatable.get(tag);
//			nformat.connect();
//			// Format operations are I/O opration and will block until complete.
//			// It must not be called from the main application thread.
//			nformat.close();
//		} catch (IOException e) {
//			Log.i(TAG, "NdefFormatable, " + e.getMessage());
//		}
		return sb.toString();
	}
	
	/*
	 * MifareClassic
	 * 
	 * Provides access to MIFARE Classic properties and I/O operations on a Tag.
	 * 
	 * MIFARE Classic is also known as MIFARE Standard.
	 */
	private void processMifareClassic(final Tag tag, final String id) {
		new Thread(new Runnable() {
			public void run() {
				StringBuilder sb = new StringBuilder();
				sb.append("NFC technology: MifareClassic\n");
				sb.append("Tag id: " + id + "\n");
//				try {
//					MifareClassic mc = MifareClassic.get(tag);
//					mc.connect();
//					int type = mc.getType();
//					switch (type) {
//					case MifareClassic.TYPE_CLASSIC:
//						sb.append("|-- Type: classic\n");
//						break;
//					case MifareClassic.TYPE_PLUS:
//						sb.append("|-- Type: plus\n");
//						break;
//					case MifareClassic.TYPE_PRO:
//						sb.append("|-- Type: pro\n");
//						break;
//					default:
//						sb.append("|-- Type: unknown\n");
//						break;
//					}
//					
//					int sectorCount = mc.getSectorCount();
//					sb.append("|-- Tag size: " + mc.getSize() + "B\n");
//					sb.append("|-- Sector count: " + sectorCount + "\n");
//					sb.append("|-- Block count: " + mc.getBlockCount() + "\n");
//					
//					int blockCount = 0;
//					int blockIndex = 0;
//					for (int i = 0; i < sectorCount; i++) {
//						if (mc.authenticateSectorWithKeyA(i, MifareClassic.KEY_DEFAULT) 
//								|| mc.authenticateSectorWithKeyA(i, MifareClassic.KEY_MIFARE_APPLICATION_DIRECTORY) 
//								|| mc.authenticateSectorWithKeyA(i, MifareClassic.KEY_NFC_FORUM)) {
//							sb.append("|  |-- Successful authentication of sector " + i + " with key A\n");
//						} else if (mc.authenticateSectorWithKeyB(i, MifareClassic.KEY_DEFAULT) 
//								|| mc.authenticateSectorWithKeyB(i, MifareClassic.KEY_MIFARE_APPLICATION_DIRECTORY) 
//								|| mc.authenticateSectorWithKeyB(i, MifareClassic.KEY_NFC_FORUM)) {
//							sb.append("|  |-- Successful authentication of sector " + i + " with key B\n");
//						} else {
//							sb.append("|  |-- Failed authentication of sector " + i + " with key A & B\n");
//							continue;
//						}
//						blockCount = mc.getBlockCountInSector(i);
//						blockIndex = mc.sectorToBlock(i);
//						for (int j = 0; j < blockCount; j++) {
//							sb.append("|  |  |-- Block " + blockIndex + ": " 
//									+ byteArrayToString(mc.readBlock(blockIndex)) + "\n");
//							blockIndex++;
//						}
//					}
//					mc.close();
//				} catch (IOException e) {
//					Log.i(TAG, "MifareClassic, " + e.getMessage());
//				}
				mHandler.sendMessage(mHandler.obtainMessage(TECH_MIFARECLASSIC, sb.toString()));
			}
		}).start();
	}
	
	/*
	 * MifareUltralight
	 * 
	 * Provides access to MIFARE Ultralight properties and I/O operations on a Tag.
	 * 
	 * The original MIFARE Ultralight consists of a 64 byte EEPROM.
	 * The MIFARE Ultralight C consists of a 192 byte EEPROM.
	 */
	private void processMifareUltralight(final Tag tag, final String id) {
		new Thread(new Runnable() {
			public void run() {
				StringBuilder sb = new StringBuilder();
				sb.append("NFC technology: MifareUltralight\n");
				sb.append("Tag id: " + id + "\n");
//				try {
//					MifareUltralight mu = MifareUltralight.get(tag);
//					mu.connect();
//					int type = mu.getType();
//					if (type == MifareUltralight.TYPE_ULTRALIGHT) {
//						sb.append("|-- Type: MIFARE Ultralight\n");
//						for (int i = 0; i < 4; i++) {
//							sb.append("|-- Page " + (i*4) + "~" + ((i+1)*4-1) + ": " 
//									+ byteArrayToString(mu.readPages(i)) + "\n");
//						}
//					} else if (type == MifareUltralight.TYPE_ULTRALIGHT_C) {
//						sb.append("|-- Type: MIFARE Ultralight C\n");
//						for (int i = 0; i < 12; i++) {
//							sb.append("|-- Page " + (i*4) + "~" + ((i+1)*4-1) + ": " 
//									+ byteArrayToString(mu.readPages(i)) + "\n");
//						}
//					} else {
//						sb.append("|-- Type: unknown\n");
//					}
//					mu.close();
//				} catch (IOException e) {
//					Log.i(TAG, "MifareUtralight, " + e.getMessage());
//				}
				mHandler.sendMessage(mHandler.obtainMessage(TECH_MIFAREULTRALIGHT, sb.toString()));
			}
		}).start();
	}
	
	/*
	 * API level 17 (current min is 16)
	 */
	/*private String processNfcBarcode(Tag tag, String id) {
		NfcBarcode nbarcode = NfcBarcode.get(tag);
		StringBuilder sb = new StringBuilder();
		sb.append("NFC technology: NfcBarcode\n");
		sb.append("Tag id: " + id + "\n");
		try {
			nbarcode.connect();
			int type = nbarcode.getType();
			if (type == NfcBarcode.TYPE_KOVIO) {
				sb.append("|-- type: kovio\n");
			} else {
				sb.append("|-- type: unknown\n");
			}
			sb.append("|-- barcode: " + byteArrayToString(nbarcode.getBarcode()) + "\n");
			nbarcode.close();
		} catch (IOException e) {
			Log.i(TAG, "NfcBarcode, " + e.getMessage());
		}
		
		return sb.toString();
	}*/
	
	private String byteArrayToString(byte[] data) {
		int lens = data.length;
		StringBuilder sb = new StringBuilder();
		sb.append("0x");
		for (int i = 0; i < lens; i++) {
			sb.append(String.format("%02X", data[i]));
		}
		return sb.toString();
	}

	@Override
	public NdefMessage createNdefMessage(NfcEvent event) {
		String text = "Beam me up, Android!\nBeam Time:" + System.currentTimeMillis();
		NdefMessage msg = new NdefMessage(new NdefRecord[]{
				NdefRecord.createMime("application/vnd.com.example.android.beam", text.getBytes()),
				/*
				 * The Android Application Record (AAR) is commented out. When a device 
				 * recives a push with an AAR in it, the applications specified in the AAR 
				 * is guaranteed to run. The AAR overrides the tag dispatch system.
				 * You can add it back in to guarantee that this activity starts when receiving 
				 * a beamed message. For now, this code uses the tag dispatch system.
				 */
				//NdefRecord.createApplicationRecord("complete package name")
		});
		return msg;
	}
}
