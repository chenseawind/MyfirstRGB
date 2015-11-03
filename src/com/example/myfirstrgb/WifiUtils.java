package com.example.myfirstrgb;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import android.content.Context;
import android.database.Cursor;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.WifiLock;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

public class WifiUtils {
	public static byte[] msg1 = {(byte)0XFD,(byte)0X08,(byte)0X12,(byte)0X55,(byte)0XEA,(byte)0X55,(byte)0X99,(byte)0X00};
  	//发送控制数据
  	public static byte[] msg2 = {(byte)0XFD,(byte)0X06,(byte)0X01,(byte)0X01,(byte)0XF5,(byte)0X00,(byte)0XF5,(byte)0X00};
  	//控制收到数据
  	public static byte[] msg3 = {(byte)0XFD,(byte)0X06,(byte)0X01,(byte)0X01,(byte)0XE0,(byte)0X00,(byte)0XE0,(byte)0X00};
  	//对码收到的数据
  	public static byte[] msg4 = {(byte)0XFD,(byte)0X04,(byte)0XFF,(byte)0X06,(byte)0XFD,(byte)0Xaa,(byte)0Xaa,(byte)0X01,(byte)0X84,(byte)0X04,(byte)0XFF,(byte)0X06};
  	public static byte[] msg5 = {(byte)0XFD,(byte)0X07,(byte)0X33,(byte)0X33,(byte)0X33,'\r','\n'};
  	public static byte[] msg6 = {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1};
  	public static byte[] msg7 = {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1};
  	public static byte[] msg8 = {(byte)0XFD,(byte)0X08,0,0,0,0,'\r','\n'};
  	public static String SERVERIP = "192.168.1.102";//
    public static int    SERVERPORT= 7799;//
    static Socket _socket = null;
    private Thread mThreadClient = null;
    private static PrintWriter _printWriter = null;
    
    public static boolean LISTEN = true;
    private static final int RxPort=11119;
	private static final int TxPort=12119;
	private static final int BUF_SIZE = 1024;
	public static WifiManager localWifiManager;//提供Wifi管理的各种主要API，主要包含wifi的扫描、建立连接、配置信息等
	//private List<ScanResult> wifiScanList;//ScanResult用来描述已经检测出的接入点，包括接入的地址、名称、身份认证、频率、信号强度等
	private List<WifiConfiguration> wifiConfigList;//WIFIConfiguration描述WIFI的链接信息，包括SSID、SSID隐藏、password等的设置
	public static  WifiInfo wifiConnectedInfo;//已经建立好网络链接的信息
	private WifiLock wifiLock;//手机锁屏后，阻止WIFI也进入睡眠状态及WIFI的关闭
	
