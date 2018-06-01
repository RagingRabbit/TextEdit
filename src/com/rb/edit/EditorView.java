package com.rb.edit;

import java.io.File;

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
		getChildren().add(tabPane);
		
		openTab(null);
		openTab(new File("test.txt"));
		openTab(new File("main.cpp"));
	}
	
	public void openTab(File file) {
		FileTab newTab = new FileTab(this, file, 1920, 1050);
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
		File result = fileChooser.showOpenDialog(null);
		openTab(result);
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
		closeTab(getCurrentTab());
	}
	
	public int getCurrentTab() {
		return tabPane.getSelectionModel().getSelectedIndex();
	}
	
	public void update() {
		if (tabPane.getTabs().size() > 0) {
			((FileTab) tabPane.getTabs().get(tabPane.getSelectionModel().getSelectedIndex())).update();
		}
	}
	
	@Override
	protected void layoutChildren() {
		super.layoutChildren();
		
		if (!tabPane.getTabs().isEmpty()) {
			((FileTab) tabPane.getTabs().get(tabPane.getSelectionModel().getSelectedIndex())).draw();
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
				((FileTab) tabPane.getTabs().get(tabPane.getSelectionModel().getSelectedIndex())).onKeyPress(e);
			}
			break;
		}
	}
	
	void onKeyTyped(KeyEvent e) {
		if (!tabPane.getTabs().isEmpty()) {
			((FileTab) tabPane.getTabs().get(tabPane.getSelectionModel().getSelectedIndex())).onKeyTyped(e);
		}
	}
	
	void onScroll(ScrollEvent e) {
		if (!tabPane.getTabs().isEmpty()) {
			((FileTab) tabPane.getTabs().get(tabPane.getSelectionModel().getSelectedIndex())).onScroll(e);
		}
	}
}
