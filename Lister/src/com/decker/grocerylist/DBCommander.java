package com.decker.grocerylist;

import java.util.ArrayList;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteStatement;
import android.widget.Toast;


public class DBCommander 
{
	MySQLiteHelper helper;
	private SQLiteDatabase db;
	Context appContext;
	
	
	public DBCommander(Context context)
	{
		appContext = context;
		helper = new MySQLiteHelper(context);
		try
		{
			db = helper.getWritableDatabase();
		}
		catch(SQLiteException ex)
		{
			showToast("Error: failed to open database, please restart application");
		}
		
	}
	
	public ArrayList<String> getList(String list)
	{
		String tempList = escapeSQLString(list);
			if(doesListExist(list)){
				Cursor tempCursor = db.rawQuery("SELECT * FROM " + tempList, new String[]{});
				ArrayList<String> tempArrayList = cursorToArrayList(tempCursor);
				tempCursor.close();
				return tempArrayList;
			}
			else{
				showToast("Error: List does not exist");
				ArrayList<String> temp = new ArrayList<String>();
				return temp;
			}
		
	}
	
	public ArrayList<String> getListofLists()
	{
		
		ArrayList<String> tempArrayList = new ArrayList<String>();
		Cursor temp = db.rawQuery("SELECT name FROM sqlite_master WHERE type = 'table'", null);
		tempArrayList = cursorToArrayList(temp);
		return tempArrayList;
	}
	public void saveList(String listName, ArrayList<String> itemsList)
	{
		deleteListDB(listName);
		createListDB(escapeSQLString(listName), itemsList);
	}
	public void deleteList(String list)
	{
		if(doesListExist(list));{
			deleteListDB(escapeSQLString(list));
		}
	}
	public void closeDB()
	{
		db.close();
	}
	public void openDB()
	{
		try
		{
		db = helper.getWritableDatabase();
		}
		catch(SQLiteException ex)
		{
			showToast("Error:Failed to open data base");
		}
	}
	public boolean isDbOpen()
	{
		return db.isOpen();
	}
	private boolean doesListExist(String list)
	{
		Cursor temp = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table' AND name=?", new String[]{list});
		if(temp.moveToFirst()){
			temp.close();
			return true;
		}
		temp.close();
		return false;
	}
	
	private void createListDB(String listName, ArrayList<String> itemsList)
	{
		db.execSQL("CREATE table " + listName + " (ITEM TEXT)", new String[]{});
		addItemsToListDB(listName,itemsList);
	}
	private void deleteListDB(String list)
	{
		if(isEscaped(list)){
		db.execSQL("DROP TABLE IF EXISTS " + list  , new String[]{});
		}
		else{
			db.execSQL("DROP TABLE IF EXISTS " + escapeSQLString(list)  , new String[]{});
		}
		
	}
	
	private ArrayList<String> cursorToArrayList(Cursor cursor)
	{
		ArrayList<String> temp = new ArrayList<String>();
		if(cursor != null){
			cursor.moveToFirst();
			for(cursor.moveToFirst();!cursor.isAfterLast();cursor.moveToNext())
			{
				if(!cursor.getString(0).equalsIgnoreCase("android_metadata")){
				temp.add(cursor.getString(0));
				}
			}
		}
		cursor.close();
		return temp;
	}
	
	private void showToast(String message)
	{
		Toast toast = Toast.makeText(appContext,message,Toast.LENGTH_LONG);
		toast.show();
	}

	private void addItemsToListDB(String listName, ArrayList<String> itemsList)
	{
		String sql = "INSERT INTO " + listName + " VALUES(?)";
		SQLiteStatement statement = db.compileStatement(sql);
		db.beginTransaction();
		for(String item:itemsList)
		{
			statement.clearBindings();
			statement.bindString(1, item);
			statement.execute();
		}
		db.setTransactionSuccessful();
		db.endTransaction();
	}
	private String escapeSQLString(String target)
	{
		StringBuilder temp = new StringBuilder();
		temp.append('[');
		temp.append(target);
		temp.append(']');
		return temp.toString();
	}
	private boolean isEscaped(String target)
	{
		if(target.charAt(0) == '[' || target.charAt(target.length() -1) == ']'){
				return true;
		}
		return false;
	}
	

}
