// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.xml.xsom.parser;

import com.sun.xml.xsom.XSSchemaSet;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import java.net.URL;
import java.io.IOException;
import java.io.File;
import org.xml.sax.SAXException;
import java.io.InputStream;
import javax.xml.parsers.SAXParserFactory;
import com.sun.xml.xsom.impl.parser.ParserContext;
import org.xml.sax.ErrorHandler;
import org.xml.sax.EntityResolver;

public final class XSOMParser
{
    private EntityResolver entityResolver;
    private ErrorHandler userErrorHandler;
    private AnnotationParserFactory apFactory;
    private final ParserContext context;
    
    public XSOMParser() {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: drem           
        //     1: ddiv           
        //     2: ldiv           
        //     3: laload         
        //     4: lmul           
        //     5: ldiv           
        //     6: irem           
        //     7: idiv           
        //     8: laload         
        //     9: irem           
        //    10: ladd           
        //    11: frem           
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  ------------------------------------
        //  0      12      0     this  Lcom/sun/xml/xsom/parser/XSOMParser;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public XSOMParser(final SAXParserFactory factory) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: laload         
        //     1: irem           
        //     2: ladd           
        //     3: frem           
        //     4: drem           
        //     5: lsub           
        //     6: frem           
        //     7: laload         
        //     8: drem           
        //     9: ineg           
        //    10: ladd           
        //    11: ineg           
        //    12: lsub           
        //    LocalVariableTable:
        //  Start  Length  Slot  Name     Signature
        //  -----  ------  ----  -------  ------------------------------------
        //  0      13      0     this     Lcom/sun/xml/xsom/parser/XSOMParser;
        //  0      13      1     factory  Ljavax/xml/parsers/SAXParserFactory;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public XSOMParser(final XMLParser parser) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: dmul           
        //     1: lsub           
        //     2: lshl           
        //     3: aastore        
        //     4: lsub           
        //     5: ineg           
        //     6: aconst_null    
        //     7: nop            
        //     8: aload           40
        //    10: astore_1        /* parser */
        //    11: fmul           
        //    12: ladd           
        //    13: fneg           
        //    14: ladd           
        //    15: laload         
        //    16: lneg           
        //    17: ineg           
        //    LocalVariableTable:
        //  Start  Length  Slot  Name    Signature
        //  -----  ------  ----  ------  ------------------------------------
        //  0      18      0     this    Lcom/sun/xml/xsom/parser/XSOMParser;
        //  0      18      1     parser  Lcom/sun/xml/xsom/parser/XMLParser;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public void parse(final InputStream is) throws SAXException {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: sastore        
        //     1: nop            
        //     2: daload         
        //     3: nop            
        //     4: fload_0         /* this */
        //     5: nop            
        //     6: fload_1         /* is */
        //     7: nop            
        //     8: nop            
        //     9: nop            
        //    10: iconst_1       
        //    11: nop            
        //    12: iconst_m1      
        //    Exceptions:
        //  throws org.xml.sax.SAXException
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  ------------------------------------
        //  0      13      0     this  Lcom/sun/xml/xsom/parser/XSOMParser;
        //  0      13      1     is    Ljava/io/InputStream;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public void parse(final File schema) throws SAXException, IOException {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: nop            
        //     1: lstore_0        /* this */
        //     2: nop            
        //     3: fconst_0       
        //     4: nop            
        //     5: lstore_1        /* schema */
        //     6: nop            
        //     7: faload         
        //     8: nop            
        //    Exceptions:
        //  throws org.xml.sax.SAXException
        //  throws java.io.IOException
        //    LocalVariableTable:
        //  Start  Length  Slot  Name    Signature
        //  -----  ------  ----  ------  ------------------------------------
        //  0      9       0     this    Lcom/sun/xml/xsom/parser/XSOMParser;
        //  0      9       1     schema  Ljava/io/File;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public void parse(final URL url) throws SAXException {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: nop            
        //     1: daload         
        //     2: nop            
        //     3: aaload         
        //     4: nop            
        //     5: nop            
        //     6: nop            
        //     7: nop            
        //     8: nop            
        //    Exceptions:
        //  throws org.xml.sax.SAXException
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  ------------------------------------
        //  0      9       0     this  Lcom/sun/xml/xsom/parser/XSOMParser;
        //  0      9       1     url   Ljava/net/URL;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public void parse(final String systemId) throws SAXException {
        // 
        // This method could not be decompiled.
        // 
        // Could not show original bytecode, likely due to the same error.
        // 
        // The error that occurred was:
        // 
        // com.strobel.assembler.metadata.MethodBodyParseException: An error occurred while parsing the bytecode of method 'com/sun/xml/xsom/parser/XSOMParser.parse:(Ljava/lang/String;)V'.
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
    
    public void parse(final InputSource source) throws SAXException {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: nop            
        //     1: nop            
        //     2: fconst_2       
        //     3: nop            
        //     4: istore_0        /* this */
        //     5: nop            
        //     6: istore_1        /* source */
        //     7: nop            
        //     8: aconst_null    
        //    Exceptions:
        //  throws org.xml.sax.SAXException
        //    LocalVariableTable:
        //  Start  Length  Slot  Name    Signature
        //  -----  ------  ----  ------  ------------------------------------
        //  0      9       0     this    Lcom/sun/xml/xsom/parser/XSOMParser;
        //  0      9       1     source  Lorg/xml/sax/InputSource;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public ContentHandler getParserHandler() {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: aaload         
        //     1: nop            
        //     2: nop            
        //     3: nop            
        //     4: nop            
        //     5: nop            
        //     6: fconst_2       
        //     7: nop            
        //     8: lstore_1        /* runtime */
        //     9: nop            
        //    10: lstore_2       
        //    11: nop            
        //    12: aconst_null    
        //    13: nop            
        //    14: istore_2       
        //    15: nop            
        //    16: nop            
        //    17: nop            
        //    18: iconst_1       
        //    19: nop            
        //    20: aconst_null    
        //    21: nop            
        //    22: istore_3       
        //    23: nop            
        //    24: aconst_null    
        //    25: nop            
        //    LocalVariableTable:
        //  Start  Length  Slot  Name     Signature
        //  -----  ------  ----  -------  --------------------------------------------
        //  0      26      0     this     Lcom/sun/xml/xsom/parser/XSOMParser;
        //  8      18      1     runtime  Lcom/sun/xml/xsom/impl/parser/NGCCRuntimeEx;
        //  19     7       2     s        Lcom/sun/xml/xsom/impl/parser/state/Schema;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public XSSchemaSet getResult() throws SAXException {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: nop            
        //     1: aconst_null    
        //     2: nop            
        //     3: dstore          this
        //     5: fstore_3       
        //     6: nop            
        //     7: iconst_m1      
        //    Exceptions:
        //  throws org.xml.sax.SAXException
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  ------------------------------------
        //  0      8       0     this  Lcom/sun/xml/xsom/parser/XSOMParser;
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
        //     0: dstore_0        /* this */
        //     1: nop            
        //     2: dstore_1       
        //     3: nop            
        //     4: aconst_null    
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  ------------------------------------
        //  0      5       0     this  Lcom/sun/xml/xsom/parser/XSOMParser;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public void setEntityResolver(final EntityResolver resolver) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: iconst_m1      
        //     1: nop            
        //     2: nop            
        //     3: nop            
        //     4: l2i            
        //     5: nop            
        //    LocalVariableTable:
        //  Start  Length  Slot  Name      Signature
        //  -----  ------  ----  --------  ------------------------------------
        //  0      6       0     this      Lcom/sun/xml/xsom/parser/XSOMParser;
        //  0      6       1     resolver  Lorg/xml/sax/EntityResolver;
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
        // com.strobel.assembler.metadata.MethodBodyParseException: An error occurred while parsing the bytecode of method 'com/sun/xml/xsom/parser/XSOMParser.getErrorHandler:()Lorg/xml/sax/ErrorHandler;'.
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
        // Caused by: java.lang.IllegalArgumentException: Argument 'index' must be in the range [0, 147], but value was: 177.
        //     at com.strobel.core.VerifyArgument.inRange(VerifyArgument.java:346)
        //     at com.strobel.assembler.ir.ConstantPool.get(ConstantPool.java:78)
        //     at com.strobel.assembler.metadata.ClassFileReader$Scope.lookupConstant(ClassFileReader.java:1313)
        //     at com.strobel.assembler.metadata.MethodReader.readBodyCore(MethodReader.java:286)
        //     at com.strobel.assembler.metadata.MethodReader.readBody(MethodReader.java:62)
        //     ... 16 more
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public void setErrorHandler(final ErrorHandler errorHandler) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: aconst_null    
        //     1: nop            
        //     2: iastore        
        //     3: nop            
        //     4: lastore        
        //     5: nop            
        //    LocalVariableTable:
        //  Start  Length  Slot  Name          Signature
        //  -----  ------  ----  ------------  ------------------------------------
        //  0      6       0     this          Lcom/sun/xml/xsom/parser/XSOMParser;
        //  0      6       1     errorHandler  Lorg/xml/sax/ErrorHandler;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public void setAnnotationParser(final Class annParser) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: faload         
        //     1: nop            
        //     2: nop            
        //     3: nop            
        //     4: lload_2        
        //     5: nop            
        //     6: iconst_0       
        //     7: nop            
        //     8: nop            
        //     9: nop            
        //    10: iload_0         /* this */
        //    11: nop            
        //    12: daload         
        //    13: nop            
        //    LocalVariableTable:
        //  Start  Length  Slot  Name       Signature
        //  -----  ------  ----  ---------  ------------------------------------
        //  0      14      0     this       Lcom/sun/xml/xsom/parser/XSOMParser;
        //  0      14      1     annParser  Ljava/lang/Class;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public void setAnnotationParser(final AnnotationParserFactory factory) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: faload         
        //     1: nop            
        //     2: nop            
        //     3: nop            
        //     4: fconst_1       
        //     5: nop            
        //    LocalVariableTable:
        //  Start  Length  Slot  Name     Signature
        //  -----  ------  ----  -------  -------------------------------------------------
        //  0      6       0     this     Lcom/sun/xml/xsom/parser/XSOMParser;
        //  0      6       1     factory  Lcom/sun/xml/xsom/parser/AnnotationParserFactory;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public AnnotationParserFactory getAnnotationParserFactory() {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: nop            
        //     1: nop            
        //     2: iconst_3       
        //     3: nop            
        //     4: aconst_null    
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  ------------------------------------
        //  0      5       0     this  Lcom/sun/xml/xsom/parser/XSOMParser;
        // 
        // The error that occurred was:
        // 
        // java.lang.NullPointerException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
}
