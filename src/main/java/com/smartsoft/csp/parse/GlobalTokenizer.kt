package com.smartsoft.csp.parse

import com.google.common.collect.ImmutableList
import com.smartsoft.csp.ast.PLConstants
import com.smartsoft.csp.ast.Ser

import java.util.ArrayList

class GlobalTokenizer private constructor() : PLConstants {

    private val tokenizers: ImmutableList<Tokenizer>

    init {
        tokenizers = ImmutableList.copyOf(createTokenizers())
    }

    internal fun takeNextToken(stream: Stream): Token? {

        if (stream.isEol) {
            return null
        }

        for (tokenizer in tokenizers) {
            val token = tokenizer.matches(stream)

            if (token != null) {
                return token
            }
        }

        stream.print()
        throw RuntimeException("inValidVarFirstChar: " + tokenizers[tokenizers.size - 1])


    }


    fun tokenize(inputText: String): TokenStream {
        return TokenStream(this, inputText)
    }


    private fun createTokenizers(): List<Tokenizer> {
        val a = ArrayList<Tokenizer>()


        a.add(createLParenTokenizer())
        a.add(createRParenTokenizer())
        a.add(createArgSepTokenizer())

        a.add(createNotTokenizer())

        a.add(createConstantTrueTokenizer())
        a.add(createConstantFalseTokenizer())

        for (complexOp in PLConstants.PosOp.getComplexOps2()) {
            val t = createPosComplexTokenizer(complexOp)
            a.add(t)
        }

        a.add(createVarTokenizer())

        return a
    }

    private fun createLParenTokenizer(): Tokenizer {
        return object : CharTokenTokenizer(LPAREN) {
            override fun isLParen(): Boolean {
                return true
            }
        }
    }

    private fun createRParenTokenizer(): Tokenizer {
        return object : CharTokenTokenizer(RPAREN) {
            override fun isRParen(): Boolean {
                return true
            }
        }
    }

    private fun createArgSepTokenizer(): Tokenizer {
        return object : CharTokenTokenizer(ARG_SEP) {
            override fun isArgSep(): Boolean {
                return true
            }

            override fun silentlyConsume(): Boolean {
                return true
            }
        }
    }

    private fun createNotTokenizer(): Tokenizer {
        return object : CharTokenTokenizer(BANG) {
            override fun isNot(): Boolean {
                return true
            }
        }
    }

    private fun createConstantTrueTokenizer(): Tokenizer {
        return object : StringTokenTokenizer(TRUE_TOKEN) {
            override fun isConstantTrue(): Boolean {
                return true
            }
        }
    }

    private fun createConstantFalseTokenizer(): Tokenizer {
        return object : StringTokenTokenizer(FALSE_TOKEN) {
            override fun isConstantFalse(): Boolean {
                return true
            }
        }
    }

    private fun createPosComplexTokenizer(posOp: PLConstants.PosOp): Tokenizer {
        assert(posOp.isComplex)
        val a = Ser()
        return PosComplexOpTokenizer(posOp, a)
    }

    private fun createVarTokenizer(): Tokenizer {
        return VarTokenizerStd()
    }

    companion object {

        val INSTANCE = GlobalTokenizer()
    }


}
