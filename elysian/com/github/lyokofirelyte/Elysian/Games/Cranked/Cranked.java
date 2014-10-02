package com.github.lyokofirelyte.Elysian.Games.Cranked;

import com.github.lyokofirelyte.Divinity.Storage.DivGame;
import com.github.lyokofirelyte.Divinity.Storage.DivinityGame;
import com.github.lyokofirelyte.Elysian.ElySave;
import com.github.lyokofirelyte.Elysian.Elysian;

public class Cranked implements DivGame, ElySave{
	
	protected Elysian main;
	public CrankedCommand command;

	public Cranked(Elysian i){
		main = i;
		command = new CrankedCommand(this);
	}

	@Override
	public DivinityGame toDivGame() {
		return main.api.getDivGame("cranked", "cranked");
	}

	@Override
	public void save() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void load(){
		// TODO Auto-generated method stub
		
	}

	
}
