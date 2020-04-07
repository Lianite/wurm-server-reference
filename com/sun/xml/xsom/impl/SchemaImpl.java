// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.xml.xsom.impl;

import com.sun.xml.xsom.visitor.XSFunction;
import com.sun.xml.xsom.visitor.XSVisitor;
import com.sun.xml.xsom.XSType;
import com.sun.xml.xsom.XSComplexType;
import com.sun.xml.xsom.XSSimpleType;
import com.sun.xml.xsom.XSModelGroupDecl;
import com.sun.xml.xsom.XSNotation;
import com.sun.xml.xsom.XSAttGroupDecl;
import com.sun.xml.xsom.XSElementDecl;
import java.util.Iterator;
import com.sun.xml.xsom.XSAttributeDecl;
import java.util.Map;
import org.xml.sax.Locator;
import com.sun.xml.xsom.XSAnnotation;
import com.sun.xml.xsom.XSSchema;

public class SchemaImpl implements XSSchema
{
    protected final SchemaSetImpl parent;
    private final String targetNamespace;
    private XSAnnotation annotation;
    private final Locator locator;
    private final Map atts;
    private final Map elems;
    private final Map attGroups;
    private final Map notations;
    private final Map modelGroups;
    private final Map simpleTypes;
    private final Map complexTypes;
    
