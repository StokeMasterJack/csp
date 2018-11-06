package com.tms.csp.fm.search;

import com.tms.csp.fm.dnnf.products.Cube;

public interface FmProductHandler {

    FmProductHandler COUNTER = new CountingFmProductHandler();

    void onProduct(Cube product);

}
