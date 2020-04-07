// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.xml.xsom.impl;

import java.util.Comparator;

public final class UName
{
    private final String nsUri;
    private final String localName;
    private final String qname;
    public static final Comparator comparator;
    
    public UName(final String _nsUri, final String _localName, final String _qname) {
        4;
        if (_qname == null) {
            throw new NullPointerException(_nsUri + " " + _localName + " " + _qname);
        }
        this.nsUri = _nsUri.intern();
        this.localName = _localName.intern();
        this.qname = _qname.intern();
    }
    
    public UName(final String nsUri, final String localName) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: nop            
        //     1: iconst_m1      
        //     2: nop            
        //     3: iload_2         /* localName */
        //     4: nop            
        //     5: nop            
        //     6: nop            
        //     7: lconst_1       
        //    LocalVariableTable:
        //  Start  Length  Slot  Name       Signature
        //  -----  ------  ----  ---------  -----------------------------
        //  0      8       0     this       Lcom/sun/xml/xsom/impl/UName;
        //  0      8       1     nsUri      Ljava/lang/String;
        //  0      8       2     localName  Ljava/lang/String;
        // 
        // The error that occurred was:
        // 
        // java.lang.NullPointerException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public String getName() {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: iload_2        
        //     1: nop            
        //     2: nop            
        //     3: nop            
        //     4: iconst_3       
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  -----------------------------
        //  0      5       0     this  Lcom/sun/xml/xsom/impl/UName;
        // 
        // The error that occurred was:
        // 
        // java.lang.NullPointerException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public String getNamespaceURI() {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: iload_2        
        //     1: nop            
        //     2: nop            
        //     3: nop            
        //     4: iconst_3       
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  -----------------------------
        //  0      5       0     this  Lcom/sun/xml/xsom/impl/UName;
        // 
        // The error that occurred was:
        // 
        // java.lang.NullPointerException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public String getQualifiedName() {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: iload_2        
        //     1: nop            
        //     2: nop            
        //     3: nop            
        //     4: iconst_3       
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  -----------------------------
        //  0      5       0     this  Lcom/sun/xml/xsom/impl/UName;
        // 
        // The error that occurred was:
        // 
        // java.lang.NullPointerException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    static {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: aconst_null    
        //     1: nop            
        //     2: nop            
        //     3: nop            
        //     4: dload_1        
        //     5: nop            
        //     6: iconst_m1      
        //     7: nop            
        //     8: iaload         
        //     9: nop            
        //    10: nop            
        // 
        // The error that occurred was:
        // 
        // java.lang.NullPointerException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
}
