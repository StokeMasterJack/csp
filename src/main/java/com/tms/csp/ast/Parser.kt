package com.tms.csp.ast

import com.google.common.collect.ImmutableList
import com.google.common.collect.ImmutableSet
import com.tms.csp.Vars
import com.tms.csp.fm.dnnf.products.Cube
import com.tms.csp.parse.*
import com.tms.csp.util.DynComplex

private val posComplexOpMap = PLConstants.PosOp.getPosComplexOpMap()

class Parser(val space: Space) {

    public val parseCounter = ParseCounter()


    private fun maybeAddVarsLines(lines: Sequence<String?>): Sequence<String?> {
        val firstLine = lines.first()
        return if (Vars.isVarsLine(firstLine)) {
            val varsLine: String? = lines.take(1).single()
            val varCodes: Iterable<String> = Vars.parseVarsLine(varsLine)
            space.mkVars(varCodes)
            lines.drop(1)
        } else {
            lines
        }
    }


    private fun parseExp(tokens: TokenStream): Exp {
        val headToken = tokens.take()
        check(headToken.isHead)
        return mkExp(headToken, tokens)
    }

    private fun mkExp(headToken: Token, tokens: TokenStream): Exp {
        if (headToken.isConstantTrue) {
            return space.mkConstant(true)
        } else if (headToken.isConstantFalse) {
            return space.mkConstant(false)
        } else if (headToken.isNot) {
            val arg = parseExp(tokens)
            return arg.flip()
        } else if (headToken.isPosComplex) {
            val t = headToken.toString()
            val op = posComplexOpMap[t.toLowerCase()]
            val args = parseExpList(tokens)
            try {
                checkNotNull(op)
                val expFactory = space.getExpFactory()
                return expFactory.mkPosComplex(op, args)
            } catch (e: Exception) {
                val s = headToken.toString()
                throw RuntimeException("Problem creating new complex expression. PosOp[$s]  args$args", e)
            }

        } else {
            //must be vr
            val posLit = headToken.getPosLit(space)
            //            headToken.


            val prefix = posLit.prefix
            parseCounter.countPrefix(prefix)
            return posLit
        }
    }


    private fun parseExpList(tokens: TokenStream): List<Exp> {
        val retVal = mutableListOf<Exp>()
        tokens.consumeAndCheck(PLConstants.LPAREN)
        while (true) {
            val next = tokens.peek()
            if (next == null) {
                check(retVal.size > 0)
                return retVal
            } else if (next.isRParen) {
                tokens.consumeAndCheck(PLConstants.RPAREN)
                break
            } else {
                checkNotNull(tokens)
                check(!tokens.isEmpty)
                val exp = parseExp(tokens)
                checkNotNull(exp)
                retVal.add(exp)
            }
        }
        return retVal
    }


//    fun parseLines(clob: String): Sequence<String?> {
//        val lines: Sequence<String?> = clob.trim().lineSequence()
//        return maybeAddVarsLines(lines)
//    }


    fun parseTinyDnnf(clob: String): Exp {
        val lines = parseLines(clob.trim())

        var lastExp: Exp? = null

        for (line in lines) {
            println("L: [$line]")
            if (line == null) {
                continue
            }
            if (line.isBlank()) {
                continue
            }

            val nodeCount1 = space._nodes.size
            val e: Exp = mkDNode(line)
            val isNew = e.isNew
            val nodeCount2 = space._nodes.size

            assert(e.isDOr || e.isDAnd || e.isLit) {
                e.toString()
            }

            if (isNew) {
                assert(nodeCount1 != nodeCount2) { line + ": " + e.serialize() }
            } else {
                assert(nodeCount1 == nodeCount2) { line + ": " + e.serialize() }
            }

            lastExp = e
        }

        return lastExp!!


    }

    private fun mkDNode(expText: String): Exp {
        assert(!expText.isBlank()) { expText }
        assert(!Parser.isComment(expText)) { expText }
        assert(expText.trim() == expText)

        val a: List<String> = expText.split(" ")
        val sOp = a[0]
        if (sOp == "L") {
            val sLit = a[1].trim()
            val lit = Integer.parseInt(sLit)
            return space.mkLit(lit)
        } else {
            val op = when (sOp) {
                "A" -> Op.DAnd
                "O" -> Op.DOr
                else -> throw IllegalStateException("Bad node type[$sOp] for line[$expText]")
            }
            assert(op.isDOr || op.isDAnd)
            val args = mutableListOf<Exp>()
            var isAllLits = true
            for (i in 1 until a.size) {
                val sExpId = a[i]
                val expId = Integer.parseInt(sExpId)
                try {
                    val arg: Exp = space.getExp(expId)
                    if (!arg.isLit) {
                        isAllLits = false
                    }
                    args.add(arg)
                } catch (e: IndexOutOfBoundsException) {
                    val msg = "Problem parsing line[$expText]:  expId[$expId] does not exist yet."
                    Space.log.severe(msg);
                    throw RuntimeException(msg)
                }
            }

            return mkDLineExp(op, args, isAllLits);
        }


    }

