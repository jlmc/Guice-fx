package org.xine.fx.guice.fxml;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javafx.util.Builder;
import javafx.util.StringConverter;
import javafx.util.converter.BigDecimalStringConverter;
import javafx.util.converter.BigIntegerStringConverter;
import javafx.util.converter.BooleanStringConverter;
import javafx.util.converter.ByteStringConverter;
import javafx.util.converter.DefaultStringConverter;
import javafx.util.converter.DoubleStringConverter;
import javafx.util.converter.FloatStringConverter;
import javafx.util.converter.IntegerStringConverter;
import javafx.util.converter.LongStringConverter;
import javafx.util.converter.ShortStringConverter;

import com.google.inject.Injector;

/**
 * The Class FXMLComponentBuilder.
 * @param <T>
 *            the generic type
 */
final class FXMLComponentBuilder<T> extends AbstractMap<String, Object> implements Builder<T> {

    // FIXME The whole logic that finds appropriate setter methods and look ups the
    // StringConverters in a Map seems a duplicate of what has already been implemented
    // in the JavaFX runtime. Unfortunately I couldn't think of any better
    // solution... :-(

    /** The Constant STRING_CONVERTERS. */
    private static final Map<Class<?>, Class<? extends StringConverter<?>>> STRING_CONVERTERS;
    static {

        STRING_CONVERTERS = new HashMap<>();

        // String
        STRING_CONVERTERS.put(String.class, DefaultStringConverter.class);

        // Primitives
        STRING_CONVERTERS.put(boolean.class, BooleanStringConverter.class);
        STRING_CONVERTERS.put(byte.class, ByteStringConverter.class);
        STRING_CONVERTERS.put(double.class, DoubleStringConverter.class);
        STRING_CONVERTERS.put(float.class, FloatStringConverter.class);
        STRING_CONVERTERS.put(int.class, IntegerStringConverter.class);
        STRING_CONVERTERS.put(long.class, LongStringConverter.class);
        STRING_CONVERTERS.put(short.class, ShortStringConverter.class);

        // Primitive wrappers
        STRING_CONVERTERS.put(Boolean.class, BooleanStringConverter.class);
        STRING_CONVERTERS.put(Byte.class, ByteStringConverter.class);
        STRING_CONVERTERS.put(Double.class, DoubleStringConverter.class);
        STRING_CONVERTERS.put(Float.class, FloatStringConverter.class);
        STRING_CONVERTERS.put(Integer.class, IntegerStringConverter.class);
        STRING_CONVERTERS.put(Long.class, LongStringConverter.class);
        STRING_CONVERTERS.put(Short.class, ShortStringConverter.class);

        // Other types
        STRING_CONVERTERS.put(BigDecimal.class, BigDecimalStringConverter.class);
        STRING_CONVERTERS.put(BigInteger.class, BigIntegerStringConverter.class);
    }

    /** The injector. */
    private final Injector injector;

    /** The component class. */
    private final Class<T> componentClass;

    /** The component properties. */
    private final Map<String, Object> componentProperties = new HashMap<>();

    /**
     * Instantiates a new FXML component builder.
     * @param injector
     *            the injector
     * @param componentClass
     *            the component class
     */
    FXMLComponentBuilder(final Injector injector, final Class<T> componentClass) {
        super();
        this.injector = injector;
        this.componentClass = componentClass;
    }

    /*
     * (non-Javadoc)
     * @see javafx.util.Builder#build()
     */
    @Override
    public T build() {
        final T component = this.injector.getInstance(this.componentClass);
        for (final String key : this.componentProperties.keySet()) {
            final Object value = this.componentProperties.get(key);
            final String setterName = String.format("set%s%s", key.substring(0, 1).toUpperCase(), key.substring(1));
            try {
                Method setterMethod = null;
                for (final Method method : this.componentClass.getMethods()) {
                    if (method.getName().equals(setterName)) {
                        setterMethod = method;
                        break;
                    }
                }
                if (setterMethod == null) {
                    throw new IllegalStateException(String.format("No setter for field '%s' could be found.", key));
                }
                final StringConverter<?> stringConverter = getStringConverter(setterMethod.getParameterTypes()[0]);
                setterMethod.invoke(component, stringConverter.fromString((String) value));
            } catch (SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | InstantiationException e) {
                throw new RuntimeException(e);
            }
        }
        return component;
    }

    /**
     * Gets the string converter.
     * @param valueClass
     *            the value class
     * @return the string converter
     * @throws InstantiationException
     *             the instantiation exception
     * @throws IllegalAccessException
     *             the illegal access exception
     */
    @SuppressWarnings("static-method")
    private StringConverter<?> getStringConverter(final Class<?> valueClass) throws InstantiationException, IllegalAccessException {
        if (STRING_CONVERTERS.containsKey(valueClass)) {
            return STRING_CONVERTERS.get(valueClass).newInstance();
        }
        throw new IllegalArgumentException(String.format("Can't find StringConverter for class '%s'.", valueClass.getName()));
    }

    /*
     * (non-Javadoc)
     * @see java.util.AbstractMap#put(java.lang.Object, java.lang.Object)
     */
    @Override
    public Object put(final String key, final Object value) {
        this.componentProperties.put(key, value);
        return null;
    }

    /*
     * (non-Javadoc)
     * @see java.util.AbstractMap#entrySet()
     */
    @Override
    public Set<Map.Entry<String, Object>> entrySet() {
        throw new UnsupportedOperationException();
    }

}
