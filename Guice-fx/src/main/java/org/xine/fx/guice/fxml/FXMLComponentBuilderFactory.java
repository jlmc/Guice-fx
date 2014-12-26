package org.xine.fx.guice.fxml;

import org.xine.fx.guice.FXMLComponent;

import com.google.inject.Injector;

import java.util.logging.Logger;

import javafx.fxml.JavaFXBuilderFactory;
import javafx.util.Builder;
import javafx.util.BuilderFactory;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * A factory for creating FXMLComponentBuilder objects.
 */
@Singleton
public final class FXMLComponentBuilderFactory implements BuilderFactory {

    /** The injector. */
    @Inject
    private Injector injector;

    /** The Constant LOGGER. */
    private static final Logger LOGGER = Logger.getLogger(FXMLComponentBuilderFactory.class
            .getName());

    /** The default builder factory. */
    private final JavaFXBuilderFactory defaultBuilderFactory = new JavaFXBuilderFactory();

    /*
     * (non-Javadoc)
     * @see javafx.util.BuilderFactory#getBuilder(java.lang.Class)
     */
    @SuppressWarnings({"rawtypes", "unchecked" })
    @Override
    public Builder<?> getBuilder(final Class<?> componentClass) {
        final String className = componentClass.getName();
        LOGGER.fine(String.format("Searching builder for component class '%s'.", className));
        if (componentClass.isAnnotationPresent(FXMLComponent.class)) {
            LOGGER.fine(String.format("Creating FXMLComponentBuilder for class '%s'.", className));
            return new FXMLComponentBuilder(this.injector, componentClass);
        }
        // Fall back to the default builder factory if we are not dealing with a FXMLComponent class.
        return this.defaultBuilderFactory.getBuilder(componentClass);
    }

}
