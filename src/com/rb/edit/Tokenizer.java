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
		if (ruleIndex >= rules.size()) {
			return null;
		}
		
		List<Token> newTokens = new ArrayList<Token>();
		Rule rule = rules.get(ruleIndex);
		
		int lastIndex = -1;
		int currentIndex = -1;
		
		boolean found = false;
		int[] indices;
		while ((indices = rule.getIndices(token.str, currentIndex + 1)) != null) {
			currentIndex = indices[0];
			found = true;
			int tokenLength = indices[1] - indices[0];
			
			if (lastIndex == -1) {
				if (currentIndex > 0) {
					// Add two tokens
					Token preRegexToken = new Token(token.str.substring(0, currentIndex), token.type);
					Token regexToken = new Token(rule.getSequence(token.str, indices), rule.type);
					
					List<Token> processedTokens = processRule(preRegexToken, ruleIndex + 1);
					if (processedTokens != null) {
						newTokens.addAll(processedTokens);
					} else {
						newTokens.add(preRegexToken);
					}
					
					//newTokens.add(preRegexToken);
					newTokens.add(regexToken);
				} else {
					// Add this token
					Token regexToken = new Token(rule.getSequence(token.str, indices), rule.type);
					newTokens.add(regexToken);
				}
			} else {
				int lastTokenEnd = lastIndex + tokenLength;
				if (currentIndex > lastTokenEnd) {
					// Add two tokens
					Token preRegexToken = new Token(token.str.substring(lastTokenEnd, currentIndex), token.type);
					Token regexToken = new Token(rule.getSequence(token.str, indices), rule.type);
					
					List<Token> processedTokens = processRule(preRegexToken, ruleIndex + 1);
					if (processedTokens != null) {
						newTokens.addAll(processedTokens);
					} else {
						newTokens.add(preRegexToken);
					}
					
					//newTokens.add(preRegexToken);
					newTokens.add(regexToken);
				} else {
					// Add this token
					Token regexToken = new Token(rule.getSequence(token.str, indices), rule.type);
					newTokens.add(regexToken);
				}
			}
			
			lastIndex = currentIndex;
			
			if (currentIndex + tokenLength < token.str.length() && rule.getIndices(token.str, currentIndex + 1) == null) {
				// This was the last found occurence of the regex
				Token lastToken = new Token(token.str.substring(currentIndex + tokenLength), token.type);
				
				List<Token> processedTokens = processRule(lastToken, ruleIndex + 1);
				if (processedTokens != null) {
					newTokens.addAll(processedTokens);
				} else {
					newTokens.add(lastToken);
				}
				
				break;
			}
		}
		
		if (!found) {
			List<Token> processedTokens = processRule(token, ruleIndex + 1);
			if (processedTokens != null) {
				newTokens.addAll(processedTokens);
			} else {
				newTokens.add(token);
			}
		}
		
		return newTokens;
	}
	
	
	public static abstract class Rule {
		protected String type;
		
		
		public Rule(String type) {
			this.type = type;
		}
		
		public abstract int[] getIndices(String input, int fromIndex);
		
		public abstract String getSequence(String token, int[] indices);
	}
	
	public static class KeywordRule extends Rule {
		private String regex;
		
		
		public KeywordRule(String regex, String type) {
			super(type);
			this.regex = regex;
		}
		
		@Override
		public int[] getIndices(String input, int fromIndex) {
			int index = input.indexOf(regex, fromIndex);
			if (index != -1) {
				int length = regex.length();
				return new int[] { index, index + length };
			}
			return null;
		}
		
		@Override
		public String getSequence(String token, int[] indices) {
			return token.substring(indices[0], indices[1]);
		}
	}
	
	public static class BeginRule extends Rule {
		private String begin;
		
		
		public BeginRule(String begin, String type) {
			super(type);
			this.begin = begin;
		}
		
		@Override
		public int[] getIndices(String input, int fromIndex) {
			int beginIndex = input.indexOf(begin, fromIndex);
			if (beginIndex != -1) {
				return new int[] { beginIndex, input.length() };
			}
			return null;
		}
		
		@Override
		public String getSequence(String token, int[] indices) {
			return token.substring(indices[0], indices[1]);
		}
	}
	
	public static class BeginEndRule extends Rule {
		private String	begin;
		private String	end;
		
		
		public BeginEndRule(String begin, String end, String type) {
			super(type);
			this.begin = begin;
			this.end = end;
		}
		
		@Override
		public int[] getIndices(String input, int fromIndex) {
			int beginIndex = input.indexOf(begin, fromIndex);
			int endIndex = input.indexOf(end, beginIndex + 1) + 1;
			if (beginIndex != -1 && endIndex > beginIndex) {
				return new int[] { beginIndex, endIndex };
			}
			return null;
		}
		
		@Override
		public String getSequence(String token, int[] indices) {
			return token.substring(indices[0], indices[1]);
		}
	}
	
	public static class Token {
		public String	str;
		public String	type;
		
		
		public Token(String s, String t) {
			str = s;
			type = t;
		}
	}
}
