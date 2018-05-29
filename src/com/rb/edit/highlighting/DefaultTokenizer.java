package com.rb.edit.highlighting;

import com.rb.edit.Tokenizer;

public class DefaultTokenizer extends Tokenizer {
	public DefaultTokenizer() {
		addRule(new BeginRule("//", "single-line-comment"));
		addRule(new BeginEndRule("\"", "\"", "string-literal"));
		addRule(new BeginEndRule("'", "'", "character-literal"));
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
		addRule(new KeywordRule("+", "operator"));
		addRule(new KeywordRule("-", "operator"));
		addRule(new KeywordRule("*", "operator"));
		addRule(new KeywordRule("/", "operator"));
		addRule(new KeywordRule("&", "operator"));
		addRule(new KeywordRule("|", "operator"));
		addRule(new KeywordRule("^", "operator"));
	}
}
