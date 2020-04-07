// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.website;

public class LoginInfo
{
    private String name;
    
    public LoginInfo(final String aName) {
        this.name = aName;
    }
    
    public String getName() {
        return this.name;
    }
    
    public boolean isAdmin() {
        return this.name.equals("admin");
    }
}
