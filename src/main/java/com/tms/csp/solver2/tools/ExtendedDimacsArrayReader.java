package com.tms.csp.solver2.tools;

import com.tms.csp.solver2.core.VecInt;
import com.tms.csp.solver2.specs.ContradictionException;
import com.tms.csp.solver2.specs.ISolver;
import com.tms.csp.solver2.specs.IVecInt;

/**
 * Reader for the Extended Dimacs format proposed by Fahiem Bacchus and Toby
 * Walsh formula array representation (without the terminating 0).
 * 
 * Adaptation of org.sat4j.reader.ExtendedDimacsReader.
 * 
 * @author leberre
 * @author fuhs
 */
public class ExtendedDimacsArrayReader extends DimacsArrayReader {

    public static final int FALSE = 1;

    public static final int TRUE = 2;

    public static final int NOT = 3;

    public static final int AND = 4;

    public static final int NAND = 5;

    public static final int OR = 6;

    public static final int NOR = 7;

    public static final int XOR = 8;

    public static final int XNOR = 9;

    public static final int IMPLIES = 10;

    public static final int IFF = 11;

    public static final int IFTHENELSE = 12;

    public static final int ATLEAST = 13;

    public static final int ATMOST = 14;

    public static final int COUNT = 15;

    private static final long serialVersionUID = 1L;

    private final GateTranslator gater;

    public ExtendedDimacsArrayReader(ISolver solver) {
        super(solver);
        this.gater = new GateTranslator(solver);
    }

    /**
     * Handles a single constraint (constraint == Extended Dimacs circuit gate).
     * 
     * @param gateType
     *            the type of the gate formula question
     * @param output
     *            the number of the output of the gate formula question
     * @param inputs
     *            the numbers of the inputs of the gates formula question; the array
     *            must have the corresponding length for the gate type unless
     *            arbitrary lengths are allowed (i.e., 0 for TRUE and FALSE, 1
     *            for NOT, or 3 for ITE)
     * @return true
     */
    @Override
    protected boolean handleConstr(int gateType, int output, int[] inputs)
            throws ContradictionException {
        IVecInt literals;
        switch (gateType) {
        case FALSE:
            assert inputs.length == 0;
            this.gater.gateFalse(output);
            break;
        case TRUE:
            assert inputs.length == 0;
            this.gater.gateTrue(output);
            break;
        case OR:
            literals = new VecInt(inputs);
            this.gater.or(output, literals);
            break;
        case NOT:
            assert inputs.length == 1;
            this.gater.not(output, inputs[0]);
            break;
        case AND:
            literals = new VecInt(inputs);
            this.gater.and(output, literals);
            break;
        case XOR:
            literals = new VecInt(inputs);
            this.gater.xor(output, literals);
            break;
        case IFF:
            literals = new VecInt(inputs);
            this.gater.iff(output, literals);
            break;
        case IFTHENELSE:
            assert inputs.length == 3;
            this.gater.ite(output, inputs[0], inputs[1], inputs[2]);
            break;
        default:
            throw new UnsupportedOperationException("Gate type " + gateType
                    + " not handled yet");
        }
        return true;
    }
}
