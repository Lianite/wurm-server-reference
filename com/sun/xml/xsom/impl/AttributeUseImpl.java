// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.xml.xsom.impl;

import com.sun.xml.xsom.visitor.XSVisitor;
import com.sun.xml.xsom.visitor.XSFunction;
import com.sun.xml.xsom.XSAttributeDecl;
import org.xml.sax.Locator;
import org.relaxng.datatype.ValidationContext;
import com.sun.xml.xsom.XSAttributeUse;

public class AttributeUseImpl extends ComponentImpl implements XSAttributeUse
{
    private final Ref.Attribute att;
    private final String defaultValue;
    private final String fixedValue;
    private final ValidationContext context;
    private final boolean required;
    
    public AttributeUseImpl(final SchemaImpl owner, final AnnotationImpl ann, final Locator loc, final Ref.Attribute _decl, final String def, final String fixed, final ValidationContext _context, final boolean req) {
        // 
        // This method could not be decompiled.
        // 
        // Could not show original bytecode, likely due to the same error.
        // 
        // The error that occurred was:
        // 
        // com.strobel.assembler.metadata.MethodBodyParseException: An error occurred while parsing the bytecode of method 'com/sun/xml/xsom/impl/AttributeUseImpl.<init>:(Lcom/sun/xml/xsom/impl/SchemaImpl;Lcom/sun/xml/xsom/impl/AnnotationImpl;Lorg/xml/sax/Locator;Lcom/sun/xml/xsom/impl/Ref$Attribute;Ljava/lang/String;Ljava/lang/String;Lorg/relaxng/datatype/ValidationContext;Z)V'.
        //     at com.strobel.assembler.metadata.MethodReader.readBody(MethodReader.java:65)
        //     at com.strobel.assembler.metadata.MethodDefinition.tryLoadBody(MethodDefinition.java:722)
        //     at com.strobel.assembler.metadata.MethodDefinition.getBody(MethodDefinition.java:83)
        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:202)
        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:99)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethodBody(AstBuilder.java:757)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createConstructor(AstBuilder.java:692)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addTypeMembers(AstBuilder.java:529)
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
    
    public XSAttributeDecl getDecl() {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: nop            
        //     1: fload_0         /* this */
        //     2: nop            
        //     3: aload_1        
        //     4: nop            
        //     5: dload           this
        //     7: iconst_5       
        //     8: nop            
        //     9: aconst_null    
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  ----------------------------------------
        //  0      10      0     this  Lcom/sun/xml/xsom/impl/AttributeUseImpl;
        // 
        // The error that occurred was:
        // 
        // java.lang.NullPointerException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public String getDefaultValue() {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: nop            
        //     1: lconst_1       
        //     2: nop            
        //     3: lload_0         /* this */
        //     4: nop            
        //     5: lload_1        
        //     6: nop            
        //     7: nop            
        //     8: nop            
        //     9: aconst_null    
        //    10: nop            
        //    11: iaload         
        //    12: nop            
        //    13: laload         
        //    14: nop            
        //    15: aconst_null    
        //    16: nop            
        //    17: iload_1        
        //    18: nop            
        //    19: nop            
        //    20: nop            
        //    21: fstore_1       
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  ----------------------------------------
        //  0      22      0     this  Lcom/sun/xml/xsom/impl/AttributeUseImpl;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public String getFixedValue() {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: nop            
        //     1: lload           this
        //     3: lload_0         /* this */
        //     4: nop            
        //     5: lload_1        
        //     6: nop            
        //     7: nop            
        //     8: nop            
        //     9: aconst_null    
        //    10: nop            
        //    11: faload         
        //    12: nop            
        //    13: laload         
        //    14: nop            
        //    15: aconst_null    
        //    16: nop            
        //    17: iload_1        
        //    18: nop            
        //    19: nop            
        //    20: nop            
        //    21: fstore_1       
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  ----------------------------------------
        //  0      22      0     this  Lcom/sun/xml/xsom/impl/AttributeUseImpl;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public ValidationContext getContext() {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: nop            
        //     1: lload           this
        //     3: lload_0         /* this */
        //     4: nop            
        //     5: lload_1        
        //     6: nop            
        //     7: nop            
        //     8: nop            
        //     9: aconst_null    
        //    10: nop            
        //    11: daload         
        //    12: nop            
        //    13: aaload         
        //    14: nop            
        //    15: aconst_null    
        //    16: nop            
        //    17: iload_1        
        //    18: nop            
        //    19: nop            
        //    20: nop            
        //    21: laload         
        //    22: nop            
        //    23: aconst_null    
        //    24: nop            
        //    25: aconst_null    
        //    26: nop            
        //    27: nop            
        //    28: nop            
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  ----------------------------------------
        //  0      29      0     this  Lcom/sun/xml/xsom/impl/AttributeUseImpl;
        // 
        // The error that occurred was:
        // 
        // java.lang.NullPointerException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public boolean isRequired() {
        // 
        // This method could not be decompiled.
        // 
        // Could not show original bytecode, likely due to the same error.
        // 
        // The error that occurred was:
        // 
        // com.strobel.assembler.metadata.MethodBodyParseException: An error occurred while parsing the bytecode of method 'com/sun/xml/xsom/impl/AttributeUseImpl.isRequired:()Z'.
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
        // Caused by: java.nio.BufferUnderflowException
        //     at com.strobel.assembler.metadata.Buffer.verifyReadableBytes(Buffer.java:387)
        //     at com.strobel.assembler.metadata.Buffer.readUnsignedShort(Buffer.java:225)
        //     at com.strobel.assembler.metadata.MethodReader.readBodyCore(MethodReader.java:203)
        //     at com.strobel.assembler.metadata.MethodReader.readBody(MethodReader.java:62)
        //     ... 16 more
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public Object apply(final XSFunction f) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: aconst_null    
        //     1: nop            
        //     2: iload_1         /* f */
        //     3: nop            
        //     4: nop            
        //     5: nop            
        //     6: lstore_1        /* f */
        //     7: nop            
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  ----------------------------------------
        //  0      8       0     this  Lcom/sun/xml/xsom/impl/AttributeUseImpl;
        //  0      8       1     f     Lcom/sun/xml/xsom/visitor/XSFunction;
        // 
        // The error that occurred was:
        // 
        // java.lang.NullPointerException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public void visit(final XSVisitor v) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: istore_0        /* this */
        //     1: nop            
        //     2: nop            
        //     3: nop            
        //     4: iconst_m1      
        //     5: nop            
        //     6: istore_1        /* v */
        //     7: nop            
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  ----------------------------------------
        //  0      8       0     this  Lcom/sun/xml/xsom/impl/AttributeUseImpl;
        //  0      8       1     v     Lcom/sun/xml/xsom/visitor/XSVisitor;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
}
