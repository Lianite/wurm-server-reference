// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.reader.internalizer;

import java.io.OutputStream;
import com.sun.xml.xsom.parser.XMLParser;
import java.io.IOException;
import org.xml.sax.SAXException;
import org.xml.sax.InputSource;
import org.w3c.dom.Document;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.ErrorHandler;
import org.xml.sax.EntityResolver;
import java.util.Set;
import java.util.Map;

public final class DOMForest
{
    private final Map core;
    public final LocatorTable locatorTable;
    public final Set outerMostBindings;
    private EntityResolver entityResolver;
    private ErrorHandler errorHandler;
    protected final InternalizationLogic logic;
    private final SAXParserFactory parserFactory;
    private final DocumentBuilder documentBuilder;
    
    public DOMForest(final SAXParserFactory parserFactory, final DocumentBuilder documentBuilder, final InternalizationLogic logic) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: lload_3         /* logic */
        //     1: aconst_null    
        //     2: fload_0         /* this */
        //     3: aconst_null    
        //     4: nop            
        //     5: iaload         
        //     6: imul           
        //     7: ineg           
        //     8: ineg           
        //     9: irem           
        //    10: astore          47
        //    12: laload         
        //    13: ishl           
        //    14: ldiv           
        //    15: idiv           
        //    16: iaload         
        //    17: ddiv           
        //    18: frem           
        //    19: dsub           
        //    20: laload         
        //    21: drem           
        //    22: ladd           
        //    23: ishl           
        //    24: laload         
        //    25: fsub           
        //    26: lsub           
        //    27: ladd           
        //    28: ineg           
        //    29: lneg           
        //    30: frem           
        //    31: lsub           
        //    32: drem           
        //    33: laload         
        //    34: fdiv           
        //    35: ladd           
        //    36: ldiv           
        //    37: lsub           
        //    38: drem           
        //    39: irem           
        //    40: ladd           
        //    41: dadd           
        //    42: lsub           
        //    43: aload_3         /* logic */
        //    44: irem           
        //    45: frem           
        //    46: lsub           
        //    47: fsub           
        //    48: lmul           
        //    49: ishl           
        //    50: lsub           
        //    51: drem           
        //    52: fconst_1       
        //    53: iconst_m1      
        //    54: astore_1        /* parserFactory */
        //    55: iconst_m1      
        //    56: astore_2        /* documentBuilder */
        //    57: iconst_4       
        //    58: iconst_m1      
        //    59: astore_3        /* logic */
        //    60: aconst_null    
        //    61: nop            
        //    62: lload_3         /* logic */
        //    LocalVariableTable:
        //  Start  Length  Slot  Name             Signature
        //  -----  ------  ----  ---------------  ------------------------------------------------------------
        //  0      63      0     this             Lcom/sun/tools/xjc/reader/internalizer/DOMForest;
        //  0      63      1     parserFactory    Ljavax/xml/parsers/SAXParserFactory;
        //  0      63      2     documentBuilder  Ljavax/xml/parsers/DocumentBuilder;
        //  0      63      3     logic            Lcom/sun/tools/xjc/reader/internalizer/InternalizationLogic;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public DOMForest(final InternalizationLogic logic) throws ParserConfigurationException {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: fstore_2       
        //     1: astore_2       
        //     2: lstore_2       
        //     3: swap           
        //     4: fstore_0        /* this */
        //     5: iastore        
        //     6: dastore        
        //     7: dastore        
        //     8: fstore_2       
        //     9: fstore_0        /* this */
        //    10: bastore        
        //    11: astore_3       
        //    12: fstore_2       
        //    13: aastore        
        //    14: aastore        
        //    15: swap           
        //    16: fstore_2       
        //    17: dastore        
        //    18: dastore        
        //    19: iastore        
        //    20: dastore        
        //    21: aconst_null    
        //    22: nop            
        //    23: bipush          106
        //    25: ladd           
        //    26: fneg           
        //    27: ladd           
        //    28: laload         
        //    29: idiv           
        //    30: ladd           
        //    31: fdiv           
        //    32: dsub           
        //    33: laload         
        //    34: iastore        
        //    35: fadd           
        //    36: fmul           
        //    37: lsub           
        //    38: dadd           
        //    39: ineg           
        //    40: iconst_4       
        //    41: iconst_m1      
        //    42: dastore        
        //    43: fconst_1       
        //    44: iconst_m1      
        //    45: aastore        
        //    46: iconst_m1      
        //    47: bastore        
        //    48: fconst_1       
        //    49: nop            
        //    50: goto_w          39126836
        //    55: sastore        
        //    56: fconst_1       
        //    57: iconst_m1      
        //    58: pop            
        //    59: iconst_m1      
        //    60: pop2           
        //    61: aconst_null    
        //    62: nop            
        //    63: dstore_3       
        //    64: dadd           
        //    65: ddiv           
        //    66: ldiv           
        //    67: laload         
        //    68: drem           
        //    69: lneg           
        //    70: fdiv           
        //    71: laload         
        //    72: ineg           
        //    73: ddiv           
        //    74: ddiv           
        //    75: idiv           
        //    76: drem           
        //    77: laload         
        //    78: ishl           
        //    79: fmul           
        //    80: dadd           
        //    81: laload         
        //    82: frem           
        //    83: lsub           
        //    84: ladd           
        //    Exceptions:
        //  throws javax.xml.parsers.ParserConfigurationException
        //    LocalVariableTable:
        //  Start  Length  Slot  Name   Signature
        //  -----  ------  ----  -----  ------------------------------------------------------------
        //  0      85      0     this   Lcom/sun/tools/xjc/reader/internalizer/DOMForest;
        //  0      85      1     logic  Lcom/sun/tools/xjc/reader/internalizer/InternalizationLogic;
        //  51     34      2     dbf    Ljavax/xml/parsers/DocumentBuilderFactory;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException: -81
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
    