	public WifiUtils( Context context){
		localWifiManager = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
	}
	private static InetAddress getBroadcastIp() {
		// Function to return the broadcast address, based on the IP address of the device
		try {
			WifiInfo wifiInfo = localWifiManager.getConnectionInfo();
			int ipAddress = wifiInfo.getIpAddress();
			String addressString = toBroadcastIp(ipAddress);
			InetAddress broadcastAddress = InetAddress.getByName(addressString);
			return broadcastAddress;
		}
		catch(UnknownHostException e) {
			
			return null;
		}
		
	}
	private static String toBroadcastIp(int ip) {
		// Returns converts an IP address in int format to a formatted string
		return (ip & 0xFF) + "." +
				((ip >> 8) & 0xFF) + "." +
				((ip >> 16) & 0xFF) + "." +
				"255";
	}
	public static void sendMessage(final byte message[])
	{

		Thread replyThread = new Thread(new Runnable() {
			@Override
			public void run() {
				
				try {
					InetAddress address = InetAddress.getByName(SERVERIP);
					DatagramSocket socket = new DatagramSocket();
					DatagramPacket packet = new DatagramPacket(message, message.length, address, TxPort);
					socket.send(packet);
					socket.disconnect();
					socket.close();
				}
				catch(UnknownHostException e) {
				}
				catch(SocketException e) {
				}
				catch(IOException e) {
				}
			}
		});
		replyThread.start();

	}
	public static void sendMessage(final String message, final int port) {
		// Creates a thread used for sending notifications
		Thread replyThread = new Thread(new Runnable() {
			
			@Override
			public void run() {
				
				try {
					InetAddress address =getBroadcastIp();
					byte[] messagedata = message.getBytes();
					DatagramSocket socket = new DatagramSocket();
					DatagramPacket packet = new DatagramPacket(messagedata, messagedata.length, address, port);
					socket.send(packet);
					socket.disconnect();
					socket.close();
				}
				catch(UnknownHostException e) {
				}
				catch(SocketException e) {
				}
				catch(IOException e) {
				}
			}
		});
		replyThread.start();
	}
	public static void startListener() {
		// Creates the listener thread
		LISTEN = true;
		Thread listener = new Thread(new Runnable() {
			
			@Override
			public void run() {
				
				try {
					// Set up the socket and packet to receive
					DatagramSocket socket = new DatagramSocket(RxPort);
					socket.setSoTimeout(1000);
					byte[] buffer = new byte[BUF_SIZE];
					DatagramPacket packet = new DatagramPacket(buffer, BUF_SIZE);
					while(LISTEN) {
						if(MainActivity.connectserver_flag!=20)sendMessage("GETIP\r\n",TxPort);
						try {
							socket.receive(packet);
							int datalength=packet.getLength();
							if(datalength>3)
							{
								final int sublength=datalength-3;
								byte rxdatabuf0=(byte)(buffer[0]+buffer[1]+buffer[2]+buffer[3]+0x55);
								if((buffer[0]=='"')&&(buffer[sublength]=='"'))
								{
									String data = new String(buffer, 0, packet.getLength());
									SERVERIP=data.substring(1,sublength);
									MainActivity.connectserver_flag=20;
									MainActivity.Time_over_done=1;
								}
								else if((buffer[0]==(byte)0xfa)&&(buffer[4]==rxdatabuf0)&&(MainActivity.CommandType == 1))
								{
									MainActivity.Time_over_done=3;
									MainActivity.CommandType=0;
									msg4[6]=buffer[1];
				    				msg4[7]=buffer[2];
				    				msg4[8]=0;
				    				MainActivity.prod_type=buffer[3];
								}
							}
						}
						catch(Exception e) {}
					}
					//Log.i(LOG_TAG, "Call Listener ending");
					socket.disconnect();
					socket.close();
				}
				catch(SocketException e) {
					
					//Log.e(LOG_TAG, "SocketException in listener " + e);
				}
			}
		});
		listener.start();
	}

