package org.danilopianini.lang;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

import com.google.common.collect.Lists;

/**
 * @author Danilo Pianini
 *
 */
@SuppressWarnings("unchecked")
public final class PrimitiveUtils {
	
	private static final Map<Class<?>, Function<Number, ? extends Number>> NUMBER_CASTER;
	private static final List<Class<? extends Number>> WRAPPER_LIST;
	private static final List<Class<? extends Number>> PRIMITIVE_LIST;
	
	static {
		WRAPPER_LIST = Collections.unmodifiableList(Lists.newArrayList(
				Byte.class,
				Short.class,
				Integer.class,
				Long.class,
				Float.class,
				Double.class));
		PRIMITIVE_LIST = Collections.unmodifiableList(Lists.newArrayList(
				byte.class,
				short.class, //NOPMD
				int.class,
				long.class,
				float.class,
				double.class));
		final LinkedHashMap<Class<?>, Function<Number, ? extends Number>> map = new LinkedHashMap<>();
		map.put(Byte.class, Number::byteValue);
		map.put(Byte.TYPE, Number::byteValue);
		map.put(Short.class, Number::shortValue);
		map.put(Short.TYPE, Number::shortValue);
		map.put(Integer.class, Number::intValue);
		map.put(Integer.TYPE, Number::intValue);
		map.put(Long.class, Number::longValue);
		map.put(Long.TYPE, Number::longValue);
		map.put(Float.class, Number::floatValue);
		map.put(Float.TYPE, Number::floatValue);
		map.put(Double.class, Number::doubleValue);
		map.put(Double.TYPE, Number::doubleValue);
		NUMBER_CASTER = Collections.unmodifiableMap(map);
	}

	/**
	 * @param clazz the class under test
	 * @return true if the class is a subclass is a number having primitive representation in Java
	 */
	public static boolean classIsNumber(final Class<?> clazz) {
		return Number.class.isAssignableFrom(clazz) || PRIMITIVE_LIST.contains(clazz);
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
	
	/**
	 * Given a {@link Number}, tries to guess at which wrapper-class it may
	 * belong.
	 * 
	 * @param n
	 *            the {@link Number}
	 * @return The wrapper-class this {@link Number} actually belongs to, or
	 *         Double.class if it is no wrapper
	 */
	public static Class<? extends Number> toPrimitiveWrapper(final Number n) {
		return WRAPPER_LIST.stream()
			.filter(c -> c.isInstance(n))
			.findFirst()
			.orElse(Double.class);
	}

	private PrimitiveUtils() {
	}
	
}
