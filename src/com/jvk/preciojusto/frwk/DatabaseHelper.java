package com.jvk.preciojusto.frwk;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {

	public static String TABLE_NAME="bid";
	
	public DatabaseHelper(Context context, String name, CursorFactory factory,
			int version) {
		
		super(context, name, factory, version);
		Log.d("TID_EXAMPLE","C:DatabaseHelper");
	
	}

	@Override
	public void onCreate(SQLiteDatabase arg0) {
		Log.d("TID_EXAMPLE","onCreate");
		arg0.execSQL("CREATE TABLE " + TABLE_NAME + " (" +
				   "id_"+TABLE_NAME  + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "urlPhoto VARCHAR(255),userPropietary VARCHAR(255)," +
                    "urlData VARCHAR(255),description VARCHAR(255)," +
                    "timeStamp NUMERIC,price REAL);");

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.d("TID_EXAMPLE","onUpgrade");
		  db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
          onCreate(db);

	}

}
