package org.everapp.kaixin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.*;
import android.widget.AdapterView.OnItemClickListener;

public class MainActivity extends Activity implements OnClickListener, OnItemClickListener {
    
	private Button refresh,favorites;
	private PullToRefreshListView refreshList;
	private ListView listview;
	
	
	private SQLiteHelper helper=new SQLiteHelper(this,"kaixin_db");
	SQLiteDatabase db;
	String jsonData=null;
	MyAdapter adapter;
	Cursor cursor;
	public static int curPosition=0;
    public static int refreshsource=0;
    public static final int PULL_REFRESH=0;
    public static final int BUTTON_REFRESH=1;
	public static final int MAX_COUNT=80;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        refresh=(Button)findViewById(R.id.refresh);
        favorites=(Button)findViewById(R.id.favorites);
        refreshList=(PullToRefreshListView)findViewById(R.id.listview);
        refreshList.setOnRefreshListener(new OnRefreshListener<ListView>(){

			public void onRefresh(PullToRefreshBase<ListView> refreshView) {
				// TODO Auto-generated method stub
				refreshsource=PULL_REFRESH;
				if(NetState.isNetworkConnected(MainActivity.this)){
					LoadTask task=new LoadTask();
					task.execute();
				}else{
					refreshList.onRefreshComplete();
					Toast.makeText(MainActivity.this, "无网络连接！", Toast.LENGTH_SHORT).show();
				}
				
			}
        	
        });
        refresh.setOnClickListener(this);
        favorites.setOnClickListener(this);
        removeOldRecords();
	    listview=refreshList.getRefreshableView();
	    listview.setOnItemClickListener(this);
        
    }
    
    
    
    @Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		bindAdapter();
	}



	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		cursor.close();
		db.close();
	}




	class LoadTask extends AsyncTask<String,Integer,String>{

		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			int maxid=getMaxid();
			
			Map< String, String> map = new HashMap< String, String>();
			map.put("request", "getnewer");
			map.put("maxid", String.valueOf(maxid));
			String result=NetOperation.postData(map);
			if(result==null)result="error";
			return result;
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			if(refreshsource==PULL_REFRESH){
				refresh.setClickable(true);
				refreshList.onRefreshComplete();
			}else if(refreshsource==BUTTON_REFRESH){
				refresh.clearAnimation();
				refresh.setClickable(true);
			}
			
			if(result.startsWith("[")){
				JsonUtils jsonUtils=new JsonUtils();
				 List<Map<String,Object>> list=new ArrayList<Map<String,Object>>();
				 //if(!list.isEmpty())list.clear();
				jsonUtils.parseJson(result);
				
				list=jsonUtils.getData();
				
			    saveData(list);
			    cursor.requery();
			}else{
				Toast.makeText(MainActivity.this, "获取数据失败!", Toast.LENGTH_SHORT).show();
			}
			
			 
		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			if(refreshsource==PULL_REFRESH){
				refresh.setClickable(false);
			}else if(refreshsource==BUTTON_REFRESH){
				
			}
		}
    	
    }
   	    
	public void bindAdapter(){
		
		db=helper.getReadableDatabase();
        cursor=db.query("kaixin", new String[]{"distinct num as _id","title","substr(content,1,20) as simcontent"}, null, null, null, null, "num DESC");
        
         adapter=new MyAdapter(this, cursor);
        
        listview.setAdapter(adapter);
       listview.setSelection(curPosition);
	}
	public int getMaxid(){
		SQLiteDatabase db=helper.getReadableDatabase();
		Cursor cursor=db.query("kaixin", new String[]{"max(num) as totalnum"}, null, null, null, null, null);
	    cursor.moveToFirst();
	    int maxid=cursor.getInt(0);
		cursor.close();
		return maxid;
		
	}
	public int getRecordsCount(){
		   SQLiteDatabase db=helper.getReadableDatabase();
	        //get the count of the records in table "kaixin"
	        Cursor cursor=db.query("kaixin", new String[]{"count(num) as totalnum"}, null, null, null, null, null);
		    cursor.moveToFirst();
		    int count=cursor.getInt(0);
		    cursor.close();
		    
			return count;
			
	}
	public void removeOldRecords(){
	    int count=getRecordsCount();
	   
	    int maxid=getMaxid();
	   
		if(count>MAX_COUNT){
			SQLiteDatabase deletedb=helper.getWritableDatabase();
			String sql="delete from kaixin where num<%d-%d";
			sql=String.format(sql, maxid,MAX_COUNT);
			deletedb.execSQL(sql);
			deletedb.close();
		}
	}

	
	//save the data from network to database
	public void saveData(List<Map<String,Object>> list){
		SQLiteDatabase db=helper.getWritableDatabase();
		for(int i=0;i<list.size();i++){
			Map<String,Object> returnmap=list.get(i);
			int num=Integer.parseInt(returnmap.get("num").toString());
			String title=returnmap.get("title").toString();
			String content=returnmap.get("content").toString();
			int up=Integer.parseInt(returnmap.get("up").toString());
			int down=Integer.parseInt(returnmap.get("down").toString());
			
			ContentValues values=new ContentValues();
			values.put("num", num);
			values.put("title", title);
			values.put("content", content);
			values.put("up", up);
			values.put("down", down);
			
			values.put("iscomment", 1);
			db.insert("kaixin", null, values);
			//an update example;
			//values.put("name","zhangsan");
			//db.update("kaixin",values,"id=?",new String[]{"1"});
			//System.out.println(returnmap.get("title").toString());
			
		}
		
	}
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
		if(v==refresh){
			
			refreshsource=BUTTON_REFRESH;
			if(NetState.isNetworkConnected(MainActivity.this)){
				refresh.setClickable(false);
				LoadTask task=new LoadTask();
				task.execute();
				Animation animation=AnimationUtils.loadAnimation(this, R.anim.refresh_rotate);
				refresh.setAnimation(animation);
				
			}else{
				Toast.makeText(this, "无网络连接!", Toast.LENGTH_SHORT).show();
			}
		}
	       	
			
		if(v==favorites){
				
					Intent intent=new Intent(MainActivity.this,FavoritesActivity.class);
					MainActivity.this.startActivity(intent);
					overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
		}			
			
		
	}
   
  /* public void setItemRead(int itemid){
	   SQLiteDatabase db=helper.getWritableDatabase();
	   ContentValues values=new ContentValues();
	   values.put("status", 0);
	   db.update("kaixin", values, "num="+itemid, null);
	   
   }*/
    

public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
	// TODO Auto-generated method stub
	
	ListView list=(ListView)parent;
	curPosition=list.getFirstVisiblePosition();
	Cursor itemcursor=(Cursor)list.getItemAtPosition(position);
	int itemid=itemcursor.getInt(itemcursor.getColumnIndex("_id"));
	itemcursor.close();
	//setItemRead(itemid);
	Intent intent=new Intent();
	intent.putExtra("itemid", itemid);
	intent.setClass(MainActivity.this, DetailActivity.class);
    startActivity(intent);
    overridePendingTransition(R.anim.slide_right_in,R.anim.slide_left_out);
}

    
}
