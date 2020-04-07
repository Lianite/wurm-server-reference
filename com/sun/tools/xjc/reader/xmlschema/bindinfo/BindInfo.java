// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.reader.xmlschema.bindinfo;

import javax.xml.namespace.QName;
import java.util.Vector;
import com.sun.tools.xjc.reader.xmlschema.BGMBuilder;
import com.sun.xml.xsom.XSComponent;
import org.xml.sax.Locator;

public final class BindInfo
{
    private final Locator location;
    private String documentation;
    private boolean _hasTitleInDocumentation;
    private XSComponent owner;
    private BGMBuilder builder;
    private final Vector decls;
    public static final BindInfo empty;
    
    public BindInfo(final Locator loc) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: bastore        
        //     1: frem           
        //     2: ladd           
        //     3: fdiv           
        //     4: drem           
        //     5: fsub           
        //     6: ddiv           
        //     7: frem           
        //     8: ldiv           
        //     9: lsub           
        //    10: frem           
        //    11: fstore_2       
        //    12: ishl           
        //    13: dadd           
        //    14: lsub           
        //    15: irem           
        //    16: ineg           
        //    17: lmul           
        //    18: ddiv           
        //    19: fdiv           
        //    20: istore_0        /* this */
        //    21: aconst_null    
        //    22: nop            
        //    23: aconst_null    
        //    24: ineg           
        //    25: aconst_null    
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  ------------------------------------------------------
        //  0      26      0     this  Lcom/sun/tools/xjc/reader/xmlschema/bindinfo/BindInfo;
        //  0      26      1     loc   Lorg/xml/sax/Locator;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public Locator getSourceLocation() {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: iastore        
        //     1: fadd           
        //     2: fmul           
        //     3: lsub           
        //     4: dadd           
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  ------------------------------------------------------
        //  0      5       0     this  Lcom/sun/tools/xjc/reader/xmlschema/bindinfo/BindInfo;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public void setOwner(final BGMBuilder _builder, final XSComponent _owner) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: fdiv           
        //     1: laload         
        //     2: ineg           
        //     3: ddiv           
        //     4: ddiv           
        //     5: idiv           
        //     6: drem           
        //     7: laload         
        //     8: ishl           
        //     9: fmul           
        //    10: dadd           
        //    LocalVariableTable:
        //  Start  Length  Slot  Name      Signature
        //  -----  ------  ----  --------  ------------------------------------------------------
        //  0      11      0     this      Lcom/sun/tools/xjc/reader/xmlschema/bindinfo/BindInfo;
        //  0      11      1     _builder  Lcom/sun/tools/xjc/reader/xmlschema/BGMBuilder;
        //  0      11      2     _owner    Lcom/sun/xml/xsom/XSComponent;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public XSComponent getOwner() {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: isub           
        //     1: lsub           
        //     2: frem           
        //     3: laload         
        //     4: ishl           
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  ------------------------------------------------------
        //  0      5       0     this  Lcom/sun/tools/xjc/reader/xmlschema/bindinfo/BindInfo;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public BGMBuilder getBuilder() {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: dload_2        
        //     1: dload_3        
        //     2: astore_1       
        //     3: fmul           
        //     4: ladd           
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  ------------------------------------------------------
        //  0      5       0     this  Lcom/sun/tools/xjc/reader/xmlschema/bindinfo/BindInfo;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public void addDecl(final BIDeclaration decl) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: laload         
        //     1: ishl           
        //     2: fmul           
        //     3: dadd           
        //     4: laload         
        //     5: frem           
        //     6: lsub           
        //     7: ladd           
        //     8: isub           
        //     9: lsub           
        //    10: frem           
        //    11: laload         
        //    12: ishl           
        //    13: ldiv           
        //    14: idiv           
        //    15: drem           
        //    16: dadd           
        //    17: imul           
        //    18: lsub           
        //    19: ldiv           
        //    20: ladd           
        //    21: laload         
        //    22: fadd           
        //    23: lmul           
        //    24: fdiv           
        //    25: isub           
        //    26: lmul           
        //    27: fdiv           
        //    28: fsub           
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  -----------------------------------------------------------
        //  0      29      0     this  Lcom/sun/tools/xjc/reader/xmlschema/bindinfo/BindInfo;
        //  0      29      1     decl  Lcom/sun/tools/xjc/reader/xmlschema/bindinfo/BIDeclaration;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public BIDeclaration get(final QName name) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: ishr           
        //     1: ladd           
        //     2: ineg           
        //     3: lmul           
        //     4: ddiv           
        //     5: fdiv           
        //     6: drem           
        //     7: istore_0        /* this */
        //     8: aconst_null    
        //     9: nop            
        //    10: iconst_m1      
        //    11: irem           
        //    12: dadd           
        //    13: aconst_null    
        //    14: nop            
        //    15: fstore_1        /* name */
        //    16: astore_1        /* name */
        //    17: dadd           
        //    18: ddiv           
        //    19: ldiv           
        //    20: laload         
        //    21: drem           
        //    22: lneg           
        //    23: fdiv           
        //    24: laload         
        //    25: ineg           
        //    26: ddiv           
        //    27: ddiv           
        //    28: idiv           
        //    29: drem           
        //    30: laload         
        //    31: ishl           
        //    32: fmul           
        //    33: dadd           
        //    34: laload         
        //    35: frem           
        //    36: lsub           
        //    37: ladd           
        //    38: isub           
        //    39: lsub           
        //    40: frem           
        //    41: laload         
        //    42: ishl           
        //    43: ldiv           
        //    44: idiv           
        //    45: drem           
        //    46: dadd           
        //    47: imul           
        //    48: lsub           
        //    49: ldiv           
        //    50: ladd           
        //    51: laload         
        //    52: fadd           
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  -----------------------------------------------------------
        //  28     17      4     decl  Lcom/sun/tools/xjc/reader/xmlschema/bindinfo/BIDeclaration;
        //  10     41      3     i     I
        //  0      53      0     this  Lcom/sun/tools/xjc/reader/xmlschema/bindinfo/BindInfo;
        //  0      53      1     name  Ljavax/xml/namespace/QName;
        //  8      45      2     len   I
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public BIDeclaration[] getDecls() {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: aaload         
        //     1: laload         
        //     2: frem           
        //     3: lneg           
        //     4: fdiv           
        //     5: ineg           
        //     6: lmul           
        //     7: ldiv           
        //     8: lsub           
        //     9: laload         
        //    10: dstore_3       
        //    11: lstore_2       
        //    12: pop2           
        //    13: lstore_3       
        //    14: fstore_0        /* this */
        //    15: ddiv           
        //    16: fdiv           
        //    17: ineg           
        //    18: lsub           
        //    19: ishl           
        //    20: ineg           
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  ------------------------------------------------------
        //  0      21      0     this  Lcom/sun/tools/xjc/reader/xmlschema/bindinfo/BindInfo;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public boolean hasTitleInDocumentation() {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: ladd           
        //     1: laload         
        //     2: idiv           
        //     3: ladd           
        //     4: fdiv           
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  ------------------------------------------------------
        //  0      5       0     this  Lcom/sun/tools/xjc/reader/xmlschema/bindinfo/BindInfo;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public String getDocumentation() {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: ldiv           
        //     1: idiv           
        //     2: drem           
        //     3: dadd           
        //     4: imul           
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  ------------------------------------------------------
        //  0      5       0     this  Lcom/sun/tools/xjc/reader/xmlschema/bindinfo/BindInfo;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public void appendDocumentation(final String fragment, final boolean hasTitleInDocumentation) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: fneg           
        //     1: ladd           
        //     2: aconst_null    
        //     3: nop            
        //     4: aload_0         /* this */
        //     5: astore_1        /* fragment */
        //     6: fmul           
        //     7: ladd           
        //     8: fneg           
        //     9: ladd           
        //    10: ishl           
        //    11: laload         
        //    12: ishl           
        //    13: ldiv           
        //    14: idiv           
        //    15: laload         
        //    16: fadd           
        //    17: lmul           
        //    18: fdiv           
        //    19: isub           
        //    20: laload         
        //    21: ladd           
        //    22: fdiv           
        //    23: fdiv           
        //    24: ddiv           
        //    25: ineg           
        //    26: ladd           
        //    27: ineg           
        //    28: lmul           
        //    29: ddiv           
        //    30: fdiv           
        //    31: laload         
        //    32: pop2           
        //    33: ldiv           
        //    34: idiv           
        //    35: dastore        
        //    36: ddiv           
        //    37: ddiv           
        //    38: ineg           
        //    39: fstore_2        /* hasTitleInDocumentation */
        //    40: idiv           
        //    41: lsub           
        //    42: ldiv           
        //    43: lsub           
        //    44: fdiv           
        //    45: ineg           
        //    46: istore_0        /* this */
        //    47: aconst_null    
        //    48: nop            
        //    49: iconst_1       
        //    50: fdiv           
        //    LocalVariableTable:
        //  Start  Length  Slot  Name                     Signature
        //  -----  ------  ----  -----------------------  ------------------------------------------------------
        //  0      51      0     this                     Lcom/sun/tools/xjc/reader/xmlschema/bindinfo/BindInfo;
        //  0      51      1     fragment                 Ljava/lang/String;
        //  0      51      2     hasTitleInDocumentation  Z
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public void absorb(final BindInfo bi) {
        // 
        // This method could not be decompiled.
        // 
        // Could not show original bytecode, likely due to the same error.
        // 
        // The error that occurred was:
        // 
        // com.strobel.assembler.metadata.MethodBodyParseException: An error occurred while parsing the bytecode of method 'com/sun/tools/xjc/reader/xmlschema/bindinfo/BindInfo.absorb:(Lcom/sun/tools/xjc/reader/xmlschema/bindinfo/BindInfo;)V'.
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
        // Caused by: java.lang.IllegalArgumentException: Argument 'index' must be in the range [0, 140], but value was: 190.
        //     at com.strobel.core.VerifyArgument.inRange(VerifyArgument.java:346)
        //     at com.strobel.assembler.ir.ConstantPool.get(ConstantPool.java:78)
        //     at com.strobel.assembler.metadata.ClassFileReader$Scope.lookupType(ClassFileReader.java:1218)
        //     at com.strobel.assembler.metadata.MethodReader.readBodyCore(MethodReader.java:186)
        //     at com.strobel.assembler.metadata.MethodReader.readBody(MethodReader.java:62)
        //     ... 16 more
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public int size() {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: fstore_2       
        //     1: ishl           
        //     2: dadd           
        //     3: lsub           
        //     4: irem           
        //     5: ineg           
        //     6: lmul           
        //     7: ddiv           
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  ------------------------------------------------------
        //  0      8       0     this  Lcom/sun/tools/xjc/reader/xmlschema/bindinfo/BindInfo;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public BIDeclaration get(final int idx) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: ishl           
        //     1: ldiv           
        //     2: idiv           
        //     3: drem           
        //     4: dadd           
        //     5: imul           
        //     6: lsub           
        //     7: ldiv           
        //     8: ladd           
        //     9: laload         
        //    10: fadd           
        //    11: lmul           
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  ------------------------------------------------------
        //  0      12      0     this  Lcom/sun/tools/xjc/reader/xmlschema/bindinfo/BindInfo;
        //  0      12      1     idx   I
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
        //     0: fconst_1       
        //     1: aconst_null    
        //     2: castore        
        //     3: nop            
        //     4: lreturn        
        //     5: fconst_1       
        //     6: aconst_null    
        //     7: sastore        
        //     8: aconst_null    
        //     9: pop            
        //    10: aconst_null    
        //    11: nop            
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
}
