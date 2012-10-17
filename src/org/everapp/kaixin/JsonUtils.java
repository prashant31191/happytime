package org.everapp.kaixin;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.stream.*;

public class JsonUtils {
	  List<Map<String,Object>> list=new ArrayList<Map<String,Object>>();
	  int intvalue;
	  String stringvalue;
	  SQLiteHelper helper;
	  public List<Map<String,Object>> getData(){
		  
		  return list;
	  }
      public void parseJson(String jsonData){
    	  
    	  try{
    	  JsonReader reader=new JsonReader(new StringReader(jsonData));
    	  reader.beginArray();
    	  while(reader.hasNext()){
    		  reader.beginObject();
    		  Map<String ,Object> map=new HashMap<String,Object>();
    		  while(reader.hasNext()){
    			  String tagName=reader.nextName();
    			  
    			  if(tagName.equals("num")){
    				  intvalue=reader.nextInt();
    				  map.put("num", intvalue);
    			  }
    			  else if(tagName.equals("title")){
    				  stringvalue=reader.nextString();
    				  map.put("title", stringvalue);
    			  }
                  else if(tagName.equals("content")){
                	  stringvalue=reader.nextString();
    				  map.put("content", stringvalue);
    			  }
                  else if(tagName.equals("up")){
                	  intvalue=reader.nextInt();
    				  map.put("up", intvalue);
    			  }
                  else if(tagName.equals("down")){
                	  intvalue=reader.nextInt();
    				  map.put("down", intvalue);
                  }
    			  
    		  }
    		  list.add(map);
    		  reader.endObject();
    	  }
    	  reader.endArray();
    	  reader.close();
    	  }catch(Exception e){
    		  e.printStackTrace();
    	  }
      }
      
}
