package com.seekting.killer.model;

import com.google.gson.annotations.SerializedName;

public class BarControl{
	@SerializedName("bar")
	private String bar;
	@SerializedName("type")
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
