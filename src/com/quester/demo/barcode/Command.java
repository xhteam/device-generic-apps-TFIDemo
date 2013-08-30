package com.quester.demo.barcode;

/**
 * Newland EM3070
 * Serial port programming manual
 * @author John.Jian
 * @version 1.2.7
 */
public interface Command {
	
	/*
	 * Define the following abbreviations
	 * ----------------------------------
	 * DF	->	default
	 * HD	->	handle
	 * IG	->	ignore
	 * TX	->	transmit
	 * SEQ	->	sequence
	 * EN	->	enable
	 * DIS	->	disable
	 * CK	->	check
	 * IDSL	->	industrial
	 * STD	->	standard
	 * MC	->	macro
	 * HLC	->	han letter code
	 * MTX	->	matrix
	 * SA	->	same
	 * DFT	->	different
	 * DB	->	databar
	 * SEC	->	start symbol and end mark
	 * IL	->	interleaved
	 * SNTY	->	sensitivity
	 * PSX	->	prefix and suffix
	 * ----------------------------------
	 */
	
	/* Basic */
	public static final byte[] PREFIX_SEND = {0x7e, 0x00};
	public static final byte[] PREFIX_RECV = {0x02, 0x00};
	public static final String PREFIX_LOWER = "nls";
	public static final String PREFIX_UPPER ="NLS";
	public static final byte QUERY_TYPES = 0x33;
	public static final byte RECV_TYPES = 0x34;
	public static final byte DEV_ASK = '?';
	public static final byte DEV_REPLY = '!';
	public static final long MAX_DELAY = 500;
	
	/* Return value */
	public static final byte SUCCESS = 0x06;
	public static final byte FAILTURE = 0x15;
	
	/* Analog read mode settings */
	public static final byte[] ANALOG_TRIIGER_DOWN = {0x1b, 0x31};
	public static final byte[] ANALOG_TRIGGER_UP = {0x1b, 0x30};
	public static final byte[] ANALOG_SENSOR = {0x1b, 0x32};
	public static final byte[] ANALOG_CONTINUE = {0x1b, 0x33};
	
