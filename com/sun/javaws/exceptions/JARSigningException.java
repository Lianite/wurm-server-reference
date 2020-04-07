// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.javaws.exceptions;

import com.sun.deploy.resources.ResourceManager;
import com.sun.javaws.jnl.LaunchDesc;
import java.net.URL;

public class JARSigningException extends DownloadException
{
    private int _code;
    private String _missingEntry;
    public static final int MULTIPLE_CERTIFICATES = 0;
    public static final int MULTIPLE_SIGNERS = 1;
    public static final int BAD_SIGNING = 2;
    public static final int UNSIGNED_FILE = 3;
    public static final int MISSING_ENTRY = 4;
    
    public JARSigningException(final URL url, final String s, final int code) {
        super(null, url, s, null);
        this._code = code;
    }
    
    public JARSigningException(final URL url, final String s, final int code, final String missingEntry) {
        super(null, url, s, null);
        this._code = code;
        this._missingEntry = missingEntry;
    }
    
    public JARSigningException(final URL url, final String s, final int code, final Exception ex) {
        super(null, url, s, ex);
        this._code = code;
    }
    
    public String getRealMessage() {
        switch (this._code) {
            case 0: {
                return ResourceManager.getString("launch.error.jarsigning-multicerts", this.getResourceString());
            }
            case 1: {
                return ResourceManager.getString("launch.error.jarsigning-multisigners", this.getResourceString());
            }
            case 2: {
                return ResourceManager.getString("launch.error.jarsigning-badsigning", this.getResourceString());
            }
            case 3: {
                return ResourceManager.getString("launch.error.jarsigning-unsignedfile", this.getResourceString());
            }
            case 4: {
                return ResourceManager.getString("launch.error.jarsigning-missingentry", this.getResourceString()) + "\n" + ResourceManager.getString("launch.error.jarsigning-missingentryname", this._missingEntry);
            }
            default: {
                return "<error>";
            }
        }
    }
}
