package canaryprism.commons.cursed;

import sun.misc.Unsafe;

import java.lang.reflect.AccessFlag;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * <p>A class that contains cursed methods that should never be used in production code.</p>
 * 
 * teehee
 */
public class CursedUtils {

    private static final Unsafe unsafe;
    static {
        try {
            var field = Unsafe.class.getDeclaredField("theUnsafe");
            field.setAccessible(true);
            unsafe = (Unsafe) field.get(null);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static final StackWalker STACK_WALKER;
    static {
        STACK_WALKER = StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE);
    }
    private static boolean processing = false;


    /**
     * <p>Gets the class of the object.</p>
     * 
     * @param <T> the type of the object
     * @param object the object to get the class of
     * @return the class of the object as a subclass of its stored type
     */
    @SuppressWarnings("unchecked")
    public static <T> Class<? extends T> getClass(T object) {
        return (Class<? extends T>) object.getClass();
    }

    /**
     * <h2>ALWAYS, LET Optional<T> BE THE RETURN TYPE OF THE FUNCTION!!!!!!</h2>
     * 
     * <p>Use this template for your function:</p>
     * <pre>
     * public/private static Optional<String> fun() {
     *     Optional<String> result = catchExceptions();
     *     if (result != null) return result;
     *     // (your code goes here)
     *     return Optional.of(stringIGotSomewhere);
     * }
     * </pre>
     * 
     * @param <T> the type that matches the Optional's type in the return type of
     *            the function
     * 
     * @return an optional containing the result of the calling function, or an
     *         empty optional if an exception occurred
     */
    @SuppressWarnings("unchecked")
    public static <T> Optional<T> catchExceptions() {
        if (processing)
            return null;
        return STACK_WALKER.walk((frames) -> {
            StackWalker.StackFrame frame;
            try {
                frame = (StackWalker.StackFrame) frames.toArray()[1];
            } catch (IndexOutOfBoundsException e) {
                throw new CursedException("What the fuck!?", e);
            }
            if (frame.getMethodType().parameterArray().length > 0)
                throw new CursedException("Calling method has too many parameters");
            Optional<Method> possibleMethod = getStackFrameMethod(frame);
            if (possibleMethod.isPresent()) {
                Method method = possibleMethod.get();
                if (method.accessFlags().contains(AccessFlag.STATIC)) {
                    processing = true;
                    Optional<T> result;
                    try {
                        method.setAccessible(true);
                        result = (Optional<T>) method.invoke(null);
                    } catch (InvocationTargetException e) {
                        if (e.getCause() instanceof Exception) {
                            result = Optional.empty();
                        } else {
                            throw new CursedException("You're fucked ", e.getCause());
                        }
                    } catch (ClassCastException e) {
                        throw new CursedException("You return the wrong thing pal");
                    } catch (IllegalAccessException impossible) {
                        throw new RuntimeException(impossible);
                    }
                    processing = false;
                    return result;
                } else {
                    throw new CursedException("Calling method needs to be static");
                }

            }
            return Optional.empty();
        });
    }


    /**
     * <h2>ALWAYS, LET Optional<T> BE THE RETURN TYPE OF THE FUNCTION!!!!!!</h2>
     * 
     * <p>
     * Use this template for your function:
     * </p>
     * 
     * <pre>
     * public/private static Optional<String> fun(int val) {
     *     Optional<String> result = catchExceptions(val);
     *     if (result != null) return result;
     *     (your code goes here)
     *     return Optional.of(stringIGotSomewhere);
     * }
     * </pre>
     * 
     * @param <T> the type that matches the Optional's type in the return type of
     *            the function
     * 
     * @param params the parameters passed to the calling function
     * 
     * @return an optional containing the result of the calling function, or an
     *         empty optional if an exception occurred
     */
    @SuppressWarnings("unchecked")
    public static <T> Optional<T> catchExceptionsWithParams(Object... params) {
        if (processing)
            return null;
        return STACK_WALKER.walk((frames) -> {
            StackWalker.StackFrame frame;
            try {
                frame = (StackWalker.StackFrame) frames.toArray()[1];
            } catch (IndexOutOfBoundsException e) {
                throw new CursedException("What the fuck!?", e);
            }
            if (frame.getMethodType().parameterArray().length != params.length)
                throw new CursedException("Calling method has different number of parameters");
            Optional<Method> possibleMethod = getStackFrameMethod(frame);
            if (possibleMethod.isPresent()) {
                Method method = possibleMethod.get();
                if (method.accessFlags().contains(AccessFlag.STATIC)) {
                    processing = true;
                    Optional<T> result;
                    try {
                        method.setAccessible(true);
                        result = (Optional<T>) method.invoke(null, params);
                    } catch (InvocationTargetException e) {
                        if (e.getCause() instanceof Exception) {
                            result = Optional.empty();
                        } else {
                            throw new CursedException("You're fucked ", e.getCause());
                        }
                    } catch (IllegalArgumentException e) {
                        throw new CursedException("Parameters of wrong type were sent");
                    } catch (ClassCastException e) {
                        throw new CursedException("You return the wrong thing pal");
                    } catch (IllegalAccessException impossible) {
                        throw new RuntimeException(impossible);
                    }
                    processing = false;
                    return result;
                } else {
                    throw new CursedException("Calling method needs to be static");
                }

            }
            return Optional.empty();
        });
    }

    private static Optional<Method> getStackFrameMethod(StackWalker.StackFrame frame) {
        try {
            return Optional.of(
                    frame.getDeclaringClass().getDeclaredMethod(
                            frame.getMethodName(),
                            frame.getMethodType().parameterArray()));
        } catch (NoSuchMethodException e) {
            return Optional.empty();
        }
    }


    static final Set<Class<?>> IMMUTABLES = Set.of(
            Integer.class, Long.class, Double.class, Float.class, Byte.class, Short.class, Character.class,
            Boolean.class, String.class, BigInteger.class, BigDecimal.class);

    /**
     * <p>Deep clones an object.</p>
     * 
     * <p>
     * This function uses a mix of Reflection and {@link sun.misc.Unsafe} to allocate and copy over 
     * every single field of the object passed recursively. it can handle circular references
     * </p>
     * 
     * <p>
     * Strings, autoboxed primitives (Reflection automatically autoboxes them), 
     * and other known immutable objects are not cloned, and are returned as is.
     * </p>
     * 
     * @param <T> the type of the object
     * @param object the object to clone
     * @return a deep clone of the object
     */
    public static <T> T unsafeUniversalDeepClone(T object) {
        if (object == null) {
            return null;
        }
        return unsafeUniversalDeepClone(object, new IdentityHashMap<>());
    }

    @SuppressWarnings({ "unchecked", "deprecation" })
    private static <T> T unsafeUniversalDeepClone(T object, Map<Object, Object> map) {

        var type = object.getClass();

        if (IMMUTABLES.contains(type) || type.isEnum()) {
            return object;
        }

        if (type.isArray()) {
            var clone = Array.newInstance(type.getComponentType(), Array.getLength(object));
            map.put(object, clone);
            for (int i = 0; i < Array.getLength(object); i++) {
                var value = Array.get(object, i);
                if (value == null) {
                    Array.set(clone, i, null);
                } else {
                    if (map.containsKey(value)) {
                        Array.set(clone, i, map.get(value));
                    } else {
                        map.put(value, unsafeUniversalDeepClone(value, map));
                        Array.set(clone, i, map.get(value));
                    }
                }
            }

            return (T) clone;
        }

        try {
            var clone = unsafe.allocateInstance(type);

            var fields = getAllInstanceFields(type);

            for (var field : fields) {

                var offset = unsafe.objectFieldOffset(field);

                if (field.getType().isPrimitive()) {
                    unsafe.putObject(clone, offset, unsafe.getObject(object, offset));
                } else {
                    var value = unsafe.getObject(object, offset);
                    if (value == null) {
                        unsafe.putObject(clone, offset, null);
                    } else {
                        if (!map.containsKey(value)) {
                            map.put(value, unsafeUniversalDeepClone(value, map));
                        }
                        unsafe.putObject(clone, offset, map.get(value));
                    }
                }
            }

            return (T) clone;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    static List<Field> getAllInstanceFields(Class<?> type) {
        var fields = new ArrayList<Field>();
        while (type != null) {
            for (var field : type.getDeclaredFields()) {
                if (Modifier.isStatic(field.getModifiers())) {
                    continue;
                }
                fields.add(field);
            }
            type = type.getSuperclass();
        }
        return fields;
    }
}
