package com.tms.csp.ast;

import com.google.common.collect.*;
import com.tms.csp.It;
import com.tms.csp.VarInfo;
import com.tms.csp.argBuilder.ArgBuilder;
import com.tms.csp.ast.formula.KFormula;
import com.tms.csp.common.SeriesYear;
import com.tms.csp.fm.dnnf.ChildCounts;
import com.tms.csp.fm.dnnf.DAnd;
import com.tms.csp.fm.dnnf.DOr;
import com.tms.csp.fm.dnnf.Dnnf;
import com.tms.csp.fm.dnnf.products.Cube;
import com.tms.csp.fm.dnnf.products.PosCube;
import com.tms.csp.fm.dnnf.vars.VarFilter;
import com.tms.csp.fm.dnnf.visitor.NodeHandler;
import com.tms.csp.fm.dnnf.visitor.NodeInfo;
import com.tms.csp.graph.Filter;
import com.tms.csp.transforms.CompoundTransformer;
import com.tms.csp.transforms.Transformer;
import com.tms.csp.util.*;
import com.tms.csp.util.ints.IntIterator;
import com.tms.csp.util.varSets.VarSet;
import com.tms.csp.util.varSets.VarSetBuilder;
import com.tms.csp.varCodes.VarCode;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import java.util.*;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static com.tms.csp.ssutil.Strings.indent;



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
            _vars
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




why flattening and is a bad idea -  may disjoiny-and and children
    dont flatten if your conjuncts are disjoin
    dont comnine adjavent  conjuncts of thet are disjoing
    dont flattenn  may be disjoint: flattening wpuls lill ythjat


 */


@Immutable
public abstract class Exp implements Comparable<Exp>, PLConstants, HasCode, HasVars, HasVarId {

    protected Space _space;

    public final int expId;

//    public DynCube bb;
//    public DynCube bbLite;
//    public Exp reduced;

//    public int traversalId;


    private Long satCount; //sat count smooth

    private HashSet<Exp> parents;  //must clear if root changes
    private Integer pd; //partial derivative - for counting graph

    public Exp next; //used for hash table lookup

    private KExp k;


    @NotNull
    @Override
    public Space getSpace() {
        return _space;
    }

    public Exp(Space space, int expId) {
        this._space = space;
        this.expId = expId;
    }


    public KExp k() {
        if (k == null) {
            k = new KExp(this);
        }
        return k;
    }


//    public Space _space {
//        return space;
//    }

    public Exp flip() {
        throw new UnsupportedOperationException();
    }

    final public int flipId() {
        return flip().getExpId();
    }


    public Exp simplify() {
        return this;
    }

    public Bit eval(Cube ctx) {
        Exp s = condition(ctx);
        if (s.isConstantTrue()) return Bit.TRUE;
        if (s.isConstantFalse()) return Bit.FALSE;
        return Bit.OPEN;
    }


    public String getPrefix() {
        if (isLit()) return asLit().getPrefix();
        else if (isXor()) return asXor().getPrefix();
        else throw new IllegalStateException();
    }


    public Exp condition(Lit lit) {
        throw new UnsupportedOperationException(getClass().getName());
    }

    //Exp nn = n.con("SER_tacoma", "YR_2014");
    public Exp con(String... sLits) {
        Space space = _space;
        DynCube aa = new DynCube(space);
        for (String sLit : sLits) {
            Lit lit = space.mkLit(sLit);
            aa.assign(lit);
        }
        return condition(aa);
    }


    public Exp con(Var... tLits) {
        Space space = _space;
        DynCube aa = new DynCube(space);
        for (Var var : tLits) {
            aa.assign(var, true);
        }
        return condition(aa);
    }

    public Exp conditionOnDealerCode(int dealerCode) {
        return conditionOnDealerCodeInt32Dealer(dealerCode);
    }

//    public Exp conditionOnDealerCodes(int... dealerCodes) {
//        return conditionOnDealerCodesVarPerDealer(dealerCodes);
//    }


    public Exp conditionOnDealerCodeInt32Dealer(int int32Value) {
        Space space = _space;
        Cube cube = space.getCubeForInt32(int32Value, DLR_PREFIX);
        return condition(cube);
    }


//    public Exp conditionOnDealerCodeVarPerDealer(int int32Value) {
//        Space space = _space;
//        String varCode = Dealers.convertDealerIntToVarCode(int32Value);
//        Var vr = space.getVr(varCode);
//        return condition(vr.mkPosLit());
//    }
//
//    public Exp conditionOnDealerCodesVarPerDealer(int... dealerCodes) {
//        Space space = _space;
//        Cube cube = Dealers.computeNegatedDealers(space, dealerCodes);
//        return condition(cube);
//    }
//
//    public Exp conditionOnMsrpBucket(int bucketValue) {
//        Space space = _space;
//        Var msrpBucketVar = space.getMsrpBucketVar(bucketValue);
//        return condition(msrpBucketVar.mkPosLit());
//    }

    public List<Exp> getXorsDeep() {
        if (isConstant() || isLit()) return ImmutableList.of();
        else if (isXor()) return ImmutableList.of(this);
        else if (isNot()) return arg().getXorsDeep();
        ImmutableList.Builder<Exp> a = ImmutableList.builder();
        for (Exp arg : args()) {
            if (arg.isConstant() || arg.isLit()) {
                //skip
            } else if (arg.isXor()) {
                a.add(arg);
            } else if (arg.isComplex()) {
                a.addAll(arg.getXorsDeep());
            } else {
                throw new IllegalStateException();
            }
        }
        return a.build();
    }

    public Exp condition(int int32Value, String int32VarPrefix) {
        Space space = _space;
        Cube cube = space.getCubeForInt32(int32Value, int32VarPrefix);
        return condition(cube);
    }

    public Exp condition(Cube ctx) {
        throw new UnsupportedOperationException(getClass().getName());
    }

    public Exp condition(VarSet ctx) {
        Cube cube = new PosCube(ctx);
        return condition(cube);
    }

    /**
     * For testing purposes
     */
    public Exp simplify(String assignments) {
        EvalContext ctx = EvalContexts.fromAssignmentString(assignments);
        return null;
    }

    public String simp(String assignments) {
        return simplify(assignments).toString();
    }

    public String getSimpleName() {
        return this.getClass().getSimpleName();
    }

    final public boolean isNeg() {
        return !isPos();
    }

    abstract public boolean isPos();

    abstract public Exp getPos();

    public Exp getNeg() {
        throw new UnsupportedOperationException();
    }

    public Var asVar() {
        throw new IllegalStateException(this.getClass().getName() + ":" + toString() + ": cannot be converted to Var");
    }

    public Lit asLit() {
        throw new UnsupportedOperationException(this.getSimpleName() + " " + toString());
    }

    public DcOr asDcOr() {
        throw new UnsupportedOperationException();
    }

    public Exp asExp() {
        return this;
    }

//    public final Vars asVars() {
//        return (Vars) this;
//    }

    public PosComplexMultiVar asPosComplex() {
        throw new UnsupportedOperationException(getClass().getName());
    }

    public And asAnd() {
        throw new UnsupportedOperationException(getSimpleName() + getClass());
    }

    public DAnd asDAnd() {
        throw new UnsupportedOperationException(this.toString());
    }

    public final Xor asXor() {
        return (Xor) this;
    }

    public final Nand asNand() {
        return (Nand) this;
    }

    public Or asOr() {
        throw new UnsupportedOperationException(this.getSimpleName() + "  " + toString());
    }

    public DOr asDOr() {
        throw new UnsupportedOperationException();
    }

    public final Imp asImp() {
        return (Imp) this;
    }

    public final Iff asIff() {
        return (Iff) this;
    }

    public final Rmp asRmp() {
        return (Rmp) this;
    }

    public boolean isConstantTrue() {
        return false;
    }

    public boolean isConstantFalse() {
        return false;
    }

    public boolean isConstant() {
        return false;
    }

    public boolean isConstant(boolean sign) {
        return false;
    }

    public boolean isLit() {
        return false;
    }

    final public boolean isPosLit() {
        return isLit() && isPos();
    }

    final public boolean isNegLit() {
        return isLit() && isNeg();
    }

    public boolean isPosLitOfType(String prefix) {
        return isPosLit() && getVr().is(prefix);
    }

    public boolean isNot() {
        return false;
    }

    public Not asNot() {
        return (Not) this;
    }


    public boolean isFlattenableNand() {
        if (!isNand()) {
            return false;
        }
        if (getArg1().isPosLit() && getArg2().isOr()) {
            return true;
        } else if (getArg2().isPosLit() && getArg1().isOr()) {
            return true;
        }
        return false;
    }

    public boolean isSimple() {
        return isLit();
    }


//    public boolean isVars() {
//        return this instanceof Vars;
//    }

    public boolean isComplex() {
        return false;
    }

    public boolean hasArgs() {
        return isComplex() || isPosVarsExp();
    }

    public boolean isPosVarsExp() {
        return false;
    }

    public boolean isVarsExp() {
        return isPosVarsExp();
    }

    public boolean isPosComplex() {
        return false;
    }

    public boolean isPosArgsExp() {
        return false;
    }

    public boolean isNegComplex() {
        return false;
    }


    public boolean isNegAnd() {
        return isNot() && getPos().isAnd();
    }

    public boolean isNegOr() {
        return isNot() && getArg().isOr();
    }

    public boolean isOr() {
        return false;
    }

    public boolean isOrWithNestedAnd() {
        return isOr() && containsArgOfType(And.class);
    }

    public And getAndWithHighestLitArgCount() {

        if (isConstant()) return null;
        if (isLit()) return null;

        And best = null;

        if (isAnd()) {
            best = this.asAnd();
        }

        for (Exp arg : argIt()) {
            And aa = arg.getAndWithHighestLitArgCount();
            if (aa == null) continue;
            if (best == null || aa.getLitArgCount() > best.getLitArgCount()) {
                best = aa;
            }

        }


        return best;
    }

    public int getAndLitArgCount() {
        if (!isAnd()) return 0;
        return getLitArgCount();
    }


    public int getLitArgCount() {
        return 0;
    }

    public boolean isImp() {
        return this instanceof Imp;
    }

    public boolean isRmp() {
        return this instanceof Rmp;
    }

    public boolean isIff() {
        return this instanceof Iff;
    }

    public boolean isNand() {
        return this instanceof Nand;
    }

    public boolean isAnd() {
        return false;
    }

    public boolean isXor() {
        return false;
    }

    public boolean isXorOrContainsXor() {
        if (isXor()) return true;
        if (isConstant() || isLit()) return false;
        for (Exp e : argIt()) {
            if (e.isXorOrContainsXor()) {
                return true;
            }
        }
        return false;
    }

    public boolean isXorOrContainsXor(String prefix) {
        if (isXor(prefix)) {
            return true;
        }
        if (isConstant() || isLit()) return false;
        for (Exp e : argIt()) {
            if (e.isXorOrContainsXor(prefix)) {
                return true;
            }
        }
        return false;
    }

    public boolean isXorOrContainsModelXor() {
        return isXorOrContainsXor(MDL_PREFIX);

    }

    public boolean isIffOrContainsIff() {
        if (isIff()) return true;
        if (isConstant() || isLit()) return false;
        for (Exp e : argIt()) {
            if (e.isIffOrContainsIff()) {
                return true;
            }
        }
        return false;
    }

    public boolean isOrContainsAnd() {
        if (isAnd()) return true;
        if (isConstant() || isLit()) return false;
        for (Exp e : getArgs()) {
            if (e.isOrContainsAnd()) {
                return true;
            }
        }
        return false;
    }

    public String serializeTinyDnnf() {
        Ser a = new Ser();
        serializeTinyDnnf(a);
        return a.toString().trim();
    }

    public String serializeTinyDnnfSpace() {
        return serializeTinyDnnfSpace(true);
    }

    public String serializeTinyDnnfSpace(boolean gcFirst) {
        if (gcFirst) {
            return gc()._space.serializeTinyDnnf();
        } else {
            return _space.serializeTinyDnnf();
        }
    }


    public void serializeTinyDnnf(Ser a) {
        throw new UnsupportedOperationException(getClass().getName());
    }

    final public List<Exp> args() {
        return getArgs();
    }

    public List<Exp> getArgs() {
        throw new UnsupportedOperationException(this.getClass().getName());
    }

    final public Exp getExpr1() {
        return getArg1();
    }

    final public Exp getExpr2() {
        return getArg2();
    }

    final public Exp arg() {
        return getArg();
    }

