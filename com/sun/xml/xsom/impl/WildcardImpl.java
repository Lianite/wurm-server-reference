// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.xml.xsom.impl;

import java.util.Collection;
import java.util.Iterator;
import java.util.Collections;
import java.util.Set;
import com.sun.xml.xsom.visitor.XSWildcardFunction;
import com.sun.xml.xsom.visitor.XSWildcardVisitor;
import com.sun.xml.xsom.impl.parser.SchemaDocumentImpl;
import com.sun.xml.xsom.XSTerm;
import com.sun.xml.xsom.XSElementDecl;
import com.sun.xml.xsom.XSModelGroup;
import com.sun.xml.xsom.XSModelGroupDecl;
import com.sun.xml.xsom.visitor.XSFunction;
import com.sun.xml.xsom.visitor.XSTermFunction;
import com.sun.xml.xsom.visitor.XSTermVisitor;
import com.sun.xml.xsom.visitor.XSVisitor;
import org.xml.sax.Locator;
import com.sun.xml.xsom.XSWildcard;

public abstract class WildcardImpl extends ComponentImpl implements XSWildcard, Ref.Term
{
    private final int mode;
    
    protected WildcardImpl(final SchemaImpl owner, final AnnotationImpl _annon, final Locator _loc, final int _mode) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: ineg           
        //     1: lmul           
        //     2: idiv           
        //     3: laload         
        //     4: aastore        
        //     5: lsub           
        //     6: ineg           
        //     7: istore_0        /* this */
        //     8: dstore_2        /* _annon */
        //     9: dload_3         /* _loc */
        //    10: sastore        
        //    11: aconst_null    
        //    12: nop            
        //    13: lconst_1       
        //    LocalVariableTable:
        //  Start  Length  Slot  Name    Signature
        //  -----  ------  ----  ------  --------------------------------------
        //  0      14      0     this    Lcom/sun/xml/xsom/impl/WildcardImpl;
        //  0      14      1     owner   Lcom/sun/xml/xsom/impl/SchemaImpl;
        //  0      14      2     _annon  Lcom/sun/xml/xsom/impl/AnnotationImpl;
        //  0      14      3     _loc    Lorg/xml/sax/Locator;
        //  0      14      4     _mode   I
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public int getMode() {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: nop            
        //     1: iload           40
        //     3: astore_1       
        //     4: fmul           
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  ------------------------------------
        //  0      5       0     this  Lcom/sun/xml/xsom/impl/WildcardImpl;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public WildcardImpl union(final SchemaImpl owner, final WildcardImpl rhs) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: ldiv           
        //     1: ladd           
        //     2: fstore_1        /* owner */
        //     3: ddiv           
        //     4: dadd           
        //     5: lneg           
        //     6: ldiv           
        //     7: lsub           
        //     8: fdiv           
        //     9: ineg           
        //    10: dstore_2        /* rhs */
        //    11: ldiv           
        //    12: irem           
        //    13: idiv           
        //    14: istore_0        /* this */
        //    15: astore_1        /* owner */
        //    16: dadd           
        //    17: ddiv           
        //    18: ldiv           
        //    19: laload         
        //    20: drem           
        //    21: lneg           
        //    22: fdiv           
        //    23: laload         
        //    24: ishl           
        //    25: ldiv           
        //    26: idiv           
        //    27: laload         
        //    28: ishl           
        //    29: drem           
        //    30: ddiv           
        //    31: ldiv           
        //    32: laload         
        //    33: lmul           
        //    34: ldiv           
        //    35: irem           
        //    36: idiv           
        //    37: laload         
        //    38: lstore_2        /* rhs */
        //    39: fdiv           
        //    40: fdiv           
        //    41: ddiv           
        //    42: ineg           
        //    43: ladd           
        //    44: ineg           
        //    45: lmul           
        //    46: ddiv           
        //    47: fdiv           
        //    48: dstore_2        /* rhs */
        //    49: ldiv           
        //    50: irem           
        //    51: idiv           
        //    52: istore_0        /* this */
        //    53: astore_1        /* owner */
        //    54: ddiv           
        //    55: frem           
        //    56: dsub           
        //    57: laload         
        //    58: ishl           
        //    59: ldiv           
        //    60: idiv           
        //    61: laload         
        //    62: drem           
        //    63: ladd           
        //    64: ishl           
        //    65: laload         
        //    66: astore_1        /* owner */
        //    67: ddiv           
        //    68: dadd           
        //    69: ladd           
        //    70: ineg           
        //    71: ddiv           
        //    72: frem           
        //    73: istore_0        /* this */
        //    74: astore_1        /* owner */
        //    75: dadd           
        //    76: ddiv           
        //    77: ldiv           
        //    78: laload         
        //    79: drem           
        //    80: lneg           
        //    81: fdiv           
        //    82: laload         
        //    83: ishl           
        //    84: ldiv           
        //    85: idiv           
        //    86: laload         
        //    87: ishl           
        //    88: drem           
        //    89: ddiv           
        //    90: ldiv           
        //    91: laload         
        //    92: lmul           
        //    93: ldiv           
        //    94: irem           
        //    95: idiv           
        //    96: laload         
        //    97: fstore_3       
        //    98: ddiv           
        //    99: frem           
        //   100: lsub           
        //   101: lmul           
        //   102: dsub           
        //   103: fdiv           
        //   104: lstore_2        /* rhs */
        //   105: ineg           
        //   106: ineg           
        //   107: frem           
        //   108: lmul           
        //   109: fadd           
        //   110: lneg           
        //   111: ineg           
        //   112: lsub           
        //   113: drem           
        //   114: dstore_2        /* rhs */
        //   115: ldiv           
        //   116: irem           
        //   117: idiv           
        //   118: istore_0        /* this */
        //   119: astore_1        /* owner */
        //   120: fmul           
        //   121: ladd           
        //   122: fneg           
        //   123: ladd           
        //   124: laload         
        //   125: idiv           
        //   126: ladd           
        //   127: fdiv           
        //   128: dsub           
        //   129: laload         
        //   130: aastore        
        //   131: ineg           
        //   132: frem           
        //   133: lmul           
        //   134: fdiv           
        //   135: dsub           
        //   136: istore_0        /* this */
        //   137: dstore_2        /* rhs */
        //   138: dload_3        
        //   139: sastore        
        //   140: aconst_null    
        //   141: nop            
        //   142: iconst_5       
        //   143: dadd           
        //   144: ddiv           
        //   145: fdiv           
        //   146: ineg           
        //   147: ladd           
        //   148: lmul           
        //   149: fdiv           
        //   150: drem           
        //   151: aconst_null    
        //   152: nop            
        //   153: fload_0         /* this */
        //   154: dadd           
        //   155: ddiv           
        //   156: ldiv           
        //   157: laload         
        //   158: drem           
        //   159: lneg           
        //   160: fdiv           
        //   161: laload         
        //   162: ishl           
        //   163: ldiv           
        //   164: idiv           
        //   165: laload         
        //   166: ishl           
        //   167: drem           
        //   168: ddiv           
        //   169: ldiv           
        //   170: laload         
        //   171: fneg           
        //   172: lmul           
        //   173: drem           
        //   174: lmul           
        //   175: ineg           
        //   176: ddiv           
        //   177: frem           
        //   178: laload         
        //   179: pop2           
        //   180: aastore        
        //   181: sastore        
        //   182: lmul           
        //   183: drem           
        //   184: lmul           
        //   185: ineg           
        //   186: ddiv           
        //   187: frem           
        //   188: aconst_null    
        //   189: nop            
        //   190: iconst_5       
        //   191: dneg           
        //   192: lmul           
        //   193: idiv           
        //   194: isub           
        //   195: dadd           
        //   196: ladd           
        //   197: frem           
        //   198: isub           
        //   199: aconst_null    
        //   200: nop            
        //   201: lload_2         /* rhs */
        //   202: dload_2         /* rhs */
        //   203: astore_1        /* owner */
        //   204: dadd           
        //   205: ddiv           
        //   206: ldiv           
        //   207: laload         
        //   208: drem           
        //   209: lneg           
        //   210: fdiv           
        //   211: laload         
        //   212: ishl           
        //   213: ldiv           
        //   214: idiv           
        //   215: laload         
        //   216: ishl           
        //   217: drem           
        //   218: ddiv           
        //   219: ldiv           
        //   220: laload         
        //   221: pop2           
        //   222: aastore        
        //   223: pop            
        //   224: lmul           
        //   225: idiv           
        //   226: isub           
        //   227: dadd           
        //   228: ladd           
        //   229: frem           
        //   230: isub           
        //   231: istore_0        /* this */
        //   232: dload_3         /* o */
        //   233: sastore        
        //   234: aconst_null    
        //   235: nop            
        //   236: dload_0         /* this */
        //   237: dadd           
        //   238: ddiv           
        //   239: ldiv           
        //   240: laload         
        //   241: drem           
        //   242: lneg           
        //   243: fdiv           
        //    LocalVariableTable:
        //  Start  Length  Slot  Name    Signature
        //  -----  ------  ----  ------  -------------------------------------------
        //  51     44      3     values  Ljava/util/Set;
        //  173    9       3     o       Lcom/sun/xml/xsom/impl/WildcardImpl$Other;
        //  179    3       4     f       Lcom/sun/xml/xsom/impl/WildcardImpl$Finite;
        //  0      244     0     this    Lcom/sun/xml/xsom/impl/WildcardImpl;
        //  0      244     1     owner   Lcom/sun/xml/xsom/impl/SchemaImpl;
        //  0      244     2     rhs     Lcom/sun/xml/xsom/impl/WildcardImpl;
        //  187    57      3     o       Lcom/sun/xml/xsom/impl/WildcardImpl$Other;
        //  193    51      4     f       Lcom/sun/xml/xsom/impl/WildcardImpl$Finite;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public final void visit(final XSVisitor visitor) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: ldiv           
        //     1: idiv           
        //     2: laload         
        //     3: ishl           
        //     4: drem           
        //     5: ddiv           
        //     6: ldiv           
        //     7: laload         
        //    LocalVariableTable:
        //  Start  Length  Slot  Name     Signature
        //  -----  ------  ----  -------  ------------------------------------
        //  0      8       0     this     Lcom/sun/xml/xsom/impl/WildcardImpl;
        //  0      8       1     visitor  Lcom/sun/xml/xsom/visitor/XSVisitor;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public final void visit(final XSTermVisitor visitor) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: drem           
        //     1: lmul           
        //     2: ineg           
        //     3: ddiv           
        //     4: frem           
        //     5: laload         
        //     6: pop2           
        //     7: aastore        
        //    LocalVariableTable:
        //  Start  Length  Slot  Name     Signature
        //  -----  ------  ----  -------  ----------------------------------------
        //  0      8       0     this     Lcom/sun/xml/xsom/impl/WildcardImpl;
        //  0      8       1     visitor  Lcom/sun/xml/xsom/visitor/XSTermVisitor;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public Object apply(final XSTermFunction function) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: nop            
        //     1: nop            
        //     2: nop            
        //     3: ineg           
        //     4: nop            
        //     5: iconst_2       
        //     6: nop            
        //     7: iconst_3       
        //    LocalVariableTable:
        //  Start  Length  Slot  Name      Signature
        //  -----  ------  ----  --------  -----------------------------------------
        //  0      8       0     this      Lcom/sun/xml/xsom/impl/WildcardImpl;
        //  0      8       1     function  Lcom/sun/xml/xsom/visitor/XSTermFunction;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public Object apply(final XSFunction function) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: nop            
        //     1: dload_1         /* function */
        //     2: nop            
        //     3: dload_2        
        //     4: nop            
        //     5: aconst_null    
        //     6: nop            
        //     7: nop            
        //    LocalVariableTable:
        //  Start  Length  Slot  Name      Signature
        //  -----  ------  ----  --------  -------------------------------------
        //  0      8       0     this      Lcom/sun/xml/xsom/impl/WildcardImpl;
        //  0      8       1     function  Lcom/sun/xml/xsom/visitor/XSFunction;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public boolean isWildcard() {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: nop            
        //     1: nop            
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  ------------------------------------
        //  0      2       0     this  Lcom/sun/xml/xsom/impl/WildcardImpl;
        // 
        // The error that occurred was:
        // 
        // java.lang.NullPointerException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public boolean isModelGroupDecl() {
        // 
        // This method could not be decompiled.
        // 
        // Could not show original bytecode, likely due to the same error.
        // 
        // The error that occurred was:
        // 
        // com.strobel.assembler.metadata.MethodBodyParseException: An error occurred while parsing the bytecode of method 'com/sun/xml/xsom/impl/WildcardImpl.isModelGroupDecl:()Z'.
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
    
    public boolean isModelGroup() {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: nop            
        //     1: lconst_0       
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  ------------------------------------
        //  0      2       0     this  Lcom/sun/xml/xsom/impl/WildcardImpl;
        // 
        // The error that occurred was:
        // 
        // java.lang.NullPointerException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public boolean isElementDecl() {
        // 
        // This method could not be decompiled.
        // 
        // Could not show original bytecode, likely due to the same error.
        // 
        // The error that occurred was:
        // 
        // com.strobel.assembler.metadata.MethodBodyParseException: An error occurred while parsing the bytecode of method 'com/sun/xml/xsom/impl/WildcardImpl.isElementDecl:()Z'.
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
        //     at com.strobel.assembler.metadata.MethodReader.readBodyCore(MethodReader.java:185)
        //     at com.strobel.assembler.metadata.MethodReader.readBody(MethodReader.java:62)
        //     ... 16 more
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public XSWildcard asWildcard() {
        // 
        // This method could not be decompiled.
        // 
        // Could not show original bytecode, likely due to the same error.
        // 
        // The error that occurred was:
        // 
        // com.strobel.assembler.metadata.MethodBodyParseException: An error occurred while parsing the bytecode of method 'com/sun/xml/xsom/impl/WildcardImpl.asWildcard:()Lcom/sun/xml/xsom/XSWildcard;'.
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
        //     at com.strobel.assembler.metadata.MethodReader.readBodyCore(MethodReader.java:275)
        //     at com.strobel.assembler.metadata.MethodReader.readBody(MethodReader.java:62)
        //     ... 16 more
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public XSModelGroupDecl asModelGroupDecl() {
        return (XSModelGroupDecl)1;
    }
    
    public XSModelGroup asModelGroup() {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: nop            
        //     1: lastore        
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  ------------------------------------
        //  0      2       0     this  Lcom/sun/xml/xsom/impl/WildcardImpl;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public XSElementDecl asElementDecl() {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: nop            
        //     1: aload_3        
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  ------------------------------------
        //  0      2       0     this  Lcom/sun/xml/xsom/impl/WildcardImpl;
        // 
        // The error that occurred was:
        // 
        // java.lang.NullPointerException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public XSTerm getTerm() {
        // 
        // This method could not be decompiled.
        // 
        // Could not show original bytecode, likely due to the same error.
        // 
        // The error that occurred was:
        // 
        // com.strobel.assembler.metadata.MethodBodyParseException: An error occurred while parsing the bytecode of method 'com/sun/xml/xsom/impl/WildcardImpl.getTerm:()Lcom/sun/xml/xsom/XSTerm;'.
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
    
    public static final class Any extends WildcardImpl implements XSWildcard.Any
    {
        public Any(final SchemaDocumentImpl owner, final AnnotationImpl _annon, final Locator _loc, final ForeignAttributesImpl _fa, final int _mode) {
            super(owner, _annon, _loc, _fa, _mode);
        }
        
        public boolean acceptsNamespace(final String namespaceURI) {
            return true;
        }
        
        public void visit(final XSWildcardVisitor visitor) {
            visitor.any(this);
        }
        
        public Object apply(final XSWildcardFunction function) {
            return function.any(this);
        }
    }
    
    public static final class Other extends WildcardImpl implements XSWildcard.Other
    {
        private final String otherNamespace;
        
        public Other(final SchemaDocumentImpl owner, final AnnotationImpl _annon, final Locator _loc, final ForeignAttributesImpl _fa, final String otherNamespace, final int _mode) {
            super(owner, _annon, _loc, _fa, _mode);
            this.otherNamespace = otherNamespace;
        }
        
        public String getOtherNamespace() {
            return this.otherNamespace;
        }
        
        public boolean acceptsNamespace(final String namespaceURI) {
            return !namespaceURI.equals(this.otherNamespace);
        }
        
        public void visit(final XSWildcardVisitor visitor) {
            visitor.other(this);
        }
        
        public Object apply(final XSWildcardFunction function) {
            return function.other(this);
        }
    }
    
    public static final class Finite extends WildcardImpl implements Union
    {
        private final Set<String> names;
        private final Set<String> namesView;
        
        public Finite(final SchemaDocumentImpl owner, final AnnotationImpl _annon, final Locator _loc, final ForeignAttributesImpl _fa, final Set<String> ns, final int _mode) {
            super(owner, _annon, _loc, _fa, _mode);
            this.names = ns;
            this.namesView = Collections.unmodifiableSet((Set<? extends String>)this.names);
        }
        
        public Iterator<String> iterateNamespaces() {
            return this.names.iterator();
        }
        
        public Collection<String> getNamespaces() {
            return this.namesView;
        }
        
        public boolean acceptsNamespace(final String namespaceURI) {
            return this.names.contains(namespaceURI);
        }
        
        public void visit(final XSWildcardVisitor visitor) {
            visitor.union(this);
        }
        
        public Object apply(final XSWildcardFunction function) {
            return function.union(this);
        }
    }
}
