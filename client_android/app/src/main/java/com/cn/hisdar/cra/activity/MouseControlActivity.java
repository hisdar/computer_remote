package com.cn.hisdar.cra.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;

import com.cn.hisdar.cra.HServerEvent;
import com.cn.hisdar.cra.HServerEventListener;
import com.cn.hisdar.cra.R;
import com.cn.hisdar.cra.common.Global;

import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.net.Socket;

import cn.hisdar.cr.communication.data.AbstractData;
import cn.hisdar.cr.communication.data.KeyEventData;
import cn.hisdar.cr.communication.data.MotionEventData;
import cn.hisdar.cr.communication.data.MouseButtonData;
import cn.hisdar.cr.communication.data.RequestData;
import cn.hisdar.cr.communication.data.ResponseData;
import cn.hisdar.cr.communication.data.ScreenPictureData;
import cn.hisdar.cr.communication.data.ScreenSizeData;
import cn.hisdar.cr.communication.handler.HMotionEvent;
import cn.hisdar.cr.communication.handler.RequestEventListener;
import cn.hisdar.cr.communication.handler.RequestHandler;
import cn.hisdar.cr.communication.handler.ResponseHandler;
import cn.hisdar.cr.communication.handler.ResponseListener;
import cn.hisdar.cr.communication.handler.ScreenPictureHandler;
import cn.hisdar.cr.communication.handler.ScreenPictureListener;
import cn.hisdar.cr.communication.socket.SocketDisconnectListener;
import cn.hisdar.cr.communication.socket.SocketIOManager;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 * 
 * @see
 */