	/* Query attributes */
	public static final byte[] QUERY_RS232 = {0x30};
	public static final byte[] QUERY_1DCODE = {0x32};
	public static final byte[] QUERY_2DCODE = {0x33};
	public static final byte[] QUERY_ZASETUP = {0x43, 0x30, 0x30};
	public static final byte[] QUERY_CODABAR = {0x43, 0x31, 0x35};
	public static final byte[] QUERY_MTX25 = {0x43, 0x31, 0x31};
	public static final byte[] QUERY_CODE39 = {0x43, 0x31, 0x33};
	public static final byte[] QUERY_EAN8 = {0x43, 0x30, 0x34};
	public static final byte[] QUERY_EAN13 = {0x43, 0x30, 0x35};
	public static final byte[] QUERY_UPCE = {0x43, 0x30, 0x36};
	public static final byte[] QUERY_UPCA = {0x43, 0x30, 0x37};
	public static final byte[] QUERY_ITFX = {0x43, 0x30, 0x38};
	public static final byte[] QUERY_CODE93 = {0x43, 0x31, 0x37};
	public static final byte[] QUERY_ISBN = {0x43, 0x32, 0x34};
	public static final byte[] QUERY_IDSL25 = {0x43, 0x32, 0x35};
	public static final byte[] QUERY_STD25 = {0x43, 0x32, 0x36};
	public static final byte[] QUERY_PLESSEY = {0x43, 0x32, 0x37};
	public static final byte[] QUERY_MSIP = {0x43, 0x32, 0x39};
	public static final byte[] QUERY_COMPOSITE = {0x43, 0x33, 0x30};
	public static final byte[] QUERY_RSS = {0x43, 0x33, 0x31};
	public static final byte[] QUERY_CODE11 = {0x43, 0x32, 0x38};
	public static final byte[] QUERY_PDF417 = {0x43, 0x33, 0x32};
	public static final byte[] QUERY_QRC = {0x43, 0x33, 0x33};
	public static final byte[] QUERY_AZTEC = {0x43, 0x33, 0x34};
	public static final byte[] QUERY_DATA_MTX = {0x43, 0x33, 0x35};
	public static final byte[] QUERY_HLC = {0x43, 0x33, 0x39};
	public static final byte[] QUERY_1DCODE_MD = {0x48, 0x30, 0x31};
	public static final byte[] QUERY_BOOT_INFO = {0x48, 0x30, 0x30};
	public static final byte[] QUERY_MIRROR = {0x4e};
	public static final byte[] QUERY_PROMPT = {0x4f};
	public static final byte[] QUERY_PKG = {0x46, 0x30, 0x30};
	public static final byte[] QUERY_EXPOSURE = {0x44, 0x30, 0x36, 0x30};
	public static final byte[] QUERY_READ_AREA = {0x44, 0x30, 0x37, 0x30};
	public static final byte[] QUERY_CAPTURE = {0x50};
	public static final byte[] QUERY_HID_KBW = {0x51};
	public static final byte[] QUERY_LIGHT = {0x35};
	public static final byte[] QUERY_CUSTOM_STYLE = {0x37};
	public static final byte[] QUERY_CODEID = {0x38};
	public static final byte[] QUERY_AIM = {0x39};
	public static final byte[] QUERY_END_MARK = {0x40};
	public static final byte[] QUERY_LENS = {0x41};
	public static final byte[] QUERY_PREFIX_SEQ = {0x42};
	public static final byte[] QUERY_READ_MODE = {0x44, 0x30, 0x30, 0x30};
	public static final byte[] QUERY_SNTY = {0x44, 0x30, 0x32, 0x30};
	public static final byte[] QUERY_DELAY_ONCE = {0x44, 0x30, 0x33, 0x30};
	public static final byte[] QUERY_DELAY = {0x44, 0x30, 0x33, 0x31};
	public static final byte[] QUERY_DEV_VER = {0x47};
	public static final byte[] QUERY_DEV_ESN = {0x48, 0x30, 0x32, 0x30};
	public static final byte[] QUERY_DEV_SN = {0x48, 0x30, 0x33, 0x30};
	public static final byte[] QUERY_DEV_DATE = {0x48, 0x30, 0x34, 0x30};
	public static final byte[] QUERY_OCR = {0x49};
	
	/* Integration settings */
	public static final String FACTORY_DF = "0001000";
	public static final String DIS_ALL_BARCODE = "0001010";
	public static final String EN_ALL_BARCODE = "0001020";
	public static final String DIS_ALL_1DCODE = "0001030";
	public static final String EN_ALL_1DCODE = "0001040";
	public static final String DIS_ALL_2DCODE = "0001050";
	public static final String EN_ALL_2DCODE = "0001060";
	public static final String EN_USER_BAT = "0001110";
	public static final String EN_ESN_SET = "0001130";
	public static final String CUR_TO_USER_DF = "0001150";
	public static final String RESET_USER_DF = "0001160";
	
	/* 1-dimensional code monocode and dicode settings */
	public static final String RO_MONOCODE_1DCODE = "0001070";	//DF
	public static final String RA_1DCODE = "0001080";
	public static final String RO_DICODE_1DCODE = "0001090";
	
	/* Setup code */
	public static final String SETUP_CODE_NTX = "0002000";	//DF
	public static final String SETUP_CODE_TX = "0002010";
	public static final String SETUP_CODE_DIS = "0006000";	//DF
	public static final String SETUP_CODE_EN = "0006010";
	
	/* System info */
	public static final String SYS_INFO_TX = "0003000";
	public static final String SYS_INFO_DIS = "0007000";	//DF
	public static final String SYS_INFO_EN = "0007010";
	
	/* Communication port, enable one of them */
	public static final String PORT_RS323 = "1100000";	//DF
	public static final String PORT_USB_DATAPIPE = "1100010";
	public static final String PORT_HID_KBW = "1100020";
	public static final String PORT_BLUETOOTH = "1100040";
	public static final String PORT_USB_EMULATION = "1100060";
	
