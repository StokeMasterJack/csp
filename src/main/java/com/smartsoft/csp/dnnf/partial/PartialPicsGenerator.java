package com.smartsoft.csp.dnnf.partial;

import com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.List;

public class PartialPicsGenerator<V> {

    private final PartialPicsCallback<V> callback;
    private boolean keepGoing = true;

    public PartialPicsGenerator(PartialPicsCallback<V> callback, ImmutableList<V> userPrefs /* aka init picks */) {
        this.callback = callback;
        start(userPrefs);
    }

    private void start(ImmutableList<V> pics) {
        for (int i = pics.size() - 1; i >= 0 && keepGoing; i--) {
            List<V> picked = ImmutableList.of();
            getPicks(picked, pics, i + 1);
        }
    }

    private void getPicks(List<V> picked, List<V> pics, int items) {

        if (!keepGoing) {
            return;
        }

        if (items == 0) {
            keepGoing = callback.onPics(picked);
            return;
        }

        if (pics.size() == items) {
            keepGoing = callback.onPics(concat(picked, pics));
            return;
        }

        getPicks(concat(picked, pics.subList(0, 1)), pics.subList(1, pics.size()), items - 1);
        getPicks(picked, pics.subList(1, pics.size()), items);


    }

    private List<V> concat(List<V> list1, List<V> list2) {
        ArrayList<V> a = new ArrayList<V>(list1.size() + list2.size());
        a.addAll(list1);
        a.addAll(list2);
        return a;
    }

}


