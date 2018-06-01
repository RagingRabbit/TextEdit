package com.rb.edit.highlighting.lang;

import com.rb.edit.Highlighter;
import com.rb.edit.Tokenizer;

public class Cs {
	public static class CsTokenizer extends Tokenizer {
		public CsTokenizer() {
			addRule(new BeginRule("//", "single-line-comment"));
			addRule(new BeginEndRule("\"", "\"", "string-literal"));
			
			addRule(new KeywordRule("(", "bracket", false));
			addRule(new KeywordRule(")", "bracket", false));
			addRule(new KeywordRule("{", "curly-brace", false));
			addRule(new KeywordRule("}", "curly-brace", false));
			addRule(new KeywordRule("[", "square-brace", false));
			addRule(new KeywordRule("]", "square-brace", false));
			addRule(new KeywordRule(";", "semicolon", false));
			addRule(new KeywordRule(":", "colon", false));
			addRule(new KeywordRule("0", "digit", false));
			addRule(new KeywordRule("1", "digit", false));
			addRule(new KeywordRule("2", "digit", false));
			addRule(new KeywordRule("3", "digit", false));
			addRule(new KeywordRule("4", "digit", false));
			addRule(new KeywordRule("5", "digit", false));
			addRule(new KeywordRule("6", "digit", false));
			addRule(new KeywordRule("7", "digit", false));
			addRule(new KeywordRule("8", "digit", false));
			addRule(new KeywordRule("9", "digit", false));
			addRule(new KeywordRule("\\.", "digit", false));
			addRule(new KeywordRule("+", "operator", false));
			addRule(new KeywordRule("-", "operator", false));
			addRule(new KeywordRule("*", "operator", false));
			addRule(new KeywordRule("/", "operator", false));
			addRule(new KeywordRule("&", "operator", false));
			addRule(new KeywordRule("|", "operator", false));
			addRule(new KeywordRule("^", "operator", false));
			
			addRule(new KeywordRule("private", "keyword", true));
			addRule(new KeywordRule("protected", "keyword", true));
			addRule(new KeywordRule("public", "keyword", true));
			addRule(new KeywordRule("static", "keyword", true));
			addRule(new KeywordRule("const", "keyword", true));
			addRule(new KeywordRule("extern", "keyword", true));
			addRule(new KeywordRule("virtual", "keyword", true));
			addRule(new KeywordRule("abstract", "keyword", true));
			addRule(new KeywordRule("internal", "keyword", true));
			addRule(new KeywordRule("sealed", "keyword", true));
			addRule(new KeywordRule("base", "keyword", true));
			addRule(new KeywordRule("default", "keyword", true));
			addRule(new KeywordRule("null", "keyword", true));
			addRule(new KeywordRule("new", "keyword", true));
			addRule(new KeywordRule("explicit", "keyword", true));
			addRule(new KeywordRule("implicit", "keyword", true));
			addRule(new KeywordRule("using", "keyword", true));
			addRule(new KeywordRule("get", "keyword", true));
			addRule(new KeywordRule("set", "keyword", true));
			
			addRule(new KeywordRule("void", "keyword", true));
			addRule(new KeywordRule("char", "keyword", true));
			addRule(new KeywordRule("short", "keyword", true));
			addRule(new KeywordRule("int", "keyword", true));
			addRule(new KeywordRule("long", "keyword", true));
			addRule(new KeywordRule("float", "keyword", true));
			addRule(new KeywordRule("double", "keyword", true));
			addRule(new KeywordRule("byte", "keyword", true));
			addRule(new KeywordRule("uchar", "keyword", true));
			addRule(new KeywordRule("ushort", "keyword", true));
			addRule(new KeywordRule("uint", "keyword", true));
			addRule(new KeywordRule("ulong", "keyword", true));
			addRule(new KeywordRule("bool", "keyword", true));
			addRule(new KeywordRule("string", "keyword", true));
			
			addRule(new KeywordRule("struct", "keyword", true));
			addRule(new KeywordRule("class", "keyword", true));
			addRule(new KeywordRule("enum", "keyword", true));
		}
	}
	
	public static class CsHighlighter extends Highlighter {
		public CsHighlighter() {
			setDefaultColor(0xcbcdcf);
			addColor("keyword", 0x0058d0);
			addColor("single-line-comment", 0xe04c55);
			addColor("string-literal", 0xb630ff);
			addColor("character-literal", 0xb630ff);
			addColor("preprocessor", 0xffa13d);
			addColor("bracket", 0x31a0e0);
			addColor("curly-brace", 0x459646);
			addColor("square-brace", 0x459646);
			addColor("semicolon", 0x459646);
			addColor("colon", 0x459646);
			addColor("digit", 0x8848ec);
			addColor("operator", 0x31a0e0);
		}
	}
}
