package com.tms.csp.ast

import com.google.common.base.Preconditions.checkState
import com.google.common.collect.*
import com.tms.csp.It
import com.tms.csp.VarInfo
import com.tms.csp.argBuilder.ArgBuilder
import com.tms.csp.ast.PLConstants.*
import com.tms.csp.ast.formula.KFormula
import com.tms.csp.common.SeriesYear
import com.tms.csp.fm.dnnf.ChildCounts
import com.tms.csp.fm.dnnf.DAnd
import com.tms.csp.fm.dnnf.DOr
import com.tms.csp.fm.dnnf.Dnnf
import com.tms.csp.fm.dnnf.products.Cube
import com.tms.csp.fm.dnnf.products.PosCube
import com.tms.csp.fm.dnnf.vars.VarFilter
import com.tms.csp.fm.dnnf.visitor.NodeHandler
import com.tms.csp.fm.dnnf.visitor.NodeInfo
import com.tms.csp.graph.Filter
import com.tms.csp.ssutil.Strings.indent
import com.tms.csp.transforms.CompoundTransformer
import com.tms.csp.transforms.Transformer
import com.tms.csp.util.*
import com.tms.csp.util.varSets.VarSet
import com.tms.csp.varCodes.VarCode
import java.math.BigInteger
import java.util.*


/*

exp
   constants
   lits
   pos complex
           T/F
       and
          all-simple - cubes

          all-complex
               disjoint-?
               disjoint-Y
                       dnnf Y
                       dnnf N

               disjoint-N
                   disjoint-N - FCC-Y
                   disjoint-N - FCC-N
                   disjoint-N - FCC-?

           mixed
               simple-complex-disjoint Y
               simple-complex-disjoint N
               simple-complex-disjoint ?


       or
           determinism Y
           determinism N

           all-simple clause
           all-complex
           mixed

       xor
       iff
       imp
       complict

non-exp
   var
   non-exp cube
   non-exp cube sets
   csp

interned
   interned Y
           constants
           _complexVars
               lits
               dcOr
           pos complex exp
               complex not

   interned N
       CSP
       non-exp cube
       non-exp cube sets

would like to unify csp - exp
   two may data structures for AND
       Imutable EXP: AND,DAND.CubeExp,Formula
    mutaable ands:
       csp
       Arg Builder

better: caching strategy:
   cachce based c var groups




why flattening and is a bad idea -  may disjoin any-and and children
   dont flatten if your conjuncts are disjoin
   dont comnine adjavent  conjuncts of thet are disjoing
   dont flattenn  may be disjoint: flattening wpuls lill ythjat


*/

abstract class Exp(override val space: Space, val expId: Int) : PLConstants, Comparable<Exp>, HasCode, HasVars, HasVarId {

    var traversalId: Int = 0

    private var _satCount: BigInteger? = null //sat count smooth

    private var parents: HashSet<Exp>? = null  //must clear if root changes

    private var _pd: Int? = null //partial derivative - for counting graph

    var next: Exp? = null //used for hash table lookup


    val th: Nothing get() = throw UnsupportedOperationException(this.javaClass.name)

    open val flip: Exp get() = th

    val flipId: Int get() = flip.expId

    open fun notNew(): Unit {
        th
    }

    open val isNew: Boolean get() = th


    val prefix: String
        get() = when (this) {
            is Lit -> this.vr.prefix
            is Xor -> vars.firstVar.prefix
            else -> throw IllegalStateException()
        }


    abstract val isPos: Boolean


    open val pos: Exp get() = th

    open val argList: List<Exp> get() = th

    open val arg: Exp get() = th


    operator fun get(index: Int): Exp = getArg(index)

    open fun getArg(index: Int): Exp = th
    open fun argAt(index: Int): Exp = getArg(index)

    val argIt: Iterable<Exp> get() = Iterable { argIter }

    val argsToList: List<Exp> get() = args.toList()
    val argsToMutableList: List<Exp> get() = args.toMutableList()
    val args: Iterable<Exp> get() = argIt

    open val argIter: Iterator<Exp> get() = It.emptyIter()

    val isAndLike: Boolean get() = isAndish
    val isOrLike: Boolean get() = isOrish

    val isAndish: Boolean
        get() = when (this) {
            is Cube -> true
            is KFormula -> true
            is And -> true
            is DAnd -> true
            is LitAndFalse -> true
            else -> false
        }

    val isOrish: Boolean
        get() = when (this) {
            is Or -> true
            is DOr -> true
            is DcOr -> true
            is Xor -> true
            else -> false
        }

    val argsFlattened: Sequence<Exp>
        get() {
            if (!isAndLike) throw IllegalStateException()
            return sequence {
                for (arg in argIt) {

                    if (arg.isAndLike) {
                        yieldAll(arg.argsFlattened)
                    } else {
                        yield(arg)
                    }
                }
            }
        }

    open val argCount: Int get() = th

    val size get() = argCount;

    val argSeq
        get() = when (this) {
            is Constant -> emptySequence<Exp>()
            is Lit -> emptySequence<Exp>()
            is Not -> kotlin.sequences.sequenceOf(arg1)
            is PosComplexMultiVar -> argIt.asSequence()
            is DcOr -> sequenceOf(vr.pLit(), vr.nLit())
            is LitAndFalse -> sequenceOf(lit, mkFalse())
            else -> throw IllegalStateException()
        }


    fun collectVarCodes(s: MutableSet<String>): Unit {
        when (this) {
            is Lit -> s.add(this.varCode)
            is Not -> arg.collectVarCodes(s)
            is PosComplex -> this.argIt.forEach { it.collectVarCodes(s) }
            is Constant -> Unit
            else -> throw  IllegalStateException()
        }
    }


    val xorsDeep: List<Exp>
        get() {
            if (isConstant || isLit)
                return ImmutableList.of()
            else if (isXor)
                return ImmutableList.of(this)
            else if (isNot) return arg.xorsDeep
            val a = ImmutableList.builder<Exp>()
            for (arg in args) {
                if (arg.isConstant || arg.isLit) {
                } else if (arg.isXor) {
                    a.add(arg)
                } else if (arg.isComplex) {
                    a.addAll(arg.xorsDeep)
                } else {
                    throw IllegalStateException()
                }
            }
            return a.build()
        }

    val simpleName: String
        get() = this.javaClass.simpleName

    val isNeg: Boolean
        get() = !isPos


    open val neg: Exp
        get() = throw UnsupportedOperationException()

    open val isConstantTrue: Boolean
        get() = false

    open val isConstantFalse: Boolean
        get() = false

    open val isConstant: Boolean
        get() = false

    open val isLit: Boolean
        get() = false

    open val isPosLit: Boolean
        get() = false

    val isNegLit: Boolean
        get() = isLit && isNeg

    open val isNot: Boolean
        get() = false


    val isFlattenableNand: Boolean
        get() {
            if (!isNand) {
                return false
            }
            if (arg1.isPosLit && arg2.isOr) {
                return true
            } else if (arg2.isPosLit && arg1.isOr) {
                return true
            }
            return false
        }

    val isSimple: Boolean
        get() = isLit


    //    public boolean isVars() {
    //        return this instanceof Vars;
    //    }

    open val isComplex: Boolean
        get() = false

    val isPosVarsExp: Boolean
        get() = false

    val isVarsExp: Boolean
        get() = isPosVarsExp

    open val isPosComplex: Boolean
        get() = false

    open val isPosArgsExp: Boolean
        get() = false

    open val isNegComplex: Boolean
        get() = false


    val isNegAnd: Boolean
        get() = isNot && pos.isAnd

    val isNegOr: Boolean
        get() = isNot && arg.isOr

    val isOrWithNestedAnd: Boolean
        get() = isOr && containsArgOfType(And::class.java)

    val andWithHighestLitArgCount: And?
        get() {

            if (isConstant) return null
            if (isLit) return null

            var best: And? = null

            if (isAnd) {
                best = this.asAnd
            }

            for (arg in argIt) {
                val aa: And = arg.andWithHighestLitArgCount ?: continue
                if (best == null || aa.litArgCount > best.litArgCount) {
                    best = aa
                }

            }


            return best
        }

    val andLitArgCount: Int
        get() = if (!isAnd) 0 else litArgCount


    open val litArgCount: Int get() = 0

    val isImp: Boolean get() = this is Imp

    val isRmp: Boolean get() = this is Rmp

    val isIff: Boolean get() = this is Iff

    val isNand: Boolean get() = this is Nand


    open val isXor: Boolean get() = false

    val isXorOrContainsXor: Boolean
        get() {
            if (isXor) return true
            if (isConstant || isLit) return false
            for (e in argIt) {
                if (e.isXorOrContainsXor) {
                    return true
                }
            }
            return false
        }

    val isXorOrContainsModelXor: Boolean
        get() = isXorOrContainsXor(MDL_PREFIX)

    val isIffOrContainsIff: Boolean
        get() {
            if (isIff) return true
            if (isConstant || isLit) return false
            for (e in argIt) {
                if (e.isIffOrContainsIff) {
                    return true
                }
            }
            return false
        }

    val isOrContainsAnd: Boolean
        get() {
            if (isAnd) return true
            if (isConstant || isLit) return false
            for (e in args) {
                if (e.isOrContainsAnd) {
                    return true
                }
            }
            return false
        }

    val arg1: Exp get() = this[0]

    val arg2: Exp get() = this[1]

    val expr1: Exp
        get() = arg1

    val expr2: Exp
        get() = arg2


    val first: Exp
        get() = arg1

    val second: Exp
        get() = arg2


    val isCnf: Boolean
        get() = isAndOfClauses || isClause || isLit

    open val isAndOfClauses: Boolean
        get() = false

    val isBinaryAnd: Boolean
        get() = isAnd && argCount() == 2

    val isBinaryOr: Boolean
        get() = isOr && argCount() == 2

    open val isBinaryType: Boolean
        get() = false

    val isUnary: Boolean
        get() = argCount == 1

    val isPair: Boolean
        get() = argCount() == 2

    val isBinary: Boolean
        get() = isPair

    val isNary: Boolean
        get() = argCount() > 2

    /**
     * And with only vr args
     */
    val isCube: Boolean
        get() {
            val retVal = isAnd && isAllLits
            if (retVal) {
                assert(isCubeExp)
            }
            return retVal
        }


    open val isLeaf: Boolean
        get() = false


    open val cubesRough: Set<Cube>
        get() = computeCubesRough()

    val products: Set<Cube>
        get() = cubesSmooth

    val bB1: DynCube
        get() = computeBB1()

    val bB2: DynCube
        get() = computeBB2()


    val forEachSatCount: Int
        get() = cubesSmooth.size


    val cubesSmoothCount: Int
        get() = cubesSmooth.size

    open val isDAnd: Boolean get() = this is DAnd

    open val isCubeExp: Boolean get() = this is CubeExp

    open val isOr: Boolean get() = false

    open val isAnd: Boolean get() = this is And

    open val isDOr: Boolean get() = this is DOr

    open val isDcOr: Boolean get() = this is DcOr

    open val complexFccs: Exp get() = throw UnsupportedOperationException(javaClass.name)

    open val isFormula: Boolean get() = this is KFormula

//    open val isFcc: Boolean get() = this is KFormula && fcc != null && fcc!!

    open val isDFormula: Boolean
        get() = false

    open val isElement: Boolean
        get() = false

    val productCount: Int
        get() = products.size

    val dontCares: VarSet
        get() {
            val allVars = _space.getVars()
            val careVars = vars
            val dontCares = allVars.minus(careVars)
            return dontCares
        }

    /**
     * To better support smoothness
     */
    val isDontCareOr: Boolean
        get() = isOr && argCount() == 2 && arg1 === arg2.flip

    /**
     * To better support smoothness
     */
    val isTrueish: Boolean
        get() = isConstantTrue || isDontCareOr

    val ymxiVars: VarSet
        get() {
            val vars = vars
            return vars.filter(object : VarFilter() {
                override fun accept(vr: Var): Boolean {
                    return vr.isCoreXor
                }
            })
        }

    val isOrContainsSeriesYearAnd: Boolean
        get() {
            if (isConstant) return false
            if (isLit) return false
            if (isSeriesYearAnd) {
                return true
            } else {
                for (arg in argIt) {
                    if (arg.isOrContainsSeriesYearAnd) {
                        return true
                    }
                }
            }
            return false
        }

    val isOrContainsSeriesYearAndPlus: Boolean
        get() {
            if (isConstant) return false
            if (isLit) return false
            if (isSeriesYearAndPlus) {
                return true
            } else {
                for (arg in argIt) {
                    if (arg.isOrContainsSeriesYearAndPlus) {
                        return true
                    }
                }
            }
            return false
        }

    override fun compareTo(that: Exp): Int {
        val ret = COMPARATOR_BY_EXP_ID.compare(this, that)
        return ret
    }

    /**
     * Code is the same as unsigned head
     *
     * @return
     */
    override val code: String
        get() {
            val ret: String
            if (isLit) {
                ret = varCode
            } else {
                val posOp = posOp
                val name = posOp.name
                ret = name.toLowerCase()
            }

            return ret
        }

