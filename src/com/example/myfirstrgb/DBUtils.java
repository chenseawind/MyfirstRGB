package com.example.myfirstrgb;

import java.util.ArrayList;
import java.util.Calendar;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBUtils extends SQLiteOpenHelper {

	// 鏁版嵁搴撳悕瀛�
	public static final String DB_NAME = "RGBController.db";
	// 鏁版嵁搴撶増鏈�
	public static final int DB_VER = 1;
	public String TAG = "Database";

	// 琛ㄥ悕
	private static final String DEF_TABLE_NAME = "Light";
	private static final String SQL_CREATETABLE = "CREATE TABLE IF NOT EXISTS MainTable(id INTEGER primary key autoincrement, " + // 主键
			"lighttype INTEGER, " + 	// 设备类型
			"lightno INTEGER, " + 		// 编号
			"lightname TEXT, " + 	// 名称
			"lightstate INTEGER,"+ 
			"addr1 INTEGER, " +
			"addr2 INTEGER, " +
			"addr3 INTEGER)" ;

	// 瀹炰緥鍙ユ焺
	public static DBUtils instance;

	private SQLiteDatabase mDB; // 鏁版嵁搴撴搷浣滃疄渚�

	private ContentValues values;

	DBUtils(Context context) {
//		private DBUtils(Context context, String name, CursorFactory factory, int version) {
		super(context, DB_NAME, null, DB_VER);
		// TODO Auto-generated constructor stub
//		mDB = getWritableDatabase();
//		values = new ContentValues();
	}

//	/**
//	 * 鍒濆鍖栨湰绫�
//	 */
//	public static DBUtils init(Context context) {
//		if (instance == null) {
//			instance = new DBUtils(context);
//		}
//		return instance;
//	}
//
//	public static DBUtils getInstance() {
//		return instance;
//	}

//	/**
//	 * 鍏抽棴鏁版嵁搴撳伐鍏风被
//	 */
//	public void close() {
//		mDB.close();
//		mDB = null;
//		SQLiteDatabase.releaseMemory();
//	}

	/**
	 * 绗竴娆″垱寤烘暟鎹簱鐨勬椂鍊� 浼氳皟鐢ㄦ鏂规硶
	 * 
	 * 鍙湪姝ゅ垱寤鸿〃
	 */
	@Override
	public void onCreate(SQLiteDatabase db) {
		// 鍒涘缓榛樿琛紝鐢ㄤ簬瀛樻斁鍏叡鏁版嵁
//		db.execSQL(SQL_CREATETABLE.replaceFirst("?", DEF_TABLE_NAME));
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub

	}

//	public Cursor Query(String SQLStr,String[] selectionArgs)
//	{
//		mDB = getWritableDatabase();
//		Cursor c = mDB.rawQuery(SQLStr, selectionArgs);
//		return c;
//	}
//	
//	public void insert(String table, int frontSluiceLevel, int backSluiceLevel, int totalFlow) {
//		
//		values.clear();
//
////		values.put(cols0[0], day);
////		values.put(cols0[1], month);
////		values.put(cols0[2], hour);
////
////		values.put(cols[0], frontSluiceLevel);
////		values.put(cols[1], backSluiceLevel);
////		values.put(cols[2], totalFlow);
//
//		long rowId = mDB.insert(table, null, values);
//		if (rowId == -1) {
//			Log.e(TAG, "鎻掑叆鏁版嵁澶辫触锛�" + table);
//		}
//
//		values.clear();
//	}
}
