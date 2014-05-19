package me.danilafe.playerviewer.plugin;

import java.io.IOException;
import java.util.HashMap;
import java.util.logging.Logger;

import me.danilafe.playerviewer.httpserver.HTTPServer;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.Configuration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

public class PlayerInfo extends JavaPlugin{

	public HashMap<String, String> alerts = new HashMap<String, String>();
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
	
	public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args){
		if(sender instanceof Player){
			Player p = (Player) sender;	
			if(commandLabel.equalsIgnoreCase("checker")){
				if(args.length > 0){
					if(args[0].equalsIgnoreCase("alert")){
						if(args.length > 2){
							String alert = args[2];
							for(int i = 3; i < args.length; i ++){
								alert += " "+ args[i];
							}
							alerts.put(args[1], alert);
						}
					} else if(args[0].equalsIgnoreCase("delalert")){
						if(args.length > 1){
							alerts.remove(args[1]);
						}
					}
				}
			}
		}

		
		return false;
	}
	

}