    final public Exp arg1() {
        return getArg1();
    }

    final public Exp arg2() {
        return getArg2();
    }

    final public Exp getArg() {
        return getArg1();
    }

    public Exp getArg1() throws IndexOutOfBoundsException {
        return getArg(0);
    }

    public Exp getArg2() throws IndexOutOfBoundsException {
        return getArg(1);
    }

    final public Exp getFirst() {
        return getArg1();
    }

    final public Exp getSecond() {
        return getArg2();
    }

    public List<Exp> argsRest() {
        throw new UnsupportedOperationException();
    }

    public Exp getArg(int i) throws IndexOutOfBoundsException {
        throw new IndexOutOfBoundsException(getClass().getName());
    }

    public Exp arg(int i) throws IndexOutOfBoundsException {
        return getArg(i);
    }

    final public int argCount() {
        return size();
    }

    public int size() {
        return getArgCount();
    }

    abstract public int getArgCount();

    public boolean isCnf() {
        return isAndOfClauses() || isClause() || isLit();
    }

    public boolean isAndOfClauses() {
        return false;
    }

    public boolean firstTermIsBinaryAndSentence() {
        return getArg1().isBinaryAnd();
    }

    public boolean secondTermIsBinaryAndSentence() {
        return getArg2().isBinaryAnd();
    }

    public boolean isBinaryAnd() {
        return isAnd() && argCount() == 2;
    }

    public boolean isBinaryOr() {
        return isOr() && argCount() == 2;
    }

    public boolean isBinaryType() {
        return false;
    }

    public boolean isUnary() {
        return getArgCount() == 1;
    }

    public boolean isPair() {
        return argCount() == 2;
    }

    public boolean isBinary() {
        return isPair();
    }

    public boolean isNary() {
        return argCount() > 2;
    }

    public List<Exp> argList() {
        return ImmutableList.copyOf(getArgs());
    }


    /**
     * True if all args are of type Var
     */
    public boolean allVarArgs() {
        checkState(args().size() > 0);
        for (Exp exp : args()) {
            if (!exp.isPosLit()) return false;
        }
        return true;
    }

    /**
     * And with only vr args
     */
    public boolean isCube() {
        boolean retVal = isAnd() && isAllLits();
        if (retVal) {
            assert isCubeExp();
        }
        return retVal;
    }


//    public boolean isCubeExp() {
//        boolean retVal = isCube();
//        if (retVal) {
//            assert isDAnd();
//        }
//        return retVal;
//    }

    public boolean containsArgOfType(Class cls) {
        if (isComplex()) {
            for (Exp arg : args()) {
                if (arg.getClass() == cls) {
                    return true;
                }
            }
        }
        return false;
    }

    public String opTag() {
        return getHead();
    }

    public Exp ensureSign(boolean sign) {
        throw new UnsupportedOperationException(getClass().getName());
    }

    public boolean is(Filter filter) {
        return filter.accept(this);
    }

    @Override
    public int compareTo(@Nonnull Exp that) {
        return COMPARATOR_BY_EXP_ID.compare(this, that);
    }

    public Exp transform(Transformer... transformers) {
        if (transformers.length == 1) {
            return transformers[0].transform(this);
        } else {
            CompoundTransformer t = new CompoundTransformer(transformers);
            return t.transform(this);
        }
    }


    public Iterable<Lit> litItFromExpArray() {

        return new Iterable<Lit>() {
            @Override
            public Iterator<Lit> iterator() {
                return litIterator();
            }
        };
    }

    public Iterator<Lit> litIterator() {
        final Iterator<Exp> it = argIter();

        return new UnmodifiableIterator<Lit>() {
            @Override
            public boolean hasNext() {
                return it.hasNext();
            }

            @Override
            public Lit next() {
                Exp next = it.next();
                return next.asLit();
            }
        };
    }


    public boolean isLeaf() {
        return false;
    }

    public boolean computeSat(Lit lit) {
        throw new UnsupportedOperationException(getClass().getName());
    }

    public boolean computeSat(Cube cube) {
        throw new UnsupportedOperationException(getClass().getName());
    }

    public boolean computeSat(VarSet trueVars) {
        throw new UnsupportedOperationException(getClass().getName());
    }

    public int computeValue(Cube cube) {
        throw new UnsupportedOperationException();
    }

    public int computeValue() {
        throw new UnsupportedOperationException(getSimpleName());
    }

    public int computeValue(String... sLits) {
        Space space = _space;
        DynCube a = new DynCube(space);
        for (String sLit : sLits) {
            Lit lit = space.mkLit(sLit);
            a.assign(lit);
        }
        return computeValue(a);
    }


    public Set<Cube> getCubesRough() {
//        this.getSmooth().getCubesSmooth()
        return computeCubesRough();
    }

    public Set<Cube> computeCubesSmooth() {
        throw new UnsupportedOperationException(getClass().getName());
    }

    public Set<Cube> computeCubesRough() {
        throw new UnsupportedOperationException(getClass().getName());
    }

    public Set<Cube> getProducts() {
        return getCubesSmooth();
    }

    public Exp flatten() {
        return this;
    }

    public boolean checkDnnf() {
        throw new UnsupportedOperationException(getClass().getName());
    }

    public Exp chkDnnf() {
        assert checkDnnf();
        return this;
    }

    public Exp project(String... outVars) {
        Space space = _space;
        VarSetBuilder b = space.newMutableVarSet();
        for (String varCode : outVars) {
            Var var = space.getVar(varCode);
            b.addVar(var);
        }
        return project(b.build());
    }


    public Exp project(Set<Var> outVars) {
        Space space = _space;
        VarSetBuilder b = space.newMutableVarSet();
        b.addVars(outVars);
        return project(b.build());
    }

    /**
     * Inportant: projection does not preserve *determinism*.
     * This means that the result "project" is non-deterministic. That is:
     * it is no longer a d-DNNf.
     * It is now just a DNNF
     * And model counting no longer works
     * You now need to to computeModel (aka all-sat, aka forEach( and count those
     */
    public Exp project(VarSet outVars) {
        throw new UnsupportedOperationException(getClass().getName());
    }

//
//    final public void print() {
//        print(0);
//    }


//    public void print(int depth) {
//
//    }

    public Bit getValue(Var var) {
        throw new UnsupportedOperationException(getClass().getName());
    }

    public DynCube getBB1() {
        return computeBB1();
    }

    public DynCube getBB2() {
        return computeBB2();
    }


    public DynCube computeBB() {
        return computeBB1();
//        return computeBB_UsingTooManyConditions(); //todo fix this
    }

    public DynCube computeBB1() {
        assert isSat();


        DynCube b = new DynCube(_space);

        for (Var var : varIt()) {
            Lit t = var.mkPosLit();
            Lit f = var.mkNegLit();
            boolean tSat = computeSat(t);
            boolean fSat = computeSat(f);

            if (tSat && !fSat) {
                b.assign(t);
            } else if (!tSat && fSat) {
                b.assign(f);
            } else if (!tSat && !fSat) {
                System.err.println("NOT SAT");
                System.err.println(this.toString());
                throw new IllegalStateException(var.getVarCode());
            } else {
                //truly open
            }
        }

        return b;
    }

    public boolean isDAnd() {
        return false;
    }

    public boolean isCubeExp() {
        return false;
    }

    public DynCube computeBB2() {
        Space space = _space;
        assert isSat();
        DynCube b = new DynCube(space);

        for (Var var : varIt()) {
            Lit t = var.mkPosLit();
            Lit f = var.mkNegLit();

            boolean tSat = condition(t).isSat();
            boolean fSat = condition(f).isSat();

            if (tSat && !fSat) {
                b.assign(t);
            } else if (!tSat && fSat) {
                b.assign(f);
            } else if (!tSat && !fSat) {
                throw new IllegalStateException();
            } else {
                //truly open
            }
        }
        return b;
    }


    public DynCube computeBB_UsingTooManyConditions() {

        assert isSat();

        DynCube b = new DynCube(_space);

        for (Var var : varIt()) {
            Lit t = var.mkPosLit();
            Lit f = var.mkNegLit();


//            boolean tSat = computeSat(tCon);
//            boolean fSat = computeSat(fCon);

            Exp tt = condition(t);
            Exp ff = condition(f);

            boolean tSat = tt.isSat();
            boolean fSat = ff.isSat();

            if (tSat && !fSat) {
                b.assign(t);
            } else if (!tSat && fSat) {
                b.assign(f);
            } else if (!tSat && !fSat) {
                throw new IllegalStateException(var.getVarCode());
            } else {
                //truly open
            }
        }

        return b;
    }


    public void checkEmptyCubeValueAgainstSatCount() {

    }

    public long computeValue(Lit tLit) {
        return 0;
    }

    public Exp chkTrue() {
        String actual = toString();
        if (isConstantTrue()) {
            return this;
        } else {
            System.err.println("expected  [true]");
            System.err.println("actual    [" + actual + "]");
            throw new IllegalStateException();
        }
    }


    public boolean chkFalse() {
        String actual = toString();
        if (isConstantFalse()) {
            return true;
        } else {
            System.err.println("expected  [false]");
            System.err.println("actual    [" + actual + "]");
            throw new IllegalStateException();
        }
    }

    public int getForEachSatCount() {
        return getCubesSmooth().size();
    }


    public int getCubesSmoothCount() {
        return getCubesSmooth().size();
    }


    public boolean isDOr() {
        return false;
    }

    public boolean isDcOr() {
        return false;
    }


    public Collection<Exp> findSubsumedVVs(VVs vvs) {


        throw new UnsupportedOperationException();
    }


    public static List<Exp> findSubsumedVVs(Exp vvp, Collection<Exp> vvs) {
        ArrayList<Exp> subsumedVVs = new ArrayList<Exp>();
        for (Exp vv : vvs) {
            if (vvp.vvpSubsumesVV(vv)) {
                subsumedVVs.add(vv);
            }
        }
        return subsumedVVs;

    }

    public KFormula asFormula() {
        throw new UnsupportedOperationException(getClass().getName());
    }


    public Exp toDnnf() {
        throw new UnsupportedOperationException(getClass().getName() + ": " + this);
    }

    public Exp toDnnfSmooth() {
        return toDnnf().getSmooth();
    }

    public void print() {
        System.err.println(getSimpleName());
    }


    public void print(String heading) {
        System.err.println(getSimpleName() + heading);
        if (hasArgs()) {
            for (Exp arg : argIt()) {
                System.err.println("  " + arg);
            }
        }
    }

    public UnionFind computeUnionFind() {
        throw new UnsupportedOperationException();
    }

    public Exp getComplexFccs() {
        throw new UnsupportedOperationException(getClass().getName());
    }

    public boolean isFormula() {
        return false;
    }

    public boolean isFcc() {
        return false;
    }

    public boolean isDFormula() {
        return false;
    }

    public boolean isElement() {
        return false;
    }


    public boolean checkSpace(Space space) {
        assert _space == space : getClass().getName();

        for (Exp exp : argIt()) {
            exp.checkSpace(space);
        }

        return true;
    }

    abstract public boolean hasFlip();

    public void serializeGiantOr(Ser a) {
        throw new UnsupportedOperationException();
    }

    final public String serializeGiantOr() {
        Ser a = new Ser();
        serializeGiantOr(a);
        return a.toString();
    }

    public int getProductCount() {
        return getProducts().size();
    }

    public static boolean isFlip(Exp arg1, Exp arg2) {
        Exp pos;
        Exp neg;
        if (arg1.isPos() && arg2.isNeg()) {
            pos = arg1;
            neg = arg2;
        } else if (arg1.isNeg() && arg2.isPos()) {
            pos = arg2;
            neg = arg1;
        } else {
            assert arg1.sign() == arg2.sign();
            return false;
        }

        if (!pos.hasFlip()) {
            return false;
        }

        if (pos.isTrue()) {
            return neg.isFalse();
        } else if (pos.isLit()) {
            return neg.isLit() && pos.asLit().sameVarCode(neg.asLit());
        } else if (pos.isComplex()) {
            return neg.isNot() && neg.asNot().getPos() == pos;
        } else {
            throw new IllegalStateException();
        }
    }

    public VarSet getDontCares() {
        VarSet allVars = _space.getVars();
        VarSet careVars = getVars();
        VarSet dontCares = allVars.minus(careVars);
        return dontCares;
    }


//    public boolean isLitMatched() {
//        throw new UnsupportedOperationException(getClass().getName());
//    }


    public Exp litMatch() {
        throw new UnsupportedOperationException(getClass().getName());
    }