    public SchemaImpl(final SchemaSetImpl _parent, final Locator loc, final String tns) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: dadd           
        //     1: idiv           
        //     2: istore_0        /* this */
        //     3: aconst_null    
        //     4: nop            
        //     5: lload           105
        //     7: ineg           
        //     8: lsub           
        //     9: frem           
        //    10: ladd           
        //    11: ineg           
        //    12: lsub           
        //    13: astore_2        /* loc */
        //    14: ddiv           
        //    15: isub           
        //    16: lsub           
        //    17: idiv           
        //    18: dstore_0        /* this */
        //    19: frem           
        //    20: ddiv           
        //    21: lneg           
        //    22: irem           
        //    23: fstore_1        /* _parent */
        //    24: lsub           
        //    25: dadd           
        //    26: idiv           
        //    27: drem           
        //    28: aconst_null    
        //    29: nop            
        //    30: istore_0        /* this */
        //    31: dload_2         /* loc */
        //    32: dload_3         /* tns */
        //    33: astore_1        /* _parent */
        //    34: fmul           
        //    35: ladd           
        //    36: fneg           
        //    37: ladd           
        //    38: laload         
        //    39: lneg           
        //    40: ineg           
        //    41: lmul           
        //    42: idiv           
        //    43: laload         
        //    44: dstore_2        /* loc */
        //    45: ineg           
        //    46: lsub           
        //    47: frem           
        //    48: ladd           
        //    49: ineg           
        //    50: ddiv           
        //    51: frem           
        //    52: istore_1        /* _parent */
        //    53: astore_1        /* _parent */
        //    54: dadd           
        //    55: ddiv           
        //    56: ldiv           
        //    57: laload         
        //    58: drem           
        //    59: lneg           
        //    60: fdiv           
        //    61: laload         
        //    62: ishl           
        //    63: ldiv           
        //    64: idiv           
        //    65: laload         
        //    66: ishl           
        //    67: drem           
        //    68: ddiv           
        //    69: ldiv           
        //    70: laload         
        //    71: pop2           
        //    72: aastore        
        //    73: astore_2        /* loc */
        //    74: ddiv           
        //    75: isub           
        //    76: lsub           
        //    77: idiv           
        //    78: dstore_0        /* this */
        //    79: frem           
        //    80: ddiv           
        //    81: lneg           
        //    82: irem           
        //    83: fstore_1        /* _parent */
        //    84: lsub           
        //    85: dadd           
        //    86: idiv           
        //    87: istore_0        /* this */
        //    88: istore_3        /* tns */
        //    89: istore_0        /* this */
        //    90: aconst_null    
        //    91: nop            
        //    92: iload           97
        //    94: isub           
        //    95: isub           
        //    96: dstore_2        /* loc */
        //    97: isub           
        //    98: lsub           
        //    99: fdiv           
        //   100: ineg           
        //   101: lmul           
        //   102: ineg           
        //   103: lshl           
        //   104: fstore_0        /* this */
        //   105: ddiv           
        //   106: fdiv           
        //   107: drem           
        //   108: ineg           
        //    LocalVariableTable:
        //  Start  Length  Slot  Name     Signature
        //  -----  ------  ----  -------  -------------------------------------
        //  0      109     0     this     Lcom/sun/xml/xsom/impl/SchemaImpl;
        //  0      109     1     _parent  Lcom/sun/xml/xsom/impl/SchemaSetImpl;
        //  0      109     2     loc      Lorg/xml/sax/Locator;
        //  0      109     3     tns      Ljava/lang/String;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public SchemaSetImpl getParent() {
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
        //  -----  ------  ----  ----  ----------------------------------
        //  0      5       0     this  Lcom/sun/xml/xsom/impl/SchemaImpl;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public String getTargetNamespace() {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: ineg           
        //     1: lshl           
        //     2: fstore_0        /* this */
        //     3: ddiv           
        //     4: fdiv           
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  ----------------------------------
        //  0      5       0     this  Lcom/sun/xml/xsom/impl/SchemaImpl;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public XSSchema getOwnerSchema() {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: dload_3        
        //     1: astore_1       
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  ----------------------------------
        //  0      2       0     this  Lcom/sun/xml/xsom/impl/SchemaImpl;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public void setAnnotation(final XSAnnotation a) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: aastore        
        //     1: lmul           
        //     2: ldiv           
        //     3: irem           
        //     4: idiv           
        //     5: lsub           
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  ----------------------------------
        //  0      6       0     this  Lcom/sun/xml/xsom/impl/SchemaImpl;
        //  0      6       1     a     Lcom/sun/xml/xsom/XSAnnotation;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public XSAnnotation getAnnotation() {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: lsub           
        //     1: bastore        
        //     2: lshl           
        //     3: irem           
        //     4: lsub           
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  ----------------------------------
        //  0      5       0     this  Lcom/sun/xml/xsom/impl/SchemaImpl;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public Locator getLocator() {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: astore_1       
        //     1: dadd           
        //     2: ddiv           
        //     3: ldiv           
        //     4: laload         
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  ----------------------------------
        //  0      5       0     this  Lcom/sun/xml/xsom/impl/SchemaImpl;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public void addAttributeDecl(final XSAttributeDecl newDecl) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: fdiv           
        //     1: dsub           
        //     2: laload         
        //     3: aastore        
        //     4: ineg           
        //     5: frem           
        //     6: lmul           
        //     7: fdiv           
        //     8: dsub           
        //     9: istore_0        /* this */
        //    10: dload_3        
        //    11: astore_1        /* newDecl */
        //    12: dadd           
        //    13: ddiv           
        //    14: ldiv           
        //    15: laload         
        //    16: drem           
        //    17: lneg           
        //    LocalVariableTable:
        //  Start  Length  Slot  Name     Signature
        //  -----  ------  ----  -------  ----------------------------------
        //  0      18      0     this     Lcom/sun/xml/xsom/impl/SchemaImpl;
        //  0      18      1     newDecl  Lcom/sun/xml/xsom/XSAttributeDecl;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public XSAttributeDecl getAttributeDecl(final String name) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: astore_1        /* name */
        //     1: dadd           
        //     2: ddiv           
        //     3: ldiv           
        //     4: laload         
        //     5: drem           
        //     6: lneg           
        //     7: fdiv           
        //     8: laload         
        //     9: ishl           
        //    10: ldiv           
        //    11: idiv           
        //    12: laload         
        //    13: ishl           
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  ----------------------------------
        //  0      14      0     this  Lcom/sun/xml/xsom/impl/SchemaImpl;
        //  0      14      1     name  Ljava/lang/String;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public Iterator iterateAttributeDecls() {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: ishl           
        //     1: bastore        
        //     2: lshl           
        //     3: irem           
        //     4: lsub           
        //     5: istore_0        /* this */
        //     6: dup_x1         
        //     7: dload_3        
        //     8: sastore        
        //     9: aconst_null    
        //    10: nop            
        //    11: lload_2        
        //    12: astore_1       
        //    13: dadd           
        //    14: ddiv           
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  ----------------------------------
        //  0      15      0     this  Lcom/sun/xml/xsom/impl/SchemaImpl;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public void addElementDecl(final XSElementDecl newDecl) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: ladd           
        //     1: laload         
        //     2: lneg           
        //     3: ineg           
        //     4: lmul           
        //     5: idiv           
        //     6: laload         
        //     7: astore_2       
        //     8: ladd           
        //     9: irem           
        //    10: istore_1        /* newDecl */
        //    11: astore_1        /* newDecl */
        //    12: fmul           
        //    13: ladd           
        //    14: fneg           
        //    15: ladd           
        //    16: laload         
        //    17: idiv           
        //    LocalVariableTable:
        //  Start  Length  Slot  Name     Signature
        //  -----  ------  ----  -------  ----------------------------------
        //  0      18      0     this     Lcom/sun/xml/xsom/impl/SchemaImpl;
        //  0      18      1     newDecl  Lcom/sun/xml/xsom/XSElementDecl;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public XSElementDecl getElementDecl(final String name) {
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
        //     5: dsub           
        //     6: laload         
        //     7: aastore        
        //     8: ineg           
        //     9: frem           
        //    10: lmul           
        //    11: fdiv           
        //    12: dsub           
        //    13: istore_0        /* this */
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  ----------------------------------
        //  0      14      0     this  Lcom/sun/xml/xsom/impl/SchemaImpl;
        //  0      14      1     name  Ljava/lang/String;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public Iterator iterateElementDecls() {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: lneg           
        //     1: ineg           
        //     2: lmul           
        //     3: idiv           
        //     4: laload         
        //     5: dstore_2       
        //     6: ineg           
        //     7: lsub           
        //     8: frem           
        //     9: ladd           
        //    10: ineg           
        //    11: ddiv           
        //    12: frem           
        //    13: istore_1       
        //    14: astore_1       
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  ----------------------------------
        //  0      15      0     this  Lcom/sun/xml/xsom/impl/SchemaImpl;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public void addAttGroupDecl(final XSAttGroupDecl newDecl) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: ineg           
        //     1: lmul           
        //     2: idiv           
        //     3: laload         
        //     4: astore_2       
        //     5: ladd           
        //     6: irem           
        //     7: istore_1        /* newDecl */
        //     8: astore_1        /* newDecl */
        //     9: fmul           
        //    10: ladd           
        //    11: fneg           
        //    12: ladd           
        //    13: laload         
        //    14: idiv           
        //    15: ladd           
        //    16: fdiv           
        //    17: dsub           
        //    LocalVariableTable:
        //  Start  Length  Slot  Name     Signature
        //  -----  ------  ----  -------  ----------------------------------
        //  0      18      0     this     Lcom/sun/xml/xsom/impl/SchemaImpl;
        //  0      18      1     newDecl  Lcom/sun/xml/xsom/XSAttGroupDecl;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public XSAttGroupDecl getAttGroupDecl(final String name) {
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
        //     7: ishl           
        //     8: ldiv           
        //     9: idiv           
        //    10: laload         
        //    11: ishl           
        //    12: drem           
        //    13: ddiv           
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  ----------------------------------
        //  0      14      0     this  Lcom/sun/xml/xsom/impl/SchemaImpl;
        //  0      14      1     name  Ljava/lang/String;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public Iterator iterateAttGroupDecls() {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: laload         
        //     1: pop2           
        //     2: aastore        
        //     3: bastore        
        //     4: lshl           
        //     5: irem           
        //     6: lsub           
        //     7: istore_0        /* this */
        //     8: istore_3       
        //     9: istore_0        /* this */
        //    10: aconst_null    
        //    11: nop            
        //    12: iconst_2       
        //    13: fneg           
        //    14: lmul           
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  ----------------------------------
        //  0      15      0     this  Lcom/sun/xml/xsom/impl/SchemaImpl;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public void addNotation(final XSNotation newDecl) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: nop            
        //     1: fload_2        
        //     2: astore_1        /* newDecl */
        //     3: dadd           
        //     4: ddiv           
        //     5: ldiv           
        //     6: laload         
        //     7: drem           
        //     8: lneg           
        //     9: fdiv           
        //    10: laload         
        //    11: ishl           
        //    12: ldiv           
        //    13: idiv           
        //    14: laload         
        //    15: ishl           
        //    16: drem           
        //    17: ddiv           
        //    LocalVariableTable:
        //  Start  Length  Slot  Name     Signature
        //  -----  ------  ----  -------  ----------------------------------
        //  0      18      0     this     Lcom/sun/xml/xsom/impl/SchemaImpl;
        //  0      18      1     newDecl  Lcom/sun/xml/xsom/XSNotation;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public XSNotation getNotation(final String name) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: astore_1        /* name */
        //     1: fmul           
        //     2: ladd           
        //     3: fneg           
        //     4: ladd           
        //     5: laload         
        //     6: idiv           
        //     7: ladd           
        //     8: fdiv           
        //     9: dsub           
        //    10: laload         
        //    11: iastore        
        //    12: fadd           
        //    13: fmul           
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  ----------------------------------
        //  0      14      0     this  Lcom/sun/xml/xsom/impl/SchemaImpl;
        //  0      14      1     name  Ljava/lang/String;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public Iterator iterateNotations() {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: dsub           
        //     1: fdiv           
        //     2: lstore_2       
        //     3: ineg           
        //     4: ineg           
        //     5: frem           
        //     6: lmul           
        //     7: fadd           
        //     8: lneg           
        //     9: ineg           
        //    10: lsub           
        //    11: drem           
        //    12: aconst_null    
        //    13: nop            
        //    14: faload         
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  ----------------------------------
        //  0      15      0     this  Lcom/sun/xml/xsom/impl/SchemaImpl;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public void addModelGroupDecl(final XSModelGroupDecl newDecl) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: astore_1        /* newDecl */
        //     1: dadd           
        //     2: ddiv           
        //     3: ldiv           
        //     4: laload         
        //     5: drem           
        //     6: lneg           
        //     7: fdiv           
        //     8: laload         
        //     9: ishl           
        //    10: ldiv           
        //    11: idiv           
        //    12: laload         
        //    13: ishl           
        //    14: drem           
        //    15: ddiv           
        //    16: ldiv           
        //    17: laload         
        //    LocalVariableTable:
        //  Start  Length  Slot  Name     Signature
        //  -----  ------  ----  -------  -----------------------------------
        //  0      18      0     this     Lcom/sun/xml/xsom/impl/SchemaImpl;
        //  0      18      1     newDecl  Lcom/sun/xml/xsom/XSModelGroupDecl;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public XSModelGroupDecl getModelGroupDecl(final String name) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: istore_0        /* this */
        //     1: aconst_null    
        //     2: nop            
        //     3: fstore          40
        //     5: dload_3        
        //     6: astore_1        /* name */
        //     7: fmul           
        //     8: ladd           
        //     9: fneg           
        //    10: ladd           
        //    11: laload         
        //    12: lneg           
        //    13: ineg           
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  ----------------------------------
        //  0      14      0     this  Lcom/sun/xml/xsom/impl/SchemaImpl;
        //  0      14      1     name  Ljava/lang/String;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public Iterator iterateModelGroupDecls() {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: ineg           
        //     1: lsub           
        //     2: aconst_null    
        //     3: nop            
        //     4: fstore          40
        //     6: astore_1       
        //     7: fmul           
        //     8: ladd           
        //     9: fneg           
        //    10: ladd           
        //    11: laload         
        //    12: idiv           
        //    13: ladd           
        //    14: fdiv           
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  ----------------------------------
        //  0      15      0     this  Lcom/sun/xml/xsom/impl/SchemaImpl;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public void addSimpleType(final XSSimpleType newDecl) {
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
        //     6: ishl           
        //     7: ldiv           
        //     8: idiv           
        //     9: laload         
        //    10: ishl           
        //    11: drem           
        //    12: ddiv           
        //    13: ldiv           
        //    14: laload         
        //    15: fstore_3       
        //    16: ddiv           
        //    17: frem           
        //    LocalVariableTable:
        //  Start  Length  Slot  Name     Signature
        //  -----  ------  ----  -------  ----------------------------------
        //  0      18      0     this     Lcom/sun/xml/xsom/impl/SchemaImpl;
        //  0      18      1     newDecl  Lcom/sun/xml/xsom/XSSimpleType;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public XSSimpleType getSimpleType(final String name) {
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
        //     5: aastore        
        //     6: ineg           
        //     7: frem           
        //     8: lmul           
        //     9: fdiv           
        //    10: dsub           
        //    11: istore_0        /* this */
        //    12: astore_1        /* name */
        //    13: fmul           
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  ----------------------------------
        //  0      14      0     this  Lcom/sun/xml/xsom/impl/SchemaImpl;
        //  0      14      1     name  Ljava/lang/String;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public Iterator iterateSimpleTypes() {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: astore_1       
        //     1: fmul           
        //     2: ladd           
        //     3: fneg           
        //     4: ladd           
        //     5: laload         
        //     6: ineg           
        //     7: lsub           
        //     8: ishl           
        //     9: ineg           
        //    10: laload         
        //    11: lastore        
        //    12: ladd           
        //    13: frem           
        //    14: drem           
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  ----------------------------------
        //  0      15      0     this  Lcom/sun/xml/xsom/impl/SchemaImpl;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public void addComplexType(final XSComplexType newDecl) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: lsub           
        //     1: drem           
        //     2: irem           
        //     3: ladd           
        //     4: dadd           
        //     5: lsub           
        //     6: fstore_0        /* this */
        //     7: ddiv           
        //     8: fdiv           
        //     9: ineg           
        //    10: lsub           
        //    11: ishl           
        //    12: ineg           
        //    13: istore_0        /* this */
        //    14: aconst_null    
        //    15: nop            
        //    16: irem           
        //    17: dload_2        
        //    LocalVariableTable:
        //  Start  Length  Slot  Name     Signature
        //  -----  ------  ----  -------  ----------------------------------
        //  0      18      0     this     Lcom/sun/xml/xsom/impl/SchemaImpl;
        //  0      18      1     newDecl  Lcom/sun/xml/xsom/XSComplexType;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public XSComplexType getComplexType(final String name) {
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
        //     8: istore_1        /* name */
        //     9: astore_1        /* name */
        //    10: dadd           
        //    11: ddiv           
        //    12: ldiv           
        //    13: laload         
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  ----------------------------------
        //  0      14      0     this  Lcom/sun/xml/xsom/impl/SchemaImpl;
        //  0      14      1     name  Ljava/lang/String;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public Iterator iterateComplexTypes() {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: ladd           
        //     1: fneg           
        //     2: ladd           
        //     3: ishl           
        //     4: laload         
        //     5: ishl           
        //     6: ldiv           
        //     7: idiv           
        //     8: laload         
        //     9: fdiv           
        //    10: ladd           
        //    11: ldiv           
        //    12: lsub           
        //    13: drem           
        //    14: irem           
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  ----------------------------------
        //  0      15      0     this  Lcom/sun/xml/xsom/impl/SchemaImpl;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public XSType getType(final String name) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: dload_3        
        //     1: astore_1        /* name */
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
        //    13: laload         
        //    14: ishl           
        //    15: drem           
        //    16: ddiv           
        //    17: ldiv           
        //    18: laload         
        //    19: pop2           
        //    20: aastore        
        //    21: aastore        
        //    22: dadd           
        //    23: imul           
        //    24: lsub           
        //    25: ldiv           
        //    26: ladd           
        //    27: aastore        
        //    28: lsub           
        //    29: ineg           
        //    30: istore_0        /* this */
        //    31: aconst_null    
        //    32: nop            
        //    33: lconst_1       
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  ----------------------------------
        //  0      34      0     this  Lcom/sun/xml/xsom/impl/SchemaImpl;
        //  0      34      1     name  Ljava/lang/String;
        //  14     20      2     r     Lcom/sun/xml/xsom/XSType;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public Iterator iterateTypes() {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: swap           
        //     1: nop            
        //     2: sastore        
        //     3: fconst_1       
        //     4: nop            
        //     5: iadd           
        //     6: nop            
        //     7: sastore        
        //     8: fconst_1       
        //     9: nop            
        //    10: fadd           
        //    11: nop            
        //    12: sastore        
        //    13: fconst_1       
        //    14: nop            
        //    15: dadd           
        //    16: nop            
        //    17: sastore        
        //    18: fconst_1       
        //    19: nop            
        //    20: lsub           
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  ----------------------------------
        //  0      21      0     this  Lcom/sun/xml/xsom/impl/SchemaImpl;
        //  5      16      1     itr1  Ljava/util/Iterator;
        //  10     11      2     itr2  Ljava/util/Iterator;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public void visit(final XSVisitor visitor) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: irem           
        //     1: ineg           
        //     2: lmul           
        //     3: ddiv           
        //     4: fdiv           
        //     5: fconst_1       
        //     6: nop            
        //     7: iastore        
        //    LocalVariableTable:
        //  Start  Length  Slot  Name     Signature
        //  -----  ------  ----  -------  ------------------------------------
        //  0      8       0     this     Lcom/sun/xml/xsom/impl/SchemaImpl;
        //  0      8       1     visitor  Lcom/sun/xml/xsom/visitor/XSVisitor;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public Object apply(final XSFunction function) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: aconst_null    
        //     1: dup            
        //     2: aconst_null    
        //     3: dup_x1         
        //     4: aconst_null    
        //     5: nop            
        //     6: lload_2        
        //     7: dadd           
        //    LocalVariableTable:
        //  Start  Length  Slot  Name      Signature
        //  -----  ------  ----  --------  -------------------------------------
        //  0      8       0     this      Lcom/sun/xml/xsom/impl/SchemaImpl;
        //  0      8       1     function  Lcom/sun/xml/xsom/visitor/XSFunction;
        // 
        // The error that occurred was:
        // 
        // java.lang.NullPointerException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
}
