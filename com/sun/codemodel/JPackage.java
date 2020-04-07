// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.codemodel;

import java.io.IOException;
import java.io.File;
import java.util.Iterator;
import java.util.Set;
import java.util.Map;

public final class JPackage implements JDeclaration, JGenerable, JClassContainer
{
    private String name;
    private final JCodeModel owner;
    private final Map classes;
    private final Set resources;
    private final Map upperCaseClassMap;
    
    JPackage(final String name, final JCodeModel cw) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     1: ddiv           
        //     2: isub           
        //     3: lsub           
        //     4: astore_2        /* cw */
        //     5: ddiv           
        //     6: isub           
        //     7: lsub           
        //     8: idiv           
        //     9: aconst_null    
        //    10: nop            
        //    11: aload           105
        //    13: drem           
        //    14: fstore_0        /* this */
        //    15: ladd           
        //    16: drem           
        //    17: lsub           
        //    18: aastore        
        //    19: lsub           
        //    20: fdiv           
        //    21: drem           
        //    22: lmul           
        //    23: ineg           
        //    24: lmul           
        //    25: fneg           
        //    26: lsub           
        //    27: fstore_3       
        //    28: lmul           
        //    29: idiv           
        //    30: lsub           
        //    31: aastore        
        //    32: lshl           
        //    33: drem           
        //    34: ineg           
        //    35: lsub           
        //    36: ldiv           
        //    37: aconst_null    
        //    38: nop            
        //    39: iconst_3       
        //    40: idiv           
        //    41: lsub           
        //    42: fdiv           
        //    43: dsub           
        //    44: ineg           
        //    45: imul           
        //    46: aconst_null    
        //    47: nop            
        //    48: fconst_0       
        //    49: idiv           
        //    50: ladd           
        //    51: drem           
        //    52: ineg           
        //    53: dstore_2        /* cw */
        //    54: fdiv           
        //    55: isub           
        //    56: lsub           
        //    57: ishl           
        //    58: iastore        
        //    59: fsub           
        //    60: aconst_null    
        //    61: nop            
        //    62: iconst_1       
        //    63: dload_2         /* cw */
        //    64: dstore_2        /* cw */
        //    65: dload_3         /* dots */
        //    66: dstore_2        /* cw */
        //    67: aconst_null    
        //    68: nop            
        //    69: lconst_0       
        //    70: drem           
        //    71: lneg           
        //    72: fadd           
        //    73: drem           
        //    74: ineg           
        //    75: frem           
        //    76: lmul           
        //    77: fdiv           
        //    78: dsub           
        //    79: aconst_null    
        //    80: nop            
        //    81: lload           40
        //    83: dstore_2        /* cw */
        //    84: dstore_2        /* cw */
        //    85: dload_3         /* dots */
        //    86: astore_1        /* name */
        //    87: fmul           
        //    88: ladd           
        //    89: fneg           
        //    90: ladd           
        //    91: laload         
        //    92: idiv           
        //    93: ladd           
        //    94: fdiv           
        //    95: dsub           
        //    96: laload         
        //    97: aastore        
        //    98: ineg           
        //    99: frem           
        //   100: lmul           
        //   101: fdiv           
        //   102: dsub           
        //   103: istore_0        /* this */
        //   104: aconst_null    
        //   105: nop            
        //   106: iconst_5       
        //   107: swap           
        //   108: irem           
        //   109: ladd           
        //   110: dadd           
        //   111: dmul           
        //   112: ladd           
        //   113: dsub           
        //   114: lsub           
        //   115: aconst_null    
        //   116: nop            
        //   117: iload_1         /* name */
        //   118: dadd           
        //   119: ddiv           
        //   120: ldiv           
        //   121: laload         
        //   122: drem           
        //   123: lneg           
        //   124: fdiv           
        //   125: laload         
        //   126: dadd           
        //   127: ddiv           
        //   128: isub           
        //   129: lsub           
        //   130: ldiv           
        //   131: ddiv           
        //   132: isub           
        //   133: lsub           
        //   134: idiv           
        //   135: laload         
        //   136: fstore_0        /* this */
        //   137: idiv           
        //   138: ladd           
        //   139: drem           
        //   140: drem           
        //   141: bastore        
        //   142: lshl           
        //   143: irem           
        //   144: lsub           
        //   145: aconst_null    
        //   146: nop            
        //   147: iconst_2       
        //   148: fstore_0        /* this */
        //   149: astore_1        /* name */
        //   150: lstore_2        /* cw */
        //   151: aastore        
        //   152: aastore        
        //   153: aconst_null    
        //   154: nop            
        //   155: lconst_0       
        //   156: dstore_2        /* cw */
        //   157: astore_3        /* dots */
        //   158: bastore        
        //   159: fstore_2        /* cw */
        //   160: dastore        
        //   161: fstore_3        /* dots */
        //   162: lstore_2        /* cw */
        //   163: fstore_0        /* this */
        //   164: fstore_2        /* cw */
        //   165: aconst_null    
        //   166: nop            
        //   167: fconst_2       
        //   168: fmul           
        //   169: ladd           
        //   170: fneg           
        //   171: ladd           
        //   172: laload         
        //   173: lneg           
        //   174: ineg           
        //   175: lmul           
        //   176: idiv           
        //   177: laload         
        //   178: astore_2        /* cw */
        //   179: ladd           
        //   180: irem           
        //   181: aconst_null    
        //   182: nop            
        //   183: fconst_0       
        //   184: dadd           
        //   185: ddiv           
        //   186: fdiv           
        //   187: ineg           
        //   188: ladd           
        //   189: lmul           
        //   190: fdiv           
        //   191: drem           
        //   192: astore_0        /* this */
        //   193: lsub           
        //   194: lshl           
        //   195: aconst_null    
        //   196: nop            
        //   197: iconst_0       
        //   198: dsub           
        //   199: lsub           
        //   200: ineg           
        //   201: aconst_null    
        //   202: nop            
        //   203: dload_0         /* this */
        //   204: dload_2         /* cw */
        //   205: astore_1        /* name */
        //   206: fmul           
        //   207: ladd           
        //   208: fneg           
        //   209: ladd           
        //   210: laload         
        //   211: idiv           
        //   212: ladd           
        //   213: fdiv           
        //   214: dsub           
        //   215: laload         
        //   216: iastore        
        //   217: fadd           
        //   218: fmul           
        //   219: lsub           
        //   220: dadd           
        //   221: ineg           
        //   222: istore_0        /* this */
        //   223: dload_3         /* dots */
        //   224: astore_1        /* name */
        //   225: fmul           
        //   226: ladd           
        //   227: fneg           
        //   228: ladd           
        //   229: laload         
        //   230: idiv           
        //   231: ladd           
        //   232: fdiv           
        //   233: dsub           
        //   234: laload         
        //   235: iastore        
        //   236: fadd           
        //   237: fmul           
        //   238: lsub           
        //   239: dadd           
        //   240: ineg           
        //   241: istore_0        /* this */
        //   242: aconst_null    
        //   243: nop            
        //   244: fload_2         /* cw */
        //   245: dload_2         /* cw */
        //   246: astore_1        /* name */
        //   247: dadd           
        //   248: ddiv           
        //   249: ldiv           
        //   250: laload         
        //   251: drem           
        //   252: lneg           
        //   253: fdiv           
        //   254: laload         
        //   255: dadd           
        //   256: ddiv           
        //   257: isub           
        //   258: lsub           
        //   259: ldiv           
        //   260: ddiv           
        //   261: isub           
        //   262: lsub           
        //   263: idiv           
        //   264: laload         
        //   265: dstore_3        /* dots */
        //   266: fstore_1        /* name */
        //   267: lsub           
        //   268: fsub           
        //   269: lmul           
        //   270: fdiv           
        //   271: lsub           
        //   272: isub           
        //   273: fstore_0        /* this */
        //   274: idiv           
        //   275: ladd           
        //   276: drem           
        //   277: drem           
        //   278: istore_0        /* this */
        //   279: dload_3         /* dots */
        //   280: sastore        
        //   281: aconst_null    
        //   282: nop            
        //   283: sastore        
        //   284: dload_2         /* cw */
        //   285: astore_1        /* name */
        //   286: dadd           
        //   287: ddiv           
        //   288: ldiv           
        //   289: laload         
        //   290: drem           
        //   291: lneg           
        //   292: fdiv           
        //   293: laload         
        //   294: dadd           
        //   295: ddiv           
        //   296: isub           
        //   297: lsub           
        //   298: ldiv           
        //   299: ddiv           
        //   300: isub           
        //   301: lsub           
        //   302: idiv           
        //   303: laload         
        //   304: dstore_3        /* dots */
        //   305: fstore_0        /* this */
        //   306: idiv           
        //   307: ladd           
        //   308: drem           
        //   309: drem           
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  ------------------------------
        //  43     9       3     msg   Ljava/lang/String;
        //  118    10      6     msg   Ljava/lang/String;
        //  177    10      6     msg   Ljava/lang/String;
        //  231    10      6     msg   Ljava/lang/String;
        //  74     169     5     c     C
        //  57     192     4     i     I
        //  269    10      4     msg   Ljava/lang/String;
        //  0      310     0     this  Lcom/sun/codemodel/JPackage;
        //  0      310     1     name  Ljava/lang/String;
        //  0      310     2     cw    Lcom/sun/codemodel/JCodeModel;
        //  54     256     3     dots  I
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public JClassContainer parentContainer() {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: iastore        
        //     1: fadd           
        //     2: fmul           
        //     3: lsub           
        //     4: dadd           
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  ----------------------------
        //  0      5       0     this  Lcom/sun/codemodel/JPackage;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public JPackage parent() {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: isub           
        //     1: aconst_null    
        //     2: nop            
        //     3: aload_3        
        //     4: dload_2        
        //     5: astore_1       
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
        //    23: dload_3        
        //    24: astore_1        /* idx */
        //    25: fmul           
        //    26: ladd           
        //    27: fneg           
        //    28: ladd           
        //    29: laload         
        //    30: idiv           
        //    31: ladd           
        //    32: fdiv           
        //    33: dsub           
        //    34: laload         
        //    35: aastore        
        //    36: ineg           
        //    37: frem           
        //    38: lmul           
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  ----------------------------
        //  0      39      0     this  Lcom/sun/codemodel/JPackage;
        //  22     17      1     idx   I
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public JDefinedClass _class(final int mods, final String name) throws JClassAlreadyExistsException {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: ddiv           
        //     1: frem           
        //     2: astore_3       
        //     3: ladd           
        //     4: ldiv           
        //     5: lsub           
        //     6: aconst_null    
        //     7: nop            
        //    Exceptions:
        //  throws com.sun.codemodel.JClassAlreadyExistsException
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  ----------------------------
        //  0      8       0     this  Lcom/sun/codemodel/JPackage;
        //  0      8       1     mods  I
        //  0      8       2     name  Ljava/lang/String;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public JDefinedClass _class(final int mods, final String name, final boolean isInterface) throws JClassAlreadyExistsException {
        // 
        // This method could not be decompiled.
        // 
        // Could not show original bytecode, likely due to the same error.
        // 
        // The error that occurred was:
        // 
        // com.strobel.assembler.metadata.MethodBodyParseException: An error occurred while parsing the bytecode of method 'com/sun/codemodel/JPackage._class:(ILjava/lang/String;Z)Lcom/sun/codemodel/JDefinedClass;'.
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
        // Caused by: java.lang.IllegalArgumentException: Argument 'index' must be in the range [0, 383], but value was: 27233.
        //     at com.strobel.core.VerifyArgument.inRange(VerifyArgument.java:346)
        //     at com.strobel.assembler.ir.ConstantPool.get(ConstantPool.java:78)
        //     at com.strobel.assembler.metadata.ClassFileReader$Scope.lookupConstant(ClassFileReader.java:1313)
        //     at com.strobel.assembler.metadata.MethodReader.readBodyCore(MethodReader.java:293)
        //     at com.strobel.assembler.metadata.MethodReader.readBody(MethodReader.java:62)
        //     ... 16 more
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public JDefinedClass _class(final String name) throws JClassAlreadyExistsException {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: dsub           
        //     1: istore_0        /* this */
        //     2: aconst_null    
        //     3: nop            
        //     4: fload_1         /* name */
        //     5: dload_2        
        //     6: astore_1        /* name */
        //    Exceptions:
        //  throws com.sun.codemodel.JClassAlreadyExistsException
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  ----------------------------
        //  0      7       0     this  Lcom/sun/codemodel/JPackage;
        //  0      7       1     name  Ljava/lang/String;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public JDefinedClass _getClass(final String name) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: lsub           
        //     1: idiv           
        //     2: laload         
        //     3: dstore_3       
        //     4: fstore_3       
        //     5: ddiv           
        //     6: frem           
        //     7: ldiv           
        //     8: ladd           
        //     9: ineg           
        //    10: ineg           
        //    11: lsub           
        //    12: frem           
        //    13: istore_0        /* this */
        //    14: aconst_null    
        //    15: nop            
        //    16: lload_3        
        //    17: dload_2        
        //    18: fstore_0        /* this */
        //    19: dload_3        
        //    20: astore_1        /* name */
        //    21: dadd           
        //    22: ddiv           
        //    23: ldiv           
        //    24: laload         
        //    25: drem           
        //    26: lneg           
        //    27: fdiv           
        //    28: laload         
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  ----------------------------
        //  0      29      0     this  Lcom/sun/codemodel/JPackage;
        //  0      29      1     name  Ljava/lang/String;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public JDefinedClass _interface(final int mods, final String name) throws JClassAlreadyExistsException {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: iconst_2       
        //     1: dneg           
        //     2: frem           
        //     3: lmul           
        //     4: ineg           
        //     5: lsub           
        //     6: aconst_null    
        //     7: nop            
        //    Exceptions:
        //  throws com.sun.codemodel.JClassAlreadyExistsException
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  ----------------------------
        //  0      8       0     this  Lcom/sun/codemodel/JPackage;
        //  0      8       1     mods  I
        //  0      8       2     name  Ljava/lang/String;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public JDefinedClass _interface(final String name) throws JClassAlreadyExistsException {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: ldiv           
        //     1: laload         
        //     2: drem           
        //     3: lneg           
        //     4: fdiv           
        //     5: laload         
        //     6: dadd           
        //    Exceptions:
        //  throws com.sun.codemodel.JClassAlreadyExistsException
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  ----------------------------
        //  0      7       0     this  Lcom/sun/codemodel/JPackage;
        //  0      7       1     name  Ljava/lang/String;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public JResourceFile addResourceFile(final JResourceFile rsrc) {
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
        //    10: laload         
        //    11: fstore_0        /* this */
        //    12: ddiv           
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  ---------------------------------
        //  0      13      0     this  Lcom/sun/codemodel/JPackage;
        //  0      13      1     rsrc  Lcom/sun/codemodel/JResourceFile;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public boolean hasResourceFile(final String name) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: istore_0        /* this */
        //     1: dload_3        
        //     2: astore_1        /* name */
        //     3: fmul           
        //     4: ladd           
        //     5: fneg           
        //     6: ladd           
        //     7: laload         
        //     8: lmul           
        //     9: ddiv           
        //    10: laload         
        //    11: iastore        
        //    12: lneg           
        //    13: ineg           
        //    14: irem           
        //    15: lneg           
        //    16: ineg           
        //    17: aastore        
        //    18: ineg           
        //    19: frem           
        //    20: lsub           
        //    21: ladd           
        //    22: ldiv           
        //    23: istore_0        /* this */
        //    24: aconst_null    
        //    25: nop            
        //    26: aload           40
        //    28: astore_1        /* name */
        //    29: fmul           
        //    30: ladd           
        //    31: fneg           
        //    32: ladd           
        //    33: laload         
        //    34: lmul           
        //    35: ddiv           
        //    36: laload         
        //    37: iastore        
        //    38: lneg           
        //    39: ineg           
        //    40: irem           
        //    41: lneg           
        //    42: ineg           
        //    43: aastore        
        //    44: ineg           
        //    45: frem           
        //    46: lsub           
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  ---------------------------------
        //  29     13      3     r     Lcom/sun/codemodel/JResourceFile;
        //  10     35      2     itr   Ljava/util/Iterator;
        //  0      47      0     this  Lcom/sun/codemodel/JPackage;
        //  0      47      1     name  Ljava/lang/String;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public Iterator propertyFiles() {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: fneg           
        //     1: ladd           
        //     2: laload         
        //     3: lmul           
        //     4: ddiv           
        //     5: laload         
        //     6: pop            
        //     7: frem           
        //     8: lmul           
        //     9: ineg           
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  ----------------------------
        //  0      10      0     this  Lcom/sun/codemodel/JPackage;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public void remove(final JClass c) {
        // 
        // This method could not be decompiled.
        // 
        // Could not show original bytecode, likely due to the same error.
        // 
        // The error that occurred was:
        // 
        // com.strobel.assembler.metadata.MethodBodyParseException: An error occurred while parsing the bytecode of method 'com/sun/codemodel/JPackage.remove:(Lcom/sun/codemodel/JClass;)V'.
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
    
    public JClass ref(final String name) {
        // 
        // This method could not be decompiled.
        // 
        // Could not show original bytecode, likely due to the same error.
        // 
        // The error that occurred was:
        // 
        // com.strobel.assembler.metadata.MethodBodyParseException: An error occurred while parsing the bytecode of method 'com/sun/codemodel/JPackage.ref:(Ljava/lang/String;)Lcom/sun/codemodel/JClass;'.
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
        // Caused by: java.lang.ClassCastException: com.strobel.assembler.ir.ConstantPool$StringConstantEntry cannot be cast to com.strobel.assembler.ir.ConstantPool$FieldReferenceEntry
        //     at com.strobel.assembler.metadata.ClassFileReader$Scope.lookupField(ClassFileReader.java:1233)
        //     at com.strobel.assembler.metadata.MethodReader.readBodyCore(MethodReader.java:214)
        //     at com.strobel.assembler.metadata.MethodReader.readBody(MethodReader.java:62)
        //     ... 16 more
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public JPackage subPackage(final String pkg) {
        // 
        // This method could not be decompiled.
        // 
        // Could not show original bytecode, likely due to the same error.
        // 
        // The error that occurred was:
        // 
        // com.strobel.assembler.metadata.MethodBodyParseException: An error occurred while parsing the bytecode of method 'com/sun/codemodel/JPackage.subPackage:(Ljava/lang/String;)Lcom/sun/codemodel/JPackage;'.
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
        // Caused by: java.lang.ClassCastException: com.strobel.assembler.ir.ConstantPool$StringConstantEntry cannot be cast to com.strobel.assembler.ir.ConstantPool$FieldReferenceEntry
        //     at com.strobel.assembler.metadata.ClassFileReader$Scope.lookupField(ClassFileReader.java:1233)
        //     at com.strobel.assembler.metadata.MethodReader.readBodyCore(MethodReader.java:214)
        //     at com.strobel.assembler.metadata.MethodReader.readBody(MethodReader.java:62)
        //     ... 16 more
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public Iterator classes() {
        // 
        // This method could not be decompiled.
        // 
        // Could not show original bytecode, likely due to the same error.
        // 
        // The error that occurred was:
        // 
        // com.strobel.assembler.metadata.MethodBodyParseException: An error occurred while parsing the bytecode of method 'com/sun/codemodel/JPackage.classes:()Ljava/util/Iterator;'.
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
        //     at com.strobel.assembler.metadata.Buffer.readShort(Buffer.java:219)
        //     at com.strobel.assembler.metadata.MethodReader.readBodyCore(MethodReader.java:231)
        //     at com.strobel.assembler.metadata.MethodReader.readBody(MethodReader.java:62)
        //     ... 16 more
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public boolean isDefined(final String classLocalName) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: nop            
        //     1: ifeq            ifeq           !!! ERROR
        //     4: nop            
        //     5: f2i            
        //     6: nop            
        //     7: nop            
        //     8: nop            
        //     9: aload_2         /* itr */
        //    10: nop            
        //    11: aconst_null    
        //    12: nop            
        //    13: aconst_null    
        //    14: nop            
        //    15: nop            
        //    16: nop            
        //    17: iconst_m1      
        //    18: iconst_1       
        //    19: ireturn        
        //    20: nop            
        //    21: nop            
        //    22: nop            
        //    23: iconst_m1      
        //    24: nop            
        //    25: f2l            
        //    26: nop            
        //    27: nop            
        //    28: nop            
        //    29: iconst_3       
        //    30: nop            
        //    31: aconst_null    
        //    32: nop            
        //    33: nop            
        //    34: nop            
        //    35: iushr          
        //    36: nop            
        //    LocalVariableTable:
        //  Start  Length  Slot  Name            Signature
        //  -----  ------  ----  --------------  ----------------------------
        //  0      37      0     this            Lcom/sun/codemodel/JPackage;
        //  0      37      1     classLocalName  Ljava/lang/String;
        //  5      32      2     itr             Ljava/util/Iterator;
        // 
        // The error that occurred was:
        // 
        // java.lang.NullPointerException
        //     at com.strobel.assembler.flowanalysis.ControlFlowGraphBuilder.getInstructionIndex(ControlFlowGraphBuilder.java:756)
        //     at com.strobel.assembler.flowanalysis.ControlFlowGraphBuilder.calculateIncomingJumps(ControlFlowGraphBuilder.java:122)
        //     at com.strobel.assembler.flowanalysis.ControlFlowGraphBuilder.build(ControlFlowGraphBuilder.java:96)
        //     at com.strobel.assembler.flowanalysis.ControlFlowGraphBuilder.build(ControlFlowGraphBuilder.java:60)
        //     at com.strobel.decompiler.ast.AstBuilder$FinallyInlining.preProcess(AstBuilder.java:4670)
        //     at com.strobel.decompiler.ast.AstBuilder$FinallyInlining.<init>(AstBuilder.java:4160)
        //     at com.strobel.decompiler.ast.AstBuilder$FinallyInlining.run(AstBuilder.java:4275)
        //     at com.strobel.decompiler.ast.AstBuilder.build(AstBuilder.java:100)
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
    
    public final boolean isUnnamed() {
        // 
        // This method could not be decompiled.
        // 
        // Could not show original bytecode, likely due to the same error.
        // 
        // The error that occurred was:
        // 
        // com.strobel.assembler.metadata.MethodBodyParseException: An error occurred while parsing the bytecode of method 'com/sun/codemodel/JPackage.isUnnamed:()Z'.
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
        // Caused by: java.lang.ClassCastException: com.strobel.assembler.ir.ConstantPool$MethodReferenceEntry cannot be cast to com.strobel.assembler.ir.ConstantPool$FieldReferenceEntry
        //     at com.strobel.assembler.metadata.ClassFileReader$Scope.lookupField(ClassFileReader.java:1233)
        //     at com.strobel.assembler.metadata.MethodReader.readBodyCore(MethodReader.java:214)
        //     at com.strobel.assembler.metadata.MethodReader.readBody(MethodReader.java:62)
        //     ... 16 more
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public String name() {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: nop            
        //     1: nop            
        //     2: iconst_1       
        //     3: nop            
        //     4: aconst_null    
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  ----------------------------
        //  0      5       0     this  Lcom/sun/codemodel/JPackage;
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
    
    public final JCodeModel owner() {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: nop            
        //     1: nop            
        //     2: nop            
        //     3: fcmpg          
        //     4: nop            
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  ----------------------------
        //  0      5       0     this  Lcom/sun/codemodel/JPackage;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    File toPath(final File dir) {
        // 
        // This method could not be decompiled.
        // 
        // Could not show original bytecode, likely due to the same error.
        // 
        // The error that occurred was:
        // 
        // com.strobel.assembler.metadata.MethodBodyParseException: An error occurred while parsing the bytecode of method 'com/sun/codemodel/JPackage.toPath:(Ljava/io/File;)Ljava/io/File;'.
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
    
    public void declare(final JFormatter f) {
        // 
        // This method could not be decompiled.
        // 
        // Could not show original bytecode, likely due to the same error.
        // 
        // The error that occurred was:
        // 
        // com.strobel.assembler.metadata.MethodBodyParseException: An error occurred while parsing the bytecode of method 'com/sun/codemodel/JPackage.declare:(Lcom/sun/codemodel/JFormatter;)V'.
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
        //     at com.strobel.assembler.metadata.signatures.SignatureParser.parseFieldTypeSignature(SignatureParser.java:176)
        //     at com.strobel.assembler.metadata.signatures.SignatureParser.parseTypeSignature(SignatureParser.java:324)
        //     at com.strobel.assembler.metadata.signatures.SignatureParser.parseTypeSignature(SignatureParser.java:94)
        //     at com.strobel.assembler.metadata.MetadataParser.parseTypeSignature(MetadataParser.java:120)
        //     at com.strobel.assembler.metadata.ClassFileReader$Scope.lookupType(ClassFileReader.java:1228)
        //     at com.strobel.assembler.metadata.MethodReader.readBodyCore(MethodReader.java:186)
        //     at com.strobel.assembler.metadata.MethodReader.readBody(MethodReader.java:62)
        //     ... 16 more
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public void generate(final JFormatter f) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: iconst_3       
        //     1: nop            
        //     2: dstore_1        /* f */
        //     3: nop            
        //     4: lload_1         /* f */
        //     5: nop            
        //     6: if_acmpeq       172
        //     9: nop            
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  ------------------------------
        //  0      10      0     this  Lcom/sun/codemodel/JPackage;
        //  0      10      1     f     Lcom/sun/codemodel/JFormatter;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException: -9
        //     at com.strobel.assembler.flowanalysis.ControlFlowGraphBuilder.calculateIncomingJumps(ControlFlowGraphBuilder.java:122)
        //     at com.strobel.assembler.flowanalysis.ControlFlowGraphBuilder.build(ControlFlowGraphBuilder.java:96)
        //     at com.strobel.assembler.flowanalysis.ControlFlowGraphBuilder.build(ControlFlowGraphBuilder.java:60)
        //     at com.strobel.decompiler.ast.AstBuilder$FinallyInlining.preProcess(AstBuilder.java:4670)
        //     at com.strobel.decompiler.ast.AstBuilder$FinallyInlining.<init>(AstBuilder.java:4160)
        //     at com.strobel.decompiler.ast.AstBuilder$FinallyInlining.run(AstBuilder.java:4275)
        //     at com.strobel.decompiler.ast.AstBuilder.build(AstBuilder.java:100)
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
    
    void build(final CodeWriter out) throws IOException {
        // 
        // This method could not be decompiled.
        // 
        // Could not show original bytecode, likely due to the same error.
        // 
        // The error that occurred was:
        // 
        // com.strobel.assembler.metadata.MethodBodyParseException: An error occurred while parsing the bytecode of method 'com/sun/codemodel/JPackage.build:(Lcom/sun/codemodel/CodeWriter;)V'.
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
        // Caused by: java.lang.ArrayIndexOutOfBoundsException: 65752
        //     at com.strobel.assembler.metadata.MethodReader.readBodyCore(MethodReader.java:316)
        //     at com.strobel.assembler.metadata.MethodReader.readBody(MethodReader.java:62)
        //     ... 16 more
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
}
