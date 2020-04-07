// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.banks;

import com.wurmonline.shared.exceptions.WurmServerException;

public final class BankUnavailableException extends WurmServerException
{
    public static final String VERSION = "$Revision: 1.4 $";
    private static final long serialVersionUID = -5632991262062075642L;
    
    public BankUnavailableException(final String message) {
        super(message);
    }
    
    public BankUnavailableException(final Throwable cause) {
        super(cause);
    }
    
    public BankUnavailableException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
