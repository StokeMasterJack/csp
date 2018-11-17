package com.tms.csp.parse

import java.util.ArrayList

import com.google.common.base.Preconditions.checkState

class TokenStream(val tokenizer: GlobalTokenizer, val stream: Stream) {

    internal var _buffer: Token? = null

    val isEmpty: Boolean
        get() = stream.isEol && _buffer == null

    constructor(tokenizer: GlobalTokenizer, inputText: String) : this(tokenizer, Stream(inputText)) {}

    fun readAllTokens(): List<Token> {
        val a = ArrayList<Token>()
        while (true) {
            if (stream.isEol) {
                break
            }
            val token:Token = take()!!
            a.add(token)
        }
        return a

    }

    fun printTokens() {
        val t = take()
        while (t != null) {
            System.err.println(t)
        }

    }

    fun peek(): Token {
        if (this._buffer == null) {
            this._buffer = tokenizer.takeNextToken(stream)
        }
        return this._buffer!!
    }

    //    public Token take() {
    //        if (tokens.isEmpty()) {
    //            return null;
    //        } else {
    //            return tokens.remove(0);
    //        }
    //    }

    //    public Token takeNextTokenFromStream() {
    //        return tokenizer.takeNextToken(stream);
    //    }

    fun take(): Token? {
        if (this._buffer != null) {
            val tmp = _buffer
            this._buffer = null
            return tmp
        } else {
            return tokenizer.takeNextToken(stream)
        }
    }


    fun consumeAndCheck(c: Char) {
        val t = consume()
        val cc = t!!.char!!
        checkState(cc == c, "check failed - expected[$c]   got[$cc]")
    }

    fun consume(): Token? {
        return take()
    }


}
