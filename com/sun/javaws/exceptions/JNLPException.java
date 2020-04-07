// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.javaws.exceptions;

import com.sun.javaws.jnl.LaunchDesc;

public abstract class JNLPException extends Exception
{
    private static LaunchDesc _defaultLaunchDesc;
    private LaunchDesc _exceptionLaunchDesc;
    private String _categoryMsg;
    private Throwable _wrappedException;
    
    public JNLPException(final String s) {
        this(s, null, null);
    }
    
    public JNLPException(final String s, final LaunchDesc launchDesc) {
        this(s, launchDesc, null);
    }
    
    public JNLPException(final String s, final Throwable t) {
        this(s, null, t);
    }
    
    public JNLPException(final String categoryMsg, final LaunchDesc exceptionLaunchDesc, final Throwable wrappedException) {
        this._exceptionLaunchDesc = null;
        this._categoryMsg = null;
        this._wrappedException = null;
        this._categoryMsg = categoryMsg;
        this._exceptionLaunchDesc = exceptionLaunchDesc;
        this._wrappedException = wrappedException;
    }
    
    public static void setDefaultLaunchDesc(final LaunchDesc defaultLaunchDesc) {
        JNLPException._defaultLaunchDesc = defaultLaunchDesc;
    }
    
    public static LaunchDesc getDefaultLaunchDesc() {
        return JNLPException._defaultLaunchDesc;
    }
    
    public String getMessage() {
        return this.getRealMessage();
    }
    
    public String getBriefMessage() {
        return null;
    }
    
    protected abstract String getRealMessage();
    
    public LaunchDesc getLaunchDesc() {
        return (this._exceptionLaunchDesc != null) ? this._exceptionLaunchDesc : JNLPException._defaultLaunchDesc;
    }
    
    public String getLaunchDescSource() {
        final LaunchDesc launchDesc = this.getLaunchDesc();
        if (launchDesc == null) {
            return null;
        }
        return launchDesc.getSource();
    }
    
    public String getCategory() {
        return this._categoryMsg;
    }
    
    public Throwable getWrappedException() {
        return this._wrappedException;
    }
    
    public String toString() {
        return "JNLPException[category: " + this._categoryMsg + " : Exception: " + this._wrappedException + " : LaunchDesc: " + this._exceptionLaunchDesc + " ]";
    }
    
    static {
        JNLPException._defaultLaunchDesc = null;
    }
}
