package com.seekting.killer.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

//@Generated("com.robohorse.robopojogenerator")
public class Persons{

	@SerializedName("persons")
	private List<Person> persons;

	@SerializedName("type")
	private String type;

	public void setPersons(List<Person> persons){
		this.persons = persons;
	}

	public List<Person> getPersons(){
		return persons;
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
			"Persons{" + 
			"persons = '" + persons + '\'' + 
			",type = '" + type + '\'' + 
			"}";
		}
}