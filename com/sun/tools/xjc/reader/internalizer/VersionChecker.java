// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.reader.internalizer;

import org.xml.sax.SAXException;
import org.xml.sax.Attributes;
import org.xml.sax.XMLReader;
import org.xml.sax.Locator;
import org.xml.sax.helpers.XMLFilterImpl;

class VersionChecker extends XMLFilterImpl
{
    private String version;
    private boolean seenRoot;
    private boolean seenBindings;
    private Locator locator;
    private Locator rootTagStart;
    
    public VersionChecker(final XMLReader parent) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: fdiv           
        //     1: ineg           
        //     2: lsub           
        //     3: fdiv           
        //     4: ineg           
        //     5: dstore_1        /* parent */
        //     6: ladd           
        //     7: fdiv           
        //     8: isub           
        //     9: idiv           
        //    10: lsub           
        //    11: frem           
        //    12: istore_0        /* this */
        //    13: dload_3        
        //    14: sastore        
        //    15: aconst_null    
        //    16: nop            
        //    17: dconst_1       
        //    18: drem           
        //    19: lsub           
        //    20: ineg           
        //    21: fstore_2       
        //    22: frem           
        //    23: frem           
        //    24: ddiv           
        //    LocalVariableTable:
        //  Start  Length  Slot  Name    Signature
        //  -----  ------  ----  ------  ------------------------------------------------------
        //  0      25      0     this    Lcom/sun/tools/xjc/reader/internalizer/VersionChecker;
        //  0      25      1     parent  Lorg/xml/sax/XMLReader;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public void startElement(final String namespaceURI, final String localName, final String qName, final Attributes atts) throws SAXException {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: ddiv           
        //     1: idiv           
        //     2: fneg           
        //     3: lsub           
        //     4: frem           
        //     5: istore_0        /* this */
        //     6: dload_3         /* qName */
        //     7: sastore        
        //     8: aconst_null    
        //     9: nop            
        //    10: lload           111
        //    12: frem           
        //    13: dsub           
        //    14: laload         
        //    15: ishl           
        //    16: ldiv           
        //    17: idiv           
        //    18: laload         
        //    19: drem           
        //    20: ladd           
        //    21: ishl           
        //    22: laload         
        //    23: lstore_2        /* localName */
        //    24: ineg           
        //    25: ineg           
        //    26: frem           
        //    27: lmul           
        //    28: fadd           
        //    29: lneg           
        //    30: ineg           
        //    31: lsub           
        //    32: drem           
        //    33: aconst_null    
        //    34: nop            
        //    35: iconst_5       
        //    36: dsub           
        //    37: lsub           
        //    38: ineg           
        //    39: sastore        
        //    40: ladd           
        //    41: idiv           
        //    42: lneg           
        //    43: lsub           
        //    44: aconst_null    
        //    45: nop            
        //    46: fstore          40
        //    48: astore_1        /* namespaceURI */
        //    49: fmul           
        //    50: ladd           
        //    51: fneg           
        //    52: ladd           
        //    53: laload         
        //    54: idiv           
        //    55: ladd           
        //    56: fdiv           
        //    57: dsub           
        //    58: laload         
        //    59: aastore        
        //    60: ineg           
        //    61: frem           
        //    62: lmul           
        //    63: fdiv           
        //    64: dsub           
        //    65: istore_0        /* this */
        //    66: astore_1        /* namespaceURI */
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
        //    77: aastore        
        //    78: ineg           
        //    79: frem           
        //    80: lmul           
        //    81: fdiv           
        //    82: dsub           
        //    83: istore_0        /* this */
        //    84: dload_3         /* qName */
        //    85: astore_1        /* namespaceURI */
        //    86: fmul           
        //    87: ladd           
        //    88: fneg           
        //    89: ladd           
        //    90: laload         
        //    91: idiv           
        //    92: ladd           
        //    93: fdiv           
        //    94: dsub           
        //    95: laload         
        //    96: aastore        
        //    97: ineg           
        //    98: frem           
        //    99: lmul           
        //   100: fdiv           
        //   101: dsub           
        //   102: istore_0        /* this */
        //   103: aconst_null    
        //   104: nop            
        //   105: iconst_3       
        //   106: lsub           
        //   107: lrem           
        //   108: lneg           
        //   109: ladd           
        //   110: idiv           
        //   111: drem           
        //   112: aconst_null    
        //   113: nop            
        //   114: iload           40
        //   116: astore_1        /* namespaceURI */
        //   117: fmul           
        //   118: ladd           
        //   119: fneg           
        //   120: ladd           
        //   121: laload         
        //   122: idiv           
        //   123: ladd           
        //   124: fdiv           
        //   125: dsub           
        //   126: laload         
        //   127: iastore        
        //   128: fadd           
        //   129: fmul           
        //   130: lsub           
        //   131: dadd           
        //   132: ineg           
        //   133: istore_0        /* this */
        //   134: dload_3         /* qName */
        //   135: dup_x1         
        //   136: aconst_null    
        //   137: nop            
        //   138: iaload         
        //   139: dadd           
        //   140: ddiv           
        //   141: ldiv           
        //    Exceptions:
        //  throws org.xml.sax.SAXException
        //    LocalVariableTable:
        //  Start  Length  Slot  Name          Signature
        //  -----  ------  ----  ------------  ------------------------------------------------------
        //  103    11      6     e             Lorg/xml/sax/SAXParseException;
        //  73     54      5     version2      Ljava/lang/String;
        //  0      142     0     this          Lcom/sun/tools/xjc/reader/internalizer/VersionChecker;
        //  0      142     1     namespaceURI  Ljava/lang/String;
        //  0      142     2     localName     Ljava/lang/String;
        //  0      142     3     qName         Ljava/lang/String;
        //  0      142     4     atts          Lorg/xml/sax/Attributes;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public void endDocument() throws SAXException {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: frem           
        //     1: dsub           
        //     2: laload         
        //     3: ishl           
        //     4: ldiv           
        //     5: idiv           
        //     6: laload         
        //     7: drem           
        //     8: ladd           
        //     9: ishl           
        //    10: laload         
        //    11: fstore_2       
        //    12: frem           
        //    13: frem           
        //    14: ddiv           
        //    15: frem           
        //    16: dstore_1       
        //    17: ladd           
        //    18: fdiv           
        //    19: isub           
        //    20: idiv           
        //    21: lsub           
        //    22: frem           
        //    23: istore_0        /* this */
        //    24: aconst_null    
        //    25: nop            
        //    26: dload           111
        //    28: frem           
        //    29: dsub           
        //    30: laload         
        //    31: ishl           
        //    32: ldiv           
        //    33: idiv           
        //    34: laload         
        //    35: drem           
        //    36: ladd           
        //    37: ishl           
        //    38: laload         
        //    39: fstore_2       
        //    40: frem           
        //    41: frem           
        //    42: ddiv           
        //    43: frem           
        //    44: dstore_1        /* e */
        //    45: ladd           
        //    46: fdiv           
        //    47: isub           
        //    48: idiv           
        //    49: lsub           
        //    50: frem           
        //    51: aconst_null    
        //    52: nop            
        //    53: iconst_2       
        //    54: lsub           
        //    55: frem           
        //    56: frem           
        //    57: ddiv           
        //    58: frem           
        //    59: aconst_null    
        //    60: nop            
        //    61: fload_0         /* this */
        //    62: dload_2        
        //    63: astore_1       
        //    64: ddiv           
        //    65: frem           
        //    66: dsub           
        //    67: laload         
        //    68: ishl           
        //    69: ldiv           
        //    70: idiv           
        //    71: laload         
        //    72: drem           
        //    73: ladd           
        //    74: ishl           
        //    75: laload         
        //    76: aastore        
        //    77: lstore_2       
        //    78: pop2           
        //    79: lastore        
        //    80: ladd           
        //    81: frem           
        //    82: drem           
        //    83: lsub           
        //    84: fstore_2       
        //    85: ishl           
        //    86: dadd           
        //    87: lsub           
        //    88: irem           
        //    89: ineg           
        //    90: lmul           
        //    91: ddiv           
        //    Exceptions:
        //  throws org.xml.sax.SAXException
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  ------------------------------------------------------
        //  35     10      1     e     Lorg/xml/sax/SAXParseException;
        //  81     10      1     e     Lorg/xml/sax/SAXParseException;
        //  0      92      0     this  Lcom/sun/tools/xjc/reader/internalizer/VersionChecker;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public void setDocumentLocator(final Locator locator) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: fstore_0        /* this */
        //     1: ddiv           
        //     2: idiv           
        //     3: idiv           
        //     4: lsub           
        //     5: dadd           
        //     6: ineg           
        //     7: lmul           
        //     8: ddiv           
        //     9: fdiv           
        //    10: istore_0        /* this */
        //    LocalVariableTable:
        //  Start  Length  Slot  Name     Signature
        //  -----  ------  ----  -------  ------------------------------------------------------
        //  0      11      0     this     Lcom/sun/tools/xjc/reader/internalizer/VersionChecker;
        //  0      11      1     locator  Lorg/xml/sax/Locator;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
}
