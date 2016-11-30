package com.sri.apps.off;

import java.io.*;
import java.util.*;

class Tokenizer
{
    final static char COMMENT_CHAR = '#';
        /**
         * Invalid token.
         */
    final static int ERROR_TOKEN = -1;

        /**
         * The end of file token.
         */
    final static int EOF_TOKEN = -2;

        /**
         * An integer token.
         */
    final static int INT_TOKEN = 1;
    
        /**
         * A float token.
         */
    final static int FLOAT_TOKEN = 2;

        /**
         * A string token.
         */
    final static int STRING_TOKEN = 3;

        /**
         * A new line token.
         */
    final static int NEW_LINE_TOKEN = 4;

        /**
         * The current line.
         */
    byte []currLine = new byte[512];
    int bufferLen = 0;

        /**
         * Flag to specify whether we should have the 
         * new line as a token or not.
         */
    boolean ignoreNewLine;

        /**
         * A line number counter.
         */
    int currLineCounter;

        /**
         * The initial size of the lexeme.
         */
    StringBuffer lexeme;

        /**
         * The position in the buffer where we are
         * read the characters from.
         */
    int pos;

        /**
         * The input stream.
         */
    InputStream input;

        /**
         * Constructor.
         *
         * Requires the input stream from which the
         * tokens are to be extracted.
         */
    Tokenizer(InputStream in)
    {
        this.input = in;

        lexeme = new StringBuffer();

        ignoreNewLine = false;      // by default.

        currLineCounter = 0;

        pos = 0;
    }

        /**
         * Determines the type of token from a given string.
         */
    int tokenType(String lexeme, int len)
    {
        boolean foundE = false;
        boolean isFloat = false;
        boolean isNumber = true;
        char ch;

        for (int i = 0;isNumber && i < len;i++)
        {
            ch = lexeme.charAt(i);

            if (ch == '-' || ch == '+')
            {
                if (i != 0 && lexeme.charAt(i - 1) != 'e')
                {
                    isNumber = false;
                } else if (!isFloat)
                {
                        // other wise if the sign is not the
                        // first char, then the number is a
                        // float...
                    isFloat = (i > 0);
                }
            } else if (ch == '.')
            {
                if (isFloat) isNumber = false;
                else
                {
                    isFloat = true;
                }
            } else if (ch == 'e' || ch == 'E')
            {
                if (foundE) isNumber = false;
                else
                {
                    foundE = true;
                    isFloat = true;
                }
            } else if (!Character.isDigit((char)ch))
            {
                isNumber = false;
            }
        }

        if (isNumber)
        {
            return (isFloat ? FLOAT_TOKEN : INT_TOKEN);
        }

        return STRING_TOKEN;
    }

