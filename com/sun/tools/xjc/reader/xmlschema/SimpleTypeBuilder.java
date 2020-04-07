// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.reader.xmlschema;

import com.sun.xml.xsom.XSRestrictionSimpleType;
import com.sun.xml.xsom.XSUnionSimpleType;
import com.sun.xml.xsom.XSFacet;
import com.sun.msv.grammar.xmlschema.OccurrenceExp;
import com.sun.msv.datatype.xsd.PositiveIntegerType;
import com.sun.xml.xsom.XSListSimpleType;
import com.sun.xml.xsom.XSComponent;
import com.sun.tools.xjc.reader.xmlschema.bindinfo.BIXIdSymbolSpace;
import com.sun.xml.xsom.visitor.XSSimpleTypeFunction;
import com.sun.tools.xjc.grammar.PrimitiveItem;
import com.sun.tools.xjc.grammar.xducer.Transducer;
import com.sun.tools.xjc.reader.xmlschema.bindinfo.BIConversion;
import com.sun.msv.grammar.Expression;
import com.sun.xml.xsom.XSSimpleType;
import java.util.Stack;
import com.sun.msv.grammar.ExpressionPool;

public class SimpleTypeBuilder
{
    protected final BGMBuilder builder;
    public final DatatypeBuilder datatypeBuilder;
    protected final ConversionFinder conversionFinder;
    private final ExpressionPool pool;
    public final Stack refererStack;
    
