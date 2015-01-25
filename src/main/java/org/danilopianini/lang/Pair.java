/*
 * Copyright (C) 2010-2014, Danilo Pianini and contributors
 * listed in the project's pom.xml file.
 * 
 * This file is part of Alchemist, and is distributed under the terms of
 * the GNU General Public License, with a linking exception, as described
 * in the file LICENSE in the Alchemist distribution's top directory.
 */
package org.danilopianini.lang;

import java.io.Serializable;

/**
 * A simple class to model a couple of elements.
 * 
 * @author Danilo Pianini
 * 
 * @param <A>
 * @param <B>
 */
public class Pair<A, B> implements Serializable {

	private static final long serialVersionUID = 2168794148033303583L;
	private int hash;
	private final A o1;
	private final B o2;

	/**
	 * @param first
	 *            the first element
	 * @param second
	 *            the second element
	 */
	public Pair(final A first, final B second) {
		o1 = first;
		o2 = second;
	}

	@Override
	public boolean equals(final Object o) {
		if (o instanceof Pair) {
			final Pair<?, ?> c = (Pair<?, ?>) o;
			return o1.equals(c.o1) && o2.equals(c.o2);
		}
		return false;
	}

	/**
	 * @return the first element
	 */
	public A getFirst() {
		return o1;
	}

	/**
	 * @return the second element
	 */
	public B getSecond() {
		return o2;
	}

	@Override
	public int hashCode() {
		if (hash == 0) {
			hash = HashUtils.djb2int32(o1.hashCode(), o2.hashCode());
		}
		return hash;
	}
	
	@Override
	public String toString() {
		return "<" + o1 + ", " + o2 + ">"; 
	}

}
