// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.skills;

public final class SkillsFactory
{
    public static Skills createSkills(final long id) {
        return new DbSkills(id);
    }
    
    public static Skills createSkills(final String templateName) {
        return new DbSkills(templateName);
    }
}
