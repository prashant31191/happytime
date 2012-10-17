package org.everapp.kaixin;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class FavoritesActivity extends Activity implements OnItemClickListener{
	
    private ListView listFavor;
    private SQLiteHelper helper=new SQLiteHelper(this,"kaixin_db");
    private Cursor cursor;
    private SimpleCursorAdapter adapter;
    private SQLiteDatabase db;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.favorites);
		listFavor=(ListView)findViewById(R.id.listfavorites);
		listFavor.setOnItemClickListener(this);
		
	}
	
	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		 db=helper.getReadableDatabase();
       cursor=db.query("shoucang", new String[]{"num as _id","title","substr(content,1,20) as simcontent"}, null, null, null, null, "num DESC");
        
        adapter=new SimpleCursorAdapter(this, R.layout.item, cursor, 
       		new String[]{"title","simcontent","_id"}, new int[]{R.id.maintitle,R.id.maincontent,R.id.mainnum});
       
       listFavor.setAdapter(adapter);
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		cursor.close();
		db.close();
	}
	

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		cursor.close();
		db.close();
	}

	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		// TODO Auto-generated method stub
		ListView list=(ListView)parent;
		Cursor itemcursor=(Cursor)list.getItemAtPosition(position);
		int itemid=itemcursor.getInt(itemcursor.getColumnIndex("_id"));
		itemcursor.close();
		Intent intent=new Intent();
		intent.putExtra("itemid", itemid);
		intent.setClass(FavoritesActivity.this, DetailActivity.class);
		startActivity(intent);
		overridePendingTransition(R.anim.slide_right_in,R.anim.slide_left_out);
		
	}
	

}
