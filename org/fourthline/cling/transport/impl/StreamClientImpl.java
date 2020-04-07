// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.transport.impl;

import org.fourthline.cling.transport.spi.StreamClientConfiguration;
import org.fourthline.cling.model.message.UpnpHeaders;
import org.fourthline.cling.model.message.UpnpResponse;
import org.seamless.util.io.IO;
import org.fourthline.cling.model.message.UpnpMessage;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.seamless.http.Headers;
import org.fourthline.cling.model.message.header.UpnpHeader;
import java.io.InputStream;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.ProtocolException;
import org.seamless.util.Exceptions;
import java.util.logging.Level;
import java.net.HttpURLConnection;
import org.seamless.util.URIUtil;
import org.fourthline.cling.model.message.UpnpRequest;
import org.fourthline.cling.model.message.StreamResponseMessage;
import org.fourthline.cling.model.message.StreamRequestMessage;
import java.net.URL;
import java.net.URLStreamHandlerFactory;
import org.fourthline.cling.transport.spi.InitializationException;
import org.fourthline.cling.model.ModelUtil;
import java.util.logging.Logger;
import org.fourthline.cling.transport.spi.StreamClient;

public class StreamClientImpl implements StreamClient
{
    static final String HACK_STREAM_HANDLER_SYSTEM_PROPERTY = "hackStreamHandlerProperty";
    private static final Logger log;
    protected final StreamClientConfigurationImpl configuration;
    
    public StreamClientImpl(final StreamClientConfigurationImpl configuration) throws InitializationException {
        this.configuration = configuration;
        if (ModelUtil.ANDROID_EMULATOR || ModelUtil.ANDROID_RUNTIME) {
            throw new InitializationException("This client does not work on Android. The design of HttpURLConnection is broken, we can not add additional 'permitted' HTTP methods. Read the Cling manual.");
        }
        StreamClientImpl.log.fine("Using persistent HTTP stream client connections: " + configuration.isUsePersistentConnections());
        System.setProperty("http.keepAlive", Boolean.toString(configuration.isUsePersistentConnections()));
        if (System.getProperty("hackStreamHandlerProperty") == null) {
            StreamClientImpl.log.fine("Setting custom static URLStreamHandlerFactory to work around bad JDK defaults");
            try {
                URL.setURLStreamHandlerFactory((URLStreamHandlerFactory)Class.forName("org.fourthline.cling.transport.impl.FixedSunURLStreamHandler").newInstance());
            }
            catch (Throwable t) {
                throw new InitializationException("Failed to set modified URLStreamHandlerFactory in this environment. Can't use bundled default client based on HTTPURLConnection, see manual.");
            }
            System.setProperty("hackStreamHandlerProperty", "alreadyWorkedAroundTheEvilJDK");
        }
    }
    
    @Override
    public StreamClientConfigurationImpl getConfiguration() {
        return this.configuration;
    }
    
    @Override
    public StreamResponseMessage sendRequest(final StreamRequestMessage requestMessage) {
        final UpnpRequest requestOperation = requestMessage.getOperation();
        StreamClientImpl.log.fine("Preparing HTTP request message with method '" + requestOperation.getHttpMethodName() + "': " + requestMessage);
        final URL url = URIUtil.toURL(requestOperation.getURI());
        HttpURLConnection urlConnection = null;
        try {
            urlConnection = (HttpURLConnection)url.openConnection();
            urlConnection.setRequestMethod(requestOperation.getHttpMethodName());
            urlConnection.setReadTimeout(this.configuration.getTimeoutSeconds() * 1000);
            urlConnection.setConnectTimeout(this.configuration.getTimeoutSeconds() * 1000);
            this.applyRequestProperties(urlConnection, requestMessage);
            this.applyRequestBody(urlConnection, requestMessage);
            StreamClientImpl.log.fine("Sending HTTP request: " + requestMessage);
            final InputStream inputStream = urlConnection.getInputStream();
            return this.createResponse(urlConnection, inputStream);
        }
        catch (ProtocolException ex) {
            StreamClientImpl.log.log(Level.WARNING, "HTTP request failed: " + requestMessage, Exceptions.unwrap(ex));
            return null;
        }
        catch (IOException ex2) {
            if (urlConnection == null) {
                StreamClientImpl.log.log(Level.WARNING, "HTTP request failed: " + requestMessage, Exceptions.unwrap(ex2));
                return null;
            }
            if (ex2 instanceof SocketTimeoutException) {
                StreamClientImpl.log.info("Timeout of " + this.getConfiguration().getTimeoutSeconds() + " seconds while waiting for HTTP request to complete, aborting: " + requestMessage);
                return null;
            }
            if (StreamClientImpl.log.isLoggable(Level.FINE)) {
                StreamClientImpl.log.fine("Exception occurred, trying to read the error stream: " + Exceptions.unwrap(ex2));
            }
            try {
                final InputStream inputStream = urlConnection.getErrorStream();
                return this.createResponse(urlConnection, inputStream);
            }
            catch (Exception errorEx) {
                if (StreamClientImpl.log.isLoggable(Level.FINE)) {
                    StreamClientImpl.log.fine("Could not read error stream: " + errorEx);
                }
                return null;
            }
        }
        catch (Exception ex3) {
            StreamClientImpl.log.log(Level.WARNING, "HTTP request failed: " + requestMessage, Exceptions.unwrap(ex3));
            return null;
        }
        finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
    }
    
