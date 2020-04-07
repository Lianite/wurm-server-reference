// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.grammar.xducer;

import com.sun.codemodel.JExpr;
import com.sun.msv.grammar.ValueExp;
import com.sun.codemodel.JStatement;
import com.sun.tools.xjc.generator.util.BlockReference;
import com.sun.codemodel.JInvocation;
import com.sun.codemodel.JExpression;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JType;

public class UserTransducer extends TransducerImpl
{
    private final JType type;
    private final JCodeModel codeModel;
    private final String parseMethod;
    private final String printMethod;
    private final boolean enableNamespaceContext;
    private static final String ERR_EXTERNAL_PARSE_METHOD_REQUIRED = "UserTransducer.ExternalParseMethodRequired";
    private static final String ERR_EXTERNAL_PRINT_METHOD_REQUIRED = "UserTransducer.ExternalPrintMethodRequired";
    
    public UserTransducer(final JType _type, final String _parseMethod, final String _printMethod, final boolean _enableNamespaceContext) {
        this.type = _type;
        this.codeModel = _type.owner();
        this.parseMethod = _parseMethod;
        this.printMethod = _printMethod;
        this.enableNamespaceContext = _enableNamespaceContext;
        if (this.type.isPrimitive()) {
            if (this.parseMethod.indexOf(46) == -1) {
                throw new IllegalArgumentException(Messages.format("UserTransducer.ExternalParseMethodRequired", (Object)_type.name()));
            }
            if (this.printMethod.indexOf(46) == -1) {
                throw new IllegalArgumentException(Messages.format("UserTransducer.ExternalPrintMethodRequired", (Object)_type.name()));
            }
        }
    }
    
    public UserTransducer(final JType _type, final String _parseMethod, final String _printMethod) {
        this(_type, _parseMethod, _printMethod, false);
    }
    
    public JType getReturnType() {
        return this.type;
    }
    
    public JExpression generateSerializer(final JExpression value, final SerializerContext context) {
        return this._generateSerializer(value, context);
    }
    
