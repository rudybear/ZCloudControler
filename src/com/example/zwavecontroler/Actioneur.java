package com.example.zwavecontroler;

public class Actioneur {
	public int id;
	public String name;
	
	public Actioneur()
	{
	}
	
	public Actioneur (int id, String name)
	{
		this.id = id;
		this.name = name;
	}
	
	public int getId() {
		return id;
	}
	
	public String getName() {
		return name;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String toString()
	{
		return "Actioneur id = "+id+", nom : "+name;
	}
}
