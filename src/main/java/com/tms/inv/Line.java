package com.tms.inv;

import com.google.common.base.CharMatcher;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.tms.csp.VarInfo;
import com.tms.csp.argBuilder.ArgBuilder;
import com.tms.csp.ast.*;
import com.tms.csp.common.*;
import com.tms.csp.fm.dnnf.products.PosCube;
import com.tms.csp.util.BadVarCodeException;
import com.tms.csp.util.varSets.VarSet;
import com.tms.csp.util.varSets.VarSetBuilder;
import com.tms.csp.varCodes.VarCode;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.logging.Logger;

import static com.google.common.base.Preconditions.checkState;

/**
 * yr
 * mdl
 * xcol
 * icol
 */
public class Line extends PosCube {

    public static final ImmutableSet<String> VALID_VAR_PREFIXES = ImmutableSet.of(
            Prefix.YR.name(),
            Prefix.MDL.name(),
            Prefix.XCOL.name(),
            Prefix.ICOL.name(),
            Prefix.ACY.name()
    );

    public static final Splitter TOKEN_SPLITTER = Splitter.on(CharMatcher.WHITESPACE).trimResults().omitEmptyStrings();



    private final Integer qty;
    private final Integer msrp;
    private final Set<Integer> dealerCodes;


    private Inv inv;
    private Set<String> context;

    public Line(VarSet vars, Integer qty, Integer msrp) {
        this(vars, qty, msrp, null);
    }

    public Line(VarSet vars, Integer qty, Integer msrp, Set<Integer> dealerCodes) {
        super(vars);

        ImmutableSet.Builder<String> b = ImmutableSet.builder();
        for (Var var : vars) {
            b.add(var.getVarCode());
        }
        this.varCodes = b.build();

        this.qty = qty;
        if (msrp != null && msrp == Integer.MAX_VALUE) {
            this.msrp = null;
        } else {
            this.msrp = msrp;
        }

        if (dealerCodes == null) {
            this.dealerCodes = ImmutableSet.of();
        } else {
            this.dealerCodes = ImmutableSet.copyOf(dealerCodes);
        }
    }

    public Line(Line line, VarSet vars) {
        super(vars);

        ImmutableSet.Builder<String> b = ImmutableSet.builder();
        for (Var var : vars) {
            b.add(var.getVarCode());
        }
        this.varCodes = b.build();

        this.qty = line.qty;
        this.msrp = line.msrp;
        this.dealerCodes = ImmutableSet.copyOf(line.dealerCodes);
    }

    public void setInv(Inv inv) {
        this.inv = inv;
    }

    public Inv getInv() {
        return inv;
    }

    public static VarSet nullNormalize(VarSet vars) {
        if (vars != null && vars.isEmpty()) {
            return null;
        } else {
            return vars;
        }
    }

    public Line removeVar(Var varToRemove) {
        if (!containsVar(varToRemove)) {
            return this;
        }
        VarSet vars = getVars(varToRemove);
        return new com.tms.inv.Line(vars, qty, msrp);
    }

    public Line refine(Var var, boolean sign) {
        if (sign) {
            return refinePos(var);
        } else {
            return refineNeg(var);
        }

    }


    //valid lines must contain vr
    public Line refinePos(Var var) {
        if (!containsVar(var)) {
            return null;
        } else {
            return removeVar(var);
        }
    }

    //valid lines must NOT contain vr
    public Line refineNeg(Var var) {
        if (containsVar(var)) {
            return null;
        } else {
            return removeVar(var);
        }

    }

    public boolean containsAny1(Set<YearModel> yms) {
        for (YearModel ym : yms) {
            if (this.matchesYearModel(ym)) {
                return true;
            }
        }
        return false;
    }

    public boolean containsAny2(Set<YearModelXCol> ymxs) {
        for (YearModelXCol ymx : ymxs) {
            if (this.containsAll(ymx)) {
                return true;
            }
        }
        return false;
    }

