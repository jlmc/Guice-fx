package org.xine.fx.guice.fxml;

import java.lang.reflect.Field;

import javax.inject.Inject;

import org.xine.fx.guice.FXMLController;

import com.google.inject.TypeLiteral;
import com.google.inject.spi.TypeEncounter;
import com.google.inject.spi.TypeListener;

final class FXMLControllerTypeListener implements TypeListener {

    @Inject
    private FXMLLoadingScope fxmlLoadingScope;

    FXMLControllerTypeListener() {
        super();
    }

    @Override
    public <T> void hear(final TypeLiteral<T> typeLiteral, final TypeEncounter<T> typeEncounter) {
        for (final Field field : typeLiteral.getRawType().getDeclaredFields()) {
            if (field.isAnnotationPresent(FXMLController.class)) {
                final FXMLController annotation = field.getAnnotation(FXMLController.class);
                typeEncounter.register(new FXMLControllerMembersInjector<T>(field, annotation, this.fxmlLoadingScope));
            }
        }
    }

}
