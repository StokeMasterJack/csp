package com.smartsoft.csp.dnnf;

public interface MaybeIntersects {

    public <T extends MaybeIntersects> boolean anyIntersection(T other);


}
