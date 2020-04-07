// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.protocol.async;

import org.fourthline.cling.transport.RouterException;
import org.fourthline.cling.model.message.OutgoingDatagramMessage;
import org.fourthline.cling.model.message.discovery.OutgoingSearchRequest;
import org.fourthline.cling.model.message.header.MXHeader;
import org.fourthline.cling.model.message.header.STAllHeader;
import org.fourthline.cling.UpnpService;
import org.fourthline.cling.model.message.header.UpnpHeader;
import java.util.logging.Logger;
import org.fourthline.cling.protocol.SendingAsync;

public class SendingSearch extends SendingAsync
{
    private static final Logger log;
    private final UpnpHeader searchTarget;
    private final int mxSeconds;
    
    public SendingSearch(final UpnpService upnpService) {
        this(upnpService, new STAllHeader());
    }
    
    public SendingSearch(final UpnpService upnpService, final UpnpHeader searchTarget) {
        this(upnpService, searchTarget, MXHeader.DEFAULT_VALUE);
    }
    
    public SendingSearch(final UpnpService upnpService, final UpnpHeader searchTarget, final int mxSeconds) {
        super(upnpService);
        if (!UpnpHeader.Type.ST.isValidHeaderType(searchTarget.getClass())) {
            throw new IllegalArgumentException("Given search target instance is not a valid header class for type ST: " + searchTarget.getClass());
        }
        this.searchTarget = searchTarget;
        this.mxSeconds = mxSeconds;
    }
    
    public UpnpHeader getSearchTarget() {
        return this.searchTarget;
    }
    
    public int getMxSeconds() {
        return this.mxSeconds;
    }
    
    @Override
    protected void execute() throws RouterException {
        SendingSearch.log.fine("Executing search for target: " + this.searchTarget.getString() + " with MX seconds: " + this.getMxSeconds());
        final OutgoingSearchRequest msg = new OutgoingSearchRequest(this.searchTarget, this.getMxSeconds());
        this.prepareOutgoingSearchRequest(msg);
        for (int i = 0; i < this.getBulkRepeat(); ++i) {
            try {
                this.getUpnpService().getRouter().send(msg);
                SendingSearch.log.finer("Sleeping " + this.getBulkIntervalMilliseconds() + " milliseconds");
                Thread.sleep(this.getBulkIntervalMilliseconds());
            }
            catch (InterruptedException ex) {
                break;
            }
        }
    }
    
    public int getBulkRepeat() {
        return 5;
    }
    
    public int getBulkIntervalMilliseconds() {
        return 500;
    }
    
    protected void prepareOutgoingSearchRequest(final OutgoingSearchRequest message) {
    }
    
    static {
        log = Logger.getLogger(SendingSearch.class.getName());
    }
}
