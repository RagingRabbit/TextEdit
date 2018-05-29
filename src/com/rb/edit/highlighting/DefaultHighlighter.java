package com.rb.edit.highlighting;

import com.rb.edit.Highlighter;

public class DefaultHighlighter extends Highlighter {
	public DefaultHighlighter() {
		setDefaultColor(0xcbcdcf);
		addColor("single-line-comment", 0xe04c55);
		addColor("string-literal", 0xb630ff);
		addColor("character-literal", 0xb630ff);
		addColor("bracket", 0x31a0e0);
		addColor("curly-brace", 0x459646);
		addColor("square-brace", 0x459646);
		addColor("semicolon", 0x459646);
		addColor("colon", 0x459646);
		addColor("digit", 0x8848ec);
		addColor("operator", 0x31a0e0);
	}
}
