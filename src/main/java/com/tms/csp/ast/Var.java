package com.tms.csp.ast;

import com.tms.csp.VarInfo;
import com.tms.csp.parse.VarSpace;
import com.tms.csp.util.HasCode;
import com.tms.csp.util.HasVarId;
import com.tms.csp.util.ints.Ints;
import com.tms.csp.util.varSets.SingletonVarSet;
import com.tms.csp.util.varSets.VarPair;
import com.tms.csp.util.varSets.VarSet;
import com.tms.csp.util.varSets.VarSetBuilder;
import com.tms.csp.varCodes.IVar;
import com.tms.csp.varCodes.VarCode;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

public class Var implements IVar, HasVarId, Comparable<Var>, HasCode, PLConstants {

    public static final int MIN_VAR_ID = 1;
    public static final int INIT_PARTNER_TABLE_SIZE = 16;
    public final VarSpace vSpace;

    public final int varId;
    public final int varIdHash;
    private final VarCode varCode;

    private Xor xorParentInitial; //admin
    private Xor xorParent;

    private MetaVar meta;

    private Lit p;
    private Lit n;
    private DcOr dcOr;

    private VarSet singletonVarSet;

    private VarPair[] partners;

    /**
     * Note: space passed to Var's constructor is not fully initialized
     */
    public Var(VarSpace vSpace, int varId, String varCode) {
        checkNotNull(varCode);
        this.vSpace = vSpace;
        this.varId = varId;
        this.varCode = new VarCode(varCode);
        this.varIdHash = Ints.superFastHash(varId);
    }

    public VarSpace getVarSpace() {
        return vSpace;
    }

    private VarSet mkPartner(int bucket, VarPair prev, Var var2) {

        if (prev == null) {
            VarPair varPair = new VarPair(this, var2);
            partners[bucket] = varPair;
            return varPair;
        }

        assert prev.var1 == this;
        if (prev.var2.varId == var2.varId) {
            return prev;
        }

        if (prev.next == null) {
            prev.next = new VarPair(this, var2);
            return prev.next;
        }

        return mkPartner(bucket, prev.next, var2);

    }

    public final int wordIndex() {
        return varId >>> 6;
    }

    public final long bitMask() {
        return 1L << varId;
    }

    public VarSet mkPartner(int varId) {
        Var var = vSpace.getVar(varId);
        return mkPartner(var);
    }

    public VarSet mkPartner(Var other) {
        int otherVarId = other.getVarId();
        if (varId == otherVarId) return mkSingletonVarSet();

        Var var1;
        Var var2;
        if (otherVarId < this.varId) {
            var1 = other;
            var2 = this;
        } else {
            var1 = this;
            var2 = other;
        }

        assert var1.getVarId() < var2.getVarId();

        if (var1.partners == null) {
            var1.partners = new VarPair[INIT_PARTNER_TABLE_SIZE];
        }

        int bucket = Space.computeBucketIndex(var2.hashCode(), partners.length);

        VarPair prev = partners[bucket];

        return mkPartner(bucket, prev, var2);

    }

    @Override
    public int hashCode() {
        return varIdHash;
    }

    @Override
    public boolean isXorChild() {

        if (meta != null) {
            return meta.isXorChild();
        }

        return xorParent != null;

    }

    //    public Var(Space space, int varId, String varCode) {
//        this(space, varId, new VarCode(varCode));
//    }

    public void setXorParent(Xor xorParent) {
        if (this.xorParentInitial == null) {
            this.xorParentInitial = xorParent;
        }
        this.xorParent = xorParent;

    }

    public int bitsUsedInLastLongBlock() {
        Space space = getSpace();
        int varCount = space.getVarCount();
        int remainder = (varCount % 64);
        if (remainder == 0) {
            return 64;
        } else {
            return remainder;
        }
    }

    public static int checkVarId(int varId) {
        if (varId < MIN_VAR_ID) throw new IllegalArgumentException("Bad varId[" + varId + "]");
        return varId;
    }

    public int getVarId() {
        assert (varId >= MIN_VAR_ID) : "Bad varId[" + varId + "]";
        return varId;
    }