    /**
     * aka signed code
     */
    val head: String
        get() = if (isPos) {
            if (isComplex)
                posOp.toString().toLowerCase()
            else
                posOp.toString().toLowerCase()
        } else {
            NOT_TOKEN + code
        }

    val litsDeep: List<Lit>
        get() {
            val a = ArrayList<Lit>()
            for (arg in args) {
                if (arg.isConstant)
                    continue
                else if (arg.isLit)
                    a.add(arg.asLit)
                else if (arg.isNot)
                    a.addAll(arg.arg.litsDeep)
                else if (arg.isComplex)
                    a.addAll(arg.litsDeep)
                else
                    throw IllegalStateException()
            }
            return a
        }


    val msrpVars: VarSet
        get() = vars.filter("DLR")


    val varCodeIt: Iterable<String>
        get() = vars.varCodeIt()

    val vars2: ImmutableSet<Int>
        get() = throw UnsupportedOperationException(javaClass.name)

    open val isNegationNormalForm: Boolean get() = false

    //    @Override
    //    final public boolean equals(Object that) {
    //        if (that == null) {
    //            return false;
    //        }
    //
    //        Class thisCls = this.getClass();
    //        Class thatCls = that.getClass();
    //
    //        if (thisCls != thatCls) {
    //            return false;
    //        }
    //
    //        int thisExpId = this.hashCode();
    //        int thatExpId = that.hashCode();
    //
    //        return thisExpId == thatExpId;
    //    }

    open val vr: Var
        get() = throw UnsupportedOperationException(javaClass.toString() + "")


    open val varCode2: VarCode
        @Throws(UnsupportedOperationException::class)
        get() = throw UnsupportedOperationException()

    open val varCode: String
        @Throws(UnsupportedOperationException::class)
        get() = throw UnsupportedOperationException(simpleName)


    val isOpen: Boolean
        get() = !isConstant

    val isTrue: Boolean
        get() = isConstantTrue

    val isFalse: Boolean
        get() = isConstantFalse


    open val posOp: PosOp
        get() = throw UnsupportedOperationException()

    abstract val firstVar: Var

    val complexOpToken: String
        get() {
            assert(isComplex)
            return posOp.getComplexOpToken(Settings.get().ser)
        }


    val token: String
        get() = getToken(Settings.get().ser)


    val isVarMap: Boolean
        get() = code.equals("varCode", ignoreCase = true)

    val isNegated: Boolean
        get() = isNot || isNegLit

    //no op1
    var xorParent: Xor?
        get() = if (isPosLit) {
            vr.xorParent
        } else {
            throw IllegalStateException("getXorParent does not make sense for class:" + javaClass.name)
        }
        set(xorParent) {}

    val isXorChild: Boolean
        get() {
            if (!isPosLit) {
                return false
            }

            val xorParent = xorParent
            return xorParent != null
        }


    val isOrContainsConstants: Boolean
        get() {
            if (isConstant) {
                return true
            }

            if (isPosLit) {
                return false
            }

            if (isNot) {
                return arg.isOrContainsConstants
            }

            if (isNary || isPair) {
                val args = args
                for (arg in args) {
                    if (arg.isOrContainsConstants) {
                        return true
                    }
                }
            }

            return false

        }


    //    public Exp[] xorSplit(Xor xor) {
    //        List<Exp> args = xor.asXor.args;
    //        Exp[] s = new Exp[args.size()];
    //        for (int i = 0; i < args.size(); i++) {
    //            s[i] = simplify(xor, i);
    //        }
    //        return s;
    //    }


    open val op: Op
        get() = throw UnsupportedOperationException(this.javaClass.name)


    val isAllPosLits: Boolean
        get() = isAllPosLits(args)

    val isAllLits: Boolean
        get() = isAllLits(argIt)

    val isAllConstants: Boolean
        get() = isAllConstants(argIt)

    val isAllComplex: Boolean
        get() = isAllComplex(argIt)

    val contentModel: String
        get() = getContentModel(argIt)

    val isYrSerMdl: Boolean
        get() {

            if (!isAllPosLits) return false
            if (arg.size() != 3) return false


            var yrCount = 0
            var serCount = 0
            var mdlCount = 0

            for (arg in args) {
                val prefix = arg.asVar().getPrefixCode()
                if (prefix == "YR") yrCount++
                if (prefix == "SER") serCount++
                if (prefix == "MDL") mdlCount++
            }

            return yrCount == 1 && serCount == 1 && mdlCount == 1


        }

    val isEveryArgAnAndYrSerMdl: Boolean
        get() {
            for (arg in args) {
                if (!arg.isAndYrSerMdl) {
                    return false
                }
            }
            return true
        }

    val isAndYrSerMdl: Boolean
        get() = isAnd && isYrSerMdl

    val isOrOfAndYrSerMdl: Boolean
        get() = isOr && isEveryArgAnAndYrSerMdl

    //non-and, 2 args, either lit-lit, lit-cube or lit-NotClause

    val isImpishOp: Boolean get() = this is Or || this is Imp || this is Rmp || this is Iff || this is Nand

    val hasLitArg: Boolean get() = if (this is PosComplexMultiVar) this._args.any { it is Lit } else false

    val hasCubeishArg: Boolean get() = this.args.any { it.isCubeExp || it.isNotClause }
    val hasCubeArg: Boolean get() = this.args.any { it.isCubeExp }
    val hasNotClauseArg: Boolean get() = this.args.any { it.isNotClause }

    val isImpish: Boolean get() = isImpishOp && isPair


    val isImpliesOrOfAndYrSerMdl: Boolean
        get() {
            if (!isImpish) return false

            if (arg1.isOrOfAndYrSerMdl) return true
            return if (arg2.isOrOfAndYrSerMdl) true else false

        }


    val firstPosLitOfTypeYr: Exp?
        get() = getFirstPosLitOfType("YR")

    val firstPosLitOfTypeSer: Exp?
        get() = getFirstPosLitOfType("SER")

    val firstPosLitOfTypeMdl: Exp?
        get() = getFirstPosLitOfType("MDL")


    val isModCore: Boolean
        get() = Mod.CORE.`is`(this)

    val isModTrim: Boolean
        get() = Mod.TRIM.`is`(this)

    val isModColor: Boolean
        get() = Mod.COLOR.`is`(this)

    val isModAccessory: Boolean
        get() = Mod.ACCESSORY.`is`(this)

    val isModTrimLocal: Boolean
        get() = Mod.TRIM.isLocal(this)

    val isModColorLocal: Boolean
        get() = Mod.COLOR.isLocal(this)

    val isModAccessoryLocal: Boolean
        get() = Mod.ACCESSORY.isLocal(this)

    val isArgsNnf: Boolean
        get() {
            for (arg in args) {
                if (!arg.isNnf) {
                    return false
                }
            }
            return true
        }


    val isArgsBnf: Boolean
        get() {
            for (arg in args) {
                if (!arg.isBnf) {
                    return false
                }
            }
            return true
        }

    val isNnf: Boolean
        get() = if (isLit) {
            true
        } else if (isAnd || isOr) {
            isArgsNnf
        } else {
            false
        }

    val isBnf: Boolean
        get() = if (isLit) {
            true
        } else if (isAnd || isOr) {
            isArgsBnf
        } else if (isNegComplex) {
            pos.isBnf
        } else {
            false
        }


    val macroType: MacroType
        get() = posOp.macroType


    override val varCount: Int
        get() {
            if (isConstant) return 0
            return if (isLit)
                1
            else
                vars.size
        }

    val isVv: Boolean
        get() {
            if (isConstant) return false
            if (isLit) return false
            if (isNot) return pos.isVv

            assert(isPosComplex)
            if (size() != 2) return false

            if (!arg1.isLit) return false
            if (!arg2.isLit) return false

            if (varCount == 1) {
                assert(isDontCareOr)
                return false
            }
            assert(varCount == 2)

            return true


        }

    val isVVPlus: Boolean
        get() = varCount > 2

    val isVVPlusWithSeriesAndYear: Boolean
        get() = if (isXorOrContainsXor) false else isVVPlus && containsSeriesVar() && containsYearVar()

    val isVVPlusWithSeriesAndModel: Boolean
        get() = if (isXorOrContainsXor) false else isVVPlus && containsSeriesVar() && containsModelVar()


    val isRequiresOr: Boolean
        get() {
            val a1 = arg1
            val a2 = arg2

            if (isImp && a1.isPosLit && a2.isOr) {
                return true
            } else if (isRmp && a2.isPosLit && a1.isOr) {
                return true
            }
            return false
        }

    val isSeriesXor: Boolean
        get() {
            if (!isXor) return false

            for (exp in args) {
                if (!exp.isSeriesVar) {
                    return false
                }
            }

            return true
        }

    val isYearXor: Boolean
        get() {
            if (!isXor) return false

            for (exp in args) {
                if (!exp.isYearVar) {
                    return false
                }
            }

            return true
        }

    val isInteriorColorXor: Boolean
        get() {
            if (!isXor) return false

            for (exp in args) {
                if (!exp.isIColVar) {
                    return false
                }
            }

            return true
        }

    val isExteriorColorXor: Boolean
        get() {
            if (!isXor) return false

            for (exp in args) {
                if (!exp.isXColVar) {
                    return false
                }
            }

            return true
        }

    val isModelXor: Boolean
        get() {
            if (!isXor) return false

            for (exp in args) {
                if (!exp.isModelVar) {
                    return false
                }
            }

            return true
        }

    //    public boolean isModelVar() {
    //        return isVar() && asVar().getPrefixCode().equals(Mod.PREFIX_MDL);
    //    }
    //
    //    public boolean isIColVar() {
    //        return isVar() && asVar().getPrefix().equals(Mod.PREFIX_ICOL);
    //    }
    //
    //    public boolean isXColVar() {
    //        return isVar() && asVar().getPrefixCode().equals(Mod.PREFIX_XCOL);
    //    }

    val isColorRelated: Boolean
        get() {
            val ICOL = Mod.PREFIX_ICOL + "_"
            val XCOL = Mod.PREFIX_XCOL + "_"
            val sss = toString()
            return sss.contains(ICOL) || sss.contains(XCOL)
        }

    val isFlat: Boolean
        get() {
            if (isConstant) return true
            if (isLit) return true
            for (e in args) {
                if (!e.isLit) {
                    return false
                }
            }
            return true
        }

    val isNestedOr: Boolean
        get() {
            if (!isOr) return false
            for (e in argIt) {
                if (e.isOr) {
                    return true
                }
            }
            return false
        }

    val isNestedAnd: Boolean
        get() {
            if (!isAndLike) {
                return false
            }
            for (e in args) {
                if (e.isAndLike) {
                    return true
                }
            }
            return false
        }

    val isNestedNot: Boolean
        get() = if (!isNot) false else arg.isNot

    val isTwoVarAnd: Boolean
        get() = isAnd && isVv && arg1.isPosLit && arg2.isPosLit

    val isYearVar: Boolean
        get() = false

    val isModelVar: Boolean
        get() = isPosLit && vr.isModelVar

    val isIColVar: Boolean
        get() = false

    val isXColVar: Boolean
        get() = false

    val isYearLit: Boolean
        get() = isLit && vr.isYearVar

    val isYearPosLit: Boolean
        get() = isPosLit && vr.isYearVar

    val isSeriesPosLit: Boolean
        get() = isPosLit && vr.isSeriesVar

    val isModelPosLit: Boolean
        get() = isPosLit && vr.isModelVar

    val isModelNegLit: Boolean
        get() = isNegLit && vr.isModelVar

    val isAcyNegLit: Boolean
        get() = isNegLit && vr.isAcyVar

    val isSeriesYearAnd: Boolean
        get() {
            if (!isAnd) return false
            if (argCount() != 2) return false

            var seriesPosLitCount = 0
            var yearPosLitCount = 0
            for (arg in argIt) {
                if (arg.isSeriesPosLit) seriesPosLitCount++
                if (arg.isYearPosLit) yearPosLitCount++
            }

            return seriesPosLitCount == 1 && yearPosLitCount == 1

        }

    //            if (argCount() != 2) return false;
    val isSeriesYearAndPlus: Boolean
        get() {
            if (!isAnd) return false

            var seriesPosLitCount = 0
            var yearPosLitCount = 0
            for (arg in argIt) {
                if (arg.isSeriesPosLit) seriesPosLitCount++
                if (arg.isYearPosLit) yearPosLitCount++
            }

            return seriesPosLitCount == 1 && yearPosLitCount == 1

        }

    val isModelYearAnd: Boolean
        get() {
            if (!isAnd) return false
            if (argCount() != 2) return false

            var modelPosLitCount = 0
            var yearPosLitCount = 0
            for (arg in argIt) {
                if (arg.isModelPosLit) modelPosLitCount++
                if (arg.isYearPosLit) yearPosLitCount++
            }

            return modelPosLitCount == 1 && yearPosLitCount == 1

        }

    val isSeriesVar: Boolean
        get() = false

