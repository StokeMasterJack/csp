package com.tms.csp.ast


import com.google.common.collect.*
import com.tms.csp.It
import com.tms.csp.argBuilder.ArgBuilder
import com.tms.csp.common.CspStableState
import com.tms.csp.common.SeriesYear
import com.tms.csp.data.CspSample
import com.tms.csp.fm.dnnf.products.Cube
import com.tms.csp.parse.Head
import com.tms.csp.transforms.Transformer
import com.tms.csp.util.DynComplex
import com.tms.csp.util.SpaceJvm
import com.tms.csp.util.varSets.VarSet
import java.util.*
import java.util.logging.Logger


class CspOldJava(var k: Csp,
        //    public Formula getFormula() {
        //        propagate();
        //        assert isStable();
        //        assert !isFailed();
        //        assert !isSolved();
        //        assert complex != null;
        //        assert !complex.isEmpty();
        //        return complex.mkFormula().asFormula();
        //    }


                 var space: Space, var simple: DynCube?, var complex: DynComplex?, var dontCares: VarSet) : PLConstants {

    val largestNonXorNonColorConstraintByVarCount: Exp
        get() = throw UnsupportedOperationException()

    val largestConstraintVarCount: Int
        get() {
            val L = largestComplexConstraintBasedOnVarCount
            return if (L == null) {
                0
            } else {
                L.varCount
            }
        }

    val largestComplexConstraintBasedOnVarCount: Exp?
        get() {
            var largest: Exp? = null
            for (e in complex!!) {
                if (e.isXor) {
                    throw IllegalStateException()
                }
                if (largest == null || e.varCount > largest.varCount) {
                    largest = e
                }
            }


            if (largest != null && largest.isXor) {
                throw IllegalStateException()
            }

            return largest
        }


    val largestNonXorConstraint: Exp?
        get() {
            var largest: Exp? = null
            var largestStrLen = -1
            for (e in complex!!) {
                if (e.isXor) continue

                val strLen = e.toString().length
                if (strLen > largestStrLen) {
                    largest = e
                    largestStrLen = strLen
                }
            }
            return largest
        }


    val openCareVarCount: Int
        get() = k.getFormulaVars().size


    val isNnf: Boolean
        get() {
            if (complex!!.isEmpty) {
                return true
            }
            for (constraint in complex!!) {
                if (!constraint.isNnf) {
                    return false
                }
            }
            return true
        }

    val isBnf: Boolean
        get() {
            if (complex!!.isEmpty) {
                return true
            }
            for (constraint in complex!!) {
                if (!constraint.isBnf) {
                    return false
                }
            }
            return true
        }


    val isClean: Boolean
        get() = !k.isDirty


    val modelImpliesSeriesYear: ImmutableSet<Exp>
        get() {
            assert(isOpen)

            val b = ImmutableSet.builder<Exp>()
            for (exp in complex!!) {
                if (exp.isModelImpliesSeriesYear) {
                    b.add(exp)
                }
            }

            return b.build()
        }


    /**
     * careVarCount of exp with greatest care
     *
     * @return
     * @throws Exception
     */
    val maxCareVarCount: Int
        @Throws(Exception::class)
        get() {
            if (complex!!.isEmpty) return 0
            var maxCareVarCount = 0
            for (e in complex!!) {
                val careVarCount = e.varCount
                if (careVarCount > maxCareVarCount) {
                    maxCareVarCount = careVarCount
                }

            }
            return maxCareVarCount
        }

    val isReduced: Boolean
        get() {
            val rv = openCareVarCount == space.varCount
            if (rv) {
                assert(simple!!.isEmpty)
            }
            return rv
        }


    val largestXor: Xor?
        get() {
            if (complex!!.isEmpty) return null
            var largest: Xor? = null
            for (exp in complex!!) {
                if (exp.isXor) {
                    if (largest == null || exp.argCount > largest.argCount) {
                        largest = exp.asXor()
                    }
                }
            }
            return largest
        }

    val stableState: CspStableState
        get() {
            checkStable()
            return if (isFailed) {
                CspStableState.FAILED
            } else if (k.isSolved) {
                CspStableState.SOLVED
            } else {
                CspStableState.OPEN
            }
        }


    val isEmpty: Boolean
        get() = k.size == 0


    //    public Csp createEfcModLocal(final Mod mod) {
    //        final Csp blank = new Csp(space);
    //        this.forEachConstraint(new ConstraintHandler() {
    //            @Override
    //            public void onConstraint(Exp constraint) {
    //                if (mod.isLocal(constraint)) {
    //                    blank.addConstraint(constraint);
    //                }
    //            }
    //        });
    //        return blank;
    //    }


    val isFailed: Boolean
        get() = k.isFailed

    val trueVars: Set<String>
        get() = simple!!.trueVarCodes

    val falseVars: Set<String>
        get() = simple!!.falseVarCodes

    val assignedVars: Set<String>
        get() = if (simple!!.isEmpty) {
            ImmutableSortedSet.of()
        } else simple!!.varCodes


    val assignmentCount: Int
        get() = k.simpleConstraintCount

    //    public void serializeLocalCubes(Ser a) {
    //        Set<Map.Entry<Integer, Assignment>> entries = ass.map.entrySet();
    //        int size = entries.size();
    //        int i = 0;
    //        for (Map.Entry<Integer, Assignment> entry : entries) {
    //            Exp lit = entryToLit(entry);
    //            a.append(lit.toString());
    //            if (i != size - 1) {
    //                a.append(' ');
    //            }
    //            i++;
    //        }
    //    }


    val iffCount: Int
        get() = 0


    val giantOr: Exp?
        get() {
            var giant: Exp? = null
            for (arg in k.complexIt) {
                val currentArgCount = giant?.argCount ?: 0
                if (arg.isOr && arg.argCount > currentArgCount) {
                    giant = arg
                }
            }
            return giant
        }

    val bestXorSplit: Xor
        get() = XorCounts.getMax(k)

    val vvpsWithSeriesAndYear: List<Exp>
        get() {
            if (complex!!.isEmpty) return ImmutableList.of()
            val b = ImmutableList.builder<Exp>()
            for (exp in complex!!) {
                if (exp.isVVPlusWithSeriesAndYear) {
                    b.add(exp)
                }
            }
            return b.build()
        }

    val vvpsWithSeriesAndModel: List<Exp>
        get() {
            if (complex!!.isEmpty) return ImmutableList.of()
            val b = ImmutableList.builder<Exp>()
            for (exp in complex!!) {
                if (exp.isVVPlusWithSeriesAndModel) {
                    b.add(exp)
                }
            }
            return b.build()
        }


    val vvPlusConstraints: List<Exp>
        get() {
            if (complex!!.isEmpty) return ImmutableList.of()
            val b = ImmutableList.builder<Exp>()
            for (exp in complex!!) {
                if (exp != null && exp.isVVPlus) {
                    b.add(exp)
                }
            }
            return b.build()
        }


    val invAcyVars: VarSet
        get() {
            space.checkVarInfo()

            val varMeta = space.varMeta
            val formulaVars = k.formula!!.vars
            val b = space.newMutableVarSet()
            for (`var` in formulaVars.varIt()) {
                if (varMeta.isInvAcyVar(`var`)) {
                    b.addVar(`var`)
                }
            }
            return b.build()
        }


    val isOpen: Boolean
        get() {
            if (isFailed) {
                return false
            }

            return if (k.isSolved) {
                false
            } else true
        }

    val isConstant: Boolean
        get() = isFailed || k.isSolved


    val modelXorDeep: Exp?
        get() = getXorDeep(PLConstants.MDL_PREFIX)

    val varList: List<Var>
        get() = space.varList


    val complexSeq: List<Exp>
        get() = createMasterList()

    val allConstraints: List<Exp>
        get() {
            val b = ImmutableList.builder<Exp>()
            if (complex != null) {
                b.addAll(complex!!)
            }

            if (simple != null) {
                b.addAll(simple!!.litIt())
            }

            return b.build()

        }

    val allNonXorConstraints: Set<Exp>
        get() {
            val b = ImmutableSet.builder<Exp>()
            if (complex != null) {
                for (exp in complex!!) {
                    if (!exp.isXor) {
                        b.add(exp)
                    }
                }
            }
            if (simple != null) {
                b.addAll(simple!!.litIt())
            }
            return b.build()
        }

    val ops: Set<PLConstants.PosOp>
        get() {
            val s = HashSet<PLConstants.PosOp>()
            val allConstraints = complexSeq
            for (c in allConstraints) {
                val posOp = c.posOp
                s.add(posOp)
            }

            return s

        }


//    fun getLargestNonXorConstraint(): Exp {
//        var largest: Exp? = null;
//        var largestStrLen = -1;
//        for (e ff k.complexIt) {
//            if (e.isXor()) continue;
//
//            val strLen = e.toString().length;
//            if (strLen > largestStrLen) {
//                largest = e;
//                largestStrLen = strLen;
//            }
//        }
//        return largest!!;
//    }


    val isSatLame: Boolean
        get() {
            k.propagate()
            return if (isFailed) {
                false
            } else true
        }


    //    public Set<Var> getOpenVars() {
    //        k.get
    //        Sets.SetView<Var> diff = Sets.difference(space.getVarSet(), getAssignedVars());
    //        return ImmutableSortedSet.copyOf(diff);
    //    }

    private val varSet: Any
        get() = k.vars


    val maxAndSize: Int
        get() {
            val exp = k.getExpWithLargestAnd() ?: return 0
            val biggestAnd = exp.andWithHighestLitArgCount ?: return 0
            return biggestAnd.andLitArgCount
        }

    val alwaysTrueVars: Set<String>
        get() {
            val b = ImmutableSet.builder<String>()
            for (atVarCode in Space.alwaysTrueVars1) {
                if (k.space.containsVarCode(atVarCode)) {
                    b.add(atVarCode)
                }
            }
            return b.build()
        }

    /**
     * Combines Space.getCoreXorsFromSpace() with Csp.getXorConstraints
     */
    val allXorConstraints: Set<Exp>
        get() {
            val xors = HashSet<Exp>()
            val xors1 = space.coreXorsFromSpace
            val xors2 = k.getXorConstraints()
            xors.addAll(xors1)
            xors.addAll(xors2)
            return ImmutableSet.copyOf(xors)
        }

    fun printYearSeriesModels2() {
        val set = modelImpliesSeriesYear
        for (exp in set) {
            System.err.println(exp)
        }
    }


    fun anyConstants(): Boolean {
        for (exp in complex!!) {
            if (exp.isOrContainsConstant) return true
        }
        return false
    }

    //    public void addConstraints(Iterable<Exp> fact) {
    //        for (Exp constraint : fact) {
    //            addConstraint(constraint);
    //        }
    //    }

    fun checkNoConstants(): Boolean {
        if (anyConstants()) {
            throw IllegalStateException()
        }
        return true
    }


    //    public boolean isVvCareVar(Var vr) {
    //        assert isOpen(vr);
    //        for (Exp e : getVVConstraints()) {
    //            IntList careVars = e.getVars();
    //            if (careVars.contains(vr.getId())) {
    //                return true;
    //            }
    //        }
    //        return false;
    //    }


    fun isCareVar(`var`: Var): Boolean {

        for (exp in complex!!) {
            if (exp.caresAbout(`var`)) return true
        }

        return false
    }


    fun sortConstraintsByArity() {
        throw UnsupportedOperationException()
    }


    fun sortConstraintsByStrLenDesc() {
        throw UnsupportedOperationException()
    }

    fun sortXorsByVarCount() {
        throw UnsupportedOperationException()
    }

    fun sortConstraintsByVarCount() {
        throw UnsupportedOperationException()
    }


    fun convertRequiresOrToConflictForModels() {
        val modelXor = k.modelXor
        val modelVars = ImmutableSet.copyOf(modelXor!!.args)
        val tmp = complex
        complex = DynComplex(space)
        for (e in tmp!!) {
            val exp = e.requiresOrToConflicts(modelVars)
            addConstraint(exp)
        }
    }

    fun nestedXorCount(): Int {
        var nxc = 0
        for (complexConstraint in complex!!) {
            var xc = complexConstraint.xorCount()
            if (complexConstraint.isXor) {
                xc--
            }
            nxc += xc
        }
        return nxc
    }


    //    public boolean isVvDirty() {
    //        if (qq == null) {
    //            return false;
    //        } else if (isFailed()) {
    //            qq = null;
    //            return false;
    //        } else if (qq.isEmpty()) {
    //            qq = null;
    //            return false;
    //        }
    //
    //        assert qq.size() > 0;
    //        return true;
    //    }


    //    public void propagateVV() {
    //
    //        if (!isVvDirty()) {
    //            return;
    //        }
    //
    //        Exp nextVv = qq.remove(0);
    //
    //
    //        simplifyForNewVV(nextVv);
    //
    //        propagate();
    //    }

    @Throws(FailedCspException::class)
    fun canSimplify(constraints: Iterable<Exp>, lit: Exp): Boolean {
        val `var` = lit.vr
        for (constraint in constraints) {
            if (constraint.caresAbout(`var`)) {
                return true
            }
        }
        return false
    }

    @Throws(FailedCspException::class)
    fun simplifyComplex() {
        throw UnsupportedOperationException()
    }

    fun getComplexConstraintsContaining(`var`: Var): ImmutableList<Exp> {
        val a = ArrayList<Exp>()
        for (e in complex!!) {
            if (e.containsVar(`var`)) {
                a.add(e)
            }
        }
        return ImmutableList.copyOf(a)
    }

    //    public ImmutableList<Exp> getMsrpConstraints() {
    //        ArrayList<Exp> a = new ArrayList<Exp>();
    //        for (Exp e : complex) {
    //            if (e.containsVarsWithPrefix(MSRP_PREFIX)) {
    //                a.add(e);
    //            }
    //        }
    //        return ImmutableList.copyOf(a);
    //    }

    fun getVVpsSubsumingVV(vv: Exp): ImmutableList<Exp> {
        val a = ArrayList<Exp>()
        for (e in complex!!.vvpIt()) {
            if (e.vvpSubsumesVV(vv)) {
                a.add(e)
            }
        }
        return ImmutableList.copyOf(a)
    }

    fun isAssignedFlip(lit: Lit): Boolean {
        return if (simple!!.isEmpty) {
            false
        } else simple!!.isAssignedFlip(lit)
    }

    //    public Csp simplify(String sLit) throws FailedCspException {
    //        Lit lit = Lit.parse(sLit).asLit();
    //        simplify(lit);
    //        return copy();
    //    }


    @Throws(FailedCspException::class)
    fun simplify2(lit: Lit) {

        if (k.isSolved || k.isFailed) {
            return
        }

        assert(lit.isLit)
        val `var` = lit.getVr()

        val dirty = getComplexConstraintsContaining(`var`)

        for (before in dirty) {
            assert(before.containsVar(lit))
            if (isFailed) {
                return
            }

            val removed = complex!!.remove(before)
            assert(removed)

            val after = before.condition(lit)
            if (before === after) {
                System.err.println("lit[$lit]")
                System.err.println("before[$before]")
                System.err.println("after[$after]")
                throw IllegalStateException()
            }
            k.logSimplified(lit, before, after)

            if (after.isFalse) {
                k.failCspConstraintSimplifiedToFalse(before, lit)
                return
            }


            if (after.isTrue) {
                //ignore
                continue
            }

            assert(after.isOpen)

            addConstraint(after)

        }

    }


    fun addSeriesModelImps() {
        val map = k.computeSeriesModelMultiMap()
        for (seriesVar in map.keySet()) {
            val models = map.get(seriesVar)
            val b = ArgBuilder(space, Op.Or)
            for (model in models) {
                b.addVars(models)
            }
            val exp = b.mk()
            addConstraint(exp)
        }
    }


    fun computeModelToSeriesMap(): ImmutableMap<Var, Var> {
        val b = HashMap<Var, Var>()
        val mm = computeSeriesYearToModelMultiMap()
        for (seriesYear in mm.keySet()) {
            val models = mm.get(seriesYear)
            for (model in models) {
                b[model] = seriesYear.series
            }
        }
        return ImmutableMap.copyOf(b)
    }

    /**
     * (series year) => (model1 model2 model3)
     */
    fun computeSeriesYearToModelMultiMap(): Multimap<SeriesYear, Var> {
        val map = HashMultimap.create<SeriesYear, Var>()
        val constraints = modelImpliesSeriesYear
        for (constraint in constraints) {
            var model: Var? = null
            val seriesYears = ArrayList<SeriesYear>()
            for (arg in constraint.argIt()) {
                if (arg.isModelNegLit) {
                    model = arg.vr
                } else {
                    assert(arg.isSeriesYearAnd)
                    val seriesVar = arg.seriesVar
                    val yearVar = arg.yearVar
                    val seriesYear = SeriesYear(seriesVar, yearVar)
                    seriesYears.add(seriesYear)
                }
            }
            assert(model != null)
            assert(seriesYears.size > 0)

            for (seriesYear in seriesYears) {
                map.put(seriesYear, model)
            }
        }
        return map
    }


    fun serializeCnf(): String {
        val a = Ser()
        serializeCnf(a)
        return a.toString()
    }

    fun serializeCnf(a: Ser) {
        space.serializeVarMap(a)
        a.newLine()

        k.serializeSimpleConstraints(a)

        for (e in complex!!) {
            e.serializeCnf(a)
            a.newLine()
        }

    }


    fun flattenTopLevelNands() {
        if (complex!!.isEmpty) return
        val tmp = complex
        complex = DynComplex(space)
        for (b in tmp!!) {
            val a: Exp
            if (b.isNand) {
                a = b.asNand().flattenNand()
            } else {
                a = b
            }
            addConstraint(a)
        }
    }

    fun flattenImpsRmps() {
        k.transform(Transformer.FLATTEN_IMPS) //3
        k.transform(Transformer.FLATTEN_RMPS) //4
    }


    fun toStringSet(): Set<String> {
        val set = HashSet<String>()
        toStringSetSimple(set)
        toStringSetComplex(set)
        return set
    }

    fun toStringSetSimple(set: Set<String>) {
        throw UnsupportedOperationException()
    }

    fun toStringSetComplex(set: MutableSet<String>) {
        if (complex!!.isEmpty) return
        for (e in complex!!) {
            set.add(e.toString())
        }
    }

    fun checkStable(): Boolean {
        if (k.isStable) {
            return true
        }
        throw IllegalStateException()
    }


    fun print(depth: Int) {
        //        if (isFailed()) {
        //            print();
        //        }

        //        if (ass != null && !ass.isEmpty()) {
        //            String label = Strings.rpad("Cube:", ' ', 20);
        //            String line = label + ass.serializeSingleLine();
        //            prindent(depth, line);
        //        }

        //        if (isOpen()) {
        //            if (complex != null) {
        //                for (Exp exp : complex) {
        //                    String label = Strings.rpad("Complex:", ' ', 20);
        //                    String line = label + exp;
        //                    prindent(depth, line);
        //                }
        //            }
        //
        //        }

    }

    fun forEachSimpleConstraint(h: ConstraintHandler) {
        if (simple!!.isEmpty) return
        for (lit in simple!!.litIt()) {
            h.onConstraint(lit)
        }
    }

    fun forEachComplexConstraint(h: ConstraintHandler) {
        if (complex!!.isEmpty) return
        for (exp in complex!!) {
            h.onConstraint(exp)
        }
    }

    fun forEachConstraint(h: ConstraintHandler) {
        forEachSimpleConstraint(h)
        forEachComplexConstraint(h)
    }

    fun removeAll(that: Csp) {
        assert(this.space === that.space)
        removeAllSimpleConstraints(that)
        removeAllComplexConstraints(that)
    }

    fun removeAllSimpleConstraints(that: Csp) {
        assert(this.space === that.space)
        if (this.simple!!.isEmpty) return
        if (that.simple!!.isEmpty) return
        simple!!.removeAll(that.simple)
    }

    fun removeAllComplexConstraints(that: Csp) {
        assert(this.space === that.space)
        val complex = that.complexDyn
        this.complex!!.removeAll(complex)
    }

    fun removeComplexConstraints(complexConstraintsToRemove: Set<Exp>) {
        complex!!.removeAll(complexConstraintsToRemove)
    }


    fun conditionOnAtVars(): Csp {
        val c = space.atVarsAsCube
        val cube = c.asCubeExp()
        return condition(cube)
    }

    fun condition(c: Cube): Csp {
        k.assignAll(c)
        k.propagate()
        return k
    }

    fun removeConstraint(constraint: Exp): Boolean {
        return k.removeConstraint(constraint)
    }

    //    public void addConstraint(Line line, VarSet allInvAcy) {
    //        VarSet vars = line.getVars();
    //
    //
    //        for (int varId : vars) {
    //            assign(varId, true);
    //        }
    //
    //        for (Var vr : line.getVars()) {
    //
    //        }
    //    }


    fun enqueueNewVv(vv: Exp) {
        //        if (qq == null) qq = new ArrayList<Exp>();
        //        qq.add(vv);
    }

    //    public void enqueueNewVV(Exp vv) {
    //        System.err.println("enqueueNewVV");
    //        if (q == null) q = new Queues();
    //        q.enqueueNewVV(vv);
    //
    //    }


    //    public Exp toDnnf() {
    //        Set<String> vars = space.getVars();
    //        return toDnnf();
    //    }


    //    Element toElement() {
    //        SimpleConstraintSet simple;
    //        complexConstraintSet complex;
    //
    //        simple = ass;
    //        complex = this.complex;
    //
    //        return new Element(simple, complex);
    //    }


    //    private Cube computeOverlapLits() {
    //        if (simple.isEmpty()) return null;
    //        if (complex.isEmpty()) return null;
    //        VarSet vs2 = getFormulaVars();
    //        return simple.intersection(vs2);
    //    }


    //    public void transform(Transformer t) {
    //        Formula formula = getFormula();
    //        DynFormula tmp = fDyn;
    //        fDyn = new DynFormula(space);
    //        for (Exp b : tmp) {
    //            if (isFailed()) {
    //                return;
    //            }
    //            Exp a = t.transform(b);
    //            addConstraint(a);
    //        }
    //    }


    //            /**
    //                * !and(a b)   => or(!a !b)
    //                */
    //               private Exp negAnd(Exp formula) {
    //                   checkArgument(formula.isNegAnd());
    //                   Exp pos = formula.getPos();
    //                   List<Exp> pArgs = pos.getArgs();
    //
    //                   ArgBuilder flippedArgs = new ArgBuilder(Op.Or);
    //                   for (Exp arg : pArgs) {
    //                       flippedArgs.add(arg.flip());
    //                   }
    //
    //                   return formula.getSpace().mkOr(flippedArgs);
    //               }


    fun mkExp(): Exp? {
        return null
    }

    fun stripSeriesVarsFromYsmAnds(): DynComplex {
        assert(!k.isDirty)
        assert(isOpen)
        val stripped = DynComplex(k.space)
        val cc = k.complexDyn


        if (cc != null) {
            for (e in cc.argIt) {
                val ee = e.stripSeriesVarsFromYsmAnds()
                stripped.add(ee)
            }
        }

        return stripped

    }

    fun stripSeriesVarsFromModelImpliesSeriesYears() {
        assert(!k.isDirty)
        assert(isOpen)
        //        System.err.println("<ModelImpliesSeriesYears>");
        val stripped = DynComplex(space)
        for (e in complex!!) {
            val ee = e.stripSeriesVarsFromModelImpliesSeriesYears()
            stripped.add(ee)
        }
        this.complex = stripped
        //        System.err.println("</ModelImpliesSeriesYears>");
    }


    fun replaceSeriesWithModels() {
        assert(!k.isDirty)
        assert(isOpen)

        val map = computeSeriesYearToModelMultiMap()

        //        System.err.println("<replaceSeriesWithModels>");
        val stripped = DynComplex(space)
        for (e in complex!!) {
            if (e.isModelImpliesSeriesYear) {
                stripped.add(e)
            } else if (e.isSeriesXor) {
                stripped.add(e)
            } else if (e.isOrContainsSeriesYearAndPlus) {
                //                System.err.println("Before: " + e);
                val ee = e.replaceSeriesWithModels(map)
                //                System.err.println("After: " + ee);
                stripped.add(ee)
            } else {
                stripped.add(e)
            }
        }
        this.complex = stripped
        //        System.err.println("</replaceSeriesWithModels>");
    }


    fun stripSeriesYearsFromAcyImpliesModelYears() {
        assert(!k.isDirty)
        assert(isOpen)
        System.err.println("<AcyImpliesModelYears>")
        val stripped = DynComplex(space)
        for (e in complex!!) {
            val ee = e.stripSeriesYearsFromAcyImpliesModelYears()
            stripped.add(ee)
        }
        this.complex = stripped
        System.err.println("</AcyImpliesModelYears>")
    }


    fun removeCoreFactoryConstraints() {
        assert(isClean)
        assert(isOpen)
        val coreVars = space.coreVars
        val keep = DynComplex(space)
        for (exp in complex!!) {
            val isCore = coreVars.containsAll(exp.vars)
            if (!isCore) {
                keep.add(exp)
            }
        }
        complex = keep
    }

    fun removeAllFactoryConstraints() {
        assert(isClean)
        assert(isOpen)
        complex = DynComplex(space)
        simple = DynCube(space)
    }


    internal fun getPrefixOccurrenceCountLight(sPrefix: String): Int {
        return space.getPrefixOccurrenceCountLight(sPrefix)
    }

    fun printXorStats(): Csp {
        val counts = XorCounts.count(k)
        counts.print()
        return k
    }


    inner class VVsConditionPropagator(private val subsumedVvs: Collection<Exp>, private val vvp: Exp) : Propagator() {
        var out: Exp? = null
            private set


        override fun execute(csp: Csp) {
            val nnf = vvp.toNnf()
            var out = nnf
            for (subsumedVv in subsumedVvs) {
                out = out.conditionVV(subsumedVv)
            }
            if (out !== vvp) {
                csp.removeConstraint(vvp)
                csp.addConstraint(out)
            }
            this.out = out
        }
    }

    //    public class NewVV extends Propagator {
    //        private final Exp vv;
    //
    //        public NewVV(Exp vv) {
    //            assert vv.isVV();
    //            this.vv = vv;
    //        }
    //
    //        @Override
    //        public void enqueue(Csp csp) {
    //            csp.qVV.add(this);
    //        }
    //
    //        @Override
    //        public void execute(Csp csp) {
    //            csp.simplifyForNewVV(vv);
    //        }
    //    }

    //    public void addConstraint(Imp imp) {
    //        Exp arg1 = imp.getArg1();
    //        Exp arg2 = imp.getArg2();
    //        addBinaryOr(arg1.flip(), arg2);
    //    }
    //
    //    public void addConstraint(Rmp rmp) {
    //        Exp arg1 = rmp.getArg1();
    //        Exp arg2 = rmp.getArg2();
    //        addBinaryOr(arg1, arg2.flip());
    //    }
    //
    //    public void addConstraint(Iff iff) {
    //        Exp arg1 = iff.getArg1();
    //        Exp arg2 = iff.getArg2();
    //        addImp(arg1, arg2);
    //        addImp(arg2, arg1);
    //    }
    //
    //    public void addImp(Exp arg1, Exp arg2) {
    //        addBinaryOr(arg1.flip(), arg2);
    //    }
    //
    //    public void addRmp(Exp arg1, Exp arg2) {
    //        addBinaryOr(arg1, arg2.flip());
    //    }
    //
    //    public void addIff(Exp arg1, Exp arg2) {
    //        if (arg1 == arg2) return;
    //
    //
    //        if (arg1.isFalse() && arg2.isFalse()) return;
    //        if (arg1.isTrue() && arg2.isTrue()) return;
    //
    //        if (arg1.isFalse() && arg2.isTrue()) {
    //            failCspFalseConstraintAdded();
    //            return;
    //        }
    //
    //        if (arg1.isTrue() && arg2.isFalse()) {
    //            failCspFalseConstraintAdded();
    //            return;
    //        }
    //
    //        if (arg1.isTrue() && arg2.isOpen()) {
    //            addConstraint(arg2);
    //            return;
    //        }
    //
    //
    //        if (arg1.isOpen() && arg2.isTrue()) {
    //            addConstraint(arg1);
    //            return;
    //        }
    //
    //        if (arg1.isFalse() && arg2.isOpen()) {
    //            addConstraint(arg2.flip());
    //            return;
    //        }
    //
    //        if (arg1.isOpen() && arg2.isFalse()) {
    //            addConstraint(arg1.flip());
    //            return;
    //        }
    //
    //        addImp(arg1, arg2);
    //        addImp(arg2, arg1);
    //
    //    }

    //    public void addConstraint(ArgBuilder b) {
    //
    //        if (b.isTrue()) {
    //            return;
    //        }
    //
    //        if (b.isFalse()) {
    //            failCspFalseConstraintAdded();
    //            return;
    //        }
    //
    //        if (b.isUnary()) {
    //            Exp first = b.first();
    //            addConstraint(first);
    //            return;
    //        }
    //
    //        if (b.isBinaryOr()) {
    //            Iterator<Exp> it = b.iterator();
    //            Exp arg1 = it.next();
    //            Exp arg2 = it.next();
    //            addBinaryOr(arg1, arg2);
    //            return;
    //        }
    //
    //        if (b.isNaryOr()) {
    //            addNaryOr(b);
    //            return;
    //        }
    //
    //        if (b.isAndLike()) {
    //            for (Exp exp : b) {
    //                addConstraint(exp);
    //            }
    //        }
    //
    //        fail();
    //    }

    //    public void addNaryOr(ArgBuilder b) {
    //        assert b.isNaryOr();
    //
    //    }


    //    public void addBinaryOr(Exp arg1, Exp arg2) {
    //
    //        if (arg1.isTrue() || arg2.isTrue()) {
    //            return;
    //        }
    //
    //        if (arg1 == arg2) {
    //            addConstraint(arg1);
    //            return;
    //        }
    //
    //        if (arg1 == arg2.flip()) {
    //            return;
    //        }
    //
    //        if (arg1.isFalse()) {
    //            addConstraint(arg2);
    //        }
    //        if (arg2.isFalse()) {
    //            addConstraint(arg1);
    //        }
    //
    //        if (arg1.isAnd()) {
    //            for (Exp a1 : arg1.getArgs()) {
    //                addBinaryOr(arg2, a1);
    //            }
    //            return;
    //        }
    //
    //        if (arg2.isAnd()) {
    //            for (Exp a2 : arg2.getArgs()) {
    //                addBinaryOr(arg1, a2);
    //            }
    //            return;
    //        }
    //
    //        ArgBuilder b = new ArgBuilder(space, Op.Or);
    //        b.add(arg1);
    //        b.add(arg2);
    //        Exp or = b.mk();
    //
    //        k.addConstraint(or);
    //        k._addComplex(or);
    //
    //    }


    //    //vars have already been added - this method skips vars line
    //    public void addConstraints(String[] lines) {
    //        Iterable<String> it = Its.itForArray(lines);
    //        addConstraints(it);
    //    }


    //
    //    public void addConstraints(Sequence<Exp> constraints, Cube cube) {
    //        Function1<Exp, Exp> ff = CspK.conditionFunctionKotlin(cube);
    //        k.addConstraints2(constraints, ff);
    //    }


    //    public void addConstraints(Sequence<Exp> constraints, Function<Exp, Exp> ff) {
    //        Function1<Exp, Exp> kFF = Fn.toKFunction(ff);
    //        k.addConstraints2(constraints, kFF);
    //    }
    //
    //    public void addConstraints(Sequence<Exp> constraints, Function1<Exp, Exp> ff) {
    //        k.addConstraints2(constraints, ff);
    //    }

    fun addXImpliesAnd(x: Exp, and: And) {
        for (arg in and.getArgs()) {
            val binaryOr = space.mkOr(x.flip(), arg)
            addConstraint(binaryOr)
        }
    }

    fun addXImpliesNonAnd(x: Exp, nonAnd: Exp) {
        assert(!nonAnd.isAnd)
        val binaryOr = space.mkOr(x.flip(), nonAnd)
        addConstraint(binaryOr)
    }


    fun simplifyForNewVV(newVv: Exp) {
        if (!isOpen) {
            return
        }

        val newVvVars = newVv.vars

        for (e in complex!!) {
            if (e === newVv) continue
            if (!e.isVv) continue
            val eVars = e.vars
            if (eVars.containsAllVars(newVvVars)) {

                //                System.err.println(e);
                //                System.err.println(newVv);
                //                System.err.println();
            }
        }

    }

    fun maybeProcessVVs() {

        if (!isOpen) return


        val vvs = ArrayList<Exp>()
        val vvps = ArrayList<Exp>()

        for (e in complex!!) {
            if (e.isVv) {
                vvs.add(e)
            } else {
                assert(e.isVVPlus)
                vvps.add(e)
            }
        }

        if (vvs.isEmpty()) return
        if (vvps.isEmpty()) return

        for (vv in vvs) {
            val vvVars = vv.vars
            for (vvp in vvps) {
                val vvpVars = vvp.vars
                if (vvpVars.containsAllVars(vvVars)) {
                    boom++
                }

            }
        }
    }


    private fun skipNnf(constraint: Exp): Boolean {
        return constraint.isXorOrContainsXor
    }

    @Throws(Exception::class)
    fun printVvStats(label: String) {
        System.err.println("vv[$label]:")
        val vvs = k.vvConstraints
        val vvpsSeriesAndYear = vvpsWithSeriesAndYear
        val vvpsSeriesAndModel = vvpsWithSeriesAndModel
        System.err.println("  vvs [" + vvs.size + "]")
        System.err.println("  vvpsSeriesAndYear [" + vvpsSeriesAndYear.size + "]")
        System.err.println("  vvpsSeriesAndModel[" + vvpsSeriesAndModel.size + "]")
    }

    @Throws(Exception::class)
    fun printVvpsWithSeriesAndYear() {
        val vvps = vvpsWithSeriesAndYear
        if (vvps.size == 0) {
            System.err.println("no series and year vvps")
            return
        }
        for (vvp in vvps) {
            System.err.println("vvp: $vvp")
        }
    }


    fun anyVvpsWithSeriesAndYear(): Boolean {
        if (complex!!.isEmpty) return false
        for (exp in complex!!) {
            if (exp.isXorOrContainsXor) continue
            if (exp.isIffOrContainsIff) continue
            if (exp.isVVPlusWithSeriesAndYear) {
                return true
            }
        }
        return false
    }

    fun anyVvpsWithSeriesAndModel(): Boolean {
        if (complex!!.isEmpty) return false
        for (exp in complex!!) {
            if (exp.isXorOrContainsXor) continue
            if (exp.isIffOrContainsIff) continue
            if (exp.isVVPlusWithSeriesAndModel) {
                return true
            }
        }
        return false
    }

    //    public void simplifyBasedOnVvsUntilStable() {
    //        if (complex.isEmpty() || complex.isEmpty()) {
    //            complex = null;
    //            return;
    //        }
    //
    //        int iteration = 0;
    //        while (!isFailed()) {
    //            iteration++;
    //            System.err.println("*** vv iteration [" + iteration + "] ***");
    //            simplifyBasedOnVvs();
    //            boolean vvps = anyVvpsWithSeriesAndYear();
    //            if (!vvps) break;
    //        }
    //
    //    }


    fun printComplexConstraintsSortedByStrLen(n: Int) {
        val exps = k.sortComplexConstraintsByStrLen()
        var i = 0
        for (e in exps) {
            if (i > n) break
            System.err.println(e)
            i++
        }
    }

    fun atRefine(): Csp {
        return conditionOnAtVars()
    }

    fun conditionOutAtVars() {
        k.maybeAddAlwaysTrueVars()
        k.propagate()
    }


    enum class ProposeResult {
        TT, //Completely Open
        TF, //Implied True
        FT, //Implied False
        FF  //Cannot be assigned True or False - Csp Failure
    }

    fun getXorDeep(prefix: String): Exp? {
        for (exp in complex!!) {
            if (exp == null) {
                continue
            }
            if (exp.isXorOrContainsXor(prefix)) {
                return exp
            }
        }
        return null
    }


    fun createLitPair(lit1: Int, lit2: Int): LitPair {
        val arg1 = getLit(lit1)
        val arg2 = getLit(lit2)
        return LitPair(arg1, arg2)
    }

    fun getLit(signedVarCode: String): Exp {
        return space.mkLit(signedVarCode)
    }

    fun getLit(lit: Int): Exp {
        val varId = Head.getVarId(lit)
        val sign = Head.getSign(lit)
        return space.getLit(varId, sign)
    }


    //    public Set<Exp> getTrueVars() {
    //        HashSet<Exp> set = new HashSet<Exp>();
    //
    //        Set<Map.Entry<Exp, Boolean>> entries = simpleConstraints.entrySet();
    //        for (Map.Entry<Exp, Boolean> entry : entries) {
    //            if (entry.getValue()) {
    //                set.add(entry.getKey());
    //            }
    //        }
    //
    //        return set;
    //    }
    //
    //    public Set<Exp> getFalseVars() {
    //        HashSet<Exp> set = new HashSet<Exp>();
    //
    //        Set<Map.Entry<Exp, Boolean>> entries = simpleConstraints.entrySet();
    //        for (Map.Entry<Exp, Boolean> entry : entries) {
    //            if (!entry.getValue()) {
    //                set.add(entry.getKey());
    //            }
    //        }
    //
    //        return set;
    //    }


    //    public SpaceCsp refine(int varId, boolean sign) {
    //        SpaceCsp copy = copy();
    //        try {
    //            Exp vr = copy.getVr(varId);
    //            if (sign) {
    //                copy.addConstraint(vr);
    //            } else {
    //                copy.addConstraint(vr.flip());
    //            }
    //            copy.propagate();
    //            return copy;
    //        } catch (FailedCspException e) {
    //            return null;
    //        }
    //    }

    fun isAssigned(`var`: Var): Boolean {
        return simple!!.isAssigned(`var`)
    }

    fun isOpen(vr: Var): Boolean {
        return !isAssigned(vr)
    }

    fun isTrue(vr: Var): Boolean {
        return simple!!.isTrue(vr)
    }

    fun isFalse(vr: Var): Boolean {
        return simple!!.isFalse(vr)
    }


    fun idToCode(varId: Int): String {
        val `var` = k.getVar(varId)
        return `var`.varCode
    }

    fun codeToId(code: String): Int {
        val `var` = getVar(code)
        return `var`.getVarId()
    }

    private fun getVar(code: String): Var {
        return k.getVar(code)
    }


    //    public void computeBB() {
    //            IntList careVars = getCareVars();
    //            for (int varId : careVars) {
    //                Var vr = getVr(varId).asVar();
    //                System.err.println("spExamining Var: " + vr);
    //                OpenVarState openVarState = validateOpenVar(vr);
    //                System.err.println("\t " + openVarState);
    //            }
    //        }
    //
    //    public OpenVarState validateOpenVar(Var vr) {
    //           assert isOpen(vr);
    //
    //           System.err.println("spValidateOpenVar[" + vr + "]");
    //           boolean d = isDontCare(vr);
    //           System.err.println("\t spDC: " + d);
    //
    //           if (d) {
    //               return OpenVarState.DontCare;
    //           }
    //
    //           boolean t = proposeTrue(vr.getVarId());
    //           System.err.println("\t spProposeTrue:" + t);
    //
    //           boolean f = proposeFalse(vr.getVarId());
    //           System.err.println("\t spProposeFalse:" + f);
    //
    //
    //           if (!t && f) {
    //               //vr must be assigned false
    //               return OpenVarState.False;
    //           } else if (t && !f) {
    //               //vr must be assigned true
    //               return OpenVarState.True;
    //           } else if (t && f) {
    //               //vr must be assigned false
    //               return OpenVarState.CareVar;
    //           } else {
    //               throw new IllegalStateException();
    //           }
    //
    //
    //       }
    //


    fun createMasterList(): List<Exp> {
        val a = ImmutableList.builder<Exp>()
        if (complex != null) {
            a.addAll(complex!!)
        }
        return a.build()
    }


    fun complexIterator(): Iterator<Exp> {
        return if (complex!!.isEmpty) It.emptyIter() else complex!!.iterator()
    }


    fun printComplexityReport() {
        if (complex!!.isEmpty) return
        val maxAndSize = maxAndSize

        val expsWithMaxAnd = ArrayList<Exp>()
        for (exp in complex!!) {
            val and = exp.andWithHighestLitArgCount
            if (and != null && and.andLitArgCount == maxAndSize) {
                expsWithMaxAnd.add(exp)
            }
        }
        System.err.println("  expsWithMaxAnd:" + expsWithMaxAnd.size + ":")
        for (exp in expsWithMaxAnd) {
            System.err.println("  $exp")
            System.err.println("    " + exp.andWithHighestLitArgCount!!)
        }


    }


    @Throws(Exception::class)
    fun getModelCodesForSeries(seriesName: String): Set<String> {
        var seriesName = seriesName
        if (!seriesName.startsWith("SER")) {
            seriesName = "SER_$seriesName"
        }
        val f = k.refineFormulaOnly(seriesName)
        val n = f.toDnnf()
        val nn = n.copyToOtherSpace()

        val outVars = nn.sp().getVars(Prefix.MDL)

        val projection = nn.project(outVars)
        val cubes = projection.getCubesSmooth()

        val s = HashSet<String>()
        for (cube in cubes) {
            val firstTrueVar = cube.trueVars.getFirstVar()
            s.add(firstTrueVar.getVarCode())
        }
        return s
    }


    fun conditionOutUnVtcVars() {
        val unVtcVars = k.space.unVtcVars
        for (unVtcVar in unVtcVars) {
            addConstraint(unVtcVar.mkNegLit())
        }
        k.propagate()
    }


    fun addConstraint(exp: Exp, condition: Condition): Boolean {
        return k.addConstraint(exp, condition)
    }

    fun addConstraint(exp: Exp): Boolean {
        return k.addConstraint(exp)
    }

    companion object {


        fun isContainedByAll(nonNullCsps: List<Csp>, e: Exp): Boolean {
            for (csp in nonNullCsps) {
                if (!csp.containsConstraint(e)) {
                    return false
                }
            }
            return true
        }

        //
        //    public void printVarReport() {
        //        System.err.println("Space VarReport :");
        //        System.err.println("  all:      " + getVarSet().size() + ":" + getVarSet());
        //        System.err.println("  true:     " + getTrueVars().size() + ":" + getTrueVars());
        //        System.err.println("  false:    " + getFalseVars().size() + ":" + getFalseVars());
        //        System.err.println("  care:     " + computeFormulaVars().size() + ":" + computeFormulaVars());
        //        System.err.println();
        //    }

        fun parse(clob: String): Csp {
            val space = Space()
            val expSeq = space.parsePL(clob)
            val aa = Add(c = expSeq,space = space)
            return Csp(space, add = aa)
        }

        fun parse(cspSample: CspSample): Csp {
            val path = cspSample.path
            val clob = SpaceJvm.loadResource(path)
            return parse(clob)
        }

        var boom: Int = 0 //3858


        fun findSubsumedVVs(vvp: Exp, vvs: Iterable<Exp>): List<Exp> {
            val subsumedVVs = ArrayList<Exp>()
            for (vv in vvs) {
                if (vvp.vvpSubsumesVV(vv)) {
                    subsumedVVs.add(vv)
                }
            }
            return subsumedVVs

        }


        private val log = Logger.getLogger(Csp::class.java.name)
    }


}
