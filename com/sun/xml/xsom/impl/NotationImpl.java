// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.xml.xsom.impl;

import com.sun.xml.xsom.visitor.XSFunction;
import com.sun.xml.xsom.visitor.XSVisitor;
import org.xml.sax.Locator;
import com.sun.xml.xsom.XSNotation;

public class NotationImpl extends DeclarationImpl implements XSNotation
{
    private final String publicId;
    private final String systemId;
    
    public NotationImpl(final SchemaImpl owner, final AnnotationImpl _annon, final Locator _loc, final String _tns, final String _name, final String _publicId, final String _systemId) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: dsub           
        //     1: istore_0        /* this */
        //     2: astore_1        /* owner */
        //     3: fmul           
        //     4: ladd           
        //     5: fneg           
        //     6: ladd           
        //     7: laload         
        //     8: idiv           
        //     9: ladd           
        //    10: fdiv           
        //    11: dsub           
        //    12: laload         
        //    13: aastore        
        //    14: ineg           
        //    15: frem           
        //    16: lmul           
        //    17: fdiv           
        //    18: dsub           
        //    19: istore_0        /* this */
        //    20: dup_x1         
        //    21: dload_3         /* _loc */
        //    22: sastore        
        //    23: aconst_null    
        //    24: nop            
        //    LocalVariableTable:
        //  Start  Length  Slot  Name       Signature
        //  -----  ------  ----  ---------  --------------------------------------
        //  0      25      0     this       Lcom/sun/xml/xsom/impl/NotationImpl;
        //  0      25      1     owner      Lcom/sun/xml/xsom/impl/SchemaImpl;
        //  0      25      2     _annon     Lcom/sun/xml/xsom/impl/AnnotationImpl;
        //  0      25      3     _loc       Lorg/xml/sax/Locator;
        //  0      25      4     _tns       Ljava/lang/String;
        //  0      25      5     _name      Ljava/lang/String;
        //  0      25      6     _publicId  Ljava/lang/String;
        //  0      25      7     _systemId  Ljava/lang/String;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public String getPublicId() {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: ddiv           
        //     1: ldiv           
        //     2: laload         
        //     3: pop2           
        //     4: aastore        
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  ------------------------------------
        //  0      5       0     this  Lcom/sun/xml/xsom/impl/NotationImpl;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public String getSystemId() {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: nop            
        //     1: iconst_2       
        //     2: nop            
        //     3: aconst_null    
        //     4: nop            
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  ------------------------------------
        //  0      5       0     this  Lcom/sun/xml/xsom/impl/NotationImpl;
        // 
        // The error that occurred was:
        // 
        // java.lang.NullPointerException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public void visit(final XSVisitor visitor) {
        // 
        // This method could not be decompiled.
        // 
        // Could not show original bytecode, likely due to the same error.
        // 
        // The error that occurred was:
        // 
        // com.strobel.assembler.metadata.MethodBodyParseException: An error occurred while parsing the bytecode of method 'com/sun/xml/xsom/impl/NotationImpl.visit:(Lcom/sun/xml/xsom/visitor/XSVisitor;)V'.
        //     at com.strobel.assembler.metadata.MethodReader.readBody(MethodReader.java:65)
        //     at com.strobel.assembler.metadata.MethodDefinition.tryLoadBody(MethodDefinition.java:722)
        //     at com.strobel.assembler.metadata.MethodDefinition.getBody(MethodDefinition.java:83)
        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:202)
        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:99)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethodBody(AstBuilder.java:757)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethod(AstBuilder.java:655)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addTypeMembers(AstBuilder.java:532)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeCore(AstBuilder.java:499)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeNoCache(AstBuilder.java:141)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createType(AstBuilder.java:130)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addType(AstBuilder.java:105)
        //     at com.strobel.decompiler.languages.java.JavaLanguage.buildAst(JavaLanguage.java:71)
        //     at com.strobel.decompiler.languages.java.JavaLanguage.decompileType(JavaLanguage.java:59)
        //     at com.strobel.decompiler.DecompilerDriver.decompileType(DecompilerDriver.java:317)
        //     at com.strobel.decompiler.DecompilerDriver.decompileJar(DecompilerDriver.java:238)
        //     at com.strobel.decompiler.DecompilerDriver.main(DecompilerDriver.java:123)
        // Caused by: java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public Object apply(final XSFunction function) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: nop            
        //     1: nop            
        //     2: nop            
        //     3: iload_3        
        //     4: nop            
        //     5: iload_2        
        //     6: nop            
        //     7: fconst_0       
        //    LocalVariableTable:
        //  Start  Length  Slot  Name      Signature
        //  -----  ------  ----  --------  -------------------------------------
        //  0      8       0     this      Lcom/sun/xml/xsom/impl/NotationImpl;
        //  0      8       1     function  Lcom/sun/xml/xsom/visitor/XSFunction;
        // 
        // The error that occurred was:
        // 
        // java.lang.NullPointerException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
}
