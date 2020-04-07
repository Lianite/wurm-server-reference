// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc;

import org.xml.sax.SAXParseException;
import org.xml.sax.Locator;
import org.xml.sax.ErrorHandler;

public abstract class ErrorReceiver implements ErrorHandler
{
    public ErrorReceiver() {
        // 
        // This method could not be decompiled.
        // 
        // Could not show original bytecode, likely due to the same error.
        // 
        // The error that occurred was:
        // 
        // com.strobel.assembler.metadata.MethodBodyParseException: An error occurred while parsing the bytecode of method 'com/sun/tools/xjc/ErrorReceiver.<init>:()V'.
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
        // Caused by: java.nio.BufferUnderflowException
        //     at com.strobel.assembler.metadata.Buffer.verifyReadableBytes(Buffer.java:387)
        //     at com.strobel.assembler.metadata.Buffer.readUnsignedShort(Buffer.java:225)
        //     at com.strobel.assembler.metadata.MethodReader.readBodyCore(MethodReader.java:291)
        //     at com.strobel.assembler.metadata.MethodReader.readBody(MethodReader.java:62)
        //     ... 16 more
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public final void error(final Locator loc, final String msg) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: nop            
        //     1: fconst_2       
        //     2: dsub           
        //     3: lsub           
        //     4: ineg           
        //     5: astore_1        /* loc */
        //     6: lmul           
        //     7: fdiv           
        //     8: lsub           
        //     9: astore_3       
        //    10: lneg           
        //    11: ldiv           
        //    12: fadd           
        //    13: lsub           
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  ---------------------------------
        //  0      14      0     this  Lcom/sun/tools/xjc/ErrorReceiver;
        //  0      14      1     loc   Lorg/xml/sax/Locator;
        //  0      14      2     msg   Ljava/lang/String;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public final void warning(final Locator loc, final String msg) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: ddiv           
        //     1: ldiv           
        //     2: laload         
        //     3: drem           
        //     4: lneg           
        //     5: fdiv           
        //     6: laload         
        //     7: ineg           
        //     8: ddiv           
        //     9: ddiv           
        //    10: idiv           
        //    11: drem           
        //    12: laload         
        //    13: ishl           
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  ---------------------------------
        //  0      14      0     this  Lcom/sun/tools/xjc/ErrorReceiver;
        //  0      14      1     loc   Lorg/xml/sax/Locator;
        //  0      14      2     msg   Ljava/lang/String;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public abstract void error(final SAXParseException p0) throws AbortException;
    
    public abstract void fatalError(final SAXParseException p0) throws AbortException;
    
    public abstract void warning(final SAXParseException p0) throws AbortException;
    
    public abstract void info(final SAXParseException p0);
    
    public final void debug(final String msg) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: nop            
        //     1: nop            
        //     2: fconst_1       
        //     3: nop            
        //     4: aconst_null    
        //     5: nop            
        //     6: nop            
        //     7: nop            
        //     8: iconst_2       
        //     9: nop            
        //    10: lload_0         /* this */
        //    11: nop            
        //    12: lload_1         /* msg */
        //    13: nop            
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  ---------------------------------
        //  0      14      0     this  Lcom/sun/tools/xjc/ErrorReceiver;
        //  0      14      1     msg   Ljava/lang/String;
        // 
        // The error that occurred was:
        // 
        // java.lang.NullPointerException
        //     at com.strobel.decompiler.ast.AstBuilder.performStackAnalysis(AstBuilder.java:2162)
        //     at com.strobel.decompiler.ast.AstBuilder.build(AstBuilder.java:108)
        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:210)
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
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    protected final String getLocationString(final SAXParseException e) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: lload_0         /* this */
        //     1: nop            
        //     2: lload_1         /* e */
        //     3: nop            
        //     4: nop            
        //     5: nop            
        //     6: nop            
        //     7: nop            
        //     8: dconst_0       
        //     9: nop            
        //    10: fload_0         /* this */
        //    11: nop            
        //    12: fload_1         /* e */
        //    13: nop            
        //    14: aconst_null    
        //    15: nop            
        //    16: nop            
        //    17: nop            
        //    18: dconst_0       
        //    19: nop            
        //    20: fload_2         /* line */
        //    21: nop            
        //    22: fload_3        
        //    23: nop            
        //    24: iconst_m1      
        //    25: nop            
        //    26: sipush          32
        //    29: nop            
        //    30: dload_0         /* this */
        //    31: nop            
        //    32: aconst_null    
        //    33: nop            
        //    34: iload_1         /* e */
        //    35: nop            
        //    36: nop            
        //    37: nop            
        //    38: dup_x2         
        //    39: nop            
        //    40: iconst_3       
        //    41: nop            
        //    42: iconst_1       
        //    43: nop            
        //    44: nop            
        //    45: nop            
        //    46: dconst_1       
        //    47: aload_0         /* this */
        //    48: new             Lorg/xml/sax/SAXParseException;
        //    51: dup            
        //    52: aload_2        
        //    53: aload_1         /* e */
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  ---------------------------------
        //  20     28      2     line  I
        //  0      54      0     this  Lcom/sun/tools/xjc/ErrorReceiver;
        //  0      54      1     e     Lorg/xml/sax/SAXParseException;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    private String getShortName(final String url) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: nop            
        //     1: fastore        
        //     2: nop            
        //     3: iconst_3       
        //     4: nop            
        //     5: iconst_0       
        //     6: nop            
        //     7: nop            
        //     8: nop            
        //     9: dconst_1       
        //    10: aload_0         /* this */
        //    11: new             Lorg/xml/sax/SAXParseException;
        //    14: dup            
        //    15: aload_1         /* url */
        //    16: aconst_null    
        //    17: aload_2         /* idx */
        //    18: invokespecial   com/sun/tools/xjc/ErrorReceiver.warning:(Lorg/xml/sax/SAXParseException;)V
        //    21: invokevirtual   com/sun/tools/xjc/ErrorReceiver.error:(Lorg/xml/sax/SAXParseException;)V
        //    24: return         
        //    25: nop            
        //    26: nop            
        //    27: nop            
        //    28: iconst_m1      
        //    29: nop            
        //    30: iload_2         /* idx */
        //    31: nop            
        //    32: nop            
        //    33: nop            
        //    34: lconst_1       
        //    35: nop            
        //    36: iconst_m1      
        //    37: nop            
        //    38: nop            
        //    39: nop            
        //    40: dup_x1         
        //    41: nop            
        //    42: dconst_0       
        //    43: nop            
        //    44: dup_x2         
        //    45: nop            
        //    46: iload_3        
        //    47: nop            
        //    48: nop            
        //    49: nop            
        //    50: lload_2         /* idx */
        //    51: nop            
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  ---------------------------------
        //  0      52      0     this  Lcom/sun/tools/xjc/ErrorReceiver;
        //  0      52      1     url   Ljava/lang/String;
        //  17     35      2     idx   I
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
}
