package me.danilafe.playerviewer.httpserver;

import java.io.IOException;
import java.net.ServerSocket;

import me.danilafe.playerviewer.plugin.PlayerInfo;

public class HTTPServer {
	
	private HTTPServer me = this;
	public ServerSocket ss;
	public PlayerInfo parent;
	private Thread listener = new Thread(){
		public void run(){
			while(true){
				try {
					Connection c = new Connection(ss.accept(), me);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	};
	
	public HTTPServer(int port, PlayerInfo parent){
		this.parent = parent;
		try {
			ss = new ServerSocket(port);
			parent.logger.info("Activated Server Socket on port " + port);
		} catch (IOException e) {
			e.printStackTrace();
			parent.logger.severe("Failed while attempting to bind to port " + port);
		}
		
		listener.start();
	}
	
}