    val isSeriesLiteral: Boolean
        get() = isLit && vr.isSeriesVar

    val isModelLiteral: Boolean
        get() {
            val lit = isLit
            val vr = vr
            val prefixCode = vr.prefixCode ?: return false

            val mdl = prefixCode == "MDL"
            return lit && mdl
        }


    val isSeriesModelVV: Boolean
        get() = isVv && containsSeriesVar() && containsModelVar()

    val seriesVar: Var?
        get() {
            for (vr in vars) {
                if (vr.isSeriesVar) {
                    return vr
                }
            }
            return null
        }

    val yearVar: Var?
        get() {
            for (vr in vars) {
                if (vr.isYearVar) {
                    return vr
                }
            }
            return null
        }

    val modelVar: Var?
        get() {
            for (vr in varIt()) {
                if (vr.isModel) {
                    return vr
                }
            }
            return null
        }


    /**
     * iff(var1,and(var2,var3))
     */
    val isAndVarIff: Boolean
        get() {
            if (!isIff) return false
            val a1 = arg1
            val a2 = arg2

            return if (a1.isPosLit && a2.isAnd && a2.isVv) {
                a2.arg1.isPosLit && a2.arg2.isPosLit
            } else if (a2.isPosLit && a1.isAnd && a1.isVv) {
                a1.arg1.isPosLit && a1.arg2.isPosLit
            } else {
                false
            }

        }

    override val varId: Int
        get() = throw UnsupportedOperationException()

    val spaceId: Int
        get() = _space.getSpaceId()


    val tagName: String
        get() = simpleName.toLowerCase()

    open val isSat: Boolean get() = th


    open val bb: DynCube
        get() = computeBB()


    val isSolved: Boolean
        get() = isConstantTrue || isLit || isCube


    val isClause: Boolean
        get() = isOr && isAllLits || isLit

    val isClauseOrLit: Boolean
        get() = isLit || isClause

    val longestPathToLeaf: Int
        get() {

            if (isLeaf) {
                return 0
            }

            var longest = -1
            for (arg in args) {

                val p = arg.longestPathToLeaf
                if (p > longest) {
                    longest = p
                }
            }

            return longest + 1
        }

    open val cubes: Set<Cube>
        get() = cubesSmooth

    open val cubesSmooth: Set<Cube>
        get() = computeCubesSmooth()

    //only works c smooth nodes
    //System.err.println("getSatCount: " + satCount + " " + getSimpleName() + " " + toString());
    open val satCount: BigInteger
        get() {
            if (_satCount == null) {
                _satCount = computeSatCount()
            }
            if (_satCount!! < BigInteger.ZERO) {
                throw IllegalStateException(simpleName + " " + toString())
            }
            return _satCount!!
        }

    //only works c smooth nodes
    val satCountLong: Long
        get() = satCount.toLong()

    open val cubeCount: Int
        get() = throw UnsupportedOperationException(javaClass.name)

    open val isDnnf: Boolean
        get() = false


    open val isSmooth: Boolean
        get() = throw UnsupportedOperationException(javaClass.name)

    open val smooth: Exp
        get() = throw UnsupportedOperationException(this.simpleName)

    /**
     * or(!MDL_2842 and(SER_venza YR_2014))
     */
    val isModelImpliesSeriesYear: Boolean
        get() {
            if (isConstant) return false
            if (isLit) return false
            if (!isOr) return false

            var modelNegLitCount = 0
            var seriesYearAndCount = 0
            for (arg in this.argIt) {
                if (arg.isModelNegLit) {
                    modelNegLitCount++
                }
                if (arg.isSeriesYearAnd) {
                    seriesYearAndCount++
                }
            }

            return modelNegLitCount == 1 && seriesYearAndCount > 0

        }

    val isYsmAnd: Boolean
        get() {
            if (isConstant) return false
            if (isLit) return false
            if (!isAnd) return false

            for (arg in argIt) {
                if (!arg.isPosLit) return false
                val vr = arg.vr
                val ysm = vr.isYear || vr.isSeries || vr.isModel
                if (!ysm) return false
            }


            assert(isAnd)
            assert(argCount() == 3)

            return true
        }

    val pd: Int
        get() {
            if (this._pd == null) {
                this._pd = computePd()
            }
            return this._pd!!
        }

    val isRoot: Boolean
        get() = parents == null

    fun eval(ctx: Cube): Bit {
        val s = condition(ctx)
        if (s.isConstantTrue) return Bit.TRUE
        return if (s.isConstantFalse) Bit.FALSE else Bit.OPEN
    }


    open fun condition(lit: Lit): Exp {
        throw UnsupportedOperationException(javaClass.name)
    }

    //Exp nn = n.con("SER_tacoma", "YR_2014");
    fun con(vararg sLits: String): Exp {
        val space = _space
        val aa = DynCube(space)
        for (sLit in sLits) {
            val lit = space.mkLit(sLit)
            aa.assign(lit)
        }
        return condition(aa)
    }


    fun con(vararg tLits: Var): Exp {
        val space = _space
        val aa = DynCube(space)
        for (vr in tLits) {
            aa.assign(vr, true)
        }
        return condition(aa)
    }

    fun conditionOnDealerCode(dealerCode: Int): Exp {
        return conditionOnDealerCodeInt32Dealer(dealerCode)
    }

    //    public Exp conditionOnDealerCodes(int... dealerCodes) {
    //        return conditionOnDealerCodesVarPerDealer(dealerCodes);
    //    }


    fun conditionOnDealerCodeInt32Dealer(int32Value: Int): Exp {
        val space = _space
        val cube = space.getCubeForInt32(int32Value, DLR_PREFIX)
        return condition(cube)
    }

    fun condition(int32Value: Int, int32VarPrefix: String): Exp {
        val space = _space
        val cube = space.getCubeForInt32(int32Value, int32VarPrefix)
        return condition(cube)
    }

    fun condition(c: ConditionOn): Exp {
        return if (c is Lit) {
            condition(c)
        } else if (c is Cube) {
            condition(c)
        } else {
            throw IllegalStateException()
        }
    }

    open fun condition(ctx: Cube): Exp {
        throw UnsupportedOperationException(javaClass.name)
    }

    fun condition(ctx: VarSet): Exp {
        val cube = PosCube(ctx)
        return condition(cube)
    }

    /**
     * For testing purposes
     */
    fun simplify(assignments: String): Exp? {
        val ctx = EvalContexts.fromAssignmentString(assignments)
        return null
    }

    fun simp(assignments: String): String {
        return simplify(assignments)!!.toString()
    }

    fun asVar(): Var {
        throw IllegalStateException(this.javaClass.name + ":" + toString() + ": cannot be converted to Var")
    }

    open val asCube: Cube get() = this as CubeExp
    open val asCubeExp: CubeExp get() = this as CubeExp

    val asExp: Exp get() = this as Exp
    val asLit: Lit get() = this as Lit
    val asPosComplex: PosComplexMultiVar get() = this as PosComplexMultiVar
    val asDcOr: DcOr get() = this as DcOr
    val asDAnd: DAnd get() = this as DAnd
    val asDOr: DOr get() = this as DOr
    val asXor: Xor get() = this as Xor
    val asNand: Nand get() = this as Nand
    val asOr: Or get() = this as Or
    val asAnd: And get() = this as And
    val asFormula: KFormula get() = this as KFormula

    open fun asDOr(): DOr {
        throw UnsupportedOperationException()
    }

    fun asImp(): Imp {
        return this as Imp
    }

    fun asIff(): Iff {
        return this as Iff
    }

    fun asRmp(): Rmp {
        return this as Rmp
    }

    open fun isConstant(sign: Boolean): Boolean {
        return false
    }

    fun isPosLitOfType(prefix: String): Boolean {
        return isPosLit && vr.`is`(prefix)
    }

    fun asNot(): Not {
        return this as Not
    }

    override val vars: VarSet
        get() = throw UnsupportedOperationException(javaClass.name)


    fun isXorOrContainsXor(prefix: String): Boolean {
        if (isXor(prefix)) {
            return true
        }
        if (isConstant || isLit) return false
        for (e in argIt) {
            if (e.isXorOrContainsXor(prefix)) {
                return true
            }
        }
        return false
    }

    fun serializeTinyDnnf(): String {
        val a = Ser()
        serializeTinyDnnf(a)
        return a.toString().trim { it <= ' ' }
    }

    @JvmOverloads
    fun serializeTinyDnnfSpace(gcFirst: Boolean = true): String {
        return if (gcFirst) {
            gc()._space.serializeTinyDnnf()
        } else {
            _space.serializeTinyDnnf()
        }
    }


    open fun serializeTinyDnnf(a: Ser) {
        throw UnsupportedOperationException(javaClass.name)
    }


    open fun argsRest(): List<Exp> {
        throw UnsupportedOperationException()
    }


    @Throws(IndexOutOfBoundsException::class)
    fun arg(i: Int): Exp {
        return getArg(i)
    }

    fun argCount(): Int {
        return size()
    }

    open fun size(): Int {
        return argCount
    }

    fun firstTermIsBinaryAndSentence(): Boolean {
        return arg1.isBinaryAnd
    }

    fun secondTermIsBinaryAndSentence(): Boolean {
        return arg2.isBinaryAnd
    }

    fun argList(): List<Exp> {
        return ImmutableList.copyOf(args)
    }


    /**
     * True if all args are of type Var
     */
    val allVarArgs: Boolean
        get() {
            for (exp in args) {
                if (!exp.isPosLit) return false
            }
            return true
        }


//    public boolean isCubeExp() {
//        boolean retVal = isCube();
//        if (retVal) {
//            assert isDAnd();
//        }
//        return retVal;
//    }

    fun containsArgOfType(cls: Class<*>): Boolean {
        if (isComplex) {
            for (arg in args) {
                if (arg.javaClass == cls) {
                    return true
                }
            }
        }
        return false
    }

    fun opTag(): String {
        return head
    }

    fun ensureSign(sign: Boolean): Exp {
        throw UnsupportedOperationException(javaClass.name)
    }

    fun `is`(filter: Filter): Boolean {
        return filter.accept(this)
    }


    fun transform(vararg transformers: Transformer): Exp {
        if (transformers.size == 1) {
            return transformers[0].transform(this)
        } else {
            val t = CompoundTransformer(*transformers)
            return t.transform(this)
        }
    }


    fun litItFromExpArray(): Iterable<Lit> {

        return object : Iterable<Lit> {
            override fun iterator(): Iterator<Lit> {
                return litIterator()
            }
        }
    }

    open fun litIterator(): Iterator<Lit> {
        val it = argIter

        return object : UnmodifiableIterator<Lit>() {
            override fun hasNext(): Boolean {
                return it.hasNext()
            }

            override fun next(): Lit {
                val next = it.next()
                return next.asLit
            }
        }
    }

    open fun computeSat(lit: Lit): Boolean {
        throw UnsupportedOperationException(javaClass.name)
    }

    open fun computeSat(cube: Cube): Boolean {
        throw UnsupportedOperationException(javaClass.name)
    }

    open fun computeSat(trueVars: VarSet): Boolean {
        throw UnsupportedOperationException(javaClass.name)
    }

    open fun computeValue(cube: Cube): Int {
        throw UnsupportedOperationException()
    }

    open fun computeValue(): Int {
        throw UnsupportedOperationException(simpleName)
    }

    fun computeValue(vararg sLits: String): Int {
        val space = _space
        val a = DynCube(space)
        for (sLit in sLits) {
            val lit = space.mkLit(sLit)
            a.assign(lit)
        }
        return computeValue(a)
    }

    open fun computeCubesSmooth(): Set<Cube> {
        throw UnsupportedOperationException(javaClass.name)
    }

    open fun computeCubesRough(): Set<Cube> {
        throw UnsupportedOperationException(javaClass.name)
    }

    open fun flatten(): Exp {
        return this
    }

    open fun checkDnnf(): Boolean {
        throw UnsupportedOperationException(javaClass.name)
    }

    fun chkDnnf(): Exp {
        assert(checkDnnf())
        return this
    }

    fun project(vararg outVars: String): Exp {
        val space = _space
        val b = space.newMutableVarSet()
        for (varCode in outVars) {
            val vr = space.getVar(varCode)
            b.addVar(vr)
        }
        return project(b.build())
    }


    fun project(outVars: Set<Var>): Exp {
        val space = _space
        val b = space.newMutableVarSet()
        b.addVars(outVars)
        return project(b.build())
    }

    /**
     * Inportant: projection does not preserve *determinism*.
     * This means that the result "project" is non-deterministic. That is:
     * it is no longer a d-DNNf.
     * It is now just a DNNF
     * And model counting no longer works
     * You now need to to computeModel (aka all-sat, aka forEach( and count those
     */
    open fun project(outVars: VarSet): Exp {
        throw UnsupportedOperationException(javaClass.name)
    }

//
//    final public void print() {
//        print(0);
//    }


//    public void print(int depth) {
//
//    }

    open fun getValue(vr: Var): Bit {
        throw UnsupportedOperationException(javaClass.name)
    }


