package com.smartsoft.inv;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.smartsoft.csp.VarInfo;
import com.smartsoft.csp.Vars;
import com.smartsoft.csp.argBuilder.ArgBuilder;
import com.smartsoft.csp.ast.*;
import com.smartsoft.csp.common.*;
import com.smartsoft.csp.dnnf.products.Cube;
import com.smartsoft.csp.dnnf.vars.VarFilter;
import com.smartsoft.csp.parse.VarSpace;
import com.smartsoft.csp.util.varSets.VarSet;
import com.smartsoft.csp.util.varSets.VarSetBuilder;
import com.smartsoft.csp.varCodes.IVar;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.google.common.base.Preconditions.checkNotNull;

public class Inv extends Mods {

    public final Space space;
    private final Set<Line> lines;
    private final VarSet vars;

    private VarSet acyVars;

    private Boolean hasMsrp;
    private Boolean hasDealerCodes;
    private ImmutableSet<Integer> dealerCodes;

    public Inv(Space space, ImmutableSet<Line> lines /* normalized - no dups */) {
        checkNotNull(space);
        checkNotNull(lines);
        this.space = space;
        this.lines = lines;

        VarSetBuilder varSetBuilder = space.newMutableVarSet();
        for (Line line : lines) {
            varSetBuilder.addVars(line.getVars());
        }
        vars = varSetBuilder.build();

        for (Line line : lines) {
            line.setInv(this);
        }

    }


    public Set<String> getPrefixes() {
        return vars.getPrefixes();
    }

    public VarSet getVars(Prefix prefix) {
        return vars.filter(prefix);
    }

    /**
     * Should not include int32Vars
     */
    public VarSet getVars() {
        return vars;
    }

    public VarSet getVars(String prefix) {
        return vars.filter(prefix);
    }

    public VarSet getVars(EnumSet<Prefix> prefixFilter) {
        return vars.filter(prefixFilter);
    }

    public boolean hasVars() {
        return !vars.isEmpty();
    }

    public boolean isLeaf() {
        return !hasVars();
    }

//    public Inv refine(String varCode) {
//        Var vr = space.getVr(varCode);
//        assert vr != null;
//        return refine(vr);
//    }

    public boolean contains(Var var) {
        return containsVar(var);
    }

    public boolean containsVar(Var var) {
        return vars.containsVar(var);
    }

    public String getStatus(boolean complete) {
        return complete ? "COMPLETE" : "OPEN";
    }

//    public void print() {
//        for (Line line : lines) {
//            System.err.println(line.serialize());
//        }
//    }
//
//    public void print(int depth) {
//        print();
//    }


    public int getLineCount() {
        return lines.size();
    }

    public Set<Line> getLines() {
        return lines;
    }

//    public Inv refine(Var vr) {
//        checkNotNull(vr);
//        checkNotNull(lines);
//
//        InvBuilder b = new InvBuilder(space);
//
//        for (Line line : lines) {
//            if (line.containsVar(vr)) {
//                b.addLine(line.removeVar(vr));
//            }
//        }
//
//        return new Inv(b);
//
//    }

    public Inv filterOnAvalon() {
        return filterOnSeriesCode("35");
    }

    public Inv filterOnSeriesCode(String twoCharSeriesCode) {
        InvBuilder b = new InvBuilder(space);
        for (Line line : lines) {
            Var model = line.getModel();
            String localName = model.getLocalName();
            if (localName.startsWith(twoCharSeriesCode)) {
                b.addLine(line);
            }
        }
        return b.build();
    }

    public Set<Line> getLinesContaining(Line core) {
        checkNotNull(lines);

        ImmutableSet.Builder<Line> b = ImmutableSet.builder();

        for (Line line : lines) {
            if (line.containsAll(core)) {
                Line acyOnly = line.removeCoreVars();
                b.add(acyOnly);
            }
        }

        return b.build();

    }

//    public Inv refine(Line vr) {
//        Set<Line> lines = refineLines(vr);
//        return new Inv(space, lines);
//    }


//    public void getOpenCoreInvXors(Groups b) {
//        for (Line line : lines) {
//            line.getCoreInvXors(b);
//        }
//    }