    private fun isDcOr(op: Op, args: List<Exp>): DcOr? {
        return if (args.size == 2 && op.isDOr) {
            val a1 = args[0]
            val a2 = args[1]
            if (a1.isLit && a2.isLit && a1.vr == a2.vr && a1.sign() != a2.sign()) {
                a1.vr.mkDcOr()
            } else {
                null
            }

        } else {
            null
        }
    }

    private fun mkDLineExp(op: Op, args: List<Exp>, isAllLits: Boolean): Exp {
        val dcOr: DcOr? = isDcOr(op, args)
        return if (dcOr != null) {
            dcOr
        } else if (op.isDAnd && isAllLits) {
            space.argBuilder(Op.Cube).addExpIt(args).mk()
        } else if (op.isDAnd && !isAllLits) {
            space.argBuilder(Op.DAnd).addExpIt(args).mk()
        } else if (op.isDOr) {
            space.argBuilder(Op.DOr).addExpIt(args).mk()
        } else {
            throw IllegalStateException()
        }


    }

//    fun parseExp(expText: String): Exp {
//        return parseExp(expText)
//    }


    fun parseExp(expText: String): Exp {
        val exp = parseExpOrNull(expText, false)
        checkNotNull(exp)
        return exp
    }

    fun parseExpOrNull(expText: String?): Exp? {
        return parseExpOrNull(expText, false)
    }

    fun parseLines(lines: Sequence<String?>): Sequence<Exp> {
        return lines.map { parseExpOrNull(it) }.filterNotNull()
    }

    fun parseExpOrNull(expText: String?, cubes: Boolean): Exp? {
        return if (expText == null) {
            null
        } else {
            val t = expText.trim()
            if (t.isBlank() || t.startsWith("#")) {
                null
            } else {
                val tt = when {
                    t.startsWith("include(") -> t.replace("include(", "imp(")
                    t.startsWith("conflict(") -> t.replace("conflict(", "nand(")
                    else -> t
                }
                val tokenStream = GlobalTokenizer.INSTANCE.tokenize(tt)
                try {
                    parseExp(tokenStream)
                } catch (e: Exception) {
                    throw RuntimeException("Problem parsing exp[$expText]", e)
                }
            }
        }
    }


    fun mkCubeSeq(sCubes: String): Sequence<Cube> {
        return sCubes.lineSequence().map { it.trim() }.map { parseCube(it) }
    }

    fun mkCubeSet(sCubes: String) = mkCubeSeq(sCubes).toSet()


//    fun parsePL(clob: String, cubes: Boolean): Sequence<Exp> {
//        return parser.parsePL(clob, cubes)
//    }


    fun parsePL(clob: String): Sequence<Exp> {
        val lines1: Sequence<String?> = parseLines(clob)
        return parsePL(lines1)
    }

    fun parsePL(lines1: Sequence<String?>): Sequence<Exp> {
        val lines2: Sequence<String?> = maybeAddVarsLines(lines1)
        return lines2.map { parseExpOrNull(it) }.filterNotNull()
    }


    fun mkComplex(): DynComplex {
        return DynComplex(space)
    }

    fun mkSimple(): DynCube {
        return DynCube(space)
    }

    fun prefixEachLine(prefix: String, inout: String): String {
        val lines = parseLines1(inout)
        val out = prefixEachLine(prefix, lines)
        val a = Ser()
        for (line in out) {
            a.ap(line)
            a.newLine()
        }
        return a.toString().trim { it <= ' ' }
    }

    fun prefixEachLine(prefix: String, lines: Iterable<String>): List<String> {
        val b = ImmutableList.builder<String>()
        for (line in lines) {
            b.add(prefix + line)
        }
        return b.build()
    }

//    fun extractVarCodes(fact: String): Set<String> {
//        val exp: Exp = parseExp(fact)
//        val vars = exp.vars
//        return vars.toVarCodeSet()
//    }

//    fun extractVars(fact: String): Space {
//        val vars = extractVarCodes(fact)
//        return Space(vars)
//    }


