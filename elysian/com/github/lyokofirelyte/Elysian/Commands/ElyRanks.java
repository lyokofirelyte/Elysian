package com.github.lyokofirelyte.Elysian.Commands;

import org.bukkit.FireworkEffect.Type;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.github.lyokofirelyte.Divinity.DivinityUtils;
import com.github.lyokofirelyte.Divinity.Commands.DivCommand;
import com.github.lyokofirelyte.Divinity.JSON.JSONChatExtra;
import com.github.lyokofirelyte.Divinity.JSON.JSONChatHoverEventType;
import com.github.lyokofirelyte.Divinity.JSON.JSONChatMessage;
import com.github.lyokofirelyte.Divinity.Storage.DPI;
import com.github.lyokofirelyte.Divinity.Storage.DivinityPlayer;
import com.github.lyokofirelyte.Elysian.Elysian;

public class ElyRanks {

	private Elysian main;
	
	public ElyRanks(Elysian i){
		main = i;
	}
	
	@DivCommand(aliases = {"rankup"}, desc = "Elysian Rankup Command", help = "/rankup", player = true)
	public void onRankup(Player p, String[] args){
		
		DivinityPlayer dp = main.api.getDivPlayer(p);
		
		if (dp.getListDPI(DPI.PERMS).contains("wa.rank.immortal")){
			main.s(p, "&c&oYou are currently the highest rank!");
			return;
		}
		
		if (dp.getDPI(DPI.RANK_NAME).equalsIgnoreCase("Guest")){
			
			DivinityUtils.bc(p.getDisplayName() + " &bis now a member of WA!");
			dp.setDPI(DPI.RANK_NAME, "M");
			dp.setDPI(DPI.RANK_COLOR, "&7");
			dp.setDPI(DPI.RANK_DESC, "&7&oA registered member of the server!\n&6/home, build access.");
			dp.setDPI(DPI.STAFF_DESC, "&7&oA registered member!");
			
		} else {
		
			for (String s : main.perms.memberGroups){
				if (!dp.getListDPI(DPI.PERMS).contains("wa.rank." + s) && !s.equals("member")){
					
					String[] rank = main.perms.rankNames.get(s).split(" % ");
					
					if (dp.getIntDPI(DPI.BALANCE) >= Integer.parseInt(rank[1].replace("k", "000").replace("m", "000000"))){
						dp.getListDPI(DPI.PERMS).add("wa.rank." + s);
						dp.setDPI(DPI.RANK_COLOR, rank[0]);
						dp.setDPI(DPI.RANK_DESC, rank[0] + s.substring(0, 1).toUpperCase() + s.substring(1) + "\n" + "&6" + rank[3].replace(", ", "&7, &6"));
						dp.setDPI(DPI.RANK_NAME, !main.silentPerms(p, "wa.staff.intern") ? s.substring(0, 1).toUpperCase() : dp.getDPI(DPI.RANK_NAME));
						DivinityUtils.bc(p.getDisplayName() + " &bhas been promoted to &6" + s.substring(0, 1).toUpperCase() + s.substring(1) + "&b!");
						main.fw(p.getWorld(), p.getLocation(), Type.BURST, main.api.divUtils.getRandomColor());
						main.s(p, "&3&oNew Unlocks:");
						main.s(p, "Sunday percentage gain increased to: &6" + rank[2] + "%");
						main.s(p, "Access to: &6" + rank[3].replace(", ", "&7, &6"));
					} else {
						main.s(p, "&c&oInsufficient funds! You need &6&o" + rank[1]);
					}
					
					break;
				}
			}
		}
	}
	
	@DivCommand(aliases = {"ranks"}, desc = "Ranks Command", help = "/ranks", player = true)
	public void onRanks(Player p, String[] args){
		
		for (String s : main.perms.memberGroups){
			String[] rank = main.perms.rankNames.get(s).split(" % ");
			JSONChatMessage message = new JSONChatMessage("", null, null);
			JSONChatExtra extra = new JSONChatExtra(main.AS(rank[0] + s.substring(0, 1).toUpperCase() + s.substring(1) + " &3- &c" + rank[1] + " &3- &2" + rank[2] + "%"), null, null);
			extra.setHoverEvent(JSONChatHoverEventType.SHOW_TEXT, main.AS("&7&o" + rank[3]));
			message.addExtra(extra);
			message.sendToPlayer(p);
		}
		main.s(p, "&7&oHover for reward information.");
	}
}