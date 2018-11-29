package com.smartsoft.inv;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multimap;
import com.smartsoft.csp.ast.Space;
import com.smartsoft.csp.util.BadVarCodeException;
import com.smartsoft.csp.varSet.VarSet;
import com.smartsoft.csp.varSet.VarSetBuilder;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

import static com.google.common.base.Preconditions.checkNotNull;

public class InvBuilder {

    private final Space space;
    private final Multimap<Set<String>, Line> multimap;

    private int dupCount;

    int rejectCount = 0;
    int vetoCount = 0;
    int dupKeyCount = 0;

    private HashSet<String> badVarCodes = new HashSet<String>();

    private Inv inv;

    public InvBuilder(Space space) {
        checkNotNull(space);
        this.space = space;
        multimap = ArrayListMultimap.create();
    }

    public static Inv buildInv(Space sp, Iterable<String> invLines) {
        InvBuilder bb = new InvBuilder(sp);
        bb.addLines1(invLines);
        return bb.build();
    }

    public Space getSpace() {
        return space;
    }

    public void addLines(Iterable<Line> lines) {
        for (Line line : lines) {
            addLine(line);
        }
    }

    public void addLines1(Iterable<String> lines) {
        for (String line : lines) {
            addLine(line);
        }
    }

    int addLineCount = 0;

    public boolean addLine(String sLine) {
        addLineCount++;

        Line line;

        try {
            line = Line.parse(sLine, space);
        } catch (BadVarCodeException e) {
//            String msg = rejectCount + ": Skipping inventory row:[" + sLine + "] because it contains an invalid vr code[" + e.getBadVarCode() + "]";
//            log.info(msg);
            badVarCodes.add(e.getBadVarCode());
            rejectCount++;
            return false;
        }

        if (!line.acceptLine()) {
            vetoCount++;
            return false;
        }

        Set<String> key = line.getVarCodes();

        boolean dupKey = multimap.containsKey(key);
        if (dupKey) {
            dupKeyCount++;
        }

        return multimap.put(key, line); //this multimap impl always returns true

    }

    public boolean addLine(Line line) {
        Set<String> key = line.getVarCodes();
        return multimap.put(key, line);
    }

    public Inv build() {

        ImmutableSet.Builder<Line> bLines = ImmutableSet.builder();

        Set<Set<String>> keys = multimap.keySet();
        for (Set<String> key : keys) {

            Collection<Line> lines = multimap.get(key);

            if (lines.size() == 0) {
                throw new IllegalStateException();
            } else if (lines.size() == 1) {
                bLines.add(lines.iterator().next());
            } else {
                log.fine("Merging [" + lines.size() + "] Inv Lines");
                int qty = 0;
                Integer msrp = Integer.MAX_VALUE;
                ImmutableSet.Builder<Integer> bDealers = ImmutableSet.builder();
                for (Line line : lines) {
                    qty += line.getQty();
                    if (line.hasMsrp()) {
                        msrp = Math.min(msrp, line.getMsrp());
                    }
                    if (line.hasDealerCodes()) {
                        bDealers.addAll(line.getDealerCodes());
                    }
                }

                VarSetBuilder b = space.newMutableVarSet();
                for (String varCode : key) {
                    b.addVar(varCode);
                }
                VarSet vars = b.build();

                if (msrp == Integer.MAX_VALUE) {
                    msrp = null;
                }

                Line combinedLine = new Line(vars, qty, msrp, bDealers.build());
                bLines.add(combinedLine);

                dupCount += lines.size() - 1;
            }

        }

        ImmutableSet<Line> normalizedLines = bLines.build();
        inv = new Inv(space, normalizedLines);

        return inv;

    }

    public void printSummary() {
        if (true) {
            log.info("addLineCount[" + addLineCount + "]");
            log.info("  rejectCount (BadVarCodeException) [" + rejectCount + "]");
            log.info("  vetoCount (!accept) [" + vetoCount + "]");
            log.info("  dupKeyCount (merged lines) [" + dupKeyCount + "]");
            log.info("  keepCount [" + inv.size() + "]");
            log.info("badVarCodes:" + badVarCodes.size() + ":" + badVarCodes);


        }
    }

    public int getAddLineCount() {
        return addLineCount;
    }

    public int getDupCount() {
        return dupCount;
    }

    private static Logger log = Logger.getLogger(InvBuilder.class.getName());

}