    public boolean isCoreComplete() {
        VarSet coreVars = getCoreVars();
        return coreVars.isEmpty();
    }


    public int getVarCount() {
        return vars.size();
    }

    public boolean isAcyOnly() {
        VarSet vars = getCoreVars();
        return vars.isEmpty();
    }

    public boolean isCoreOnly() {
        VarSet vars = getAcyVars();
        return vars.isEmpty();
    }


    public boolean isInventoryCareVar(IVar var) {
        return Mods.isInvCareVar(var);
    }


    public Line getLineWithLargestAcyCount() {
        Line best = null;
        int cBest = -1;
        for (Line line : lines) {
            int c = line.getAcyCount();
            if (best == null || c > cBest) {
                best = line;
                cBest = c;
            }
        }
        return best;
    }


    public int getSatCount() {
        return lines.size();
    }


    public AcyVarInfos buildAcyInfos() {
        return new AcyVarInfos(this);
    }

    public int size() {
        return lines.size();
    }

    public VarSet getYearVars() {
        return getVars(Prefix.YR);
    }


    public VarSet getModelVars() {
        return getVars(Prefix.MDL);
    }

    public VarSet getModelVarsForAvalon() {
        return getModelVarsForSeries("35");
    }

    public VarSet getModelVarsForSeries(final String twoCharSeriesCode) {
        VarSet modelVars = getModelVars();
        return modelVars.filter(new VarFilter() {
            @Override
            public boolean accept(Var var) {
                return var.getVarCode().startsWith(twoCharSeriesCode);
            }
        });
    }

    public VarSet getIColVars() {
        return getVars(Prefix.ICOL);
    }

    public Set<Integer> getDealerCodes() {
        if (this.dealerCodes == null) {
            ImmutableSet.Builder<Integer> b = ImmutableSet.builder();
            for (Line line : getLines()) {
                Set<Integer> dealerCodes = line.getDealerCodes();
                b.addAll(dealerCodes);
            }
            this.dealerCodes = b.build();
        }
        return this.dealerCodes;
    }


    public VarSet getXColVars() {
        return getVars(Prefix.XCOL);
    }

    public VarSet getAcyVars() {
        if (acyVars == null) {
            acyVars = getVars(Prefix.ACY);
        }
        return acyVars;
    }

    public Inv project(EnumSet<Prefix> prefixes) {
        InvBuilder bb = new InvBuilder(space);
        for (Line line : lines) {
            Line pp = line.project(prefixes);
            bb.addLine(pp);
        }
        return bb.build();
    }

    public Inv projectOnYearModelAcys() {
        EnumSet<Prefix> filter = Prefix.of(Prefix.YR, Prefix.MDL, Prefix.ACY);
        return project(filter);
    }

    public Inv projectOnYearModel() {
        EnumSet<Prefix> filter = Prefix.of(Prefix.YR, Prefix.MDL);
        return project(filter);
    }

    public Inv projectOnColorCombo() {
        EnumSet<Prefix> filter = Prefix.of(Prefix.XCOL, Prefix.ICOL);
        return project(filter);
    }

    public VarSet getCoreVars() {
        return getVars(Prefix.core);
    }


    public Inv processAllThreshold() {
        space.checkVarInfo();
        Inv inv = this;
        inv = inv.processModelThreshold();
        inv = inv.processModelXColThreshold();
        inv = inv.processModelAcyThreshold();

        return inv;
    }


    public Inv processModelThreshold() {

        Map<YearModel, Set<Line>> map = new HashMap<YearModel, Set<Line>>();

        for (Line line : lines) {
            YearModel ym = line.getYearModel();
            Set<Line> ymLines = map.get(ym);
            if (ymLines == null) {
                ymLines = new HashSet<Line>();
                map.put(ym, ymLines);
            }
            ymLines.add(line);
        }


        InvBuilder toKeep = new InvBuilder(space);

        for (Map.Entry<YearModel, Set<Line>> entry : map.entrySet()) {

            YearModel ym = entry.getKey();
            Set<Line> lines = entry.getValue();

            int qty = 0;

            for (Line line : lines) {
                qty += line.getQty();
            }


            int ymThreshold = getMinThreshold(ym);

            if (qty >= ymThreshold) {
                toKeep.addLines(lines);
            }

        }


        return toKeep.build();


    }


//    public Inv processModelXColThreshold() {
//        Set<YearModelXCol> toRemove = new HashSet<YearModelXCol>();
//        Set<YearModelXCol> yearModelXCols = getYearModelXCols();
//        for (YearModelXCol ymx : yearModelXCols) {
//            processThreshold(ymx, toRemove);
//        }
//        return this.removeLines2(toRemove);
//    }
//