    public int getVarIndex() {
        return getVarId() - MIN_VAR_ID;
    }

    public String getTinyId() {
        int varId = getVarId();
        if (varId < MIN_VAR_ID) throw new IllegalStateException();
        return Integer.toString(varId, Character.MAX_RADIX);
    }

    public Space getSpace() {
        return getVSpace().getSpace();
    }


    public VarCode getVarCode2() throws UnsupportedOperationException {
        return varCode;
    }

    public Xor getXorParent() {
        return xorParent;
    }

    public Xor getXorParentInitial() {
        return xorParentInitial;
    }

//    @Override
//    public VarSet getCareVarsOld() {
//        return new VarSet(space, ImmutableSet.of(this));
//    }


    @Nonnull
    public String getLocalName() {
        return varCode.getLocalName();
    }

    @Override
    public boolean isAny(String... prefixes) {
        return varCode.isAny(prefixes);
    }

    public boolean isAny(Iterable<String> prefixes) {
        return varCode.isAny(prefixes);
    }

    @Override
    public boolean is(EnumSet<Prefix> prefixes) {
        return varCode.is(prefixes);
    }

    @Override
    public boolean is(String prefix) {
        return varCode.is(prefix);
    }

    @Override
    public boolean is(Prefix prefix) {
        return varCode.is((prefix));
    }

    @Override
    public boolean isAcy() {
        return varCode.isAcy();
    }

    public boolean isAcyVar() {
        return isAcy();
    }

    @Override
    public boolean isCoreXor() {
        return varCode.isCoreXor();
    }

    public boolean isCore(VarInfo varInfo) {
        return varCode.isCoreXor() || varInfo.isInvAcy(getVarCode());
    }

    @Nonnull
    @Override
    public String getPrefix() {
        return varCode.getPrefix();
    }

    public boolean hasPrefix() {
        return varCode.hasPrefix();
    }

    public String getPrefixCode() {
        return varCode.getPrefix();
    }

    @Override
    public String getVarCode() {
        return varCode.toString();

    }

    @Override
    public String toString() {
        return getVarCode();
    }

    @Override
    public boolean isInv() {
        return varCode.isInv();
    }

    public int getCoreIndex() {
        return getPrefix2().getIndex();
    }

    @Override
    public Prefix getPrefix2() {
        return varCode.getPrefix2();
    }

    @Override
    public VarCode toVarCode() {
        return varCode;
    }

    @Override
    public boolean isYear() {
        return varCode.isYear();
    }

    public boolean isSeries() {
        return varCode.isSeries();
    }

    public boolean isModel() {
        return varCode.isMdl();
    }

    public boolean isXCol() {
        return varCode.isXCol();
    }

    public boolean isICol() {
        return varCode.isICol();
    }

    public boolean isYearVar() {
        return isYear();
    }

    public boolean isModelVar() {
        return isModel();
    }

    public boolean isIColVar() {
        return isICol();
    }

    public boolean isXColVar() {
        return isXCol();
    }

    public boolean isSeriesVar() {
        return isSeries();
    }

    public static void sortByVarCode(List<Var> list) {
        Collections.sort(list, COMPARATOR_BY_VAR_CODE);
    }

    public static final Comparator<Var> COMPARATOR_BY_VAR_CODE = new Comparator<Var>() {
        @Override
        public int compare(Var e1, Var e2) {
            checkNotNull(e1);
            checkNotNull(e2);
            VarCode vc1 = e1.getVarCode2();
            VarCode vc2 = e2.getVarCode2();
            return vc1.compareTo(vc2);
        }
    };

    public Lit mkPosLit() {
        assert vSpace != null;
        if (p == null) {
            Space sp = getSpace();
            int nodeCount = sp.getNodeCount();
            p = new Lit(this, true, nodeCount);
            getSpace().addNode(p);
        } else {
            p.notNew();
        }

        return p;
    }

    public boolean hasDcOr() {
        return dcOr != null;
    }

