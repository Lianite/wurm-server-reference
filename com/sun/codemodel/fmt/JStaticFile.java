// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.codemodel.fmt;

import java.io.IOException;
import java.io.OutputStream;
import com.sun.codemodel.JResourceFile;

public final class JStaticFile extends JResourceFile
{
    private final ClassLoader classLoader;
    private final String resourceName;
    
    public JStaticFile(final String _resourceName) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: nop            
        //     1: nop            
        //     2: nop            
        //     3: lload_2        
        //     4: nop            
        //     5: lload_1         /* _resourceName */
        //     6: nop            
        //     7: lload_2        
        //     8: nop            
        //     9: nop            
        //    10: nop            
        //    11: nop            
        //    12: nop            
        //    13: lload_2        
        //    14: nop            
        //    15: fload_2        
        //    16: nop            
        //    17: iload           this
        //    19: aconst_null    
        //    20: nop            
        //    21: nop            
        //    22: nop            
        //    23: lload_2        
        //    24: nop            
        //    25: lload_3        
        //    26: nop            
        //    27: fload           this
        //    29: iconst_m1      
        //    LocalVariableTable:
        //  Start  Length  Slot  Name           Signature
        //  -----  ------  ----  -------------  -----------------------------------
        //  0      30      0     this           Lcom/sun/codemodel/fmt/JStaticFile;
        //  0      30      1     _resourceName  Ljava/lang/String;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public JStaticFile(final ClassLoader _classLoader, final String _resourceName) {
        // 
        // This method could not be decompiled.
        // 
        // Could not show original bytecode, likely due to the same error.
        // 
        // The error that occurred was:
        // 
        // com.strobel.assembler.metadata.MethodBodyParseException: An error occurred while parsing the bytecode of method 'com/sun/codemodel/fmt/JStaticFile.<init>:(Ljava/lang/ClassLoader;Ljava/lang/String;)V'.
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
        // Caused by: java.lang.ClassCastException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    protected void build(final OutputStream os) throws IOException {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: nop            
        //     1: caload         
        //     2: nop            
        //     3: iconst_2       
        //     4: nop            
        //     5: nop            
        //     6: nop            
        //     7: caload         
        //     8: nop            
        //     9: lload_1         /* os */
        //    10: nop            
        //    11: lload_2        
        //    12: nop            
        //    13: nop            
        //    14: nop            
        //    15: nop            
        //    16: nop            
        //    17: caload         
        //    18: nop            
        //    19: dload_2         /* dis */
        //    20: nop            
        //    21: dload_3        
        //    22: nop            
        //    23: aconst_null    
        //    24: nop            
        //    25: ldc_w           "Code"
        //    28: nop            
        //    29: aload_0         /* this */
        //    30: nop            
        //    31: aload_1         /* os */
        //    32: nop            
        //    33: iconst_m1      
        //    34: nop            
        //    35: aload           this
        //    37: iload_1         /* os */
        //    38: nop            
        //    39: aload_2         /* dis */
        //    40: nop            
        //    41: aload_3         /* buf */
        //    42: nop            
        //    43: iconst_0       
        //    44: nop            
        //    45: lload_3         /* buf */
        //    46: nop            
        //    47: ldc_w           "Ljava/io/DataInputStream;"
        //    50: nop            
        //    51: laload         
        //    Exceptions:
        //  throws java.io.IOException
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  -----------------------------------
        //  0      52      0     this  Lcom/sun/codemodel/fmt/JStaticFile;
        //  0      52      1     os    Ljava/io/OutputStream;
        //  19     33      2     dis   Ljava/io/DataInputStream;
        //  25     27      3     buf   [B
        //  33     19      4     sz    I
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
}
