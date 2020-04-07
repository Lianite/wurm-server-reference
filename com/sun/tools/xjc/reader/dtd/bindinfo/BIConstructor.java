// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.reader.dtd.bindinfo;

import org.xml.sax.Locator;
import com.sun.tools.xjc.reader.annotator.AnnotatorController;
import com.sun.tools.xjc.grammar.ClassItem;
import org.dom4j.Element;

public class BIConstructor
{
    private final Element dom;
    private final String[] properties;
    
    BIConstructor(final Element _node) {
        // 
        // This method could not be decompiled.
        // 
        // Could not show original bytecode, likely due to the same error.
        // 
        // The error that occurred was:
        // 
        // com.strobel.assembler.metadata.MethodBodyParseException: An error occurred while parsing the bytecode of method 'com/sun/tools/xjc/reader/dtd/bindinfo/BIConstructor.<init>:(Lorg/dom4j/Element;)V'.
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
        // Caused by: java.lang.NullPointerException
        //     at com.strobel.assembler.metadata.MethodReader.readBodyCore(MethodReader.java:170)
        //     at com.strobel.assembler.metadata.MethodReader.readBody(MethodReader.java:62)
        //     ... 16 more
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public void createDeclaration(final ClassItem cls, final AnnotatorController controller) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: dload_0         /* this */
        //     1: nop            
        //     2: iconst_0       
        //     3: nop            
        //     4: dload_1         /* cls */
        //     5: nop            
        //     6: nop            
        //     7: nop            
        //     8: fconst_1       
        //    LocalVariableTable:
        //  Start  Length  Slot  Name        Signature
        //  -----  ------  ----  ----------  --------------------------------------------------------
        //  0      9       0     this        Lcom/sun/tools/xjc/reader/dtd/bindinfo/BIConstructor;
        //  0      9       1     cls         Lcom/sun/tools/xjc/grammar/ClassItem;
        //  0      9       2     controller  Lcom/sun/tools/xjc/reader/annotator/AnnotatorController;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public Locator getSourceLocation() {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: nop            
        //     1: nop            
        //     2: nop            
        //     3: nop            
        //     4: lconst_0       
        //     5: nop            
        //     6: aload_1        
        //     7: nop            
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  -----------------------------------------------------
        //  0      8       0     this  Lcom/sun/tools/xjc/reader/dtd/bindinfo/BIConstructor;
        // 
        // The error that occurred was:
        // 
        // java.lang.NullPointerException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
}
