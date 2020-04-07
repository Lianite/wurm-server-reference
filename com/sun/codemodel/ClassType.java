// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.codemodel;

public final class ClassType
{
    final String declarationToken;
    public static final ClassType CLASS;
    public static final ClassType INTERFACE;
    public static final ClassType ANNOTATION_TYPE_DECL;
    public static final ClassType ENUM;
    
    private ClassType(final String token) {
        this.declarationToken = token;
    }
    
    static {
        CLASS = new ClassType("class");
        INTERFACE = new ClassType("interface");
        ANNOTATION_TYPE_DECL = new ClassType("@interface");
        ENUM = new ClassType("enum");
    }
}
