package com.tms.csp.ast;

import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSortedSet;
import com.google.common.collect.Multimap;
import com.tms.csp.VarInfo;
import com.tms.csp.Vars;
import com.tms.csp.argBuilder.ArgBuilder;
import com.tms.csp.argBuilder.IArgBuilder;
import com.tms.csp.fm.dnnf.Dnnf;
import com.tms.csp.fm.dnnf.TrueOrArg;
import com.tms.csp.fm.dnnf.products.Cube;
import com.tms.csp.fm.dnnf.products.EmptyCube;
import com.tms.csp.fm.dnnf.vars.VarFilter;
import com.tms.csp.fm.dnnf.vars.VarGrp;
import com.tms.csp.parse.Head;
import com.tms.csp.parse.ParseCounter;
import com.tms.csp.parse.ParseUtil;
import com.tms.csp.parse.VarSpace;
import com.tms.csp.ssutil.SingleLineLogFormatter;
import com.tms.csp.util.*;
import com.tms.csp.util.ints.IntIterator;
import com.tms.csp.util.varSets.VarSet;
import com.tms.csp.util.varSets.VarSetBuilder;
import com.tms.csp.varCodes.IVar;
import kotlin.sequences.Sequence;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.logging.Handler;
import java.util.logging.Logger;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.tms.csp.ssutil.Strings.lpad;

/*
1: constantTrue.expId
2: minVarId


N: maxVarId
N + 1: minComplexPos

 */
public class Space extends SpaceUtil implements PLConstants {

    public static final Splitter MY_SPLITTER = Splitter.on(' ').trimResults().omitEmptyStrings();

    public final Random random = new Random();

    public final static SpaceConfig config = new SpaceConfig();


    public VarSpace varSpace;


    private final True constantTrue;
    private final False constantFalse;

    public final Cube emptyCube;

    public final ArrayList<Exp> _nodes = new ArrayList<Exp>();

    public Csp csp; //PL

    public Dnnf dnnf;

    public VarSet invVars;


    private VarMeta varMeta;  //EFC Callback (aka VarInfo)
    private MetaVar metaVar;  //from vr-meta xml file (ui meta)

    final public ExpFactory expFactory;
    final public Parser parser;
    final public PosComplexSpace posComplexSpace;

    /**
     * For use by Counting Graph
     */
    public Cube pics;

    public Space() {

        varMeta = new VarMeta();

        constantTrue = new True(this, TRUE_EXP_ID);
        constantFalse = new False(constantTrue);
        addNode(constantTrue);
        addNode(constantFalse);

        emptyCube = new EmptyCube(this);

        varSpace = new VarSpace(this);
        posComplexSpace = new PosComplexSpace(this);

        expFactory = new ExpFactory(this);
        parser = new Parser(this);


    }

    public Space(@Nonnull Iterable<String> varCodes) {
        this();
        varSpace.mkVars(varCodes);
    }

    public Space(@Nonnull VarSet vars) {
        this();
        varSpace.mkVars(vars);
    }


    public SortedSet<String> getVarCodesSorted() {
        return varSpace.getVarCodesSorted();
    }

    public Exp parseExp(String expText) {
        return parser.parseExp(expText);

    }


    public static Space withVars(String varList) {
        if (Vars.isVarsLine(varList)) {
            return new Space(Vars.parseVarsLine(varList));
        } else {
            return new Space(Vars.parseVarList(varList));
        }

    }


//    public static Raw mkRaw1(String fact) {
//        Parse ff = Parse.fact(fact);
//        Raw raw = ff.mkRaw();
//        return raw;
//    }

//    public static Space extractVars(String fact) {
//        Set<String> _complexVars = extractVarCodes(fact);
//        return new Space(_complexVars);
//    }

//    public static Set<String> extractVarCodes(String fact) {
//        return Parse.fact(fact).mkRaw().extractVarCodes();
//    }

//
//
//    public Space(String factory, Set<String> extraVars) {
//        this(Parse.fact(factory).extraVars(extraVars).mkRaw(), false);
//    }
//
//
//    public Space(String fact, String inv, VarInfo varInfo) {
//        this(Parse.fact(fact)
//                .invDL(inv)
//                .varInfo(varInfo)
//                .extraVars(Inv.extractDealerVarCodes(inv))
//                .mkRaw(), false);
//    }
//
//    public Space(String fact, String inv, String varInfo) {
//        this(Parse.fact(fact)
//                .invDL(inv)
//                .varInfoClob(varInfo)
//                .extraVars(Inv.extractDealerVarCodes(inv))
//                .mkRaw(), false);
//    }
//
//    public Space(String fact, String inv) {
//        this(Parse.fact(fact).invDL(inv).mkRaw(), false);
//    }
//
//

    public ParseCounter getParseCounter() {
        return parser.getParseCounter();
    }


    public VarSet getAtVars() {
        VarSetBuilder b = newMutableVarSet();
        for (String s : alwaysTrueVars1) {
            if (containsVarCode(s)) {
                b.addVar(s);
            }
        }
        return b.build();
    }


    public static boolean isDnnf(String[] lines) {
        String line = null;
        try {
            line = lines[1];
        } catch (IndexOutOfBoundsException e) {
            throw new RuntimeException(e);
        }
        return Vars.isInvVarsLine(line);
    }


    public int getVarCount() {
        return getVarSpace().getVarCount();
    }

    public int getMaxVarId() {
        return varSpace.getMaxVarId();
    }


    public VarSetBuilder newMutableVarSet() {
        return varSpace.varSetBuilder();
    }

    public VarSetBuilder varSetBuilder() {
        return varSpace.varSetBuilder();
    }


    public VarSet getVars() {
        VarSpace varMap1 = getVarSpace();
        return varMap1.getVars();
    }


    //msrp - 32 bits
    public static int getBuiltInVarCount() {
        return 32;
    }


    public void setVarInfo(VarInfo varInfo) {
        this.varMeta.setVarInfo(varInfo);
    }

    public void checkVarInfo() {
        varMeta.checkVarInfo();
    }


    public Exp mkFalse() {
        return constantFalse;
    }

    public Exp mkTrue() {
        return constantTrue;
    }

    public Var getVar(int varId) throws BadVarIdException {
        if (varId < Var.MIN_VAR_ID) throw new BadVarIdException(varId);
        VarSpace vm = getVarSpace();
        return vm.getVar(varId);
    }

    public Var getVar(String varCode) throws BadVarCodeException {
        VarSpace varMap1 = getVarSpace();
        assert varSpace != null;
//        return varMap1.getVar(varCode);
        return varMap1.mkVar(varCode);
    }

    public Var getVar(Var var) throws BadVarCodeException {
        return getVar(var.getVarCode());
    }


    public List<Var> getVarList() {
        VarSpace varMap = getVarSpace();
        return varMap.getVarList();
    }


    public VarSpace getVarSpace() {
        return varSpace;
    }


    public Set<String> getVarCodes() {
        return getVarSpace().getVarCodes();
    }


    public Exp mkNot(Exp pos) {
        return pos.flip();
    }

    public Exp pushNotsIn(Not exp) {
        return exp.hardFlip();
    }

    public Lit getLit(int varId, boolean sign) {
        return varSpace.getLit(varId, sign);
    }

    public Lit getLit(String varCode, boolean sign) {
        return getVar(varCode).lit(sign);
    }

    public Lit mkLit(int lit) {
        return varSpace.mkLit(lit);
    }