    public boolean containsAny3(Set<YearModelAcy> ymas) {
        for (YearModelAcy yma : ymas) {
            if (this.matchesYearModelAcy(yma)) {
                return true;
            }
        }
        return false;
    }

    public static Line parseDead(String sLine, Space space) throws BadVarCodeException {
        sLine = sLine.trim();


        String[] a = sLine.split(" ");

        int varCount = a.length - 1;


        VarSet vars = space.newMutableVarSet();
        Integer qty = null;
        Integer msrp = null;
        HashSet<Integer> dealerCodes = new HashSet<Integer>();

//        boolean hasMsrp = a[a.length - 1].charAt(0) == '$';

        int indexOfMsrp = indexOfMsrp(a);
        int indexOfQty;
//        if (indexOfMsrp == -1) {
//            indexOfQty = (a.length - 1);
//        } else {
        checkState(indexOfMsrp > 4);
        indexOfQty = indexOfMsrp - 1;
//        }


        for (int i = 0; i < a.length; i++) {
            String s = a[i];

            boolean isMsrp = (i == indexOfMsrp);
            boolean isQty = (i == indexOfQty);
//            boolean isQty = hasMsrp ? (i == (a.length - 2)) : (i == (a.length - 1));
//            boolean isMsrp = hasMsrp && i == (a.length - 1);

            if (s.contains("_")) {

                assert !isQty;
                assert !isMsrp;

                VarCode vc = new VarCode(s);
                if (vc.getPrefix().equals("DLR")) {
                    String localName = vc.getLocalName();
                    int dealerCode = Integer.parseInt(localName);
                    dealerCodes.add(dealerCode);
                } else {
                    Var var = space.getVar(s);
                    vars.addVar(var);
                }

            } else if (s.charAt(0) == '$') {
                assert msrp == null;
                assert isMsrp;
                assert s.length() != 1;
                String sMsrp = s.substring(1);
                if (sMsrp.endsWith(".00")) {
                    sMsrp = sMsrp.substring(0, sMsrp.length() - 3);
                } else if (sMsrp.endsWith(".0")) {
                    sMsrp = sMsrp.substring(0, sMsrp.length() - 2);
                }
                msrp = new Integer(sMsrp);
            } else {
                if (isQty) {
                    assert qty == null;
                    qty = new Integer(s);
                } else {
                    //must be acy
                    s = "ACY_" + s;
                    Var var = space.getVar(s);
                    vars.addVar(var);
                }
            }
        }

        return new Line(vars, qty, msrp, dealerCodes);
    }

    public static int getAcyCount(List<VarCode> fixedLine) {
        Set<String> set = new HashSet<String>();
        for (VarCode varCode : fixedLine) {
            if (varCode.isAcy()) {
                set.add(varCode.getVarCode());
            }
        }

        return set.size();
    }

