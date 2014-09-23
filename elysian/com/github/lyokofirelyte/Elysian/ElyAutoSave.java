package com.github.lyokofirelyte.Elysian;

import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.github.lyokofirelyte.Divinity.Events.DivinityChannelEvent;
import com.github.lyokofirelyte.Divinity.Storage.DPI;
import com.github.lyokofirelyte.Elysian.MMO.MMO;

public class ElyAutoSave implements Runnable {
	
	private Elysian main;
	
	public ElyAutoSave(Elysian i){
		main = i;
	}
	
	@Override
	public void run(){
		
		long startTime = new Long(System.currentTimeMillis());
		
		for (ElySave saveClass : main.saveClasses.values()){
			saveClass.save();
		}
		
		for (Player p : Bukkit.getOnlinePlayers()){
			if (main.api.getDivPlayer(p).getBool(MMO.IS_MINING) || main.api.getDivPlayer(p).getBool(MMO.IS_DIGGING)){
				return;
			}
		}
		
		try {
			main.api.divManager.save();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		main.api.event(new DivinityChannelEvent("&6System", "wa.staff.intern", "&c&oOh! &4\u2744", "&7Auto-save complete (" + (System.currentTimeMillis()-startTime) + "ms)", "&c"));
	}
}