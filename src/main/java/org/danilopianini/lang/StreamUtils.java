package org.danilopianini.lang;

import java8.util.function.Function;
import java8.util.stream.RefStreams;
import java8.util.stream.Stream;

/**
 * Complementary utilities for the Java 8 Stream library.
 */
public final class StreamUtils {

    private StreamUtils() {
    }

    /**
     * Flattens a recursive, non circular data structure into a {@link Stream}.
     * 
     * @param target
     *            the root element of the structure
     * @param extractor
     *            the function that maps a node of the structure to a stream of
     *            its children
     * @param <E> the type of object
     * @return a {@link Stream} walking through the data structure
     */
    public static <E> Stream<E> flatten(
            final E target,
            final Function<? super E, ? extends Stream<? extends E>> extractor) {
        return RefStreams.concat(RefStreams.of(target), extractor.apply(target).flatMap(el -> flatten(el, extractor)));
    }

}
