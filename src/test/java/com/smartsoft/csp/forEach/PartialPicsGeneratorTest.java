package com.smartsoft.csp.forEach;

import com.google.common.collect.ImmutableList;
import com.smartsoft.csp.util.CspBaseTest2;
import com.smartsoft.csp.ast.Ser;
import com.smartsoft.csp.dnnf.partial.PartialPicsCallback;
import com.smartsoft.csp.dnnf.partial.PartialPicsGenerator;
import com.smartsoft.csp.parse.ParseUtil;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class PartialPicsGeneratorTest extends CspBaseTest2 {

    int counter;


    @Before
    public void setUp() throws Exception {
        counter = 0;
    }

    @Test
    public void testPartialPicsGenerator1() throws Exception {

        final Ser aa = new Ser();

        PartialPicsCallback<Integer> callback = new PartialPicsCallback<Integer>() {
            @Override
            public boolean onPics(List<Integer> pics) {
                String s = ParseUtil.Companion.serializeCodes2(pics);
                aa.append(s);
                aa.newLine();
                return true;
            }
        };

        ImmutableList<Integer> pics = ImmutableList.of(1, 2, 3, 4, 5);

        new PartialPicsGenerator<Integer>(callback, pics);

        String result = aa.toString().trim();

//        System.err.println(result1);

        assertEquals(expectedResult1, result);

    }

    @Test
    public void testPartialPicsGenerator2() throws Exception {
        final Ser aa = new Ser();

        PartialPicsCallback<Integer> callback = new PartialPicsCallback<Integer>() {
            @Override
            public boolean onPics(List<Integer> pics) {
                String s = ParseUtil.Companion.serializeCodes2(pics);
                aa.append(s);
                aa.newLine();
                counter++;
                return counter < 10;
            }
        };

        ImmutableList<Integer> pics = ImmutableList.of(1, 2, 3, 4, 5);

        new PartialPicsGenerator<Integer>(callback, pics);

        String result = aa.toString().trim();

        //        System.err.println(result1);

        assertEquals(expectedResult2, result);

    }

    String expectedResult1 =
            "1 2 3 4 5\n" +
                    "1 2 3 4\n" +
                    "1 2 3 5\n" +
                    "1 2 4 5\n" +
                    "1 3 4 5\n" +
                    "2 3 4 5\n" +
                    "1 2 3\n" +
                    "1 2 4\n" +
                    "1 2 5\n" +
                    "1 3 4\n" +
                    "1 3 5\n" +
                    "1 4 5\n" +
                    "2 3 4\n" +
                    "2 3 5\n" +
                    "2 4 5\n" +
                    "3 4 5\n" +
                    "1 2\n" +
                    "1 3\n" +
                    "1 4\n" +
                    "1 5\n" +
                    "2 3\n" +
                    "2 4\n" +
                    "2 5\n" +
                    "3 4\n" +
                    "3 5\n" +
                    "4 5\n" +
                    "1\n" +
                    "2\n" +
                    "3\n" +
                    "4\n" +
                    "5";


    String expectedResult2 =
            "1 2 3 4 5\n" +
                    "1 2 3 4\n" +
                    "1 2 3 5\n" +
                    "1 2 4 5\n" +
                    "1 3 4 5\n" +
                    "2 3 4 5\n" +
                    "1 2 3\n" +
                    "1 2 4\n" +
                    "1 2 5\n" +
                    "1 3 4";


}
