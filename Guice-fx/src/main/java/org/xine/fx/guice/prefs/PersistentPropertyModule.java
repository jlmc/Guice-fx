package org.xine.fx.guice.prefs;

import com.google.inject.AbstractModule;
import com.google.inject.matcher.Matchers;

/**
 * The Class PersistentPropertyModule.
 */
public final class PersistentPropertyModule extends AbstractModule {

    /*
     * (non-Javadoc)
     * @see com.google.inject.AbstractModule#configure()
     */
    @Override
    public void configure() {
        bindListener(Matchers.any(), new PersistentPropertyTypeListener());
    }

}
