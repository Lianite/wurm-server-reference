// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.support.model;

import org.fourthline.cling.model.types.UnsignedIntegerFourBytes;

public class Connection
{
    public static class StatusInfo
    {
        private Status status;
        private long uptimeSeconds;
        private Error lastError;
        
        public StatusInfo(final Status status, final UnsignedIntegerFourBytes uptime, final Error lastError) {
            this(status, uptime.getValue(), lastError);
        }
        
        public StatusInfo(final Status status, final long uptimeSeconds, final Error lastError) {
            this.status = status;
            this.uptimeSeconds = uptimeSeconds;
            this.lastError = lastError;
        }
        
        public Status getStatus() {
            return this.status;
        }
        
        public long getUptimeSeconds() {
            return this.uptimeSeconds;
        }
        
        public UnsignedIntegerFourBytes getUptime() {
            return new UnsignedIntegerFourBytes(this.getUptimeSeconds());
        }
        
        public Error getLastError() {
            return this.lastError;
        }
        
        @Override
        public boolean equals(final Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || this.getClass() != o.getClass()) {
                return false;
            }
            final StatusInfo that = (StatusInfo)o;
            return this.uptimeSeconds == that.uptimeSeconds && this.lastError == that.lastError && this.status == that.status;
        }
        
        @Override
        public int hashCode() {
            int result = this.status.hashCode();
            result = 31 * result + (int)(this.uptimeSeconds ^ this.uptimeSeconds >>> 32);
            result = 31 * result + this.lastError.hashCode();
            return result;
        }
        
        @Override
        public String toString() {
            return "(" + this.getClass().getSimpleName() + ") " + this.getStatus();
        }
    }
    
    public enum Type
    {
        Unconfigured, 
        IP_Routed, 
        IP_Bridged;
    }
    
    public enum Status
    {
        Unconfigured, 
        Connecting, 
        Connected, 
        PendingDisconnect, 
        Disconnecting, 
        Disconnected;
    }
    
    public enum Error
    {
        ERROR_NONE, 
        ERROR_COMMAND_ABORTED, 
        ERROR_NOT_ENABLED_FOR_INTERNET, 
        ERROR_USER_DISCONNECT, 
        ERROR_ISP_DISCONNECT, 
        ERROR_IDLE_DISCONNECT, 
        ERROR_FORCED_DISCONNECT, 
        ERROR_NO_CARRIER, 
        ERROR_IP_CONFIGURATION, 
        ERROR_UNKNOWN;
    }
}
