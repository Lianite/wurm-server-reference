// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.xml.xsom.impl.parser.state;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.Locator;
import com.sun.xml.xsom.parser.AnnotationParser;
import com.sun.xml.xsom.impl.parser.NGCCRuntimeEx;
import com.sun.xml.xsom.impl.AnnotationImpl;
import com.sun.xml.xsom.parser.AnnotationContext;

class annotation extends NGCCHandler
{
    private AnnotationContext context;
    private AnnotationImpl existing;
    protected final NGCCRuntimeEx $runtime;
    private int $_ngcc_current_state;
    protected String $uri;
    protected String $localName;
    protected String $qname;
    private AnnotationParser parser;
    private Locator locator;
    
    public final NGCCRuntime getRuntime() {
        return this.$runtime;
    }
    
    public annotation(final NGCCHandler parent, final NGCCEventSource source, final NGCCRuntimeEx runtime, final int cookie, final AnnotationImpl _existing, final AnnotationContext _context) {
        super(source, parent, cookie);
        this.$runtime = runtime;
        this.existing = _existing;
        this.context = _context;
        this.$_ngcc_current_state = 2;
    }
    
    public annotation(final NGCCRuntimeEx runtime, final AnnotationImpl _existing, final AnnotationContext _context) {
        this(null, runtime, runtime, -1, _existing, _context);
    }
    
    private void action0() throws SAXException {
        this.locator = this.$runtime.copyLocator();
        this.parser = this.$runtime.createAnnotationParser();
        this.$runtime.redirectSubtree(this.parser.getContentHandler(this.context, this.$runtime.getAnnotationContextElementName(), this.$runtime.getErrorHandler(), this.$runtime.parser.getEntityResolver()), this.$uri, this.$localName, this.$qname);
    }
    
    public void enterElement(final String $__uri, final String $__local, final String $__qname, final Attributes $attrs) throws SAXException {
        this.$uri = $__uri;
        this.$localName = $__local;
        this.$qname = $__qname;
        switch (this.$_ngcc_current_state) {
            case 0: {
                this.revertToParentFromEnterElement(this.makeResult(), super._cookie, $__uri, $__local, $__qname, $attrs);
                break;
            }
            case 2: {
                if ($__uri.equals("http://www.w3.org/2001/XMLSchema") && $__local.equals("annotation")) {
                    this.$runtime.onEnterElementConsumed($__uri, $__local, $__qname, $attrs);
                    this.action0();
                    this.$_ngcc_current_state = 1;
                    break;
                }
                this.unexpectedEnterElement($__qname);
                break;
            }
            default: {
                this.unexpectedEnterElement($__qname);
                break;
            }
        }
    }
    
