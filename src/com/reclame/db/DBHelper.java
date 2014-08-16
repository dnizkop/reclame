package com.reclame.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBHelper extends SQLiteOpenHelper {

	static String LOG_TAG = "DB";
	
	public DBHelper(Context context) {
		super(context, "news", null, 1);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		Log.d(LOG_TAG, "--- onCreate database ---");
 
		db.execSQL("create table news ("
				+ "ID integer primary key autoincrement," + "name varchar(100), description text,"
				+ "url_picture text, checked boolean, is_posting boolean" + ");");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}

}
