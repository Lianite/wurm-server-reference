// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.model.message;

public class UpnpResponse extends UpnpOperation
{
    private int statusCode;
    private String statusMessage;
    
    public UpnpResponse(final int statusCode, final String statusMessage) {
        this.statusCode = statusCode;
        this.statusMessage = statusMessage;
    }
    
    public UpnpResponse(final Status status) {
        this.statusCode = status.getStatusCode();
        this.statusMessage = status.getStatusMsg();
    }
    
    public int getStatusCode() {
        return this.statusCode;
    }
    
    public String getStatusMessage() {
        return this.statusMessage;
    }
    
    public boolean isFailed() {
        return this.statusCode >= 300;
    }
    
    public String getResponseDetails() {
        return this.getStatusCode() + " " + this.getStatusMessage();
    }
    
    @Override
    public String toString() {
        return this.getResponseDetails();
    }
    
    public enum Status
    {
        OK(200, "OK"), 
        BAD_REQUEST(400, "Bad Request"), 
        NOT_FOUND(404, "Not Found"), 
        METHOD_NOT_SUPPORTED(405, "Method Not Supported"), 
        PRECONDITION_FAILED(412, "Precondition Failed"), 
        UNSUPPORTED_MEDIA_TYPE(415, "Unsupported Media Type"), 
        INTERNAL_SERVER_ERROR(500, "Internal Server Error"), 
        NOT_IMPLEMENTED(501, "Not Implemented");
        
        private int statusCode;
        private String statusMsg;
        
        private Status(final int statusCode, final String statusMsg) {
            this.statusCode = statusCode;
            this.statusMsg = statusMsg;
        }
        
        public int getStatusCode() {
            return this.statusCode;
        }
        
        public String getStatusMsg() {
            return this.statusMsg;
        }
        
        public static Status getByStatusCode(final int statusCode) {
            for (final Status status : values()) {
                if (status.getStatusCode() == statusCode) {
                    return status;
                }
            }
            return null;
        }
    }
}
