package com.uniks.fsmsim.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DbHelper extends SQLiteOpenHelper {
	static String DATABASE_NAME = "filedata";
	public static final String TABLE_NAME = "file";
	public static final String DATA_ID="id";
	public static final String KEY_FNAME = "fName";

	public DbHelper(Context context) {
		super(context, DATABASE_NAME, null, 1);

	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " ("+ DATA_ID + " INTEGER PRIMARY KEY "+KEY_FNAME + " TEXT)";
		db.execSQL(CREATE_TABLE);

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
		onCreate(db);

	}

}