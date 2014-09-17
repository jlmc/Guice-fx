package org.xine.fx.guice.fxml;

import java.lang.reflect.Field;

import org.xine.fx.guice.FXMLController;
import org.xine.fx.guice.controllerlookup.ControllerLookup;

import com.google.inject.MembersInjector;

/**
 * The Class FXMLControllerMembersInjector.
 * @param <T>
 *            the generic type
 */
final class FXMLControllerMembersInjector<T> implements MembersInjector<T> {

    /** The field. */
    private final Field field;

    /** The annotation. */
    private final FXMLController annotation;

    /** The scope. */
    private final FXMLLoadingScope scope;

    /**
     * Instantiates a new FXML controller members injector.
     * @param field
     *            the field
     * @param annotation
     *            the annotation
     * @param scope
     *            the scope
     */
    FXMLControllerMembersInjector(final Field field, final FXMLController annotation, final FXMLLoadingScope scope) {
        super();
        this.field = field;
        this.annotation = annotation;
        this.scope = scope;
        field.setAccessible(true);
    }

    /*
     * (non-Javadoc)
     * @see com.google.inject.MembersInjector#injectMembers(java.lang.Object)
     */
    @Override
    public void injectMembers(final T instance) {
        Object controllerInstance = null;
        if (!this.annotation.controllerId().isEmpty()) {
            controllerInstance = new ControllerLookup(this.scope.getIdentifiables()).lookup(this.annotation.controllerId());
        } else {
            controllerInstance = this.scope.getInstance(this.annotation.controllerId());
        }
        if (controllerInstance == null) {
            throw new IllegalStateException("No suitable controller instance found!");
        }
        try {
            this.field.set(instance, controllerInstance);
        } catch (final IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

}
