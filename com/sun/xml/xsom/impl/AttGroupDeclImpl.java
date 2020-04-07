// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.xml.xsom.impl;

import com.sun.xml.xsom.visitor.XSFunction;
import com.sun.xml.xsom.visitor.XSVisitor;
import com.sun.xml.xsom.XSAttributeUse;
import com.sun.xml.xsom.XSWildcard;
import org.xml.sax.Locator;
import com.sun.xml.xsom.XSAttGroupDecl;

public class AttGroupDeclImpl extends AttributesHolder implements XSAttGroupDecl
{
    private WildcardImpl wildcard;
    
    public AttGroupDeclImpl(final SchemaImpl _parent, final AnnotationImpl _annon, final Locator _loc, final String _name, final WildcardImpl _wildcard) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: ladd           
        //     1: laload         
        //     2: lneg           
        //     3: ineg           
        //     4: lmul           
        //     5: idiv           
        //     6: laload         
        //     7: aastore        
        //     8: lsub           
        //     9: ineg           
        //    10: aconst_null    
        //    11: nop            
        //    12: iconst_5       
        //    13: lmul           
        //    14: ineg           
        //    15: lsub           
        //    LocalVariableTable:
        //  Start  Length  Slot  Name       Signature
        //  -----  ------  ----  ---------  ----------------------------------------
        //  0      16      0     this       Lcom/sun/xml/xsom/impl/AttGroupDeclImpl;
        //  0      16      1     _parent    Lcom/sun/xml/xsom/impl/SchemaImpl;
        //  0      16      2     _annon     Lcom/sun/xml/xsom/impl/AnnotationImpl;
        //  0      16      3     _loc       Lorg/xml/sax/Locator;
        //  0      16      4     _name      Ljava/lang/String;
        //  0      16      5     _wildcard  Lcom/sun/xml/xsom/impl/WildcardImpl;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public AttGroupDeclImpl(final SchemaImpl _parent, final AnnotationImpl _annon, final Locator _loc, final String _name) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: aastore        
        //     1: sastore        
        //     2: lmul           
        //     3: drem           
        //     4: lmul           
        //     5: ineg           
        //     6: ddiv           
        //     7: frem           
        //     8: aconst_null    
        //     9: nop            
        //    10: fconst_1       
        //    LocalVariableTable:
        //  Start  Length  Slot  Name     Signature
        //  -----  ------  ----  -------  ----------------------------------------
        //  0      11      0     this     Lcom/sun/xml/xsom/impl/AttGroupDeclImpl;
        //  0      11      1     _parent  Lcom/sun/xml/xsom/impl/SchemaImpl;
        //  0      11      2     _annon   Lcom/sun/xml/xsom/impl/AnnotationImpl;
        //  0      11      3     _loc     Lorg/xml/sax/Locator;
        //  0      11      4     _name    Ljava/lang/String;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public void setWildcard(final WildcardImpl wc) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: fdiv           
        //     1: laload         
        //     2: ishl           
        //     3: ldiv           
        //     4: idiv           
        //     5: laload         
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  ----------------------------------------
        //  0      6       0     this  Lcom/sun/xml/xsom/impl/AttGroupDeclImpl;
        //  0      6       1     wc    Lcom/sun/xml/xsom/impl/WildcardImpl;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public XSWildcard getAttributeWildcard() {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: ladd           
        //     1: fneg           
        //     2: ladd           
        //     3: laload         
        //     4: lneg           
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  ----------------------------------------
        //  0      5       0     this  Lcom/sun/xml/xsom/impl/AttGroupDeclImpl;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public XSAttributeUse getAttributeUse(final String nsURI, final String localName) {
        // 
        // This method could not be decompiled.
        // 
        // Could not show original bytecode, likely due to the same error.
        // 
        // The error that occurred was:
        // 
        // com.strobel.assembler.metadata.MethodBodyParseException: An error occurred while parsing the bytecode of method 'com/sun/xml/xsom/impl/AttGroupDeclImpl.getAttributeUse:(Ljava/lang/String;Ljava/lang/String;)Lcom/sun/xml/xsom/XSAttributeUse;'.
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
    
    public void redefine(final AttGroupDeclImpl ag) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: iconst_m1      
        //     1: nop            
        //     2: nop            
        //     3: nop            
        //     4: fconst_2       
        //     5: nop            
        //     6: dload_0         /* this */
        //     7: nop            
        //     8: dload_1         /* ag */
        //     9: nop            
        //    10: iconst_0       
        //    11: nop            
        //    12: nop            
        //    13: nop            
        //    14: fconst_2       
        //    15: nop            
        //    16: dload_2         /* itr */
        //    17: nop            
        //    18: dload_3        
        //    19: nop            
        //    20: iconst_1       
        //    21: nop            
        //    22: nop            
        //    23: nop            
        //    24: fconst_2       
        //    25: nop            
        //    26: aload_0         /* this */
        //    27: nop            
        //    28: aload_1         /* ag */
        //    29: nop            
        //    30: iconst_2       
        //    31: nop            
        //    32: aconst_null    
        //    33: nop            
        //    34: iaload         
        //    35: nop            
        //    36: laload         
        //    37: nop            
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  --------------------------------------------------
        //  29     5       3     r     Lcom/sun/xml/xsom/impl/parser/DelayedRef$AttGroup;
        //  10     27      2     itr   Ljava/util/Iterator;
        //  0      38      0     this  Lcom/sun/xml/xsom/impl/AttGroupDeclImpl;
        //  0      38      1     ag    Lcom/sun/xml/xsom/impl/AttGroupDeclImpl;
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
        // Original Bytecode:
        // 
        //     0: nop            
        //     1: nop            
        //     2: nop            
        //     3: iconst_3       
        //     4: nop            
        //     5: aconst_null    
        //     6: nop            
        //     7: nop            
        //    LocalVariableTable:
        //  Start  Length  Slot  Name     Signature
        //  -----  ------  ----  -------  ----------------------------------------
        //  0      8       0     this     Lcom/sun/xml/xsom/impl/AttGroupDeclImpl;
        //  0      8       1     visitor  Lcom/sun/xml/xsom/visitor/XSVisitor;
        // 
        // The error that occurred was:
        // 
        // java.lang.NullPointerException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public Object apply(final XSFunction function) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: lload_0         /* this */
        //     1: aload           4
        //     3: ifnonnull       28
        //     6: aload           5
        //    LocalVariableTable:
        //  Start  Length  Slot  Name      Signature
        //  -----  ------  ----  --------  ----------------------------------------
        //  0      8       0     this      Lcom/sun/xml/xsom/impl/AttGroupDeclImpl;
        //  0      8       1     function  Lcom/sun/xml/xsom/visitor/XSFunction;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
}