    public Lit mkLit(Lit lit) {
        return varSpace.mkLit(lit);
    }

    public Lit mkLit(String signedVarCode) {
        return varSpace.mkLit(signedVarCode);
    }


    public String serializeWithVarMap() {
        Ser a = new Ser();
        serializeWithVarMap(a);
        return a.toString();
    }

    public void serializeWithVarMap(Ser a) {
        serializeVarMap(a);
        a.newLine();
        serialize(a);
    }

    public static void serializeVarIt(Ser a, Iterable<Var> vars) {
        ParseUtil.serializeVarIt(a, vars);
    }

    public static void serializeVarArray(Ser a, Var... vars) {
        ParseUtil.serializeVarArray(a, vars);
    }

    public static void serializeVarCodes(Ser a, Iterable<String> varCodes) {
        ParseUtil.serializeVarCodes(a, varCodes);
    }

    public static void serializeVarCodes(Iterable<String> varCodes) {
        ParseUtil.serializeVarCodes(varCodes);
    }


    public void serialize(Ser a) {
        csp.serialize(a);
    }

    public String serializeConstraints() {
        return csp.serializeConstraints();
    }

    public String serialize() {
        return csp.serialize();
    }

    @Override
    public String toString() {
        return "Space";
    }

    public String toString(Ser a) {
        return csp.toString(a);
    }

    public Exp mkConstant(boolean sign) {
        if (sign) {
            return mkTrue();
        } else {
            return mkTrue().flip();
        }
    }

    public String getVarCode(int varId) {
        return getVarSpace().getVarCode(varId).toString();
    }


//    public Space copy(Transformer transformer) {
//        Space c1 = copy();
//        c1.transform(transformer);
//        return c1.copy();
//    }

    public Space copy() {
        throw new UnsupportedOperationException();
//        String serialize = serialize();
//        Space copy = new Space(serialize);
//        copy.sortConstraintsByStrLen();
//        return copy;
    }


    public Space copyReduce(VarSet varsToKeep) {
        return new Space(varsToKeep.toVarCodeSet());
    }

    public List<Exp> getComplexConstraints() {
        throw new IllegalStateException();
    }


    private void logConstraintSolved(Exp before) {
        System.err.println("Constraint Solved:" + before);
    }


//
//    public Set<String> getPrefixes() {
//        return getPrefixGroups().getPrefixes();
//    }


//
//    public Exp getLargestNonXorNonColorConstraintByVarCount() {
//        return csp.getLargestNonXorNonColorConstraintByVarCount();
//    }
//
//    public int getLargestConstraintVarCount() {
//        return csp.getLargestConstraintVarCount();
//    }
//
//    public Exp getLargestConstraintBasedOnVarCount() {
//        return csp.getLargestComplexConstraintBasedOnVarCount();
//    }
//
//
//    public Exp getLargestNonXorConstraint() {
//        return csp.getLargestNonXorConstraint();
//    }


    public List<Var> getVarsOfType(String prefix) {
        ArrayList<Var> aa = new ArrayList<Var>();
        for (Var var : getVarList()) {
            if (var.is(prefix)) {
                aa.add(var);
            }

        }
        return aa;
    }

//
//    public Exp getYearXor() {
//        return csp.getYearXor();
//    }
//
//    public Exp getModelXor() {
//        return csp.getModelXor();
//    }
//
//    public Exp getInteriorColorXor() {
//        return csp.getInteriorColorXor();
//    }
//
//    public Exp getExteriorColorXor() {
//        return csp.getExteriorColorXor();
//    }


//    public void addVVConstraints(PosOp op1, HashSet<VarCodePair> varCodePairs) {
//        getSpaceCsp();
//        spaceCsp.addVVConstraints(op1, varCodePairs);
//    }


    public void printVarMap() {
        System.err.println("VarMap: ");
        VarSpace varMap = getVarSpace();
        varMap.print();
    }


    public static boolean isAndVar(String varCode) {
        return varCode.startsWith("AND__");
    }

    public VarSet mkVarSet(String varCodes) {
        return parseVarCodes2(varCodes);
    }

    public VarSet parseVarCodes2(String varCodes) {
        String[] a = parseVarCodes(varCodes);
        VarSetBuilder b = newMutableVarSet();
        b.addVarCodes(a);
        return b.build();
    }

    public static String[] parseVarCodes(String ss) {
        String[] a = ss.split(" ");
        ImmutableSet<String> set = ImmutableSet.copyOf(a);
        ArrayList<String> list = new ArrayList<String>(set);
        String[] aa = new String[list.size()];
        list.toArray(aa);
        Arrays.sort(aa);
        return aa;
    }


    /**
     * args should be all complex
     *
     * @param args
     * @return
     */
    public Exp mkFormula(Iterable<Exp> args) {
        assert Exp.isAllComplex(args);
        return expFactory.mkFormula(args);
    }

    public Exp mkBinaryOr(Exp arg1, Exp arg2) {
        return expFactory.mkBinaryOr(arg1, arg2);
    }


    public Exp mkPosComplex(IArgBuilder b) {
        return posComplexSpace.mkExp(b);
    }

    /**
     * should only be called from parser or SimplifyTest
     */
    public Exp mkPosComplex(PosOp op, Iterable<Exp> args) {
        return expFactory.mkPosComplex(op, args);
    }


    public Exp mkCube(final VarSet vars, final VarSet trueVars) {
        return expFactory.mkCubeExp(vars, trueVars);
    }

    public Exp mkCube(final Lit lit1, final Lit lit2) {
        if (lit1.vr == lit2.vr) {
            if (lit1.sign == lit2.sign) {
                return lit2;
            } else {
                return mkFalse();
            }
        } else {
            ArgBuilder b = argBuilder(Op.Cube);
            b.addExp(lit1);
            b.addExp(lit2);
            Exp exp = b.mk();
            assert exp.isCubeExp();
            return exp;
        }
    }


    public Exp getAtVarsAsCube() {
        VarSet atVars = getAtVars();
        return mkPCube(atVars);
    }

    public Exp mkPCube(VarSet vars) {
        return mkCube(vars, true);
    }

    public Exp mkNCube(VarSet vars) {
        return mkCube(vars, false);
    }

    public Exp mkCube(VarSet vars, boolean sign) {
        return expFactory.mkCubeExp(vars, sign);
    }

    public Exp mkCube(String sLits) {
        Exp exp = parser.parseLitsToExp(sLits);
        assert exp.isCube();
        return exp;
    }


    public Exp mkDAnd(Iterable<? extends Exp> args) {
        return expFactory.mkExp(Op.DAnd, args);
    }

    public Exp mkCube(Iterable<Exp> args) {
        assert (Exp.isAllLits(args));
        return expFactory.argBuilder(Op.Cube).addExpIt(args).mk();
    }

    public Exp mkBinaryImp(Exp lhs, Exp rhs) {
        return expFactory.mkBinaryImp(lhs, rhs);
    }


    public Exp mkBinaryNand(Exp lhs, Exp rhs) {
        return expFactory.mkBinaryNand(lhs, rhs);
    }

//    public Exp mkBinaryNand(Exp lhs, Exp rhs) {
//        return expFactory.argBuilder(Op.Nand).add(lhs);
//    }

    public Exp mkBinaryNand(Var lhs, Var rhs) {
        return expFactory.mkBinaryNand(lhs.pLit(), rhs.pLit());
    }


    public Exp mkXor(List<Exp> lits) {
        return expFactory.mkXor(lits);
    }

