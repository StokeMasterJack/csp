package com.smartsoft.csp.transforms

import com.smartsoft.csp.ast.Exp
import com.smartsoft.csp.ast.PLConstants

open class Transforms : PLConstants {
    companion object {


        fun bnf(): Transformer {
            return Transformer.BNF
        }

        fun pushNotsIn(): Transformer {
            return Transformer.PUSH_NOTS_IN
        }

        fun nnf(): Transformer {
            return Transformer.NNF
        }

        fun identity(): Transformer {
            return Transformer.IDENTITY
        }


        fun xorToCnf(inExp: Exp): Exp {
            return XorsToCnf().transform(inExp)
        }

        fun removeConflicts(inExp: Exp): Exp {
            return ConflictsToCnf().transform(inExp)
        }

        fun removeIffs(inExp: Exp): Exp {
            return IffToCnf().transform(inExp)
        }

        fun flattenImps(inExp: Exp): Exp {
            return FlattenImps().transform(inExp)
        }

        fun removeImps(inExp: Exp): Exp {
            return ImpsToCnf().transform(inExp)
        }

        fun flattenOrs(inExp: Exp): Exp {
            return FlattenOrs().transform(inExp)
        }

        fun flattenAnd(inExp: Exp): Exp {
            return FlattenAnds().transform(inExp)
        }

        fun unFlattenAnd(inExp: Exp): Exp {
            return andToBinary(inExp)
        }

        fun andToBinary(inExp: Exp): Exp {
            return AndsToBinary().transform(inExp)
        }

        fun pushNotsIn(inExp: Exp): Exp {
            return PushNotsIn().transform(inExp)
        }

        fun pushUpAnds(inExp: Exp): Exp {
            return PushAndsOut().transform(inExp)
        }
    }


}
