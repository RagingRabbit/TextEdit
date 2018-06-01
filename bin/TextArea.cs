using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Diagnostics;
using System.Drawing;

using Win32;


class TextArea
{
    private const int HorizontalScrollDelta = 3;
    private const int TabSize = 4;
    private const bool TabSkip = true;

    private const int cursorForeground = 0x404040;
    private const int cursorBackground = 0xc0c0c0;


    public FileTab tab;
    private Cmd cmd;

    public int cursorX { get; private set; }
    public int cursorY { get; private set; }

    public List<TextLine> lines { get; private set; }
    public int scrollY { get; private set; }
    public int scrollX { get; private set; }

    private bool updateScrolling;


    public TextArea(FileTab fileTab, Cmd cmd, string[] content)
    {
        this.tab = fileTab;
        this.cmd = cmd;

        this.lines = new List<TextLine>();
        this.scrollX = 0;
        this.scrollY = 0;

        for (int i = 0; i < content.Length; i++)
        {
            TextLine line = new TextLine(this, cmd, content[i]);
            lines.Add(line);
        }
    }

    public bool RunCommand(string command)
    {
        switch (command)
        {
            case "edit_left":
                if (cursorX > 0)
                {
                    cursorX--;
                    DoTabSkip(false);
                }
                else if (cursorX == 0 && cursorY > 0)
                {
                    cursorY--;
                    cursorX = lines[cursorY].text.Length;
                }
                UpdateCursor();
                break;

            case "edit_right":
                if (cursorX < lines[cursorY].text.Length)
                {
                    cursorX++;
                    DoTabSkip(true);
                }
                else if (cursorX >= lines[cursorY].text.Length && cursorY < lines.Count - 1)
                {
                    cursorY++;
                    cursorX = 0;
                }
                UpdateCursor();
                break;

            case "edit_up":
                if (cursorY > 0)
                {
                    cursorY--;
                    cursorX = Math.Min(cursorX, lines[cursorY].text.Length);
                    DoTabSkip(true);
                }
                UpdateCursor();
                break;

            case "edit_down":
                if (cursorY < lines.Count - 1)
                {
                    cursorY++;
                    cursorX = Math.Min(cursorX, lines[cursorY].text.Length);
                    DoTabSkip(true);
                }
                UpdateCursor();
                break;

            case "edit_left_absolute":
                if (cursorX > 0)
                {
                    cursorX--;
                    DoTabSkip(false);
                }
                UpdateCursor();
                break;

            case "edit_right_absolute":
                cursorX++;
                DoTabSkip(true);
                UpdateCursor();
                break;

            case "edit_up_absolute":
                if (cursorY > 0)
                {
                    cursorY--;
                }
                UpdateCursor();
                break;

            case "edit_down_absolute":
                if (cursorY < lines.Count - 1)
                {
                    cursorY++;
                }
                UpdateCursor();
                break;

            case "edit_left_skip":
                if (cursorX > 0)
                {
                    int len = GetPreviousWordSpan(cursorX, cursorY);
                    cursorX -= len;
                }
                else
                {
                    RunCommand("edit_left");
                }
                UpdateCursor();
                break;

            case "edit_right_skip":
                if (cursorX < lines[cursorY].text.Length)
                {
                    int len = GetNextWordSpan(cursorX, cursorY);
                    cursorX += len;
                }
                else
                {
                    RunCommand("edit_right");
                }
                UpdateCursor();
                break;

            case "edit_left_scroll":
                if (scrollX > 0)
                {
                    int scrollDelta = Math.Min(scrollX, HorizontalScrollDelta);
                    scrollX -= scrollDelta;
                }
                break;

            case "edit_right_scroll":
                scrollX += HorizontalScrollDelta;
                break;

            case "edit_up_scroll":
                if (scrollY > 0)
                {
                    scrollY--;
                }
                break;

            case "edit_down_scroll":
                if (scrollY < lines.Count - 1)
                {
                    scrollY++;
                }
                break;

            case "edit_end":
                cursorX = lines[cursorY].text.Length;
                UpdateCursor();
                break;

            case "edit_home":
                cursorX = 0;
                UpdateCursor();
                break;

            case "edit_end_file":
                cursorY = lines.Count - 1;
                cursorX = lines[cursorY].text.Length;
                UpdateCursor();
                break;

            case "edit_home_file":
                cursorX = 0;
                cursorY = 0;
                UpdateCursor();
                break;

            case "edit_tab":
                NormalizeCursor();
                cursorX += InsertTab(cursorX, cursorY, TabSize);
                UpdateCursor();
                break;

            case "edit_return":
                NormalizeCursor();
                int indentation = EvaluateNextIndentation(cursorY);
                NewLine(cursorX, cursorY);
                cursorY++;
                cursorX = indentation;
                InsertTab(0, cursorY, indentation);
                UpdateCursor();
                break;

            case "edit_backspace":
                NormalizeCursor();
                if (cursorX > 0)
                {
                    DeleteCharacter(cursorX - 1, cursorY);
                    cursorX--;
                }
                else if (cursorY > 0)
                {
                    int newX = lines[cursorY - 1].text.Length;
                    JoinLines(cursorY - 1, cursorY);
                    cursorX = newX;
                    cursorY--;
                }
                UpdateCursor();
                break;

            case "edit_delete":
                NormalizeCursor();
                if (cursorX < lines[cursorY].text.Length)
                {
                    DeleteCharacter(cursorX, cursorY);
                }
                else if (cursorY < lines.Count - 1)
                {
                    JoinLines(cursorY, cursorY + 1);
                }
                UpdateCursor();
                break;

            case "edit_backspace_skip":
                NormalizeCursor();
                if (cursorX > 0)
                {
                    int len = GetPreviousWordSpan(cursorX, cursorY);
                    for (int i = 0; i < len; i++)
                    {
                        DeleteCharacter(cursorX - 1, cursorY);
                        cursorX--;
                    }
                    UpdateCursor();
                }
                else
                {
                    RunCommand("edit_backspace");
                }
                break;

            case "edit_delete_skip":
                NormalizeCursor();
                if (cursorX < lines[cursorY].text.Length)
                {
                    int len = GetNextWordSpan(cursorX, cursorY);
                    for (int i = 0; i < len; i++)
                    {
                        DeleteCharacter(cursorX, cursorY);
                    }
                    UpdateCursor();
                }
                else
                {
                    RunCommand("edit_delete");
                }
                break;

            case "edit_delete_line":
                DeleteLine(cursorY);
                cursorX = 0;
                UpdateCursor();
                break;

            default:
                break;
        }

        return true;
    }

