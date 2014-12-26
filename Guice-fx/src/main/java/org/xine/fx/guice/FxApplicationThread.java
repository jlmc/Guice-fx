package org.xine.fx.guice;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * This annotation marks methods that must be executed on the JavaFX application
 * thread.
 * @see javafx.application.Platform#isFxApplicationThread()
 * @see javafx.application.Platform#runLater(java.lang.Runnable)
 */

@Documented
@Retention(RUNTIME)
@Target({METHOD })
public @interface FxApplicationThread {
    // Intentionally left empty.
}