    public Inv processModelXColThreshold() {

        Map<YearModelXCol, Set<Line>> map = new HashMap<YearModelXCol, Set<Line>>();

        for (Line line : lines) {
            YearModelXCol ymx = line.getYearModelXCol();
            Set<Line> ymxLines = map.get(ymx);
            if (ymxLines == null) {
                ymxLines = new HashSet<Line>();
                map.put(ymx, ymxLines);
            }
            ymxLines.add(line);
        }


        InvBuilder toKeep = new InvBuilder(space);

        for (Map.Entry<YearModelXCol, Set<Line>> entry : map.entrySet()) {

            YearModelXCol ymx = entry.getKey();
            Set<Line> lines = entry.getValue();

            int qty = 0;

            for (Line line : lines) {
                qty += line.getQty();
            }


            int xColThreshold = getMinThreshold(ymx);

            if (qty >= xColThreshold) {
                toKeep.addLines(lines);
            }

        }


        return toKeep.build();


    }


    public Inv processModelAcyThreshold() {

        Map<YearModelAcy, Set<Line>> map = new HashMap<YearModelAcy, Set<Line>>();

        for (Line line : lines) {
            YearModelAcy yma = line.getYearModelAcy();
            Set<Line> ymaLines = map.get(yma);
            if (ymaLines == null) {
                ymaLines = new HashSet<Line>();
                map.put(yma, ymaLines);
            }
            ymaLines.add(line);
        }


        InvBuilder toKeep = new InvBuilder(space);

        for (Map.Entry<YearModelAcy, Set<Line>> entry : map.entrySet()) {

            YearModelAcy yma = entry.getKey();
            Set<Line> lines = entry.getValue();

            int qty = 0;

            for (Line line : lines) {
                qty += line.getQty();
            }


            int acyThreshold = getMinThreshold(yma);

            if (qty >= acyThreshold) {
                toKeep.addLines(lines);
            }

        }


        return toKeep.build();


    }


    private int getMinThreshold(YearModel ym) {
        Set<String> ctx = ImmutableSet.of(ym.model.getVarCode(), ym.year.getVarCode());
        try {
            String th = space.getAttribute(ctx, ym.model.getVarCode(), "modelthreshold");
            if (th == null || th.trim().equals("")) {
                return 0;
            }
            return Integer.parseInt(th);
        } catch (Exception e) {
            log.log(Level.SEVERE, "Call to varInfo.getAttribute('modelthreshold') caused an exception. Using value of 0.", e);
            return 0;
        }
    }

    private int getMinThreshold(YearModelXCol ymx) {
        Set<String> ctx = ImmutableSet.of(ymx.getModel().getVarCode(), ymx.getYear().getVarCode());
        try {
            String th = space.getAttribute(ctx, ymx.getModel().getVarCode(), "colorthreshold");
            if (th == null || th.trim().equals("")) {
                return 0;
            }
            return Integer.parseInt(th);
        } catch (Exception e) {
            log.log(Level.SEVERE, "Call to varInfo.getAttribute('colorthreshold') caused an exception. Using value of 0.", e);
            return 0;
        }
    }

    private int getMinThreshold(YearModelAcy yma) {
        ImmutableSet<String> ctx = ImmutableSet.of(yma.getModel().getVarCode(), yma.getYear().getVarCode());
        try {
            String th = space.getAttribute(ctx, yma.getModel().getVarCode(), "accessorythreshold");
            if (th == null || th.trim().equals("")) {
                return 0;
            }
            return Integer.parseInt(th);
        } catch (Exception e) {
            log.log(Level.SEVERE, "Call to varInfo.getAttribute('accessorythreshold') caused an exception. Using value of 0.", e);
            e.printStackTrace();
            return 0;
        }
    }


