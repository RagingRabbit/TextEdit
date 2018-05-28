package com.rb.edit;

import java.util.ArrayList;
import java.util.List;

public class Tokenizer {
	private List<Rule> rules;
	
	
	public Tokenizer() {
		rules = new ArrayList<Rule>();
	}
	
	public void addRule(Rule rule) {
		rules.add(rule);
	}
	
	public List<Token> tokenize(String str) {
		List<Token> tokens = new ArrayList<Token>();
		Token startToken = new Token(str, null);
		tokens.add(startToken);
		
		int ruleIndex = 0;
		List<Token> processedTokens = processRule(startToken, ruleIndex);
		
		return processedTokens;
	}
	
	private List<Token> processRule(Token token, int ruleIndex) {
		List<Token> newTokens = new ArrayList<Token>();
		Rule rule = rules.get(ruleIndex);
		
		int lastIndex = -1;
		int currentIndex = -1;
		
		boolean found = false;
		while (token.str.indexOf(rule.regex, currentIndex + 1) != -1) {
			currentIndex = token.str.indexOf(rule.regex, currentIndex + 1);
			found = true;
			
			if (lastIndex == -1) {
				if (currentIndex > 0) {
					// Add two tokens
					Token preRegexToken = new Token(token.str.substring(0, currentIndex), token.type);
					Token regexToken = new Token(rule.regex, rule.type);
					
					newTokens.add(preRegexToken);
					newTokens.add(regexToken);
				} else {
					// Add this token
					Token regexToken = new Token(rule.regex, rule.type);
					newTokens.add(regexToken);
				}
			} else {
				int lastTokenEnd = lastIndex + rule.regex.length();
				if (currentIndex > lastTokenEnd) {
					// Add two tokens
					Token preRegexToken = new Token(token.str.substring(lastTokenEnd, currentIndex), token.type);
					Token regexToken = new Token(rule.regex, rule.type);
					
					newTokens.add(preRegexToken);
					newTokens.add(regexToken);
				} else {
					// Add this token
					Token regexToken = new Token(rule.regex, rule.type);
					newTokens.add(regexToken);
				}
			}
			
			lastIndex = currentIndex;
			
			if (currentIndex + rule.regex.length() < token.str.length() && token.str.indexOf(rule.regex, currentIndex + 1) == -1) {
				// This was the last found occurence of the regex
				Token lastToken = new Token(token.str.substring(currentIndex + rule.regex.length()), token.type);
				newTokens.add(lastToken);
				
				break;
			}
		}
		
		if (!found) {
			newTokens.add(token);
		}
		
		if (ruleIndex < rules.size() - 1) {
			// If there are still rules to apply
			for (int i = 0; i < newTokens.size(); i += 0) {
				Token newToken = newTokens.get(i);
				List<Token> processedTokens = processRule(newToken, ruleIndex + 1);
				newTokens.remove(i);
				newTokens.addAll(i, processedTokens);
				
				i += processedTokens.size();
			}
		}
		
		return newTokens;
	}
	
	
	public static class Rule {
		public String	regex;
		public String	type;
		
		
		public Rule(String regex, String type) {
			this.regex = regex;
			this.type = type;
		}
	}
	
	public static class Token {
		public String	str;
		public String	type;
		
		
		public Token(String s, String t) {
			str = s;
			type = t;
			//col = 0xABB2BF;
		}
	}
}