    open fun computeBB(): DynCube {
        return computeBB1()
        //        return computeBB_UsingTooManyConditions(); //todo fix this
    }

    fun computeBB1(): DynCube {
        assert(isSat)


        val b = DynCube(_space)

        for (vr in varIt()) {
            val t = vr.mkPosLit()
            val f = vr.mkNegLit()
            val tSat = computeSat(t)
            val fSat = computeSat(f)

            if (tSat && !fSat) {
                b.assign(t)
            } else if (!tSat && fSat) {
                b.assign(f)
            } else if (!tSat && !fSat) {
                System.err.println("NOT SAT")
                System.err.println(this.toString())
                throw IllegalStateException(vr.varCode)
            } else {
                //truly open
            }
        }

        return b
    }

    fun computeBB2(): DynCube {
        val space = _space
        assert(isSat)
        val b = DynCube(space)

        for (vr in varIt()) {
            val t = vr.mkPosLit()
            val f = vr.mkNegLit()

            val tSat = condition(t).isSat
            val fSat = condition(f).isSat

            if (tSat && !fSat) {
                b.assign(t)
            } else if (!tSat && fSat) {
                b.assign(f)
            } else if (!tSat && !fSat) {
                throw IllegalStateException()
            } else {
                //truly open
            }
        }
        return b
    }


    fun computeBB_UsingTooManyConditions(): DynCube {

        assert(isSat)

        val b = DynCube(_space)

        for (vr in varIt()) {
            val t = vr.mkPosLit()
            val f = vr.mkNegLit()


            //            boolean tSat = computeSat(tCon);
            //            boolean fSat = computeSat(fCon);

            val tt = condition(t)
            val ff = condition(f)

            val tSat = tt.isSat
            val fSat = ff.isSat

            if (tSat && !fSat) {
                b.assign(t)
            } else if (!tSat && fSat) {
                b.assign(f)
            } else if (!tSat && !fSat) {
                throw IllegalStateException(vr.varCode)
            } else {
                //truly open
            }
        }

        return b
    }


    fun checkEmptyCubeValueAgainstSatCount() {

    }

    fun computeValue(tLit: Lit): Long {
        return 0
    }

    fun chkTrue(): Exp {
        val actual = toString()
        if (isConstantTrue) {
            return this
        } else {
            System.err.println("expected  [true]")
            System.err.println("actual    [$actual]")
            throw IllegalStateException()
        }
    }


    fun chkFalse(): Boolean {
        val actual = toString()
        if (isConstantFalse) {
            return true
        } else {
            System.err.println("expected  [false]")
            System.err.println("actual    [$actual]")
            throw IllegalStateException()
        }
    }


    fun findSubsumedVVs(vvs: VVs): Collection<Exp> {


        throw UnsupportedOperationException()
    }


    open fun toDnnf(): Exp {
        throw UnsupportedOperationException(javaClass.name + ": " + this)
    }

    fun toDnnfSmooth(): Exp {
        return toDnnf().smooth
    }

    open fun print() {
        System.err.println(simpleName)
    }


    open fun print(heading: String) {
        System.err.println(simpleName + heading)
        for (arg in argIt) {
            System.err.println("  $arg")
        }
    }

    open fun computeUnionFind(): UnionFind {
        throw UnsupportedOperationException()
    }


    fun checkSpace(space: Space): Boolean {
        assert(_space === space) { javaClass.name }

        for (exp in argIt) {
            exp.checkSpace(space)
        }

        return true
    }

    open val hasFlip: Boolean get() = th

    open fun serializeGiantOr(a: Ser) {
        throw UnsupportedOperationException()
    }

    fun serializeGiantOr(): String {
        val a = Ser()
        serializeGiantOr(a)
        return a.toString()
    }


//    public boolean isLitMatched() {
//        throw new UnsupportedOperationException(getClass().getName());
//    }


    open fun litMatch(): Exp {
        throw UnsupportedOperationException(javaClass.name)
    }

    open val asFalse: False get() = th

    open fun smooth(dontCares: VarSet): Exp {
        throw UnsupportedOperationException(javaClass.name)
    }

    open fun _setIsSmooth() {
        throw UnsupportedOperationException(javaClass.name)
    }

    fun getCoreVars(varInfo: VarInfo): VarSet {
        val vars = vars
        return vars.filter(object : VarFilter() {
            override fun accept(vr: Var): Boolean {
                return vr.isCoreXor || varInfo.isInvAcy(vr.varCode)
            }
        })
    }


    open val satCountPL: Long get() = th

    fun sameSign(that: Lit): Boolean {
        return asLit.isPos == that.isPos
    }

    fun sameVar(that: Lit): Boolean {
        return varId == that.varId
    }

    fun printXml() {
        System.err.println(toXml())
    }

    fun getVar(code: String): Var {
        return _space.getVar(code)
    }

    class LitArg(val varMatch: Boolean, val signMatch: Boolean) {
        companion object {

            val NONE = LitArg(false, false)
            val FLIP = LitArg(true, false)
            val BOTH = LitArg(true, true)
        }

    }


    /**
     * @param sLits
     * @return
     */
    fun condition(sLits: String): Exp {
        val conditionOn = _space.parser.parseLitsToConditionOn(sLits)

        return if (conditionOn is Lit) {
            condition(conditionOn as Lit)
        } else if (conditionOn is Cube) {
            condition(conditionOn as Cube)
        } else {
            throw IllegalStateException()
        }
        //        return when (conditionOn) {
        //            is Lit -> condition(conditionOn)
        //            is Cube -> condition(conditionOn)
        //            else -> throw IllegalStateException()
        //        }

        //        return conditionOn.conditionThat(this);
    }


    fun compare(that: Exp): LitArg {
        if (!that.isLit) return LitArg.NONE
        if (!this.isLit) return LitArg.NONE
        if (this.varId != that.varId) return LitArg.NONE
        if (this === that) return LitArg.BOTH
        if (this === that.flip) return LitArg.FLIP
        throw IllegalStateException()
    }

    fun argItFlipped(): Iterable<Exp> {
        return object : Iterable<Exp> {
            override fun iterator(): Iterator<Exp> {
                return argIteratorFlipped()
            }
        }
    }


    fun argBuilder(op: Op): ArgBuilder {
        return _space.expFactory.argBuilder(op)
    }

    fun argBuilder(op: Op, cube: Cube): ArgBuilder {
        return _space.expFactory.argBuilder(op, cube)
    }

    fun argBuilder(op: Op, args: Iterable<Exp>): ArgBuilder {
        return _space.expFactory.argBuilder(op, args)
    }

    fun mkFalse(): Exp {
        return _space.mkFalse()
    }

    fun mkTrue(): Exp {
        return _space.mkTrue()
    }

    fun mkAnd(vararg args: Exp): Exp {
        return _space.mkAnd(*args)
    }


    fun mkAnd(args: List<Exp>): Exp {
        return _space.mkAnd(args)
    }

    fun mkXor(args: List<Exp>): Exp {
        return _space.mkXor(args)
    }

    fun mkOr(vararg args: Exp): Exp {
        return _space.mkOr(*args)
    }

    fun mkOr(args: List<Exp>): Exp {
        return _space.mkOr(args)
    }

    fun mkIff(arg1: Exp, arg2: Exp): Exp {
        return ef().mkBinaryIff(arg1, arg2)
    }

    fun mkRmp(arg1: Exp, arg2: Exp): Exp {
        return _space.mkBinaryImp(arg2, arg1)
    }

    fun mkImp(arg1: Exp, arg2: Exp): Exp {
        return _space.mkBinaryImp(arg1, arg2)
    }

    fun mkNand(arg1: Exp, arg2: Exp): Exp {
        return _space.mkBinaryNand(arg1, arg2)
    }

    fun eqStr(expToString: String): Boolean {
        return toString() == expToString
    }

    fun eq(that: Exp): Boolean {
        return eq1(that) && eq2(that)
    }

    fun eq1(that: Exp): Boolean {
        return toString() == that.toString()
    }

    fun eq2(that: Exp?): Boolean {
        if (this === that) return true
        if (that == null || javaClass != that.javaClass) return false

        //        assert this._space == that._space;
        //        int id1 = this.expId;
        //        int id2 = that.expId;


        //        boolean retVal = (id1 == id2);

        if (argCount() != that.argCount()) return false
        if (javaClass != that.javaClass) return false


        if (isPosComplex) {
            val pc1 = asPosComplex
            val pc2 = that.asPosComplex
            return pc1.sameArgs(pc2)
        } else if (isNot) {
            return asNot().samePos(that.asNot())
        } else if (isLit) {
            val l1 = asLit
            val l2 = that.asLit
            return l1.toString() == l2.toString()
        } else return if (isConstantTrue) {
            that.isConstantTrue
        } else if (isConstantFalse) {
            that.isConstantFalse
        } else {
            throw IllegalStateException()
        }

    }

    fun identical(that: Any?): Boolean {
        return this === that
    }

    override fun equals(other: Any?): Boolean {
        if (identical(other)) return true
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        val that = other as Exp?


        val retVal = eq1(that!!)

        if (retVal) {
            val s1 = this.toString()
            val s2 = that.toString()
            if (s1 != s2) {
                System.err.println(s1)
                System.err.println(s2)
                throw AssertionError()
            }
        }

        return retVal

    }


    fun sp(): Space = space


    val _space: Space get() = space
    val isNotOr: Boolean get() = this is Not && this.arg.isOr
    val isNotClause: Boolean get() = this is Not && this.arg.isClause
    val isNotCube: Boolean get() = this is Not && this.arg.isCubeExp

    val notClauseToCube: CubeExp
        get() {
            if (this is Not && arg.isClause) {
                val b = ArgBuilder(space, Op.And)
                for (lit in arg.args) {
                    b.addLit(lit.asLit.flipLit)
                }
                return b.mk().asCubeExp
            } else {
                throw IllegalStateException()
            }

        }

    val notOrToAnd: Exp
        get() {
            if (this is Not && arg.isOr) {
                val b = ArgBuilder(space, Op.And)
                for (a in arg.args) {
                    b.addExp(a.flip)
                }
                return b.mk()
            } else {
                throw IllegalStateException()
            }

        }

    val notCubeToClause: Or
        get() {
            if (this is Not && arg.isCubeExp) {
                val b = ArgBuilder(space, Op.Or)
                for (lit in arg.args) {
                    b.addLit(lit.asLit.flipLit)
                }
                return b.mk().asOr
            } else {
                throw IllegalStateException()
            }

        }


    @Throws(UnsupportedOperationException::class)
    fun getVarToken(a: Ser): String {
        return varCode
    }

    override fun toString(): String {
        val a = Ser()
        serialize(a)
        return a.toString()
    }

    fun toString(a: Ser) {
        serialize(a)
    }

    override fun hashCode(): Int {
        return expId
    }

    fun isAssignedValue(value: Boolean): Boolean {
        return if (isTrue) {
            value
        } else if (isFalse) {
            !value
        } else {
            false
        }
    }


    fun sign(): Boolean {
        return isPos
    }


//    public static final Comparator<Exp> COMPARATOR_BY_SORT_KEY_2 = new Comparator<Exp>() {
//
//        @Override
//        public int compare(Exp e1, Exp e2) {
//
//            if (e1 == null && e2 == null) {
//                return 0;
//            }
//
//            if (e1 == null && e2 != null) {
//                return -1;
//            }
//
//            if (e1 != null && e2 == null) {
//                return 1;
//            }
//
//            String k1 = e1.getSortKey();
//            String k2 = e1.getSortKey();
//
//            return k1.compareTo(k2);
//        }
//    };


    fun getConstantTrueToken(a: Ser): String {
        return a.tokens.constantTrue
    }

    fun getConstantFalseToken(a: Ser): String {
        return a.tokens.constantFalse
    }

    fun getConstantToken(value: Boolean, a: Ser): String {
        val token = getConstantToken(value)
        return if (value) {
            getConstantTrueToken(a)
        } else {
            getConstantTrueToken(a)
        }
    }

    fun getConstantToken(sign: Boolean): String {
        return if (sign) TRUE_TOKEN else FALSE_TOKEN
    }

    fun getNotToken(a: Ser): String {
        return if (isNegLit) {
            a.tokens.negationVar.token
        } else if (isNegComplex) {
            a.tokens.negationComplex.token
        } else {
            throw IllegalStateException()
        }
    }

    fun getPosComplexOpToken(a: Ser): String {
        return posOp.getComplexOpToken(a)
    }

    fun getToken(a: Ser): String {
        if (isConstantTrue) return getConstantTrueToken(a)
        if (isConstantFalse) return getConstantFalseToken(a)
        if (isPosLit) return getVarToken(a)
        if (isNot) return getNotToken(a)
        if (isComplex) return getPosComplexOpToken(a)
        throw IllegalStateException(javaClass.name)
    }


    fun serializeDimacsCnf(a: Ser) {
        assert(isAnd)
        val clauses = args
        for (clause in clauses) {
            clause.serializeDimacs(a)
        }
    }

