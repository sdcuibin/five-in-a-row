package com.example.chess;

import android.R.string;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Paint.Style;

public class Button {
	float x,y,h,w;
	String text;
	public Button(float x, float y,float w, float h,String text) {
		super();
		this.x = x;
		this.y = y;
		this.w = w;
		this.h = h;
		this.text = text;
	}
	public void draw(Canvas canvas,Paint p){
		p.setColor(0xff648890);
		p.setStyle(Style.FILL);
		canvas.drawRect(x, y,x+w,y+h,p);
		p.setStyle(Style.STROKE);
		p.setColor(0xff44ff44);
		p.setStrokeWidth(3);
		canvas.drawRect(x, y,x+w,y+h,p);
		Tools.drawText(canvas, new RectF(x,y,x+w,y+h), text, 20, 0xff411341, 2);
	}
	public boolean contains(float px,float py){
		return new RectF(x, y, x+w, y+h).contains(px,py);
	}
}
