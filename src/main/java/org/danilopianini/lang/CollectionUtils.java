/*
 * Copyright (C) 2010-2014, Danilo Pianini and contributors
 * listed in the project's pom.xml file.
 * 
 * This file is part of Alchemist, and is distributed under the terms of
 * the GNU General Public License, with a linking exception, as described
 * in the file LICENSE in the Alchemist distribution's top directory.
 */
package org.danilopianini.lang;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java8.util.function.BiConsumer;
import java8.util.function.BiFunction;

/**
 */
public final class CollectionUtils {

    private CollectionUtils() {
    };

    /**
     * Computes the size of a map (with 0.75 as load factor) such as the map
     * does not need to resize itself if n elements are added.
     * 
     * @param n
     *            the number of elements
     * @return the size of the map
     */
    public static int optimalSizeMap(final int n) {
        return n * 3 / 2 + 1;
    }

    /**
     * Computes the size of a map (with 0.75 as load factor) such as the map
     * does not need to resize itself if all the elements of c are passed.
     * 
     * @param c
     *            the collection containing the elements
     * @return the size of the map
     */
    public static int optimalSizeMap(final Collection<?> c) {
        return optimalSizeMap(c.size());
    }

    /**
     * Facility for executing lambdas requiring access to the index. The passed
     * function will run once per element of the list. Avoid side effects on the
     * list, they won't work correctly.
     * 
     * @param <E>
     *            {@link List} data type
     * @param s
     *            the {@link List} to apply the lambda on
     * @param f
     *            the {@link BiConsumer} to apply to each element
     */
    public static <E> void forEach(final List<E> s, final BiConsumer<? super Integer, ? super E> f) {
        for (int i = 0; i < s.size(); i++) {
            f.accept(i, s.get(i));
        }
    }

    /**
     * Facility for executing lambdas requiring access to the index. The passed
     * function will run once per element of the list and its result will be
     * added in the corresponding position of the returned {@link List}. Avoid
     * side effects on the list, they won't work correctly.
     * 
     * @param <E>
     *            input {@link List} data type
     * @param <R>
     *            return {@link List} data type
     * @param s
     *            the {@link List} to apply the lambda on
     * @param f
     *            the {@link BiFunction} to apply to each element
     * 
     * @return a new list containing all the element produced by the
     *         {@link BiFunction} applied to the give {@link List}
     */
    public static <E, R> List<R> map(final List<E> s, final BiFunction<? super Integer, ? super E, ? extends R> f) {
        final List<R> result = new ArrayList<>(s.size());
        for (int i = 0; i < s.size(); i++) {
            result.add(f.apply(i, s.get(i)));
        }
        return result;
    }

}
