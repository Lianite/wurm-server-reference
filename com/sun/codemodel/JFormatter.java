// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.codemodel;

import java.io.PrintWriter;

public class JFormatter
{
    private int indentLevel;
    private String indentSpace;
    private PrintWriter pw;
    private char lastChar;
    private boolean atBeginningOfLine;
    
    public JFormatter(final PrintWriter s, final String space) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: ineg           
        //     1: lmul           
        //     2: idiv           
        //     3: laload         
        //     4: dstore_2        /* space */
        //     5: ineg           
        //     6: lsub           
        //     7: frem           
        //     8: ladd           
        //     9: ineg           
        //    10: ddiv           
        //    11: frem           
        //    12: istore_0        /* this */
        //    13: aconst_null    
        //    14: nop            
        //    15: iconst_m1      
        //    16: fdiv           
        //    17: idiv           
        //    18: aconst_null    
        //    19: nop            
        //    20: aconst_null    
        //    21: dsub           
        //    22: aconst_null    
        //    23: nop            
        //    24: istore_3       
        //    LocalVariableTable:
        //  Start  Length  Slot  Name   Signature
        //  -----  ------  ----  -----  ------------------------------
        //  0      25      0     this   Lcom/sun/codemodel/JFormatter;
        //  0      25      1     s      Ljava/io/PrintWriter;
        //  0      25      2     space  Ljava/lang/String;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public JFormatter(final PrintWriter s) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: nop            
        //     1: istore          40
        //     3: astore_1        /* s */
        //     4: fmul           
        //     5: ladd           
        //     6: fneg           
        //     7: ladd           
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  ------------------------------
        //  0      8       0     this  Lcom/sun/codemodel/JFormatter;
        //  0      8       1     s     Ljava/io/PrintWriter;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public void close() {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: laload         
        //     1: lneg           
        //     2: ineg           
        //     3: lmul           
        //     4: idiv           
        //     5: laload         
        //     6: fstore_0        /* this */
        //     7: ddiv           
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  ------------------------------
        //  0      8       0     this  Lcom/sun/codemodel/JFormatter;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public JFormatter o() {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: lsub           
        //     1: dadd           
        //     2: ineg           
        //     3: lmul           
        //     4: ddiv           
        //     5: fdiv           
        //     6: istore_1       
        //     7: aload_1        
        //     8: astore_1       
        //     9: dadd           
        //    10: ddiv           
        //    11: ldiv           
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  ------------------------------
        //  0      12      0     this  Lcom/sun/codemodel/JFormatter;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public JFormatter i() {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: drem           
        //     1: lneg           
        //     2: fdiv           
        //     3: laload         
        //     4: dadd           
        //     5: ddiv           
        //     6: isub           
        //     7: lsub           
        //     8: ldiv           
        //     9: ddiv           
        //    10: isub           
        //    11: lsub           
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  ------------------------------
        //  0      12      0     this  Lcom/sun/codemodel/JFormatter;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    private boolean needSpace(final char c1, final char c2) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: lneg           
        //     1: fdiv           
        //     2: laload         
        //     3: dadd           
        //     4: ddiv           
        //     5: isub           
        //     6: lsub           
        //     7: ldiv           
        //     8: ddiv           
        //     9: isub           
        //    10: lsub           
        //    11: idiv           
        //    12: laload         
        //    13: dstore_3       
        //    14: fstore_1        /* c1 */
        //    15: lsub           
        //    16: dadd           
        //    17: idiv           
        //    18: ladd           
        //    19: frem           
        //    20: ladd           
        //    21: ineg           
        //    22: lmul           
        //    23: ddiv           
        //    24: fdiv           
        //    25: istore_0        /* this */
        //    26: dload_3        
        //    27: astore_1        /* c1 */
        //    28: dadd           
        //    29: ddiv           
        //    30: ldiv           
        //    31: laload         
        //    32: drem           
        //    33: lneg           
        //    34: fdiv           
        //    35: laload         
        //    36: dadd           
        //    37: ddiv           
        //    38: isub           
        //    39: lsub           
        //    40: ldiv           
        //    41: ddiv           
        //    42: isub           
        //    43: lsub           
        //    44: idiv           
        //    45: laload         
        //    46: dstore_3       
        //    47: fstore_3       
        //    48: ddiv           
        //    49: frem           
        //    50: ldiv           
        //    51: ladd           
        //    52: ineg           
        //    53: ineg           
        //    54: lsub           
        //    55: frem           
        //    56: istore_0        /* this */
        //    57: aconst_null    
        //    58: nop            
        //    59: lload_2         /* c2 */
        //    60: astore_1        /* c1 */
        //    61: dadd           
        //    62: ddiv           
        //    63: ldiv           
        //    64: laload         
        //    65: drem           
        //    66: lneg           
        //    67: fdiv           
        //    68: laload         
        //    69: dadd           
        //    70: ddiv           
        //    71: isub           
        //    72: lsub           
        //    73: ldiv           
        //    74: ddiv           
        //    75: isub           
        //    76: lsub           
        //    77: idiv           
        //    78: laload         
        //    79: dstore_3       
        //    80: fstore_1        /* c1 */
        //    81: lsub           
        //    82: dadd           
        //    83: idiv           
        //    84: ladd           
        //    85: frem           
        //    86: ladd           
        //    87: ineg           
        //    88: lmul           
        //    89: ddiv           
        //    90: fdiv           
        //    91: istore_0        /* this */
        //    92: aconst_null    
        //    93: nop            
        //    94: istore_3       
        //    95: dload_2         /* c2 */
        //    96: astore_1        /* c1 */
        //    97: dadd           
        //    98: ddiv           
        //    99: ldiv           
        //   100: laload         
        //   101: drem           
        //   102: lneg           
        //   103: fdiv           
        //   104: laload         
        //   105: dadd           
        //   106: ddiv           
        //   107: isub           
        //   108: lsub           
        //   109: ldiv           
        //   110: ddiv           
        //   111: isub           
        //   112: lsub           
        //   113: idiv           
        //   114: laload         
        //   115: dstore_3       
        //   116: aastore        
        //   117: ineg           
        //   118: ladd           
        //   119: ineg           
        //   120: lsub           
        //   121: ldiv           
        //   122: lsub           
        //   123: fdiv           
        //   124: ineg           
        //   125: istore_0        /* this */
        //   126: dload_3        
        //   127: astore_1        /* c1 */
        //   128: dadd           
        //   129: ddiv           
        //   130: ldiv           
        //   131: laload         
        //   132: drem           
        //   133: lneg           
        //   134: fdiv           
        //   135: laload         
        //   136: dadd           
        //   137: ddiv           
        //   138: isub           
        //   139: lsub           
        //   140: ldiv           
        //   141: ddiv           
        //   142: isub           
        //   143: lsub           
        //   144: idiv           
        //   145: laload         
        //   146: dstore_3       
        //   147: fstore_3       
        //   148: ddiv           
        //   149: frem           
        //   150: ldiv           
        //   151: ladd           
        //   152: ineg           
        //   153: ineg           
        //   154: lsub           
        //   155: frem           
        //   156: istore_0        /* this */
        //   157: aconst_null    
        //   158: nop            
        //   159: lload_0         /* this */
        //   160: astore_1        /* c1 */
        //   161: dadd           
        //   162: ddiv           
        //   163: ldiv           
        //   164: laload         
        //   165: drem           
        //   166: lneg           
        //   167: fdiv           
        //   168: laload         
        //   169: dadd           
        //   170: ddiv           
        //   171: isub           
        //   172: lsub           
        //   173: ldiv           
        //   174: ddiv           
        //   175: isub           
        //   176: lsub           
        //   177: idiv           
        //   178: laload         
        //   179: dstore_3       
        //   180: aastore        
        //   181: ineg           
        //   182: ladd           
        //   183: ineg           
        //   184: lsub           
        //   185: ldiv           
        //   186: lsub           
        //   187: fdiv           
        //   188: ineg           
        //   189: istore_0        /* this */
        //   190: aconst_null    
        //   191: nop            
        //   192: aconst_null    
        //   193: fadd           
        //   194: aconst_null    
        //   195: nop            
        //   196: fstore          40
        //   198: astore_1        /* c1 */
        //   199: dadd           
        //   200: ddiv           
        //   201: ldiv           
        //   202: laload         
        //   203: drem           
        //   204: lneg           
        //   205: fdiv           
        //   206: laload         
        //   207: dadd           
        //   208: ddiv           
        //   209: isub           
        //   210: lsub           
        //   211: ldiv           
        //   212: ddiv           
        //   213: isub           
        //   214: lsub           
        //   215: idiv           
        //   216: laload         
        //   217: dstore_3       
        //   218: sastore        
        //   219: ladd           
        //   220: frem           
        //   221: istore_0        /* this */
        //   222: dload_3        
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  ------------------------------
        //  0      223     0     this  Lcom/sun/codemodel/JFormatter;
        //  0      223     1     c1    C
        //  0      223     2     c2    C
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    private void spaceIfNeeded(final char c) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: idiv           
        //     1: lsub           
        //     2: fdiv           
        //     3: fload_2        
        //     4: aconst_null    
        //     5: nop            
        //     6: lload_3        
        //     7: astore_1        /* c */
        //     8: dadd           
        //     9: ddiv           
        //    10: ldiv           
        //    11: laload         
        //    12: drem           
        //    13: lneg           
        //    14: fdiv           
        //    15: laload         
        //    16: dadd           
        //    17: ddiv           
        //    18: isub           
        //    19: lsub           
        //    20: ldiv           
        //    21: ddiv           
        //    22: isub           
        //    23: lsub           
        //    24: idiv           
        //    25: laload         
        //    26: dstore_3       
        //    27: fstore_1        /* c */
        //    28: lsub           
        //    29: fsub           
        //    30: lmul           
        //    31: fdiv           
        //    32: lsub           
        //    33: isub           
        //    34: fstore_0        /* this */
        //    35: idiv           
        //    36: ladd           
        //    37: drem           
        //    38: drem           
        //    39: istore_0        /* this */
        //    40: aconst_null    
        //    41: nop            
        //    42: iconst_0       
        //    43: irem           
        //    44: dmul           
        //    45: dsub           
        //    46: aconst_null    
        //    47: nop            
        //    48: iconst_4       
        //    49: lmul           
        //    50: ldiv           
        //    51: irem           
        //    52: ddiv           
        //    53: frem           
        //    54: ineg           
        //    55: drem           
        //    56: aconst_null    
        //    57: nop            
        //    58: fconst_2       
        //    59: drem           
        //    60: lneg           
        //    61: irem           
        //    62: frem           
        //    63: lsub           
        //    64: drem           
        //    65: drem           
        //    66: dstore_2       
        //    67: ldiv           
        //    68: irem           
        //    69: ddiv           
        //    70: frem           
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  ------------------------------
        //  9      25      2     i     I
        //  0      71      0     this  Lcom/sun/codemodel/JFormatter;
        //  0      71      1     c     C
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public JFormatter p(final char c) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: ddiv           
        //     1: isub           
        //     2: lsub           
        //     3: idiv           
        //     4: laload         
        //     5: dstore_3       
        //     6: fstore_3       
        //     7: ddiv           
        //     8: frem           
        //     9: ldiv           
        //    10: ladd           
        //    11: ineg           
        //    12: ineg           
        //    13: lsub           
        //    14: frem           
        //    15: istore_0        /* this */
        //    16: dload_3        
        //    17: astore_1        /* c */
        //    18: dadd           
        //    19: ddiv           
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  ------------------------------
        //  0      20      0     this  Lcom/sun/codemodel/JFormatter;
        //  0      20      1     c     C
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public JFormatter p(final String s) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: nop            
        //     1: land           
        //     2: nop            
        //     3: f2d            
        //     4: fconst_1       
        //     5: aconst_null    
        //     6: lstore          this
        //     8: ddiv           
        //     9: fconst_1       
        //    10: nop            
        //    11: fdiv           
        //    12: nop            
        //    13: ddiv           
        //    14: fconst_1       
        //    15: nop            
        //    16: fneg           
        //    17: nop            
        //    18: dneg           
        //    19: fconst_1       
        //    20: nop            
        //    21: ishl           
        //    22: nop            
        //    23: lshl           
        //    24: fconst_1       
        //    25: nop            
        //    26: ineg           
        //    27: nop            
        //    28: lneg           
        //    29: fconst_1       
        //    30: nop            
        //    31: frem           
        //    32: nop            
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  ------------------------------
        //  0      33      0     this  Lcom/sun/codemodel/JFormatter;
        //  0      33      1     s     Ljava/lang/String;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public JFormatter nl() {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: ineg           
        //     1: pop            
        //     2: frem           
        //     3: lmul           
        //     4: ineg           
        //     5: lsub           
        //     6: frem           
        //     7: fconst_1       
        //     8: nop            
        //     9: land           
        //    10: nop            
        //    11: l2f            
        //    12: fconst_1       
        //    13: nop            
        //    14: land           
        //    15: nop            
        //    16: l2i            
        //    17: fconst_1       
        //    18: nop            
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  ------------------------------
        //  0      19      0     this  Lcom/sun/codemodel/JFormatter;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public JFormatter g(final JGenerable g) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: nop            
        //     1: dload           99
        //     3: ddiv           
        //     4: ldiv           
        //     5: laload         
        //     6: drem           
        //     7: lneg           
        //     8: fdiv           
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  ------------------------------
        //  0      9       0     this  Lcom/sun/codemodel/JFormatter;
        //  0      9       1     g     Lcom/sun/codemodel/JGenerable;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public JFormatter d(final JDeclaration d) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: aconst_null    
        //     1: dstore_3       
        //     2: fconst_1       
        //     3: aconst_null    
        //     4: fastore        
        //     5: aconst_null    
        //     6: dastore        
        //     7: aconst_null    
        //     8: nop            
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  --------------------------------
        //  0      9       0     this  Lcom/sun/codemodel/JFormatter;
        //  0      9       1     d     Lcom/sun/codemodel/JDeclaration;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public JFormatter s(final JStatement s) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: d2l            
        //     1: fconst_1       
        //     2: aconst_null    
        //     3: dup2           
        //     4: aconst_null    
        //     5: dup2_x1        
        //     6: iconst_4       
        //     7: aconst_null    
        //     8: dup2_x2        
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  ------------------------------
        //  0      9       0     this  Lcom/sun/codemodel/JFormatter;
        //  0      9       1     s     Lcom/sun/codemodel/JStatement;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public JFormatter b(final JVar v) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: fconst_1       
        //     1: aconst_null    
        //     2: fmul           
        //     3: aconst_null    
        //     4: castore        
        //     5: iconst_4       
        //     6: aconst_null    
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  ------------------------------
        //  0      7       0     this  Lcom/sun/codemodel/JFormatter;
        //  0      7       1     v     Lcom/sun/codemodel/JVar;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
}
