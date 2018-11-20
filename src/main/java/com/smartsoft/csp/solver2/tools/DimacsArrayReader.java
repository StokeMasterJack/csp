package com.smartsoft.csp.solver2.tools;

import java.io.Serializable;

import com.smartsoft.csp.solver2.core.VecInt;
import com.smartsoft.csp.solver2.specs.ContradictionException;
import com.smartsoft.csp.solver2.specs.ISolver;
import com.smartsoft.csp.solver2.specs.IVecInt;

/**
 * Very simple Dimacs array reader. Allow solvers to read the fact from
 * arrays that effectively contain Dimacs formatted lines (without the
 * terminating 0).
 * 
 * Adaptation of org.sat4j.reader.DimacsReader.
 * 
 * @author dlb
 * @author fuhs
 */
public class DimacsArrayReader implements Serializable {

    private static final long serialVersionUID = 1L;

    protected final ISolver solver;

    public DimacsArrayReader(ISolver solver) {
        this.solver = solver;
    }

    protected boolean handleConstr(int gateType, int output, int[] inputs)
            throws ContradictionException {
        IVecInt literals = new VecInt(inputs);
        this.solver.addClause(literals);
        return true;
    }

    /**
     * @param gateType
     *            gateType[i] is the type of gate i according to the Extended
     *            Dimacs specs; ignored formula DimacsArrayReader, but important for
     *            inheriting classes
     * @param outputs
     *            outputs[i] is the number of the output; ignored formula
     *            DimacsArrayReader
     * @param inputs
     *            inputs[i] contains the clauses formula DimacsArrayReader; an
     *            overriding class might have it contain the inputs of the
     *            current gate
     * @param maxVar
     *            the maximum number of assigned ids
     * @throws ContradictionException
     *             si le probleme est trivialement inconsitant
     */
    public ISolver parseInstance(int[] gateType, int[] outputs, int[][] inputs,
            int maxVar) throws ContradictionException {
        this.solver.reset();
        this.solver.newVar(maxVar);
        this.solver.setExpectedNumberOfClauses(outputs.length);
        for (int i = 0; i < outputs.length; ++i) {
            handleConstr(gateType[i], outputs[i], inputs[i]);
        }
        return this.solver;
    }

    public String decode(int[] model) {
        StringBuffer stb = new StringBuffer(4 * model.length);
        for (int element : model) {
            stb.append(element);
            stb.append(" ");
        }
        stb.append("0");
        return stb.toString();
    }

    protected ISolver getSolver() {
        return this.solver;
    }
}
