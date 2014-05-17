package me.danilafe.playerviewer.httpserver;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Calendar;

import org.bukkit.entity.Player;

public class Connection {
	
	public HTTPServer parent;
	public BufferedReader br;
	public PrintStream ps;
	public Socket s;
	public ArrayList<String> args = new ArrayList<String>();
	public Thread clientlisten = new Thread(){
		public void run(){
			String st;
			try {
				while((st = br.readLine()) != null){
					if(st.equals("")) break;
					args.add(st);
				}
				
				if(args.size() > 0){
					command(args.get(0));
				}
			} catch (IOException e) {

			}
		}
	};
	
	public Connection(Socket sock, HTTPServer s){
		parent = s;
		this.s = sock;
		try{
			ps = new PrintStream(this.s.getOutputStream());
			br = new BufferedReader(new InputStreamReader(this.s.getInputStream()));
		} catch(IOException e){
			s.parent.logger.severe("Error trying to initialize means of communication.");
		}
		
		clientlisten.start();
		
		
	}
	
	public void command(String command){
		if(command.startsWith("GET")){
			String[] pieces = command.split(" ");
			if(pieces[1].equals("/")){
				ps.println("HTTP/1.1 200 OK");
				ps.println("Date: " + Calendar.getInstance().getTime().toGMTString());
				ps.println("Content-Type:" + "text/html");
				ps.println();
				
				String[] mainhtml1 = new String[]{
					"<!DOCTYPE HTML>",
					"<html>",
					"<head>",
					getInsideTag("title", "DanilaFe's Server Checker"),//TODO custom name
					"<link rel=\"stylesheet\" type=\"text/css\" href=\"styles/playerstyle.css\">",
					"</head>",
					"<body>",
					"<div class = \"parent\">",
					getInsideTag("h1", "Players online right now:"),
					getInsideTag("h3", "Click the player card to view more info!")
				};
				
				String[] mainhtml2 = new String[]{
						"</div>",
						"</body>",
						"</html>"
				};
				
				for(String s: mainhtml1){
					ps.println(s);
				}
				
				
					
					for(Player p: parent.parent.players){
						ps.println("<div class=\"player\" onclick=\"window.open('player_" + p.getName() + ".html','Google!')\">");
						ps.println(getInsideTag("h2", p.getName(), "center"));
						ps.println("<img class = \"img\" src = \"https://minotar.net/avatar/" + p.getName() + "\">");
						ps.println(getInsideTag("h3", "Is OP?", "center"));
						ps.println(getInsideTag("p", Boolean.toString(p.isOp()), "center"));
						ps.println(getInsideTag("h3", "Gamemode:", "center"));
						switch(p.getGameMode()){
						case SURVIVAL:
							ps.println(getInsideTag("p", "Survivial", "center"));
							break;
						case CREATIVE:
							ps.println(getInsideTag("p", "Creative", "center"));
							break;
						case ADVENTURE:
							ps.println(getInsideTag("p", "Adventure", "center"));
							break;
						}
						ps.println("</div>");
					}

				
					

				
				for(String s: mainhtml2){
					ps.println(s);
				}
				
				ps.close();
				try {
					s.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else if(pieces[1].equals("/styles/playerstyle.css")){
				ps.println("HTTP/1.1 200 OK");
				ps.println("Date: " + Calendar.getInstance().getTime().toGMTString());
				ps.println("Content-Type:" + "text/css");
				ps.println();
				
				BufferedReader cssread = new BufferedReader(new InputStreamReader(this.getClass().getClassLoader().getResourceAsStream("me/danilafe/playerviewer/styles/playerstyle.css")));
				String red;
				try {
					while((red = cssread.readLine()) != null){
						
						ps.println(red);
					}
					
					ps.close();
					try {
						s.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
					try {
						br.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			else if(pieces[1].equals("/styles/info-style.css")){
				ps.println("HTTP/1.1 200 OK");
				ps.println("Date: " + Calendar.getInstance().getTime().toGMTString());
				ps.println("Content-Type:" + "text/css");
				ps.println();
				
				BufferedReader cssread = new BufferedReader(new InputStreamReader(this.getClass().getClassLoader().getResourceAsStream("me/danilafe/playerviewer/styles/info-style.css")));
				String red;
				try {
					while((red = cssread.readLine()) != null){
						
						ps.println(red);
					}
					
					ps.close();
					try {
						s.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
					try {
						br.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else if(pieces[1].startsWith("/player_")){
					Player p = parent.parent.getServer().getPlayer(pieces[1].replace("/player_", "").replace(".html", ""));
					if(p!=null){
						ps.println("HTTP/1.1 200 OK");
						ps.println("Date: " + Calendar.getInstance().getTime().toGMTString());
						ps.println("Content-Type:" + "text/html");
						ps.println();
						String[] mainhtml1 = new String[]{
								"<!DOCTYPE HTML>",
								"<html>",
								"<head>",
								getInsideTag("title", p.getName()),
								"<link rel=\"stylesheet\" type=\"text/css\" href=\"styles/info-style.css\">",
								"</head>",
								"<body>",
								"<div class = \"info\">",
								getInsideTag("h1", "Profile of " + p.getName()),
							};
						for(String s: mainhtml1){
							ps.println(s);
						}
						
						ps.println("<div class = \"infopiece\" >");
						ps.println(getInsideTag("h2", "Profile Picture:", "cent"));
						ps.println("<img class = \"cent\" src=\"https://minotar.net/avatar/" + p.getName() + "/100\">");
						ps.println("</div>");
						ps.println("</div>");
						
						printInfoElement("Health:", p.getHealth() + "/" + "20");
						printInfoElement("Is allowed to fly?", Boolean.toString(p.getAllowFlight()));
						printInfoElement("XP Level:", Integer.toString(p.getLevel()));
						
						ps.println("</body>");
						ps.println("</html>");
						
						ps.close();
						try {
							s.close();
						} catch (IOException e) {

							e.printStackTrace();
						}
						try {
							br.close();
						} catch (IOException e) {

							e.printStackTrace();
						}
					} else {
						ps.println("HTTP/1.1 404 Not Found");
						ps.println("Date: " + Calendar.getInstance().getTime().toGMTString());
						ps.println("Content-Type:" + "text/html");
						ps.println();
						
						ps.close();
						try {
							s.close();
						} catch (IOException e) {

							e.printStackTrace();
						}
						try {
							br.close();
						} catch (IOException e) {

							e.printStackTrace();
						}
					}

			}
		}
	}
	
	public String getInsideTag(String tag, String text){
		return "<" + tag + ">" + text + "</" + tag + ">";
	}
	
	public String getInsideTag(String tag, String text, String classid){
		return "<" + tag + " " + "class=\"" + classid + "\"" + ">" + text + "</" + tag + ">";
	}
	
	public void printInfoElement(String title, String content){
		ps.println("<div class = \"infopiece\" >");
		ps.println(getInsideTag("h2", title, "cent"));
		ps.println(getInsideTag("h3", content, "cent"));
		ps.println("</div>");
		ps.println("</div>");
	}

}
