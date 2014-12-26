package org.xine.fx.guice.prefs;

import static java.util.prefs.Preferences.systemNodeForPackage;
import static java.util.prefs.Preferences.userNodeForPackage;

import org.xine.fx.guice.PersistentProperty;
import org.xine.fx.guice.PersistentProperty.NodeType;

import com.google.inject.MembersInjector;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.prefs.Preferences;

import javafx.beans.property.Property;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.beans.value.WritableBooleanValue;
import javafx.beans.value.WritableDoubleValue;
import javafx.beans.value.WritableFloatValue;
import javafx.beans.value.WritableIntegerValue;
import javafx.beans.value.WritableLongValue;
import javafx.beans.value.WritableStringValue;
import javafx.beans.value.WritableValue;

/**
 * The Class PersistentPropertyMembersInjector.
 * @param <T>
 *            the generic type
 */

final class PersistentPropertyMembersInjector<T> implements MembersInjector<T> {

    /** The field. */
    private final Field field;

    /** The annotation. */
    private final PersistentProperty annotation;

    /**
     * Instantiates a new persistent property members injector.
     * @param field
     *            the field
     * @param annotation
     *            the annotation
     */
    PersistentPropertyMembersInjector(final Field field, final PersistentProperty annotation) {
        super();
        this.field = field;
        this.annotation = annotation;
        field.setAccessible(true);
    }

    /*
     * (non-Javadoc)
     * @see com.google.inject.MembersInjector#injectMembers(java.lang.Object)
     */
    @Override
    public void injectMembers(final T instance) {

        final NodeType nodeType = this.annotation.type();
        final Class<?> nodeClass = this.annotation.clazz();

        final Preferences prefs;
        switch (nodeType) {
        case SYSTEM_NODE:
            prefs = systemNodeForPackage(nodeClass);
            break;
        case USER_NODE:
            prefs = userNodeForPackage(nodeClass);
            break;
        default:
            throw new IllegalStateException(String.format("Unknown Preferences node type: %s!",
                    nodeType));
        }

        // Only set the initial value of the property during injection if the
        // field that has been stored in the preferences backend is not null.
        final String initialValue = prefs.get(this.annotation.key(), null);
        if (initialValue != null && !initialValue.isEmpty()) {
            updatePropertyField(instance, initialValue);
        }

        try {
            ((Property<?>) this.field.get(instance)).addListener(new ChangeListener<Object>() {
                @SuppressWarnings("synthetic-access")
                @Override
                public void changed(final ObservableValue<?> prop, final Object oldValue,
                        final Object newValue) {
                    @SuppressWarnings("unused")
                    final String curVal = prefs.get(
                            PersistentPropertyMembersInjector.this.annotation.key(), null);
                    prefs.put(PersistentPropertyMembersInjector.this.annotation.key(),
                            String.valueOf(newValue));
                }
            });
        } catch (IllegalArgumentException | IllegalAccessException e) {
            // TODO Use a more meaningful exception
            throw new RuntimeException(e);
        }

    }

    /**
     * Update property field.
     * @param instance
     *            the instance
     * @param newValString
     *            the new val string
     */
    @SuppressWarnings("unused")
    private void updatePropertyField(final T instance, final String newValString) {
        @SuppressWarnings("unchecked")
        final Class<? extends Property<?>> fieldType = (Class<? extends Property<?>>) this.field
                .getType();
        try {

            final Object fieldInstance = this.field.get(instance);
            final Method getter = WritableValue.class.getMethod("getValue");
            final Method setter = WritableValue.class.getMethod("setValue", Object.class);

            final Object curVal = getter.invoke(fieldInstance);
            final Object newVal;

            if (WritableStringValue.class.isAssignableFrom(fieldType)) {
                newVal = newValString;
            } else if (WritableBooleanValue.class.isAssignableFrom(fieldType)) {
                newVal = Boolean.valueOf(newValString);
            } else if (WritableIntegerValue.class.isAssignableFrom(fieldType)) {
                newVal = Integer.valueOf(newValString);
            } else if (WritableLongValue.class.isAssignableFrom(fieldType)) {
                newVal = Long.valueOf(newValString);
            } else if (WritableDoubleValue.class.isAssignableFrom(fieldType)) {
                newVal = Double.valueOf(newValString);
            } else if (WritableFloatValue.class.isAssignableFrom(fieldType)) {
                newVal = Float.valueOf(newValString);
            } else {
                throw new IllegalStateException("Cannot inject value into field.");
            }
            setter.invoke(fieldInstance, newVal);
        } catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException
                | NoSuchMethodException | SecurityException e) {
            // TODO Use a more meaningful exception
            throw new RuntimeException(e);
        }
    }
}