    private JInvocation _generateSerializer(final JExpression value, final SerializerContext context) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: aload_0         /* this */
        //     1: getfield        com/sun/tools/xjc/grammar/xducer/UserTransducer.printMethod:Ljava/lang/String;
        //     4: bipush          46
        //     6: invokevirtual   java/lang/String.lastIndexOf:(I)I
        //     9: istore          idx
        //    11: iload           idx
        //    13: ifge            30
        //    16: aload_1         /* value */
        //    17: aload_0         /* this */
        //    18: getfield        com/sun/tools/xjc/grammar/xducer/UserTransducer.printMethod:Ljava/lang/String;
        //    21: invokeinterface com/sun/codemodel/JExpression.invoke:(Ljava/lang/String;)Lcom/sun/codemodel/JInvocation;
        //    26: astore_3        /* inv */
        //    27: goto            84
        //    30: aload_0         /* this */
        //    31: getfield        com/sun/tools/xjc/grammar/xducer/UserTransducer.codeModel:Lcom/sun/codemodel/JCodeModel;
        //    34: aload_0         /* this */
        //    35: getfield        com/sun/tools/xjc/grammar/xducer/UserTransducer.printMethod:Ljava/lang/String;
        //    38: iconst_0       
        //    39: iload           idx
        //    41: invokevirtual   java/lang/String.substring:(II)Ljava/lang/String;
        //    44: invokevirtual   com/sun/codemodel/JCodeModel.ref:(Ljava/lang/String;)Lcom/sun/codemodel/JClass;
        //    47: aload_0         /* this */
        //    48: getfield        com/sun/tools/xjc/grammar/xducer/UserTransducer.printMethod:Ljava/lang/String;
        //    51: iload           idx
        //    53: iconst_1       
        //    54: iadd           
        //    55: invokevirtual   java/lang/String.substring:(I)Ljava/lang/String;
        //    58: invokevirtual   com/sun/codemodel/JClass.staticInvoke:(Ljava/lang/String;)Lcom/sun/codemodel/JInvocation;
        //    61: aload_1         /* value */
        //    62: invokevirtual   com/sun/codemodel/JInvocation.arg:(Lcom/sun/codemodel/JExpression;)Lcom/sun/codemodel/JInvocation;
        //    65: astore_3        /* inv */
        //    66: goto            84
        //    69: astore          e
        //    71: new             Ljava/lang/NoClassDefFoundError;
        //    74: dup            
        //    75: aload           e
        //    77: invokevirtual   java/lang/ClassNotFoundException.getMessage:()Ljava/lang/String;
        //    80: invokespecial   java/lang/NoClassDefFoundError.<init>:(Ljava/lang/String;)V
        //    83: athrow         
        //    84: aload_0         /* this */
        //    85: getfield        com/sun/tools/xjc/grammar/xducer/UserTransducer.enableNamespaceContext:Z
        //    88: ifeq            102
        //    91: aload_3        
        //    92: aload_2         /* context */
        //    93: invokeinterface com/sun/tools/xjc/grammar/xducer/SerializerContext.getNamespaceContext:()Lcom/sun/codemodel/JExpression;
        //    98: invokevirtual   com/sun/codemodel/JInvocation.arg:(Lcom/sun/codemodel/JExpression;)Lcom/sun/codemodel/JInvocation;
        //   101: pop            
        //   102: aload_3        
        //   103: areturn        
        //    LocalVariableTable:
        //  Start  Length  Slot  Name     Signature
        //  -----  ------  ----  -------  ----------------------------------------------------
        //  27     3       3     inv      Lcom/sun/codemodel/JInvocation;
        //  66     3       3     inv      Lcom/sun/codemodel/JInvocation;
        //  71     13      5     e        Ljava/lang/ClassNotFoundException;
        //  0      104     0     this     Lcom/sun/tools/xjc/grammar/xducer/UserTransducer;
        //  0      104     1     value    Lcom/sun/codemodel/JExpression;
        //  0      104     2     context  Lcom/sun/tools/xjc/grammar/xducer/SerializerContext;
        //  11     93      4     idx      I
        //    Exceptions:
        //  Try           Handler
        //  Start  End    Start  End    Type                              
        //  -----  -----  -----  -----  ----------------------------------
        //  30     66     69     84     Ljava/lang/ClassNotFoundException;
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
    
