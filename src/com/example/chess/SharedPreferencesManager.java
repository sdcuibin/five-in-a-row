package com.example.chess;

import java.util.ArrayList;

import android.R.integer;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class SharedPreferencesManager {
	/**
	 * 保存棋局
	 */
	public static void save(Context c,ArrayList<chess> list,int ... m){
		SharedPreferences s = c.getSharedPreferences("db",Context.MODE_PRIVATE );
		String types = "";
		for (int i = 0; i < list.size(); i++) {
			types+=list.get(i).type+"~";
		}
		Editor e = s.edit();
		e.putString("types", types);
		e.putInt("type", m[0]);
		e.putInt("lastindex", m[1]);
		e.commit();
	}
	/**
	 * 还原棋局
	 */
	public static SharedPreferences load(Context c,ArrayList<chess> list) {
		SharedPreferences s = c.getSharedPreferences("db",Context.MODE_PRIVATE );
		String []types = s.getString("types", "").split("~");
		for (int i = 0; i < list.size(); i++) {
			list.get(i).type=Integer.valueOf(types[i]);
		}
		return s;
	}
	//保存网络设置
	public static void saveconfig(Context c,String ... strs){
		SharedPreferences s = c.getSharedPreferences("net",Context.MODE_PRIVATE );
		Editor e = s.edit();
		e.putString("selfport", strs[0]);
		e.putString("destip", strs[1]);
		e.putString("destport", strs[2]);
		e.commit();
	}
	//加载网络设置
	public static SharedPreferences loadconfig(Context c) {
		SharedPreferences s = c.getSharedPreferences("net",Context.MODE_PRIVATE );
		return s;
	}
}
