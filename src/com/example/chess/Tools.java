package com.example.chess;

import java.text.DecimalFormat;

import android.R.integer;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Paint.Style;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

public class Tools {
	public static void drawText(Canvas c,RectF rec,String text,int fontsize,int color,int fontbold) {
		Paint p = new Paint();
		p.setColor(color);
		p.setStyle(Style.STROKE);
		p.setTextSize(fontsize);
		p.setStrokeWidth(fontbold);
		float w = fontsize*text.length();
		c.drawText(text, (rec.width()-w)/2+rec.left, rec.top+(rec.height()/2)+fontsize/2, p);
	}
	public static void drawTextA(Canvas c,RectF rec,String text,int fontsize,int color,int fontbold) {
		Paint p = new Paint();
		p.setColor(color);
		p.setStyle(Style.STROKE);
		p.setTextSize(fontsize);
		p.setStrokeWidth(fontbold);
		float w = fontsize*text.length()/2;
		c.drawText(text, (rec.width()-w)/2+rec.left, rec.top+(rec.height()/2)+fontsize/2, p);
	}
	//转换时间格式
	public static String gettimeStr(int time) {
		int s = time%60;
		int m = time/60%60;
		int h = time/3600%60;
		DecimalFormat f = new DecimalFormat("00");
		return f.format(h)+":"+f.format(m)+":"+f.format(s);
	}
	public static int getcolor(Context context,int Resid) {
		return context.getResources().getColor(Resid);
	}
	/**
	 * 获取本机ip
	 */
	public static String getWifiIp(Context context) {
		WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		if(!wifiManager.isWifiEnabled()){
			wifiManager.setWifiEnabled(true);
		}
		WifiInfo wifiInfo = wifiManager.getConnectionInfo();
		int ipAddress = wifiInfo.getIpAddress();
		return intToIp(ipAddress);
	}
	private static String intToIp(int i) {
		return (i & 0xFF) + "."+((i>>8) & 0xff)+"."+((i>>16) & 0xFF)+"."+(i>>24 & 0xFF);	}
	
}
