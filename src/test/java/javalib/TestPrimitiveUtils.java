package javalib;

import java.math.BigDecimal;
import java.math.BigInteger;

import org.junit.Test;

import static org.danilopianini.lang.PrimitiveUtils.classIsNumber;
import static org.danilopianini.lang.PrimitiveUtils.castIfNeeded;
import static org.danilopianini.lang.PrimitiveUtils.toPrimitiveWrapper;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

/**
 * @author Danilo Pianini
 *
 */
public class TestPrimitiveUtils {

	/**
	 * 
	 */
	@Test
	public void testIsNumber() {
		assertTrue(classIsNumber(Integer.class));
		assertTrue(classIsNumber(Byte.class));
		assertTrue(classIsNumber(Short.class));
		assertTrue(classIsNumber(Double.class));
		assertTrue(classIsNumber(Long.class));
		assertTrue(classIsNumber(Float.class));
		assertTrue(classIsNumber(BigInteger.class));
		assertFalse(classIsNumber(Character.class));
		assertTrue(classIsNumber(Integer.TYPE));
		assertTrue(classIsNumber(Byte.TYPE));
		assertTrue(classIsNumber(Short.TYPE));
		assertTrue(classIsNumber(Double.TYPE));
		assertTrue(classIsNumber(Long.TYPE));
		assertTrue(classIsNumber(Float.TYPE));
	}
	
	/**
	 * 
	 */
	@Test
	public void testCastIfNeeded() {
		assertEquals(1, castIfNeeded(Integer.class, 1.0d).get());
		assertNotEquals(1.0, castIfNeeded(Integer.class, 1.0d).get());
		assertEquals((byte) 1, castIfNeeded(Byte.class, 1.0d).get());
		assertEquals(1.0, castIfNeeded(Double.class, 1).get());
		assertEquals(1L, castIfNeeded(Long.class, 1).get());
		assertNotEquals(1L, castIfNeeded(Integer.class, 1L).get());
	}

	/**
	 * 
	 */
	@Test
	public void testToPrimitiveWrapper() {
		assertEquals(Byte.class, toPrimitiveWrapper((byte) 0));
		assertEquals(Short.class, toPrimitiveWrapper((short) 0)); //NOPMD
		assertEquals(Integer.class, toPrimitiveWrapper(0));
		assertEquals(Long.class, toPrimitiveWrapper(0L));
		assertEquals(Float.class, toPrimitiveWrapper(0f));
		assertEquals(Double.class, toPrimitiveWrapper(0d));
		assertEquals(Double.class, toPrimitiveWrapper(BigDecimal.ZERO));
	}

}
