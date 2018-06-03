package com.rb.edit;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class Main extends Application {
	static Main					instance;
	static boolean				initialized;
	
	private static final int	WIDTH	= 650;
	private static final int	HEIGHT	= 500;
	
	
	private BorderPane			layout;
	private Scene				scene;
	
	private Editor				editor;
	
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		instance = this;
		
		layout = new BorderPane();
		
		editor = Editor.instance;
		layout.getChildren().add(editor);
		
		scene = new Scene(layout, WIDTH, HEIGHT);
		scene.getStylesheets().add("tabs.css");
		
		primaryStage.setScene(scene);
		primaryStage.show();
		
		editor.init(primaryStage);
		
		initialized = true;
	}
	
	@Override
	public void stop() throws Exception {
		editor.stop();
	}
	
	public Scene getScene() {
		return scene;
	}
	
	
	public static double getWidth() {
		return instance.scene.getWidth();
	}
	
	public static double getHeight() {
		return instance.scene.getHeight();
	}
	
	public static void main(String[] args) {
		Thread thread = new Thread(() -> {
			while (!initialized) {
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
