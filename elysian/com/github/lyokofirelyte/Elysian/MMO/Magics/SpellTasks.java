package com.github.lyokofirelyte.Elysian.MMO.Magics;

import org.bukkit.entity.Arrow;
import org.bukkit.entity.SmallFireball;

import com.github.lyokofirelyte.Divinity.PublicUtils.ParticleEffect;
import com.github.lyokofirelyte.Elysian.Elysian;
import com.github.lyokofirelyte.Elysian.MMO.ElyMMO;

public class SpellTasks extends ElyMMO {

	public SpellTasks(Elysian i) {
		super(i);
	}
	
	public void kersplash(Elysian main, SmallFireball fireball){
		if (!fireball.isDead()){
			ParticleEffect.SPLASH.display(1, 1, 1, 1, 300, fireball.getLocation(), 30);
		} else {
			main.api.cancelTask(main.spellTasks.get(fireball));
		}
	}
	
	public void fireball(Elysian main, SmallFireball fireball){
		if (!fireball.isDead()){
			ParticleEffect.RED_DUST.display(0, 0, 0, 0, 200, fireball.getLocation(), 30);
		} else {
			main.api.cancelTask(main.spellTasks.get(fireball));
		}
	}
	
	public void normalArrow(Elysian main, Arrow arrow){
		if (!arrow.isDead()){
			ParticleEffect.FIREWORKS_SPARK.display(1, 0, 1, 2, 30, arrow.getLocation(), 16);
		} else {
			main.api.cancelTask(main.spellTasks.get(arrow));
		}
	}
}