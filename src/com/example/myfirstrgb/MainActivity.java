package com.example.myfirstrgb;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.PrintStream; 
import java.lang.ref.WeakReference;
import java.net.DatagramPacket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

 




import com.example.myfirstrgb.MainActivity.MyOnClickListener;
import com.example.myfirstrgb.MainActivity.RestaurantHolder;
import com.example.myfirstrgb.ColorPickerActivity;
import com.example.myfirstrgb.MtitlePopupWindow;
import com.example.myfirstrgb.MtitlePopupWindow.OnPopupWindowClickListener;
import com.example.myfirstrgb.Restaurant;
import com.example.myfirstrgb.server.BackService;
import com.example.myfirstrgb.server.IBackService;
import com.example.myfirstrgb.MainActivity;
import com.example.myfirstrgb.DBUtils;
import com.example.myfirstrgb.Module;
import com.example.myfirstrgb.Utils;
import com.example.myfirstrgb.R;
import com.example.smartlinklib.SmartLinkManipulator;
import com.example.smartlinklib.ModuleInfo;
import com.example.smartlinklib.SmartLinkManipulator;
import com.example.smartlinklib.SmartLinkManipulator.ConnectCallBack;
import com.example.myfirstrgb.WifiUtils;

import android.content.ActivityNotFoundException;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager; 
import android.content.pm.ResolveInfo;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager.WifiLock;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;


public class MainActivity extends Activity {
	List<Restaurant> model=new ArrayList<Restaurant>();
	ListView listView;
	Button senddatabutton;
	Button settowifibutton;
	SmartLinkManipulator sm;
	RestaurantAdapter adapter=null;
	Restaurant current=null;
	Restaurant currentbuf=null;

	
	public static WifiUtils localWifiUtils=null;
	MtitlePopupWindow mtitlePopupWindow;
	MtitlePopupWindow mPopDelDes;
	ImageButton setbtn;
	ImageButton wifistate;
	final String [] items_en = {"Add a light","Add a RgbController","set wifi"};		
	final String [] items_zh = { "添加一个开关","添加一个RGB控制器","设置wifi"};
	public String [] serverip_buf={"1","1","1","1","1","1","1","1","1","1","1","1","1","1","1","1","1","1","1","1","1"};
	public static String delete_buf;
	public static byte ipbuf_cout=0;
	
	public Cursor curs = null;
	public Cursor curs_ip = null;
	public Cursor curs_ipbuf=null;
	public Cursor curs_find=null;
	public static SQLiteDatabase db = null;
	public static String sqlstr;
	private PrintStream output;

	public static int wificonnect_flag=1;
	public static int connectserver_flag=0;
	public static int connectserver_cout=0;
	public static int connectserver_error=0;
	public static int senddelay_flag=0;
	public static int senddelay_cout=0;
	public static int connect_mode=0;
	public static byte connect_ip_ready;
	public static byte connect_ook_cout=0;
	public static byte connect_ok=0;
	public static byte connect_ok_continue=0;
	public static byte connect_flag=0;
	public static byte lightno_max=0;
	public static byte find_flag=0;
	public static byte light_state_find=0;
	public static byte save_cout=0;;
	public static byte addr1 = 0;
	public static byte addr2 = 0;
	public static byte addr3 = 1;
	public static byte lighttype = 1;
	public static byte lightno = 0;
	public static byte lightno_buf=0;
	public static byte lightstate=0;
	public static byte currlightno = 0;
	public static byte prod_type = 1;
	public static byte have_send_flag=0;
	public static byte search_ip_flag=0;
	public static int  search_ip_cout=0;
	public static int  search_ip_cout_high=0;
	public static int add_flag = 0;
	
	int i;
	private byte dbcout;
	private Thread mThreadClient = null;
	private Socket mSocketClient = null;
	
	private View pview;
	private Handler mHandler;
	private List<Module> mModules;
	private int currposition;
	public static int screenWidth;
	public static int screenHeigh;
	public static String Language = "zh";
	
