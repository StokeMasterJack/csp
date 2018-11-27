package com.smartsoft.csp.parse;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.smartsoft.csp.ast.*;
import com.smartsoft.csp.dnnf.products.VarPredicate;
import com.smartsoft.csp.util.BadVarCodeException;
import com.smartsoft.csp.util.BadVarIdException;
import com.smartsoft.csp.util.HasVarId;
import com.smartsoft.csp.util.it.Its;
import com.smartsoft.csp.varSets.Converter;
import com.smartsoft.csp.varSets.EmptyVarSet;
import com.smartsoft.csp.varSets.VarSet;
import com.smartsoft.csp.varSets.VarSetBuilder;
import com.smartsoft.csp.varCodes.VarCode;
import com.smartsoft.csp.varCodes.VarCodes;
import org.jetbrains.annotations.NotNull;

import java.util.*;

import static com.google.common.base.Preconditions.checkState;
import static com.smartsoft.csp.ssutil.Console.prindent;

public class VarSpace implements PLConstants, Iterable<Var> {

    private static final ImmutableList<String> int32VarPrefixes = ImmutableList.of(MSRP_PREFIX, DLR_PREFIX);   //todo this should not include dealers

    private final int minVarId = 1;

    private final Map<String, Var> map = new HashMap<String, Var>();
    private final List<Var> list = new ArrayList<Var>();  //varIndex to Var

    private VarSet vars;

    private Converter<Var> varConverter;
    private Converter<String> varCodeConverter;
    private Converter<Integer> varIdConverter;

    private Space space;

    private boolean freeze = false;

    public VarSpace(Space space) {
        this.space = space;
    }

//    public VarSpace() {
//    }

//    public int getMaxVarCount() {
//        if (maxVarCount == null) {
//            maxVarCount = map.size();
//        }
//        assert maxVarCount == list.size();
//        return maxVarCount;
//    }

    public int getVarCount() {
        return list.size();
    }

    public VarSpace() {
    }

    public boolean setFreeze() {
        if (!freeze) {
//            if(true) throw new RuntimeException();
            freeze = true;
            return true;
        } else {
            return false;
        }
    }

    public Var mkVar(String varCode) {
        VarCode vc = new VarCode(varCode);
        String fixed = vc.getVarCode();
        Var vr = map.get(fixed);
        if (vr == null) {
            checkState(!freeze, fixed);
            int varId = getVarIdFromVarIndex(list.size());
            vr = new Var(this, varId, fixed);
            map.put(fixed, vr);
            list.add(vr);
        }
        return vr;
    }

    public Var mkVar(Var vr) {
        return mkVar(vr.getVarCode());
    }


    public void _resetSpace() {
        this.space = null;
    }


//    private static Map<String, Var> initVarMap2() {
//        HashMap<String, Var> map = new HashMap<String, Var>();
//        for (Var var : _complexVars) {
//            map.put(var.getVarCode(), var);
//        }
//        return map;
//    }


    private static ImmutableList<String> sortVarCodes(Set<String> varCodes) {
        ArrayList<String> a = new ArrayList<String>(varCodes);
        Collections.sort(a);
        return ImmutableList.copyOf(a);
    }

    private Var[] initVarMap1(Set<String> varCodes) {
        ImmutableList<String> sortedVarCodes = sortVarCodes(varCodes);
        Var[] vars = new Var[sortedVarCodes.size()];
        for (int varIndex = 0; varIndex < sortedVarCodes.size(); varIndex++) {
            String varCode = sortedVarCodes.get(varIndex);
            int varId = getVarIdFromVarIndex(varIndex);
            Var var = new Var(this, varId, varCode);
            vars[varIndex] = var;
        }
        return vars;
    }

    public int getVarId(String varCode) throws BadVarCodeException {
        return getVar(varCode).getVarId();
    }

    public Var getVar(String varCode) throws BadVarCodeException {
        Var var = map.get(varCode);
        if (var == null) {
            String varCode1 = new VarCode(varCode).getVarCode();
            var = map.get(varCode1);
            if (var == null) {
                throw new BadVarCodeException(varCode);
            }
        }
        return var;
    }

//    public Var mkVar(String varCode) {
//        Var vr = map.get(varCode);
//        if (vr == null) {
//            VarCode vc = new VarCode(varCode);
//            vr = new Var(this, list.size(), vc.toString());
//            map.put(varCode, vr);
//            list.add(vr);
//            set.add(vr);
//        }
//        return vr;
//    }

    public Var getVar(VarCode varCode) throws BadVarCodeException {
        return getVar(varCode.getVarCode());
    }