    public Exp mkXor(VarSet vars) {
        return expFactory.mkXor(vars);
    }


    public Exp mkAnd(Exp arg1, Exp arg2) {
        return expFactory.mkBinaryAnd(arg1, arg2);
    }


    public Exp mkAnd(Exp... args) {
//        return expFactory.mCube(args);

        ArgBuilder b = argBuilder(Op.And);
        for (Exp arg : args) {
            b.addExp(arg);
        }
        return b.mk();
    }

    public Exp mkAnd(Iterable<? extends Exp> args) {
        return expFactory.mkAnd(args);
    }


    public Exp mkAnd(String argList) {
        return parser.parseExp("and(" + argList + ")");
    }

    public Exp mkAnd(DynCube c, DynComplex f) {
        return expFactory.mkCubeExp(c, f);
    }


    public static ImmutableList<Exp> fixArgs(Exp[] rawArgs) {
        ImmutableSortedSet<Exp> set = ImmutableSortedSet.copyOf(rawArgs);
        return ImmutableList.copyOf(set);
    }

    public static ImmutableList<Exp> fixArgs(Iterable<Exp> rawArgs) {
        ImmutableSortedSet<Exp> set = ImmutableSortedSet.copyOf(rawArgs);
        return ImmutableList.copyOf(set);
    }


    public void collectOrArgs(Iterable<Exp> args, Set<Exp> b) throws TrueOrArg {
        for (Exp arg : args) {
            if (arg.isConstantTrue()) {
                throw new TrueOrArg();
            } else if (arg.isConstantFalse()) {
                //skip
            } else if (arg.isOr()) {
                collectOrArgs(arg.argIt(), b);
            } else {
                b.add(arg);
            }
        }
    }


    public VarSet createVars(Set<Var> vars) {
        return varSpace.createVars(vars);
    }

    public VarSet createVarsFromVarIdSet(Set<Integer> varIds) {
        return varSpace.createVarsFromVarIdSet(varIds);
    }

    public VarSet createVars(String sVars) {
        return createVarSet2(sVars);
    }

    public Cube mkEmptyCube() {
        return emptyCube;
    }

    public void initAllLits() {
        for (Var var : varSpace) {
            var.mkPosLit();
            var.mkNegLit();
        }
    }


    /**
     * msrp_0 .. msrp_32
     */
    public Set<Exp> getLitsForInt32(int int32Value, String int32VarPrefix) {
        LinkedHashSet<Exp> set = new LinkedHashSet<Exp>();
        String s = Integer.toString(int32Value, 2);
        s = lpad(s, '0', 32);
        for (int i = 0; i < 32; i++) {
            char c = s.charAt(i);
            String ss = lpad(i + "", '0', 2);
            String varCode = int32VarPrefix + "_" + ss;
            Var var = getVar(varCode);
            if (c == '1') {
                set.add(var.mkPosLit());
            } else {
                assert c == '0';
                set.add(var.mkNegLit());
            }
        }
        return set;
    }

    public Cube getCubeForInt32(int int32Value, String int32VarPrefix) {
        Set<Exp> lits = getLitsForInt32(int32Value, int32VarPrefix);
        DynCube cube = new DynCube(this);
        for (Exp lit : lits) {
            cube.assign(lit.asLit());
        }
        return cube;
    }

    /**
     * Create a DAnd consisting of 32 lits
     */
    public Exp mkInt32AndExp(int int32Value, String int32VarPrefix) {
        Set<Exp> lits = getLitsForInt32(int32Value, int32VarPrefix);
        ArgBuilder b = new ArgBuilder(this, Op.DAnd, lits);
        return b.mk();
    }

    public Exp mkDealerCodeAnd(int int32Value) {
        return mkInt32AndExp(int32Value, DLR_PREFIX);
    }

    public Exp mkDealerCodesOrInt32(Set<Integer> dealerCodes) {
        ArgBuilder b = new ArgBuilder(this, Op.Or);
        for (Integer dealerCode : dealerCodes) {
            Exp dcAnd = mkDealerCodeAnd(dealerCode);
            b.addExp(dcAnd);
        }
        return b.mk();
    }


//    public Exp mkDealerCodesOrVarPerDealer(Set<Integer> dealers) {
//        ArgBuilder b = new ArgBuilder(Op.Or);
//        for (Integer dealer : dealers) {
//            String varCode = Dealers.convertDealerIntToVarCode(dealer);
//            Var vr = getVr(varCode);
//            b.add(vr.mkPosLit());
//        }
//        return b.mk(this);
//    }

//    public VarSet mkDealerVars(Set<Integer> dealers) {
//        VarSetBuilder b = varSetBuilder();
//        for (Integer dealer : dealers) {
//            String varCode = Dealers.convertDealerIntToVarCode(dealer);
//            Var vr = getVr(varCode);
//            b.addVar(vr);
//        }
//        return b.build();
//    }

    public Exp mkMsrp32BitAndExp(int int32Value) {
        return mkInt32AndExp(int32Value, MSRP_PREFIX);
    }


    public VarSet getInt32Vars() {
        ImmutableList<String> int32VarPrefixes = VarSpace.getInt32VarPrefixes();
        return getVarsByPrefix(int32VarPrefixes);
    }

    public static String getMsrpBucketVarCode(int msrpBucket) {
        return MSRP_PREFIX + "_" + msrpBucket;
    }

    public static String getMsrpStrictVarCode(int msrp) {
        return MSRP_PREFIX + "_" + msrp;
    }

    public Var getMsrpBucketVar(int msrpBucket) {
        String varCode = getMsrpBucketVarCode(msrpBucket);
        return getVar(varCode);
    }

    public Var getMsrpStrictVar(int msrp) {
        String varCode = getMsrpStrictVarCode(msrp);
        return getVar(varCode);
    }

    public VarInfo getVarInfo() {
        return varMeta;
    }

    public void clearPics() {
        pics = null;
    }

    public void setMetaVar(MetaVar metaVar) {
        this.metaVar = metaVar;

        VarSet vars = getVars();
        for (Var var : vars) {
            String varCode = var.getVarCode();
            MetaVar meta = metaVar.getByCode(varCode);
            var.setMeta(meta);
        }

    }

    public MetaVar getMetaVar() {
        return metaVar;
    }

    public int getPrefixOccurrenceCountLight(String sPrefix) {
        return parser.getParseCounter().getPrefixCounts().get(sPrefix);
    }

    public boolean hasPrefixes() {
        for (Var var : varSpace) {
            if (var.getVarCode().startsWith(Prefix.SER.getName())) return true;
        }
        return false;
    }

    public void printPosComplexTableReport() {
        posComplexSpace.printPosComplexTableReport();
    }

    public ExpFactory getExpFactory() {
        return expFactory;
    }

    @NotNull
    public ArgBuilder argBuilder(@NotNull Op op) {
        return expFactory.argBuilder(op);
    }

    @NotNull
    public Var mkVar(String varCode) {
        return varSpace.mkVar(varCode);
    }

    @NotNull
    public Var mkVar(Var vr) {
        return mkVar(vr.getVarCode());
    }


    public void mkVars(Iterable<String> varCodes) {
        varSpace.mkVars(varCodes);
    }

