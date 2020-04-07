// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.reader.xmlschema.bindinfo;

import com.sun.xml.xsom.XSDeclaration;
import com.sun.xml.xsom.XSSchemaSet;
import org.xml.sax.Locator;
import com.sun.tools.xjc.generator.field.FieldRendererFactory;
import com.sun.codemodel.JCodeModel;
import javax.xml.namespace.QName;
import java.util.Map;
import java.util.Set;
import com.sun.tools.xjc.reader.NameConverter;

public final class BIGlobalBinding extends AbstractDeclarationImpl
{
    private final NameConverter nameConverter;
    private final boolean enableJavaNamingConvention;
    private final boolean modelGroupBinding;
    private final BIProperty property;
    private final boolean generateEnumMemberName;
    private final boolean choiceContentPropertyWithModelGroupBinding;
    private final Set enumBaseTypes;
    private final BIXSerializable serializable;
    private final BIXSuperClass superClass;
    public final boolean smartWildcardDefaultBinding;
    private final boolean enableTypeSubstitutionSupport;
    private final Map globalConversions;
    public static final QName NAME;
    
    private static Set createSet() {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: laload         
        //     1: ishl           
        //     2: ldiv           
        //     3: idiv           
        //     4: laload         
        //     5: ishl           
        //     6: drem           
        //     7: ddiv           
        //     8: ldiv           
        //     9: laload         
        //    10: pop2           
        //    11: aastore        
        //    12: aastore        
        //    13: dadd           
        //    14: imul           
        //    15: lsub           
        //    16: ldiv           
        //    17: ladd           
        //    18: aastore        
        //    19: lsub           
        //    20: ineg           
        //    21: fconst_1       
        //    22: aconst_null    
        //    23: f2d            
        //    24: aconst_null    
        //    25: d2i            
        //    26: fconst_1       
        //    27: aconst_null    
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  ---------------
        //  8      20      0     s     Ljava/util/Set;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public BIGlobalBinding(final JCodeModel codeModel) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: aconst_null    
        //     1: ifle            3074
        //     4: if_icmpeq       157
        //     7: fconst_1       
        //     8: aconst_null    
        //     9: if_icmpne       162
        //    12: fconst_1       
        //    13: nop            
        //    14: dcmpg          
        //    15: nop            
        //    16: ifeq            1809
        //    19: if_icmplt       3092
        //    22: if_icmpge       441
        //    25: fconst_1       
        //    26: nop            
        //    27: fcmpl          
        //    28: nop            
        //    29: fcmpg          
        //    30: fconst_1       
        //    LocalVariableTable:
        //  Start  Length  Slot  Name       Signature
        //  -----  ------  ----  ---------  -------------------------------------------------------------
        //  0      31      0     this       Lcom/sun/tools/xjc/reader/xmlschema/bindinfo/BIGlobalBinding;
        //  0      31      1     codeModel  Lcom/sun/codemodel/JCodeModel;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public BIGlobalBinding(final JCodeModel codeModel, final Map _globalConvs, final NameConverter nconv, final boolean _modelGroupBinding, final boolean _choiceContentPropertyWithModelGroupBinding, final boolean _enableJavaNamingConvention, final boolean _fixedAttrToConstantProperty, final boolean _needIsSetMethod, final boolean _generateEnumMemberName, final Set _enumBaseTypes, final FieldRendererFactory collectionFieldRenderer, final BIXSerializable _serializable, final BIXSuperClass _superClass, final boolean _enableTypeSubstitutionSupport, final boolean _smartWildcardDefaultBinding, final Locator _loc) {
        // 
        // This method could not be decompiled.
        // 
        // Could not show original bytecode, likely due to the same error.
        // 
        // The error that occurred was:
        // 
        // com.strobel.assembler.metadata.MethodBodyParseException: An error occurred while parsing the bytecode of method 'com/sun/tools/xjc/reader/xmlschema/bindinfo/BIGlobalBinding.<init>:(Lcom/sun/codemodel/JCodeModel;Ljava/util/Map;Lcom/sun/tools/xjc/reader/NameConverter;ZZZZZZLjava/util/Set;Lcom/sun/tools/xjc/generator/field/FieldRendererFactory;Lcom/sun/tools/xjc/reader/xmlschema/bindinfo/BIXSerializable;Lcom/sun/tools/xjc/reader/xmlschema/bindinfo/BIXSuperClass;ZZLorg/xml/sax/Locator;)V'.
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
        // Caused by: java.lang.ClassCastException: com.strobel.assembler.ir.ConstantPool$Utf8StringConstantEntry cannot be cast to com.strobel.assembler.ir.ConstantPool$FieldReferenceEntry
        //     at com.strobel.assembler.metadata.ClassFileReader$Scope.lookupField(ClassFileReader.java:1233)
        //     at com.strobel.assembler.metadata.MethodReader.readBodyCore(MethodReader.java:214)
        //     at com.strobel.assembler.metadata.MethodReader.readBody(MethodReader.java:62)
        //     ... 16 more
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public NameConverter getNameConverter() {
        // 
        // This method could not be decompiled.
        // 
        // Could not show original bytecode, likely due to the same error.
        // 
        // The error that occurred was:
        // 
        // com.strobel.assembler.metadata.MethodBodyParseException: An error occurred while parsing the bytecode of method 'com/sun/tools/xjc/reader/xmlschema/bindinfo/BIGlobalBinding.getNameConverter:()Lcom/sun/tools/xjc/reader/NameConverter;'.
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
    
    boolean isJavaNamingConventionEnabled() {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     1: ddiv           
        //     2: ldiv           
        //     3: laload         
        //     4: drem           
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  -------------------------------------------------------------
        //  0      5       0     this  Lcom/sun/tools/xjc/reader/xmlschema/bindinfo/BIGlobalBinding;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public boolean isModelGroupBinding() {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: fstore_0        /* this */
        //     1: dadd           
        //     2: ddiv           
        //     3: ldiv           
        //     4: laload         
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  -------------------------------------------------------------
        //  0      5       0     this  Lcom/sun/tools/xjc/reader/xmlschema/bindinfo/BIGlobalBinding;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public boolean isChoiceContentPropertyModelGroupBinding() {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: lmul           
        //     1: ddiv           
        //     2: fdiv           
        //     3: dstore_2       
        //     4: ldiv           
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  -------------------------------------------------------------
        //  0      5       0     this  Lcom/sun/tools/xjc/reader/xmlschema/bindinfo/BIGlobalBinding;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public boolean isTypeSubstitutionSupportEnabled() {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: idiv           
        //     1: lstore_3       
        //     2: lmul           
        //     3: fdiv           
        //     4: isub           
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  -------------------------------------------------------------
        //  0      5       0     this  Lcom/sun/tools/xjc/reader/xmlschema/bindinfo/BIGlobalBinding;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public BIProperty getDefaultProperty() {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: idiv           
        //     1: drem           
        //     2: dadd           
        //     3: imul           
        //     4: lsub           
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  -------------------------------------------------------------
        //  0      5       0     this  Lcom/sun/tools/xjc/reader/xmlschema/bindinfo/BIGlobalBinding;
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
        //     0: ddiv           
        //     1: ldiv           
        //     2: laload         
        //     3: drem           
        //     4: lneg           
        //     5: fdiv           
        //     6: laload         
        //     7: ineg           
        //     8: ddiv           
        //     9: ddiv           
        //    10: idiv           
        //    11: drem           
        //    12: laload         
        //    13: ishl           
        //    LocalVariableTable:
        //  Start  Length  Slot  Name    Signature
        //  -----  ------  ----  ------  -------------------------------------------------------------
        //  0      14      0     this    Lcom/sun/tools/xjc/reader/xmlschema/bindinfo/BIGlobalBinding;
        //  0      14      1     parent  Lcom/sun/tools/xjc/reader/xmlschema/bindinfo/BindInfo;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public void dispatchGlobalConversions(final XSSchemaSet schema) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: ddiv           
        //     1: idiv           
        //     2: drem           
        //     3: laload         
        //     4: ishl           
        //     5: fmul           
        //     6: dadd           
        //     7: laload         
        //     8: frem           
        //     9: lsub           
        //    10: ladd           
        //    11: isub           
        //    12: lsub           
        //    13: frem           
        //    14: laload         
        //    15: ishl           
        //    16: ldiv           
        //    17: idiv           
        //    18: drem           
        //    19: dadd           
        //    20: imul           
        //    21: lsub           
        //    22: ldiv           
        //    23: ladd           
        //    24: laload         
        //    25: fadd           
        //    26: lmul           
        //    27: fdiv           
        //    28: isub           
        //    29: lmul           
        //    30: fdiv           
        //    31: fsub           
        //    32: ddiv           
        //    33: laload         
        //    34: lstore_3        /* e */
        //    35: dstore_2        /* itr */
        //    36: dstore_0        /* this */
        //    37: idiv           
        //    38: ddiv           
        //    39: fadd           
        //    40: ladd           
        //    41: idiv           
        //    42: lstore_3        /* e */
        //    43: lmul           
        //    44: fdiv           
        //    45: isub           
        //    46: lmul           
        //    47: fdiv           
        //    48: dsub           
        //    49: fload_2         /* itr */
        //    50: fstore_0        /* this */
        //    51: idiv           
        //    52: ladd           
        //    53: drem           
        //    54: drem           
        //    55: astore_3        /* e */
        //    56: ladd           
        //    57: ldiv           
        //    58: lsub           
        //    59: lstore_3        /* e */
        //    60: lsub           
        //    61: ladd           
        //    62: fdiv           
        //    63: aconst_null    
        //    64: nop            
        //    65: aastore        
        //    66: dadd           
        //    67: ddiv           
        //    68: ldiv           
        //    69: laload         
        //    70: drem           
        //    71: lneg           
        //    72: fdiv           
        //    73: laload         
        //    74: ineg           
        //    75: ddiv           
        //    76: ddiv           
        //    77: idiv           
        //    78: drem           
        //    79: laload         
        //    80: ishl           
        //    81: fmul           
        //    82: dadd           
        //    83: laload         
        //    84: frem           
        //    85: lsub           
        //    86: ladd           
        //    87: isub           
        //    88: lsub           
        //    89: frem           
        //    90: laload         
        //    91: ishl           
        //    92: ldiv           
        //    93: idiv           
        //    94: drem           
        //    95: dadd           
        //    96: imul           
        //    97: lsub           
        //    98: ldiv           
        //    99: ladd           
        //   100: laload         
        //   101: fadd           
        //   102: lmul           
        //   103: fdiv           
        //   104: isub           
        //   105: lmul           
        //   106: fdiv           
        //   107: fsub           
        //   108: ddiv           
        //   109: laload         
        //   110: lstore_3        /* e */
        //   111: dstore_2        /* itr */
        //   112: dstore_0        /* this */
        //   113: idiv           
        //   114: ddiv           
        //   115: fadd           
        //   116: ladd           
        //   117: idiv           
        //   118: lstore_3       
        //   119: lmul           
        //   120: fdiv           
        //    LocalVariableTable:
        //  Start  Length  Slot  Name    Signature
        //  -----  ------  ----  ------  -------------------------------------------------------------
        //  34     83      3     e       Ljava/util/Map$Entry;
        //  45     72      4     name    Ljavax/xml/namespace/QName;
        //  56     61      5     conv    Lcom/sun/tools/xjc/reader/xmlschema/bindinfo/BIConversion;
        //  74     43      6     st      Lcom/sun/xml/xsom/XSSimpleType;
        //  15     105     2     itr     Ljava/util/Iterator;
        //  0      121     0     this    Lcom/sun/tools/xjc/reader/xmlschema/bindinfo/BIGlobalBinding;
        //  0      121     1     schema  Lcom/sun/xml/xsom/XSSchemaSet;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public boolean canBeMappedToTypeSafeEnum(final QName typeName) {
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
        //     6: dadd           
        //     7: ddiv           
        //     8: isub           
        //     9: lsub           
        //    10: ldiv           
        //    LocalVariableTable:
        //  Start  Length  Slot  Name      Signature
        //  -----  ------  ----  --------  -------------------------------------------------------------
        //  0      11      0     this      Lcom/sun/tools/xjc/reader/xmlschema/bindinfo/BIGlobalBinding;
        //  0      11      1     typeName  Ljavax/xml/namespace/QName;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public boolean canBeMappedToTypeSafeEnum(final String nsUri, final String localName) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: lneg           
        //     1: fdiv           
        //     2: laload         
        //     3: dadd           
        //     4: ddiv           
        //     5: isub           
        //     6: lsub           
        //     7: ldiv           
        //     8: ddiv           
        //     9: isub           
        //    10: lsub           
        //    11: idiv           
        //    12: laload         
        //    13: dstore_3       
        //    LocalVariableTable:
        //  Start  Length  Slot  Name       Signature
        //  -----  ------  ----  ---------  -------------------------------------------------------------
        //  0      14      0     this       Lcom/sun/tools/xjc/reader/xmlschema/bindinfo/BIGlobalBinding;
        //  0      14      1     nsUri      Ljava/lang/String;
        //  0      14      2     localName  Ljava/lang/String;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public boolean canBeMappedToTypeSafeEnum(final XSDeclaration decl) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: dsub           
        //     1: laload         
        //     2: fstore_0        /* this */
        //     3: idiv           
        //     4: ladd           
        //     5: drem           
        //     6: drem           
        //     7: istore_0        /* this */
        //     8: dload_3        
        //     9: astore_1        /* decl */
        //    10: fmul           
        //    11: ladd           
        //    12: fneg           
        //    13: ladd           
        //    14: laload         
        //    15: idiv           
        //    16: ladd           
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  -------------------------------------------------------------
        //  0      17      0     this  Lcom/sun/tools/xjc/reader/xmlschema/bindinfo/BIGlobalBinding;
        //  0      17      1     decl  Lcom/sun/xml/xsom/XSDeclaration;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public boolean needsToGenerateEnumMemberName() {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: ladd           
        //     1: fneg           
        //     2: ladd           
        //     3: laload         
        //     4: lneg           
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  -------------------------------------------------------------
        //  0      5       0     this  Lcom/sun/tools/xjc/reader/xmlschema/bindinfo/BIGlobalBinding;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public BIXSerializable getSerializableExtension() {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: lsub           
        //     1: ineg           
        //     2: astore_3       
        //     3: ladd           
        //     4: ldiv           
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  -------------------------------------------------------------
        //  0      5       0     this  Lcom/sun/tools/xjc/reader/xmlschema/bindinfo/BIGlobalBinding;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public BIXSuperClass getSuperClassExtension() {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: irem           
        //     1: idiv           
        //     2: lsub           
        //     3: bastore        
        //     4: lshl           
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  -------------------------------------------------------------
        //  0      5       0     this  Lcom/sun/tools/xjc/reader/xmlschema/bindinfo/BIGlobalBinding;
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
        //     0: ishl           
        //     1: drem           
        //     2: ddiv           
        //     3: ldiv           
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  -------------------------------------------------------------
        //  0      4       0     this  Lcom/sun/tools/xjc/reader/xmlschema/bindinfo/BIGlobalBinding;
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
        //     0: ddiv           
        //     1: ddiv           
        //     2: idiv           
        //     3: drem           
        //     4: laload         
        //     5: ishl           
        //     6: fmul           
        //     7: dadd           
        //     8: laload         
        //     9: frem           
        //    10: lsub           
        //    11: ladd           
        //    12: isub           
        //    13: lsub           
        //    14: frem           
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
}
