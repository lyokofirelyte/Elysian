package com.github.lyokofirelyte.Elysian.MMO.Magics;

import org.bukkit.entity.Player;

import com.github.lyokofirelyte.Divinity.Commands.DivCommand;
import com.github.lyokofirelyte.Elysian.Elysian;
import com.github.lyokofirelyte.Elysian.Gui.GuiLunarSpells;
import com.github.lyokofirelyte.Elysian.Gui.GuiSolarSpells;
import com.github.lyokofirelyte.Elysian.MMO.ElyMMO;

public class SpellCommand {
	
	private Elysian main;

	public SpellCommand(Elysian i) {
		main = i;
	}

	@DivCommand(aliases = {"solar"}, desc = "Open the solar spellbook", help = "/solar", player = true)
	public void onSolar(Player p, String[] args){
		main.invManager.displayGui(p, new GuiSolarSpells(main));
	}
	
	@DivCommand(aliases = {"lunar"}, desc = "Open the lunar spellbook", help = "/lunar", player = true)
	public void onLunar(Player p, String[] args){
		main.invManager.displayGui(p, new GuiLunarSpells(main));
	}
}