    public Document get(final String systemId) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: idiv           
        //     1: drem           
        //     2: laload         
        //     3: ishl           
        //     4: fmul           
        //     5: dadd           
        //     6: laload         
        //     7: frem           
        //     8: lsub           
        //     9: ladd           
        //    10: isub           
        //    11: lsub           
        //    12: frem           
        //    13: laload         
        //    14: lmul           
        //    15: fdiv           
        //    16: ineg           
        //    17: lsub           
        //    18: frem           
        //    19: fdiv           
        //    20: ladd           
        //    21: idiv           
        //    22: lmul           
        //    23: ishr           
        //    24: lsub           
        //    25: frem           
        //    26: laload         
        //    27: fstore_1        /* systemId */
        //    28: iastore        
        //    29: astore_2        /* doc */
        //    30: fstore_3       
        //    31: ddiv           
        //    32: frem           
        //    33: lsub           
        //    34: drem           
        //    35: ineg           
        //    36: lastore        
        //    37: ladd           
        //    38: frem           
        //    39: drem           
        //    40: lsub           
        //    41: frem           
        //    42: aconst_null    
        //    43: nop            
        //    44: fload_0         /* this */
        //    45: dadd           
        //    46: ddiv           
        //    47: ldiv           
        //    48: laload         
        //    49: drem           
        //    50: lneg           
        //    51: fdiv           
        //    52: laload         
        //    53: ishl           
        //    54: ldiv           
        //    55: idiv           
        //    56: laload         
        //    57: ishl           
        //    58: drem           
        //    59: ddiv           
        //    60: ldiv           
        //    61: laload         
        //    62: irem           
        //    63: ladd           
        //    64: frem           
        //    65: drem           
        //    66: lsub           
        //    67: frem           
        //    68: laload         
        //    69: dstore_3       
        //    70: lstore_2        /* doc */
        //    71: pop2           
        //    72: lastore        
        //    73: lastore        
        //    LocalVariableTable:
        //  Start  Length  Slot  Name      Signature
        //  -----  ------  ----  --------  -------------------------------------------------
        //  0      74      0     this      Lcom/sun/tools/xjc/reader/internalizer/DOMForest;
        //  0      74      1     systemId  Ljava/lang/String;
        //  14     60      2     doc       Lorg/w3c/dom/Document;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public String getSystemId(final Document dom) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: iconst_m1      
        //     1: dadd           
        //     2: iconst_4       
        //     3: iconst_m1      
        //     4: isub           
        //     5: fconst_1       
        //     6: iconst_m1      
        //     7: lsub           
        //     8: iconst_m1      
        //     9: fsub           
        //    10: aconst_null    
        //    11: nop            
        //    12: dload_0         /* this */
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
        //    25: fadd           
        //    26: lmul           
        //    27: fdiv           
        //    28: isub           
        //    29: laload         
        //    30: ldiv           
        //    31: ladd           
        //    32: frem           
        //    33: drem           
        //    34: imul           
        //    35: ladd           
        //    36: idiv           
        //    37: idiv           
        //    38: lsub           
        //    39: frem           
        //    40: laload         
        //    41: fstore_1        /* dom */
        //    42: ladd           
        //    43: ineg           
        //    44: ladd           
        //    45: pop            
        //    46: frem           
        //    47: lmul           
        //    48: ineg           
        //    49: lsub           
        //    50: frem           
        //    51: aconst_null    
        //    52: nop            
        //    53: iload_0         /* this */
        //    54: fmul           
        //    55: ladd           
        //    56: fneg           
        //    57: ladd           
        //    58: laload         
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  -------------------------------------------------
        //  34     20      3     e     Ljava/util/Map$Entry;
        //  15     42      2     itr   Ljava/util/Iterator;
        //  0      59      0     this  Lcom/sun/tools/xjc/reader/internalizer/DOMForest;
        //  0      59      1     dom   Lorg/w3c/dom/Document;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public Document parse(final InputSource source) throws SAXException, IOException {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: nop            
        //     1: goto_w          36898563
        //     6: fmul           
        //     7: fconst_1       
        //     8: aconst_null    
        //     9: lload           2
        //    11: dmul           
        //    12: aconst_null    
        //    13: nop            
        //    14: iconst_0       
        //    15: lconst_1       
        //    16: lconst_1       
        //    17: lconst_1       
        //    18: aconst_null    
        //    19: nop            
        //    20: dload_2        
        //    21: fmul           
        //    22: ladd           
        //    23: fneg           
        //    24: ladd           
        //    Exceptions:
        //  throws org.xml.sax.SAXException
        //  throws java.io.IOException
        //    LocalVariableTable:
        //  Start  Length  Slot  Name    Signature
        //  -----  ------  ----  ------  -------------------------------------------------
        //  0      25      0     this    Lcom/sun/tools/xjc/reader/internalizer/DOMForest;
        //  0      25      1     source  Lorg/xml/sax/InputSource;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException: -21
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
    
