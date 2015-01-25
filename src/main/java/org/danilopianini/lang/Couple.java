/*
 * Copyright (C) 2010-2014, Danilo Pianini and contributors
 * listed in the project's pom.xml file.
 * 
 * This file is part of Alchemist, and is distributed under the terms of
 * the GNU General Public License, with a linking exception, as described
 * in the file LICENSE in the Alchemist distribution's top directory.
 */
package org.danilopianini.lang;

/**
 * A simple class to model a couple of elements.
 * 
 * @author Danilo Pianini
 * 
 * @param <T>
 */
public class Couple<T> extends Pair<T, T> {

	private static final long serialVersionUID = 2168794148033303583L;

	/**
	 * @param first the first element
	 * @param second the second element
	 */
	public Couple(final T first, final T second) {
		super(first, second);
	}

}
