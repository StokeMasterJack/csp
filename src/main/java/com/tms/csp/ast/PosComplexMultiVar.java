package com.tms.csp.ast;

import com.google.common.collect.Iterators;
import com.tms.csp.argBuilder.ArgBuilder;
import com.tms.csp.util.Bit;
import com.tms.csp.util.DynComplex;
import com.tms.csp.util.ints.IntIterator;
import com.tms.csp.util.varSets.VarSet;
import com.tms.csp.util.varSets.VarSetBuilder;
import org.jetbrains.annotations.NotNull;

import java.util.*;

import static com.google.common.base.Preconditions.checkNotNull;

public abstract class PosComplexMultiVar extends PosComplex {

    protected final Exp[] args;
    public final Exp[] _args;

    protected Exp neg;

    //cache
    protected VarSet _vars;

    private boolean isNew = true;


    protected PosComplexMultiVar(Space space, int expId, Exp[] fixedArgs) {
        super(space, expId);

        assert expId >= 0;
        assert fixedArgs != null;
        this.args = fixedArgs;
        this._args = args;

        for (Exp fixedArg : fixedArgs) {
            assert fixedArg != null;
            assert fixedArg.getSpace() == space;
        }

    }

//    protected PosComplex(Space space, int expId, ArgBuilder args) {
//        this(space, expId, args.toArray());
//    }


    /**
     * Creates new Complex expr of same type and sign
     */
    public Exp newComplex(Iterable<Exp> args) {
        PosOp op = getPosOp();
        Space space = getSpace();
        return space.mkPosComplex(op, args);
    }





    @Override
    public boolean isPosArgsExp() {
        return true;
    }


    @Override
    public int getLitArgCount() {
        int c = 0;
        for (Exp arg : argIt()) {
            if (arg.isLit()) {
                c++;
            }
        }
        return c;
    }

    @Override
    public List<Exp> getArgs() {
        return Arrays.asList(args);
    }


    @Override
    final public Exp getArg(int i) {
        return args[i];
    }

    @Override
    public int getArgCount() {
        return args.length;
    }

    public List<Exp> argsRest() {
        return subList(1, size() - 1);
    }

    public Iterator<Exp> iterator() {
        return Iterators.forArray(args);
    }

    @Override
    public Var getFirstVar() {
        for (Exp arg : args) {
            Var var = arg.getFirstVar();
            if (var != null) {
                return var;
            }
        }
        return null;
    }

    public void serialize(Ser a) {
        String token = getPosComplexOpToken(a);
        a.append(token);
        a.append(LPAREN);
//        Exp.serializeArgsUnsorted(a, args);
        Exp.serializeArgsSorted(a, args);
        a.append(RPAREN);
    }

    @Override
    public boolean containsVarId(int varId) {
        if (size() < 2) throw new IllegalStateException();
        return getVars().containsVarId(varId);
    }

    @Override
    public boolean anyVarOverlap(Exp exp) {
        if (exp == null || exp.isConstant()) return false;
        if (exp.isLit()) {
            return getVars().containsVar(exp.getVr());
        }
        if (exp.isComplex()) {
            VarSet otherVars = exp.getVars();
            return otherVars.anyVarOverlap(getVars());
        }

        throw new IllegalStateException();
    }