	/* RS232 attributes */
	public static final String RS232_BAUD_1200 = "0100000";
	public static final String RS232_BAUD_2400 = "0100010";
	public static final String RS232_BAUD_4800 = "0100020";
	public static final String RS232_BAUD_9600 = "0100030";	//DF
	public static final String RS232_BAUD_14400 = "0100040";
	public static final String RS232_BAUD_19200 = "0100050";
	public static final String RS232_BAUD_38400 = "0100060";
	public static final String RS232_BAUD_57600 = "0100070";
	public static final String RS232_BAUD_115200 = "0100080";
	public static final String RS232_NCK = "0101000";	//DF
	public static final String RS232_EVEN_CK = "0101010";
	public static final String RS232_ODD_CK = "0101020";
	public static final String RS232_STOP_BIT1 = "0102000";	//DF
	public static final String RS232_STOP_BIT2 = "0102010";
	public static final String RS232_DATA_BIT5 = "0103000";
	public static final String RS232_DATA_BIT6 = "0103010";
	public static final String RS232_DATA_BIT7 = "0103020";
	public static final String RS232_DATA_BIT8 = "0103030";	//DF
	
	/* HID-KBW attributes */
	public static final String KBW_TYPE = "1103000";	//NLS1103000=0
	public static final String KBW_CAPS_LOCK_DIS = "1103010";	//DF
	public static final String KBW_CPAS_LOCK_EN = "1103020";
	public static final String KBW_PROMPT_UNKNOWN_CHAR_DIS = "1103030";	//DF
	public static final String KBW_PROMPT_UNKNOWN_CHAR_EN = "1103031";
	public static final String KBW_LETTER_UNCHANGE = "1103040";	//DF
	public static final String KBW_LETTER_UPPER = "1103041";
	public static final String KBW_LETTER_LOWER = "1103042";
	public static final String KBW_DELAY_DIS = "1103050";	//DF
	public static final String KBW_DELAY_SHORT = "1103051";	//20ms
	public static final String KBW_DELAY_LONG = "1103052";	//40ms
	public static final String KBW_EMULATION_DIS = "1103060";	//DF
	public static final String KBW_EMULATION_EN = "1103061";
	public static final String KBW_NUMERICK_DIS = "1103110";	//DF
	public static final String KBW_NUMERICK_EN = "1103120";
	public static final String KBW_CTRL_ASCII_DIS = "1103130";	//DF
	public static final String KBW_CTRL_ASCII_EN = "1103140";
	
	/* Bluetooth attributes */
	public static final String BT_RESET = "1105000";
	public static final String BT_DEV_NAME = "1105010";	//16byte
	public static final String BT_AUTH_DIS = "1105020";
	public static final String BT_AUTH_EN = "1105021";	//DF
	public static final String BT_PASSWORD = "1105022";	//6byte
	
	/* Hardware settings */
	public static final String HW_LIGHT_FLASH = "0200000";	//DF
	public static final String HW_LIGHT_ON = "0200010";
	public static final String HW_LIGHT_OFF = "0200020";
	public static final String HW_LIGHT_READ_ON = "0200030";
	public static final String HW_FOCUS_FLASH = "0201000";	//DF
	public static final String HW_FOCUS_ON = "0201010";
	public static final String HW_FOCUS_OFF = "0201020";
	public static final String HW_FOCUS_SENSOR = "0201030";
	public static final String HW_SOUND_OFF = "0203000";
	public static final String HW_SOUND_ON = "0203010";	//DF
	public static final String HW_MIRROR_OFF = "0202000";	//defalut
	public static final String HW_MIRROR_ON = "0202030";
	
	/* Data format settings */
	public static final String PSX_DIS = "0311000";	//DF
	public static final String PSX_EN = "0311010";
	
