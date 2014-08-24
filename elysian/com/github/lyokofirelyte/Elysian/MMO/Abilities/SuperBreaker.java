package com.github.lyokofirelyte.Elysian.MMO.Abilities;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.github.lyokofirelyte.Divinity.Storage.DRF;
import com.github.lyokofirelyte.Divinity.Storage.DivinityPlayer;
import com.github.lyokofirelyte.Divinity.Storage.ElySkill;
import com.github.lyokofirelyte.Elysian.Elysian;
import com.github.lyokofirelyte.Elysian.MMO.ElyMMO;
import com.github.lyokofirelyte.Elysian.MMO.MMO;

public class SuperBreaker extends ElyMMO {

	public SuperBreaker(Elysian i) {
		super(i);
	}

	public void r(Player p, DivinityPlayer dp){
		dp.set(MMO.IS_SUPER_BREAKING, !dp.getBool(MMO.IS_SUPER_BREAKING));
		dp.s("Super breaker " + (dp.getBool(MMO.IS_SUPER_BREAKING) + "").replace("true", "&aactive! Left click a block to get rekt!").replace("false", "&cinactive."));
	}

	public void l(Player p, DivinityPlayer dp, Block b){
		
		String result = main.pro.isInAnyRegion(b.getLocation());
		
		if (main.pro.hasFlag(result, DRF.BLOCK_BREAK)){
			if (!main.pro.hasRegionPerms(p, result)){
				dp.err("No permissions for this area!");
				return;
			}
		}
		
		if (dp.getLong(MMO.SUPER_BREAKER_CD) <= System.currentTimeMillis()){
			dp.set(MMO.IS_MINING, true);
			mine(p, dp);
			dp.set(MMO.SUPER_BREAKER_CD, System.currentTimeMillis() + (600000 - (dp.getInt(ElySkill.MINING)*1000)));
		} else if (!dp.getBool(MMO.IS_MINING)){
			dp.err("Super breaker on cooldown! &6" + ((System.currentTimeMillis() - dp.getLong(MMO.SUPER_BREAKER_CD))/1000)*-1 + " &c&oseconds remain.");
			dp.set(MMO.IS_SUPER_BREAKING, false);
		}
	}
	
	private void mine(Player p, DivinityPlayer dp){
		
		ItemStack i = p.getItemInHand();
		ItemMeta im = i.getItemMeta();
		dp.set(MMO.SAVED_ENCHANTS, i != null && i.hasItemMeta() && i.getItemMeta().hasEnchants() ? i.getItemMeta().getEnchants() : new HashMap<Enchantment, Integer>());
		
		List<Enchantment> enchants = Arrays.asList(Enchantment.DIG_SPEED, Enchantment.SILK_TOUCH, Enchantment.DURABILITY);
		
		if (i.hasItemMeta() && i.getItemMeta().hasLore()){
			im.getLore().add(main.AS("&3&oSuperbreaker active!"));
		} else {
			im.setLore(Arrays.asList(main.AS("&3&oSuperbreaker active!")));
		}
		
		i.setItemMeta(im);
		
		for (Enchantment e : enchants){
			i.addUnsafeEnchantment(e, 10);
		}
		
		p.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, 200, 3), true);
		main.api.schedule(this, "removeEnchants", 200L, "sb" + p.getName(), p, dp, i, im);
	}
	
	@SuppressWarnings("unchecked")
	public void removeEnchants(Player p, DivinityPlayer dp, ItemStack i, ItemMeta im){
		
		List<String> lore = im.getLore();
		lore.remove(main.AS("&3&oSuperbreaker active!"));
		im.setLore(lore);
		i.setItemMeta(im);
		
		dp.set(MMO.IS_SUPER_BREAKING, false);
		dp.set(MMO.IS_MINING, false);
		
		for (Enchantment e : i.getItemMeta().getEnchants().keySet()){
			i.removeEnchantment(e);
		}
		
		if (((Map<Enchantment,Integer>)dp.getRawInfo(MMO.SAVED_ENCHANTS)).size() > 0){
			for (Enchantment e : ((Map<Enchantment,Integer>)dp.getRawInfo(MMO.SAVED_ENCHANTS)).keySet()){
				i.addUnsafeEnchantment(e, ((Map<Enchantment, Integer>)dp.getRawInfo(MMO.SAVED_ENCHANTS)).get(e));
			}
		}
	}
}