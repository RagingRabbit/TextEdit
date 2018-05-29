package com.rb.edit.highlighting.lang;

import com.rb.edit.Highlighter;
import com.rb.edit.Tokenizer;

public class Cs {
	public static class CsTokenizer extends Tokenizer {
		public CsTokenizer() {
			addRule(new BeginRule("//", "single-line-comment"));
			addRule(new BeginEndRule("\"", "\"", "string-literal"));
			
			addRule(new KeywordRule("(", "bracket"));
			addRule(new KeywordRule(")", "bracket"));
			addRule(new KeywordRule("{", "curly-brace"));
			addRule(new KeywordRule("}", "curly-brace"));
			addRule(new KeywordRule("[", "square-brace"));
			addRule(new KeywordRule("]", "square-brace"));
			addRule(new KeywordRule(";", "semicolon"));
			addRule(new KeywordRule(":", "colon"));
			addRule(new KeywordRule("0", "digit"));
			addRule(new KeywordRule("1", "digit"));
			addRule(new KeywordRule("2", "digit"));
			addRule(new KeywordRule("3", "digit"));
			addRule(new KeywordRule("4", "digit"));
			addRule(new KeywordRule("5", "digit"));
			addRule(new KeywordRule("6", "digit"));
			addRule(new KeywordRule("7", "digit"));
			addRule(new KeywordRule("8", "digit"));
			addRule(new KeywordRule("9", "digit"));
			addRule(new KeywordRule("\\.", "digit"));
			addRule(new KeywordRule("+", "operator"));
			addRule(new KeywordRule("-", "operator"));
			addRule(new KeywordRule("*", "operator"));
			addRule(new KeywordRule("/", "operator"));
			addRule(new KeywordRule("&", "operator"));
			addRule(new KeywordRule("|", "operator"));
			addRule(new KeywordRule("^", "operator"));
			
			addRule(new KeywordRule("private", "keyword"));
			addRule(new KeywordRule("protected", "keyword"));
			addRule(new KeywordRule("public", "keyword"));
			addRule(new KeywordRule("static", "keyword"));
			addRule(new KeywordRule("const", "keyword"));
			addRule(new KeywordRule("extern", "keyword"));
			addRule(new KeywordRule("virtual", "keyword"));
			addRule(new KeywordRule("abstract", "keyword"));
			addRule(new KeywordRule("internal", "keyword"));
			addRule(new KeywordRule("sealed", "keyword"));
			addRule(new KeywordRule("base", "keyword"));
			addRule(new KeywordRule("default", "keyword"));
			addRule(new KeywordRule("null", "keyword"));
			addRule(new KeywordRule("new", "keyword"));
			addRule(new KeywordRule("explicit", "keyword"));
			addRule(new KeywordRule("implicit", "keyword"));
			addRule(new KeywordRule("using", "keyword"));
			addRule(new KeywordRule("get", "keyword"));
			addRule(new KeywordRule("set", "keyword"));
			
			addRule(new KeywordRule("void", "keyword"));
			addRule(new KeywordRule("char", "keyword"));
			addRule(new KeywordRule("short", "keyword"));
			addRule(new KeywordRule("int", "keyword"));
			addRule(new KeywordRule("long", "keyword"));
			addRule(new KeywordRule("float", "keyword"));
			addRule(new KeywordRule("double", "keyword"));
			addRule(new KeywordRule("byte", "keyword"));
			addRule(new KeywordRule("uchar", "keyword"));
			addRule(new KeywordRule("ushort", "keyword"));
			addRule(new KeywordRule("uint", "keyword"));
			addRule(new KeywordRule("ulong", "keyword"));
			addRule(new KeywordRule("bool", "keyword"));
			addRule(new KeywordRule("string", "keyword"));
			
			addRule(new KeywordRule("struct", "keyword"));
			addRule(new KeywordRule("class", "keyword"));
			addRule(new KeywordRule("enum", "keyword"));
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