    public void ProcessCharInput(char character)
    {
        NormalizeCursor();
        if (character >= 0x20 && character <= 0x7e)
        {
            InsertCharacter(cursorX, cursorY, character);
            cursorX++;

            if (character == '}')
            {
                int delta = SetIndentation(cursorY, lines[cursorY].indentation - TabSize);
                cursorX += delta;
            }

            UpdateCursor();
        }
    }

    public void Refresh()
    {
        for (int i = 0; i < lines.Count; i++)
        {
            lines[i].Refresh();
        }
    }

    private int GetNextWordSpan(int x, int y)
    {
        int start = x;
        int i = start;
        bool spaces = lines[y].text[x] == ' ';
        do
        {
            i++;
            if (i == lines[y].text.Length)
            {
                break;
            }
        } while (spaces ? lines[y].text[i] == ' ' : (IsLetter(lines[y].text[i]) || IsNumber(lines[y].text[i])));

        return i - start;
    }

    private int GetPreviousWordSpan(int x, int y)
    {
        int start = x;
        int i = start;
        bool spaces = lines[y].text[x - 1] == ' ';
        do
        {
            i--;
            if (i == 0)
            {
                i--;
                break;
            }
        } while (spaces ? lines[y].text[i] == ' ' : (IsLetter(lines[y].text[i]) || IsNumber(lines[y].text[i])));
        if (i < start - 1)
        {
            i++;
        }

        return start - i;
    }

    private int EvaluateNextIndentation(int y)
    {
        int indentation = lines[y].indentation;
        if (lines[y].text.Trim().EndsWith("{"))
        {
            indentation += TabSize;
        }

        return indentation;
    }