    fun serializeDimacsDnf(a: Ser) {
        assert(isOr)
        val cubes = args
        for (cube in cubes) {
            cube.serializeDimacs(a)
        }
    }

    fun serializeDimacs(a: Ser) {
        checkState(isClause || isCube)
        if (isOr || isAnd) {
            val args = args
            for (arg in args) {
                arg.serializeDimacs(a)
                a.argSep()
            }
            a.append(0)
            a.newLine()
        } else if (isLit) {
            if (isNegLit) {
                a.append("-")
            }
            a.append(varId)
        } else {
            throw IllegalStateException()
        }
    }

    fun serializeCnf(a: Ser) {
        assert(isClause) { this }
        when {
            isOr -> {
                val args = args
                for ((i, arg) in args.withIndex()) {
                    arg.serializeCnf(a)
                    if (i < argCount - 1) {
                        a.argSep()
                    }
                }
            }
            isPosLit -> {
                val varCode = varCode
                a.append(varCode)
            }
            isNegLit -> {
                a.bang()
                a.append(vr.varCode)
            }
            else -> throw IllegalStateException()
        }
    }

    fun serializeTinyCnf(a: Ser) {
        checkState(isClause, "Not a clause: " + this)
        if (isOr) {
            for ((i, arg) in args.withIndex()) {
                arg.serializeTinyCnf(a)
                if (i < argCount - 1) {
                    a.argSep()
                }
            }
        } else if (isPosLit) {
            val tinyId = asVar().tinyId
            a.append(tinyId)
        } else if (isNegLit) {
            val vr = vr
            val tinyId = vr.tinyId
            a.append("!")
            a.append(tinyId)
        } else {
            throw IllegalStateException()
        }
    }

    open fun serialize(a: Ser) {
        a.ap("Exp")
        //        throw new UnsupportedOperationException();
    }

    fun serialize(): String {
        val a = Ser()
        serialize(a)
        return a.toString().trim { it <= ' ' }
    }

    fun serializeArgs(a: Ser) {
        throw UnsupportedOperationException()
    }

    fun prindent(depth: Int, a: Ser) {
        a.append(indent(depth))
        serialize(a)
        a.append(NEW_LINE)
    }

    @Throws(Exception::class)
    fun loadToNnf() {
        throw UnsupportedOperationException()
    }


    fun copyArgs(): ImmutableList<Exp> {
        return ImmutableList.copyOf(args)
    }

    fun simplifySeriesModelAnd(): Exp {
        if (isConstant || isLit) {
            return this
        }
        assert(isComplex)
        if (isAnd && size() == 2 && arg(0).isPosLit && arg(1).isPosLit && containsSeriesVar() && containsModelVar()) {
            return if (arg(0).isModelVar)
                arg(0)
            else
                arg(1)
        } else if (isAnd && containsSeriesPosLit() && containsModelPosLit()) {
            return removeSeriesPosLitFromAnd()
        } else if (isNot) {
            return arg.simplifySeriesModelAnd().flip
        } else if (isAnd) {
            val aa = ArrayList<Exp>()
            for (arg in args) {
                aa.add(arg.simplifySeriesModelAnd())
            }
            return mkAnd(aa)
        } else if (isOr) {
            val aa = ArrayList<Exp>()
            for (arg in args) {
                aa.add(arg.simplifySeriesModelAnd())
            }
            return mkOr(aa)
        } else if (isXor) {
            val aa = ArrayList<Exp>()
            for (arg in args) {
                aa.add(arg.simplifySeriesModelAnd())
            }
            return mkXor(aa)
        } else {
            throw IllegalStateException(this.toString())
        }

    }

    /**
     * @param sm
     * @return
     */
    fun modelImplySeries(sm: SeriesModel, vvs: Set<Exp>): Boolean {
        assert(sm.seriesVar != null)
        assert(sm.modelVar != null)

        val sv = sm.seriesVar!!.asVar()
        val mv = sm.modelVar!!.asVar()


        for (vv in vvs) {


            val a1: Exp
            val a2: Exp
            if (vv.isNot) {
                a1 = vv.pos.arg1
                a2 = vv.pos.arg2
            } else {
                a1 = vv.arg1
                a2 = vv.arg2
            }

            val v1 = a1.vr
            val v2 = a2.vr

            val op = vv.posOp

            if (a1 == sv && a2 == mv) {
                if (op.isRmp) return true
            } else if (a1 == mv && a2 == sv) {
                if (op.isImp) return true
            } else if (a1.flip.equals(mv) && a2 == sv) {
                if (op.isOr) return true
            } else if (a2.flip.equals(mv) && a1 == sv) {
                if (op.isOr) return true
            }

        }

        return false

    }

    fun indexOf(a: Exp): Int {
        for ((i, arg) in args.withIndex()) {
            if (arg === a) return i
        }
        return -1
    }

    override fun containsVarId(varId: Int): Boolean {
        return false
    }

    fun containsVarsWithPrefix(prefix: String): Boolean {
        if (isConstant) {
            return false
        } else if (isLit) {
            return vr.`is`(prefix)
        } else {
            val vars = vars
            for (vr in vars) {
                if (vr.`is`(prefix)) {
                    return true
                }
            }
        }
        return false
    }

    override fun containsVar(lit: Exp): Boolean {
        return containsVarId(lit.varId)
    }

    override fun containsVar(varCode: String): Boolean {
        val space = _space
        val vr = space.getVar(varCode)
        return containsVar(vr)
    }

    override fun containsVar(vr: Var): Boolean {
        return containsVarId(vr.vrId)
    }


    override fun containsVar(lit: Lit): Boolean {
        return containsVarId(lit.varId)
    }


    fun caresAbout(vr: Var): Boolean {
        return containsVar(vr)
    }


    fun op(): Op {
        return op
    }

    inner class SeriesModel {
        var seriesVar: Exp? = null
        var modelVar: Exp? = null
    }

    fun getFirstPosLitOfType(prefix: String): Exp? {
        for (arg in args) {
            if (arg.isPosLitOfType(prefix)) {
                return arg
            }
        }
        return null
    }


    fun xorCount(): Int {
        if (isConstant || isLit) return 0
        var xc = 0
        if (isXor) {
            xc++
        }

        for (arg in args) {
            xc += arg.xorCount()
        }

        return xc

    }

    /**
     * @return true if this._complexVars.containsAll(that._complexVars)
     */
    fun vvpSubsumesVV(vv: Exp): Boolean {
        assert(vv.isVv)
        assert(isVVPlus) { javaClass.toString() + this.toString() }

        val vars = vv.vars
        val it = vars.intIterator()

        val varId1 = it.next()
        val varId2 = it.next()

        return containsVarId(varId1) && containsVarId(varId2)
    }

    fun allArgsAreModelVars(): Boolean {
        return allArgsAre(Mod.PREFIX_MDL)
    }

    fun allArgsAre(prefix: String): Boolean {
        if (isConstant || isLit) return false
        for (arg in args) {
            if (!arg.isPosLit) return false
            val pArg = arg.asVar().getPrefix()
            if (pArg != prefix) return false
        }
        return true
    }

    fun requiresOrToConflicts(allModels: ImmutableSet<Exp>): Exp {
        val space = _space

        if (isRequiresOr) {
            val vr: Var
            val or: Or
            if (isImp) {
                vr = arg1.asVar()
                or = arg2.asOr
            } else if (isRmp) {
                vr = arg2.asVar()
                or = arg1.asOr
            } else {
                throw IllegalStateException()
            }

            if (or.allArgsAreModelVars()) {

                val argModels = ImmutableSet.copyOf(or.args)
                val diff = Sets.difference(allModels, argModels)

                val conflicts = ArgBuilder(space, Op.And)
                for (model in diff) {
                    val mdlVar = model.asVar()
                    val conflict = space.mkBinaryNand(vr.pLit(), mdlVar.pLit())
                    conflicts.addExp(conflict)
                }
                return conflicts.mk()

            } else {
                return this
            }


        } else {
            return this
        }
    }

    fun isXor(prefix: String): Boolean {
        if (!isXor) return false

        for (exp in args) {
            if (!exp.isVarWithPrefix(prefix)) {
                return false
            }
        }

        return true
    }

    fun clauseToSortedSet(): TreeSet<String> {
        assert(isClause)
        val s = TreeSet<String>()
        if (isLit) {
            s.add(toString())
        } else if (isOr) {
            for (arg in args) {
                assert(arg.isLit)
                s.add(arg.toString())
            }
        } else {
            throw IllegalStateException()
        }
        return s
    }


    override fun isVarDisjoint(vars: VarSet): Boolean {
        return !anyVarOverlap(vars)
    }

    override fun isVarDisjoint(ctx: Cube): Boolean {
        return !anyVarOverlap(ctx.vars)
    }

    override fun isVarDisjoint(exp: Exp): Boolean {
        return !anyVarOverlap(exp)
    }

    override fun anyVarOverlap(vs: VarSet): Boolean {
        if (isConstant) return false
        return if (isLit) vs.containsVar(this) else vs.anyVarOverlap(vs)
    }

    override fun anyVarOverlap(exp: Exp): Boolean {
        if (this.isConstant || exp.isConstant) return false
        if (isLit && exp.isLit) return this.vr === exp.vr

        if (this.isLit) return exp.containsVar(this)
        return if (exp.isLit) this.containsVar(exp) else exp.anyVarOverlap(vars)

    }

    override fun anyVarOverlap(cube: Cube): Boolean {
        return anyVarOverlap(cube.vars)
    }


    fun conditionVV(vv: String): Exp {
        val exp = _space.parseExp(vv)
        if (!exp.isVv) {
            throw IllegalArgumentException()
        }
        return conditionVV(exp)
    }

    open fun conditionVV(vv: Exp): Exp {
        return conditionVV(vv, false)
    }

    fun conditionVV(vv: Exp, keepXors: Boolean): Exp {
        var vv = vv

        if (!vv.isNnf) {
            vv = vv.toNnf(keepXors)
        }

        if (!vv.isOr) {
            throw IllegalArgumentException(vv.toString())
        }
        return this
    }

    @JvmOverloads
    fun conditionVVs(vvs: List<Exp>, keepXors: Boolean = false): Exp {
        var nnf = toNnf(keepXors)
        for (vv in vvs) {

            nnf = nnf.conditionVV(vv)
        }
        return nnf
    }


    fun argIteratorFlipped(): Iterator<Exp> {
        val it = argIter
        return object : UnmodifiableIterator<Exp>() {
            override fun hasNext(): Boolean {
                return it.hasNext()
            }

            override fun next(): Exp {
                return it.next().flip
            }
        }
    }


    fun isVarWithPrefix(prefix: String): Boolean {
        return isPosLit && vr.prefixCode == prefix
    }

    fun containsSeriesVar(): Boolean {
        val vars = vars
        for (vr in vars.varIt()) {
            if (vr.isSeriesVar) {
                return true
            }
        }
        return false
    }

    fun containsSeriesPosLit(): Boolean {
        for (arg in args) {
            if (arg.isSeriesPosLit) return true
        }
        return false
    }

    fun containsModelPosLit(): Boolean {
        for (arg in args) {
            if (arg.isModelPosLit) return true
        }
        return false
    }

    fun removeSeriesPosLitFromAnd(): Exp {
        val a = ArrayList<Exp>()
        for (arg in args) {
            if (arg.isSeriesPosLit) continue
            a.add(arg)
        }
        return if (isAnd)
            mkAnd(a)
        else if (isOr)
            mkOr(a)
        else
            throw IllegalStateException()
    }

    fun replaceSeriesWithModels(seriesYearToModels: Multimap<SeriesYear, Var>): Exp {
        if (isConstant) return this
        if (isLit) return this
        if (isSeriesYearAndPlus) {
            return replaceSeriesWithModels(this, seriesYearToModels)
        }
        val op = op

        if (op.isNot) {
            val newArg = arg.replaceSeriesWithModels(seriesYearToModels)
            return newArg.flip
        } else {
            val b = ArgBuilder(_space, op)
            for (arg in argIt) {
                b.addExp(arg.replaceSeriesWithModels(seriesYearToModels))
            }
            return b.mk()
        }


    }

    fun containsYearVar(): Boolean {
        val vars = vars
        val space = _space
        for (vr in vars) {
            if (vr.isYear) {
                return true
            }
        }
        return false
    }

    fun containsSeriesAndYearVar(): Boolean {
        return containsSeriesVar() && containsYearVar()
    }

    fun containsModelVar(): Boolean {
        for (vr in vars) {
            if (vr.isModelVar) {
                return true
            }
        }
        return false
    }

    override fun varIterator(): Iterator<Var> {
        return vars.iterator()
    }

    override fun varIt(): Iterable<Var> {
        return object : Iterable<Var> {
            override fun iterator(): Iterator<Var> {
                return varIterator()
            }
        }
    }

    fun isAndVarIffFor(twoVarAnd: Exp): Boolean {
        if (isAndVarIff) {
            val and = andVarIff_getAnd()
            return and == twoVarAnd
        } else {
            return false
        }
    }

