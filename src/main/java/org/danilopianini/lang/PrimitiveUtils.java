package org.danilopianini.lang;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

/**
 * @author Danilo Pianini
 *
 */
public final class PrimitiveUtils {
	
	private static final List<Pair<Class<?>[], Function<Number, ? extends Number>>> NUMBER_CASTER  = new LinkedList<>();
	
	static {
		NUMBER_CASTER.add(new Pair<>(new Class<?>[] {Byte.class,  Byte.TYPE}, Number::byteValue));
		NUMBER_CASTER.add(new Pair<>(new Class<?>[] {Short.class,  Short.TYPE}, Number::shortValue));
		NUMBER_CASTER.add(new Pair<>(new Class<?>[] {Integer.class,  Integer.TYPE}, Number::intValue));
		NUMBER_CASTER.add(new Pair<>(new Class<?>[] {Long.class,  Long.TYPE}, Number::longValue));
		NUMBER_CASTER.add(new Pair<>(new Class<?>[] {Float.class,  Float.TYPE}, Number::floatValue));
		NUMBER_CASTER.add(new Pair<>(new Class<?>[] {Double.class,  Double.TYPE}, Number::doubleValue));
	}

	/**
	 * @param clazz the class under test
	 * @return true if the class is a subclass is a number having primitive representation in Java
	 */
	public static boolean classIsNumber(final Class<?> clazz) {
		Objects.requireNonNull(clazz);
		return NUMBER_CASTER.stream().map(Pair::getFirst).anyMatch(array -> Arrays.stream(array).anyMatch(clazz::isAssignableFrom));
	}
	
	/**
	 * @param dest the desired number class
	 * @param arg the argument that may need to be cast
	 * @return a possibly cast version of the argument
	 */
	public static Number castIfNeeded(final Class<?> dest, final Number arg) {
		Objects.requireNonNull(dest);
		Objects.requireNonNull(arg);
		final Optional<Pair<Class<?>[], Function<Number, ? extends Number>>> fconv = NUMBER_CASTER.stream()
				.filter(pair -> Arrays.stream(pair.getFirst()).anyMatch(dest::equals))
				.findFirst();
		if (fconv.isPresent()) {
			return fconv.get().getSecond().apply(arg);
		}
		throw new IllegalArgumentException(arg + " can not be cast to " + dest);
	}

	private PrimitiveUtils() {
	}
	
}