    private int SetIndentation(int y, int num)
    {
        string trimmedLine = new string(' ', num) + lines[y].text.TrimStart(' ');
        int delta = trimmedLine.Length - lines[y].text.Length;
        lines[y].text = trimmedLine;

        return delta;
    }

    private bool IsLetter(char c)
    {
        return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || (c >= 0xC0 && c <= 0x2AF);
    }

    private bool IsNumber(char c)
    {
        return (c >= '0' && c <= '9') || c == '.';
    }

    private void InsertCharacter(int x, int y, char c)
    {
        string part1 = lines[y].text.Substring(0, x);
        string part2 = lines[y].text.Substring(x);
        lines[y].text = part1 + c + part2;
    }

    private int InsertTab(int x, int y, int size)
    {
        int numSpaces = size > 0 ? size - x % size : 0;
        for (int i = 0; i < numSpaces; i++)
        {
            InsertCharacter(x, y, ' ');
        }

        return numSpaces;
    }

    private void DeleteCharacter(int x, int y)
    {
        string part1 = lines[y].text.Substring(0, x);
        string part2 = x < lines[y].text.Length ? lines[y].text.Substring(x + 1) : "";
        lines[y].text = part1 + part2;
    }

    private void DeleteLine(int y)
    {
        if (y == lines.Count - 1)
        {
            lines[y].text = "";
        }
        else
        {
            lines.RemoveAt(y);
        }
    }

    private void NewLine(int x, int y)
    {
        string part1 = lines[y].text.Substring(0, x);
        string part2 = lines[y].text.Substring(x);

        TextLine line = new TextLine(this, cmd, part2);
        lines.Insert(y + 1, line);

        lines[y].text = part1;
    }

    private void JoinLines(int line1, int line2)
    {
        string part1 = lines[line1].text;
        string part2 = lines[line2].text;

        lines.RemoveAt(line2);
        lines[line1].text = part1 + part2;
    }

    private void UpdateCursor()
    {
        tab.editor.RestartCursor();
        updateScrolling = true;
    }

    private void NormalizeCursor()
    {
        cursorX = Math.Min(cursorX, lines[cursorY].text.Length);
    }

    private void DoTabSkip(bool end)
    {
        int tabStart = (cursorX / TabSize) * TabSize;
        if (tabStart != cursorX && tabStart + TabSize <= lines[cursorY].text.Length)
        {
            bool isTab = true;
            for (int i = 0; i < TabSize; i++)
            {
                if (lines[cursorY].text[tabStart + i] != ' ')
                {
                    isTab = false;
                }
            }
            if (isTab)
            {
                cursorX += TabSize - (cursorX - tabStart);
                if (!end)
                {
                    cursorX -= TabSize;
                }
            }
        }
    }

    public void Draw(int numVisibleLines, bool focus, int left, int top, int width, int height)
    {
        if (updateScrolling)
        {
            UpdateScrolling(width * cmd.fontWidth, height * cmd.fontHeight);
            updateScrolling = false;
        }

        for (int y = scrollY; y < scrollY + numVisibleLines; y++)
        {
            lines[y].DrawText(left, top + (y - scrollY) * cmd.fontHeight, width, scrollX, cursorY == y);
        }

        if (focus)
        {
            int x = left + (cursorX - scrollX) * cmd.fontWidth;
            int y = top + (cursorY - scrollY) * cmd.fontHeight;

            bool active = tab.editor.cursorActive && y < top + height && y >= top && x >= left && x < left + width;

            if (active)
            {
                char character = cursorX < lines[cursorY].text.Length ? lines[cursorY].text[cursorX] : ' ';
                cmd.Print(x, y, new string(character, 1), cursorForeground, cursorBackground);
            }
        }
    }

    private void UpdateScrolling(int width, int height)
    {
        if (cursorY >= scrollY + height)
        {
            scrollY = cursorY - height + 1;
        }
        else if (cursorY < scrollY)
        {
            scrollY = cursorY;
        }
        if (cursorX >= scrollX + width)
        {
            scrollX = cursorX - width + 1;
        }
        else if (cursorX < scrollX)
        {
            scrollX = cursorX;
        }
    }
}