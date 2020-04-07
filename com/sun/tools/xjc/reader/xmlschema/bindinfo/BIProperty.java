// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.reader.xmlschema.bindinfo;

import com.sun.tools.xjc.reader.xmlschema.BGMBuilder;
import com.sun.tools.xjc.grammar.FieldItem;
import com.sun.xml.xsom.XSComponent;
import com.sun.msv.grammar.Expression;
import org.xml.sax.Locator;
import javax.xml.namespace.QName;
import com.sun.xml.xsom.visitor.XSFunction;
import com.sun.xml.xsom.util.XSFinder;
import com.sun.tools.xjc.generator.field.FieldRendererFactory;
import com.sun.codemodel.JType;

public final class BIProperty extends AbstractDeclarationImpl
{
    private final String propName;
    private final String javadoc;
    private final JType baseType;
    public final BIConversion conv;
    private final FieldRendererFactory realization;
    private final Boolean needIsSetMethod;
    private Boolean isConstantProperty;
    private final XSFinder hasFixedValue;
    private static final XSFunction defaultCustomizationFinder;
    public static final QName NAME;
    private static final String ERR_ILLEGAL_FIXEDATTR = "BIProperty.IllegalFixedAttributeAsConstantProperty";
    
    public BIProperty(final Locator loc, final String _propName, final String _javadoc, final JType _baseType, final BIConversion _conv, final FieldRendererFactory real, final Boolean isConst, final Boolean isSet) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: aastore        
        //     1: fstore_0        /* this */
        //     2: ddiv           
        //     3: ldiv           
        //     4: irem           
        //     5: ddiv           
        //     6: fdiv           
        //     7: lsub           
        //     8: fdiv           
        //     9: ineg           
        //    10: istore_0        /* this */
        //    11: dload_3         /* _javadoc */
        //    12: astore_1        /* loc */
        //    13: dadd           
        //    14: ddiv           
        //    15: ldiv           
        //    16: laload         
        //    17: drem           
        //    18: lneg           
        //    19: fdiv           
        //    20: laload         
        //    21: ineg           
        //    22: ddiv           
        //    23: ddiv           
        //    24: idiv           
        //    25: drem           
        //    26: laload         
        //    27: ishl           
        //    28: fmul           
        //    29: dadd           
        //    30: laload         
        //    31: frem           
        //    32: lsub           
        //    33: ladd           
        //    34: isub           
        //    35: lsub           
        //    36: frem           
        //    37: laload         
        //    38: ishl           
        //    39: ldiv           
        //    40: idiv           
        //    41: drem           
        //    42: dadd           
        //    43: imul           
        //    44: lsub           
        //    45: ldiv           
        //    46: ladd           
        //    47: laload         
        //    48: fadd           
        //    49: lmul           
        //    50: fdiv           
        //    51: isub           
        //    52: lmul           
        //    53: fdiv           
        //    54: fsub           
        //    55: ddiv           
        //    56: laload         
        //    57: lstore_3        /* _javadoc */
        //    LocalVariableTable:
        //  Start  Length  Slot  Name       Signature
        //  -----  ------  ----  ---------  ----------------------------------------------------------
        //  0      58      0     this       Lcom/sun/tools/xjc/reader/xmlschema/bindinfo/BIProperty;
        //  0      58      1     loc        Lorg/xml/sax/Locator;
        //  0      58      2     _propName  Ljava/lang/String;
        //  0      58      3     _javadoc   Ljava/lang/String;
        //  0      58      4     _baseType  Lcom/sun/codemodel/JType;
        //  0      58      5     _conv      Lcom/sun/tools/xjc/reader/xmlschema/bindinfo/BIConversion;
        //  0      58      6     real       Lcom/sun/tools/xjc/generator/field/FieldRendererFactory;
        //  0      58      7     isConst    Ljava/lang/Boolean;
        //  0      58      8     isSet      Ljava/lang/Boolean;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public void setParent(final BindInfo parent) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: frem           
        //     1: ddiv           
        //     2: irem           
        //     3: lsub           
        //     4: frem           
        //     5: ineg           
        //     6: lshl           
        //     7: istore_0        /* this */
        //     8: aconst_null    
        //     9: nop            
        //    10: iconst_3       
        //    11: dadd           
        //    12: ddiv           
        //    13: fdiv           
        //    14: dadd           
        //    15: ladd           
        //    16: ineg           
        //    17: aconst_null    
        //    18: nop            
        //    19: fstore          40
        //    LocalVariableTable:
        //  Start  Length  Slot  Name    Signature
        //  -----  ------  ----  ------  --------------------------------------------------------
        //  0      21      0     this    Lcom/sun/tools/xjc/reader/xmlschema/bindinfo/BIProperty;
        //  0      21      1     parent  Lcom/sun/tools/xjc/reader/xmlschema/bindinfo/BindInfo;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public String getPropertyName(final boolean forConstant) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: dload_2        
        //     1: dload_3        
        //     2: astore_1        /* forConstant */
        //     3: fmul           
        //     4: ladd           
        //     5: fneg           
        //     6: ladd           
        //     7: ishl           
        //     8: laload         
        //     9: ishl           
        //    10: ldiv           
        //    11: idiv           
        //    12: laload         
        //    13: fdiv           
        //    14: ladd           
        //    15: ldiv           
        //    16: lsub           
        //    17: drem           
        //    18: irem           
        //    19: ladd           
        //    20: dadd           
        //    21: lsub           
        //    22: laload         
        //    23: fastore        
        //    24: astore_3       
        //    25: ladd           
        //    26: ldiv           
        //    27: lsub           
        //    28: istore_0        /* this */
        //    29: aconst_null    
        //    30: nop            
        //    31: iconst_4       
        //    32: dsub           
        //    33: lsub           
        //    34: ineg           
        //    35: fstore_0        /* this */
        //    36: ddiv           
        //    37: fdiv           
        //    38: fneg           
        //    39: aconst_null    
        //    40: nop            
        //    41: istore_1        /* forConstant */
        //    42: dload_2         /* gb */
        //    43: dload_3        
        //    44: astore_1        /* forConstant */
        //    45: dadd           
        //    46: ddiv           
        //    47: ldiv           
        //    48: laload         
        //    49: drem           
        //    50: lneg           
        //    51: fdiv           
        //    52: laload         
        //    53: ineg           
        //    54: ddiv           
        //    55: ddiv           
        //    56: idiv           
        //    57: drem           
        //    58: laload         
        //    59: ishl           
        //    60: fmul           
        //    61: dadd           
        //    LocalVariableTable:
        //  Start  Length  Slot  Name         Signature
        //  -----  ------  ----  -----------  -------------------------------------------------------------
        //  15     30      2     gb           Lcom/sun/tools/xjc/reader/xmlschema/bindinfo/BIGlobalBinding;
        //  0      62      0     this         Lcom/sun/tools/xjc/reader/xmlschema/bindinfo/BIProperty;
        //  0      62      1     forConstant  Z
        //  50     12      2     next         Lcom/sun/tools/xjc/reader/xmlschema/bindinfo/BIProperty;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public String getJavadoc() {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: idiv           
        //     1: dastore        
        //     2: ddiv           
        //     3: ddiv           
        //     4: ineg           
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  --------------------------------------------------------
        //  0      5       0     this  Lcom/sun/tools/xjc/reader/xmlschema/bindinfo/BIProperty;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public JType getBaseType() {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: if_acmpne       167
        //     3: aconst_null    
        //     4: nop            
        //     5: fstore          99
        //     7: ddiv           
        //     8: ldiv           
        //     9: laload         
        //    10: drem           
        //    11: lneg           
        //    12: fdiv           
        //    13: laload         
        //    14: ineg           
        //    15: ddiv           
        //    16: ddiv           
        //    17: idiv           
        //    18: drem           
        //    19: laload         
        //    20: ishl           
        //    21: fmul           
        //    22: dadd           
        //    23: laload         
        //    24: frem           
        //    25: lsub           
        //    26: ladd           
        //    27: isub           
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  --------------------------------------------------------
        //  0      28      0     this  Lcom/sun/tools/xjc/reader/xmlschema/bindinfo/BIProperty;
        //  17     11      1     next  Lcom/sun/tools/xjc/reader/xmlschema/bindinfo/BIProperty;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public FieldRendererFactory getRealization() {
        // 
        // This method could not be decompiled.
        // 
        // Could not show original bytecode, likely due to the same error.
        // 
        // The error that occurred was:
        // 
        // com.strobel.assembler.metadata.MethodBodyParseException: An error occurred while parsing the bytecode of method 'com/sun/tools/xjc/reader/xmlschema/bindinfo/BIProperty.getRealization:()Lcom/sun/tools/xjc/generator/field/FieldRendererFactory;'.
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
    
