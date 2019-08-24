package com.seekting.killer.model;

import com.google.gson.annotations.SerializedName;

public class PersonControl{
	@SerializedName("person")
	private String person;
	@SerializedName("type")
	private String type;

	public void setPerson(String person){
		this.person = person;
	}

	public String getPerson(){
		return person;
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
			"PersonControl{" + 
			"person = '" + person + '\'' + 
			",type = '" + type + '\'' + 
			"}";
		}
}
