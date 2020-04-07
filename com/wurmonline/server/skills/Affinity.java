// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.skills;

public class Affinity
{
    public int skillNumber;
    public int number;
    
    public Affinity(final int skillnum, final int _number) {
        this.number = _number;
        this.skillNumber = skillnum;
    }
    
    public int getSkillNumber() {
        return this.skillNumber;
    }
    
    public void setSkillNumber(final int aSkillNumber) {
        this.skillNumber = aSkillNumber;
    }
    
    public int getNumber() {
        return this.number;
    }
    
    public void setNumber(final int aNumber) {
        this.number = aNumber;
    }
}