    private static Logger log = Logger.getLogger(Inv.class.getName());

    public int computeQty(YearModel ym) {
        int q = 0;
        for (Line line : lines) {
            if (line.matchesYearModel(ym)) {
                q += line.getQty();
            }
        }
        return q;
    }

    public int computeQty(YearModelXCol ymx) {
        int q = 0;
        for (Line line : lines) {
            if (line.containsAll(ymx)) {
                q += line.getQty();
            }
        }
        return q;
    }

    public int computeQty(YearModelAcy yma) {
        int q = 0;
        for (Line line : lines) {
            if (line.matchesYearModelAcy(yma)) {
                q += line.getQty();
            }
        }
        return q;
    }

    @Override
    public boolean equals(Object obj) {
        Inv that = (Inv) obj;
        return lines.equals(that.lines);
    }

    public Space getSpace() {
        return space;
    }

    public Set<Line> computeAcyLines() {
        ImmutableSet.Builder<Line> b = ImmutableSet.builder();
        for (Line line : lines) {
            Line r = line.removeCoreVars();
            b.add(r);
        }
        return b.build();
    }

    public Set<Line> computeCoreLines() {
        ImmutableSet.Builder<Line> b = ImmutableSet.builder();
        for (Line line : lines) {
            Line r = line.removeAcyVars();
            b.add(r);
        }
        return b.build();
    }

    public boolean isEmpty() {
        return size() == 0;
    }

//    public Exp toDnnf(DSpace dSpace) {
//
//        if (isEmpty()) {
//            return dSpace.mkConstantFalse();
//        }
//
//        IXor xor = getBestXorSplit();
//
//        if (xor != null) {
//            return mkXorSplit(dSpace, xor);
//        } else {
//            return mkAcySplit(dSpace);
//        }
//
//    }

//    private Exp mkXorSplit(DSpace dSpace, IXor xor) {
//        ArrayList<Exp> ands = new ArrayList<Exp>();
//        for (int varId : xor.getVars()) {
//            Var vr = space.getVr(varId);
//            Inv r = refine(vr);
//            XorChildEvalContext c = new XorChildEvalContext(xor, varId);
//            Assignments aa = new Assignments(space, c);
//
//            Exp d1 = r.toDnnf(dSpace);
//            Exp d2 = dSpace.mkAnd(aa);
//
//            Exp and = dSpace.mkAnd(d1, d2);
//            ands.add(and);
//
//        }
//        return dSpace.mkOr(ands);
//    }

//    private Exp mkAcySplit() {
//        Var vr = getBestAcyVar();
//
//        ImmutableSet.Builder<Line> b1 = ImmutableSet.builder();
//        ImmutableSet.Builder<Line> b2 = ImmutableSet.builder();
//
//        for (Line line : lines) {
//            if (line.containsVar(vr)) {
//                b1.add(line.removeVar(vr));
//            } else {
//                b2.add(line);
//            }
//        }
//
//        Inv i1 = new Inv(space, b1.build());
//        Exp n1a = i1.toDnnf(dSpace);
//        Lit n1b = dVar.mkPosLit();
//        Exp a1 = dSpace.mkAnd(n1a, n1b);
//
//        Inv i2 = new Inv(space, b2.build());
//        Exp n2a = i2.toDnnf(dSpace);
//        Exp n2b = dVar.mkNegLit();
//        Exp a2 = dSpace.mkAnd(n2a, n2b);
//
//        return dSpace.mkOr(a1, a2);
//    }

    private Var getBestAcyVar() {
        AcyVarInfos a = new AcyVarInfos(this);
        return a.getBestVar();
    }


    private Prefix getBestXorSplit() {
        Set<String> prefixes = getPrefixes();
        String prefix = prefixes.iterator().next();
        return Prefix.get(prefix);
    }


