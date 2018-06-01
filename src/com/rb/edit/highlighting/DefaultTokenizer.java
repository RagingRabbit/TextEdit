package com.rb.edit.highlighting;

import com.rb.edit.Tokenizer;

public class DefaultTokenizer extends Tokenizer {
	public DefaultTokenizer() {
		addRule(new BeginRule("//", "single-line-comment"));
		addRule(new BeginEndRule("\"", "\"", "string-literal"));
		addRule(new BeginEndRule("'", "'", "character-literal"));
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
		addRule(new KeywordRule("+", "operator", false));
		addRule(new KeywordRule("-", "operator", false));
		addRule(new KeywordRule("*", "operator", false));
		addRule(new KeywordRule("/", "operator", false));
		addRule(new KeywordRule("&", "operator", false));
		addRule(new KeywordRule("|", "operator", false));
		addRule(new KeywordRule("^", "operator", false));
	}
}
