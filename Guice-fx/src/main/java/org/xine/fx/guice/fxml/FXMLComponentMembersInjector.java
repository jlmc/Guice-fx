package org.xine.fx.guice.fxml;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ResourceBundle;
import java.util.logging.Logger;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;

import org.xine.fx.guice.FXMLComponent;
import org.xine.fx.guice.GuiceFXMLLoader;

import com.google.inject.MembersInjector;

final class FXMLComponentMembersInjector<T> implements MembersInjector<T> {

    @SuppressWarnings("unused")
    private final GuiceFXMLLoader fxmlLoader;

    /** Logger for this class. */
    private static final Logger LOGGER = Logger.getLogger(FXMLComponentMembersInjector.class.getName());

    private final FXMLComponent annotation;

    FXMLComponentMembersInjector(final GuiceFXMLLoader fxmlLoader, final FXMLComponent annotation) {
        super();
        this.fxmlLoader = fxmlLoader;
        this.annotation = annotation;
    }

    @Override
    public void injectMembers(final T instance) {

        String locationString = this.annotation.location();
        if (locationString.isEmpty()) {
            locationString = String.format("%s.fxml", instance.getClass().getSimpleName());
            LOGGER.fine(String.format("No location for FXML component has been set for class '%s'. Assuming default ('%s').", instance.getClass().getName(), locationString));
        }
        URL location;
        location = instance.getClass().getResource(locationString);
        if (location == null) {
            LOGGER.fine(String.format("Location '%s' cannot be found on the classpath. Trying to construct a new URL...", locationString));
            try {
                location = new URL(locationString);
            } catch (final MalformedURLException e) {
                throw new RuntimeException(String.format("Cannot construct URL from string '%s'.", locationString), e);
            }
        }
        @SuppressWarnings("hiding")
        final FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(location);
        final String resourcesString = this.annotation.resources();
        if (!resourcesString.isEmpty()) {
            fxmlLoader.setResources(ResourceBundle.getBundle(resourcesString));
        }
        fxmlLoader.setCharset(Charset.forName(this.annotation.charset()));
        fxmlLoader.setController(instance);
        fxmlLoader.setRoot(instance);

        // Invoke "fxmlLoader.setTemplate(true)" if we are using JavaFX 8.0 or
        // higher to improve performance on objects that are created multiple times.
        try {
            final Method setTemplateMethod = FXMLLoader.class.getMethod("setTemplate", boolean.class);
            setTemplateMethod.invoke(fxmlLoader, Boolean.TRUE);
        } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            // We simply ignore this exception. It means that we are using
            // a JavaFX runtime prior to JavaFX 8.0.
        }

        // Actual instantiation of the component has to happen on the JavaFX thread.
        // We simply delegate the loading.
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                try {
                    final Object loaded = fxmlLoader.load();
                    if (loaded != instance) {
                        throw new IllegalStateException("Loading of FXML component went terribly wrong! :-(");
                    }
                } catch (final IOException e) {
                    throw new RuntimeException(e);
                }

            }
        });

    }
}
