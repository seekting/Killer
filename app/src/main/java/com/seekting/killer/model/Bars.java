package com.seekting.killer.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

//@Generated("com.robohorse.robopojogenerator")
public class Bars{

	@SerializedName("type")
	private String type;

	@SerializedName("bars")
	private List<Bar> bars;

	public void setType(String type){
		this.type = type;
	}

	public String getType(){
		return type;
	}

	public void setBars(List<Bar> bars){
		this.bars = bars;
	}

	public List<Bar> getBars(){
		return bars;
	}

	@Override
 	public String toString(){
		return 
			"Bars{" + 
			"type = '" + type + '\'' + 
			",bars = '" + bars + '\'' + 
			"}";
		}
}