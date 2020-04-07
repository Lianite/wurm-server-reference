// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.support.model.dlna.message.header;

import java.util.HashMap;
import java.util.Map;
import org.seamless.util.Exceptions;
import java.util.logging.Level;
import org.fourthline.cling.model.message.header.InvalidHeaderException;
import java.util.logging.Logger;
import org.fourthline.cling.model.message.header.UpnpHeader;

public abstract class DLNAHeader<T> extends UpnpHeader<T>
{
    private static final Logger log;
    
    public static DLNAHeader newInstance(final Type type, final String headerValue) {
        DLNAHeader upnpHeader = null;
        for (int i = 0; i < type.getHeaderTypes().length && upnpHeader == null; ++i) {
            final Class<? extends DLNAHeader> headerClass = type.getHeaderTypes()[i];
            try {
                DLNAHeader.log.finest("Trying to parse '" + type + "' with class: " + headerClass.getSimpleName());
                upnpHeader = (DLNAHeader)headerClass.newInstance();
                if (headerValue != null) {
                    upnpHeader.setString(headerValue);
                }
            }
            catch (InvalidHeaderException ex) {
                DLNAHeader.log.finest("Invalid header value for tested type: " + headerClass.getSimpleName() + " - " + ex.getMessage());
                upnpHeader = null;
            }
            catch (Exception ex2) {
                DLNAHeader.log.severe("Error instantiating header of type '" + type + "' with value: " + headerValue);
                DLNAHeader.log.log(Level.SEVERE, "Exception root cause: ", Exceptions.unwrap(ex2));
            }
        }
        return upnpHeader;
    }
    
    static {
        log = Logger.getLogger(DLNAHeader.class.getName());
    }
    
    public enum Type
    {
        TimeSeekRange("TimeSeekRange.dlna.org", (Class<? extends DLNAHeader>[])new Class[] { TimeSeekRangeHeader.class }), 
        XSeekRange("X-Seek-Range", (Class<? extends DLNAHeader>[])new Class[] { TimeSeekRangeHeader.class }), 
        PlaySpeed("PlaySpeed.dlna.org", (Class<? extends DLNAHeader>[])new Class[] { PlaySpeedHeader.class }), 
        AvailableSeekRange("availableSeekRange.dlna.org", (Class<? extends DLNAHeader>[])new Class[] { AvailableSeekRangeHeader.class }), 
        GetAvailableSeekRange("getAvailableSeekRange.dlna.org", (Class<? extends DLNAHeader>[])new Class[] { GetAvailableSeekRangeHeader.class }), 
        GetContentFeatures("getcontentFeatures.dlna.org", (Class<? extends DLNAHeader>[])new Class[] { GetContentFeaturesHeader.class }), 
        ContentFeatures("contentFeatures.dlna.org", (Class<? extends DLNAHeader>[])new Class[] { ContentFeaturesHeader.class }), 
        TransferMode("transferMode.dlna.org", (Class<? extends DLNAHeader>[])new Class[] { TransferModeHeader.class }), 
        FriendlyName("friendlyName.dlna.org", (Class<? extends DLNAHeader>[])new Class[] { FriendlyNameHeader.class }), 
        PeerManager("peerManager.dlna.org", (Class<? extends DLNAHeader>[])new Class[] { PeerManagerHeader.class }), 
        AvailableRange("Available-Range.dlna.org", (Class<? extends DLNAHeader>[])new Class[] { AvailableRangeHeader.class }), 
        SCID("scid.dlna.org", (Class<? extends DLNAHeader>[])new Class[] { SCIDHeader.class }), 
        RealTimeInfo("realTimeInfo.dlna.org", (Class<? extends DLNAHeader>[])new Class[] { RealTimeInfoHeader.class }), 
        ScmsFlag("scmsFlag.dlna.org", (Class<? extends DLNAHeader>[])new Class[] { ScmsFlagHeader.class }), 
        WCT("WCT.dlna.org", (Class<? extends DLNAHeader>[])new Class[] { WCTHeader.class }), 
        MaxPrate("Max-Prate.dlna.org", (Class<? extends DLNAHeader>[])new Class[] { MaxPrateHeader.class }), 
        EventType("Event-Type.dlna.org", (Class<? extends DLNAHeader>[])new Class[] { EventTypeHeader.class }), 
        Supported("Supported", (Class<? extends DLNAHeader>[])new Class[] { SupportedHeader.class }), 
        BufferInfo("Buffer-Info.dlna.org", (Class<? extends DLNAHeader>[])new Class[] { BufferInfoHeader.class }), 
        RTPH264DeInterleaving("rtp-h264-deint-buf-cap.dlna.org", (Class<? extends DLNAHeader>[])new Class[] { BufferBytesHeader.class }), 
        RTPAACDeInterleaving("rtp-aac-deint-buf-cap.dlna.org", (Class<? extends DLNAHeader>[])new Class[] { BufferBytesHeader.class }), 
        RTPAMRDeInterleaving("rtp-amr-deint-buf-cap.dlna.org", (Class<? extends DLNAHeader>[])new Class[] { BufferBytesHeader.class }), 
        RTPAMRWBPlusDeInterleaving("rtp-amrwbplus-deint-buf-cap.dlna.org", (Class<? extends DLNAHeader>[])new Class[] { BufferBytesHeader.class }), 
        PRAGMA("PRAGMA", (Class<? extends DLNAHeader>[])new Class[] { PragmaHeader.class });
        
        private static Map<String, Type> byName;
        private String httpName;
        private Class<? extends DLNAHeader>[] headerTypes;
        
        private Type(final String httpName, final Class<? extends DLNAHeader>[] headerClass) {
            this.httpName = httpName;
            this.headerTypes = headerClass;
        }
        
        public String getHttpName() {
            return this.httpName;
        }
        
        public Class<? extends DLNAHeader>[] getHeaderTypes() {
            return this.headerTypes;
        }
        
        public boolean isValidHeaderType(final Class<? extends DLNAHeader> clazz) {
            for (final Class<? extends DLNAHeader> permissibleType : this.getHeaderTypes()) {
                if (permissibleType.isAssignableFrom(clazz)) {
                    return true;
                }
            }
            return false;
        }
        
        public static Type getByHttpName(final String httpName) {
            if (httpName == null) {
                return null;
            }
            return Type.byName.get(httpName);
        }
        
        static {
            Type.byName = new HashMap<String, Type>() {
                {
                    for (final Type t : Type.values()) {
                        this.put(t.getHttpName(), t);
                    }
                }
            };
        }
    }
}
