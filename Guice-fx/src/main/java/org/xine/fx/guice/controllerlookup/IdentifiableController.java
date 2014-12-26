package org.xine.fx.guice.controllerlookup;

import javafx.scene.Parent;

/**
 * The Interface IdentifiableController.
 */

public interface IdentifiableController {

    /**
     * Gets the id.
     * @return The string ID that can be used to lookup this controller. This
     *         could be hard coded or could return the ID set on the FXML for
     *         this control in the {@link Parent}.
     */
    String getId();

}
