package org.xine.fx.guice;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.xine.fx.guice.controllerlookup.ControllerLookup;
import org.xine.fx.guice.controllerlookup.IdentifiableController;

import com.google.inject.Scope;
import com.google.inject.ScopeAnnotation;

/**
 * This {@link Scope} annotation should be used on controllers which need to be
 * looked up via {@link ControllerLookup}.
 * @author Andy Till
 * @see IdentifiableController
 */
@Documented
@ScopeAnnotation
@Retention(RUNTIME)
@Target({TYPE, FIELD })
public @interface FXMLController {

    /**
     * Controller id.
     * @return the string
     */
    public String controllerId() default "";

}
