// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.codemodel;

import java.util.List;
import java.lang.reflect.Modifier;
import java.io.IOException;
import java.io.PrintStream;
import java.io.File;
import java.util.Iterator;
import java.util.HashMap;

public final class JCodeModel
{
    private HashMap packages;
    private final HashMap refClasses;
    public final JNullType NULL;
    public final JPrimitiveType VOID;
    public final JPrimitiveType BOOLEAN;
    public final JPrimitiveType BYTE;
    public final JPrimitiveType SHORT;
    public final JPrimitiveType CHAR;
    public final JPrimitiveType INT;
    public final JPrimitiveType FLOAT;
    public final JPrimitiveType LONG;
    public final JPrimitiveType DOUBLE;
    protected static final boolean isCaseSensitiveFileSystem;
    
    private static boolean getFileSystemCaseSensitivity() {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: aconst_null    
        //     1: nop            
        //     2: fconst_2       
        //     3: drem           
        //     4: lsub           
        //     5: irem           
        //     6: ladd           
        //     7: frem           
        //     8: ladd           
        //     9: ineg           
        //    10: ddiv           
        //    11: frem           
        //    12: fstore_0       
        //    13: imul           
        //    14: ladd           
        //    15: frem           
        //    16: aconst_null    
        //    17: nop            
        //    18: aconst_null    
        //    19: fstore_0       
        //    20: aconst_null    
        //    21: nop            
        //    22: lload_3        
        //    23: dload_2        
        //    24: astore_1       
        //    25: dadd           
        //    26: ddiv           
        //    27: ldiv           
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  ---------------------
        //  14     0       0     e     Ljava/lang/Exception;
        //    Exceptions:
        //  Try           Handler
        //  Start  End    Start  End    Type                 
        //  -----  -----  -----  -----  ---------------------
        //  0      9      13     28     Ljava/lang/Exception;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public JCodeModel() {
        // 
        // This method could not be decompiled.
        // 
        // Could not show original bytecode, likely due to the same error.
        // 
        // The error that occurred was:
        // 
        // com.strobel.assembler.metadata.MethodBodyParseException: An error occurred while parsing the bytecode of method 'com/sun/codemodel/JCodeModel.<init>:()V'.
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
        // Caused by: java.lang.IllegalArgumentException: Argument 'index' must be in the range [0, 343], but value was: 27233.
        //     at com.strobel.core.VerifyArgument.inRange(VerifyArgument.java:346)
        //     at com.strobel.assembler.ir.ConstantPool.get(ConstantPool.java:78)
        //     at com.strobel.assembler.metadata.ClassFileReader$Scope.lookupConstant(ClassFileReader.java:1313)
        //     at com.strobel.assembler.metadata.MethodReader.readBodyCore(MethodReader.java:293)
        //     at com.strobel.assembler.metadata.MethodReader.readBody(MethodReader.java:62)
        //     ... 16 more
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public JPackage _package(final String name) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: laload         
        //     1: idiv           
        //     2: ladd           
        //     3: fdiv           
        //     4: dsub           
        //     5: laload         
        //     6: aastore        
        //     7: ineg           
        //     8: frem           
        //     9: lmul           
        //    10: fdiv           
        //    11: dsub           
        //    12: istore_0        /* this */
        //    13: astore_1        /* name */
        //    14: dadd           
        //    15: ddiv           
        //    16: ldiv           
        //    17: laload         
        //    18: drem           
        //    19: lneg           
        //    20: fdiv           
        //    21: laload         
        //    22: dadd           
        //    23: ddiv           
        //    24: isub           
        //    25: lsub           
        //    26: ldiv           
        //    27: ddiv           
        //    28: isub           
        //    29: lsub           
        //    30: idiv           
        //    31: laload         
        //    32: fstore_0        /* this */
        //    33: idiv           
        //    34: ladd           
        //    35: drem           
        //    36: drem           
        //    37: bastore        
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  ------------------------------
        //  0      38      0     this  Lcom/sun/codemodel/JCodeModel;
        //  0      38      1     name  Ljava/lang/String;
        //  12     26      2     p     Lcom/sun/codemodel/JPackage;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public final JPackage rootPackage() {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: laload         
        //     1: fstore_0        /* this */
        //     2: ddiv           
        //     3: isub           
        //     4: lsub           
        //     5: pop            
        //     6: frem           
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  ------------------------------
        //  0      7       0     this  Lcom/sun/codemodel/JCodeModel;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public Iterator packages() {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: idiv           
        //     1: ladd           
        //     2: fdiv           
        //     3: dsub           
        //     4: laload         
        //     5: iastore        
        //     6: fadd           
        //     7: fmul           
        //     8: lsub           
        //     9: dadd           
        //    10: ineg           
        //    11: istore_0        /* this */
        //    12: dload_3        
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  ------------------------------
        //  0      13      0     this  Lcom/sun/codemodel/JCodeModel;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public JDefinedClass _class(final String fullyqualifiedName) throws JClassAlreadyExistsException {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: drem           
        //     1: lsub           
        //     2: aconst_null    
        //     3: nop            
        //     4: dconst_1       
        //     5: fmul           
        //     6: ladd           
        //     7: fneg           
        //     8: ladd           
        //     9: laload         
        //    10: idiv           
        //    11: ladd           
        //    12: fdiv           
        //    13: dsub           
        //    14: laload         
        //    15: fstore_0        /* this */
        //    16: idiv           
        //    17: ladd           
        //    18: drem           
        //    19: drem           
        //    20: aconst_null    
        //    21: nop            
        //    22: fconst_0       
        //    23: lmul           
        //    24: drem           
        //    25: lastore        
        //    26: frem           
        //    27: lmul           
        //    28: ldiv           
        //    29: lmul           
        //    30: ineg           
        //    31: lmul           
        //    32: fneg           
        //    33: lsub           
        //    34: aconst_null    
        //    35: nop            
        //    36: iconst_3       
        //    37: ladd           
        //    38: irem           
        //    39: irem           
        //    40: lsub           
        //    Exceptions:
        //  throws com.sun.codemodel.JClassAlreadyExistsException
        //    LocalVariableTable:
        //  Start  Length  Slot  Name                Signature
        //  -----  ------  ----  ------------------  ------------------------------
        //  0      41      0     this                Lcom/sun/codemodel/JCodeModel;
        //  0      41      1     fullyqualifiedName  Ljava/lang/String;
        //  7      34      2     idx                 I
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public JDefinedClass _getClass(final String fullyQualifiedName) {
        // 
        // This method could not be decompiled.
        // 
        // Could not show original bytecode, likely due to the same error.
        // 
        // The error that occurred was:
        // 
        // com.strobel.assembler.metadata.MethodBodyParseException: An error occurred while parsing the bytecode of method 'com/sun/codemodel/JCodeModel._getClass:(Ljava/lang/String;)Lcom/sun/codemodel/JDefinedClass;'.
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
        // Caused by: java.lang.IllegalArgumentException: Argument 'index' must be in the range [0, 343], but value was: 10281.
        //     at com.strobel.core.VerifyArgument.inRange(VerifyArgument.java:346)
        //     at com.strobel.assembler.ir.ConstantPool.get(ConstantPool.java:78)
        //     at com.strobel.assembler.metadata.ClassFileReader$Scope.lookupConstant(ClassFileReader.java:1313)
        //     at com.strobel.assembler.metadata.MethodReader.readBodyCore(MethodReader.java:293)
        //     at com.strobel.assembler.metadata.MethodReader.readBody(MethodReader.java:62)
        //     ... 16 more
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public JDefinedClass newAnonymousClass(final JClass baseType) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: laload         
        //     1: dadd           
        //     2: ddiv           
        //     3: isub           
        //     4: lsub           
        //     5: ldiv           
        //     6: ddiv           
        //     7: isub           
        //     8: lsub           
        //     9: idiv           
        //    LocalVariableTable:
        //  Start  Length  Slot  Name      Signature
        //  -----  ------  ----  --------  ------------------------------
        //  0      10      0     this      Lcom/sun/codemodel/JCodeModel;
        //  0      10      1     baseType  Lcom/sun/codemodel/JClass;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public void build(final File destDir, final PrintStream status) throws IOException {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: lsub           
        //     1: astore_2        /* status */
        //     2: ddiv           
        //     3: isub           
        //     4: lsub           
        //     5: idiv           
        //     6: istore_0        /* this */
        //     7: astore_1        /* destDir */
        //     8: fmul           
        //     9: ladd           
        //    10: fneg           
        //    11: ladd           
        //    12: laload         
        //    13: idiv           
        //    14: ladd           
        //    15: fdiv           
        //    16: dsub           
        //    17: laload         
        //    18: fstore_0        /* this */
        //    19: idiv           
        //    20: ladd           
        //    21: drem           
        //    22: drem           
        //    23: istore_0        /* this */
        //    24: dload_3         /* out */
        //    25: sastore        
        //    26: aconst_null    
        //    27: nop            
        //    28: iconst_4       
        //    Exceptions:
        //  throws java.io.IOException
        //    LocalVariableTable:
        //  Start  Length  Slot  Name     Signature
        //  -----  ------  ----  -------  ------------------------------
        //  0      29      0     this     Lcom/sun/codemodel/JCodeModel;
        //  0      29      1     destDir  Ljava/io/File;
        //  0      29      2     status   Ljava/io/PrintStream;
        //  9      20      3     out      Lcom/sun/codemodel/CodeWriter;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public void build(final File destDir) throws IOException {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: laload         
        //     1: dstore_3       
        //     2: lastore        
        //     3: frem           
        //     4: lmul           
        //     5: ldiv           
        //     6: lmul           
        //     7: ineg           
        //     8: lmul           
        //    Exceptions:
        //  throws java.io.IOException
        //    LocalVariableTable:
        //  Start  Length  Slot  Name     Signature
        //  -----  ------  ----  -------  ------------------------------
        //  0      9       0     this     Lcom/sun/codemodel/JCodeModel;
        //  0      9       1     destDir  Ljava/io/File;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public void build(final CodeWriter out) throws IOException {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: ladd           
        //     1: drem           
        //     2: drem           
        //     3: astore_1        /* out */
        //     4: ddiv           
        //     5: ladd           
        //     6: isub           
        //     7: lsub           
        //     8: frem           
        //     9: aconst_null    
        //    10: nop            
        //    11: aload           40
        //    13: dload_3        
        //    14: astore_1        /* out */
        //    15: fmul           
        //    16: ladd           
        //    17: fneg           
        //    18: ladd           
        //    19: laload         
        //    20: idiv           
        //    21: ladd           
        //    22: fdiv           
        //    23: dsub           
        //    24: laload         
        //    25: fstore_0        /* this */
        //    26: idiv           
        //    27: ladd           
        //    28: drem           
        //    29: drem           
        //    30: astore_1        /* out */
        //    31: ddiv           
        //    32: ladd           
        //    33: isub           
        //    34: lsub           
        //    35: frem           
        //    36: istore_0        /* this */
        //    37: aconst_null    
        //    38: nop            
        //    39: iload           106
        //    41: ladd           
        //    42: fneg           
        //    43: ladd           
        //    44: laload         
        //    Exceptions:
        //  throws java.io.IOException
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  ------------------------------
        //  13     25      2     i     Ljava/util/Iterator;
        //  0      45      0     this  Lcom/sun/codemodel/JCodeModel;
        //  0      45      1     out   Lcom/sun/codemodel/CodeWriter;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public JClass ref(final Class clazz) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: laload         
        //     1: dstore_3       
        //     2: fstore_0        /* this */
        //     3: idiv           
        //     4: ladd           
        //     5: drem           
        //     6: drem           
        //     7: aconst_null    
        //     8: nop            
        //     9: iconst_5       
        //    10: lsub           
        //    11: fdiv           
        //    12: isub           
        //    13: drem           
        //    14: pop            
        //    15: lmul           
        //    16: ineg           
        //    17: imul           
        //    18: aconst_null    
        //    19: nop            
        //    20: iload           40
        //    22: astore_1        /* clazz */
        //    23: fmul           
        //    24: ladd           
        //    25: fneg           
        //    26: ladd           
        //    27: laload         
        //    28: idiv           
        //    29: ladd           
        //    30: fdiv           
        //    31: dsub           
        //    32: laload         
        //    33: aastore        
        //    34: ineg           
        //    35: frem           
        //    36: lmul           
        //    37: fdiv           
        //    38: dsub           
        //    39: istore_0        /* this */
        //    40: dload_3        
        //    41: dup_x1         
        //    42: aconst_null    
        //    43: nop            
        //    44: iconst_3       
        //    45: idiv           
        //    46: lsub           
        //    47: fdiv           
        //    48: dsub           
        //    49: ineg           
        //    50: imul           
        //    51: aconst_null    
        //    52: nop            
        //    53: iconst_2       
        //    54: ladd           
        //    55: frem           
        //    56: frem           
        //    57: ladd           
        //    58: lshl           
        //    59: aconst_null    
        //    60: nop            
        //    61: fconst_2       
        //    LocalVariableTable:
        //  Start  Length  Slot  Name   Signature
        //  -----  ------  ----  -----  -----------------------------------------------
        //  0      62      0     this   Lcom/sun/codemodel/JCodeModel;
        //  0      62      1     clazz  Ljava/lang/Class;
        //  12     50      2     jrc    Lcom/sun/codemodel/JCodeModel$JReferencedClass;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public JClass ref(final String fullyQualifiedClassName) throws ClassNotFoundException {
        // 
        // This method could not be decompiled.
        // 
        // Could not show original bytecode, likely due to the same error.
        // 
        // The error that occurred was:
        // 
        // com.strobel.assembler.metadata.MethodBodyParseException: An error occurred while parsing the bytecode of method 'com/sun/codemodel/JCodeModel.ref:(Ljava/lang/String;)Lcom/sun/codemodel/JClass;'.
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
        //     at com.strobel.assembler.metadata.MethodReader.readBodyCore(MethodReader.java:291)
        //     at com.strobel.assembler.metadata.MethodReader.readBody(MethodReader.java:62)
        //     ... 16 more
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    static {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: nop            
        //     1: dcmpg          
        //     2: nop            
        //     3: lcmp           
        //     4: nop            
        //     5: nop            
        //     6: nop            
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    private class JReferencedClass extends JClass implements JDeclaration
    {
        private final Class _class;
        
        JReferencedClass(final Class _clazz) {
            super(JCodeModel.this);
            this._class = _clazz;
            assert !this._class.isArray();
        }
        
        public String name() {
            return this._class.getSimpleName().replace('$', '.');
        }
        
        public String fullName() {
            return this._class.getName().replace('$', '.');
        }
        
        public String binaryName() {
            return this._class.getName();
        }
        
        public JClass outer() {
            final Class p = this._class.getDeclaringClass();
            if (p == null) {
                return null;
            }
            return JCodeModel.this.ref(p);
        }
        
        public JPackage _package() {
            final String name = this.fullName();
            if (name.indexOf(91) != -1) {
                return JCodeModel.this._package("");
            }
            final int idx = name.lastIndexOf(46);
            if (idx < 0) {
                return JCodeModel.this._package("");
            }
            return JCodeModel.this._package(name.substring(0, idx));
        }
        
        public JClass _extends() {
            final Class sp = this._class.getSuperclass();
            if (sp != null) {
                return JCodeModel.this.ref(sp);
            }
            if (this.isInterface()) {
                return this.owner().ref(Object.class);
            }
            return null;
        }
        
        public Iterator<JClass> _implements() {
            final Class[] interfaces = this._class.getInterfaces();
            return new Iterator<JClass>() {
                private int idx = 0;
                
                public boolean hasNext() {
                    return this.idx < interfaces.length;
                }
                
                public JClass next() {
                    return JCodeModel.this.ref(interfaces[this.idx++]);
                }
                
                public void remove() {
                    throw new UnsupportedOperationException();
                }
            };
        }
        
        public boolean isInterface() {
            return this._class.isInterface();
        }
        
        public boolean isAbstract() {
            return Modifier.isAbstract(this._class.getModifiers());
        }
        
        public JPrimitiveType getPrimitiveType() {
            final Class v = JCodeModel.boxToPrimitive.get(this._class);
            if (v != null) {
                return JType.parse(JCodeModel.this, v.getName());
            }
            return null;
        }
        
        public boolean isArray() {
            return false;
        }
        
        public void declare(final JFormatter f) {
        }
        
        public JTypeVar[] typeParams() {
            return super.typeParams();
        }
        
        protected JClass substituteParams(final JTypeVar[] variables, final List<JClass> bindings) {
            return this;
        }
    }
}
