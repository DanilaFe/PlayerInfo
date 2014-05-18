package me.danilafe.playerviewer.plugin;

import java.io.IOException;
import java.util.logging.Logger;

import me.danilafe.playerviewer.httpserver.HTTPServer;

import org.bukkit.Bukkit;
import org.bukkit.configuration.Configuration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

public class PlayerInfo extends JavaPlugin{

	public Logger logger = Logger.getLogger("minecraft");
	public PluginDescriptionFile pdfile = this.getDescription();
	public Configuration config = this.getConfig();
	public HTTPServer http;
	public Player[] players;
	private Thread t = new Thread(){
		public void run(){
		 while(true){
			 try {
				players = Bukkit.getServer().getOnlinePlayers();
				Thread.sleep(20);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		 }
		}
	};
	
	public void onEnable(){
		t.start();
		http = new HTTPServer(4312, this);
		logger.info(pdfile.getName() + " has been enabled.");
		config.addDefault("Scoreboard", true);
		config.options().copyDefaults(true);
		saveConfig();
	}
	
	public void onDisable(){
		logger.info(pdfile.getName() + " has been disabled.");
		try {
			http.listener.stop();
			http.ss.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	

}
