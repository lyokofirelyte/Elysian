package com.github.lyokofirelyte.Elysian.MMO;

public enum MMO {

	IS_TREE_FELLING("IS_TREE_FELLING"),
	TREE_FELLER_CD("TREE_FELLER_CD");
	
	MMO(String type){
		this.type = type;
	}
	
	public String type;
}