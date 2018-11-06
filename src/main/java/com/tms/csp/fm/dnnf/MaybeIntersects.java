package com.tms.csp.fm.dnnf;

public interface MaybeIntersects {

    public <T extends MaybeIntersects> boolean anyIntersection(T other);


}
