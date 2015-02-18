package org.danilopianini.lang;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

/**
 * @author Danilo Pianini
 *
 */
public final class PrimitiveUtils {
	
	private static final Map<Class<?>, Function<Number, ? extends Number>> NUMBER_CASTER  = new LinkedHashMap<>();
	
	static {
		NUMBER_CASTER.put(Byte.class, Number::byteValue);
		NUMBER_CASTER.put(Byte.TYPE, Number::byteValue);
		NUMBER_CASTER.put(Short.class, Number::shortValue);
		NUMBER_CASTER.put(Short.TYPE, Number::shortValue);
		NUMBER_CASTER.put(Integer.class, Number::intValue);
		NUMBER_CASTER.put(Integer.TYPE, Number::intValue);
		NUMBER_CASTER.put(Long.class, Number::longValue);
		NUMBER_CASTER.put(Long.TYPE, Number::longValue);
		NUMBER_CASTER.put(Float.class, Number::floatValue);
		NUMBER_CASTER.put(Float.TYPE, Number::floatValue);
		NUMBER_CASTER.put(Double.class, Number::doubleValue);
		NUMBER_CASTER.put(Double.TYPE, Number::doubleValue);
	}

	/**
	 * @param clazz the class under test
	 * @return true if the class is a subclass is a number having primitive representation in Java
	 */
	public static boolean classIsNumber(final Class<?> clazz) {
		if (Number.class.isAssignableFrom(clazz)) {
			return true;
		}
		return NUMBER_CASTER.containsKey(clazz);
	}
	
	/**
	 * @param dest the desired number class
	 * @param arg the argument that may need to be cast
	 * @return a possibly cast version of the argument
	 */
	public static Optional<Number> castIfNeeded(final Class<?> dest, final Number arg) {
		Objects.requireNonNull(dest);
		Objects.requireNonNull(arg);
		if (dest.isAssignableFrom(arg.getClass())) {
			return Optional.of(arg);
		}
		final Function<Number, ? extends Number> cast = NUMBER_CASTER.get(dest);
		if (cast != null) {
			return Optional.of(cast.apply(arg));
		}
		return Optional.empty();
	}

	private PrimitiveUtils() {
	}
	
}
