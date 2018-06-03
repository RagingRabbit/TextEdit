package com.rb.edit;

public class Selection {
	public int	startX, startY;
	public int	endX, endY;
	
	
	public int getStartX() {
		return shouldReverse() ? endX : startX;
	}
	
	public int getEndX() {
		return shouldReverse() ? startX : endX;
	}
	
	public int getStartY() {
		return shouldReverse() ? endY : startY;
	}
	
	public int getEndY() {
		return shouldReverse() ? startY : endY;
	}
	
	private boolean shouldReverse() {
		if (endY < startY) {
			return true;
		} else if (endY > startY) {
			return false;
		} else {
			return endX < startX;
		}
	}
}
