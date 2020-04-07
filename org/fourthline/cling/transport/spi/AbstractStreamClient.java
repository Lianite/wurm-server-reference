// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.transport.spi;

import java.util.concurrent.Future;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import org.seamless.util.Exceptions;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import org.fourthline.cling.model.message.StreamResponseMessage;
import org.fourthline.cling.model.message.StreamRequestMessage;
import java.util.logging.Logger;

public abstract class AbstractStreamClient<C extends StreamClientConfiguration, REQUEST> implements StreamClient<C>
{
    private static final Logger log;
    
    @Override
    public StreamResponseMessage sendRequest(final StreamRequestMessage requestMessage) throws InterruptedException {
        if (AbstractStreamClient.log.isLoggable(Level.FINE)) {
            AbstractStreamClient.log.fine("Preparing HTTP request: " + requestMessage);
        }
        final REQUEST request = this.createRequest(requestMessage);
        if (request == null) {
            return null;
        }
        final Callable<StreamResponseMessage> callable = this.createCallable(requestMessage, request);
        final long start = System.currentTimeMillis();
        final Future<StreamResponseMessage> future = this.getConfiguration().getRequestExecutorService().submit(callable);
        try {
            if (AbstractStreamClient.log.isLoggable(Level.FINE)) {
                AbstractStreamClient.log.fine("Waiting " + this.getConfiguration().getTimeoutSeconds() + " seconds for HTTP request to complete: " + requestMessage);
            }
            final StreamResponseMessage response = future.get(this.getConfiguration().getTimeoutSeconds(), TimeUnit.SECONDS);
            final long elapsed = System.currentTimeMillis() - start;
            if (AbstractStreamClient.log.isLoggable(Level.FINEST)) {
                AbstractStreamClient.log.finest("Got HTTP response in " + elapsed + "ms: " + requestMessage);
            }
            if (this.getConfiguration().getLogWarningSeconds() > 0 && elapsed > this.getConfiguration().getLogWarningSeconds() * 1000) {
                AbstractStreamClient.log.warning("HTTP request took a long time (" + elapsed + "ms): " + requestMessage);
            }
            return response;
        }
        catch (InterruptedException ex2) {
            if (AbstractStreamClient.log.isLoggable(Level.FINE)) {
                AbstractStreamClient.log.fine("Interruption, aborting request: " + requestMessage);
            }
            this.abort(request);
            throw new InterruptedException("HTTP request interrupted and aborted");
        }
        catch (TimeoutException ex3) {
            AbstractStreamClient.log.info("Timeout of " + this.getConfiguration().getTimeoutSeconds() + " seconds while waiting for HTTP request to complete, aborting: " + requestMessage);
            this.abort(request);
            return null;
        }
        catch (ExecutionException ex) {
            final Throwable cause = ex.getCause();
            if (!this.logExecutionException(cause)) {
                AbstractStreamClient.log.log(Level.WARNING, "HTTP request failed: " + requestMessage, Exceptions.unwrap(cause));
            }
            return null;
        }
        finally {
            this.onFinally(request);
        }
    }
    
    protected abstract REQUEST createRequest(final StreamRequestMessage p0);
    
    protected abstract Callable<StreamResponseMessage> createCallable(final StreamRequestMessage p0, final REQUEST p1);
    
    protected abstract void abort(final REQUEST p0);
    
    protected abstract boolean logExecutionException(final Throwable p0);
    
    protected void onFinally(final REQUEST request) {
    }
    
    static {
        log = Logger.getLogger(StreamClient.class.getName());
    }
}
