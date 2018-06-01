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
import javafx.scene.input.ScrollEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

public class TextArea extends Canvas {
	private static final String	FONT_FACE			= "Consolas";
	private static final int	FONT_SIZE			= 13;
	
	private static final int	BACKGROUND_COLOR	= 0x1C1F22;
	
	private static final int	LINE_NUMBERS_WIDTH	= 6;
	private static final int	LINE_NUMBERS_COLOR	= 0xffffff;
	
	
	private FileTab				tab;
	
	private File				file;
	
	private List<Line>			lines;
	private Tokenizer			tokenizer;
	private Highlighter			highlighter;
	
	private int					cursorX, cursorY;
	private int					uncappedCursorX;
	private long				lastFrame;
	private long				cursorTimer;
	
	private double				scrollX, scrollY;
	
	
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
		
		uncappedCursorX = -1;
	}
	
	void onKeyPress(KeyEvent e) {
		switch (e.getCode()) {
		case LEFT:
			if (cursorX > 0) {
				if (e.isControlDown()) {
					cursorX -= getPreviousWordSpan(cursorX, cursorY);
				} else {
					cursorX--;
				}
			} else if (cursorY > 0) {
				cursorY--;
				cursorX = lines.get(cursorY).raw.length();
			}
			resetCursor(true);
			break;
		case RIGHT:
			if (cursorX < lines.get(cursorY).raw.length()) {
				if (e.isControlDown()) {
					cursorX += getNextWordSpan(cursorX, cursorY);
				} else {
					cursorX++;
				}
			} else if (cursorY < lines.size() - 1) {
				cursorY++;
				cursorX = 0;
			}
			resetCursor(true);
			break;
		case UP:
			if (cursorY > 0) {
				cursorY--;
				if (uncappedCursorX == -1) {
					uncappedCursorX = cursorX;
				} else {
					cursorX = uncappedCursorX;
				}
				resetCursor(false);
			}
			break;
		case DOWN:
			if (cursorY < lines.size() - 1) {
				cursorY++;
				if (uncappedCursorX == -1) {
					uncappedCursorX = cursorX;
				} else {
					cursorX = uncappedCursorX;
				}
				resetCursor(false);
			}
			break;
		case BACK_SPACE:
			if (cursorX > 0) {
				if (e.isControlDown()) {
					int len = getPreviousWordSpan(cursorX, cursorY);
					for (int i = 0; i < len; i++) {
						deleteCharacter(cursorX - 1, cursorY);
						cursorX--;
					}
				} else {
					deleteCharacter(cursorX - 1, cursorY);
					cursorX--;
				}
			} else if (cursorY > 0) {
				cursorX = lines.get(cursorY - 1).raw.length();
				cursorY--;
				joinLines(cursorY, cursorY + 1);
			}
			resetCursor(true);
			break;
		case ENTER:
			if (cursorX == lines.get(cursorY).raw.length()) {
				insertLine(cursorY + 1, "");
				cursorY++;
				cursorX = 0;
			} else {
				String thisLine = lines.get(cursorY).raw.substring(0, cursorX);
				String nextLine = lines.get(cursorY).raw.substring(cursorX);
				lines.get(cursorY).setLine(thisLine);
				insertLine(cursorY + 1, nextLine);
				cursorY++;
				cursorX = 0;
			}
			resetCursor(true);
			break;
		case END:
			if (e.isControlDown()) {
				cursorY = lines.size() - 1;
			}
			cursorX = lines.get(cursorY).raw.length();
			resetCursor(true);
			break;
		case HOME:
			if (e.isControlDown()) {
				cursorY = 0;
			}
			cursorX = 0;
			resetCursor(true);
			break;
		default:
			break;
		}
	}
	
	void onKeyTyped(KeyEvent e) {
		if (e.getCharacter().length() > 0 && !Character.isISOControl(e.getCharacter().toCharArray()[0])) {
			insertCharacter(cursorX, cursorY, e.getCharacter());
			cursorX++;
		}
	}
	
	void onScroll(ScrollEvent e) {
		scrollY += e.getDeltaY();
	}
	
	private void insertLine(int y, String text) {
		lines.add(y, new Line(this, text));
	}
	
	private void joinLines(int first, int second) {
		lines.get(first).setLine(lines.get(first).raw + lines.get(second).raw);
		lines.remove(second);
	}
	
	private void insertCharacter(int x, int y, String chars) {
		String line = lines.get(y).raw;
		String newLine = line.substring(0, x) + chars + line.substring(x);
		lines.get(y).setLine(newLine);
	}
	
	private void deleteCharacter(int x, int y) {
		String line = lines.get(y).raw;
		String newLine = line.substring(0, x) + line.substring(x + 1);
		lines.get(y).setLine(newLine);
	}
	
	private void resetCursor(boolean capCursor) {
		cursorX = Math.min(lines.get(cursorY).raw.length(), cursorX);
		cursorTimer = 0;
		if (capCursor) {
			uncappedCursorX = -1;
		}
	}
	
	private int getNextWordSpan(int x, int y) {
		int start = x;
		int i = start;
		boolean spaces = lines.get(y).raw.charAt(x) == ' ';
		do {
			i++;
			if (i == lines.get(y).raw.length()) {
				break;
			}
		} while (spaces ? lines.get(y).raw.charAt(i) == ' '
				: ((isLetterOrDigit(lines.get(y).raw.charAt(i)) && lines.get(y).raw.charAt(i - 1) != ' ')
						|| (isLetterOrDigit(lines.get(y).raw.charAt(i - 1)) && lines.get(y).raw.charAt(i) == ' ')));
		
		return i - start;
	}
	
	private int getPreviousWordSpan(int x, int y) {
		int start = x;
		int i = start;
		boolean spaces = lines.get(y).raw.charAt(x - 1) == ' ';
		do {
			i--;
			if (i == 0) {
				break;
			}
			if (isLetterOrDigit(lines.get(y).raw.charAt(i))) {
				spaces = false;
			}
		} while (spaces ? lines.get(y).raw.charAt(i) == ' ' : (isLetterOrDigit(lines.get(y).raw.charAt(i))));
		
		if (i < start - 1) {
			i++;
		}
		
		return start - i;
	}
	
	private boolean isLetterOrDigit(char c) {
		return Character.isLetter(c) || Character.isDigit(c);
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
		long now = System.currentTimeMillis();
		cursorTimer += (now - lastFrame);
		lastFrame = now;
		tab.getView().requestLayout();
	}
	
	public void draw() {
		GraphicsContext g = getGraphicsContext2D();
		
		g.setFill(Color.web("#" + Integer.toHexString(BACKGROUND_COLOR)));
		g.fillRect(0, 0, getWidth(), getHeight());
		g.setFont(Font.font(FONT_FACE, FontWeight.BLACK, FontPosture.REGULAR, FONT_SIZE));
		
		boolean cursorActive = cursorTimer % 1000 < 500;
		
		int linenumbersWidthPixels = getWordLengthPixels(g.getFont(), " ") * LINE_NUMBERS_WIDTH;
		
		for (int i = 0; i < lines.size(); i++) {
			int x = linenumbersWidthPixels;
			int length = 0;
			
			int yy = i * FONT_SIZE + FONT_SIZE + (int) scrollY;
			if (yy > getHeight()) {
				i = lines.size();
				break;
			}
			
			Color linenumberColor = Color.rgb((LINE_NUMBERS_COLOR & 0xFF0000) >> 16, (LINE_NUMBERS_COLOR & 0x00FF00) >> 8, (LINE_NUMBERS_COLOR & 0x0000FF) >> 0);
			String linenumberString = Integer.toString(i + 1);
			g.setFill(linenumberColor);
			g.fillText(linenumberString, linenumbersWidthPixels - getWordLengthPixels(g.getFont(), linenumberString) - getWordLengthPixels(g.getFont(), " "), yy);
			
			for (int j = 0; j < lines.get(i).tokens.size(); j++) {
				String line = lines.get(i).tokens.get(j).str;
				String type = lines.get(i).tokens.get(j).type;
				int color = type == null ? highlighter.getDefaultColor() : highlighter.getColor(type);
				
				Color fillColor = Color.rgb((color & 0xFF0000) >> 16, (color & 0x00FF00) >> 8, (color & 0x0000FF) >> 0);
				g.setFill(fillColor);
				g.fillText(line, x, yy);
				
				int tokenLengthPixels = getWordLengthPixels(g.getFont(), line);
				if (cursorActive && i == cursorY && length <= cursorX && length + line.length() >= cursorX) {
					g.setFill(Color.WHITE);
					g.fillText("|", x + getWordLengthPixels(g.getFont(), line.substring(0, cursorX - length)) - 2, yy);
					g.fillText("|", x + getWordLengthPixels(g.getFont(), line.substring(0, cursorX - length)) - 3, yy);
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