    fun andVarIff_getVar(): Exp {
        checkState(this.isAndVarIff)
        val a1 = arg1
        val a2 = arg2

        return if (a1.isPosLit && a2.isAnd) {
            a1
        } else if (a2.isPosLit && a1.isAnd) {
            a2
        } else {
            throw IllegalArgumentException()
        }
    }

    fun andVarIff_getAnd(): Exp {
        checkState(this.isAndVarIff)
        val a1 = arg1
        val a2 = arg2

        return if (a1.isPosLit && a2.isAnd) {
            a2
        } else if (a2.isPosLit && a1.isAnd) {
            a1
        } else {
            throw IllegalArgumentException()
        }
    }


    fun anyVarIntersection(other: Iterator<Exp>): Boolean {
        throw UnsupportedOperationException()
    }


    open fun pushNotsIn(): Exp {
        return this
    }


    open fun containsLit(lit: Exp): Boolean? {
        return false
    }

    open fun containsArg(arg: Exp): Boolean {
        return false
    }

    fun toNnf(keepXors: Boolean = true): Exp {
        val bnf = toBnf(keepXors)
        return bnf.transform(Transformer.BNF_TO_NNF)
    }

    fun toBnf(keepXors: Boolean): Exp {
        return if (keepXors) {
            transform(Transformer.BNF_KEEP_XORS)
        } else {
            transform(Transformer.BNF)
        }
    }

    fun toBnfKeepXors(): Exp {
        return transform(Transformer.BNF_KEEP_XORS)
    }

    fun anyNestingDeep(parent: Exp): Boolean {
        if (isNested(parent)) {
            return true
        }
        if (isComplex) {
            for (arg in argIt) {
                if (arg.anyNestingDeep(this)) {
                    return true
                }
            }
            return false
        } else {
            return false
        }
    }

    fun isNested(parent: Exp?): Boolean {
        if (parent == null) return false
        assert(!parent.isLeaf)

        if (parent.isAnd && isAnd) {
            return true
        }

        if (parent.isOr && isOr) {
            return true
        }

        return if (parent.isDOr && isDOr) {
            true
        } else false

    }


    fun startTag(a: Ser, depth: Int): String {

        val varCount = varCount
        val arity = argCount

        val satCount = satCount
        val spaceId = spaceId

        a.indent(depth)
        a.ap('<')
        a.ap(tagName)
        a.ap(' ')


        a.ap(" spaceId='$spaceId'")
        a.ap(" expId='" + expId + "'")
        a.ap(" arity='$arity'")
        a.ap(" varCount='$varCount'")
        a.ap(" satCount='$satCount'")

        a.ap('>')
        a.newLine()

        return a.toString()


    }

    fun endTag(a: Ser, depth: Int) {
        a.indent(depth)
        a.ap('<')
        a.ap('/')
        a.ap(tagName)
        a.ap('>')
        a.newLine()
    }

    fun toXml(): String {
        val a = Ser()
        toXml(a, 0)
        return a.toString()
    }

    fun toXml(depth: Int): String {
        val a = Ser()
        toXml(a, depth)
        return a.toString()
    }

    open fun toXml(a: Ser, depth: Int) {
        if (isComplex) {
            startTag(a, depth)
            for (arg in argIt) {
                arg.toXml(a, depth + 1)
            }
            endTag(a, depth)
        } else {
            val lit = toString()
            a.prindent(depth, lit)
        }
    }


    open fun computeCubesNoVarSet(): Set<Cube> {
        throw UnsupportedOperationException(javaClass.name)
    }

    open fun checkChildCounts(childCounts: ChildCounts) {
        //do nothing
    }

    operator fun contains(o: Any): Boolean {
        return containsArg(o as Exp)
    }

    fun mkConstantFalse(): Exp {
        return _space.mkConstantFalse()
    }

    fun mkConstantTrue(): Exp {
        return _space.mkConstantTrue()
    }

    fun getValue(varCode: String): Bit {
        val vr = _space.getVar(varCode)
        return getValue(vr)
    }


    fun createCsp(): Dnnf {
        return Dnnf(this)
    }

//    public VarSet getOpenCareVars() {
//        Exp reduced = reduce();
//        return reduced.get_complexVars();
//    }

    fun reduce(): Exp {
        val bb = bb
        return this.condition(bb)
    }


    fun forEachHead(h: NodeHandler) {
        if (!h.visited(this)) {
            h.onHead(this)
            h.markAsVisited(this)
            if (isComplex) {
                for (arg in argIt) {
                    arg.forEachHead(h)
                }
            }
        }

    }

    fun printInfo(depth: Int) {


        //        NodeInfo nodeInfo = computeNodeInfo();
        //        nodeInfo.print(depth);
    }


    @JvmOverloads
    fun printHead(depth: Int = 0) {
        //        prindent(depth, "op1:         " + getOp1());
        //        prindent(depth, "satCount:   " + getSatCount());
        //        prindent(depth, "careVars:   " + get_complexVars().size() + ": " + get_complexVars());
        //        System.err.println();
    }

    fun printInfo() {
        System.err.println(toXml())
    }

    fun printNodeInfo(depth: Int = 0) {
        val nodeInfo = NodeInfo()
        forEachHead(nodeInfo)
        nodeInfo.print(depth)
    }

    fun nodeInfo() {
        val nodeInfo = NodeInfo()
        forEachHead(nodeInfo)
        nodeInfo.print(0)
    }

    fun serializeModels(): String {
        val a = Ser()
        serializeModels(a)
        return a.toString()
    }

    fun serializeModels(a: Ser) {
        val cubes = cubesSmooth
        serializeModels(cubes, a)
    }

    fun printCubes() {
        assert(isDnnf && checkDnnf())
        val cubes = cubesSmooth
        printCubes(cubes)
    }

    fun printCubesTrueVarsOnly() {
        val cubes = cubesSmooth
        printModels(cubes)
    }

    //only works c smooth nodes
    @JvmOverloads
    fun getSatCount(parentVars: VarSet, picVars: VarSet = _space.mkEmptyVarSet()): BigInteger {
        val baseSatCount = satCount
        val dcVars = parentVars.minus(picVars).minus(vars)
        return Csp.computeDcVars(baseSatCount, dcVars.size)
    }

    open fun computeSatCount(): BigInteger {
        throw UnsupportedOperationException(javaClass.name)
    }

    open fun computeSatCount1(lit: Lit): Long {
        throw UnsupportedOperationException(javaClass.name)
    }

    fun computeSatCount2(lit: Lit): Long {
        throw UnsupportedOperationException(javaClass.name)
    }

    fun varsSet(): Set<Var> {
        val vars = vars
        throw UnsupportedOperationException()
    }

    fun putNext(next: Exp) {
        this.next = next
    }

//    public static boolean containsArg(Exp[] args, int argCount, int expId) {
//        for (int i = 0; i < argCount; i++) {
//            Exp arg = args[i];
//            if (arg.expId == expId) {
//                return true;
//            }
//        }
//
//        return false;
//    }

    fun copyToOtherSpace(): Exp {
        val sourceSpace = _space
        val varCodes = sourceSpace.getVarCodes()
        val destSpace = Space(varCodes)
        destSpace.initAllLits()
        return copyToOtherSpace(destSpace)
    }

    fun gc(): Exp {
        return copyToOtherSpace()
    }

    open fun copyToOtherSpace(destSpace: Space): Exp {
        if (_space === destSpace) {
            return this
        }
        throw UnsupportedOperationException(javaClass.name)
    }

    fun minCard(): Int {
        return minTCard()
    }

    fun minTCard(): Int {
        if (isPosLit) {
            return 1
        } else if (isNegLit) {
            return 0
        } else if (isConstantFalse) {
            throw IllegalStateException()
        } else if (isConstantTrue) {
            throw IllegalStateException()
        } else if (isDOr || isDcOr) {
            //todo: make more efficient for DcOr
            var min = Integer.MAX_VALUE
            for (arg in argIt) {
                val argMin = arg.minTCard()
                if (argMin < min) {
                    min = argMin
                }
            }
            return min
        } else if (isXor) {
            return 1
        } else if (isDAnd) {
            var sum = 0
            for (arg in argIt) {
                sum += arg.minTCard()
            }
            return sum
        } else {
            throw UnsupportedOperationException(simpleName + "  " + this)
        }
    }

    fun minFCard(): Int {
        when {
            isPosLit -> return 0
            isNegLit -> return 1
            isConstantFalse -> throw IllegalStateException()
            isConstantTrue -> throw IllegalStateException()
            isDOr -> {
                var min = Integer.MAX_VALUE
                for (arg in argIt) {
                    val argMin = arg.minFCard()
                    if (argMin < min) {
                        min = argMin
                    }
                }
                return min
            }
            isDcOr -> {
                //todo: make this more efficient
                var min = Integer.MAX_VALUE
                for (arg in argIt) {
                    val argMin = arg.minFCard()
                    if (argMin < min) {
                        min = argMin
                    }
                }
                return min
            }
            isXor -> return varCount - 1
            isDAnd -> {
                var sum = 0
                for (arg in argIt) {
                    sum += arg.minFCard()
                }
                return sum
            }
            else -> throw UnsupportedOperationException("$simpleName  ${this}")
        }
    }

    fun minModels(): Exp {
        return minTModels()
    }

    private fun minTModelsRough(): Exp {
        val mCard = minTCard()
        val space = _space
        if (isConstant || isLit) {
            return this
        } else if (isDOr || isDcOr) {
            //todo: make more efficient for DcOr
            val parentVars = vars
            val b = ArgBuilder(space, Op.DOr)
            for (arg in argIt) {
                val argMCard = arg.minTCard()
                if (argMCard == mCard) {
                    val childVars = arg.vars
                    val dcVars = parentVars.minus(childVars)
                    val nCube = space.mkNCube(dcVars)
                    val argMinTModels = arg.minTModels()
                    val aa = space.expFactory.mkDAnd(argMinTModels, nCube)
                    b.addExp(aa)
                }
            }
            return b.mk()
        } else if (isXor) {
            return this
        } else if (isDAnd) {
            val b = ArgBuilder(space, Op.DAnd)
            for (arg in argIt) {
                val argMinTModels = arg.minTModels()
                b.addExp(argMinTModels)
            }
            return b.mk()
        } else {
            throw UnsupportedOperationException("$simpleName  ${this}")
        }
    }

    private fun minTModelsSmooth(): Exp {
        assert(isSmooth)
        val mCard = minTCard()
        val space = _space
        if (isConstant || isLit) {
            return this
        } else if (isDOr) {
            val b = ArgBuilder(space, Op.DOr)
            for (arg in argIt) {
                val argMCard = arg.minTCard()
                if (argMCard == mCard) {
                    val argMinTModels = arg.minTModels()
                    b.addExp(argMinTModels)
                }
            }
            return b.mk()
        } else if (isXor) {
            return this
        } else if (isDAnd) {
            val b = ArgBuilder(space, Op.DAnd)
            for (arg in argIt) {
                val argMinTModels = arg.minTModels()
                b.addExp(argMinTModels)
            }
            return b.mk()
        } else {
            throw UnsupportedOperationException()
        }
    }

    fun minFModels(): Exp {
        return if (ROUGH_MIN_MODELS) {
            minFModelsRough()
        } else {
            minFModelsSmooth()
        }
    }

    fun minTModels(): Exp {
        return if (ROUGH_MIN_MODELS) {
            minTModelsRough()
        } else {
            minTModelsSmooth()
        }
    }

    private fun minFModelsRough(): Exp {
        val mCard = minFCard()
        val space = _space
        if (isConstant || isLit) {
            return this
        } else if (isDOr) {
            val parentVars = vars
            val b = ArgBuilder(space, Op.DOr)
            for (arg in argIt) {
                val argMCard = arg.minFCard()
                if (argMCard == mCard) {
                    val childVars = arg.vars
                    val dcVars = parentVars.minus(childVars)
                    val pCube = space.mkPCube(dcVars)
                    val argMinFModels = arg.minFModels()
                    val aa = space.expFactory.mkDAnd(argMinFModels, pCube)
                    b.addExp(aa)
                }
            }
            return b.mk()
        } else if (isDcOr) {
            //todo: make this more efficient
            val parentVars = vars
            val b = ArgBuilder(space, Op.DOr)
            for (arg in argIt) {
                val argMCard = arg.minFCard()
                if (argMCard == mCard) {
                    val childVars = arg.vars
                    val dcVars = parentVars.minus(childVars)
                    val pCube = space.mkPCube(dcVars)
                    val argMinFModels = arg.minFModels()
                    val aa = space.expFactory.mkDAnd(argMinFModels, pCube)
                    b.addExp(aa)
                }
            }
            return b.mk()
        } else if (isXor) {
            return this
        } else if (isDAnd) {
            val b = ArgBuilder(space, Op.DAnd)
            for (arg in argIt) {
                val argMinModels = arg.minFModels()
                b.addExp(argMinModels)
            }
            return b.mk()
        } else {
            throw UnsupportedOperationException(simpleName + "  " + this)
        }
    }

