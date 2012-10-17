package org.everapp.kaixin;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.SimpleCursorAdapter;
import android.widget.SimpleCursorAdapter.ViewBinder;
import android.widget.TextView;

public class MyAdapter extends CursorAdapter{
    
	public Context context;
    public LayoutInflater inflater;
    Cursor c;
    public MyAdapter(Context context, Cursor c, boolean autoRequery) {
		super(context, c, autoRequery);
		// TODO Auto-generated constructor stub
	}
    public MyAdapter(Context context, Cursor c) {
		super(context, c, true);
		this.context=context;
		this.c=c;
		inflater=LayoutInflater.from(context);
		// TODO Auto-generated constructor stub
	}
	
	
 

	@Override
	public View newView(Context context, Cursor c, ViewGroup parent) {
		// TODO Auto-generated method stub
		
		return inflater.inflate(R.layout.item, parent, false);
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		// TODO Auto-generated method stub
		TextView maintitle=(TextView)view.findViewById(R.id.maintitle);
		TextView maincontent=(TextView)view.findViewById(R.id.maincontent);
		    maincontent.setText(cursor.getString(cursor.getColumnIndex("simcontent"))+"...");
		    maintitle.setText(cursor.getString(cursor.getColumnIndex("title")));
		 	
	}
	
    
}
