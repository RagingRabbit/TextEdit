package com.rb.edit;

import java.lang.reflect.InvocationTargetException;

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
	
	public void invoke(String command) {
		String[] elements = command.split("\\.");
		switch (elements[0]) {
		case "App":
			try {
				getClass().getMethod(elements[1]).invoke(this);
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
			break;
		
		case "Editor":
			try {
				view.getClass().getMethod(elements[1]).invoke(view);
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
			break;
		}
	}
	
	public void toggleFullscreen() {
		stage.setFullScreen(!stage.isFullScreen());
	}
	
	public EditorView getView() {
		return view;
	}
}
