package com.smartsoft.csp.parse

class VarTokenizerStd : VarTokenizer() {

    fun isValidVarFirstChar(c: Char): Boolean {
        return Character.isLetterOrDigit(c)
    }

    fun isValidVarChar(c: Char): Boolean {
        return Character.isLetterOrDigit(c) || c == UNDERSCORE || c == '|' || c == '.' || c == ':'
    }


    override fun matches(stream: Stream): Token? {
        if (!matchesInternal(stream)) return null
        val sb = StringBuilder()

        while (keepReading(stream)) {
            sb.append(stream.take())
        }
        return VarTokenStd(sb.toString())
    }


    fun matchesInternal(stream: Stream): Boolean {
        val c = stream.peek()
        val b = isValidVarFirstChar(c)
        if (!b) {
            System.err.println("badFirstChar: " + c + " at cursor[" + stream.getCursor() + "] formula string: " + stream.input)
        }
        return b
    }

    fun keepReading(stream: Stream): Boolean {
        if (stream.isEol) {
            return false
        }
        val c = stream.peek()

        val validVarChar = isValidVarChar(c)

        if (!validVarChar) {
            //            System.err.println("invalidVarChar[" + c + "]");
        }

        return validVarChar
    }


    fun isValidVarFirstChar(stream: Stream): Boolean {
        return isValidVarFirstChar(stream.peek())
    }

}