	/* Data prefix sequence */
	/** CodeID+AIM+Custom */
	public static final String PREFIX_SQE1 = "0317000";	//DF
	/** CodeID+Custom+AIM */
	public static final String PREFIX_SQE2 = "0317010";
	/** AIM+CodeID+Custom */
	public static final String PREFIX_SQE3 = "0317020";
	/** AIM+Custom+CodeID */
	public static final String PREFIX_SQE4 = "0317030";
	/** Custom+CodeID+AIM */
	public static final String PREFIX_SQE5 = "0317040";
	/** Custom+AIM+CodeID */
	public static final String PREFIX_SQE6 = "0317050";
	
	/* Data custom prefix */
	public static final String CUSTOM_PREFIX_DIS = "0305000";	//DF
	public static final String CUSTOM_PREFIX_EN = "0305010";
	public static final String CUSTOM_PREFIX_VALUE = "0300000";	//9byte
	
	/* Data custom suffix */
	public static final String CUSTOM_SUFFIX_DIS = "0306000";	//DF
	public static final String CUSTOM_SUFFIX_EN = "0306010";
	public static final String CUSTOM_SUFFIX_VALUE = "0301000";	//10byte
	
	/* Data CodeID */
	public static final String CODEID_DIS = "0307000";	//DF
	public static final String CODEID_EN = "0307010";
	public static final String CODEID_DF = "0307020";
	
	/* Data AIM */
	public static final String AIM_DIS = "0308000";	//DF
	public static final String AIM_EN = "0308030";
	
	/* Data, 1-dimensional code, CodeID */
	public static final String CODEID_CODE128 = "0004020";
	public static final String CODEID_EAN128 = "0004030";
	public static final String CODEID_EAN8 = "0004040";
	public static final String CODEID_EAN13 = "0004050";
	public static final String CODEID_ISBN = "0004240";
	public static final String CODEID_UPCE = "0004060";
	public static final String CODEID_UPCA = "004070";
	public static final String CODEID_IL = "0004080";
	public static final String CODEID_ITF6 = "0004100";
	public static final String CODEID_ITF14 = "0004090";
	public static final String CODEID_IDSL25 = "0004250";
	public static final String CODEID_STD25 = "0004260";
	public static final String CODEID_MTX25 = "0004110";
	public static final String CODEID_CODE39 = "0004130";
	public static final String CODEID_CODABAR = "0004150";
	public static final String CODEID_CODE11 = "0004280";
	public static final String CODEID_EAN_UCC = "0004300";
	public static final String CODEID_GS1_DB = "0004310";
	public static final String CODEID_PLESSEY = "0004270";
	public static final String CODEID_MSIP = "0004290";
	public static final String CODEID_CODE93 = "0004170";
	
	/* Data, 2-dimensional code, CodeID */
	public static final String CODEID_PDF417 = "0005000";
	public static final String CODEID_QRC = "0005010";
	public static final String CODEID_AZTEC = "0005020";
	public static final String CODEID_DATA_MTX = "0005030";
	public static final String CODEID_MAXIC = "0005040";
	public static final String CODEID_HLC = "0005070";
	
	/* Data package */
	public static final String PKG_DIS = "0314000";	//DF
	public static final String PKG_EN = "0314010";
	
	/* Data end mark */
	public static final String END_MARK_DIS = "0309000";	//DF
	public static final String END_MARK_EN = "0309010";
	public static final String CUSTOM_END_MARK_VALUE = "0310000";	//2byte
	
	/* Data capture settings */
	public static final String CAPTURE_DIS = "0315000";	//DF
	public static final String CAPTURE_EN = "0315010";
	public static final String CAPTURE_ADD = "0316000";
	public static final String CAPTURE_DEL = "0316010";
	public static final String CAPTURE_DEL_RECENTLY = "0316020";
	public static final String CAPTURE_DEL_ALL = "0316030";
	
