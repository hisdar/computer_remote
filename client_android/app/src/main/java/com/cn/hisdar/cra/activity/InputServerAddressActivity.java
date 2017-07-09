package com.cn.hisdar.cra.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.cn.hisdar.cra.R;
import com.cn.hisdar.cra.lib.configuration.ConfigItem;
import com.cn.hisdar.cra.lib.configuration.HConfig;
import com.cn.hisdar.cra.server.ServerCommunication;

@SuppressLint("NewApi") public class InputServerAddressActivity extends Activity
implements OnClickListener {

	private EditText ipaddressEditText;
	private EditText portEditText;
	
	private Button loginButton;
	private Button returnButton;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.input_server_address);
		ipaddressEditText = (EditText)findViewById(R.id.ipaddress_edit_text);
		portEditText = (EditText)findViewById(R.id.port_edit_text);
		
		loginButton = (Button)findViewById(R.id.login_button);
		returnButton = (Button)findViewById(R.id.return_button);
		
		loadServerInfo();
		
		loginButton.setOnClickListener(this);
		returnButton.setOnClickListener(this);
	}

	@Override
	public void onClick(View arg0) {
		if (arg0.getId() == loginButton.getId()) {
			String ipAddress = ipaddressEditText.getText().toString();
			String port = portEditText.getText().toString();
			connectToServerButtonActionHandler(getApplicationContext(), ipAddress, port);
		} else if (arg0.getId() == returnButton.getId()) {
			finish();
		}
	}
	
	private void connectToServerButtonActionHandler(Context context, String ipAddress, String portString) {

		Log.i(CRAActivity.TAG, "Input ip address is:" + ipAddress);
		Log.i(CRAActivity.TAG, "Input ip port is:" + portString);
		
		if (!isIpAddress(ipAddress)) {
			Log.e(CRAActivity.TAG, "Input ip address is not a ip address");
			return;
		}
		
		int port = 5299;
		try {
			port = Integer.parseInt(portString.trim());
		} catch (NumberFormatException e) {
			Log.e(CRAActivity.TAG, "port is not visiable");
			return;
		}
		
		// try to connect to server
		ServerCommunication serverCommunication = ServerCommunication.getInstance();
		serverCommunication.disconnect(getMainLooper().getThread(), ipAddress, port);
		boolean ret = serverCommunication.connectToServer(getMainLooper().getThread(), ipAddress, port);
		if (!ret) {
			showMessage("���ӷ�����ʧ��", "��ʾ");
			return;
		}
		
		saveServerInfo();
		
		sendScreenSizeToServer();
		
		Log.i(CRAActivity.TAG, "jump to mouse control activity");
		// jump to control activity
        Intent intent = new Intent(context, MouseControlActivity.class);
        //����Intent��ͨ��ֵ�ķ�ʽ  
        intent.putExtra("skip", "����MainActivity��������ֵ��");  
        //��תActivity  
        startActivityForResult(intent, CRAActivity.MOUSE_CONTROL_ACTIVITY_CODE);  
	}

	private boolean saveServerInfo() {
		String configFileFolder = getFilesDir().getAbsolutePath();
		String configFilePath = configFileFolder + "/server_config.xml";
		HConfig serverConfig = HConfig.getInstance(configFilePath, true);
		
		String ipaddress = ipaddressEditText.getText().toString().trim();
		String port = portEditText.getText().toString().trim();
		
		serverConfig.setConfigItem(new ConfigItem("server-ip-address", ipaddress));
		serverConfig.setConfigItem(new ConfigItem("server-port", port));
		
		return true;
	}
	
	private boolean loadServerInfo() {
		String configFileFolder = getFilesDir().getAbsolutePath();
		String configFilePath = configFileFolder + "/server_config.xml";
		HConfig serverConfig = HConfig.getInstance(configFilePath);
		
		String ipaddress = serverConfig.getConfigValue("server-ip-address", "");
		String port = serverConfig.getConfigValue("server-port", "");
		
		ipaddressEditText.setText(ipaddress);
		portEditText.setText(port);
		
		return true;
	}
	
	private void sendScreenSizeToServer() {
		WindowManager windowManager = getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        Point screenSize = new Point();
        display.getSize(screenSize);
        
        ServerCommunication serverCommunication = ServerCommunication.getInstance();
        serverCommunication.sendScreenSize(getMainLooper().getThread(), screenSize.x, screenSize.y);
	}
	
	private boolean isIpAddress(String ipAddress) {
		String[] ipAddressArray = ipAddress.split("[.]");
		
		if (ipAddressArray.length == 0 || ipAddressArray.length % 4 != 0) {
			return false;
		}
		
		for (int i = 0; i < ipAddressArray.length; i++) {
			try {
				Integer.parseInt(ipAddressArray[i]);
			} catch (Exception e) {
				return false;
			}
		}
		
		return true;
	}
	
	private void showMessage(String message, String title) {
		new AlertDialog.Builder(this)
		.setTitle(title)
		.setMessage(message)
		.show();
	}
}