    public static Line parse1(String sLine, Space space) {

        List<VarCode> fixedLine = fixup(sLine);

        int acyCount = getAcyCount(fixedLine);
        System.err.println("acyCount[" + acyCount + "]");


        VarSetBuilder varsBuilder = space.newMutableVarSet();
        Integer qty = null;
        Integer msrp = null;
        ImmutableSet.Builder<Integer> dealerCodesBuilder = ImmutableSet.builder();

        String lineMDL = "";
        String lineYR = "";
        String lineSER = "";

        List<String> accessoriesToCheck = new ArrayList<String>();
        boolean atLeastOneNonDerived = false;

        for (VarCode varCode : fixedLine) {
            String s = varCode.getVarCode();
            String prefix = varCode.getPrefix();
            if (prefix.equals(YR_PREFIX)) {
                space.getVar(s);
                lineYR = s;
            }
            if (prefix.equals(MDL_PREFIX)) {
                space.getVar(s);
                lineMDL = s;
            }
            if (prefix.equals(ACY_PREFIX)) {
                space.getVar(s);
                accessoriesToCheck.add(s);
            }

            if (prefix.equals(XCOL_PREFIX)) {
                space.getVar(s);
            }

            if (prefix.equals(ICOL_PREFIX)) {
                space.getVar(s);
            }


            if (prefix.equals(MSRP_PREFIX)) {
                assert msrp == null;
                msrp = new Integer(varCode.getLocalName());
            } else if (prefix.equals(QTY_PREFIX)) {
                assert qty == null;
                qty = new Integer(varCode.getLocalName());
            } else if (prefix.equals(MSRP_PREFIX)) {
                int dealerCode = Integer.parseInt(varCode.getLocalName());
                dealerCodesBuilder.add(dealerCode);
            } else {
                assert VALID_VAR_PREFIXES.contains(prefix);

                // Build context for attribute look up
                Set<String> ctx = new HashSet<String>();
                if (!lineMDL.equals("") && !lineYR.equals("")) {
                    ctx.add(lineMDL);
                    ctx.add(lineYR);
                    if (lineSER.equals("")) {
                        String serVarCode = space.getVarMeta().getImpliedVarCode(ctx, "series");
                        if (serVarCode != null && !serVarCode.equals("")) {
                            lineSER = serVarCode;
                        }
                    }
                    ctx.add(lineSER);
                }


                // Do not proceed if a color or accessory is not associated or unVTC'd at this level
                if (!varCode.getVarCode().contains("MDL_") && !varCode.getVarCode().contains("YR_")) {
//                    System.err.println("ctx[" + ctx + "]");
//                    System.err.println("varCode[" + varCode.getVarCode() + "]");
                    boolean isAssociated = space.getVarMeta().isAssociated(ctx, varCode.getVarCode());
                    boolean isVtc = space.getVarMeta().getAttribute(ctx, varCode.getVarCode(), "vtc").equals("true");

                    if (!isAssociated || !isVtc) {
                        //                		 System.err.println(varCode.getVarCode());
                        throw new BadVarCodeException(varCode.getVarCode());
                    }
                }
                for (String accy : accessoriesToCheck) {
                    if (!space.getVarMeta().getAttribute(ctx, accy, "derived").equals("true"))
                        atLeastOneNonDerived = true;
                }
            }

            if (varCode.isQty()) {
                continue;
            }

            if (varCode.isMsrp()) {
                continue;
            }

            if (varCode.isDlr()) {
                continue;
            }

            varsBuilder.addVar(varCode.toString());

        }

        assert qty != null;

        VarSet vars = varsBuilder.build();
        ImmutableSet<Integer> dealerCodes = dealerCodesBuilder.build();

        if (!atLeastOneNonDerived)
            throw new BadVarCodeException("All ACCY are Derived for " + lineYR + " " + lineMDL);
        String temp = "";
        for (Var v : vars) {
            temp += v.getVarCode() + " ";
        }
        return new Line(vars, qty, msrp, dealerCodes);
    }

    public VarInfo getVarInfo() {
        return getSpace().getVarMeta();
    }


