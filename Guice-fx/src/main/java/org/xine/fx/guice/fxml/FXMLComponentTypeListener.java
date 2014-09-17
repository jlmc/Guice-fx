package org.xine.fx.guice.fxml;

import javax.inject.Inject;

import org.xine.fx.guice.FXMLComponent;
import org.xine.fx.guice.GuiceFXMLLoader;

import com.google.inject.TypeLiteral;
import com.google.inject.spi.TypeEncounter;
import com.google.inject.spi.TypeListener;

/**
 * The listener interface for receiving FXMLComponentType events.
 * The class that is interested in processing a FXMLComponentType
 * event implements this interface, and the object created
 * with that class is registered with a component using the
 * component's <code>addFXMLComponentTypeListener<code> method. When
 * the FXMLComponentType event occurs, that object's appropriate
 * method is invoked.
 * @see FXMLComponentTypeEvent
 */
final class FXMLComponentTypeListener implements TypeListener {

    /** The fxml loader. */
    @Inject
    private GuiceFXMLLoader fxmlLoader;

    /**
     * Instantiates a new FXML component type listener.
     */
    FXMLComponentTypeListener() {
        super();
    }

    /*
     * (non-Javadoc)
     * @see com.google.inject.spi.TypeListener#hear(com.google.inject.TypeLiteral, com.google.inject.spi.TypeEncounter)
     */
    @Override
    public <T> void hear(final TypeLiteral<T> typeLiteral, final TypeEncounter<T> typeEncounter) {
        final Class<? super T> rawType = typeLiteral.getRawType();
        if (rawType.isAnnotationPresent(FXMLComponent.class)) {
            final FXMLComponent annotation = rawType.getAnnotation(FXMLComponent.class);
            final FXMLComponentMembersInjector<T> membersInjector = new FXMLComponentMembersInjector<>(this.fxmlLoader, annotation);
            typeEncounter.register(membersInjector);
        }
    }

}