@SuppressLint("ClickableViewAccessibility")
public class MouseControlActivity extends Activity
	implements OnTouchListener, HServerEventListener,
		RequestEventListener, ScreenPictureListener, SocketDisconnectListener,
		ResponseListener {

	private static final String TAG = "CR-MouseControlActivity";

	private static final int SHOW_RESPONSE_MESSAGE 	= 0x10001;
	private static final int SERVER_EVENT 			= 0x10002;
	private static final int SCREEN_PICTURE			= 0x10003;

	//private TextView touchPanelView = null;
	private MessagedImageView touchPanelView = null;
	
	private Button leftButton;
	private Button rightButton;
	
	private MessageHandler messageHandler;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_mouse_control);
		//touchPanelView = (TextView)findViewById(R.id.touch_panel_text_view);
		touchPanelView = (MessagedImageView)findViewById(R.id.touch_panel_text_view);
		touchPanelView.setOnTouchListener(this);

		leftButton = (Button)findViewById(R.id.left_button);
		rightButton = (Button)findViewById(R.id.right_button);
		
		leftButton.setOnTouchListener(this);
		rightButton.setOnTouchListener(this);
		
		messageHandler = new MessageHandler();

		RequestHandler.getInstance().addRequestEventListener(this);
		ScreenPictureHandler.getInstance().addScreenPictureListener(this);
		SocketIOManager.getInstance().addSocketDisconnectListener(this);
		ResponseHandler.getInstance().addResponseListener(this);

		sendScreenSize();
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


	@Override
	public void serverEvent(HServerEvent serverEvent) {
		Message message = new Message();
		message.arg1 = SERVER_EVENT;
		message.obj = serverEvent;
		messageHandler.sendMessage(message);
	}

	@Override
	public void requestEvent(RequestData requestData) {
		if (requestData.getRequestDataType() == AbstractData.DATA_TYPE_SCREEN_SIZE) {
			sendScreenSize();
		}
	}

	@Override
	public void screenPictureEvent(ScreenPictureData screenPictureData) {
		//Log.d(TAG, "screenPictureEvent");
		Message message = new Message();
		message.arg1 = SCREEN_PICTURE;
		message.obj = screenPictureData.encode();
		messageHandler.sendMessage(message);
	}

	@Override
	public void socketDisconnectEvent(Socket socket) {
		finish();
	}

	@Override
	public void responseEvent(ResponseData responseData) {
		Message message = new Message();
		message.arg1 = SHOW_RESPONSE_MESSAGE;
		message.obj = responseData;
		messageHandler.sendMessage(message);
	}

	private class MessageHandler extends Handler {

		@Override
		public void handleMessage(Message msg) {
			
			switch (msg.arg1) {
				case SHOW_RESPONSE_MESSAGE:
					showResponseData((ResponseData)msg.obj);
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

	private void buttonTouchEvent(MotionEvent arg1, int buttonId) {

		MouseButtonData mouseButtonData = new MouseButtonData();
		mouseButtonData.setButtioID(buttonId);

		if (arg1.getAction() == MotionEvent.ACTION_DOWN) {
			mouseButtonData.setActionCode(HMotionEvent.ACTION_DOWN);
		} else if (arg1.getAction() == MotionEvent.ACTION_UP) {
			mouseButtonData.setActionCode(HMotionEvent.ACTION_UP);
		}

		SocketIOManager.getInstance().sendDataToClient(mouseButtonData, null);
	}

	private void keyEvent(int keyCode, int keyValue) {

		KeyEventData keyEventData = new KeyEventData(keyCode, keyValue);
		SocketIOManager.getInstance().sendDataToClient(keyEventData, null);
	}

	private void touchPanelViewTouchEventHandler(MotionEvent e) {

		HMotionEvent hMotionEvent = new HMotionEvent();
		hMotionEvent.setAction(e.getAction());
		hMotionEvent.setActionIndex(e.getActionIndex());
		hMotionEvent.setButtonState(e.getButtonState());
		hMotionEvent.setDeviceId(e.getDeviceId());
		hMotionEvent.setDownTime(e.getDownTime());
		hMotionEvent.setEdgeFlags(e.getEdgeFlags());
		hMotionEvent.setEventTime(e.getEventTime());
		hMotionEvent.setFlags(e.getFlags());
		hMotionEvent.setHistorySize(e.getHistorySize());
		hMotionEvent.setMetaState(e.getMetaState());
		hMotionEvent.setSource(e.getSource());
		hMotionEvent.setPointerCount(e.getPointerCount());

		for (int i = 0; i < e.getPointerCount(); i++) {
			hMotionEvent.setToolType(i, e.getToolType(i));
			hMotionEvent.setX(i, e.getX(i));
			hMotionEvent.setY(i, e.getY(i));
		}

		//Log.i(TAG, "send motion event data");
		MotionEventData motionEventData = new MotionEventData();
		motionEventData.setMotionEvent(hMotionEvent);
		SocketIOManager.getInstance().sendDataToClient(motionEventData, null);
	}

	private void serverEventHandler(HServerEvent serverEvent) {
		if (serverEvent.event.equals(Global.SERVER_EVENT_EXIT)) {
			finish();
		}
	}

	private void screenPictureShow(byte[] data) {
		ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(data);
		InputStreamReader inputStreamReader = new InputStreamReader(byteArrayInputStream);

		Bitmap screenPicture = BitmapFactory.decodeByteArray(data, 0, data.length);
        //Log.i(CRAActivity.TAG, "[MouseActivity]refresh a picture");
		touchPanelView.setImageBitmap(screenPicture);
	}

	private boolean sendScreenSize() {
		int height = touchPanelView.getHeight();
		int width = touchPanelView.getWidth();

		//ScreenSizeHandler screenSizeData = new ScreenSizeHandler(width, height);
		//return CRClient.getInstance().sendData(screenSizeData);
		//Log.d(TAG, "send screen size data:" + width + ", " + height);
		ScreenSizeData screenSizeData = new ScreenSizeData(width, height);
		SocketIOManager.getInstance().sendDataToClient(screenSizeData, null);

		return true;
	}

	private long maxDelay = 0;
	private void showResponseData(ResponseData responseData) {
		long writeTime = responseData.getWriteTime();
		long currateTime = System.currentTimeMillis();

		long timeDelay = currateTime - writeTime;
		maxDelay = maxDelay < timeDelay ? timeDelay : maxDelay;

		String message = "delay-time:" + timeDelay + "; max-delay:" + maxDelay + "; data-type:" + responseData.getResponseDataType();
		touchPanelView.setMessage(message);
	}
}