    public Csp getCsp() {
        if (csp == null) {
            csp = new Csp(this);
        }
        return csp;
    }


//    public static class Cube implements LitSet, DAnd {
//
//    }
//
//    public static class Clause implements LitSet, Or {
//
//    }
//
//    public static class DClause extends Clause,Dnnf { //xor
//
//    }
//
//    public static class XClause extends Clause,Dnnf {    //non xor
//
//       }
//
//
//
//    public static class Formula extends And { //all complex children
//         TreeSequence complex;
//         Fcc[] fcss; //null | 0 len | or non-zero len
//    }
//
//    public static class Fcc extends And { //aka fccs
//       //aka fccs
//    }
//
//    public static class Fccs extends DAnd{
//
//    }
//
//    public static class XFormula extends Formula {
//
//    }
//
//
//    public static class Csp1 extends And {
//        //all over lap
//        Cube cube;
//        Formula fCon;
//    }
//
//    //no over lap
//    public static class StableCsp{
//        Cube cube;
//        Formula fCon;
//    }
//
//    public static class DElement1{
//            Cube cube;
//            Fccs fccs;
//        }
//
//    public static DElement2{
//                Cube cube;
//                Fcc fcc;
//            }


    public Exp mkOr(String argList) {
        return parser.parseExp("or(" + argList + ")");
    }

    public Exp mkOr(Exp... rawArgs) {
        if (rawArgs == null) {
            throw new IllegalStateException();
        }
        if (rawArgs.length == 0) {
            return mkFalse();
        }
        ArgBuilder b = new ArgBuilder(this, Op.Or, rawArgs);
        return b.mk();
    }

    public Exp mkOr(Iterable<Exp> rawArgs) {
        if (rawArgs == null) {
            throw new IllegalStateException();
        }
        ArgBuilder b = new ArgBuilder(this, Op.Or, rawArgs);
        return b.mk();
    }

    public Exp mkOr(VarSet rawArgs) {
        VarSet rawArgs1 = rawArgs;
        ArgBuilder b = new ArgBuilder(this, Op.Or, rawArgs1);
        return b.mk();
    }


    public Exp mkDOr(Exp arg1, Exp arg2) {
        return expFactory.mkDOr(arg1, arg2);
    }

    public Exp mkDOr(Set<Exp> args) {
        if (true) throw new UnsupportedOperationException();
        ArgBuilder b = new ArgBuilder(this, Op.DOr, args);
        return b.mk(this);
    }

//    public Exp mkDOr(Iterable<Exp> rawArgs) {
//        assert !(rawArgs instanceof ArgBuilder);
//        return mkOr(new ArgBuilder(Op.DOr, rawArgs));
//    }


    public static boolean checkNoConstants(Iterable<Exp> args) {
        for (Exp arg : args) {
            if (arg.isConstant()) {
                throw new IllegalStateException();
            }
        }
        return true;
    }

    public int getVarId(String varCode) {
        return varSpace.getVar(varCode).getVarId();
    }

    public Set<String> toLitCodes(Iterator<? extends Exp> litIterator) {
        ImmutableSet.Builder<String> b = ImmutableSet.builder();
        while (litIterator.hasNext()) {
            Lit lit = litIterator.next().asLit();
            b.add(lit.toString());
        }
        return b.build();
    }

    public SortedSet<String> toLitCodesSorted(Iterator<? extends Exp> litIterator) {
        return ImmutableSortedSet.copyOf(toLitCodes(litIterator));
    }


    public VarMeta getVarMeta() {
        return varMeta;
    }

    public String getAttribute(Set<String> context, String varName, String attName) {
        return varMeta.getAttribute(context, varName, attName);
    }


    public boolean isInvVar(Var var) {
        return varMeta.isInvVar(var.getVarCode());
    }


    public VarSet computeVars(Iterable<Exp> args) {
        VarSetBuilder b = newMutableVarSet();
        b.addVars(args);
        return b.build();
    }

    public VarSet computeVars(Exp[] args) {
        VarSetBuilder b = newMutableVarSet();
        b.addVars(args);
        return b.build();
    }

    public Set<Var> convert(final VarSet vars) {
        return new AbstractSet<Var>() {
            @Override
            public Iterator<Var> iterator() {
                return vars.varIter();
            }

            @Override
            public int size() {
                return vars.size();
            }
        };
    }

    public DynCube initAssignments() {
        return new DynCube(this);
    }

    public Set<Var> getVarSet() {
        return getVarSpace().getVarSet();
    }


    public static Csp minimize(String text) {
        return Csp.parse(text);
    }

    public Exp mkConstantFalse() {
        return mkFalse();
    }

    public Exp mkConstantTrue() {
        return mkTrue();
    }


    public VarSet getVars(EnumSet<Prefix> filter) {
        return getVars().filter(filter);
    }

    public VarSet getVars(Prefix filter) {
        return getVars().filter(filter);
    }

    public VarSet getVars(String filter) {
        return getVars().filter(filter);
    }

//    public VarsExp mkCube(Assignments a) {
//        PosFixedVarArgs fixedArgs = new PosFixedVarArgs(this, Op.POS_CUBE, args);
//        return fixedArgs.mkExp();
//    }


    public int getLitNodeCount() {
        return varSpace.getLitNodeCount();
    }

    public Space toDnnfSpace() {
        return this;
    }

    public DynCube lits(String... sLits) {
        DynCube a = new DynCube(this);
        for (String sLit : sLits) {
            Lit lit = mkLit(sLit);
            a.assign(lit);
        }
        return a;
    }


    public Exp getExp(int expId) throws IndexOutOfBoundsException {
        if (expId > (_nodes.size() - 1)) {
            throw new IndexOutOfBoundsException("Invalid expId[" + expId + "]");
        }
        return _nodes.get(expId);
    }


    public int getNodeCount() {
        return _nodes.size();
    }


    public Exp getNode(int expId) {
        return getExp(expId);
    }

//    public static Space init(String _complexVars) {
//        String[] split = _complexVars.split(" ");
//        return new Space(split);
//    }


    public VarSet mkEmptyVarSet() {
        return varSpace.mkEmptyVarSet();
    }

    public boolean addNode(Exp exp) {
        assert exp != null;
        return _nodes.add(exp);
    }

    public void ensureVarCodes(Iterable<String> varCodes) throws BadVarCodeException {
        for (String varCode : varCodes) {
            Var var = getVar(varCode);
        }
    }

    public int getSpaceId() {
        return System.identityHashCode(this);
    }


    public static class IsSatContext {
        int solutionCount;
        int failureCount;
        int openCount;

        public void print() {
            System.err.println("solutionCount[" + solutionCount + "]");
            System.err.println("failureCount[" + failureCount + "]");
            System.err.println("openCount[" + openCount + "]");
            System.err.println();
        }
    }


    public void log() {
        log("");
    }

    public void log(String s) {
        log.info(s);
    }


    public static final String TRIM_CLOB = "xor(2513 2514 2531 2532 2552 2540 2554 2545 2546 2550 2560)\n" +
            "xor(L4 V6 Hybrid)\n" +
            "xor(Base LE SE XLE Hyb)\n" +
            "xor(6MT 6AT ECVT)\n" +
            "iff(2531 and(LE L4 6MT))\n" +
            "iff(2546 and(SE L4 6AT))\n" +
            "iff(2513 and(Base L4 6MT))\n" +
            "iff(2545 and(SE L4 6MT))\n" +
            "iff(2554 and(XLE V6 6AT))\n" +
            "iff(2540 and(XLE L4 6AT))\n" +
            "iff(2550 and(SE V6 6AT))\n" +
            "iff(2514 and(Base L4 6AT))\n" +
            "iff(2552 and(LE V6 6AT))\n" +
            "iff(2532 and(LE L4 6AT))\n" +
            "iff(2560 and(Hyb Hybrid ECVT))";


