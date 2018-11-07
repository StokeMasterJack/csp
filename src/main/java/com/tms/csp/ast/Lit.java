package com.tms.csp.ast;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterators;
import com.tms.csp.argBuilder.ArgBuilder;
import com.tms.csp.fm.dnnf.products.Cube;
import com.tms.csp.fm.dnnf.products.LitCube;
import com.tms.csp.util.Bit;
import com.tms.csp.util.varSets.VarSet;
import com.tms.csp.varCodes.VarCode;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.Set;

import static com.tms.csp.ssutil.Strings.lpad;

public class Lit extends Exp implements ConditionOn {

    private static final String L_SP = "L ";

    public final Var vr;
    public final boolean sign;

    private final Cube cube;
    private final ImmutableSet<Cube> cubes;

    private Integer value;  //for counting graph

    private LitAndFalse litAndFalse;

    private boolean isNew;

    public boolean hasLitAndFalse() {
        return litAndFalse != null;
    }

    public LitAndFalse mkLitAndFalse() {
        if (litAndFalse == null) {
            Space sp = getSpace();
            int nodeCount = sp.getNodeCount();
            litAndFalse = new LitAndFalse(this, nodeCount);
            getSpace().addNode(litAndFalse);
        }
        return litAndFalse;
    }


    public Lit(Var vr, boolean sign, int expId) {
        super(vr.getSpace(), expId);
        this.vr = vr;
        this.sign = sign;
        this.cube = new LitCube(this);
        this.cubes = ImmutableSet.of(cube);
        isNew = true;
    }

    public void notNew() {
        isNew = false;
    }

    @Override
    public boolean isNew() {
        return isNew;
    }

    public Var getVr() {
        return vr;
    }

    @Override
    public String getCode() {
        return vr.getVarCode();
    }

    @Override
    public boolean isPos() {
        return sign;
    }

    public int getVarId() {
        return vr.getVarId();
    }

    @Override
    public boolean anyVarOverlap(Exp exp) {
        if (exp == null || exp.isConstant()) return false;
        if (exp.isLit()) {
            return vr == exp.getVr();
        }
        if (exp.isComplex()) {
            VarSet vars = exp.getVars();
            return vars.containsVar(vr);
        }

        throw new IllegalStateException();
    }

    public Exp flip() {
        return vr.mkLit(!sign);
    }


    @Override
    public Exp condition(Cube ctx) {
        if (!ctx.containsVar(vr)) return this;

        if (ctx.isTrue(vr) && isPos()) return mkTrue();
        if (ctx.isTrue(vr) && isNeg()) return mkFalse();
        if (ctx.isFalse(vr) && isPos()) return mkFalse();
        if (ctx.isFalse(vr) && isNeg()) return mkTrue();

        throw new IllegalStateException();
    }

    public Exp getPos() {
        if (sign) return this;
        else return flip();
    }

    public Exp getNeg() {
        if (!sign) return this;
        else return flip();
    }

    @Override
    public Lit asLit() {
        return this;
    }

    @Override
    public boolean isLit() {
        return true;
    }

    @Override
    public boolean isSat() {
        return true;
    }

    @Override
    public long computeSatCount() {
        return 1L;
    }

    @Override
    public long getSatCount() {
        return 1L;
    }

    @Override
    public int getCubeCount() {
        return 1;
    }

    @Override
    public boolean isLeaf() {
        return true;
    }

    @Override
    public Var getFirstVar() {
        return vr;
    }

    @Override
    public boolean containsVarId(int varId) {
        return vr.varId == varId;
    }

    final public void serialize(Ser a) {
        if (!sign) {
            a.bang();
        }
        a.append(getVarCode());
    }

    @Override
    public String getVarCode() throws UnsupportedOperationException {
        return vr.getVarCode();
    }

    @Override
    public VarCode getVarCode2() throws UnsupportedOperationException {
        return vr.getVarCode2();
    }

    @Override
    public Exp condition(Lit lit) {

        if (vr != lit.vr) {
            return this;
        }

        if (sign == lit.sign) {
            return mkConstantTrue();
        } else {
            return mkConstantFalse();
        }

    }


    @Override
    public Exp flatten() {
        return this;
    }

    public int getLit() {
        return sign() ? vr.varId : -vr.varId;
    }

    @Override
    public void serializeTinyDnnf(Ser a) {
        a.append(L_SP);
        a.append(getLit());
    }


    @Override
    public void toXml(Ser a, int depth) {
        a.indent(depth);
        if (isNeg()) {
            a.bang();
        }
        String varCode = getVarCode();
        a.append(varCode);
        a.newLine();
    }


    public Exp project(VarSet outVars) {
        if (outVars.containsVar(vr)) {
            return this;
        } else {
            return mkConstantTrue();
        }
    }

