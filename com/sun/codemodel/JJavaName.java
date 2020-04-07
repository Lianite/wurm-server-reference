// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.codemodel;

import java.util.HashSet;

public class JJavaName
{
    private static HashSet reservedKeywords;
    
    public JJavaName() {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: lsub           
        //     1: aconst_null    
        //     2: nop            
        //     3: lconst_0       
        //     4: irem           
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  -----------------------------
        //  0      5       0     this  Lcom/sun/codemodel/JJavaName;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public static boolean isJavaIdentifier(final String s) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: iconst_2       
        //     1: drem           
        //     2: lneg           
        //     3: irem           
        //     4: lsub           
        //     5: frem           
        //     6: aconst_null    
        //     7: nop            
        //     8: iconst_3       
        //     9: drem           
        //    10: dneg           
        //    11: lmul           
        //    12: ineg           
        //    13: dadd           
        //    14: imul           
        //    15: aconst_null    
        //    16: nop            
        //    17: fconst_1       
        //    18: drem           
        //    19: lshl           
        //    20: fdiv           
        //    21: dadd           
        //    22: imul           
        //    23: frem           
        //    24: ddiv           
        //    25: fdiv           
        //    26: lmul           
        //    27: ishr           
        //    28: lsub           
        //    29: isub           
        //    30: aconst_null    
        //    31: nop            
        //    32: iconst_2       
        //    33: ineg           
        //    34: imul           
        //    35: frem           
        //    36: ddiv           
        //    37: dneg           
        //    38: aconst_null    
        //    39: nop            
        //    40: iconst_3       
        //    41: ineg           
        //    42: imul           
        //    43: frem           
        //    44: ddiv           
        //    45: dneg           
        //    46: drem           
        //    47: aconst_null    
        //    48: nop            
        //    49: lconst_0       
        //    50: ineg           
        //    51: frem           
        //    52: ladd           
        //    53: fdiv           
        //    54: drem           
        //    55: lmul           
        //    56: lsub           
        //    57: fdiv           
        //    58: ineg           
        //    59: aconst_null    
        //    60: nop            
        //    61: iconst_0       
        //    62: ineg           
        //    63: frem           
        //    64: lshl           
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  ------------------
        //  36     27      1     i     I
        //  0      65      0     s     Ljava/lang/String;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public static boolean isJavaPackageName(final String s) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: aconst_null    
        //     1: nop            
        //     2: iconst_3       
        //     3: dload_2        
        //     4: iaload         
        //     5: aload_1        
        //     6: dload_3        
        //     7: fsub           
        //     8: lsub           
        //     9: aconst_null    
        //    10: nop            
        //    11: iconst_2       
        //    12: fload_2        
        //    13: daload         
        //    14: fneg           
        //    15: lsub           
        //    16: drem           
        //    17: aconst_null    
        //    18: nop            
        //    19: lconst_0       
        //    20: dload_2        
        //    21: iaload         
        //    22: aload_0         /* s */
        //    23: dload_3        
        //    24: ldiv           
        //    25: ddiv           
        //    26: lneg           
        //    27: drem           
        //    28: lsub           
        //    29: aconst_null    
        //    30: nop            
        //    31: iconst_3       
        //    32: fload_2        
        //    33: daload         
        //    34: ldiv           
        //    35: lmul           
        //    36: drem           
        //    37: lsub           
        //    38: aconst_null    
        //    39: nop            
        //    40: iconst_2       
        //    41: dload_2        
        //    42: iaload         
        //    43: aload_1         /* idx */
        //    44: dload_3        
        //    45: fsub           
        //    46: aconst_null    
        //    47: nop            
        //    48: iconst_3       
        //    49: dload_2        
        //    50: iaload         
        //    51: aload_1         /* idx */
        //    52: dload_3        
        //    53: dadd           
        //    54: imul           
        //    55: aconst_null    
        //    56: nop            
        //    57: iconst_3       
        //    58: fload_2        
        //    59: daload         
        //    60: dadd           
        //    61: imul           
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  ------------------
        //  14     43      1     idx   I
        //  0      62      0     s     Ljava/lang/String;
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
        //     0: nop            
        //     1: lconst_0       
        //     2: dload_2        
        //     3: iaload         
        //     4: aload_0        
        //     5: dload_3        
        //     6: fadd           
        //     7: ladd           
        //     8: drem           
        //     9: lmul           
        //    10: drem           
        //    11: aconst_null    
        //    12: nop            
        //    13: iconst_4       
        //    14: fload_2        
        //    15: daload         
        //    16: fadd           
        //    17: ladd           
        //    18: drem           
        //    19: lsub           
        //    20: drem           
        //    21: aconst_null    
        //    22: nop            
        //    23: iconst_5       
        //    24: dload_2        
        //    25: iaload         
        //    26: aload_0        
        //    27: dload_3        
        //    28: ladd           
        //    29: ishl           
        //    30: lmul           
        //    31: drem           
        //    32: aconst_null    
        //    33: nop            
        //    34: iconst_3       
        //    35: fload_2        
        //    36: daload         
        //    37: ladd           
        //    38: ishl           
        //    39: lsub           
        //    40: drem           
        //    41: aconst_null    
        //    42: nop            
        //    43: iconst_3       
        //    44: dload_2        
        //    45: iaload         
        //    46: aload_1        
        //    47: dload_3        
        //    48: lmul           
        //    49: drem           
        //    50: aconst_null    
        //    51: nop            
        //    52: iconst_3       
        //    53: fload_2        
        //    54: daload         
        //    55: lmul           
        //    56: drem           
        //    57: lsub           
        //    58: drem           
        //    59: aconst_null    
        //    60: nop            
        //    61: iconst_3       
        //    62: dload_2        
        //    63: iaload         
        //    64: aload_1        
        //    65: dload_3        
        //    66: lneg           
        //    67: drem           
        //    68: aconst_null    
        //    69: nop            
        //    70: iconst_3       
        //    71: fload_2        
        //    72: daload         
        //    73: lneg           
        //    74: drem           
        //    75: lsub           
        //    76: drem           
        //    77: aconst_null    
        //    78: nop            
        //    79: iconst_2       
        //    80: dload_2        
        //    81: iaload         
        //    82: aload_1        
        //    83: dload_3        
        //    84: drem           
        //    85: aconst_null    
        //    86: nop            
        //    87: iconst_0       
        //    88: fload_2        
        //    89: daload         
        //    90: drem           
        //    91: aconst_null    
        //    92: nop            
        //    93: iconst_5       
        //    94: dload_2        
        //    95: iaload         
        //    96: aload_0        
        //    97: dload_3        
        //    98: fsub           
        //    99: ddiv           
        //   100: ddiv           
        //   101: ineg           
        //   102: aconst_null    
        //   103: nop            
        //   104: iconst_3       
        //   105: fload_2        
        //   106: daload         
        //   107: fsub           
        //   108: lsub           
        //   109: lsub           
        //   110: ineg           
        //   111: aconst_null    
        //   112: nop            
        //   113: iconst_3       
        //   114: dload_2        
        //   115: iaload         
        //   116: aload_1        
        //   117: dload_3        
        //   118: lmul           
        //   119: ishl           
        //   120: aconst_null    
        //   121: nop            
        //   122: iconst_3       
        //   123: fload_2        
        //   124: daload         
        //   125: lmul           
        //   126: ishl           
        //   127: lsub           
        //   128: drem           
        //   129: aconst_null    
        //   130: nop            
        //   131: iconst_3       
        //   132: dload_2        
        //   133: iaload         
        //   134: aload_1        
        //   135: dload_3        
        //   136: lsub           
        //   137: ishl           
        //   138: aconst_null    
        //   139: nop            
        //   140: iconst_3       
        //   141: fload_2        
        //   142: daload         
        //   143: lmul           
        //   144: dadd           
        //   145: lsub           
        //   146: drem           
        //   147: aconst_null    
        //   148: nop            
        //   149: iconst_3       
        //   150: dload_2        
        //   151: iaload         
        //   152: aload_1        
        //   153: dload_3        
        //   154: fdiv           
        //   155: ishl           
        //   156: aconst_null    
        //   157: nop            
        //   158: iconst_3       
        //   159: fload_2        
        //   160: daload         
        //   161: fdiv           
        //   162: ishl           
        //   163: lsub           
        //   164: drem           
        //   165: aconst_null    
        //   166: nop            
        //   167: iconst_2       
        //   168: dload_2        
        //   169: iaload         
        //   170: aload_1        
        //   171: dload_3        
        //   172: ishl           
        //   173: aconst_null    
        //   174: nop            
        //   175: iconst_2       
        //   176: fload_2        
        //   177: daload         
        //   178: ishl           
        //   179: lsub           
        //   180: drem           
        //   181: aconst_null    
        //   182: nop            
        //   183: iconst_2       
        //   184: dload_2        
        //   185: iaload         
        //   186: aload_1        
        //   187: dload_3        
        //   188: lshl           
        //   189: aconst_null    
        //   190: nop            
        //   191: iconst_2       
        //   192: fload_2        
        //   193: daload         
        //   194: lmul           
        //   195: lsub           
        //   196: drem           
        //   197: aconst_null    
        //   198: nop            
        //   199: iconst_1       
        //   200: dload_2        
        //   201: iaload         
        //   202: aload_1        
        //   203: dload_3        
        //   204: aconst_null    
        //   205: nop            
        //   206: lload_3        
        //   207: dadd           
        //   208: ddiv           
        //   209: ldiv           
        //   210: laload         
        //   211: drem           
        //   212: lneg           
        //   213: fdiv           
        //   214: laload         
        //   215: dadd           
        //   216: ddiv           
        //   217: isub           
        //   218: lsub           
        //   219: ldiv           
        //   220: ddiv           
        //   221: isub           
        //   222: lsub           
        //   223: idiv           
        //   224: laload         
        //   225: dstore_3       
        //   226: dstore_3       
        //   227: ladd           
        //   228: fneg           
        //   229: ladd           
        //   230: astore_3       
        //   231: ladd           
        //   232: ldiv           
        //   233: lsub           
        //   234: fload_2        
        //   235: fstore_2       
        //   236: fdiv           
        //   237: ineg           
        //   238: frem           
        //   239: lshl           
        //   240: fconst_1       
        //   241: nop            
        //   242: ior            
        //   243: aconst_null    
        //   244: iaload         
        //   245: aconst_null    
        //   246: nop            
        //   247: iload_1        
        //   248: dadd           
        //   249: ddiv           
        //   250: ldiv           
        //   251: laload         
        //   252: drem           
        //   253: lneg           
        //   254: fdiv           
        //   255: laload         
        //   256: dadd           
        //   257: ddiv           
        //   258: isub           
        //   259: lsub           
        //   260: ldiv           
        //   261: ddiv           
        //   262: isub           
        //   263: lsub           
        //   264: idiv           
        //   265: laload         
        //   266: dstore_3       
        //   267: dstore_3       
        //   268: ladd           
        //   269: fneg           
        //   270: ladd           
        //   271: astore_3       
        //   272: ladd           
        //   273: ldiv           
        //   274: lsub           
        //   275: aconst_null    
        //   276: nop            
        //   277: bipush          106
        //   279: ladd           
        //   280: fneg           
        //   281: ladd           
        //   282: laload         
        //   283: idiv           
        //   284: ladd           
        //   285: fdiv           
        //   286: dsub           
        //   287: laload         
        //   288: iastore        
        //   289: fadd           
        //   290: fmul           
        //   291: lsub           
        //   292: dadd           
        //   293: ineg           
        //   294: aconst_null    
        //   295: nop            
        //   296: iconst_3       
        //   297: idiv           
        //   298: lsub           
        //   299: fdiv           
        //   300: dsub           
        //   301: ineg           
        //   302: imul           
        //   303: aconst_null    
        //   304: nop            
        //   305: iconst_0       
        //   306: dload_2        
        //   307: dload_3        
        //   308: dstore_2       
        //   309: aconst_null    
        //   310: nop            
        //   311: iconst_5       
        //   312: dadd           
        //   313: ddiv           
        //   314: fdiv           
        //   315: ineg           
        //   316: ladd           
        //   317: lmul           
        //   318: fdiv           
        //   319: drem           
        //   320: aconst_null    
        //   321: nop            
        //   322: iload           40
        //   324: astore_1        /* i */
        //   325: fmul           
        //   326: ladd           
        //   327: fneg           
        //   328: ladd           
        //   329: laload         
        //   330: idiv           
        //   331: ladd           
        //   332: fdiv           
        //   333: dsub           
        //   334: laload         
        //   335: iastore        
        //   336: fadd           
        //   337: fmul           
        //   338: lsub           
        //   339: dadd           
        //   340: ineg           
        //   341: istore_0        /* words */
        //   342: dload_3        
        //   343: dup_x1         
        //   344: aconst_null    
        //   345: nop            
        //   346: iconst_3       
        //    LocalVariableTable:
        //  Start  Length  Slot  Name   Signature
        //  -----  ------  ----  -----  -------------------
        //  324    22      1     i      I
        //  322    24      0     words  [Ljava/lang/String;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
}
