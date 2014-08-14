package com.github.lyokofirelyte.Elysian.Events;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import com.github.lyokofirelyte.Divinity.DivinityUtils;
import com.github.lyokofirelyte.Divinity.Commands.DivCommand;
import com.github.lyokofirelyte.Divinity.Events.DivinityChannelEvent;
import com.github.lyokofirelyte.Divinity.JSON.JSONChatClickEventType;
import com.github.lyokofirelyte.Divinity.JSON.JSONChatExtra;
import com.github.lyokofirelyte.Divinity.JSON.JSONChatHoverEventType;
import com.github.lyokofirelyte.Divinity.JSON.JSONChatMessage;
import com.github.lyokofirelyte.Divinity.Storage.DPI;
import com.github.lyokofirelyte.Divinity.Storage.DivinityPlayer;
import com.github.lyokofirelyte.Elysian.Elysian;

public class ElyChat implements Listener {
	
	private Elysian main;
	
	public ElyChat(Elysian i){
		main = i;
		fillMap();
	}
	
	private Map<String, String> qc = new HashMap<String, String>();
	
	private void fillMap(){
		qc.put("mobs", "I have killed % monsters in total!");
		qc.put("words", "I have spoken % times in chat!");
		qc.put("break", "I have broken % blocks in total!");
		qc.put("place", "I have placed % blocks in total!");
		qc.put("died", "I have died % times!");
		qc.put("duels", "I have won % duels!");
		qc.put("coords", "My current coords are: %");
		qc.put("exp", "I currently have % exp.");
		qc.put("money", "I currently have % shinies.");
	}
	
	@DivCommand(name = "PM", aliases = {"tell", "pm", "msg", "message", "t", "r"}, desc = "Private Message Command", help = "/tell <player> <message>", min = 2, player = false)
	public void onPrivateMessage(CommandSender cs, String[] args, String cmd){

		DivinityPlayer dp = cs instanceof Player ? main.api.getDivPlayer((Player)cs) : main.api.getSystem();
		String sendTo = !cmd.equals("r") ? args[0] : dp.getDPI(DPI.PREVIOUS_PM);;
		String message = !cmd.equals("r") ? args[1] : args[0];
		int start = !cmd.equals("r") ? 2 : 1;
		
		for (int i = start; i < args.length; i++){
			message = message + " " + args[i];
		}
		
		if (main.doesPartialPlayerExist(sendTo) || sendTo.equals("console")){
			if (sendTo.equals("console") || main.isOnline(sendTo)){
				
				if (!sendTo.equals("console")){	
					Bukkit.getPlayer(main.matchDivPlayer(sendTo).uuid()).sendMessage(main.AS(("&3<- " + dp.getDPI(DPI.DISPLAY_NAME) + "&f: " + main.matchDivPlayer(sendTo).getDPI(DPI.PM_COLOR) + message)));
					cs.sendMessage(main.AS(("&3-> " + main.matchDivPlayer(sendTo).getDPI(DPI.DISPLAY_NAME)) + "&f: " + dp.getDPI(DPI.PM_COLOR) + message));
					main.matchDivPlayer(sendTo).setDPI(DPI.PREVIOUS_PM, dp.name());
				} else {
					Bukkit.getConsoleSender().sendMessage(main.AS(("&3<- " + dp.getDPI(DPI.DISPLAY_NAME) + "&f: " + message)));
					cs.sendMessage(main.AS(("&3-> " + "&6Console" + "&f: " + dp.getDPI(DPI.PM_COLOR) + message)));
					main.api.getSystem().setDPI(DPI.PREVIOUS_PM, dp.name());
					dp.setDPI(DPI.PREVIOUS_PM, "console");
				}
				
				dp.setDPI(DPI.PREVIOUS_PM, sendTo);
				
			} else {
				main.s(cs, "&c&oThat player is not online.");
			}
			
		} else {
			main.s(cs, "playerNotFound");
		}
	}
	
	@DivCommand(perm = "wa.staff.mod2", aliases = {"filter"}, desc = "Chat & Command Filter Command", help = "/filter <word> <replacement>. If it already has a filter it will be removed.", player = false, min = 2)
	public void onFilter(CommandSender cs, String[] args){
		
		DivinityPlayer system = main.api.getSystem();
		List<String> toRemove = new ArrayList<String>();
		String dispName = cs instanceof Player ? ((Player)cs).getDisplayName() : "&6Console";
		
		for (String filter : system.getListDPI(DPI.FILTER)){
			if (filter.split(" % ")[0].equalsIgnoreCase(args[0])){
				toRemove.add(filter);
			}
		}
		
		if (toRemove.size() <= 0){
			system.getListDPI(DPI.FILTER).add(args[0].toLowerCase() + " % " + args[1].toLowerCase());
			main.s(cs, "Added &6" + args[0].toLowerCase() + " &4-> &6" + args[1].toLowerCase());
			main.api.event(new DivinityChannelEvent("&6System", "wa.staff.intern", "&c&oOh! &4\u2744", dispName + " filtered &6" + args[0].toLowerCase() + " &4-> &6" + args[1].toLowerCase() + "&c!", "&c"));
		} else {
			for (String s : toRemove){
				system.getListDPI(DPI.FILTER).remove(s);
				main.s(cs, "Removed &6" + s.split(" % ")[0] + " &4-> &6" + s.split(" % ")[1]);
				main.api.event(new DivinityChannelEvent("&6System", "wa.staff.intern", "&c&oOh! &4\u2744", dispName + " un-filtered &6" + s.split(" % ")[0] + " &4-> &6" + s.split(" % ")[1] + "&c!", "&c"));
			}
		}
	}
	
