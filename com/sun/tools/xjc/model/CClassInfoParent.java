// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.model;

import com.sun.codemodel.JPackage;

public interface CClassInfoParent
{
    String fullName();
    
     <T> T accept(final Visitor<T> p0);
    
    JPackage getOwnerPackage();
    
    public static final class Package implements CClassInfoParent
    {
        public final JPackage pkg;
        
        public Package(final JPackage pkg) {
            this.pkg = pkg;
        }
        
        public String fullName() {
            return this.pkg.name();
        }
        
        public <T> T accept(final Visitor<T> visitor) {
            return visitor.onPackage(this.pkg);
        }
        
        public JPackage getOwnerPackage() {
            return this.pkg;
        }
    }
    
    public interface Visitor<T>
    {
        T onBean(final CClassInfo p0);
        
        T onPackage(final JPackage p0);
        
        T onElement(final CElementInfo p0);
    }
}
