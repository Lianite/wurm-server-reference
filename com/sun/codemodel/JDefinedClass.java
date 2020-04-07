// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.codemodel;

import java.util.Iterator;
import java.util.Map;
import java.util.List;

public abstract class JDefinedClass extends JClass implements JDeclaration, JClassContainer
{
    private String name;
    private final boolean isInterface;
    private JMods mods;
    private JClass superClass;
    private final List interfaces;
    private final List fields;
    private final Map fieldsByName;
    private JBlock init;
    private JDocComment jdoc;
    private final List constructors;
    private final List methods;
    private final Map classes;
    private final Map upperCaseClassMap;
    private boolean hideFile;
    public Object metadata;
    private String directBlock;
    
    JDefinedClass(final int mods, final String name, final boolean isInterface, final JCodeModel owner) {
        // 
        // This method could not be decompiled.
        // 
        // Could not show original bytecode, likely due to the same error.
        // 
        // The error that occurred was:
        // 
        // com.strobel.assembler.metadata.MethodBodyParseException: An error occurred while parsing the bytecode of method 'com/sun/codemodel/JDefinedClass.<init>:(ILjava/lang/String;ZLcom/sun/codemodel/JCodeModel;)V'.
        //     at com.strobel.assembler.metadata.MethodReader.readBody(MethodReader.java:65)
        //     at com.strobel.assembler.metadata.MethodDefinition.tryLoadBody(MethodDefinition.java:722)
        //     at com.strobel.assembler.metadata.MethodDefinition.getBody(MethodDefinition.java:83)
        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:202)
        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:99)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethodBody(AstBuilder.java:757)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createConstructor(AstBuilder.java:692)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addTypeMembers(AstBuilder.java:529)
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
        //     at com.strobel.assembler.metadata.MethodReader.readBodyCore(MethodReader.java:191)
        //     at com.strobel.assembler.metadata.MethodReader.readBody(MethodReader.java:62)
        //     ... 16 more
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public JDefinedClass _extends(final JClass superClass) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: ldiv           
        //     1: fstore_0        /* this */
        //     2: ddiv           
        //     3: fdiv           
        //     4: drem           
        //     5: ineg           
        //     6: ladd           
        //     7: fdiv           
        //     8: ineg           
        //     9: fconst_1       
        //    10: nop            
        //    11: ifnonnull       543
        //    14: fconst_1       
        //    15: iconst_m1      
        //    16: iload           2
        //    18: lload           superClass
        //    20: nop            
        //    21: lload_1         /* superClass */
        //    22: dadd           
        //    23: ddiv           
        //    24: ldiv           
        //    25: laload         
        //    26: drem           
        //    27: lneg           
        //    28: fdiv           
        //    29: laload         
        //    30: dadd           
        //    31: ddiv           
        //    32: isub           
        //    33: lsub           
        //    34: ldiv           
        //    35: ddiv           
        //    LocalVariableTable:
        //  Start  Length  Slot  Name        Signature
        //  -----  ------  ----  ----------  ---------------------------------
        //  0      36      0     this        Lcom/sun/codemodel/JDefinedClass;
        //  0      36      1     superClass  Lcom/sun/codemodel/JClass;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException: -33
        //     at com.strobel.assembler.flowanalysis.ControlFlowGraphBuilder.calculateIncomingJumps(ControlFlowGraphBuilder.java:122)
        //     at com.strobel.assembler.flowanalysis.ControlFlowGraphBuilder.build(ControlFlowGraphBuilder.java:96)
        //     at com.strobel.assembler.flowanalysis.ControlFlowGraphBuilder.build(ControlFlowGraphBuilder.java:60)
        //     at com.strobel.decompiler.ast.AstBuilder$FinallyInlining.preProcess(AstBuilder.java:4670)
        //     at com.strobel.decompiler.ast.AstBuilder$FinallyInlining.<init>(AstBuilder.java:4160)
        //     at com.strobel.decompiler.ast.AstBuilder$FinallyInlining.run(AstBuilder.java:4275)
        //     at com.strobel.decompiler.ast.AstBuilder.build(AstBuilder.java:100)
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
    
    public JDefinedClass _extends(final Class superClass) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: frem           
        //     1: fconst_1       
        //     2: iconst_m1      
        //     3: iload_0         /* this */
        //     4: iconst_m1      
        //     5: iconst_4       
        //     6: fconst_1       
        //     7: nop            
        //     8: ifnonnull       547
        //    11: aconst_null    
        //    12: nop            
        //    LocalVariableTable:
        //  Start  Length  Slot  Name        Signature
        //  -----  ------  ----  ----------  ---------------------------------
        //  0      13      0     this        Lcom/sun/codemodel/JDefinedClass;
        //  0      13      1     superClass  Ljava/lang/Class;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException: -12
        //     at com.strobel.assembler.flowanalysis.ControlFlowGraphBuilder.calculateIncomingJumps(ControlFlowGraphBuilder.java:122)
        //     at com.strobel.assembler.flowanalysis.ControlFlowGraphBuilder.build(ControlFlowGraphBuilder.java:96)
        //     at com.strobel.assembler.flowanalysis.ControlFlowGraphBuilder.build(ControlFlowGraphBuilder.java:60)
        //     at com.strobel.decompiler.ast.AstBuilder$FinallyInlining.preProcess(AstBuilder.java:4670)
        //     at com.strobel.decompiler.ast.AstBuilder$FinallyInlining.<init>(AstBuilder.java:4160)
        //     at com.strobel.decompiler.ast.AstBuilder$FinallyInlining.run(AstBuilder.java:4275)
        //     at com.strobel.decompiler.ast.AstBuilder.build(AstBuilder.java:100)
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
    
    public JClass _extends() {
        // 
        // This method could not be decompiled.
        // 
        // Could not show original bytecode, likely due to the same error.
        // 
        // The error that occurred was:
        // 
        // com.strobel.assembler.metadata.MethodBodyParseException: An error occurred while parsing the bytecode of method 'com/sun/codemodel/JDefinedClass._extends:()Lcom/sun/codemodel/JClass;'.
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
        // Caused by: java.lang.ClassCastException: com.strobel.assembler.ir.ConstantPool$NameAndTypeDescriptorEntry cannot be cast to com.strobel.assembler.ir.ConstantPool$ConstantEntry
        //     at com.strobel.assembler.ir.ConstantPool.lookupConstant(ConstantPool.java:120)
        //     at com.strobel.assembler.metadata.ClassFileReader$Scope.lookupConstant(ClassFileReader.java:1319)
        //     at com.strobel.assembler.metadata.MethodReader.readBodyCore(MethodReader.java:293)
        //     at com.strobel.assembler.metadata.MethodReader.readBody(MethodReader.java:62)
        //     ... 16 more
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public JDefinedClass _implements(final JClass iface) {
        // 
        // This method could not be decompiled.
        // 
        // Could not show original bytecode, likely due to the same error.
        // 
        // The error that occurred was:
        // 
        // com.strobel.assembler.metadata.MethodBodyParseException: An error occurred while parsing the bytecode of method 'com/sun/codemodel/JDefinedClass._implements:(Lcom/sun/codemodel/JClass;)Lcom/sun/codemodel/JDefinedClass;'.
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
        // Caused by: java.lang.NullPointerException
        //     at com.strobel.assembler.metadata.MethodReader.readBodyCore(MethodReader.java:170)
        //     at com.strobel.assembler.metadata.MethodReader.readBody(MethodReader.java:62)
        //     ... 16 more
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public JDefinedClass _implements(final Class iface) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: ddiv           
        //     1: isub           
        //     2: lsub           
        //     3: idiv           
        //     4: laload         
        //     5: dstore_3       
        //     6: fstore_0        /* this */
        //     7: idiv           
        //     8: ladd           
        //     9: drem           
        //    10: drem           
        //    11: lstore_2       
        //    12: idiv           
        //    LocalVariableTable:
        //  Start  Length  Slot  Name   Signature
        //  -----  ------  ----  -----  ---------------------------------
        //  0      13      0     this   Lcom/sun/codemodel/JDefinedClass;
        //  0      13      1     iface  Ljava/lang/Class;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public Iterator _implements() {
        // 
        // This method could not be decompiled.
        // 
        // Could not show original bytecode, likely due to the same error.
        // 
        // The error that occurred was:
        // 
        // com.strobel.assembler.metadata.MethodBodyParseException: An error occurred while parsing the bytecode of method 'com/sun/codemodel/JDefinedClass._implements:()Ljava/util/Iterator;'.
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
    
    public String name() {
        // 
        // This method could not be decompiled.
        // 
        // Could not show original bytecode, likely due to the same error.
        // 
        // The error that occurred was:
        // 
        // com.strobel.assembler.metadata.MethodBodyParseException: An error occurred while parsing the bytecode of method 'com/sun/codemodel/JDefinedClass.name:()Ljava/lang/String;'.
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
        // Caused by: java.lang.NullPointerException
        //     at com.strobel.assembler.metadata.MethodReader.readBodyCore(MethodReader.java:170)
        //     at com.strobel.assembler.metadata.MethodReader.readBody(MethodReader.java:62)
        //     ... 16 more
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public boolean isInterface() {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: dadd           
        //     1: ddiv           
        //     2: ldiv           
        //     3: laload         
        //     4: drem           
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  ---------------------------------
        //  0      5       0     this  Lcom/sun/codemodel/JDefinedClass;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public JFieldVar field(final int mods, final JType type, final String name) {
        // 
        // This method could not be decompiled.
        // 
        // Could not show original bytecode, likely due to the same error.
        // 
        // The error that occurred was:
        // 
        // com.strobel.assembler.metadata.MethodBodyParseException: An error occurred while parsing the bytecode of method 'com/sun/codemodel/JDefinedClass.field:(ILcom/sun/codemodel/JType;Ljava/lang/String;)Lcom/sun/codemodel/JFieldVar;'.
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
        // Caused by: java.lang.NullPointerException
        //     at com.strobel.assembler.metadata.MethodReader.readBodyCore(MethodReader.java:170)
        //     at com.strobel.assembler.metadata.MethodReader.readBody(MethodReader.java:62)
        //     ... 16 more
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public JFieldVar field(final int mods, final Class type, final String name) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: fconst_1       
        //     1: aconst_null    
        //     2: istore_0        /* this */
        //     3: aconst_null    
        //     4: istore_1        /* mods */
        //     5: fconst_1       
        //     6: aconst_null    
        //     7: istore_0        /* this */
        //     8: aconst_null    
        //     9: istore_2        /* type */
        //    10: fconst_1       
        //    11: aconst_null    
        //    12: istore_0        /* this */
        //    13: aconst_null    
        //    14: lstore_0        /* this */
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  ---------------------------------
        //  0      15      0     this  Lcom/sun/codemodel/JDefinedClass;
        //  0      15      1     mods  I
        //  0      15      2     type  Ljava/lang/Class;
        //  0      15      3     name  Ljava/lang/String;
        // 
        // The error that occurred was:
        // 
        // java.lang.NullPointerException
        //     at com.strobel.decompiler.ast.AstBuilder.performStackAnalysis(AstBuilder.java:2162)
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
    
    public JFieldVar field(final int mods, final JType type, final String name, final JExpression init) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: ineg           
        //     1: ladd           
        //     2: lmul           
        //     3: fdiv           
        //     4: lsub           
        //     5: frem           
        //     6: aconst_null    
        //     7: nop            
        //     8: lload_1         /* mods */
        //     9: dadd           
        //    10: ddiv           
        //    11: ldiv           
        //    12: laload         
        //    13: drem           
        //    14: lneg           
        //    15: fdiv           
        //    16: laload         
        //    17: dadd           
        //    18: ddiv           
        //    19: isub           
        //    20: lsub           
        //    21: ldiv           
        //    22: ddiv           
        //    23: isub           
        //    24: lsub           
        //    25: idiv           
        //    26: laload         
        //    27: dstore_3        /* name */
        //    28: dstore_0        /* this */
        //    29: lsub           
        //    30: fdiv           
        //    31: lsub           
        //    32: frem           
        //    33: lmul           
        //    34: fsub           
        //    35: lmul           
        //    36: ladd           
        //    37: fadd           
        //    38: idiv           
        //    39: lsub           
        //    40: aconst_null    
        //    41: nop            
        //    42: lload_0         /* this */
        //    43: dadd           
        //    44: ddiv           
        //    45: ldiv           
        //    46: laload         
        //    47: drem           
        //    48: lneg           
        //    49: fdiv           
        //    50: laload         
        //    51: dadd           
        //    52: ddiv           
        //    53: isub           
        //    54: lsub           
        //    55: ldiv           
        //    56: ddiv           
        //    57: isub           
        //    58: lsub           
        //    59: idiv           
        //    60: laload         
        //    61: dstore_3        /* name */
        //    62: lstore_2        /* type */
        //    63: fdiv           
        //    64: fdiv           
        //    65: ddiv           
        //    66: ineg           
        //    67: ladd           
        //    68: ineg           
        //    69: ladd           
        //    70: fadd           
        //    71: idiv           
        //    72: lsub           
        //    73: aconst_null    
        //    74: nop            
        //    75: lload_2         /* type */
        //    76: dload_2         /* type */
        //    LocalVariableTable:
        //  Start  Length  Slot  Name      Signature
        //  -----  ------  ----  --------  ---------------------------------
        //  0      77      0     this      Lcom/sun/codemodel/JDefinedClass;
        //  0      77      1     mods      I
        //  0      77      2     type      Lcom/sun/codemodel/JType;
        //  0      77      3     name      Ljava/lang/String;
        //  0      77      4     init      Lcom/sun/codemodel/JExpression;
        //  17     60      5     f         Lcom/sun/codemodel/JFieldVar;
        //  44     33      6     existing  Lcom/sun/codemodel/JFieldVar;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public JFieldVar field(final int mods, final Class type, final String name, final JExpression init) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: drem           
        //     1: istore_0        /* this */
        //     2: dload_3         /* name */
        //     3: sastore        
        //     4: aconst_null    
        //     5: nop            
        //     6: bipush          106
        //     8: ladd           
        //     9: fneg           
        //    10: ladd           
        //    11: laload         
        //    12: idiv           
        //    13: ladd           
        //    14: fdiv           
        //    15: dsub           
        //    16: laload         
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  ---------------------------------
        //  0      17      0     this  Lcom/sun/codemodel/JDefinedClass;
        //  0      17      1     mods  I
        //  0      17      2     type  Ljava/lang/Class;
        //  0      17      3     name  Ljava/lang/String;
        //  0      17      4     init  Lcom/sun/codemodel/JExpression;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public Iterator fields() {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: dup_x1         
        //     1: aconst_null    
        //     2: nop            
        //     3: iconst_3       
        //     4: ladd           
        //     5: irem           
        //     6: irem           
        //     7: lsub           
        //     8: fdiv           
        //     9: isub           
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  ---------------------------------
        //  0      10      0     this  Lcom/sun/codemodel/JDefinedClass;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public JBlock init() {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: ladd           
        //     1: fneg           
        //     2: ladd           
        //     3: laload         
        //     4: idiv           
        //     5: ladd           
        //     6: fdiv           
        //     7: dsub           
        //     8: laload         
        //     9: aastore        
        //    10: ineg           
        //    11: frem           
        //    12: lmul           
        //    13: fdiv           
        //    14: dsub           
        //    15: lstore_3       
        //    16: lneg           
        //    17: lmul           
        //    18: idiv           
        //    19: isub           
        //    20: lsub           
        //    21: frem           
        //    22: istore_0        /* this */
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  ---------------------------------
        //  0      23      0     this  Lcom/sun/codemodel/JDefinedClass;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public JMethod constructor(final int mods) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: aconst_null    
        //     1: nop            
        //     2: fconst_1       
        //     3: fsub           
        //     4: ddiv           
        //     5: frem           
        //     6: dstore_2       
        //     7: fdiv           
        //     8: ineg           
        //     9: lsub           
        //    10: frem           
        //    11: fsub           
        //    12: ladd           
        //    13: dadd           
        //    14: lsub           
        //    15: aconst_null    
        //    16: nop            
        //    17: iload_2         /* c */
        //    18: dload_2         /* c */
        //    19: dstore_2        /* c */
        //    20: dload_3        
        //    21: astore_1        /* mods */
        //    22: dadd           
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  ---------------------------------
        //  0      23      0     this  Lcom/sun/codemodel/JDefinedClass;
        //  0      23      1     mods  I
        //  10     13      2     c     Lcom/sun/codemodel/JMethod;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public Iterator constructors() {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: nop            
        //     1: aload_3        
        //     2: dload_2        
        //     3: astore_1       
        //     4: fmul           
        //     5: ladd           
        //     6: fneg           
        //     7: ladd           
        //     8: laload         
        //     9: idiv           
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  ---------------------------------
        //  0      10      0     this  Lcom/sun/codemodel/JDefinedClass;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException: 1
        //     at com.strobel.decompiler.ast.AstBuilder.performStackAnalysis(AstBuilder.java:2064)
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
    
    public JMethod getConstructor(final JType[] argTypes) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: ladd           
        //     1: isub           
        //     2: isub           
        //     3: aconst_null    
        //     4: nop            
        //     5: iload           40
        //     7: astore_1        /* argTypes */
        //     8: fmul           
        //     9: ladd           
        //    10: fneg           
        //    11: ladd           
        //    12: laload         
        //    13: idiv           
        //    14: ladd           
        //    15: fdiv           
        //    16: dsub           
        //    17: laload         
        //    18: iastore        
        //    19: fadd           
        //    20: fmul           
        //    21: lsub           
        //    22: dadd           
        //    23: ineg           
        //    24: istore_0        /* this */
        //    25: dload_3        
        //    26: dup_x1         
        //    27: aconst_null    
        //    28: nop            
        //    29: iconst_5       
        //    30: lmul           
        //    31: ineg           
        //    32: lsub           
        //    33: frem           
        //    34: ladd           
        //    35: ineg           
        //    36: ddiv           
        //    37: frem           
        //    38: aconst_null    
        //    39: nop            
        //    40: fconst_2       
        //    41: fmul           
        //    42: ladd           
        //    43: fneg           
        //    LocalVariableTable:
        //  Start  Length  Slot  Name      Signature
        //  -----  ------  ----  --------  ---------------------------------
        //  29     10      3     m         Lcom/sun/codemodel/JMethod;
        //  10     32      2     itr       Ljava/util/Iterator;
        //  0      44      0     this      Lcom/sun/codemodel/JDefinedClass;
        //  0      44      1     argTypes  [Lcom/sun/codemodel/JType;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public JMethod method(final int mods, final JType type, final String name) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: fdiv           
        //     1: dsub           
        //     2: laload         
        //     3: aastore        
        //     4: ineg           
        //     5: frem           
        //     6: lmul           
        //     7: fdiv           
        //     8: dsub           
        //     9: istore_0        /* this */
        //    10: dload_3         /* name */
        //    11: sastore        
        //    12: aconst_null    
        //    13: nop            
        //    14: iconst_0       
        //    15: irem           
        //    16: lneg           
        //    17: ineg           
        //    18: aconst_null    
        //    19: nop            
        //    20: fstore          40
        //    22: astore_1        /* mods */
        //    23: fmul           
        //    24: ladd           
        //    25: fneg           
        //    26: ladd           
        //    27: laload         
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  ---------------------------------
        //  0      28      0     this  Lcom/sun/codemodel/JDefinedClass;
        //  0      28      1     mods  I
        //  0      28      2     type  Lcom/sun/codemodel/JType;
        //  0      28      3     name  Ljava/lang/String;
        //  13     15      4     m     Lcom/sun/codemodel/JMethod;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public JMethod method(final int mods, final Class type, final String name) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: ddiv           
        //     1: isub           
        //     2: lsub           
        //     3: idiv           
        //     4: laload         
        //     5: dstore_3        /* name */
        //     6: bastore        
        //     7: lshl           
        //     8: irem           
        //     9: lsub           
        //    10: istore_0        /* this */
        //    11: aconst_null    
        //    12: nop            
        //    13: iconst_5       
        //    14: fsub           
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  ---------------------------------
        //  0      15      0     this  Lcom/sun/codemodel/JDefinedClass;
        //  0      15      1     mods  I
        //  0      15      2     type  Ljava/lang/Class;
        //  0      15      3     name  Ljava/lang/String;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public Iterator methods() {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: idiv           
        //     1: laload         
        //     2: dstore_3       
        //     3: bastore        
        //     4: lshl           
        //     5: irem           
        //     6: lsub           
        //     7: istore_0        /* this */
        //     8: astore_1       
        //     9: fmul           
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  ---------------------------------
        //  0      10      0     this  Lcom/sun/codemodel/JDefinedClass;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public JMethod getMethod(final String name, final JType[] argTypes) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: bastore        
        //     1: lstore_2        /* argTypes */
        //     2: bastore        
        //     3: dstore_2        /* argTypes */
        //     4: iastore        
        //     5: astore_3       
        //     6: swap           
        //     7: bastore        
        //     8: dup            
        //     9: lastore        
        //    10: fstore_2        /* argTypes */
        //    11: swap           
        //    12: fstore_1        /* name */
        //    13: fstore_2        /* argTypes */
        //    14: fstore_0        /* this */
        //    15: astore_1        /* name */
        //    16: aconst_null    
        //    17: nop            
        //    18: iconst_1       
        //    19: fstore_2        /* argTypes */
        //    20: astore_3        /* itr */
        //    21: castore        
        //    22: astore_2        /* argTypes */
        //    23: aconst_null    
        //    24: nop            
        //    25: iload           106
        //    27: ladd           
        //    28: fneg           
        //    29: ladd           
        //    30: laload         
        //    31: lneg           
        //    32: ineg           
        //    33: lmul           
        //    34: idiv           
        //    35: laload         
        //    36: fstore_0        /* this */
        //    37: ddiv           
        //    38: idiv           
        //    39: idiv           
        //    40: lsub           
        //    41: dadd           
        //    42: ineg           
        //    43: lmul           
        //    44: ddiv           
        //    45: fdiv           
        //    46: drem           
        //    47: aconst_null    
        //    48: nop            
        //    49: dconst_1       
        //    50: lneg           
        //    51: fdiv           
        //    52: ldiv           
        //    53: ddiv           
        //    54: isub           
        //    55: lmul           
        //    56: fsub           
        //    57: lmul           
        //    58: ladd           
        //    59: fadd           
        //    60: idiv           
        //    61: lsub           
        //    LocalVariableTable:
        //  Start  Length  Slot  Name      Signature
        //  -----  ------  ----  --------  ---------------------------------
        //  30     27      4     m         Lcom/sun/codemodel/JMethod;
        //  10     50      3     itr       Ljava/util/Iterator;
        //  0      62      0     this      Lcom/sun/codemodel/JDefinedClass;
        //  0      62      1     name      Ljava/lang/String;
        //  0      62      2     argTypes  [Lcom/sun/codemodel/JType;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public JDefinedClass _class(final int mods, final String name) throws JClassAlreadyExistsException {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: frem           
        //     1: ladd           
        //     2: ineg           
        //     3: ddiv           
        //     4: frem           
        //     5: aconst_null    
        //     6: nop            
        //     7: iconst_4       
        //    Exceptions:
        //  throws com.sun.codemodel.JClassAlreadyExistsException
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  ---------------------------------
        //  0      8       0     this  Lcom/sun/codemodel/JDefinedClass;
        //  0      8       1     mods  I
        //  0      8       2     name  Ljava/lang/String;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public JDefinedClass _class(final int mods, final String name, final boolean isInterface) throws JClassAlreadyExistsException {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: lastore        
        //     1: dload_2         /* name */
        //     2: astore_1        /* mods */
        //     3: dadd           
        //     4: ddiv           
        //     5: ldiv           
        //     6: laload         
        //     7: drem           
        //     8: lneg           
        //     9: fdiv           
        //    10: laload         
        //    11: dadd           
        //    12: ddiv           
        //    13: isub           
        //    14: lsub           
        //    15: ldiv           
        //    16: ddiv           
        //    17: isub           
        //    18: lsub           
        //    19: idiv           
        //    20: laload         
        //    21: dstore_3        /* isInterface */
        //    22: fstore_1        /* mods */
        //    23: lsub           
        //    24: fsub           
        //    25: lmul           
        //    26: fdiv           
        //    27: lsub           
        //    28: isub           
        //    29: fstore_0        /* this */
        //    30: idiv           
        //    31: ladd           
        //    32: drem           
        //    33: drem           
        //    34: istore_0        /* this */
        //    35: dstore_2        /* name */
        //    36: astore_1        /* mods */
        //    37: dadd           
        //    38: ddiv           
        //    39: ldiv           
        //    40: laload         
        //    41: drem           
        //    42: lneg           
        //    43: fdiv           
        //    44: laload         
        //    45: dadd           
        //    46: ddiv           
        //    47: isub           
        //    48: lsub           
        //    49: ldiv           
        //    50: ddiv           
        //    51: isub           
        //    52: lsub           
        //    53: idiv           
        //    54: laload         
        //    55: dstore_3        /* isInterface */
        //    56: bastore        
        //    57: lshl           
        //    58: irem           
        //    59: lsub           
        //    60: istore_0        /* this */
        //    61: astore_1        /* mods */
        //    62: fmul           
        //    63: ladd           
        //    64: fneg           
        //    65: ladd           
        //    66: laload         
        //    67: idiv           
        //    68: ladd           
        //    69: fdiv           
        //    70: dsub           
        //    71: laload         
        //    72: aastore        
        //    73: ineg           
        //    74: frem           
        //    75: lmul           
        //    76: fdiv           
        //    77: dsub           
        //    78: istore_0        /* this */
        //    79: dload_3         /* isInterface */
        //    80: sastore        
        //    81: aconst_null    
        //    82: nop            
        //    83: iconst_3       
        //    84: lsub           
        //    85: lrem           
        //    86: lneg           
        //    87: ladd           
        //    88: idiv           
        //    89: drem           
        //    90: aconst_null    
        //    91: nop            
        //    92: aload           105
        //    94: drem           
        //    95: fstore_0        /* this */
        //    96: ladd           
        //    97: drem           
        //    98: lsub           
        //    99: aastore        
        //   100: lsub           
        //   101: fdiv           
        //   102: drem           
        //   103: lmul           
        //   104: ineg           
        //   105: lmul           
        //   106: fneg           
        //   107: lsub           
        //   108: fstore_3        /* isInterface */
        //   109: lmul           
        //   110: idiv           
        //   111: lsub           
        //   112: aastore        
        //   113: lshl           
        //   114: drem           
        //   115: ineg           
        //   116: lsub           
        //   117: ldiv           
        //   118: aconst_null    
        //    Exceptions:
        //  throws com.sun.codemodel.JClassAlreadyExistsException
        //    LocalVariableTable:
        //  Start  Length  Slot  Name         Signature
        //  -----  ------  ----  -----------  ---------------------------------
        //  72     31      5     dc           Lcom/sun/codemodel/JDefinedClass;
        //  47     72      4     c            Lcom/sun/codemodel/JDefinedClass;
        //  0      119     0     this         Lcom/sun/codemodel/JDefinedClass;
        //  0      119     1     mods         I
        //  0      119     2     name         Ljava/lang/String;
        //  0      119     3     isInterface  Z
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public JDefinedClass _class(final String name) throws JClassAlreadyExistsException {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: aconst_null    
        //     1: nop            
        //     2: iload_2        
        //     3: dadd           
        //     4: ddiv           
        //     5: ldiv           
        //     6: laload         
        //    Exceptions:
        //  throws com.sun.codemodel.JClassAlreadyExistsException
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  ---------------------------------
        //  0      7       0     this  Lcom/sun/codemodel/JDefinedClass;
        //  0      7       1     name  Ljava/lang/String;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public JDefinedClass _interface(final int mods, final String name) throws JClassAlreadyExistsException {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: laload         
        //     1: drem           
        //     2: lneg           
        //     3: fdiv           
        //     4: laload         
        //     5: dadd           
        //     6: ddiv           
        //     7: isub           
        //    Exceptions:
        //  throws com.sun.codemodel.JClassAlreadyExistsException
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  ---------------------------------
        //  0      8       0     this  Lcom/sun/codemodel/JDefinedClass;
        //  0      8       1     mods  I
        //  0      8       2     name  Ljava/lang/String;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public JDefinedClass _interface(final String name) throws JClassAlreadyExistsException {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: lmul           
        //     1: fdiv           
        //     2: dsub           
        //     3: istore_0        /* this */
        //     4: dload_3        
        //     5: astore_1        /* name */
        //     6: dadd           
        //    Exceptions:
        //  throws com.sun.codemodel.JClassAlreadyExistsException
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  ---------------------------------
        //  0      7       0     this  Lcom/sun/codemodel/JDefinedClass;
        //  0      7       1     name  Ljava/lang/String;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public JDocComment javadoc() {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: ddiv           
        //     1: ldiv           
        //     2: laload         
        //     3: drem           
        //     4: lneg           
        //     5: fdiv           
        //     6: laload         
        //     7: dadd           
        //     8: ddiv           
        //     9: isub           
        //    10: lsub           
        //    11: ldiv           
        //    12: ddiv           
        //    13: isub           
        //    14: lsub           
        //    15: idiv           
        //    16: laload         
        //    17: dstore_3       
        //    18: fstore_3       
        //    19: ddiv           
        //    20: frem           
        //    21: ldiv           
        //    22: ladd           
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  ---------------------------------
        //  0      23      0     this  Lcom/sun/codemodel/JDefinedClass;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public void hide() {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: frem           
        //     1: ldiv           
        //     2: ladd           
        //     3: ineg           
        //     4: ineg           
        //     5: lsub           
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  ---------------------------------
        //  0      6       0     this  Lcom/sun/codemodel/JDefinedClass;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public boolean isHidden() {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: ddiv           
        //     1: isub           
        //     2: lsub           
        //     3: idiv           
        //     4: laload         
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  ---------------------------------
        //  0      5       0     this  Lcom/sun/codemodel/JDefinedClass;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public final Iterator classes() {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: lsub           
        //     1: ldiv           
        //     2: ddiv           
        //     3: isub           
        //     4: lsub           
        //     5: idiv           
        //     6: laload         
        //     7: dstore_3       
        //     8: dstore_0        /* this */
        //     9: lsub           
        //    10: fdiv           
        //    11: lsub           
        //    12: frem           
        //    13: lmul           
        //    14: fsub           
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  ---------------------------------
        //  0      15      0     this  Lcom/sun/codemodel/JDefinedClass;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public final JClass[] listClasses() {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: ldiv           
        //     1: ddiv           
        //     2: isub           
        //     3: lsub           
        //     4: idiv           
        //     5: laload         
        //     6: bastore        
        //     7: lshl           
        //     8: irem           
        //     9: lsub           
        //    10: isub           
        //    11: lstore_2       
        //    12: fdiv           
        //    13: fdiv           
        //    14: ddiv           
        //    15: ineg           
        //    16: ladd           
        //    17: ineg           
        //    18: lmul           
        //    19: ddiv           
        //    20: fdiv           
        //    21: pop            
        //    22: frem           
        //    23: lmul           
        //    24: ineg           
        //    25: lsub           
        //    26: frem           
        //    27: aconst_null    
        //    28: nop            
        //    29: iconst_3       
        //    30: dadd           
        //    31: frem           
        //    32: lsub           
        //    33: ladd           
        //    34: ineg           
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  ---------------------------------
        //  0      35      0     this  Lcom/sun/codemodel/JDefinedClass;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public JClass outer() {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: dadd           
        //     1: ddiv           
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  ---------------------------------
        //  0      2       0     this  Lcom/sun/codemodel/JDefinedClass;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public void declare(final JFormatter f) {
        // 
        // This method could not be decompiled.
        // 
        // Could not show original bytecode, likely due to the same error.
        // 
        // The error that occurred was:
        // 
        // com.strobel.assembler.metadata.MethodBodyParseException: An error occurred while parsing the bytecode of method 'com/sun/codemodel/JDefinedClass.declare:(Lcom/sun/codemodel/JFormatter;)V'.
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
    
    protected void declareBody(final JFormatter f) {
        // 
        // This method could not be decompiled.
        // 
        // Could not show original bytecode, likely due to the same error.
        // 
        // The error that occurred was:
        // 
        // com.strobel.assembler.metadata.MethodBodyParseException: An error occurred while parsing the bytecode of method 'com/sun/codemodel/JDefinedClass.declareBody:(Lcom/sun/codemodel/JFormatter;)V'.
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
        // Caused by: java.lang.NullPointerException
        //     at com.strobel.assembler.metadata.MethodReader.readBodyCore(MethodReader.java:170)
        //     at com.strobel.assembler.metadata.MethodReader.readBody(MethodReader.java:62)
        //     ... 16 more
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public void generate(final JFormatter f) {
        // 
        // This method could not be decompiled.
        // 
        // Could not show original bytecode, likely due to the same error.
        // 
        // The error that occurred was:
        // 
        // com.strobel.assembler.metadata.MethodBodyParseException: An error occurred while parsing the bytecode of method 'com/sun/codemodel/JDefinedClass.generate:(Lcom/sun/codemodel/JFormatter;)V'.
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
    
    public void direct(final String string) {
        // 
        // This method could not be decompiled.
        // 
        // Could not show original bytecode, likely due to the same error.
        // 
        // The error that occurred was:
        // 
        // com.strobel.assembler.metadata.MethodBodyParseException: An error occurred while parsing the bytecode of method 'com/sun/codemodel/JDefinedClass.direct:(Ljava/lang/String;)V'.
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
        // Caused by: java.lang.IllegalArgumentException: Argument 'index' must be in the range [0, 416], but value was: 45312.
        //     at com.strobel.core.VerifyArgument.inRange(VerifyArgument.java:346)
        //     at com.strobel.assembler.ir.ConstantPool.get(ConstantPool.java:78)
        //     at com.strobel.assembler.metadata.ClassFileReader$Scope.lookupConstant(ClassFileReader.java:1313)
        //     at com.strobel.assembler.metadata.MethodReader.readBodyCore(MethodReader.java:293)
        //     at com.strobel.assembler.metadata.MethodReader.readBody(MethodReader.java:62)
        //     ... 16 more
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
}
