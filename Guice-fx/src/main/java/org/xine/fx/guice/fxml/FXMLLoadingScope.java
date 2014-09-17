package org.xine.fx.guice.fxml;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.xine.fx.guice.FXMLController;
import org.xine.fx.guice.GuiceFXMLLoader;
import org.xine.fx.guice.controllerlookup.ControllerLookup;
import org.xine.fx.guice.controllerlookup.IdentifiableController;

import com.google.inject.Key;
import com.google.inject.Provider;
import com.google.inject.Scope;
import com.google.inject.Singleton;

/**
 * The Class FXMLLoadingScope.
 */
@Singleton
public final class FXMLLoadingScope implements Scope {

    /** The fxml loader. */
    @SuppressWarnings("unused")
    private GuiceFXMLLoader fxmlLoader;

    /** The identifiables. */
    private Set<IdentifiableController> identifiables;

    /**
     * Instantiates a new FXML loading scope.
     */
    public FXMLLoadingScope() {
        super();
    }

    /**
     * Enter the scope. From here on in, controllers implementing {@link IdentifiableController} and annotated wih {@link FXMLController} will
     * be retrievable from any {@link ControllerLookup} instance that is
     * injected.
     * @param fxmlLoader
     *            The FXML Loader to be used within this scope.
     */
    @SuppressWarnings("hiding")
    public void enter(final GuiceFXMLLoader fxmlLoader) {
        this.fxmlLoader = fxmlLoader;
        this.identifiables = new HashSet<>();
    }

    /**
     * End the scope.
     */
    public void exit() {
        this.identifiables = null;
        this.fxmlLoader = null;
    }

    /*
     * (non-Javadoc)
     * @see com.google.inject.Scope#scope(com.google.inject.Key, com.google.inject.Provider)
     */
    @Override
    public <T> Provider<T> scope(final Key<T> key, final Provider<T> unscoped) {
        return new Provider<T>() {
            @SuppressWarnings("synthetic-access")
            @Override
            public T get() {
                final T providedObject = unscoped.get();
                if (FXMLLoadingScope.this.identifiables != null && providedObject instanceof IdentifiableController) {
                    final IdentifiableController identifiable = (IdentifiableController) providedObject;
                    FXMLLoadingScope.this.identifiables.add(identifiable);
                }
                return providedObject;
            }
        };
    }

    /**
     * Gets the single instance of FXMLLoadingScope.
     * @param <T>
     *            the generic type
     * @param controllerId
     *            the controller id
     * @return single instance of FXMLLoadingScope
     */
    @SuppressWarnings("unchecked")
    public <T> T getInstance(final String controllerId) {
        for (final IdentifiableController identifiable : this.identifiables) {
            if (identifiable.getId().equals(controllerId)) {
                return (T) identifiable;
            }
        }
        // TODO Throw an exception maybe?
        return null;
    }

    /**
     * Gets the identifiables.
     * @return the identifiables
     */
    public Collection<IdentifiableController> getIdentifiables() {
        return this.identifiables;
    }

    /**
     * Checks whether the FXML Loading Scope is currently being used.
     * @return {@code true} if this scope is currently active, {@code false} otherwise.
     */
    public boolean isActive() {
        return (this.identifiables != null);
    }

}
