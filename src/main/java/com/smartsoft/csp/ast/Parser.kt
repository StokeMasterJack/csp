package com.smartsoft.csp.ast

import com.google.common.collect.ImmutableList
import com.google.common.collect.ImmutableSet
import com.smartsoft.csp.Vars
import com.smartsoft.csp.dnnf.products.Cube
import com.smartsoft.csp.parse.*
import com.smartsoft.csp.util.DynComplex

private val posComplexOpMap = PLConstants.PosOp.getPosComplexOpMap()

class Parser(val space: Space) {

//    public val parseCounter = ParseCounter()


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


    private fun parseExp(tokens: TokenStream, flatten: Boolean = true): Exp {
        val headToken = tokens.take()
        check(headToken!!.isHead)
        return mkExp(headToken, tokens, flatten)
    }

    private fun mkExp(headToken: Token, tokens: TokenStream, flatten: Boolean = true): Exp {
        when {
            headToken.isConstantTrue -> return space.mkConstant(true)
            headToken.isConstantFalse -> return space.mkConstant(false)
            headToken.isNot -> {
                val arg = parseExp(tokens)
                return arg.flip
            }
            headToken.isPosComplex -> {
                val t = headToken.toString()
                val op = posComplexOpMap[t.toLowerCase()]
                val args = parseExpList(tokens, flatten = flatten)
                try {
                    checkNotNull(op)
                    val expFactory = space.getExpFactory()
                    return expFactory.mkPosComplex(op, args, flatten = flatten)
                } catch (e: Exception) {
                    val s = headToken.toString()
                    throw RuntimeException("Problem creating new complex expression. PosOp[$s]  args$args", e)
                }

            }
            else -> {
                //must be vr
                val posLit = headToken.getPosLit(space)
                //            headToken.


                @Suppress("UNUSED_VARIABLE") val prefix = posLit.prefix
//                parseCounter.countPrefix(prefix)
                return posLit
            }
        }
    }


    private fun parseExpList(tokens: TokenStream, flatten: Boolean = true): List<Exp> {
        val retVal = mutableListOf<Exp>()
        tokens.consumeAndCheck(PLConstants.LPAREN)
        while (true) {
            val next = tokens.peek()
            @Suppress("SENSELESS_COMPARISON")
            if (next === null) {
                check(retVal.size > 0)
                return retVal
            } else if (next.isRParen) {
                tokens.consumeAndCheck(PLConstants.RPAREN)
                break
            } else {
                checkNotNull(tokens)
                check(!tokens.isEmpty)
                val exp = parseExp(tokens, flatten = flatten)
                checkNotNull(exp)
                retVal.add(exp)
            }
        }
        return retVal
    }

