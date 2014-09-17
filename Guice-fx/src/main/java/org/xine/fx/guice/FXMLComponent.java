package org.xine.fx.guice;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javafx.fxml.FXMLLoader;

import com.google.inject.ScopeAnnotation;

/**
 * The Interface FXMLComponent.
 */
@Documented
@ScopeAnnotation
@Retention(RUNTIME)
@Target({TYPE })
public @interface FXMLComponent {

    /**
     * A string definition of the URL to be used for loading the FXML file.
     * <p>
     * The location can either be a location absolute or relative within the classpath (e.g. "/com/example/MyComponent.fxml" or "MyComponent.fxml") or a complete URL (e.g. "http://www.example.com/MyComponent.fxml")
     * </p>
     * @return A String that represents the location of the FXML file
     *         that shall be loaded.
     * @see FXMLLoader#setLocation(java.net.URL)
     */
    public String location() default "";

    /**
     * Location of the {@link java.util.ResourceBundle} to be used when loading the FXML
     * document that represents this component.
     * <p>
     * Default: none
     * </p>
     * @return Location of the resource bundle
     * @see FXMLLoader#setResources(java.util.ResourceBundle)
     */
    public String resources() default "";

    /**
     * Character set that has to be used, when parsing the FXML input given.
     * <p>
     * Default: UTF-8
     * </p>
     * @return The charset of the FXML file
     * @see FXMLLoader#setCharset(java.nio.charset.Charset)
     */
    public String charset() default "UTF-8";

}
