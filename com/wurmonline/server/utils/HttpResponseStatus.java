// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.utils;

public enum HttpResponseStatus
{
    OK(200, "OK"), 
    CREATED(201, "Created"), 
    ACCEPTED(202, "Accepted"), 
    NO_CONTENT(204, "No Content"), 
    MOVED_PERMANENTLY(301, "Moved Permanently"), 
    SEE_OTHER(303, "See Other"), 
    NOT_MODIFIED(304, "Not Modified"), 
    TEMPORARY_REDIRECT(307, "Temporary Redirect"), 
    BAD_REQUEST(400, "Bad Request"), 
    UNAUTHORIZED(401, "Unauthorized"), 
    FORBIDDEN(403, "Forbidden"), 
    NOT_FOUND(404, "Not Found"), 
    NOT_ACCEPTABLE(406, "Not Acceptable"), 
    CONFLICT(409, "Conflict"), 
    GONE(410, "Gone"), 
    PRECONDITION_FAILED(412, "Precondition Failed"), 
    UNSUPPORTED_MEDIA_TYPE(415, "Unsupported Media Type"), 
    INTERNAL_SERVER_ERROR(500, "Internal Server Error"), 
    SERVICE_UNAVAILABLE(503, "Service Unavailable");
    
    private final int code;
    private final String reason;
    private Family family;
    
    private HttpResponseStatus(final int statusCode, final String reasonPhrase) {
        this.code = statusCode;
        this.reason = reasonPhrase;
        switch (this.code / 100) {
            case 1: {
                this.family = Family.INFORMATIONAL;
                break;
            }
            case 2: {
                this.family = Family.SUCCESSFUL;
                break;
            }
            case 3: {
                this.family = Family.REDIRECTION;
                break;
            }
            case 4: {
                this.family = Family.CLIENT_ERROR;
                break;
            }
            case 5: {
                this.family = Family.SERVER_ERROR;
                break;
            }
            default: {
                this.family = Family.OTHER;
                break;
            }
        }
    }
    
    public Family getFamily() {
        return this.family;
    }
    
    public int getStatusCode() {
        return this.code;
    }
    
    @Override
    public String toString() {
        return this.reason;
    }
    
    public static HttpResponseStatus fromStatusCode(final int statusCode) {
        for (final HttpResponseStatus s : values()) {
            if (s.code == statusCode) {
                return s;
            }
        }
        return null;
    }
    
    public enum Family
    {
        INFORMATIONAL, 
        SUCCESSFUL, 
        REDIRECTION, 
        CLIENT_ERROR, 
        SERVER_ERROR, 
        OTHER;
    }
}
