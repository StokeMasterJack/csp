package com.smartsoft.csp.pl;

import com.google.common.collect.ImmutableSet;
import com.smartsoft.csp.util.Pair;
import com.smartsoft.csp.util.Range;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;

public class RangeTest {

    @Test
    public void test1() throws Exception {
        Range r03 = new Range(0, 3);
        Range r04 = new Range(0, 4);

        assertEquals(4, r03.size());
        assertEquals(5, r04.size());

        assertEquals(6, r03.twoSetCount());
        assertEquals(10, r04.twoSetCount());

        assertEquals(16, r03.twoTupleCount());
        assertEquals(25, r04.twoTupleCount());

        Set<Pair> list = r03.getAllPairs();
        System.err.println(list);


        HashSet<Pair> set = new HashSet<Pair>();
        set.addAll(list);

        assertEquals(set.size(), list.size());

        assertEquals(6, list.size());

        System.err.println("set.size()[" + set.size() + "]");
    }

    @Test
    public void test2() throws Exception {
        Range.dupCheck();

        Range r = new Range(0, 1000);

        System.err.println(r.twoSetCount());
        System.err.println(r.twoTupleCount());

    }

    @Test
    public void testNTupleCount() throws Exception {
        Range r00 = new Range(0, 0);
        Range r01 = new Range(0, 1);
        Range r02 = new Range(0, 2);
        Range r03 = new Range(0, 3);

        assertEquals(1, r00.size());
        assertEquals(1, r00.nTupleCount(1));
        assertEquals(1, r00.getNSets(1).size());


        assertEquals(2, r01.size());
        assertEquals(2, r01.nTupleCount(1));
        assertEquals(4, r01.nTupleCount(2));
        assertEquals(1, r01.twoSetCount());
        assertEquals(1, r01.getNSets(2).size());

        assertEquals(3, r02.size());
        assertEquals(3, r02.nTupleCount(1));
        assertEquals(6, r02.nTupleCount(2));
        assertEquals(9, r02.nTupleCount(3));

        assertEquals(3, r02.twoSetCount());
        assertEquals(3, r02.getAllTwoSets().size());
        assertEquals(3, r02.getAllPairs().size());
        assertEquals(3, r02.getNSets(2).size());

        ImmutableSet<ImmutableSet<Integer>> n3 = r02.getNSets(3);
        System.err.println("threeSets.size()[" + n3.size() + "]");


//        assertEquals(4, r03.size());
//        assertEquals(3, r03.twoSetCount());
//        assertEquals(3, r03.getAllTwoSets().size());
//        assertEquals(3, r03.getAllPairs().size());

        r03.print();

//        assertEquals(3, r03.getNSets(2).size());


//        r00.getNSets(1);


    }


}