    @Override
    public void stop() {
    }
    
    protected void applyRequestProperties(final HttpURLConnection urlConnection, final StreamRequestMessage requestMessage) {
        urlConnection.setInstanceFollowRedirects(false);
        if (!requestMessage.getHeaders().containsKey(UpnpHeader.Type.USER_AGENT)) {
            urlConnection.setRequestProperty(UpnpHeader.Type.USER_AGENT.getHttpName(), this.getConfiguration().getUserAgentValue(requestMessage.getUdaMajorVersion(), requestMessage.getUdaMinorVersion()));
        }
        this.applyHeaders(urlConnection, requestMessage.getHeaders());
    }
    
    protected void applyHeaders(final HttpURLConnection urlConnection, final Headers headers) {
        StreamClientImpl.log.fine("Writing headers on HttpURLConnection: " + headers.size());
        for (final Map.Entry<String, List<String>> entry : headers.entrySet()) {
            for (final String v : entry.getValue()) {
                final String headerName = entry.getKey();
                StreamClientImpl.log.fine("Setting header '" + headerName + "': " + v);
                urlConnection.setRequestProperty(headerName, v);
            }
        }
    }
    
    protected void applyRequestBody(final HttpURLConnection urlConnection, final StreamRequestMessage requestMessage) throws IOException {
        if (requestMessage.hasBody()) {
            urlConnection.setDoOutput(true);
            if (requestMessage.getBodyType().equals(UpnpMessage.BodyType.STRING)) {
                IO.writeUTF8(urlConnection.getOutputStream(), requestMessage.getBodyString());
            }
            else if (requestMessage.getBodyType().equals(UpnpMessage.BodyType.BYTES)) {
                IO.writeBytes(urlConnection.getOutputStream(), requestMessage.getBodyBytes());
            }
            urlConnection.getOutputStream().flush();
            return;
        }
        urlConnection.setDoOutput(false);
    }
    
    protected StreamResponseMessage createResponse(final HttpURLConnection urlConnection, final InputStream inputStream) throws Exception {
        if (urlConnection.getResponseCode() == -1) {
            StreamClientImpl.log.warning("Received an invalid HTTP response: " + urlConnection.getURL());
            StreamClientImpl.log.warning("Is your Cling-based server sending connection heartbeats with RemoteClientInfo#isRequestCancelled? This client can't handle heartbeats, read the manual.");
            return null;
        }
        final UpnpResponse responseOperation = new UpnpResponse(urlConnection.getResponseCode(), urlConnection.getResponseMessage());
        StreamClientImpl.log.fine("Received response: " + responseOperation);
        final StreamResponseMessage responseMessage = new StreamResponseMessage(responseOperation);
        responseMessage.setHeaders(new UpnpHeaders(urlConnection.getHeaderFields()));
        byte[] bodyBytes = null;
        InputStream is = null;
        try {
            is = inputStream;
            if (inputStream != null) {
                bodyBytes = IO.readBytes(is);
            }
        }
        finally {
            if (is != null) {
                is.close();
            }
        }
        if (bodyBytes != null && bodyBytes.length > 0 && responseMessage.isContentTypeMissingOrText()) {
            StreamClientImpl.log.fine("Response contains textual entity body, converting then setting string on message");
            responseMessage.setBodyCharacters(bodyBytes);
        }
        else if (bodyBytes != null && bodyBytes.length > 0) {
            StreamClientImpl.log.fine("Response contains binary entity body, setting bytes on message");
            responseMessage.setBody(UpnpMessage.BodyType.BYTES, bodyBytes);
        }
        else {
            StreamClientImpl.log.fine("Response did not contain entity body");
        }
        StreamClientImpl.log.fine("Response message complete: " + responseMessage);
        return responseMessage;
    }
    
    static {
        log = Logger.getLogger(StreamClient.class.getName());
    }
}
