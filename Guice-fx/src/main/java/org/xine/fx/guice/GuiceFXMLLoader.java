package org.xine.fx.guice;

import org.xine.fx.guice.fxml.FXMLComponentBuilderFactory;
import org.xine.fx.guice.fxml.FXMLLoadingScope;

import com.google.inject.Inject;
import com.google.inject.Injector;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ResourceBundle;

import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.util.Callback;

/**
 * If you want to `guicify` your JavaFX experience you can use an instance of
 * this class instead of the FXMLLoader that ships with JavaFX 2.x.
 * <p>
 * The easiest way to use this class is by just injecting it into your JavaFX application in the
 * right places.
 * </p>
 * @author Benjamin P. Jung
 * @see javafx.fxml.FXMLLoader
 */

public class GuiceFXMLLoader {

    /**
     * Guice Injector that will be used to fetch an instance of our `controller
     * class`.
     */
    private final Injector injector;

    /**
     * The loading scope that is being entered when loading FXML files.
     */
    private final FXMLLoadingScope fxmlLoadingScope;

    /**
     * This constructor is usually never called directly.
     * <p>
     * Instead use an existing {@link com.google.inject.Injector} instance to fetch an instance of
     * this class.
     * </p>
     * @param injector
     *            Usually injected via Guice.
     * @param fxmlLoadingScope
     *            Usually injected via Guice.
     * @throws IllegalArgumentException
     *             if you try to pass a {@code null} value as
     *             injector instance.
     * @throws IllegalStateException
     *             if the injector has no binding for the {@link FXMLController} loading scope.
     */
    @Inject
    public GuiceFXMLLoader(final Injector injector, final FXMLLoadingScope fxmlLoadingScope)
            throws IllegalArgumentException, IllegalStateException {
        super();
        if (injector == null) {
            throw new IllegalArgumentException("The Injector instance must not be null.");
        }
        if (!injector.getScopeBindings().containsKey(FXMLController.class)) {
            throw new IllegalStateException(
                    "FXMLController loading scope is not bound in your Injector.");
        }
        this.injector = injector;
        this.fxmlLoadingScope = fxmlLoadingScope;
    }

    /**
     * Loads an object hierarchy from a FXML document.
     * <p>
     * A simple wrapper around the {@link javafx.fxml.FXMLLoader#load(URL, ResourceBundle) load
     * method} of JavaFX' FXMLLoader class that adds a tiny notch of Guice-related magic.
     * </p>
     * @param url
     *            URL of the FXML resource to be loaded.
     * @param resources
     *            Resources to be used to localize strings.
     * @return The loaded object hierarchy encapsulated in a special result object.
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     * @see javafx.fxml.FXMLLoader#load(URL, ResourceBundle)
     */
    @SuppressWarnings("synthetic-access")
    public Result load(final URL url, final ResourceBundle resources) throws IOException {

        this.fxmlLoadingScope.enter(this);

        final FXMLLoader loader = new FXMLLoader();
        loader.setLocation(url);
        if (resources != null) {
            loader.setResources(resources);
        }
        loader.setBuilderFactory(this.injector.getInstance(FXMLComponentBuilderFactory.class));
        loader.setControllerFactory(new Callback<Class<?>, Object>() {
            @Override
            public Object call(final Class<?> param) {
                // Use our Guice injector to fetch an instance of the desired
                // controller class
                return param == null ? null : GuiceFXMLLoader.this.injector.getInstance(param);
            }
        });

        final Node root = (Node) loader.load(url.openStream());

        // Prepares the result that is being returned after loading the FXML hierarchy.
        final Result result = new Result();
        result.location.set(loader.getLocation());
        result.resources.set(loader.getResources());
        result.controller.set(loader.getController());
        result.root.set(root);
        result.charset.set(loader.getCharset());

        this.fxmlLoadingScope.exit();

        return result;

    }

    /**
     * Loads an object hierarchy from a FXML document.
     * <p>
     * A simple wrapper around the {@link javafx.fxml.FXMLLoader#load(URL) load method} of JavaFX'
     * FXMLLoader class that adds a tiny notch of Guice-related magic.
     * </p>
     * @param url
     *            URL of the FXML resource to be loaded.
     * @return The loaded object hierarchy encapsulated in a special result object.
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     * @see javafx.fxml.FXMLLoader#load(URL)
     */
    public Result load(final URL url) throws IOException {
        // Delegates method call.
        return load(url, null);
    }

    /**
     * A simple wrapper around the result of a FXML loading operation.
     * @author Benjamin P. Jung
     */
    public static final class Result {

        /** The location. */
        private final ReadOnlyObjectWrapper<URL> location = new ReadOnlyObjectWrapper<>();

        /** The resources. */
        private final ReadOnlyObjectWrapper<ResourceBundle> resources = new ReadOnlyObjectWrapper<>();

        /** The root. */
        private final ReadOnlyObjectWrapper<Object> root = new ReadOnlyObjectWrapper<>();

        /** The controller. */
        private final ReadOnlyObjectWrapper<Object> controller = new ReadOnlyObjectWrapper<>();

        /** The charset. */
        private final ReadOnlyObjectWrapper<Charset> charset = new ReadOnlyObjectWrapper<>();

        /**
         * Location property.
         * @return the read only object property
         */
        public ReadOnlyObjectProperty<URL> locationProperty() {
            return this.location.getReadOnlyProperty();
        }

        /**
         * Gets the url.
         * @return the url
         */
        public URL getUrl() {
            return this.location.get();
        }

        /**
         * Resources property.
         * @return the read only object property
         */
        public ReadOnlyObjectProperty<ResourceBundle> resourcesProperty() {
            return this.resources.getReadOnlyProperty();
        }

        /**
         * Gets the resources.
         * @return the resources
         */
        public ResourceBundle getResources() {
            return this.resources.get();
        }

        /**
         * Root property.
         * @return the read only object property
         */
        public ReadOnlyObjectProperty<Object> rootProperty() {
            return this.root.getReadOnlyProperty();
        }

        /**
         * Gets the root.
         * @param <N>
         *            the number type
         * @return the root
         */
        @SuppressWarnings("unchecked")
        public <N> N getRoot() {
            return (N) this.root.get();
        }

        /**
         * Controller property.
         * @return the read only object property
         */
        public ReadOnlyObjectProperty<Object> controllerProperty() {
            return this.controller.getReadOnlyProperty();
        }

        /**
         * Gets the controller.
         * @param <N>
         *            the number type
         * @return The controller associated with the root object.
         * @see #getRoot()
         */
        @SuppressWarnings("unchecked")
        public <N> N getController() {
            return (N) this.controller.get();
        }

        /**
         * Charset property.
         * @return the read only object property
         */
        public ReadOnlyObjectProperty<Charset> charsetProperty() {
            return this.charset.getReadOnlyProperty();
        }

        /**
         * Gets the charset.
         * @return the charset
         */
        public Charset getCharset() {
            return this.charset.get();
        }

    }

}
