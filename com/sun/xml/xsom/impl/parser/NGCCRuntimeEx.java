// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.xml.xsom.impl.parser;

import com.sun.xml.xsom.impl.UName;
import org.relaxng.datatype.ValidationContext;
import org.xml.sax.Attributes;
import org.xml.sax.ErrorHandler;
import com.sun.xml.xsom.parser.AnnotationParser;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import com.sun.xml.xsom.XSDeclaration;
import java.util.Stack;
import com.sun.xml.xsom.impl.SchemaImpl;
import com.sun.xml.xsom.impl.parser.state.NGCCRuntime;

public class NGCCRuntimeEx extends NGCCRuntime implements PatcherManager
{
    public final ParserContext parser;
    public SchemaImpl currentSchema;
    public int finalDefault;
    public int blockDefault;
    public boolean elementFormDefault;
    public boolean attributeFormDefault;
    public boolean chameleonMode;
    private String documentSystemId;
    private final Stack elementNames;
    private Context currentContext;
    public static final String XMLSchemaNSURI = "http://www.w3.org/2001/XMLSchema";
    
    NGCCRuntimeEx(final ParserContext _parser) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     1: sastore        
        //     2: aconst_null    
        //     3: nop            
        //     4: lconst_1       
        //     5: frem           
        //     6: lsub           
        //    LocalVariableTable:
        //  Start  Length  Slot  Name     Signature
        //  -----  ------  ----  -------  --------------------------------------------
        //  0      7       0     this     Lcom/sun/xml/xsom/impl/parser/NGCCRuntimeEx;
        //  0      7       1     _parser  Lcom/sun/xml/xsom/impl/parser/ParserContext;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    private NGCCRuntimeEx(final ParserContext _parser, final boolean chameleonMode) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: fdiv           
        //     1: laload         
        //     2: ishl           
        //     3: ldiv           
        //     4: idiv           
        //     5: laload         
        //     6: ishl           
        //     7: drem           
        //     8: ddiv           
        //     9: ldiv           
        //    10: laload         
        //    11: lmul           
        //    12: ldiv           
        //    13: irem           
        //    14: idiv           
        //    15: laload         
        //    16: irem           
        //    17: ladd           
        //    18: frem           
        //    19: drem           
        //    20: lsub           
        //    21: frem           
        //    22: laload         
        //    23: astore_3       
        //    24: dstore_0        /* this */
        //    25: fstore_0        /* this */
        //    26: fstore_0        /* this */
        //    27: dastore        
        //    28: lneg           
        //    29: fdiv           
        //    30: ineg           
        //    31: lmul           
        //    32: ldiv           
        //    33: lsub           
        //    34: fstore_2        /* chameleonMode */
        //    35: ishl           
        //    36: istore_0        /* this */
        //    37: dup_x1         
        //    38: astore_1        /* _parser */
        //    39: fmul           
        //    40: ladd           
        //    41: fneg           
        //    42: ladd           
        //    43: laload         
        //    44: idiv           
        //    45: ladd           
        //    46: fdiv           
        //    47: dsub           
        //    48: laload         
        //    49: aastore        
        //    50: ineg           
        //    51: frem           
        //    52: lmul           
        //    53: fdiv           
        //    54: dsub           
        //    55: istore_0        /* this */
        //    56: dload_3        
        //    57: sastore        
        //    58: aconst_null    
        //    59: nop            
        //    60: dconst_0       
        //    61: drem           
        //    62: lsub           
        //    63: ineg           
        //    64: dastore        
        //    65: ddiv           
        //    66: ddiv           
        //    67: ineg           
        //    68: dstore_1        /* _parser */
        //    69: ladd           
        //    70: fdiv           
        //    71: isub           
        //    72: idiv           
        //    73: lsub           
        //    74: frem           
        //    75: aconst_null    
        //    76: nop            
        //    77: baload         
        //    78: dload_2         /* chameleonMode */
        //    79: astore_1        /* _parser */
        //    80: dadd           
        //    81: ddiv           
        //    82: ldiv           
        //    83: laload         
        //    84: drem           
        //    85: lneg           
        //    86: fdiv           
        //    87: laload         
        //    88: ishl           
        //    89: ldiv           
        //    90: idiv           
        //    LocalVariableTable:
        //  Start  Length  Slot  Name           Signature
        //  -----  ------  ----  -------------  --------------------------------------------
        //  0      91      0     this           Lcom/sun/xml/xsom/impl/parser/NGCCRuntimeEx;
        //  0      91      1     _parser        Lcom/sun/xml/xsom/impl/parser/ParserContext;
        //  0      91      2     chameleonMode  Z
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public void checkDoubleDefError(final XSDeclaration c) throws SAXException {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: astore_1        /* c */
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
        //    13: dstore_2       
        //    14: fdiv           
        //    15: irem           
        //    16: lneg           
        //    17: ineg           
        //    18: aastore        
        //    19: ddiv           
        //    20: lneg           
        //    21: frem           
        //    22: dadd           
        //    23: lsub           
        //    24: istore_0        /* this */
        //    25: astore_1        /* c */
        //    26: ddiv           
        //    27: frem           
        //    28: dsub           
        //    29: laload         
        //    30: ishl           
        //    31: ldiv           
        //    32: idiv           
        //    33: laload         
        //    34: drem           
        //    35: ladd           
        //    36: ishl           
        //    37: laload         
        //    38: fstore_0        /* this */
        //    39: ddiv           
        //    Exceptions:
        //  throws org.xml.sax.SAXException
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  --------------------------------------------
        //  0      40      0     this  Lcom/sun/xml/xsom/impl/parser/NGCCRuntimeEx;
        //  0      40      1     c     Lcom/sun/xml/xsom/XSDeclaration;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public void addPatcher(final Patch patcher) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: lsub           
        //     1: ineg           
        //     2: lstore_2       
        //     3: fdiv           
        //     4: fdiv           
        //     5: ddiv           
        //     6: ineg           
        //     7: ladd           
        //     8: ineg           
        //     9: lmul           
        //    10: ddiv           
        //    11: fdiv           
        //    12: lastore        
        //    13: ladd           
        //    LocalVariableTable:
        //  Start  Length  Slot  Name     Signature
        //  -----  ------  ----  -------  --------------------------------------------
        //  0      14      0     this     Lcom/sun/xml/xsom/impl/parser/NGCCRuntimeEx;
        //  0      14      1     patcher  Lcom/sun/xml/xsom/impl/parser/Patch;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public void reportError(final String msg, final Locator loc) throws SAXException {
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
        //     6: ishl           
        //     7: ldiv           
        //     8: idiv           
        //     9: laload         
        //    10: ishl           
        //    11: drem           
        //    12: ddiv           
        //    13: ldiv           
        //    14: laload         
        //    Exceptions:
        //  throws org.xml.sax.SAXException
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  --------------------------------------------
        //  0      15      0     this  Lcom/sun/xml/xsom/impl/parser/NGCCRuntimeEx;
        //  0      15      1     msg   Ljava/lang/String;
        //  0      15      2     loc   Lorg/xml/sax/Locator;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    private InputSource resolveRelativeURL(final String relativeUri) throws SAXException {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: drem           
        //     1: lsub           
        //     2: frem           
        //     3: istore_0        /* this */
        //     4: aconst_null    
        //     5: nop            
        //     6: laload         
        //     7: dadd           
        //     8: ddiv           
        //     9: ldiv           
        //    10: laload         
        //    11: drem           
        //    12: lneg           
        //    13: fdiv           
        //    14: laload         
        //    15: ishl           
        //    16: ldiv           
        //    17: idiv           
        //    18: laload         
        //    19: ishl           
        //    20: drem           
        //    21: ddiv           
        //    22: ldiv           
        //    23: laload         
        //    24: irem           
        //    25: ladd           
        //    26: frem           
        //    27: drem           
        //    28: lsub           
        //    29: frem           
        //    30: laload         
        //    31: lstore_2        /* baseUri */
        //    32: fdiv           
        //    33: fdiv           
        //    Exceptions:
        //  throws org.xml.sax.SAXException
        //    LocalVariableTable:
        //  Start  Length  Slot  Name         Signature
        //  -----  ------  ----  -----------  --------------------------------------------
        //  0      34      0     this         Lcom/sun/xml/xsom/impl/parser/NGCCRuntimeEx;
        //  0      34      1     relativeUri  Ljava/lang/String;
        //  10     24      2     baseUri      Ljava/lang/String;
        //  25     9       3     systemId     Ljava/lang/String;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public void includeSchema(final String schemaLocation) throws SAXException {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: fmul           
        //     1: ladd           
        //     2: fneg           
        //     3: ladd           
        //     4: laload         
        //     5: idiv           
        //     6: ladd           
        //     7: fdiv           
        //     8: dsub           
        //     9: laload         
        //    10: iastore        
        //    11: fadd           
        //    12: fmul           
        //    13: lsub           
        //    14: dadd           
        //    15: ineg           
        //    16: istore_0        /* this */
        //    17: aconst_null    
        //    18: nop            
        //    19: istore_2        /* runtime */
        //    20: dload_2         /* runtime */
        //    21: astore_1        /* schemaLocation */
        //    22: fmul           
        //    23: ladd           
        //    24: fneg           
        //    25: ladd           
        //    26: laload         
        //    27: idiv           
        //    28: ladd           
        //    29: fdiv           
        //    30: dsub           
        //    31: laload         
        //    32: aastore        
        //    33: ineg           
        //    34: frem           
        //    35: lmul           
        //    36: fdiv           
        //    37: dsub           
        //    38: istore_0        /* this */
        //    39: astore_1        /* schemaLocation */
        //    40: ddiv           
        //    41: frem           
        //    42: dsub           
        //    43: laload         
        //    44: frem           
        //    45: lsub           
        //    46: idiv           
        //    47: ladd           
        //    48: ishl           
        //    49: fdiv           
        //    50: dsub           
        //    51: laload         
        //    52: isub           
        //    53: ladd           
        //    54: ineg           
        //    55: ladd           
        //    56: ineg           
        //    57: lshl           
        //    58: irem           
        //    59: lsub           
        //    60: laload         
        //    61: sastore        
        //    62: ladd           
        //    63: idiv           
        //    64: lmul           
        //    65: isub           
        //    66: ladd           
        //    67: ineg           
        //    68: lmul           
        //    69: ddiv           
        //    70: fdiv           
        //    71: fstore_0        /* this */
        //    72: ddiv           
        //    73: fdiv           
        //    74: ineg           
        //    75: lsub           
        //    76: ishl           
        //    77: ineg           
        //    78: istore_0        /* this */
        //    79: dload_3        
        //    80: sastore        
        //    81: aconst_null    
        //    82: nop            
        //    83: lconst_1       
        //    84: ladd           
        //    85: dadd           
        //    86: dadd           
        //    87: lsub           
        //    88: drem           
        //    89: drem           
        //    90: fload_2         /* runtime */
        //    91: faload         
        //    92: faload         
        //    93: faload         
        //    94: aconst_null    
        //    95: nop            
        //    96: fmul           
        //    97: dload_2         /* runtime */
        //    Exceptions:
        //  throws org.xml.sax.SAXException
        //    LocalVariableTable:
        //  Start  Length  Slot  Name            Signature
        //  -----  ------  ----  --------------  --------------------------------------------
        //  61     15      3     e               Lorg/xml/sax/SAXParseException;
        //  0      98      0     this            Lcom/sun/xml/xsom/impl/parser/NGCCRuntimeEx;
        //  0      98      1     schemaLocation  Ljava/lang/String;
        //  16     82      2     runtime         Lcom/sun/xml/xsom/impl/parser/NGCCRuntimeEx;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public void importSchema(final String ns, final String schemaLocation) throws SAXException {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: astore_3       
        //     1: ladd           
        //     2: ldiv           
        //     3: lsub           
        //     4: drem           
        //     5: irem           
        //     6: ladd           
        //     7: dadd           
        //     8: lsub           
        //     9: lastore        
        //    10: frem           
        //    11: lsub           
        //    12: fsub           
        //    13: lmul           
        //    14: ishl           
        //    15: aconst_null    
        //    16: nop            
        //    17: dload_0         /* this */
        //    18: dload_2         /* schemaLocation */
        //    19: astore_1        /* ns */
        //    20: fmul           
        //    21: ladd           
        //    22: fneg           
        //    23: ladd           
        //    24: laload         
        //    25: idiv           
        //    26: ladd           
        //    27: fdiv           
        //    28: dsub           
        //    29: laload         
        //    30: aastore        
        //    31: ineg           
        //    32: frem           
        //    Exceptions:
        //  throws org.xml.sax.SAXException
        //    LocalVariableTable:
        //  Start  Length  Slot  Name            Signature
        //  -----  ------  ----  --------------  --------------------------------------------
        //  0      33      0     this            Lcom/sun/xml/xsom/impl/parser/NGCCRuntimeEx;
        //  0      33      1     ns              Ljava/lang/String;
        //  0      33      2     schemaLocation  Ljava/lang/String;
        //  17     16      3     newRuntime      Lcom/sun/xml/xsom/impl/parser/NGCCRuntimeEx;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public boolean hasAlreadyBeenRead(final String targetNamespace) {
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
        //     6: dstore_2       
        //     7: fdiv           
        //     8: ineg           
        //     9: lsub           
        //    10: dsub           
        //    11: lsub           
        //    12: frem           
        //    13: istore_0        /* this */
        //    14: aconst_null    
        //    15: nop            
        //    16: dconst_1       
        //    17: dsub           
        //    18: lsub           
        //    19: ineg           
        //    20: fstore_0        /* this */
        //    21: ddiv           
        //    22: idiv           
        //    23: lneg           
        //    24: ldiv           
        //    25: fdiv           
        //    26: astore_3       
        //    27: lneg           
        //    28: ldiv           
        //    29: fadd           
        //    30: lsub           
        //    31: frem           
        //    32: aconst_null    
        //    33: nop            
        //    34: fload           106
        //    36: ladd           
        //    37: fneg           
        //    38: ladd           
        //    39: laload         
        //    40: ineg           
        //    41: lsub           
        //    42: ishl           
        //    43: ineg           
        //    44: laload         
        //    45: astore_2       
        //    46: lsub           
        //    47: drem           
        //    48: drem           
        //    49: ladd           
        //    50: dsub           
        //    51: lsub           
        //    52: fstore_3       
        //    53: ddiv           
        //    54: frem           
        //    55: ldiv           
        //    56: ladd           
        //    57: ineg           
        //    58: aconst_null    
        //    59: nop            
        //    60: ldiv           
        //    61: dload_2        
        //    62: astore_1        /* targetNamespace */
        //    63: ddiv           
        //    64: frem           
        //    65: dsub           
        //    66: laload         
        //    67: frem           
        //    68: lsub           
        //    69: idiv           
        //    70: ladd           
        //    71: ishl           
        //    72: fdiv           
        //    73: dsub           
        //    74: laload         
        //    75: isub           
        //    76: ladd           
        //    77: ineg           
        //    78: ladd           
        //    79: ineg           
        //    80: lshl           
        //    81: irem           
        //    82: lsub           
        //    83: laload         
        //    84: sastore        
        //    85: ladd           
        //    86: idiv           
        //    LocalVariableTable:
        //  Start  Length  Slot  Name             Signature
        //  -----  ------  ----  ---------------  -------------------------------------------------------------
        //  0      87      0     this             Lcom/sun/xml/xsom/impl/parser/NGCCRuntimeEx;
        //  0      87      1     targetNamespace  Ljava/lang/String;
        //  65     22      2     docIdentity      Lcom/sun/xml/xsom/impl/parser/ParserContext$DocumentIdentity;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public void parseEntity(final InputSource source, final boolean includeMode, final String expectedNamespace, final Locator importLocation) throws SAXException {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: lstore_2        /* includeMode */
        //     1: ineg           
        //     2: ineg           
        //     3: frem           
        //     4: lmul           
        //     5: fadd           
        //     6: lneg           
        //     7: ineg           
        //     8: lsub           
        //     9: drem           
        //    10: aconst_null    
        //    11: nop            
        //    12: iload_0         /* this */
        //    13: dload_2         /* includeMode */
        //    14: dload_3         /* expectedNamespace */
        //    15: astore_1        /* source */
        //    16: ddiv           
        //    17: frem           
        //    18: dsub           
        //    19: laload         
        //    20: ishl           
        //    21: ldiv           
        //    22: idiv           
        //    23: laload         
        //    24: drem           
        //    25: ladd           
        //    26: ishl           
        //    27: laload         
        //    28: lstore_2        /* includeMode */
        //    29: ineg           
        //    30: ineg           
        //    31: frem           
        //    32: lmul           
        //    33: fadd           
        //    34: lneg           
        //    35: ineg           
        //    36: lsub           
        //    37: drem           
        //    38: istore_0        /* this */
        //    39: aconst_null    
        //    40: nop            
        //    41: lload           111
        //    43: frem           
        //    44: dsub           
        //    45: laload         
        //    46: ishl           
        //    47: ldiv           
        //    48: idiv           
        //    49: laload         
        //    50: drem           
        //    51: ladd           
        //    52: ishl           
        //    53: laload         
        //    54: lstore_2        /* includeMode */
        //    55: ineg           
        //    56: ineg           
        //    57: frem           
        //    58: lmul           
        //    59: fadd           
        //    60: lneg           
        //    61: ineg           
        //    62: lsub           
        //    63: drem           
        //    64: aconst_null    
        //    65: nop            
        //    66: lconst_0       
        //    67: dsub           
        //    68: lsub           
        //    69: ineg           
        //    70: astore_1        /* source */
        //    71: lsub           
        //    72: fdiv           
        //    73: dsub           
        //    74: ineg           
        //    75: imul           
        //    76: aconst_null    
        //    77: nop            
        //    78: iconst_3       
        //    79: dsub           
        //    80: lsub           
        //    81: ineg           
        //    82: castore        
        //    83: dastore        
        //    84: dstore_2        /* includeMode */
        //    85: aconst_null    
        //    86: nop            
        //    87: iconst_3       
        //    88: idiv           
        //    89: lsub           
        //    90: fdiv           
        //    91: dsub           
        //    92: ineg           
        //    93: imul           
        //    94: aconst_null    
        //    95: nop            
        //    96: fconst_1       
        //    97: dsub           
        //    98: lsub           
        //    99: ineg           
        //   100: astore_1        /* source */
        //   101: ddiv           
        //   102: dadd           
        //   103: ladd           
        //   104: idiv           
        //   105: astore_3        /* expectedNamespace */
        //   106: ladd           
        //    Exceptions:
        //  throws org.xml.sax.SAXException
        //    LocalVariableTable:
        //  Start  Length  Slot  Name               Signature
        //  -----  ------  ----  -----------------  --------------------------------------------
        //  74     17      7     se                 Lorg/xml/sax/SAXParseException;
        //  56     35      6     e                  Ljava/io/IOException;
        //  20     71      5     s                  Lcom/sun/xml/xsom/impl/parser/state/Schema;
        //  96     10      5     e                  Lorg/xml/sax/SAXException;
        //  0      107     0     this               Lcom/sun/xml/xsom/impl/parser/NGCCRuntimeEx;
        //  0      107     1     source             Lorg/xml/sax/InputSource;
        //  0      107     2     includeMode        Z
        //  0      107     3     expectedNamespace  Ljava/lang/String;
        //  0      107     4     importLocation     Lorg/xml/sax/Locator;
        //    Exceptions:
        //  Try           Handler
        //  Start  End    Start  End    Type                      
        //  -----  -----  -----  -----  --------------------------
        //  26     51     54     94     Ljava/io/IOException;
        //  8      91     94     107    Lorg/xml/sax/SAXException;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public AnnotationParser createAnnotationParser() {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: nop            
        //     1: nop            
        //     2: nop            
        //     3: aconst_null    
        //     4: nop            
        //     5: lcmp           
        //     6: nop            
        //     7: fcmpl          
        //     8: nop            
        //     9: nop            
        //    10: nop            
        //    11: aconst_null    
        //    12: nop            
        //    13: fcmpg          
        //    14: nop            
        //    15: fcmpl          
        //    16: nop            
        //    17: nop            
        //    18: nop            
        //    19: aconst_null    
        //    20: nop            
        //    21: dcmpl          
        //    22: nop            
        //    23: dcmpg          
        //    24: nop            
        //    25: nop            
        //    26: nop            
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  --------------------------------------------
        //  0      27      0     this  Lcom/sun/xml/xsom/impl/parser/NGCCRuntimeEx;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public String getAnnotationContextElementName() {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: aload           this
        //     2: goto            158
        //     5: nop            
        //     6: aconst_null    
        //     7: nop            
        //     8: jsr             8
        //    11: nop            
        //    12: iconst_m1      
        //    13: nop            
        //    14: iload_3        
        //    15: bipush          24
        //    17: nop            
        //    18: ret             this
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  --------------------------------------------
        //  0      20      0     this  Lcom/sun/xml/xsom/impl/parser/NGCCRuntimeEx;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public Locator copyLocator() {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: freturn        
        //     1: nop            
        //     2: nop            
        //     3: nop            
        //     4: lload           this
        //     6: iconst_m1      
        //     7: nop            
        //     8: nop            
        //     9: nop            
        //    10: iconst_5       
        //    11: nop            
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  --------------------------------------------
        //  0      12      0     this  Lcom/sun/xml/xsom/impl/parser/NGCCRuntimeEx;
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
        // Could not show original bytecode, likely due to the same error.
        // 
        // The error that occurred was:
        // 
        // com.strobel.assembler.metadata.MethodBodyParseException: An error occurred while parsing the bytecode of method 'com/sun/xml/xsom/impl/parser/NGCCRuntimeEx.getErrorHandler:()Lorg/xml/sax/ErrorHandler;'.
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
    
    public void onEnterElementConsumed(final String uri, final String localName, final String qname, final Attributes atts) throws SAXException {
        // 
        // This method could not be decompiled.
        // 
        // Could not show original bytecode, likely due to the same error.
        // 
        // The error that occurred was:
        // 
        // com.strobel.assembler.metadata.MethodBodyParseException: An error occurred while parsing the bytecode of method 'com/sun/xml/xsom/impl/parser/NGCCRuntimeEx.onEnterElementConsumed:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Lorg/xml/sax/Attributes;)V'.
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
    
    public void onLeaveElementConsumed(final String uri, final String localName, final String qname) throws SAXException {
        // 
        // This method could not be decompiled.
        // 
        // Could not show original bytecode, likely due to the same error.
        // 
        // The error that occurred was:
        // 
        // com.strobel.assembler.metadata.MethodBodyParseException: An error occurred while parsing the bytecode of method 'com/sun/xml/xsom/impl/parser/NGCCRuntimeEx.onLeaveElementConsumed:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V'.
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
    
    public ValidationContext createValidationContext() {
        // 
        // This method could not be decompiled.
        // 
        // Could not show original bytecode, likely due to the same error.
        // 
        // The error that occurred was:
        // 
        // com.strobel.assembler.metadata.MethodBodyParseException: An error occurred while parsing the bytecode of method 'com/sun/xml/xsom/impl/parser/NGCCRuntimeEx.createValidationContext:()Lorg/relaxng/datatype/ValidationContext;'.
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
    
    public void startPrefixMapping(final String prefix, final String uri) throws SAXException {
        // 
        // This method could not be decompiled.
        // 
        // Could not show original bytecode, likely due to the same error.
        // 
        // The error that occurred was:
        // 
        // com.strobel.assembler.metadata.MethodBodyParseException: An error occurred while parsing the bytecode of method 'com/sun/xml/xsom/impl/parser/NGCCRuntimeEx.startPrefixMapping:(Ljava/lang/String;Ljava/lang/String;)V'.
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
    
    public void endPrefixMapping(final String prefix) throws SAXException {
    }
    
    public UName parseUName(final String qname) throws SAXException {
        // 
        // This method could not be decompiled.
        // 
        // Could not show original bytecode, likely due to the same error.
        // 
        // The error that occurred was:
        // 
        // com.strobel.assembler.metadata.MethodBodyParseException: An error occurred while parsing the bytecode of method 'com/sun/xml/xsom/impl/parser/NGCCRuntimeEx.parseUName:(Ljava/lang/String;)Lcom/sun/xml/xsom/impl/UName;'.
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
    
    public boolean parseBoolean(final String v) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: aload_2        
        //     1: ifnull          11
        //     4: aload_3        
        //     5: aload_2        
        //     6: invokestatic    com/sun/xml/xsom/impl/parser/NGCCRuntimeEx.resolveRelativeURL:(Ljava/lang/String;)Lorg/xml/sax/InputSource;
        //     9: astore          4
        //    11: aload_0         /* this */
        //    12: getfield        com/sun/xml/xsom/impl/parser/NGCCRuntimeEx.parser:Lcom/sun/xml/xsom/impl/parser/ParserContext;
        //    15: invokevirtual   com/sun/xml/xsom/impl/SchemaImpl.getTargetNamespace:()Ljava/lang/String;
        //    18: astore          5
        //    20: aload           5
        //    22: ifnull          45
        //    25: aload           5
        //    27: aload_1         /* v */
        //    28: aload           4
        //    30: invokeinterface com/sun/xml/xsom/impl/parser/NGCCRuntimeEx.parseEntity:(Lorg/xml/sax/InputSource;ZLjava/lang/String;Lorg/xml/sax/Locator;)V
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  --------------------------------------------
        //  0      35      0     this  Lcom/sun/xml/xsom/impl/parser/NGCCRuntimeEx;
        //  0      35      1     v     Ljava/lang/String;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    protected void unexpectedX(final String token) throws SAXException {
        // 
        // This method could not be decompiled.
        // 
        // Could not show original bytecode, likely due to the same error.
        // 
        // The error that occurred was:
        // 
        // com.strobel.assembler.metadata.MethodBodyParseException: An error occurred while parsing the bytecode of method 'com/sun/xml/xsom/impl/parser/NGCCRuntimeEx.unexpectedX:(Ljava/lang/String;)V'.
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
        // Caused by: java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    private static class Context implements ValidationContext
    {
        private final String prefix;
        private final String uri;
        private final Context previous;
        
        Context(final String _prefix, final String _uri, final Context _context) {
            this.previous = _context;
            this.prefix = _prefix;
            this.uri = _uri;
        }
        
        public String resolveNamespacePrefix(final String p) {
            if (p.equals(this.prefix)) {
                return this.uri;
            }
            if (this.previous == null) {
                return null;
            }
            return this.previous.resolveNamespacePrefix(p);
        }
        
        public String getBaseUri() {
            return null;
        }
        
        public boolean isNotation(final String arg0) {
            return false;
        }
        
        public boolean isUnparsedEntity(final String arg0) {
            return false;
        }
    }
}
