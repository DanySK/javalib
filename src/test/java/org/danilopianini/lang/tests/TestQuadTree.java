package org.danilopianini.lang.tests;

import static org.junit.Assert.*;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.danilopianini.lang.FlexibleQuadTree;
import org.danilopianini.lang.QuadTree;
import org.junit.Test;

import com.google.common.primitives.Ints;

public class TestQuadTree {
	
	private static final int INSERTIONS = 10;
	private static final int SUB_INS = 800;

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
		testCase.stream().forEach(o -> assertTrue(qt.remove(o, o[0], o[1])));
	}

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
		IntStream.range(0, SUB_INS).forEach(v -> {
			final double val = v / (double) SUB_INS;
			assertTrue(qt.move(v, val, val, val / 2, val / 2));
			assertTrue(qt.move(v, -val, val, -val / 2, val / 2));
			assertTrue(qt.move(v, val, -val, val / 2, -val / 2));
			assertTrue(qt.move(v, -val, -val, -val / 2, -val / 2));
		});
		IntStream.range(0, SUB_INS).forEach(v -> {
			final double val = v / (double) SUB_INS / 2;
			assertTrue(qt.remove(v, val, val));
			assertTrue(qt.remove(v, -val, val));
			assertTrue(qt.remove(v, val, -val));
			assertTrue(qt.remove(v, -val, -val));
		});
	}

}
