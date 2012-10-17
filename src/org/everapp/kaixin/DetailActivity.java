package org.everapp.kaixin;

import java.util.ArrayList;


import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;

public class DetailActivity extends FragmentActivity {
    

	ViewPager pager;
	MyPagerAdapter adapter;
	private SQLiteHelper helper=new SQLiteHelper(this,"kaixin_db");
	private int itemid;
	ArrayList<Integer> list=new ArrayList<Integer>();
	

	int id;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.newdetail);
		pager=(ViewPager)findViewById(R.id.detail_pager);
		
		
		Intent intent=getIntent();
		 itemid=intent.getIntExtra("itemid", 0);
		if(itemid==0)return;
		SQLiteDatabase db=helper.getReadableDatabase();
		//Cursor cursor=db.rawQuery("select title,content,up,down from kaixin where num="+itemid, null);
		Cursor cursor=db.query("kaixin", new String[]{"num"} , null,
				null, null, null, null);
		while(cursor.moveToNext()){
			id=cursor.getInt(0);
			list.add(id);
		}
		
		cursor.close();
		db.close();
		FragmentManager fm=getSupportFragmentManager();
		
		adapter=new MyPagerAdapter(fm,list);
		pager.setAdapter(adapter);
		pager.setCurrentItem(list.indexOf((Integer)itemid));
	}
	


	

}
