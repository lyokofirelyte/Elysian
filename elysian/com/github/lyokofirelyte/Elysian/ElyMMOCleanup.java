package com.github.lyokofirelyte.Elysian;

import java.util.ArrayList;
import com.github.lyokofirelyte.Divinity.Storage.DivinitySystem;
import com.github.lyokofirelyte.Elysian.MMO.MMO;

public class ElyMMOCleanup implements Runnable {
	
	private Elysian main;
	
	public ElyMMOCleanup(Elysian i){
		main = i;
	}

	@Override
	public void run(){
		DivinitySystem dp = main.api.getSystem();
		dp.set(MMO.INVALID_BLOCKS, new ArrayList<String>());
	}
	
	public void stop(){
		main.cancelTask(ElyTask.MMO_BLOCKS);
	}
}