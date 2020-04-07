// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.runtime;

import com.sun.msv.verifier.DocumentDeclaration;

public interface ValidatableObject extends XMLSerializable
{
    DocumentDeclaration createRawValidator();
    
    Class getPrimaryInterface();
}
