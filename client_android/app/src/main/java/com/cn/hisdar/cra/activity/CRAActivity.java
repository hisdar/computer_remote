package com.cn.hisdar.cra.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cn.hisdar.cra.R;
import com.cn.hisdar.cra.lib.configuration.HConfig;
import cn.hisdar.cr.communication.ServerCommunication;
import cn.hisdar.cr.communication.data.AbstractData;
import cn.hisdar.cr.communication.data.RequestData;
import cn.hisdar.cr.communication.data.ServerInfoData;
import cn.hisdar.cr.communication.socket.SocketIOManager;

import com.cn.hisdar.cra.server.ServerSearcher;
import com.cn.hisdar.cra.server.ServerSearcheerEventListener;
import com.cn.hisdar.cra.server.ServerSearcherState;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

import java.net.Socket;
import java.util.ArrayList;

public class CRAActivity extends AppCompatActivity
        implements View.OnClickListener, ServerSearcheerEventListener {

    public static final String TAG = "Hisdar-CR";
    public static final int MOUSE_CONTROL_ACTIVITY_CODE = 5299;
    public static final int KEYBOARD_CONTROL_ACTIVITY_CODE = 5300;
    private static final CharSequence MESSAGE_SEARCHING_SERVER = "正在搜索服务器......";
    private static final CharSequence MESSAGE_ACTION = "请点击下面的按钮操作";
    private static final CharSequence MESSAGE_SEARCH_FINISHED = "搜索结束";
    private static final CharSequence TEXT_STOP_SEARCH = "停止搜索";
    private static final CharSequence TEXT_START_SEARCH = "自动搜索";
    private static final int CRA_MESSAGE_SERVER_FOUND = 0x00000001;
    private static final int CRA_MESSAGE_SERVER_MESSAGE = 0x00000002;

    private Button autoSearchButton;
    private Button inputAddressButton;

    private TextView messageView;

    private LinearLayout serverListView;

    private MessageHandler messageHandler;

    private ArrayList<TextView> serverTextViews;
    private ArrayList<ServerInfoData> serverInfoDatas;
    private ServerSearcher serverSearcher;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cra);

        serverListView = (LinearLayout) findViewById(R.id.server_list_view);
        messageView = (TextView) findViewById(R.id.message_view);

        autoSearchButton = (Button) findViewById(R.id.auto_serch_server_button);
        inputAddressButton = (Button) findViewById(R.id.input_server_button);

        autoSearchButton.setOnClickListener(this);
        inputAddressButton.setOnClickListener(this);

        //ipAddressEditText = (EditText)findViewById(R.id.server_ipaddress_edit_text);
        //portEditText = (EditText)findViewById(R.id.server_port_edit_text);

        loadServerInfo();

        serverTextViews = new ArrayList<TextView>();
        serverInfoDatas = new ArrayList<ServerInfoData>();
        messageHandler = new MessageHandler();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    @Override
    public void onClick(View arg0) {
        if (arg0.getId() == autoSearchButton.getId()) {

            if (autoSearchButton.getText().equals(TEXT_STOP_SEARCH)) {

                serverSearcher.stopSearch();
                messageView.setText(MESSAGE_ACTION);
                autoSearchButton.setText(TEXT_START_SEARCH);

            } else {
                messageView.setText(MESSAGE_SEARCHING_SERVER);
                autoSearchButton.setText(TEXT_STOP_SEARCH);

                clearServerList();

                Log.i(CRAActivity.TAG, "start search server");
                serverSearcher = new ServerSearcher();
                serverSearcher.addServerSearcherListener(this);
                serverSearcher.startSearch(getBaseContext());
            }

        } else if (arg0.getId() == inputAddressButton.getId()) {
            Intent intent = new Intent(CRAActivity.this, InputServerAddressActivity.class);
            intent.putExtra("skip", "MainActivity");
            //��תActivity
            startActivity(intent);
            //startActivityForResult(intent, MOUSE_CONTROL_ACTIVITY_CODE);
        } else {
            serverViewEvent(arg0);
        }
    }

    private void serverViewEvent(View arg0) {
        boolean isServerView = false;
        for (int i = 0; i < serverTextViews.size(); i++) {
            if (arg0.getId() == serverTextViews.get(i).getId()) {
                isServerView = true;
            }
        }

        if (isServerView) {
            for (int i = 0; i < serverTextViews.size(); i++) {
                if (serverInfoDatas.get(i).getId() == arg0.getId()) {
                    serverSearcher.stopSearch();
                    autoSearchButton.setText(TEXT_START_SEARCH);
                    messageView.setText(MESSAGE_ACTION);
                    connectToServerButtonActionHandler(CRAActivity.this, serverInfoDatas.get(i).getIpAddress(), serverInfoDatas.get(i).getPort());
                    break;
                }
            }
        }
    }

    private void connectToServerButtonActionHandler(Context context, String ipAddress, String portString) {

        Log.i(TAG, "Input ip address is:" + ipAddress);
        Log.i(TAG, "Input ip port is:" + portString);

        if (!isIpAddress(ipAddress)) {
            Log.e(TAG, "Input ip address is not a ip address");
            return;
        }

        int port = 5299;
        try {
            port = Integer.parseInt(portString.trim());
        } catch (NumberFormatException e) {
            Log.e(TAG, "port is not visiable");
            return;
        }

        // try to connect to server
        ServerCommunication sc = ServerCommunication.getInstance();
        sc.disconnect(getMainLooper().getThread(), ipAddress, port);
        boolean ret = sc.connectToCmdServer(getMainLooper().getThread(), ipAddress, port, 5300);
        if (!ret) {
            showMessage("提示", "服务器连接失败");
            return;
        }

        saveServerInfo();

        sendScreenSizeToServer();

        Log.i(TAG, "jump to mouse control activity");
        // jump to control activity
        Intent intent = new Intent(context, MouseControlActivity.class);
        //����Intent��ͨ��ֵ�ķ�ʽ
        intent.putExtra("skip", "����MainActivity��������ֵ��");
        //��תActivity
        startActivityForResult(intent, MOUSE_CONTROL_ACTIVITY_CODE);
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

    public boolean isWifiConnected(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiNetworkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (wifiNetworkInfo.isConnected()) {
            return true;
        }

        return false;
    }

    private void showMessage(String message, String title) {
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .show();
    }

    private boolean saveServerInfo() {
        String configFileFolder = getFilesDir().getAbsolutePath();
        String configFilePath = configFileFolder + "/server_config.xml";
        HConfig serverConfig = HConfig.getInstance(configFilePath, true);

        //String ipaddress = ipAddressEditText.getText().toString().trim();
        //String port = portEditText.getText().toString().trim();

        //serverConfig.setConfigItem(new ConfigItem("server-ip-address", ipaddress));
        //serverConfig.setConfigItem(new ConfigItem("server-port", port));

        return true;
    }

    private boolean loadServerInfo() {
        String configFileFolder = getFilesDir().getAbsolutePath();
        String configFilePath = configFileFolder + "/server_config.xml";
        HConfig serverConfig = HConfig.getInstance(configFilePath);

        String ipaddress = serverConfig.getConfigValue("server-ip-address", "");
        String port = serverConfig.getConfigValue("server-port", "");

        //ipAddressEditText.setText(ipaddress);
        //portEditText.setText(port);

        return true;
    }

    public void newServerFoundEvent(ServerInfoData serverInfo) {
        Message message = new Message();
        message.arg1 = CRA_MESSAGE_SERVER_FOUND;
        message.obj = serverInfo;

        messageHandler.sendMessage(message);
    }

    @Override
    public void socketConnectedEvent(Socket socket) {
        // request server information
        RequestData requestData = new RequestData();
        requestData.setRequestDataType(AbstractData.DATA_TYPE_SERVER_INFO);
        SocketIOManager.getInstance().sendDataToClient(requestData, socket);
    }

    @Override
    public void serverSercherStateEvent(ServerSearcherState msg) {
        Message message = new Message();
        message.arg1 = CRA_MESSAGE_SERVER_MESSAGE;
        message.obj = msg;

        messageHandler.sendMessage(message);
    }

    private void setServerViewParam(TextView textView) {
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(messageView.getLayoutParams());
        layoutParams.height = 120;
        layoutParams.bottomMargin = 20;
        layoutParams.leftMargin = 10;
        layoutParams.rightMargin = 10;

        textView.setLayoutParams(layoutParams);
        textView.setGravity(Gravity.CENTER_VERTICAL);
        textView.setBackgroundColor(Color.rgb(0xEE, 0xEE, 0xEE));
    }

    private void clearServerList() {

        int length = serverTextViews.size();
        for (int i = length; i > 0; i--) {
            serverListView.removeView(serverTextViews.get(i - 1));
            serverTextViews.remove(i - 1);
        }
    }

    private void handleServerFoundMessage(ServerInfoData serverInfo) {

        serverInfoDatas.add(serverInfo);

        TextView textView = new TextView(getBaseContext());
        textView.setId(serverInfo.getId());
        textView.setText(serverInfo.getServerName() + " - " + serverInfo.getIpAddress());
        textView.setTextColor(0xFF0000AA);
        textView.setOnClickListener(this);

        serverTextViews.add(textView);
        serverListView.addView(textView);
        setServerViewParam(textView);
    }

    private void handleServerMessage(ServerSearcherState msg) {
        switch (msg.message) {
            case ServerSearcherState.MESSAGE_WIFI_NOT_CONNECTED:
                showMessage("WIFI 没有连接", "提示");
                break;
            case ServerSearcherState.SEARCH_FINISHED:
                messageView.setText(MESSAGE_SEARCH_FINISHED);
                autoSearchButton.setText(TEXT_START_SEARCH);
                break;
            default:
                break;
        }
    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("CRA Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }

    private class MessageHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {

            switch (msg.arg1) {
                case CRA_MESSAGE_SERVER_FOUND:

                    handleServerFoundMessage((ServerInfoData) msg.obj);
                    break;
                case CRA_MESSAGE_SERVER_MESSAGE:
                    handleServerMessage((ServerSearcherState) msg.obj);
                    break;
                default:
                    break;
            }

            super.handleMessage(msg);
        }
    }
}
