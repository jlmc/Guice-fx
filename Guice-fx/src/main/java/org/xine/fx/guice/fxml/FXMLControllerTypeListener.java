package org.xine.fx.guice.fxml;

import org.xine.fx.guice.FXMLController;

import com.google.inject.TypeLiteral;
import com.google.inject.spi.TypeEncounter;
import com.google.inject.spi.TypeListener;

import java.lang.reflect.Field;

import javax.inject.Inject;

/**
 * The listener interface for receiving FXMLControllerType events.
 * The class that is interested in processing a FXMLControllerType
 * event implements this interface, and the object created
 * with that class is registered with a component using the
 * component's <code>addFXMLControllerTypeListener<code> method. When
 * the FXMLControllerType event occurs, that object's appropriate
 * method is invoked.
 * @see FXMLControllerTypeEvent
 */
final class FXMLControllerTypeListener implements TypeListener {

    /** The fxml loading scope. */
    @Inject
    private FXMLLoadingScope fxmlLoadingScope;

    /**
     * Instantiates a new FXML controller type listener.
     */
    FXMLControllerTypeListener() {
        super();
    }

    /*
     * (non-Javadoc)
     * @see com.google.inject.spi.TypeListener#hear(com.google.inject.TypeLiteral, com.google.inject.spi.TypeEncounter)
     */
    @Override
    public <T> void hear(final TypeLiteral<T> typeLiteral, final TypeEncounter<T> typeEncounter) {
        for (final Field field : typeLiteral.getRawType().getDeclaredFields()) {
            if (field.isAnnotationPresent(FXMLController.class)) {
                final FXMLController annotation = field.getAnnotation(FXMLController.class);
                typeEncounter.register(new FXMLControllerMembersInjector<T>(field, annotation,
                        this.fxmlLoadingScope));
            }
        }
    }

}