    public static Inv parse(Space space, String invClob) {

        InvBuilder bb = new InvBuilder(space);

        if (invClob == null) {
            return null;
        }
        String[] sLines = invClob.split("\n");

        System.err.println("sLines.length[" + sLines.length + "]");

        for (String sLine : sLines) {
            if (sLine == null) continue;
            sLine = sLine.trim();
            if (sLine.isEmpty()) continue;
            bb.addLine(sLine);
        }


        Inv inv = bb.build();


        bb.printSummary();

        if (inv.getLines().isEmpty()) {
            log.severe("After removing non-fact vars, inventory table is empty.");
            return null;
        } else {
            return inv;
        }
    }


//    public static Inv parseInvNewSpaceInt32Dealer(String iClob) {
//
//        boolean hasMsrps = Inv.hasMsrps(iClob);
//        boolean hasDealers = Inv.hasDealers(iClob);
//
//
//        boolean includeMsrpVars;
//        if (!hasMsrps) {
//            includeMsrpVars = false;
//        }
//
//        if (!hasDealers) {
//            includeDealerVars = false;
//        }
//
//        Set<String> vars = Inv.extractVarCodes(iClob);
//        Space space = new Space(vars);
//        Inv retVal = Inv.parse(space, iClob);
//        space.setInv(retVal);
//        return retVal;
//    }


    public static Inv parseInvNewSpace(String iClob) {
        return parseInvNewSpace(iClob, null);
    }

    public static Inv parseInvNewSpace(String iClob, VarInfo varInfo) {


        Set<String> featureVarCodes = KInv.extractVarCodes(iClob);
        Set<String> extraVarCodes = KInv.buildExtraVars(iClob);


        ImmutableSet.Builder<String> b = ImmutableSet.builder();
        b.addAll(featureVarCodes);
        b.addAll(extraVarCodes);

        ImmutableSet<String> varCodes = b.build();

        Space space = new Space(varCodes);
        space.setVarInfo(varInfo);
        Inv retVal = Inv.parse(space, iClob);
        return retVal;
    }

//    public static String singleSeriesClob(String iClob, String twoCharSeriesCode) {
//        ImmutableList<String> lines = Parser.clobToLines(iClob);
//        Ser a = new Ser();
//        String pp = Prefix.MDL_ + twoCharSeriesCode;
//        for (String line : lines) {
//            if (line != null && line.startsWith(pp)) {
//                a.append(line);
//                a.newLine();
//            }
//        }
//        return a.toString();
//    }

//    public static Set<String> extractVarCodesDead(String invClob) {
//        ImmutableSet.Builder<String> b = ImmutableSet.builder();
//        ImmutableList<String> sLines = Space.parseLines(invClob);
//        for (String sLine : sLines) {
//            Collection<String> varCodes = Line.extractVarCodes(sLine);
//            b.addAll(varCodes);
//        }
//        return b.build();
//    }


    public static ImmutableSet<String> createMsrpBitVarCodes() {
        return VarSpace.createInt32VarCodes(MSRP_PREFIX);
    }

    public static ImmutableSet<String> createDealerBitVarCodes() {
        return VarSpace.createInt32VarCodes(DLR_PREFIX);
    }

    public void print() {
        for (Line line : lines) {
            System.err.println(line);
        }

    }

    @Override
    public String toString() {
        return super.toString();
    }

    public String serializeLines() {
        Ser a = new Ser();
        serializeLines(a);
        return a.toString();
    }

    public void serializeLines(Ser a) {
        for (Line line : lines) {
            line.serializeLine(a);
            a.newLine();
        }
    }

    public static boolean hasMsrps(String iClob) {
        return iClob.indexOf('$') != -1;
    }

    public static boolean hasDealers(String iClob) {
        return iClob.indexOf(DLR_PREFIX + "_") != -1;
    }

    public boolean invFileContainsMsrps() {
        if (hasMsrp == null) {
            Line firstLine = getLines().iterator().next();
            this.hasMsrp = firstLine.hasMsrp();
            for (Line line : lines) {
                assert line.hasMsrp() == this.hasMsrp;
            }
        }
        return this.hasMsrp;
    }

    public boolean hasDealerCodes() {
        if (this.hasDealerCodes == null) {
            Line firstLine = getLines().iterator().next();
            this.hasDealerCodes = firstLine.hasDealerCodes();
            for (Line line : lines) {
                assert line.hasDealerCodes() == this.hasDealerCodes;
            }
        }
        return this.hasDealerCodes;
    }

