package cn.hisdar.computerremote.common;


public class Global {

	// for server information
	public static final String SERVER_INFO_SERVER_NAME = "server-name";
	
	// for configuration
	public static final String APPLICATION_CONFIG_FILE 				= "./config/application-config.xml";
	public static final String AUTION_START_CONFIG_NAME 			= "autoStart";
	public static final String START_SERVER_WHEN_APPLICATION_START 	= "startServerWhenApplicationStart";
	public static final String IS_APPLICATION_STARTED_CONFIG_NAME 	= "isApplicationStarted";
	public static final String APPLICATION_LAST_IP_ADDRESS 			= "lastIpAddress";
	public static final String APPLICATION_LAST_PORT 				= "port";
	
	public static final String START_UP_PATH = "AppData/Roaming/Microsoft/Windows/Start Menu/Programs/Startup";
	public static final String AUTO_START_COMMAND = "auto-start";
	public static final String APPLICATION_NAME = "ComputerRemote";

	// for communication
	public static final String DATA_BEGIN_FLAG = "<computer_remote_data_begin>";
	public static final String DATA_END_FLAG   = "<computer_remote_data_end>";

	// for xml
	public static final String XML_FILE_HEAD = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n";
	
	public static final String XML_NODE_COMPUTER_REMOTE			= "computerRemote";
	public static final String XML_NODE_SERVER_DATA				= "serverData";
	public static final String XML_NODE_SERVER_EVENT			= "serverEvent";
	public static final String XML_NODE_DATA_TYPE 				= "dataType";
	public static final String XML_NODE_MOTION_EVENT 			= "motionEvent";
	public static final String XML_NODE_MOUSE_BUTTON_EVENT 		= "mouseEvent";
	public static final String XML_NODE_KEY_BUTTON_EVENT 		= "keyEvent";
	public static final String XML_NODE_TIME_DATA				= "timeData";
	public static final String XML_NODE_RESPONSE_DATA 			= "responseData";
	
	public static final String DATA_TYPE_SCREEN_SIZE			= "screenSize";
	public static final String DATA_TYPE_RESPONSE_DATA 			= "responseData";
	public static final String DATA_TYPE_SERVER_EVENT 			= "serverEvent";
	public static final String DATA_TYPE_SCREEN_PICTURE			= "screenPicture";
	
	// for motion event
	public static final String XML_NODE_ACTION 					= "action";
	public static final String XML_NODE_ACTION_BUTTON 			= "actionButton";
	public static final String XML_NODE_BUTTON_STATE 			= "buttonState";
	public static final String XML_NODE_META_STATE				= "metaState";
	public static final String XML_NODE_FLAGS 					= "flags";
	public static final String XML_NODE_EDGE_FLAGS 				= "edgeFlags";
	public static final String XML_NODE_POINTER_COUNT 			= "pointerCount";
	public static final String XML_NODE_HISTORY_SIZE 			= "historySize";
	public static final String XML_NODE_EVENT_TIME 				= "eventTime";
	public static final String XML_NODE_DOWN_TIME 				= "downTime";
	public static final String XML_NODE_DEVICE_ID 				= "deviceId";
	public static final String XML_NODE_SOURCE 					= "source";
	public static final String XML_NODE_POINTERS 				= "pointers";
	public static final String XML_POINTER_X 					= "x";
	public static final String XML_POINTER_Y 					= "y";
	public static final String XML_POINTER_TOOL_TYPE 			= "toolType";
	public static final String XML_POINTER_ID 					= "id";
	public static final String XML_NODE_POINTER 				= "pointer";
	
	// for mouse event
	public static final String XML_NODE_BUTTON_ID 				= "buttonId";
	public static final String XML_NODE_BUTTON_VALUE			= "value";
	
	public static final int BUTTON1 = 1;
	public static final int BUTTON2 = 2;
	public static final int BUTTON3 = 3;
	
	public static final int KEYCODE_VOLUME_UP   = 24;
	public static final int KEYCODE_VOLUME_DOWN = 25;
	
	// for screen size
	public static final String SCREEN_SIZE_WIDTH = "width";
	public static final String SCREEN_SIZE_HEIGHT = "height";

	// for server event
	public static final Object SERVER_EVENT_EXIT = "SERVER_EVENT_EXIT";

	/// client send to server
	public static final String CONTROL_DATA_LABEL_2 = "" +
			"<computerRemote>\n" +
			"%s" +
			"%s" +
			"</computerRemote>\n";
	
	public static final String DATA_TYPE_FORMAT = "" +
			"	<dataType>%s</dataType>\n";
	
	public static final String TIME_DATA_FORMAT = "" +
			"	<timeData>%s</timeData>\n";
	
	public static final String SCREEN_SIZE_XML_FORMAT = "" +
			"	<screenSize>\n" +
			"		<width>%s</width>\n" +
			"		<height>%s</height>\n" +
			"	</screenSize>\n";

	/// server send to client
	public static final String CONTROL_DATA_LABEL_1 = "" +
			"<computerRemote>\n" +
			"%s" +
			"</computerRemote>\n";
	
	public static final String RESPONSE_DATA_FORMAT = "" +
			"	<responseData>\n" +
			"	%s" +
			"	</responseData>\n";

	public static final String SERVER_EXIT_DATA_FORMAT = "" +
			"	<serverEvent>" +
			"	%s" +
			"	</serverEvent>\n";

	public static final String BYTE_DATA_HEAD_FORMAT = "" + 
			"	<byteDataHead>\n" +
			"		<dataSize>%d</dataSize>\n" +
			"	</byteDataHead>\n";
			
}