    SimpleTypeBuilder(final BGMBuilder builder) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     1: irem           
        //     2: lsub           
        //     3: istore_0        /* this */
        //     4: dup_x1         
        //     5: astore_1        /* builder */
        //     6: fmul           
        //     7: ladd           
        //     8: fneg           
        //     9: ladd           
        //    10: laload         
        //    11: lneg           
        //    12: ineg           
        //    13: lmul           
        //    14: idiv           
        //    15: laload         
        //    16: astore_2       
        //    17: ladd           
        //    18: irem           
        //    19: istore_1        /* builder */
        //    20: astore_1        /* builder */
        //    21: fmul           
        //    22: ladd           
        //    23: fneg           
        //    24: ladd           
        //    25: laload         
        //    26: idiv           
        //    27: ladd           
        //    28: fdiv           
        //    29: dsub           
        //    30: laload         
        //    31: aastore        
        //    32: ineg           
        //    33: frem           
        //    34: lmul           
        //    35: fdiv           
        //    36: dsub           
        //    37: istore_0        /* this */
        //    38: astore_1        /* builder */
        //    39: dadd           
        //    40: ddiv           
        //    41: ldiv           
        //    42: laload         
        //    43: drem           
        //    44: lneg           
        //    45: fdiv           
        //    46: laload         
        //    47: ineg           
        //    48: ddiv           
        //    49: ddiv           
        //    50: idiv           
        //    51: drem           
        //    52: laload         
        //    53: ishl           
        //    54: fmul           
        //    55: dadd           
        //    56: laload         
        //    LocalVariableTable:
        //  Start  Length  Slot  Name     Signature
        //  -----  ------  ----  -------  ------------------------------------------------------
        //  0      57      0     this     Lcom/sun/tools/xjc/reader/xmlschema/SimpleTypeBuilder;
        //  0      57      1     builder  Lcom/sun/tools/xjc/reader/xmlschema/BGMBuilder;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public Expression build(final XSSimpleType type) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: drem           
        //     1: lneg           
        //     2: fdiv           
        //     3: laload         
        //     4: ineg           
        //     5: ddiv           
        //     6: ddiv           
        //     7: idiv           
        //     8: drem           
        //     9: laload         
        //    10: ishl           
        //    11: fmul           
        //    12: dadd           
        //    13: laload         
        //    14: ldiv           
        //    15: ddiv           
        //    16: isub           
        //    17: lsub           
        //    18: idiv           
        //    19: laload         
        //    20: fstore_0        /* this */
        //    21: fstore_2        /* e */
        //    22: fdiv           
        //    23: lneg           
        //    24: ldiv           
        //    25: fstore_0        /* this */
        //    26: ddiv           
        //    27: fdiv           
        //    28: drem           
        //    29: ineg           
        //    30: ladd           
        //    31: fdiv           
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  ------------------------------------------------------
        //  0      32      0     this  Lcom/sun/tools/xjc/reader/xmlschema/SimpleTypeBuilder;
        //  0      32      1     type  Lcom/sun/xml/xsom/XSSimpleType;
        //  6      26      2     e     Lcom/sun/msv/grammar/Expression;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    private BIConversion getRefererCustomization() {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: ladd           
        //     1: fdiv           
        //     2: ineg           
        //     3: istore_0        /* this */
        //     4: aconst_null    
        //     5: nop            
        //     6: iconst_0       
        //     7: ddiv           
        //     8: idiv           
        //     9: isub           
        //    10: aconst_null    
        //    11: nop            
        //    12: dload_1        
        //    13: astore_1       
        //    14: dadd           
        //    15: ddiv           
        //    16: ldiv           
        //    17: laload         
        //    18: drem           
        //    19: lneg           
        //    20: fdiv           
        //    21: laload         
        //    22: ineg           
        //    23: ddiv           
        //    24: ddiv           
        //    25: idiv           
        //    26: drem           
        //    27: laload         
        //    28: ishl           
        //    29: fmul           
        //    30: dadd           
        //    31: laload         
        //    32: ldiv           
        //    33: ddiv           
        //    34: isub           
        //    35: lsub           
        //    36: idiv           
        //    37: laload         
        //    38: fstore_0        /* this */
        //    39: fstore_2        /* prop */
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  --------------------------------------------------------
        //  0      40      0     this  Lcom/sun/tools/xjc/reader/xmlschema/SimpleTypeBuilder;
        //  18     22      1     info  Lcom/sun/tools/xjc/reader/xmlschema/bindinfo/BindInfo;
        //  29     11      2     prop  Lcom/sun/tools/xjc/reader/xmlschema/bindinfo/BIProperty;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    private Expression checkRefererCustomization(final XSSimpleType type) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: lneg           
        //     1: ldiv           
        //     2: fstore_0        /* this */
        //     3: ddiv           
        //     4: fdiv           
        //     5: drem           
        //     6: ineg           
        //     7: ladd           
        //     8: fdiv           
        //     9: ineg           
        //    10: istore_0        /* this */
        //    11: istore_3       
        //    12: istore_0        /* this */
        //    13: aconst_null    
        //    14: nop            
        //    15: dadd           
        //    16: dload_2         /* top */
        //    17: astore_1        /* type */
        //    18: fmul           
        //    19: ladd           
        //    20: fneg           
        //    21: ladd           
        //    22: laload         
        //    23: lneg           
        //    24: ineg           
        //    25: lmul           
        //    26: idiv           
        //    27: laload         
        //    28: astore_1        /* type */
        //    29: lmul           
        //    30: drem           
        //    31: ineg           
        //    32: istore_1        /* type */
        //    33: astore_1        /* type */
        //    34: dadd           
        //    35: ddiv           
        //    36: ldiv           
        //    37: laload         
        //    38: drem           
        //    39: lneg           
        //    40: fdiv           
        //    41: laload         
        //    42: ineg           
        //    43: ddiv           
        //    44: ddiv           
        //    45: idiv           
        //    46: drem           
        //    47: laload         
        //    48: ishl           
        //    49: fmul           
        //    50: dadd           
        //    51: laload         
        //    52: ldiv           
        //    53: ddiv           
        //    54: isub           
        //    55: lsub           
        //    56: idiv           
        //    57: laload         
        //    58: fstore_0        /* this */
        //    59: fstore_2        /* top */
        //    60: fdiv           
        //    61: lneg           
        //    62: ldiv           
        //    63: fstore_0        /* this */
        //    64: ddiv           
        //    65: fdiv           
        //    66: drem           
        //    67: ineg           
        //    68: ladd           
        //    69: fdiv           
        //    70: ineg           
        //    71: istore_0        /* this */
        //    72: istore_3        /* aref */
        //    73: istore_0        /* this */
        //    74: dload_3         /* aref */
        //    75: dup_x2         
        //    76: astore_1        /* type */
        //    77: dadd           
        //    78: ddiv           
        //    79: ldiv           
        //    80: laload         
        //    81: drem           
        //    82: lneg           
        //    83: fdiv           
        //    84: laload         
        //    85: ineg           
        //    86: ddiv           
        //    87: ddiv           
        //    88: idiv           
        //    89: drem           
        //    90: laload         
        //    91: ishl           
        //    92: fmul           
        //    93: dadd           
        //    94: laload         
        //    95: ldiv           
        //    96: ddiv           
        //    97: isub           
        //    98: lsub           
        //    99: idiv           
        //   100: laload         
        //   101: fstore_0        /* this */
        //   102: fstore_2        /* top */
        //   103: fdiv           
        //   104: lneg           
        //   105: ldiv           
        //   106: fstore_0        /* this */
        //   107: ddiv           
        //   108: fdiv           
        //   109: drem           
        //   110: ineg           
        //   111: ladd           
        //   112: fdiv           
        //   113: ineg           
        //   114: istore_0        /* this */
        //   115: aconst_null    
        //   116: nop            
        //   117: sipush          26469
        //   120: ineg           
        //   121: fstore_2        /* top */
        //   122: fdiv           
        //   123: lneg           
        //   124: ldiv           
        //   125: astore_2        /* top */
        //   126: lsub           
        //   127: ldiv           
        //   128: fadd           
        //   129: lsub           
        //   130: frem           
        //   131: astore_2        /* top */
        //   132: ddiv           
        //   133: isub           
        //   134: lsub           
        //   135: aconst_null    
        //   136: nop            
        //   137: istore_3       
        //   138: dload_2         /* top */
        //   139: dload_3         /* conv */
        //   140: astore_1        /* type */
        //   141: dadd           
        //   142: ddiv           
        //   143: ldiv           
        //   144: laload         
        //   145: drem           
        //   146: lneg           
        //   147: fdiv           
        //   148: laload         
        //   149: ineg           
        //   150: ddiv           
        //   151: ddiv           
        //   152: idiv           
        //   153: drem           
        //   154: laload         
        //   155: ishl           
        //   156: fmul           
        //   157: dadd           
        //   158: laload         
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  ----------------------------------------------------------
        //  23     22      3     eref  Lcom/sun/xml/xsom/XSElementDecl;
        //  60     22      3     aref  Lcom/sun/xml/xsom/XSAttributeDecl;
        //  97     22      3     tref  Lcom/sun/xml/xsom/XSComplexType;
        //  0      159     0     this  Lcom/sun/tools/xjc/reader/xmlschema/SimpleTypeBuilder;
        //  0      159     1     type  Lcom/sun/xml/xsom/XSSimpleType;
        //  11     148     2     top   Lcom/sun/xml/xsom/XSComponent;
        //  139    20      3     conv  Lcom/sun/tools/xjc/reader/xmlschema/bindinfo/BIConversion;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    private void detectJavaTypeCustomization() {
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
        //     5: ineg           
        //     6: ddiv           
        //     7: ddiv           
        //     8: idiv           
        //     9: drem           
        //    10: laload         
        //    11: ishl           
        //    12: fmul           
        //    13: dadd           
        //    14: laload         
        //    15: ldiv           
        //    16: ddiv           
        //    17: isub           
        //    18: lsub           
        //    19: idiv           
        //    20: laload         
        //    21: bastore        
        //    22: lshl           
        //    23: irem           
        //    24: lsub           
        //    25: castore        
        //    26: drem           
        //    27: lsub           
        //    28: istore_0        /* this */
        //    29: aconst_null    
        //    30: nop            
        //    31: iconst_2       
        //    32: ineg           
        //    33: lshl           
        //    34: irem           
        //    35: lsub           
        //    36: drem           
        //    37: aconst_null    
        //    38: nop            
        //    39: iconst_m1      
        //    40: ldiv           
        //    41: ineg           
        //    42: aconst_null    
        //    43: nop            
        //    44: dload_0         /* this */
        //    45: astore_1        /* info */
        //    46: dadd           
        //    47: ddiv           
        //    48: ldiv           
        //    49: laload         
        //    50: drem           
        //    51: lneg           
        //    52: fdiv           
        //    53: laload         
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  ----------------------------------------------------------
        //  0      54      0     this  Lcom/sun/tools/xjc/reader/xmlschema/SimpleTypeBuilder;
        //  18     36      1     info  Lcom/sun/tools/xjc/reader/xmlschema/bindinfo/BindInfo;
        //  29     25      2     conv  Lcom/sun/tools/xjc/reader/xmlschema/bindinfo/BIConversion;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    private PrimitiveItem buildPrimitiveType(final XSSimpleType type, final Transducer xducer) {
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
        //     5: istore_0        /* this */
        //     6: aconst_null    
        //     7: nop            
        //     8: iconst_4       
        //     9: frem           
        //    10: lsub           
        //    11: fsub           
        //    12: lsub           
        //    13: frem           
        //    14: lsub           
        //    15: frem           
        //    16: aconst_null    
        //    17: nop            
        //    18: iconst_0       
        //    19: lsub           
        //    20: ldiv           
        //    21: ineg           
        //    22: aconst_null    
        //    23: nop            
        //    24: fstore          76
        //    26: fmul           
        //    27: ladd           
        //    28: fneg           
        //    29: ladd           
        //    30: laload         
        //    31: lneg           
        //    32: ineg           
        //    33: lmul           
        //    34: idiv           
        //    35: laload         
        //    36: astore_1        /* type */
        //    37: lmul           
        //    38: drem           
        //    39: ineg           
        //    40: istore_1        /* type */
        //    41: astore_1        /* type */
        //    42: dadd           
        //    43: ddiv           
        //    44: ldiv           
        //    45: laload         
        //    46: drem           
        //    47: lneg           
        //    48: fdiv           
        //    49: laload         
        //    LocalVariableTable:
        //  Start  Length  Slot  Name    Signature
        //  -----  ------  ----  ------  ------------------------------------------------------
        //  0      50      0     this    Lcom/sun/tools/xjc/reader/xmlschema/SimpleTypeBuilder;
        //  0      50      1     type    Lcom/sun/xml/xsom/XSSimpleType;
        //  0      50      2     xducer  Lcom/sun/tools/xjc/grammar/xducer/Transducer;
        //  9      41      3     dt      Lcom/sun/msv/datatype/xsd/XSDatatype;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    private static final void _assert(final boolean b) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: ddiv           
        //     1: ldiv           
        //     2: irem           
        //     3: ddiv           
        //     4: fdiv           
        //     5: lsub           
        //     6: fdiv           
        //     7: ineg           
        //     8: istore_0        /* b */
        //     9: dload_3        
        //    10: dup_x1         
        //    11: aconst_null    
        //    12: nop            
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  ---------
        //  0      13      0     b     Z
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    private class Functor implements XSSimpleTypeFunction
    {
        private final XSSimpleType originalType;
        
        private Functor(final XSSimpleType _type) {
            this.originalType = _type;
        }
        
        private Expression checkConversion(final XSSimpleType type) {
            Transducer t = SimpleTypeBuilder.this.conversionFinder.find(type);
            if (t != null) {
                if (t.getIDSymbolSpace() != null) {
                    BIXIdSymbolSpace ssc = (BIXIdSymbolSpace)SimpleTypeBuilder.this.builder.getBindInfo(SimpleTypeBuilder.this.refererStack.peek()).get(BIXIdSymbolSpace.NAME);
                    if (ssc == null) {
                        ssc = (BIXIdSymbolSpace)SimpleTypeBuilder.this.builder.getBindInfo(type).get(BIXIdSymbolSpace.NAME);
                    }
                    if (ssc != null) {
                        t = ssc.makeTransducer(t);
                    }
                }
                return (Expression)SimpleTypeBuilder.access$100(SimpleTypeBuilder.this, type, t);
            }
            return null;
        }
        
        public Object listSimpleType(final XSListSimpleType type) {
            final Expression e = this.checkConversion(type);
            if (e != null) {
                return e;
            }
            int min = 0;
            int max = -1;
            final XSFacet length = this.originalType.getFacet("length");
            if (length != null) {
                final int v = PositiveIntegerType.load(length.getValue()).intValue();
                max = (min = v);
            }
            else {
                final XSFacet minLength = this.originalType.getFacet("minLength");
                if (minLength != null) {
                    min = PositiveIntegerType.load(minLength.getValue()).intValue();
                }
                final XSFacet maxLength = this.originalType.getFacet("maxLength");
                if (maxLength != null) {
                    final String v2 = maxLength.getValue().trim();
                    if (v2.equals("unbounded")) {
                        max = -1;
                    }
                    else {
                        max = PositiveIntegerType.load(v2).intValue();
                    }
                }
            }
            final Expression item = type.getItemType().apply((XSSimpleTypeFunction<Expression>)this);
            final Expression body = (min > 0) ? SimpleTypeBuilder.access$200(SimpleTypeBuilder.this).createOneOrMore(item) : SimpleTypeBuilder.access$200(SimpleTypeBuilder.this).createZeroOrMore(item);
            return SimpleTypeBuilder.access$200(SimpleTypeBuilder.this).createList((Expression)new OccurrenceExp(body, max, min, item));
        }
        
        public Object unionSimpleType(final XSUnionSimpleType type) {
            final Expression e = this.checkConversion(type);
            if (e != null) {
                return e;
            }
            final int sz = type.getMemberSize();
            Expression exp = Expression.nullSet;
            for (int i = 0; i < sz; ++i) {
                exp = SimpleTypeBuilder.access$200(SimpleTypeBuilder.this).createChoice(exp, (Expression)type.getMember(i).apply((XSSimpleTypeFunction<Expression>)this));
            }
            return exp;
        }
        
        public Object restrictionSimpleType(final XSRestrictionSimpleType type) {
            final Expression e = this.checkConversion(type);
            if (e != null) {
                return e;
            }
            return type.getSimpleBaseType().apply((XSSimpleTypeFunction<Object>)this);
        }
    }
}