    public Set<Var> computeAlwaysTrueVars() {
        ImmutableSet.Builder<Var> b = new ImmutableSet.Builder<Var>();
        for (Var var : vars) {
            if (isAlwaysTrueVar(var)) {
                b.add(var);
            }
        }
        return b.build();
    }

    public boolean isAlwaysTrueVar(Var var) {
        for (Line line : lines) {
            if (!line.containsVar(var)) {
                return false;
            }
        }
        return true;
    }

    public VarInfo mkVarInfo() {
        return new VarInfo() {
            @Override
            public String getAttribute(Set<String> context, String varName, String attName) {
                throw new UnsupportedOperationException();
            }

            @Override
            public boolean isFio(String varCode) {
                throw new UnsupportedOperationException();
            }

            @Override
            public boolean isPio(String varCode) {
                throw new UnsupportedOperationException();
            }

            @Override
            public boolean isVtc(String varCode) {
                throw new UnsupportedOperationException();
            }

            @Override
            public boolean isInvAcy(String varCode) {
                throw new UnsupportedOperationException();
            }

            @Override
            public String getImpliedVarCode(Set<String> context, String featureType) {
                throw new UnsupportedOperationException();
            }

            @Override
            public boolean isAssociated(Set<String> context, String varCode) {
                throw new UnsupportedOperationException();
            }

            @Override
            public String getFeatureType(String pickCode) {
                throw new UnsupportedOperationException();
            }

            @Override
            public int getPrice(String[] picks) {
                throw new UnsupportedOperationException();
            }

            @Override
            public String getFeaturesByType(String[] context, String type) {
                throw new UnsupportedOperationException();
            }

            @Override
            public boolean isDerived(String[] context, String pickCode) {
                throw new UnsupportedOperationException();
            }
        };
    }

    public Exp mkGiantOr(Space space) {
        assert !isEmpty();
        ArgBuilder orArgs = new ArgBuilder(space, Op.Or);
        for (Line line : lines) {
            Exp record = line.mkRecord();
            orArgs.addExp(record);
        }
        return orArgs.mk();
    }

    public Set<Cube> toCubeSet() {
        ImmutableSet.Builder<Cube> b = ImmutableSet.builder();
        for (Line line : lines) {
            Cube cube = line.toFullCube(this);
            b.add(cube);
        }
        return b.build();
    }

    public Set<VarSet> toTrueVarRecordSet() {
        ImmutableSet.Builder<VarSet> b = ImmutableSet.builder();
        for (Line line : lines) {
            VarSet record = line.getVars();
            b.add(record);
        }
        return b.build();
    }

    public Exp createXor(VarSet vars) {
        return space.mkXor(vars);
    }

    public Exp mkYearXor() {
        return createXor(getYearVars());
    }

    public Exp mkModelXor() {
        return createXor(getModelVars());
    }

    public Exp mkXColXor() {
        return createXor(getXColVars());
    }

    public Exp mkIColXor() {
        return createXor(getIColVars());
    }

    public Exp mkMsrpBucketXor() {
        VarSet msrpVars = getMsrpBucketVars();
        return createXor(msrpVars);
    }

    public Exp mkMsrpStrictXor() {
        VarSet msrpVars = getMsrpVars();
        return createXor(msrpVars);
    }

    public Csp createInvOnlyCsp() {
        Csp csp = new Csp(space);
        addConstraintsToCsp(csp);
        return csp;
    }

    public Exp buildInventoryConstraint() {

        ArgBuilder and = new ArgBuilder(space, Op.And);
        and.addExp(mkYearXor());
        and.addExp(mkModelXor());
        and.addExp(mkXColXor());
        and.addExp(mkIColXor());

        //add msrps (vr per msrp)
        if (invFileContainsMsrps()) {
            if (USE_MSRP_BUCKETS) {
                and.addExp(mkMsrpBucketXor());
            }
            if (USE_MSRP_STRICT) {
                and.addExp(mkMsrpStrictXor());
            }
        }

        Exp arg = mkGiantOr(space);

        and.addExp(arg);

        return and.mk();

    }