    fun parseTinyDnnf(lines: Sequence<String>): Exp {

        var lastExp: Exp? = null

        for (line in lines) {
            if (line.isBlank()) {
                continue
            }

            val nodeCount1 = space._nodes.size
            val e: Exp = mkDNode(line)
            val isNew = e.isNew
            val nodeCount2 = space._nodes.size

            assert(e.isDOr || e.isDcOr || e.isDAnd || e.isLit) {
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


    @JvmOverloads
    fun parseExp(expText: String, flatten: Boolean = true): Exp {
        val exp = parseExpOrNull(expText, flatten = flatten)
        checkNotNull(exp)
        return exp
    }

    fun parseExpOrNull(expText: String?): Exp? {
        return parseExpOrNull(expText, false)
    }

    fun parseLines(lines: Sequence<String?>): Sequence<Exp> {
        return lines.map { parseExpOrNull(it) }.filterNotNull()
    }

    fun parseCleanLines(lines: Sequence<String>): Sequence<Exp> {
        return lines.map { parseExp(it) }.filterNotNull()
    }

    fun parseLines(lines: Iterable<String?>): Iterable<Exp> {
        return lines.map { parseExpOrNull(it) }.filterNotNull()
    }

    fun parseCleanLines(lines: Iterable<String>): Iterable<Exp> {
        return lines.map { parseExp(it) }
    }


    fun parseExpOrNull(expText: String?, flatten: Boolean = true): Exp? {
        return if (expText == null) {
            null
        } else {
            val t = expText.trim()
            if (t.isBlank() || t.startsWith("#")) {
                null
            } else if (Vars.isVarsLine(t)) {
                null
            } else if (Vars.isDcLine(t)) {
                val dcVarCodes = Vars.parseDcLine(t)
                val dcVars = dcVarCodes.map { space.mkVar(it) }
                val dcOrs = dcVars.map { it.mkDcOr() }
                space.mkPosComplex(PLConstants.PosOp.AND, dcOrs)
            } else {
                val tt = when {
                    t.startsWith("include(") -> t.replace("include(", "imp(")
                    t.startsWith("conflict(") -> t.replace("conflict(", "nand(")
                    else -> t
                }
                val tokenStream = GlobalTokenizer.INSTANCE.tokenize(tt)
                try {
                    parseExp(tokenStream, flatten = flatten)
                } catch (e: Exception) {
                    throw RuntimeException("Problem parsing exp[$expText]", e)
                }
            }
        }
    }


    fun mkCubeSeq(sCubes: String): Sequence<Cube> {
        return sCubes.lineSequence().map { it.trim() }.map { parseLitsToDynCube(it) }
    }

    fun mkCubeSet(sCubes: String) = mkCubeSeq(sCubes).toSet()


//    fun parsePL(clob: String, cubes: Boolean): Sequence<Exp> {
//        return parser.parsePL(clob, cubes)
//    }


    fun parsePL(clob: String): Sequence<Exp> {
        val lines1: Sequence<String?> = clobToLines(clob)
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
            val split: Iterable<String> = Space.MY_SPLITTER.split(sLits.trim().run {
                if (this.startsWith("and")) {
                    //cube
                    this.replace("and(", "").replace(")", "")
                } else {
                    this
                }
            })

            val b = ImmutableSet.builder<Lit>()

            for (sLit: String in split) {
                val lit = parseLit(sLit)
                b.add(lit)
            }
            return b.build()

        }


    }

    fun parseLit(sLit: String): Lit {
        val code = Head.getVarCode(sLit)
        val sign = Head.getSign(sLit)
        val dVar = space.getVar(code) ?: throw IllegalStateException("Bad Var[$code]")
        return dVar.mkLit(sign)
    }

    fun mkLitSet(vars: Set<Var>?): Set<Lit> {
        if (vars == null || vars.size == 0) return ImmutableSet.of()
        val b = ImmutableSet.builder<Lit>()
        for (vr in vars) {
            val dLit = vr.mkPosLit()
            b.add(dLit)
        }
        return b.build()
    }

    fun parseCubes(expText: String): Set<Cube> {
        return mkCubeSet(expText)
    }

    fun parseLitsToDynCube(sLits: String): DynCube {
        val litSet = mkLitSet(sLits)
        return DynCube(space, litSet)
    }


    fun parseLitsToConditionOn(sLits: String): ConditionOn {
        val tLits = sLits.trim()
        val ret: ConditionOn = if (tLits.contains(" ")) {
            //must be an sCube
            parseLitsToDynCube(sLits)
        } else {
            //single lit
            parseLit(sLits)
        }
        assert(ret is Cube || ret is Lit) { sLits }
        return ret
    }

    fun parseLitsToExp(sLits: String): Exp {
        val tLits = sLits.trim()
        val ret: Exp = if (tLits.contains(" ")) {
            //must be an sCube
            parseExp("and($sLits)")
        } else {
            //single lit
            parseLit(sLits)
        }
        assert(ret is Cube || ret is Lit) { sLits }
        return ret
    }

    data class Raw(val varsLine: String, val strSeq: Sequence<String>) {
        val varCodes: Iterable<String> by lazy { Vars.parseVarsLine(varsLine) }
        val space: Space by lazy { Space(varCodes) }

        val expSeq: Sequence<Exp> by lazy {
            strSeq.map { space.parseExp(it) }
        }

        val csp: Csp by lazy {
            Csp(space = space, constraints = expSeq)
        }

        val csp1: Csp by lazy {
            Csp(space = space, constraints = expSeq)
        }

        val dnnf: Exp by lazy {
            space.parser.parseTinyDnnf(strSeq)
        }


    }

    companion object {


        fun isComment(expLine: String): Boolean = expLine.trim().startsWith("#")

        @JvmStatic
        fun parseCsp(clob: String): Csp {
            val raw: Raw = clobToRaw(clob)
            return raw.csp
        }

        @JvmStatic
        fun parseCsp1(clob: String): Csp {
            val raw: Raw = clobToRaw(clob)
            return raw.csp1
        }

        @JvmStatic
        fun parseTinyDnnf(clob: String): Exp {
            val raw: Raw = clobToRaw(clob)
            return raw.dnnf
        }

        @JvmStatic
        fun clobToRaw(clob: String): Raw {
            val lines = clobToLines(clob)
            val firstLine = lines.first()
            return if (Vars.isVarsLine(firstLine)) {
                val varsLine: String = lines.take(1).single()
                val constraints = lines.drop(1)
                Raw(varsLine, constraints)
            } else {
                println("extracting var codes")
                val varsLine: String = extractVarCodesLine(lines)
                val constraints = lines
                Raw(varsLine, constraints)
            }
        }

        @JvmStatic
        fun extractVarCodes(clob: String): Set<String> {
            val lines = clobToLines(clob)
            return extractVarCodes(lines)
        }


        @JvmStatic
        fun compareVarsLineToExtract(clob: String) {
            val extract = extractVarCodes(clob)
            val lines = clobToLines(clob)
            val firstLine = lines.first()
            val varsLine = if (Vars.isVarsLine(firstLine)) {
                val varsLine: String = lines.take(1).single()
                Vars.parseVarsLine(varsLine).toSet()
            } else {
                emptySet<String>()
            }
            println("extract: ${extract.size}: ${extract}")
            println("varsLine:  ${varsLine.size}:   ${varsLine}")

            println("extract.minus(varsLine): " + extract.minus(varsLine))
            println("varsLine.minus(extract): " + varsLine.minus(extract))

            println("extract as a varsLine: ")
            println(extract.joinToString(" ", "vars(", postfix = ")"))

        }


        @JvmStatic
        fun clobToLines(clob: String): Sequence<String> = clob.lineSequence().map { preProcessLine(it) }.filterNotNull()

        fun preProcessLine(line: String?): String? {
            if (line.isNullOrBlank()) {
                return null;
            } else if (isComment(line)) {
                return null;
            } else {
                return line.trim();
            }
        }


        fun parseLines1(clob: String): List<String> = clobToLines(clob).toList();

        @JvmStatic
        fun extractVarCodes(constraints: Sequence<String>): Set<String> {
            val space0 = Space()
            val parser = space0.parser;
            val set = mutableSetOf<String>()
            val sLines = constraints.filterNot { Vars.isVarsLine(it) }
            sLines.forEach { parser.parseExpOrNull(it)?.collectVarCodes(set) }
            return set
        }

        @JvmStatic
        fun extractVarCodesLine(constraints: Sequence<String>): String {
            val codes = extractVarCodes(constraints)
            val a = Ser()
            a.ap(Vars.HEAD_VARS_LINE)
            ParseUtil.serializeVarCodes(a, codes);
            a.ap(Vars.FOOT)
            return a.toString()
        }


    }


}