// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.reader.xmlschema.bindinfo;

import javax.xml.bind.annotation.XmlAttribute;
import com.sun.xml.xsom.XSComponent;
import org.xml.sax.Locator;
import javax.xml.namespace.QName;

public final class BISchemaBinding extends AbstractDeclarationImpl
{
    private final NamingRule typeNamingRule;
    private final NamingRule elementNamingRule;
    private final NamingRule attributeNamingRule;
    private final NamingRule modelGroupNamingRule;
    private final NamingRule anonymousTypeNamingRule;
    private String packageName;
    private final String javadoc;
    private static final NamingRule defaultNamingRule;
    public static final QName NAME;
    
    public BISchemaBinding(final String _packageName, final String _javadoc, final NamingRule rType, final NamingRule rElement, final NamingRule rAttribute, final NamingRule rModelGroup, final NamingRule rAnonymousType, final Locator _loc) {
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
        //    15: istore_0        /* this */
        //    16: dload_3         /* rType */
        //    17: sastore        
        //    18: nop            
        //    19: daload         
        //    20: nop            
        //    21: lload_1         /* _packageName */
        //    22: nop            
        //    23: lload_2         /* _javadoc */
        //    24: nop            
        //    25: nop            
        //    26: nop            
        //    27: iconst_2       
        //    28: nop            
        //    29: iconst_m1      
        //    30: nop            
        //    31: dload_1         /* _packageName */
        //    32: nop            
        //    33: dload_2         /* _javadoc */
        //    34: nop            
        //    35: aconst_null    
        //    36: nop            
        //    37: dload_3         /* rType */
        //    38: nop            
        //    39: nop            
        //    40: nop            
        //    41: iconst_3       
        //    42: nop            
        //    43: aconst_null    
        //    44: nop            
        //    45: aload_0         /* this */
        //    46: nop            
        //    47: nop            
        //    48: nop            
        //    49: iconst_m1      
        //    50: nop            
        //    51: aload_1         /* _packageName */
        //    52: nop            
        //    53: aload_2         /* _javadoc */
        //    54: nop            
        //    55: aconst_null    
        //    56: nop            
        //    57: dload_3         /* rType */
        //    58: nop            
        //    59: nop            
        //    60: nop            
        //    61: fconst_0       
        //    62: nop            
        //    63: aconst_null    
        //    64: nop            
        //    65: aload_0         /* this */
        //    66: nop            
        //    67: aconst_null    
        //    68: nop            
        //    69: aload_3         /* rType */
        //    70: drem           
        //    71: nop            
        //    72: iaload         
        //    73: nop            
        //    74: aconst_null    
        //    75: nop            
        //    76: laload         
        //    77: nop            
        //    78: faload         
        //    79: nop            
        //    80: aconst_null    
        //    81: nop            
        //    82: dload_3         /* rType */
        //    83: nop            
        //    84: nop            
        //    85: nop            
        //    86: fconst_0       
        //    87: nop            
        //    88: aconst_null    
        //    89: nop            
        //    90: daload         
        //    91: nop            
        //    92: aconst_null    
        //    93: nop            
        //    94: aload_3         /* rType */
        //    95: drem           
        //    96: nop            
        //    97: laload         
        //    98: nop            
        //    99: iload_0         /* this */
        //   100: nop            
        //   101: aaload         
        //   102: nop            
        //   103: baload         
        //   104: nop            
        //   105: nop            
        //    LocalVariableTable:
        //  Start  Length  Slot  Name            Signature
        //  -----  ------  ----  --------------  ------------------------------------------------------------------------
        //  0      106     0     this            Lcom/sun/tools/xjc/reader/xmlschema/bindinfo/BISchemaBinding;
        //  0      106     1     _packageName    Ljava/lang/String;
        //  0      106     2     _javadoc        Ljava/lang/String;
        //  0      106     3     rType           Lcom/sun/tools/xjc/reader/xmlschema/bindinfo/BISchemaBinding$NamingRule;
        //  0      106     4     rElement        Lcom/sun/tools/xjc/reader/xmlschema/bindinfo/BISchemaBinding$NamingRule;
        //  0      106     5     rAttribute      Lcom/sun/tools/xjc/reader/xmlschema/bindinfo/BISchemaBinding$NamingRule;
        //  0      106     6     rModelGroup     Lcom/sun/tools/xjc/reader/xmlschema/bindinfo/BISchemaBinding$NamingRule;
        //  0      106     7     rAnonymousType  Lcom/sun/tools/xjc/reader/xmlschema/bindinfo/BISchemaBinding$NamingRule;
        //  0      106     8     _loc            Lorg/xml/sax/Locator;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public String mangleClassName(final String name, final XSComponent cmp) {
        // 
        // This method could not be decompiled.
        // 
        // Could not show original bytecode, likely due to the same error.
        // 
        // The error that occurred was:
        // 
        // com.strobel.assembler.metadata.MethodBodyParseException: An error occurred while parsing the bytecode of method 'com/sun/tools/xjc/reader/xmlschema/bindinfo/BISchemaBinding.mangleClassName:(Ljava/lang/String;Lcom/sun/xml/xsom/XSComponent;)Ljava/lang/String;'.
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
        // Caused by: java.lang.ClassCastException: com.strobel.assembler.ir.ConstantPool$TypeInfoEntry cannot be cast to com.strobel.assembler.ir.ConstantPool$FieldReferenceEntry
        //     at com.strobel.assembler.metadata.ClassFileReader$Scope.lookupField(ClassFileReader.java:1233)
        //     at com.strobel.assembler.metadata.MethodReader.readBodyCore(MethodReader.java:214)
        //     at com.strobel.assembler.metadata.MethodReader.readBody(MethodReader.java:62)
        //     ... 16 more
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public String mangleAnonymousTypeClassName(final String name) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: nop            
        //     1: istore_1        /* name */
        //     2: nop            
        //     3: nop            
        //     4: nop            
        //     5: nop            
        //     6: nop            
        //     7: fconst_1       
        //     8: nop            
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  -------------------------------------------------------------
        //  0      9       0     this  Lcom/sun/tools/xjc/reader/xmlschema/bindinfo/BISchemaBinding;
        //  0      9       1     name  Ljava/lang/String;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public void setPackageName(final String val) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: istore_1        /* val */
        //     1: nop            
        //     2: nop            
        //     3: nop            
        //     4: aconst_null    
        //     5: nop            
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  -------------------------------------------------------------
        //  0      6       0     this  Lcom/sun/tools/xjc/reader/xmlschema/bindinfo/BISchemaBinding;
        //  0      6       1     val   Ljava/lang/String;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public String getPackageName() {
        // 
        // This method could not be decompiled.
        // 
        // Could not show original bytecode, likely due to the same error.
        // 
        // The error that occurred was:
        // 
        // com.strobel.assembler.metadata.MethodBodyParseException: An error occurred while parsing the bytecode of method 'com/sun/tools/xjc/reader/xmlschema/bindinfo/BISchemaBinding.getPackageName:()Ljava/lang/String;'.
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
        //     at com.strobel.assembler.metadata.Buffer.readUnsignedByte(Buffer.java:214)
        //     at com.strobel.assembler.metadata.MethodReader.readBodyCore(MethodReader.java:412)
        //     at com.strobel.assembler.metadata.MethodReader.readBody(MethodReader.java:62)
        //     ... 16 more
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public String getJavadoc() {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: nop            
        //     1: aconst_null    
        //     2: nop            
        //     3: fstore          this
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  -------------------------------------------------------------
        //  0      5       0     this  Lcom/sun/tools/xjc/reader/xmlschema/bindinfo/BISchemaBinding;
        // 
        // The error that occurred was:
        // 
        // java.lang.NullPointerException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public QName getName() {
        // 
        // This method could not be decompiled.
        // 
        // Could not show original bytecode, likely due to the same error.
        // 
        // The error that occurred was:
        // 
        // com.strobel.assembler.metadata.MethodBodyParseException: An error occurred while parsing the bytecode of method 'com/sun/tools/xjc/reader/xmlschema/bindinfo/BISchemaBinding.getName:()Ljavax/xml/namespace/QName;'.
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
        // Caused by: java.lang.ClassCastException: com.strobel.assembler.ir.ConstantPool$MethodReferenceEntry cannot be cast to com.strobel.assembler.ir.ConstantPool$ConstantEntry
        //     at com.strobel.assembler.ir.ConstantPool.lookupConstant(ConstantPool.java:120)
        //     at com.strobel.assembler.metadata.ClassFileReader$Scope.lookupConstant(ClassFileReader.java:1319)
        //     at com.strobel.assembler.metadata.MethodReader.readBodyCore(MethodReader.java:286)
        //     at com.strobel.assembler.metadata.MethodReader.readBody(MethodReader.java:62)
        //     ... 16 more
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    static {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: nop            
        //     1: fconst_0       
        //     2: nop            
        //     3: aconst_null    
        //     4: nop            
        //     5: astore_3       
        //     6: nop            
        //     7: aconst_null    
        //     8: nop            
        //     9: aload_3        
        //    10: drem           
        //    11: nop            
        //    12: iastore        
        //    13: nop            
        //    14: fload_1        
        //    15: nop            
        //    16: nop            
        //    17: nop            
        //    18: fload_0        
        //    19: nop            
        //    20: iconst_1       
        //    21: nop            
        //    22: lload_3        
        //    23: nop            
        //    24: lload_1        
        //    25: nop            
        //    26: nop            
        //    27: bipush          8
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public static final class NamingRule
    {
        @XmlAttribute
        private String prefix;
        @XmlAttribute
        private String suffix;
        
        public NamingRule(final String _prefix, final String _suffix) {
            this.prefix = "";
            this.suffix = "";
            this.prefix = _prefix;
            this.suffix = _suffix;
        }
        
        public NamingRule() {
            this.prefix = "";
            this.suffix = "";
        }
        
        public String mangle(final String originalName) {
            return this.prefix + originalName + this.suffix;
        }
    }
}
