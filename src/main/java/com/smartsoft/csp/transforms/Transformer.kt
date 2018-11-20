package com.smartsoft.csp.transforms

import com.smartsoft.csp.ast.Exp
import com.smartsoft.csp.ast.PLConstants

import com.google.common.base.Preconditions.checkNotNull
import com.smartsoft.csp.ssutil.Strings.getSimpleName

abstract class Transformer : Transforms(), PLConstants {


    var _name: String? = null

    fun getName(): String? {
        if (_name == null) {
            _name = getSimpleName(this)
        }
        return _name
    }

//    fun howToHandle(expIn: Exp): Any? {
//        return null
//    }


    abstract fun transform(inExp: Exp): Exp

    fun transformUntilStable(expIn: Exp): Exp {
        return transformUntilStable(this, expIn)
    }

    fun transformArgs(args: List<Exp>): List<Exp> {
        val exps = transformArgs(this, args) ?: throw IllegalStateException()
        return exps
    }

    fun transformArgs2(args: Iterable<Exp>): Iterable<Exp> {
        val exps = transformArgs2(this, args) ?: throw IllegalStateException()
        return exps
    }

    /**
     * Leave Exp.type the same
     */
    protected fun transformArgsOnly(expIn: Exp): Exp {
        checkNotNull(expIn)
        if (expIn.isConstant || expIn.isLit) {
            return expIn
        } else if (expIn.isNegComplex) {
            val argBefore = expIn.arg
            val argAfter = this.transform(argBefore)
            return if (argAfter === argBefore) {
                expIn
            } else {
                argAfter.flip
            }
        } else if (expIn.isPosComplex) {
            val inArgs = expIn.args
            val outArgs = transformArgs2(inArgs)

            val retVal: Exp
            if (outArgs === inArgs) {
                retVal = expIn
            } else {
                retVal = expIn.asPosComplex.newComplex(outArgs)
            }

            checkNotNull(retVal)

            return retVal
        } else {
            throw IllegalArgumentException()
        }


    }

    companion object {

        val FLATTEN_ANDS: Transformer = FlattenAnds()
        val FLATTEN_IMPS: Transformer = FlattenImps()
        val FLATTEN_RMPS: Transformer = FlattenRmps()
        val FLATTEN_ORS: Transformer = FlattenOrs()
        val OR_TO_AND: Transformer = OrToAnd()
        val AND_TO_BINARY: Transformer = AndsToBinary()
        val FLATTEN: Transformer = UntilStableTransformer(FLATTEN_ANDS, FLATTEN_ORS)


        internal var outMatchesInCount = 0

        fun transformUntilStable(transformer: Transformer, expIn: Exp): Exp {

            val out = transformer.transform(expIn)

            return if (out === expIn) {
                out
            } else {
                transformUntilStable(transformer, out)
            }
        }

        fun transformList(transformer: Transformer, list: List<Exp>): List<Exp> {
            return ListTransform.transform(list, transformer)
        }

        fun transformArgs(transformer: Transformer, argsIn: List<Exp>): List<Exp>? {
            val listOut = ListTransform.transform(argsIn, transformer)
            return if (argsIn === listOut) {
                argsIn
            } else {
                listOut
            }
        }

        fun transformArgs2(transformer: Transformer, argsIn: Iterable<Exp>): Iterable<Exp>? {
            val listOut = ListTransform.transform2(argsIn, transformer)
            return if (argsIn === listOut) {
                argsIn
            } else {
                listOut
            }
        }

        //    public ExpSet applyDelta(Exp[] delta) {
        //        if (delta == null) {
        //            return this;
        //        }
        //
        //        Exp[] ret = new Exp[size()];
        //        for (int i = 0; i < size(); i++) {
        //            if (delta[i] != null) {
        //                ret[i] = delta[i];
        //            } else {
        //                ret[i] = get(i);
        //            }
        //        }
        //
        //
        //        return new NSet(ret);
        //    }


        val IDENTITY: Transformer = IdentityTransformer()


        val PUSH_NOTS_IN: Transformer = UntilStableTransformer(CompoundTransformer(
                PushNotsIn(),
                FLATTEN,
                OneArgOr(), OneArgAnd()
        ))

        val PUSH_ANDS_OUT: Transformer = PushAndsOut()


        /**
         * BNF:mBase Normal Form
         * Allow:
         * And(N)
         * Not
         * Or(N)
         * Var
         */
        val BNF: Transformer = UntilStableTransformer("BNF", CompoundTransformer(
                FlattenImps(),
                FlattenRmps(),
                XorsToCnf(),
                ConflictsToCnf(),
                IffToCnf(),
                ImpsToCnf(),
                RmpsToCnf(),
                FlattenOrs(),
                FlattenAnds(),
                OneArgOr(),
                OneArgAnd()
        ))

        val BNF_KEEP_XORS: Transformer = UntilStableTransformer("BNF", CompoundTransformer(
                FlattenImps(),
                FlattenRmps(),
                ConflictsToCnf(),
                IffToCnf(),
                ImpsToCnf(),
                RmpsToCnf(),
                FlattenOrs(),
                FlattenAnds(),
                OneArgOr(),
                OneArgAnd()
        ))


        val BNF_TO_NNF: Transformer = UntilStableTransformer("BNF_TO_NNF", PUSH_NOTS_IN, FLATTEN)

        /**
         * Only allow:
         * Same as BNF,but only _complexVars can be negated
         */
        val NNF: Transformer = UntilStableTransformer("NNF", BNF, BNF_TO_NNF)

        val BNF_TO_AIG: Transformer = UntilStableTransformer("AIG",
                OR_TO_AND,
                AND_TO_BINARY
        )


        val NNF_TO_CNF: Transformer = UntilStableTransformer("NNF_TO_CNF",
                PushAndsOut(),
                OneArgOr(),
                OneArgAnd(),
                FLATTEN
        )

        val CNF: Transformer = UntilStableTransformer("CNF", NNF_TO_CNF, NNF)

        val AIG: Transformer = CompoundTransformer("AIG", BNF, BNF_TO_AIG)
    }


}