	public static void stopListener() {
		// Ends the listener thread
		LISTEN = false;
	}
	private Thread _thread = new Thread(){
		  public void run(){ 
		  	while(true){
		  		try{ 
		  			InputStream in = _socket.getInputStream();
		  			byte[] buffer = new byte[in.available()];
		  			in.read(buffer);
		  			String mm="";
		  			for (int i = 0; i < buffer.length; i++) { 
		  				String hex = Integer.toHexString(buffer[i] & 0xFF);  
		  				if (hex.length() == 1) {   
		  					mm += '0' + hex+" "; 
		  				}else{
		  					mm+=hex+" ";
		  				}
		  			}
		  			String msg=mm;
		  			if(msg.length() != 0){
		  				System.out.println("in: " + msg);
		  				Message message = new Message(); 
		  				Bundle bundle = new Bundle(); 
//		  				bundle.putString("msg", msg); 
		  				bundle.putByteArray("msg", buffer);
		  				message.setData(bundle);
		  				_handler.sendMessage(message); 
		  			}
		  		}
		  		catch (Exception e){
		  		}
			    	}
			    	}
			   	};
			   	Handler _handler = new Handler(){
				    public void handleMessage(Message msg){  
				    super.handleMessage(msg); 
				    int bt;
				    int i = 0;
				    int msglength;
				    msglength=msg.getData().getByteArray("msg").length;
//				    showstate+=1;
//				    Utils.toast(MainActivity.ma,Integer.toString(msg.getData().getByteArray("msg")[6]));			    
//				    ColorPickerActivity.delaytime.removeCallbacks(ColorPickerActivity.runnable);
				    try{
				    	switch(MainActivity.CommandType)
				    	{
				    	case 1:
				    		for(i=0;i<msglength;i++)
				    		{
				    			msg4[i] = msg.getData().getByteArray("msg")[i];
				    		}
				    		if((msg4[3]==msg1[3])&&(msg4[4]==msg1[4])&&(msg4[5]==msg1[5])&&(msg4[1]==0x0b))
				    		{}
				    		if(msg4[0]==(byte)0xfa)
				    		{
				    			msg4[5]=(byte)(msg4[0]+msg4[1]+msg4[2]+msg4[3]+0x55);
				    			if(msg4[4]==msg4[5])
				    			{
				    				MainActivity.CommandType=0;
				    				msg4[6]=msg4[1];
				    				msg4[7]=msg4[2];
				    				msg4[8]=0;
				    				MainActivity.search_ip_flag=9;
				    			}
				    		}
				    		break;
				    	case 0:
				    		for(i=0;i<msglength;i++)
				    		{
				    			msg3[i] = msg.getData().getByteArray("msg")[i];
				    		}
				    		if((msg3[1]==8)&&(msg3[0]==(byte)0xf5)&&(msg3[2]==0x44)&&(msg3[3]==0x44)&&(msg3[4]==0x44)&&(msg3[6]=='\r')&&(msg3[7]=='\n'))
				    		{
				    			MainActivity.connectserver_flag=1;
				    		}
				    		if(msg3[0]==(byte)0xfa)
				    		{}
				    		if((msg3[1]==8)&&(msg3[0]==(byte)0xf5))
				    		{}
				    	
				    		break;
				    	case 3:
				    		for(i=0;i<msglength;i++)
				    		{
				    			msg7[i] = msg.getData().getByteArray("msg")[i];
				    		}
				    		if((msg7[0]=='"')&&(msg7[msglength-1]=='"'))
				    		{
				    			MainActivity.search_ip_flag=1;
				    			String str_buf=new String(msg7);
				    			SERVERIP=str_buf.substring(1, msglength-1);
				    		}
				    		break;
				    	case 4:
				    		break;
				    	case 5:
				    		for(i=0;i<msglength;i++)
				    		{
				    			msg3[i] = msg.getData().getByteArray("msg")[i];
				    		}
				    		break;
				    	}
//						Utils.toast(MainActivity.this,Integer.toString(CommandType) +msg.getData().getString("msg"),Toast.LENGTH_LONG);
//				    	Toast.makeText(MainActivity.this,hexStr2Str(msg.getData().getString("msg")), Toast.LENGTH_LONG);
				    }  
				    
				    catch (Exception e){ 
				    	//Log.d("TCPTest",e.getMessage());
//				    	st.setText("½ÓÊÕ³öÏÖ´íÎó£º"+e.getMessage());
				    	//Utils.toast(MainActivity.this,e.getMessage(),Toast.LENGTH_LONG);
//				    	Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_LONG);
				    }
				    
				    }
				   };
	Runnable newsocket = new Runnable(){
	     @Override
	     public void run() {
	    	 	Looper.prepare();
		    	 try {
			         _socket = new Socket(SERVERIP, SERVERPORT); 
			         BufferedReader _bufferedReader = new BufferedReader(new InputStreamReader(_socket.getInputStream(), "GB2312"));
			         _printWriter    = new PrintWriter(_socket.getOutputStream(), true);
			         _thread.start();
		     }
		     catch (Exception e) {
	     }
	     }
	 };
	 public void disconnecttoserver()
	 {
		// _socket = new Socket(SERVERIP, SERVERPORT);
		// _socket.close();
		 mThreadClient.interrupt();
		 mThreadClient.stop();
	 }
	 public void connecttoserver()
		{
		 mThreadClient =new Thread(newsocket);
		 mThreadClient.start();
		//newsocket.run();
		}
    //检查WIFI状态
	//开启WIFI
	public void WifiOpen(){
		if(!localWifiManager.isWifiEnabled()){
			localWifiManager.setWifiEnabled(true);
		}
	}
	
	//关闭WIFI
	public void WifiClose(){
		if(!localWifiManager.isWifiEnabled()){
			localWifiManager.setWifiEnabled(false);
		}
	}
	
	//扫描wifi
	public void WifiStartScan(){
		localWifiManager.startScan();
	}
	
	//得到Scan结果
	public List<ScanResult> getScanResults(){
		return localWifiManager.getScanResults();//得到扫描结果
	}

