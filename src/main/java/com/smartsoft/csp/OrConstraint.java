package com.smartsoft.csp;

import java.util.Set;

/**
 * args must be _complexVars and must be PickOne child _complexVars
 */
public class OrConstraint {

    private final Set<String> args;

    /**
     * @param args must be _complexVars and must be PickOne child _complexVars
     */
    public OrConstraint(Set<String> args) {
        this.args = args;

    }

    public Set<String> getArgs() {
        return args;
    }
    
    public boolean addArg(String code){
		return args.add(code);
	}
	
	public boolean removeArg(String code){
		return args.remove(code);
	}
}
