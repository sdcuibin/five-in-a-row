package com.example.chess;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.LinkedList;

import android.R.integer;
import android.R.string;
import android.app.AlertDialog;
import android.app.LauncherActivity.ListItem;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.drawable.Animatable;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Message;
import android.text.style.BackgroundColorSpan;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.SimpleAdapter.ViewBinder;
import android.widget.Toast;

public class panel extends View implements View.OnTouchListener {
	static panel self;
	boolean isclick = true;
	LinkedList<chess> recodes = new LinkedList<chess>();
	// Bitmap b;//设置变量b
	boolean isnet = false;
	float linecount = 12;// 设置12条线（棋盘）
	ArrayList<chess> list = new ArrayList<chess>();
	ArrayList<Button> bts = new ArrayList<Button>();
	float optop;
	float optheight;
	UpdateTime uTime = new UpdateTime(this);
	int type = 0;
	chess lastitem;
	int win1, win2;
	// bitmap位图
	static Bitmap[] imgs = new Bitmap[2];
	public panel(Context context, AttributeSet attrs) {
		super(context, attrs);
		setOnTouchListener(this);
		imgs[0] = BitmapFactory.decodeResource(getResources(), R.drawable.q1);// 获取黑棋
		imgs[1] = BitmapFactory.decodeResource(getResources(), R.drawable.q0);// 获取白棋
		// b=BitmapFactory.decodeResource(context.getResources(),
		// R.drawable.ic_launcher);//加载drawable中的图片
		float cellwidth = MainActivity.width / linecount;
		optop = MainActivity.width;
		optheight = (MainActivity.height - optop) / 6;
		for (int i = 0; i < linecount * linecount; i++) {
			int row = i / (int) linecount;
			int col = i % (int) linecount;
			list.add(new chess((col * cellwidth), (row * cellwidth), cellwidth,
					i, row, col, row + col, col - row));
		}
		bts.add(new Button(5, optop + optheight * 3,
				(MainActivity.width - 10) / 2, optheight, "开始游戏"));
		bts.add(new Button(5, optop + optheight * 4,
				(MainActivity.width - 10) / 2, optheight, "保存棋局"));
		bts.add(new Button(5, optop + optheight * 5,
				(MainActivity.width - 10) / 2, optheight, "悔棋"));
		bts.add(new Button(MainActivity.width / 2, optop + optheight * 3,
				(MainActivity.width - 10) / 2, optheight, "结束游戏"));
		bts.add(new Button(MainActivity.width / 2, optop + optheight * 4,
				(MainActivity.width - 10) / 2, optheight, "读取棋局"));
		bts.add(new Button(MainActivity.width / 2, optop + optheight * 5,
				(MainActivity.width - 10) / 2, optheight, "网络设置"));
	}