	/* Decode mode settings */
	public static final String MODE_TRIGGER = "0302000";	//DF
	public static final String MODE_SENSER = "0302010";
	public static final String MODE_CONTINUE = "0302020";
	/** Single & Continue & Sensor */
	public static final String MODE_SCS_MODE = "0302030";
	public static final String SNTY_LOW = "0312000";
	public static final String SNYT_NORMAL = "0312010";	//DF
	public static final String SNTY_HIGH = "0312020";
	public static final String SNTY_ENHANCE = "0312030";
	public static final String SNTY_VALUE = "0312040";	//1~20
	public static final String DELAY_ONCE = "0313000";	//2000ms
	public static final String DELAY_COMMON = "0313010";	//1500ms
	public static final String DELAY_INCOMPLETELY = "0313020";	//DF
	public static final String DELAY_COMPLETELY = "0313030";
	
	/* 1-dimensional code, Code128 settings */
	public static final String CODE128_DF = "0400000";
	public static final String CODE128_DIS = "0400010";
	public static final String CODE128_EN = "0400020";	//DF
	public static final String CODE128_MIN_LENS = "0400030";	//1
	public static final String CODE128_MAX_LENS = "0400040";	//48
	
	/* 1-dimensional code, EAN-8 settings */
	public static final String EAN8_DF = "0401000";
	public static final String EAN8_DIS = "0401010";
	public static final String EAN8_EN = "0401020";	//DF
	public static final String EAN8_NTX_LRC = "0401030";
	public static final String EAN8_TX_LRC = "0401040";	//DF
	public static final String EAN8_EXTEN2_DIS = "0401050";	//DF
	public static final String EAN8_EXTEN2_EN = "0401060";
	public static final String EAN8_EXTEN5_DIS = "0401070";	//DF
	public static final String EAN8_EXTEN5_EN = "0401080";
	public static final String EAN8_EXTEN_EAN13_DIS = "0401090";	//DF
	public static final String EAN8_EXTEN_EAN13_EN = "0401100";
	
	/* 1-dimensional code, EAN-13 settings */
	public static final String EAN13_DF = "0402000";
	public static final String EAN13_DIS = "0402010";
	public static final String EAN13_EN = "0402020";	//DF
	public static final String EAN13_NTX_LRC = "0402030";
	public static final String EAN13_TX_LRC = "0402040";	//DF
	public static final String EAN13_EXTEN2_DIS = "0402050";	//DF
	public static final String EAN13_EXTEN2_EN = "0402060";
	public static final String EAN13_EXTEN5_DIS = "0402070";	//DF
	public static final String EAN13_EXTEN5_EN = "0402080";
	
	/* 1-dimensional code, UPC-E settings */
	public static final String UPCE_DF = "0403000";
	public static final String UPCE_DIS = "0403010";
	public static final String UPCE_EN = "0403020";	//DF
	public static final String UPCE_NTX_LRC = "0403030";
	public static final String UPCE_TX_LRC = "0403040";	//DF
	public static final String UPCE_EXTEN2_DIS = "0403050";	//DF
	public static final String UPCE_EXTEN2_EN = "0403060";
	public static final String UPCE_EXTEN5_DIS = "0403070";	//DF
	public static final String UPCE_EXTEN5_EN = "0403080";
	public static final String UPCE_NTX_CAHR0 = "0403090";	//DF
	public static final String UPCE_TX_CHAR0 = "0403100";
	public static final String UPCE_EXTEN_UPCA_DIS = "0403110";	//DF
	public static final String UPCE_EXTEN_UPCA_EN = "0403120";
	
	/* 1-dimensional code, UPC-A settings */
	public static final String UPCA_DF = "0404000";
	public static final String UPCA_DIS = "0404010";
	public static final String UPCA_EN = "0404020";	//DF
	public static final String UPCA_NTX_LRC = "0404030";
	public static final String UPCA_TX_LRC = "0404040";	//DF
	public static final String UPCA_EXTEN2_DIS = "0404050";	//DF
	public static final String UPCA_EXTEN2_EN = "0404060";
	public static final String UPCA_EXTEN5_DIS = "0404070";	//DF
	public static final String UPCA_EXTEN5_EN = "0404080";
	public static final String UPCA_NTX_CHAR0 = "0404090";	//DF
	public static final String UPCA_TX_CHAR0 = "0404100";
	