    private fun minFModelsSmooth(): Exp {
        assert(isSmooth)
        val mCard = minFCard()
        val space = _space
        if (isConstant || isLit) {
            return this
        } else if (isDOr) {
            val b = ArgBuilder(space, Op.DOr)
            for (arg in argIt) {
                val argMCard = arg.minFCard()
                if (argMCard == mCard) {
                    val argMinFModels = arg.minFModels()
                    b.addExp(argMinFModels)
                }
            }
            return b.mk()
        } else if (isXor) {
            return this
        } else if (isDAnd) {
            val b = ef().argBuilder(Op.DAnd)
            for (arg in argIt) {
                val argMinModels = arg.minFModels()
                b.addExp(argMinModels)
            }
            return b.mk()
        } else {
            throw UnsupportedOperationException()
        }
    }


    fun conditionOnAtVars(): Exp {
        val c = _space.getAtVarsAsCube()
        return condition(c.asCube)
    }

    open fun getFirstConjunctContaining(varCode: String): Exp? {
        throw UnsupportedOperationException()
    }

    fun hasMsrpVars(): Boolean {
        return vars.containsMsrpVars()
    }

    fun hasDealers(): Boolean {
        return vars.containsDealerVars()
    }

    fun stripSeriesVarsFromModelImpliesSeriesYears(): Exp {
        if (!isOr) return this

        var modelNegLitCount = 0
        var seriesYearAndCount = 0
        var otherCount = 0

        for (arg in argIt) {
            if (arg.isModelNegLit) {
                modelNegLitCount++
            } else if (arg.isSeriesYearAnd) {
                seriesYearAndCount++
            } else {
                otherCount++
            }
        }

        val isModelImpliesSeriesYears = modelNegLitCount == 1 && seriesYearAndCount > 0 && otherCount == 0

        if (!isModelImpliesSeriesYears) {
            return this
        }

        //        System.err.println("Before: " + this);

        val b = ArgBuilder(_space, Op.Or)
        for (arg in argIt) {
            if (arg.isModelNegLit) {
                //leave as is
                b.addExp(arg)
            } else if (arg.isSeriesYearAnd) {
                for (lit in arg.argIt) {
                    if (lit.isYearPosLit) {
                        b.addExp(lit)
                    } else {
                        assert(lit.isSeriesPosLit)
                    }
                }

            } else {
                throw IllegalStateException()
            }
        }

        val after = b.mk()

        //        System.err.println("After: " + after);
        //        System.err.println();

        return after

    }

    fun stripSeriesYearsFromAcyImpliesModelYears(): Exp {
        if (!isOr) return this

        var acyNegLitCount = 0
        var seriesYearAndCount = 0
        var modelYearAndCount = 0
        var otherCount = 0
        for (arg in argIt) {
            if (arg.isAcyNegLit) {
                acyNegLitCount++
            } else if (arg.isSeriesYearAnd) {
                seriesYearAndCount++
            } else if (arg.isModelYearAnd) {
                modelYearAndCount++
            } else {
                otherCount++
            }
        }

        val isModelImpliesSeriesYears = acyNegLitCount == 1 && seriesYearAndCount > 0 && modelYearAndCount > 0 && otherCount == 0

        if (!isModelImpliesSeriesYears) {
            return this
        }

        System.err.println("Before: " + this)

        val b = ArgBuilder(_space, Op.Or)
        for (arg in argIt) {
            if (arg.isAcyNegLit) {
                //leave as is
                b.addExp(arg)
            } else if (arg.isSeriesYearAnd) {
                //kill it
            } else if (arg.isModelYearAnd) {
                //keep it
                b.addExp(arg)
            } else {
                throw IllegalStateException()
            }
        }

        val after = b.mk()

        System.err.println("After: $after")
        System.err.println()

        return after

    }

    fun stripSeriesVarsFromYsmAnds(): Exp {
        if (isConstant) return this
        if (isLit) return this

        if (isNot) {
            return _space.mkNot(arg.stripSeriesVarsFromYsmAnds())
        }

        var y = false
        var s = false
        var m = false

        for (arg in argIt) {
            if (arg.isYearPosLit) y = true
            if (arg.isSeriesPosLit) s = true
            if (arg.isModelPosLit) m = true
        }

        val ysm = y && s && m

        val op = this.op
        val b = ArgBuilder(_space, op)
        for (arg in argIt) {

            if (arg.isSeriesPosLit && ysm) {
                //skip
            } else {
                b.addExp(arg.stripSeriesVarsFromYsmAnds())
            }


        }
        return b.mk()

    }

    private fun computePd(): Int {
        if (isRoot) {
            return 1
        } else {
            val parents = parents
            var cpdSum = 0
            for (m in parents!!) {
                cpdSum += computeCpd(m, this)
            }
            return cpdSum
        }
    }

    fun computeValueAfterAsserting(lit: Lit): Int {
        return lit.pd
    }

    fun computeValueAfterRetraction(lit: Lit): Int {
        return lit.pd + lit.flip.pd
    }

    fun computeValueAfterFlip(lit: Lit): Int {
        return computeValue() - lit.pd + lit.flip.pd
    }

    fun computeValueAfterRadioFlip(var1: Var, var2: Var): Int {
        assert(var1 !== var2)
        val lit1 = var1.mkPosLit()
        val lit2 = var2.mkPosLit()
        return computeValue() - lit1.pd + lit2.pd
    }

    fun initParentsForArgs() {
        if (isConstant) return
        if (isLit) return
        assert(isDAnd || isDOr || isDcOr)
        for (arg in args) {
            arg._addParent(this)
            arg.initParentsForArgs()
        }
    }

    private fun _addParent(parent: Exp) {
        if (parents == null) {
            parents = HashSet()
        }
        parents!!.add(parent)
    }

    fun clearPics() {
        _space.clearPics()
        clearPartialDerivatives()
    }

    fun clearPartialDerivatives() {
        _pd = null
        if (isLeaf) {
            return
        }
        for (arg in args) {
            arg.clearPartialDerivatives()
        }
    }

    fun ef(): ExpFactory {
        return _space.getExpFactory()
    }


