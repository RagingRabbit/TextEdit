package com.rb.edit;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
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
import javafx.scene.control.Tab;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;

public class TextArea extends Tab {
	private static final String	FONT_FACE				= "Consolas";
	private static final int	FONT_SIZE				= 13;
	
	private static final int	BACKGROUND_COLOR		= 0x1C1F22;
	private static final int	CURSOR_COLOR			= 0xffffff;
	
	private static final int	PADDING_TOP				= 5;
	
	private static final int	LINE_NUMBERS_WIDTH		= 6;
	private static final int	LINE_NUMBERS_COLOR		= 0xffffff;
	
	private static final int	MIN_SELECTION_WIDTH		= 5;
	private static final int	SELECTION_COLOR			= 0x003B68;
	
	private static final double	SCROLL_LERP_SPEED		= 20.0;
	private static final double	SCROLL_CTRL_MULTIPLIER	= 5.0;
	private static final double	CURSOR_LERP_SPEED		= 50.0;
	private static final double	LINE_DELETE_LERP_SPEED	= 150.0;
	
	private static final int	TAB_SIZE				= 4;
	
	
	private EditorView			view;
	private Canvas				canvas;
	
	private File				file;
	private boolean				changed;
	
	private List<Line>			lines;
	private Tokenizer			tokenizer;
	private Highlighter			highlighter;
	
	private int					cursorX, cursorY;
	private int					uncappedCursorX;
	private double				tmpCursorX;
	private long				lastFrame;
	private long				cursorTimer;
	
	private double				dt;
	
	private double				scrollY;
	private double				destScrollY;
	
	private Selection			selection;
	
	
	public TextArea(EditorView view, File file, double width, double height) {
		this.view = view;
		this.canvas = new Canvas(width, height);
		this.lines = new ArrayList<Line>();
		this.file = file;
		
		updateTitle();
		loadFile();
		
		this.tokenizer = tokenizer == null ? new DefaultTokenizer() : tokenizer;
		this.highlighter = highlighter == null ? new DefaultHighlighter() : highlighter;
		
		uncappedCursorX = -1;
		
		setContent(canvas);
	}
	
	private void updateTitle() {
		if (file == null) {
			setText("untitled" + (changed ? "*" : ""));
		} else {
			String[] path = file.getAbsolutePath().split("\\\\");
			String filename = path[path.length - 1];
			setText(filename + (changed ? "*" : ""));
		}
	}
	
