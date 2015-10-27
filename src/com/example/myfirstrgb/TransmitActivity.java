package com.example.myfirstrgb;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.net.wifi.ScanResult;
import android.os.Bundle;
import android.os.Handler;
import android.util.AttributeSet;
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
import com.example.myfirstrgb.WifiUtils;

public class TransmitActivity extends Activity {
	private ArrayAdapter<String> adapter_s;
	private List<ScanResult> getScanResults;
	public static  List<String>	scanssidstring=null;
	public static String WifiSSID;
    private String pswd;
    private String WifiSSID2;
    private String pswd2;
	private Spinner s2 =null;
	private Spinner s3 =null;
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
		Button btn1 = (Button)findViewById(R.id.button1);
		Button btn2 = (Button)findViewById(R.id.button2);
		WifiSSID=adapter_s.getItem(0);
		WifiSSID2=adapter_s.getItem(0);
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
				pswd = pw.getText().toString();
				pswd2 = ppw.getText().toString();
				//dialog.dismiss();
				connectLAN();
				new Handler().postDelayed(new Runnable(){    
				    public void run() {    
				    	MainActivity.localWifiUtils.SERVERIP="192.168.4.1";
				    	MainActivity.localWifiUtils.connecttoserver();
				    	 new Handler().postDelayed(new Runnable(){    
							    public void run() { 
									final String sendpswddata;
									sendpswddata="SSID"+WifiSSID2+","+pswd2+"\r\n";
									MainActivity.localWifiUtils.msg6=sendpswddata.getBytes();
									MainActivity.localWifiUtils.sendMessage(MainActivity.localWifiUtils.msg6); 
									new Handler().postDelayed(new Runnable(){    
									    public void run() { 
									    		connectStaLAN();
										    	new Handler().postDelayed(new Runnable(){    
												    public void run() { 
												    	MainActivity.connect_mode=20;
												    	MainActivity.search_ip_flag=10;
												    	TransmitActivity.this.finish();
												    }
										    	}, 4000);									    
										    }
									}, 2000);
							    }
				    	 }, 4000);
				    }    
				 }, 4000);
		 	}
		 });
	}
	private void connectStaLAN()
	{
		//MainActivity.localWifiUtils.WifiOpen();
		//MainActivity.localWifiUtils.getConfiguration();
		int netId= MainActivity.localWifiUtils.IsConfiguration("\""+WifiSSID2+"\"");
		MainActivity.localWifiUtils.ConnectWifi(netId);
	}
	private void connectLAN()
  	{
		MainActivity.localWifiUtils.WifiOpen();
		MainActivity.localWifiUtils.getConfiguration();
  		int netId= MainActivity.localWifiUtils.IsConfiguration("\""+WifiSSID+"\"");
  		if(pswd != null)
  		{
	  		if(netId != -1){
	  				MainActivity.localWifiUtils.deleteWifiLock(netId);
				}
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
}