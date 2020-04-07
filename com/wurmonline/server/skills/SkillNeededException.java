// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.skills;

import com.wurmonline.shared.exceptions.WurmServerException;

public class SkillNeededException extends WurmServerException
{
    private static final long serialVersionUID = 928122916198689152L;
    
    public SkillNeededException(final String message) {
        super(message);
    }
    
    public SkillNeededException(final Throwable cause) {
        super(cause);
    }
    
    public SkillNeededException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
