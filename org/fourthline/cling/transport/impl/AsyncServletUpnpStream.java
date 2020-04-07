// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.transport.impl;

import org.fourthline.cling.model.message.Connection;
import java.util.Iterator;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;
import org.fourthline.cling.model.message.UpnpResponse;
import java.io.InputStream;
import java.util.Enumeration;
import org.fourthline.cling.model.message.UpnpMessage;
import org.seamless.util.io.IO;
import org.fourthline.cling.model.message.UpnpHeaders;
import java.net.URI;
import org.fourthline.cling.model.message.UpnpRequest;
import java.io.IOException;
import javax.servlet.AsyncEvent;
import org.fourthline.cling.model.message.StreamRequestMessage;
import org.seamless.util.Exceptions;
import java.util.logging.Level;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
import org.fourthline.cling.protocol.ProtocolFactory;
import org.fourthline.cling.model.message.StreamResponseMessage;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.AsyncContext;
import java.util.logging.Logger;
import javax.servlet.AsyncListener;
import org.fourthline.cling.transport.spi.UpnpStream;

public abstract class AsyncServletUpnpStream extends UpnpStream implements AsyncListener
{
    private static final Logger log;
    protected final AsyncContext asyncContext;
    protected final HttpServletRequest request;
    protected StreamResponseMessage responseMessage;
    
    public AsyncServletUpnpStream(final ProtocolFactory protocolFactory, final AsyncContext asyncContext, final HttpServletRequest request) {
        super(protocolFactory);
        this.asyncContext = asyncContext;
        this.request = request;
        asyncContext.addListener(this);
    }
    
    protected HttpServletRequest getRequest() {
        return this.request;
    }
    
    protected HttpServletResponse getResponse() {
        final ServletResponse response;
        if ((response = this.asyncContext.getResponse()) == null) {
            throw new IllegalStateException("Couldn't get response from asynchronous context, already timed out");
        }
        return (HttpServletResponse)response;
    }
    
    protected void complete() {
        try {
            this.asyncContext.complete();
        }
        catch (IllegalStateException ex) {
            AsyncServletUpnpStream.log.info("Error calling servlet container's AsyncContext#complete() method: " + ex);
        }
    }
    
    @Override
    public void run() {
        try {
            final StreamRequestMessage requestMessage = this.readRequestMessage();
            if (AsyncServletUpnpStream.log.isLoggable(Level.FINER)) {
                AsyncServletUpnpStream.log.finer("Processing new request message: " + requestMessage);
            }
            this.responseMessage = this.process(requestMessage);
            if (this.responseMessage != null) {
                if (AsyncServletUpnpStream.log.isLoggable(Level.FINER)) {
                    AsyncServletUpnpStream.log.finer("Preparing HTTP response message: " + this.responseMessage);
                }
                this.writeResponseMessage(this.responseMessage);
            }
            else {
                if (AsyncServletUpnpStream.log.isLoggable(Level.FINER)) {
                    AsyncServletUpnpStream.log.finer("Sending HTTP response status: 404");
                }
                this.getResponse().setStatus(404);
            }
        }
        catch (Throwable t) {
            AsyncServletUpnpStream.log.info("Exception occurred during UPnP stream processing: " + t);
            if (AsyncServletUpnpStream.log.isLoggable(Level.FINER)) {
                AsyncServletUpnpStream.log.log(Level.FINER, "Cause: " + Exceptions.unwrap(t), Exceptions.unwrap(t));
            }
            if (!this.getResponse().isCommitted()) {
                AsyncServletUpnpStream.log.finer("Response hasn't been committed, returning INTERNAL SERVER ERROR to client");
                this.getResponse().setStatus(500);
            }
            else {
                AsyncServletUpnpStream.log.info("Could not return INTERNAL SERVER ERROR to client, response was already committed");
            }
            this.responseException(t);
        }
        finally {
            this.complete();
        }
    }
    
    @Override
    public void onStartAsync(final AsyncEvent event) throws IOException {
    }
    
    @Override
    public void onComplete(final AsyncEvent event) throws IOException {
        if (AsyncServletUpnpStream.log.isLoggable(Level.FINER)) {
            AsyncServletUpnpStream.log.finer("Completed asynchronous processing of HTTP request: " + event.getSuppliedRequest());
        }
        this.responseSent(this.responseMessage);
    }
    
    @Override
    public void onTimeout(final AsyncEvent event) throws IOException {
        if (AsyncServletUpnpStream.log.isLoggable(Level.FINER)) {
            AsyncServletUpnpStream.log.finer("Asynchronous processing of HTTP request timed out: " + event.getSuppliedRequest());
        }
        this.responseException(new Exception("Asynchronous request timed out"));
    }
    