	void onKeyPress(KeyEvent e) {
		int oldCursorX, oldCursorY;
		
		switch (e.getCode()) {
		case LEFT:
			oldCursorX = cursorX;
			oldCursorY = cursorY;
			
			if (cursorX > 0) {
				if (e.isControlDown()) {
					cursorX -= getPreviousWordSpan(cursorX, cursorY);
				} else {
					cursorX--;
				}
			} else if (cursorY > 0) {
				cursorY--;
				cursorX = lines.get(cursorY).raw.length();
				tmpCursorX = cursorX;
			}
			
			resetCursor(true);
			updateSelection(e, oldCursorX, oldCursorY);
			break;
		case RIGHT:
			oldCursorX = cursorX;
			oldCursorY = cursorY;
			
			if (cursorX < lines.get(cursorY).raw.length()) {
				if (e.isControlDown()) {
					cursorX += getNextWordSpan(cursorX, cursorY);
				} else {
					cursorX++;
				}
			} else if (cursorY < lines.size() - 1) {
				cursorY++;
				cursorX = 0;
				tmpCursorX = cursorX;
			}
			
			resetCursor(true);
			updateSelection(e, oldCursorX, oldCursorY);
			break;
		case UP:
			oldCursorX = cursorX;
			oldCursorY = cursorY;
			
			if (cursorY > 0) {
				cursorY--;
				if (uncappedCursorX == -1) {
					uncappedCursorX = cursorX;
				} else {
					cursorX = uncappedCursorX;
				}
				
				resetCursor(false);
				updateSelection(e, oldCursorX, oldCursorY);
			}
			break;
		case DOWN:
			oldCursorX = cursorX;
			oldCursorY = cursorY;
			
			if (cursorY < lines.size() - 1) {
				cursorY++;
				if (uncappedCursorX == -1) {
					uncappedCursorX = cursorX;
				} else {
					cursorX = uncappedCursorX;
				}
				
				resetCursor(false);
				updateSelection(e, oldCursorX, oldCursorY);
			}
			break;
		case END:
			oldCursorX = cursorX;
			oldCursorY = cursorY;
			
			if (e.isControlDown()) {
				cursorY = lines.size() - 1;
			}
			cursorX = lines.get(cursorY).raw.length();
			
			resetCursor(true);
			updateSelection(e, oldCursorX, oldCursorY);
			break;
		case HOME:
			oldCursorX = cursorX;
			oldCursorY = cursorY;
			
			if (e.isControlDown()) {
				cursorY = 0;
				cursorX = 0;
			} else {
				int indentation = getIndentation(cursorY);
				if (cursorX == indentation) {
					cursorX = 0;
				} else {
					cursorX = indentation;
				}
			}
			
			resetCursor(true);
			updateSelection(e, oldCursorX, oldCursorY);
			break;
		case BACK_SPACE:
			if (selection == null) {
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
					tmpCursorX = cursorX;
					joinLines(cursorY, cursorY + 1);
				}
			} else {
				deleteSelection();
			}
			resetCursor(true);
			break;
		case DELETE:
			if (selection == null) {
				if (cursorX < lines.get(cursorY).raw.length() - 1) {
					if (e.isControlDown()) {
						int len = getNextWordSpan(cursorX, cursorY);
						for (int i = 0; i < len; i++) {
							deleteCharacter(cursorX, cursorY);
						}
					} else {
						deleteCharacter(cursorX, cursorY);
					}
				} else if (cursorY < lines.size() - 1) {
					joinLines(cursorY, cursorY + 1);
				}
			} else {
				deleteSelection();
			}
			resetCursor(true);
			break;
		case ENTER:
			if (selection != null) {
				deleteSelection();
			}
			
			int indentation = getIndentation(cursorY);
			if (cursorX == lines.get(cursorY).raw.length()) {
				insertLine(cursorY + 1, createString(' ', indentation));
				cursorY++;
				cursorX = indentation;
				tmpCursorX = cursorX;
			} else {
				String thisLine = lines.get(cursorY).raw.substring(0, cursorX);
				String nextLine = lines.get(cursorY).raw.substring(cursorX);
				lines.get(cursorY).setLine(thisLine);
				insertLine(cursorY + 1, createString(' ', indentation) + nextLine);
				cursorY++;
				cursorX = indentation;
				tmpCursorX = cursorX;
			}
			resetCursor(true);
			break;
		case TAB:
			if (selection != null) {
				deleteSelection();
			}
			
			for (int i = 0; i < TAB_SIZE; i++) {
				insertCharacter(cursorX + i, cursorY, " ");
			}
			cursorX += TAB_SIZE;
			resetCursor(true);
			break;
		case D:
			if (selection == null) {
				if (e.isControlDown()) {
					if (cursorY < lines.size() - 1) {
						deleteLine(cursorY);
					} else {
						lines.get(cursorY).setLine("");
					}
					cursorX = 0;
					resetCursor(true);
				}
			} else {
				deleteSelection();
			}
			break;
		case A:
			if (e.isControlDown()) {
				if (selection == null) {
					selection = new Selection();
				}
				selection.startX = 0;
				selection.startY = 0;
				selection.endX = lines.get(lines.size() - 1).raw.length();
				selection.endY = lines.size() - 1;
				
				cursorX = lines.get(lines.size() - 1).raw.length();
				cursorY = lines.size() - 1;
				
				resetCursor(true);
			}
			break;
		case S:
			if (e.isControlDown()) {
				saveFile(e.isShiftDown());
				notifySaved();
				updateTitle();
			}
			break;
		default:
			break;
		}
	}
	
	private String createString(char c, int n) {
		StringBuilder str = new StringBuilder();
		for (int i = 0; i < n; i++) {
			str.append(c);
		}
		return str.toString();
	}
	
	private void updateSelection(KeyEvent e, int oldCursorX, int oldCursorY) {
		if (e.isShiftDown()) {
			if (oldCursorX != cursorX || oldCursorY != cursorY) {
				if (selection == null) {
					selection = new Selection();
					selection.startX = oldCursorX;
					selection.startY = oldCursorY;
				}
				selection.endX = cursorX;
				selection.endY = cursorY;
			}
		} else {
			selection = null;
		}
	}
	
	private void resetCursor(boolean capCursor) {
		cursorX = Math.min(lines.get(cursorY).raw.length(), cursorX);
		cursorTimer = 0;
		if (getLineYPixels(cursorY - 1) - (int) scrollY < 0) {
			destScrollY = getLineYPixels(cursorY - 1);
		}
		if (getLineYPixels(cursorY + 1) - (int) scrollY > canvas.getHeight()) {
			destScrollY = getLineYPixels(cursorY + 1) - canvas.getHeight() + PADDING_TOP;
		}
		if (capCursor) {
			uncappedCursorX = -1;
		}
	}
	
	void onKeyTyped(KeyEvent e) {
		if (e.getCharacter().length() > 0 && !Character.isISOControl(e.getCharacter().toCharArray()[0])) {
			insertCharacter(cursorX, cursorY, e.getCharacter());
			cursorX++;
			resetCursor(true);
		}
	}
	
	void onScroll(ScrollEvent e) {
		destScrollY -= e.getDeltaY() * (e.isControlDown() ? SCROLL_CTRL_MULTIPLIER : 1.0);
		destScrollY = Math.max(destScrollY, 0.0);
		destScrollY = Math.min(destScrollY, getLineYPixels(lines.size() - 2));
	}
	
	void onResize(double w, double h) {
		canvas.setWidth(w);
		canvas.setHeight(h);
	}
	
	private int getLineYPixels(int y) {
		return y * FONT_SIZE + FONT_SIZE;
	}
	
	private void deleteSelection() {
		//		int yy = 0;
		//		for (int y = selection.getStartY(); y <= selection.getEndY(); y++) {
		//			if (y > selection.getStartY() && y < selection.getEndY()) {
		//				deleteLine(y + yy);
		//			} else if (y == selection.getStartY()) {
		//				int selectionStart = selection.getStartX();
		//				int selectionEnd = y == selection.getEndY() ? selection.getEndX() : lines.get(y).raw.length();
		//				for (int x = 0; x < selectionEnd - selectionStart; x++) {
		//					deleteCharacter(selectionStart, y);
		//				}
		//				if (y < selection.getEndY()) {
		//					joinLines(y + yy, y + yy + 1);
		//					//deleteLine(y + yy);
		//				} else {
		//					yy++;
		//				}
		//			} else if (y == selection.getEndY()) {
		//				int selectionStart = 0;
		//				int selectionEnd = selection.getEndX();
		//				for (int x = 0; x < selectionEnd - selectionStart; x++) {
		//					deleteCharacter(selectionStart, y);
		//				}
		//				yy++;
		//			}
		//		}
		
		String firstLineBegin = lines.get(selection.getStartY()).raw.substring(0, selection.getStartX());
		String firstLineEnd = lines.get(selection.getEndY()).raw.substring(selection.getEndX());
		lines.get(selection.getStartY()).setLine(firstLineBegin + firstLineEnd);
		
		for (int y = selection.getStartY() + 1; y <= selection.getEndY(); y++) {
			deleteLine(selection.getStartY() + 1);
		}
		
		cursorX = selection.getStartX();
		cursorY = selection.getStartY();
		
		selection = null;
	}
	
	private void insertLine(int y, String text) {
		lines.add(y, new Line(this, text));
		notifyChanged();
	}
	
	private void joinLines(int first, int second) {
		lines.get(first).setLine(lines.get(first).raw + lines.get(second).raw);
		lines.remove(second);
		notifyChanged();
	}
	
	private void insertCharacter(int x, int y, String chars) {
		String line = lines.get(y).raw;
		String newLine = line.substring(0, x) + chars + line.substring(x);
		lines.get(y).setLine(newLine);
		notifyChanged();
	}
	
	private void deleteCharacter(int x, int y) {
		String line = lines.get(y).raw;
		String newLine = line.substring(0, x) + line.substring(x + 1);
		lines.get(y).setLine(newLine);
		notifyChanged();
	}
	
	private void deleteLine(int y) {
		if (y < lines.size() - 1) {
			lines.get(y + 1).offset += lines.get(y + 1).offset + 1.0;
		}
		lines.remove(y);
		notifyChanged();
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
		if (!spaces && i > 0) {
			i++;
		}
		i = Math.min(i, start - 1);
		
		return start - i;
	}
	
	private int getIndentation(int y) {
		char[] lineChars = lines.get(y).raw.toCharArray();
		for (int i = 0; i < lineChars.length; i++) {
			if (lineChars[i] != ' ') {
				return i;
			}
		}
		return lineChars.length;
	}
	
	private boolean isLetterOrDigit(char c) {
		return Character.isLetter(c) || Character.isDigit(c);
	}
	
	private void notifyChanged() {
		changed = true;
		updateTitle();
	}
	
	private void notifySaved() {
		changed = false;
		updateTitle();
	}
	
	private void loadFile() {
		if (file == null) {
			return;
		}
		
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
	
	private void saveFile(boolean saveDialog) {
		File dest = null;
		if (file == null || saveDialog) {
			FileChooser fileChooser = new FileChooser();
			if (file != null) {
				fileChooser.setInitialDirectory(file.getParentFile());
				fileChooser.setInitialFileName(file.getName());
			}
			dest = fileChooser.showSaveDialog(null);
			file = dest;
		} else {
			dest = file;
		}
		
		writeToFile(dest);
	}
	
	private void writeToFile(File dest) {
		if (!dest.exists()) {
			try {
				dest.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		try (PrintWriter out = new PrintWriter(dest)) {
			for (Line line : lines) {
				out.println(line.raw);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public File getFile() {
		return file;
	}
	
	public void update() {
		long now = System.currentTimeMillis();
		long delta = now - lastFrame;
		dt = delta / 1000.0;
		lastFrame = now;
		
		cursorTimer += delta;
		
		scrollY = lerp(scrollY, destScrollY, SCROLL_LERP_SPEED * dt);
		tmpCursorX = lerp(tmpCursorX, cursorX, CURSOR_LERP_SPEED * dt);
		
		view.requestLayout();
	}
	
	private double lerp(double d0, double d1, double blend) {
		return d0 + (d1 - d0) * blend;
	}
	
	public void draw() {
		GraphicsContext g = canvas.getGraphicsContext2D();
		
		g.setFill(Color.rgb((BACKGROUND_COLOR & 0xFF0000) >> 16, (BACKGROUND_COLOR & 0x00FF00) >> 8, (BACKGROUND_COLOR & 0x0000FF) >> 0));
		g.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
		g.setFont(Font.font(FONT_FACE, FontWeight.BLACK, FontPosture.REGULAR, FONT_SIZE));
		
		boolean cursorActive = cursorTimer % 1000 < 500;
		
		int linenumbersWidthPixels = getWordLengthPixels(g.getFont(), " ") * LINE_NUMBERS_WIDTH;
		int y = FONT_SIZE - (int) scrollY + PADDING_TOP;
		
		for (int i = 0; i < lines.size(); i++) {
			int x = linenumbersWidthPixels;
			
			y += lines.get(i).offset * FONT_SIZE;
			if (y < 0) {
				y += FONT_SIZE;
				continue;
			}
			if (y > canvas.getHeight() + g.getFont().getSize()) {
				i = lines.size();
				break;
			}
			
			Color linenumberColor = getColor(LINE_NUMBERS_COLOR);
			String linenumberString = Integer.toString(i + 1);
			g.setFill(linenumberColor);
			g.fillText(linenumberString, linenumbersWidthPixels - getWordLengthPixels(g.getFont(), linenumberString) - getWordLengthPixels(g.getFont(), " "), y);
			
			if (selection != null && selection.getStartY() <= i && selection.getEndY() >= i) {
				int selectionStartX = -1, selectionWidth = -1;
				
				if (selection.getStartY() < i) {
					selectionStartX = 0;
				} else if (selection.getStartY() == i) {
					selectionStartX = getWordLengthPixels(g.getFont(), lines.get(i).raw.substring(0, selection.getStartX()));
				}
				
				if (selection.getEndY() > i) {
					selectionWidth = getWordLengthPixels(g.getFont(), lines.get(i).raw) - selectionStartX;
				} else if (selection.getEndY() == i) {
					selectionWidth = getWordLengthPixels(g.getFont(), lines.get(i).raw.substring(0, selection.getEndX())) - selectionStartX;
				}
				
				if (i < selection.getEndY()) {
					selectionWidth = Math.max(selectionWidth, MIN_SELECTION_WIDTH);
				}
				
				if (selectionStartX != -1 && selectionWidth != -1) {
					g.setFill(getColor(SELECTION_COLOR));
					//g.fillRect(x + selectionStartX, y - FONT_SIZE * 13 / 16, selectionWidth, FONT_SIZE);
					g.fillRoundRect(x + selectionStartX, y - FONT_SIZE * 13 / 16, selectionWidth, FONT_SIZE, 5, 5);
				}
			}
			
			for (int j = 0; j < lines.get(i).tokens.size(); j++) {
				String str = lines.get(i).tokens.get(j).str;
				String type = lines.get(i).tokens.get(j).type;
				
				int color = type == null ? highlighter.getDefaultColor() : highlighter.getColor(type);
				Color fillColor = Color.rgb((color & 0xFF0000) >> 16, (color & 0x00FF00) >> 8, (color & 0x0000FF) >> 0);
				
				g.setFill(fillColor);
				g.fillText(str, x, y);
				
				int tokenLengthPixels = getWordLengthPixels(g.getFont(), str);
				x += tokenLengthPixels;
			}
			if (lines.get(i).offset > 0.001) {
				lines.get(i).offset = lerp(lines.get(i).offset, 0.0, LINE_DELETE_LERP_SPEED * dt);
				if (lines.get(i).offset < 0.001) {
					lines.get(i).offset = 0.0;
				}
			}
			
			if (cursorActive && i == cursorY) {
				g.setFill(getColor(CURSOR_COLOR));
				double lengthPixels = getWordLengthPixels(g.getFont(), " ") * tmpCursorX;
				lengthPixels = getWordLengthPixels(g.getFont(), lines.get(i).raw.substring(0, Math.min(lines.get(i).raw.length(), cursorX)))
						+ getWordLengthPixels(g.getFont(), " ") * (tmpCursorX - cursorX);
				//lengthPixels = getWordLengthPixels(g.getFont(), lines.get(i).raw.substring(0, Math.min(lines.get(i).raw.length(), (int) Math.floor(tmpCursorX))))
				//		+ getWordLengthPixels(g.getFont(), " ") * (tmpCursorX - (int) Math.floor(tmpCursorX));
				//System.out.println(getWordLengthPixels(g.getFont(), lines.get(i).raw.substring(0, Math.min(lines.get(i).raw.length(), (int) Math.floor(tmpCursorX))))
				//		+ getWordLengthPixels(g.getFont(), " ") * (tmpCursorX - (int) Math.floor(tmpCursorX)));
				g.fillText("|", linenumbersWidthPixels + lengthPixels - 1, y);
				g.fillText("|", linenumbersWidthPixels + lengthPixels - 2, y);
			}
			
			y += FONT_SIZE;
		}
		
		g.setFill(Color.rgb((BACKGROUND_COLOR & 0xFF0000) >> 16, (BACKGROUND_COLOR & 0x00FF00) >> 8, (BACKGROUND_COLOR & 0x0000FF) >> 0));
		g.fillRect(0, 0, canvas.getWidth(), PADDING_TOP);
	}
	
	private Color getColor(int hex) {
		return Color.rgb((hex & 0xFF0000) >> 16, (hex & 0x00FF00) >> 8, (hex & 0x0000FF) >> 0);
	}
	
	private int getWordLengthPixels(Font font, String str) {
		Text text = new Text(str);
		text.setFont(font);
		Bounds tb = text.getBoundsInLocal();
		return (int) tb.getWidth();
	}
	
	private static class Line {
		private TextArea	area;
		private double		offset;
		private String		raw;
		private List<Token>	tokens;
		
		
		private Line(TextArea area, String line) {
			this.area = area;
			offset = 0.0;
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