    public static Line parse(String sLine, Space space) {

        List<VarCode> fixedLine = fixup(sLine);

        VarSetBuilder varsBuilder = space.newMutableVarSet();
        Integer qty = null;
        Integer msrp = null;
        ImmutableSet.Builder<Integer> dealerCodesBuilder = ImmutableSet.builder();
        VarSetBuilder acyVarsBuilder = space.newMutableVarSet();

        Var mdl = null;
        Var yr = null;
        Var xCol = null;
        Var iCol = null;

        for (VarCode vc : fixedLine) {
            String varCode = vc.getVarCode();
            String prefix = vc.getPrefix();

            if (prefix.equals(YR_PREFIX)) {
                assert yr == null;
                yr = space.getVar(varCode);
            } else if (prefix.equals(MDL_PREFIX)) {
                assert mdl == null;
                mdl = space.getVar(varCode);
            } else if (prefix.equals(ACY_PREFIX)) {
                Var acy = space.getVar(varCode);
                acyVarsBuilder.add(acy);
            } else if (prefix.equals(XCOL_PREFIX)) {
                assert xCol == null;
                xCol = space.getVar(varCode);
            } else if (prefix.equals(ICOL_PREFIX)) {
                assert iCol == null;
                iCol = space.getVar(varCode);
            } else if (prefix.equals(MSRP_PREFIX)) {
                assert msrp == null;
                msrp = new Integer(vc.getLocalName());
            } else if (prefix.equals(QTY_PREFIX)) {
                assert qty == null;
                qty = new Integer(vc.getLocalName());
            } else if (prefix.equals(DLR_PREFIX)) {
                int dealerCode = Integer.parseInt(vc.getLocalName());
                dealerCodesBuilder.add(dealerCode);
            } else {
                throw new IllegalStateException();
            }

            if (vc.isQty()) {
                continue;
            }

            if (vc.isMsrp()) {
                continue;
            }

            if (vc.isDlr()) {
                continue;
            }

            varsBuilder.addVar(vc.toString());

        }

        assert qty != null;


        VarSet vars = varsBuilder.build();
        VarSet acyVars = acyVarsBuilder.build();
        ImmutableSet<Integer> dealerCodes = dealerCodesBuilder.build();

        return new Line(vars, qty, msrp, dealerCodes);
    }

    public boolean isVarDerived(Var var) {
        String value = getVarAttribute(var, VarInfo.DERIVED);
        return value != null && value.equalsIgnoreCase(Boolean.toString(true));
    }

    public boolean isVarVTC(Var var) {
        String value = getVarAttribute(var, VarInfo.VTC);
        return value != null && value.equals(Boolean.toString(true));
    }

    public boolean isVarAssociatedVTC(Var var) {
        VarInfo varInfo = getVarInfo();
        Set<String> context = getContext();
        return varInfo.isAssociated(context, var.getVarCode());
    }

//    public String getAttribute(Var vr, String attName) {
//        String varCode = vr.getVarCode();
//        Set<String> context = getContext();
//        VarInfo varInfo = getVarInfo();
//        return varInfo.getAttribute(context, varCode, attName);
//    }

    public String getVarAttribute(Var var, String attName) {
        String varCode = var.getVarCode();
        Set<String> context = getContext();
        VarInfo varInfo = getVarInfo();
        return varInfo.getAttribute(context, varCode, attName);
    }

    public boolean atLeastOneNonDerivedAcy() {
        VarSet acyVars = getAcyVars();
        for (Var acyVar : acyVars) {
            boolean acyDerived = isVarDerived(acyVar);
            if (!acyDerived) {
                return true;
            }
        }
        return false;
    }

    public Set<String> getContext() {
        if (this.context == null) {
            Space space = getSpace();
            Var year = getYear();
            Var model = getModel();

            //Build context for attribute look up
            this.context = space.createContext(year, model);
        }
        return this.context;
    }

    public boolean acceptLine() {
        if (true) {
            return true; //todo need to deal with this
        }
        Space space = getSpace();

        if (!atLeastOneNonDerivedAcy()) {    //todo dave: doesn't this also kill any vehicle with no accessories?
            log.info("Rejecting line [" + this + "] because all ACY are derived");
            return false;
        }

        for (Var var : vars) {
            if (var.isXCol() || var.isICol() || var.isAcy()) {
                boolean vtc = isVarVTC(var);
                if (!vtc) {
                    log.info("Rejecting line [" + this + "] because it contains an un-vtc'd varCode[" + var.getVarCode() + "]");
                    return false;
                }

                boolean associated = isVarAssociatedVTC(var);
                if (!associated) {
                    log.info("Removing line [" + this + "] because it contains an un-associated varCode[" + var.getVarCode() + "]");
                    return false;
                }
            }
        }

        return true;

    }

