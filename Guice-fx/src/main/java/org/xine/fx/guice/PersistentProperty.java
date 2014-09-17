package org.xine.fx.guice;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * This annotation can be used to mark JavaFX Properties as persistent, thus
 * the value of these properties will be stored on your local hard drive and
 * restored the next time your application is started.
 * <p>
 * The values for the JavaFX properties will be fetched and stored using an instance of java.util.prefs.Preferences in the background.
 * </p>
 * @see java.util.prefs.Preferences
 */
@Documented
@Retention(RUNTIME)
@Target({FIELD })
public @interface PersistentProperty {

    /**
     * Type.
     * @see java.util.prefs.Preferences#userNodeForPackage(Class).
     * @see java.util.prefs.Preferences#systemNodeForPackage(Class)
     */
    public NodeType type() default NodeType.USER_NODE;

    /**
     * Specifies the preference node from the calling user's preference tree that is associated
     * (by convention) with the specified class's package. The convention is as follows: the absolute
     * path name of the node is the fully qualified package name, preceded by a slash ('/'), and with
     * each period ('.') replaced by a slash. For example the absolute path name of the node associated
     * with the class com.acme.widget.Foo is /com/acme/widget.
     */
    public Class<?> clazz();

    /**
     * Key.
     */
    public String key();

    /**
     * Enumeration over the different storage facilities for your
     * persistent properties.
     * @author Benjamin P. Jung
     */
    public enum NodeType {
        /**
         * Indicated, that the property shall be persisted using the <strong>user</strong> preference tree.
         * @see java.util.prefs.Preferences#userNodeForPackage(Class)
         */
        USER_NODE,

        /**
         * Indicated, that the property shall be persisted using the <strong>system</strong> preference tree.
         * @see java.util.prefs.Preferences#systemNodeForPackage(Class)
         */
        SYSTEM_NODE;

    }

}
