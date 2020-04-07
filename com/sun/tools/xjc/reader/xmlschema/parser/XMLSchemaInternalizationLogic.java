// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.reader.xmlschema.parser;

import org.xml.sax.Attributes;
import com.sun.tools.xjc.reader.internalizer.AbstractReferenceFinderImpl;
import org.xml.sax.SAXException;
import org.w3c.dom.Element;
import org.xml.sax.XMLFilter;
import com.sun.tools.xjc.reader.internalizer.DOMForest;
import com.sun.tools.xjc.reader.internalizer.InternalizationLogic;

public class XMLSchemaInternalizationLogic implements InternalizationLogic
{
    public XMLSchemaInternalizationLogic() {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: nop            
        //     1: fload_1        
        //     2: nop            
        //     3: fload_2        
        //     4: nop            
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  -------------------------------------------------------------------------
        //  0      5       0     this  Lcom/sun/tools/xjc/reader/xmlschema/parser/XMLSchemaInternalizationLogic;
        // 
        // The error that occurred was:
        // 
        // java.lang.NullPointerException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public XMLFilter createExternalReferenceFinder(final DOMForest parent) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: lastore        
        //     1: nop            
        //     2: fload_0         /* this */
        //     3: nop            
        //     4: nop            
        //     5: nop            
        //     6: aload_0         /* this */
        //     7: nop            
        //     8: iconst_1       
        //     9: nop            
        //    LocalVariableTable:
        //  Start  Length  Slot  Name    Signature
        //  -----  ------  ----  ------  -------------------------------------------------------------------------
        //  0      10      0     this    Lcom/sun/tools/xjc/reader/xmlschema/parser/XMLSchemaInternalizationLogic;
        //  0      10      1     parent  Lcom/sun/tools/xjc/reader/internalizer/DOMForest;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public boolean checkIfValidTargetNode(final DOMForest parent, final Element bindings, final Element target) throws SAXException {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: invokestatic    com/sun/tools/xjc/util/DOMUtils.getFirstChildElement:(Lorg/w3c/dom/Element;Ljava/lang/String;Ljava/lang/String;)Lorg/w3c/dom/Element;
        //     3: astore_2        /* bindings */
        //     4: aload_2         /* bindings */
        //     5: ifnonnull       16
        //     8: aload_0         /* this */
        //     9: aload_1         /* parent */
        //    10: ldc             "annotation"
        //    Exceptions:
        //  throws org.xml.sax.SAXException
        //    LocalVariableTable:
        //  Start  Length  Slot  Name      Signature
        //  -----  ------  ----  --------  -------------------------------------------------------------------------
        //  0      12      0     this      Lcom/sun/tools/xjc/reader/xmlschema/parser/XMLSchemaInternalizationLogic;
        //  0      12      1     parent    Lcom/sun/tools/xjc/reader/internalizer/DOMForest;
        //  0      12      2     bindings  Lorg/w3c/dom/Element;
        //  0      12      3     target    Lorg/w3c/dom/Element;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public Element refineTarget(final Element target) {
        // 
        // This method could not be decompiled.
        // 
        // Could not show original bytecode, likely due to the same error.
        // 
        // The error that occurred was:
        // 
        // com.strobel.assembler.metadata.MethodBodyParseException: An error occurred while parsing the bytecode of method 'com/sun/tools/xjc/reader/xmlschema/parser/XMLSchemaInternalizationLogic.refineTarget:(Lorg/w3c/dom/Element;)Lorg/w3c/dom/Element;'.
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
        //     at com.strobel.assembler.metadata.MethodReader.readBodyCore(MethodReader.java:203)
        //     at com.strobel.assembler.metadata.MethodReader.readBody(MethodReader.java:62)
        //     ... 16 more
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    private Element insertXMLSchemaElement(final Element parent, final String localName) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: iconst_0       
        //     1: nop            
        //     2: pop            
        //     3: aload           5
        //     5: areturn        
        //     6: nop            
        //     7: nop            
        //     8: nop            
        //     9: iconst_m1      
        //    10: nop            
        //    11: lload_3         /* qname */
        //    12: nop            
        //    13: nop            
        //    14: nop            
        //    15: aload_0         /* this */
        //    16: nop            
        //    17: lconst_1       
        //    18: nop            
        //    19: nop            
        //    20: nop            
        //    21: ldiv           
        //    22: nop            
        //    23: iconst_4       
        //    24: nop            
        //    25: fdiv           
        //    26: nop            
        //    27: dconst_1       
        //    28: nop            
        //    29: ddiv           
        //    30: nop            
        //    31: iload_0         /* this */
        //    32: nop            
        //    33: irem           
        //    34: nop            
        //    35: saload         
        //    36: nop            
        //    37: frem           
        //    38: nop            
        //    39: fstore_2        /* localName */
        //    40: nop            
        //    41: ineg           
        //    42: nop            
        //    43: astore_2        /* localName */
        //    44: nop            
        //    45: fneg           
        //    46: nop            
        //    47: pop            
        //    48: nop            
        //    49: dneg           
        //    50: nop            
        //    51: dadd           
        //    52: nop            
        //    53: lshl           
        //    54: nop            
        //    55: ineg           
        //    56: nop            
        //    57: lshr           
        //    58: nop            
        //    59: fload_0         /* this */
        //    60: nop            
        //    61: nop            
        //    62: nop            
        //    63: dstore_1        /* parent */
        //    64: nop            
        //    65: iconst_4       
        //    66: nop            
        //    67: nop            
        //    68: nop            
        //    69: dneg           
        //    70: nop            
        //    71: fload_1         /* parent */
        //    72: nop            
        //    73: fload_2         /* localName */
        //    74: nop            
        //    75: nop            
        //    76: nop            
        //    77: nop            
        //    78: nop            
        //    79: dneg           
        //    80: nop            
        //    81: dload_1         /* parent */
        //    82: nop            
        //    83: aload_2         /* localName */
        //    84: nop            
        //    85: aconst_null    
        //    86: nop            
        //    87: nop            
        //    88: nop            
        //    89: dneg           
        //    90: nop            
        //    91: caload         
        //    92: nop            
        //    93: saload         
        //    94: nop            
        //    95: iconst_m1      
        //    96: nop            
        //    97: iconst_4       
        //    98: nop            
        //    99: irem           
        //   100: nop            
        //   101: istore          this
        //   103: saload         
        //   104: nop            
        //   105: iconst_0       
        //   106: nop            
        //   107: dconst_1       
        //   108: nop            
        //   109: imul           
        //   110: nop            
        //   111: lstore          this
        //   113: fstore          this
        //   115: iconst_1       
        //   116: nop            
        //   117: fstore_2        /* localName */
        //   118: nop            
        //    LocalVariableTable:
        //  Start  Length  Slot  Name       Signature
        //  -----  ------  ----  ---------  -------------------------------------------------------------------------
        //  0      119     0     this       Lcom/sun/tools/xjc/reader/xmlschema/parser/XMLSchemaInternalizationLogic;
        //  0      119     1     parent     Lorg/w3c/dom/Element;
        //  0      119     2     localName  Ljava/lang/String;
        //  7      112     3     qname      Ljava/lang/String;
        //  15     104     4     idx        I
        //  69     50      5     child      Lorg/w3c/dom/Element;
        //  77     42      6     children   Lorg/w3c/dom/NodeList;
        // 
        // The error that occurred was:
        // 
        // java.lang.IndexOutOfBoundsException: Index: 0, Size: 0
        //     at java.util.ArrayList.rangeCheck(Unknown Source)
        //     at java.util.ArrayList.get(Unknown Source)
        //     at com.strobel.decompiler.ast.AstBuilder.convertLocalVariables(AstBuilder.java:3035)
        //     at com.strobel.decompiler.ast.AstBuilder.performStackAnalysis(AstBuilder.java:2445)
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
    
    private static final class ReferenceFinder extends AbstractReferenceFinderImpl
    {
        ReferenceFinder(final DOMForest parent) {
            super(parent);
        }
        
        protected String findExternalResource(final String nsURI, final String localName, final Attributes atts) {
            if ("http://www.w3.org/2001/XMLSchema".equals(nsURI) && ("import".equals(localName) || "include".equals(localName))) {
                return atts.getValue("schemaLocation");
            }
            return null;
        }
    }
}
