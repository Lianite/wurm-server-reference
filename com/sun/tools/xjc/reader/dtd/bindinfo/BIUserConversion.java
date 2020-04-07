// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.reader.dtd.bindinfo;

import com.sun.tools.xjc.grammar.xducer.Transducer;
import org.xml.sax.Locator;
import java.util.Map;
import org.dom4j.Element;

public class BIUserConversion implements BIConversion
{
    private final BindInfo owner;
    private final Element e;
    
    BIUserConversion(final BindInfo bi, final Element _e) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     1: astore_1        /* bi */
        //     2: fmul           
        //     3: ladd           
        //     4: fneg           
        //     5: ladd           
        //     6: laload         
        //     7: idiv           
        //     8: ladd           
        //     9: fdiv           
        //    10: dsub           
        //    11: laload         
        //    12: iastore        
        //    13: fadd           
        //    14: fmul           
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  --------------------------------------------------------
        //  0      15      0     this  Lcom/sun/tools/xjc/reader/dtd/bindinfo/BIUserConversion;
        //  0      15      1     bi    Lcom/sun/tools/xjc/reader/dtd/bindinfo/BindInfo;
        //  0      15      2     _e    Lorg/dom4j/Element;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    private static void add(final Map m, final BIConversion c) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: lsub           
        //     1: dneg           
        //     2: dstore_2       
        //     3: fdiv           
        //     4: drem           
        //     5: ineg           
        //     6: ladd           
        //     7: fdiv           
        //     8: dadd           
        //     9: lsub           
        //    10: aconst_null    
        //    11: nop            
        //    12: aload_2        
        //    13: dload_2        
        //    14: dload_3        
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  ----------------------------------------------------
        //  0      15      0     m     Ljava/util/Map;
        //  0      15      1     c     Lcom/sun/tools/xjc/reader/dtd/bindinfo/BIConversion;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    static void addBuiltinConversions(final BindInfo bi, final Map m) {
        // 
        // This method could not be decompiled.
        // 
        // Could not show original bytecode, likely due to the same error.
        // 
        // The error that occurred was:
        // 
        // com.strobel.assembler.metadata.MethodBodyParseException: An error occurred while parsing the bytecode of method 'com/sun/tools/xjc/reader/dtd/bindinfo/BIUserConversion.addBuiltinConversions:(Lcom/sun/tools/xjc/reader/dtd/bindinfo/BindInfo;Ljava/util/Map;)V'.
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
        // Caused by: java.lang.IllegalArgumentException: Argument 'index' must be in the range [0, 238], but value was: 10316.
        //     at com.strobel.core.VerifyArgument.inRange(VerifyArgument.java:346)
        //     at com.strobel.assembler.ir.ConstantPool.get(ConstantPool.java:78)
        //     at com.strobel.assembler.metadata.ClassFileReader$Scope.lookupConstant(ClassFileReader.java:1313)
        //     at com.strobel.assembler.metadata.MethodReader.readBodyCore(MethodReader.java:293)
        //     at com.strobel.assembler.metadata.MethodReader.readBody(MethodReader.java:62)
        //     ... 16 more
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public Locator getSourceLocation() {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: frem           
        //     1: lsub           
        //     2: ladd           
        //     3: isub           
        //     4: lsub           
        //     5: frem           
        //     6: laload         
        //     7: isub           
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  --------------------------------------------------------
        //  0      8       0     this  Lcom/sun/tools/xjc/reader/dtd/bindinfo/BIUserConversion;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    private String attValue(final String name, final String defaultValue) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: fdiv           
        //     1: ineg           
        //     2: istore_0        /* this */
        //     3: astore_1        /* name */
        //     4: fmul           
        //     5: ladd           
        //     6: fneg           
        //     7: ladd           
        //     8: laload         
        //     9: idiv           
        //    10: ladd           
        //    11: fdiv           
        //    12: dsub           
        //    13: laload         
        //    14: aastore        
        //    15: ineg           
        //    16: frem           
        //    17: lmul           
        //    18: fdiv           
        //    LocalVariableTable:
        //  Start  Length  Slot  Name          Signature
        //  -----  ------  ----  ------------  --------------------------------------------------------
        //  0      19      0     this          Lcom/sun/tools/xjc/reader/dtd/bindinfo/BIUserConversion;
        //  0      19      1     name          Ljava/lang/String;
        //  0      19      2     defaultValue  Ljava/lang/String;
        //  11     8       3     r             Ljava/lang/String;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public String name() {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: dadd           
        //     1: ddiv           
        //     2: isub           
        //     3: lsub           
        //     4: astore_2       
        //     5: ddiv           
        //     6: isub           
        //     7: lsub           
        //     8: idiv           
        //     9: aconst_null    
        //    10: nop            
        //    11: lload_2        
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  --------------------------------------------------------
        //  0      12      0     this  Lcom/sun/tools/xjc/reader/dtd/bindinfo/BIUserConversion;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public Transducer getTransducer() {
        // 
        // This method could not be decompiled.
        // 
        // Could not show original bytecode, likely due to the same error.
        // 
        // The error that occurred was:
        // 
        // com.strobel.assembler.metadata.MethodBodyParseException: An error occurred while parsing the bytecode of method 'com/sun/tools/xjc/reader/dtd/bindinfo/BIUserConversion.getTransducer:()Lcom/sun/tools/xjc/grammar/xducer/Transducer;'.
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
        // Caused by: java.lang.IndexOutOfBoundsException: No instruction found at offset 66.
        //     at com.strobel.assembler.ir.InstructionCollection.atOffset(InstructionCollection.java:38)
        //     at com.strobel.assembler.metadata.ExceptionHandlerMapper.createHandlerPlaceholders(ExceptionHandlerMapper.java:642)
        //     at com.strobel.assembler.metadata.ExceptionHandlerMapper.<init>(ExceptionHandlerMapper.java:246)
        //     at com.strobel.assembler.metadata.ExceptionHandlerMapper.run(ExceptionHandlerMapper.java:50)
        //     at com.strobel.assembler.metadata.MethodReader.readBodyCore(MethodReader.java:557)
        //     at com.strobel.assembler.metadata.MethodReader.readBody(MethodReader.java:62)
        //     ... 16 more
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
}
