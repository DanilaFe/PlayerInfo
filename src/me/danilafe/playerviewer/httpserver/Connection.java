package me.danilafe.playerviewer.httpserver;



import java.awt.List;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Set;

import org.json.simple.*;
import org.omg.CORBA.Request;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

public class Connection {
	
	protected HTTPServer parent;
	protected BufferedReader br;
	protected PrintStream ps;
	protected Socket s;
	protected ArrayList<String> args = new ArrayList<String>();
	protected Thread clientlisten = new Thread(){
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
	
	protected Connection(Socket sock, HTTPServer s){
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
	
	@SuppressWarnings("deprecation")
	protected void command(String command){
		if(command.startsWith("GET")){
			String[] pieces = command.split(" ");
			if(pieces[1].equals("/")){
				ps.println("HTTP/1.1 200 OK");
				ps.println("Date: " + Calendar.getInstance().getTime().toGMTString());
				ps.println("Content-Type:" + "text/html");
				ps.println("Connection: Closed");
				ps.println();
				
				String[] mainhtml1 = new String[]{
					"<!DOCTYPE HTML>",
					"<html>",
					"<head>",
					getInsideTag("title", "DanilaFe's Server Checker"),//TODO custom name
					"<meta http-equiv=\"refresh\" content=\"5\">",
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
				
				ArrayList<String> alerts =  new ArrayList<String>(parent.parent.alerts.values());
				for(String alert: alerts){
					ps.println(getInsideTag("div", alert, "alert"));
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
				ps.println("Connection: Closed");
				ps.println();
				
				BufferedReader cssread = new BufferedReader(new InputStreamReader(this.getClass().getClassLoader().getResourceAsStream("me/danilafe/playerviewer/styles/playerstyle.css")));
				String red;
				try {
					while((red = cssread.readLine()) != null){
						
						ps.println(red);
					}
					
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
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			else if(pieces[1].equals("/styles/info-style.css")){
				ps.println("HTTP/1.1 200 OK");
				ps.println("Date: " + Calendar.getInstance().getTime().toGMTString());
				ps.println("Content-Type:" + "text/css");
				ps.println("Connection: Closed");
				ps.println();
				
				BufferedReader cssread = new BufferedReader(new InputStreamReader(this.getClass().getClassLoader().getResourceAsStream("me/danilafe/playerviewer/styles/info-style.css")));
				String red;
				try {
					while((red = cssread.readLine()) != null){
						
						ps.println(red);
					}
					
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
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else if(pieces[1].startsWith("/player_")){
					Player p = parent.parent.getServer().getPlayer(pieces[1].replace("/player_", "").replace(".html", ""));
					if(p!=null){
						ps.println("HTTP/1.1 200 OK");
						ps.println("Date: " + Calendar.getInstance().getTime().toGMTString());
						ps.println("Content-Type:" + "text/html");
						ps.println("Connection: Closed");
						ps.println();
						String[] mainhtml1 = new String[]{
								"<!DOCTYPE HTML>",
								"<html>",
								"<head>",
								getInsideTag("title", p.getName()),
								"<link rel=\"stylesheet\" type=\"text/css\" href=\"styles/info-style.css\">",
								"<meta http-equiv=\"refresh\" content=\"5\">",
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
						printInfoElement("Hunger:", p.getFoodLevel() + "/" + "20");
						printInfoElement("Is allowed to fly?", Boolean.toString(p.getAllowFlight()));
						printInfoElement("XP Level:", Integer.toString(p.getLevel()));
						printInfoElement("Current world:", p.getWorld().getName());
						printInfoElement("Item in hand:", p.getItemInHand().getType().name().replace("_", "<br>"));
						printInfoElement("IP:", p.getAddress().getAddress().toString());
						
						if(parent.parent.config.getBoolean("Scoreboard")){
							ScoreboardManager manager = Bukkit.getScoreboardManager();
							Scoreboard board = manager.getMainScoreboard();
							Set<Objective> obj = board.getObjectives();
							for(Objective objective: obj){
								ps.println("<div class = \"infopiece\" >");
								ps.println(getInsideTag("h3", objective.getName(), "cent"));
								ps.println(getInsideTag("h4", "" + objective.getScore(p).getScore(), "cent"));
								ps.println("</div>");
								ps.println("</div>");
							}
							
							
						}

						
						ps.println("</body>");
						ps.println("</html>");
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
	
	protected String getInsideTag(String tag, String text){
		return "<" + tag + ">" + text + "</" + tag + ">";
	}
	
	protected String getInsideTag(String tag, String text, String classid){
		return "<" + tag + " " + "class=\"" + classid + "\"" + ">" + text + "</" + tag + ">";
	}
	
	protected void printInfoElement(String title, String content){
		ps.println("<div class = \"infopiece\" >");
		ps.println(getInsideTag("h2", title, "cent"));
		ps.println(getInsideTag("h3", content, "cent"));
		ps.println("</div>");
		ps.println("</div>");
	}


}
