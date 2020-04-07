// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.codemodel;

import java.util.List;

public class JInvocation extends JExpressionImpl implements JStatement
{
    private JGenerable object;
    private String name;
    private boolean isConstructor;
    private List args;
    private JType type;
    
    JInvocation(final JExpression object, final String name) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     1: lmul           
        //     2: drem           
        //     3: lstore_2        /* name */
        //     4: frem           
        //     5: frem           
        //     6: ladd           
        //    LocalVariableTable:
        //  Start  Length  Slot  Name    Signature
        //  -----  ------  ----  ------  -------------------------------
        //  0      7       0     this    Lcom/sun/codemodel/JInvocation;
        //  0      7       1     object  Lcom/sun/codemodel/JExpression;
        //  0      7       2     name    Ljava/lang/String;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    JInvocation(final JClass type, final String name) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     1: lsub           
        //     2: idiv           
        //     3: laload         
        //     4: dstore_3       
        //     5: fstore_3       
        //     6: ddiv           
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  -------------------------------
        //  0      7       0     this  Lcom/sun/codemodel/JInvocation;
        //  0      7       1     type  Lcom/sun/codemodel/JClass;
        //  0      7       2     name  Ljava/lang/String;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    private JInvocation(final JGenerable object, final String name) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: dload_2         /* name */
        //     1: fstore_0        /* this */
        //     2: dload_3        
        //     3: astore_1        /* object */
        //     4: dadd           
        //     5: ddiv           
        //     6: ldiv           
        //     7: laload         
        //     8: drem           
        //     9: lneg           
        //    10: fdiv           
        //    11: laload         
        //    12: dadd           
        //    13: ddiv           
        //    14: isub           
        //    15: lsub           
        //    16: ldiv           
        //    17: ddiv           
        //    18: isub           
        //    19: lsub           
        //    20: idiv           
        //    21: laload         
        //    22: dstore_3       
        //    23: fstore_3       
        //    24: ddiv           
        //    25: frem           
        //    26: ldiv           
        //    27: ladd           
        //    28: ineg           
        //    29: ineg           
        //    30: lsub           
        //    31: frem           
        //    32: istore_0        /* this */
        //    33: aconst_null    
        //    34: nop            
        //    35: aload           99
        //    37: ddiv           
        //    38: ldiv           
        //    39: laload         
        //    40: drem           
        //    41: lneg           
        //    42: fdiv           
        //    43: laload         
        //    44: dadd           
        //    45: ddiv           
        //    46: isub           
        //    47: lsub           
        //    48: ldiv           
        //    49: ddiv           
        //    50: isub           
        //    51: lsub           
        //    52: idiv           
        //    53: laload         
        //    54: dstore_3       
        //    55: astore_2        /* name */
        //    56: lsub           
        //    57: ineg           
        //    58: imul           
        //    59: ddiv           
        //    60: isub           
        //    61: aconst_null    
        //    62: nop            
        //    63: iconst_m1      
        //    64: lmul           
        //    65: isub           
        //    66: aconst_null    
        //    67: nop            
        //    68: istore          40
        //    70: astore_1        /* object */
        //    71: fmul           
        //    LocalVariableTable:
        //  Start  Length  Slot  Name    Signature
        //  -----  ------  ----  ------  -------------------------------
        //  0      72      0     this    Lcom/sun/codemodel/JInvocation;
        //  0      72      1     object  Lcom/sun/codemodel/JGenerable;
        //  0      72      2     name    Ljava/lang/String;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    JInvocation(final JType c) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     1: fstore_3       
        //     2: ddiv           
        //     3: frem           
        //     4: ldiv           
        //     5: ladd           
        //     6: ineg           
        //     7: ineg           
        //     8: lsub           
        //     9: frem           
        //    10: istore_0        /* this */
        //    11: nop            
        //    12: daload         
        //    13: nop            
        //    14: fload_3        
        //    15: nop            
        //    16: dload_0         /* this */
        //    17: nop            
        //    18: aconst_null    
        //    19: nop            
        //    20: dload_1         /* c */
        //    21: nop            
        //    22: iconst_3       
        //    23: nop            
        //    24: iconst_m1      
        //    25: nop            
        //    26: dload_2        
        //    27: nop            
        //    28: dload_3        
        //    29: nop            
        //    30: nop            
        //    31: nop            
        //    32: iconst_m1      
        //    33: nop            
        //    34: aload_0         /* this */
        //    35: nop            
        //    36: aload_1         /* c */
        //    37: nop            
        //    38: nop            
        //    39: nop            
        //    40: iconst_m1      
        //    41: nop            
        //    42: aload_2        
        //    43: nop            
        //    44: aload_3        
        //    45: nop            
        //    46: nop            
        //    47: nop            
        //    48: iconst_m1      
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  -------------------------------
        //  0      49      0     this  Lcom/sun/codemodel/JInvocation;
        //  0      49      1     c     Lcom/sun/codemodel/JType;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public JInvocation arg(final JExpression arg) {
        // 
        // This method could not be decompiled.
        // 
        // Could not show original bytecode, likely due to the same error.
        // 
        // The error that occurred was:
        // 
        // com.strobel.assembler.metadata.MethodBodyParseException: An error occurred while parsing the bytecode of method 'com/sun/codemodel/JInvocation.arg:(Lcom/sun/codemodel/JExpression;)Lcom/sun/codemodel/JInvocation;'.
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
        //     at com.strobel.assembler.metadata.Buffer.readUnsignedByte(Buffer.java:214)
        //     at com.strobel.assembler.metadata.MethodReader.readBodyCore(MethodReader.java:412)
        //     at com.strobel.assembler.metadata.MethodReader.readBody(MethodReader.java:62)
        //     ... 16 more
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public void generate(final JFormatter f) {
        // 
        // This method could not be decompiled.
        // 
        // Could not show original bytecode, likely due to the same error.
        // 
        // The error that occurred was:
        // 
        // com.strobel.assembler.metadata.MethodBodyParseException: An error occurred while parsing the bytecode of method 'com/sun/codemodel/JInvocation.generate:(Lcom/sun/codemodel/JFormatter;)V'.
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
        // Caused by: java.lang.reflect.GenericSignatureFormatError
        //     at com.strobel.assembler.metadata.signatures.SignatureParser.error(SignatureParser.java:67)
        //     at com.strobel.assembler.metadata.signatures.SignatureParser.parseFormalParameters(SignatureParser.java:413)
        //     at com.strobel.assembler.metadata.signatures.SignatureParser.parseMethodTypeSignature(SignatureParser.java:402)
        //     at com.strobel.assembler.metadata.signatures.SignatureParser.parseMethodSignature(SignatureParser.java:85)
        //     at com.strobel.assembler.metadata.MetadataParser.parseMethodSignature(MetadataParser.java:223)
        //     at com.strobel.assembler.metadata.MetadataParser.parseMethod(MetadataParser.java:155)
        //     at com.strobel.assembler.metadata.ClassFileReader$Scope.lookupMethod(ClassFileReader.java:1303)
        //     at com.strobel.assembler.metadata.ClassFileReader$Scope.lookupMethod(ClassFileReader.java:1250)
        //     at com.strobel.assembler.metadata.MethodReader.readBodyCore(MethodReader.java:203)
        //     at com.strobel.assembler.metadata.MethodReader.readBody(MethodReader.java:62)
        //     ... 16 more
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public void state(final JFormatter f) {
        // 
        // This method could not be decompiled.
        // 
        // Could not show original bytecode, likely due to the same error.
        // 
        // The error that occurred was:
        // 
        // com.strobel.assembler.metadata.MethodBodyParseException: An error occurred while parsing the bytecode of method 'com/sun/codemodel/JInvocation.state:(Lcom/sun/codemodel/JFormatter;)V'.
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
        // Caused by: java.lang.reflect.GenericSignatureFormatError
        //     at com.strobel.assembler.metadata.signatures.SignatureParser.error(SignatureParser.java:67)
        //     at com.strobel.assembler.metadata.signatures.SignatureParser.parseFormalParameters(SignatureParser.java:413)
        //     at com.strobel.assembler.metadata.signatures.SignatureParser.parseMethodTypeSignature(SignatureParser.java:402)
        //     at com.strobel.assembler.metadata.signatures.SignatureParser.parseMethodSignature(SignatureParser.java:85)
        //     at com.strobel.assembler.metadata.MetadataParser.parseMethodSignature(MetadataParser.java:223)
        //     at com.strobel.assembler.metadata.MetadataParser.parseMethod(MetadataParser.java:155)
        //     at com.strobel.assembler.metadata.ClassFileReader$Scope.lookupMethod(ClassFileReader.java:1303)
        //     at com.strobel.assembler.metadata.ClassFileReader$Scope.lookupMethod(ClassFileReader.java:1250)
        //     at com.strobel.assembler.metadata.MethodReader.readBodyCore(MethodReader.java:203)
        //     at com.strobel.assembler.metadata.MethodReader.readBody(MethodReader.java:62)
        //     ... 16 more
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
}
