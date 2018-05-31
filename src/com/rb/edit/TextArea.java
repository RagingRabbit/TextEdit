package com.rb.edit;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.rb.edit.Tokenizer.Token;
import com.rb.edit.highlighting.DefaultHighlighter;
import com.rb.edit.highlighting.DefaultTokenizer;
import com.rb.edit.highlighting.lang.Cpp.CppHighlighter;
import com.rb.edit.highlighting.lang.Cpp.CppTokenizer;
import com.rb.edit.highlighting.lang.Cs.CsHighlighter;
import com.rb.edit.highlighting.lang.Cs.CsTokenizer;

import javafx.geometry.Bounds;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

public class TextArea extends Canvas {
	private static final String	FONT_FACE			= "Consolas";
	private static final int	FONT_SIZE			= 13;
	
	private static final int	BACKGROUND_COLOR	= 0x1C1F22;
	
	
	private FileTab				tab;
	
	private File				file;
	
	private List<Line>			lines;
	private Tokenizer			tokenizer;
	private Highlighter			highlighter;
	
	private int					cursorX, cursorY;
	private boolean				cursorActive;
	private int					cursorTimerOffset;
	
	
	public TextArea(FileTab tab, File file, double width, double height) {
		super(width, height);
		this.tab = tab;
		this.file = file;
		this.lines = new ArrayList<Line>();
		
		if (file != null) {
			loadFile();
		}
		
		this.tokenizer = tokenizer == null ? new DefaultTokenizer() : tokenizer;
		this.highlighter = highlighter == null ? new DefaultHighlighter() : highlighter;
		
		setFocusTraversable(true);
		setOnKeyPressed(e -> {
			e.consume();
			onKeyPress(e);
		});
	}
	
	private void onKeyPress(KeyEvent e) {
		switch (e.getCode()) {
		case LEFT:
			if (cursorX > 0) {
				cursorX--;
				updateCursorState();
			}
			break;
		case RIGHT:
			cursorX++;
			if (cursorX < lines.get(cursorY).raw.length() - 1) {
				cursorX++;
				updateCursorState();
			}
			break;
		case UP:
			if (cursorY > 0) {
				cursorY--;
				updateCursorState();
			}
			break;
		case DOWN:
			if (cursorY < lines.size() - 1) {
				cursorY++;
				updateCursorState();
			}
			break;
		default:
			break;
		}
	}
	
	private void updateCursorState() {
		if (cursorActive = System.currentTimeMillis() % 1000 >= 500) {
			cursorTimerOffset = 0;
		}
	}
	
	private void loadFile() {
		lines.clear();
		
		try {
			String[] filepath = file.getAbsolutePath().split("\\\\");
			String[] filename = filepath[filepath.length - 1].split("\\.");
			String extension = filename.length == 0 ? null : filename[1];
			
			switch (extension) {
			case "h":
			case "c":
			case "hh":
			case "cc":
			case "hpp":
			case "cpp":
				tokenizer = new CppTokenizer();
				highlighter = new CppHighlighter();
				break;
			
			case "cs":
				tokenizer = new CsTokenizer();
				highlighter = new CsHighlighter();
				break;
			
			default:
				tokenizer = new DefaultTokenizer();
				highlighter = new DefaultHighlighter();
				break;
			}
			
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
	
	public void update() {
		cursorActive = System.currentTimeMillis() % 1000 < 500;
		tab.getView().requestLayout();
	}
	
	public void draw() {
		GraphicsContext g = getGraphicsContext2D();
		
		g.setFill(Color.web("#" + Integer.toHexString(BACKGROUND_COLOR)));
		g.fillRect(0, 0, getWidth(), getHeight());
		g.setFont(Font.font(FONT_FACE, FontWeight.BLACK, FontPosture.REGULAR, FONT_SIZE));
		
		for (int i = 0; i < lines.size(); i++) {
			int x = 0;
			int length = 0;
			for (int j = 0; j < lines.get(i).tokens.size(); j++) {
				String line = lines.get(i).tokens.get(j).str;
				String type = lines.get(i).tokens.get(j).type;
				int color = type == null ? highlighter.getDefaultColor() : highlighter.getColor(type);
				
				int yy = i * FONT_SIZE + FONT_SIZE;
				if (yy > getHeight()) {
					i = lines.size();
					break;
				}
				
				Color fillColor = Color.rgb((color & 0xFF0000) >> 16, (color & 0x00FF00) >> 8, (color & 0x0000FF) >> 0);
				g.setFill(fillColor);
				g.fillText(line, x, yy);
				
				int tokenLengthPixels = getWordLengthPixels(g.getFont(), line);
				if (cursorActive && i == cursorY && length <= cursorX && length + line.length() > cursorX) {
					g.setFill(Color.WHITE);
					g.fillText("|", x + getWordLengthPixels(g.getFont(), line.substring(0, cursorX - length)) - 2, yy);
				}
				
				x += tokenLengthPixels;
				length += line.length();
			}
		}
	}
	
	private int getWordLengthPixels(Font font, String str) {
		Text text = new Text(str);
		text.setFont(font);
		Bounds tb = text.getBoundsInLocal();
		return (int) tb.getWidth();
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
