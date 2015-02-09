package javalib;

import org.junit.Test;

import static org.danilopianini.lang.PrimitiveUtils.classIsNumber;
import static org.danilopianini.lang.PrimitiveUtils.castIfNeeded;
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
		assertEquals(1, castIfNeeded(Integer.class, 1.0d));
		assertNotEquals(1.0, castIfNeeded(Integer.class, 1.0d));
		assertEquals((byte) 1, castIfNeeded(Byte.class, 1.0d));
		assertEquals(1.0, castIfNeeded(Double.class, 1));
		assertEquals(1L, castIfNeeded(Long.class, 1));
		assertNotEquals(1L, castIfNeeded(Integer.class, 1L));
	}

}
