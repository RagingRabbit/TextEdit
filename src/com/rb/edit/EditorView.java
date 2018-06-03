package com.rb.edit;

import java.io.File;
import java.util.List;

import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TabPane.TabClosingPolicy;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;

public class EditorView extends Pane {
	private Editor	editor;
	private TabPane	tabPane;
	private int		selectedTab;
	
	
	public EditorView(Editor editor) {
		this.editor = editor;
		
		tabPane = new TabPane();
		tabPane.setTabClosingPolicy(TabClosingPolicy.ALL_TABS);
		tabPane.setOnKeyPressed(e -> {
			tabPane.getSelectionModel().select(selectedTab);
			onKeyPress(e);
		});
		tabPane.setOnKeyTyped(e -> {
			onKeyTyped(e);
		});
		tabPane.setOnMousePressed(e -> {
			selectTab(tabPane.getSelectionModel().getSelectedIndex());
		});
		tabPane.setOnScroll(e -> {
			onScroll(e);
		});
		Main.instance.getScene().widthProperty().addListener(e -> {
			for (Tab tab : tabPane.getTabs()) {
				((TextArea) tab).onResize(Main.instance.getScene().getWidth(), Main.instance.getScene().getHeight() - 32);
			}
		});
		Main.instance.getScene().heightProperty().addListener(e -> {
			for (Tab tab : tabPane.getTabs()) {
				((TextArea) tab).onResize(Main.instance.getScene().getWidth(), Main.instance.getScene().getHeight() - 32);
			}
		});
		getChildren().add(tabPane);
		
		openTab(null);
		openTab(new File("test.txt"));
		openTab(new File("main.cpp"));
	}
	
	public void openTab(File file) {
		TextArea newTab = new TextArea(this, file, Main.getWidth(), Main.getHeight() - 32);
		tabPane.getTabs().add(newTab);
		tabPane.getSelectionModel().select(tabPane.getTabs().size() - 1);
		selectedTab = tabPane.getTabs().size() - 1;
	}
	
	public void closeTab(int index) {
		if (index < tabPane.getTabs().size()) {
			tabPane.getTabs().remove(index);
			if (selectedTab >= tabPane.getTabs().size()) {
				selectedTab = tabPane.getTabs().size() - 1;
			}
		}
	}
	
	public void openFile() {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setInitialDirectory(getSelectedTab().getFile().getAbsoluteFile().getParentFile());
		List<File> results = fileChooser.showOpenMultipleDialog(null);
		if (results != null && !results.isEmpty()) {
			for (File file : results) {
				openTab(file);
			}
		}
	}
	
	public void selectTab(int index) {
		selectedTab = index;
		if (selectedTab == -1) {
			selectedTab = tabPane.getTabs().size() - 1;
		} else if (selectedTab == tabPane.getTabs().size()) {
			selectedTab = 0;
		}
		tabPane.getSelectionModel().select(selectedTab);
	}
	
	public void closeCurrentTab() {
		closeTab(selectedTab);
	}
	
	public TextArea getSelectedTab() {
		return (TextArea) tabPane.getTabs().get(selectedTab);
	}
	
	public void update() {
		if (tabPane.getTabs().size() > 0) {
			((TextArea) tabPane.getTabs().get(tabPane.getSelectionModel().getSelectedIndex())).update();
		}
	}
	
	@Override
	protected void layoutChildren() {
		super.layoutChildren();
		
		if (!tabPane.getTabs().isEmpty()) {
			((TextArea) tabPane.getTabs().get(tabPane.getSelectionModel().getSelectedIndex())).draw();
		}
	}
	
	void onKeyPress(KeyEvent e) {
		switch (e.getCode()) {
		case F11:
			editor.toggleFullscreen();
			break;
		
		case O:
			if (e.isControlDown()) {
				openFile();
			}
			break;
		
		case W:
			if (e.isControlDown()) {
				closeCurrentTab();
			}
			break;
		case TAB:
			if (e.isControlDown()) {
				if (e.isShiftDown()) {
					selectTab(selectedTab - 1);
				} else {
					selectTab(selectedTab + 1);
				}
				break;
			}
		default:
			if (!tabPane.getTabs().isEmpty()) {
				((TextArea) tabPane.getTabs().get(tabPane.getSelectionModel().getSelectedIndex())).onKeyPress(e);
			}
			break;
		}
	}
	
	void onKeyTyped(KeyEvent e) {
		if (!tabPane.getTabs().isEmpty()) {
			((TextArea) tabPane.getTabs().get(tabPane.getSelectionModel().getSelectedIndex())).onKeyTyped(e);
		}
	}
	
	void onScroll(ScrollEvent e) {
		if (!tabPane.getTabs().isEmpty()) {
			((TextArea) tabPane.getTabs().get(tabPane.getSelectionModel().getSelectedIndex())).onScroll(e);
		}
	}
}
