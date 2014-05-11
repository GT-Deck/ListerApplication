package com.decker.grocerylist;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MySQLiteHelper extends SQLiteOpenHelper
{

	private static final int DATABASE_VERSION = 1;
	private static final String DATABASE_NAME = "UserLists";
	
	public MySQLiteHelper(Context context) 
	{
		//specify our database name and version
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) 
	{
		/* Do nothing here. Empty database will be created when getWritableDatabase is called
		tables represent a users item lists which are created by the user at runtime*/
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) 
	{
		//No Upgrades planned, do nothing
	}

}
