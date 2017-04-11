package com.example.chess;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketAddress;
import java.util.Arrays;


public class udpmanager implements Runnable {
private DatagramSocket sock;
private boolean isrun=true;
netListenter listenter;
public udpmanager(int port) throws Exception{
	sock=new DatagramSocket(port);
	new Thread(this).start();
}
	public void send(final String text, final String ip, final int port)
			throws Exception {
		new Thread() {
			public void run() {
				try {
					byte[] data = text.getBytes();
					DatagramPacket p = new DatagramPacket(data, data.length,
							InetAddress.getByName(ip), port);
					sock.send(p);
				} catch (IOException e) {
				}
			}
		}.start();
	}
@Override
public void run() {
	try {
	while(isrun){
		DatagramPacket p = new DatagramPacket(new byte[100], 100);
			sock.receive(p);
			byte[] data=Arrays.copyOf(p.getData(), p.getLength());
		if(listenter!=null)listenter.getmag(new String(data), p.getAddress(), p.getPort());
	}
	} catch (IOException e) {
	}
}
public void close(){
	isrun=false;
	sock.close();
}
interface netListenter{
public	void getmag(String str,InetAddress addr,int port);
	}
}
