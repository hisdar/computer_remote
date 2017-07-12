package com.cn.hisdar.cra.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.ImageView;

import com.cn.hisdar.cra.EventDispatcher;
import com.cn.hisdar.cra.HResponseData;
import com.cn.hisdar.cra.HResponseDataListener;
import com.cn.hisdar.cra.HServerEvent;
import com.cn.hisdar.cra.HServerEventListener;
import com.cn.hisdar.cra.R;
import com.cn.hisdar.cra.common.Global;
import com.cn.hisdar.cra.commnunication.AbstractDataType;
import com.cn.hisdar.cra.commnunication.ServerCommunication;
import com.cn.hisdar.cra.server.ds.DataServer;
import com.cn.hisdar.cra.server.ds.CommunicationEventListener;

import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 * 
 * @see
 */
@SuppressLint("ClickableViewAccessibility") public class MouseControlActivity extends Activity
	implements OnTouchListener, HResponseDataListener, HServerEventListener, CommunicationEventListener {
	
	private static final int SHOW_RESPONSE_MESSAGE 	= 0x10001;
	private static final int SERVER_EVENT 			= 0x10002;
	private static final int SCREEN_PICTURE			= 0x10003;

	//private TextView touchPanelView = null;
	private ImageView touchPanelView = null;
	
	private Button leftButton;
	private Button rightButton;
	
	private MessageHandler messageHandler;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_mouse_control);
		//touchPanelView = (TextView)findViewById(R.id.touch_panel_text_view);
		touchPanelView = (ImageView)findViewById(R.id.touch_panel_text_view);
		touchPanelView.setOnTouchListener(this);

		leftButton = (Button)findViewById(R.id.left_button);
		rightButton = (Button)findViewById(R.id.right_button);
		
		leftButton.setOnTouchListener(this);
		rightButton.setOnTouchListener(this);
		
		messageHandler = new MessageHandler();
		EventDispatcher eventDispatcher = EventDispatcher.getInstance();
		eventDispatcher.addHResponseDataListener(this);
		eventDispatcher.addHServerEventListener(this);

        DataServer dataServer = DataServer.getInstance();
        dataServer.addCommunicationEventListener(this, AbstractDataType.DATA_TYPE_SCREEN_PICTURE);
	}
	
	@Override
	public boolean onTouch(View arg0, MotionEvent arg1) {
		
		if (arg0.getId() == touchPanelView.getId()) {
			touchPanelViewTouchEventHandler(arg1);
		} else if (arg0.getId() == leftButton.getId()) {
			buttonTouchEvent(arg1, Global.BUTTON1);
		} else if (arg0.getId() == rightButton.getId()) {
			buttonTouchEvent(arg1, Global.BUTTON3);
		}
		
		return true;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN || keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
			keyEvent(keyCode, KeyEvent.ACTION_DOWN);
			return true;
		} else {
			return super.onKeyDown(keyCode, event);
		}
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN || keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
			keyEvent(keyCode, KeyEvent.ACTION_UP);
			return true;
		} else {
			return super.onKeyUp(keyCode, event);
		}
	}

	private void buttonTouchEvent(MotionEvent arg1, int buttonId) {
		if (arg1.getAction() == MotionEvent.ACTION_DOWN) {
			ServerCommunication.getInstance()
				.sendMouseButtonEvent(getMainLooper().getThread(), buttonId, MotionEvent.ACTION_DOWN);
		} else if (arg1.getAction() == MotionEvent.ACTION_UP) {
			ServerCommunication.getInstance()
			.sendMouseButtonEvent(getMainLooper().getThread(), buttonId, MotionEvent.ACTION_UP);
		}
	}
	
	private void keyEvent(int keyCode, int keyValue) {
		ServerCommunication.getInstance()
		.sendKeyButtonEvent(getMainLooper().getThread(), keyCode, keyValue);
	}

	private void touchPanelViewTouchEventHandler(MotionEvent e) {
		
		ServerCommunication.getInstance().sendTouchEvent(getMainLooper().getThread(), e);
	}

	@Override
	public void responseDataEvent(HResponseData responseData) {
		Message message = new Message();
		message.arg1 = SHOW_RESPONSE_MESSAGE;
		message.obj = responseData;
		messageHandler.sendMessage(message);
	}
	
	@Override
	public void serverEvent(HServerEvent serverEvent) {
		Message message = new Message();
		message.arg1 = SERVER_EVENT;
		message.obj = serverEvent;
		messageHandler.sendMessage(message);
	}

	@Override
	public void screenPictureEvent(byte[] data) {
		Message message = new Message();
		message.arg1 = SCREEN_PICTURE;
		message.obj = data;
		messageHandler.sendMessage(message);
	}

	private class MessageHandler extends Handler {

		@Override
		public void handleMessage(Message msg) {
			
			switch (msg.arg1) {
				case SHOW_RESPONSE_MESSAGE:
					showResponseData((HResponseData)msg.obj);
					break;
				case SERVER_EVENT:
					serverEventHandler((HServerEvent)msg.obj);
					break;
				case SCREEN_PICTURE:
					screenPictureShow((byte[])msg.obj);
					break;
				default:
					break;
			}
			
			super.handleMessage(msg);
		}

	}

	private void showResponseData(HResponseData responseData) {
		long delayTime = responseData.readTime - responseData.sendTime;
		String responseDataString = "延时：" + delayTime + "ms";
		//touchPanelView.setText(responseDataString);
	}
	
	private void serverEventHandler(HServerEvent serverEvent) {
		if (serverEvent.event.equals(Global.SERVER_EVENT_EXIT)) {
			finish();
		}
	}

	private void screenPictureShow(byte[] data) {
		//Bitmap screenPicture = BitmapFactory.decodeByteArray(screenPiture.bytesData.getData(), 0, screenPiture.bytesData.getDataSize());
		//BitmapDrawable drawable = new BitmapDrawable(getApplicationContext().getResources(), screenPicture);
		//touchPanelView.
		//touchPanelView.setImageDrawable(drawable);
		//touchPanelView.setImageBitmap(screenPicture);
		ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(data);
		InputStreamReader inputStreamReader = new InputStreamReader(byteArrayInputStream);

		Bitmap screenPicture = BitmapFactory.decodeByteArray(data, 0, data.length);
		//ImageReader imageReader = ImageReader.newInstance();
		//imageReader.
        Log.i(CRAActivity.TAG, "[MouseActivity]refresh a picture");
		touchPanelView.setImageBitmap(screenPicture);
	}
}
