package com.github.lyokofirelyte.Elysian.MMO.Magics;

import java.util.Random;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.SmallFireball;
import org.bukkit.inventory.ItemStack;

import com.github.lyokofirelyte.Divinity.PublicUtils.ParticleEffect;
import com.github.lyokofirelyte.Divinity.Storage.DivinityPlayer;
import com.github.lyokofirelyte.Divinity.Storage.ElySkill;
import com.github.lyokofirelyte.Elysian.Elysian;

public enum Spell {

	NORMAL_ARROW("NORMAL_ARROW", ElySkill.SOLAR, 0, 0),
	FIRE_BLAST("FIRE_BLAST", ElySkill.SOLAR, 0, 2000L),
	KERSPLASH("KERSPLASH", ElySkill.SOLAR, 5, 2000L),
	DEFLECT("DEFLECT", ElySkill.LUNAR, 0, 10000L);
	
	Spell(String type, ElySkill skill, int level, long cooldown){
		this.type = type;
		this.skill = skill;
		this.level = level;
		this.cooldown = cooldown;
	}
	
	String type;
	ElySkill skill;
	int level;
	long cooldown;
	
	public void cast(Elysian main, Player shooter){
		
		DivinityPlayer dp = main.api.getDivPlayer(shooter);
		ItemStack toRemove = null;
		boolean cont = false;
		boolean found = false;
		
		if (dp.hasLevel(skill, level)){
			
			if (dp.contains(type + "_COOLDOWN")){
				if (dp.getLong(type + "_COOLDOWN") > System.currentTimeMillis()){
					dp.err((dp.getLong(type + "_COOLDOWN") - System.currentTimeMillis())/1000 + " seconds cooldown!");
					return;
				}
			}
			
			for (String s : shooter.getItemInHand().getItemMeta().getLore()){
				if (s.contains(shooter.getName())){
					cont = true;
				}
			}
			
			for (ItemStack i : shooter.getInventory().getContents()){
				if (!found && i != null && i.hasItemMeta() && i.getItemMeta().hasLore()){
					for (String lore : i.getItemMeta().getLore()){
						if (lore.contains("Consumed by magic spells")){
							if (i.getAmount() > 1){
								i.setAmount(i.getAmount() -1);
							} else {
								toRemove = i;
							}
							found = true;
							shooter.updateInventory();
							break;
						}
					}
				}
			}
			
			if (toRemove != null){
				shooter.getInventory().removeItem(toRemove);
			}
			
			if (cont && found){
		
				Location from = shooter.getLocation();
				from.setY(from.getY() + 1.5);
				Location frontLocation = from.add(from.getDirection());
				dp.set(type + "_COOLDOWN", System.currentTimeMillis() + cooldown);
				
				switch (type){
				
					case "FIRE_BLAST": case "KERSPLASH":
						
						SmallFireball fireball = (SmallFireball) shooter.getWorld().spawnEntity(frontLocation, EntityType.SMALL_FIREBALL);
						fireball.setShooter(shooter);
						fireball.setVelocity(shooter.getLocation().getDirection().multiply(1.4));
						main.spellTasks.put(fireball, type + "%" + new Random().nextInt(1000));
						
						if (type.equals("FIRE_BLAST")){
							main.api.repeat(main.mmo.spellTasks, "fireball", 0L, 1L, main.spellTasks.get(fireball), main, fireball);
						} else {
							main.api.repeat(main.mmo.spellTasks, "kersplash", 0L, 1L, main.spellTasks.get(fireball), main, fireball);
						}
						
					break;
					
					case "DEFLECT":
						
						ParticleEffect.SPELL.display(2, 0, 2, 1, 6000, shooter.getLocation(), 30);
						
						for (Entity ee : shooter.getNearbyEntities(5D, 5D, 5D)){
							ee.setVelocity(ee.getLocation().getDirection().multiply(-3));
						}
						
					break;
				}
				
			} else {
				dp.err("This isn't yours or you're out of supercobble.");
			}
			
		} else {
			main.s(shooter, "&c&oThis spell requires level &6&o" + level + " &c&oin &6&o" + skill.s() + "&c&o!");
		}
	}
	
	public boolean contains(String name){
		
		for (Spell spell : Spell.values()){
			if (spell.toString().equalsIgnoreCase(name.replace(" ", "_"))){
				return true;
			}
		}
		
		return false;
	}
}