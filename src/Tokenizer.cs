using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;


abstract class Tokenizer
{
    public string name { get; protected set; }

    private List<Token> tokens;

    protected Tokenizer(string s)
    {
        tokens = new List<Token>();
        name = s;
    }

    public Token[] GetTokens(string line)
    {
        InitLine(line, TokenType.PlainText);
        AddRegexes();

        return tokens.ToArray();
    }

    public Token[] GetPlainToken(string line)
    {
        InitLine(line, TokenType.PlainText);
        return tokens.ToArray();
    }

    protected abstract void AddRegexes();

    private void InitLine(string str, TokenType defaultType)
    {
        tokens.Clear();
        tokens.Add(new Token { str = str, type = defaultType });
    }

    protected void ProcessRegex(string regex, TokenType type)
    {
        List<Token> tmpTokens = new List<Token>();
        tmpTokens.AddRange(tokens);

        for (int i = 0; i < tokens.Count; i++)
        {
            Token token = tokens[i];
            if (token.ignore)
            {
                continue;
            }

            List<Token> newTokens = new List<Token>();
            bool regexFound = false;
            int currentIndex = -1;
            int lastIndex = -1;
            while (token.str.IndexOf(regex, currentIndex + 1) != -1)
            {
                currentIndex = token.str.IndexOf(regex, currentIndex + 1);
                regexFound = true;

                if (lastIndex == -1)
                {
                    if (currentIndex > 0)
                    {
                        // Add two tokens
                        Token preRegexToken = new Token
                        {
                            str = token.str.Substring(0, currentIndex),
                            type = token.type
                        };

                        Token regexToken = new Token
                        {
                            str = regex,
                            type = type
                        };

                        newTokens.Add(preRegexToken);
                        newTokens.Add(regexToken);
                    }
                    else
                    {
                        // Just add this token
                        Token regexToken = new Token
                        {
                            str = regex,
                            type = type
                        };

                        newTokens.Add(regexToken);
                    }
                }
                else
                {
                    int lastTokenEnd = lastIndex + regex.Length;
                    if (currentIndex > lastTokenEnd)
                    {
                        // Add two tokens
                        Token preRegexToken = new Token
                        {
                            str = token.str.Substring(lastTokenEnd, currentIndex - lastTokenEnd),
                            type = token.type
                        };

                        Token regexToken = new Token
                        {
                            str = regex,
                            type = type
                        };

                        newTokens.Add(preRegexToken);
                        newTokens.Add(regexToken);
                    }
                    else
                    {
                        // Just add this token
                        Token regexToken = new Token
                        {
                            str = regex,
                            type = type
                        };

                        newTokens.Add(regexToken);
                    }
                }

                lastIndex = currentIndex;

                if (currentIndex + regex.Length < token.str.Length && token.str.IndexOf(regex, currentIndex + 1) == -1)
                {
                    // This was the last found occurence of the regex
                    Token lastToken = new Token
                    {
                        str = token.str.Substring(currentIndex + regex.Length),
                        type = token.type
                    };

                    newTokens.Add(lastToken);

                    break;
                }
            }

            if (regexFound)
            {
                int tokenIndex = tmpTokens.IndexOf(token);
                tmpTokens.RemoveAt(tokenIndex);
                tmpTokens.InsertRange(tokenIndex, newTokens);
            }
        }

        tokens.Clear();
        tokens.AddRange(tmpTokens);
    }

