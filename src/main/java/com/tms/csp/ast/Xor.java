package com.tms.csp.ast;

import com.google.common.collect.ImmutableSet;
import com.tms.csp.argBuilder.ArgBuilder;
import com.tms.csp.fm.dnnf.DAnd;
import com.tms.csp.fm.dnnf.products.Cube;
import com.tms.csp.util.Bit;
import com.tms.csp.util.IntPairCallback;
import com.tms.csp.util.Range;
import com.tms.csp.util.XorCube;
import com.tms.csp.util.varSets.VarSet;

import javax.annotation.Nonnull;
import java.math.BigInteger;
import java.util.Iterator;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

public class Xor extends PosComplexMultiVar implements IXor {

    public static final PosOp OP = PosOp.XOR;

    protected Exp smooth;

    public Xor(Space space, int expId, Exp[] fixedArgs) {
        super(space, expId, fixedArgs);
        assert fixedArgs.length > 1 : "Invalid argCount[" + fixedArgs.length + "]";
        assert checkFixedArgs(fixedArgs);
        assert !isNestedOr();


        for (Exp lit : fixedArgs) {
            assert lit.isLit() : "invalid xor arg[" + lit + "]";
            assert lit.isDnnf();
            checkState(lit.isLit());
            assert lit.isPosLit();
            Var var = lit.getVr();
            var.setXorParent(this);
        }

        if (args.length == 2) {
            if (Exp.isFlip(args[0], args[1])) {
                throw new IllegalStateException();
            }
        }

//        if (isModelXor()) {
//            System.err.println(this);
//        }
    }

    @Override
    public boolean isXor() {
        return true;
    }

    public boolean isDnnf() {
        return true;
    }

    @Override
    public boolean computeSat(Lit lit) {
        assert getVars().size() != 0;
        assert getVars().size() != 1;
        assert isAllLits();
        return true;
    }

    @Override
    public Set<Cube> getCubesSmooth() {
        ImmutableSet.Builder<Cube> bb = ImmutableSet.builder();
        for (Exp arg : args) {
            assert arg.isPosLit() : arg;
            Cube cube = new XorCube(this, arg.getVr());
//            Cube cube = arg.asLit().asCube();
            bb.add(cube);
        }
        return bb.build();
    }

    @Override
    public boolean checkDnnf() {
        return true;
    }

    private Exp conditionPosLit(Var var) {
        VarSet vs1 = getVars();
        VarSet vs2 = vs1.minus(var);
        assert vs2.size() == vs1.size() - 1;
        return _space.mkNCube(vs2);
    }

    private Exp conditionNegLit(Var var) {
        VarSet vs1 = getVars();
        VarSet vs2 = vs1.minus(var);
        assert vs2.size() == vs1.size() - 1;
        return _space.mkXor(vs2);
    }

    public Exp condition(Lit lit) {
        Var var = lit.getVr();
        if (!containsVarId(var.varId)) {
            return this;
        }
        boolean sign = lit.sign();
        if (sign) {
            return conditionPosLit(var);
        } else {
            return conditionNegLit(var);
        }
    }


    @Override
    public Exp condition(Cube ctx) {

        if (isVarDisjoint(ctx)) {
            return this;
        }


        Var tVar = null;

        int fCount = 0;
        int oCount = 0;

        for (Var var : getVars()) {
            Bit v = ctx.getValue(var);

            if (v.isTrue()) {
                if (tVar == null) {
                    tVar = var;
                } else {
                    //tCount>1 => return false
                    System.err.println("Xor.condition(cube) TT");
                    return mkFalse();
                }
            } else if (v.isFalse()) {
                fCount++;
                //skip
            } else {
                oCount++;
            }
        }

        int tCount = getArgCount() - (fCount + oCount);

        int tCount2 = tVar == null ? 0 : 1;
        assert tCount == tCount2;

        assert fCount + tCount + oCount == getArgCount();

        if (tCount > 1) {
            return mkFalse();
        }

        if (tCount == 1 && oCount == 0) {
            return mkTrue();
        }


        if (tVar == null) {
            //tCount = 0
            if (oCount == 0) {
                assert fCount == getVars().size();
                //all false
                return mkFalse();
            } else {
                //0 true, some opens
                assert fCount > 0;
                VarSet thisVars = getVars();
                VarSet varsToRemove = ctx.getVars();
                VarSet opens = thisVars.minus(varsToRemove);
                assert opens.size() < thisVars.size();
                return getSpace().mkXor(opens);
            }
        } else {
            //tCount = 1
            if (oCount == 0) {
                //1 true, rest false => return true
                assert tCount + fCount == getVars().size();
                return mkTrue();
            } else {
                //1 true, some opens
                assert oCount > 0;
                VarSet thisVars = getVars();
                VarSet varsToRemove = ctx.getVars();
                VarSet opens = thisVars.minus(varsToRemove);
                return _space.mkNCube(opens);
            }

        }


    }


    @Override
    public PosOp getPosOp() {
        return OP;
    }


    @Override
    public Op getOp() {
        return Op.Xor;
    }

    @Nonnull
    public String getPrefix() {
        return getVars().getFirstVar().getPrefix();
    }