	/* 1-dimensional code, Interleaved 2 of 5 settings */
	public static final String IL_DF = "0405000";
	public static final String IL_DIS = "0405010";
	public static final String IL_EN = "0405020";	//DF
	public static final String IL_MIN_LENS = "0405030";	//6
	public static final String IL_MAX_LENS = "0405040";	//80
	public static final String IL_IG_LRC = "0405050";	//DF
	public static final String IL_HD_NTX_LRC = "0405060";
	public static final String IL_HD_TX_LRC = "0405070";
	public static final String IL_IG_ITF14 = "0405080";	//DF
	public static final String IL_HD_NTX_ITF14 = "0405090";
	public static final String IL_HD_TX_ITF14 = "0405100";
	public static final String IL_IG_ITF6 = "0405110";	//DF
	public static final String IL_HD_NTX_ITF6 = "0405120";
	public static final String IL_HD_TX_ITF6 = "0405130";
	public static final String IL_FIXED_LENS_DIS = "0405140";	//DF
	public static final String IL_FIXED_LENS_EN = "0405150";
	public static final String IL_FIXED_LENS_SET = "0405160";
	public static final String IL_FIXED_LENS_CAL = "0405170";
	
	/* 1-dimensional code, Matrix25 settings */
	public static final String MTX25_DF = "0406000";
	public static final String MTX25_DIS = "0406010";	//DF
	public static final String MTX25_EN = "0406020";
	public static final String MTX25_MIN_LENS = "0406030";	//4
	public static final String MTX25_MAX_LENS = "0406040";	//80
	public static final String MTX25_IG_LRC = "0406050";	//DF
	public static final String MTX25_HD_NTX_LRC = "0406060";	//DF
	public static final String MTX25_HD_TX_LRC = "0406070";
	
	/* 1-dimensional code, Code39 settings */
	public static final String CODE39_DF = "0408000";
	public static final String CODE39_DIS = "0408010";
	public static final String CODE39_EN = "0408020";	//DF
	public static final String CODE39_MIN_LENS = "0408030";	//1
	public static final String CODE39_MAX_LENS = "0408040";	//48
	public static final String CODE39_IG_LRC = "0408050";	//DF
	public static final String CODE39_HD_NTX_LRC = "0408060";
	public static final String CODE39_HD_TX_LRC = "0408070";
	public static final String CODE39_NTX_SEC = "0408080";
	public static final String CODE39_TX_SEC = "0408090";	//DF
	public static final String CODE39_ASCII_ALL_DIS = "0408100";	//DF
	public static final String CODE39_ASCII_ALL_EN = "0408110";
	
	/* 1-dimensional code, Codabar settings */
	public static final String CODABAR_DF = "0409000";
	public static final String CODABAR_DIS = "0409010";
	public static final String CODABAR_EN = "0409020";	//DF
	public static final String CODABAR_MIN_LENS = "0409030";	//2
	public static final String CODABAR_MAX_LENS = "0409040";	//60
	public static final String CODABAR_NTX_SEC = "0409080";
	public static final String CODABAR_TX_SEC = "0409090";	//DF
	
	/* 1-dimensional code, Code93 settings */
	public static final String CODE93_DF = "0410000";
	public static final String CODE93_DIS = "0410010";	//DF
	public static final String CODE93_EN = "0410020";
	public static final String CODE93_MIN_LENS = "0410030";	//1
	public static final String CODE93_MAX_LENS = "0410040";	//48
	
	/* 1-dimensional code, UCC/EAN-128 settings */
	public static final String EAN128_DF = "0412000";
	public static final String EAN128_DIS = "0412010";
	public static final String EAN128_EN = "0412020";	//DF
	