    public False asFalse() {
        throw new UnsupportedOperationException();
    }

    public Exp smooth(VarSet dontCares) {
        throw new UnsupportedOperationException(getClass().getName());
    }

    public void _setIsSmooth() {
        throw new UnsupportedOperationException(getClass().getName());
    }

    /**
     * To better support smoothness
     */
    public boolean isDontCareOr() {
        return isOr() && argCount() == 2 && arg1() == arg2().flip();
    }

    /**
     * To better support smoothness
     */
    public boolean isTrueish() {
        return isConstantTrue() || isDontCareOr();
    }

    public VarSet getCoreVars(final VarInfo varInfo) {
        VarSet vars = getVars();
        return vars.filter(new VarFilter() {
            @Override
            public boolean accept(Var var) {
                return var.isCoreXor() || varInfo.isInvAcy(var.getVarCode());
            }
        });
    }

    public VarSet getYmxiVars() {
        VarSet vars = getVars();
        return vars.filter(new VarFilter() {
            @Override
            public boolean accept(Var var) {
                return var.isCoreXor();
            }
        });
    }

    public boolean isOrContainsSeriesYearAnd() {
        if (isConstant()) return false;
        if (isLit()) return false;
        if (isSeriesYearAnd()) {
            return true;
        } else {
            for (Exp arg : argIt()) {
                if (arg.isOrContainsSeriesYearAnd()) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isOrContainsSeriesYearAndPlus() {
        if (isConstant()) return false;
        if (isLit()) return false;
        if (isSeriesYearAndPlus()) {
            return true;
        } else {
            for (Exp arg : argIt()) {
                if (arg.isOrContainsSeriesYearAndPlus()) {
                    return true;
                }
            }
        }
        return false;
    }

    public List<Lit> getLitsDeep() {
        List<Lit> a = new ArrayList<Lit>();
        for (Exp arg : args()) {
            if (arg.isConstant()) continue;
            else if (arg.isLit()) a.add(arg.asLit());
            else if (arg.isNot()) a.addAll(arg.arg().getLitsDeep());
            else if (arg.isComplex()) a.addAll(arg.getLitsDeep());
            else throw new IllegalStateException();
        }
        return a;
    }

    abstract public boolean isNew();

    public abstract void notNew();

    public long satCountPL() {
        throw new UnsupportedOperationException(this.getSimpleName() + ": " + toString());
    }

    public boolean sameSign(@NotNull Lit that) {
        return asLit().sign == that.sign;
    }

    public boolean sameVar(@NotNull Lit that) {
        return getVarId() == that.getVarId();
    }

    public void printXml() {
        System.err.println(toXml());
    }

    @NotNull
    public Var getVar(@NotNull String code) {
        return _space.getVar(code);
    }

    public static class LitArg {

        public static final LitArg NONE = new LitArg(false, false);
        public static final LitArg FLIP = new LitArg(true, false);
        public static final LitArg BOTH = new LitArg(true, true);

        public final boolean varMatch;
        public final boolean signMatch;

        public LitArg(boolean varMatch, boolean signMatch) {
            this.varMatch = varMatch;
            this.signMatch = signMatch;
        }

    }


    /**
     * @param sLits
     * @return
     */
    public Exp condition(String sLits) {
        ConditionOn conditionOn = _space.parser.parseLitsToConditionOn(sLits);
        return conditionOn.conditionThat(this);
    }

    public Cube asCube() {
        throw new UnsupportedOperationException();
    }

    public CubeExp asCubeExp() {
        throw new UnsupportedOperationException();
    }

    public LitArg compare(Exp that) {
        if (!that.isLit()) return LitArg.NONE;
        if (!this.isLit()) return LitArg.NONE;
        if (this.getVarId() != that.getVarId()) return LitArg.NONE;
        if (this == that) return LitArg.BOTH;
        if (this == that.flip()) return LitArg.FLIP;
        throw new IllegalStateException();
    }

    public Iterable<Exp> argItFlipped() {
        return new Iterable<Exp>() {
            @Override
            public Iterator<Exp> iterator() {
                return argIteratorFlipped();
            }
        };
    }

    public Iterable<Exp> argIt() {
        return () -> argIter();
    }

    public ArgBuilder argBuilder(Op op) {
        return _space.expFactory.argBuilder(op);
    }

    public ArgBuilder argBuilder(Op op, Cube cube) {
        return _space.expFactory.argBuilder(op, cube);
    }

    public ArgBuilder argBuilder(Op op, Iterable<Exp> args) {
        return _space.expFactory.argBuilder(op, args);
    }


    public VarSet getVars() {
        throw new UnsupportedOperationException(getClass().getName());
    }

    public Iterable<String> getVarCodeIt() {
        return getVars().varCodeIt();
    }

    public ImmutableSet<Integer> getVars2() {
        throw new UnsupportedOperationException(getClass().getName());
    }

    public Exp mkFalse() {
        return _space.mkFalse();
    }

    public Exp mkTrue() {
        return _space.mkTrue();
    }

    public Exp mkAnd(Exp... args) {
        return _space.mkAnd(args);
    }


    public Exp mkAnd(List<Exp> args) {
        return _space.mkAnd(args);
    }

    public Exp mkXor(List<Exp> args) {
        return _space.mkXor(args);
    }

    public Exp mkOr(Exp... args) {
        return _space.mkOr(args);
    }

    public Exp mkOr(List<Exp> args) {
        return _space.mkOr(args);
    }

    public Exp mkIff(Exp arg1, Exp arg2) {
        return ef().mkBinaryIff(arg1, arg2);
    }

    public Exp mkRmp(Exp arg1, Exp arg2) {
        return _space.mkBinaryImp(arg2, arg1);
    }

    public Exp mkImp(Exp arg1, Exp arg2) {
        return _space.mkBinaryImp(arg1, arg2);
    }

    public Exp mkNand(Exp arg1, Exp arg2) {
        return _space.mkBinaryNand(arg1, arg2);
    }

    public boolean isNegationNormalForm() {
        return false;
    }

    public static PosOp getOp(String code) {
        String codeUpperCase = code.toUpperCase();
        PosOp[] values = PosOp.values();
        for (PosOp value : values) {
            String token = value.name();
            if (token.equals(codeUpperCase)) {
                return value;
            }
        }
        return null;
    }

    public static boolean isComplexOpToken(String code) {
        PosOp op = getOp(code);
        return op != null && op.isComplex();
    }

    public static boolean isOpToken(String code) {
        return getOp(code) != null;
    }

    final public boolean eqStr(String expToString) {
        return toString().equals(expToString);
    }

    final public boolean eq(Exp that) {
        return eq1(that) && eq2(that);
    }

    final public boolean eq1(Exp that) {
        return toString().equals(that.toString());
    }

    final public boolean eq2(Exp that) {
        if (this == that) return true;
        if (that == null || getClass() != that.getClass()) return false;

//        assert this._space == that._space;
//        int id1 = this.getExpId();
//        int id2 = that.getExpId();


//        boolean retVal = (id1 == id2);

        if (argCount() != that.argCount()) return false;
        if (getClass() != that.getClass()) return false;


        if (isPosComplex()) {
            PosComplexMultiVar pc1 = asPosComplex();
            PosComplexMultiVar pc2 = that.asPosComplex();
            return pc1.sameArgs(pc2);
        } else if (isNot()) {
            return asNot().samePos(that.asNot());
        } else if (isLit()) {
            Lit l1 = asLit();
            Lit l2 = that.asLit();
            return l1.toString().equals(l2.toString());
        } else if (isConstantTrue()) {
            return that.isConstantTrue();
        } else if (isConstantFalse()) {
            return that.isConstantFalse();
        } else {
            throw new IllegalStateException();
        }

    }

    final public boolean identical(Object that) {
        return this == that;
    }

    @Override
    final public boolean equals(Object tt) {
        if (identical(tt)) return true;
        if (this == tt) return true;
        if (tt == null || getClass() != tt.getClass()) return false;
        Exp that = (Exp) tt;


        boolean retVal = eq1(that);

        if (retVal) {
            String s1 = this.toString();
            String s2 = that.toString();
            if (!s1.equals(s2)) {
                System.err.println(s1);
                System.err.println(s2);
                throw new AssertionError();
            }
        }

        return retVal;

    }

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

    public Var getVr() {
        throw new UnsupportedOperationException(getClass() + "");
    }


    public Space sp() {
        return _space;
    }


    //index formula space
    final public int getExpId() {
        return expId;
    }

    /**
     * Code is the same as unsigned head
     *
     * @return
     */
    @Override
    public String getCode() {
        String ret;
        if (isLit()) {
            ret = getVarCode();
        } else {
            PosOp posOp = getPosOp();
            String name = posOp.name();
            ret = name.toLowerCase();
        }

        return ret;
    }

    /**
     * aka signed code
     */
    final public String getHead() {
        if (isPos()) {
            if (isComplex()) return getPosOp().toString().toLowerCase();
            else return getPosOp().toString().toLowerCase();
        } else {
            return NOT_TOKEN + getCode();
        }

    }

    public String getVarToken(Ser a) throws UnsupportedOperationException {
        return getVarCode();
    }

    public VarCode getVarCode2() throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }

    public String getVarCode() throws UnsupportedOperationException {
        throw new UnsupportedOperationException(getSimpleName());
    }

    @Override
    public String toString() {
        Ser a = new Ser();
        serialize(a);
        return a.toString();
    }

    public void toString(Ser a) {
        serialize(a);
    }

    @Override
    final public int hashCode() {
        return expId;
    }

    public boolean isAssignedValue(boolean value) {
        if (isTrue()) {
            return value;
        } else if (isFalse()) {
            return !value;
        } else {
            return false;
        }
    }


    public static boolean checkFixedArgs(Exp[] fixedArgs) {
        return checkFixedArgs(fixedArgs, fixedArgs.length);
    }

    public static boolean checkFixedArgs(Exp[] fixedArgs, int argCount) {

        int ii = 0;
        int i = 0;
        Exp a1 = null;
        while (ii < argCount) {

            Exp a2 = fixedArgs[i];
            if (a2 != null) {

                if (a1 != null) {

                    int expId1 = a1.getExpId();
                    int expId2 = a2.getExpId();

                    if (expId1 == expId2) {
                        fixedArgs[i - 1] = null;
                    } else if (expId1 > expId2) {
                        throw new IllegalStateException(expId1 + " " + expId2);
                    }

                }

                a1 = a2;

                ii++;
            } else {
                System.err.println("Null arg");
            }

            i++;


        }

        return true;
    }


    public boolean isOpen() {
        return !isConstant();
    }

    final public boolean isTrue() {
        return isConstantTrue();
    }

    final public boolean isFalse() {
        return isConstantFalse();
    }


    public PosOp getPosOp() {
        throw new UnsupportedOperationException();
    }


    final public boolean sign() {
        return isPos();
    }

    abstract public Var getFirstVar();

    public static final Comparator<Exp> COMPARATOR_BY_SORT_KEY = new Comparator<Exp>() {

        @Override
        public int compare(Exp e1, Exp e2) {

            if (e1 == null && e2 == null) {
                return 0;
            }

            if (e1 == null && e2 != null) {
                return -1;
            }

            if (e1 != null && e2 == null) {
                return 1;
            }


            Exp p1 = e1.getPos();
            Exp p2 = e2.getPos();

            MacroType m1 = p1.getMacroType();
            MacroType m2 = p2.getMacroType();
            int m = m2.compareTo(m2);
            if (m != 0) return m;


            Integer c1 = p1.argCount();
            Integer c2 = p2.argCount();
            int c = c1.compareTo(c2);
            if (c != 0) return c;


            String t1 = p1.getToken();
            String t2 = p2.getToken();
            int t = t1.compareTo(t2);
            if (t != 0) return t;

            Integer s1 = e1.sign() ? 1 : 0;
            Integer s2 = e2.sign() ? 1 : 0;

            return s1.compareTo(s2);
        }
    };

    /**
     * xors and iffs first
     * <p>
     * within that
     * varCount
     */
    public static final Comparator<Exp> COMPARATOR_F = new Comparator<Exp>() {

        @Override
        public int compare(Exp e1, Exp e2) {

            if (e1 == null && e2 == null) {
                return 0;
            }

            if (e1 == null && e2 != null) {
                return -1;
            }

            if (e1 != null && e2 == null) {
                return 1;
            }


            Exp p1 = e1.getPos();
            Exp p2 = e2.getPos();

            MacroType m1 = p1.getMacroType();
            MacroType m2 = p2.getMacroType();
            int m = m2.compareTo(m2);
            if (m != 0) return m;


            Integer c1 = p1.argCount();
            Integer c2 = p2.argCount();
            int c = c1.compareTo(c2);
            if (c != 0) return c;


            String t1 = p1.getToken();
            String t2 = p2.getToken();
            int t = t1.compareTo(t2);
            if (t != 0) return t;

            Integer s1 = e1.sign() ? 1 : 0;
            Integer s2 = e2.sign() ? 1 : 0;

            return s1.compareTo(s2);
        }
    };


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


    public String getConstantTrueToken(Ser a) {
        return a.tokens.constantTrue;
    }

    public String getConstantFalseToken(Ser a) {
        return a.tokens.constantFalse;
    }

    public String getConstantToken(boolean value, Ser a) {
        String token = getConstantToken(value);
        if (value) {
            return getConstantTrueToken(a);
        } else {
            return getConstantTrueToken(a);
        }
    }

    public String getConstantToken(boolean sign) {
        return sign ? TRUE_TOKEN : FALSE_TOKEN;
    }

    public String getNotToken(Ser a) {
        if (isNegLit()) {
            return a.tokens.negationVar.token;
        } else if (isNegComplex()) {
            return a.tokens.negationComplex.token;
        } else {
            throw new IllegalStateException();
        }
    }

    public String getComplexOpToken() {
        assert isComplex();
        return getPosOp().getComplexOpToken(Settings.get().ser);
    }

    public String getPosComplexOpToken(Ser a) {
        return getPosOp().getComplexOpToken(a);
    }


    public String getToken() {
        return getToken(Settings.get().ser);
    }

    public String getToken(Ser a) {
        if (isConstantTrue()) return getConstantTrueToken(a);
        if (isConstantFalse()) return getConstantFalseToken(a);
        if (isPosLit()) return getVarToken(a);
        if (isNot()) return getNotToken(a);
        if (isComplex()) return getPosComplexOpToken(a);
        throw new IllegalStateException(getClass().getName());
    }


    final public void serializeDimacsCnf(Ser a) {
        assert isAnd();
        List<Exp> clauses = args();
        for (Exp clause : clauses) {
            clause.serializeDimacs(a);
        }
    }

    final public void serializeDimacsDnf(Ser a) {
        assert isOr();
        List<Exp> cubes = args();
        for (Exp cube : cubes) {
            cube.serializeDimacs(a);
        }
    }

    final public void serializeDimacs(Ser a) {
        checkState(isClause() || isCube());
        if (isOr() || isAnd()) {
            List<Exp> args = getArgs();
            for (int i = 0; i < args.size(); i++) {
                Exp arg = args.get(i);
                arg.serializeDimacs(a);
                a.argSep();
            }
            a.append(0);
            a.newLine();
        } else if (isLit()) {
            Var var = getVr();
            int varId = var.getVarId();
            if (isNegLit()) {
                a.append("-");
            }
            a.append(varId);
        } else {
            throw new IllegalStateException();
        }
    }

    final public void serializeCnf(Ser a) {
//        checkState(isClause());
        assert isClause() : this;
        if (isOr()) {
            List<Exp> args = getArgs();
            for (int i = 0; i < args.size(); i++) {
                Exp arg = args.get(i);
                arg.serializeCnf(a);
                if (i < (getArgCount() - 1)) {
                    a.argSep();
                }
            }
        } else if (isPosLit()) {
            String varCode = getVarCode();
            a.append(varCode);
        } else if (isNegLit()) {
            Var var = getVr();
            String varCode = var.getVarCode();
            a.bang();
            a.append(varCode);
        } else {
            throw new IllegalStateException();
        }
    }

    final public void serializeTinyCnf(Ser a) {
        checkState(isClause(), "Not a clause: " + this);
        if (isOr()) {
            List<Exp> expSet = getArgs();
//            System.err.println(expSet.size() + ":" + expSet);
            for (int i = 0; i < expSet.size(); i++) {
                Exp arg = expSet.get(i);
                arg.serializeTinyCnf(a);
                if (i < (getArgCount() - 1)) {
                    a.argSep();
                }
            }
        } else if (isPosLit()) {
            String tinyId = asVar().getTinyId();
            a.append(tinyId);
        } else if (isNegLit()) {
            Var var = getVr();
            String tinyId = var.getTinyId();
            a.append("!");
            a.append(tinyId);
        } else {
            throw new IllegalStateException();
        }
    }

    public void serialize(Ser a) {
        a.ap("Exp");
//        throw new UnsupportedOperationException();
    }

    public String serialize() {
        Ser a = new Ser();
        serialize(a);
        return a.toString().trim();
    }

    public void serializeArgs(Ser a) {
        throw new UnsupportedOperationException();
    }

    public void prindent(int depth, Ser a) {
        a.append(indent(depth));
        serialize(a);
        a.append(NEW_LINE);
    }

    public void loadToNnf() throws Exception {
        throw new UnsupportedOperationException();
    }


    public boolean isVarMap() {
        return getCode().equalsIgnoreCase("varCode");
    }

    public boolean isNegated() {
        return isNot() || isNegLit();
    }


    public static final Comparator<Exp> COMPARATOR_BY_ARITY = new Comparator<Exp>() {
        @Override
        public int compare(Exp e1, Exp e2) {
            checkNotNull(e1);
            checkNotNull(e2);
            Integer argCount1 = e1.getArgCount();
            Integer argCount2 = e2.getArgCount();
            return argCount1.compareTo(argCount2);
        }
    };

    public static final Comparator<Exp> COMPARATOR_FOR_FORMULA = new Comparator<Exp>() {
        @Override
        public int compare(Exp e1, Exp e2) {
            checkNotNull(e1);
            checkNotNull(e2);
            Integer argCount1 = e1.getArgCount();
            Integer argCount2 = e2.getArgCount();
            return argCount1.compareTo(argCount2);
        }
    };


    public static final Comparator<Exp> COMPARATOR_BY_STR_LEN_DESC = new Comparator<Exp>() {
        @Override
        public int compare(Exp e1, Exp e2) {
            checkNotNull(e1);
            checkNotNull(e2);
            String o1 = e1.toString();
            String o2 = e2.toString();
            Integer i1 = o1.length();
            Integer i2 = o2.length();
            return i2.compareTo(i1);
        }
    };

    public static final Comparator<Exp> COMPARATOR_BY_STR_LEN = new Comparator<Exp>() {
        @Override
        public int compare(Exp e1, Exp e2) {
            checkNotNull(e1);
            checkNotNull(e2);
            String o1 = e1.toString();
            String o2 = e2.toString();
            Integer i1 = o1.length();
            Integer i2 = o2.length();
            return i1.compareTo(i2);
        }
    };

    public static final Comparator<Exp> COMPARATOR_BY_EXP_ID = new Comparator<Exp>() {
        @Override
        public int compare(Exp e1, Exp e2) {
            checkNotNull(e1);
            checkNotNull(e2);
            Integer expId1 = e1.getExpId();
            Integer expId2 = e2.getExpId();
            return expId1.compareTo(expId2);
        }
    };

    public static final Comparator<Exp> COMPARATOR_BY_VAR_COUNT = new Comparator<Exp>() {
        @Override
        public int compare(Exp e1, Exp e2) {
            checkNotNull(e1);
            checkNotNull(e2);
            Integer expId1 = e1.getVars().size();
            Integer expId2 = e2.getVars().size();
            return expId1.compareTo(expId2);
        }
    };

    public static final Comparator<String> COMPARATOR_BY_STR_LEN2 = new Comparator<String>() {
        @Override
        public int compare(String o1, String o2) {
            checkNotNull(o1);
            checkNotNull(o2);
            Integer i1 = o1.length();
            Integer i2 = o2.length();
            return i1.compareTo(i2);
        }
    };


    public ImmutableList<Exp> copyOfArgs() {
        return ImmutableList.copyOf(getArgs());
    }

    public Exp simplifySeriesModelAnd() {
        if (isConstant() || isLit()) return this;
        assert isComplex();
        if (isConstant() || isLit()) {
            return this;
        } else if (isAnd() && size() == 2 && arg(0).isPosLit() && arg(1).isPosLit() && containsSeriesVar() && containsModelVar()) {
            if (arg(0).isModelVar()) return arg(0);
            else return arg(1);
        } else if (isAnd() && containsSeriesPosLit() && containsModelPosLit()) {
            return removeSeriesPosLitFromAnd();
        } else if (isNot()) {
            return arg().simplifySeriesModelAnd().flip();
        } else if (isAnd()) {
            ArrayList<Exp> args = new ArrayList<Exp>();
            for (Exp arg : args()) {
                args.add(arg.simplifySeriesModelAnd());
            }
            return mkAnd(args);
        } else if (isOr()) {
            ArrayList<Exp> args = new ArrayList<Exp>();
            for (Exp arg : args()) {
                args.add(arg.simplifySeriesModelAnd());
            }
            return mkOr(args);
        } else if (isXor()) {
            ArrayList<Exp> args = new ArrayList<Exp>();
            for (Exp arg : args()) {
                args.add(arg.simplifySeriesModelAnd());
            }
            return mkXor(args);
        } else {
            throw new IllegalStateException(this.toString());
        }

    }

    /**
     * @param sm
     * @return
     */
    public boolean modelImplySeries(SeriesModel sm, Set<Exp> vvs) {
        Space space = _space;

        assert sm.seriesVar != null;
        assert sm.modelVar != null;

        Var sv = sm.seriesVar.asVar();
        Var mv = sm.modelVar.asVar();


        for (Exp vv : vvs) {


            Exp a1;
            Exp a2;
            if (vv.isNot()) {
                a1 = vv.getPos().getArg1();
                a2 = vv.getPos().getArg2();
            } else {
                a1 = vv.getArg1();
                a2 = vv.getArg2();
            }

            Var v1 = a1.getVr();
            Var v2 = a2.getVr();

            PosOp op = vv.getPosOp();

            if (a1.equals(sv) && a2.equals(mv)) {
                if (op.isRmp()) return true;
            } else if (a1.equals(mv) && a2.equals(sv)) {
                if (op.isImp()) return true;
            } else if (a1.flip().equals(mv) && a2.equals(sv)) {
                if (op.isOr()) return true;
            } else if (a2.flip().equals(mv) && a1.equals(sv)) {
                if (op.isOr()) return true;
            }

        }

        return false;

    }

    public int indexOf(Exp arg) {
        List<Exp> args = getArgs();

        for (int i = 0; i < args.size(); i++) {
            Exp exp = args.get(i);
            if (exp.equals(arg)) {
                return i;
            }
        }
        return -1;
    }

    public Xor getXorParent() {
        if (isPosLit()) {
            return getVr().getXorParent();
        } else {
            throw new IllegalStateException("getXorParent does not make sense for class:" + getClass().getName());
        }
    }

    public void setXorParent(Xor xorParent) {
        //no op1
    }

    public boolean isXorChild() {
        if (!isPosLit()) {
            return false;
        }

        Xor xorParent = getXorParent();
        return xorParent != null;
    }


    public boolean isOrContainsConstants() {
        if (isConstant()) {
            return true;
        }

        if (isPosLit()) {
            return false;
        }

        if (isNot()) {
            return getArg().isOrContainsConstants();
        }

        if (isNary() || isPair()) {
            List<Exp> args = getArgs();
            for (Exp arg : args) {
                if (arg.isOrContainsConstants()) {
                    return true;
                }
            }
        }

        return false;

    }

    public static boolean anyConstants(Exp constraint) {
        return anyConstants(constraint.argIt());
    }

    public static boolean anyConstants(Iterable<Exp> constraints) {
        for (Exp constraint : constraints) {
            if (constraint.isOrContainsConstants()) {
                return true;
            }
        }
        return false;
    }

    public boolean containsVarId(int varId) {
        return false;
    }

    public boolean containsVarsWithPrefix(String prefix) {
        if (isConstant()) {
            return false;
        } else if (isLit()) {
            return getVr().is(prefix);
        } else {
            VarSet vars = getVars();
            for (Var var : vars) {
                if (var.is(prefix)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean containsVar(Exp lit) {
        return containsVarId(lit.getVarId());
    }

    @Override
    public boolean containsVar(String varCode) {
        Space space = _space;
        Var var = space.getVar(varCode);
        return containsVar(var);
    }

    @Override
    public boolean containsVar(Var var) {
        return containsVarId(var.getVarId());
    }


    @Override
    public boolean containsVar(Lit lit) {
        return containsVarId(lit.getVarId());
    }


    public boolean caresAbout(Var var) {
        return containsVar(var);
    }


//    public Exp[] xorSplit(Xor xor) {
//        List<Exp> args = xor.asXor().getArgs();
//        Exp[] s = new Exp[args.size()];
//        for (int i = 0; i < args.size(); i++) {
//            s[i] = simplify(xor, i);
//        }
//        return s;
//    }


    public boolean isOrContainsConstant() {
        return false;
    }

    public Op getOp() {
        throw new UnsupportedOperationException(this.getClass().getName());
    }


    public Op op() {
        return getOp();
    }

    class SeriesModel {
        Exp seriesVar;
        Exp modelVar;
    }


    public boolean isAllPosLits() {
        return isAllPosLits(getArgs());
    }

    public static boolean isAllPosLits(Iterable<Exp> args) {
        for (Exp arg : args) {
            if (!arg.isPosLit()) {
                return false;
            }
        }
        return true;
    }

    public static boolean isAllPosLits(Exp[] args) {
        for (Exp arg : args) {
            if (!arg.isPosLit()) {
                return false;
            }
        }
        return true;
    }

    public boolean isAllLits() {
        return isAllLits(argIt());
    }

    public boolean isAllConstants() {
        return isAllConstants(argIt());
    }

    public boolean isAllComplex() {
        return isAllComplex(argIt());
    }

    public String getContentModel() {
        return getContentModel(argIt());
    }

    public static String getContentModel(Iterable<Exp> args) {
        int constantArgCount = 0;
        int litArgCount = 0;
        int complexArgCount = 0;
        for (Exp arg : args) {
            if (arg.isConstant()) {
                constantArgCount++;
            } else if (arg.isLit()) {
                litArgCount++;
            } else if (arg.isComplex()) {
                complexArgCount++;
            } else {
                throw new IllegalStateException();
            }
        }
        Ser a = new Ser();
        if (constantArgCount > 0) {
            a.append("C");
            a.append(constantArgCount);
        }
        if (litArgCount > 0) {
            a.append("L");
            a.append(litArgCount);
        }
        if (complexArgCount > 0) {
            a.append("X");
            a.append(complexArgCount);
        }
        return a.toString().trim();
    }

    public static boolean isAllConstants(Iterable<Exp> args) {
        int constantArgCount = 0;
        for (Exp arg : args) {
            if (!arg.isConstant()) {
                return false;
            } else {
                constantArgCount++;
            }
        }
        return constantArgCount > 0;
    }

    public static boolean isAllLits(Iterable<? extends Exp> args) {
        for (Exp arg : args) {
            if (!arg.isLit()) {
                return false;
            }
        }
        return true;
    }

    public static boolean isAllLits(Exp[] args) {
        for (Exp arg : args) {
            if (!arg.isLit()) {
                return false;
            }
        }
        return true;
    }

    public static boolean isAllComplex(Iterable<Exp> args) {
        int complexArgCount = 0;
        for (Exp arg : args) {
            if (!arg.isComplex()) {
                return false;
            } else {
                complexArgCount++;
            }
        }
        return complexArgCount > 0;
    }

    public static boolean isAllComplex(Exp[] args) {
        int complexArgCount = 0;
        for (Exp arg : args) {
            if (!arg.isComplex()) {
                return false;
            } else {
                complexArgCount++;
            }
        }
        return complexArgCount > 0;
    }

    public boolean isYrSerMdl() {

        if (!isAllPosLits()) return false;
        if (getArg().size() != 3) return false;


        int yrCount = 0;
        int serCount = 0;
        int mdlCount = 0;

        for (Exp arg : getArgs()) {
            String prefix = arg.asVar().getPrefixCode();
            if (prefix.equals("YR")) yrCount++;
            if (prefix.equals("SER")) serCount++;
            if (prefix.equals("MDL")) mdlCount++;
        }

        return yrCount == 1 && serCount == 1 && mdlCount == 1;


    }

    public boolean isEveryArgAnAndYrSerMdl() {
        for (Exp arg : getArgs()) {
            if (!arg.isAndYrSerMdl()) {
                return false;
            }
        }
        return true;
    }

    public boolean isAndYrSerMdl() {
        return isAnd() && isYrSerMdl();
    }

    public boolean isOrOfAndYrSerMdl() {

        return isOr() && isEveryArgAnAndYrSerMdl();
    }

    public boolean isImpish() {
        return (isOr() || isImp() || isRmp()) && (getArgCount() == 2);
    }

    public boolean isImpliesOrOfAndYrSerMdl() {
        if (!isImpish()) return false;

        if (getArg1().isOrOfAndYrSerMdl()) return true;
        if (getArg2().isOrOfAndYrSerMdl()) return true;

        return false;

    }

    public Exp getFirstPosLitOfType(String prefix) {
        for (Exp arg : getArgs()) {
            if (arg.isPosLitOfType(prefix)) {
                return arg;
            }
        }
        return null;
    }


    public Exp getFirstPosLitOfTypeYr() {
        return getFirstPosLitOfType("YR");
    }

    public Exp getFirstPosLitOfTypeSer() {
        return getFirstPosLitOfType("SER");
    }

    public Exp getFirstPosLitOfTypeMdl() {
        return getFirstPosLitOfType("MDL");
    }


    public boolean isModCore() {
        return Mod.CORE.is(this);
    }

    public boolean isModTrim() {
        return Mod.TRIM.is(this);
    }

    public boolean isModColor() {
        return Mod.COLOR.is(this);
    }

    public boolean isModAccessory() {
        return Mod.ACCESSORY.is(this);
    }

    public boolean isModTrimLocal() {
        return Mod.TRIM.isLocal(this);
    }

    public boolean isModColorLocal() {
        return Mod.COLOR.isLocal(this);
    }

    public boolean isModAccessoryLocal() {
        return Mod.ACCESSORY.isLocal(this);
    }

    public boolean isArgsNnf() {
        for (Exp arg : getArgs()) {
            if (!arg.isNnf()) {
                return false;
            }
        }
        return true;
    }


    public boolean isArgsBnf() {
        for (Exp arg : getArgs()) {
            if (!arg.isBnf()) {
                return false;
            }
        }
        return true;
    }

    public boolean isNnf() {
        if (isLit()) {
            return true;
        } else if (isAnd() || isOr()) {
            return isArgsNnf();
        } else {
            return false;
        }
    }

    public boolean isBnf() {
        if (isLit()) {
            return true;
        } else if (isAnd() || isOr()) {
            return isArgsBnf();
        } else if (isNegComplex()) {
            return getPos().isBnf();
        } else {
            return false;
        }
    }


    public MacroType getMacroType() {
        return getPosOp().getMacroType();
    }


    public int xorCount() {
        if (isConstant() || isLit()) return 0;
        int xc = 0;
        if (isXor()) {
            xc++;
        }

        for (Exp arg : getArgs()) {
            xc += arg.xorCount();
        }

        return xc;

    }


    public int getVarCount() {
        if (isConstant()) return 0;
        if (isLit()) return 1;
        else return getVars().size();
    }

    public boolean isVv() {
        if (isConstant()) return false;
        if (isLit()) return false;
        if (isNot()) return getPos().isVv();

        assert isPosComplex();
        if (size() != 2) return false;

        if (!arg1().isLit()) return false;
        if (!arg2().isLit()) return false;

        if (getVarCount() == 1) {
            assert isDontCareOr();
            return false;
        }
        assert getVarCount() == 2;

        return true;


    }

    public boolean isVVPlus() {
        return getVarCount() > 2;
    }

    public boolean isVVPlusWithSeriesAndYear() {
        if (isXorOrContainsXor()) return false;
        return isVVPlus() && containsSeriesVar() && containsYearVar();
    }

    public boolean isVVPlusWithSeriesAndModel() {
        if (isXorOrContainsXor()) return false;
        return isVVPlus() && containsSeriesVar() && containsModelVar();
    }

    /**
     * @return true if this._vars.containsAll(that._vars)
     */
    public boolean vvpSubsumesVV(Exp vv) {
        assert vv.isVv();
        assert isVVPlus() : getClass() + this.toString();

        VarSet vars = vv.getVars();
        IntIterator it = vars.intIterator();

        int varId1 = it.next();
        int varId2 = it.next();

        return containsVarId(varId1) && containsVarId(varId2);
    }

    public boolean allArgsAreModelVars() {
        return allArgsAre(Mod.PREFIX_MDL);
    }

    public boolean allArgsAre(String prefix) {
        if (isConstant() || isLit()) return false;
        for (Exp arg : getArgs()) {
            if (!arg.isPosLit()) return false;
            String pArg = arg.asVar().getPrefix();
            if (!pArg.equals(prefix)) return false;
        }
        return true;
    }

    public Exp requiresOrToConflicts(ImmutableSet<Exp> allModels) {
        Space space = _space;

        if (isRequiresOr()) {
            Var var;
            Or or;
            if (isImp()) {
                var = getArg1().asVar();
                or = getArg2().asOr();
            } else if (isRmp()) {
                var = getArg2().asVar();
                or = getArg1().asOr();
            } else {
                throw new IllegalStateException();
            }

            if (or.allArgsAreModelVars()) {

                ImmutableSet<Exp> argModels = ImmutableSet.copyOf(or.getArgs());
                Sets.SetView<Exp> diff = Sets.difference(allModels, argModels);

                ArgBuilder conflicts = new ArgBuilder(space, Op.And);
                for (Exp model : diff) {
                    Var mdlVar = model.asVar();
                    Exp conflict = space.mkBinaryNand(var.pLit(), mdlVar.pLit());
                    conflicts.addExp(conflict);
                }
                return conflicts.mk();

            } else {
                return this;
            }


        } else {
            return this;
        }
    }


    public boolean isRequiresOr() {
        Exp a1 = getArg1();
        Exp a2 = getArg2();

        if (isImp() && a1.isPosLit() && a2.isOr()) {
            return true;
        } else if (isRmp() && a2.isPosLit() && a1.isOr()) {
            return true;
        }
        return false;
    }

    public boolean isXor(String prefix) {
        if (!isXor()) return false;

        for (Exp exp : args()) {
            if (!exp.isVarWithPrefix(prefix)) {
                return false;
            }
        }

        return true;
    }

    public boolean isSeriesXor() {
        if (!isXor()) return false;

        for (Exp exp : args()) {
            if (!exp.isSeriesVar()) {
                return false;
            }
        }

        return true;
    }

    public boolean isYearXor() {
        if (!isXor()) return false;

        for (Exp exp : args()) {
            if (!exp.isYearVar()) {
                return false;
            }
        }

        return true;
    }

    public boolean isInteriorColorXor() {
        if (!isXor()) return false;

        for (Exp exp : args()) {
            if (!exp.isIColVar()) {
                return false;
            }
        }

        return true;
    }

    public boolean isExteriorColorXor() {
        if (!isXor()) return false;

        for (Exp exp : args()) {
            if (!exp.isXColVar()) {
                return false;
            }
        }

        return true;
    }

    public boolean isModelXor() {
        if (!isXor()) return false;

        for (Exp exp : args()) {
            if (!exp.isModelVar()) {
                return false;
            }
        }

        return true;
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

    public boolean isColorRelated() {
        String ICOL = Mod.PREFIX_ICOL + "_";
        String XCOL = Mod.PREFIX_XCOL + "_";
        String sss = toString();
        return sss.contains(ICOL) || sss.contains(XCOL);
    }

    public boolean isFlat() {
        if (isConstant()) return true;
        if (isLit()) return true;
        for (Exp e : getArgs()) {
            if (!e.isLit()) {
                return false;
            }
        }
        return true;
    }

    public boolean isNestedOr() {
        if (!isOr()) return false;
        for (Exp e : argIt()) {
            if (e.isOr()) {
                return true;
            }
        }
        return false;
    }

    public boolean isNestedAnd() {
        if (!isAnd()) return false;
        for (Exp e : getArgs()) {
            if (e.isAnd()) {
                return true;
            }
        }
        return false;
    }

    public boolean isNestedNot() {
        if (!isNot()) return false;
        return getArg().isNot();
    }

    public boolean isTwoVarAnd() {
        return isAnd() && isVv() && getArg1().isPosLit() && getArg2().isPosLit();
    }

    public boolean isYearVar() {
        return false;
    }

    public boolean isModelVar() {
        return isPosLit() && getVr().isModelVar();
    }

    public boolean isIColVar() {
        return false;
    }

    public boolean isXColVar() {
        return false;
    }

    public boolean isYearLit() {
        return isLit() && getVr().isYearVar();
    }

    public boolean isYearPosLit() {
        return isPosLit() && getVr().isYearVar();
    }

    public boolean isSeriesPosLit() {
        return isPosLit() && getVr().isSeriesVar();
    }

    public boolean isModelPosLit() {
        return isPosLit() && getVr().isModelVar();
    }

    public boolean isModelNegLit() {
        return isNegLit() && getVr().isModelVar();
    }

    public boolean isAcyNegLit() {
        return isNegLit() && getVr().isAcyVar();
    }

    public boolean isSeriesYearAnd() {
        if (!isAnd()) return false;
        if (argCount() != 2) return false;

        int seriesPosLitCount = 0;
        int yearPosLitCount = 0;
        for (Exp arg : argIt()) {
            if (arg.isSeriesPosLit()) seriesPosLitCount++;
            if (arg.isYearPosLit()) yearPosLitCount++;
        }

        return seriesPosLitCount == 1 && yearPosLitCount == 1;

    }

    public boolean isSeriesYearAndPlus() {
        if (!isAnd()) return false;
//            if (argCount() != 2) return false;

        int seriesPosLitCount = 0;
        int yearPosLitCount = 0;
        for (Exp arg : argIt()) {
            if (arg.isSeriesPosLit()) seriesPosLitCount++;
            if (arg.isYearPosLit()) yearPosLitCount++;
        }

        return seriesPosLitCount == 1 && yearPosLitCount == 1;

    }

    public boolean isModelYearAnd() {
        if (!isAnd()) return false;
        if (argCount() != 2) return false;

        int modelPosLitCount = 0;
        int yearPosLitCount = 0;
        for (Exp arg : argIt()) {
            if (arg.isModelPosLit()) modelPosLitCount++;
            if (arg.isYearPosLit()) yearPosLitCount++;
        }

        return modelPosLitCount == 1 && yearPosLitCount == 1;

    }

    public TreeSet<String> clauseToSortedSet() {
        assert isClause();
        TreeSet<String> s = new TreeSet<String>();
        if (isLit()) {
            s.add(toString());
        } else if (isOr()) {
            for (Exp arg : getArgs()) {
                assert arg.isLit();
                s.add(arg.toString());
            }
        } else {
            throw new IllegalStateException();
        }
        return s;
    }


    public boolean isVarDisjoint(@NotNull VarSet vars) {
        return !anyVarOverlap(vars);
    }

    public boolean isVarDisjoint(Cube ctx) {
        return !anyVarOverlap(ctx.getVars());
    }

    public boolean isVarDisjoint(@NotNull Exp ctx) {
        return !anyVarOverlap(ctx);
    }

    public boolean anyVarOverlap(@NotNull VarSet vars) {
        if (isConstant()) return false;
        if (isLit()) return vars.containsVar(this);
        return vars.anyVarOverlap(getVars());
    }

    public boolean anyVarOverlap(@NotNull Exp that) {
        if (this.isConstant() || that.isConstant()) return false;
        if (isLit() && that.isLit()) return this.getVr() == that.getVr();

        if (this.isLit()) return that.containsVar(this);
        if (that.isLit()) return this.containsVar(that);

        return that.anyVarOverlap(getVars());
    }

    public boolean anyVarOverlap(@NotNull Cube cube) {
        return anyVarOverlap(cube.getVars());
    }


    @Nonnull
    final public Exp conditionVV(String vv) {
        Exp exp = _space.parseExp(vv);
        if (!exp.isVv()) {
            throw new IllegalArgumentException();
        }
        return conditionVV(exp);
    }

    @Nonnull
    public Exp conditionVV(Exp vv) {
        return conditionVV(vv, false);
    }

    @Nonnull
    public Exp conditionVV(Exp vv, boolean keepXors) {

        if (!vv.isNnf()) {
            vv = vv.toNnf(keepXors);
        }

        if (!vv.isOr()) {
            throw new IllegalArgumentException(vv.toString());
        }
        return this;
    }

    public Exp conditionVVs(List<Exp> vvs) {
        return conditionVVs(vvs, false);
    }

    public Exp conditionVVs(List<Exp> vvs, boolean keepXors) {
        Exp nnf = toNnf(keepXors);
        for (Exp vv : vvs) {

            nnf = nnf.conditionVV(vv);
        }
        return nnf;
    }


    public Iterator<Exp> argIter() {
        return It.INSTANCE.emptyIter();
    }

    public Iterator<Exp> argIteratorFlipped() {
        final Iterator<Exp> it = argIter();
        return new UnmodifiableIterator<Exp>() {
            @Override
            public boolean hasNext() {
                return it.hasNext();
            }

            @Override
            public Exp next() {
                return it.next().flip();
            }
        };
    }


    public boolean isVarWithPrefix(String prefix) {
        return isPosLit() && getVr().getPrefixCode().equals(prefix);
    }

    public boolean isSeriesVar() {
        return false;
    }

    public boolean isSeriesLiteral() {
        return isLit() && getVr().isSeriesVar();
    }

    public boolean isModelLiteral() {
        boolean lit = isLit();
        Var var = getVr();
        String prefixCode = var.getPrefixCode();

        if (prefixCode == null) {
            return false;
        }

        boolean mdl = prefixCode.equals("MDL");
        return lit && mdl;
    }

    public boolean containsSeriesVar() {
        VarSet vars = getVars();
        for (Var var : vars.varIt()) {
            if (var.isSeriesVar()) {
                return true;
            }
        }
        return false;
    }

    public boolean containsSeriesPosLit() {
        for (Exp arg : args()) {
            if (arg.isSeriesPosLit()) return true;
        }
        return false;
    }

    public boolean containsModelPosLit() {
        for (Exp arg : args()) {
            if (arg.isModelPosLit()) return true;
        }
        return false;
    }

    public Exp removeSeriesPosLitFromAnd() {
        ArrayList<Exp> a = new ArrayList<Exp>();
        for (Exp arg : args()) {
            if (arg.isSeriesPosLit()) continue;
            a.add(arg);
        }
        if (isAnd()) return mkAnd(a);
        else if (isOr()) return mkOr(a);
        else throw new IllegalStateException();
    }

    public Exp replaceSeriesWithModels(Multimap<SeriesYear, Var> seriesYearToModels) {
        if (isConstant()) return this;
        if (isLit()) return this;
        if (isSeriesYearAndPlus()) {
            return replaceSeriesWithModels(this, seriesYearToModels);
        }
        Op op = getOp();

        if (op.isNot()) {
            Exp newArg = getArg().replaceSeriesWithModels(seriesYearToModels);
            return newArg.flip();
        } else {
            ArgBuilder b = new ArgBuilder(_space, op);
            for (Exp arg : argIt()) {
                b.addExp(arg.replaceSeriesWithModels(seriesYearToModels));
            }
            return b.mk();
        }


    }

    public static Exp replaceSeriesWithModels(Exp seriesYearAnd, Multimap<SeriesYear, Var> seriesYearToModels) {
        assert seriesYearAnd.isSeriesYearAndPlus();

        Space space = seriesYearAnd._space;
        Exp seriesLit = seriesYearAnd.getFirstPosLitOfTypeSer();
        Exp yearLit = seriesYearAnd.getFirstPosLitOfTypeYr();

        Var seriesVar = seriesLit.getVr();
        Var yearVar = yearLit.getVr();


        //get everything else
        ArrayList<Exp> otherArgs = new ArrayList<Exp>();
        for (Exp otherArg : seriesYearAnd.argIt()) {
            if (otherArg == seriesLit) continue;
            if (otherArg == yearLit) continue;
            otherArgs.add(otherArg);
        }

        SeriesYear seriesYear = new SeriesYear(seriesVar, yearVar);
        Collection<Var> modelsCol = seriesYearToModels.get(seriesYear);
        ArrayList<Var> models = new ArrayList<Var>(modelsCol);

        ArgBuilder bOr = new ArgBuilder(space, Op.Or);
        for (Var model : models) {
            bOr.addExp(model.mkPosLit());
        }
        Exp modelsOr = bOr.mk();
        if (otherArgs.isEmpty()) {
            return space.mkAnd(yearLit, modelsOr);
        } else {

            ArgBuilder bAnd = new ArgBuilder(space, Op.And);
            for (Exp otherArg : otherArgs) {
                bAnd.addExp(otherArg);


            }

            bAnd.addExp(yearLit);
            bAnd.addExp(modelsOr);
            bAnd.addExpIt(otherArgs);
            return bAnd.mk();
        }

    }

    public boolean containsYearVar() {
        VarSet vars = getVars();
        Space space = _space;
        for (Var var : getVars()) {
            if (var.isYear()) {
                return true;
            }
        }
        return false;
    }

    public boolean containsSeriesAndYearVar() {
        return containsSeriesVar() && containsYearVar();
    }

    public boolean containsModelVar() {
        for (Var var : getVars()) {
            if (var.isModelVar()) {
                return true;
            }
        }
        return false;
    }


    public boolean isSeriesModelVV() {
        return isVv() && containsSeriesVar() && containsModelVar();
    }

    public Var getSeriesVar() {
        for (Var var : getVars()) {
            if (var.isSeriesVar()) {
                return var;
            }
        }
        return null;
    }

    public Var getYearVar() {
        for (Var var : getVars()) {
            if (var.isYearVar()) {
                return var;
            }
        }
        return null;
    }

    public Iterator<Var> varIterator() {
        return getVars().iterator();
    }

    public Iterable<Var> varIt() {
        return new Iterable<Var>() {
            @Override
            public Iterator<Var> iterator() {
                return varIterator();
            }
        };
    }

    public Var getModelVar() {
        for (Var var : varIt()) {
            if (var.isModel()) {
                return var;
            }
        }
        return null;
    }


    /**
     * iff(var1,and(var2,var3))
     */
    public boolean isAndVarIff() {
        if (!isIff()) return false;
        Exp a1 = getArg1();
        Exp a2 = getArg2();

        if (a1.isPosLit() && a2.isAnd() && a2.isVv()) {
            return a2.getArg1().isPosLit() && a2.getArg2().isPosLit();
        } else if (a2.isPosLit() && a1.isAnd() && a1.isVv()) {
            return a1.getArg1().isPosLit() && a1.getArg2().isPosLit();
        } else {
            return false;
        }

    }

    public boolean isAndVarIffFor(Exp twoVarAnd) {
        if (isAndVarIff()) {
            Exp and = andVarIff_getAnd();
            return and.equals(twoVarAnd);
        } else {
            return false;
        }
    }

    public Exp andVarIff_getVar() {
        checkState(this.isAndVarIff());
        Exp a1 = getArg1();
        Exp a2 = getArg2();

        if (a1.isPosLit() && a2.isAnd()) {
            return a1;
        } else if (a2.isPosLit() && a1.isAnd()) {
            return a2;
        } else {
            throw new IllegalArgumentException();
        }
    }

    public Exp andVarIff_getAnd() {
        checkState(this.isAndVarIff());
        Exp a1 = getArg1();
        Exp a2 = getArg2();

        if (a1.isPosLit() && a2.isAnd()) {
            return a2;
        } else if (a2.isPosLit() && a1.isAnd()) {
            return a1;
        } else {
            throw new IllegalArgumentException();
        }
    }

    public int getVarId() {
        throw new UnsupportedOperationException();
    }


    public boolean anyVarIntersection(Iterator<Exp> other) {
        throw new UnsupportedOperationException();
    }


    public Exp pushNotsIn() {
        return this;
    }

    final public Exp get(int index) {
        return getArg(index);
    }

    public Boolean containsLit(Exp lit) {
        return false;
    }

    public boolean containsArg(Exp arg) {
        return false;
    }

    public Exp toNnf(boolean keepXors) {
        Exp bnf = toBnf(keepXors);
        return bnf.transform(Transformer.BNF_TO_NNF);
    }

    public Exp toBnf(boolean keepXors) {
        if (keepXors) {
            return transform(Transformer.BNF_KEEP_XORS);
        } else {
            return transform(Transformer.BNF);
        }
    }

    public Exp toBnfKeepXors() {
        return transform(Transformer.BNF_KEEP_XORS);
    }

    final public boolean anyNestingDeep(Exp parent) {
        if (isNested(parent)) {
            return true;
        }
        if (isComplex()) {
            for (Exp arg : argIt()) {
                if (arg.anyNestingDeep(this)) {
                    return true;
                }
            }
            return false;
        } else {
            return false;
        }
    }

    final public boolean isNested(Exp parent) {
        if (parent == null) return false;
        assert !parent.isLeaf();

        if (parent.isAnd() && isAnd()) {
            return true;
        }

        if (parent.isOr() && isOr()) {
            return true;
        }

        if (parent.isDOr() && isDOr()) {
            return true;
        }

        return false;
    }

    public int getSpaceId() {
        return _space.getSpaceId();
    }


    public String getTagName() {
        return getSimpleName().toLowerCase();
    }


    public String startTag(Ser a, int depth) {

        int varCount = getVarCount();
        int arity = getArgCount();

        long satCount = getSatCount();
        int spaceId = getSpaceId();

        a.indent(depth);
        a.ap('<');
        a.ap(getTagName());
        a.ap(' ');


        a.ap(" spaceId='" + spaceId + "'");
        a.ap(" expId='" + getExpId() + "'");
        a.ap(" arity='" + arity + "'");
        a.ap(" varCount='" + varCount + "'");
        a.ap(" satCount='" + satCount + "'");

        a.ap('>');
        a.newLine();

        return a.toString();


    }

    public void endTag(Ser a, int depth) {
        a.indent(depth);
        a.ap('<');
        a.ap('/');
        a.ap(getTagName());
        a.ap('>');
        a.newLine();
    }

    public String toXml() {
        Ser a = new Ser();
        toXml(a, 0);
        return a.toString();
    }

    public String toXml(int depth) {
        Ser a = new Ser();
        toXml(a, depth);
        return a.toString();
    }

    public void toXml(Ser a, int depth) {
        if (isComplex()) {
            startTag(a, depth);
            for (Exp arg : argIt()) {
                arg.toXml(a, depth + 1);
            }
            endTag(a, depth);
        } else {
            String lit = toString();
            a.prindent(depth, lit);
        }
    }


    public Set<Cube> computeCubesNoVarSet() {
        throw new UnsupportedOperationException(getClass().getName());
    }

    public void checkChildCounts(ChildCounts childCounts) {
        //do nothing
    }

    final public boolean contains(Object o) {
        return containsArg((Exp) o);
    }

    public Exp mkConstantFalse() {
        return _space.mkConstantFalse();
    }

    public Exp mkConstantTrue() {
        return _space.mkConstantTrue();
    }

    public static int twoToThePowerOf(int power) {
        return (int) Math.pow(2, power);
    }

    public static long twoToThePowerOfLong(int power) {
        return (long) Math.pow(2, power);
    }

    public static int computeDcPermCount(int dcCount) {
        return twoToThePowerOf(dcCount);
        //        return com.google.common.primitives.Ints.checkedCast(permCountLong);
    }

    public static long computeDcPermCountLong(int dcCount) {
        return twoToThePowerOfLong(dcCount);
        //        return com.google.common.primitives.Ints.checkedCast(permCountLong);
    }

    public boolean isSat() {
        throw new UnsupportedOperationException(this.getSimpleName());
    }

    public Bit getValue(String varCode) {
        Var var = _space.getVar(varCode);
        return getValue(var);
    }


    public Dnnf createCsp() {
        return new Dnnf(this);
    }

//    public VarSet getOpenCareVars() {
//        Exp reduced = reduce();
//        return reduced.get_vars();
//    }

    public Exp reduce() {
        DynCube bb = getBB();
        return this.condition(bb);
    }


    public DynCube getBB() {
        return computeBB();
    }

    @Nonnull
    @NotNull
    public static Exp compileDnnf(String clob) {
        return Csp.compileDnnf(clob);
    }

    public static Exp parseTinyDnnf(String tinyDnnfClob) {
        return Parser.parseTinyDnnf(tinyDnnfClob);
    }


    public void forEachHead(NodeHandler h) {
        if (!h.visited(this)) {
            h.onHead(this);
            h.markAsVisited(this);
            if (isComplex()) {
                for (Exp arg : argIt()) {
                    arg.forEachHead(h);
                }
            }
        }

    }

    public void printHead() {
        printHead(0);

    }

    public void printInfo(int depth) {


        //        NodeInfo nodeInfo = computeNodeInfo();
        //        nodeInfo.print(depth);
    }


    public void printHead(int depth) {
//        prindent(depth, "op1:         " + getOp1());
//        prindent(depth, "satCount:   " + getSatCount());
//        prindent(depth, "careVars:   " + get_vars().size() + ": " + get_vars());
//        System.err.println();
    }

    public void printInfo() {
        System.err.println(toXml());
    }


    public boolean isSolved() {
        return isConstantTrue() || isLit() || isCube();
    }


    public boolean isClause() {
        return (isOr() && isAllLits()) || isLit();
    }

    public boolean isClauseOrLit() {
        return isLit() || isClause();
    }

    public int getLongestPathToLeaf() {

        if (isLeaf()) {
            return 0;
        }

        int longest = -1;
        for (Exp arg : getArgs()) {

            int p = arg.getLongestPathToLeaf();
            if (p > longest) {
                longest = p;
            }
        }

        return longest + 1;
    }


    public void nodeInfo() {
        NodeInfo nodeInfo = new NodeInfo();
        forEachHead(nodeInfo);
//        nodeInfo.print(0);
    }

    public String serializeModels() {
        Ser a = new Ser();
        serializeModels(a);
        return a.toString();
    }

    public static void serializeModels(Iterable<Cube> cubes, Ser a) {
        for (Cube cube : cubes) {
            cube.serialize(a);
        }
    }

    public void serializeModels(Ser a) {
        Set<Cube> cubes = getCubesSmooth();
        serializeModels(cubes, a);
    }

    public static void printCubes(Iterable<Cube> cubes) {
        assert cubes != null;
        Ser a = new Ser();


        for (Cube cube : cubes) {
            a.ap(cube.serialize(10));
            a.newLine();
        }
        System.err.println(a);
    }

    public static void printCubesTrueVars(Iterable<Cube> cubes) {
        System.err.println("----");
        for (Cube cube : cubes) {
            System.err.println(cube.serializeTrueVars());
        }
        System.err.println("====");
    }

    public static void printCubes(Iterable<Cube> cubes, boolean trueVarsOnly) {
        System.err.println("----");
        for (Cube cube : cubes) {
            String line;
            if (trueVarsOnly) {
                line = cube.serializeTrueVars();
//                VarSet trueVars = cube.getTrueVars();
//                line = ParseUtil.serializeCodes(trueVars);
            } else {
                line = cube.serialize();
            }
            System.err.println(line);
        }
        System.err.println("====");
    }


    public static void printModels(Iterable<Cube> cubes) {
        printCubesTrueVars(cubes);
    }

    public void printCubes() {
        assert isDnnf() && checkDnnf();
        Set<Cube> cubes = getCubesSmooth();
        printCubes(cubes);
    }

    public void printCubesTrueVarsOnly() {
        Set<Cube> cubes = getCubesSmooth();
        printModels(cubes);
    }

    public Set<Cube> getCubes() {
        return getCubesSmooth();
    }

    public Set<Cube> getCubesSmooth() {
        if (isConstant()) {
            throw new UnsupportedOperationException(getSimpleName());
        }
        return computeCubesSmooth();

    }

    //only works c smooth nodes
    public long getSatCount() {
        if (satCount == null) {
            satCount = computeSatCount();
        }
//        if (satCount < 0) throw new IllegalStateException();
        return satCount;
    }

    public long computeSatCount() {
        throw new UnsupportedOperationException(getClass().getName());
    }

    public long computeSatCount1(Lit lit) {
        throw new UnsupportedOperationException(getClass().getName());
    }

    public long computeSatCount2(Lit lit) {
        throw new UnsupportedOperationException(getClass().getName());
    }

    public int getCubeCount() {
        throw new UnsupportedOperationException(getClass().getName());
    }

    public Set<Var> varsSet() {
        VarSet vars = getVars();
        throw new UnsupportedOperationException();
    }


    public Exp getNext() {
        return next;
    }

    public void putNext(Exp next) {
        this.next = next;
    }

    public static void serializeArgsSorted(Ser a, Exp[] args) {
        List<Exp> copy = ExpComparator.sortCopyArray(args);
        for (int i = 0; i < copy.size(); i++) {
            Exp arg = copy.get(i);
            arg.serialize(a);
            if (i != copy.size() - 1) {
                a.argSep();
            }
        }
    }


    public static void serializeArgsUnsorted(Ser a, Exp[] args) {
        for (int i = 0; i < args.length; i++) {
            Exp arg = args[i];
            arg.serialize(a);
            if (i != args.length - 1) {
                a.argSep();
            }
        }
    }

    public static void serializeArgs(Ser a, Iterable<Exp> args) {
        List<Exp> copy = ExpComparator.sortCopyIt(args);
        for (int i = 0; i < copy.size(); i++) {
            Exp arg = copy.get(i);
            arg.serialize(a);
            if (i != copy.size() - 1) {
                a.argSep();
            }
        }
    }

    public static void serializeArgList(Ser a, Iterable<Exp> args) {
        a.append(LPAREN);
        serializeArgs(a, args);
        a.append(RPAREN);
    }

//    public static boolean containsArg(Exp[] args, int argCount, int expId) {
//        for (int i = 0; i < argCount; i++) {
//            Exp arg = args[i];
//            if (arg.getExpId() == expId) {
//                return true;
//            }
//        }
//
//        return false;
//    }

    public Exp copyToOtherSpace() {
        Space sourceSpace = _space;
        Set<String> varCodes = sourceSpace.getVarCodes();
        Space destSpace = new Space(varCodes);
        destSpace.initAllLits();
        return copyToOtherSpace(destSpace);
    }

    public Exp gc() {
        return copyToOtherSpace();
    }

    public Exp copyToOtherSpace(Space destSpace) {
        if (_space == destSpace) {
            return this;
        }
        throw new UnsupportedOperationException(getClass().getName());
    }

    public static Exp copyArgsExpToOtherSpace(Space destSpace, Op op, Iterable<Exp> args) {
        ArgBuilder b = new ArgBuilder(destSpace, op);
        for (Exp arg : args) {
            Exp copy = arg.copyToOtherSpace(destSpace);
            b.addExp(copy);
        }
        return b.mk();
    }

    public static VarSet copyVarSetToOtherSpace(Space destSpace, VarSet varSet) {
        if (varSet.getSpace() == destSpace) {
            for (Var var : varSet) {
                assert var.getSpace() == destSpace;
            }
            return varSet;
        }

        VarSetBuilder destVarSet = destSpace.newMutableVarSet();
        for (Var var : varSet) {
            String varCode = var.getVarCode();
            Var destVar = destSpace.getVar(varCode);
            destVarSet.addVar(destVar);
        }

        return destVarSet;
    }

    public int minCard() {
        return minTCard();
    }

    public int minTCard() {
        if (isPosLit()) {
            return 1;
        } else if (isNegLit()) {
            return 0;
        } else if (isConstantFalse()) {
            throw new IllegalStateException();
        } else if (isConstantTrue()) {
            throw new IllegalStateException();
        } else if (isDOr()) {
            int min = Integer.MAX_VALUE;
            for (Exp arg : argIt()) {
                int argMin = arg.minTCard();
                if (argMin < min) {
                    min = argMin;
                }
            }
            return min;
        } else if (isXor()) {
            return 1;
        } else if (isDAnd()) {
            int sum = 0;
            for (Exp arg : argIt()) {
                sum += arg.minTCard();
            }
            return sum;
        } else {
            throw new UnsupportedOperationException();
        }
    }

    public int minFCard() {
        if (isPosLit()) {
            return 0;
        } else if (isNegLit()) {
            return 1;
        } else if (isConstantFalse()) {
            throw new IllegalStateException();
        } else if (isConstantTrue()) {
            throw new IllegalStateException();
        } else if (isDOr()) {
            int min = Integer.MAX_VALUE;
            for (Exp arg : argIt()) {
                int argMin = arg.minFCard();
                if (argMin < min) {
                    min = argMin;
                }
            }
            return min;
        } else if (isXor()) {
            return getVarCount() - 1;
        } else if (isDAnd()) {
            int sum = 0;
            for (Exp arg : argIt()) {
                sum += arg.minFCard();
            }
            return sum;
        } else {
            throw new UnsupportedOperationException();
        }
    }

    public Exp minModels() {
        return minTModels();
    }

    private Exp minTModelsRough() {
        int mCard = minTCard();
        Space space = _space;
        if (isConstant() || isLit()) {
            return this;
        } else if (isDOr()) {
            VarSet parentVars = getVars();
            ArgBuilder b = new ArgBuilder(space, Op.DOr);
            for (Exp arg : argIt()) {
                int argMCard = arg.minTCard();
                if (argMCard == mCard) {
                    VarSet childVars = arg.getVars();
                    VarSet dcVars = parentVars.minus(childVars);
                    Exp nCube = space.mkNCube(dcVars);
                    Exp argMinTModels = arg.minTModels();
                    Exp aa = space.expFactory.mkDAnd(argMinTModels, nCube);
                    b.addExp(aa);
                }
            }
            return b.mk();
        } else if (isXor()) {
            return this;
        } else if (isDAnd()) {
            ArgBuilder b = new ArgBuilder(space, Op.DAnd);
            for (Exp arg : argIt()) {
                Exp argMinTModels = arg.minTModels();
                b.addExp(argMinTModels);
            }
            return b.mk();
        } else {
            throw new UnsupportedOperationException();
        }
    }

    private Exp minTModelsSmooth() {
        assert isSmooth();
        int mCard = minTCard();
        Space space = _space;
        if (isConstant() || isLit()) {
            return this;
        } else if (isDOr()) {
            ArgBuilder b = new ArgBuilder(space, Op.DOr);
            for (Exp arg : argIt()) {
                int argMCard = arg.minTCard();
                if (argMCard == mCard) {
                    Exp argMinTModels = arg.minTModels();
                    b.addExp(argMinTModels);
                }
            }
            return b.mk();
        } else if (isXor()) {
            return this;
        } else if (isDAnd()) {
            ArgBuilder b = new ArgBuilder(space, Op.DAnd);
            for (Exp arg : argIt()) {
                Exp argMinTModels = arg.minTModels();
                b.addExp(argMinTModels);
            }
            return b.mk();
        } else {
            throw new UnsupportedOperationException();
        }
    }

    private static final boolean ROUGH_MIN_MODELS = true;

    public Exp minFModels() {
        if (ROUGH_MIN_MODELS) {
            return minFModelsRough();
        } else {
            return minFModelsSmooth();
        }
    }

    public Exp minTModels() {
        if (ROUGH_MIN_MODELS) {
            return minTModelsRough();
        } else {
            return minTModelsSmooth();
        }
    }

    private Exp minFModelsRough() {
        int mCard = minFCard();
        Space space = _space;
        if (isConstant() || isLit()) {
            return this;
        } else if (isDOr()) {
            VarSet parentVars = getVars();
            ArgBuilder b = new ArgBuilder(space, Op.DOr);
            for (Exp arg : argIt()) {
                int argMCard = arg.minFCard();
                if (argMCard == mCard) {
                    VarSet childVars = arg.getVars();
                    VarSet dcVars = parentVars.minus(childVars);
                    Exp pCube = space.mkPCube(dcVars);
                    Exp argMinFModels = arg.minFModels();
                    Exp aa = space.expFactory.mkDAnd(argMinFModels, pCube);
                    b.addExp(aa);
                }
            }
            return b.mk();
        } else if (isXor()) {
            return this;
        } else if (isDAnd()) {
            ArgBuilder b = new ArgBuilder(space, Op.DAnd);
            for (Exp arg : argIt()) {
                Exp argMinModels = arg.minFModels();
                b.addExp(argMinModels);
            }
            return b.mk();
        } else {
            throw new UnsupportedOperationException();
        }
    }

    private Exp minFModelsSmooth() {
        assert isSmooth();
        int mCard = minFCard();
        Space space = _space;
        if (isConstant() || isLit()) {
            return this;
        } else if (isDOr()) {
            ArgBuilder b = new ArgBuilder(space, Op.DOr);
            for (Exp arg : argIt()) {
                int argMCard = arg.minFCard();
                if (argMCard == mCard) {
                    Exp argMinFModels = arg.minFModels();
                    b.addExp(argMinFModels);
                }
            }
            return b.mk();
        } else if (isXor()) {
            return this;
        } else if (isDAnd()) {
            ArgBuilder b = ef().argBuilder(Op.DAnd);
            for (Exp arg : argIt()) {
                Exp argMinModels = arg.minFModels();
                b.addExp(argMinModels);
            }
            return b.mk();
        } else {
            throw new UnsupportedOperationException();
        }
    }


    public Exp conditionOnAtVars() {
        Exp c = _space.getAtVarsAsCube();
        return condition(c.asCube());
    }


    public boolean isDnnf() {
        return false;
    }

    public Exp getFirstConjunctContaining(String varCode) {
        throw new UnsupportedOperationException();
    }

    public static void sort(List<? extends Exp> constraints) {
        Collections.sort(constraints, Exp.COMPARATOR_BY_ARITY);
    }

    final public boolean hasMsrpVars() {
        return getVars().containsMsrpVars();
    }

    final public boolean hasDealers() {
        return getVars().containsDealerVars();
    }


    public boolean isSmooth() {
        throw new UnsupportedOperationException(getClass().getName());
    }

    public Exp getSmooth() {
        throw new UnsupportedOperationException(this.getSimpleName());
    }

    public Exp stripSeriesVarsFromModelImpliesSeriesYears() {
        if (!isOr()) return this;

        int modelNegLitCount = 0;
        int seriesYearAndCount = 0;
        int otherCount = 0;

        for (Exp arg : argIt()) {
            if (arg.isModelNegLit()) {
                modelNegLitCount++;
            } else if (arg.isSeriesYearAnd()) {
                seriesYearAndCount++;
            } else {
                otherCount++;
            }
        }

        boolean isModelImpliesSeriesYears = modelNegLitCount == 1 && seriesYearAndCount > 0 && otherCount == 0;

        if (!isModelImpliesSeriesYears) {
            return this;
        }

//        System.err.println("Before: " + this);

        ArgBuilder b = new ArgBuilder(_space, Op.Or);
        for (Exp arg : argIt()) {
            if (arg.isModelNegLit()) {
                //leave as is
                b.addExp(arg);
            } else if (arg.isSeriesYearAnd()) {
                for (Exp lit : arg.argIt()) {
                    if (lit.isYearPosLit()) {
                        b.addExp(lit);
                    } else {
                        assert lit.isSeriesPosLit();
                    }
                }

            } else {
                throw new IllegalStateException();
            }
        }

        Exp after = b.mk();

//        System.err.println("After: " + after);
//        System.err.println();

        return after;

    }

    public Exp stripSeriesYearsFromAcyImpliesModelYears() {
        if (!isOr()) return this;

        int acyNegLitCount = 0;
        int seriesYearAndCount = 0;
        int modelYearAndCount = 0;
        int otherCount = 0;
        for (Exp arg : argIt()) {
            if (arg.isAcyNegLit()) {
                acyNegLitCount++;
            } else if (arg.isSeriesYearAnd()) {
                seriesYearAndCount++;
            } else if (arg.isModelYearAnd()) {
                modelYearAndCount++;
            } else {
                otherCount++;
            }
        }

        boolean isModelImpliesSeriesYears = acyNegLitCount == 1 && seriesYearAndCount > 0 && modelYearAndCount > 0 && otherCount == 0;

        if (!isModelImpliesSeriesYears) {
            return this;
        }

        System.err.println("Before: " + this);

        ArgBuilder b = new ArgBuilder(_space, Op.Or);
        for (Exp arg : argIt()) {
            if (arg.isAcyNegLit()) {
                //leave as is
                b.addExp(arg);
            } else if (arg.isSeriesYearAnd()) {
                //kill it
            } else if (arg.isModelYearAnd()) {
                //keep it
                b.addExp(arg);
            } else {
                throw new IllegalStateException();
            }
        }

        Exp after = b.mk();

        System.err.println("After: " + after);
        System.err.println();

        return after;

    }

    public Exp stripSeriesVarsFromYsmAnds() {
        if (isConstant()) return this;
        if (isLit()) return this;

        if (isNot()) {
            return _space.mkNot(getArg().stripSeriesVarsFromYsmAnds());
        }

        boolean y = false;
        boolean s = false;
        boolean m = false;

        for (Exp arg : argIt()) {
            if (arg.isYearPosLit()) y = true;
            if (arg.isSeriesPosLit()) s = true;
            if (arg.isModelPosLit()) m = true;
        }

        boolean ysm = y && s && m;

        Op op = this.getOp();
        ArgBuilder b = new ArgBuilder(_space, op);
        for (Exp arg : argIt()) {

            if (arg.isSeriesPosLit() && ysm) {
                //skip
            } else {
                b.addExp(arg.stripSeriesVarsFromYsmAnds());
            }


        }
        return b.mk();

    }

    /**
     * or(!MDL_2842 and(SER_venza YR_2014))
     */
    public boolean isModelImpliesSeriesYear() {
        if (isConstant()) return false;
        if (isLit()) return false;
        if (!isOr()) return false;

        int modelNegLitCount = 0;
        int seriesYearAndCount = 0;
        for (Exp arg : this.argIt()) {
            if (arg.isModelNegLit()) {
                modelNegLitCount++;
            }
            if (arg.isSeriesYearAnd()) {
                seriesYearAndCount++;
            }
        }

        return modelNegLitCount == 1 && seriesYearAndCount > 0;

    }

    public boolean isYsmAnd() {
        if (isConstant()) return false;
        if (isLit()) return false;
        if (!isAnd()) return false;

        for (Exp arg : argIt()) {
            if (!arg.isPosLit()) return false;
            Var var = arg.getVr();
            boolean ysm = var.isYear() || var.isSeries() || var.isModel();
            if (!ysm) return false;
        }


        assert isAnd();
        assert argCount() == 3;

        return true;
    }


    private static int computeCpd(Exp m, Exp n) {
        if (m.isDOr()) {
            return m.getPd();
        } else if (m.isAnd()) {
            int vProd = 1;
            for (Exp k : m.getArgs()) {
                if (k != n) {
                    vProd *= k.computeValue();
                }
            }
            return m.getPd() * vProd;
        } else {
            throw new IllegalStateException(m + "");
        }
    }

    public int getPd() {
        if (this.pd == null) {
            this.pd = computePd();
        }
        return this.pd;
    }

    private int computePd() {
        if (isRoot()) {
            return 1;
        } else {
            Iterable<Exp> parents = getParents();
            int cpdSum = 0;
            for (Exp m : parents) {
                cpdSum += computeCpd(m, this);
            }
            return cpdSum;
        }
    }

    public int computeValueAfterAsserting(Lit lit) {
        return lit.getPd();
    }

    public int computeValueAfterRetraction(Lit lit) {
        return lit.getPd() + lit.flip().getPd();
    }

    public int computeValueAfterFlip(Lit lit) {
        return computeValue() - lit.getPd() + lit.flip().getPd();
    }

    public int computeValueAfterRadioFlip(Var var1, Var var2) {
        assert var1 != var2;
        Lit lit1 = var1.mkPosLit();
        Lit lit2 = var2.mkPosLit();
        return computeValue() - lit1.getPd() + lit2.getPd();
    }

    public boolean isRoot() {
        return parents == null;
    }

    public void initParentsForArgs() {
        if (isConstant()) return;
        if (isLit()) return;
        assert isDAnd() || isDOr();
        for (Exp arg : getArgs()) {
            arg._addParent(this);
            arg.initParentsForArgs();
        }
    }

    private void _addParent(Exp parent) {
        if (parents == null) {
            parents = new HashSet<Exp>();
        }
        parents.add(parent);
    }

    public HashSet<Exp> getParents() {
        return parents;
    }

    public void clearPics() {
        _space.clearPics();
        clearPartialDerivatives();
    }

    public void clearPartialDerivatives() {
        pd = null;
        if (isLeaf()) {
            return;
        }
        for (Exp arg : getArgs()) {
            arg.clearPartialDerivatives();
        }
    }


    public static Iterable<Exp> extractComplex(Iterable<Exp> args) {
        ArrayList<Exp> aa = new ArrayList<Exp>();
        for (Exp a : args) {
            if (a.isComplex()) {
                aa.add(a);
            }
        }
        return aa;
    }

    public static Iterable<Lit> extractSimple(Iterable<Exp> args) {
        ArrayList<Lit> aa = new ArrayList<Lit>();
        for (Exp a : args) {
            if (a.isSimple()) {
                aa.add(a.asLit());
            }
        }
        return aa;
    }

    public static DynCube extractCube(Space space, Iterable<Exp> args) {
        return DynCube.create(space, args);
    }

    public ExpFactory ef() {
        return _space.getExpFactory();
    }


}

