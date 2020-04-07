// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.transport.impl.jetty;

import org.eclipse.jetty.http.HttpFields;
import org.fourthline.cling.model.message.UpnpResponse;
import org.eclipse.jetty.io.Buffer;
import java.io.UnsupportedEncodingException;
import org.eclipse.jetty.io.ByteArrayBuffer;
import org.fourthline.cling.model.message.header.ContentTypeHeader;
import org.seamless.util.MimeType;
import org.fourthline.cling.model.message.UpnpMessage;
import java.util.Iterator;
import org.fourthline.cling.model.message.UpnpHeaders;
import java.util.List;
import java.util.Map;
import org.fourthline.cling.model.message.header.UpnpHeader;
import org.fourthline.cling.model.message.UpnpRequest;
import org.eclipse.jetty.client.ContentExchange;
import org.fourthline.cling.transport.spi.StreamClient;
import org.fourthline.cling.transport.spi.StreamClientConfiguration;
import org.seamless.util.Exceptions;
import org.eclipse.jetty.client.HttpExchange;
import java.util.logging.Level;
import org.fourthline.cling.model.message.StreamResponseMessage;
import java.util.concurrent.Callable;
import org.fourthline.cling.model.message.StreamRequestMessage;
import org.fourthline.cling.transport.spi.InitializationException;
import org.eclipse.jetty.util.thread.ThreadPool;
import java.util.concurrent.ExecutorService;
import org.eclipse.jetty.util.thread.ExecutorThreadPool;
import org.eclipse.jetty.client.HttpClient;
import java.util.logging.Logger;
import org.fourthline.cling.transport.spi.AbstractStreamClient;

public class StreamClientImpl extends AbstractStreamClient<StreamClientConfigurationImpl, HttpContentExchange>
{
    private static final Logger log;
    protected final StreamClientConfigurationImpl configuration;
    protected final HttpClient client;
    
    public StreamClientImpl(final StreamClientConfigurationImpl configuration) throws InitializationException {
        this.configuration = configuration;
        StreamClientImpl.log.info("Starting Jetty HttpClient...");
        (this.client = new HttpClient()).setThreadPool((ThreadPool)new ExecutorThreadPool(this.getConfiguration().getRequestExecutorService()) {
            protected void doStop() throws Exception {
            }
        });
        this.client.setTimeout((long)((configuration.getTimeoutSeconds() + 5) * 1000));
        this.client.setConnectTimeout((configuration.getTimeoutSeconds() + 5) * 1000);
        this.client.setMaxRetries(configuration.getRequestRetryCount());
        try {
            this.client.start();
        }
        catch (Exception ex) {
            throw new InitializationException("Could not start Jetty HTTP client: " + ex, ex);
        }
    }
    
    @Override
    public StreamClientConfigurationImpl getConfiguration() {
        return this.configuration;
    }
    
    @Override
    protected HttpContentExchange createRequest(final StreamRequestMessage requestMessage) {
        return new HttpContentExchange(this.getConfiguration(), this.client, requestMessage);
    }
    
    @Override
    protected Callable<StreamResponseMessage> createCallable(final StreamRequestMessage requestMessage, final HttpContentExchange exchange) {
        return new Callable<StreamResponseMessage>() {
            @Override
            public StreamResponseMessage call() throws Exception {
                if (StreamClientImpl.log.isLoggable(Level.FINE)) {
                    StreamClientImpl.log.fine("Sending HTTP request: " + requestMessage);
                }
                StreamClientImpl.this.client.send((HttpExchange)exchange);
                final int exchangeState = exchange.waitForDone();
                if (exchangeState == 7) {
                    try {
                        return exchange.createResponse();
                    }
                    catch (Throwable t) {
                        StreamClientImpl.log.log(Level.WARNING, "Error reading response: " + requestMessage, Exceptions.unwrap(t));
                        return null;
                    }
                }
                if (exchangeState == 11) {
                    return null;
                }
                if (exchangeState == 9) {
                    return null;
                }
                StreamClientImpl.log.warning("Unhandled HTTP exchange status: " + exchangeState);
                return null;
            }
        };
    }
    
    @Override
    protected void abort(final HttpContentExchange exchange) {
        exchange.cancel();
    }
    
    @Override
    protected boolean logExecutionException(final Throwable t) {
        return false;
    }
    
    @Override
    public void stop() {
        try {
            this.client.stop();
        }
        catch (Exception ex) {
            StreamClientImpl.log.info("Error stopping HTTP client: " + ex);
        }
    }
    
    static {
        log = Logger.getLogger(StreamClient.class.getName());
    }
    
    public static class HttpContentExchange extends ContentExchange
    {
        protected final StreamClientConfigurationImpl configuration;
        protected final HttpClient client;
        protected final StreamRequestMessage requestMessage;
        protected Throwable exception;
        
        public HttpContentExchange(final StreamClientConfigurationImpl configuration, final HttpClient client, final StreamRequestMessage requestMessage) {
            super(true);
            this.configuration = configuration;
            this.client = client;
            this.requestMessage = requestMessage;
            this.applyRequestURLMethod();
            this.applyRequestHeaders();
            this.applyRequestBody();
        }
        
        protected void onConnectionFailed(final Throwable t) {
            StreamClientImpl.log.log(Level.WARNING, "HTTP connection failed: " + this.requestMessage, Exceptions.unwrap(t));
        }
        
        protected void onException(final Throwable t) {
            StreamClientImpl.log.log(Level.WARNING, "HTTP request failed: " + this.requestMessage, Exceptions.unwrap(t));
        }
        
        public StreamClientConfigurationImpl getConfiguration() {
            return this.configuration;
        }
        
        public StreamRequestMessage getRequestMessage() {
            return this.requestMessage;
        }
        
