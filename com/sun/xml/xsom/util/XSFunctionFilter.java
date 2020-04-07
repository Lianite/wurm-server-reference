// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.xml.xsom.util;

import com.sun.xml.xsom.XSElementDecl;
import com.sun.xml.xsom.XSModelGroup;
import com.sun.xml.xsom.XSModelGroupDecl;
import com.sun.xml.xsom.XSWildcard;
import com.sun.xml.xsom.XSContentType;
import com.sun.xml.xsom.XSParticle;
import com.sun.xml.xsom.XSSimpleType;
import com.sun.xml.xsom.XSNotation;
import com.sun.xml.xsom.XSFacet;
import com.sun.xml.xsom.XSSchema;
import com.sun.xml.xsom.XSComplexType;
import com.sun.xml.xsom.XSAttributeUse;
import com.sun.xml.xsom.XSAttributeDecl;
import com.sun.xml.xsom.XSAttGroupDecl;
import com.sun.xml.xsom.XSAnnotation;
import com.sun.xml.xsom.visitor.XSFunction;

public class XSFunctionFilter implements XSFunction
{
    protected XSFunction core;
    
    public XSFunctionFilter(final XSFunction _core) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: lneg           
        //     1: fdiv           
        //     2: laload         
        //     3: ishl           
        //     4: ldiv           
        //     5: idiv           
        //     6: laload         
        //     7: ishl           
        //     8: drem           
        //     9: ddiv           
        //    LocalVariableTable:
        //  Start  Length  Slot  Name   Signature
        //  -----  ------  ----  -----  ----------------------------------------
        //  0      10      0     this   Lcom/sun/xml/xsom/util/XSFunctionFilter;
        //  0      10      1     _core  Lcom/sun/xml/xsom/visitor/XSFunction;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public XSFunctionFilter() {
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
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  ----------------------------------------
        //  0      5       0     this  Lcom/sun/xml/xsom/util/XSFunctionFilter;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public Object annotation(final XSAnnotation ann) {
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
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  ----------------------------------------
        //  0      11      0     this  Lcom/sun/xml/xsom/util/XSFunctionFilter;
        //  0      11      1     ann   Lcom/sun/xml/xsom/XSAnnotation;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public Object attGroupDecl(final XSAttGroupDecl decl) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: lsub           
        //     1: idiv           
        //     2: dstore_0        /* this */
        //     3: frem           
        //     4: ddiv           
        //     5: lneg           
        //     6: irem           
        //     7: aconst_null    
        //     8: nop            
        //     9: baload         
        //    10: dload_2        
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  ----------------------------------------
        //  0      11      0     this  Lcom/sun/xml/xsom/util/XSFunctionFilter;
        //  0      11      1     decl  Lcom/sun/xml/xsom/XSAttGroupDecl;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public Object attributeDecl(final XSAttributeDecl decl) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: drem           
        //     1: lneg           
        //     2: fdiv           
        //     3: laload         
        //     4: ishl           
        //     5: ldiv           
        //     6: idiv           
        //     7: laload         
        //     8: ishl           
        //     9: drem           
        //    10: ddiv           
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  ----------------------------------------
        //  0      11      0     this  Lcom/sun/xml/xsom/util/XSFunctionFilter;
        //  0      11      1     decl  Lcom/sun/xml/xsom/XSAttributeDecl;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public Object attributeUse(final XSAttributeUse use) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: dadd           
        //     1: idiv           
        //     2: aconst_null    
        //     3: nop            
        //     4: caload         
        //     5: dload_2        
        //     6: astore_1        /* use */
        //     7: dadd           
        //     8: ddiv           
        //     9: ldiv           
        //    10: laload         
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  ----------------------------------------
        //  0      11      0     this  Lcom/sun/xml/xsom/util/XSFunctionFilter;
        //  0      11      1     use   Lcom/sun/xml/xsom/XSAttributeUse;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public Object complexType(final XSComplexType type) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: laload         
        //     1: pop2           
        //     2: aastore        
        //     3: fstore_2       
        //     4: idiv           
        //     5: lsub           
        //     6: ldiv           
        //     7: lsub           
        //     8: fdiv           
        //     9: ineg           
        //    10: fstore_1        /* type */
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  ----------------------------------------
        //  0      11      0     this  Lcom/sun/xml/xsom/util/XSFunctionFilter;
        //  0      11      1     type  Lcom/sun/xml/xsom/XSComplexType;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public Object schema(final XSSchema schema) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: nop            
        //     1: istore_0        /* this */
        //     2: dload_2        
        //     3: astore_1        /* schema */
        //     4: dadd           
        //     5: ddiv           
        //     6: ldiv           
        //     7: laload         
        //     8: drem           
        //     9: lneg           
        //    10: fdiv           
        //    LocalVariableTable:
        //  Start  Length  Slot  Name    Signature
        //  -----  ------  ----  ------  ----------------------------------------
        //  0      11      0     this    Lcom/sun/xml/xsom/util/XSFunctionFilter;
        //  0      11      1     schema  Lcom/sun/xml/xsom/XSSchema;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public Object facet(final XSFacet facet) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: ishl           
        //     1: drem           
        //     2: ddiv           
        //     3: ldiv           
        //     4: laload         
        //     5: pop2           
        //     6: aastore        
        //     7: dstore_2       
        //     8: isub           
        //     9: lsub           
        //    10: fdiv           
        //    LocalVariableTable:
        //  Start  Length  Slot  Name   Signature
        //  -----  ------  ----  -----  ----------------------------------------
        //  0      11      0     this   Lcom/sun/xml/xsom/util/XSFunctionFilter;
        //  0      11      1     facet  Lcom/sun/xml/xsom/XSFacet;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public Object notation(final XSNotation notation) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: irem           
        //     1: ladd           
        //     2: ineg           
        //     3: imul           
        //     4: aconst_null    
        //     5: nop            
        //     6: iaload         
        //     7: dload_2        
        //     8: astore_1        /* notation */
        //     9: dadd           
        //    10: ddiv           
        //    LocalVariableTable:
        //  Start  Length  Slot  Name      Signature
        //  -----  ------  ----  --------  ----------------------------------------
        //  0      11      0     this      Lcom/sun/xml/xsom/util/XSFunctionFilter;
        //  0      11      1     notation  Lcom/sun/xml/xsom/XSNotation;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public Object simpleType(final XSSimpleType simpleType) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: lastore        
        //     1: ladd           
        //     2: ineg           
        //     3: imul           
        //     4: istore_0        /* this */
        //     5: aconst_null    
        //     6: nop            
        //     7: lload_1         /* simpleType */
        //     8: dload_2        
        //     9: astore_1        /* simpleType */
        //    10: dadd           
        //    LocalVariableTable:
        //  Start  Length  Slot  Name        Signature
        //  -----  ------  ----  ----------  ----------------------------------------
        //  0      11      0     this        Lcom/sun/xml/xsom/util/XSFunctionFilter;
        //  0      11      1     simpleType  Lcom/sun/xml/xsom/XSSimpleType;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public Object particle(final XSParticle particle) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: fmul           
        //     1: lsub           
        //     2: dadd           
        //     3: ineg           
        //     4: istore_0        /* this */
        //     5: astore_1        /* particle */
        //     6: dadd           
        //     7: ddiv           
        //     8: ldiv           
        //     9: laload         
        //    10: drem           
        //    LocalVariableTable:
        //  Start  Length  Slot  Name      Signature
        //  -----  ------  ----  --------  ----------------------------------------
        //  0      11      0     this      Lcom/sun/xml/xsom/util/XSFunctionFilter;
        //  0      11      1     particle  Lcom/sun/xml/xsom/XSParticle;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public Object empty(final XSContentType empty) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: lsub           
        //     1: frem           
        //     2: iaload         
        //     3: fmul           
        //     4: ladd           
        //     5: fneg           
        //     6: ladd           
        //     7: fconst_1       
        //     8: nop            
        //     9: iload_1         /* empty */
        //    10: nop            
        //    LocalVariableTable:
        //  Start  Length  Slot  Name   Signature
        //  -----  ------  ----  -----  ----------------------------------------
        //  0      11      0     this   Lcom/sun/xml/xsom/util/XSFunctionFilter;
        //  0      11      1     empty  Lcom/sun/xml/xsom/XSContentType;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public Object wildcard(final XSWildcard wc) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: fconst_1       
        //     1: nop            
        //     2: dup2           
        //     3: nop            
        //     4: dup2_x1        
        //     5: fconst_1       
        //     6: nop            
        //     7: iadd           
        //     8: nop            
        //     9: ladd           
        //    10: fconst_1       
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  ----------------------------------------
        //  0      11      0     this  Lcom/sun/xml/xsom/util/XSFunctionFilter;
        //  0      11      1     wc    Lcom/sun/xml/xsom/XSWildcard;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public Object modelGroupDecl(final XSModelGroupDecl decl) {
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
        //     8: aconst_null    
        //     9: nop            
        //    10: fload_1         /* decl */
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  ----------------------------------------
        //  0      11      0     this  Lcom/sun/xml/xsom/util/XSFunctionFilter;
        //  0      11      1     decl  Lcom/sun/xml/xsom/XSModelGroupDecl;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public Object modelGroup(final XSModelGroup group) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: aconst_null    
        //     1: nop            
        //     2: iload_1         /* group */
        //     3: nop            
        //     4: iload_2        
        //     5: nop            
        //     6: iconst_m1      
        //     7: nop            
        //     8: iload_3        
        //     9: nop            
        //    10: nop            
        //    LocalVariableTable:
        //  Start  Length  Slot  Name   Signature
        //  -----  ------  ----  -----  ----------------------------------------
        //  0      11      0     this   Lcom/sun/xml/xsom/util/XSFunctionFilter;
        //  0      11      1     group  Lcom/sun/xml/xsom/XSModelGroup;
        // 
        // The error that occurred was:
        // 
        // java.lang.NullPointerException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public Object elementDecl(final XSElementDecl decl) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: nop            
        //     1: fload_0         /* this */
        //     2: nop            
        //     3: dload           this
        //     5: aconst_null    
        //     6: nop            
        //     7: fload_1         /* decl */
        //     8: nop            
        //     9: nop            
        //    10: nop            
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  ----------------------------------------
        //  0      11      0     this  Lcom/sun/xml/xsom/util/XSFunctionFilter;
        //  0      11      1     decl  Lcom/sun/xml/xsom/XSElementDecl;
        // 
        // The error that occurred was:
        // 
        // java.lang.NullPointerException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
}
