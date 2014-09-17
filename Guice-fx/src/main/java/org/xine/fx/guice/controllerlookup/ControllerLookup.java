package org.xine.fx.guice.controllerlookup;

import java.util.Collection;

/**
 * A store of all controller instances that have been injected while an FXML
 * control was being created.
 * <p>
 * {@link ControllerLookup} must only be injected into controllers that are created while an FXML control is being created.
 * @author jcosta
 */
public class ControllerLookup {

    /** The identifiables. */
    private final Collection<IdentifiableController> identifiables;

    /**
     * Instantiates a new controller lookup.
     * @param identifiables
     *            the identifiables
     */
    public ControllerLookup(final Collection<IdentifiableController> identifiables) {
        super();
        this.identifiables = identifiables;
    }

    /**
     * Returns a controller instance with the given ID.
     * @param <T>
     *            the generic type
     * @param id
     *            The string ID of the controller as returned by {@link IdentifiableController#getId()}
     * @return The controller with the given ID that has just been
     *         looked up.
     */
    @SuppressWarnings("unchecked")
    public <T> T lookup(final String id) {
        for (final IdentifiableController controller : this.identifiables) {
            if (controller.getId().equals(id)) {
                return (T) controller;
            }
        }
        throw new IllegalArgumentException("Could not find a controller with the ID '" + id + "'");
    }

}
