// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.xml.xsom.impl;

import com.sun.xml.xsom.XSTerm;
import com.sun.xml.xsom.visitor.XSFunction;
import com.sun.xml.xsom.visitor.XSTermFunction;
import com.sun.xml.xsom.visitor.XSTermVisitor;
import com.sun.xml.xsom.visitor.XSVisitor;
import com.sun.xml.xsom.XSModelGroup;
import com.sun.xml.xsom.XSModelGroupDecl;
import com.sun.xml.xsom.XSWildcard;
import com.sun.xml.xsom.XSType;
import org.xml.sax.Locator;
import com.sun.xml.xsom.impl.parser.PatcherManager;
import java.util.Set;
import com.sun.xml.xsom.XSElementDecl;

public class ElementDecl extends DeclarationImpl implements XSElementDecl, Ref.Term
{
    private String defaultValue;
    private String fixedValue;
    private boolean nillable;
    private boolean _abstract;
    private Ref.Type type;
    private Ref.Element substHead;
    private int substDisallowed;
    private int substExcluded;
    private Set substitutables;
    private Set substitutablesView;
    
    public ElementDecl(final PatcherManager reader, final SchemaImpl owner, final AnnotationImpl _annon, final Locator _loc, final String _tns, final String _name, final boolean _anonymous, final String _defv, final String _fixedv, final boolean _nillable, final boolean _abstract, final Ref.Type _type, final Ref.Element _substHead, final int _substDisallowed, final int _substExcluded) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: fdiv           
        //     1: laload         
        //     2: ishl           
        //     3: ldiv           
        //     4: idiv           
        //     5: laload         
        //     6: ishl           
        //     7: drem           
        //     8: ddiv           
        //     9: ldiv           
        //    10: laload         
        //    11: fneg           
        //    12: lmul           
        //    13: drem           
        //    14: lmul           
        //    15: ineg           
        //    16: ddiv           
        //    17: frem           
        //    18: laload         
        //    19: pop2           
        //    20: aastore        
        //    21: fstore_3        /* _annon */
        //    22: lneg           
        //    23: fdiv           
        //    24: dadd           
        //    25: ineg           
        //    26: lmul           
        //    27: ddiv           
        //    28: fdiv           
        //    29: istore_0        /* this */
        //    30: aconst_null    
        //    31: nop            
        //    32: iconst_4       
        //    33: dsub           
        //    34: lsub           
        //    35: ineg           
        //    36: bastore        
        //    37: lsub           
        //    38: frem           
        //    39: ldiv           
        //    40: aconst_null    
        //    41: nop            
        //    42: iload_1         /* reader */
        //    43: dload_2         /* owner */
        //    44: dload_3         /* _annon */
        //    45: astore_1        /* reader */
        //    46: dadd           
        //    47: ddiv           
        //    48: ldiv           
        //    49: laload         
        //    50: drem           
        //    51: lneg           
        //    52: fdiv           
        //    53: laload         
        //    54: ishl           
        //    55: ldiv           
        //    56: idiv           
        //    57: laload         
        //    58: ishl           
        //    59: drem           
        //    60: ddiv           
        //    61: ldiv           
        //    62: laload         
        //    63: pop2           
        //    64: aastore        
        //    65: bastore        
        //    66: lsub           
        //    67: frem           
        //    68: ldiv           
        //    69: istore_0        /* this */
        //    70: aconst_null    
        //    71: nop            
        //    72: lconst_1       
        //    73: aastore        
        //    74: ddiv           
        //    75: lneg           
        //    76: frem           
        //    77: dadd           
        //    78: lsub           
        //    79: fstore_3        /* _annon */
        //    80: lmul           
        //    81: idiv           
        //    82: lsub           
        //    83: aconst_null    
        //    84: nop            
        //    85: bipush          69
        //    87: idiv           
        //    LocalVariableTable:
        //  Start  Length  Slot  Name              Signature
        //  -----  ------  ----  ----------------  ---------------------------------------------
        //  0      88      0     this              Lcom/sun/xml/xsom/impl/ElementDecl;
        //  0      88      1     reader            Lcom/sun/xml/xsom/impl/parser/PatcherManager;
        //  0      88      2     owner             Lcom/sun/xml/xsom/impl/SchemaImpl;
        //  0      88      3     _annon            Lcom/sun/xml/xsom/impl/AnnotationImpl;
        //  0      88      4     _loc              Lorg/xml/sax/Locator;
        //  0      88      5     _tns              Ljava/lang/String;
        //  0      88      6     _name             Ljava/lang/String;
        //  0      88      7     _anonymous        Z
        //  0      88      8     _defv             Ljava/lang/String;
        //  0      88      9     _fixedv           Ljava/lang/String;
        //  0      88      10    _nillable         Z
        //  0      88      11    _abstract         Z
        //  0      88      12    _type             Lcom/sun/xml/xsom/impl/Ref$Type;
        //  0      88      13    _substHead        Lcom/sun/xml/xsom/impl/Ref$Element;
        //  0      88      14    _substDisallowed  I
        //  0      88      15    _substExcluded    I
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public String getDefaultValue() {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: aconst_null    
        //     1: nop            
        //     2: lload_3        
        //     3: dup_x2         
        //     4: astore_1       
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  -----------------------------------
        //  0      5       0     this  Lcom/sun/xml/xsom/impl/ElementDecl;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public String getFixedValue() {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: fconst_1       
        //     1: aconst_null    
        //     2: fconst_1       
        //     3: aconst_null    
        //     4: fconst_2       
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  -----------------------------------
        //  0      5       0     this  Lcom/sun/xml/xsom/impl/ElementDecl;
        // 
        // The error that occurred was:
        // 
        // java.lang.NullPointerException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public boolean isNillable() {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: sipush          263
        //     3: fconst_1       
        //     4: aconst_null    
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  -----------------------------------
        //  0      5       0     this  Lcom/sun/xml/xsom/impl/ElementDecl;
        // 
        // The error that occurred was:
        // 
        // java.lang.NullPointerException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public boolean isAbstract() {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: aconst_null    
        //     1: lload_2        
        //     2: iconst_4       
        //     3: aconst_null    
        //     4: lload_3        
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  -----------------------------------
        //  0      5       0     this  Lcom/sun/xml/xsom/impl/ElementDecl;
        // 
        // The error that occurred was:
        // 
        // java.lang.NullPointerException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public XSType getType() {
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
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  -----------------------------------
        //  0      10      0     this  Lcom/sun/xml/xsom/impl/ElementDecl;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public XSElementDecl getSubstAffiliation() {
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
        //     5: ishl           
        //     6: ldiv           
        //     7: idiv           
        //     8: laload         
        //     9: ishl           
        //    10: drem           
        //    11: ddiv           
        //    12: ldiv           
        //    13: laload         
        //    14: lmul           
        //    15: ldiv           
        //    16: irem           
        //    17: idiv           
        //    18: laload         
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  -----------------------------------
        //  0      19      0     this  Lcom/sun/xml/xsom/impl/ElementDecl;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public boolean isSubstitutionDisallowed(final int method) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: ldiv           
        //     1: irem           
        //     2: idiv           
        //     3: istore_0        /* this */
        //     4: astore_1        /* method */
        //     5: dadd           
        //     6: ddiv           
        //     7: ldiv           
        //     8: laload         
        //     9: drem           
        //    10: lneg           
        //    11: fdiv           
        //    12: laload         
        //    13: ishl           
        //    14: ldiv           
        //    LocalVariableTable:
        //  Start  Length  Slot  Name    Signature
        //  -----  ------  ----  ------  -----------------------------------
        //  0      15      0     this    Lcom/sun/xml/xsom/impl/ElementDecl;
        //  0      15      1     method  I
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public boolean isSubstitutionExcluded(final int method) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: lmul           
        //     1: ldiv           
        //     2: irem           
        //     3: idiv           
        //     4: laload         
        //     5: fstore_3       
        //     6: ddiv           
        //     7: frem           
        //     8: lsub           
        //     9: lmul           
        //    10: dsub           
        //    11: fdiv           
        //    12: lstore_2       
        //    13: ineg           
        //    14: ineg           
        //    LocalVariableTable:
        //  Start  Length  Slot  Name    Signature
        //  -----  ------  ----  ------  -----------------------------------
        //  0      15      0     this    Lcom/sun/xml/xsom/impl/ElementDecl;
        //  0      15      1     method  I
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public XSElementDecl[] listSubstitutables() {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: idiv           
        //     1: idiv           
        //     2: lsub           
        //     3: dadd           
        //     4: ineg           
        //     5: lmul           
        //     6: ddiv           
        //     7: fdiv           
        //     8: drem           
        //     9: aconst_null    
        //    10: nop            
        //    11: bipush          117
        //    13: fdiv           
        //    14: ldiv           
        //    15: ddiv           
        //    16: isub           
        //    17: lmul           
        //    18: fsub           
        //    19: lmul           
        //    20: ladd           
        //    21: fadd           
        //    22: idiv           
        //    23: lsub           
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  -----------------------------------
        //  0      24      0     this  Lcom/sun/xml/xsom/impl/ElementDecl;
        //  5      19      1     s     Ljava/util/Set;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public Set getSubstitutables() {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: nop            
        //     1: lload           40
        //     3: dload_3        
        //     4: astore_1       
        //     5: fmul           
        //     6: ladd           
        //     7: fneg           
        //     8: ladd           
        //     9: laload         
        //    10: lneg           
        //    11: ineg           
        //    12: lmul           
        //    13: idiv           
        //    14: laload         
        //    15: dstore_2       
        //    16: ineg           
        //    17: lsub           
        //    18: frem           
        //    19: ladd           
        //    20: ineg           
        //    21: ddiv           
        //    22: frem           
        //    23: istore_0        /* this */
        //    24: aconst_null    
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  -----------------------------------
        //  0      25      0     this  Lcom/sun/xml/xsom/impl/ElementDecl;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    protected void addSubstitutable(final ElementDecl decl) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: lsub           
        //     1: ineg           
        //     2: lastore        
        //     3: ladd           
        //     4: frem           
        //     5: lsub           
        //     6: fdiv           
        //     7: ineg           
        //     8: aconst_null    
        //     9: nop            
        //    10: iconst_0       
        //    11: dsub           
        //    12: lsub           
        //    13: ineg           
        //    14: aconst_null    
        //    15: nop            
        //    16: fconst_2       
        //    17: fmul           
        //    18: ladd           
        //    19: fneg           
        //    20: ladd           
        //    21: laload         
        //    22: lneg           
        //    23: ineg           
        //    24: lmul           
        //    25: idiv           
        //    26: laload         
        //    27: aastore        
        //    28: lsub           
        //    29: ineg           
        //    30: aconst_null    
        //    31: nop            
        //    32: iconst_1       
        //    33: drem           
        //    34: lmul           
        //    35: ishr           
        //    36: lsub           
        //    37: aconst_null    
        //    38: nop            
        //    39: iconst_0       
        //    40: dload_2        
        //    41: dload_3        
        //    42: dstore_2       
        //    43: aconst_null    
        //    44: nop            
        //    45: iconst_4       
        //    46: ineg           
        //    47: ddiv           
        //    48: lstore_2       
        //    49: frem           
        //    50: frem           
        //    51: ladd           
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  -----------------------------------
        //  0      52      0     this  Lcom/sun/xml/xsom/impl/ElementDecl;
        //  0      52      1     decl  Lcom/sun/xml/xsom/impl/ElementDecl;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public void updateSubstitutabilityMap() {
        // 
        // This method could not be decompiled.
        // 
        // Could not show original bytecode, likely due to the same error.
        // 
        // The error that occurred was:
        // 
        // com.strobel.assembler.metadata.MethodBodyParseException: An error occurred while parsing the bytecode of method 'com/sun/xml/xsom/impl/ElementDecl.updateSubstitutabilityMap:()V'.
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
        // Caused by: java.lang.IllegalArgumentException: Argument 'index' must be in the range [0, 232], but value was: 26469.
        //     at com.strobel.core.VerifyArgument.inRange(VerifyArgument.java:346)
        //     at com.strobel.assembler.ir.ConstantPool.get(ConstantPool.java:78)
        //     at com.strobel.assembler.metadata.ClassFileReader$Scope.lookupConstant(ClassFileReader.java:1313)
        //     at com.strobel.assembler.metadata.MethodReader.readBodyCore(MethodReader.java:293)
        //     at com.strobel.assembler.metadata.MethodReader.readBody(MethodReader.java:62)
        //     ... 16 more
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public boolean canBeSubstitutedBy(final XSElementDecl e) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: ineg           
        //     1: ddiv           
        //     2: frem           
        //     3: laload         
        //     4: pop2           
        //     5: aastore        
        //     6: bastore        
        //     7: lsub           
        //     8: frem           
        //     9: ldiv           
        //    10: sastore        
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  -----------------------------------
        //  0      11      0     this  Lcom/sun/xml/xsom/impl/ElementDecl;
        //  0      11      1     e     Lcom/sun/xml/xsom/XSElementDecl;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public boolean isWildcard() {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: drem           
        //     1: ddiv           
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  -----------------------------------
        //  0      2       0     this  Lcom/sun/xml/xsom/impl/ElementDecl;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public boolean isModelGroupDecl() {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: lmul           
        //     1: drem           
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  -----------------------------------
        //  0      2       0     this  Lcom/sun/xml/xsom/impl/ElementDecl;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public boolean isModelGroup() {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: lsub           
        //     1: fdiv           
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  -----------------------------------
        //  0      2       0     this  Lcom/sun/xml/xsom/impl/ElementDecl;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public boolean isElementDecl() {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: idiv           
        //     1: laload         
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  -----------------------------------
        //  0      2       0     this  Lcom/sun/xml/xsom/impl/ElementDecl;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public XSWildcard asWildcard() {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: daload         
        //     1: nop            
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  -----------------------------------
        //  0      2       0     this  Lcom/sun/xml/xsom/impl/ElementDecl;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public XSModelGroupDecl asModelGroupDecl() {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: iconst_m1      
        //     1: nop            
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  -----------------------------------
        //  0      2       0     this  Lcom/sun/xml/xsom/impl/ElementDecl;
        // 
        // The error that occurred was:
        // 
        // java.lang.NullPointerException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public XSModelGroup asModelGroup() {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: nop            
        //     1: nop            
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  -----------------------------------
        //  0      2       0     this  Lcom/sun/xml/xsom/impl/ElementDecl;
        // 
        // The error that occurred was:
        // 
        // java.lang.NullPointerException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public XSElementDecl asElementDecl() {
        // 
        // This method could not be decompiled.
        // 
        // Could not show original bytecode, likely due to the same error.
        // 
        // The error that occurred was:
        // 
        // com.strobel.assembler.metadata.MethodBodyParseException: An error occurred while parsing the bytecode of method 'com/sun/xml/xsom/impl/ElementDecl.asElementDecl:()Lcom/sun/xml/xsom/XSElementDecl;'.
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
        //     at com.strobel.assembler.metadata.Buffer.readUnsignedShort(Buffer.java:225)
        //     at com.strobel.assembler.metadata.MethodReader.readBodyCore(MethodReader.java:214)
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
        // com.strobel.assembler.metadata.MethodBodyParseException: An error occurred while parsing the bytecode of method 'com/sun/xml/xsom/impl/ElementDecl.visit:(Lcom/sun/xml/xsom/visitor/XSVisitor;)V'.
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
        //     at com.strobel.assembler.metadata.Buffer.readUnsignedShort(Buffer.java:225)
        //     at com.strobel.assembler.metadata.MethodReader.readBodyCore(MethodReader.java:203)
        //     at com.strobel.assembler.metadata.MethodReader.readBody(MethodReader.java:62)
        //     ... 16 more
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public void visit(final XSTermVisitor visitor) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: nop            
        //     1: astore          this
        //     3: lload_2        
        //     4: nop            
        //     5: istore_0        /* this */
        //     6: nop            
        //     7: dload_0         /* this */
        //    LocalVariableTable:
        //  Start  Length  Slot  Name     Signature
        //  -----  ------  ----  -------  ----------------------------------------
        //  0      8       0     this     Lcom/sun/xml/xsom/impl/ElementDecl;
        //  0      8       1     visitor  Lcom/sun/xml/xsom/visitor/XSTermVisitor;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public Object apply(final XSTermFunction function) {
        // 
        // This method could not be decompiled.
        // 
        // Could not show original bytecode, likely due to the same error.
        // 
        // The error that occurred was:
        // 
        // com.strobel.assembler.metadata.MethodBodyParseException: An error occurred while parsing the bytecode of method 'com/sun/xml/xsom/impl/ElementDecl.apply:(Lcom/sun/xml/xsom/visitor/XSTermFunction;)Ljava/lang/Object;'.
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
    
    public Object apply(final XSFunction function) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: nop            
        //     1: nop            
        //     2: nop            
        //     3: f2i            
        //     4: nop            
        //     5: isub           
        //     6: nop            
        //     7: dadd           
        //    LocalVariableTable:
        //  Start  Length  Slot  Name      Signature
        //  -----  ------  ----  --------  -------------------------------------
        //  0      8       0     this      Lcom/sun/xml/xsom/impl/ElementDecl;
        //  0      8       1     function  Lcom/sun/xml/xsom/visitor/XSFunction;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public XSTerm getTerm() {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: nop            
        //     1: fmul           
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  -----------------------------------
        //  0      2       0     this  Lcom/sun/xml/xsom/impl/ElementDecl;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
}
