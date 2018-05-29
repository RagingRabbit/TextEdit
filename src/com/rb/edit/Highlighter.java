package com.rb.edit;

import java.util.HashMap;
import java.util.Map;

public class Highlighter {
	private Map<String, Integer>	colors;
	private int						defaultColor;
	
	
	public Highlighter() {
		colors = new HashMap<String, Integer>();
	}
	
	public void setDefaultColor(int color) {
		defaultColor = color;
	}
	
	public void addColor(String type, int color) {
		colors.put(type, color);
	}
	
	public int getDefaultColor() {
		return defaultColor;
	}
	
	public int getColor(String type) {
		return colors.get(type);
	}
}
