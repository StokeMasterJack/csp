package com.smartsoft.csp.fm.dnnf;

public interface MaybeIntersects {

    public <T extends MaybeIntersects> boolean anyIntersection(T other);


}
