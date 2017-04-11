package com.example.chess;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.graphics.RectF;

public class chess {
	float x,y,w;
	int index,row,col,left,right;
	//有无棋子落下的状态
	int type=-1;
	//棋子的红框状态
	boolean islast=false;
	public chess(float x, float y, float w, int index, int row, int col,
			int left, int right) {
		super();
		this.x = x;
		this.y = y;
		this.w = w;
		this.index = index;
		this.row = row;
		this.col = col;
		this.left = left;
		this.right = right;
	}
	
	public void draw(Canvas canvas,Paint p){
		if(type==-1) return;
		float jg=+w*0.15f;
		canvas.drawBitmap(panel.imgs[type], new Rect(0, 0, panel.imgs[type].getWidth(),
				panel.imgs[type].getHeight()), new RectF(x+jg, y+jg, x+w-jg, y+w-jg), p);
		//绘制棋子的边框
		if(islast){
			p.setColor(0xffff0000);
			p.setStyle(Style.STROKE);
			p.setStrokeWidth(1);
			canvas.drawRect(x+jg, y+jg, x+w-jg, y+w-jg,p);
		    }
		}
	public boolean contains(float px,float py){
		return new RectF(x, y, x+w, y+w).contains(px,py);
	}
}