	public static String namebuf;
	public static String wifistaicon;
    public static String SERVERIP = "192.168.1.102";//
    public static int    SERVERPORT= 7799;//
    public static String LOCALIP = null;
    public static String WifiSSID;
    private String pswd;
    private String WifiSSID2;
    private String pswd2;
    static Socket _socket = null;
    boolean isconncting = false;
    private String iswificonnecting;
    private AlertDialog dialog;
    private ArrayAdapter<String> adapter_s;
    private static PrintWriter _printWriter = null;
    public static int showstate =0;
    public static int CommandType = 0;
  	public static byte[] msg8 = {(byte)0XFD,(byte)0X08,0,0,0,0,'\r','\n'};
  	private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
            }
            super.handleMessage(msg);
        }
    };
    @Override
   	protected void onStart() {		
   		super.onStart();
   		//udpBroadcast.open();
   	}
   	
   	@Override
   	protected void onStop() {
   		super.onStop();
   		//udpBroadcast.close();
   	}
    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
			localWifiUtils = new WifiUtils(MainActivity.this);
			localWifiUtils.WifiOpen();

			
			Language = Locale.getDefault().getLanguage();
			//自定义标题。当需要自定义标题时必须指定。如：标题是一个按钮时
			requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);  
			//启动activity_main窗口
			setContentView(R.layout.activity_main);
			listView = (ListView) findViewById(R.id.list_wifitransmit);
			
			//初始化对象以获取DisplayMetrics成员
			DisplayMetrics dm = new DisplayMetrics();
			//获取屏幕信息，并把信息放入DisplayMetrics类中
			getWindowManager().getDefaultDisplay().getMetrics(dm); 
			//取出屏幕分辨率的width,height
			screenWidth = dm.widthPixels; 
			screenHeigh = dm.heightPixels;
			 //自定义标题栏
			getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.titlebtn);
			setbtn = (ImageButton)findViewById(R.id.setbtn);  
			wifistate = (ImageButton)findViewById(R.id.wifistate);
	        db = openOrCreateDatabase("RGBController.db", this.MODE_PRIVATE, null);  
			sqlstr =  "CREATE TABLE IF NOT EXISTS MainTable(_id INTEGER primary key autoincrement, " + // 主键
					"lighttype INTEGER, " + 	// 设备类型
					"lightno INTEGER, " + 		// 编号
					"lightname TEXT, " + 	// 名称
					"lightstate INTEGER,"+ 
					"addr1 INTEGER, " +
					"addr2 INTEGER, " +
					"addr3 INTEGER)" ;
			db.execSQL(sqlstr);
	  		currentbuf=new Restaurant();
			Timer searchiptimer = new Timer(true);
		  	searchiptimer.schedule(searchiptask,1000,20);
			//显示灯列表
		  		adapter=new RestaurantAdapter();
				listView.setAdapter(adapter);
				listView.setOnItemClickListener(onListClick);		
				//长按事件
				listView.setOnItemLongClickListener(new OnItemLongClickListener() {
					@Override
					public boolean onItemLongClick(AdapterView<?> parent, View view,int position, long id) {
						// TODO Auto-generated method stub
						currposition = position;
						current = model.get(position);
						lightno = (byte) current.getprod_id();				
						disppopmenu(view);
						return true;
					}			
				});
				mPopDelDes = new MtitlePopupWindow(this);
				if(Language.equals("zh"))
				{
					mPopDelDes.changeData(Arrays.asList("删除","修改名称"));
				}
				else
				{
					mPopDelDes.changeData(Arrays.asList("Delete","Change Desc"));
				}
				mPopDelDes.setOnPopupWindowClickListener(new OnPopupWindowClickListener() {
					
					@Override
					public void onPopupWindowItemClick(int position) {
						
						//ÄãÒª×öµÄÊÂ
						switch(position)
						{
							case 0:
								//删除操作
								sqlstr =  "Delete from MainTable Where lightno=" + lightno; 	// 状态
								db.execSQL(sqlstr);						
								current = model.get(currposition);
								adapter.remove(current);
//								listItem.remove(position);
								adapter.notifyDataSetChanged();
//								mtitlePopupWindow.delrow(Arrays.asList(items),position);
								break;
							case 1:
								//修改描述
								changelightdesc(pview);
								if(Language.equals("zh"))
								{
									dialog.setTitle("请输入新的名称:");
								}
								else
								{
									dialog.setTitle("Please input new name:");
								}
								dialog.show();
//								Utils.toast(MainActivity.this, "Description");
								break;
							default:
								break;
						}
					}
				});
			setbtn.setOnClickListener(new View.OnClickListener() {			
				@Override
				public void onClick(View arg0) {
	//				Utils.toast(getApplicationContext(), R.string.str_conntowifi);
	//				();
	//				popMenu.showAsDropDown(arg0);
					mtitlePopupWindow.showAsDropDown(arg0);
				}
			});
			mtitlePopupWindow = new MtitlePopupWindow(this);
			if(Language.equals("zh"))
			{
				mtitlePopupWindow.changeData(Arrays.asList(items_zh));
			}
			else
			{
				mtitlePopupWindow.changeData(Arrays.asList(items_en));
			}
			mtitlePopupWindow.setOnPopupWindowClickListener(new OnPopupWindowClickListener() {
				
				@Override
				public void onPopupWindowItemClick(int position) {
					//ÄãÒª×öµÄÊÂ
					switch(position)
					{
					case 0:
						//閰嶅
						setlightname();
						break;
					case 1:
						setlightname();
						prod_type=1;
						break;
					case 2:
						//璁剧疆WIFI
						setwifi();
						CommandType = 3;
						break;					
					default:
						break;
					}
				}
			});
			if(connect_ip_ready==1)
			{
				wifistate.setImageResource(R.drawable.wifi_on_48);	
			}
			else
			{
				wifistate.setImageResource(R.drawable.wifi_off_48);	
			}
			
				    	curs= db.rawQuery("Select * From MainTable", null);
				    	lightno_max=(byte)curs.getCount();
				    	if(lightno_max>0)
				    	{
				    		curs.moveToFirst();
				    		localWifiUtils.SERVERIP=curs.getString(3);
				    		connect_mode=curs.getInt(1);
				    		localWifiUtils.connecttoserver();
				    		lightno=(byte)curs.getInt(2);
				    		lightno=lightno_max;
				    		CommandType=3;
				    		curs.moveToNext();
				    		while(!curs.isAfterLast())
							{
				    			lightno=(byte)curs.getInt(2);
								addlist(curs.getString(3),(byte)curs.getInt(1),(byte)curs.getInt(2),curs.getInt(4)); 
								adapter.notifyDataSetChanged();
								curs.moveToNext();
							}
				    	}
				    	else
				    	{
				    		lightno=0;
				    		connect_mode=10;
				    		localWifiUtils.SERVERIP="192.168.4.1";
				    		sqlstr = "Insert Into MainTable(_id,lighttype,lightno,lightname,addr1,addr2,addr3,lightstate) values(null," + connect_mode + "," + 0 + ",'" + localWifiUtils.SERVERIP + "'," + 0 + "," + 0 + "," + 0 + "," + 0 +")";
	     					db.execSQL(sqlstr);
				    		lightno=1;
				    	//	search_ip_flag=2;
				    	}		
				    	if(connect_mode == 10)
				    	{
				    		search_ip_flag=2;
				    		if(localWifiUtils.isWifiConnected(MainActivity.this))
				    		{
				    			connhandler.sendEmptyMessage(4);	
				    		}
				    		else
				    		{
				    			connhandler.sendEmptyMessage(5);	
				    		}
				    		
				    	}
    }
    TimerTask searchiptask=new TimerTask(){  
    	 public void run() { 
    		 connectserver_cout++;
    		 if(connectserver_cout>1000)
    		 {
    			 connectserver_cout=0;
    			 if(connectserver_flag==0)
    			 {
    				 connectserver_error++;
    				 if(connectserver_error>2)
    				 {
    					 connectserver_error=0;
    					 if(connect_mode == 10)
    					 {
    						 localWifiUtils.SERVERIP="192.168.4.1";
    						 localWifiUtils.connecttoserver();
    					 }
    					 else
    					 {
	    					 search_ip_cout=0;
	    					 search_ip_flag=0;
    					 }
    				 }
    			 }
    			 localWifiUtils.sendMessage(localWifiUtils.msg5);
    			 connectserver_flag=0;
    		 }
    		 senddelay_cout++;
    		 if(senddelay_cout>2)
    			 {
    			 	senddelay_cout=0;
    			 	senddelay_flag=0;
    			 	if(!localWifiUtils.isWifiConnected(MainActivity.this))
    			 	{
    			 		wificonnect_flag=0;
    			 		connhandler.sendEmptyMessage(5);	
    			 	}
    			 	else if(localWifiUtils.isWifiConnected(MainActivity.this)&&(wificonnect_flag==0))
    			 	{
    			 		 if(connect_mode == 10)
    					 {
    						 localWifiUtils.SERVERIP="192.168.4.1";
    						 localWifiUtils.connecttoserver();
    						 connhandler.sendEmptyMessage(4);
    						 wificonnect_flag=1;
    					 }
    			 		 else
    			 		 {
	    			 		wificonnect_flag=1;
	    			 		search_ip_cout=0;
	    			 		search_ip_flag=0;
    			 		 }
    			 	}
    			 }
    		 switch(search_ip_flag)
    		 {
    		 	case 0:
    		 		if(search_ip_cout<230)
    	  			{
	    	  			search_ip_cout++;
	    	  			localWifiUtils.SERVERIP=intToIp(search_ip_cout);
	    	  			localWifiUtils.connecttoserver();
	    	  			CommandType=3;
    	  			}
    		 		else 
    		 		{
    		 			if(search_ip_cout<400)
    		 			{
    		 				search_ip_cout++;
    		 			}
    		 			else
    		 			{
    		 				search_ip_flag=2;
    		 			}
    		 		}
    		 		break;
    		 	case 1:
    		 		search_ip_cout=255;
	  				CommandType=0;	
	  				connect_ip_ready=1;
	  				connhandler.sendEmptyMessage(4);	
	  				search_ip_flag=2;
	  				lightno_buf=0;
	  				connhandler.sendEmptyMessage(10);	
	  				break;
    		 	case 2:
//    		 		CommandType=0;
    		 		break;
    		 	case  9:
    		 		connhandler.sendEmptyMessage(9);	
    		 		search_ip_flag=2;
    		 		break;
    		 	case 10:
    		 		connhandler.sendEmptyMessage(10);	
    		 		search_ip_flag=0;
    		 		search_ip_cout=0;
    		 		break;
    		 	case 11:
    		 		connhandler.sendEmptyMessage(10);	
    		 		search_ip_flag=2;
    		 		break;
    		 }
    	  }
    };
	private AdapterView.OnItemClickListener onListClick=new AdapterView.OnItemClickListener() {
		public void onItemClick(AdapterView<?> parent,View view, int position,long id) {
			current=model.get(position);
			CommandType = 0;
			lightno_buf = (byte) current.getprod_id();	
			Cursor curs= db.rawQuery("Select * From MainTable", null);
			if(curs.getCount()>0)
			{
				curs.moveToFirst();
				while(!curs.isAfterLast())
				{
					if((lightno_buf==(byte)curs.getInt(2)))
					{
						if((byte)curs.getInt(1)==1)
						{
							localWifiUtils.msg8[2]=0x01;
							localWifiUtils.msg8[3]=(byte)curs.getInt(5);
							localWifiUtils.msg8[4]=(byte)curs.getInt(6);
							Intent intent = new Intent(MainActivity.this, ColorPickerActivity.class);
							startActivity(intent);
						}
						else
						{
							if((byte)curs.getInt(4)==(byte)0xaa)
							{
								localWifiUtils.msg2[6]=0x55;
							}
							else
							{	
								localWifiUtils.msg2[6]=(byte)0xaa;	
							}
							localWifiUtils.msg2[1]=8;
							localWifiUtils.msg2[2]=(byte)curs.getInt(1);
							localWifiUtils.msg2[3]=(byte)curs.getInt(5);
							localWifiUtils.msg2[4]=(byte)curs.getInt(6);
							localWifiUtils.msg2[5]=(byte)curs.getInt(7);
						}
					}
					curs.moveToNext();
				}
			}
			localWifiUtils.sendMessage(localWifiUtils.msg2);	
			have_send_flag=1;
		
			
			
		}
			
	};
	private void changelightdesc(final View view)
	{
		 LayoutInflater factory = LayoutInflater.from(getApplicationContext());
		 // 引入一个外部布局
		View contview = factory.inflate(R.layout.changelightname_dialog, null);
		
//			 contview.setBackgroundColor(Color.BLACK);// 设置该外部布局的背景
		final EditText edit = (EditText) contview.findViewById(R.id.edit_dialog);// 找到该外部布局对应的EditText控件
		Button btOK = (Button) contview.findViewById(R.id.btOK_dialog);	
		 btOK.setOnClickListener(new OnClickListener() {// 设置按钮的点击事件

			@Override
			 public void onClick(View v) {
				TextView v1 = (TextView)view.findViewById(R.id.title);
				TextView v2 = (TextView)view.findViewById(R.id.prod_type);
				TextView v3 = (TextView)view.findViewById(R.id.prod_id);
				v1.setText(edit.getText().toString());
				//更新数据库信息
				sqlstr = "update MainTable Set lightname = '" + edit.getText().toString() + "' Where lighttype = " + v2.getText() + " and lightno = " + v3.getText();
				db.execSQL(sqlstr);
//				adapter.notifyDataSetChanged();
				dialog.dismiss();
		 	}
		 });
		 dialog = new AlertDialog.Builder(MainActivity.this).setView(contview)
		 .create();
}
	private void disppopmenu(final View view)
	{
		 LayoutInflater factory = LayoutInflater.from(getApplicationContext());
		 // 引入一个外部布局
//		View contview = factory.inflate(R.layout.titlebtn, null);
//			 contview.setBackgroundColor(Color.BLACK);// 设置该外部布局的背景
		 pview = view;
		 mPopDelDes.showAsDropDown(view);
//		 dialog = new AlertDialog.Builder(MainActivity.this).setView(contview)
//		 .create();
	}
  //系统打开时，根据数据库查询结果显示出灯的信息
  	private void addlist(String moduleName,byte blighttype,byte blightno,int bstate){
  		current=new Restaurant();
  		current.setName(moduleName);
  		current.setProd_type(blighttype);
  		current.setProd_id((int)blightno);
  		current.setstate(bstate);
  		i=i+1;
  		adapter.add(current);
  }
  	private void setlightname()
  	{

  		final Dialog dialog = new Dialog(this);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.setnamebutton);
		final EditText name = (EditText)dialog.getWindow().findViewById(R.id.editnameText);
		Button btn1 = (Button)dialog.getWindow().findViewById(R.id.okbutton);
		btn1.setOnClickListener(new  android.view.View.OnClickListener() {// 璁剧疆鎸夐挳鐨勭偣鍑讳簨浠�
			@Override
			 public void onClick(View v) {
				namebuf = name.getText().toString();
				CommandType = 1;
				have_send_flag=1;
				dialog.dismiss();
		 	}
		 });
		dialog.show(); 
  	}
  	private void setdirectmode()
  	{
  		search_ip_flag=2;
  		Intent intent = new Intent(MainActivity.this, DirectActivity.class);
		startActivity(intent);
  	}
  //判定指定WIFI是否已经配置好,依据WIFI的地址BSSID,返回NetId
  	private void settransmitconnectmode()
  	{
  		search_ip_flag=2;
  		Intent intent = new Intent(MainActivity.this, TransmitActivity.class);
		startActivity(intent);
  	}
  	private void setwifi()
	{
		final Dialog dialog = new Dialog(this);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.selectconnectmode);
		//final EditText pw = (EditText)dialog.getWindow().findViewById(R.id.editText1);
	//	scanlan();
		Button btn1 = (Button)dialog.getWindow().findViewById(R.id.button1);
		Button btn2 = (Button)dialog.getWindow().findViewById(R.id.button2);
		btn1.setOnClickListener(new  android.view.View.OnClickListener() {// 设置按钮的点击事件
			@Override
			 public void onClick(View v) {
				dialog.dismiss();
				setdirectmode();
		 	}
		 });
		btn2.setOnClickListener(new  android.view.View.OnClickListener() {// 设置按钮的点击事件
			@Override
			 public void onClick(View v) {
				dialog.dismiss();
				settransmitconnectmode();
		 	}
		 });
		dialog.show(); 
	}
  //add a light
  	public void addlist(String moduleName,int blighttype){
  		save_cout=0;
		curs = db.rawQuery("Select * From MainTable", null);
		if(curs.getCount()>1)
		{
			curs.moveToFirst();
			curs.moveToNext();
			while(!curs.isAfterLast())
			{
				if((localWifiUtils.msg4[6]==(byte)curs.getInt(5))&&(localWifiUtils.msg4[7]==(byte)curs.getInt(6))&&(localWifiUtils.msg4[8]==(byte)curs.getInt(7)))
				{
					save_cout++;
				}
				curs.moveToNext();
			
			}
//			Utils.toast(MainActivity.this,Integer.toString(curs.getCount()));
		}
		if(save_cout==0)
		{
			
			current=new Restaurant();
			current.setName(moduleName);
			current.setProd_type(blighttype);
			current.setProd_id(lightno);
			current.setstate(lightstate);
			adapter.add(current);
			sqlstr = "Insert Into MainTable(_id,lighttype,lightno,lightname,addr1,addr2,addr3,lightstate) values(null," + blighttype + "," + lightno + ",'" + moduleName + "'," + localWifiUtils.msg4[6] + "," + localWifiUtils.msg4[7] + "," + localWifiUtils.msg4[8] + "," + localWifiUtils.msg4[9] +")";
			db.execSQL(sqlstr);
			lightno++;
			
		}
		//addflag = 0;
}
	//light holder
	static class RestaurantHolder {
		private TextView name=null;
//		private TextView address=null;
		private TextView prod_id = null;
		private TextView prod_type = null;
		private ImageView icon=null;
		private ImageView btn = null;  
		@SuppressWarnings("unused")
		private View row=null;
		
		RestaurantHolder(View row) {
			this.row=row;
			
			name=(TextView)row.findViewById(R.id.title);
			prod_type=(TextView)row.findViewById(R.id.prod_type);
//			prod_id = (TextView)row.findViewById(R.id.prod_id);
			prod_id = (TextView)row.findViewById(R.id.prod_id);
			btn = (ImageView)row.findViewById(R.id.btn);
			icon=(ImageView)row.findViewById(R.id.icon);
		}
		
		//modify light property
		void populateFrom(Restaurant r) {
			name.setText(r.getName());
//			address.setText(r.getAddress());
			prod_id.setText(Integer.toString(r.getprod_id()));
			prod_type.setText(Integer.toString(r.getprod_type()));
			if(r.getstate()==(byte)0xaa)
			{
				btn.setBackgroundResource(R.drawable.switch_on);
			}
			else if(r.getstate()==(byte)0x55)
			{
				btn.setBackgroundResource(R.drawable.switch_off);
			}
//			icon.setImageResource(R.drawable.light_50);
//			btn.setBackgroundResource(R.drawable.switch_on);
//			btn.setImageResource(R.drawable.switch_on);
//			if (r.getType().equals("sit_down")) {
//				icon.setImageResource(R.drawable.ball_red);
//			}
//			else if (r.getType().equals("take_out")) {
//				icon.setImageResource(R.drawable.ball_yellow);
//			}
//			else {
//				icon.setImageResource(R.drawable.ball_green);
//			}
		}
	}
	
  //light view
  	class RestaurantAdapter extends ArrayAdapter<Restaurant> {
  		RestaurantAdapter() {
  			super(MainActivity.this, R.layout.wifitransmit, model);
  		}
  		
  		public View getView(final int position, View convertView,
  												ViewGroup parent) {
  			View row=convertView;
  			RestaurantHolder holder=null;
  			
  			if (row==null) {													
  				LayoutInflater inflater=getLayoutInflater();
  				
  				row=inflater.inflate(R.layout.wifitransmit, parent, false);
  				holder=new RestaurantHolder(row);
  				row.setTag(holder);				
  			}
  			else {
  				holder=(RestaurantHolder)row.getTag();
  			}
  			holder.btn = (ImageView)row.findViewById(R.id.btn);
//  			holder.btn.setBackgroundResource(R.drawable.switch_on);
  			holder.btn.setTag(position);
  			holder.populateFrom(model.get(position));
//  			holder.btn.setIndex(position);
//  			MyOnClickListener moc = new MyOnClickListener();
  			holder.btn.setOnClickListener(MyOnClickListener.getInstance(position,holder));
  			
  			return(row);
  		}
  	}