    public boolean checkVarId(int varId) throws BadVarIdException {
        if (varId < minVarId) {
            System.err.println("minVarId[" + minVarId + "] actual[" + varId + "]");
            throw new BadVarIdException(varId);
        }
//        if (varId > maxVarId) {
//            System.err.println("maxVarId[" + maxVarId + "] actual[" + varId + "]");
//            throw new BadVarIdException(varId);
//        }
        return true;
    }

    public Var getVar(int varId) throws BadVarIdException {
        int varIndex = getVarIndexFromVarId(varId);
        return list.get(varIndex);
    }

    public int getVarIndexFromVarId(int varId) throws BadVarIdException {
        checkVarId(varId);
        return varId - 1;
    }

    public static int getVarIdFromVarIndex(int varIndex) {
        return varIndex + 1;
    }

    public int size() {
        assert check();
        return list.size();
    }

    public boolean check() {
        checkState(list.size() == map.size());
        return true;
    }


    public String serialize() {
        Ser a = new Ser();
        serialize(a);
        return a.toString();
    }

    @Override
    public String toString() {
        return serialize();
    }

    public void serialize(Ser a) {
        a.append("vars(");
        for (int i = 0; i < list.size(); i++) {
            Var vr = list.get(i);
            a.append(vr.getVarCode());
            if (i != (list.size() - 1)) {
                a.append(ARG_SEP);
            }
        }
        a.append(")");
    }

    public static String serializeCodeList(List<VarCode> varCodes) {
        Ser a = new Ser();
        serializeCodeList(a, varCodes);
        return a.toString();
    }

    public static void serializeCodeList(Ser a, List<VarCode> varCodes) {
        a.append("_complexVars(");
        for (int i = 0; i < varCodes.size(); i++) {
            VarCode varCode = varCodes.get(i);
            a.append(varCode.toString());
            if (i != (varCodes.size() - 1)) {
                a.append(ARG_SEP);
            }
        }
        a.append(")");
    }


    /**
     * varSpace(var1 var2 var2)
     */
    public static void parseVarMap(String varMap, ImmutableList.Builder<VarCode> b) {
        String token = "_complexVars";
        varMap = varMap.replaceAll(", ", " ");
        varMap = varMap.replaceAll(",", " ");
        varMap = varMap.replaceAll("  ", " ");
        int i1 = token.length() + 1;
        int i2 = varMap.length() - 1;
        varMap = varMap.substring(i1, i2);
        String[] varCodes = varMap.split(" ");
        for (String sVarCode : varCodes) {
            VarCode varCode = new VarCode(sVarCode);
            b.add(varCode);
        }
    }

    public static Set<VarCode> parseVarMapToSet(String varMap) {
        ImmutableSet.Builder<VarCode> b = ImmutableSet.builder();
        parseVarMap(varMap, b);
        return b.build();
    }

    public static void parseVarMap(String varMap, ImmutableSet.Builder<VarCode> b) {
        String token = "_complexVars";
        varMap = varMap.replaceAll(", ", " ");
        varMap = varMap.replaceAll(",", " ");
        varMap = varMap.replaceAll("  ", " ");
        int i1 = token.length() + 1;
        int i2 = varMap.length() - 1;
        varMap = varMap.substring(i1, i2);
        String[] varCodes = varMap.split(" ");
        for (String sVarCode : varCodes) {
            VarCode varCode = new VarCode(sVarCode);
            b.add(varCode);
        }
    }


    public static ImmutableList<VarCode> parseVarMap(String varMap) {
        ImmutableList.Builder<VarCode> b = ImmutableList.builder();
        parseVarMap(varMap, b);
        return b.build();

    }

    public static ImmutableList<String> parseVarMap3(String varMap) {
        return VarCodes.toStringList(parseVarMap(varMap));
    }

    public List<Var> getVarList() {
        return list;
    }


    public static String convertToBase36(int varId) {
        return Integer.toString(varId, Character.MAX_RADIX);
    }

    public VarCode getVarCode2(int varId) throws BadVarIdException {
        Var var = getVar(varId);
        return var.toVarCode();
    }

    public String getVarCode(int varId) throws BadVarIdException {
        return getVarCode2(varId).getVarCode();
    }

    public void print() {

        System.err.println("varCount[" + getVarCount() + "]");
        System.err.println("minVarId[" + minVarId + "]");
//        System.err.println("maxVarId[" + maxVarId + "]");

        int maxWordCount = getWordCount();
        System.err.println("maxWordCount[" + maxWordCount + "]");

        for (int i = 0; i < list.size(); i++) {
            Var var = list.get(i);
            assert var.getVarId() == i + 1;
            System.err.println("i[" + i + "]  varCode[" + var.getVarCode() + "]  varIndex[" + var.getVarIndex() + "]   varId[" + var.getVarId() + "] expId[" + var.getVarId() + "] tinyId[" + var.getTinyId() + "]");
        }
    }

