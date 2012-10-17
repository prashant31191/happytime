package org.everapp.kaixin;

import java.util.HashMap;
import java.util.Map;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class DetailFragment extends Fragment implements OnClickListener{
    TextView title,content,up,down;
    Button share,save;
    String titleText,contentText,upNumber,downNumber;
    int iscomment;
    int id;
    String s;
     SQLiteHelper helpe;
    public static DetailFragment newInstance(int id){
    	
    	DetailFragment f=new DetailFragment();
    	Bundle bundle=new Bundle();
    	bundle.putInt("id", id);
    	f.setArguments(bundle);
		return f;
    	
    }
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		
		helpe=new SQLiteHelper(getActivity(),"kaixin_db");
		SQLiteDatabase db=helpe.getReadableDatabase();
		
		Cursor cursor=db.query("kaixin", new String[]{"title","content","up","down","iscomment"} , "num="+id,
				null, null, null, null);
		cursor.moveToFirst();
		titleText=cursor.getString(0);
		title.setText(titleText);
		contentText=cursor.getString(1);
		content.setText(contentText);
		upNumber=String.valueOf(cursor.getInt(2));
		downNumber=String.valueOf(cursor.getInt(3));
		iscomment=cursor.getInt(4);
		
		up.setText(String.valueOf(cursor.getInt(2)));
		down.setText(String.valueOf(cursor.getInt(3)));
		up.setOnClickListener(this);
		down.setOnClickListener(this);
		share.setOnClickListener(this);
		save.setOnClickListener(this);
		checkCommentStatus(iscomment);
	
		cursor.close();
		db.close();
	}
	private void checkCommentStatus(int iscomment){
		if(iscomment==0){
			up.setClickable(false);
			down.setClickable(false);
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		id=getArguments()!=null?getArguments().getInt("id"):0;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view=inflater.inflate(R.layout.detail_fragment, container, false);
		title=(TextView)view.findViewById(R.id.detailTitle);
		content=(TextView)view.findViewById(R.id.detailContent);
		up=(TextView)view.findViewById(R.id.upwidget);
		down=(TextView)view.findViewById(R.id.downwidget);
		share=(Button)view.findViewById(R.id.detailshare);
		save=(Button)view.findViewById(R.id.detailsave);
		return view;
	}
	public void onClick(View view) {
		// TODO Auto-generated method stub
		if(view==up){
			SQLiteDatabase db=helpe.getWritableDatabase();
			int upnum=Integer.parseInt(upNumber)+1;
			up.setText(String.valueOf(upnum));
			ContentValues values=new ContentValues();
			iscomment=0;
			values.put("up", upnum);
			values.put("iscomment", iscomment);
			db.update("kaixin", values, "num="+id, null);
			db.close();
			updateStatus();
			s="addup";
			updateStats();
		}
		if(view==down){
			SQLiteDatabase db=helpe.getWritableDatabase();
			int downnum=Integer.parseInt(upNumber)+1;
			down.setText(String.valueOf(downnum));
			ContentValues values=new ContentValues();
			iscomment=0;
			values.put("down", downnum);
			values.put("iscomment", iscomment);
			db.update("kaixin", values, "num="+id, null);
			db.close();
			updateStatus();
			s="adddown";
			updateStats();
		}
		
		if(view==share){
			Intent intent=new Intent(Intent.ACTION_SEND);
			intent.setType("text/plain");
			intent.putExtra(Intent.EXTRA_SUBJECT, "share");
			intent.putExtra(Intent.EXTRA_TEXT, titleText+"\n"+contentText);
			startActivity(Intent.createChooser(intent, getActivity().getTitle()));
			getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
			
		}
		if(view==save){
			//首先检测是否已经收藏
			SQLiteDatabase readdb=helpe.getReadableDatabase();
			Cursor cursor=readdb.query("shoucang", new String[]{"num"}, "num="+id, null, null, null, null);	
			if(cursor.moveToNext()){
				Toast.makeText(getActivity(), "您已经收藏过了", Toast.LENGTH_SHORT).show();
				return;
			}
			cursor.close();
			//readdb.close();
			SQLiteDatabase db=helpe.getWritableDatabase();
			ContentValues values=new ContentValues();
			values.put("num", id);
			values.put("title", titleText);
			values.put("content", contentText);
			db.insert("shoucang", null, values);
			//db.close();
			Toast.makeText(getActivity(), "收藏成功", Toast.LENGTH_SHORT).show();
		}
	}
	
	private void updateStatus(){
		SQLiteDatabase db=helpe.getReadableDatabase();
		Cursor cursor=db.query("kaixin", new String[]{"up","down","iscomment"}, "num="+id, null, null, null, null);
			cursor.moveToFirst();
			String newupNumber=String.valueOf(cursor.getInt(0));
			String newdownNumber=String.valueOf(cursor.getInt(1));
			int newisComment=cursor.getInt(2);
			up.setText(newupNumber);
			down.setText(newdownNumber);
			checkCommentStatus(newisComment);
			cursor.close();
			db.close();
				
	}
	public void updateStats(){
		MyRunnable r=new MyRunnable();
		Thread thread=new Thread(r);
		thread.start();
		
		
	}
	class MyRunnable implements Runnable{
		


	public void run() {
		// TODO Auto-generated method stub
		Map<String ,String> map=new HashMap<String,String>();
		map.put("request", s);
		map.put("id", String.valueOf(id));
		NetOperation.postData(map);
	}

	}
}
