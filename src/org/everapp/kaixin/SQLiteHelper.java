package org.everapp.kaixin;

import android.content.Context;
import android.database.sqlite.*;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
public class SQLiteHelper extends SQLiteOpenHelper{
    private static final int VERSION=1;
	public SQLiteHelper(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);
		// TODO Auto-generated constructor stub
	}
	public SQLiteHelper(Context context, String name,
			int version) {
		super(context, name, null, version);
		// TODO Auto-generated constructor stub
	}
	public SQLiteHelper(Context context, String name
	) {
		super(context, name, null, VERSION);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		String sql="CREATE TABLE IF NOT EXISTS %s (%s int ,%s varchar(20),%s varchar(255),%s int,%s int,%s int default 1)";
		//未评价iscomment 为1，评价后为0；
		sql=String.format(sql, "kaixin","num","title","content","up","down","iscomment");
		String sql2="CREATE TABLE  IF NOT EXISTS %s(%s int,%s varchar(20),%s varchar(255))";
		sql2=String.format(sql2, "shoucang","num","title","content");
		//String sql="create table test if not exists (title varchar(10),content varchar(40))";
		db.execSQL(sql);
		db.execSQL(sql2);
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		
	}

}