    private static int indexOfMsrp(String[] a) {
        for (int i = 0; i < a.length; i++) {
            if (a[i] == null) {
                continue;
            }
            String s = a[i].trim();
            if (s.isEmpty()) {
                continue;
            }
            if (s.charAt(0) == '$') {
                return i;
            }
        }
        return -1;
    }


    public Var getModel() {
        return getVar(Prefix.MDL);
    }

    public Var getYear() {
        return getVar(Prefix.YR);
    }

    public Var getICol() {
        return getVar(Prefix.ICOL);
    }

    public Var getXCol() {
        return getVar(Prefix.XCOL);
    }

    public VarSet getAcyVars() {
        return getVars(Prefix.ACY);
    }

    public VarSet getCoreVars() {
        return getVars(Prefix.core);
    }

    public int getCoreCount() {
        return getCoreVars().size();
    }

    public int getAcyCount() {
        return getAcyVars().size();
    }

    public int getVarCount() {
        return getCoreCount() + getAcyCount();
    }

    public boolean matchesYearModel(YearModel ym) {
        return containsVar(ym.year) && containsVar(ym.model);
    }

    public boolean containsAll(YearModelXCol ymx) {
        return matchesYearModel(ymx.yearModel) && containsVar(ymx.xCol);
    }

    public boolean matchesYearModelAcy(YearModelAcy yma) {
        return matchesYearModel(yma.yearModel) && matchesAcy(yma.acyVars);
    }

    public boolean matchesAcy(VarSet that) {
        VarSet acyVars = getAcyVars();
        return acyVars.equals(that);
    }

    public boolean anyVarIntersection(VarSet that) {
        return vars.anyVarOverlap(that);
    }

    public boolean isVarDisjoint(@NotNull VarSet that) {
        return !anyVarIntersection(that);
    }


//    public Line refineToAcy() {
//        ImmutableList.Builder<IVar> b = ImmutableList.builder();
//        for (IVar vr : vars) {
//            if (vr.isAcy()) {
//                b.add(vr);
//            }
//        }
//        return Line.create(b.build());
//    }


    public YearModel getYearModel() {
        return new YearModel(getYear(), getModel());
    }

    public YearModelXCol getYearModelXCol() {
        return new YearModelXCol(getYearModel(), getXCol());
    }

    public YearModelAcy getYearModelAcy() {
        return new YearModelAcy(getYearModel(), getAcyVars());
    }

    public ColorCombo getColorCombo() {
        return new ColorCombo(getXCol(), getICol());
    }

    public Ymxi getYmxi() {
        return new Ymxi(getYearModel(), getColorCombo());
    }

    public int getQty() {
        return qty;
    }


//    public Integer getMsrpRound() {
//        return (msrp + 50) / 100 * 100;
//    }

    /**
     * msrp_0 .. msrp_32
     */
    public Set<Exp> getMsrpInt32Lits() {
        return getSpace().getLitsForInt32(msrp, MSRP_PREFIX);
    }

//    public Exp mkMsrp() {
//        if (!hasMsrp()) {
//            return null;
//        }
//
//        switch (options.msrp2PL) {
//            case SKIP:
//                return null;
//            case INT32:
//                return mkMsrpExp_Int32AndExp();
//            case VAR_PER_MSRP:
//                return mkMsrpExp_MsrpBucketLit();
//            default:
//                throw new IllegalStateException();
//
//        }
//    }

    public Exp mkMsrpExp_Int32AndExp() {
        assert hasMsrp();
        return getSpace().mkMsrp32BitAndExp(msrp);
    }

    public Exp mkMsrpExp_MsrpStrictLit() {
        assert hasMsrp();
        Space space = getSpace();
        Integer msrp = getMsrp();
        Var msrpVar = space.getMsrpStrictVar(msrp);
        return msrpVar.mkPosLit();
    }