    public boolean eq(VarSpace that) {

        if (this.list.size() != that.list.size()) {
            return false;
        }

        int L = list.size();
        for (int i = 0; i < list.size(); i++) {
            Var thisVar = this.list.get(i);
            Var thatVar = that.list.get(i);

            String thisCode = thisVar.getVarCode();
            String thatCode = thatVar.getVarCode();

            if (!thisCode.equals(thatCode)) {
                return false;
            }

            int thisVarId = thisVar.getVarId();
            int thatVarId = thatVar.getVarId();


            if (thisVarId != thatVarId) {
                return false;
            }

        }

        return true;
    }


    public static List<String> varToCode(List<Exp> vars) {
        ArrayList<String> codes = new ArrayList<String>();
        for (Exp var : vars) {
            codes.add(var.getVarCode());
        }
        return codes;
    }


    public int getMaxVarIndex() {
        return list.size() - 1;
    }

    public int getWordCount() {
        int varCount = getVarCount();
        return (varCount >>> 6) + 1;
    }

    public final long bitMask(int varId) {
        return 1L << varId;
    }

    public int getMaxVarId() {
        return getMaxVarIndex() + Var.MIN_VAR_ID;
    }

    public void printVarInfo(int depth) {

        prindent(depth, "Space varInfo:");
        prindent(depth, "list.size(): " + list.size());
        prindent(depth, "map.size(): " + map.size());
        prindent(depth, "maxVarIndex(): " + getMaxVarIndex());
        prindent(depth, "getMaxVarId(): " + getMaxVarId());
        prindent(depth, "getMaxWordCount(): " + getWordCount());

    }


    public boolean containsVarCode(String varCode) {
        return map.containsKey(varCode);
    }

    public int getLitNodeCount() {
        int c = 0;
        for (Var var : list) {
            int litCount = var.getLitCount();
            c += litCount;
        }
        return c;
    }

    @NotNull
    @Override
    public Iterator<Var> iterator() {
        return list.iterator();
    }


    public Set<String> getVarCodes() {
        return map.keySet();
    }

    public SortedSet<String> getVarCodesSorted() {
        return Its.toSortedCodeSet(this.getVarList());
    }

    public Lit getLit(int varId, VarPredicate p) {
        Var var = getVar(varId);
        boolean sign = p.isTrue(var);
        return var.mkLit(sign);
    }

    public Exp getLitExp(int varId, VarPredicate p) {
        return getLit(varId, p);
    }

    public int convertVarIndexToVarId(int varIndex) {
        return varIndex + 1;
    }

    public int convertVarIdToVarIndex(int varId) {
        return varId - 1;
    }

    public VarSet mkVarSet(int... varIds) {
        setFreeze();
        if (varIds == null || varIds.length == 0) {
            return mkEmptyVarSet();
        } else if (varIds.length == 1) {
            return getVar(varIds[0]).mkSingletonVarSet();
        } else if (varIds.length == 2) {
            return mkVarPair(varIds[0], varIds[1]);
        } else {
            VarSetBuilder b = varSetBuilder();
            for (int varId : varIds) {
                b.addVarId(varId);
            }
            return b.build();
        }
    }

    public VarSet mkVarPair(Var var1, Var var2) {
        setFreeze();
        return var1.mkPartner(var2);
    }

    public VarSet mkVarPair(int varId1, int varId2) {
        setFreeze();
        Var var1 = getVar(varId1);
        return var1.mkPartner(varId2);
    }

    public VarSet mkSingleton(int varId) {
        setFreeze();
        return getVar(varId).mkSingletonVarSet();
    }

    public VarSet mkEmptyVarSet() {
        setFreeze();
        return EmptyVarSet.getInstance();
    }


    public Converter<Var> varConverter() {
        if (varConverter == null) {
            varConverter = new Converter<Var>() {
                @Override
                public Var toE(int varId) {
                    return getVar(varId);
                }

                @Override
                public int toVarId(Var var) {
                    return var.getVarId();
                }
            };
        }
        return varConverter;
    }

    public Converter<String> varCodeConverter() {
        if (varCodeConverter == null) {
            varCodeConverter = new Converter<String>() {
                @Override
                public String toE(int varId) {
                    return getVar(varId).getVarCode();
                }

                @Override
                public int toVarId(String varCode) {
                    return getVar(varCode).getVarId();
                }
            };
        }
        return varCodeConverter;
    }

