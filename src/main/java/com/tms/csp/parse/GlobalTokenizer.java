package com.tms.csp.parse;

import com.google.common.collect.ImmutableList;
import com.tms.csp.ast.PLConstants;
import com.tms.csp.ast.Ser;

import java.util.ArrayList;
import java.util.List;

public class GlobalTokenizer implements PLConstants {

    public static final GlobalTokenizer INSTANCE = new GlobalTokenizer();

    private final ImmutableList<Tokenizer> tokenizers;

    private GlobalTokenizer() {
        tokenizers = ImmutableList.copyOf(createTokenizers());
    }

    Token takeNextToken(Stream stream) {

        if (stream.isEol()) {
            return null;
        }

        for (Tokenizer tokenizer : tokenizers) {
            Token token = tokenizer.matches(stream);

            if (token != null) {
                return token;
            }
        }

        stream.print();
        throw new RuntimeException("inValidVarFirstChar: " + tokenizers.get(tokenizers.size() - 1));


    }


    public TokenStream tokenize(String inputText) {
        return new TokenStream(this, inputText);
    }


    private final List<Tokenizer> createTokenizers() {
        ArrayList<Tokenizer> a = new ArrayList<Tokenizer>();


        a.add(createLParenTokenizer());
        a.add(createRParenTokenizer());
        a.add(createArgSepTokenizer());

        a.add(createNotTokenizer());

        a.add(createConstantTrueTokenizer());
        a.add(createConstantFalseTokenizer());

        for (PosOp complexOp : PosOp.getComplexOps2()) {
            Tokenizer t = createPosComplexTokenizer(complexOp);
            a.add(t);
        }

        a.add(createVarTokenizer());

        return a;
    }

    private Tokenizer createLParenTokenizer() {
        return new CharTokenTokenizer(LPAREN) {
            @Override
            public boolean isLParen() {
                return true;
            }
        };
    }

    private Tokenizer createRParenTokenizer() {
        return new CharTokenTokenizer(RPAREN) {
            @Override
            public boolean isRParen() {
                return true;
            }
        };
    }

    private Tokenizer createArgSepTokenizer() {
        return new CharTokenTokenizer(ARG_SEP) {
            @Override
            public boolean isArgSep() {
                return true;
            }

            @Override
            public boolean silentlyConsume() {
                return true;
            }
        };
    }

    private Tokenizer createNotTokenizer() {
        return new CharTokenTokenizer(BANG) {
            @Override
            public boolean isNot() {
                return true;
            }
        };
    }

    private Tokenizer createConstantTrueTokenizer() {
        return new StringTokenTokenizer(TRUE_TOKEN) {
            @Override
            public boolean isConstantTrue() {
                return true;
            }
        };
    }

    private Tokenizer createConstantFalseTokenizer() {
        return new StringTokenTokenizer(FALSE_TOKEN) {
            @Override
            public boolean isConstantFalse() {
                return true;
            }
        };
    }

    private Tokenizer createPosComplexTokenizer(final PosOp posOp) {
        assert posOp.isComplex();
        Ser a = new Ser();
        return new PosComplexOpTokenizer(posOp, a);
    }

    private Tokenizer createVarTokenizer() {
        return new VarTokenizerStd();
    }


}
