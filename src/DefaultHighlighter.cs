using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Drawing;

class DefaultHighlighter : Highlighter
{
    public DefaultHighlighter()
    {
        AddColor(TokenType.PlainText, 0xcbcdcf);
        AddColor(TokenType.SingleLineComment, 0xe04c55);
        AddColor(TokenType.StringLiteral, 0xb630ff);
        AddColor(TokenType.Parentheses, 0x31a0e0);
        AddColor(TokenType.CurlyBrackets, 0x459646);
        AddColor(TokenType.SquareBrackets, 0x459646);
        AddColor(TokenType.Semicolon, 0x459646);
        AddColor(TokenType.Colon, 0x459646);
        AddColor(TokenType.Digit, 0x8848ec);
        AddColor(TokenType.Operator, 0x31a0e0);

        AddColor(TokenType.Keyword, 0x0058d0);
        AddColor(TokenType.PrimitiveType, 0x0058d0);
        AddColor(TokenType.BuiltInType, 0x0058d0);
        AddColor(TokenType.Preprocessor, 0xffa13d);
    }
}