    @Override
    public boolean isSat() {

        if (isAllLits()) {
            try {
                new DynCube(getSpace(), this.litItFromExpArray());
                return true;
            } catch (ConflictingAssignmentException e) {
                return false;
            }
        }

        for (Exp arg : argIt()) {
            if (arg.isSat()) {
                return true;
            }
        }

        return false;
    }


    public Exp toCnf() {
        return toOrAndConflicts();
    }


    public Exp toOrAndConflicts() {
        if (getVars().size() == 0) {
            throw new IllegalStateException();
        } else if (getVars().size() == 1) {
            throw new IllegalStateException();
        } else {
            ArgBuilder aa = new ArgBuilder(_space, Op.And);
            Exp or = getSpace().mkOr(getVars());
            aa.addExp(or);
            getConflictsFromXorArgs(aa);
            return aa.mk();
        }
    }

    private void getConflictsFromXorArgs(final ArgBuilder aa) {
        Range range = new Range(getVars().size() - 1);
        range.forEachPair(new IntPairCallback() {
            @Override
            public void processPair(int i, int j) {
                Var ai = getVars().get(i);
                Var aj = getVars().get(j);
                Exp conflict = getSpace().mkBinaryNand(ai, aj);
                aa.addExp(conflict);
            }
        });
    }


    @Override
    public Iterator<Lit> litIterator() {
        return new LitIterator(varIterator(), true);
    }

//    final public void serialize(Ser a) {
//        String token = "xor";
//        a.append(token);
//        a.append(LPAREN);
//
//        Iterator<Lit> it = litIterator();
//        while (it.hasNext()) {
//            Lit lit = it.next();
//            lit.serialize(a);
//            if (it.hasNext()) {
//                a.argSep();
//            }
//        }
//
//        a.append(RPAREN);
//    }

    @Override
    public Exp toDnnf() {
        return computeSmooth();
//        return this;
    }

//    @Override
//    public Set<Lit> getLits() {
//        ImmutableSet.Builder<Lit> b = ImmutableSet.builder();
//        for (Exp arg : args) {
//            b.addAll(arg.getLits());
//        }
//        return b.build();
//    }

    @Override
    public BigInteger getSatCount() {
        return BigInteger.valueOf(getArgCount());
    }


    @Override
    public BigInteger computeSatCount() {
        return BigInteger.valueOf(getArgCount());
    }

    @Override
    public int getCubeCount() {
        return getVarCount();
    }


    @Override
    public Exp copyToOtherSpace(Space destSpace) {
        if (getSpace() == destSpace) {
            return this;
        }
        return copyArgsExpToOtherSpace(destSpace, op(), argIt());
    }


    @Override
    public void serializeTinyDnnf(Ser a) {
        a.append('O');
        a.append(' ');
        serializeArgsTinyDnnf(a);
    }

    @Override
    public Exp project(VarSet outVars) {

        checkNotNull(outVars);

        VarSet vars = this.getVars();
        if (outVars.containsAllVars(vars)) {
            return this;
        }

        ArgBuilder b = new ArgBuilder(_space, Op.Xor);


        for (int i = 0; i < args.length; i++) {
            Exp arg = args[i];
            Exp s = arg.project(outVars);

            if (s.isTrue()) {
                return mkTrue();
            } else if (s.isFalse()) {
                //skip
            } else {
                assert s.isOpen();
                b.addExp(s);

            }

        }

        return b.mk();
    }


    /**
     * replace unmatched lits with: lit or (!lit and false)
     */
    public Exp litMatch() {
        Space space = getSpace();

        ImmutableSet.Builder<Exp> newArgs = ImmutableSet.builder();

        for (Exp arg : args) {
            Exp newArg = arg.litMatch();
            newArgs.add(newArg);
        }

        Exp retVal = space.mkDOr(newArgs.build());


        return retVal;

    }


//    @Override
//    public boolean isLitMatched() {
//        for (Exp arg : args) {
//            if (!arg.isLitMatched()) {
//                return false;
//            }
//        }
//
//        return true;
//    }

    @Override
    public boolean isSmooth() {
        return false;
    }

    public Exp getSmooth() {
        if (smooth == null) {
            smooth = computeSmooth();
        }
        return smooth;
    }


    private Exp computeSmooth() {
        VarSet vars = getVars();
        ArgBuilder dOr = new ArgBuilder(_space, Op.DOr);
        for (Var v1 : vars) {
            ArgBuilder dAnd = new ArgBuilder(_space, Op.DAnd);
            for (Var v2 : vars) {
                dAnd.addExp(v2.mkLit(v2 == v1));
            }
            DAnd and = dAnd.mk(getSpace()).asDAnd();
            dOr.addExp(and);
        }


        Exp retVal = dOr.mk();
        assert retVal.isSmooth() : retVal + " " + retVal.getSimpleName();
        return retVal;
    }

    @Override
    public Set<Cube> computeCubesSmooth() {
        return getSmooth().computeCubesSmooth();
    }

    @Override
    public Set<Cube> computeCubesRough() {
        return _space.expFactory.computeCubesForXor(this);
    }


    public long satCountPL() {
        return getArgCount();
    }


}