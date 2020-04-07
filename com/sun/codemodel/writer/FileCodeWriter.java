// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.codemodel.writer;

import java.io.OutputStream;
import com.sun.codemodel.JPackage;
import java.io.IOException;
import java.util.Set;
import java.io.File;
import com.sun.codemodel.CodeWriter;

public class FileCodeWriter implements CodeWriter
{
    private final File target;
    private final boolean readOnly;
    private final Set readonlyFiles;
    
    public FileCodeWriter(final File target) throws IOException {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: nop            
        //     1: aload_0         /* this */
        //     2: nop            
        //     3: aload_1         /* target */
        //     4: nop            
        //     5: aconst_null    
        //     6: nop            
        //    Exceptions:
        //  throws java.io.IOException
        //    LocalVariableTable:
        //  Start  Length  Slot  Name    Signature
        //  -----  ------  ----  ------  -----------------------------------------
        //  0      7       0     this    Lcom/sun/codemodel/writer/FileCodeWriter;
        //  0      7       1     target  Ljava/io/File;
        // 
        // The error that occurred was:
        // 
        // java.lang.NullPointerException
        //     at com.strobel.decompiler.ast.AstBuilder.performStackAnalysis(AstBuilder.java:2162)
        //     at com.strobel.decompiler.ast.AstBuilder.build(AstBuilder.java:108)
        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:210)
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
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public FileCodeWriter(final File target, final boolean readOnly) throws IOException {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: nop            
        //     1: dload_0         /* this */
        //     2: nop            
        //     3: dload_1         /* target */
        //     4: nop            
        //     5: aconst_null    
        //     6: nop            
        //     7: saload         
        //     8: nop            
        //     9: nop            
        //    10: nop            
        //    11: iconst_1       
        //    12: nop            
        //    13: aconst_null    
        //    14: nop            
        //    15: lconst_1       
        //    16: nop            
        //    17: aconst_null    
        //    18: nop            
        //    19: iaload         
        //    20: nop            
        //    21: istore          this
        //    23: iconst_m1      
        //    24: nop            
        //    25: faload         
        //    26: nop            
        //    27: nop            
        //    28: nop            
        //    29: ifeq            33
        //    32: nop            
        //    33: iconst_0       
        //    34: nop            
        //    35: nop            
        //    36: nop            
        //    37: fstore_0        /* this */
        //    38: aload_0         /* this */
        //    39: invokespecial   java/lang/Object.<init>:()V
        //    42: aload_0         /* this */
        //    43: new             Ljava/util/HashSet;
        //    46: dup            
        //    47: invokespecial   java/util/HashSet.<init>:()V
        //    50: putfield        com/sun/codemodel/writer/FileCodeWriter.readonlyFiles:Ljava/util/Set;
        //    53: aload_0         /* this */
        //    54: aload_1         /* target */
        //    55: putfield        com/sun/codemodel/writer/FileCodeWriter.target:Ljava/io/File;
        //    58: aload_0         /* this */
        //    59: iload_2         /* readOnly */
        //    60: putfield        com/sun/codemodel/writer/FileCodeWriter.readOnly:Z
        //    63: aload_1         /* target */
        //    64: invokevirtual   java/io/File.exists:()Z
        //    Exceptions:
        //  throws java.io.IOException
        //    LocalVariableTable:
        //  Start  Length  Slot  Name      Signature
        //  -----  ------  ----  --------  -----------------------------------------
        //  0      67      0     this      Lcom/sun/codemodel/writer/FileCodeWriter;
        //  0      67      1     target    Ljava/io/File;
        //  0      67      2     readOnly  Z
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
        // Original Bytecode:
        // 
        //     0: nop            
        //     1: dload_2         /* fileName */
        //     2: nop            
        //     3: dload_3        
        //     4: nop            
        //     5: iconst_m1      
        //     6: nop            
        //     7: saload         
        //     8: nop            
        //     9: nop            
        //    10: nop            
        //    11: iconst_1       
        //    12: nop            
        //    13: aconst_null    
        //    Exceptions:
        //  throws java.io.IOException
        //    LocalVariableTable:
        //  Start  Length  Slot  Name      Signature
        //  -----  ------  ----  --------  -----------------------------------------
        //  0      14      0     this      Lcom/sun/codemodel/writer/FileCodeWriter;
        //  0      14      1     pkg       Lcom/sun/codemodel/JPackage;
        //  0      14      2     fileName  Ljava/lang/String;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    protected File getFile(final JPackage pkg, final String fileName) throws IOException {
        // 
        // This method could not be decompiled.
        // 
        // Could not show original bytecode, likely due to the same error.
        // 
        // The error that occurred was:
        // 
        // com.strobel.assembler.metadata.MethodBodyParseException: An error occurred while parsing the bytecode of method 'com/sun/codemodel/writer/FileCodeWriter.getFile:(Lcom/sun/codemodel/JPackage;Ljava/lang/String;)Ljava/io/File;'.
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
        // Caused by: java.lang.NullPointerException
        //     at com.strobel.assembler.metadata.MethodReader.readBodyCore(MethodReader.java:170)
        //     at com.strobel.assembler.metadata.MethodReader.readBody(MethodReader.java:62)
        //     ... 16 more
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public void close() throws IOException {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: lstore_1       
        //     1: nop            
        //     2: dload_1        
        //     3: nop            
        //     4: iconst_1       
        //     5: nop            
        //     6: saload         
        //     7: nop            
        //     8: nop            
        //     9: nop            
        //    10: iconst_1       
        //    11: nop            
        //    12: aconst_null    
        //    13: nop            
        //    14: lconst_1       
        //    15: nop            
        //    16: aconst_null    
        //    17: nop            
        //    18: lstore_2       
        //    19: nop            
        //    20: lstore_3       
        //    21: nop            
        //    22: iconst_m1      
        //    23: nop            
        //    24: faload         
        //    25: nop            
        //    26: nop            
        //    27: nop            
        //    28: idiv           
        //    29: nop            
        //    30: aconst_null    
        //    31: nop            
        //    32: iconst_0       
        //    33: nop            
        //    34: nop            
        //    35: nop            
        //    36: dload_0         /* this */
        //    37: aload_0         /* this */
        //    Exceptions:
        //  throws java.io.IOException
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  -----------------------------------------
        //  29     5       2     f     Ljava/io/File;
        //  10     27      1     itr   Ljava/util/Iterator;
        //  0      38      0     this  Lcom/sun/codemodel/writer/FileCodeWriter;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    private String toDirName(final JPackage pkg) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: nop            
        //     1: nop            
        //     2: saload         
        //     3: nop            
        //     4: nop            
        //     5: nop            
        //     6: iconst_1       
        //     7: nop            
        //     8: aconst_null    
        //     9: nop            
        //    10: lconst_1       
        //    11: nop            
        //    12: lconst_1       
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  -----------------------------------------
        //  0      13      0     this  Lcom/sun/codemodel/writer/FileCodeWriter;
        //  0      13      1     pkg   Lcom/sun/codemodel/JPackage;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
}