    @Override
    public Bit getValue(Var var) {
        if (var == this.vr) {
            if (sign) return Bit.TRUE;
            else return Bit.FALSE;
        } else {
            return Bit.OPEN;
        }
    }

    @Override
    public Exp toDnnf() {
        return this;
    }

    @Override
    public boolean hasFlip() {
        if (sign) {
            return vr.hasNegLit();
        } else {
            return vr.hasPosLit();
        }
    }


    @Override
    public int computeValue(Cube cube) {
        Lit flip = flip().asLit();
        if (cube.containsLit(flip)) {
            return 0;
        } else {
            return 1;
        }
    }

    @Override
    public int computeValue() {
        assert _space.pics != null;
        if (this.value == null) {
            Lit flip = flip().asLit();
            if (_space.pics.containsLit(flip)) {
                this.value = 0;
            } else {
                this.value = 1;
            }
        }
        return this.value;
    }


    @Override
    public Set<Cube> getCubesSmooth() {
        return cubes;
    }

    @Override
    public boolean checkDnnf() {
        return true;
    }


    @Override
    public boolean computeSat(Lit lit) {
        if (vr != lit.vr) return true;
        return sign == lit.sign;
    }

    @Override
    public boolean computeSat(Cube cube) {
        Lit flip = flip().asLit();
        if (cube.containsLit(flip)) {
            return false;
        } else {
            return true;
        }
    }

    @Override
    public boolean computeSat(VarSet trueVars) {
        if (isNeg() && trueVars.containsVar(vr)) {
            return false;
        } else {
            return true;
        }
    }

    @Override
    public long computeSatCount1(Lit lit) {
        boolean sat = computeSat(lit);
        if (sat) {
            return 1;
        } else {
            return 0;
        }
    }


    public void serializeTrueVars(Ser a) {
        if (isPos()) {
            serialize(a);
        }
    }

    public String serializeTrueVars() {
        Ser a = new Ser();
        serializeTrueVars(a);
        return a.toString();
    }

    @Override
    public Iterator<Lit> litIterator() {
        return Iterators.singletonIterator(this);
    }

    @Override
    public Iterator<Var> varIterator() {
        return Iterators.singletonIterator(vr);
    }

    public Prefix getPref() {
        return vr.getPrefix2();
    }

    public String getPrefix() {
        return vr.getPrefix();
    }

    public Cube asCube() {
        return cube;
    }


    @Override
    public Iterator<Exp> argIter() {
        return Iterators.singletonIterator(asExp());
    }

    @Override
    public VarSet getVars() {
        return vr.mkSingletonVarSet();
    }


    public Exp copyToOtherSpace(Space destSpace) {
        if (getSpace() == destSpace) {
            return this;
        }
        String varCode = getVarCode();
        Var destVar = destSpace.getVar(varCode);
        assert destVar.getSpace() == destSpace;
        return destVar.mkLit(sign());
    }

    @Override
    public boolean isDnnf() {
        return true;
    }

    @Override
    public boolean isSmooth() {
        return true;
    }

    public boolean sameVarCode(Lit that) {
        return getVarCode().equals(that.getVarCode());
    }

    @Override
    public Exp litMatch() {
        return this;
    }

    public Exp getSmooth() {
        return this;
    }

    public Exp smooth(VarSet dontCares) {

        Space space = getSpace();

        if (dontCares == null || dontCares.isEmpty()) {
            return this;
        }
        ArgBuilder bAnd = new ArgBuilder(space, Op.DAnd);


        //add special dontCare DOrs
        for (Var dontCare : dontCares) {
            Exp dcOr = dontCare.mkDcOr();
            bAnd.addExp(dcOr);
        }

        if (bAnd.isEmpty()) {
            throw new IllegalStateException();
        }

        //add current args
        bAnd.addExp(this);

        Exp exp = bAnd.mk(space);
        return exp.asDAnd();
    }

    public String getHashKey() {
        StringBuilder sb = new StringBuilder();
        sb.append('L');
        sb.append(vr.getHashKey());
        if (isPos()) {
            sb.append('T');
        } else {
            sb.append('F');
        }
        return sb.toString();
    }

    @Override
    public int getArgCount() {
        return 0;
    }

    @NotNull
    public String toString(int cols) {
        return lpad(toString(), ' ', cols);
    }

    @NotNull
    @Override
    public Exp conditionThat(@NotNull Exp that) {
        return that.condition(this);
    }

    @Override
    public void assignSafe(@NotNull Csp csp) {
        csp._assignSafe(this);
    }

    public long satCountPL(VarSet parentVars) {
        return Csp.computeDcVars(1, parentVars, getVars());
    }


}
