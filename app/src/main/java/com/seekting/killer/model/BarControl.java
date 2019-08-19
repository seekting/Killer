package com.seekting.killer.model;

public class BarControl{
	private String bar;
	private String type;

	public void setBar(String bar){
		this.bar = bar;
	}

	public String getBar(){
		return bar;
	}

	public void setType(String type){
		this.type = type;
	}

	public String getType(){
		return type;
	}

	@Override
 	public String toString(){
		return 
			"BarControl{" + 
			"bar = '" + bar + '\'' + 
			",type = '" + type + '\'' + 
			"}";
		}
}
