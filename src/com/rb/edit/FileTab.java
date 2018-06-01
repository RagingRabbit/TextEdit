package com.rb.edit;

import java.io.File;

import javafx.scene.control.Tab;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.ScrollEvent;

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
	
	void onKeyPress(KeyEvent e) {
		textArea.onKeyPress(e);
	}
	
	void onKeyTyped(KeyEvent e) {
		textArea.onKeyTyped(e);
	}
	
	void onScroll(ScrollEvent e) {
		textArea.onScroll(e);
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
