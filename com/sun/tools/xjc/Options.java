// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc;

import java.io.IOException;
import java.net.URLClassLoader;
import org.xml.sax.InputSource;
import java.util.List;
import org.xml.sax.EntityResolver;
import java.io.File;

public class Options
{
    public boolean debugMode;
    public boolean verbose;
    public boolean quiet;
    public boolean traceUnmarshaller;
    public boolean readOnly;
    public boolean generateValidationCode;
    public boolean generateMarshallingCode;
    public boolean generateUnmarshallingCode;
    public boolean generateValidatingUnmarshallingCode;
    public boolean strictCheck;
    public static final int STRICT = 1;
    public static final int EXTENSION = 2;
    public int compatibilityMode;
    public File targetDir;
    public EntityResolver entityResolver;
    public static final int SCHEMA_DTD = 0;
    public static final int SCHEMA_XMLSCHEMA = 1;
    public static final int SCHEMA_RELAXNG = 2;
    public static final int SCHEMA_WSDL = 3;
    private static final int SCHEMA_AUTODETECT = -1;
    private int schemaLanguage;
    public String defaultPackage;
    private final List grammars;
    private final List bindFiles;
    String proxyHost;
    String proxyPort;
    public boolean generateRuntime;
    public String runtimePackage;
    public final List enabledModelAugmentors;
    public static final Object[] codeAugmenters;
    public final List classpaths;
    
