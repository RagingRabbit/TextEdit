package com.rb.edit;

import java.io.File;

import javafx.scene.control.TabPane;
import javafx.scene.control.TabPane.TabClosingPolicy;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;

public class EditorView extends Pane {
	private TabPane tabPane;
	
	
	public EditorView() {
		tabPane = new TabPane();
		tabPane.setTabClosingPolicy(TabClosingPolicy.ALL_TABS);
		getChildren().add(tabPane);
		
		openTab(null);
		openTab(new File("test.txt"));
		openTab(new File("main.cpp"));
	}
	
	public void openTab(File file) {
		FileTab newTab = new FileTab(file, 1920, 1050);
		tabPane.getTabs().add(newTab);
		tabPane.getSelectionModel().select(tabPane.getTabs().size() - 1);
	}
	
	public void closeTab(int index) {
		if (index < tabPane.getTabs().size()) {
			tabPane.getTabs().remove(index);
		}
	}
	
	public void openFile() {
		FileChooser fileChooser = new FileChooser();
		File result = fileChooser.showOpenDialog(null);
		openTab(result);
	}
	
	public void closeCurrentTab() {
		closeTab(getCurrentTab());
	}
	
	public int getCurrentTab() {
		return tabPane.getSelectionModel().getSelectedIndex();
	}
	
	@Override
	protected void layoutChildren() {
		super.layoutChildren();
		
		if (tabPane.getTabs().size() > 0) {
			((FileTab) tabPane.getTabs().get(tabPane.getSelectionModel().getSelectedIndex())).draw();
		}
	}
}
