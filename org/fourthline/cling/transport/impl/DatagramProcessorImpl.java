// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.transport.impl;

import java.io.ObjectInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Locale;
import org.fourthline.cling.model.message.UpnpHeaders;
import org.fourthline.cling.model.message.UpnpOperation;
import java.io.UnsupportedEncodingException;
import org.fourthline.cling.model.message.UpnpResponse;
import org.fourthline.cling.model.message.UpnpRequest;
import org.fourthline.cling.model.message.OutgoingDatagramMessage;
import org.fourthline.cling.model.UnsupportedDataException;
import org.seamless.http.Headers;
import java.io.ByteArrayInputStream;
import java.util.logging.Level;
import org.fourthline.cling.model.message.IncomingDatagramMessage;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.util.logging.Logger;
import org.fourthline.cling.transport.spi.DatagramProcessor;

public class DatagramProcessorImpl implements DatagramProcessor
{
    private static Logger log;
    
    @Override
    public IncomingDatagramMessage read(final InetAddress receivedOnAddress, final DatagramPacket datagram) throws UnsupportedDataException {
        try {
            if (DatagramProcessorImpl.log.isLoggable(Level.FINER)) {
                DatagramProcessorImpl.log.finer("===================================== DATAGRAM BEGIN ============================================");
                DatagramProcessorImpl.log.finer(new String(datagram.getData(), "UTF-8"));
                DatagramProcessorImpl.log.finer("-===================================== DATAGRAM END =============================================");
            }
            final ByteArrayInputStream is = new ByteArrayInputStream(datagram.getData());
            final String[] startLine = Headers.readLine(is).split(" ");
            if (startLine[0].startsWith("HTTP/1.")) {
                return this.readResponseMessage(receivedOnAddress, datagram, is, Integer.valueOf(startLine[1]), startLine[2], startLine[0]);
            }
            return this.readRequestMessage(receivedOnAddress, datagram, is, startLine[0], startLine[2]);
        }
        catch (Exception ex) {
            throw new UnsupportedDataException("Could not parse headers: " + ex, ex, datagram.getData());
        }
    }
    
    @Override
    public DatagramPacket write(final OutgoingDatagramMessage message) throws UnsupportedDataException {
        final StringBuilder statusLine = new StringBuilder();
        final UpnpOperation operation = message.getOperation();
        if (operation instanceof UpnpRequest) {
            final UpnpRequest requestOperation = (UpnpRequest)operation;
            statusLine.append(requestOperation.getHttpMethodName()).append(" * ");
            statusLine.append("HTTP/1.").append(operation.getHttpMinorVersion()).append("\r\n");
        }
        else {
            if (!(operation instanceof UpnpResponse)) {
                throw new UnsupportedDataException("Message operation is not request or response, don't know how to process: " + message);
            }
            final UpnpResponse responseOperation = (UpnpResponse)operation;
            statusLine.append("HTTP/1.").append(operation.getHttpMinorVersion()).append(" ");
            statusLine.append(responseOperation.getStatusCode()).append(" ").append(responseOperation.getStatusMessage());
            statusLine.append("\r\n");
        }
        final StringBuilder messageData = new StringBuilder();
        messageData.append((CharSequence)statusLine);
        messageData.append(message.getHeaders().toString()).append("\r\n");
        if (DatagramProcessorImpl.log.isLoggable(Level.FINER)) {
            DatagramProcessorImpl.log.finer("Writing message data for: " + message);
            DatagramProcessorImpl.log.finer("---------------------------------------------------------------------------------");
            DatagramProcessorImpl.log.finer(messageData.toString().substring(0, messageData.length() - 2));
            DatagramProcessorImpl.log.finer("---------------------------------------------------------------------------------");
        }
        try {
            final byte[] data = messageData.toString().getBytes("US-ASCII");
            DatagramProcessorImpl.log.fine("Writing new datagram packet with " + data.length + " bytes for: " + message);
            return new DatagramPacket(data, data.length, message.getDestinationAddress(), message.getDestinationPort());
        }
        catch (UnsupportedEncodingException ex) {
            throw new UnsupportedDataException("Can't convert message content to US-ASCII: " + ex.getMessage(), ex, messageData);
        }
    }
    
    protected IncomingDatagramMessage readRequestMessage(final InetAddress receivedOnAddress, final DatagramPacket datagram, final ByteArrayInputStream is, final String requestMethod, final String httpProtocol) throws Exception {
        final UpnpHeaders headers = new UpnpHeaders(is);
        final UpnpRequest upnpRequest = new UpnpRequest(UpnpRequest.Method.getByHttpName(requestMethod));
        upnpRequest.setHttpMinorVersion(httpProtocol.toUpperCase(Locale.ROOT).equals("HTTP/1.1") ? 1 : 0);
        final IncomingDatagramMessage requestMessage = new IncomingDatagramMessage((O)upnpRequest, datagram.getAddress(), datagram.getPort(), receivedOnAddress);
        requestMessage.setHeaders(headers);
        return requestMessage;
    }
    
    protected IncomingDatagramMessage readResponseMessage(final InetAddress receivedOnAddress, final DatagramPacket datagram, final ByteArrayInputStream is, final int statusCode, final String statusMessage, final String httpProtocol) throws Exception {
        final UpnpHeaders headers = new UpnpHeaders(is);
        final UpnpResponse upnpResponse = new UpnpResponse(statusCode, statusMessage);
        upnpResponse.setHttpMinorVersion(httpProtocol.toUpperCase(Locale.ROOT).equals("HTTP/1.1") ? 1 : 0);
        final IncomingDatagramMessage responseMessage = new IncomingDatagramMessage((O)upnpResponse, datagram.getAddress(), datagram.getPort(), receivedOnAddress);
        responseMessage.setHeaders(headers);
        return responseMessage;
    }
    
    static {
        DatagramProcessorImpl.log = Logger.getLogger(DatagramProcessor.class.getName());
    }
}
