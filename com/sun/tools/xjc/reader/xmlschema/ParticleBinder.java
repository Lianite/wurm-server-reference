// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.reader.xmlschema;

import com.sun.tools.xjc.reader.xmlschema.bindinfo.BIProperty;
import com.sun.xml.xsom.XSTerm;
import com.sun.xml.xsom.XSElementDecl;
import com.sun.msv.grammar.Expression;
import com.sun.tools.xjc.grammar.ClassItem;
import com.sun.xml.xsom.XSParticle;
import com.sun.msv.grammar.ExpressionPool;

public abstract class ParticleBinder
{
    protected final BGMBuilder builder;
    protected final ExpressionPool pool;
    
    public abstract Expression build(final XSParticle p0, final ClassItem p1);
    
    public abstract boolean checkFallback(final XSParticle p0, final ClassItem p1);
    
    protected ParticleBinder(final BGMBuilder builder) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: fstore_1        /* builder */
        //     1: lsub           
        //     2: dadd           
        //     3: idiv           
        //     4: aconst_null    
        //     5: nop            
        //     6: bipush          97
        //     8: drem           
        //     9: astore_2       
        //    10: ddiv           
        //    11: isub           
        //    12: lsub           
        //    13: idiv           
        //    14: dstore_0        /* this */
        //    15: frem           
        //    16: ddiv           
        //    17: lneg           
        //    LocalVariableTable:
        //  Start  Length  Slot  Name     Signature
        //  -----  ------  ----  -------  ---------------------------------------------------
        //  0      18      0     this     Lcom/sun/tools/xjc/reader/xmlschema/ParticleBinder;
        //  0      18      1     builder  Lcom/sun/tools/xjc/reader/xmlschema/BGMBuilder;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    protected final boolean needSkippableElement(final XSElementDecl e) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: lsub           
        //     1: dadd           
        //     2: idiv           
        //     3: aconst_null    
        //     4: nop            
        //     5: lconst_1       
        //     6: lmul           
        //     7: drem           
        //     8: pop            
        //     9: lmul           
        //    10: idiv           
        //    11: isub           
        //    12: dadd           
        //    13: ladd           
        //    14: frem           
        //    15: isub           
        //    16: aconst_null    
        //    17: nop            
        //    18: fconst_1       
        //    19: lmul           
        //    20: drem           
        //    21: astore_2       
        //    22: ddiv           
        //    23: isub           
        //    24: lsub           
        //    25: idiv           
        //    26: dstore_0        /* this */
        //    27: frem           
        //    28: ddiv           
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  ---------------------------------------------------
        //  0      29      0     this  Lcom/sun/tools/xjc/reader/xmlschema/ParticleBinder;
        //  0      29      1     e     Lcom/sun/xml/xsom/XSElementDecl;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    protected final boolean needSkip(final XSTerm t) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: aconst_null    
        //     1: nop            
        //     2: fload           40
        //     4: dload_3        
        //     5: astore_1        /* t */
        //     6: ddiv           
        //     7: frem           
        //     8: dsub           
        //     9: laload         
        //    10: ishl           
        //    11: ldiv           
        //    12: idiv           
        //    13: laload         
        //    14: drem           
        //    15: ladd           
        //    16: ishl           
        //    17: laload         
        //    18: astore_1        /* t */
        //    19: ddiv           
        //    20: dadd           
        //    21: ladd           
        //    22: ineg           
        //    23: ddiv           
        //    24: frem           
        //    25: istore_0        /* this */
        //    26: aconst_null    
        //    27: nop            
        //    28: iconst_2       
        //    29: lsub           
        //    30: frem           
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  ---------------------------------------------------
        //  0      31      0     this  Lcom/sun/tools/xjc/reader/xmlschema/ParticleBinder;
        //  0      31      1     t     Lcom/sun/xml/xsom/XSTerm;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    protected final boolean isGlobalElementDecl(final XSTerm t) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: sastore        
        //     1: aconst_null    
        //     2: nop            
        //     3: bipush          103
        //     5: lsub           
        //     6: ineg           
        //     7: astore_3       
        //     8: ladd           
        //     9: ldiv           
        //    10: lsub           
        //    11: fstore_0        /* this */
        //    12: ddiv           
        //    13: fdiv           
        //    14: fneg           
        //    15: lsub           
        //    16: frem           
        //    17: ineg           
        //    18: lsub           
        //    19: frem           
        //    20: aconst_null    
        //    21: nop            
        //    22: aload_1         /* t */
        //    23: dload_2         /* e */
        //    24: dload_3        
        //    25: astore_1        /* t */
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  ---------------------------------------------------
        //  0      26      0     this  Lcom/sun/tools/xjc/reader/xmlschema/ParticleBinder;
        //  0      26      1     t     Lcom/sun/xml/xsom/XSTerm;
        //  7      19      2     e     Lcom/sun/xml/xsom/XSElementDecl;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    protected final BIProperty getLocalPropCustomization(final XSParticle p) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: lsub           
        //     1: frem           
        //     2: aconst_null    
        //     3: nop            
        //     4: dconst_0       
        //     5: ineg           
        //     6: ddiv           
        //     7: lastore        
        //     8: frem           
        //     9: ddiv           
        //    10: irem           
        //    11: lsub           
        //    12: frem           
        //    13: ineg           
        //    14: lshl           
        //    15: astore_3       
        //    16: ladd           
        //    17: ldiv           
        //    18: lsub           
        //    19: aconst_null    
        //    20: nop            
        //    21: dload_0         /* this */
        //    22: dload_2         /* cust */
        //    23: astore_1        /* p */
        //    24: fmul           
        //    25: ladd           
        //    26: fneg           
        //    27: ladd           
        //    28: laload         
        //    29: idiv           
        //    30: ladd           
        //    31: fdiv           
        //    32: dsub           
        //    33: laload         
        //    34: aastore        
        //    35: ineg           
        //    36: frem           
        //    37: lmul           
        //    38: fdiv           
        //    39: dsub           
        //    40: istore_0        /* this */
        //    41: dload_3        
        //    42: astore_1        /* p */
        //    43: fmul           
        //    44: ladd           
        //    45: fneg           
        //    46: ladd           
        //    47: laload         
        //    48: idiv           
        //    49: ladd           
        //    50: fdiv           
        //    51: dsub           
        //    52: laload         
        //    53: aastore        
        //    54: ineg           
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  --------------------------------------------------------
        //  0      55      0     this  Lcom/sun/tools/xjc/reader/xmlschema/ParticleBinder;
        //  0      55      1     p     Lcom/sun/xml/xsom/XSParticle;
        //  18     37      2     cust  Lcom/sun/tools/xjc/reader/xmlschema/bindinfo/BIProperty;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    protected final String computeLabel(final XSParticle p) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: istore_0        /* this */
        //     1: dadd           
        //     2: ddiv           
        //     3: ldiv           
        //     4: laload         
        //     5: drem           
        //     6: lneg           
        //     7: fdiv           
        //     8: laload         
        //     9: ineg           
        //    10: ddiv           
        //    11: ddiv           
        //    12: idiv           
        //    13: drem           
        //    14: laload         
        //    15: ishl           
        //    16: fmul           
        //    17: dadd           
        //    18: laload         
        //    19: frem           
        //    20: lsub           
        //    21: ladd           
        //    22: isub           
        //    23: lsub           
        //    24: frem           
        //    25: laload         
        //    26: ishl           
        //    27: ldiv           
        //    28: idiv           
        //    29: drem           
        //    30: dadd           
        //    31: imul           
        //    32: lsub           
        //    33: ldiv           
        //    34: ladd           
        //    35: laload         
        //    36: fadd           
        //    37: lmul           
        //    38: fdiv           
        //    39: isub           
        //    40: lmul           
        //    41: fdiv           
        //    42: fsub           
        //    43: ddiv           
        //    44: laload         
        //    45: lstore_3        /* t */
        //    46: dstore_2        /* cust */
        //    47: dstore_0        /* this */
        //    48: idiv           
        //    49: ddiv           
        //    50: fadd           
        //    51: ladd           
        //    52: idiv           
        //    53: lstore_3        /* t */
        //    54: lmul           
        //    55: fdiv           
        //    56: isub           
        //    57: lmul           
        //    58: fdiv           
        //    59: dsub           
        //    60: aconst_null    
        //    61: nop            
        //    62: fconst_1       
        //    63: lmul           
        //    64: drem           
        //    65: aastore        
        //    66: lmul           
        //    67: ldiv           
        //    68: irem           
        //    69: idiv           
        //    70: lsub           
        //    71: astore_2        /* cust */
        //    72: ddiv           
        //    73: isub           
        //    74: lsub           
        //    75: aconst_null    
        //    76: nop            
        //    77: iload_1         /* p */
        //    78: dadd           
        //    79: ddiv           
        //    80: ldiv           
        //    81: laload         
        //    82: drem           
        //    83: lneg           
        //    84: fdiv           
        //    85: laload         
        //    86: dadd           
        //    87: ddiv           
        //    88: isub           
        //    89: lsub           
        //    90: ldiv           
        //    91: ddiv           
        //    92: isub           
        //    93: lsub           
        //    94: idiv           
        //    95: laload         
        //    96: dstore_3        /* t */
        //    97: dstore_3        /* t */
        //    98: ladd           
        //    99: fneg           
        //   100: ladd           
        //   101: astore_3        /* t */
        //   102: ladd           
        //   103: ldiv           
        //   104: lsub           
        //   105: aconst_null    
        //   106: nop            
        //   107: fconst_2       
        //   108: dsub           
        //   109: lsub           
        //   110: ineg           
        //   111: lastore        
        //   112: idiv           
        //   113: lneg           
        //   114: frem           
        //   115: ladd           
        //   116: idiv           
        //   117: fstore_3        /* t */
        //   118: ddiv           
        //   119: frem           
        //   120: ldiv           
        //   121: aconst_null    
        //   122: nop            
        //   123: lastore        
        //   124: dload_2         /* cust */
        //   125: astore_1        /* p */
        //   126: dadd           
        //   127: ddiv           
        //   128: ldiv           
        //   129: laload         
        //   130: drem           
        //   131: lneg           
        //   132: fdiv           
        //   133: laload         
        //   134: ineg           
        //   135: ddiv           
        //   136: ddiv           
        //   137: idiv           
        //   138: drem           
        //   139: laload         
        //   140: ishl           
        //   141: fmul           
        //   142: dadd           
        //   143: laload         
        //   144: frem           
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  --------------------------------------------------------
        //  118    21      4     e     Ljava/text/ParseException;
        //  0      145     0     this  Lcom/sun/tools/xjc/reader/xmlschema/ParticleBinder;
        //  0      145     1     p     Lcom/sun/xml/xsom/XSParticle;
        //  6      139     2     cust  Lcom/sun/tools/xjc/reader/xmlschema/bindinfo/BIProperty;
        //  31     114     3     t     Lcom/sun/xml/xsom/XSTerm;
        //    Exceptions:
        //  Try           Handler
        //  Start  End    Start  End    Type                      
        //  -----  -----  -----  -----  --------------------------
        //  102    115    116    145    Ljava/text/ParseException;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    private String makeJavaName(final String xmlName) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: ladd           
        //     1: laload         
        //     2: idiv           
        //     3: ladd           
        //     4: fdiv           
        //     5: dsub           
        //     6: laload         
        //     7: aastore        
        //     8: ineg           
        //     9: frem           
        //    10: lmul           
        //    11: fdiv           
        //    12: dsub           
        //    13: istore_0        /* this */
        //    LocalVariableTable:
        //  Start  Length  Slot  Name     Signature
        //  -----  ------  ----  -------  ---------------------------------------------------
        //  0      14      0     this     Lcom/sun/tools/xjc/reader/xmlschema/ParticleBinder;
        //  0      14      1     xmlName  Ljava/lang/String;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    protected static void _assert(final boolean b) {
        // 
        // This method could not be decompiled.
        // 
        // Could not show original bytecode, likely due to the same error.
        // 
        // The error that occurred was:
        // 
        // com.strobel.assembler.metadata.MethodBodyParseException: An error occurred while parsing the bytecode of method 'com/sun/tools/xjc/reader/xmlschema/ParticleBinder._assert:(Z)V'.
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
}
