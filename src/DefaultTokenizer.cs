using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

class DefaultTokenizer : Tokenizer
{
    public DefaultTokenizer()
        : base("Plain Text")
    {
    }

    protected override void AddRegexes()
    {
        ProcessSingleLineComments("//", TokenType.SingleLineComment);
        ProcessBeginEndRegex('"', '"', TokenType.StringLiteral);

        ProcessRegex("(", TokenType.Parentheses);
        ProcessRegex(")", TokenType.Parentheses);
        ProcessRegex("{", TokenType.CurlyBrackets);
        ProcessRegex("}", TokenType.CurlyBrackets);
        ProcessRegex("[", TokenType.SquareBrackets);
        ProcessRegex("]", TokenType.SquareBrackets);
        ProcessRegex(";", TokenType.Semicolon);
        ProcessRegex(":", TokenType.Colon);
        ProcessRegex("0", TokenType.Digit);
        ProcessRegex("1", TokenType.Digit);
        ProcessRegex("2", TokenType.Digit);
        ProcessRegex("3", TokenType.Digit);
        ProcessRegex("4", TokenType.Digit);
        ProcessRegex("5", TokenType.Digit);
        ProcessRegex("6", TokenType.Digit);
        ProcessRegex("7", TokenType.Digit);
        ProcessRegex("8", TokenType.Digit);
        ProcessRegex("9", TokenType.Digit);
        ProcessRegex("+", TokenType.Operator);
        ProcessRegex("-", TokenType.Operator);
        ProcessRegex("*", TokenType.Operator);
        ProcessRegex("/", TokenType.Operator);
        ProcessRegex("&", TokenType.Operator);
        ProcessRegex("|", TokenType.Operator);
        ProcessRegex("^", TokenType.Operator);
    }
}