    public void leaveElement(final String $__uri, final String $__local, final String $__qname) throws SAXException {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: nop            
        //     1: nop            
        //     2: nop            
        //     3: irem           
        //     4: aload_0         /* this */
        //     5: aload_1         /* $__uri */
        //     6: putfield        com/sun/xml/xsom/impl/parser/state/annotation.$uri:Ljava/lang/String;
        //     9: aload_0         /* this */
        //    10: aload_2         /* $__local */
        //    11: putfield        com/sun/xml/xsom/impl/parser/state/annotation.$localName:Ljava/lang/String;
        //    14: aload_0         /* this */
        //    15: aload_3         /* $__qname */
        //    16: putfield        com/sun/xml/xsom/impl/parser/state/annotation.$qname:Ljava/lang/String;
        //    19: aload_0         /* this */
        //    20: getfield        com/sun/xml/xsom/impl/parser/state/annotation.$_ngcc_current_state:I
        //    23: lookupswitch {
        //                0: 48
        //                1: 66
        //          default: 110
        //        }
        //    48: aload_0         /* this */
        //    49: aload_0         /* this */
        //    50: invokevirtual   com/sun/xml/xsom/impl/parser/state/annotation.makeResult:()Lcom/sun/xml/xsom/impl/AnnotationImpl;
        //    53: aload_0         /* this */
        //    54: getfield        com/sun/xml/xsom/impl/parser/state/NGCCHandler._cookie:I
        //    57: aload_1         /* $__uri */
        //    58: aload_2         /* $__local */
        //    59: aload_3         /* $__qname */
        //    60: invokevirtual   com/sun/xml/xsom/impl/parser/state/annotation.revertToParentFromLeaveElement:(Ljava/lang/Object;ILjava/lang/String;Ljava/lang/String;Ljava/lang/String;)V
        //    63: goto            115
        //    66: aload_1         /* $__uri */
        //    67: ldc             "http://www.w3.org/2001/XMLSchema"
        //    69: invokevirtual   java/lang/String.equals:(Ljava/lang/Object;)Z
        //    72: ifeq            102
        //    75: aload_2         /* $__local */
        //    76: ldc             "annotation"
        //    78: invokevirtual   java/lang/String.equals:(Ljava/lang/Object;)Z
        //    81: ifeq            102
        //    84: aload_0         /* this */
        //    85: getfield        com/sun/xml/xsom/impl/parser/state/annotation.$runtime:Lcom/sun/xml/xsom/impl/parser/NGCCRuntimeEx;
        //    88: aload_1         /* $__uri */
        //    89: aload_2         /* $__local */
        //    90: aload_3         /* $__qname */
        //    91: invokevirtual   com/sun/xml/xsom/impl/parser/NGCCRuntimeEx.onLeaveElementConsumed:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V
        //    94: aload_0         /* this */
        //    95: iconst_0       
        //    96: putfield        com/sun/xml/xsom/impl/parser/state/annotation.$_ngcc_current_state:I
        //    99: goto            115
        //   102: aload_0         /* this */
        //   103: aload_3         /* $__qname */
        //   104: invokevirtual   com/sun/xml/xsom/impl/parser/state/annotation.unexpectedLeaveElement:(Ljava/lang/String;)V
        //   107: goto            115
        //   110: aload_0         /* this */
        //   111: aload_3         /* $__qname */
        //    Exceptions:
        //  throws org.xml.sax.SAXException
        //    LocalVariableTable:
        //  Start  Length  Slot  Name      Signature
        //  -----  ------  ----  --------  -----------------------------------------------
        //  0      112     0     this      Lcom/sun/xml/xsom/impl/parser/state/annotation;
        //  0      112     1     $__uri    Ljava/lang/String;
        //  0      112     2     $__local  Ljava/lang/String;
        //  0      112     3     $__qname  Ljava/lang/String;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public void enterAttribute(final String $__uri, final String $__local, final String $__qname) throws SAXException {
        // 
        // This method could not be decompiled.
        // 
        // Could not show original bytecode, likely due to the same error.
        // 
        // The error that occurred was:
        // 
        // com.strobel.assembler.metadata.MethodBodyParseException: An error occurred while parsing the bytecode of method 'com/sun/xml/xsom/impl/parser/state/annotation.enterAttribute:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V'.
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
    
    public void leaveAttribute(final String $__uri, final String $__local, final String $__qname) throws SAXException {
        // 
        // This method could not be decompiled.
        // 
        // Could not show original bytecode, likely due to the same error.
        // 
        // The error that occurred was:
        // 
        // com.strobel.assembler.metadata.MethodBodyParseException: An error occurred while parsing the bytecode of method 'com/sun/xml/xsom/impl/parser/state/annotation.leaveAttribute:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V'.
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
    
    public void text(final String $value) throws SAXException {
        // 
        // This method could not be decompiled.
        // 
        // Could not show original bytecode, likely due to the same error.
        // 
        // The error that occurred was:
        // 
        // com.strobel.assembler.metadata.MethodBodyParseException: An error occurred while parsing the bytecode of method 'com/sun/xml/xsom/impl/parser/state/annotation.text:(Ljava/lang/String;)V'.
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
    
    public void onChildCompleted(final Object $__result__, final int $__cookie__, final boolean $__needAttCheck__) throws SAXException {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: nop            
        //     1: aconst_null    
        //     2: nop            
        //    Exceptions:
        //  throws org.xml.sax.SAXException
        //    LocalVariableTable:
        //  Start  Length  Slot  Name               Signature
        //  -----  ------  ----  -----------------  -----------------------------------------------
        //  0      3       0     this               Lcom/sun/xml/xsom/impl/parser/state/annotation;
        //  0      3       1     $__result__        Ljava/lang/Object;
        //  0      3       2     $__cookie__        I
        //  0      3       3     $__needAttCheck__  Z
        // 
        // The error that occurred was:
        // 
        // java.lang.NullPointerException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public boolean accepted() {
        // 
        // This method could not be decompiled.
        // 
        // Could not show original bytecode, likely due to the same error.
        // 
        // The error that occurred was:
        // 
        // com.strobel.assembler.metadata.MethodBodyParseException: An error occurred while parsing the bytecode of method 'com/sun/xml/xsom/impl/parser/state/annotation.accepted:()Z'.
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
    
    public AnnotationImpl makeResult() {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: nop            
        //     1: iconst_1       
        //     2: nop            
        //     3: iconst_m1      
        //     4: nop            
        //     5: nop            
        //     6: nop            
        //     7: fload_3        
        //     8: aconst_null    
        //     9: astore_1        /* e */
        //    10: aload_0         /* this */
        //    11: getfield        com/sun/xml/xsom/impl/parser/state/annotation.existing:Lcom/sun/xml/xsom/impl/AnnotationImpl;
        //    14: ifnull          25
        //    17: aload_0         /* this */
        //    18: getfield        com/sun/xml/xsom/impl/parser/state/annotation.existing:Lcom/sun/xml/xsom/impl/AnnotationImpl;
        //    21: invokevirtual   com/sun/xml/xsom/impl/AnnotationImpl.getAnnotation:()Ljava/lang/Object;
        //    24: astore_1        /* e */
        //    25: new             Lcom/sun/xml/xsom/impl/AnnotationImpl;
        //    28: dup            
        //    29: aload_0         /* this */
        //    30: getfield        com/sun/xml/xsom/impl/parser/state/annotation.parser:Lcom/sun/xml/xsom/parser/AnnotationParser;
        //    33: aload_1         /* e */
        //    34: invokevirtual   com/sun/xml/xsom/parser/AnnotationParser.getResult:(Ljava/lang/Object;)Ljava/lang/Object;
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  -----------------------------------------------
        //  0      37      0     this  Lcom/sun/xml/xsom/impl/parser/state/annotation;
        //  2      35      1     e     Ljava/lang/Object;
        // 
        // The error that occurred was:
        // 
        // java.lang.NullPointerException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
}