    /**
     * Note: this only works c a "year" cofactor
     */
    public Multimap<Var, Var> extractSeriesModelMultiMap() {
        return csp.computeSeriesModelMultiMap();
    }

    public void printSeriesModels() {
        Multimap<Var, Var> map = extractSeriesModelMultiMap();
        for (Var seriesVar : map.keySet()) {
            System.err.println(seriesVar + ": " + map.get(seriesVar));
        }
    }

//    public void addSeriesModelImps() {
//        csp.addSeriesModelImps();
//    }
//
//
//    public void convertRequiresOrToConflictForModels() {
//        csp.convertRequiresOrToConflictForModels();
//    }

    final public String serializeDimacs() {
        Ser a = new Ser();
        serializeDimacs(a);
        return a.toString();
    }

    final public void serializeDimacs(Ser a) {
        csp.serializeDimacs(a);
    }

    final public String serializeVarMap() {
        Ser a = new Ser();
        serializeVarMap(a);
        return a.toString();
    }

    final public void serializeVarMap(Ser a) {
        VarSpace varMap = getVarSpace();
        varMap.serialize(a);
    }

    public TreeSet<TreeSet<String>> toCnfSortedSet() {
        return csp.toCnfSortedSet();
    }

    public String serializeCnfSortedSet() {
        TreeSet<TreeSet<String>> xx = this.toCnfSortedSet();
        return serializeCnfSortedSet(xx);
    }

    public static String serializeCnfSortedSet(TreeSet<TreeSet<String>> cnf) {
        Ser a = new Ser();
        for (TreeSet<String> clause : cnf) {
            a.ap(serializeClauseSortedSet(clause));
            a.newLine();
        }
        return a.toString().trim();
    }

    public static String serializeClauseSortedSet(TreeSet<String> clause) {
        StringBuilder a = new StringBuilder();
        for (String lit : clause) {
            a.append(lit);
            a.append(' ');
        }
        return a.toString().trim();
    }


    public Collection<Exp> getCoreXorsFromSpace() {
        ArrayList<Exp> retVal = new ArrayList<Exp>();
        retVal.add(getXor(YR_PREFIX));
        retVal.add(getXor(MDL_PREFIX));
        retVal.add(getXor(XCOL_PREFIX));
        retVal.add(getXor(ICOL_PREFIX));
        return ImmutableList.copyOf(retVal);
    }

    public Exp getXor(String prefix) {
        VarSet vars = getVars(prefix);
        return mkXor(vars);
    }

    public String serializeTinyDnnf() {
        return serializeTinyDnnf(new CompileOptions());
    }

    public String serializeTinyDnnf(CompileOptions options) {
        Exp rootNode = null;
        return serializeTinyDnnf(rootNode, options);
    }

    public String serializeTinyDnnf(Exp rootNode, CompileOptions options) {
        Ser a = new Ser();
        serializeTinyDnnf(a, rootNode, options);
        return a.toString();
    }

    final public void serializeTinyCnf(Ser a) {
        csp.serializeTinyCnf(a);
    }


    public static boolean isPotentialAlwaysTrueVars(String varCode) {
        Set<String> set = getPotentialAlwaysTrueVars();
        return set.contains(varCode);
    }

    public static Set<String> getPotentialAlwaysTrueVars() {

        ImmutableSet.Builder<String> b = ImmutableSet.builder();
        b.add(alwaysTrueVars1);
        ImmutableSet<String> retVal = b.build();
        return retVal;
    }

    //Root series model exteriorcolor interiorcolor
    public static final String alwaysTrueVars0Ser = "Root";
    public static final String alwaysTrueVars1Ser = "series model exteriorcolor interiorcolor";
    public static final String[] alwaysTrueVars1 = {"Root", "series", "model", "exteriorcolor", "interiorcolor"};
    public static final String[] alwaysTrueVars2 = {"options"};
    public static final String[] alwaysTrueVars3 = {"transmission", "drive", "grade"};
    public static final String[] alwaysTrueVars4 = {"bed", "cab"};


    public boolean containsVarCode(String varCode) {
        return getVarSpace().containsVarCode(varCode);
    }


    private static TreeSet<TreeSet<String>> computeSubsumedClauses(TreeSet<TreeSet<String>> cnf) {

        TreeSet<TreeSet<String>> subsumed = new TreeSet<TreeSet<String>>(new ClauseSortedSetComparator());

        for (TreeSet<String> clauseToMaybeDelete : cnf) {
            boolean canDelete = canClauseBeDeleted(cnf, clauseToMaybeDelete);
            if (canDelete) {
                subsumed.add(clauseToMaybeDelete);
            }
        }

        return subsumed;

    }

    /**
     * Can this clause be deleted
     * aka is this clause subsumed by a smaller (subset) clause?
     */
    private static boolean canClauseBeDeleted(TreeSet<TreeSet<String>> cnf, TreeSet<String> clauseToMaybeDelete) {
        for (TreeSet<String> clause : cnf) {

            if (clause.equals(clauseToMaybeDelete)) {
                continue;
            }

            if (clauseToMaybeDelete.containsAll(clause)) {
                assert clause.size() < clauseToMaybeDelete.size();
                return true;
            }
        }

        return false;
    }


    public boolean isDirectlyRelated(Exp e1, Exp e2) {
        return e1.anyVarOverlap(e2);
    }


    public static Set<String> varsToCodes(Iterable<? extends IVar> vars) {
        ImmutableSet.Builder<String> b = ImmutableSet.builder();
        for (IVar var : vars) {
            b.add(var.getVarCode());
        }
        return b.build();
    }

    public Set<String> varsToCodes(VarSet vars) {
        return vars.toVarCodeSet();
    }

    public static SortedSet<String> varsToCodesSorted(Iterable<? extends IVar> vars) {
        return ImmutableSortedSet.copyOf(varsToCodes(vars));
    }

    public SortedSet<String> varsToCodesSorted(VarSet vars) {
        return ImmutableSortedSet.copyOf(varsToCodes(vars));
    }

    public static Set<String> varsToCodes(Var[] vars) {
        ImmutableSet.Builder<String> b = ImmutableSet.builder();
        for (IVar var : vars) {
            b.add(var.getVarCode());
        }
        return b.build();
    }


    public static Logger log = createLogger(Space.class);

    public static Logger getLogger() {
        return log;
    }

    public static Logger createLogger(Class cls) {
        Logger tmp = Logger.getLogger(cls.getName());
        Handler[] handlers = tmp.getParent().getHandlers();
        for (Handler handler : handlers) {
            handler.setFormatter(new SingleLineLogFormatter());
        }
        return tmp;
    }

    public static Exp min(Exp arg1, Exp arg2) {
        if (arg1.getExpId() < arg2.getExpId()) {
            return arg1;
        } else {
            return arg2;
        }
    }

    public static Exp max(Exp arg1, Exp arg2) {
        if (arg1.getExpId() > arg2.getExpId()) {
            return arg1;
        } else {
            return arg2;
        }
    }


//    public VarSet computeFormulaVars2(Iterable<Exp> complex) {
//        VarSetBuilder b = newMutableVarSet();
//        if (complex.isEmpty()) {
//            return varSpace.mkEmptyVarSet();
//        }
//
//        for (Exp constraint : complex) {
//            b.addVars(constraint.get_complexVars());
//        }
//        return b.build();
//    }