	@Override
	public boolean onTouch(View v, MotionEvent e) {
		float x = e.getX();
		float y = e.getY();
		// Toast.makeText(getContext(),x,0).show();
		for (int i = 0; i < linecount * linecount; i++) {
			if (list.get(i).contains(x, y)) {
				if(isnet&&isclick) return false;
				if (list.get(i).type != -1)
					return false;
				if (lastitem != null)
					lastitem.islast = false;
				list.get(i).type = type;
				list.get(i).islast = true;
				lastitem = list.get(i);
				if(!isnet){
				recodes.push(list.get(i));
				type = type == 0 ? 1 : 0;
				uTime.type = type;
				}
				else{
					uTime.type = type == 0 ? 1 : 0;
					isclick=false;
				}
				send(""+list.get(i).index);
				// 强制重绘
				invalidate();
				boolean iswin = false;
				iswin = check(list.get(i), 1);
				if (!iswin)
					iswin = check(list.get(i), 2);
				if (!iswin)
					iswin = check(list.get(i), 3);
				if (!iswin)
					iswin = check(list.get(i), 4);
				if (iswin) {
					// Toast.makeText(getContext(),
					// (list.get(i).type==1?"白方":"黑方")+"赢了", 1).show();
					send("c");
					showwin(list.get(i).type);
				}
				break;
			}
		}
		// 设置按钮功能
		for (int i = 0; i < 6; i++) {
			if (bts.get(i).contains(x, y)) {
				switch (i) {
				case 0:
					initgame();
					break;
				case 1:
					SharedPreferencesManager.save(getContext(), list, type,
							lastitem != null ? lastitem.index : -1);
					Toast.makeText(getContext(), "保存成功", 0).show();
					break;
				case 2:
					back();
					break;
				case 3:
					System.exit(0);
					break;
				case 4:
					SharedPreferences s = SharedPreferencesManager.load(
							getContext(), list);
					type = s.getInt("type", 0);
					int lastindex = s.getInt("lastindex", 0);
					if (lastindex != -1) {
						lastitem = list.get(lastindex);
						lastitem.islast = true;
					}
					invalidate();
					break;
				case 5:
					shownetconfig();
					break;
				}
			}
		}
		return false;
	}
	EditText selfport;
	EditText destip;
	EditText destport;
	// 设置网络
	private void shownetconfig() {
		// TODO Auto-generated method stub
		AlertDialog.Builder b = new AlertDialog.Builder(getContext());
		b.setTitle("网络设置: 本机Ip:"+Tools.getWifiIp(getContext()));
		View v = View.inflate(getContext(), R.layout.netconfig, null);
		selfport = (EditText)v.findViewById(R.id.EditText02);
		destip = (EditText)v.findViewById(R.id.EditText01);
		destport = (EditText)v.findViewById(R.id.editText1);
		SharedPreferences s = SharedPreferencesManager.loadconfig(getContext());
		//回显与设置默认值
		selfport.setText(s.getString("selfport", "12345"));
		destip.setText(s.getString("despip", "192.168.0.1"));
		destport.setText(s.getString("destport", "12345"));
		b.setView(v);
		b.setPositiveButton("确定", new okbutton());
		b.setNeutralButton("取消", new cancelbutton());
		b.setCancelable(false);
		b.create().show();
	}
	class okbutton implements android.content.DialogInterface.OnClickListener {
		@Override
		public void onClick(DialogInterface dialog, int which) {
			// TODO Auto-generated method stub
			SharedPreferencesManager.saveconfig(getContext(),selfport.getText().toString(),
			destip.getText().toString(),destport.getText().toString());
			isnet = true;
			invalidate();
		}
	}
	class cancelbutton implements
			android.content.DialogInterface.OnClickListener {
		@Override
		public void onClick(DialogInterface dialog, int which) {
			// TODO Auto-generated method stub
			if(net!=null){
			net.close();
			net=null;
			}
			isnet = false;
			invalidate();
		}
	}

	// 游戏结束对话框
	private void showwin(int type) {
		// TODO Auto-generated method stub
		AlertDialog.Builder b = new AlertDialog.Builder(getContext());
		b.setTitle("游戏结束");
		b.setMessage((type == 1 ? "白方" : "黑方") + "赢了");
		b.setPositiveButton("新一局", new newbutton(type));
		b.setNeutralButton("退出", new editbutton());
		b.setCancelable(false);
		b.create().show();

	}

	class newbutton implements android.content.DialogInterface.OnClickListener {
		int type;
		public newbutton(int type) {
			super();
			this.type = type;
		}
		@Override
		public void onClick(DialogInterface arg0, int arg1) {
			// TODO Auto-generated method stub
			if (type == 0)
				win1++;
			else
				win2++;
			if(isnet)restartgame();
				else {
				initgame();

			}
		}
		
	}
	class editbutton implements android.content.DialogInterface.OnClickListener {

		@Override
		public void onClick(DialogInterface arg0, int arg1) {
			// TODO Auto-generated method stub
			send("d");
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			net.close();
			System.exit(0);
		}
	}
	private void restartgame() {
		// TODO Auto-generated method stub
		send("e");
		init();
		isclick=true;
		lastitem=null;
	}
	/**
	 * 悔棋
	 */
	private void back() {
		if (recodes.size() == 0)
			return;
		chess c = recodes.pop();
		c.type = -1;
		type = type == 0 ? 1 : 0;
		if (recodes.size() > 0) {
			c = recodes.getFirst();
			c.islast = true;
			lastitem = c;
		}
		invalidate();
	}

