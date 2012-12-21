package com.tu.wahlinfo.frontend.model;

public class Party {

	String name;
	// TODO: Make enum
	String color;

	public Party(String name) {
		super();
		this.name = name;
	}

	public Party(String name, String color) {
		super();
		this.name = name;
		this.color = color;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

}
