// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.dtdparser;

final class InternalEntity extends EntityDecl
{
    char[] buf;
    
    InternalEntity(final String name, final char[] value) {
        this.name = name;
        this.buf = value;
    }
}