    public Converter<Lit> litConverter(final VarPredicate p) {
        return new Converter<Lit>() {
            @Override
            public Lit toE(int varId) {
                Var var = getVar(varId);
                boolean sign = p.isTrue(var);
                return var.lit(sign);
            }

            @Override
            public int toVarId(Lit lit) {
                return lit.getVarId();
            }
        };
    }

    public Converter<Exp> litExpConverter(final VarPredicate p) {
        return new Converter<Exp>() {
            @Override
            public Exp toE(int varId) {
                Var var = getVar(varId);
                boolean sign = p.isTrue(var);
                return getVar(varId).lit(sign);
            }

            @Override
            public int toVarId(Exp lit) {
                return lit.getVarId();
            }
        };
    }

    public Converter<Integer> varIdConverter() {
        if (varIdConverter == null) {
            varIdConverter = new Converter<Integer>() {
                @Override
                public Integer toE(int varId) {
                    return varId;
                }

                @Override
                public int toVarId(Integer varId) {
                    return varId;
                }
            };
        }
        return varIdConverter;
    }

    public VarSetBuilder varSetBuilder() {
        setFreeze();
        return new VarSetBuilder(this);
    }

    public VarSet getVars() {
        if (vars == null) {
            VarSetBuilder b = varSetBuilder();
            for (Var var : list) {
                b.addVar(var);
            }
            this.vars = b.build();
        }
        return this.vars;
    }


    public Set<Var> toSet() {
        return ImmutableSet.copyOf(map.values());
    }


    public VarSet createVars(Set<Var> vars) {
        if (vars instanceof VarSet) {
            return (VarSet) vars;
        }
        VarSetBuilder b = varSetBuilder();
        b.addVars(vars);
        return b.build();
    }

    public VarSet copyOf(Iterable<? extends HasVarId> vars) {
        if (vars instanceof VarSet) {
            return (VarSet) vars;
        }
        VarSetBuilder b = varSetBuilder();
        b.addVars(vars);
        return b.build();
    }

    public VarSet copyOf(HasVarId[] vars) {
        VarSetBuilder b = varSetBuilder();
        b.addVars(vars);
        return b.build();
    }

    public VarSet createVarsFromVarIdSet(Set<Integer> varIds) {
        VarSetBuilder b = varSetBuilder();
        for (int varId : varIds) {
            b.addVarId(varId);
        }
        return b.build();
    }

    public Lit getLit(int varId, boolean sign) {
        return getVar(varId).lit(sign);
    }

    public Lit mkLit(int lit) {
        int varId = Head.Companion.getVarId(lit);
        boolean sign = Head.Companion.getSign(lit);
        return getVar(varId).lit(sign);
    }

    public Lit mkLit(Lit lit) {
        int varId = lit.getVarId();
        boolean sign = lit.sign();
        return getLit(varId, sign);
    }

    public Lit mkLit(String signedVarCode) {
        String varCode = Head.Companion.getVarCode(signedVarCode);
        boolean sign = Head.Companion.getSign(signedVarCode);
        return getVar(varCode).lit(sign);
    }


    public static boolean isInt32(String prefix) {
        return int32VarPrefixes.contains(prefix);
    }

    public static ImmutableList<String> getInt32VarPrefixes() {
        return int32VarPrefixes;
    }

    public static ImmutableSet<String> createInt32VarCodes(String intVarPrefix) {
        ImmutableSet.Builder<String> b = ImmutableSet.builder();
        for (int i = 0; i < 32; i++) {
            VarCode vc = new VarCode(intVarPrefix, i);
            b.add(vc.toString());
        }
        return b.build();
    }

    @Deprecated
    public static Set<String> createInt32VarCodes() {
        if (true) {
            throw new UnsupportedOperationException();
        }
        ImmutableSet.Builder<String> b = ImmutableSet.builder();
        for (String int32VarPrefix : int32VarPrefixes) {
            Set<String> int32VarCodes = createInt32VarCodes(int32VarPrefix);
            b.addAll(int32VarCodes);
        }
        return b.build();
    }

    public static Set<String> createMsrpVarCodes() {
        return createInt32VarCodes(MSRP_PREFIX);
    }

    public static Set<String> createDealerVarCodes() {
        return createInt32VarCodes(DLR_PREFIX);
    }

    public Space getSpace() {
        return space;
    }

    public void mkVars(Iterable<String> varCodes) {
        for (String varCode : varCodes) {
            mkVar(varCode);
        }
    }

    public void mkVars(VarSet vars) {
        for (Var vr : vars) {
            mkVar(vr);
        }
    }

//    public VarSpace copyReduce(VarSet varsToKeep) {
//        Set<String> varCodes = varsToKeep.toVarCodeSet();
//        return new VarSpace(varCodes);
//    }

}