    public DcOr mkDcOr() {
        if (dcOr == null) {
            Space sp = getSpace();
            int nodeCount = sp.getNodeCount();
            dcOr = new DcOr(this, nodeCount);
            getSpace().addNode(dcOr);
            assert dcOr.isNew();
        } else {
            dcOr.notNew();
            assert !dcOr.isNew();
        }
        return dcOr;
    }


    public Lit pLit() {
        return mkPosLit();
    }

    public Lit nLit() {
        return mkNegLit();
    }

    public Lit mkLit(boolean sign) {
        if (sign) return mkPosLit();
        return mkNegLit();
    }

    public Lit lit(boolean sign) {
        return mkLit(sign);
    }

    public Lit mkNegLit() {
        if (n == null) {
            int nodeCount = getSpace().getNodeCount();
            n = new Lit(this, false, nodeCount);
            getSpace().addNode(n);
        } else {
            n.notNew();
        }
        return n;
    }

    public int getLitCount() {
        int c = 0;
        if (p != null) c++;
        if (n != null) c++;
        return c;
    }

//    @Override
//    public int compare(Var o1, Var o2) {
//        VarCode vc1 = o1.getVarCode2();
//        VarCode vc2 = o2.getVarCode2();
//        return vc1.compareTo(vc2);
//    }


    @Override
    public int compareTo(Var o) {
        String s1 = getVarCode();
        String s2 = o.getVarCode();
        return s1.compareTo(s2);
    }

    public int compareFast(Var that) {
        if (this.varId < that.varId) {
            return -1;
        } else if (this.varId > that.varId) {
            return 1;
        } else {
            assert this.varId == that.varId;
            return 0;
        }
    }


    public VarSet mkSingletonVarSet() {
        if (singletonVarSet == null) {
            singletonVarSet = new SingletonVarSet(this);
        }
        return singletonVarSet;
    }

    @Override
    public String getCode() {
        return getVarCode();
    }

    @Override
    final public boolean equals(Object obj) {
        return eq((Var) obj);
    }

    final public boolean eq(Var that) {
        return this.varId == that.varId;
    }

    public static boolean eq(Var v1, Var v2) {
        if (v1 == null && v2 == null) return true;
        if (v1 != null && v2 != null) {
            return v1.varId == v2.varId;
        }
        return false;
    }

    public VarSpace getVSpace() {
        return vSpace;
    }

    public boolean hasPosLit() {
        return p != null;
    }

    public boolean hasNegLit() {
        return n != null;
    }

//    public int getValue() {
//        checkState(isMsrpStrictVar() || isMsrpBucketVar());
//        return Integer.parseInt(getLocalName());
//    }

    public boolean isMsrpVar() {
        return getPrefix().equals(MSRP_PREFIX);
    }

    public String getHashKey() {
        return Integer.toString(varId, Character.MAX_RADIX);
    }

    public int getValue() {
        throw new UnsupportedOperationException();
    }

    public boolean isCheckboxVar() {
        return !isXorChild();
    }

    public boolean isRadioVar() {
        return isXorChild();
    }

    /**
     * based c *initial* xor parent
     */
    public VarSet getAllXorSiblingsOld() {
        assert isXorChild();
        Xor xorParent = getXorParentInitial();
        List<Exp> args = xorParent.getArgs();

        VarSetBuilder b = getSpace().newMutableVarSet();
        for (Exp arg : args) {
            assert arg.isPosLit();
            Var var = arg.getVr();
            b.add(var);
        }

        return b.build();
    }

    /**
     * based c VarMeta
     */
    public VarSet getAllXorSiblings() {
        assert meta != null;
        assert isXorChild();

        Space space = getSpace();

        List<MetaVar> varMetas = meta.getParent().getChildVars();

//        VarSet vars = space.mkEmptyVarSet();
        VarSetBuilder vars = space.newMutableVarSet();
        for (MetaVar varMeta : varMetas) {
            String varCode = varMeta.getCode();
            vars.addVarCode(varCode);
        }

        return vars.build();
    }


    public void setMeta(MetaVar meta) {
        this.meta = meta;
    }
}