    protected void ProcessBeginRegex(string regex, TokenType type, bool ignoreNext)
    {
        List<Token> tmpTokens = new List<Token>();
        tmpTokens.AddRange(tokens);

        for (int i = 0; i < tokens.Count; i++)
        {
            Token token = tokens[i];

            List<Token> newTokens = new List<Token>();
            bool regexFound = false;

            if (token.str.IndexOf(regex) != -1)
            {
                regexFound = true;

                int startIndex = token.str.IndexOf(regex);
                int spaceIndex = token.str.IndexOf(' ', startIndex);

                Token preRegexToken = null;
                Token regexToken = null;
                Token postRegexToken = null;

                if (startIndex > 0)
                {
                    preRegexToken = new Token
                    {
                        str = token.str.Substring(0, startIndex),
                        type = token.type
                    };
                }
                if (spaceIndex == -1)
                {
                    if (startIndex > 0)
                    {
                        regexToken = new Token
                        {
                            str = token.str.Substring(startIndex),
                            type = type,
                            ignore = ignoreNext
                        };
                    }
                    else
                    {
                        regexToken = new Token
                        {
                            str = token.str,
                            type = type,
                            ignore = ignoreNext
                        };
                    }
                }
                else
                {
                    if (startIndex > 0)
                    {
                        regexToken = new Token
                        {
                            str = token.str.Substring(startIndex, spaceIndex - startIndex),
                            type = type,
                            ignore = ignoreNext
                        };
                    }
                    else
                    {
                        regexToken = new Token
                        {
                            str = token.str.Substring(0, spaceIndex),
                            type = type,
                            ignore = ignoreNext
                        };
                    }
                }
                if (spaceIndex != -1)
                {
                    postRegexToken = new Token
                    {
                        str = token.str.Substring(spaceIndex),
                        type = token.type
                    };
                }

                if (preRegexToken != null)
                {
                    newTokens.Add(preRegexToken);
                }
                if (regexToken != null)
                {
                    newTokens.Add(regexToken);
                }
                if (postRegexToken != null)
                {
                    newTokens.Add(postRegexToken);
                }
            }

            if (regexFound)
            {
                int tokenIndex = tmpTokens.IndexOf(token);
                tmpTokens.RemoveAt(tokenIndex);
                tmpTokens.InsertRange(tokenIndex, newTokens);
            }
        }

        tokens.Clear();
        tokens.AddRange(tmpTokens);
    }

    protected void ProcessBeginEndRegex(char begin, char end, TokenType type)
    {
        List<Token> tmpTokens = new List<Token>();
        tmpTokens.AddRange(tokens);

        for (int i = 0; i < tokens.Count; i++)
        {
            Token token = tokens[i];

            if (token.ignore)
            {
                continue;
            }

            int start = -1;
            for (int j = 0; j < token.str.Length; j++)
            {
                if (token.str[j] == begin && start == -1)
                {
                    start = j;
                }
                else if (token.str[j] == end && start != -1)
                {
                    j++;

                    Token preStringToken = start > 0 ? new Token
                    {
                        str = token.str.Substring(0, start),
                        type = token.type
                    } : null;

                    Token stringToken = new Token
                    {
                        str = token.str.Substring(start, j - start),
                        type = type,
                        ignore = true
                    };

                    Token postStringToken = j < token.str.Length ? new Token
                    {
                        str = token.str.Substring(j),
                        type = token.type
                    } : null;

                    tmpTokens.RemoveAt(i);
                    if (postStringToken != null)
                    {
                        tmpTokens.Insert(i, postStringToken);
                    }
                    tmpTokens.Insert(i, stringToken);
                    if (preStringToken != null)
                    {
                        tmpTokens.Insert(i, preStringToken);
                    }
                }
            }
        }

        tokens.Clear();
        tokens.AddRange(tmpTokens);
    }

    protected void ProcessSingleLineComments(string regex, TokenType type)
    {
        for (int i = 0; i < tokens.Count; i++)
        {
            Token token = tokens[i];

            if (token.str.StartsWith(regex))
            {
                token.type = type;
                token.ignore = true;
            }
        }
    }
}

public class Token
{
    public string str;
    public TokenType type;
    public bool ignore;

    public override string ToString()
    {
        return str;
    }
}

public enum TokenType
{
    PlainText,
    SingleLineComment,
    StringLiteral,
    Parentheses,
    CurlyBrackets,
    SquareBrackets,
    Semicolon,
    Colon,
    Digit,
    Operator,

    Keyword,
    PrimitiveType,
    BuiltInType,
    Preprocessor
}