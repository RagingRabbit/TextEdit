package com.rb.edit;

import java.io.File;

import javafx.scene.control.Tab;

public class FileTab extends Tab {
	private EditorView	view;
	private TextArea	textArea;
	
	
	public FileTab(EditorView view, File file, double width, double height) {
		this.view = view;
		
		if (file == null) {
			setText("untitled");
		} else {
			String[] path = file.getAbsolutePath().split("\\\\");
			String filename = path[path.length - 1];
			setText(filename);
		}
		
		textArea = new TextArea(this, file, width, height);
		setContent(textArea);
	}
	
	public void update() {
		textArea.update();
	}
	
	public void draw() {
		textArea.draw();
	}
	
	public EditorView getView() {
		return view;
	}
}
