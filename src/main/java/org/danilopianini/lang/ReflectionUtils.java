package org.danilopianini.lang;

import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java8.util.Optional;
import java8.util.stream.IntStreams;
import java8.util.stream.StreamSupport;

import org.apache.commons.math3.util.Pair;

/**
 * Utilities that ease the use of Java Reflection.
 */
public final class ReflectionUtils {

    private ReflectionUtils() {
    }

    /**
     * Searches the best method in a class, given its name and arguments. Uses a
     * scoring system to deal with overloading.
     * 
     * @param clazz
     *            the class
     * @param methodName
     *            the method name
     * @param argClass
     *            the arguments for the method
     * @return the method that better fits the passed arguments
     */
    public static Method bestMethod(final Class<?> clazz, final String methodName, final Class<?>[] argClass) {
        Objects.requireNonNull(clazz, "The class on which the method will be invoked can not be null.");
        Objects.requireNonNull(methodName, "Method name can not be null.");
        Objects.requireNonNull(argClass, "Method arguments can not be null.");
        /*
         * If there is a matching method, return it
         */
        try {
            return clazz.getMethod(methodName, argClass);
        } catch (NoSuchMethodException | SecurityException e) {
            return bestExecutable((Method[]) Arrays.stream(clazz.getMethods())
                    .filter(m -> m.getName().equals(methodName))
                    .toArray(), argClass);
        }
    }

    /**
     * Searches the best method in a class, given its name and arguments. Uses a
     * scoring system to deal with overloading.
     * 
     * @param clazz
     *            the class
     * @param <T>
     *            the type
     * @param argClass
     *            the arguments for the method
     * @return the method that better fits the passed arguments
     */
    public static <T> Constructor<T> bestConstructor(final Class<T> clazz, final Class<?>[] argClass) {
        Objects.requireNonNull(clazz, "The class on which the method will be invoked can not be null.");
        Objects.requireNonNull(argClass, "Method arguments can not be null.");
        /*
         * If there is a matching method, return it
         */
        try {
            return clazz.getConstructor(argClass);
        } catch (NoSuchMethodException | SecurityException e) {
            /*
             * Deal with Java method overloading scoring methods
             */
            @SuppressWarnings("unchecked")
            final Constructor<T>[] candidates = (Constructor<T>[]) clazz.getConstructors();
            return bestExecutable(candidates, argClass);
        }
    }

    private static <T extends Executable> T bestExecutable(final T[] candidates, final Class<?>[] argClass) {
        final List<Pair<Integer, T>> lm = new ArrayList<>(candidates.length);
        for (final T m : candidates) {
            if (m.getParameterCount() == argClass.length) {
                final Class<?>[] params = m.getParameterTypes();
                int p = 0;
                boolean compatible = true;
                for (int i = 0; compatible && i < argClass.length; i++) {
                    final Class<?> expected = params[i];
                    if (expected.isAssignableFrom(argClass[i])) {
                        /*
                         * No downcast required, there is compatibility
                         */
                        p++;
                    } else if (!PrimitiveUtils.classIsNumber(expected)) {
                        compatible = false;
                    }
                }
                if (compatible) {
                    lm.add(new Pair<>(p, m));
                }
            }
        }
        /*
         * Find best
         */
        final Optional<T> best = StreamSupport.stream(lm)
                .max((pm1, pm2) -> pm1.getFirst().compareTo(pm2.getFirst()))
                .map(Pair::getSecond);
        if (best.isPresent()) {
            return best.get();
        }
        throw new IllegalArgumentException("No candidate in " + Arrays.toString(candidates) + " is ok for " + Arrays.toString(argClass));
    }

    /**
     * This method tries to invoke the passed method. In case of failure, it
     * tries to cast the arguments before failing (e.g. if you pass a Integer
     * value, but a int is expected).
     * 
     * @param method
     *            the methods to invoke
     * @param target
     *            the target object. It can be null, if the method which is
     *            being invoked is static
     * @param args
     *            the arguments for the method
     * @return the result of the invocation, or an {@link IllegalStateException}
     *         if something goes wrong.
     */
    public static Object invokeMethod(final Method method, final Object target, final Object[] args) {
        try {
            return method.invoke(target, args);
        } catch (IllegalAccessException | InvocationTargetException | RuntimeException e1) {
            /*
             * Failure: maybe some cast was required?
             */
            final Class<?>[] params = method.getParameterTypes();
            final Object[] actualArgs = IntStreams.range(0, args.length).parallel().mapToObj(i -> {
                final Class<?> expected = params[i];
                final Object actual = args[i];
                if (!expected.isAssignableFrom(actual.getClass()) && PrimitiveUtils.classIsNumber(expected)) {
                    return PrimitiveUtils.castIfNeeded(expected, (Number) actual).get();
                }
                return actual;
            }).toArray();
            try {
                return method.invoke(target, actualArgs);
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                throw new IllegalStateException(
                        "Cannot invoke " + method + " with arguments " + Arrays.toString(args) + " on " + target, e);
            }
        }
    }

}
