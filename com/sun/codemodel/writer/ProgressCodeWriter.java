// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.codemodel.writer;

import java.io.IOException;
import java.io.OutputStream;
import com.sun.codemodel.JPackage;
import java.io.PrintStream;
import com.sun.codemodel.CodeWriter;

public class ProgressCodeWriter implements CodeWriter
{
    private final CodeWriter output;
    private final PrintStream progress;
    
    public ProgressCodeWriter(final CodeWriter output, final PrintStream progress) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: lload           40
        //     2: fstore_0        /* this */
        //     3: fstore_0        /* this */
        //     4: dload_3        
        //     5: astore_1        /* output */
        //     6: fmul           
        //     7: ladd           
        //     8: fneg           
        //     9: ladd           
        //    10: laload         
        //    11: idiv           
        //    12: ladd           
        //    13: fdiv           
        //    14: dsub           
        //    15: laload         
        //    16: aastore        
        //    17: ineg           
        //    18: frem           
        //    19: lmul           
        //    20: fdiv           
        //    21: dsub           
        //    22: istore_0        /* this */
        //    23: aconst_null    
        //    24: nop            
        //    25: iconst_3       
        //    26: ladd           
        //    LocalVariableTable:
        //  Start  Length  Slot  Name      Signature
        //  -----  ------  ----  --------  ---------------------------------------------
        //  0      27      0     this      Lcom/sun/codemodel/writer/ProgressCodeWriter;
        //  0      27      1     output    Lcom/sun/codemodel/CodeWriter;
        //  0      27      2     progress  Ljava/io/PrintStream;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public OutputStream open(final JPackage pkg, final String fileName) throws IOException {
        // 
        // This method could not be decompiled.
        // 
        // Could not show original bytecode, likely due to the same error.
        // 
        // The error that occurred was:
        // 
        // com.strobel.assembler.metadata.MethodBodyParseException: An error occurred while parsing the bytecode of method 'com/sun/codemodel/writer/ProgressCodeWriter.open:(Lcom/sun/codemodel/JPackage;Ljava/lang/String;)Ljava/io/OutputStream;'.
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
        // Caused by: java.lang.ArrayIndexOutOfBoundsException: -1
        //     at java.util.ArrayList.elementData(Unknown Source)
        //     at java.util.ArrayList.get(Unknown Source)
        //     at com.strobel.assembler.ir.ConstantPool.get(ConstantPool.java:80)
        //     at com.strobel.assembler.metadata.ClassFileReader$Scope.lookupConstant(ClassFileReader.java:1313)
        //     at com.strobel.assembler.metadata.MethodReader.readBodyCore(MethodReader.java:286)
        //     at com.strobel.assembler.metadata.MethodReader.readBody(MethodReader.java:62)
        //     ... 16 more
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public void close() throws IOException {
        return 3;
    }
}