    public Exp mkMsrpExp_MsrpBucketLit() {
        assert hasMsrp();
        Space space = getSpace();
        Integer msrpBucket = getMsrpBucket();
        Var msrpBucketVar = space.getMsrpBucketVar(msrpBucket);
        return msrpBucketVar.mkPosLit();
    }


//    public Exp mkDealerCodesOr() {
//        if (!hasDealerCodes()) {
//            return null;
//        }
//
//        Space space = getSpace();
//
//        switch (options.dealer2PL) {
//            case SKIP:
//                return null;
//            case INT32:
//                return space.mkDealerCodesOrInt32(dealerCodes);
//            case VAR_PER_DEALER:
//                return space.mkDealerCodesOrVarPerDealer(dealerCodes);
//            default:
//                throw new IllegalStateException();
//
//        }
//
//    }

    /**
     * dealerCode_0 .. dealerCode_32
     */
    public Exp mkDealerCodesOrInt32() {
        assert hasDealerCodes();
        Space space = getSpace();
        return space.mkDealerCodesOrInt32(dealerCodes);
    }

//    public Exp mkDealerCodesOrVarPerDealer() {
//        assert hasDealerCodes();
//        Space space = getSpace();
//        return space.mkDealerCodesOrVarPerDealer(dealerCodes);
//    }

    public Line removeCoreVars() {
        return new com.tms.inv.Line(this, getAcyVars());
    }

    public Line removeAcyVars() {
        return new com.tms.inv.Line(this, getCoreVars());
    }

    public Line project(EnumSet<Prefix> prefixes) {
        VarSet varSet = vars.filter(prefixes);
        return new com.tms.inv.Line(varSet, qty, msrp);
    }

    public static List<VarCode> fixup(String sLine) {
        sLine = sLine.trim();
        try {
            List<String> tokens = splitLine(sLine);

            List<VarCode> vcTokens = new ArrayList<VarCode>();

            for (int i = 0; i < tokens.size(); i++) {
                String token = tokens.get(i);
                try {
                    VarCode vc;
                    if (isMsrp(token)) {
                        vc = parseMsrpToken(token);
                    } else if (VarCode.noPrefix(token)) {
                        if (isQty(tokens, i)) {
                            vc = new VarCode(QTY_PREFIX + UNDERSCORE + token);
                        } else {
                            //must be acy missing prefix
                            vc = new VarCode("ACY_" + token);
                        }
                    } else {
                        vc = new VarCode(token);
                    }
                    vcTokens.add(vc);
                } catch (RuntimeException e) {
                    System.err.println("token[" + i + "]:[" + token + "]");
                    throw e;
                }
            }

            return vcTokens;
        } catch (RuntimeException e) {
            System.err.println(sLine);
            throw e;
        }
    }

    public static boolean isMsrp(String token) {
        return token.charAt(0) == '$';
    }

    public static boolean isDealer(String token) {
        return token.startsWith(DLR_PREFIX + "_");
    }

    public static boolean isQty(List<String> tokens, int index) {

        String token = tokens.get(index);

        if (isMsrp(token)) return false;
        if (isDealer(token)) return false;
        if (VarCode.hasUnderscore(token)) return false;

        try {
            Integer.parseInt(token);
        } catch (NumberFormatException e) {
            return false;
        }

        boolean isLast = (index == tokens.size() - 1);

        if (isLast) {
            return true;
        }

        String nextToken = tokens.get(index + 1);

        if (isMsrp(nextToken) || isDealer(nextToken)) {
            return true;
        }

        return false;
    }

    public static VarCode parseMsrpToken(String token) {
        assert token.charAt(0) == '$';

        token = token.substring(1);
        if (token.endsWith(".00")) {
            token = token.substring(0, token.length() - 3);
        } else if (token.endsWith(".0")) {
            token = token.substring(0, token.length() - 2);
        }

        return new VarCode(MSRP_PREFIX + "_" + token);

    }