	/* 1-dimensional code, GS1 Databar settings */
	public static final String GS1_DB_DF = "0413000";
	public static final String GS1_DB_DIS = "0413010";
	public static final String GS1_DB_EN = "0413020";	//DF
	public static final String GS1_DB_NTX_AI = "0413050";
	public static final String GS1_DB_TX_AI = "0413060";	//DF
	
	/* 1-dimensional code, EAN-UCC Composite settings */
	public static final String EAN_UCC_DF = "0414000";
	public static final String EAN_UCC_READ_DIS = "0414010";	//DF
	public static final String EAN_UCC_READ_EN = "0414020";
	public static final String EAN_UPC_READ_DIS = "0414030";	//DF
	public static final String EAN_UPC_READ_EN = "0414040";
	
	/* 1-dimensional code, Code11 settings */
	public static final String CODE11_DF = "0415000";
	public static final String CODE11_DIS = "0415010";	//DF
	public static final String CODE11_EN = "0415020";
	public static final String CODE11_MIN_LENS = "0415030";	//4
	public static final String CODE11_MAX_LENS = "0415040";	//48
	public static final String CODE11_NCK = "0415050";
	public static final String CODE11_CK1 = "0415060";	//DF(MOD11)
	public static final String CODE11_CK2_SA = "0415070";	//(MOD11/MOD11)
	public static final String CODE11_CK2_DFT = "0415080";	//(MOD11/MOD9)
	public static final String CODE11_CKC_SA = "0415090";	//(MOD11,MOD11, LENS<=10 -> SC, LENS>10 -> DC)
	public static final String CODE11_CKC_DFT = "0415100";	//(MOD11,MOD9, LENS<=10 -> SC, LENS>10 -> DC)
	public static final String CODE11_NTX_LRC = "0415110";
	public static final String CODE11_TX_LRC = "0415120";	//DF
	
	/* 1-dimensional code, ISBN settings */
	public static final String ISBN_DF = "0416000";
	public static final String ISBN_DIS = "0416010";	//DF
	public static final String ISBN_EN = "0416020";
	public static final String ISBN_TX_13NUM = "0416030";	//DF
	public static final String ISBN_TX_10NUM = "0416040";
	
	/* 1-dimensional code, Industrial 25 settings */
	public static final String IDSL25_DF = "0417000";
	public static final String IDSL25_DIS = "0417010";	//DF
	public static final String IDSL25_EN = "0417020";
	public static final String IDSL25_MIN_LENS = "0417030";	//6
	public static final String IDSL25_MAX_LENS = "0417040";	//48
	public static final String IDSL25_NCK = "0417050";	//DF
	public static final String IDSL25_CK_NTX_LRC = "0417060";
	public static final String IDSL25_CK_TX_LRC = "0417070";
	
	/* 1-dimensional code, Standard25 settings */
	public static final String STD25_DF = "0418000";
	public static final String STD25_DIS = "0418010";	//DF
	public static final String STD25_EN = "0418020";
	public static final String STD25_MIN_LENS = "0418030";	//6
	public static final String STD25_MAX_LENS = "0418040";	//48
	public static final String STD25_NCK = "0418050";	//DF
	public static final String STD25_CK_NTX = "0418060";
	public static final String STD25_CK_TX = "0418070";
	
	/* 1-dimensional code, Plessey settings */
	public static final String PLESSEY_DF = "0419000";
	public static final String PLESSEY_DIS = "0419010";	//DF
	public static final String PLESSEY_EN = "0419020";
	public static final String PLESSEY_MIN_LENS = "0419030";	//4
	public static final String PLESSEY_MAX_LENS = "0419040";	//48
	public static final String PLESSEY_NCK = "0419050";
	public static final String PLESSEY_CK_NTX = "0419060";
	public static final String PLESSEY_CK_TX = "0419070";	//DF
	
