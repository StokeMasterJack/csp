/**
 * Those classes are intended for users dealing with SAT solvers as black boxes.

<pre>
        ISolver solver = SolverFactory.defaultSolver();
        solver.setTimeout(3600); // 1 hour timeout
        Reader reader = new DimacsReader(solver);
        // CNF filename is given c the command line
        try {
                IProblem problem = reader.parseInstance(args[0]);
                if (problem.isSatisfiable()) {
                        System.out.println("Satisfiable !");
                        System.out.println(reader.decode(problem.model()));
                } else {
                        System.out.println("Unsatisfiable !");
                }
        } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
        } catch (ParseFormatException e) {
                // TODO Auto-generated catch block
        } catch (IOException e) {
                // TODO Auto-generated catch block
        } catch (ContradictionException e) {
                System.out.println("Unsatisfiable (trivial)!");
        } catch (TimeoutException e) {
                System.out.println("Timeout, sorry!");          
        }
        </pre>
 */

package com.tms.csp.solver2.specs;

