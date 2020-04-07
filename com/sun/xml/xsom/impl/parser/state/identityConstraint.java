// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.xml.xsom.impl.parser.state;

import org.xml.sax.SAXException;
import org.xml.sax.Attributes;
import com.sun.xml.xsom.impl.parser.NGCCRuntimeEx;

class identityConstraint extends NGCCHandler
{
    protected final NGCCRuntimeEx $runtime;
    private int $_ngcc_current_state;
    protected String $uri;
    protected String $localName;
    protected String $qname;
    
    public final NGCCRuntime getRuntime() {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: idiv           
        //     1: laload         
        //     2: aastore        
        //     3: dadd           
        //     4: imul           
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  -------------------------------------------------------
        //  0      5       0     this  Lcom/sun/xml/xsom/impl/parser/state/identityConstraint;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public identityConstraint(final NGCCHandler parent, final NGCCEventSource source, final NGCCRuntimeEx runtime, final int cookie) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: fdiv           
        //     1: dsub           
        //     2: aconst_null    
        //     3: nop            
        //     4: iconst_3       
        //     5: lsub           
        //     6: lrem           
        //     7: lneg           
        //     8: ladd           
        //     9: idiv           
        //    10: drem           
        //    11: aconst_null    
        //    12: nop            
        //    13: sipush          26469
        //    16: ineg           
        //    17: lstore_2        /* source */
        //    18: ineg           
        //    19: ineg           
        //    LocalVariableTable:
        //  Start  Length  Slot  Name     Signature
        //  -----  ------  ----  -------  -------------------------------------------------------
        //  0      20      0     this     Lcom/sun/xml/xsom/impl/parser/state/identityConstraint;
        //  0      20      1     parent   Lcom/sun/xml/xsom/impl/parser/state/NGCCHandler;
        //  0      20      2     source   Lcom/sun/xml/xsom/impl/parser/state/NGCCEventSource;
        //  0      20      3     runtime  Lcom/sun/xml/xsom/impl/parser/NGCCRuntimeEx;
        //  0      20      4     cookie   I
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public identityConstraint(final NGCCRuntimeEx runtime) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: aconst_null    
        //     1: nop            
        //     2: dastore        
        //     3: dload_2        
        //     4: dstore_2       
        //     5: astore_1        /* runtime */
        //     6: fmul           
        //     7: ladd           
        //     8: fneg           
        //    LocalVariableTable:
        //  Start  Length  Slot  Name     Signature
        //  -----  ------  ----  -------  -------------------------------------------------------
        //  0      9       0     this     Lcom/sun/xml/xsom/impl/parser/state/identityConstraint;
        //  0      9       1     runtime  Lcom/sun/xml/xsom/impl/parser/NGCCRuntimeEx;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public void enterElement(final String $__uri, final String $__local, final String $__qname, final Attributes $attrs) throws SAXException {
        // 
        // This method could not be decompiled.
        // 
        // Could not show original bytecode, likely due to the same error.
        // 
        // The error that occurred was:
        // 
        // com.strobel.assembler.metadata.MethodBodyParseException: An error occurred while parsing the bytecode of method 'com/sun/xml/xsom/impl/parser/state/identityConstraint.enterElement:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Lorg/xml/sax/Attributes;)V'.
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
        // Caused by: java.lang.IllegalArgumentException: Argument 'index' must be in the range [0, 170], but value was: 10316.
        //     at com.strobel.core.VerifyArgument.inRange(VerifyArgument.java:346)
        //     at com.strobel.assembler.ir.ConstantPool.get(ConstantPool.java:78)
        //     at com.strobel.assembler.metadata.ClassFileReader$Scope.lookupType(ClassFileReader.java:1218)
        //     at com.strobel.assembler.metadata.MethodReader.readBodyCore(MethodReader.java:186)
        //     at com.strobel.assembler.metadata.MethodReader.readBody(MethodReader.java:62)
        //     ... 16 more
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public void leaveElement(final String $__uri, final String $__local, final String $__qname) throws SAXException {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: fdiv           
        //     1: fdiv           
        //     2: ddiv           
        //     3: ineg           
        //     4: ladd           
        //     5: ineg           
        //     6: lmul           
        //     7: ddiv           
        //     8: fdiv           
        //     9: fstore_0        /* this */
        //    10: ddiv           
        //    11: fdiv           
        //    12: ineg           
        //    13: lsub           
        //    14: ishl           
        //    15: ineg           
        //    16: istore_0        /* this */
        //    17: dload_3         /* $__qname */
        //    18: sastore        
        //    19: aconst_null    
        //    20: nop            
        //    21: lload           111
        //    23: fdiv           
        //    24: astore_1        /* $__uri */
        //    25: lsub           
        //    26: ladd           
        //    27: fneg           
        //    28: lsub           
        //    29: fstore_2        /* $__local */
        //    30: idiv           
        //    31: lsub           
        //    32: ldiv           
        //    33: lsub           
        //    34: fdiv           
        //    35: ineg           
        //    36: fstore_0        /* this */
        //    37: ddiv           
        //    38: fdiv           
        //    39: drem           
        //    40: lneg           
        //    41: ldiv           
        //    42: lsub           
        //    43: isub           
        //    44: aconst_null    
        //    45: nop            
        //    46: lload           117
        //    48: fdiv           
        //    49: lsub           
        //    50: ishl           
        //    51: irem           
        //    52: lsub           
        //    53: dadd           
        //    54: ineg           
        //    55: lsub           
        //    56: isub           
        //    57: astore_1        /* $__uri */
        //    58: lsub           
        //    59: ladd           
        //    60: fneg           
        //    61: lsub           
        //    62: fstore_2        /* $__local */
        //    63: idiv           
        //    64: lsub           
        //    65: ldiv           
        //    66: lsub           
        //    67: fdiv           
        //    68: ineg           
        //    69: aconst_null    
        //    70: nop            
        //    71: bipush          115
        //    73: lsub           
        //    74: fdiv           
        //    75: isub           
        //    76: astore_1        /* $__uri */
        //    77: lsub           
        //    78: ladd           
        //    79: fneg           
        //    80: lsub           
        //    81: fstore_2        /* $__local */
        //    82: idiv           
        //    83: lsub           
        //    84: ldiv           
        //    85: lsub           
        //    86: fdiv           
        //    87: ineg           
        //    88: aconst_null    
        //    89: nop            
        //    90: astore          40
        //    92: dstore_2        /* $__local */
        //    93: astore_1        /* $__uri */
        //    94: fmul           
        //    95: ladd           
        //    96: fneg           
        //    97: ladd           
        //    98: laload         
        //    99: idiv           
        //   100: ladd           
        //   101: fdiv           
        //   102: dsub           
        //   103: laload         
        //   104: aastore        
        //   105: ineg           
        //   106: frem           
        //   107: lmul           
        //   108: fdiv           
        //   109: dsub           
        //   110: istore_0        /* this */
        //   111: astore_1        /* $__uri */
        //   112: fmul           
        //   113: ladd           
        //   114: fneg           
        //   115: ladd           
        //   116: laload         
        //   117: idiv           
        //   118: ladd           
        //   119: fdiv           
        //   120: dsub           
        //   121: laload         
        //   122: aastore        
        //   123: ineg           
        //   124: frem           
        //   125: lmul           
        //   126: fdiv           
        //   127: dsub           
        //   128: istore_0        /* this */
        //   129: astore_1        /* $__uri */
        //   130: fmul           
        //   131: ladd           
        //   132: fneg           
        //   133: ladd           
        //   134: laload         
        //   135: idiv           
        //   136: ladd           
        //   137: fdiv           
        //   138: dsub           
        //   139: laload         
        //   140: aastore        
        //   141: ineg           
        //   142: frem           
        //   143: lmul           
        //   144: fdiv           
        //   145: dsub           
        //   146: istore_0        /* this */
        //   147: dload_3         /* $__qname */
        //   148: sastore        
        //   149: aconst_null    
        //   150: nop            
        //   151: iload_0         /* this */
        //   152: drem           
        //   153: irem           
        //   154: ladd           
        //   155: dneg           
        //   156: fdiv           
        //   157: fstore_0        /* this */
        //   158: imul           
        //   159: lmul           
        //   160: idiv           
        //   161: isub           
        //   162: fstore_3        /* $__qname */
        //   163: frem           
        //   164: ddiv           
        //   165: ldiv           
        //   166: astore_1        /* $__uri */
        //   167: lsub           
        //   168: ladd           
        //   169: fneg           
        //   170: lsub           
        //   171: fstore_2        /* $__local */
        //   172: idiv           
        //   173: lsub           
        //   174: ldiv           
        //   175: lsub           
        //   176: fdiv           
        //   177: ineg           
        //   178: aconst_null    
        //   179: nop            
        //   180: ddiv           
        //   181: dload_2         /* $__local */
        //   182: astore_1        /* $__uri */
        //   183: dadd           
        //   184: ddiv           
        //   185: ldiv           
        //   186: laload         
        //   187: drem           
        //   188: lneg           
        //   189: fdiv           
        //   190: laload         
        //   191: ishl           
        //   192: ldiv           
        //   193: idiv           
        //   194: laload         
        //   195: ishl           
        //   196: drem           
        //   197: ddiv           
        //   198: ldiv           
        //   199: laload         
        //   200: lmul           
        //   201: ldiv           
        //   202: irem           
        //   203: idiv           
        //   204: laload         
        //   205: irem           
        //   206: ladd           
        //   207: frem           
        //   208: drem           
        //   209: lsub           
        //   210: frem           
        //   211: laload         
        //   212: drem           
        //   213: ineg           
        //   214: ladd           
        //   215: ineg           
        //   216: lsub           
        //   217: laload         
        //   218: astore_3        /* $__qname */
        //   219: dstore_0        /* this */
        //   220: fstore_0        /* this */
        //   221: fstore_0        /* this */
        //   222: fstore_2        /* $__local */
        //   223: fneg           
        //   224: lsub           
        //   225: fdiv           
        //   226: ineg           
        //   227: dastore        
        //   228: lsub           
        //   229: dadd           
        //   230: lsub           
        //   231: lmul           
        //   232: fneg           
        //   233: lsub           
        //   234: frem           
        //   235: istore_0        /* this */
        //   236: astore_1        /* $__uri */
        //   237: fmul           
        //   238: ladd           
        //   239: fneg           
        //   240: ladd           
        //   241: laload         
        //   242: idiv           
        //   243: ladd           
        //   244: fdiv           
        //   245: dsub           
        //   246: laload         
        //   247: aastore        
        //   248: ineg           
        //   249: frem           
        //   250: lmul           
        //   251: fdiv           
        //   252: dsub           
        //   253: istore_0        /* this */
        //   254: astore_1        /* $__uri */
        //   255: fmul           
        //   256: ladd           
        //   257: fneg           
        //   258: ladd           
        //   259: laload         
        //   260: idiv           
        //   261: ladd           
        //   262: fdiv           
        //   263: dsub           
        //   264: laload         
        //   265: aastore        
        //   266: ineg           
        //   267: frem           
        //   268: lmul           
        //   269: fdiv           
        //   270: dsub           
        //   271: istore_0        /* this */
        //   272: astore_1        /* $__uri */
        //   273: fmul           
        //   274: ladd           
        //   275: fneg           
        //   276: ladd           
        //   277: laload         
        //   278: idiv           
        //   279: ladd           
        //   280: fdiv           
        //   281: dsub           
        //   282: laload         
        //   283: aastore        
        //   284: ineg           
        //   285: frem           
        //   286: lmul           
        //   287: fdiv           
        //   288: dsub           
        //   289: istore_0        /* this */
        //   290: dload_3         /* $__qname */
        //   291: sastore        
        //   292: aconst_null    
        //   293: nop            
        //   294: lload_0         /* this */
        //   295: frem           
        //   296: lsub           
        //   297: fneg           
        //   298: lsub           
        //   299: frem           
        //   300: ineg           
        //   301: bastore        
        //   302: ddiv           
        //   303: lastore        
        //   304: ladd           
        //   305: frem           
        //   306: lsub           
        //   307: fdiv           
        //   308: ineg           
        //   309: fstore_3        /* $__qname */
        //   310: frem           
        //   311: ddiv           
        //   312: ldiv           
        //   313: astore_1        /* $__uri */
        //   314: lsub           
        //   315: ladd           
        //   316: fneg           
        //   317: lsub           
        //   318: fstore_2        /* $__local */
        //   319: idiv           
        //   320: lsub           
        //   321: ldiv           
        //   322: lsub           
        //   323: fdiv           
        //   324: ineg           
        //   325: aconst_null    
        //   326: nop            
        //   327: astore_1        /* $__uri */
        //   328: dload_2         /* $__local */
        //   329: astore_1        /* $__uri */
        //   330: fmul           
        //   331: ladd           
        //   332: fneg           
        //    Exceptions:
        //  throws org.xml.sax.SAXException
        //    LocalVariableTable:
        //  Start  Length  Slot  Name      Signature
        //  -----  ------  ----  --------  -------------------------------------------------------
        //  0      333     0     this      Lcom/sun/xml/xsom/impl/parser/state/identityConstraint;
        //  0      333     1     $__uri    Ljava/lang/String;
        //  0      333     2     $__local  Ljava/lang/String;
        //  0      333     3     $__qname  Ljava/lang/String;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public void enterAttribute(final String $__uri, final String $__local, final String $__qname) throws SAXException {
        // 
        // This method could not be decompiled.
        // 
        // Could not show original bytecode, likely due to the same error.
        // 
        // The error that occurred was:
        // 
        // com.strobel.assembler.metadata.MethodBodyParseException: An error occurred while parsing the bytecode of method 'com/sun/xml/xsom/impl/parser/state/identityConstraint.enterAttribute:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V'.
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
        // Caused by: java.lang.ClassCastException: com.strobel.assembler.ir.ConstantPool$NameAndTypeDescriptorEntry cannot be cast to com.strobel.assembler.ir.ConstantPool$ConstantEntry
        //     at com.strobel.assembler.ir.ConstantPool.lookupConstant(ConstantPool.java:120)
        //     at com.strobel.assembler.metadata.ClassFileReader$Scope.lookupConstant(ClassFileReader.java:1319)
        //     at com.strobel.assembler.metadata.MethodReader.readBodyCore(MethodReader.java:286)
        //     at com.strobel.assembler.metadata.MethodReader.readBody(MethodReader.java:62)
        //     ... 16 more
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public void leaveAttribute(final String $__uri, final String $__local, final String $__qname) throws SAXException {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: dstore_2        /* $__local */
        //     1: ldiv           
        //     2: irem           
        //     3: idiv           
        //     4: istore_0        /* this */
        //     5: iconst_4       
        //     6: aconst_null    
        //     7: aload_0         /* this */
        //     8: aconst_null    
        //     9: aconst_null    
        //    10: dconst_0       
        //    11: dload_2         /* $__local */
        //    12: astore_1        /* $__uri */
        //    13: dadd           
        //    14: ddiv           
        //    15: ldiv           
        //    16: laload         
        //    17: drem           
        //    18: lneg           
        //    19: fdiv           
        //    20: laload         
        //    21: ishl           
        //    22: ldiv           
        //    23: idiv           
        //    24: laload         
        //    25: ishl           
        //    26: drem           
        //    27: ddiv           
        //    28: ldiv           
        //    29: laload         
        //    30: lmul           
        //    31: ldiv           
        //    32: irem           
        //    33: idiv           
        //    34: laload         
        //    35: irem           
        //    36: ladd           
        //    37: frem           
        //    38: drem           
        //    39: lsub           
        //    40: frem           
        //    41: laload         
        //    42: aastore        
        //    43: dadd           
        //    44: imul           
        //    45: lsub           
        //    46: ldiv           
        //    47: ladd           
        //    48: fstore_1        /* $__uri */
        //    49: ddiv           
        //    50: dadd           
        //    51: lneg           
        //    52: ldiv           
        //    53: lsub           
        //    54: fdiv           
        //    55: ineg           
        //    56: dstore_2        /* $__local */
        //    57: ldiv           
        //    58: irem           
        //    59: idiv           
        //    60: istore_0        /* this */
        //    61: astore_1        /* $__uri */
        //    62: dadd           
        //    63: ddiv           
        //    64: ldiv           
        //    65: laload         
        //    66: drem           
        //    67: lneg           
        //    68: fdiv           
        //    69: laload         
        //    70: ishl           
        //    71: ldiv           
        //    72: idiv           
        //    73: laload         
        //    74: ishl           
        //    75: drem           
        //    76: ddiv           
        //    77: ldiv           
        //    78: laload         
        //    79: lmul           
        //    80: ldiv           
        //    81: irem           
        //    82: idiv           
        //    83: laload         
        //    84: lstore_2        /* $__local */
        //    85: fdiv           
        //    86: fdiv           
        //    87: ddiv           
        //    88: ineg           
        //    89: ladd           
        //    90: ineg           
        //    91: lmul           
        //    92: ddiv           
        //    93: fdiv           
        //    94: dstore_2        /* $__local */
        //    95: ldiv           
        //    96: irem           
        //    97: idiv           
        //    98: istore_0        /* this */
        //    99: astore_1        /* $__uri */
        //   100: ddiv           
        //   101: frem           
        //   102: dsub           
        //   103: laload         
        //   104: ishl           
        //   105: ldiv           
        //   106: idiv           
        //   107: laload         
        //   108: drem           
        //   109: ladd           
        //   110: ishl           
        //   111: laload         
        //   112: astore_1        /* $__uri */
        //   113: ddiv           
        //   114: dadd           
        //   115: ladd           
        //   116: ineg           
        //   117: ddiv           
        //   118: frem           
        //   119: istore_0        /* this */
        //   120: astore_1        /* $__uri */
        //   121: dadd           
        //   122: ddiv           
        //   123: ldiv           
        //   124: laload         
        //   125: drem           
        //   126: lneg           
        //   127: fdiv           
        //   128: laload         
        //   129: ishl           
        //   130: ldiv           
        //   131: idiv           
        //   132: laload         
        //   133: ishl           
        //   134: drem           
        //   135: ddiv           
        //   136: ldiv           
        //   137: laload         
        //   138: lmul           
        //   139: ldiv           
        //   140: irem           
        //   141: idiv           
        //   142: laload         
        //   143: fstore_3        /* $__qname */
        //   144: ddiv           
        //   145: frem           
        //   146: lsub           
        //   147: lmul           
        //   148: dsub           
        //    Exceptions:
        //  throws org.xml.sax.SAXException
        //    LocalVariableTable:
        //  Start  Length  Slot  Name      Signature
        //  -----  ------  ----  --------  -------------------------------------------------------
        //  0      149     0     this      Lcom/sun/xml/xsom/impl/parser/state/identityConstraint;
        //  0      149     1     $__uri    Ljava/lang/String;
        //  0      149     2     $__local  Ljava/lang/String;
        //  0      149     3     $__qname  Ljava/lang/String;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public void text(final String $value) throws SAXException {
        // 
        // This method could not be decompiled.
        // 
        // Could not show original bytecode, likely due to the same error.
        // 
        // The error that occurred was:
        // 
        // com.strobel.assembler.metadata.MethodBodyParseException: An error occurred while parsing the bytecode of method 'com/sun/xml/xsom/impl/parser/state/identityConstraint.text:(Ljava/lang/String;)V'.
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
        // Caused by: java.lang.ClassCastException: com.strobel.assembler.ir.ConstantPool$NameAndTypeDescriptorEntry cannot be cast to com.strobel.assembler.ir.ConstantPool$ConstantEntry
        //     at com.strobel.assembler.ir.ConstantPool.lookupConstant(ConstantPool.java:120)
        //     at com.strobel.assembler.metadata.ClassFileReader$Scope.lookupConstant(ClassFileReader.java:1319)
        //     at com.strobel.assembler.metadata.MethodReader.readBodyCore(MethodReader.java:293)
        //     at com.strobel.assembler.metadata.MethodReader.readBody(MethodReader.java:62)
        //     ... 16 more
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public void onChildCompleted(final Object $__result__, final int $__cookie__, final boolean $__needAttCheck__) throws SAXException {
        // 
        // This method could not be decompiled.
        // 
        // Could not show original bytecode, likely due to the same error.
        // 
        // The error that occurred was:
        // 
        // com.strobel.assembler.metadata.MethodBodyParseException: An error occurred while parsing the bytecode of method 'com/sun/xml/xsom/impl/parser/state/identityConstraint.onChildCompleted:(Ljava/lang/Object;IZ)V'.
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
        // Caused by: java.lang.ClassCastException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public boolean accepted() {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: iconst_m1      
        //     1: nop            
        //     2: ddiv           
        //     3: nop            
        //     4: nop            
        //     5: nop            
        //     6: lconst_1       
        //     7: nop            
        //     8: iconst_m1      
        //     9: nop            
        //    10: nop            
        //    11: nop            
        //    12: aload_0         /* this */
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  -------------------------------------------------------
        //  0      13      0     this  Lcom/sun/xml/xsom/impl/parser/state/identityConstraint;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
}
