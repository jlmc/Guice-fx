package org.xine.fx.guice.thread;

import org.xine.fx.guice.FxApplicationThread;

import com.google.inject.AbstractModule;
import com.google.inject.matcher.Matchers;

/**
 * The Class FxApplicationThreadModule.
 */

public final class FxApplicationThreadModule extends AbstractModule {

    /*
     * (non-Javadoc)
     * @see com.google.inject.AbstractModule#configure()
     */
    @Override
    protected void configure() {
        bindInterceptor(Matchers.any(), Matchers.annotatedWith(FxApplicationThread.class),
                new FxApplicationThreadMethodInterceptor());
    }

}