    @Override
    public void onError(final AsyncEvent event) throws IOException {
        if (AsyncServletUpnpStream.log.isLoggable(Level.FINER)) {
            AsyncServletUpnpStream.log.finer("Asynchronous processing of HTTP request error: " + event.getThrowable());
        }
        this.responseException(event.getThrowable());
    }
    
    protected StreamRequestMessage readRequestMessage() throws IOException {
        final String requestMethod = this.getRequest().getMethod();
        final String requestURI = this.getRequest().getRequestURI();
        if (AsyncServletUpnpStream.log.isLoggable(Level.FINER)) {
            AsyncServletUpnpStream.log.finer("Processing HTTP request: " + requestMethod + " " + requestURI);
        }
        StreamRequestMessage requestMessage;
        try {
            requestMessage = new StreamRequestMessage(UpnpRequest.Method.getByHttpName(requestMethod), URI.create(requestURI));
        }
        catch (IllegalArgumentException ex) {
            throw new RuntimeException("Invalid request URI: " + requestURI, ex);
        }
        if (requestMessage.getOperation().getMethod().equals(UpnpRequest.Method.UNKNOWN)) {
            throw new RuntimeException("Method not supported: " + requestMethod);
        }
        requestMessage.setConnection(this.createConnection());
        final UpnpHeaders headers = new UpnpHeaders();
        final Enumeration<String> headerNames = this.getRequest().getHeaderNames();
        while (headerNames.hasMoreElements()) {
            final String headerName = headerNames.nextElement();
            final Enumeration<String> headerValues = this.getRequest().getHeaders(headerName);
            while (headerValues.hasMoreElements()) {
                final String headerValue = headerValues.nextElement();
                headers.add(headerName, headerValue);
            }
        }
        requestMessage.setHeaders(headers);
        InputStream is = null;
        byte[] bodyBytes;
        try {
            is = this.getRequest().getInputStream();
            bodyBytes = IO.readBytes(is);
        }
        finally {
            if (is != null) {
                is.close();
            }
        }
        if (AsyncServletUpnpStream.log.isLoggable(Level.FINER)) {
            AsyncServletUpnpStream.log.finer("Reading request body bytes: " + bodyBytes.length);
        }
        if (bodyBytes.length > 0 && requestMessage.isContentTypeMissingOrText()) {
            if (AsyncServletUpnpStream.log.isLoggable(Level.FINER)) {
                AsyncServletUpnpStream.log.finer("Request contains textual entity body, converting then setting string on message");
            }
            requestMessage.setBodyCharacters(bodyBytes);
        }
        else if (bodyBytes.length > 0) {
            if (AsyncServletUpnpStream.log.isLoggable(Level.FINER)) {
                AsyncServletUpnpStream.log.finer("Request contains binary entity body, setting bytes on message");
            }
            requestMessage.setBody(UpnpMessage.BodyType.BYTES, bodyBytes);
        }
        else if (AsyncServletUpnpStream.log.isLoggable(Level.FINER)) {
            AsyncServletUpnpStream.log.finer("Request did not contain entity body");
        }
        return requestMessage;
    }
    
    protected void writeResponseMessage(final StreamResponseMessage responseMessage) throws IOException {
        if (AsyncServletUpnpStream.log.isLoggable(Level.FINER)) {
            AsyncServletUpnpStream.log.finer("Sending HTTP response status: " + responseMessage.getOperation().getStatusCode());
        }
        this.getResponse().setStatus(responseMessage.getOperation().getStatusCode());
        for (final Map.Entry<String, List<String>> entry : responseMessage.getHeaders().entrySet()) {
            for (final String value : entry.getValue()) {
                this.getResponse().addHeader(entry.getKey(), value);
            }
        }
        this.getResponse().setDateHeader("Date", System.currentTimeMillis());
        final byte[] responseBodyBytes = (byte[])(responseMessage.hasBody() ? responseMessage.getBodyBytes() : null);
        final int contentLength = (responseBodyBytes != null) ? responseBodyBytes.length : -1;
        if (contentLength > 0) {
            this.getResponse().setContentLength(contentLength);
            AsyncServletUpnpStream.log.finer("Response message has body, writing bytes to stream...");
            IO.writeBytes(this.getResponse().getOutputStream(), responseBodyBytes);
        }
    }
    
    protected abstract Connection createConnection();
    
    static {
        log = Logger.getLogger(UpnpStream.class.getName());
    }
}
