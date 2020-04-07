// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.reader.internalizer;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLFilterImpl;

class WhitespaceStripper extends XMLFilterImpl
{
    private int state;
    private char[] buf;
    private int bufLen;
    private static final int AFTER_START_ELEMENT = 1;
    private static final int AFTER_END_ELEMENT = 2;
    
    public WhitespaceStripper(final XMLReader reader) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: dstore_1        /* reader */
        //     1: ladd           
        //     2: fdiv           
        //     3: isub           
        //     4: idiv           
        //     5: lsub           
        //     6: frem           
        //     7: aconst_null    
        //     8: nop            
        //     9: lload_1         /* reader */
        //    10: dload_2        
        //    11: astore_1        /* reader */
        //    12: ddiv           
        //    13: frem           
        //    14: dsub           
        //    15: laload         
        //    16: ishl           
        //    17: ldiv           
        //    18: idiv           
        //    19: laload         
        //    20: drem           
        //    21: ladd           
        //    22: ishl           
        //    23: laload         
        //    24: fstore_0        /* this */
        //    25: ddiv           
        //    26: fdiv           
        //    27: ineg           
        //    28: lsub           
        //    LocalVariableTable:
        //  Start  Length  Slot  Name    Signature
        //  -----  ------  ----  ------  ----------------------------------------------------------
        //  0      29      0     this    Lcom/sun/tools/xjc/reader/internalizer/WhitespaceStripper;
        //  0      29      1     reader  Lorg/xml/sax/XMLReader;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public void characters(final char[] ch, final int start, final int length) throws SAXException {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: astore_1        /* ch */
        //     1: ddiv           
        //     2: frem           
        //     3: dsub           
        //     4: laload         
        //     5: ishl           
        //     6: ldiv           
        //     7: idiv           
        //     8: laload         
        //     9: drem           
        //    10: ladd           
        //    11: ishl           
        //    12: laload         
        //    13: fstore_2        /* start */
        //    14: fdiv           
        //    15: ineg           
        //    16: lmul           
        //    17: ineg           
        //    18: lshl           
        //    19: dastore        
        //    20: lsub           
        //    21: drem           
        //    22: ddiv           
        //    23: idiv           
        //    24: fneg           
        //    25: lsub           
        //    26: frem           
        //    27: istore_0        /* this */
        //    28: dload_3         /* length */
        //    29: sastore        
        //    30: aconst_null    
        //    31: nop            
        //    32: dconst_0       
        //    33: fmul           
        //    34: ladd           
        //    35: fneg           
        //    36: ladd           
        //    37: laload         
        //    38: idiv           
        //    39: ladd           
        //    40: fdiv           
        //    41: dsub           
        //    42: laload         
        //    43: astore_2        /* start */
        //    44: ladd           
        //    45: ineg           
        //    46: imul           
        //    47: aconst_null    
        //    48: nop            
        //    49: iconst_0       
        //    50: ldiv           
        //    51: ladd           
        //    52: ishl           
        //    53: aconst_null    
        //    54: nop            
        //    55: iconst_2       
        //    56: dload_2         /* start */
        //    57: dstore_2        /* start */
        //    58: dstore_2        /* start */
        //    59: dload_3         /* length */
        //    60: dstore_2        /* start */
        //    61: aconst_null    
        //    62: nop            
        //    63: bipush          106
        //    65: ladd           
        //    66: fneg           
        //    67: ladd           
        //    68: laload         
        //    69: idiv           
        //    70: ladd           
        //    71: fdiv           
        //    72: dsub           
        //    73: laload         
        //    74: aastore        
        //    75: lshl           
        //    76: drem           
        //    77: ineg           
        //    78: lsub           
        //    79: ldiv           
        //    80: aconst_null    
        //    81: nop            
        //    82: lconst_0       
        //    83: ladd           
        //    84: frem           
        //    85: frem           
        //    86: ladd           
        //    87: lshl           
        //    88: dadd           
        //    89: ddiv           
        //    90: irem           
        //    91: lshl           
        //    92: aconst_null    
        //    93: nop            
        //    94: aload_0         /* this */
        //    95: dload_2         /* start */
        //    96: astore_1        /* ch */
        //    97: fmul           
        //    98: ladd           
        //    99: fneg           
        //   100: ladd           
        //   101: laload         
        //   102: idiv           
        //   103: ladd           
        //   104: fdiv           
        //   105: dsub           
        //   106: laload         
        //   107: iastore        
        //   108: fadd           
        //   109: fmul           
        //   110: lsub           
        //   111: dadd           
        //   112: ineg           
        //   113: istore_0        /* this */
        //   114: dstore_2        /* start */
        //   115: astore_1        /* ch */
        //   116: fmul           
        //   117: ladd           
        //   118: fneg           
        //   119: ladd           
        //   120: laload         
        //   121: idiv           
        //   122: ladd           
        //   123: fdiv           
        //   124: dsub           
        //   125: laload         
        //   126: iastore        
        //   127: fadd           
        //   128: fmul           
        //   129: lsub           
        //   130: dadd           
        //   131: ineg           
        //   132: istore_0        /* this */
        //   133: dstore_2        /* start */
        //   134: dstore_2        /* start */
        //   135: dload_3         /* length */
        //   136: sastore        
        //   137: aconst_null    
        //   138: nop            
        //   139: fload_2         /* start */
        //   140: dadd           
        //   141: ddiv           
        //   142: ldiv           
        //   143: laload         
        //   144: drem           
        //   145: lneg           
        //   146: fdiv           
        //   147: laload         
        //   148: ishl           
        //   149: ldiv           
        //   150: idiv           
        //   151: laload         
        //   152: fadd           
        //   153: lmul           
        //    Exceptions:
        //  throws org.xml.sax.SAXException
        //    LocalVariableTable:
        //  Start  Length  Slot  Name    Signature
        //  -----  ------  ----  ------  ----------------------------------------------------------
        //  66     21      4     newBuf  [C
        //  122    31      5     i       I
        //  119    34      4     len     I
        //  0      154     0     this    Lcom/sun/tools/xjc/reader/internalizer/WhitespaceStripper;
        //  0      154     1     ch      [C
        //  0      154     2     start   I
        //  0      154     3     length  I
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public void startElement(final String uri, final String localName, final String qName, final Attributes atts) throws SAXException {
        // 
        // This method could not be decompiled.
        // 
        // Could not show original bytecode, likely due to the same error.
        // 
        // The error that occurred was:
        // 
        // com.strobel.assembler.metadata.MethodBodyParseException: An error occurred while parsing the bytecode of method 'com/sun/tools/xjc/reader/internalizer/WhitespaceStripper.startElement:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Lorg/xml/sax/Attributes;)V'.
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
    
    public void endElement(final String uri, final String localName, final String qName) throws SAXException {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: nop            
        //     1: iload_2         /* localName */
        //     2: nop            
        //     3: astore_3        /* qName */
        //     4: nop            
        //     5: fload_3         /* qName */
        //     6: nop            
        //     7: iastore        
        //     8: nop            
        //     9: iaload         
        //    10: nop            
        //    11: lastore        
        //    12: nop            
        //    13: lload_2         /* localName */
        //    14: nop            
        //    15: nop            
        //    16: nop            
        //    Exceptions:
        //  throws org.xml.sax.SAXException
        //    LocalVariableTable:
        //  Start  Length  Slot  Name       Signature
        //  -----  ------  ----  ---------  ----------------------------------------------------------
        //  0      17      0     this       Lcom/sun/tools/xjc/reader/internalizer/WhitespaceStripper;
        //  0      17      1     uri        Ljava/lang/String;
        //  0      17      2     localName  Ljava/lang/String;
        //  0      17      3     qName      Ljava/lang/String;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    private void processPendingText() throws SAXException {
        // 
        // This method could not be decompiled.
        // 
        // Could not show original bytecode, likely due to the same error.
        // 
        // The error that occurred was:
        // 
        // com.strobel.assembler.metadata.MethodBodyParseException: An error occurred while parsing the bytecode of method 'com/sun/tools/xjc/reader/internalizer/WhitespaceStripper.processPendingText:()V'.
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
        //     at com.strobel.assembler.metadata.MethodReader.readBodyCore(MethodReader.java:214)
        //     at com.strobel.assembler.metadata.MethodReader.readBody(MethodReader.java:62)
        //     ... 16 more
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public void ignorableWhitespace(final char[] ch, final int start, final int length) throws SAXException {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: ddiv           
        //    Exceptions:
        //  throws org.xml.sax.SAXException
        //    LocalVariableTable:
        //  Start  Length  Slot  Name    Signature
        //  -----  ------  ----  ------  ----------------------------------------------------------
        //  0      1       0     this    Lcom/sun/tools/xjc/reader/internalizer/WhitespaceStripper;
        //  0      1       1     ch      [C
        //  0      1       2     start   I
        //  0      1       3     length  I
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
}
