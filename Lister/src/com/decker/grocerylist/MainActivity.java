package com.decker.grocerylist;
import java.util.ArrayList;

import android.os.Bundle;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.os.Vibrator;


public  class MainActivity extends ListActivity{
	public ArrayList<String> loadedItemsList;
	public DBCommander DBConnection;
	public String loadedListName;
	public  CustomArrayAdapter loadListAdapter;
	public Vibrator vibrator;
	
	EditText itemInputBox;
	ListView itemsList;
	TextView listName;
	
	public int listSelection;
	AlertDialog.Builder listDialog;
	public int listDialogSelection;
	public ArrayList<String> DBLists;
	AlertDialog.Builder inputDialog;
	EditText inputDialogTextBox;
	
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		getApplicationContext();
		vibrator = (Vibrator)getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
		loadedItemsList = new ArrayList<String>();
		itemInputBox = (EditText)findViewById(R.id.editText1);
		listName = (TextView)findViewById(R.id.textView1);
		itemsList = getListView();
		loadListAdapter = new CustomArrayAdapter(this,loadedItemsList);
		DBLists = new ArrayList<String>();
		
		DBConnection = new DBCommander(this);
		itemsList.setAdapter(loadListAdapter);	
		
		inputDialog = new AlertDialog.Builder(this);
		inputDialogTextBox = new EditText(this);
		inputDialog.setMessage("Name your List");
		inputDialogTextBox.setInputType(InputType.TYPE_CLASS_TEXT);
		inputDialog.setView(inputDialogTextBox);
		inputDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				listName.setText(inputDialogTextBox.getText().toString());
				
			}
		});
		
		listDialog = new AlertDialog.Builder(this);
		listDialog.setTitle("Select a List");
		itemsList.setOnItemClickListener(new OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,long arg3) 
			{
				vibrator.vibrate(50);
				arg1.setSelected(true);
				listSelection = arg2;
			}
		});
		
		
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu){
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	public void buttonClick(View v){
		Button b =(Button)v;
		if(b.getText().toString().equalsIgnoreCase("Add"))
		{
			vibrator.vibrate(50);
			loadedItemsList.add(itemInputBox.getText().toString());
			loadListAdapter.clear();
			loadListAdapter.addAll(loadedItemsList);
			loadListAdapter.notifyDataSetChanged();
			itemInputBox.setText("");
		}
		else
		{
			vibrator.vibrate(50);
			if(loadedItemsList.size() > 0)
			{
				//if the user hasn't selected an item to delete and click delete remove the last item in the list
				if(listSelection < 0)
				{
				   listSelection = loadedItemsList.size() -1;
				}
				loadedItemsList.remove(listSelection);
				loadListAdapter.clear();
				loadListAdapter.addAll(loadedItemsList);
				loadListAdapter.notifyDataSetChanged();
				listSelection = -1;
			}
		}
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item){
		vibrator.vibrate(50);
		switch(item.getItemId())
		{
		case R.id.open_btn:
			DBLists = DBConnection.getListofLists();
			if(!DBLists.isEmpty());
			{
			listDialog.setItems(DBLists.toArray(new String[DBLists.size()]), new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					loadedItemsList = DBConnection.getList(DBLists.get(which));
					loadedListName = DBLists.get(which);
					listName.setText(loadedListName);
					loadListAdapter.clear();
					loadListAdapter.addAll(loadedItemsList);
					loadListAdapter.notifyDataSetChanged();
				}
			});
			listDialog.show();
			}
			return true;
		case R.id.save_btn:
			if(loadedListName.length() == 0)
			{
				inputDialogTextBox = new EditText(this);
				inputDialog.setView(inputDialogTextBox);
				inputDialog.show();
			}
			else
			{
				DBConnection.saveList(listName.getText().toString(), loadedItemsList);
			}
			return true;
		case R.id.delete_btn:
			DBLists = DBConnection.getListofLists();
			if(!DBLists.isEmpty());
			{
			listDialog.setItems(DBLists.toArray(new String[DBLists.size()]), new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					DBConnection.deleteList(DBLists.get(which));
				}
			});
			listDialog.show();
			}
			return true;
		case R.id.new_file_btn:
			 inputDialogTextBox = new EditText(this);
			 inputDialog.setView(inputDialogTextBox);
			 inputDialog.show();
			 loadedListName = listName.getText().toString();
			 loadedItemsList = new ArrayList<String>();
			 loadListAdapter.clear();
			 loadListAdapter.notifyDataSetChanged();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}	
	}
	@Override
	public void onPause()
	{
		if(DBConnection.isDbOpen())
		{
			DBConnection.closeDB();
		}
		super.onPause();
	}
	@Override
	public void onResume()
	{
		if(!DBConnection.isDbOpen())
		{
			DBConnection.openDB();
		}
		super.onResume();
	}
	
	
}
