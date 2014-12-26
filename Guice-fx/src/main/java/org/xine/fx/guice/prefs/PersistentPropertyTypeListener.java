package org.xine.fx.guice.prefs;

import org.xine.fx.guice.PersistentProperty;

import com.google.inject.TypeLiteral;
import com.google.inject.spi.TypeEncounter;
import com.google.inject.spi.TypeListener;

import java.lang.reflect.Field;

/**
 * The listener interface for receiving persistentPropertyType events.
 * The class that is interested in processing a persistentPropertyType
 * event implements this interface, and the object created
 * with that class is registered with a component using the
 * component's <code>addPersistentPropertyTypeListener<code> method. When
 * the persistentPropertyType event occurs, that object's appropriate
 * method is invoked.
 * @see PersistentPropertyTypeEvent
 */
class PersistentPropertyTypeListener implements TypeListener {

    /**
     * Instantiates a new persistent property type listener.
     */
    PersistentPropertyTypeListener() {
        super();
    }

    /*
     * (non-Javadoc)
     * @see com.google.inject.spi.TypeListener#hear(com.google.inject.TypeLiteral,
     * com.google.inject.spi.TypeEncounter)
     */
    @Override
    public <T> void hear(final TypeLiteral<T> typeLiteral, final TypeEncounter<T> typeEncounter) {
        for (final Field field : typeLiteral.getRawType().getDeclaredFields()) {
            if (field.isAnnotationPresent(PersistentProperty.class)) {
                final PersistentProperty annotation = field.getAnnotation(PersistentProperty.class);
                typeEncounter.register(new PersistentPropertyMembersInjector<T>(field, annotation));
            }
        }
    }

}