    public JExpression generateDeserializer(final JExpression literal, final DeserializerContext context) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: aload_0         /* this */
        //     1: getfield        com/sun/tools/xjc/grammar/xducer/UserTransducer.parseMethod:Ljava/lang/String;
        //     4: ldc             "new"
        //     6: invokevirtual   java/lang/String.equals:(Ljava/lang/Object;)Z
        //     9: ifeq            23
        //    12: aload_0         /* this */
        //    13: getfield        com/sun/tools/xjc/grammar/xducer/UserTransducer.type:Lcom/sun/codemodel/JType;
        //    16: invokestatic    com/sun/codemodel/JExpr._new:(Lcom/sun/codemodel/JType;)Lcom/sun/codemodel/JInvocation;
        //    19: astore_3        /* inv */
        //    20: goto            107
        //    23: aload_0         /* this */
        //    24: getfield        com/sun/tools/xjc/grammar/xducer/UserTransducer.parseMethod:Ljava/lang/String;
        //    27: bipush          46
        //    29: invokevirtual   java/lang/String.lastIndexOf:(I)I
        //    32: istore          idx
        //    34: iload           idx
        //    36: ifge            57
        //    39: aload_0         /* this */
        //    40: getfield        com/sun/tools/xjc/grammar/xducer/UserTransducer.type:Lcom/sun/codemodel/JType;
        //    43: checkcast       Lcom/sun/codemodel/JClass;
        //    46: aload_0         /* this */
        //    47: getfield        com/sun/tools/xjc/grammar/xducer/UserTransducer.parseMethod:Ljava/lang/String;
        //    50: invokevirtual   com/sun/codemodel/JClass.staticInvoke:(Ljava/lang/String;)Lcom/sun/codemodel/JInvocation;
        //    53: astore_3        /* inv */
        //    54: goto            107
        //    57: aload_0         /* this */
        //    58: getfield        com/sun/tools/xjc/grammar/xducer/UserTransducer.codeModel:Lcom/sun/codemodel/JCodeModel;
        //    61: aload_0         /* this */
        //    62: getfield        com/sun/tools/xjc/grammar/xducer/UserTransducer.parseMethod:Ljava/lang/String;
        //    65: iconst_0       
        //    66: iload           idx
        //    68: invokevirtual   java/lang/String.substring:(II)Ljava/lang/String;
        //    71: invokevirtual   com/sun/codemodel/JCodeModel.ref:(Ljava/lang/String;)Lcom/sun/codemodel/JClass;
        //    74: aload_0         /* this */
        //    75: getfield        com/sun/tools/xjc/grammar/xducer/UserTransducer.parseMethod:Ljava/lang/String;
        //    78: iload           idx
        //    80: iconst_1       
        //    81: iadd           
        //    82: invokevirtual   java/lang/String.substring:(I)Ljava/lang/String;
        //    85: invokevirtual   com/sun/codemodel/JClass.staticInvoke:(Ljava/lang/String;)Lcom/sun/codemodel/JInvocation;
        //    88: astore_3        /* inv */
        //    89: goto            107
        //    92: astore          e
        //    94: new             Ljava/lang/NoClassDefFoundError;
        //    97: dup            
        //    98: aload           e
        //   100: invokevirtual   java/lang/ClassNotFoundException.getMessage:()Ljava/lang/String;
        //   103: invokespecial   java/lang/NoClassDefFoundError.<init>:(Ljava/lang/String;)V
        //   106: athrow         
        //   107: aload_3        
        //   108: aload_1         /* literal */
        //   109: invokevirtual   com/sun/codemodel/JInvocation.arg:(Lcom/sun/codemodel/JExpression;)Lcom/sun/codemodel/JInvocation;
        //   112: pop            
        //   113: aload_0         /* this */
        //   114: getfield        com/sun/tools/xjc/grammar/xducer/UserTransducer.enableNamespaceContext:Z
        //   117: ifeq            131
        //   120: aload_3        
        //   121: aload_2         /* context */
        //   122: invokeinterface com/sun/tools/xjc/grammar/xducer/DeserializerContext.getNamespaceContext:()Lcom/sun/codemodel/JExpression;
        //   127: invokevirtual   com/sun/codemodel/JInvocation.arg:(Lcom/sun/codemodel/JExpression;)Lcom/sun/codemodel/JInvocation;
        //   130: pop            
        //   131: aload_3        
        //   132: areturn        
        //    LocalVariableTable:
        //  Start  Length  Slot  Name     Signature
        //  -----  ------  ----  -------  ------------------------------------------------------
        //  20     3       3     inv      Lcom/sun/codemodel/JInvocation;
        //  54     3       3     inv      Lcom/sun/codemodel/JInvocation;
        //  89     3       3     inv      Lcom/sun/codemodel/JInvocation;
        //  94     13      5     e        Ljava/lang/ClassNotFoundException;
        //  34     73      4     idx      I
        //  0      133     0     this     Lcom/sun/tools/xjc/grammar/xducer/UserTransducer;
        //  0      133     1     literal  Lcom/sun/codemodel/JExpression;
        //  0      133     2     context  Lcom/sun/tools/xjc/grammar/xducer/DeserializerContext;
        //    Exceptions:
        //  Try           Handler
        //  Start  End    Start  End    Type                              
        //  -----  -----  -----  -----  ----------------------------------
        //  57     89     92     107    Ljava/lang/ClassNotFoundException;
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
    
    public void declareNamespace(final BlockReference body, final JExpression value, final SerializerContext context) {
        if (this.enableNamespaceContext) {
            body.get(true).add(this._generateSerializer(value, context));
        }
    }
    
    public JExpression generateConstant(final ValueExp exp) {
        return this.generateDeserializer(this.codeModel.ref((UserTransducer.class$com$sun$xml$bind$DatatypeConverterImpl == null) ? (UserTransducer.class$com$sun$xml$bind$DatatypeConverterImpl = class$("com.sun.xml.bind.DatatypeConverterImpl")) : UserTransducer.class$com$sun$xml$bind$DatatypeConverterImpl).staticInvoke("installHook").arg(JExpr.lit(this.obtainString(exp))), null);
    }
}
