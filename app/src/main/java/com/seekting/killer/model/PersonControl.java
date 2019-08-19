package com.seekting.killer.model;

public class PersonControl{
	private String person;
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
