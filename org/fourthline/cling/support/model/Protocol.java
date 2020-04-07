// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.support.model;

import java.util.logging.Logger;

public enum Protocol
{
    ALL("*"), 
    HTTP_GET("http-get"), 
    RTSP_RTP_UDP("rtsp-rtp-udp"), 
    INTERNAL("internal"), 
    IEC61883("iec61883"), 
    XBMC_GET("xbmc-get"), 
    OTHER("other");
    
    private static final Logger LOG;
    private String protocolString;
    
    private Protocol(final String protocolString) {
        this.protocolString = protocolString;
    }
    
    @Override
    public String toString() {
        return this.protocolString;
    }
    
    public static Protocol value(final String s) {
        for (final Protocol protocol : values()) {
            if (protocol.toString().equals(s)) {
                return protocol;
            }
        }
        Protocol.LOG.info("Unsupported OTHER protocol string: " + s);
        return Protocol.OTHER;
    }
    
    static {
        LOG = Logger.getLogger(Protocol.class.getName());
    }
}
