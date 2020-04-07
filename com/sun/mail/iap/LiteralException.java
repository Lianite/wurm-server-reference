// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.mail.iap;

public class LiteralException extends ProtocolException
{
    private static final long serialVersionUID = -6919179828339609913L;
    
    public LiteralException(final Response r) {
        super(r.toString());
        this.response = r;
    }
}
