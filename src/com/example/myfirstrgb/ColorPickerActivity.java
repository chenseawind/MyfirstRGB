package com.example.myfirstrgb;

import java.util.ArrayList;
import java.util.List;

import com.example.myfirstrgb.MainActivity;
import android.os.Message;
import android.view.MotionEvent;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
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

public class ColorPickerActivity extends Activity {
	List<Restaurant> model=new ArrayList<Restaurant>();
	RefreshableView refreshableView;
	ListView listView;
	EditText name=null;
	EditText address=null;
	RadioGroup types=null;
	Restaurant current=null;
	ImageButton setbtn;
	ImageButton wifistate;
	int i;
	MiusThread miusThread;
    Context context;
    boolean isconncting = false;
    public static TextView tv1 = null;
    public static TextView tv2 = null;
    public static byte wheel_value;
    public static int rgbr = 0;
    public static int rgbg = 0;
    public static int rgbb = 0;
    public static boolean isOnLongClick=false;
    private TextView tv3= null;
    private TextView tv4 = null;
    private TextView tv5= null;
    private TextView tv6 = null;
    
    final  byte  CMD_SYS_ON_OFF=(byte)0x96;
    final  byte  CMD_WHITE_LEDS_ON=(byte)0x92;
    final  byte  CMD_BLINK_MODE_UP=(byte)0x95;
    final  byte  CMD_BLINK_MODE_DOWN=(byte)0x85;
    final  byte  CMD_BRIGHTNESS_UP=(byte)0x90;
    final  byte  CMD_BRIGHTNESS_DOWN=(byte)0x91;
    final  byte  CMD_TC_SPEED_UP=(byte)0x94;
    final  byte  CMD_TC_SPEED_DOWN=(byte)0x93;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.colorpickerview);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.titlebtn);
		wifistate = (ImageButton)findViewById(R.id.wifistate);
		wifistate.setImageResource(R.drawable.wifi_on_48);
		CompentOnTouch b = new CompentOnTouch(); 
		Button btn1 = (Button)findViewById(R.id.button1);
		Button btn2 = (Button)findViewById(R.id.button2);
		Button btn3 = (Button)findViewById(R.id.button3);
		Button btn4 = (Button)findViewById(R.id.button4);
		Button btn5 = (Button)findViewById(R.id.button5);
		Button btn6 = (Button)findViewById(R.id.button6);
		Button btn7 = (Button)findViewById(R.id.button7);
		Button btn8 = (Button)findViewById(R.id.button8);
		btn5.setOnTouchListener(b);
		btn6.setOnTouchListener(b);
		btn7.setOnTouchListener(b);
		btn8.setOnTouchListener(b);
		
		btn1.setOnClickListener(new  android.view.View.OnClickListener() {// 设置按钮的点击事件
			@Override
			 public void onClick(View v) {
				MainActivity.localWifiUtils.msg8[5]=CMD_SYS_ON_OFF;
				MainActivity.localWifiUtils.sendMessage(MainActivity.localWifiUtils.msg8); 
		 	}
		 });
		btn2.setOnClickListener(new  android.view.View.OnClickListener() {// 设置按钮的点击事件
			@Override
			 public void onClick(View v) {
				MainActivity.localWifiUtils.msg8[5]=CMD_WHITE_LEDS_ON;
				MainActivity.localWifiUtils.sendMessage(MainActivity.localWifiUtils.msg8); 
		 	}
		 });
		btn3.setOnClickListener(new  android.view.View.OnClickListener() {// 设置按钮的点击事件
			@Override
			 public void onClick(View v) {
				MainActivity.localWifiUtils.msg8[5]=CMD_BLINK_MODE_UP;
				MainActivity.localWifiUtils.sendMessage(MainActivity.localWifiUtils.msg8); 
		 	}
		 });
		btn4.setOnClickListener(new  android.view.View.OnClickListener() {// 设置按钮的点击事件
			@Override
			 public void onClick(View v) {
				MainActivity.localWifiUtils.msg8[5]=CMD_BLINK_MODE_DOWN;
				MainActivity.localWifiUtils.sendMessage(MainActivity.localWifiUtils.msg8); 
		 	}
		 });
		btn5.setOnClickListener(new  android.view.View.OnClickListener() {// 设置按钮的点击事件
			@Override
			 public void onClick(View v) {
				MainActivity.localWifiUtils.msg8[5]=CMD_BRIGHTNESS_UP;
				MainActivity.localWifiUtils.sendMessage(MainActivity.localWifiUtils.msg8); 
		 	}
		 });
		btn6.setOnClickListener(new  android.view.View.OnClickListener() {// 设置按钮的点击事件
			@Override
			 public void onClick(View v) {
				MainActivity.localWifiUtils.msg8[5]=CMD_BRIGHTNESS_DOWN;
				MainActivity.localWifiUtils.sendMessage(MainActivity.localWifiUtils.msg8); 
		 	}
		 });
		btn7.setOnClickListener(new  android.view.View.OnClickListener() {// 设置按钮的点击事件
			@Override
			 public void onClick(View v) {
				MainActivity.localWifiUtils.msg8[5]=CMD_TC_SPEED_UP;
				MainActivity.localWifiUtils.sendMessage(MainActivity.localWifiUtils.msg8); 
		 	}
		 });
		btn8.setOnClickListener(new  android.view.View.OnClickListener() {// 设置按钮的点击事件
			@Override
			 public void onClick(View v) {
				MainActivity.localWifiUtils.msg8[5]=CMD_TC_SPEED_DOWN;
				MainActivity.localWifiUtils.sendMessage(MainActivity.localWifiUtils.msg8); 
		 	}
		 });
	}
	class CompentOnTouch implements OnTouchListener {
		@Override
		public boolean onTouch(View v, MotionEvent event) {
		switch (v.getId()) {
		case R.id.button5:
			MainActivity.localWifiUtils.msg8[5]=CMD_BRIGHTNESS_UP;
			onTouchChange(event.getAction());
		break;
		case R.id.button6:
			MainActivity.localWifiUtils.msg8[5]=CMD_BRIGHTNESS_DOWN;
			onTouchChange(event.getAction());
		break;
		case R.id.button7:
			MainActivity.localWifiUtils.msg8[5]=CMD_TC_SPEED_UP;
			onTouchChange(event.getAction());
			break;
		case R.id.button8:
			MainActivity.localWifiUtils.msg8[5]=CMD_TC_SPEED_DOWN;
			onTouchChange(event.getAction());
			break;
		
		}
		return true;
		}
		}
	class MiusThread extends Thread {
		@Override
		public void run() {
		while (isOnLongClick) {
		try {
		Thread.sleep(10);
		myHandler.sendEmptyMessage(1);
		} catch (InterruptedException e) {
		e.printStackTrace();
		}
		super.run();
		}
		}
		}
	private void onTouchChange(int eventAction) {
		// 按下松开分别对应启动停止减线程方法
		if (eventAction == MotionEvent.ACTION_DOWN) {
		miusThread = new MiusThread();
		isOnLongClick = true;
		miusThread.start();
		} else if (eventAction == MotionEvent.ACTION_UP) {
		if (miusThread != null) {
		isOnLongClick = false;
		}
		} else if (eventAction == MotionEvent.ACTION_MOVE) {
		if (miusThread != null) {
		isOnLongClick = true;
		}
		}
		}
	Handler myHandler = new Handler() {
		public void handleMessage(Message msg) {
		switch (msg.what) {
		case 1:
			MainActivity.localWifiUtils.sendMessage(MainActivity.localWifiUtils.msg8); 
		break;
		case 2:
			MainActivity.localWifiUtils.sendMessage(MainActivity.localWifiUtils.msg8); 
		break;
		}
		};
		};
	public static void sendmsg(Integer lMode){
		MainActivity.CommandType = 4;
		switch(lMode)
		{
		case 1:
			break;
		case 2:
			MainActivity.localWifiUtils.msg8[5] = wheel_value;
			break;
		case 3:
			break;
		}
        MainActivity.localWifiUtils.sendMessage(MainActivity.localWifiUtils.msg8); 
	}
}
