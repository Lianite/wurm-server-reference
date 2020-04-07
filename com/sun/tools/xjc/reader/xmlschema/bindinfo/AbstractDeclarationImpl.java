// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.reader.xmlschema.bindinfo;

import com.sun.tools.xjc.reader.xmlschema.BGMBuilder;
import com.sun.xml.xsom.XSComponent;
import org.xml.sax.Locator;

abstract class AbstractDeclarationImpl implements BIDeclaration
{
    private final Locator loc;
    protected BindInfo parent;
    private boolean isAcknowledged;
    
    protected AbstractDeclarationImpl(final Locator _loc) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: dadd           
        //     1: ddiv           
        //     2: isub           
        //     3: lsub           
        //     4: ldiv           
        //     5: ddiv           
        //     6: isub           
        //     7: lsub           
        //     8: idiv           
        //     9: laload         
        //    10: dstore_3       
        //    11: fstore_0        /* this */
        //    12: ddiv           
        //    13: isub           
        //    14: lsub           
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  ---------------------------------------------------------------------
        //  0      15      0     this  Lcom/sun/tools/xjc/reader/xmlschema/bindinfo/AbstractDeclarationImpl;
        //  0      15      1     _loc  Lorg/xml/sax/Locator;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public Locator getLocation() {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: dadd           
        //     1: idiv           
        //     2: ladd           
        //     3: frem           
        //     4: ladd           
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  ---------------------------------------------------------------------
        //  0      5       0     this  Lcom/sun/tools/xjc/reader/xmlschema/bindinfo/AbstractDeclarationImpl;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public void setParent(final BindInfo p) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: ldiv           
        //     1: idiv           
        //     2: drem           
        //     3: dadd           
        //     4: imul           
        //     5: lsub           
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  ---------------------------------------------------------------------
        //  0      6       0     this  Lcom/sun/tools/xjc/reader/xmlschema/bindinfo/AbstractDeclarationImpl;
        //  0      6       1     p     Lcom/sun/tools/xjc/reader/xmlschema/bindinfo/BindInfo;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    protected final XSComponent getOwner() {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: isub           
        //     1: lmul           
        //     2: fdiv           
        //     3: fsub           
        //     4: ddiv           
        //     5: laload         
        //     6: lstore_3       
        //     7: lmul           
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  ---------------------------------------------------------------------
        //  0      8       0     this  Lcom/sun/tools/xjc/reader/xmlschema/bindinfo/AbstractDeclarationImpl;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    protected final BGMBuilder getBuilder() {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: fdiv           
        //     1: dsub           
        //     2: laload         
        //     3: fstore_0        /* this */
        //     4: idiv           
        //     5: ladd           
        //     6: drem           
        //     7: drem           
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  ---------------------------------------------------------------------
        //  0      8       0     this  Lcom/sun/tools/xjc/reader/xmlschema/bindinfo/AbstractDeclarationImpl;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public final boolean isAcknowledged() {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: aconst_null    
        //     1: nop            
        //     2: ldc             "b"
        //     4: dload_3        
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  ---------------------------------------------------------------------
        //  0      5       0     this  Lcom/sun/tools/xjc/reader/xmlschema/bindinfo/AbstractDeclarationImpl;
        // 
        // The error that occurred was:
        // 
        // java.lang.NullPointerException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public void markAsAcknowledged() {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: nop            
        //     1: iconst_m1      
        //     2: nop            
        //     3: ldc_w           "Code"
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  ---------------------------------------------------------------------
        //  0      6       0     this  Lcom/sun/tools/xjc/reader/xmlschema/bindinfo/AbstractDeclarationImpl;
        // 
        // The error that occurred was:
        // 
        // java.lang.NullPointerException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    protected static final void _assert(final boolean b) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: aastore        
        //     1: nop            
        //     2: lconst_0       
        //     3: nop            
        //     4: istore_0        /* b */
        //     5: nop            
        //     6: dconst_0       
        //     7: nop            
        //     8: istore_1       
        //     9: nop            
        //    10: aload           b
        //    12: nop            
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  ---------
        //  0      13      0     b     Z
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
}
