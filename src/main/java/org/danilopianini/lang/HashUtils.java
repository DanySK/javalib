/*
 * Copyright (C) 2010-2014, Danilo Pianini and contributors
 * listed in the project's pom.xml file.
 * 
 * This file is part of Alchemist, and is distributed under the terms of
 * the GNU General Public License, with a linking exception, as described
 * in the file LICENSE in the Alchemist distribution's top directory.
 */
package org.danilopianini.lang;

import static org.danilopianini.lang.Constants.DJB2_MAGIC;
import static org.danilopianini.lang.Constants.DJB2_SHIFT;

import java.io.Serializable;
import org.danilopianini.io.FileUtilities;

/**
 * @author Danilo Pianini
 * 
 */
public final class HashUtils {

	private static final int MASK_BYTE = 0xff;
	private static final int SHIFT3 = 24;
	private static final int SHIFT4 = 32;
	private static final int SHIFT5 = 40;
	private static final int SHIFT6 = 48;
	private static final int SHIFT7 = 56;
	
	/**
	 * @param bytes
	 *            bytes
	 * @return DJB2 32bit hash
	 */
	public static int djb2int32(final byte... bytes) {
		int hash32 = Constants.DJB2_START;
		for (final byte b : bytes) {
			hash32 = hash32 * DJB2_MAGIC ^ b;
		}
		return hash32;
	}

	/**
	 * @param bytes
	 *            bytes
	 * @return DJB2 32bit hash
	 */
	public static int djb2int32(final double... bytes) {
		return djb2int32(toByta(bytes));
	}

	/**
	 * @param bytes
	 *            bytes
	 * @return DJB2 32bit hash
	 */
	public static int djb2int32(final int... bytes) {
		return djb2int32(toByta(bytes));
	}

	/**
	 * @param bytes
	 *            bytes
	 * @return DJB2 32bit hash
	 */
	public static int djb2int32obj(final Serializable... bytes) {
		return djb2int32(FileUtilities.serializeObject(bytes));
	}

	/**
	 * @param bytes
	 *            bytes
	 * @return DJB2 64bit hash
	 */
	public static long djb2long64(final byte... bytes) {
		long hash = Constants.DJB2_START;
		for (final byte b : bytes) {
			hash = ((hash << DJB2_SHIFT) + hash) + b;
		}
		return hash;
	}

	/**
	 * @param bytes
	 *            bytes
	 * @return DJB2 64bit hash
	 */
	public static long djb2long64(final double... bytes) {
		return djb2long64(toByta(bytes));
	}

	/**
	 * @param bytes
	 *            bytes
	 * @return DJB2 64bit hash
	 */
	public static long djb2long64(final int... bytes) {
		return djb2long64(toByta(bytes));
	}

	/**
	 * @param bytes
	 *            bytes
	 * @return DJB2 64bit hash
	 */
	public static long djb2long64obj(final Serializable... bytes) {
		return djb2long64(FileUtilities.serializeObject(bytes));
	}

	/**
	 * @param data double
	 * @return byte[]
	 */
	public static byte[] toByta(final double data) {
		return toByta(Double.doubleToRawLongBits(data));
	}

	/**
	 * @param data double[]
	 * @return byte[]
	 */
	public static byte[] toByta(final double[] data) {
		if (data == null) {
			return null;
		}
		final byte[] byts = new byte[data.length * 8];
		for (int i = 0; i < data.length; i++) {
			System.arraycopy(toByta(data[i]), 0, byts, i * 8, 8);
		}
		return byts;
	}

	/**
	 * @param data float
	 * @return byte[]
	 */
	public static byte[] toByta(final float data) {
		return toByta(Float.floatToRawIntBits(data));
	}

	/**
	 * @param data float[]
	 * @return byte[]
	 */
	public static byte[] toByta(final float[] data) {
		if (data == null) {
			return null;
		}
		final byte[] byts = new byte[data.length * 4];
		for (int i = 0; i < data.length; i++) {
			System.arraycopy(toByta(data[i]), 0, byts, i * 4, 4);
		}
		return byts;
	}

	/**
	 * @param data int
	 * @return byte[]
	 */
	public static byte[] toByta(final int data) {
		return new byte[] { (byte) ((data >> SHIFT3) & MASK_BYTE), (byte) ((data >> 16) & MASK_BYTE), (byte) ((data >> 8) & MASK_BYTE), (byte) ((data >> 0) & MASK_BYTE), };
	}

	/**
	 * @param data int[]
	 * @return byte[]
	 */
	public static byte[] toByta(final int[] data) {
		if (data == null) {
			return null;
		}
		final byte[] byts = new byte[data.length * 4];
		for (int i = 0; i < data.length; i++) {
			System.arraycopy(toByta(data[i]), 0, byts, i * 4, 4);
		}
		return byts;
	}

	/**
	 * @param data long
	 * @return byte[]
	 */
	public static byte[] toByta(final long data) {
		return new byte[] { (byte) ((data >> SHIFT7) & MASK_BYTE), (byte) ((data >> SHIFT6) & MASK_BYTE), (byte) ((data >> SHIFT5) & MASK_BYTE), (byte) ((data >> SHIFT4) & MASK_BYTE), (byte) ((data >> SHIFT3) & MASK_BYTE), (byte) ((data >> 16) & MASK_BYTE), (byte) ((data >> 8) & MASK_BYTE), (byte) ((data >> 0) & MASK_BYTE), };
	}

	/**
	 * @param data long[]
	 * @return byte[]
	 */
	public static byte[] toByta(final long[] data) {
		if (data == null) {
			return null;
		}
		final byte[] byts = new byte[data.length * 8];
		for (int i = 0; i < data.length; i++) {
			System.arraycopy(toByta(data[i]), 0, byts, i * 8, 8);
		}
		return byts;
	}

	/**
	 * Runs System.identityHashCode(a) == System.identityHashCode(b). In most JVM implementations, this is actually a pointer comparison. Nevertheless, this is not guaranteed: handle with care
	 * 
	 * @param a an {@link Object}
	 * @param b another {@link Object}
	 * @return System.identityHashCode(a) == System.identityHashCode(b)
	 */
	public static boolean pointerEquals(final Object a, final Object b) {
		return System.identityHashCode(a) == System.identityHashCode(b);
	}
	
	/**
	 * 
	 */
	private HashUtils() {
	}

}