    public EnumSet<Prefix> computeOpenCoreXorPrefixes(VarSet fVars) {
        EnumSet<Prefix> set = EnumSet.noneOf(Prefix.class);

        if (fVars == null) {
            return set;
        }

        IntIterator it = fVars.intIterator();
        while (it.hasNext()) {
            int varId = it.next();
            Var var = getVar(varId);
            Prefix prefix = var.getPrefix2();
            if (prefix != null && prefix.isCore()) {
                set.add(prefix);
            }
            if (set.size() >= 4) {
                return set;
            }
        }

        return set;
    }


    public VarSet getVars(VarGrp key) {
        VarFilter varFilter = mkVarFilter(key);
        return getVars(varFilter);
    }

    public VarSet getVars(VarFilter filter) {
        VarSetBuilder b = newMutableVarSet();
        for (Var var : getVarList()) {
            if (filter.accept(var)) {
                b.addVar(var);
            }
        }
        return b.build();
    }

    public VarSet getVarsByPrefix(String... prefixes) {
        VarFilter filter = VarFilter.prefixes(prefixes);
        return getVars(filter);
    }

    public VarSet getVarsByPrefix(Iterable<String> prefixes) {
        VarFilter filter = VarFilter.prefixes(prefixes);
        return getVars(filter);
    }

    @Nullable
    public Prefix getBestCoreXorPrefix(VarSet formulaVars) {
        EnumSet<Prefix> corePrefixes = computeOpenCoreXorPrefixes(formulaVars);
        if (corePrefixes == null || corePrefixes.isEmpty()) return null;
        return getBestCoreXorPrefixByScore(corePrefixes);
    }

    @Nonnull
    public Prefix getBestCoreXorPrefixByScore(EnumSet<Prefix> corePrefixes) {
        checkNotNull(corePrefixes);
        Prefix best = null;
        int bestScore = 0;
        for (Prefix prefix : corePrefixes) {
            if (prefix.getScore() > bestScore) {
                best = prefix;
                bestScore = best.getScore();
            }
        }
        assert best != null;
        return best;
    }

    public CoreXor getBestCoreXor(VarSet formulaVars) {
        Prefix prefix = getBestCoreXorPrefix(formulaVars);
        if (prefix != null) {
            return createCoreXor(prefix, formulaVars);
        } else {
            return null;
        }
    }


    public CoreXor createCoreXor(Prefix prefix, VarSet fVars) {
        VarSet xorVars = fVars.filter(prefix);
        return new CoreXor(prefix.getName(), xorVars);
    }


//    public Var decide(Iterable<Exp> complex) {
//        FVars fVars = computeFVars(complex);
//        FVar decide = fVars.decide();
//        int varId = decide.getVr();
//        return this.getVr(varId);
//    }


    public SortedSet<String> createVarCodeSet(Set<Var> vars) {
//        if (true) throw new UnsupportedOperationException("tmp"); //todo
        ImmutableSortedSet.Builder<String> b = ImmutableSortedSet.naturalOrder();
        for (Var var : vars) {
            b.add(var.getVarCode());
        }
        return b.build();
    }


    public List<Lit> createLitList(Iterable<String> sLits) {
        ImmutableList.Builder<Lit> b = ImmutableList.builder();
        for (String sLit : sLits) {
            Lit lit = mkLit(sLit);
            b.add(lit);
        }
        return b.build();
    }


    public List<Lit> createLitList(String[] sLits) {
        ImmutableList.Builder<Lit> b = ImmutableList.builder();
        for (String sLit : sLits) {
            Lit lit = mkLit(sLit);
            b.add(lit);
        }
        return b.build();
    }


    public List<Lit> createLitList(String sLits) {
        if (sLits == null || sLits.trim().length() == 0) return ImmutableList.of();

        if (sLits.startsWith("and(") && sLits.endsWith(")")) {
            //cube
            sLits = sLits.replace("and(", "").replace(")", "");
        }

        if (sLits.startsWith("or(") && sLits.endsWith(")")) {
            //cube
            sLits = sLits.replace("or(", "").replace(")", "");
        }

        Iterable<String> lits = MY_SPLITTER.split(sLits);

        ArrayList<Lit> aa = new ArrayList<Lit>();

        for (String sLit : lits) {
            Lit lit = this.mkLit(sLit);
            if (lit != null && !aa.contains(lit)) {
                aa.add(lit);
            }

        }

        return ImmutableList.copyOf(aa);
    }


//    public List<Lit> createLitList(String sLits) {
//        if (sLits == null || sLits.trim().length() == 0) return ImmutableList.of();
//
//        if (sLits.startsWith("and(") && sLits.endsWith(")")) {
//            //cube
//            sLits = sLits.replace("and(", "").replace(")", "");
//        }
//
//        if (sLits.startsWith("or(") && sLits.endsWith(")")) {
//            //cube
//            sLits = sLits.replace("or(", "").replace(")", "");
//        }
//
//        Iterable<String> lits = MY_SPLITTER.split(sLits);
//
//        ArrayList<Lit> aa = new ArrayList<Lit>();
//
//        for (String sLit : lits) {
//            Lit lit = this.mkLit(sLit);
//            if (!aa.contains(lit)) {
//                aa.add(lit);
//            }
//
//        }
//
//        return ImmutableList.copyOf(aa);
//    }


    public VarFilter mkVarFilter(VarGrp key) {
        Prefix p = Prefix.getPrefix(key);
        if (p != null) {
            return VarFilter.prefix(p);
        }

        EnumSet<Prefix> grp = Prefix.getGroup(key);
        if (grp != null && !grp.isEmpty()) {
            return VarFilter.prefixes(grp);
        }

        if (key.equals(VarGrp.FIO)) {
            return VarFilter.fio(this);
        }


        System.err.println("badVarFilterKey[" + key + "]");
        return null;

    }

    public static Set<Var> getTrueUserPics(Collection<Lit> userPics) {
        ImmutableSet.Builder<Var> b = ImmutableSet.builder();
        for (Lit pic : userPics) {
            if (pic.isPos()) {
                b.add(pic.getVr());
            }
        }
        return b.build();
    }

    public SortedSet<String> getFioVarsCodes() {
        ImmutableSet<Var> fioVars = getFioVars();
        return this.createVarCodeSet(fioVars);
    }

    public ImmutableSet<Var> getFioVars() {
        varMeta.checkVarInfo();
        ImmutableSet.Builder<Var> b = ImmutableSet.builder();
        for (Var var : varSpace) {
            if (varMeta.isFio(var)) {
                assert var.isAcy();
                b.add(var);
            }
        }
        return b.build();
    }


    public Set<Lit> createLitSet(Iterable<String> sLits) {
        ImmutableSet.Builder<Lit> b = ImmutableSet.builder();
        for (String sLit : sLits) {
            Lit lit = mkLit(sLit);
            b.add(lit);
        }
        return b.build();
    }

    public VarSet createVarSet(Set<Var> vars) {
        VarSetBuilder b = newMutableVarSet();
        b.addVars(vars);
        return b.immutable();
    }

    public VarSet createVarSet(String sVarCodes) {
        Iterable<String> it = Vars.parseVarList(sVarCodes);
        return createVarSet(it);
    }

    public Set<Var> createVarSet(String... varCodes) {
        if (varCodes == null || varCodes.length == 0) return ImmutableSet.of();
        ImmutableSet.Builder<Var> b = ImmutableSet.builder();
        for (String varCode : varCodes) {
            String code = Head.getVarCode(varCode);
            Var dVar = getVar(code);
            b.add(dVar);
        }
        return b.build();
    }

