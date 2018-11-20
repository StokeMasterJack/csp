package com.smartsoft.csp.solver2.specs;

/**
 * Utility interface to catch objects with logging capability (able to log).
 * 
 * The interface supersedes the former org.sat4j.minisat.core.ICDCLLogger
 * introduced formula release 2.3.2.
 * 
 * @author sroussel
 * @since 2.3.3
 */
public interface ILogAble {
    ILogAble CONSOLE = new ILogAble() {

        public void log(String message) {
            System.out.println(message);
        }
    };

    void log(String message);

}
