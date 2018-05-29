using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

class CppTokenizer : DefaultTokenizer
{
    public CppTokenizer()
    {
        name = "C++";
    }

    protected override void AddRegexes()
    {
        ProcessSingleLineComments("//", TokenType.SingleLineComment);
        ProcessBeginRegex("#", TokenType.Preprocessor, true);
        ProcessBeginEndRegex('"', '"', TokenType.StringLiteral);
        ProcessBeginEndRegex('<', '>', TokenType.StringLiteral);

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
        ProcessRegex(".", TokenType.Digit);

        ProcessRegex("void", TokenType.PrimitiveType);
        ProcessRegex("char", TokenType.PrimitiveType);
        ProcessRegex("short", TokenType.PrimitiveType);
        ProcessRegex("int", TokenType.PrimitiveType);
        ProcessRegex("long", TokenType.PrimitiveType);
        ProcessRegex("unsigned", TokenType.PrimitiveType);
        ProcessRegex("float", TokenType.PrimitiveType);
        ProcessRegex("double", TokenType.PrimitiveType);

        ProcessRegex("struct", TokenType.Keyword);
        ProcessRegex("class", TokenType.Keyword);
        ProcessRegex("enum", TokenType.Keyword);
        ProcessRegex("typedef", TokenType.Keyword);

        ProcessRegex("private", TokenType.Keyword);
        ProcessRegex("protected", TokenType.Keyword);
        ProcessRegex("public", TokenType.Keyword);
        ProcessRegex("static", TokenType.Keyword);
        ProcessRegex("const", TokenType.Keyword);
        ProcessRegex("extern", TokenType.Keyword);
        ProcessRegex("virtual", TokenType.Keyword);
        ProcessRegex("abstract", TokenType.Keyword);
        ProcessRegex("friend", TokenType.Keyword);
        ProcessRegex("default", TokenType.Keyword);
        ProcessRegex("nullptr", TokenType.Keyword);
        ProcessRegex("new", TokenType.Keyword);
        ProcessRegex("delete", TokenType.Keyword);
    }
}