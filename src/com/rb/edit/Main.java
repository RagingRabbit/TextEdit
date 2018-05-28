package com.rb.edit;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class Main extends Application {
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
		
		scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent event) {
				switch (event.getCode()) {
				case F11:
					EditorEvent.invoke("Editor.toggleFullscreen");
					break;
				
				case O:
					if (event.isControlDown()) {
						EditorEvent.invoke("Editor.openFile");
					}
					break;
				
				case W:
					if (event.isControlDown()) {
						EditorEvent.invoke("Editor.closeCurrentTab");
					}
					break;
				
				default:
					break;
				}
			}
		});
	}
	
	
	public static void main(String[] args) {
		launch(args);
	}
}
