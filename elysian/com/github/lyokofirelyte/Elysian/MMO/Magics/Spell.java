package com.github.lyokofirelyte.Elysian.MMO.Magics;

import java.util.Random;

import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.SmallFireball;

import com.github.lyokofirelyte.Divinity.Storage.ElySkill;
import com.github.lyokofirelyte.Elysian.Elysian;

public enum Spell {

	NORMAL_ARROW("NORMAL_ARROW", ElySkill.SOLAR_MAGICS, 0),
	FIRE_BLAST("FIRE_BLAST", ElySkill.SOLAR_MAGICS, 0);
	
	Spell(String type, ElySkill skill, int level){
		this.type = type;
		this.skill = skill;
		this.level = level;
	}
	
	String type;
	ElySkill skill;
	int level;
	
	public void cast(Elysian main, Player shooter){
		
		if (main.api.getDivPlayer(shooter).hasLevel(skill, level)){
		
			Location from = shooter.getLocation();
			from.setY(from.getY() + 1.5);
			Location frontLocation = from.add(from.getDirection());
			
			switch (type){
			
				case "FIRE_BLAST":
					
					SmallFireball fireball = (SmallFireball) shooter.getWorld().spawnEntity(frontLocation, EntityType.SMALL_FIREBALL);
					fireball.setShooter(shooter);
					fireball.setVelocity(shooter.getLocation().getDirection().multiply(1.4));
					main.spellTasks.put(fireball, type + "%" + new Random().nextInt(1000));
					main.api.repeat(main.mmo.spellTasks, "fireball", 0L, 1L, main.spellTasks.get(fireball), main, fireball);
					
				break;
			}
			
		} else {
			main.s(shooter, "&c&oThis spell requires level &6&o" + level + " &c&oin &6&o" + skill.s() + "&c&o!");
		}
	}
	
	public boolean contains(String name){
		
		for (Spell spell : Spell.values()){
			if (spell.toString().equalsIgnoreCase(name)){
				return true;
			}
		}
		
		return false;
	}
}