    companion object {

        fun getFirstAnd(args: Iterable<Exp>): Exp? {
            for (e in args) {
                if (e.isAnd) return e
            }
            return null
        }


        fun getFirstAnd(args: Array<Exp>): Exp? {
            for (e in args) {
                if (e.isAnd) return e
            }
            return null
        }

        fun findSubsumedVVs(vvp: Exp, vvs: Collection<Exp>): List<Exp> {
            val subsumedVVs = ArrayList<Exp>()
            for (vv in vvs) {
                if (vvp.vvpSubsumesVV(vv)) {
                    subsumedVVs.add(vv)
                }
            }
            return subsumedVVs

        }

        fun isFlip(arg1: Exp, arg2: Exp): Boolean {
            val pos: Exp
            val neg: Exp
            if (arg1.isPos && arg2.isNeg) {
                pos = arg1
                neg = arg2
            } else if (arg1.isNeg && arg2.isPos) {
                pos = arg2
                neg = arg1
            } else {
                assert(arg1.sign() == arg2.sign())
                return false
            }

            if (!pos.hasFlip) {
                return false
            }

            return if (pos.isTrue) {
                neg.isFalse
            } else if (pos.isLit) {
                neg.isLit && pos.asLit.sameVarCode(neg.asLit)
            } else if (pos.isComplex) {
                neg.isNot && neg.asNot().pos === pos
            } else {
                throw IllegalStateException()
            }
        }

        fun getOp(code: String): PosOp? {
            val codeUpperCase = code.toUpperCase()
            val values = PosOp.values()
            for (value in values) {
                val token = value.name
                if (token == codeUpperCase) {
                    return value
                }
            }
            return null
        }

        fun isComplexOpToken(code: String): Boolean {
            val op = getOp(code)
            return op != null && op.isComplex
        }

        fun isOpToken(code: String): Boolean {
            return getOp(code) != null
        }


        fun assertFixedArgs(fixedArgs: Array<Exp>): Boolean {
            val r = checkFixedArgs(fixedArgs)
            if (r is ArgsBad) {
                throw AssertionError(r.msg)
            } else {
                return true
            }
        }

        /**
         * Checks:
         *      must be sorted by expId
         *      should have no dups
         *      min length 2
         *      must contain no constants
         *      applies to and,or,xor
         */
        @JvmStatic
        fun checkFixedArgs(fixedArgs: Array<Exp>): ArgsResult {
            if (fixedArgs.size < 2) {
                return TooFewArgs(fixedArgs.size)
            }
            var ii = 0
            var i = 0
            var a1: Exp? = null
            while (ii < fixedArgs.size) {
                val a2 = fixedArgs[i]
                if (a2 is Constant) {
                    return ConstArg(i)
                }
                if (a1 != null) {
                    val expId1 = a1.expId
                    val expId2 = a2.expId
                    if (expId1 == expId2) {
                        return DupArgs(i, ii, a1.expId)
                    } else if (expId1 > expId2) {
                        return BadSort(i, ii, a1.expId, a2.expId)
                    }
                }
                a1 = a2
                ii++
                i++
            }
            return ArgsOk()
        }

        val COMPARATOR_BY_SORT_KEY: Comparator<Exp> = Comparator { e1, e2 ->
            if (e1 == null && e2 == null) {
                return@Comparator 0
            }

            if (e1 == null && e2 != null) {
                return@Comparator -1
            }

            if (e1 != null && e2 == null) {
                return@Comparator 1
            }


            val p1 = e1!!.pos
            val p2 = e2!!.pos

            val m1 = p1.macroType
            val m2 = p2.macroType
            val m = m2.compareTo(m2)
            if (m != 0) return@Comparator m


            val c1 = p1.argCount()
            val c2 = p2.argCount()
            val c = c1.compareTo(c2)
            if (c != 0) return@Comparator c


            val t1 = p1.token
            val t2 = p2.token
            val t = t1.compareTo(t2)
            if (t != 0) return@Comparator t

            val s1 = if (e1.sign()) 1 else 0
            val s2 = if (e2.sign()) 1 else 0

            s1.compareTo(s2)
        }

        /**
         * xors and iffs first
         *
         *
         * within that
         * varCount
         */
        val COMPARATOR_F: Comparator<Exp> = Comparator { e1, e2 ->
            if (e1 == null && e2 == null) {
                return@Comparator 0
            }

            if (e1 == null && e2 != null) {
                return@Comparator -1
            }

            if (e1 != null && e2 == null) {
                return@Comparator 1
            }


            val p1 = e1!!.pos
            val p2 = e2!!.pos

            val m1 = p1.macroType
            val m2 = p2.macroType
            val m = m2.compareTo(m2)
            if (m != 0) return@Comparator m


            val c1 = p1.argCount()
            val c2 = p2.argCount()
            val c = c1.compareTo(c2)
            if (c != 0) return@Comparator c


            val t1 = p1.token
            val t2 = p2.token
            val t = t1.compareTo(t2)
            if (t != 0) return@Comparator t

            val s1 = if (e1.sign()) 1 else 0
            val s2 = if (e2.sign()) 1 else 0

            s1.compareTo(s2)
        }


        val COMPARATOR_BY_ARITY: Comparator<Exp> = Comparator { e1, e2 ->
            kotlin.checkNotNull(e1)
            kotlin.checkNotNull(e2)
            val argCount1 = e1.argCount
            val argCount2 = e2.argCount
            argCount1.compareTo(argCount2)
        }

        val COMPARATOR_FOR_FORMULA: Comparator<Exp> = Comparator { e1, e2 ->
            kotlin.checkNotNull(e1)
            kotlin.checkNotNull(e2)
            val argCount1 = e1.argCount
            val argCount2 = e2.argCount
            argCount1.compareTo(argCount2)
        }


        val COMPARATOR_BY_STR_LEN_DESC: Comparator<Exp> = Comparator { e1, e2 ->
            kotlin.checkNotNull(e1)
            kotlin.checkNotNull(e2)
            val o1 = e1.toString()
            val o2 = e2.toString()
            val i1 = o1.length
            val i2 = o2.length
            i2.compareTo(i1)
        }

        val COMPARATOR_BY_STR_LEN: Comparator<Exp> = Comparator { e1, e2 ->
            kotlin.checkNotNull(e1)
            kotlin.checkNotNull(e2)
            val o1 = e1.toString()
            val o2 = e2.toString()
            val i1 = o1.length
            val i2 = o2.length
            i1.compareTo(i2)
        }

        @JvmStatic
        val COMPARATOR_BY_EXP_ID: Comparator<Exp?> = Comparator { e1, e2 ->
            kotlin.checkNotNull(e1)
            kotlin.checkNotNull(e2)
            val expId1 = e1.expId
            val expId2 = e2.expId
            expId1.compareTo(expId2)
        }

        val COMPARATOR_BY_VAR_COUNT: Comparator<Exp> = Comparator { e1, e2 ->
            kotlin.checkNotNull(e1)
            kotlin.checkNotNull(e2)
            val expId1 = e1.vars.size
            val expId2 = e2.vars.size
            expId1.compareTo(expId2)
        }

        val COMPARATOR_BY_STR_LEN2: Comparator<String> = Comparator { o1, o2 ->
            kotlin.checkNotNull(o1)
            kotlin.checkNotNull(o2)
            val i1 = o1.length
            val i2 = o2.length
            i1.compareTo(i2)
        }

        fun anyConstants(constraint: Exp): Boolean {
            return anyConstants(constraint.argIt)
        }

        fun anyConstants(constraints: Iterable<Exp>): Boolean {
            for (constraint in constraints) {
                if (constraint.isOrContainsConstants) {
                    return true
                }
            }
            return false
        }

        fun isAllPosLits(args: Iterable<Exp>): Boolean {
            for (arg in args) {
                if (!arg.isPosLit) {
                    return false
                }
            }
            return true
        }

        fun isAllPosLits(args: Array<Exp>): Boolean {
            for (arg in args) {
                if (!arg.isPosLit) {
                    return false
                }
            }
            return true
        }

        fun getContentModel(args: Iterable<Exp>): String {
            var constantArgCount = 0
            var litArgCount = 0
            var complexArgCount = 0
            for (arg in args) {
                if (arg.isConstant) {
                    constantArgCount++
                } else if (arg.isLit) {
                    litArgCount++
                } else if (arg.isComplex) {
                    complexArgCount++
                } else {
                    throw IllegalStateException()
                }
            }
            val a = Ser()
            if (constantArgCount > 0) {
                a.append("C")
                a.append(constantArgCount)
            }
            if (litArgCount > 0) {
                a.append("L")
                a.append(litArgCount)
            }
            if (complexArgCount > 0) {
                a.append("X")
                a.append(complexArgCount)
            }
            return a.toString().trim { it <= ' ' }
        }

        fun isAllConstants(args: Iterable<Exp>): Boolean {
            var constantArgCount = 0
            for (arg in args) {
                if (!arg.isConstant) {
                    return false
                } else {
                    constantArgCount++
                }
            }
            return constantArgCount > 0
        }

        fun isAllLits(args: Iterable<Exp>): Boolean {
            for (arg in args) {
                if (!arg.isLit) {
                    return false
                }
            }
            return true
        }

        fun isAllLits(args: Array<out Exp>): Boolean {
            for (arg in args) {
                if (!arg.isLit) {
                    return false
                }
            }
            return true
        }

        fun isAllComplex(args: Iterable<Exp>): Boolean {
            var complexArgCount = 0
            for (arg in args) {
                if (!arg.isComplex) {
                    return false
                } else {
                    complexArgCount++
                }
            }
            return complexArgCount > 0
        }

        fun isAllComplex(args: Array<Exp>): Boolean {
            var complexArgCount = 0
            for (arg in args) {
                if (!arg.isComplex) {
                    return false
                } else {
                    complexArgCount++
                }
            }
            return complexArgCount > 0
        }

        fun replaceSeriesWithModels(seriesYearAnd: Exp, seriesYearToModels: Multimap<SeriesYear, Var>): Exp {
            assert(seriesYearAnd.isSeriesYearAndPlus)

            val space = seriesYearAnd._space
            val seriesLit = seriesYearAnd.firstPosLitOfTypeSer
            val yearLit = seriesYearAnd.firstPosLitOfTypeYr

            val seriesVar = seriesLit!!.vr
            val yearVar = yearLit!!.vr


            //get everything else
            val otherArgs = ArrayList<Exp>()
            for (otherArg in seriesYearAnd.argIt) {
                if (otherArg === seriesLit) continue
                if (otherArg === yearLit) continue
                otherArgs.add(otherArg)
            }

            val seriesYear = SeriesYear(seriesVar, yearVar)
            val modelsCol = seriesYearToModels.get(seriesYear)
            val models = ArrayList(modelsCol)

            val bOr = ArgBuilder(space, Op.Or)
            for (model in models) {
                bOr.addExp(model.mkPosLit())
            }
            val modelsOr = bOr.mk()
            if (otherArgs.isEmpty()) {
                return space.mkAnd(yearLit, modelsOr)
            } else {

                val bAnd = ArgBuilder(space, Op.And)
                for (otherArg in otherArgs) {
                    bAnd.addExp(otherArg)


                }

                bAnd.addExp(yearLit)
                bAnd.addExp(modelsOr)
                bAnd.addExpIt(otherArgs)
                return bAnd.mk()
            }

        }

        fun twoToThePowerOf(power: Int): Int {
            return Math.pow(2.0, power.toDouble()).toInt()
        }

        fun twoToThePowerOfLong(power: Int): Long {
            return Math.pow(2.0, power.toDouble()).toLong()
        }

        fun computeDcPermCount(dcCount: Int): Int {
            return twoToThePowerOf(dcCount)
            //        return com.google.common.primitives.Ints.checkedCast(permCountLong);
        }

        fun computeDcPermCountLong(dcCount: Int): Long {
            return twoToThePowerOfLong(dcCount)
            //        return com.google.common.primitives.Ints.checkedCast(permCountLong);
        }

        fun compileDnnf(clob: String): Exp {
            return Csp.compileDnnf(clob)
        }

        fun parseTinyDnnf(tinyDnnfClob: String): Exp {
            return Parser.parseTinyDnnf(tinyDnnfClob)
        }

        fun serializeModels(cubes: Iterable<Cube>, a: Ser) {
            for (cube in cubes) {
                cube.serialize(a)
            }
        }

        fun printCubes(cubes: Iterable<Cube>) {
            assert(cubes != null)
            val a = Ser()


            for (cube in cubes) {
                a.ap(cube.serialize(10))
                a.newLine()
            }
            System.err.println(a)
        }

        fun printCubesTrueVars(cubes: Iterable<Cube>) {
            System.err.println("----")
            for (cube in cubes) {
                System.err.println(cube.serializeTrueVars())
            }
            System.err.println("====")
        }

        fun printCubes(cubes: Iterable<Cube>, trueVarsOnly: Boolean) {
            System.err.println("----")
            for (cube in cubes) {
                val line: String
                if (trueVarsOnly) {
                    line = cube.serializeTrueVars()
                    //                VarSet trueVars = cube.getTrueVars();
                    //                line = ParseUtil.serializeCodes(trueVars);
                } else {
                    line = cube.serialize()
                }
                System.err.println(line)
            }
            System.err.println("====")
        }


        fun printModels(cubes: Iterable<Cube>) {
            printCubesTrueVars(cubes)
        }

        fun serializeArgsSorted(a: Ser, args: Array<Exp>) {
            val copy = ExpComparator.sortCopyArray(args)
            for (i in copy.indices) {
                val arg = copy[i]
                arg.serialize(a)
                if (i != copy.size - 1) {
                    a.argSep()
                }
            }
        }


        fun serializeArgsUnsorted(a: Ser, args: Array<Exp>) {
            for (i in args.indices) {
                val arg = args[i]
                arg.serialize(a)
                if (i != args.size - 1) {
                    a.argSep()
                }
            }
        }

        fun serializeArgs(a: Ser, args: Iterable<Exp>) {
            val copy = ExpComparator.sortCopyIt(args)
            for (i in copy.indices) {
                val arg = copy[i]
                arg.serialize(a)
                if (i != copy.size - 1) {
                    a.argSep()
                }
            }
        }

        fun serializeArgList(a: Ser, args: Iterable<Exp>) {
            a.append(LPAREN)
            serializeArgs(a, args)
            a.append(RPAREN)
        }

        fun copyArgsExpToOtherSpace(destSpace: Space, op: Op, args: Iterable<Exp>): Exp {
            val b = ArgBuilder(destSpace, op)
            for (arg in args) {
                val copy = arg.copyToOtherSpace(destSpace)
                b.addExp(copy)
            }
            return b.mk()
        }

        fun copyVarSetToOtherSpace(destSpace: Space, varSet: VarSet): VarSet {
            if (varSet.space === destSpace) {
                for (vr in varSet) {
                    assert(vr.space === destSpace)
                }
                return varSet
            }

            val destVarSet = destSpace.newMutableVarSet()
            for (vr in varSet) {
                val varCode = vr.varCode
                val destVar = destSpace.getVar(varCode)
                destVarSet.addVar(destVar)
            }

            return destVarSet
        }

        private val ROUGH_MIN_MODELS = true

        fun sort(constraints: List<Exp>) {
            Collections.sort(constraints, Exp.COMPARATOR_BY_ARITY)
        }


        private fun computeCpd(m: Exp, n: Exp): Int {
            if (m.isDOr) {
                return m.pd
            } else if (m.isAnd) {
                var vProd = 1
                for (k in m.args) {
                    if (k !== n) {
                        vProd *= k.computeValue()
                    }
                }
                return m.pd * vProd
            } else {
                throw IllegalStateException(m.toString() + "")
            }
        }


        fun extractComplex(args: Iterable<Exp>): Iterable<Exp> {
            val aa = ArrayList<Exp>()
            for (a in args) {
                if (a.isComplex) {
                    aa.add(a)
                }
            }
            return aa
        }

        fun extractSimple(args: Iterable<Exp>): Iterable<Lit> {
            val aa = ArrayList<Lit>()
            for (a in args) {
                if (a.isSimple) {
                    aa.add(a.asLit)
                }
            }
            return aa
        }

        fun extractCube(space: Space, args: Iterable<Exp>): DynCube {
            return DynCube.create(space, args)
        }


        fun fixArgs(args: Array<Exp?>): Array<Exp> {
            kotlin.checkNotNull(args)
            Arrays.sort(args, COMPARATOR_BY_EXP_ID)

            if (Space.config.log.complexDups) {
                val dupCount = Exp.countDups(args)
                System.err.println("dupCount[" + dupCount + "/" + args.size + "]")
            }

            //any null array element to be removed
            //any dup array element to be set to null

            var lenOfFinalArray = args.size
            for (i in args.indices) {
                if (args[i] == null) {
                    lenOfFinalArray--
                } else if (i > 0 && args[i] === args[i - 1]) {
                    args[i] = null
                    lenOfFinalArray--
                } else if (i > 1 && args[i] === args[i - 2]) {
                    args[i] = null
                    lenOfFinalArray--
                } else if (i > 2 && args[i] === args[i - 3]) {
                    args[i] = null
                    lenOfFinalArray--
                } else if (i > 3 && args[i] === args[i - 4]) {
                    args[i] = null
                    lenOfFinalArray--
                } else if (i > 4 && args[i] === args[i - 5]) {
                    args[i] = null
                    lenOfFinalArray--
                } else if (i > 5 && args[i] === args[i - 6]) {
                    args[i] = null
                    lenOfFinalArray--
                }
            }

            if (lenOfFinalArray == args.size) {
                return args.requireNoNulls()
            } else {
                val a = arrayOfNulls<Exp>(lenOfFinalArray)
                var i = 0
                for (arg in args) {
                    if (arg == null) continue
                    a[i] = arg
                    i++
                }
                return a.requireNoNulls()
            }
        }

        @JvmStatic
        val selector: (Exp) -> Int = { it.expId }


        @JvmStatic
        fun Exp.conditionThat(that: Exp): Exp {
            return when (this) {
                is Lit -> that.condition(this)
                is Cube -> that.condition(this)
                else -> throw IllegalStateException()
            }

        }

        @JvmStatic
        fun countDups(sorted: Array<Exp?>): Int {
            var dupCount = 0
            for (i in sorted.indices) {
                if (i > 0 && sorted[i] === sorted[i - 1]) {
                    dupCount++
                }
            }
            return dupCount
        }
    }

}


sealed class ArgsResult(val ok: Boolean) {
    private val msg1 get() = if (ok) "ArgsOk" else "ArgsNoGood"

    open protected val msg2: String? get() = null

    val msg: String
        get() {
            val m2 = msg2
            val prefix = if (m2 == null) "" else ": "
            return "${this.msg1} $prefix $m2"

        }
}

class ArgsOk : ArgsResult(true)

sealed class ArgsBad() : ArgsResult(false)

data class TooFewArgs(val argCount: Int) : ArgsBad() {
    override val msg2 = "Must have at least 2 args: argCount: $argCount."
}

data class DupArgs(val i1: Int, val i2: Int, val expId: Int) : ArgsBad() {
    override val msg2 = "Duplicate args not allowed. index1: $i1 index2: $i2 expId:$expId"
}

data class BadSort(val i1: Int, val i2: Int, val expId1: Int, val expId2: Int) : ArgsBad() {
    override val msg2 = "Incorrect sort. index1: $i1 expId1:$expId1   index2: $i2 expId2:$expId2"
}

data class ConstArg(val i: Int) : ArgsBad() {
    override val msg2 = "Constant args are not allowed. index: $i"
}


//fun And.condition(): ArgBuilder {
//    val b = argBuilder(op())
//            .addExpArray(args, condition = condition)
//    return b.mk();
//}

//fun DynCube.litIterator() Iterator<Lit> {
//    return LitIterator(varIterator(), this)
//}
//
//