    public Document parse(final String systemId) throws SAXException, IOException {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: ddiv           
        //     1: frem           
        //     2: lsub           
        //     3: drem           
        //     4: ineg           
        //     5: aconst_null    
        //     6: nop            
        //     7: istore_0        /* this */
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
        //    26: frem           
        //    27: lsub           
        //    28: ladd           
        //    29: isub           
        //    30: lsub           
        //    31: frem           
        //    32: laload         
        //    33: lmul           
        //    34: fdiv           
        //    35: ineg           
        //    36: lsub           
        //    37: frem           
        //    38: fdiv           
        //    39: ladd           
        //    40: idiv           
        //    41: lmul           
        //    42: ishr           
        //    43: lsub           
        //    44: frem           
        //    45: laload         
        //    46: fstore_1        /* systemId */
        //    47: iastore        
        //    48: astore_2        /* is */
        //    49: fstore_3       
        //    50: ddiv           
        //    51: frem           
        //    52: lsub           
        //    53: drem           
        //    54: ineg           
        //    55: fload_2         /* is */
        //    56: dstore_1        /* systemId */
        //    57: ladd           
        //    58: fdiv           
        //    59: isub           
        //    60: idiv           
        //    61: lsub           
        //    62: frem           
        //    63: dstore_2        /* is */
        //    64: ldiv           
        //    65: irem           
        //    66: idiv           
        //    67: aconst_null    
        //    Exceptions:
        //  throws org.xml.sax.SAXException
        //  throws java.io.IOException
        //    LocalVariableTable:
        //  Start  Length  Slot  Name      Signature
        //  -----  ------  ----  --------  -------------------------------------------------
        //  0      68      0     this      Lcom/sun/tools/xjc/reader/internalizer/DOMForest;
        //  0      68      1     systemId  Ljava/lang/String;
        //  29     39      2     is        Lorg/xml/sax/InputSource;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public Document parse(final String systemId, final InputSource inputSource) throws SAXException, IOException {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: frem           
        //     1: drem           
        //     2: lsub           
        //     3: frem           
        //     4: drem           
        //     5: laload         
        //     6: fstore_1        /* systemId */
        //     7: ddiv           
        //     8: dadd           
        //     9: lneg           
        //    10: ldiv           
        //    11: lsub           
        //    12: fdiv           
        //    13: ineg           
        //    14: lstore_3        /* dom */
        //    15: lneg           
        //    16: lmul           
        //    17: idiv           
        //    18: isub           
        //    19: lsub           
        //    20: frem           
        //    21: fstore_3        /* dom */
        //    22: ladd           
        //    23: dadd           
        //    24: ineg           
        //    25: ddiv           
        //    26: frem           
        //    27: lshl           
        //    28: aconst_null    
        //    29: nop            
        //    30: fconst_0       
        //    31: fdiv           
        //    32: lsub           
        //    33: dneg           
        //    34: dstore_2        /* inputSource */
        //    35: fdiv           
        //    36: drem           
        //    37: ineg           
        //    38: ladd           
        //    39: fdiv           
        //    40: dadd           
        //    41: lsub           
        //    42: aconst_null    
        //    43: nop            
        //    44: aload_2         /* inputSource */
        //    45: dload_2         /* inputSource */
        //    46: dload_3         /* dom */
        //    47: astore_1        /* systemId */
        //    48: fmul           
        //    49: ladd           
        //    50: fneg           
        //    51: ladd           
        //    52: ishl           
        //    53: laload         
        //    54: ishl           
        //    55: ldiv           
        //    56: idiv           
        //    57: laload         
        //    58: irem           
        //    59: ladd           
        //    60: frem           
        //    61: drem           
        //    62: lsub           
        //    63: frem           
        //    64: drem           
        //    65: laload         
        //    66: fstore_1        /* systemId */
        //    67: ddiv           
        //    68: dadd           
        //    69: lneg           
        //    70: ldiv           
        //    71: lsub           
        //    72: fdiv           
        //    73: ineg           
        //    74: lstore_3        /* dom */
        //    75: lneg           
        //    76: lmul           
        //    77: idiv           
        //    78: isub           
        //    79: lsub           
        //    80: frem           
        //    81: fstore_3        /* dom */
        //    82: ladd           
        //    83: dadd           
        //    84: ineg           
        //    85: ddiv           
        //    86: frem           
        //    87: lshl           
        //    88: istore_0        /* this */
        //    89: aconst_null    
        //    90: nop            
        //    91: sipush          29541
        //    94: ineg           
        //    95: astore_3        /* dom */
        //    96: ladd           
        //    97: ldiv           
        //    98: lsub           
        //    99: drem           
        //   100: irem           
        //   101: ladd           
        //   102: dadd           
        //   103: lsub           
        //   104: lstore_2        /* inputSource */
        //   105: dneg           
        //   106: ladd           
        //   107: frem           
        //   108: lsub           
        //   109: aconst_null    
        //   110: nop            
        //   111: iconst_1       
        //   112: dload_2         /* inputSource */
        //   113: dup_x1         
        //   114: dload_3         /* dom */
        //   115: sastore        
        //   116: aconst_null    
        //   117: nop            
        //   118: ldc             "Ljavax/xml/parsers/DocumentBuilder;"
        //   120: lsub           
        //   121: dneg           
        //   122: fstore_1        /* systemId */
        //   123: ddiv           
        //   124: dadd           
        //   125: lneg           
        //   126: ldiv           
        //   127: lsub           
        //   128: fdiv           
        //   129: ineg           
        //   130: lstore_3        /* dom */
        //   131: lneg           
        //   132: lmul           
        //   133: idiv           
        //   134: isub           
        //   135: lsub           
        //   136: frem           
        //   137: aconst_null    
        //   138: nop            
        //   139: fload_3         /* dom */
        //   140: dload_2         /* inputSource */
        //   141: dload_3         /* dom */
        //   142: astore_1        /* systemId */
        //   143: fmul           
        //   144: ladd           
        //   145: fneg           
        //   146: ladd           
        //   147: ishl           
        //   148: laload         
        //   149: ishl           
        //   150: ldiv           
        //   151: idiv           
        //   152: laload         
        //   153: irem           
        //   154: ladd           
        //   155: frem           
        //   156: drem           
        //   157: lsub           
        //    Exceptions:
        //  throws org.xml.sax.SAXException
        //  throws java.io.IOException
        //    LocalVariableTable:
        //  Start  Length  Slot  Name         Signature
        //  -----  ------  ----  -----------  -------------------------------------------------
        //  32     114     4     reader       Lorg/xml/sax/XMLReader;
        //  44     102     5     f            Lorg/xml/sax/XMLFilter;
        //  151    5       4     e            Ljavax/xml/parsers/ParserConfigurationException;
        //  0      158     0     this         Lcom/sun/tools/xjc/reader/internalizer/DOMForest;
        //  0      158     1     systemId     Ljava/lang/String;
        //  0      158     2     inputSource  Lorg/xml/sax/InputSource;
        //  8      150     3     dom          Lorg/w3c/dom/Document;
        //    Exceptions:
        //  Try           Handler
        //  Start  End    Start  End    Type                                            
        //  -----  -----  -----  -----  ------------------------------------------------
        //  20     146    149    158    Ljavax/xml/parsers/ParserConfigurationException;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public void transform() throws SAXException {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: dsub           
        //     1: laload         
        //     2: aastore        
        //     3: ineg           
        //     4: frem           
        //    Exceptions:
        //  throws org.xml.sax.SAXException
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  -------------------------------------------------
        //  0      5       0     this  Lcom/sun/tools/xjc/reader/internalizer/DOMForest;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public XMLParser createParser() {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: ineg           
        //     1: frem           
        //     2: lmul           
        //     3: fdiv           
        //     4: dsub           
        //     5: aconst_null    
        //     6: nop            
        //     7: iload           40
        //     9: dstore_2       
        //    10: dload_3        
        //    11: astore_1       
        //    12: fmul           
        //    13: ladd           
        //    14: fneg           
        //    15: ladd           
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  -------------------------------------------------
        //  0      16      0     this  Lcom/sun/tools/xjc/reader/internalizer/DOMForest;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public EntityResolver getEntityResolver() {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: aconst_null    
        //     1: nop            
        //     2: fconst_2       
        //     3: fmul           
        //     4: ladd           
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  -------------------------------------------------
        //  0      5       0     this  Lcom/sun/tools/xjc/reader/internalizer/DOMForest;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public void setEntityResolver(final EntityResolver entityResolver) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: ineg           
        //     1: lmul           
        //     2: idiv           
        //     3: laload         
        //     4: dstore_2       
        //     5: ineg           
        //    LocalVariableTable:
        //  Start  Length  Slot  Name            Signature
        //  -----  ------  ----  --------------  -------------------------------------------------
        //  0      6       0     this            Lcom/sun/tools/xjc/reader/internalizer/DOMForest;
        //  0      6       1     entityResolver  Lorg/xml/sax/EntityResolver;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public ErrorHandler getErrorHandler() {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: lsub           
        //     1: aconst_null    
        //     2: nop            
        //     3: iconst_3       
        //     4: idiv           
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  -------------------------------------------------
        //  0      5       0     this  Lcom/sun/tools/xjc/reader/internalizer/DOMForest;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public void setErrorHandler(final ErrorHandler errorHandler) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: ldiv           
        //     1: ddiv           
        //     2: isub           
        //     3: lmul           
        //     4: fsub           
        //     5: lmul           
        //    LocalVariableTable:
        //  Start  Length  Slot  Name          Signature
        //  -----  ------  ----  ------------  -------------------------------------------------
        //  0      6       0     this          Lcom/sun/tools/xjc/reader/internalizer/DOMForest;
        //  0      6       1     errorHandler  Lorg/xml/sax/ErrorHandler;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public Document[] listDocuments() {
        // 
        // This method could not be decompiled.
        // 
        // Could not show original bytecode, likely due to the same error.
        // 
        // The error that occurred was:
        // 
        // com.strobel.assembler.metadata.MethodBodyParseException: An error occurred while parsing the bytecode of method 'com/sun/tools/xjc/reader/internalizer/DOMForest.listDocuments:()[Lorg/w3c/dom/Document;'.
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
        // Caused by: java.lang.IllegalArgumentException: Argument 'index' must be in the range [0, 360], but value was: 27233.
        //     at com.strobel.core.VerifyArgument.inRange(VerifyArgument.java:346)
        //     at com.strobel.assembler.ir.ConstantPool.get(ConstantPool.java:78)
        //     at com.strobel.assembler.metadata.ClassFileReader$Scope.lookupConstant(ClassFileReader.java:1313)
        //     at com.strobel.assembler.metadata.MethodReader.readBodyCore(MethodReader.java:293)
        //     at com.strobel.assembler.metadata.MethodReader.readBody(MethodReader.java:62)
        //     ... 16 more
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public String[] listSystemIDs() {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: baload         
        //     1: dadd           
        //     2: laload         
        //     3: isub           
        //     4: ddiv           
        //     5: ldiv           
        //     6: laload         
        //     7: fstore_2       
        //     8: idiv           
        //     9: lsub           
        //    10: ldiv           
        //    11: lsub           
        //    12: fdiv           
        //    13: ineg           
        //    14: aconst_null    
        //    15: nop            
        //    16: dconst_1       
        //    17: dsub           
        //    18: lsub           
        //    19: ineg           
        //    20: astore_3       
        //    21: ladd           
        //    22: ldiv           
        //    23: lsub           
        //    24: drem           
        //    25: irem           
        //    26: ladd           
        //    27: dadd           
        //    28: lsub           
        //    29: castore        
        //    30: dastore        
        //    31: dstore_2       
        //    32: aconst_null    
        //    33: nop            
        //    34: iconst_3       
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  -------------------------------------------------
        //  0      35      0     this  Lcom/sun/tools/xjc/reader/internalizer/DOMForest;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public void dump(final OutputStream out) throws IOException {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: imul           
        //     1: lsub           
        //     2: ldiv           
        //     3: ladd           
        //     4: fstore_3       
        //     5: ladd           
        //     6: dadd           
        //     7: ineg           
        //     8: ddiv           
        //     9: frem           
        //    10: lshl           
        //    11: aconst_null    
        //    12: nop            
        //    13: fstore          40
        //    15: astore_1        /* out */
        //    16: fmul           
        //    17: ladd           
        //    18: fneg           
        //    19: ladd           
        //    20: laload         
        //    21: idiv           
        //    22: ladd           
        //    23: fdiv           
        //    24: dsub           
        //    25: laload         
        //    26: aastore        
        //    27: ineg           
        //    28: frem           
        //    29: lmul           
        //    30: fdiv           
        //    31: dsub           
        //    32: istore_0        /* this */
        //    33: dload_3         /* itr */
        //    34: astore_1        /* out */
        //    35: fmul           
        //    36: ladd           
        //    37: fneg           
        //    38: ladd           
        //    39: ishl           
        //    40: laload         
        //    41: ishl           
        //    42: ldiv           
        //    43: idiv           
        //    44: laload         
        //    45: fneg           
        //    46: ladd           
        //    47: idiv           
        //    48: lmul           
        //    49: isub           
        //    50: ladd           
        //    51: ineg           
        //    52: lmul           
        //    53: ddiv           
        //    54: fdiv           
        //    55: laload         
        //    56: aastore        
        //    57: dadd           
        //    58: imul           
        //    59: lsub           
        //    60: ldiv           
        //    61: ladd           
        //    62: fstore_3        /* itr */
        //    63: ladd           
        //    64: dadd           
        //    65: ineg           
        //    66: ddiv           
        //    67: frem           
        //    68: lshl           
        //    69: istore_0        /* this */
        //    70: aconst_null    
        //    71: nop            
        //    72: dload_2         /* it */
        //    73: dload_2         /* it */
        //    74: astore_1        /* out */
        //    75: dadd           
        //    76: ddiv           
        //    77: ldiv           
        //    78: laload         
        //    79: drem           
        //    80: lneg           
        //    81: fdiv           
        //    82: laload         
        //    83: ineg           
        //    84: ddiv           
        //    85: ddiv           
        //    86: idiv           
        //    87: drem           
        //    88: laload         
        //    89: ishl           
        //    90: fmul           
        //    91: dadd           
        //    92: laload         
        //    93: ladd           
        //    94: irem           
        //    95: lmul           
        //    96: laload         
        //    97: fstore_2        /* it */
        //    98: frem           
        //    99: frem           
        //   100: ddiv           
        //   101: frem           
        //   102: astore_1        /* out */
        //   103: lmul           
        //   104: drem           
        //   105: ineg           
        //   106: lsub           
        //   107: fdiv           
        //   108: lsub           
        //   109: frem           
        //   110: istore_0        /* this */
        //   111: dload_3         /* itr */
        //   112: sastore        
        //   113: aconst_null    
        //   114: nop            
        //   115: iload_3         /* itr */
        //   116: dload_2         /* it */
        //   117: astore_1        /* out */
        //   118: ddiv           
        //   119: frem           
        //   120: dsub           
        //   121: laload         
        //   122: ishl           
        //   123: ldiv           
        //   124: idiv           
        //   125: laload         
        //   126: drem           
        //   127: ladd           
        //   128: ishl           
        //    Exceptions:
        //  throws java.io.IOException
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  -------------------------------------------------
        //  42     75      4     e     Ljava/util/Map$Entry;
        //  22     98      3     itr   Ljava/util/Iterator;
        //  7      113     2     it    Ljavax/xml/transform/Transformer;
        //  124    4       2     e     Ljavax/xml/transform/TransformerException;
        //  0      129     0     this  Lcom/sun/tools/xjc/reader/internalizer/DOMForest;
        //  0      129     1     out   Ljava/io/OutputStream;
        //    Exceptions:
        //  Try           Handler
        //  Start  End    Start  End    Type                                      
        //  -----  -----  -----  -----  ------------------------------------------
        //  0      120    123    129    Ljavax/xml/transform/TransformerException;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
}