    public Exp mkAllDealersZero() {
        ArgBuilder and = new ArgBuilder(space, Op.And);
        VarSet dealerVars = getDealerVars();
        for (Var dealerVar : dealerVars) {
            and.addExp(dealerVar.mkNegLit());
        }
        return and.mk();
    }

    public ImmutableSet<ColorCombo> computeValidColorCombos() {
        ImmutableSet.Builder<ColorCombo> b = ImmutableSet.builder();
        for (Line line : lines) {
            ColorCombo colorCombo = line.getColorCombo();
            b.add(colorCombo);
        }
        return b.build();
    }

    public ImmutableSet<SeriesYear> computeValidSeriesYearCombos(Map<Var, Var> modelToSeriesMap) {
        ImmutableSet.Builder<SeriesYear> b = ImmutableSet.builder();
        for (Line line : lines) {
            YearModel yearModel = line.getYearModel();
            Var model = yearModel.getModel();
            Var series = modelToSeriesMap.get(model);
            SeriesYear seriesYear = new SeriesYear(series, yearModel.getYear());
            b.add(seriesYear);
        }
        return b.build();
    }

    public ImmutableSet<Ymxi> computeYmxis() {
        ImmutableSet.Builder<Ymxi> b = ImmutableSet.builder();
        for (Line line : lines) {
            Ymxi ymxi = line.getYmxi();
            b.add(ymxi);
        }
        return b.build();
    }

    public ImmutableSet<ColorCombo> computeInvalidColorCombos() {

        ImmutableSet<ColorCombo> validColorCombos = computeValidColorCombos();

        VarSet xColVars = getXColVars();
        VarSet iColVars = getIColVars();


        ImmutableSet.Builder<ColorCombo> b = ImmutableSet.builder();

        for (Var xColVar : xColVars) {
            for (Var iColVar : iColVars) {
                ColorCombo colorCombo = new ColorCombo(xColVar, iColVar);
                if (!validColorCombos.contains(colorCombo)) {
                    b.add(colorCombo);
                }

            }
        }

        return b.build();
    }


    public void addConstraintsToCsp(Csp csp) {


        negateOutOfStockCoreVars(csp);

        addCoreXorsToCsp(csp);

        assert csp.isOpen();

        //add msrps (vr per msrp)
        if (invFileContainsMsrps()) {
            if (USE_MSRP_BUCKETS) {
                csp.addConstraint(mkMsrpBucketXor());
            }
            if (USE_MSRP_STRICT) {
                csp.addConstraint(mkMsrpStrictXor());
            }

            assert csp.isOpen();
        }


        assert csp.isOpen();
        addGiantOrToCsp(csp);
        assert csp.isOpen();
    }

    private void negateOutOfStockCoreVars(Csp csp) {
        VarSet coreFactoryVars = space.getCoreVars();
        VarSet invVars = getVars();
        VarSet invModeDeadVars = coreFactoryVars.minus(invVars);
        for (Var invModeDeadVar : invModeDeadVars) {
            csp.addConstraint(invModeDeadVar.mkNegLit());
        }
    }


    public void addConstraintsToCsp2(Csp csp) {
        Exp exp = buildInventoryConstraint();
        csp.addConstraint(exp);
    }

    public void addGiantOrToCsp(Csp csp) {
        Exp giantOr = mkGiantOr(space);
        csp.addConstraint(giantOr);
    }


    public void addCoreXorsToCsp(Csp csp) {
        csp.addConstraint(mkYearXor());
        csp.addConstraint(mkModelXor());
        csp.addConstraint(mkXColXor());
        csp.addConstraint(mkIColXor());
    }

    public void addColorConflictsToCsp(Csp csp) {
        ImmutableSet<ColorCombo> invalidColorCombos = computeInvalidColorCombos();
        for (ColorCombo colorCombo : invalidColorCombos) {
            Var xCol = colorCombo.getXCol();
            Var iCol = colorCombo.getICol();
            Exp constraint = space.mkBinaryNand(xCol, iCol);
            csp.addConstraint(constraint);
        }
    }

//    public Csp createComboCsp(Csp factoryCsp) {
//        Csp cspCombo = factoryCsp.copy();
//        addConstraintsToCsp(cspCombo);
//        return cspCombo;
//    }