    public static Set<String> extractVarCodes(String sLine) {
        ImmutableSet.Builder<String> b = ImmutableSet.builder();
        List<VarCode> fixedLine = fixup(sLine);
        for (VarCode varCode : fixedLine) {
            if (varCode.isInt32()) {
                //MSRP or DLR
                continue;
            }
            if (varCode.isQty()) {
                continue;
            }
            b.add(varCode.toString());
        }
        return b.build();
    }

    public static Set<String> extractDealerVarCodes(String sLine) {
        ImmutableSet.Builder<String> b = ImmutableSet.builder();
        List<VarCode> fixedLine = fixup(sLine);
        for (VarCode varCode : fixedLine) {
            if (varCode.isDlr()) {
                b.add(varCode.toString());
            }
        }
        return b.build();
    }

    public Integer getMsrp() {
        return msrp;
    }

    public static Integer floorMsrp(Integer msrp) {
        if (msrp == null) return null;
        return msrp / 100 * 100;
    }

    public Integer getMsrpBucket() {
        return floorMsrp(msrp);
    }

    public static Integer extractMsrp(String sLine) {
        int i1 = sLine.indexOf('$');
        if (i1 == -1) {
            return null;
        }
        int i2 = sLine.indexOf(' ', i1);
        String sMsrp = sLine.substring(i1 + 1, i2);
        double d = Double.parseDouble(sMsrp);
        return (int) d;
    }

    public static String extractMsrpVarCode(String sLine) {
        Integer msrp = extractMsrp(sLine);
        return MSRP_PREFIX + "_" + msrp;
    }

    public static VarCode extractMsrpVarCode1(String sLine) {
        List<VarCode> fixedLine = fixup(sLine);
        for (VarCode varCode : fixedLine) {
            if (varCode.isMsrp()) {
                return varCode;
            }
        }
        return null;
    }


    public void serializeLine(Ser a) {
        VarSet vars1 = getVars();
        for (Var var : vars1) {
            a.ap(var);
            a.argSep();
        }
        a.ap(qty);
        a.argSep();
        a.ap("$" + msrp);
    }

    public boolean containsAll(Line core) {
        return vars.containsAllVars(core.getVars());
    }


    public static List<String> splitLine(String sLine) {
        assert sLine != null;
        sLine = sLine.trim();
        assert !sLine.isEmpty();
        return ImmutableList.copyOf(TOKEN_SPLITTER.split(sLine));
    }

    public boolean hasMsrp() {
        return msrp != null && msrp > 0;
    }

    public boolean hasDealerCodes() {
        return dealerCodes != null && dealerCodes.size() > 0;
    }


    public static class LinesInfo {

        String label;
        int largestLine;
        int smallestLine;
        int lineCount;
        long avgVarCount;

        public void print() {
            System.err.println(label);
            System.err.println("  lineCount[" + lineCount + "]");
            System.err.println("  maxVarCount[" + largestLine + "]");
            System.err.println("  avgVarCount[" + avgVarCount + "]");
            System.err.println();
        }


    }

    public static LinesInfo getLinesInfo(String label, Set<com.tms.inv.Line> lines) {

        LinesInfo info = new LinesInfo();
        info.label = label;
        int currentMax = 0;

        int total = 0;
        for (com.tms.inv.Line line : lines) {
            currentMax = line.getMaxVarCount(currentMax);
            total += line.getVarCount();

        }

        double t = total;
        double s = lines.size();
        double avg = t / s;
        long avgLineCount = Math.round(avg);
        info.avgVarCount = avgLineCount;


        info.largestLine = currentMax;
        info.lineCount = lines.size();

        info.avgVarCount = (int) Math.round(avgLineCount);

        return info;
    }

    private int getMaxVarCount(int currentMax) {
        int varCount = getVarCount();
        if (varCount > currentMax) {
            return varCount;
        } else {
            return currentMax;
        }

    }


    public DynCube toCoreCube(Inv inv) {
        int acyCount = getAcyCount();
        assert acyCount == 0;

//        assert invClob.isCoreOnly();
        assert this.isCoreOnly();

        VarSetBuilder v = getSpace().varSetBuilder();
        v.addVars(vars);

        VarSetBuilder t = getSpace().varSetBuilder();
        t.addVars(vars);

        return new DynCube(getSpace(), v, t);

    }

