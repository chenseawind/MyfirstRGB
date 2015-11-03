package com.example.myfirstrgb;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.myfirstrgb.Utils;
import com.example.myfirstrgb.WifiUtils;
import com.example.myfirstrgb.ContactManager;
import com.example.myfirstrgb.ColorPickerActivity.MiusThread;
public class TransmitActivity extends Activity 
{
	private static ContactManager contactManager;
	private ArrayAdapter<String> adapter_s;
	private List<ScanResult> getScanResults;
	public static  List<String>	scanssidstring=null;
	public static String WifiSSID;
    private String pswd;
    private String WifiSSID2;
    private String pswd2;
	private Spinner s2 =null;
	private Spinner s3 =null;
	private boolean LISTEN = true;
	public static int    RxPort= 11119;//
	public static int    TxPort= 12119;//
	private static final int BUF_SIZE = 1024;
	public Button btn1;
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.transmitconnect);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.titlebtn);
		s2 = (Spinner) findViewById(R.id.spinner2);
		s3 = (Spinner) findViewById(R.id.spinner3);
		scanlan();
		adapter_s = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,scanssidstring);
		adapter_s.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		s2.setAdapter(adapter_s);
		s3.setAdapter(adapter_s);
		final EditText pw = (EditText)findViewById(R.id.editText2);
		final EditText ppw = (EditText)findViewById(R.id.editText4);
		btn1 = (Button)findViewById(R.id.button1);
		Button btn2 = (Button)findViewById(R.id.button2);
		btn1.setEnabled(true);
		WifiSSID=adapter_s.getItem(0);
		WifiSSID2=adapter_s.getItem(0);
		MainActivity.localWifiUtils.stopListener();
		Timer waitforsetoktimer = new Timer(true);
		waitforsetoktimer.schedule(waitforsetok,100,100);
		MainActivity.connectserver_flag=0;
		s2.setOnItemSelectedListener(new Spinner.OnItemSelectedListener(){    
            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {      
            	WifiSSID=adapter_s.getItem(arg2);
            }     
            public void onNothingSelected(AdapterView<?> arg0) {    
                // TODO Auto-generated method stub      
            }  
        });  
		s3.setOnItemSelectedListener(new Spinner.OnItemSelectedListener(){    
            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {      
            	WifiSSID2=adapter_s.getItem(arg2);
            }     
            public void onNothingSelected(AdapterView<?> arg0) {    
                // TODO Auto-generated method stub      
            }  
        });  
		btn2.setOnClickListener(new  android.view.View.OnClickListener() {// 设置按钮的点击事件
			@Override
			 public void onClick(View v) {
				scanlan();
				adapter_s.clear();
				adapter_s.addAll(scanssidstring);
				adapter_s.notifyDataSetChanged();
		 	}
		 });
		btn1.setOnClickListener(new  android.view.View.OnClickListener() {// 设置按钮的点击事件
			@Override
			 public void onClick(View v) {
				connhandler.sendEmptyMessage(2);
				pswd = pw.getText().toString();
				pswd2 = ppw.getText().toString();
				connectLAN();
				new Handler().postDelayed(new Runnable(){    
				    public void run() {  
				    	if(MainActivity.localWifiUtils.isWifiConnected(TransmitActivity.this))
				    	{
				    		final String sendpswddata;
				    		sendpswddata="SSID"+WifiSSID2+","+pswd2+"\r\n";
				    		MainActivity.localWifiUtils.sendMessage(sendpswddata,TxPort);
					   	 	connectStaLAN();
					   	 	MainActivity.localWifiUtils.startListener();
					   	 	//connhandler.sendEmptyMessage(3);
					   	 	Utils.toast(getApplicationContext(), "已配置网络，正在搜索控制器");
				    	}
				    	else
				    	{
				    		new Handler().postDelayed(new Runnable(){    
							    public void run() {  
							    	if(MainActivity.localWifiUtils.isWifiConnected(TransmitActivity.this))
							    	{
							    		final String sendpswddata;
										sendpswddata="SSID"+WifiSSID2+","+pswd2+"\r\n";
										MainActivity.localWifiUtils.sendMessage(sendpswddata,TxPort);
								   	 	connectStaLAN();
								   	 	MainActivity.localWifiUtils.startListener();
								   	 	//connhandler.sendEmptyMessage(3);
								   	 	Utils.toast(getApplicationContext(), "已配置网络，正在搜索控制器");
							    	}
							    	else
							    	{
							    		connhandler.sendEmptyMessage(3);
							    		Utils.toast(getApplicationContext(), "设置不成功，请检查ＷＩＦＩ密码及设置连接是否正确。再重新设置");
							    	}
							    }    
							 }, 4000);
				    	}
				    }    
				 }, 4000);
		 	}
		 });
	}
	private void connectStaLAN()
	{
		//MainActivity.localWifiUtils.WifiOpen();
		MainActivity.localWifiUtils.getConfiguration();
  		int netId= MainActivity.localWifiUtils.IsConfiguration("\""+WifiSSID2+"\"");
	  		if(netId != -1){
	  				MainActivity.localWifiUtils.ConnectWifi(netId);
				}
	  		else if(pswd2 != null)
	  		{
		  		MainActivity.localWifiUtils.AddWifiConfig(WifiSSID2, pswd2);
		  		netId= MainActivity.localWifiUtils.IsConfiguration("\""+WifiSSID2+"\"");
		  		MainActivity.localWifiUtils.ConnectWifi(netId);
	  		}
	}
	private void connectLAN()
  	{
		//MainActivity.localWifiUtils.WifiOpen();
		MainActivity.localWifiUtils.getConfiguration();
  		int netId= MainActivity.localWifiUtils.IsConfiguration("\""+WifiSSID+"\"");
	  		if(netId != -1){
	  				MainActivity.localWifiUtils.ConnectWifi(netId);
				}
	  		else if(pswd != null)
	  			{
	  				MainActivity.localWifiUtils.AddWifiConfig(WifiSSID, pswd);
	  				netId= MainActivity.localWifiUtils.IsConfiguration("\""+WifiSSID+"\"");
	  				MainActivity.localWifiUtils.ConnectWifi(netId);
	  			}
  	}
	private void scanlan()
  	{
		MainActivity.localWifiUtils.WifiOpen();
		MainActivity.localWifiUtils.WifiStartScan();
		getScanResults=MainActivity.localWifiUtils.getScanResults();
  		scanssidstring=MainActivity.localWifiUtils.scanResultToString(getScanResults);
  	}
	TimerTask waitforsetok=new TimerTask(){  
   	 public void run() { 
   		 if(MainActivity.connectserver_flag==20)
   		 {
   			connhandler.sendEmptyMessage(1);
   		 }
   	}
	};
   	Handler connhandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			super.handleMessage(msg); 
			switch (msg.what) {
			case 1:
				waitforsetok.cancel();
				Utils.toast(getApplicationContext(), "已设置成功");
				finish();
				break;
			case 2:
				btn1.setEnabled(false);
				break;
			case 3:
				btn1.setEnabled(true);
				break;
			default:
				break;
			}
		}
		};		  
@Override
   public boolean onKeyDown(int keyCode,KeyEvent event)
   {
	   if(keyCode==KeyEvent.KEYCODE_BACK && event.getRepeatCount()==0)
	   {
		   waitforsetok.cancel();
		   finish();
		   return true;
	   }
	   return super.onKeyDown(keyCode, event);
   }
}