    fun parseExpressions1(vararg expressions: String): Array<Exp> {
        val a = arrayOfNulls<Exp>(expressions.size)
        for (i in expressions.indices) {
            a[i] = parseExp(expressions[i])
        }
        return a.requireNoNulls();
    }

    fun parseExpressions2(expressions: Iterable<String>): List<Exp> {
        val b = ImmutableList.builder<Exp>()
        for (expText in expressions) {
            val exp = parseExp(expText)
            b.add(exp)
        }
        return b.build()
    }


    fun mkLitSet(sLits: String?): Set<Lit> {
        return if (sLits.isNullOrBlank()) {
            emptySet()
        } else {
            val lits = Space.MY_SPLITTER.split(sLits.trim().run {
                if (this.startsWith("and")) {
                    //cube
                    this.replace("and(", "").replace(")", "")
                } else {
                    this
                }
            })

            val b = ImmutableSet.builder<Lit>()

            for (lit in lits) {
                val code = Head.getVarCode(lit)
                val sign = Head.getSign(lit)
                val dVar = space.getVar(code) ?: throw IllegalStateException("Bad Var[$code]")
                val dLit = dVar.mkLit(sign)
                b.add(dLit)
            }
            return b.build()

        }


    }

    fun mkLitSet(vars: Set<Var>?): Set<Lit> {
        if (vars == null || vars.size == 0) return ImmutableSet.of()
        val b = ImmutableSet.builder<Lit>()
        for (`var` in vars) {
            val dLit = `var`.mkPosLit()
            b.add(dLit)
        }
        return b.build()
    }

    fun parseCubes(expText: String): Set<Cube> {
        return mkCubeSet(expText)
    }

    fun parseDynCube(sLits: String): DynCube {
        val litSet = mkLitSet(sLits)
        return DynCube(space, litSet)
    }


    fun parseCube(sLits: String): Cube {
        val litSet = mkLitSet(sLits)
        return DynCube(space, litSet)
    }

    fun parseLits(sLits: String): Exp {

        val exp = parseExp("and($sLits)")

        assert(exp.isCube() || exp.isLit()) { sLits }
        return exp
    }

    data class Raw(val varsLine: String, val strSeq: Sequence<String>) {
        val varCodes: Iterable<String> by lazy { Vars.parseVarsLine(varsLine) }
        val space: Space by lazy { Space(varCodes) }

        val expSeq: Sequence<Exp> by lazy {
            strSeq.map { space.parseExp(it) }
        }

        val csp: Csp by lazy {
            val add = Add(expSeq, space)
            add.mkCsp()
        }

    }

    companion object {


        fun isComment(expLine: String): Boolean = expLine.trim().startsWith("#")

        @JvmStatic
        fun parseCsp(clob: String, tiny: Boolean = false): Csp {
            val raw: Raw = parsePL(clob, tiny)
            return raw.csp
        }


        @JvmStatic
        fun parsePL(clob: String, tiny: Boolean): Raw {
            val lines = parseLines(clob)
            val firstLine = lines.first()
            return if (Vars.isVarsLine(firstLine)) {
                val varsLine: String = lines.take(1).single()
                val constraints = lines.drop(1)
                Raw(varsLine, constraints)
            } else {
                val varsLine: String = extractVarCodesLine(lines, tiny)
                val constraints = lines
                Raw(varsLine, constraints)
            }
        }


        @JvmStatic
        fun parseLines(clob: String): Sequence<String> = clob.lineSequence().map { preProcessLine(it) }.filterNotNull()

        fun preProcessLine(line: String?): String? {
            if (line.isNullOrBlank()) {
                return null;
            } else if (isComment(line)) {
                return null;
            } else {
                return line.trim();
            }
        }


        fun parseLines1(clob: String): List<String> = parseLines(clob).toList();

        @JvmStatic
        @JvmOverloads
        fun extractVarCodes(constraints: Sequence<String>, tiny: Boolean = false): Set<String> {
            val space0 = Space()
            val parser = space0.parser;

            return constraints
                    .map { parser.parseExpOrNull(it) }
                    .filterNotNull()
                    .fold(emptySet()) { acc, e -> acc.plus(e.vars.map { it.getVarCode(tiny) }) }
        }

        @JvmStatic
        @JvmOverloads
        fun extractVarCodesLine(constraints: Sequence<String>, tiny: Boolean = false): String {
            val codes = extractVarCodes(constraints, tiny)
            val a = Ser()
            a.ap(Vars.HEAD_VARS_LINE)
            ParseUtil.serializeVarCodes(a, codes);
            a.ap(Vars.FOOT)
            return a.toString()
        }


    }


}