	@DivCommand(aliases = {"qc", "quickchat"}, desc = "QuickChat Command", help = "/qc <option>, /qc list", player = true)
	public void onQC(Player p, String[] args){
		
		if (args.length == 0){
			main.s(p, main.help("qc", this));
		} else {
			switch (args[0]){
			
			}
		}
	}
	
	@EventHandler (priority = EventPriority.HIGHEST)
	public void onChat(final AsyncPlayerChatEvent e){
		
		if (e.isCancelled()){
			return;
		}
		
		e.setCancelled(true);
		main.afkCheck(e.getPlayer());

		if (!main.api.getDivPlayer(e.getPlayer()).getBoolDPI(DPI.MUTED)){

			new Thread(new Runnable(){ public void run(){
				
				for (Player p : Bukkit.getOnlinePlayers()){
					
					DivinityPlayer sendTo = main.api.getDivPlayer(p);
					DivinityPlayer sentFrom = main.api.getDivPlayer(e.getPlayer());
					String rawMsg = new String(e.getMessage());
					sendTo.setDPI(DPI.ELY, false);
					sentFrom.setDPI(DPI.ELY, false);
					
					if (sendTo.getBoolDPI(DPI.CHAT_FILTER_TOGGLE)){
						rawMsg = (filter(rawMsg));
					}
					
					String rankColor = sentFrom.getDPI(DPI.RANK_COLOR);
					String rankName = sentFrom.getDPI(DPI.RANK_NAME);
					String rankDesc = sentFrom.getDPI(DPI.RANK_DESC);
					String staffDesc = sentFrom.getDPI(DPI.STAFF_DESC);
					String staffColor = sentFrom.getDPI(DPI.STAFF_COLOR);
					String playerDesc = sentFrom.getDPI(DPI.PLAYER_DESC);
					String globalColor = sendTo.getDPI(DPI.GLOBAL_COLOR);
					
					JSONChatMessage msg = new JSONChatMessage("", null, null);
					
					JSONChatExtra extra = new JSONChatExtra(main.AS(staffColor + rankName + " "), null, null);
					extra.setHoverEvent(JSONChatHoverEventType.SHOW_TEXT, main.AS(staffDesc));
					msg.addExtra(extra);
					
					extra = new JSONChatExtra(main.AS(rankColor + "❅ "), null, null);
					extra.setHoverEvent(JSONChatHoverEventType.SHOW_TEXT, main.AS("&6" + rankDesc));
					msg.addExtra(extra);
					
					extra = new JSONChatExtra(main.AS(e.getPlayer().getDisplayName() + "&f:" + globalColor), null, null);
					extra.setHoverEvent(JSONChatHoverEventType.SHOW_TEXT, main.AS(playerDesc));
					extra.setClickEvent(JSONChatClickEventType.SUGGEST_COMMAND, "/tell " + e.getPlayer().getName() + " ");
					msg.addExtra(extra);
					
					for (String message : rawMsg.split(" ")){
						if (linkCheck(message)){
							extra = new JSONChatExtra(main.AS(" &6&o" + shorten(message) + globalColor), null, null);
							extra.setHoverEvent(JSONChatHoverEventType.SHOW_TEXT, main.AS("&7&oNavigate to URL"));
							extra.setClickEvent(JSONChatClickEventType.OPEN_URL, message);
						} else if (message.startsWith("cmd:")){
							extra = new JSONChatExtra(main.AS(" &6&o" + message.replace("cmd:", "").replace("_", " ") + globalColor), null, null);
							extra.setHoverEvent(JSONChatHoverEventType.SHOW_TEXT, main.AS("&7&oRun Command /" + message.replace("cmd:", "").replace("_", " ")));
							extra.setClickEvent(JSONChatClickEventType.RUN_COMMAND, "/" + message.replace("cmd:", "").replace("_", " "));
						} else {
							extra = new JSONChatExtra(main.AS(" " + message), null, null);
							extra.setHoverEvent(JSONChatHoverEventType.SHOW_TEXT, main.AS("&7&oSearch google for " + message));
							extra.setClickEvent(JSONChatClickEventType.OPEN_URL, "https://www.google.com/search?q=" + message);
						}
						msg.addExtra(extra);
					}
					msg.sendToPlayer(p);
				}
				
				Bukkit.getConsoleSender().sendMessage(main.AS(e.getPlayer().getDisplayName() + "&f: " + e.getMessage()));
				
			}}).start();
		} else {
			main.s(e.getPlayer(), "muted");
		}
	}
	
	private String filter(String msg){

		msg = msg.replace("place", "pLace").replace("&k", "");

    	for (String filter : main.api.getSystem().getListDPI(DPI.FILTER)){
    		if (ChatColor.stripColor(DivinityUtils.AS(msg.toLowerCase())).contains(filter.split(" % ")[0])){
    			msg = msg.replace(filter.split(" % ")[0], filter.split(" % ")[1]);
    		}
    	}
    	
		return msg;
	}
	
	private boolean linkCheck(String msg){
	  	if ((msg.contains("http://") || msg.contains("https://")) && !msg.contains("tinyurl") && !msg.contains("bit.ly")){	
	  		return true;
	  	}
	  	return false;
	}
	
	public static String shorten(String URL){
			
		String link = "";
			
		if (!URL.startsWith("http")){
			URL = "http://" + URL;
		}
			
		int error = 0;
		
		try {
			URL url = new URL("http://www.tinyurl.com/api-create.php?url=" + URL);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("GET");
			
			if (connection.getResponseCode() != 200){
				error = connection.getResponseCode();
				throw new RuntimeException("Failed to shorten link. HTTP error code: " + error);
			} else {
				BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
				link = reader.readLine();
			}
				
			connection.disconnect();
				
		} catch (Exception e){
			link = "&4&l(&cHTTP Error: " + error + "&4&l)&r";
		}
		return link;
	}
}