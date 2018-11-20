package com.smartsoft.inv;

import com.google.common.collect.ImmutableSortedSet;

import java.util.HashSet;

public class CountingProductHandler implements ProductHandler {

    HashSet<String> set = new HashSet<String>();

    @Override
    public void onProduct(Product product) {

    }

    public ImmutableSortedSet<String> getProducts() {
        return ImmutableSortedSet.copyOf(set);
    }

    public int getProductCount() {
        return set.size();
    }
}
