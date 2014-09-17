package org.xine.fx.guice.fxml;

import org.xine.fx.guice.FXMLComponent;
import org.xine.fx.guice.FXMLController;
import org.xine.fx.guice.GuiceFXMLLoader;
import org.xine.fx.guice.controllerlookup.ControllerLookup;

import com.google.inject.AbstractModule;
import com.google.inject.Provider;
import com.google.inject.matcher.Matchers;

/**
 * The Class FXMLLoadingModule.
 */
public final class FXMLLoadingModule extends AbstractModule {

    /**
     * Instantiates a new FXML loading module.
     */
    public FXMLLoadingModule() {
        super();
    }

    /*
     * (non-Javadoc)
     * @see com.google.inject.AbstractModule#configure()
     */
    @SuppressWarnings("synthetic-access")
    @Override
    protected void configure() {

        // FXMLLoadingScope
        final FXMLLoadingScope fxmlLoadingScope = new FXMLLoadingScope();
        bind(FXMLLoadingScope.class).toInstance(fxmlLoadingScope);
        bindScope(FXMLController.class, fxmlLoadingScope);
        bindScope(FXMLComponent.class, fxmlLoadingScope);

        // GuiceFXMLLoader
        bind(GuiceFXMLLoader.class);

        // FXMLControllerTypeListener
        final FXMLControllerTypeListener fxmlControllerTypeListener = new FXMLControllerTypeListener();
        requestInjection(fxmlControllerTypeListener);
        bind(FXMLControllerTypeListener.class).toInstance(fxmlControllerTypeListener);
        bindListener(Matchers.any(), fxmlControllerTypeListener);

        // FXMLComponentTypeListener
        final FXMLComponentTypeListener fxmlComponentTypeListener = new FXMLComponentTypeListener();
        requestInjection(fxmlComponentTypeListener);
        bind(FXMLComponentTypeListener.class).toInstance(fxmlComponentTypeListener);
        bindListener(Matchers.any(), fxmlComponentTypeListener);

        // ControllerLookup
        bind(ControllerLookup.class).toProvider(new ControllerLookupProvider(fxmlLoadingScope));

    }

    /**
     * The Class ControllerLookupProvider.
     */
    private final class ControllerLookupProvider implements Provider<ControllerLookup> {

        /** The fxml loading scope. */
        private final FXMLLoadingScope fxmlLoadingScope;

        /**
         * Instantiates a new controller lookup provider.
         * @param fxmlLoadingScope
         *            the fxml loading scope
         */
        private ControllerLookupProvider(final FXMLLoadingScope fxmlLoadingScope) {
            super();
            this.fxmlLoadingScope = fxmlLoadingScope;
        }

        /*
         * (non-Javadoc)
         * @see com.google.inject.Provider#get()
         */
        @Override
        public ControllerLookup get() {
            if (!this.fxmlLoadingScope.isActive()) {
                throw new IllegalStateException("A ControllerLookup instance cannot be injected while outside of the FXML Loading scope.");
            }
            return new ControllerLookup(this.fxmlLoadingScope.getIdentifiables());
        }
    }

}