    public Csp createComboCsp(Csp factoryCsp) {
        Csp cspCombo = factoryCsp.copy();
        addConstraintsToCsp(cspCombo);
        return cspCombo;
    }

    public ImmutableList<Integer> getMsrpsSorted() {
        return getMsrpBucketsSorted();
    }

    public ImmutableList<Integer> getMsrpsSortedStrict() {
        HashSet<Integer> b = new HashSet<Integer>();
        for (Line line : getLines()) {
            Integer msrp = line.getMsrp();
            b.add(msrp);
        }
        ArrayList<Integer> a = new ArrayList<Integer>(b);
        Collections.sort(a);
        return ImmutableList.copyOf(a);
    }

    public ImmutableList<Integer> getMsrpBucketsSorted() {
        ImmutableSet<Integer> msrpBuckets = getMsrpBuckets();
        ArrayList<Integer> a = new ArrayList<Integer>(msrpBuckets);
        Collections.sort(a);
        return ImmutableList.copyOf(a);
    }

    public ImmutableSet<Integer> getMsrpBuckets() {
        ImmutableSet.Builder<Integer> b = ImmutableSet.builder();
        for (Line line : getLines()) {
            Integer msrp = line.getMsrpBucket();
            b.add(msrp);
        }
        return b.build();
    }

    public ImmutableSet<Integer> getMsrps() {
        ImmutableSet.Builder<Integer> b = ImmutableSet.builder();
        for (Line line : getLines()) {
            Integer msrp = line.getMsrp();
            b.add(msrp);
        }
        return b.build();
    }

    public VarSet getMsrpBucketVars() {
        ImmutableSet<Integer> msrpsBuckets = getMsrpBuckets();
        VarSetBuilder b = space.newMutableVarSet();
        for (Integer msrpBucket : msrpsBuckets) {
            String varCode = Space.getMsrpBucketVarCode(msrpBucket);
            b.addVar(varCode);
        }
        return b.build();
    }

    public VarSet getMsrpVars() {
        ImmutableSet<Integer> msrps = getMsrps();
        VarSetBuilder b = space.newMutableVarSet();
        for (Integer msrp : msrps) {
            String varCode = Space.getMsrpStrictVarCode(msrp);
            b.addVar(varCode);
        }
        return b.build();
    }

    public Exp toDnf() {
        Exp exp = mkGiantOr(space);
        return exp;
    }


    public Set<Ymxi> computeCoreXorCombos() {
        return computeYmxis();
    }

    public Inv removeRowWithUnVtcVars() {
        InvBuilder toKeep = new InvBuilder(space);


        Space space = getSpace();
        VarInfo varInfo = space.getVarInfo();
        VarSetBuilder b = space.newMutableVarSet();
        VarSet invVars = getVars();
        for (Var invVar : invVars) {
            if (!varInfo.isVtc(invVar.getVarCode())) {
                b.add(invVar);
            }
        }

        VarSet invUnVtcVars = b.build();


        for (Line line : lines) {
            VarSet vars = line.getVars();
            VarSet intersection = vars.overlap(invUnVtcVars);
            if (intersection.isEmpty()) {
                toKeep.addLine(line);
            } else {
                log.info("Skipping invClob line[" + line + "] because it contains un-vtc vars[" + intersection + "]");
            }
        }

        return toKeep.build();
    }

    public boolean containsCube(Cube cube) {
        VarSet cubeVars = cube.getTrueVars();
        for (Line line : lines) {
            VarSet lineVars = line.getVars();
            if (lineVars.equals(cubeVars)) {
                return true;
            }
        }
        return false;
    }

    public static void serializeInvVarMapTiny(Ser a) {
        a.append(Vars.HEAD_INV_VARS_LINE);
        VarSet invVars = getInvVars();
        if (invVars != null) {
            Iterator<Var> it = invVars.iterator();
            while (it.hasNext()) {
                Var invVar = it.next();
                int varId = invVar.getVarId();
                a.append(varId);
                if (it.hasNext()) {
                    a.argSep();
                }
            }
        }

        a.append(RPAREN);
    }

    static public VarSet getInvVars() {
        throw new UnsupportedOperationException();
    }

    static public VarSet getDealerVars() {
        throw new UnsupportedOperationException();
    }


}