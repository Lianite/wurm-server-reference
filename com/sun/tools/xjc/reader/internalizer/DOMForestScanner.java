// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.reader.internalizer;

import org.w3c.dom.Node;
import org.xml.sax.Attributes;
import com.sun.xml.bind.unmarshaller.DOMScanner;
import org.xml.sax.Locator;
import org.xml.sax.helpers.XMLFilterImpl;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;
import org.xml.sax.ContentHandler;
import org.w3c.dom.Element;

public class DOMForestScanner
{
    private final DOMForest forest;
    
    public DOMForestScanner(final DOMForest _forest) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: lconst_1       
        //     1: aload_0         /* this */
        //     2: invokespecial   java/lang/Object.<init>:()V
        //     5: aload_0         /* this */
        //     6: aload_1         /* _forest */
        //     7: putfield        com/sun/tools/xjc/reader/internalizer/DOMForestScanner.forest:Lcom/sun/tools/xjc/reader/internalizer/DOMForest;
        //    LocalVariableTable:
        //  Start  Length  Slot  Name     Signature
        //  -----  ------  ----  -------  --------------------------------------------------------
        //  0      10      0     this     Lcom/sun/tools/xjc/reader/internalizer/DOMForestScanner;
        //  0      10      1     _forest  Lcom/sun/tools/xjc/reader/internalizer/DOMForest;
        // 
        // The error that occurred was:
        // 
        // java.lang.NullPointerException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public void scan(final Element e, final ContentHandler contentHandler) throws SAXException {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: fload_3        
        //     1: new             Lcom/sun/xml/bind/unmarshaller/DOMScanner;
        //     4: dup            
        //     5: invokespecial   com/sun/xml/bind/unmarshaller/DOMScanner.<init>:()V
        //     8: astore_3        /* scanner */
        //     9: new             Lcom/sun/tools/xjc/reader/internalizer/DOMForestScanner$LocationResolver;
        //    12: dup            
        //    13: aload_0         /* this */
        //    14: aload_3         /* scanner */
        //    15: invokespecial   com/sun/tools/xjc/reader/internalizer/DOMForestScanner$LocationResolver.<init>:(Lcom/sun/tools/xjc/reader/internalizer/DOMForestScanner;Lcom/sun/xml/bind/unmarshaller/DOMScanner;)V
        //    18: astore          resolver
        //    20: aload           resolver
        //    22: aload_2         /* contentHandler */
        //    23: invokevirtual   com/sun/tools/xjc/reader/internalizer/DOMForestScanner$LocationResolver.setContentHandler:(Lorg/xml/sax/ContentHandler;)V
        //    26: aload_3         /* scanner */
        //    27: aload           resolver
        //    29: invokevirtual   com/sun/xml/bind/unmarshaller/DOMScanner.parseWithContext:(Lorg/w3c/dom/Element;Lorg/xml/sax/ContentHandler;)V
        //    32: aload_3         /* scanner */
        //    Exceptions:
        //  throws org.xml.sax.SAXException
        //    LocalVariableTable:
        //  Start  Length  Slot  Name            Signature
        //  -----  ------  ----  --------------  -------------------------------------------------------------------------
        //  0      33      0     this            Lcom/sun/tools/xjc/reader/internalizer/DOMForestScanner;
        //  0      33      1     e               Lorg/w3c/dom/Element;
        //  0      33      2     contentHandler  Lorg/xml/sax/ContentHandler;
        //  8      25      3     scanner         Lcom/sun/xml/bind/unmarshaller/DOMScanner;
        //  19     14      4     resolver        Lcom/sun/tools/xjc/reader/internalizer/DOMForestScanner$LocationResolver;
        // 
        // The error that occurred was:
        // 
        // java.lang.NullPointerException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public void scan(final Document d, final ContentHandler contentHandler) throws SAXException {
        // 
        // This method could not be decompiled.
        // 
        // Could not show original bytecode, likely due to the same error.
        // 
        // The error that occurred was:
        // 
        // com.strobel.assembler.metadata.MethodBodyParseException: An error occurred while parsing the bytecode of method 'com/sun/tools/xjc/reader/internalizer/DOMForestScanner.scan:(Lorg/w3c/dom/Document;Lorg/xml/sax/ContentHandler;)V'.
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
    
    static /* synthetic */ DOMForest access$000(final DOMForestScanner x0) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: laload         
        //     1: nop            
        //     2: aconst_null    
        //     3: nop            
        //     4: aconst_null    
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  --------------------------------------------------------
        //  0      5       0     x0    Lcom/sun/tools/xjc/reader/internalizer/DOMForestScanner;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    private class LocationResolver extends XMLFilterImpl implements Locator
    {
        private final DOMScanner parent;
        private boolean inStart;
        
        LocationResolver(final DOMScanner _parent) {
            this.inStart = false;
            this.parent = _parent;
        }
        
        public void setDocumentLocator(final Locator locator) {
            super.setDocumentLocator(this);
        }
        
        public void endElement(final String namespaceURI, final String localName, final String qName) throws SAXException {
            this.inStart = false;
            super.endElement(namespaceURI, localName, qName);
        }
        
        public void startElement(final String namespaceURI, final String localName, final String qName, final Attributes atts) throws SAXException {
            this.inStart = true;
            super.startElement(namespaceURI, localName, qName, atts);
        }
        
        private Locator findLocator() {
            final Node n = this.parent.getCurrentLocation();
            if (!(n instanceof Element)) {
                return null;
            }
            final Element e = (Element)n;
            if (this.inStart) {
                return DOMForestScanner.access$000(DOMForestScanner.this).locatorTable.getStartLocation(e);
            }
            return DOMForestScanner.access$000(DOMForestScanner.this).locatorTable.getEndLocation(e);
        }
        
        public int getColumnNumber() {
            final Locator l = this.findLocator();
            if (l != null) {
                return l.getColumnNumber();
            }
            return -1;
        }
        
        public int getLineNumber() {
            final Locator l = this.findLocator();
            if (l != null) {
                return l.getLineNumber();
            }
            return -1;
        }
        
        public String getPublicId() {
            final Locator l = this.findLocator();
            if (l != null) {
                return l.getPublicId();
            }
            return null;
        }
        
        public String getSystemId() {
            final Locator l = this.findLocator();
            if (l != null) {
                return l.getSystemId();
            }
            return null;
        }
    }
}
