package com.example.chess;

import android.R.integer;
import android.os.AsyncTask;

public class UpdateTime extends AsyncTask{
	int time,time1,time2;
	public boolean isrun=true;
	public int type = 0;
	panel panel;
	public UpdateTime(com.example.chess.panel panel) {
		super();
		this.panel = panel;
	}
	@Override
	protected Object doInBackground(Object... arg0) {
		while(isrun) {
		try{
			Thread.sleep(1000);
		}catch(InterruptedException e){
		}
			time++;
			if(type==0)time1++;
			else time2++;
			publishProgress();
		}
		return null;
	}
	@Override
	protected void onProgressUpdate(Object... values) {
		// TODO Auto-generated method stub
		panel.invalidate();
	}
	public void close() {
		isrun=false;
		try{
			finalize();
		}catch(Throwable e){
		}
	}
}
