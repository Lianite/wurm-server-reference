// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.transport.impl;

import org.fourthline.cling.model.message.Connection;
import java.io.OutputStream;
import org.fourthline.cling.model.message.StreamResponseMessage;
import java.io.InputStream;
import java.io.IOException;
import org.seamless.util.Exceptions;
import java.util.logging.Level;
import org.fourthline.cling.model.message.UpnpResponse;
import org.fourthline.cling.model.message.UpnpMessage;
import org.seamless.util.io.IO;
import java.util.List;
import java.util.Map;
import org.fourthline.cling.model.message.UpnpHeaders;
import java.util.Locale;
import org.fourthline.cling.model.message.StreamRequestMessage;
import org.fourthline.cling.model.message.UpnpRequest;
import org.fourthline.cling.protocol.ProtocolFactory;
import com.sun.net.httpserver.HttpExchange;
import java.util.logging.Logger;
import org.fourthline.cling.transport.spi.UpnpStream;

public abstract class HttpExchangeUpnpStream extends UpnpStream
{
    private static Logger log;
    private HttpExchange httpExchange;
    
    public HttpExchangeUpnpStream(final ProtocolFactory protocolFactory, final HttpExchange httpExchange) {
        super(protocolFactory);
        this.httpExchange = httpExchange;
    }
    
    public HttpExchange getHttpExchange() {
        return this.httpExchange;
    }
    
    @Override
    public void run() {
        try {
            HttpExchangeUpnpStream.log.fine("Processing HTTP request: " + this.getHttpExchange().getRequestMethod() + " " + this.getHttpExchange().getRequestURI());
            final StreamRequestMessage requestMessage = new StreamRequestMessage(UpnpRequest.Method.getByHttpName(this.getHttpExchange().getRequestMethod()), this.getHttpExchange().getRequestURI());
            if (requestMessage.getOperation().getMethod().equals(UpnpRequest.Method.UNKNOWN)) {
                HttpExchangeUpnpStream.log.fine("Method not supported by UPnP stack: " + this.getHttpExchange().getRequestMethod());
                throw new RuntimeException("Method not supported: " + this.getHttpExchange().getRequestMethod());
            }
            requestMessage.getOperation().setHttpMinorVersion(this.getHttpExchange().getProtocol().toUpperCase(Locale.ROOT).equals("HTTP/1.1") ? 1 : 0);
            HttpExchangeUpnpStream.log.fine("Created new request message: " + requestMessage);
            requestMessage.setConnection(this.createConnection());
            requestMessage.setHeaders(new UpnpHeaders(this.getHttpExchange().getRequestHeaders()));
            InputStream is = null;
            byte[] bodyBytes;
            try {
                is = this.getHttpExchange().getRequestBody();
                bodyBytes = IO.readBytes(is);
            }
            finally {
                if (is != null) {
                    is.close();
                }
            }
            HttpExchangeUpnpStream.log.fine("Reading request body bytes: " + bodyBytes.length);
            if (bodyBytes.length > 0 && requestMessage.isContentTypeMissingOrText()) {
                HttpExchangeUpnpStream.log.fine("Request contains textual entity body, converting then setting string on message");
                requestMessage.setBodyCharacters(bodyBytes);
            }
            else if (bodyBytes.length > 0) {
                HttpExchangeUpnpStream.log.fine("Request contains binary entity body, setting bytes on message");
                requestMessage.setBody(UpnpMessage.BodyType.BYTES, bodyBytes);
            }
            else {
                HttpExchangeUpnpStream.log.fine("Request did not contain entity body");
            }
            final StreamResponseMessage responseMessage = this.process(requestMessage);
            if (responseMessage != null) {
                HttpExchangeUpnpStream.log.fine("Preparing HTTP response message: " + responseMessage);
                this.getHttpExchange().getResponseHeaders().putAll(responseMessage.getHeaders());
                final byte[] responseBodyBytes = (byte[])(responseMessage.hasBody() ? responseMessage.getBodyBytes() : null);
                final int contentLength = (responseBodyBytes != null) ? responseBodyBytes.length : -1;
                HttpExchangeUpnpStream.log.fine("Sending HTTP response message: " + responseMessage + " with content length: " + contentLength);
                this.getHttpExchange().sendResponseHeaders(responseMessage.getOperation().getStatusCode(), contentLength);
                if (contentLength > 0) {
                    HttpExchangeUpnpStream.log.fine("Response message has body, writing bytes to stream...");
                    OutputStream os = null;
                    try {
                        os = this.getHttpExchange().getResponseBody();
                        IO.writeBytes(os, responseBodyBytes);
                        os.flush();
                    }
                    finally {
                        if (os != null) {
                            os.close();
                        }
                    }
                }
            }
            else {
                HttpExchangeUpnpStream.log.fine("Sending HTTP response status: 404");
                this.getHttpExchange().sendResponseHeaders(404, -1L);
            }
            this.responseSent(responseMessage);
        }
        catch (Throwable t) {
            HttpExchangeUpnpStream.log.fine("Exception occured during UPnP stream processing: " + t);
            if (HttpExchangeUpnpStream.log.isLoggable(Level.FINE)) {
                HttpExchangeUpnpStream.log.log(Level.FINE, "Cause: " + Exceptions.unwrap(t), Exceptions.unwrap(t));
            }
            try {
                this.httpExchange.sendResponseHeaders(500, -1L);
            }
            catch (IOException ex) {
                HttpExchangeUpnpStream.log.warning("Couldn't send error response: " + ex);
            }
            this.responseException(t);
        }
    }
    
    protected abstract Connection createConnection();
    
    static {
        HttpExchangeUpnpStream.log = Logger.getLogger(UpnpStream.class.getName());
    }
}
