// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.javaws.exceptions;

import com.sun.deploy.resources.ResourceManager;
import java.net.URL;

public class ErrorCodeResponseException extends DownloadException
{
    private String _errorLine;
    private int _errorCode;
    private boolean _jreDownload;
    public static final int ERR_10_NO_RESOURCE = 10;
    public static final int ERR_11_NO_VERSION = 11;
    public static final int ERR_20_UNSUP_OS = 20;
    public static final int ERR_21_UNSUP_ARCH = 21;
    public static final int ERR_22_UNSUP_LOCALE = 22;
    public static final int ERR_23_UNSUP_JRE = 23;
    public static final int ERR_99_UNKNOWN = 99;
    
    public ErrorCodeResponseException(final URL url, final String s, final String errorLine) {
        super(url, s);
        this._errorLine = errorLine;
        this._jreDownload = false;
        this._errorCode = 99;
        if (this._errorLine != null) {
            try {
                final int index = this._errorLine.indexOf(32);
                if (index != -1) {
                    this._errorCode = Integer.parseInt(this._errorLine.substring(0, index));
                }
            }
            catch (NumberFormatException ex) {
                this._errorCode = 99;
            }
        }
    }
    
    public void setJreDownload(final boolean jreDownload) {
        this._jreDownload = jreDownload;
    }
    
    public int getErrorCode() {
        return this._errorCode;
    }
    
    public String getRealMessage() {
        final String s = this._jreDownload ? ResourceManager.getString("launch.error.noJre") : "";
        if (this._errorCode != 99) {
            return s + ResourceManager.getString("launch.error.errorcoderesponse-known", this.getResourceString(), this._errorCode, this._errorLine);
        }
        return s + ResourceManager.getString("launch.error.errorcoderesponse-unknown", this.getResourceString());
    }
}
