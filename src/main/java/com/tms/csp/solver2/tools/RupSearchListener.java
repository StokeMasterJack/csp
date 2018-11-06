package com.tms.csp.solver2.tools;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;

import com.tms.csp.solver2.core.LiteralsUtils;
import com.tms.csp.solver2.specs.IConstr;
import com.tms.csp.solver2.specs.ISolverService;
import com.tms.csp.solver2.specs.Lbool;

/**
 * Output an unsat proof using the reverse unit propagation (RUP) format.
 * 
 * @author daniel
 * 
 * @param <S>
 *            a solver service
 * @since 2.3.4
 */
public class RupSearchListener<S extends ISolverService> extends
        SearchListenerAdapter<S> {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private PrintStream out;
    private final File file;

    public RupSearchListener(String filename) {
        file = new File(filename);
    }

    @Override
    public void init(S solverService) {
        try {
            out = new PrintStream(new FileOutputStream(file));
        } catch (FileNotFoundException e) {
            out = System.out;
        }
    }

    @Override
    public void end(Lbool result) {
        if (result == Lbool.FALSE) {
            out.println("0");
            out.close();
        } else {
            out.close();
            file.delete();
        }
    }

    @Override
    public void learn(IConstr c) {
        for (int i = 0; i < c.size(); i++) {
            out.print(LiteralsUtils.toDimacs(c.get(i)));
            out.print(" ");
        }
        out.println("0");
    }

    @Override
    public void learnUnit(int p) {
        out.print(p);
        out.println(" 0");
    }

}
