// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.xml.xsom.impl;

import com.sun.xml.xsom.XSAttributeUse;
import java.util.Iterator;
import org.xml.sax.Locator;
import java.util.Set;
import java.util.Map;

public abstract class AttributesHolder extends DeclarationImpl
{
    protected final Map attributes;
    protected final Set prohibitedAtts;
    protected final Set attGroups;
    
    protected AttributesHolder(final SchemaImpl _parent, final AnnotationImpl _annon, final Locator loc, final String _name, final boolean _anonymous) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: lsub           
        //     1: frem           
        //     2: fload_2         /* _annon */
        //     3: daload         
        //     4: aconst_null    
        //     5: nop            
        //     6: nop            
        //     7: fconst_1       
        //     8: nop            
        //     9: faload         
        //    10: nop            
        //    11: freturn        
        //    12: aconst_null    
        //    13: nop            
        //    14: dload_2         /* _annon */
        //    15: dadd           
        //    16: ddiv           
        //    17: ldiv           
        //    18: laload         
        //    19: drem           
        //    20: lneg           
        //    21: fdiv           
        //    22: laload         
        //    23: ishl           
        //    24: ldiv           
        //    25: idiv           
        //    26: laload         
        //    27: ishl           
        //    28: drem           
        //    29: ddiv           
        //    30: ldiv           
        //    31: laload         
        //    32: lmul           
        //    33: ldiv           
        //    34: irem           
        //    35: idiv           
        //    36: laload         
        //    37: lstore_2        /* _annon */
        //    38: ineg           
        //    39: ineg           
        //    40: frem           
        //    41: lmul           
        //    42: fadd           
        //    43: lneg           
        //    44: ineg           
        //    45: lsub           
        //    46: drem           
        //    47: dstore_1        /* _parent */
        //    48: ddiv           
        //    49: idiv           
        //    50: isub           
        //    51: lsub           
        //    LocalVariableTable:
        //  Start  Length  Slot  Name        Signature
        //  -----  ------  ----  ----------  ----------------------------------------
        //  0      52      0     this        Lcom/sun/xml/xsom/impl/AttributesHolder;
        //  0      52      1     _parent     Lcom/sun/xml/xsom/impl/SchemaImpl;
        //  0      52      2     _annon      Lcom/sun/xml/xsom/impl/AnnotationImpl;
        //  0      52      3     loc         Lorg/xml/sax/Locator;
        //  0      52      4     _name       Ljava/lang/String;
        //  0      52      5     _anonymous  Z
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public abstract void setWildcard(final WildcardImpl p0);
    
