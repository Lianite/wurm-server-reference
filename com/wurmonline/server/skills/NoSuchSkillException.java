// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.skills;

import com.wurmonline.shared.exceptions.WurmServerException;

public class NoSuchSkillException extends WurmServerException
{
    private static final long serialVersionUID = 534621721301818809L;
    
    public NoSuchSkillException(final String message) {
        super(message);
    }
    
    public NoSuchSkillException(final Throwable cause) {
        super(cause);
    }
    
    public NoSuchSkillException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
