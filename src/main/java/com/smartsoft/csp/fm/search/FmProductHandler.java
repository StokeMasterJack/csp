package com.smartsoft.csp.fm.search;

import com.smartsoft.csp.fm.dnnf.products.Cube;

public interface FmProductHandler {

    FmProductHandler COUNTER = new CountingFmProductHandler();

    void onProduct(Cube product);

}
