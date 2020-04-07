// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.xml.xsom.impl;

import com.sun.xml.xsom.visitor.XSSimpleTypeFunction;
import com.sun.xml.xsom.visitor.XSSimpleTypeVisitor;
import com.sun.xml.xsom.XSVariety;
import java.util.Iterator;
import com.sun.xml.xsom.XSFacet;
import org.xml.sax.Locator;
import java.util.Vector;
import com.sun.xml.xsom.XSRestrictionSimpleType;

public class RestrictionSimpleTypeImpl extends SimpleTypeImpl implements XSRestrictionSimpleType
{
    private final Vector facets;
    
    public RestrictionSimpleTypeImpl(final SchemaImpl _parent, final AnnotationImpl _annon, final Locator _loc, final String _name, final boolean _anonymous, final Ref.SimpleType _baseType) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: ineg           
        //     1: frem           
        //     2: lmul           
        //     3: dadd           
        //     4: ineg           
        //     5: lmul           
        //     6: ddiv           
        //     7: fdiv           
        //     8: aconst_null    
        //     9: nop            
        //    10: fconst_2       
        //    11: ladd           
        //    12: drem           
        //    13: dastore        
        //    14: lsub           
        //    15: drem           
        //    16: ineg           
        //    17: frem           
        //    18: lmul           
        //    19: dadd           
        //    20: ineg           
        //    21: lmul           
        //    22: ddiv           
        //    23: fdiv           
        //    24: aconst_null    
        //    LocalVariableTable:
        //  Start  Length  Slot  Name        Signature
        //  -----  ------  ----  ----------  -------------------------------------------------
        //  0      25      0     this        Lcom/sun/xml/xsom/impl/RestrictionSimpleTypeImpl;
        //  0      25      1     _parent     Lcom/sun/xml/xsom/impl/SchemaImpl;
        //  0      25      2     _annon      Lcom/sun/xml/xsom/impl/AnnotationImpl;
        //  0      25      3     _loc        Lorg/xml/sax/Locator;
        //  0      25      4     _name       Ljava/lang/String;
        //  0      25      5     _anonymous  Z
        //  0      25      6     _baseType   Lcom/sun/xml/xsom/impl/Ref$SimpleType;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public void addFacet(final XSFacet facet) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: fconst_1       
        //     1: nop            
        //     2: iload_3        
        //     3: nop            
        //     4: lload_0         /* this */
        //     5: iconst_4       
        //     6: nop            
        //     7: d2i            
        //     8: fconst_1       
        //     9: nop            
        //    LocalVariableTable:
        //  Start  Length  Slot  Name   Signature
        //  -----  ------  ----  -----  -------------------------------------------------
        //  0      10      0     this   Lcom/sun/xml/xsom/impl/RestrictionSimpleTypeImpl;
        //  0      10      1     facet  Lcom/sun/xml/xsom/XSFacet;
        // 
        // The error that occurred was:
        // 
        // java.lang.NullPointerException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public Iterator iterateDeclaredFacets() {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: ifgt            3072
        //     3: dstore_1       
        //     4: nop            
        //     5: dstore_2       
        //     6: fconst_1       
        //     7: nop            
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  -------------------------------------------------
        //  0      8       0     this  Lcom/sun/xml/xsom/impl/RestrictionSimpleTypeImpl;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public XSFacet getDeclaredFacet(final String name) {
        // 
        // This method could not be decompiled.
        // 
        // Could not show original bytecode, likely due to the same error.
        // 
        // The error that occurred was:
        // 
        // com.strobel.assembler.metadata.MethodBodyParseException: An error occurred while parsing the bytecode of method 'com/sun/xml/xsom/impl/RestrictionSimpleTypeImpl.getDeclaredFacet:(Ljava/lang/String;)Lcom/sun/xml/xsom/XSFacet;'.
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
    
    public XSFacet getFacet(final String name) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: ldiv           
        //     1: irem           
        //     2: idiv           
        //     3: lsub           
        //     4: bastore        
        //     5: lshl           
        //     6: irem           
        //     7: lsub           
        //     8: aconst_null    
        //     9: nop            
        //    10: iconst_0       
        //    11: dload_2         /* f */
        //    12: dload_3        
        //    13: sastore        
        //    14: aconst_null    
        //    15: nop            
        //    16: dconst_0       
        //    17: fmul           
        //    18: ladd           
        //    19: fneg           
        //    20: ladd           
        //    21: laload         
        //    22: lneg           
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  -------------------------------------------------
        //  0      23      0     this  Lcom/sun/xml/xsom/impl/RestrictionSimpleTypeImpl;
        //  0      23      1     name  Ljava/lang/String;
        //  6      17      2     f     Lcom/sun/xml/xsom/XSFacet;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public XSVariety getVariety() {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: laload         
        //     1: iastore        
        //     2: fadd           
        //     3: fmul           
        //     4: lsub           
        //     5: dadd           
        //     6: ineg           
        //     7: istore_0        /* this */
        //     8: aconst_null    
        //     9: nop            
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  -------------------------------------------------
        //  0      10      0     this  Lcom/sun/xml/xsom/impl/RestrictionSimpleTypeImpl;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public void visit(final XSSimpleTypeVisitor visitor) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: ladd           
        //     1: idiv           
        //     2: drem           
        //     3: aconst_null    
        //     4: nop            
        //     5: ldc             "()I"
        //     7: ladd           
        //    LocalVariableTable:
        //  Start  Length  Slot  Name     Signature
        //  -----  ------  ----  -------  -------------------------------------------------
        //  0      8       0     this     Lcom/sun/xml/xsom/impl/RestrictionSimpleTypeImpl;
        //  0      8       1     visitor  Lcom/sun/xml/xsom/visitor/XSSimpleTypeVisitor;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public Object apply(final XSSimpleTypeFunction function) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: drem           
        //     1: lsub           
        //     2: bastore        
        //     3: lshl           
        //     4: irem           
        //     5: lsub           
        //     6: aconst_null    
        //     7: nop            
        //    LocalVariableTable:
        //  Start  Length  Slot  Name      Signature
        //  -----  ------  ----  --------  -------------------------------------------------
        //  0      8       0     this      Lcom/sun/xml/xsom/impl/RestrictionSimpleTypeImpl;
        //  0      8       1     function  Lcom/sun/xml/xsom/visitor/XSSimpleTypeFunction;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public boolean isRestriction() {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: drem           
        //     1: ddiv           
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  -------------------------------------------------
        //  0      2       0     this  Lcom/sun/xml/xsom/impl/RestrictionSimpleTypeImpl;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public XSRestrictionSimpleType asRestriction() {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: dsub           
        //     1: lsub           
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  -------------------------------------------------
        //  0      2       0     this  Lcom/sun/xml/xsom/impl/RestrictionSimpleTypeImpl;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
}