	/**
	 * 开始游戏
	 */
	private void initgame() {
		if(isnet){
			netinit();
			return;
		}
		init();
	}
	private void init() {
		// TODO Auto-generated method stub
		recodes.clear();
		for (int i = 0; i < linecount * linecount; i++) {
			list.get(i).type = -1;
			type = 0;
			uTime.close();
			invalidate();
			uTime = new UpdateTime(this);
			uTime.execute();
		}
	}
	udpmanager net;
	/**
	 * 网络初始化
	 */
	ProgressDialog pd;
	private void netinit() {
		if (net != null) {
			net.close();
			net = null;
		}
		new Thread() {
			public void run() {
				try {
					net = new udpmanager(Integer.valueOf(selfport.getText()
							.toString()));
					net.listenter=listenter;
					send("a");
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}.start();
		pd = new ProgressDialog(getContext());
		pd.setMessage("网络正在连接，请稍候");
		pd.setCancelable(false);
		pd.show();
	}
	udpmanager.netListenter listenter = new udpmanager.netListenter() {
		@Override
		public void getmag(String str, InetAddress addr, int port) {
			// TODO Auto-generated method stub
			Message m = Message.obtain();
			m.obj = str;
			nethandler.sendMessage(m);
		}
	};
	Handler nethandler = new Handler(new Handler.Callback() {
		@Override
		public boolean handleMessage(Message arg0) {
			// TODO Auto-generated method stub
			String str = arg0.obj.toString();
			if(str.equals("a")){
				init();
				pd.dismiss();
				send("b");
				
			}else if (str.equals("b")) {
				init();
				pd.dismiss();
				isclick=false;
				type=1;
			}else if (str.equals("c")) {
				showwin(type==0?1:0);
			}else if (str.equals("d")) {
				if(pd!=null&&pd.isShowing())pd.dismiss();
				net.close();
				net=null;
				isnet=false;
				win1=win2=0;
				init();
				Toast.makeText(getContext(), "对方已退出", 1).show();
				
			}else if (str.equals("e")) {
				listenter=null;
				isclick=false;
				init();
				type=1;
			}
			else {
				uTime.type=type;
				if(lastitem!=null)
					lastitem.islast=false;
				int pos = Integer.valueOf(str);
				list.get(pos).type = type==0?1:0;
				list.get(pos).islast=true;
				lastitem=list.get(pos);
				isclick=true;
				invalidate();
			}
			return false;
		}

		
	});
	private void send(String str) {
		// TODO Auto-generated method stub
		try {
			net.send(str, destip.getText().toString(), Integer.valueOf(destport.getText().toString()));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/**
	 * 检查是否5个连一线
	 * 
	 */
	private boolean check(chess chess, int fx) {
		int count = 0;
		for (int i = 0; i < linecount * linecount; i++) {
			switch (fx) {
			case 1:
				if (list.get(i).row != chess.row)
					continue;
				break;
			case 2:
				if (list.get(i).col != chess.col)
					continue;
				break;
			case 3:
				if (list.get(i).left != chess.left)
					continue;
				break;
			case 4:
				if (list.get(i).right != chess.right)
					continue;
				break;
			}
			if (list.get(i).type == chess.type)
				count++;
			else
				count = 0;
			if (count == 5)
				return true;
		}
		return false;
	}

	@Override
	public void draw(Canvas canvas) {

		Paint p = new Paint();
		canvas.drawColor(Tools.getcolor(getContext(), R.color.backcolor));
		p.setColor(Tools.getcolor(getContext(), R.color.buttonbordercolor));
		p.setStyle(Style.STROKE);
		p.setStrokeWidth(2);

		// 绘制棋盘下方区域方格
		canvas.drawRect(5, optop, getWidth() - 5, optop + optheight * 3, p);
		canvas.drawLine(5, optop + optheight * 2, getWidth() - 5, optop
				+ optheight * 2, p);
		canvas.drawLine(getWidth() / 3, optop, getWidth() / 3, optop
				+ optheight * 3, p);
		canvas.drawLine(getWidth() / 3 * 2, optop, getWidth() / 3 * 2, optop
				+ optheight * 3, p);
		canvas.drawLine(getWidth() / 3, optop + optheight, getWidth() / 3 * 2,
				optop + optheight, p);
		canvas.drawLine(getWidth() / 2, optop + optheight, getWidth() / 2,
				optop + optheight * 2, p);

		// 绘制文字
		Tools.drawText(canvas, new RectF(getWidth() / 3, optop,
				getWidth() / 3 * 2, optop + optheight),
				isnet ? "网络模式" : "单机模式", 30, Tools.getcolor(getContext(),
						R.color.fontcolor), 3);
		Tools.drawTextA(canvas, new RectF(5, optop + optheight * 2,
				getWidth() / 3, optop + optheight * 3), Tools
				.gettimeStr(uTime.time1), 20, Tools.getcolor(getContext(),
						R.color.fontcolor), 2);
		Tools.drawTextA(canvas, new RectF(getWidth() / 3,
				optop + optheight * 2, getWidth() / 3 * 2, optop + optheight
						* 3), Tools.gettimeStr(uTime.time), 20, Tools.getcolor(
				getContext(), R.color.fontcolor), 2);
		Tools.drawTextA(canvas, new RectF(getWidth() / 3 * 2, optop + optheight
				* 2, getWidth(), optop + optheight * 3),
				Tools.gettimeStr(uTime.time2), 20,
				Tools.getcolor(getContext(), R.color.fontcolor), 2);
		Tools.drawTextA(canvas, new RectF(getWidth() / 3, optop + optheight,
				getWidth() / 2, optop + optheight * 2), "" + win1, 30, Tools
				.getcolor(getContext(), R.color.fontcolor), 3);
		Tools.drawTextA(canvas, new RectF(getWidth() / 2, optop + optheight,
				getWidth() / 3 * 2, optop + optheight * 2), "" + win2, 30,
				Tools.getcolor(getContext(), R.color.fontcolor), 3);

		// 画棋子标示背景
		p.setColor(Tools.getcolor(getContext(),
				R.color.selectcolor));
		p.setStyle(Style.FILL);
		float cw = getWidth() / 3 > optheight * 2 ? optheight * 2 * 0.7f
				: getWidth() / 3 * 0.7f;
		if (type == 0)
			canvas.drawRect(5, optop, getWidth() / 3, optop + optheight * 2, p);
		else
			canvas.drawRect(getWidth() / 3 * 2, optop, getWidth() - 5, optop
					+ optheight * 2, p);
		// 画棋子标示
		float pw = getWidth() / 15;
		canvas.drawBitmap(
				panel.imgs[0],
				new Rect(0, 0, panel.imgs[0].getWidth(), panel.imgs[0]
						.getHeight()), new RectF((getWidth() / 3 - cw) / 2,
						optop + (optheight * 2 - cw) / 2, getWidth() / 6 + cw
								/ 2, optop + optheight + cw / 2), p);
		canvas.drawBitmap(
				panel.imgs[1],
				new Rect(0, 0, panel.imgs[1].getWidth(), panel.imgs[1]
						.getHeight()),
				new RectF(getWidth() / 3 * 2 + (getWidth() / 3 - cw) / 2, optop
						+ (optheight * 2 - cw) / 2,
						getWidth() / 6 * 5 + cw / 2, optop + optheight + cw / 2),
				p);
		
		float cellwidth = getWidth() / linecount;
		p.setColor(Tools.getcolor(getContext(), R.color.linecolor));
		for (int i = 0; i < linecount; i++) {
			
			canvas.drawLine(cellwidth / 2, cellwidth / 2 + i * cellwidth,
					cellwidth * (linecount - 0.5f), cellwidth / 2 + i
							* cellwidth, p);
			canvas.drawLine(cellwidth / 2 + i * cellwidth, cellwidth / 2,
					cellwidth / 2 + i * cellwidth, cellwidth
							* (linecount - 0.5f), p);
		}
		for (int i = 0; i < linecount * linecount; i++)
			list.get(i).draw(canvas, p);
		for (int i = 0; i < 6; i++)
			bts.get(i).draw(canvas, p);
	}
}
  