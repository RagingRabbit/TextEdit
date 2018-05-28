package com.rb.edit;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.rb.edit.Tokenizer.Rule;
import com.rb.edit.Tokenizer.Token;

import javafx.geometry.Bounds;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

public class TextArea extends Canvas {
	private static final String	FONT_FACE			= "Consolas";
	private static final int	FONT_SIZE			= 13;
	
	private static final int	BACKGROUND_COLOR	= 0x1C1F22;
	
	
	private File				file;
	
	private List<Line>			lines;
	private Tokenizer			tokenizer;
	
	
	public TextArea(File file, double width, double height) {
		super(width, height);
		this.file = file;
		this.lines = new ArrayList<Line>();
		
		this.tokenizer = new Tokenizer();
		this.tokenizer.addRule(new Rule("int", "keyword"));
		this.tokenizer.addRule(new Rule("return", "keyword"));
		this.tokenizer.addRule(new Rule("(", "bracket"));
		this.tokenizer.addRule(new Rule(")", "bracket"));
		
		if (file != null) {
			loadFile();
		}
	}
	
	private void loadFile() {
		lines.clear();
		
		try {
			BufferedReader reader = new BufferedReader(new FileReader(file));
			String line = null;
			while ((line = reader.readLine()) != null) {
				lines.add(new Line(this, line));
			}
			reader.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void draw() {
		GraphicsContext g = getGraphicsContext2D();
		
		g.setFill(Color.web("#" + Integer.toHexString(BACKGROUND_COLOR)));
		g.fillRect(0, 0, getWidth(), getHeight());
		g.setFont(Font.font(FONT_FACE, FontWeight.BLACK, FontPosture.REGULAR, FONT_SIZE));
		
		for (int i = 0; i < lines.size(); i++) {
			int x = 0;
			for (int j = 0; j < lines.get(i).tokens.size(); j++) {
				String line = lines.get(i).tokens.get(j).str;
				String type = lines.get(i).tokens.get(j).type;
				int color = getColor(type);
				
				g.setFill(Color.rgb((color & 0xFF0000) >> 16, (color & 0x00FF00) >> 8, (color & 0x0000FF) >> 0));
				g.fillText(line, x, i * FONT_SIZE + FONT_SIZE);
				
				Text text = new Text(line);
				text.setFont(g.getFont());
				Bounds tb = text.getBoundsInLocal();
				
				x += tb.getWidth();
			}
		}
	}
	
	private int getColor(String type) {
		if (type != null && type.equals("keyword")) {
			return 0x0058d0;
		} else if (type != null && type.equals("bracket")) {
			return 0x31a0e0;
		} else {
			return 0xcbcdcf;
		}
	}
	
	
	private static class Line {
		private TextArea	area;
		private String		raw;
		private List<Token>	tokens;
		
		
		private Line(TextArea area, String line) {
			this.area = area;
			tokens = new ArrayList<Token>();
			setLine(line);
		}
		
		private void setLine(String line) {
			raw = line;
			tokenize();
		}
		
		private void tokenize() {
			tokens = area.tokenizer.tokenize(raw);
		}
	}
}
