package com.rb.edit;

import java.io.File;

import javafx.scene.control.Tab;

public class FileTab extends Tab {
	private TextArea textArea;
	
	
	public FileTab(File file, double width, double height) {
		if (file == null) {
			setText("untitled");
		} else {
			String[] path = file.getAbsolutePath().split("\\\\");
			String filename = path[path.length - 1];
			setText(filename);
		}
		
		textArea = new TextArea(file, width, height);
		setContent(textArea);
	}
	
	public void draw() {
		textArea.draw();
	}
}
