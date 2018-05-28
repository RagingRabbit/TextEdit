package com.rb.edit;

import java.io.File;

import javafx.stage.FileChooser;

public enum EditorEvent {
	TOGGLE_FULLSCREEN("Editor.toggleFullscreen", () -> {
		Editor.instance.toggleFullscreen();
	}),
	OPEN_FILE("Editor.openFile", () -> {
		FileChooser fileChooser = new FileChooser();
		File result = fileChooser.showOpenDialog(null);
		Editor.instance.getView().openTab(result);
	}),
	CLOSE_CURRENT_TAB("Editor.closeCurrentTab", () -> {
		Editor.instance.getView().closeTab(Editor.instance.getView().getCurrentTab());
	});
	
	
	private String		macro;
	private Runnable	callback;
	
	EditorEvent(String macro, Runnable callback) {
		this.macro = macro;
		this.callback = callback;
	}
	
	
	public static void invoke(String macro) {
		for (EditorEvent e : values()) {
			if (e.macro.equalsIgnoreCase(macro)) {
				e.callback.run();
			}
		}
	}
}
