package com.rb.edit;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class Main extends Application {
	private static Main	instance;
	
	
	private BorderPane	layout;
	private Scene		scene;
	
	private Editor		editor;
	
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		layout = new BorderPane();
		
		editor = Editor.instance;
		layout.getChildren().add(editor);
		
		scene = new Scene(layout, 1280, 720);
		scene.getStylesheets().add("tabs.css");
		
		primaryStage.setScene(scene);
		primaryStage.show();
		
		editor.init(primaryStage);
		
		instance = this;
	}
	
	@Override
	public void stop() throws Exception {
		editor.stop();
	}
	
	
	public static void main(String[] args) {
		Thread thread = new Thread(() -> {
			while (instance == null) {
				try {
					Thread.sleep(1);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			
			instance.editor.run();
		});
		thread.start();
		
		launch(args);
	}
}