	//Scan结果转为Sting
	public List<String> scanResultToString(List<ScanResult> list){
		List<String> strReturnList = new ArrayList<String>();
		for(int i = 0; i < list.size(); i++){
			ScanResult strScan = list.get(i);
			String str = strScan.toString();
			StringTokenizer token = new StringTokenizer(str, ",");
			if (token.hasMoreTokens()) {
			str=token.nextToken();
			str=str.substring(6);
			}
			
			boolean bool = strReturnList.add(str);
			if(!bool){
				Log.i("scanResultToSting","Addfail");
			}
		}
		return strReturnList;
	}
	
	//得到Wifi配置好的信息
	public void getConfiguration(){
		wifiConfigList = localWifiManager.getConfiguredNetworks();//得到配置好的网络信息
		for(int i =0;i<wifiConfigList.size();i++){
			Log.i("getConfiguration",wifiConfigList.get(i).SSID);
			Log.i("getConfiguration",String.valueOf(wifiConfigList.get(i).networkId));
		}
	}
	//判定指定WIFI是否已经配置好,依据WIFI的地址BSSID,返回NetId
	public int IsConfiguration(String SSID){
		if(wifiConfigList.size()>0)
		{
			Log.i("IsConfiguration",String.valueOf(wifiConfigList.size()));
			for(int i = 0; i < wifiConfigList.size(); i++){
				Log.i(wifiConfigList.get(i).SSID,String.valueOf( wifiConfigList.get(i).networkId));
				if(wifiConfigList.get(i).SSID.equals(SSID)){//地址相同
					return wifiConfigList.get(i).networkId;
				}
			}
		}
		return -1;
	}

	//添加指定WIFI的配置信息,原列表不存在此SSID
	public void AddWifiConfig(String ssid,String pwd){
		WifiConfiguration wifiCong = new WifiConfiguration();
		wifiCong.SSID = "\""+ssid+"\"";//\"转义字符，代表"
		wifiCong.preSharedKey = "\""+pwd+"\"";//WPA-PSK密码
		wifiCong.hiddenSSID = false;
		wifiCong.status = WifiConfiguration.Status.ENABLED;
		localWifiManager.addNetwork(wifiCong);//将配置好的特定WIFI密码信息添加,添加完成后默认是不激活状态，成功返回ID，否则为-1
}
	
	//连接指定Id的WIFI
	public boolean ConnectWifi(int wifiId){
		for(int i = 0; i < wifiConfigList.size(); i++){
			WifiConfiguration wifi = wifiConfigList.get(i);
			if(wifi.networkId == wifiId){
				while(!(localWifiManager.enableNetwork(wifiId, true))){//激活该Id，建立连接
					Log.i("ConnectWifi",String.valueOf(wifiConfigList.get(wifiId).status));//status:0--已经连接，1--不可连接，2--可以连接
				}
				return true;
			}
		}
		return false;
	}
	//删除一个WIFILock
	public void deleteWifiLock(int netId)
	{
		localWifiManager.removeNetwork(netId);
		localWifiManager.saveConfiguration();
	}
	//创建一个WIFILock
	public void createWifiLock(String lockName){
		wifiLock = localWifiManager.createWifiLock(lockName);
	}
	
	//锁定wifilock
	public void acquireWifiLock(){
		wifiLock.acquire();
	}
	
	//解锁WIFI
	public void releaseWifiLock(){
		if(wifiLock.isHeld()){//判定是否锁定
			wifiLock.release();
		}
	}
	
	//得到建立连接的信息
	public void getConnectedInfo(){
		wifiConnectedInfo = localWifiManager.getConnectionInfo();
	}
	//得到连接的MAC地址
	public String getConnectedMacAddr(){
		return (wifiConnectedInfo == null)? "NULL":wifiConnectedInfo.getMacAddress();
	}
	public static boolean isWifiConnected(Context context)
    {
        ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiNetworkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if(wifiNetworkInfo.isConnected())
        {
            return true ;
        }
     
        return false ;
    }
	//得到连接的名称SSID
	public String getConnectedSSID(){
		return (wifiConnectedInfo == null)? "NULL":wifiConnectedInfo.getSSID();
	}
	
	//得到连接的IP地址
	public int getConnectedIPAddr(){
		return (wifiConnectedInfo == null)? 0:wifiConnectedInfo.getIpAddress();
	}
	
	//得到连接的ID
	public int getConnectedID(){
		return (wifiConnectedInfo == null)? 0:wifiConnectedInfo.getNetworkId();
	}
}