    public VarSet createVarSet2(String sVars) {

        if (sVars == null || sVars.trim().length() == 0) return mkEmptyVarSet();

        if (sVars.startsWith("and")) {
            //cube
            sVars = sVars.replace("and(", "").replace(")", "");
        }

        Iterable<String> varCodes = MY_SPLITTER.split(sVars);

        VarSetBuilder b = newMutableVarSet();
        for (String varCode : varCodes) {
            Var var = getVar(varCode);
            b.addVar(varCode);
        }
        return b.build();
    }


    public VarSet createVarSet(Iterable<String> varCodes) {
        VarSetBuilder b = newMutableVarSet();
        for (String varCode : varCodes) {
            Var var = getVar(varCode);
            b.addVar(varCode);
        }
        return b.build();
    }

    public Set<Cube> computeFixList(Exp baseConstraint, VarSet softPicks, Var hardPick) {

        Exp baseConstraintReduced = baseConstraint.reduce();

        Exp hard = baseConstraintReduced.con(hardPick);
        Exp soft = hard.project(softPicks);
        Exp minFModels = soft.minFModels();

        return minFModels.getCubesSmooth();

    }


//    public Set<Var> getPotentialAlwaysTrueVars() {
//        Set<String> set = Space.getPotentialAlwaysTrueVars();
//        ImmutableSet.Builder<Var> b = ImmutableSet.builder();
//        for (String varCode : set) {
//            if (this.containsVarCode(varCode)) {
//                Var dVar = getVr(varCode);
//                b.add(dVar);
//            }
//        }
//        return b.build();
//    }


    public Set<Var> createVarSet(int... ids) {
        ImmutableSet.Builder<Var> b = ImmutableSet.builder();
        for (int id : ids) {
            try {
                Var var = getVar(id);
                b.add(var);
            } catch (BadVarCodeException e) {
                log.warning(e.getMessage());
            }
        }
        return b.build();
    }


    public Set<Var> createVarSetFromCodes(Iterable<String> varCodes) {
        ImmutableSet.Builder<Var> b = ImmutableSet.builder();
        for (String varCode : varCodes) {
            try {
                Var var = getVar(varCode);
                b.add(var);
            } catch (BadVarCodeException e) {
                log.warning(e.getMessage());
            }
        }
        return b.build();
    }

    public Set<Var> createVarSetFromCodes(String... varCodes) {
        ImmutableSet<String> strings = ImmutableSet.copyOf(varCodes);
        return createVarSetFromCodes(strings);
    }

    public Set<Var> createVarSetFromLitSet(Set<Lit> lits) {
        ImmutableSet.Builder<Var> b = ImmutableSet.builder();
        for (Lit lit : lits) {
            try {
                Var var = lit.getVr();
                b.add(var);
            } catch (BadVarCodeException e) {
                log.warning(e.getMessage());
            }
        }
        return b.build();
    }

//    public Set<Var> get_complexVars(VarGrp key) {
//        VarFilter varFilter = mkVarFilter(key);
//        return get_complexVars(varFilter);
//    }
//
//
//    public void _complexVars(VarGrp key) {
//        Set<Var> _complexVars = get_complexVars(key);
//        String sVars = ParseUtil.serializeVars(_complexVars);
//        System.err.println(sVars);
//    }
//
//    public Set<Var> get_complexVars(VarFilter filter) {
//        Set<Var> _complexVars = get_complexVars();
//        ImmutableSet.Builder<Var> b = ImmutableSet.builder();
//        for (Var vr : _complexVars) {
//            if (filter.accept(vr)) {
//                b.add(vr);
//            }
//        }
//        return b.build();
//    }


    public static int computeBucketIndex(int contentHash, int tableLength) {
        return contentHash & (tableLength - 1);
    }

    public VarSet mkVarSet(int varId1, int varId2) {
        Var var1 = getVar(varId1);
        Var var2 = getVar(varId2);
        return mkVarPair(var1, var2);
    }

    public VarSet mkVarSet(ConditionOn con) {
        if (con instanceof Lit) {
            return ((Lit) con).getVars();
        } else if (con instanceof Cube) {
            return ((Cube) con).getVars();
        } else {
            throw new IllegalStateException();
        }
    }

    public VarSet mkVarSet(int varId) {
        Var var = getVar(varId);
        return var.mkSingletonVarSet();
    }

//    public VarSet mkVarSet(VarSet vs) {
//        if (vs.getSpace() == this) return vs;
//        VarSetBuilder b = varSetBuilder();
//        for (Var vr : vs) {
//            Var vv = getVr(vr);
//            b.add(vv);
//        }
//        return b.build();
//    }

    public VarSet mkVarPair(Var var1, Var var2) {
        return varSpace.mkVarPair(var1, var2);
    }


    public Exp getRootDNode() {
        return dnnf.getBaseConstraint();
    }

    public Exp getLastNode() {
        return _nodes.get(_nodes.size() - 1);
    }

    public void serializeVarMapTinyDnnf(Ser a) {
        VarSpace varMap = getVarSpace();
        a.append(Vars.HEAD_VARS_LINE);
        Iterator<Var> it = varMap.iterator();
        while (it.hasNext()) {
            Var var = it.next();
            a.append(var.getVarCode());
            if (it.hasNext()) {
                a.argSep();
            }
        }
        a.append(")");
    }


    public void serializeTinyDnnf(Ser a) {
        serializeTinyDnnf(a, null, new CompileOptions());
    }

    public void serializeTinyDnnf(Ser a, Exp rootNode, CompileOptions options) {
        if (options.varsLine) {
            serializeVarMapTinyDnnf(a);
            a.newLine();
        }
//        if (options.invVarsLine) {
//            serializeInvVarMapTiny(a);
//            a.newLine();
//        }
        serializeDag(a, rootNode);
    }

    private void serializeDag(Ser a, Exp rootNode) {
        int L = _nodes.size();
        for (int i = 2; i < L; i++) {
            Exp n = _nodes.get(i);
            assert n != null;
            assert n.isDnnf() : "Not dnnf: " + n.getClass().getSimpleName() + ": " + n;
            n.serializeTinyDnnf(a);
            a.newLine();
            if (rootNode != null && n == rootNode) {
                break;
            }
        }
    }


    public static Set<String> createInt32VarCodes(String intVarPrefix) {
        return VarSpace.createInt32VarCodes(intVarPrefix);
    }

    public static boolean isInt32(String prefix) {
        return VarSpace.isInt32(prefix);
    }

    public static ImmutableList<String> getInt32VarPrefixes() {
        return VarSpace.getInt32VarPrefixes();
    }

    public static Set<String> createInt32VarCodes() {
        return VarSpace.createInt32VarCodes();
    }

    public boolean check() {
        for (int i = 0; i < _nodes.size(); i++) {
            Exp exp = _nodes.get(i);
            if (exp.getExpId() != i) {
                System.err.println("Failed: ");
                System.err.println("  _nodes.index[" + i + "] != expId[" + exp.getExpId() + "]: " + exp);

                if (i != 0) {
                    Exp previous = _nodes.get(i - 1);
                    System.err.println("Previous: ");
                    System.err.println("  _nodes.index[" + (i - 1) + "] != expId[" + previous.getExpId() + "]: " + previous);
                }

                System.err.println();
                throw new IllegalStateException();
            }
        }
        return true;
    }