    public Options() {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: lstore_2       
        //     1: aconst_null    
        //     2: nop            
        //     3: iconst_2       
        //     4: aload_3        
        //     5: dneg           
        //     6: drem           
        //     7: isub           
        //     8: idiv           
        //     9: fconst_1       
        //    10: iconst_0       
        //    11: dup            
        //    12: aconst_null    
        //    13: lstore_2       
        //    14: aconst_null    
        //    15: nop            
        //    16: lconst_1       
        //    17: aload_3        
        //    18: lsub           
        //    19: ishl           
        //    20: ineg           
        //    21: lsub           
        //    22: fdiv           
        //    23: drem           
        //    24: lmul           
        //    25: ddiv           
        //    26: fdiv           
        //    27: aconst_null    
        //    28: nop            
        //    29: iconst_4       
        //    30: aload_3        
        //    31: ineg           
        //    32: ladd           
        //    33: frem           
        //    34: dsub           
        //    35: lsub           
        //    36: ineg           
        //    37: fconst_1       
        //    38: iconst_0       
        //    39: dup_x1         
        //    40: iconst_0       
        //    41: dup_x2         
        //    42: aconst_null    
        //    43: nop            
        //    44: iload_3        
        //    45: fstore_1       
        //    46: frem           
        //    47: lmul           
        //    48: fneg           
        //    49: lsub           
        //    50: frem           
        //    51: iaload         
        //    52: dstore_2       
        //    53: astore_1       
        //    54: astore_1       
        //    55: fstore_2       
        //    56: dstore_0        /* this */
        //    57: lstore_2       
        //    58: astore_1       
        //    59: swap           
        //    60: bastore        
        //    61: lstore_2       
        //    62: dastore        
        //    63: dstore_0        /* this */
        //    64: fstore_2       
        //    65: bastore        
        //    66: swap           
        //    67: sastore        
        //    68: fstore_2       
        //    69: dastore        
        //    70: aastore        
        //    71: dstore_2       
        //    72: iastore        
        //    73: astore_3       
        //    74: aconst_null    
        //    75: nop            
        //    76: dconst_0       
        //    77: aload_3        
        //    78: imul           
        //    79: ineg           
        //    80: ineg           
        //    81: irem           
        //    82: irem           
        //    83: frem           
        //    84: ddiv           
        //    85: ishl           
        //    86: lshl           
        //    87: fsub           
        //    88: lmul           
        //    89: idiv           
        //    90: lsub           
        //    91: aconst_null    
        //    92: nop            
        //    93: aconst_null    
        //    94: aload_3        
        //    95: fconst_1       
        //    96: iconst_0       
        //    97: dup2           
        //    98: iconst_0       
        //    99: dstore_2       
        //   100: aconst_null    
        //   101: nop            
        //   102: dload           68
        //   104: frem           
        //   105: lmul           
        //   106: fneg           
        //   107: lsub           
        //   108: frem           
        //   109: iaload         
        //   110: astore_2       
        //   111: dstore_2       
        //   112: aastore        
        //   113: aastore        
        //   114: dstore_2       
        //   115: astore_3       
        //   116: dstore_0        /* this */
        //   117: swap           
        //   118: lastore        
        //   119: dastore        
        //   120: iastore        
        //   121: pop2           
        //   122: dup            
        //   123: fstore_3       
        //   124: dstore_2       
        //   125: astore_1       
        //   126: fstore_2       
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  ---------------------------
        //  0      127     0     this  Lcom/sun/tools/xjc/Options;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public int getSchemaLanguage() {
        // 
        // This method could not be decompiled.
        // 
        // Could not show original bytecode, likely due to the same error.
        // 
        // The error that occurred was:
        // 
        // com.strobel.assembler.metadata.MethodBodyParseException: An error occurred while parsing the bytecode of method 'com/sun/tools/xjc/Options.getSchemaLanguage:()I'.
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
    
    public void setSchemaLanguage(final int _schemaLanguage) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: iconst_2       
        //     1: aload_3        
        //     2: irem           
        //     3: ddiv           
        //     4: frem           
        //     5: ineg           
        //    LocalVariableTable:
        //  Start  Length  Slot  Name             Signature
        //  -----  ------  ----  ---------------  ---------------------------
        //  0      6       0     this             Lcom/sun/tools/xjc/Options;
        //  0      6       1     _schemaLanguage  I
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public InputSource[] getGrammars() {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: ineg           
        //     1: aload_3        
        //     2: dadd           
        //     3: idiv           
        //     4: ladd           
        //     5: drem           
        //     6: drem           
        //     7: aload_3        
        //     8: fdiv           
        //     9: ladd           
        //    10: ldiv           
        //    11: lsub           
        //    12: aload_3        
        //    13: ladd           
        //    14: idiv           
        //    15: idiv           
        //    16: ddiv           
        //    17: dadd           
        //    18: ladd           
        //    19: ineg           
        //    20: ddiv           
        //    21: frem           
        //    22: aconst_null    
        //    23: nop            
        //    24: iload_1        
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  ---------------------------
        //  0      25      0     this  Lcom/sun/tools/xjc/Options;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public void addGrammar(final InputSource is) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: aconst_null    
        //     1: fneg           
        //     2: aconst_null    
        //     3: dneg           
        //     4: fconst_1       
        //     5: iconst_0       
        //     6: dadd           
        //     7: iconst_0       
        //     8: isub           
        //     9: iconst_4       
        //    10: iconst_0       
        //    11: lsub           
        //    12: fconst_1       
        //    13: iconst_0       
        //    14: fsub           
        //    15: aconst_null    
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  ---------------------------
        //  0      16      0     this  Lcom/sun/tools/xjc/Options;
        //  0      16      1     is    Lorg/xml/sax/InputSource;
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
    
    private InputSource absolutize(final InputSource is) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: aconst_null    
        //     1: nop            
        //     2: iconst_5       
        //     3: dload_2        
        //     4: dup_x2         
        //     5: dup2_x2        
        //     6: lstore_1        /* is */
        //     7: astore          93
        //     9: aload_1         /* is */
        //    10: dload_3        
        //    11: aconst_null    
        //    12: nop            
        //    13: iconst_0       
        //    14: dload_2        
        //    15: lstore_0        /* this */
        //    16: astore          is
        //    18: nop            
        //    19: iconst_2       
        //    20: dload_2         /* baseURL */
        //    21: lstore_0        /* this */
        //    22: astore          92
        //    24: astore          is
        //    26: nop            
        //    27: iconst_3       
        //    28: dload_3        
        //    29: lstore_0        /* this */
        //    30: dup2           
        //    31: lstore_1        /* is */
        //    32: dload_3        
        //    33: lstore_0        /* this */
        //    34: aconst_null    
        //    35: nop            
        //    36: aconst_null    
        //    37: dload_3        
        //    38: iconst_4       
        //    39: iconst_0       
        //    40: drem           
        //    LocalVariableTable:
        //  Start  Length  Slot  Name     Signature
        //  -----  ------  ----  -------  ---------------------------
        //  16     19      2     baseURL  Ljava/net/URL;
        //  39     0       2     e        Ljava/io/IOException;
        //  0      41      0     this     Lcom/sun/tools/xjc/Options;
        //  0      41      1     is       Lorg/xml/sax/InputSource;
        //    Exceptions:
        //  Try           Handler
        //  Start  End    Start  End    Type                 
        //  -----  -----  -----  -----  ---------------------
        //  0      35     38     41     Ljava/io/IOException;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException: 93
        //     at com.strobel.decompiler.ast.AstBuilder.performStackAnalysis(AstBuilder.java:2064)
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
    
    public InputSource[] getBindFiles() {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: fdiv           
        //     1: isub           
        //     2: fconst_1       
        //     3: iconst_0       
        //     4: iand           
        //     5: iconst_0       
        //     6: land           
        //     7: aconst_null    
        //     8: nop            
        //     9: iload           68
        //    11: frem           
        //    12: lmul           
        //    13: fneg           
        //    14: lsub           
        //    15: frem           
        //    16: iaload         
        //    17: astore_3       
        //    18: ddiv           
        //    19: ineg           
        //    20: lstore_2       
        //    21: fstore_3       
        //    22: lmul           
        //    23: idiv           
        //    24: lsub           
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  ---------------------------
        //  0      25      0     this  Lcom/sun/tools/xjc/Options;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public void addBindFile(final InputSource is) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: drem           
        //     1: ddiv           
        //     2: idiv           
        //     3: fneg           
        //     4: lsub           
        //     5: frem           
        //     6: laload         
        //     7: ineg           
        //     8: ddiv           
        //     9: ddiv           
        //    10: idiv           
        //    11: drem           
        //    12: laload         
        //    13: fstore_0        /* this */
        //    14: ladd           
        //    15: ineg           
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  ---------------------------
        //  0      16      0     this  Lcom/sun/tools/xjc/Options;
        //  0      16      1     is    Lorg/xml/sax/InputSource;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public URLClassLoader getUserClassLoader(final ClassLoader parent) {
        // 
        // This method could not be decompiled.
        // 
        // Could not show original bytecode, likely due to the same error.
        // 
        // The error that occurred was:
        // 
        // com.strobel.assembler.metadata.MethodBodyParseException: An error occurred while parsing the bytecode of method 'com/sun/tools/xjc/Options.getUserClassLoader:(Ljava/lang/ClassLoader;)Ljava/net/URLClassLoader;'.
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
    
    protected int parseArgument(final String[] args, final int i) throws BadCommandLineException, IOException {
        // 
        // This method could not be decompiled.
        // 
        // Could not show original bytecode, likely due to the same error.
        // 
        // The error that occurred was:
        // 
        // com.strobel.assembler.metadata.MethodBodyParseException: An error occurred while parsing the bytecode of method 'com/sun/tools/xjc/Options.parseArgument:([Ljava/lang/String;I)I'.
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
        // Caused by: java.lang.ClassCastException: com.strobel.assembler.ir.ConstantPool$MethodReferenceEntry cannot be cast to com.strobel.assembler.ir.ConstantPool$ConstantEntry
        //     at com.strobel.assembler.ir.ConstantPool.lookupConstant(ConstantPool.java:120)
        //     at com.strobel.assembler.metadata.ClassFileReader$Scope.lookupConstant(ClassFileReader.java:1319)
        //     at com.strobel.assembler.metadata.MethodReader.readBodyCore(MethodReader.java:286)
        //     at com.strobel.assembler.metadata.MethodReader.readBody(MethodReader.java:62)
        //     ... 16 more
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public void addCatalog(final File catalogFile) throws IOException {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: fdiv           
        //     1: astore_3       
        //     2: ladd           
        //     3: ldiv           
        //     4: lsub           
        //     5: aconst_null    
        //     6: nop            
        //     7: iload_0         /* this */
        //     8: dadd           
        //     9: ddiv           
        //    10: ldiv           
        //    11: laload         
        //    12: drem           
        //    13: lneg           
        //    14: fdiv           
        //    15: laload         
        //    16: ineg           
        //    17: ddiv           
        //    18: ddiv           
        //    19: idiv           
        //    20: drem           
        //    21: laload         
        //    22: ishl           
        //    23: fmul           
        //    24: dadd           
        //    25: laload         
        //    26: astore_2       
        //    27: lsub           
        //    28: drem           
        //    29: drem           
        //    30: ladd           
        //    31: dsub           
        //    32: lsub           
        //    33: drem           
        //    34: aconst_null    
        //    35: nop            
        //    36: dstore          40
        //    38: astore_1        /* catalogFile */
        //    39: fmul           
        //    40: ladd           
        //    Exceptions:
        //  throws java.io.IOException
        //    LocalVariableTable:
        //  Start  Length  Slot  Name         Signature
        //  -----  ------  ----  -----------  ---------------------------
        //  0      41      0     this         Lcom/sun/tools/xjc/Options;
        //  0      41      1     catalogFile  Ljava/io/File;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public void parseArguments(final String[] args) throws BadCommandLineException, IOException {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: fstore_0        /* this */
        //     1: idiv           
        //     2: ladd           
        //     3: drem           
        //     4: drem           
        //     5: astore_1        /* args */
        //     6: ddiv           
        //     7: ladd           
        //     8: isub           
        //     9: lsub           
        //    10: frem           
        //    11: aconst_null    
        //    12: nop            
        //    13: aload           40
        //    15: dload_3        
        //    16: astore_1        /* args */
        //    17: fmul           
        //    18: ladd           
        //    19: fneg           
        //    20: ladd           
        //    21: laload         
        //    22: idiv           
        //    23: ladd           
        //    24: fdiv           
        //    25: dsub           
        //    26: laload         
        //    27: fstore_0        /* this */
        //    28: idiv           
        //    29: ladd           
        //    30: drem           
        //    31: drem           
        //    32: astore_1        /* args */
        //    33: ddiv           
        //    34: ladd           
        //    35: isub           
        //    36: lsub           
        //    37: frem           
        //    38: istore_0        /* this */
        //    39: aconst_null    
        //    40: nop            
        //    41: dconst_0       
        //    42: fmul           
        //    43: ladd           
        //    44: fneg           
        //    45: ladd           
        //    46: laload         
        //    47: lneg           
        //    48: ineg           
        //    49: lmul           
        //    50: idiv           
        //    51: laload         
        //    52: astore_1        /* args */
        //    53: lmul           
        //    54: drem           
        //    55: ineg           
        //    56: aconst_null    
        //    57: nop            
        //    58: iconst_0       
        //    59: ladd           
        //    60: isub           
        //    61: isub           
        //    62: aconst_null    
        //    63: nop            
        //    64: iload           40
        //    66: astore_1        /* args */
        //    67: fmul           
        //    68: ladd           
        //    69: fneg           
        //    70: ladd           
        //    71: laload         
        //    72: idiv           
        //    73: ladd           
        //    74: fdiv           
        //    75: dsub           
        //    76: laload         
        //    77: iastore        
        //    78: fadd           
        //    79: fmul           
        //    80: lsub           
        //    81: dadd           
        //    82: ineg           
        //    83: istore_0        /* this */
        //    84: dload_3        
        //    85: dup_x1         
        //    86: aconst_null    
        //    87: nop            
        //    88: iconst_1       
        //    89: drem           
        //    90: lmul           
        //    91: ishr           
        //    92: lsub           
        //    93: aconst_null    
        //    94: nop            
        //    95: iconst_0       
        //    96: dload_2        
        //    97: dload_3        
        //    98: dstore_2       
        //    99: aconst_null    
        //   100: nop            
        //   101: iconst_4       
        //   102: ineg           
        //   103: ddiv           
        //   104: lstore_2       
        //   105: frem           
        //   106: frem           
        //   107: ladd           
        //   108: lshl           
        //   109: aconst_null    
        //   110: nop            
        //   111: dload_2        
        //   112: dload_2        
        //   113: dup_x2         
        //   114: astore_1        /* args */
        //   115: fmul           
        //   116: ladd           
        //   117: fneg           
        //   118: ladd           
        //   119: laload         
        //   120: idiv           
        //   121: ladd           
        //   122: fdiv           
        //   123: dsub           
        //   124: laload         
        //   125: iastore        
        //   126: fadd           
        //   127: fmul           
        //   128: lsub           
        //   129: dadd           
        //   130: ineg           
        //   131: istore_0        /* this */
        //   132: dload_3        
        //   133: dup_x2         
        //   134: astore_1        /* args */
        //   135: fmul           
        //   136: ladd           
        //   137: fneg           
        //   138: ladd           
        //   139: laload         
        //   140: idiv           
        //   141: ladd           
        //   142: fdiv           
        //   143: dsub           
        //   144: laload         
        //   145: iastore        
        //   146: fadd           
        //   147: fmul           
        //   148: lsub           
        //   149: dadd           
        //   150: ineg           
        //   151: istore_0        /* this */
        //   152: aconst_null    
        //   153: nop            
        //   154: iconst_2       
        //   155: ineg           
        //   156: ddiv           
        //   157: castore        
        //   158: dastore        
        //   159: astore_1        /* args */
        //   160: aconst_null    
        //   161: nop            
        //   162: bipush          40
        //   164: dload_3        
        //   165: astore_1        /* args */
        //   166: fmul           
        //   167: ladd           
        //   168: fneg           
        //   169: ladd           
        //   170: laload         
        //   171: fdiv           
        //   172: lsub           
        //   173: ineg           
        //   174: laload         
        //   175: castore        
        //   176: dastore        
        //   177: astore_1        /* args */
        //   178: istore_0        /* this */
        //   179: aconst_null    
        //   180: nop            
        //   181: dconst_0       
        //   182: ineg           
        //   183: ddiv           
        //   184: fstore_2       
        //   185: ishl           
        //   186: ineg           
        //   187: lsub           
        //   188: frem           
        //   189: fdiv           
        //   190: ladd           
        //   191: idiv           
        //   192: fstore_3       
        //   193: ddiv           
        //   194: frem           
        //   195: ldiv           
        //   196: aconst_null    
        //   197: nop            
        //   198: iload_3        
        //   199: dadd           
        //   200: ddiv           
        //   201: ldiv           
        //   202: laload         
        //   203: drem           
        //   204: lneg           
        //   205: fdiv           
        //   206: laload         
        //   207: ineg           
        //   208: ddiv           
        //   209: ddiv           
        //   210: idiv           
        //   211: drem           
        //   212: laload         
        //   213: ishl           
        //   214: fmul           
        //   215: dadd           
        //   216: laload         
        //   217: frem           
        //    Exceptions:
        //  throws com.sun.tools.xjc.BadCommandLineException
        //  throws java.io.IOException
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  ---------------------------
        //  27     26      3     j     I
        //  2      70      2     i     I
        //  0      218     0     this  Lcom/sun/tools/xjc/Options;
        //  0      218     1     args  [Ljava/lang/String;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public int guessSchemaLanguage() {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: ladd           
        //     1: fdiv           
        //     2: ddiv           
        //     3: fdiv           
        //     4: lmul           
        //     5: dadd           
        //     6: ladd           
        //     7: idiv           
        //     8: fstore_3       
        //     9: lmul           
        //    10: idiv           
        //    11: lsub           
        //    12: aconst_null    
        //    13: nop            
        //    14: bipush          40
        //    16: dload_3        
        //    17: astore_1       
        //    18: fmul           
        //    19: ladd           
        //    20: fneg           
        //    21: ladd           
        //    22: laload         
        //    23: lmul           
        //    24: ddiv           
        //    25: laload         
        //    26: fstore_3       
        //    27: lmul           
        //    28: idiv           
        //    29: lsub           
        //    30: istore_0        /* this */
        //    31: aconst_null    
        //    32: nop            
        //    33: fconst_0       
        //    34: dsub           
        //    35: lsub           
        //    36: ineg           
        //    37: aastore        
        //    38: lshl           
        //    39: drem           
        //    40: ineg           
        //    41: lsub           
        //    42: ldiv           
        //    43: dstore_2       
        //    44: isub           
        //    45: aconst_null    
        //    46: nop            
        //    47: fload_1         /* name */
        //    48: dload_2        
        //    49: astore_1        /* name */
        //    50: fmul           
        //    51: ladd           
        //    52: fneg           
        //    53: ladd           
        //    54: laload         
        //    55: fdiv           
        //    56: lsub           
        //    57: ineg           
        //    58: laload         
        //    59: castore        
        //    60: dastore        
        //    61: astore_1        /* name */
        //    62: istore_0        /* this */
        //    63: astore_1        /* name */
        //    64: fmul           
        //    65: ladd           
        //    66: fneg           
        //    67: ladd           
        //    68: laload         
        //    69: idiv           
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  ---------------------------
        //  0      70      0     this  Lcom/sun/tools/xjc/Options;
        //  35     35      1     name  Ljava/lang/String;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    private static Object[] findServices(final String className) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: astore_1       
        //     1: fmul           
        //     2: ladd           
        //     3: fneg           
        //     4: ladd           
        //     5: laload         
        //     6: idiv           
        //     7: ladd           
        //     8: fdiv           
        //     9: dsub           
        //    10: laload         
        //    11: bastore        
        //    12: imul           
        //    13: frem           
        //    14: ddiv           
        //    15: dneg           
        //    16: ladd           
        //    17: fadd           
        //    18: idiv           
        //    19: lsub           
        //    20: istore_0        /* className */
        //    21: dload_3        
        //    22: sastore        
        //    23: aconst_null    
        //    24: nop            
        //    25: iconst_3       
        //    26: lsub           
        //    27: ishl           
        //    28: lmul           
        //    LocalVariableTable:
        //  Start  Length  Slot  Name       Signature
        //  -----  ------  ----  ---------  ------------------
        //  0      29      0     className  Ljava/lang/String;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    private static Object[] findServices(final String className, final ClassLoader classLoader) {
        // 
        // This method could not be decompiled.
        // 
        // Could not show original bytecode, likely due to the same error.
        // 
        // The error that occurred was:
        // 
        // com.strobel.assembler.metadata.MethodBodyParseException: An error occurred while parsing the bytecode of method 'com/sun/tools/xjc/Options.findServices:(Ljava/lang/String;Ljava/lang/ClassLoader;)[Ljava/lang/Object;'.
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
        // Caused by: java.lang.IllegalArgumentException: Argument 'index' must be in the range [0, 552], but value was: 10316.
        //     at com.strobel.core.VerifyArgument.inRange(VerifyArgument.java:346)
        //     at com.strobel.assembler.ir.ConstantPool.get(ConstantPool.java:78)
        //     at com.strobel.assembler.metadata.ClassFileReader$Scope.lookupConstant(ClassFileReader.java:1313)
        //     at com.strobel.assembler.metadata.MethodReader.readBodyCore(MethodReader.java:293)
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
        //     0: irem           
        //     1: aconst_null    
        //     2: nop            
        //     3: iload           40
        //     5: dstore_2       
        //     6: dload_3        
        //     7: astore_1       
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
        //    18: aastore        
        //    19: ineg           
        //    20: frem           
        //    21: lmul           
        //    22: fdiv           
        //    23: dsub           
        //    24: istore_0       
        //    25: aconst_null    
        //    26: nop            
        //    27: sipush          27233
        //    30: fneg           
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
}
