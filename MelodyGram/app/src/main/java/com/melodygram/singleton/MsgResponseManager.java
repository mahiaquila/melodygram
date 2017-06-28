package com.melodygram.singleton;

import android.content.Context;
import android.util.Log;

import com.melodygram.model.ChatMessageModel;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;
/**
 * Created by LALIT on 15-06-2016.
 */

public class MsgResponseManager {
	private Context contextRef;
	private static MsgResponseManager msgResponseRef;
	ArrayList<ChatMessageModel> localPoolDataList = new ArrayList<ChatMessageModel>();
	LinkedHashMap<String, ChatMessageModel> localPoolData = new LinkedHashMap<String, ChatMessageModel>();
	LinkedHashMap<String, Integer> localPool = new LinkedHashMap<String, Integer>();
	
	private MsgResponseManager(Context contextRef){
        this.contextRef = contextRef;
    }

    public static synchronized MsgResponseManager initMsgResponseManager(Context contextRef) {      
        if (msgResponseRef == null) msgResponseRef = new MsgResponseManager(contextRef);
        return msgResponseRef;
    }
    
    public void clearLocalPool() {
    	localPool.clear();
	}
    
    public void clearLocalPool(String dummyMessageid) {
    	localPool.remove(dummyMessageid);
	}
    
    public void addLocalPool(String dummyMessageid, Integer listPosition) {
    	localPool.put(dummyMessageid, listPosition);
	}


    
    public Integer getPositionFromLocalPool(String dummyMessageid) {
		return localPool.get(dummyMessageid);
	}
    
    public void increaseLocalPoolValue() {
    	List<Entry<String, Integer>> indexedList = new ArrayList<Entry<String, Integer>>(localPool.entrySet());
    	clearLocalPool();
    	for (int i = 0; i < indexedList.size(); i++) {
    		Entry<String, Integer> entry = indexedList.get(i);
        	String key = entry.getKey();
        	Integer value = entry.getValue();
        	
        	addLocalPool(key, value + 1);
		}
	}
    
    public void clearLocalPoolData() {
    	localPoolData.clear();
	}
    
    public void clearLocalPoolData(String dummyMessageid) {
    	localPoolData.remove(dummyMessageid);
	}
    
    public void addLocalPoolData(LinkedHashMap<String, ChatMessageModel> unsentItemList) {
    	clearLocalPoolData();
    	localPoolData.putAll(unsentItemList);
	}
    
    public void addOneToLocalPoolData(String dummyMessageid, ChatMessageModel unsentItem) {
    	localPoolData.put(dummyMessageid, unsentItem);
	}

    
    public void clearLocalPoolDataList() {
    	localPoolDataList.clear();
	}
    

    
    public void addLocalPoolDataList() {
    	clearLocalPoolDataList();
    	localPoolDataList.addAll(new ArrayList<>(localPoolData.values()));
	}
    
    public ArrayList<ChatMessageModel> getLocalPoolDataList() {
    	addLocalPoolDataList();
		return localPoolDataList;
	}
}
