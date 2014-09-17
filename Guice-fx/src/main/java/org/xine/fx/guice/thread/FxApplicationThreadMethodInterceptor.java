package org.xine.fx.guice.thread;

import javafx.application.Platform;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.xine.fx.guice.FxApplicationThread;

/**
 * The Class FxApplicationThreadMethodInterceptor.
 */
class FxApplicationThreadMethodInterceptor implements MethodInterceptor {

    /*
     * (non-Javadoc)
     * @see org.aopalliance.intercept.MethodInterceptor#invoke(org.aopalliance.intercept.MethodInvocation)
     */
    @Override
    public Object invoke(final MethodInvocation invocation) throws Throwable {

        final FxApplicationThread annotation = invocation.getMethod().getAnnotation(FxApplicationThread.class);
        @SuppressWarnings("synthetic-access")
        final FxTask fxTask = new FxTask(invocation);

        if (annotation == null) {
            throw new IllegalStateException("Method is not annotated with @FxApplicationThread!");
        }

        final Class<?> returnType = invocation.getMethod().getReturnType();
        if (!(returnType.equals(void.class) || returnType.equals(Void.class))) {
            throw new RuntimeException(String.format("[%s#%s] Only methods with return type 'void' can be annotated with @FxApplicationThread!", invocation.getThis().getClass().getName(), invocation.getMethod().getName()));
        }

        if (invocation.getMethod().getExceptionTypes().length > 0) {
            throw new RuntimeException("Only methods that don't declare exception types can be annotated with @FxApplicationThread!");
        }

        final Object retval;
        if (Platform.isFxApplicationThread()) {
            retval = invocation.proceed();
        } else {
            Platform.runLater(fxTask);
            retval = null;
        }
        return retval;

    }

    /**
     * The Class FxTask.
     */
    private static class FxTask implements Runnable {

        /** The method invocation. */
        private final MethodInvocation methodInvocation;

        /** The return value. */
        @SuppressWarnings("unused")
        private Object returnValue;

        /**
         * Instantiates a new fx task.
         * @param methodInvocation
         *            the method invocation
         */
        private FxTask(final MethodInvocation methodInvocation) {
            super();
            this.methodInvocation = methodInvocation;
        }

        /*
         * (non-Javadoc)
         * @see java.lang.Runnable#run()
         */
        @Override
        public void run() {
            try {
                this.returnValue = this.methodInvocation.proceed();
            } catch (final Throwable t) {
                throw new RuntimeException(t);
            }
        }

    }

}