        protected void applyRequestURLMethod() {
            final UpnpRequest requestOperation = this.getRequestMessage().getOperation();
            if (StreamClientImpl.log.isLoggable(Level.FINE)) {
                StreamClientImpl.log.fine("Preparing HTTP request message with method '" + requestOperation.getHttpMethodName() + "': " + this.getRequestMessage());
            }
            this.setURL(requestOperation.getURI().toString());
            this.setMethod(requestOperation.getHttpMethodName());
        }
        
        protected void applyRequestHeaders() {
            final UpnpHeaders headers = this.getRequestMessage().getHeaders();
            if (StreamClientImpl.log.isLoggable(Level.FINE)) {
                StreamClientImpl.log.fine("Writing headers on HttpContentExchange: " + headers.size());
            }
            if (!headers.containsKey(UpnpHeader.Type.USER_AGENT)) {
                this.setRequestHeader(UpnpHeader.Type.USER_AGENT.getHttpName(), this.getConfiguration().getUserAgentValue(this.getRequestMessage().getUdaMajorVersion(), this.getRequestMessage().getUdaMinorVersion()));
            }
            for (final Map.Entry<String, List<String>> entry : headers.entrySet()) {
                for (final String v : entry.getValue()) {
                    final String headerName = entry.getKey();
                    if (StreamClientImpl.log.isLoggable(Level.FINE)) {
                        StreamClientImpl.log.fine("Setting header '" + headerName + "': " + v);
                    }
                    this.addRequestHeader(headerName, v);
                }
            }
        }
        
        protected void applyRequestBody() {
            if (this.getRequestMessage().hasBody()) {
                if (this.getRequestMessage().getBodyType() == UpnpMessage.BodyType.STRING) {
                    if (StreamClientImpl.log.isLoggable(Level.FINE)) {
                        StreamClientImpl.log.fine("Writing textual request body: " + this.getRequestMessage());
                    }
                    final MimeType contentType = (this.getRequestMessage().getContentTypeHeader() != null) ? this.getRequestMessage().getContentTypeHeader().getValue() : ContentTypeHeader.DEFAULT_CONTENT_TYPE_UTF8;
                    final String charset = (this.getRequestMessage().getContentTypeCharset() != null) ? this.getRequestMessage().getContentTypeCharset() : "UTF-8";
                    this.setRequestContentType(contentType.toString());
                    ByteArrayBuffer buffer;
                    try {
                        buffer = new ByteArrayBuffer(this.getRequestMessage().getBodyString(), charset);
                    }
                    catch (UnsupportedEncodingException ex) {
                        throw new RuntimeException("Unsupported character encoding: " + charset, ex);
                    }
                    this.setRequestHeader("Content-Length", String.valueOf(buffer.length()));
                    this.setRequestContent((Buffer)buffer);
                }
                else {
                    if (StreamClientImpl.log.isLoggable(Level.FINE)) {
                        StreamClientImpl.log.fine("Writing binary request body: " + this.getRequestMessage());
                    }
                    if (this.getRequestMessage().getContentTypeHeader() == null) {
                        throw new RuntimeException("Missing content type header in request message: " + this.requestMessage);
                    }
                    final MimeType contentType = this.getRequestMessage().getContentTypeHeader().getValue();
                    this.setRequestContentType(contentType.toString());
                    final ByteArrayBuffer buffer2 = new ByteArrayBuffer(this.getRequestMessage().getBodyBytes());
                    this.setRequestHeader("Content-Length", String.valueOf(buffer2.length()));
                    this.setRequestContent((Buffer)buffer2);
                }
            }
        }
        
        protected StreamResponseMessage createResponse() {
            final UpnpResponse responseOperation = new UpnpResponse(this.getResponseStatus(), UpnpResponse.Status.getByStatusCode(this.getResponseStatus()).getStatusMsg());
            if (StreamClientImpl.log.isLoggable(Level.FINE)) {
                StreamClientImpl.log.fine("Received response: " + responseOperation);
            }
            final StreamResponseMessage responseMessage = new StreamResponseMessage(responseOperation);
            final UpnpHeaders headers = new UpnpHeaders();
            final HttpFields responseFields = this.getResponseFields();
            for (final String name : responseFields.getFieldNamesCollection()) {
                for (final String value : responseFields.getValuesCollection(name)) {
                    headers.add(name, value);
                }
            }
            responseMessage.setHeaders(headers);
            final byte[] bytes = this.getResponseContentBytes();
            Label_0319: {
                if (bytes != null && bytes.length > 0 && responseMessage.isContentTypeMissingOrText()) {
                    if (StreamClientImpl.log.isLoggable(Level.FINE)) {
                        StreamClientImpl.log.fine("Response contains textual entity body, converting then setting string on message");
                    }
                    try {
                        responseMessage.setBodyCharacters(bytes);
                        break Label_0319;
                    }
                    catch (UnsupportedEncodingException ex) {
                        throw new RuntimeException("Unsupported character encoding: " + ex, ex);
                    }
                }
                if (bytes != null && bytes.length > 0) {
                    if (StreamClientImpl.log.isLoggable(Level.FINE)) {
                        StreamClientImpl.log.fine("Response contains binary entity body, setting bytes on message");
                    }
                    responseMessage.setBody(UpnpMessage.BodyType.BYTES, bytes);
                }
                else if (StreamClientImpl.log.isLoggable(Level.FINE)) {
                    StreamClientImpl.log.fine("Response did not contain entity body");
                }
            }
            if (StreamClientImpl.log.isLoggable(Level.FINE)) {
                StreamClientImpl.log.fine("Response message complete: " + responseMessage);
            }
            return responseMessage;
        }
    }
}
