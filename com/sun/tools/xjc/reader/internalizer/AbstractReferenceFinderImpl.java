// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.reader.internalizer;

import org.xml.sax.SAXException;
import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.helpers.XMLFilterImpl;

public abstract class AbstractReferenceFinderImpl extends XMLFilterImpl
{
    protected final DOMForest parent;
    private Locator locator;
    
    protected AbstractReferenceFinderImpl(final DOMForest _parent) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: lsub           
        //     1: ladd           
        //     2: isub           
        //     3: lsub           
        //     4: frem           
        //     5: laload         
        //     6: lmul           
        //     7: fdiv           
        //     8: ineg           
        //     9: lsub           
        //    LocalVariableTable:
        //  Start  Length  Slot  Name     Signature
        //  -----  ------  ----  -------  -------------------------------------------------------------------
        //  0      10      0     this     Lcom/sun/tools/xjc/reader/internalizer/AbstractReferenceFinderImpl;
        //  0      10      1     _parent  Lcom/sun/tools/xjc/reader/internalizer/DOMForest;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    protected abstract String findExternalResource(final String p0, final String p1, final Attributes p2);
    
    public void startElement(final String namespaceURI, final String localName, final String qName, final Attributes atts) throws SAXException {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: lmul           
        //     1: fdiv           
        //     2: dsub           
        //     3: istore_0        /* this */
        //     4: aconst_null    
        //     5: nop            
        //     6: iconst_3       
        //     7: ladd           
        //     8: irem           
        //     9: irem           
        //    10: lsub           
        //    11: fdiv           
        //    12: isub           
        //    13: aconst_null    
        //    14: nop            
        //    15: aload_3         /* qName */
        //    16: dload_2         /* localName */
        //    17: astore_1        /* namespaceURI */
        //    18: fmul           
        //    19: ladd           
        //    20: fneg           
        //    21: ladd           
        //    22: laload         
        //    23: idiv           
        //    24: ladd           
        //    25: fdiv           
        //    26: dsub           
        //    27: laload         
        //    28: aastore        
        //    29: ineg           
        //    30: frem           
        //    31: lmul           
        //    32: fdiv           
        //    33: dsub           
        //    34: istore_0        /* this */
        //    35: dload_3         /* qName */
        //    36: astore_1        /* namespaceURI */
        //    37: fmul           
        //    38: ladd           
        //    39: fneg           
        //    40: ladd           
        //    41: laload         
        //    42: idiv           
        //    43: ladd           
        //    44: fdiv           
        //    45: dsub           
        //    46: laload         
        //    47: aastore        
        //    48: ineg           
        //    49: frem           
        //    50: lmul           
        //    51: fdiv           
        //    52: dsub           
        //    53: lstore_3        /* qName */
        //    54: lneg           
        //    55: lmul           
        //    56: idiv           
        //    57: isub           
        //    58: lsub           
        //    59: frem           
        //    60: istore_0        /* this */
        //    61: aconst_null    
        //    62: nop            
        //    63: iload_2         /* localName */
        //    64: dload_2         /* localName */
        //    65: fstore_0        /* this */
        //    66: dload_3         /* qName */
        //    67: astore_1        /* namespaceURI */
        //    68: fmul           
        //    69: ladd           
        //    70: fneg           
        //    71: ladd           
        //    72: laload         
        //    73: idiv           
        //    74: ladd           
        //    75: fdiv           
        //    76: dsub           
        //    77: laload         
        //    78: aastore        
        //    79: ineg           
        //    80: frem           
        //    81: lmul           
        //    82: fdiv           
        //    83: dsub           
        //    84: lstore_3        /* qName */
        //    85: lneg           
        //    86: lmul           
        //    87: idiv           
        //    88: isub           
        //    89: lsub           
        //    90: frem           
        //    91: istore_0        /* this */
        //    92: aconst_null    
        //    93: nop            
        //    94: lstore_0        /* this */
        //    95: dload_2         /* localName */
        //    96: astore_1        /* namespaceURI */
        //    97: fmul           
        //    98: ladd           
        //    99: fneg           
        //   100: ladd           
        //   101: laload         
        //    Exceptions:
        //  throws org.xml.sax.SAXException
        //    LocalVariableTable:
        //  Start  Length  Slot  Name          Signature
        //  -----  ------  ----  ------------  -------------------------------------------------------------------
        //  55     10      6     ref           Ljava/lang/String;
        //  92     9       7     spe           Lorg/xml/sax/SAXParseException;
        //  70     31      6     e             Ljava/io/IOException;
        //  0      102     0     this          Lcom/sun/tools/xjc/reader/internalizer/AbstractReferenceFinderImpl;
        //  0      102     1     namespaceURI  Ljava/lang/String;
        //  0      102     2     localName     Ljava/lang/String;
        //  0      102     3     qName         Ljava/lang/String;
        //  0      102     4     atts          Lorg/xml/sax/Attributes;
        //  19     83      5     relativeRef   Ljava/lang/String;
        //    Exceptions:
        //  Try           Handler
        //  Start  End    Start  End    Type                 
        //  -----  -----  -----  -----  ---------------------
        //  25     65     68     102    Ljava/io/IOException;
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
        //     0: nop            
        //     1: lload           this
        //     3: iconst_m1      
        //     4: nop            
        //     5: nop            
        //     6: nop            
        //     7: lconst_1       
        //     8: nop            
        //     9: aload_1         /* locator */
        //    10: nop            
        //    LocalVariableTable:
        //  Start  Length  Slot  Name     Signature
        //  -----  ------  ----  -------  -------------------------------------------------------------------
        //  0      11      0     this     Lcom/sun/tools/xjc/reader/internalizer/AbstractReferenceFinderImpl;
        //  0      11      1     locator  Lorg/xml/sax/Locator;
        // 
        // The error that occurred was:
        // 
        // java.lang.NullPointerException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
}