    public boolean isCoreOnly() {
        VarSet vars = getAcyVars();
        return vars.isEmpty();
    }

    public DynCube toFullCube(Inv inv) {

        VarSet allInvAcy = inv.getAcyVars();

        VarSetBuilder v = getSpace().varSetBuilder();
        v.addVars(vars);
        v.addVars(allInvAcy);

        VarSetBuilder t = getSpace().varSetBuilder();
        t.addVars(vars);

        return new DynCube(getSpace(), v, t);

    }

    private static Logger log = Logger.getLogger(com.tms.inv.Line.class.getName());


    public Exp createAcyConstraint(Inv ctx) {
        throw new UnsupportedOperationException();
    }


    public Set<Integer> getDealerCodes() {
        return dealerCodes;
    }

    @Override
    public String toString() {
        return super.toString();
    }

    /*
    and(
        Y14 M1234 X1 I1
        A_1 A_2 !A_3 !A_4
        MSRP
        !D1 !D2 !D3 or(D4 D5)
    )
    */
    public Exp mkRecord() {
        Exp record = mkFeatureRecord();

        Space space = getSpace();

        ArgBuilder b = new ArgBuilder(space,Op.And);

        b.addExp(record);

        if (hasMsrp()) {
            if (USE_MSRP_BUCKETS) {
                Exp msrpBucketExp = mkMsrpExp_MsrpBucketLit();
                b.addExp(msrpBucketExp);
            }

            if (USE_MSRP_STRICT) {
                Exp msrpStrictExp = mkMsrpExp_MsrpStrictLit();
                b.addExp(msrpStrictExp);
            }

            if (USE_MSRP_BITS) {
                Exp msrpBitExp = mkMsrpExp_Int32AndExp();
                assert msrpBitExp.isAnd();
                b.addExp(msrpBitExp);
            }
        }

        if (hasDealerCodes()) {

            Exp dealerExp = mkDealerCodesOrInt32();
            if (dealerExp != null) {
                b.addExp(dealerExp);
            }
        }

        return b.mk();
    }

    public Exp mkFeatureRecord() {

        if (inv == null) {
            throw new IllegalStateException("must call Line.setInv(..) before calling mkFeatureRecord");
        }


        Space space = getSpace();

        ArgBuilder b = new ArgBuilder(space,Op.DAnd);



        for (Var var : varIt()) {
            assert !var.is(DLR_PREFIX);
            assert !var.is(MSRP_PREFIX);
            Lit lit = var.mkPosLit();
            b.addExp(lit);
        }

        VarSet allInvAcyVars = inv.getAcyVars();
        VarSet lineInvAcyVars = this.getAcyVars();

        VarSet negated = allInvAcyVars.minus(lineInvAcyVars);

        for (Var var : negated.varIt()) {
            Lit lit = var.mkNegLit();
            b.addExp(lit);
        }

        return b.mk();


    }


//    private void addNegatedDealersVarPerDealer(ArgBuilder b) {
//        Set<Integer> allDealerVars = invClob.getDealerCodes();
//        Set<Integer> lineDealerVars = this.getDealerCodes();
//
//        Set<Integer> negated = Sets.difference(allDealerVars, lineDealerVars);
//
//        for (Integer dealerCode : negated) {
//            String varCode = Dealers.convertDealerIntToVarCode(dealerCode);
//            Var vr = getSpace().getVr(varCode);
//            Lit lit = vr.mkNegLit();
//            b.add(lit);
//        }
//
//    }

    @NotNull
    @Override
    public Set<String> getVarCodes() {
        return varCodes;
    }

    private final ImmutableSet<String> varCodes;

    @Deprecated
    private void addNegatedDealersInt32(ArgBuilder b) {
        throw new UnsupportedOperationException();
    }


}
