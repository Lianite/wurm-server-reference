// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.util;

import javax.xml.namespace.QName;
import org.w3c.dom.Document;
import org.w3c.dom.DOMException;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Element;

public class DOMUtils
{
    public static Element getFirstChildElement(final Element parent, final String nsUri, final String localPart) {
        final NodeList children = parent.getChildNodes();
        for (int i = 0; i < children.getLength(); ++i) {
            final Node item = children.item(i);
            if (item instanceof Element) {
                if (nsUri.equals(item.getNamespaceURI()) && localPart.equals(item.getLocalName())) {
                    return (Element)item;
                }
            }
        }
        return null;
    }
    
    public static Element[] getChildElements(final Element parent, final String nsUri, final String localPart) {
        // 
        // This method could not be decompiled.
        // 
        // Could not show original bytecode, likely due to the same error.
        // 
        // The error that occurred was:
        // 
        // com.strobel.assembler.metadata.MethodBodyParseException: An error occurred while parsing the bytecode of method 'com/sun/tools/xjc/util/DOMUtils.getChildElements:(Lorg/w3c/dom/Element;Ljava/lang/String;Ljava/lang/String;)[Lorg/w3c/dom/Element;'.
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
    
    public static Element[] getChildElements(final Element parent) {
        // 
        // This method could not be decompiled.
        // 
        // Could not show original bytecode, likely due to the same error.
        // 
        // The error that occurred was:
        // 
        // com.strobel.assembler.metadata.MethodBodyParseException: An error occurred while parsing the bytecode of method 'com/sun/tools/xjc/util/DOMUtils.getChildElements:(Lorg/w3c/dom/Element;)[Lorg/w3c/dom/Element;'.
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
    
    public static String getElementText(final Element element) throws DOMException {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: nop            
        //     1: iconst_m1      
        //     2: nop            
        //     3: nop            
        //     4: nop            
        //     5: aload_3        
        //     6: aload_0         /* element */
        //     7: invokeinterface org/w3c/dom/Element.getFirstChild:()Lorg/w3c/dom/Node;
        //    12: astore_1        /* child */
        //    13: aload_1         /* child */
        //    14: ifnull          44
        //    17: aload_1         /* child */
        //    18: invokeinterface org/w3c/dom/Node.getNodeType:()S
        //    23: iconst_3       
        //    24: if_icmpne       34
        //    27: aload_1         /* child */
        //    28: invokeinterface org/w3c/dom/Node.getNodeValue:()Ljava/lang/String;
        //    33: areturn        
        //    34: aload_1         /* child */
        //    35: invokeinterface org/w3c/dom/Node.getNextSibling:()Lorg/w3c/dom/Node;
        //    40: astore_1       
        //    41: goto            13
        //    44: aload_0         /* element */
        //    Exceptions:
        //  throws org.w3c.dom.DOMException
        //    LocalVariableTable:
        //  Start  Length  Slot  Name     Signature
        //  -----  ------  ----  -------  ---------------------
        //  7      31      1     child    Lorg/w3c/dom/Node;
        //  0      45      0     element  Lorg/w3c/dom/Element;
        // 
        // The error that occurred was:
        // 
        // java.lang.NullPointerException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public static Element getElement(final Document parent, final String name) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: nop            
        //     1: iconst_0       
        //     2: nop            
        //     3: nop            
        //     4: nop            
        //     5: lload_1         /* name */
        //     6: aload_0         /* parent */
        //     7: aload_1         /* name */
        //     8: invokeinterface org/w3c/dom/Document.getElementsByTagName:(Ljava/lang/String;)Lorg/w3c/dom/NodeList;
        //    13: astore_2        /* children */
        //    14: aload_2         /* children */
        //    15: invokeinterface org/w3c/dom/NodeList.getLength:()I
        //    20: iconst_1       
        //    21: if_icmplt       35
        //    24: aload_2         /* children */
        //    25: iconst_0       
        //    26: invokeinterface org/w3c/dom/NodeList.item:(I)Lorg/w3c/dom/Node;
        //    LocalVariableTable:
        //  Start  Length  Slot  Name      Signature
        //  -----  ------  ----  --------  ----------------------
        //  0      31      0     parent    Lorg/w3c/dom/Document;
        //  0      31      1     name      Ljava/lang/String;
        //  8      23      2     children  Lorg/w3c/dom/NodeList;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public static Element getElement(final Document parent, final QName qname) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: nop            
        //     1: iconst_0       
        //     2: nop            
        //     3: nop            
        //     4: nop            
        //     5: dload_0         /* parent */
        //     6: aload_0         /* parent */
        //     7: aload_1         /* qname */
        //     8: invokevirtual   javax/xml/namespace/QName.getNamespaceURI:()Ljava/lang/String;
        //    11: aload_1         /* qname */
        //    12: invokevirtual   javax/xml/namespace/QName.getLocalPart:()Ljava/lang/String;
        //    15: invokeinterface org/w3c/dom/Document.getElementsByTagNameNS:(Ljava/lang/String;Ljava/lang/String;)Lorg/w3c/dom/NodeList;
        //    20: astore_2        /* children */
        //    21: aload_2         /* children */
        //    22: invokeinterface org/w3c/dom/NodeList.getLength:()I
        //    27: iconst_1       
        //    28: if_icmplt       42
        //    31: aload_2         /* children */
        //    32: iconst_0       
        //    33: invokeinterface org/w3c/dom/NodeList.item:(I)Lorg/w3c/dom/Node;
        //    LocalVariableTable:
        //  Start  Length  Slot  Name      Signature
        //  -----  ------  ----  --------  ---------------------------
        //  0      38      0     parent    Lorg/w3c/dom/Document;
        //  0      38      1     qname     Ljavax/xml/namespace/QName;
        //  15     23      2     children  Lorg/w3c/dom/NodeList;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public static Element getElement(final Document parent, final String namespaceURI, final String localName) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: nop            
        //     1: iconst_1       
        //     2: nop            
        //     3: nop            
        //     4: nop            
        //     5: lload_2         /* localName */
        //     6: aload_0         /* parent */
        //     7: aload_1         /* namespaceURI */
        //     8: aload_2         /* localName */
        //     9: invokeinterface org/w3c/dom/Document.getElementsByTagNameNS:(Ljava/lang/String;Ljava/lang/String;)Lorg/w3c/dom/NodeList;
        //    14: astore_3        /* children */
        //    15: aload_3         /* children */
        //    16: invokeinterface org/w3c/dom/NodeList.getLength:()I
        //    21: iconst_1       
        //    22: if_icmplt       36
        //    25: aload_3         /* children */
        //    26: iconst_0       
        //    27: invokeinterface org/w3c/dom/NodeList.item:(I)Lorg/w3c/dom/Node;
        //    LocalVariableTable:
        //  Start  Length  Slot  Name          Signature
        //  -----  ------  ----  ------------  ----------------------
        //  0      32      0     parent        Lorg/w3c/dom/Document;
        //  0      32      1     namespaceURI  Ljava/lang/String;
        //  0      32      2     localName     Ljava/lang/String;
        //  9      23      3     children      Lorg/w3c/dom/NodeList;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public static Element[] getElements(final NodeList children) {
        // 
        // This method could not be decompiled.
        // 
        // Could not show original bytecode, likely due to the same error.
        // 
        // The error that occurred was:
        // 
        // com.strobel.assembler.metadata.MethodBodyParseException: An error occurred while parsing the bytecode of method 'com/sun/tools/xjc/util/DOMUtils.getElements:(Lorg/w3c/dom/NodeList;)[Lorg/w3c/dom/Element;'.
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
        //     at com.strobel.assembler.metadata.Buffer.readByte(Buffer.java:209)
        //     at com.strobel.assembler.metadata.MethodReader.readBodyCore(MethodReader.java:440)
        //     at com.strobel.assembler.metadata.MethodReader.readBody(MethodReader.java:62)
        //     ... 16 more
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
}
