// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.xml.xsom.impl;

import com.sun.xml.xsom.visitor.XSFunction;
import com.sun.xml.xsom.visitor.XSVisitor;
import java.util.Iterator;
import com.sun.xml.xsom.XSAttributeUse;
import com.sun.xml.xsom.XSWildcard;
import com.sun.xml.xsom.XSType;
import com.sun.xml.xsom.XSSimpleType;
import org.xml.sax.Locator;
import com.sun.xml.xsom.XSContentType;
import com.sun.xml.xsom.XSElementDecl;
import com.sun.xml.xsom.XSComplexType;

public class ComplexTypeImpl extends AttributesHolder implements XSComplexType, Ref.ComplexType
{
    private int derivationMethod;
    private Ref.Type baseType;
    private XSElementDecl scope;
    private final boolean _abstract;
    private WildcardImpl localAttWildcard;
    private final int finalValue;
    private final int blockValue;
    private Ref.ContentType contentType;
    private XSContentType explicitContent;
    private final boolean mixed;
    
    public ComplexTypeImpl(final SchemaImpl _parent, final AnnotationImpl _annon, final Locator _loc, final String _name, final boolean _anonymous, final boolean _abstract, final int _derivationMethod, final Ref.Type _base, final int _final, final int _block, final boolean _mixed) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: lmul           
        //     1: idiv           
        //     2: laload         
        //     3: aastore        
        //     4: lsub           
        //     5: ineg           
        //     6: istore_0        /* this */
        //     7: aconst_null    
        //     8: nop            
        //     9: fconst_2       
        //    10: fmul           
        //    11: ladd           
        //    12: fneg           
        //    13: ladd           
        //    14: laload         
        //    15: lneg           
        //    16: ineg           
        //    17: lmul           
        //    18: idiv           
        //    19: laload         
        //    20: aastore        
        //    21: lsub           
        //    22: ineg           
        //    23: aconst_null    
        //    24: nop            
        //    25: iconst_5       
        //    26: dadd           
        //    27: ddiv           
        //    28: fdiv           
        //    29: ineg           
        //    30: ladd           
        //    31: lmul           
        //    32: fdiv           
        //    33: drem           
        //    34: aconst_null    
        //    35: nop            
        //    36: iload           40
        //    38: astore_1        /* _parent */
        //    39: fmul           
        //    40: ladd           
        //    41: fneg           
        //    42: ladd           
        //    43: laload         
        //    44: idiv           
        //    45: ladd           
        //    46: fdiv           
        //    47: dsub           
        //    48: laload         
        //    49: iastore        
        //    50: fadd           
        //    51: fmul           
        //    52: lsub           
        //    53: dadd           
        //    54: ineg           
        //    55: istore_0        /* this */
        //    56: dload_3         /* _loc */
        //    57: dup_x1         
        //    58: aconst_null    
        //    59: nop            
        //    60: lconst_1       
        //    LocalVariableTable:
        //  Start  Length  Slot  Name               Signature
        //  -----  ------  ----  -----------------  ---------------------------------------
        //  0      61      0     this               Lcom/sun/xml/xsom/impl/ComplexTypeImpl;
        //  0      61      1     _parent            Lcom/sun/xml/xsom/impl/SchemaImpl;
        //  0      61      2     _annon             Lcom/sun/xml/xsom/impl/AnnotationImpl;
        //  0      61      3     _loc               Lorg/xml/sax/Locator;
        //  0      61      4     _name              Ljava/lang/String;
        //  0      61      5     _anonymous         Z
        //  0      61      6     _abstract          Z
        //  0      61      7     _derivationMethod  I
        //  0      61      8     _base              Lcom/sun/xml/xsom/impl/Ref$Type;
        //  0      61      9     _final             I
        //  0      61      10    _block             I
        //  0      61      11    _mixed             Z
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public XSComplexType asComplexType() {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: ladd           
        //     1: laload         
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  ---------------------------------------
        //  0      2       0     this  Lcom/sun/xml/xsom/impl/ComplexTypeImpl;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public XSSimpleType asSimpleType() {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: lmul           
        //     1: ldiv           
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  ---------------------------------------
        //  0      2       0     this  Lcom/sun/xml/xsom/impl/ComplexTypeImpl;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public final boolean isSimpleType() {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: pop2           
        //     1: aastore        
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  ---------------------------------------
        //  0      2       0     this  Lcom/sun/xml/xsom/impl/ComplexTypeImpl;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public final boolean isComplexType() {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: aconst_null    
        //     1: nop            
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  ---------------------------------------
        //  0      2       0     this  Lcom/sun/xml/xsom/impl/ComplexTypeImpl;
        // 
        // The error that occurred was:
        // 
        // java.lang.NullPointerException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public int getDerivationMethod() {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: fneg           
        //     1: lmul           
        //     2: drem           
        //     3: lmul           
        //     4: ineg           
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  ---------------------------------------
        //  0      5       0     this  Lcom/sun/xml/xsom/impl/ComplexTypeImpl;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public XSType getBaseType() {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: idiv           
        //     1: ladd           
        //     2: fdiv           
        //     3: dsub           
        //     4: laload         
        //     5: iastore        
        //     6: fadd           
        //     7: fmul           
        //     8: lsub           
        //     9: dadd           
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  ---------------------------------------
        //  0      10      0     this  Lcom/sun/xml/xsom/impl/ComplexTypeImpl;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public void redefine(final ComplexTypeImpl ct) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: idiv           
        //     1: laload         
        //     2: ishl           
        //     3: drem           
        //     4: ddiv           
        //     5: ldiv           
        //     6: laload         
        //     7: lmul           
        //     8: ldiv           
        //     9: irem           
        //    10: idiv           
        //    11: laload         
        //    12: dastore        
        //    13: lsub           
        //    14: fsub           
        //    15: nop            
        //    16: lload_3        
        //    17: nop            
        //    18: lstore          this
        //    20: fstore          this
        //    22: iconst_m1      
        //    23: nop            
        //    24: dstore          this
        //    26: astore          this
        //    28: fconst_1       
        //    29: nop            
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  ---------------------------------------
        //  0      30      0     this  Lcom/sun/xml/xsom/impl/ComplexTypeImpl;
        //  0      30      1     ct    Lcom/sun/xml/xsom/impl/ComplexTypeImpl;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public XSElementDecl getScope() {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: nop            
        //     1: nop            
        //     2: iconst_m1      
        //     3: nop            
        //     4: dastore        
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  ---------------------------------------
        //  0      5       0     this  Lcom/sun/xml/xsom/impl/ComplexTypeImpl;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public void setScope(final XSElementDecl _scope) {
        // 
        // This method could not be decompiled.
        // 
        // Could not show original bytecode, likely due to the same error.
        // 
        // The error that occurred was:
        // 
        // com.strobel.assembler.metadata.MethodBodyParseException: An error occurred while parsing the bytecode of method 'com/sun/xml/xsom/impl/ComplexTypeImpl.setScope:(Lcom/sun/xml/xsom/XSElementDecl;)V'.
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
    
    public boolean isAbstract() {
        // 
        // This method could not be decompiled.
        // 
        // Could not show original bytecode, likely due to the same error.
        // 
        // The error that occurred was:
        // 
        // com.strobel.assembler.metadata.MethodBodyParseException: An error occurred while parsing the bytecode of method 'com/sun/xml/xsom/impl/ComplexTypeImpl.isAbstract:()Z'.
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
    
    public void setWildcard(final WildcardImpl wc) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: nop            
        //     1: dstore_2       
        //     2: nop            
        //     3: dup_x2         
        //     4: nop            
        //     5: dup2           
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  ---------------------------------------
        //  0      6       0     this  Lcom/sun/xml/xsom/impl/ComplexTypeImpl;
        //  0      6       1     wc    Lcom/sun/xml/xsom/impl/WildcardImpl;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public XSWildcard getAttributeWildcard() {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: nop            
        //     1: iconst_5       
        //     2: nop            
        //     3: nop            
        //     4: nop            
        //     5: dstore_2       
        //     6: nop            
        //     7: dsub           
        //     8: nop            
        //     9: lstore_2        /* itr */
        //    10: nop            
        //    11: lconst_0       
        //    12: nop            
        //    13: nop            
        //    14: nop            
        //    15: dstore_2        /* itr */
        //    16: nop            
        //    17: imul           
        //    18: nop            
        //    19: istore_1        /* complete */
        //    20: nop            
        //    21: lconst_1       
        //    22: nop            
        //    23: nop            
        //    24: nop            
        //    25: dstore_2        /* itr */
        //    26: nop            
        //    27: lmul           
        //    28: nop            
        //    29: istore_1        /* complete */
        //    30: nop            
        //    31: fconst_0       
        //    32: nop            
        //    33: nop            
        //    34: nop            
        //    35: dstore_2        /* itr */
        //    36: nop            
        //    37: fmul           
        //    38: nop            
        //    39: dstore_2        /* itr */
        //    40: nop            
        //    41: fconst_1       
        //    42: nop            
        //    43: aconst_null    
        //    44: nop            
        //    45: dmul           
        //    46: nop            
        //    47: idiv           
        //    48: nop            
        //    49: aconst_null    
        //    50: nop            
        //    51: pop            
        //    52: nop            
        //    53: nop            
        //    54: nop            
        //    55: aload_2         /* itr */
        //    56: nop            
        //    57: aconst_null    
        //    58: nop            
        //    59: aconst_null    
        //    60: nop            
        //    61: nop            
        //    62: nop            
        //    63: iconst_m1      
        //    64: aload_0         /* this */
        //    65: areturn        
        //    66: nop            
        //    67: nop            
        //    68: nop            
        //    69: iconst_m1      
        //    70: nop            
        //    71: pop2           
        //    72: nop            
        //    73: nop            
        //    74: nop            
        //    75: iconst_3       
        //    76: nop            
        //    77: aconst_null    
        //    78: nop            
        //    79: nop            
        //    80: nop            
        //    81: istore_3        /* base */
        //    82: nop            
        //    83: dup            
        //    84: nop            
        //    85: nop            
        //    86: nop            
        //    87: fconst_1       
        //    88: nop            
        //    89: aconst_null    
        //    90: nop            
        //    91: nop            
        //    92: nop            
        //    93: iconst_m1      
        //    94: nop            
        //    95: dup_x1         
        //    96: nop            
        //    97: fstore_2        /* itr */
        //    98: nop            
        //    99: nop            
        //   100: nop            
        //   101: aconst_null    
        //   102: nop            
        //   103: ldiv           
        //   104: nop            
        //   105: fdiv           
        //   106: nop            
        //   107: aconst_null    
        //   108: nop            
        //   109: pop            
        //   110: nop            
        //   111: nop            
        //   112: nop            
        //   113: ior            
        //   114: nop            
        //   115: iconst_m1      
        //   116: nop            
        //   117: iconst_1       
        //   118: nop            
        //   119: nop            
        //   120: nop            
        //   121: iload_2         /* itr */
        //   122: aload_0         /* this */
        //   123: astore_2        /* itr */
        //   124: aload_1         /* complete */
        //   125: aload_2         /* itr */
        //   126: if_acmpne       131
        //   129: iconst_1       
        //   130: ireturn        
        //   131: aload_2         /* itr */
        //    LocalVariableTable:
        //  Start  Length  Slot  Name      Signature
        //  -----  ------  ----  --------  ---------------------------------------
        //  37     26      3     w         Lcom/sun/xml/xsom/impl/WildcardImpl;
        //  0      132     0     this      Lcom/sun/xml/xsom/impl/ComplexTypeImpl;
        //  5      127     1     complete  Lcom/sun/xml/xsom/impl/WildcardImpl;
        //  10     122     2     itr       Ljava/util/Iterator;
        //  78     54      3     base      Lcom/sun/xml/xsom/impl/WildcardImpl;
        //  84     48      4     baseType  Lcom/sun/xml/xsom/XSType;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public boolean isFinal(final int derivationMethod) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: nop            
        //     1: nop            
        //     2: nop            
        //     3: sipush          117
        //     6: nop            
        //     7: fneg           
        //     8: nop            
        //     9: aconst_null    
        //    10: nop            
        //    11: pop            
        //    12: nop            
        //    13: nop            
        //    14: nop            
        //    LocalVariableTable:
        //  Start  Length  Slot  Name              Signature
        //  -----  ------  ----  ----------------  ---------------------------------------
        //  0      15      0     this              Lcom/sun/xml/xsom/impl/ComplexTypeImpl;
        //  0      15      1     derivationMethod  I
        // 
        // The error that occurred was:
        // 
        // java.lang.NullPointerException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public boolean isSubstitutionProhibited(final int method) {
        -1;
        return true;
    }
    
    public void setContentType(final Ref.ContentType v) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: dup            
        //     1: nop            
        //     2: nop            
        //     3: nop            
        //     4: fconst_1       
        //     5: nop            
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  ---------------------------------------
        //  0      6       0     this  Lcom/sun/xml/xsom/impl/ComplexTypeImpl;
        //  0      6       1     v     Lcom/sun/xml/xsom/impl/Ref$ContentType;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public XSContentType getContentType() {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: aconst_null    
        //     1: nop            
        //     2: nop            
        //     3: nop            
        //     4: lconst_1       
        //     5: nop            
        //     6: dup_x1         
        //     7: nop            
        //     8: fstore_2       
        //     9: nop            
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  ---------------------------------------
        //  0      10      0     this  Lcom/sun/xml/xsom/impl/ComplexTypeImpl;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public void setExplicitContent(final XSContentType v) {
        // 
        // This method could not be decompiled.
        // 
        // Could not show original bytecode, likely due to the same error.
        // 
        // The error that occurred was:
        // 
        // com.strobel.assembler.metadata.MethodBodyParseException: An error occurred while parsing the bytecode of method 'com/sun/xml/xsom/impl/ComplexTypeImpl.setExplicitContent:(Lcom/sun/xml/xsom/XSContentType;)V'.
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
    
    public XSContentType getExplicitContent() {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: aconst_null    
        //     1: nop            
        //     2: aconst_null    
        //     3: nop            
        //     4: land           
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  ---------------------------------------
        //  0      5       0     this  Lcom/sun/xml/xsom/impl/ComplexTypeImpl;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public boolean isMixed() {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: nop            
        //     1: nop            
        //     2: aconst_null    
        //     3: nop            
        //     4: ior            
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  ---------------------------------------
        //  0      5       0     this  Lcom/sun/xml/xsom/impl/ComplexTypeImpl;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public XSAttributeUse getAttributeUse(final String nsURI, final String localName) {
        // 
        // This method could not be decompiled.
        // 
        // Could not show original bytecode, likely due to the same error.
        // 
        // The error that occurred was:
        // 
        // com.strobel.assembler.metadata.MethodBodyParseException: An error occurred while parsing the bytecode of method 'com/sun/xml/xsom/impl/ComplexTypeImpl.getAttributeUse:(Ljava/lang/String;Ljava/lang/String;)Lcom/sun/xml/xsom/XSAttributeUse;'.
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
    
    public Iterator iterateAttributeUses() {
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
        //     5: lor            
        //     6: nop            
        //     7: iconst_2       
        //     8: nop            
        //     9: ixor           
        //    10: nop            
        //    11: dup            
        //    12: nop            
        //    13: nop            
        //    14: nop            
        //    15: lload           this
        //    17: iconst_m1      
        //    18: nop            
        //    19: nop            
        //    20: nop            
        //    21: iconst_3       
        //    22: nop            
        //    23: dup_x1         
        //    24: nop            
        //    25: fstore_2       
        //    26: nop            
        //    27: nop            
        //    28: nop            
        //    29: nop            
        //    30: nop            
        //    31: iconst_3       
        //    32: nop            
        //    33: l2d            
        //    34: nop            
        //    35: astore_0        /* this */
        //    36: nop            
        //    37: aconst_null    
        //    38: nop            
        //    39: aconst_null    
        //    40: nop            
        //    41: f2i            
        //    42: nop            
        //    43: f2l            
        //    44: nop            
        //    LocalVariableTable:
        //  Start  Length  Slot  Name      Signature
        //  -----  ------  ----  --------  ---------------------------------------
        //  0      45      0     this      Lcom/sun/xml/xsom/impl/ComplexTypeImpl;
        //  10     35      1     baseType  Lcom/sun/xml/xsom/XSComplexType;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public XSType[] listSubstitutables() {
        // 
        // This method could not be decompiled.
        // 
        // Could not show original bytecode, likely due to the same error.
        // 
        // The error that occurred was:
        // 
        // com.strobel.assembler.metadata.MethodBodyParseException: An error occurred while parsing the bytecode of method 'com/sun/xml/xsom/impl/ComplexTypeImpl.listSubstitutables:()[Lcom/sun/xml/xsom/XSType;'.
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
    
    public void visit(final XSVisitor visitor) {
        // 
        // This method could not be decompiled.
        // 
        // Could not show original bytecode, likely due to the same error.
        // 
        // The error that occurred was:
        // 
        // com.strobel.assembler.metadata.MethodBodyParseException: An error occurred while parsing the bytecode of method 'com/sun/xml/xsom/impl/ComplexTypeImpl.visit:(Lcom/sun/xml/xsom/visitor/XSVisitor;)V'.
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
    
    public Object apply(final XSFunction function) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: nop            
        //     1: ifeq            111
        //     4: nop            
        //     5: iflt            121
        //    LocalVariableTable:
        //  Start  Length  Slot  Name      Signature
        //  -----  ------  ----  --------  ---------------------------------------
        //  0      8       0     this      Lcom/sun/xml/xsom/impl/ComplexTypeImpl;
        //  0      8       1     function  Lcom/sun/xml/xsom/visitor/XSFunction;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public XSType getType() {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: nop            
        //     1: faload         
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  ---------------------------------------
        //  0      2       0     this  Lcom/sun/xml/xsom/impl/ComplexTypeImpl;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public XSComplexType getComplexType() {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: nop            
        //     1: nop            
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  ---------------------------------------
        //  0      2       0     this  Lcom/sun/xml/xsom/impl/ComplexTypeImpl;
        // 
        // The error that occurred was:
        // 
        // java.lang.NullPointerException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
}
