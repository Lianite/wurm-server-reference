// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.support.model;

import org.fourthline.cling.model.action.ActionArgumentValue;
import java.util.Map;

public class TransportInfo
{
    private TransportState currentTransportState;
    private TransportStatus currentTransportStatus;
    private String currentSpeed;
    
    public TransportInfo() {
        this.currentTransportState = TransportState.NO_MEDIA_PRESENT;
        this.currentTransportStatus = TransportStatus.OK;
        this.currentSpeed = "1";
    }
    
    public TransportInfo(final Map<String, ActionArgumentValue> args) {
        this(TransportState.valueOrCustomOf((String)args.get("CurrentTransportState").getValue()), TransportStatus.valueOrCustomOf((String)args.get("CurrentTransportStatus").getValue()), (String)args.get("CurrentSpeed").getValue());
    }
    
    public TransportInfo(final TransportState currentTransportState) {
        this.currentTransportState = TransportState.NO_MEDIA_PRESENT;
        this.currentTransportStatus = TransportStatus.OK;
        this.currentSpeed = "1";
        this.currentTransportState = currentTransportState;
    }
    
    public TransportInfo(final TransportState currentTransportState, final String currentSpeed) {
        this.currentTransportState = TransportState.NO_MEDIA_PRESENT;
        this.currentTransportStatus = TransportStatus.OK;
        this.currentSpeed = "1";
        this.currentTransportState = currentTransportState;
        this.currentSpeed = currentSpeed;
    }
    
    public TransportInfo(final TransportState currentTransportState, final TransportStatus currentTransportStatus) {
        this.currentTransportState = TransportState.NO_MEDIA_PRESENT;
        this.currentTransportStatus = TransportStatus.OK;
        this.currentSpeed = "1";
        this.currentTransportState = currentTransportState;
        this.currentTransportStatus = currentTransportStatus;
    }
    
    public TransportInfo(final TransportState currentTransportState, final TransportStatus currentTransportStatus, final String currentSpeed) {
        this.currentTransportState = TransportState.NO_MEDIA_PRESENT;
        this.currentTransportStatus = TransportStatus.OK;
        this.currentSpeed = "1";
        this.currentTransportState = currentTransportState;
        this.currentTransportStatus = currentTransportStatus;
        this.currentSpeed = currentSpeed;
    }
    
    public TransportState getCurrentTransportState() {
        return this.currentTransportState;
    }
    
    public TransportStatus getCurrentTransportStatus() {
        return this.currentTransportStatus;
    }
    
    public String getCurrentSpeed() {
        return this.currentSpeed;
    }
}
