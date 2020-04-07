// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.xml.xsom.impl;

import com.sun.xml.xsom.visitor.XSContentTypeVisitor;
import com.sun.xml.xsom.visitor.XSVisitor;
import com.sun.xml.xsom.visitor.XSContentTypeFunction;
import com.sun.xml.xsom.visitor.XSFunction;
import com.sun.xml.xsom.XSContentType;
import com.sun.xml.xsom.XSSimpleType;
import com.sun.xml.xsom.XSTerm;
import org.xml.sax.Locator;
import com.sun.xml.xsom.XSParticle;

public class ParticleImpl extends ComponentImpl implements XSParticle, ContentTypeImpl
{
    private Ref.Term term;
    private int maxOccurs;
    private int minOccurs;
    
    public ParticleImpl(final SchemaImpl owner, final AnnotationImpl _ann, final Ref.Term _term, final Locator _loc, final int _maxOccurs, final int _minOccurs) {
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
        //     6: sastore        
        //     7: lmul           
        //     8: drem           
        //     9: lmul           
        //    10: ineg           
        //    11: ddiv           
        //    12: frem           
        //    13: aconst_null    
        //    14: nop            
        //    15: lload_2         /* _ann */
        //    16: dload_2         /* _ann */
        //    17: astore_1        /* owner */
        //    18: dadd           
        //    19: ddiv           
        //    20: ldiv           
        //    21: laload         
        //    22: drem           
        //    23: lneg           
        //    24: fdiv           
        //    25: laload         
        //    LocalVariableTable:
        //  Start  Length  Slot  Name        Signature
        //  -----  ------  ----  ----------  --------------------------------------
        //  0      26      0     this        Lcom/sun/xml/xsom/impl/ParticleImpl;
        //  0      26      1     owner       Lcom/sun/xml/xsom/impl/SchemaImpl;
        //  0      26      2     _ann        Lcom/sun/xml/xsom/impl/AnnotationImpl;
        //  0      26      3     _term       Lcom/sun/xml/xsom/impl/Ref$Term;
        //  0      26      4     _loc        Lorg/xml/sax/Locator;
        //  0      26      5     _maxOccurs  I
        //  0      26      6     _minOccurs  I
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public ParticleImpl(final SchemaImpl owner, final AnnotationImpl _ann, final Ref.Term _term, final Locator _loc) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: nop            
        //     1: ldc2_w          Lcom/sun/xml/xsom/impl/Ref$Term;.class
        //     4: nop            
        //     5: iconst_0       
        //     6: nop            
        //     7: iconst_m1      
        //     8: nop            
        //     9: lload           this
        //    11: iload_0         /* this */
        //    LocalVariableTable:
        //  Start  Length  Slot  Name   Signature
        //  -----  ------  ----  -----  --------------------------------------
        //  0      12      0     this   Lcom/sun/xml/xsom/impl/ParticleImpl;
        //  0      12      1     owner  Lcom/sun/xml/xsom/impl/SchemaImpl;
        //  0      12      2     _ann   Lcom/sun/xml/xsom/impl/AnnotationImpl;
        //  0      12      3     _term  Lcom/sun/xml/xsom/impl/Ref$Term;
        //  0      12      4     _loc   Lorg/xml/sax/Locator;
        // 
        // The error that occurred was:
        // 
        // java.lang.ClassCastException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public XSTerm getTerm() {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: iaload         
        //     1: nop            
        //     2: fload_0         /* this */
        //     3: nop            
        //     4: nop            
        //     5: nop            
        //     6: dstore_1       
        //     7: nop            
        //     8: iconst_4       
        //     9: nop            
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  ------------------------------------
        //  0      10      0     this  Lcom/sun/xml/xsom/impl/ParticleImpl;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public int getMaxOccurs() {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: iload_2        
        //     1: nop            
        //     2: iconst_2       
        //     3: nop            
        //     4: nop            
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  ------------------------------------
        //  0      5       0     this  Lcom/sun/xml/xsom/impl/ParticleImpl;
        // 
        // The error that occurred was:
        // 
        // java.lang.NullPointerException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public int getMinOccurs() {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: nop            
        //     1: faload         
        //     2: nop            
        //     3: fconst_0       
        //     4: nop            
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  ------------------------------------
        //  0      5       0     this  Lcom/sun/xml/xsom/impl/ParticleImpl;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public void redefine(final ModelGroupDeclImpl oldMG) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: aload_1         /* oldMG */
        //     1: nop            
        //     2: iconst_1       
        //     3: nop            
        //     4: aconst_null    
        //     5: nop            
        //     6: laload         
        //     7: nop            
        //     8: faload         
        //     9: nop            
        //    10: aconst_null    
        //    11: nop            
        //    12: lload_2        
        //    13: nop            
        //    14: nop            
        //    15: nop            
        //    16: caload         
        //    17: nop            
        //    18: aconst_null    
        //    19: nop            
        //    20: aconst_null    
        //    21: nop            
        //    22: nop            
        //    23: nop            
        //    24: lconst_1       
        //    25: aload_0         /* this */
        //    26: getfield        com/sun/xml/xsom/impl/ParticleImpl.term:Lcom/sun/xml/xsom/impl/Ref$Term;
        //    29: invokeinterface com/sun/xml/xsom/impl/Ref$Term.getTerm:()Lcom/sun/xml/xsom/XSTerm;
        //    34: areturn        
        //    35: nop            
        //    36: nop            
        //    37: nop            
        //    38: iconst_m1      
        //    39: nop            
        //    40: lload_3        
        //    41: nop            
        //    42: nop            
        //    43: nop            
        //    LocalVariableTable:
        //  Start  Length  Slot  Name   Signature
        //  -----  ------  ----  -----  ------------------------------------------
        //  0      44      0     this   Lcom/sun/xml/xsom/impl/ParticleImpl;
        //  0      44      1     oldMG  Lcom/sun/xml/xsom/impl/ModelGroupDeclImpl;
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
        //     0: nop            
        //     1: aconst_null    
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  ------------------------------------
        //  0      2       0     this  Lcom/sun/xml/xsom/impl/ParticleImpl;
        // 
        // The error that occurred was:
        // 
        // java.lang.NullPointerException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public XSParticle asParticle() {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: astore          this
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  ------------------------------------
        //  0      2       0     this  Lcom/sun/xml/xsom/impl/ParticleImpl;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public XSContentType asEmpty() {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: nop            
        //     1: nop            
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  ------------------------------------
        //  0      2       0     this  Lcom/sun/xml/xsom/impl/ParticleImpl;
        // 
        // The error that occurred was:
        // 
        // java.lang.NullPointerException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public final Object apply(final XSFunction function) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: checkcast       Lcom/sun/xml/xsom/impl/ModelGroupImpl;
        //     3: aload_1         /* function */
        //     4: invokevirtual   com/sun/xml/xsom/impl/ModelGroupImpl.redefine:(Lcom/sun/xml/xsom/impl/ModelGroupDeclImpl;)V
        //     7: return         
        //    LocalVariableTable:
        //  Start  Length  Slot  Name      Signature
        //  -----  ------  ----  --------  -------------------------------------
        //  0      8       0     this      Lcom/sun/xml/xsom/impl/ParticleImpl;
        //  0      8       1     function  Lcom/sun/xml/xsom/visitor/XSFunction;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public final Object apply(final XSContentTypeFunction function) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: nop            
        //     1: nop            
        //     2: nop            
        //     3: aload_2        
        //     4: nop            
        //     5: fload_1         /* function */
        //     6: nop            
        //     7: fload_2        
        //    LocalVariableTable:
        //  Start  Length  Slot  Name      Signature
        //  -----  ------  ----  --------  ------------------------------------------------
        //  0      8       0     this      Lcom/sun/xml/xsom/impl/ParticleImpl;
        //  0      8       1     function  Lcom/sun/xml/xsom/visitor/XSContentTypeFunction;
        // 
        // The error that occurred was:
        // 
        // java.lang.NullPointerException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public final void visit(final XSVisitor visitor) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: nop            
        //     1: fload_2        
        //     2: nop            
        //     3: nop            
        //     4: nop            
        //     5: aconst_null    
        //     6: nop            
        //     7: istore_1        /* visitor */
        //    LocalVariableTable:
        //  Start  Length  Slot  Name     Signature
        //  -----  ------  ----  -------  ------------------------------------
        //  0      8       0     this     Lcom/sun/xml/xsom/impl/ParticleImpl;
        //  0      8       1     visitor  Lcom/sun/xml/xsom/visitor/XSVisitor;
        // 
        // The error that occurred was:
        // 
        // java.lang.NullPointerException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public final void visit(final XSContentTypeVisitor visitor) {
        null;
        -1;
        return (void)null;
    }
    
    public XSContentType getContentType() {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: nop            
        //     1: nop            
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  ------------------------------------
        //  0      2       0     this  Lcom/sun/xml/xsom/impl/ParticleImpl;
        // 
        // The error that occurred was:
        // 
        // java.lang.NullPointerException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
}