private static String intToIp(int i) {       
        
        return "192"+ "." +       
      "168"+ "." +       
      "1"+ "." +i;  
   }
	private OnClickListener SendClickListener = new OnClickListener() {
			@Override
			public void onClick(View arg0) {	
				have_send_flag=1;
			}
		};
		Handler connhandler = new Handler(){
				public void handleMessage(android.os.Message msg) {
					super.handleMessage(msg); 
					switch (msg.what) {
					case 1:
						if(Language.equals("zh"))
						{
							Utils.toast(getApplicationContext(), R.string.str_conntowifi_zh);
						}
						else
						{
							Utils.toast(getApplicationContext(), R.string.str_conntowifi);
						}
						break;
					case 2:
						if(Language.equals("zh"))
						{
							Utils.toast(getApplicationContext(), R.string.str_completing_zh);
						}
						else
						{
							Utils.toast(getApplicationContext(), R.string.str_completing);
						}
						break;
					case 3:
						//connected
						if(Language.equals("zh"))
						{
							Utils.toast(getApplicationContext(), R.string.str_configuring_zh);
						}
						else
						{
							Utils.toast(getApplicationContext(), R.string.str_configuring);
						}
//						wifistate.setImageResource(R.drawable.wifi_on_48);					
						break;
					case 4:
						wifistate.setImageResource(R.drawable.wifi_on_48);	
						break;
					case 5:
						wifistate.setImageResource(R.drawable.wifi_off_48);
						break;
					case 9:
						addlist(namebuf,prod_type);
	    				adapter.notifyDataSetChanged();
	    				break;
					case 10:
						
						curs_ip = db.rawQuery("Select * From MainTable", null);
	    		 		if((byte)curs_ip.getCount()>0)
	     		    	{
	    		 			curs_ip.moveToFirst();
	     		    		lightno_buf=0;
	     		    		sqlstr = "update MainTable set lighttype = '" + connect_mode + "'  Where lightno = "+ lightno_buf;
	    					db.execSQL(sqlstr);
	    					sqlstr = "update MainTable set lightname = '" + localWifiUtils.SERVERIP + "'  Where lightno = "+ lightno_buf;
	    					db.execSQL(sqlstr);
	     		    	}
	     		    	else
	     		    	{ 		
	     		    		sqlstr = "Insert Into MainTable(_id,lighttype,lightno,lightname,addr1,addr2,addr3,lightstate) values(null," + connect_mode + "," + 0 + ",'" + localWifiUtils.SERVERIP + "'," + 0 + "," + 0 + "," + 0 + "," + 0 +")";
	     					db.execSQL(sqlstr);
	     		    	} 
						break;
					default:
						break;
					}
				}
				};
	private List<Module> decodePackets(List<DatagramPacket> packets) {			
		int i = 1;
		Module module;
		List<String> list = new ArrayList<String>();
		List<Module> modules = new ArrayList<Module>();
		
		DECODE_PACKETS:
		for (DatagramPacket packet : packets) {
			
			String data = new String(packet.getData(), 0, packet.getLength());
//			Log.d(TAG, i + ": " + data);
			if (data.equals(Utils.getCMDScanModules(this))) {
				continue;
			}
			
			for (String item : list) {
				if (item.equals(data)) {
					continue DECODE_PACKETS;
				}
			}
			
			list.add(data);
			if ((module = Utils.decodeBroadcast2Module(data)) != null) {
				module.setId(i);
				modules.add(module);
				i++;
			}
		}
		
		return modules;
	
    	
    }
	public static class MyOnClickListener implements OnClickListener {

		private static  MyOnClickListener instance = null;
		int mPosition;   
		RestaurantHolder viewHolder;  

		public MyOnClickListener(int inPosition,RestaurantHolder viewHolder) {
//			Utils.toast(MainActivity.ma, "testabc");
			mPosition= inPosition;    	           
		    viewHolder= viewHolder;  
		}

		public static MyOnClickListener getInstance(int inPosition,RestaurantHolder viewHolder) {
			if (instance == null)
				instance = new MyOnClickListener(inPosition,viewHolder);
				return instance;
			}
		/*
		public void onClick(View v) {
			TextView tv = null;
			TextView tv1 = null;
			TextView tv2 = null;
			View tlv=null;
			tlv = (View)v.getParent();
			tv = (TextView)tlv.findViewById(R.id.TextView01);
			tv1 = (TextView)tlv.findViewById(R.id.prod_id);
			tv2 = (TextView)tlv.findViewById(R.id.prod_type);
			lightno_buf = (byte) Integer.parseInt(tv1.getText().toString());
			Cursor curs= db.rawQuery("Select * From MainTable", null);
			if(curs.getCount()>0)
			{
				curs.moveToFirst();
				while(!curs.isAfterLast())
				{
					if(lightno_buf==(byte)curs.getInt(2))
					{
						prod_type=(byte)curs.getInt(1);
						if(prod_type==1)
						{
							localWifiUtils.msg8[5]=(byte)curs.getInt(4);
							if((byte)curs.getInt(4)==(byte)0xaa)
							{
								v.setBackgroundResource(R.drawable.switch_off);	
								localWifiUtils.msg8[5]=(byte)0xa2;
								sqlstr = "update MainTable set lightstate = " + (byte)0x55 + "  Where lightno = " + lightno_buf;
							}
							else
							{
								v.setBackgroundResource(R.drawable.switch_on);
								localWifiUtils.msg8[5]=(byte)0xac;
								sqlstr = "update MainTable set lightstate = " + (byte)0xaa + "  Where lightno = " + lightno_buf;
							}
							localWifiUtils.msg8[2]=0x01;
							localWifiUtils.msg8[3]=(byte)curs.getInt(5);
							localWifiUtils.msg8[4]=(byte)curs.getInt(6);
							localWifiUtils.sendMessage(localWifiUtils.msg8);

						}
					}
					curs.moveToNext();
				}
			}
		}
*/
		public void onClick(View v) {
		//TODO: do something here
			TextView tv = null;
			TextView tv1 = null;
			TextView tv2 = null;
			View tlv=null;
			tlv = (View)v.getParent();
			tv = (TextView)tlv.findViewById(R.id.TextView01);
			tv1 = (TextView)tlv.findViewById(R.id.prod_id);
			tv2 = (TextView)tlv.findViewById(R.id.prod_type);			
		//	CommandType = 4;
		//	localWifiUtils.msg4[3] = (byte) Integer.parseInt(tv1.getText().toString());
		//	if(tv2.getText()=="1")
		//			{
		//				lightno_buf= (byte) Integer.parseInt(tv1.getText().toString());
						if(tv.getText()=="1")
						{
							v.setBackgroundResource(R.drawable.switch_off);	
							sqlstr = "update MainTable set lightstate = 0 Where lighttype = " + tv2.getText() + " And lightno = " + tv1.getText();
							tv.setText("0");
							localWifiUtils.msg8[5]=(byte)0xa2;
						}
						else
						{
							v.setBackgroundResource(R.drawable.switch_on);
							sqlstr = "update MainTable set lightstate = 1 Where lighttype = " + tv2.getText() + " And lightno = " + tv1.getText();
							tv.setText("1");
							localWifiUtils.msg8[5]=(byte)0xac;
						}
						db.execSQL(sqlstr);
						final Cursor curs_buf= db.rawQuery("Select * From MainTable where lightno=?", new String[]{tv1.getText().toString()});
						curs_buf.moveToFirst();
						localWifiUtils.msg8[2]=0x01;
						localWifiUtils.msg8[3]=(byte)curs_buf.getInt(5);
						localWifiUtils.msg8[4]=(byte)curs_buf.getInt(6);
						localWifiUtils.sendMessage(localWifiUtils.msg8);
		//			}
		}
		}
//重写返回键
public boolean onKeyDown(int keyCode, KeyEvent event) {
PackageManager pm = getPackageManager();
ResolveInfo homeInfo =
pm.resolveActivity(new Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_HOME), 0);
if (keyCode == KeyEvent.KEYCODE_BACK) {
ActivityInfo ai = homeInfo.activityInfo;
Intent startIntent = new Intent(Intent.ACTION_MAIN);
startIntent.addCategory(Intent.CATEGORY_LAUNCHER);
startIntent.setComponent(new ComponentName(ai.packageName, ai.name));
startActivitySafely(startIntent);
return true;
} else
return super.onKeyDown(keyCode, event);
}
private void startActivitySafely(Intent intent) {
intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
try {
startActivity(intent);
} catch (ActivityNotFoundException e) {
Toast.makeText(this, "null",
Toast.LENGTH_SHORT).show();
} catch (SecurityException e) {
Toast.makeText(this, "null",
Toast.LENGTH_SHORT).show();
}
} 


}

