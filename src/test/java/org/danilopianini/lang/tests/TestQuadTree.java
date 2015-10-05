package org.danilopianini.lang.tests;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertEquals;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.danilopianini.lang.FlexibleQuadTree;
import org.junit.Test;

/**
 * @author Danilo Pianini
 *
 */
public class TestQuadTree {
	
	private static final int INSERTIONS = 10000;
	private static final int SUB_INS = INSERTIONS / 4;

	/**
	 * 
	 */
	@Test
	public void testRandom() {
		final Random rnd = new Random(0);
		final List<double[]> testCase = IntStream.range(0, INSERTIONS)
		.mapToObj(i -> new double[]{
				rnd.nextLong() + rnd.nextDouble(),
				rnd.nextLong() + rnd.nextDouble()
		})
		.collect(Collectors.toList());
		final FlexibleQuadTree<Object> qt = new FlexibleQuadTree<>();
		testCase.stream().forEach(o -> {
			qt.insert(o, o[0], o[1]);
		});
		assertEquals(INSERTIONS, qt.query(-Double.MAX_VALUE, -Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE).size());
		testCase.stream().forEach(o -> assertTrue(qt.remove(o, o[0], o[1])));
		assertEquals(0, qt.query(-Double.MAX_VALUE, -Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE).size());
		testCase.stream().forEach(o -> assertFalse(qt.remove(o, o[0], o[1])));
	}

	/**
	 * 
	 */
	@Test
	public void testSubdivide() {
		final FlexibleQuadTree<Object> qt = new FlexibleQuadTree<>();
		IntStream.range(0, SUB_INS).forEach(v -> {
			final double val = v / (double) SUB_INS;
			qt.insert(v, val, val);
			qt.insert(v, -val, val);
			qt.insert(v, val, -val);
			qt.insert(v, -val, -val);
		});
		assertEquals(4 * SUB_INS, qt.query(-Double.MAX_VALUE, -Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE).size());
		assertEquals(SUB_INS + 3, qt.query(0, 0, Double.MAX_VALUE, Double.MAX_VALUE).size());
		assertEquals(SUB_INS - 1, qt.query(0, 0, -Double.MAX_VALUE, Double.MAX_VALUE).size());
		assertEquals(SUB_INS - 1, qt.query(0, 0, Double.MAX_VALUE, -Double.MAX_VALUE).size());
		assertEquals(SUB_INS - 1, qt.query(0, 0, -Double.MAX_VALUE, -Double.MAX_VALUE).size());
		IntStream.range(0, SUB_INS).forEach(v -> {
			final double val = v / (double) SUB_INS;
			assertTrue("Test failed for " + v + ".", qt.move(v, val, val, val / 2, val / 2));
			assertTrue(qt.move(v, -val, val, -val / 2, val / 2));
			assertTrue(qt.move(v, val, -val, val / 2, -val / 2));
			assertTrue(qt.move(v, -val, -val, -val / 2, -val / 2));
		});
		assertEquals(4 * SUB_INS, qt.query(-Double.MAX_VALUE, -Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE).size());
		IntStream.range(0, SUB_INS).forEach(v -> {
			final double val = v / (double) SUB_INS / 2;
			assertTrue(qt.remove(v, val, val));
			assertTrue(qt.remove(v, -val, val));
			assertTrue(qt.remove(v, val, -val));
			assertTrue(qt.remove(v, -val, -val));
		});
		assertEquals(0, qt.query(-Double.MAX_VALUE, -Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE).size());
		IntStream.range(0, SUB_INS).forEach(v -> {
			final double val = v / (double) SUB_INS / 2;
			assertFalse(qt.remove(v, val, val));
			assertFalse(qt.remove(v, -val, val));
			assertFalse(qt.remove(v, val, -val));
			assertFalse(qt.remove(v, -val, -val));
		});
		assertEquals(0, qt.query(-Double.MAX_VALUE, -Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE).size());
	}

}