    public Set<Cube> computeFixList(Exp baseConstraint, VarSet softPicks, List<String> hardPick) {

        Exp baseConstraintReduced = baseConstraint.reduce();

        Exp hard = baseConstraintReduced.con(hardPick.toArray(new String[hardPick.size()]));
        Exp soft = hard.project(softPicks);
        Exp minFModels = soft.minFModels();

        return minFModels.getCubesSmooth();
    }

    public Exp computeFixListExp(Exp baseConstraint, VarSet softPicks, List<String> hardPick) {
        long t0 = System.currentTimeMillis();

        long t1 = System.currentTimeMillis();
        Exp baseConstraintReduced = baseConstraint;//.reduce();
        log.info("Exp:computeFixListExp:reduce: " + (System.currentTimeMillis() - t1) + " ms");

        long t2 = System.currentTimeMillis();
        Exp hard = baseConstraintReduced.con(hardPick.toArray(new String[hardPick.size()]));
        log.info("Exp:computeFixListExp:condition: " + (System.currentTimeMillis() - t2) + " ms");

        long t3 = System.currentTimeMillis();
        Exp soft = hard.project(softPicks);
        log.info("Exp:computeFixListExp:project: " + (System.currentTimeMillis() - t3) + " ms");

        Exp minFModels = null;
        if (!soft.isTrue()) {
//	    	minFModels = soft.minFModels();
            long t4 = System.currentTimeMillis();
            minFModels = soft.minModels();
            log.info("Exp:computeFixListExp:minModels: " + (System.currentTimeMillis() - t4) + " ms");
        }

//        cspLog("Exp:computeFixListExp:overall: " + (System.currentTimeMillis() - t0) + " ms");
        return minFModels;
    }
//	public Set<Cube> computeFixListAdds(Exp baseConstraint, VarSet softPicks, Var hardPick) {
//
//	    Exp baseConstraintReduced = baseConstraint.reduce();
//
//	    Exp hard = baseConstraintReduced.con(hardPick);
//	    Exp soft = hard.project(softPicks);
//
//	    Exp minFModels = soft;
//	    try{
//	    	minFModels = soft.minFModels();
//	    } catch(IllegalStateException e ){
//
//	    }
//
//	    return minFModels.getCubes();
//	}

    public List<String> computeFixListRemoves(Exp baseConstraint, VarSet softPicks, List<String> hardPick) {

        long t0 = System.currentTimeMillis();

        long t1 = System.currentTimeMillis();
        Exp baseConstraintReduced = baseConstraint;//.reduce();
        log.info("Exp:computeFixListRemoves:reduce: " + (System.currentTimeMillis() - t1) + " ms");

        long t2 = System.currentTimeMillis();
        Exp hard = baseConstraintReduced.con(hardPick.toArray(new String[hardPick.size()]));
        log.info("Exp:computeFixListRemoves:condition: " + (System.currentTimeMillis() - t2) + " ms");

        long t3 = System.currentTimeMillis();
        Exp soft = hard.project(softPicks);
        log.info("Exp:computeFixListRemoves:project: " + (System.currentTimeMillis() - t3) + " ms");

        List<String> falseTypes = new ArrayList<String>();
        if (!soft.isTrue()) {
            long t4 = System.currentTimeMillis();
            for (Cube c : soft.minFModels().getProducts()) {
                for (String s : c.getFalseVarCodes()) {
                    falseTypes.add(s);
                }
            }
            log.info("Exp:computeFixListRemoves:getFalseCodes: " + (System.currentTimeMillis() - t4) + " ms");

        }

        log.info("Exp:computeFixListRemoves:overall: " + (System.currentTimeMillis() - t0) + " ms");
        return falseTypes;
    }

    public Cube computePrefix(List<Lit> pics) {
        if (pics == null) {
            return null;
        }
        Set<Lit> picSet = ImmutableSet.copyOf(pics);

        DynCube aa = new DynCube(this);
        for (Lit lit : picSet) {
            aa.assign(lit);
        }

        return new DynCube(this, (Cube) aa);
    }

//    public native void cspLog(String value) /*-{
//        top.console.info(value);
//    }-*/;


    public Set<String> createContext(Var yr, Var mdl) {
        Set<String> ctx = new HashSet<String>();
        ctx.add(mdl.getVarCode());
        ctx.add(yr.getVarCode());
        String seriesVarCode = varMeta.getImpliedVarCode(ctx, "series");
        if (seriesVarCode != null && !seriesVarCode.equals("")) {
            ctx.add(seriesVarCode);
        }
        return ctx;
    }


    /**
     * YR,MDL,XCOL,ICOL,CoreAcy
     * where CoreAcy = ACY and (dio or (pio and !alaCarte)
     */
    public VarSet getCoreVars() {
        return getVars().filter(new VarFilter() {
            @Override
            public boolean accept(Var var) {
                return getVarMeta().isCoreVar(var.getVarCode());
            }
        });
    }

    public VarSet getUnVtcVars() {
        return getVars().filter(new VarFilter() {
            @Override
            public boolean accept(Var var) {
                return !getVarMeta().isVtc(var.getVarCode());
            }
        });
    }

    public VarSet getVtcVars() {
        return getVars().filter(new VarFilter() {
            @Override
            public boolean accept(Var var) {
                return getVarMeta().isVtc(var.getVarCode());
            }
        });
    }

    public static Exp compilePl2Dnnf(String plText, CompileOptions options) {
        Csp csp = Csp.parse(plText);
        if (options.at) {
            csp.simplifyAlwaysTrueVars();
        }
        Exp dnnf = csp.toDnnf();
        if (options.gc) {
            dnnf = dnnf.gc();
        }
        return dnnf;
    }

    public static String compilePl2DnnfAndSerialize(String plText, CompileOptions options) {
        Exp dnnf = compilePl2Dnnf(plText, options);
        if (options.serializationFormat == CompileOptions.SerializationFormat.TINY) {
            return dnnf.getSpace().serializeTinyDnnf(options);
        } else if (options.serializationFormat == CompileOptions.SerializationFormat.XML) {
            return dnnf.toXml();
        } else {
            throw new IllegalStateException();
        }
    }

    public static String compilePl2DnnfAndSerialize(String plText) {
        return compilePl2DnnfAndSerialize(plText, new CompileOptions());
    }

    public void addAll(List<Exp> lits) {
        throw new IllegalStateException();
    }

//    public Csp mkCsp(Iterable<Exp> constraints) {
//        return expFactory.mkCsp(constraints);
//    }


    public void print() {
        System.err.println("VarSpace: " + (varSpace == null ? "null" : varSpace.size()));
        if (varSpace != null) varSpace.print();

        if (csp != null) csp.print();
        System.err.println("nodeCount: " + _nodes.size());
    }

    public Exp parseTinyDnnf(String tinyDnnfClob) {
        return Parser.parseTinyDnnf(tinyDnnfClob);
    }

//    public Csp parseCsp(String clob) {
//        Sequence<String> lines = parser.parseLines(clob);
//        return new Csp(this, lines);
//    }

//    public Sequence<String> parseLines(String clob) {
//        return parser.pa(clob);
//    }


    public Sequence<Exp> parsePL(String clob) {
        return parser.parsePL(clob);
    }

    public DynComplex mkComplex() {
        return new DynComplex(this);
    }

    public DynCube mkSimple() {
        return new DynCube(this);
    }

}