    public boolean needIsSetMethod() {
        // 
        // This method could not be decompiled.
        // 
        // Could not show original bytecode, likely due to the same error.
        // 
        // The error that occurred was:
        // 
        // com.strobel.assembler.metadata.MethodBodyParseException: An error occurred while parsing the bytecode of method 'com/sun/tools/xjc/reader/xmlschema/bindinfo/BIProperty.needIsSetMethod:()Z'.
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
    
    public boolean isConstantProperty() {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: drem           
        //     1: lneg           
        //     2: fdiv           
        //     3: laload         
        //     4: ineg           
        //     5: ddiv           
        //     6: ddiv           
        //     7: idiv           
        //     8: drem           
        //     9: laload         
        //    10: ishl           
        //    11: fmul           
        //    12: dadd           
        //    13: laload         
        //    14: ldiv           
        //    15: ddiv           
        //    16: isub           
        //    17: lsub           
        //    18: idiv           
        //    19: laload         
        //    20: fstore_0        /* this */
        //    21: sastore        
        //    22: ladd           
        //    23: idiv           
        //    24: lneg           
        //    25: lsub           
        //    26: lastore        
        //    27: frem           
        //    28: ddiv           
        //    29: irem           
        //    30: lsub           
        //    31: frem           
        //    32: ineg           
        //    33: lshl           
        //    34: dstore_2       
        //    35: fdiv           
        //    36: fsub           
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  --------------------------------------------------------
        //  0      37      0     this  Lcom/sun/tools/xjc/reader/xmlschema/bindinfo/BIProperty;
        //  20     17      1     next  Lcom/sun/tools/xjc/reader/xmlschema/bindinfo/BIProperty;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public FieldItem createFieldItem(final String defaultName, final boolean forConstant, final Expression body, final XSComponent source) {
        // 
        // This method could not be decompiled.
        // 
        // Could not show original bytecode, likely due to the same error.
        // 
        // The error that occurred was:
        // 
        // com.strobel.assembler.metadata.MethodBodyParseException: An error occurred while parsing the bytecode of method 'com/sun/tools/xjc/reader/xmlschema/bindinfo/BIProperty.createFieldItem:(Ljava/lang/String;ZLcom/sun/msv/grammar/Expression;Lcom/sun/xml/xsom/XSComponent;)Lcom/sun/tools/xjc/grammar/FieldItem;'.
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
        // Caused by: java.lang.IllegalArgumentException: Argument 'index' must be in the range [0, 280], but value was: 3073.
        //     at com.strobel.core.VerifyArgument.inRange(VerifyArgument.java:346)
        //     at com.strobel.assembler.ir.ConstantPool.get(ConstantPool.java:78)
        //     at com.strobel.assembler.metadata.ClassFileReader$Scope.lookupConstant(ClassFileReader.java:1313)
        //     at com.strobel.assembler.metadata.MethodReader.readBodyCore(MethodReader.java:293)
        //     at com.strobel.assembler.metadata.MethodReader.readBody(MethodReader.java:62)
        //     ... 16 more
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public void markAsAcknowledged() {
        // 
        // This method could not be decompiled.
        // 
        // Could not show original bytecode, likely due to the same error.
        // 
        // The error that occurred was:
        // 
        // com.strobel.assembler.metadata.MethodBodyParseException: An error occurred while parsing the bytecode of method 'com/sun/tools/xjc/reader/xmlschema/bindinfo/BIProperty.markAsAcknowledged:()V'.
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
    
    private void constantPropertyErrorCheck() {
        // 
        // This method could not be decompiled.
        // 
        // Could not show original bytecode, likely due to the same error.
        // 
        // The error that occurred was:
        // 
        // com.strobel.assembler.metadata.MethodBodyParseException: An error occurred while parsing the bytecode of method 'com/sun/tools/xjc/reader/xmlschema/bindinfo/BIProperty.constantPropertyErrorCheck:()V'.
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
        // Caused by: java.lang.IllegalArgumentException: Argument 'index' must be in the range [0, 280], but value was: 565.
        //     at com.strobel.core.VerifyArgument.inRange(VerifyArgument.java:346)
        //     at com.strobel.assembler.ir.ConstantPool.getEntry(ConstantPool.java:66)
        //     at com.strobel.assembler.metadata.ClassFileReader$Scope.lookupField(ClassFileReader.java:1233)
        //     at com.strobel.assembler.metadata.MethodReader.readBodyCore(MethodReader.java:214)
        //     at com.strobel.assembler.metadata.MethodReader.readBody(MethodReader.java:62)
        //     ... 16 more
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    protected BIProperty getDefault() {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: iconst_m1      
        //     1: dstore_2       
        //     2: fconst_1       
        //     3: iconst_m1      
        //     4: dstore_3       
        //     5: iconst_m1      
        //     6: astore_0        /* this */
        //     7: aconst_null    
        //     8: nop            
        //     9: bipush          106
        //    11: ladd           
        //    12: fneg           
        //    13: ladd           
        //    14: laload         
        //    15: idiv           
        //    16: ladd           
        //    17: fdiv           
        //    18: dsub           
        //    19: laload         
        //    20: iastore        
        //    21: fadd           
        //    22: fmul           
        //    23: lsub           
        //    24: dadd           
        //    25: ineg           
        //    26: fconst_1       
        //    27: iconst_m1      
        //    28: astore_1        /* next */
        //    29: iconst_m1      
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  --------------------------------------------------------
        //  0      30      0     this  Lcom/sun/tools/xjc/reader/xmlschema/bindinfo/BIProperty;
        //  21     9       1     next  Lcom/sun/tools/xjc/reader/xmlschema/bindinfo/BIProperty;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    private static BIProperty getDefault(final BGMBuilder builder, final XSComponent c) {
        // 
        // This method could not be decompiled.
        // 
        // Could not show original bytecode, likely due to the same error.
        // 
        // The error that occurred was:
        // 
        // com.strobel.assembler.metadata.MethodBodyParseException: An error occurred while parsing the bytecode of method 'com/sun/tools/xjc/reader/xmlschema/bindinfo/BIProperty.getDefault:(Lcom/sun/tools/xjc/reader/xmlschema/BGMBuilder;Lcom/sun/xml/xsom/XSComponent;)Lcom/sun/tools/xjc/reader/xmlschema/bindinfo/BIProperty;'.
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
    
    public static BIProperty getCustomization(final BGMBuilder builder, final XSComponent c) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: ddiv           
        //     1: laload         
        //     2: lstore_3       
        //     3: dstore_2       
        //     4: lastore        
        //     5: frem           
        //     6: ddiv           
        //     7: irem           
        //     8: lsub           
        //     9: frem           
        //    10: ineg           
        //    11: lshl           
        //    12: fload_2        
        //    13: aaload         
        //    14: aconst_null    
        //    15: nop            
        //    16: aload           106
        //    18: ladd           
        //    19: fneg           
        //    20: ladd           
        //    21: ishl           
        //    22: laload         
        //    23: ishl           
        //    24: ldiv           
        //    25: idiv           
        //    26: laload         
        //    27: fdiv           
        //    28: ladd           
        //    29: ldiv           
        //    30: lsub           
        //    LocalVariableTable:
        //  Start  Length  Slot  Name     Signature
        //  -----  ------  ----  -------  --------------------------------------------------------
        //  19     6       2     prop     Lcom/sun/tools/xjc/reader/xmlschema/bindinfo/BIProperty;
        //  0      31      0     builder  Lcom/sun/tools/xjc/reader/xmlschema/BGMBuilder;
        //  0      31      1     c        Lcom/sun/xml/xsom/XSComponent;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    private static String concat(final String s1, final String s2) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: fadd           
        //     1: lmul           
        //     2: fdiv           
        //     3: isub           
        //     4: lmul           
        //     5: fdiv           
        //     6: fsub           
        //     7: ddiv           
        //     8: laload         
        //     9: lstore_2       
        //    10: fadd           
        //    11: drem           
        //    12: ineg           
        //    13: frem           
        //    14: ladd           
        //    15: dadd           
        //    16: ineg           
        //    17: fstore_1        /* s2 */
        //    18: lsub           
        //    19: dadd           
        //    20: idiv           
        //    21: ladd           
        //    22: frem           
        //    23: ladd           
        //    24: ineg           
        //    25: lmul           
        //    26: ddiv           
        //    27: fdiv           
        //    28: dstore_2       
        //    29: ldiv           
        //    30: irem           
        //    31: idiv           
        //    32: aconst_null    
        //    33: nop            
        //    34: fstore          99
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  ------------------
        //  0      36      0     s1    Ljava/lang/String;
        //  0      36      1     s2    Ljava/lang/String;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public QName getName() {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: dadd           
        //     1: laload         
        //     2: frem           
        //     3: lsub           
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  --------------------------------------------------------
        //  0      4       0     this  Lcom/sun/tools/xjc/reader/xmlschema/bindinfo/BIProperty;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    static {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: ishl           
        //     1: ldiv           
        //     2: idiv           
        //     3: laload         
        //     4: drem           
        //     5: ladd           
        //     6: ishl           
        //     7: laload         
        //     8: astore_1       
        //     9: ddiv           
        //    10: dadd           
        //    11: ladd           
        //    12: ineg           
        //    13: ddiv           
        //    14: frem           
        //    15: istore_0       
        //    16: dload_3        
        //    17: sastore        
        //    18: aconst_null    
        //    19: nop            
        //    20: istore_0       
        //    21: dload_2        
        //    22: astore_1       
        //    23: dadd           
        //    24: ddiv           
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
}
