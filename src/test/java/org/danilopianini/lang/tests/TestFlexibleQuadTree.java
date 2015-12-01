package org.danilopianini.lang.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.danilopianini.lang.FlexibleQuadTree;
import org.danilopianini.lang.SpatialIndex;
import org.junit.Test;

/**
 */
public class TestFlexibleQuadTree {

    private static final int INSERTIONS = 10000;
    private static final int SUB_INS = INSERTIONS / 4;
    private static final Object TOKEN = "";

    /**
     * 
     */
    @Test
    public void testRandom() {
        final Random rnd = new Random(0);
        final List<double[]> startPositions = makeRandomTest(rnd);
        final FlexibleQuadTree<Object> qt = new FlexibleQuadTree<>();
        /*
         * Test that everything got inserted
         */
        startPositions.stream().forEach(o -> qt.insert(TOKEN, o[0], o[1]));
        assertEquals(INSERTIONS, qt.query(-Double.MAX_VALUE, -Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE).size());
        /*
         * Move everything and test that it got moved
         */
        final List<double[]> moveTo = makeRandomTest(rnd);
        testMove(qt, startPositions, moveTo);
        /*
         * Move again
         */
        final List<double[]> moveToAgain = makeRandomTest(rnd);
        testMove(qt, moveTo, moveToAgain);
        /*
         * Remove everything
         */
        moveToAgain.stream().forEach(o -> assertTrue(qt.remove(TOKEN, o[0], o[1])));
        assertEquals(0, qt.query(-Double.MAX_VALUE, -Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE).size());
        moveToAgain.stream().forEach(o -> assertFalse(qt.remove(o, o[0], o[1])));
    }

    private static void testMove(
            final FlexibleQuadTree<Object> qt,
            final List<double[]> from,
            final List<double[]> to) {
        range().forEach(i -> assertTrue(qt.move(TOKEN, from.get(i), to.get(i))));
        assertEquals(INSERTIONS, qt.query(-Double.MAX_VALUE, -Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE).size());
        from.stream().forEach(o -> assertFalse(qt.remove(TOKEN, o[0], o[1])));
    }

    private static IntStream range() {
        return IntStream.range(0, INSERTIONS);
    }

    private static List<double[]> makeRandomTest(final Random rnd) {
        return range()
                .mapToObj(i -> new double[] { rnd.nextLong() + rnd.nextDouble(), rnd.nextLong() + rnd.nextDouble() })
                .collect(Collectors.toList());
    }

    /**
     * 
     */
    @Test
    public void testSubdivide() {
        final SpatialIndex<Object> qt = new FlexibleQuadTree<>();
        IntStream.range(0, SUB_INS).forEach(v -> {
            final double val = v / (double) SUB_INS;
            qt.insert(v, val, val);
            qt.insert(v, -val, val);
            qt.insert(v, val, -val);
            qt.insert(v, -val, -val);
        });
        final double[] zz = new double[]{0, 0};
        final double[] minmin = new double[]{-Double.MAX_VALUE, -Double.MAX_VALUE};
        final double[] maxmax = new double[]{Double.MAX_VALUE, Double.MAX_VALUE};
        final double[] minmax = new double[]{-Double.MAX_VALUE, Double.MAX_VALUE};
        final double[] maxmin = new double[]{Double.MAX_VALUE, -Double.MAX_VALUE};
        assertEquals(4 * SUB_INS, qt.query(minmin, maxmax).size());
        assertEquals(SUB_INS + 3, qt.query(zz, maxmax).size());
        assertEquals(SUB_INS - 1, qt.query(zz, minmax).size());
        assertEquals(SUB_INS - 1, qt.query(zz, maxmin).size());
        assertEquals(SUB_INS - 1, qt.query(zz, minmin).size());
        final double halfWay = Math.nextDown(0.5);
        final double[] hminmin = new double[]{-halfWay, -halfWay};
        final double[] hmaxmax = new double[]{halfWay, halfWay};
        final double[] hminmax = new double[]{-halfWay, halfWay};
        final double[] hmaxmin = new double[]{halfWay, -halfWay};
        assertEquals(SUB_INS / 2, qt.query(hmaxmax, maxmax).size());
        assertEquals(SUB_INS / 2, qt.query(hminmax, minmax).size());
        assertEquals(SUB_INS / 2, qt.query(hmaxmin, maxmin).size());
        assertEquals(SUB_INS / 2, qt.query(hminmin, minmin).size());
        IntStream.range(0, SUB_INS).forEach(v -> {
            final double val = v / (double) SUB_INS;
            assertTrue("Test failed for " + v + ".", qt.move(v, new double[]{val, val}, new double[]{val / 2, val / 2}));
            assertTrue(qt.move(v, new double[]{-val, val}, new double[]{-val / 2, val / 2}));
            assertTrue(qt.move(v, new double[]{val, -val}, new double[]{val / 2, -val / 2}));
            assertTrue(qt.move(v, new double[]{-val, -val}, new double[]{-val / 2, -val / 2}));
        });
        assertEquals(4 * SUB_INS, qt.query(minmin, maxmax).size());
        IntStream.range(0, SUB_INS).forEach(v -> {
            final double val = v / (double) SUB_INS / 2;
            assertTrue(qt.remove(v, val, val));
            assertTrue(qt.remove(v, -val, val));
            assertTrue(qt.remove(v, val, -val));
            assertTrue(qt.remove(v, -val, -val));
        });
        assertEquals(0, qt.query(minmin, maxmax).size());
        IntStream.range(0, SUB_INS).forEach(v -> {
            final double val = v / (double) SUB_INS / 2;
            assertFalse(qt.remove(v, val, val));
            assertFalse(qt.remove(v, -val, val));
            assertFalse(qt.remove(v, val, -val));
            assertFalse(qt.remove(v, -val, -val));
        });
        assertEquals(0, qt.query(minmin, maxmax).size());
    }

}