	/* 1-dimensional code, MSI-Plessey settings */
	public static final String MSIP_DF = "0420000";
	public static final String MSIP_DIS = "0420010";	//DF
	public static final String MSIP_EN = "0420020";
	public static final String MSIP_MIN_LENS = "0420030";	//4
	public static final String MSIP_MAX_LENS = "0420040";	//48
	public static final String MSIP_NCK = "0420050";
	public static final String MSIP_CK1 = "0420060";	//DF(MOD10)
	public static final String MSIP_CK2_SA = "0420070";	//(MOD10/MOD10)
	public static final String MSIP_CK2_DFT = "0420080";	//(MOD10/MOD11)
	public static final String MSIP_NTX_LRC = "0420090";
	public static final String MSIP_TX_LRC = "0420100";	//DF
	
	/* 2-dimensional code, macro settings */
	public static final String MC_CLS_CBUF = "0500000";
	/** Each block of data transmitted directly after reading */
	public static final String MC_MODE1 = "0500010";	//DF
	/** Cache data transmitted in sequence (no more than 64kbyte) */
	public static final String MC_MODE2 = "0500020";
	/** 
	 * Connections to send after reading all of the data block, transmitting data 
	 * as mode 2 when the cached data quantity more than 64kbyte.
	 */
	public static final String MC_MODE3 = "0500030";
	
	/* 2-dimensional code, PDF417 settings */
	public static final String PDF417_DF = "0501000";
	public static final String PDF417_DIS = "0501010";
	public static final String PDF417_EN = "0501020";	//DF
	public static final String PDF417_MIN_LENS = "0501030";	//1
	public static final String PDF417_MAX_LENs = "0501040";	//2710
	
	/* 2-dimensional code, QR code settings */
	public static final String QRC_DF = "0502000";
	public static final String QRC_DIS = "0502010";
	public static final String QRC_EN = "0502020";	//DF
	public static final String QRC_MIN_LENS = "0502030";	//1
	public static final String QRC_MAX_LENS = "0502040";	//7089
	public static final String QRC_RS = "0502070";	//DF
	public static final String QRC_RD = "0502080";
	public static final String QRC_DS = "0502090";
	
	/* 2-dimensional code, Aztec settings */
	public static final String AZTEC_DF = "0503000";
	public static final String AZTEC_DIS = "0503010";	//DF
	public static final String AZTEC_EN = "0503020";
	public static final String AZTEC_MIN_LENS = "0503030";	//1
	public static final String AZTEC_MAX_LENS = "0503040";	//3832
	
	/* 2-dimensional code, Data matrix settings */
	public static final String DATA_MTX_DF = "0504000";
	public static final String DATA_MTX_DIS = "0504010";
	public static final String DATA_MTX_EN = "0504020";	//DF
	public static final String DATA_MTX_MIN_LENS = "0504030";	//1
	public static final String DATA_MTX_MAX_LENS = "0504040";	//3116
	public static final String DATA_MTX_RS = "0504070";	//DF
	public static final String DATA_MTX_RD = "0504080";
	public static final String DATA_MTX_DS = "0504090";
	
	/* 2-dimensional code, Han letter code settings */
	public static final String HLC_DF = "0508000";
	public static final String HLC_DIS = "0508010";	//DF
	public static final String HLC_EN = "0508020";
	public static final String HLC_MIN_LENS = "0508030";	//1
	public static final String HLC_MAX_LENS = "0508040";	//7827
	
	/* 2-dimensional code, Maxicode settings */
	public static final String MAXIC_DF = "0505000";
	public static final String MAXIC_DIS = "0505010";	//DF
	public static final String MAXIC_EN = "0505020";
	public static final String MAXIC_MIN_LENS = "0505030";	//1
	public static final String MAXIC_MAX_LENS = "0505040";	//150
	
	/* 2-dimensional code, Custom settings */
	public static final String CUSTOM_DF = "0510000";
	public static final String CUSTOM_DIS = "0510010";	//DF
	public static final String CUSTOM_EN = "0510020";
	
	/* OCR settings */
	public static final String OCR_B_DF = "0600000";	//DF
	public static final String OCR_B_DIS = "0600010";	//DF
	public static final String OCR_B_EN = "0600020";
	
}
