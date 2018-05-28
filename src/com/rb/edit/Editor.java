package com.rb.edit;

import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class Editor extends GridPane {
	public static final Editor instance;
	
	static {
		instance = new Editor();
	}
	
	
	private Stage		stage;
	private EditorView	view;
	
	
	public void init(Stage stage) {
		this.stage = stage;
		
		view = new EditorView();
		add(view, 0, 0);
	}
	
	public void toggleFullscreen() {
		stage.setFullScreen(!stage.isFullScreen());
	}
	
	public EditorView getView() {
		return view;
	}
}