        /**
         * Returns the next token.
         */
    int getToken() throws Exception
    {

            // read a character...
            // first check if we have a character pushed back or not..
            // then if we do read that character other wise
            // read a character from the input stream.
        int ch = getNextChar();

            // skip over all spaces and newlines (if we are ignoring them)
        while (ch >= 0 &&
               (ch == ' ' || ch == '\t' ||
                (ignoreNewLine && ch == '\n'))) ch = getNextChar();

        if (ch < 0) return EOF_TOKEN;

        else if (ch == '\n') return NEW_LINE_TOKEN;

        else if (ch == COMMENT_CHAR)
        {
                // then skip all characters till the new line...
            while (ch >= 0 && ch != '\n') ch = getNextChar();

                // if at eof then return it...
            if (ch < 0) return EOF_TOKEN;

                    // see if we should ignore the new line..
            if (ch == '\n' && !ignoreNewLine) pos--;

                // so now return getToken on the next line...
                // if at this stage we are at EOF, this is 
                // recognised by the next call.
            return getToken();
        }

        else if (ch == '.' || ch == '-' || ch == '+' || Character.isDigit((char)ch))
        {
                                    // while we have more digits
                                    // we keep reading it.
            lexeme = new StringBuffer();
            lexeme.append((char)ch);

            ch = getNextChar();

                // now we are pretty much reading till the next space character
                // or a coment char which ever comes first...
                //
                // Now the whole deal is that 
            while (ch >= 0 && !Character.isSpace((char)ch) && ch != COMMENT_CHAR)
            {
                    // store the current character in the lexeme
                lexeme.append((char)ch);

                    // read the next character...
                ch = getNextChar();
            }

                // another thing... if the current character
                // is not a space, then push it back to the stream..
                // eg if the input was 123445#, we need to put the # back
                // into the stream as it could a comment which might be
                // discarded in the next call to getToken.
            if (ch != ' ' && ch != '\t') pos--;

            return tokenType(lexeme.toString().toLowerCase(), lexeme.length());
        } else
        {
            lexeme = new StringBuffer();
                // other wise read strings...
            while (ch >= 0 && !Character.isSpace((char)ch) && ch != COMMENT_CHAR)
            {
                    // add the character to the lexeme
                lexeme.append((char)ch);

                    // read the next character.
                ch = getNextChar();
            }

                // if the last character read was
                // a  # or a new line then push it back
                // so it can be extracted again next time
            if (ch == COMMENT_CHAR || ch == '\n') pos--;

                // add the null character at the end..
            lexeme.append((char)ch);


                // return the string token type...
            return STRING_TOKEN;
        }

        //return EOF_TOKEN;
    }

        /**
         * Returns the current lexeme.
         */
    String getLexeme()
    {
        return lexeme.toString();
    }

        /**
         * Specifies whether the newline token should be ignored or not.
         */
    void ignoreNewLineToken(boolean ignore)
    {
        this.ignoreNewLine = ignore;
    }

        /**
         * Returns the current line number.
         */
    int getLineNumber()
    {
        return currLineCounter;
    }

        /**
         * Returns an integer.
         */
    int getInt() throws Exception
    {
        int currToken = getToken();

            // and skip over all new line chars
        while (currToken == NEW_LINE_TOKEN) currToken = getToken();

            // if the token is a float
        if (currToken == INT_TOKEN)
        {
            return Integer.parseInt(lexeme.toString());
        }

                // otherwise throw an illegal token exception
        throw new Exception("Invalid Token");
    }

        /**
         * Returns a floating point number.
         */
    double getFloat() throws Exception
    {
        int currToken = getToken();

            // and skip over all new line chars
        while (currToken == NEW_LINE_TOKEN) currToken = getToken();

            // if the token is an integer or a float
        if (currToken == FLOAT_TOKEN || currToken == INT_TOKEN)
        {
                        // then convert it to a float
            return (new Double(lexeme.toString())).doubleValue();
        }

                // otherwise throw an illegal token exception
        throw new Exception("Invalid Token");
    }

        /**
         * Returns a string.
         */
    String getString() throws Exception
    {
        int currToken = getToken();

            // and skip over all new line chars
        while (currToken == NEW_LINE_TOKEN) currToken = getToken();

            // if the token is an integer or a float
        if (currToken != EOF_TOKEN && currToken != ERROR_TOKEN)
        {
            return lexeme.toString();
        }

                // otherwise throw an illegal token exception
        throw new Exception("Invalid Token");
    }

        /**
         * Fill the buffer from the input stream.
         *
         * Returns if the buffer was filled or not
         * ie was eof reached.
         */
    boolean fillBuffer() throws Exception
    {
        bufferLen = input.read(currLine);
	        if (bufferLen < 0) return false;

        pos = 0;            // reset pos = 0

        return true;
    }

        /**
         * Get the next character from the input buffer.
         */
    int getNextChar() throws Exception
    {
        if (pos >= bufferLen)
        {
                //then we need to fill the buffer again.
            if (!fillBuffer()) return -1;
        }
        return (((int)(currLine[pos++])) & 0xff);
    }
}