    @Override
    public boolean isOrContainsConstant() {
        for (Exp arg : args) {
            if (arg.isConstant()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Iterator<Exp> argIter() {
        return Iterators.forArray(args);
    }


    public void gcNeg() {
        if (neg != null) {
            neg = null;
        }
    }

    public static final Comparator<PosComplexMultiVar> COMPARATOR_BY_ARITY = new Comparator<PosComplexMultiVar>() {
        @Override
        public int compare(PosComplexMultiVar e1, PosComplexMultiVar e2) {
            checkNotNull(e1);
            checkNotNull(e2);
            Integer argCount1 = e1.getArgCount();
            Integer argCount2 = e2.getArgCount();
            return argCount1.compareTo(argCount2);
        }
    };

    public boolean isAllClauses() {
        for (Exp arg : getArgs()) {
            if (!arg.isClause()) {
                return false;
            }
        }
        return true;
    }


    public boolean isUnitDEAD() {
        return false;
    }

    public Exp getUnitDEAD() {
        return null;
    }

//    public boolean isFrontier() {
//        if (assignment == null) return false;
//        if (conflict != null) return false;
//        if (eval == null) return false;
//        if (eval.value != assignment.value) return false;
//        return true;
//    }
//
//    public boolean isLive() {
//        if (isFrontier()) {
//            return true;
//        } else {
//            for (Complex parent : parents) {
//                if (parent.isLive()) {
//                    return true;
//                }
//            }
//            return false;
//        }
//
//    }


    @Override
    public Exp simplify() {
        throw new UnsupportedOperationException();
//        EvalContext ctx = null;
//        return simplify(ctx);
    }


    public void notNew() {
        isNew = false;
    }

    @Override
    public boolean isNew() {
        return isNew;
    }

    @Override
    public boolean isPos() {
        return true;
    }
//
//    public Exp simplifyAfterVVPick(Exp vv) {
//        if (this.containsAll(vv)) {
//            boolean ch = false;
//            ArrayList<Exp> a = new ArrayList<Exp>();
//            for (Exp e : args) {
//                Exp s = e.simplifyAfterVVPick(vv);
//                if (s != e) {
//                    ch = true;
//                }
//                a.add(s);
//            }
//            if (ch) {
//                PosOp op1 = getPosOp();
//                if (op1.isAnd()) return space.mkAnd(a);
//                if (op1.isOr()) return space.mkOr(a);
//                if (op1.isXor()) return space.mkXor(a);
//                throw new IllegalStateException();
//            } else {
//                return this;
//            }
//
//        } else {
//            return this;
//        }
//    }


    protected VarSet computeVars() {
        VarSetBuilder b = _space.newMutableVarSet();
        for (Exp arg : args) {
            if (arg.isLit()) {
                Var var = arg.getVr();
                b.addVar(var);
            } else if (arg.isComplex()) {
                VarSet vars1 = arg.getVars();
                b.addVarSet(vars1);
            }
        }
        return b.immutable();
    }

    @NotNull
    public VarSet getVars() {
        if (this._vars == null) {
            _vars = computeVars();
        }
        return this._vars;
    }

    @Override
    public String getCode() {
        Op1 op1 = getOp().getOp1();
        return getSimpleName().toLowerCase();
    }

    /**
     * Returns true if the given iterator and this.iterator
     * return the same elements, formula the same order.
     *
     * @return true if values and this.iterator return the same elements,
     * formula the same order.
     */
    public boolean sameArgs(Iterator<? extends Exp> values) {
        for (Exp f : args) {
            if (!(values.hasNext() && f == values.next()))
                return false;
        }
        return !values.hasNext();
    }

    public boolean sameSize(int thatSize) {
        return size() == thatSize;
    }


    public boolean sameOp(Op op) {
        return getOp() == op;
    }

    public Boolean containsLit(Exp lit) {
        if (containsArg(lit)) return true;
        if (containsArg(lit.flip())) return false;
        return null;
    }


    public boolean containsArg(Exp thatArg) {
        for (Exp arg : argIt()) {
            if (arg == thatArg) return true;
        }
        return false;
    }

    public static enum LL {
        TT, TF, FT, FF;

        public static LL create(boolean L1, boolean L2) {
            if (L1 && L2) return TT;
            if (!L1 && !L2) return FF;
            if (L1) return TF;
            return FT;
        }

        public boolean tt() {
            return this == TT;
        }

        public boolean ff() {
            return this == FF;
        }

        public boolean tf() {
            return this == TF;
        }


        public boolean ft() {
            return this == FT;
        }
    }

    public LL containsVVArgs(Exp vv) {
        assert vv.isVv();
        Exp lit1 = vv.getArg1();
        Exp lit2 = vv.getArg2();
        return containsVVArgs(lit1, lit2);
    }

    public boolean containsLocalLitWithVar(int var1, int var2) {
        return containsLocalLitWithVar(var1) && containsLocalLitWithVar(var2);
    }

    public boolean containsLocalLits(Exp vv) {

        VarSet vars = vv.getVars();
        IntIterator it = vars.intIterator();

        int var1 = it.next();
        if (!containsLocalLitWithVar(var1)) return false;

        int var2 = it.next();
        if (!containsLocalLitWithVar(var2)) return false;

        return true;

    }

    public boolean containsLocalLitWithVar(int varId) {
        for (Exp arg : args) {
            if (arg.isLit() && arg.getVarId() == varId) {
                return true;
            }
        }
        return false;
    }

    /**
     * NN
     * YY
     * FF
     *
     * @return
     */
    public LL containsVVArgs(Exp lit1, Exp lit2) {
        Boolean L1 = containsLit(lit1);
        if (L1 == null) return null;
        Boolean L2 = containsLit(lit2);
        if (L2 == null) return null;
        return LL.create(L1, L2);
    }

    public boolean computeIsSat() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isSat() {
        return computeIsSat();
    }

    final public void serializeArgsTinyDnnf(Ser a) {
        Iterator<Exp> it = argIter();
        while (it.hasNext()) {
            Exp arg = it.next();
            int nodeId = arg.getExpId();
            a.append(nodeId);
            if (it.hasNext()) {
                a.argSep();
            }
        }
    }


    public Iterable<Exp> getSimpleArgs() {
        ArrayList<Exp> aa = new ArrayList<Exp>();
        for (Exp a : argIt()) {
            if (a.isSimple()) {
                aa.add(a);
            }
        }
        return aa;
    }

    public Iterable<Lit> getLits() {
        return Exp.extractSimple(argIt());
    }

    public DynCube getSimple() {
        return new DynCube(_space, getLits());
    }


    public DynComplex getComplex() {
        Iterable<Exp> complexArgs = getComplexArgs();
        return new DynComplex(_space, complexArgs);
    }

    public Iterable<Exp> getComplexArgs() {
        return Exp.extractComplex(argIt());
    }


    public List<Exp> subList(int fromIndex, int toIndex) {
        ArrayList<Exp> a = new ArrayList<Exp>((toIndex - fromIndex) + 1);
        for (int i = fromIndex; i < toIndex; i++) {
            a.add(args[i]);
        }
        return a;
    }


    final public Exp flip() {
        return getNeg();
    }

    @Override
    final public Exp getPos() {
        return this;
    }

    @Override
    public Exp getNeg() {
        if (this.neg == null) {
            this.neg = new Not(this, _space.getNodeCount());
            _space.addNode(this.neg);
        } else {
            this.neg.notNew();
        }
        return this.neg;
    }

    @Override
    public PosComplexMultiVar asPosComplex() {
        return this;
    }

    public Bit getValue(Var var) {

        if (!containsVar(var)) {
            return Bit.OPEN;  //d
        }

        Lit t = var.mkPosLit();
        Lit f = var.mkNegLit();
        boolean tSat = computeSat(t);
        boolean fSat = computeSat(f);
        if (tSat && !fSat) {
            return Bit.TRUE;
        } else if (!tSat && fSat) {
            return Bit.FALSE;
        } else if (!tSat && !fSat) {
            throw new IllegalStateException();
        } else {
            return Bit.OPEN;
        }
    }

    @Override
    public boolean hasFlip() {
        return neg != null;
    }


    public ArgBuilder flipArgs() {
        throw new UnsupportedOperationException();
    }

    public Exp createHardFlip() {
        throw new UnsupportedOperationException();
    }

    public boolean sameArgs(PosComplexMultiVar that) {
        return Arrays.equals(args, that.args);
    }

    public String getHashKey() {
        assert isDAnd() || isDOr();
        return buildHashKey();
    }

    public String buildHashKey() {
        StringBuilder sb = new StringBuilder();
        if (isDAnd()) {
            sb.append('A');
        } else if (isDOr()) {
            sb.append('O');
        } else {
            throw new IllegalStateException();
        }
        for (Exp arg : args) {
            String sId = Integer.toString(arg.getExpId(), Character.MAX_RADIX);
            sb.append(sId);
        }
        return sb.toString();
    }

    @Override
    public boolean anyVarOverlap(@NotNull VarSet vs) {
        return getVars().anyVarOverlap(vs);
    }

    @Override
    public boolean isVarDisjoint(@NotNull VarSet vs) {
        return getVars().isVarDisjoint(vs);
    }
}