    public void addAttributeUse(final UName name, final AttributeUseImpl a) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: nop            
        //     1: laload         
        //     2: dadd           
        //     3: ddiv           
        //     4: ldiv           
        //     5: laload         
        //     6: drem           
        //     7: lneg           
        //     8: fdiv           
        //     9: laload         
        //    10: ishl           
        //    11: ldiv           
        //    12: idiv           
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  ----------------------------------------
        //  0      13      0     this  Lcom/sun/xml/xsom/impl/AttributesHolder;
        //  0      13      1     name  Lcom/sun/xml/xsom/impl/UName;
        //  0      13      2     a     Lcom/sun/xml/xsom/impl/AttributeUseImpl;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public void addProhibitedAttribute(final UName name) {
        // 
        // This method could not be decompiled.
        // 
        // Could not show original bytecode, likely due to the same error.
        // 
        // The error that occurred was:
        // 
        // com.strobel.assembler.metadata.MethodBodyParseException: An error occurred while parsing the bytecode of method 'com/sun/xml/xsom/impl/AttributesHolder.addProhibitedAttribute:(Lcom/sun/xml/xsom/impl/UName;)V'.
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
        // Caused by: java.lang.IllegalArgumentException: Argument 'index' must be in the range [0, 155], but value was: 10316.
        //     at com.strobel.core.VerifyArgument.inRange(VerifyArgument.java:346)
        //     at com.strobel.assembler.ir.ConstantPool.get(ConstantPool.java:78)
        //     at com.strobel.assembler.metadata.ClassFileReader$Scope.lookupType(ClassFileReader.java:1218)
        //     at com.strobel.assembler.metadata.MethodReader.readBodyCore(MethodReader.java:186)
        //     at com.strobel.assembler.metadata.MethodReader.readBody(MethodReader.java:62)
        //     ... 16 more
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public Iterator iterateAttributeUses() {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: ineg           
        //     1: lmul           
        //     2: ddiv           
        //     3: fdiv           
        //     4: dstore_2       
        //     5: ldiv           
        //     6: irem           
        //     7: idiv           
        //     8: istore_0        /* this */
        //     9: astore_1        /* v */
        //    10: ddiv           
        //    11: frem           
        //    12: dsub           
        //    13: laload         
        //    14: ishl           
        //    15: ldiv           
        //    16: idiv           
        //    17: laload         
        //    18: drem           
        //    19: ladd           
        //    20: ishl           
        //    21: laload         
        //    22: astore_1        /* v */
        //    23: ddiv           
        //    24: dadd           
        //    25: ladd           
        //    26: ineg           
        //    27: ddiv           
        //    28: frem           
        //    29: istore_0        /* this */
        //    30: astore_1        /* v */
        //    31: dadd           
        //    32: ddiv           
        //    33: ldiv           
        //    34: laload         
        //    35: drem           
        //    36: lneg           
        //    37: fdiv           
        //    38: laload         
        //    39: ishl           
        //    40: ldiv           
        //    41: idiv           
        //    42: laload         
        //    43: ishl           
        //    44: drem           
        //    45: ddiv           
        //    46: ldiv           
        //    47: laload         
        //    48: lmul           
        //    49: ldiv           
        //    50: irem           
        //    51: idiv           
        //    52: laload         
        //    53: fstore_3        /* jtr */
        //    54: ddiv           
        //    55: frem           
        //    56: lsub           
        //    57: lmul           
        //    58: dsub           
        //    59: fdiv           
        //    60: lstore_2        /* itr */
        //    61: ineg           
        //    62: ineg           
        //    63: frem           
        //    64: lmul           
        //    65: fadd           
        //    66: lneg           
        //    67: ineg           
        //    68: lsub           
        //    69: drem           
        //    70: dstore_2        /* itr */
        //    71: ldiv           
        //    72: irem           
        //    73: idiv           
        //    74: istore_0        /* this */
        //    75: astore_1        /* v */
        //    76: fmul           
        //    77: ladd           
        //    78: fneg           
        //    79: ladd           
        //    80: laload         
        //    81: idiv           
        //    82: ladd           
        //    83: fdiv           
        //    84: dsub           
        //    85: laload         
        //    86: aastore        
        //    87: ineg           
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  ----------------------------------------
        //  53     25      3     jtr   Ljava/util/Iterator;
        //  0      88      0     this  Lcom/sun/xml/xsom/impl/AttributesHolder;
        //  8      80      1     v     Ljava/util/List;
        //  29     59      2     itr   Ljava/util/Iterator;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public XSAttributeUse getDeclaredAttributeUse(final String nsURI, final String localName) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: nop            
        //     1: fconst_2       
        //     2: fmul           
        //     3: ladd           
        //     4: fneg           
        //     5: ladd           
        //     6: laload         
        //     7: lneg           
        //     8: ineg           
        //     9: lmul           
        //    10: idiv           
        //    11: laload         
        //    12: aastore        
        //    13: lsub           
        //    14: ineg           
        //    15: aconst_null    
        //    16: nop            
        //    17: iconst_0       
        //    18: ladd           
        //    19: isub           
        //    20: isub           
        //    21: aconst_null    
        //    LocalVariableTable:
        //  Start  Length  Slot  Name       Signature
        //  -----  ------  ----  ---------  ----------------------------------------
        //  0      22      0     this       Lcom/sun/xml/xsom/impl/AttributesHolder;
        //  0      22      1     nsURI      Ljava/lang/String;
        //  0      22      2     localName  Ljava/lang/String;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public Iterator iterateDeclaredAttributeUses() {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: idiv           
        //     1: lsub           
        //     2: dadd           
        //     3: ineg           
        //     4: lmul           
        //     5: ddiv           
        //     6: fdiv           
        //     7: istore_0        /* this */
        //     8: dload_3        
        //     9: dup_x1         
        //    10: aconst_null    
        //    11: nop            
        //    12: iconst_5       
        //    13: lmul           
        //    14: ineg           
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  ----------------------------------------
        //  0      15      0     this  Lcom/sun/xml/xsom/impl/AttributesHolder;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public void addAttGroup(final Ref.AttGroup a) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: fmul           
        //     1: ladd           
        //     2: fneg           
        //     3: ladd           
        //     4: laload         
        //     5: idiv           
        //     6: ladd           
        //     7: fdiv           
        //     8: dsub           
        //     9: laload         
        //    10: iastore        
        //    11: fadd           
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  ----------------------------------------
        //  0      12      0     this  Lcom/sun/xml/xsom/impl/AttributesHolder;
        //  0      12      1     a     Lcom/sun/xml/xsom/impl/Ref$AttGroup;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public Iterator iterateAttGroups() {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: dsub           
        //     1: laload         
        //     2: iastore        
        //     3: fadd           
        //     4: fmul           
        //     5: lsub           
        //     6: dadd           
        //     7: ineg           
        //     8: istore_0        /* this */
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  ----------------------------------------
        //  0      9       0     this  Lcom/sun/xml/xsom/impl/AttributesHolder;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
}
