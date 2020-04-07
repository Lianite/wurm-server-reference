// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.xml.xsom.util;

import com.sun.xml.xsom.XSNotation;
import com.sun.xml.xsom.XSElementDecl;
import com.sun.xml.xsom.XSModelGroup;
import com.sun.xml.xsom.XSModelGroupDecl;
import com.sun.xml.xsom.XSWildcard;
import com.sun.xml.xsom.XSContentType;
import com.sun.xml.xsom.XSParticle;
import com.sun.xml.xsom.XSSimpleType;
import com.sun.xml.xsom.XSFacet;
import com.sun.xml.xsom.XSSchema;
import com.sun.xml.xsom.XSComplexType;
import com.sun.xml.xsom.XSAttributeDecl;
import com.sun.xml.xsom.XSAttributeUse;
import com.sun.xml.xsom.XSAttGroupDecl;
import com.sun.xml.xsom.XSAnnotation;
import com.sun.xml.xsom.XSComponent;
import java.util.Locale;
import com.sun.xml.xsom.visitor.XSFunction;

public class NameGetter implements XSFunction
{
    private final Locale locale;
    public static final XSFunction theInstance;
    
    public NameGetter(final Locale _locale) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: lmul           
        //     1: ldiv           
        //     2: irem           
        //     3: idiv           
        //     4: lsub           
        //     5: bastore        
        //     6: lshl           
        //     7: irem           
        //     8: lsub           
        //     9: istore_0        /* this */
        //    LocalVariableTable:
        //  Start  Length  Slot  Name     Signature
        //  -----  ------  ----  -------  ----------------------------------
        //  0      10      0     this     Lcom/sun/xml/xsom/util/NameGetter;
        //  0      10      1     _locale  Ljava/util/Locale;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public static String get(final XSComponent comp) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: aconst_null    
        //     1: nop            
        //     2: baload         
        //     3: dload_2        
        //     4: astore_1       
        //     5: dadd           
        //     6: ddiv           
        //     7: ldiv           
        //     8: laload         
        //     9: drem           
        //    10: lneg           
        //    11: fdiv           
        //    12: laload         
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  ------------------------------
        //  0      13      0     comp  Lcom/sun/xml/xsom/XSComponent;
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
        //     0: idiv           
        //     1: laload         
        //     2: ishl           
        //     3: drem           
        //     4: ddiv           
        //     5: ldiv           
        //     6: laload         
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  ----------------------------------
        //  0      7       0     this  Lcom/sun/xml/xsom/util/NameGetter;
        //  0      7       1     ann   Lcom/sun/xml/xsom/XSAnnotation;
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
        //     0: frem           
        //     1: isub           
        //     2: istore_0        /* this */
        //     3: dload_3        
        //     4: astore_1        /* decl */
        //     5: fmul           
        //     6: ladd           
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  ----------------------------------
        //  0      7       0     this  Lcom/sun/xml/xsom/util/NameGetter;
        //  0      7       1     decl  Lcom/sun/xml/xsom/XSAttGroupDecl;
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
        //     1: ddiv           
        //     2: ldiv           
        //     3: laload         
        //     4: drem           
        //     5: lneg           
        //     6: fdiv           
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  ----------------------------------
        //  0      7       0     this  Lcom/sun/xml/xsom/util/NameGetter;
        //  0      7       1     use   Lcom/sun/xml/xsom/XSAttributeUse;
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
        //     0: ladd           
        //     1: ldiv           
        //     2: lsub           
        //     3: dstore_0        /* this */
        //     4: lsub           
        //     5: ineg           
        //     6: ineg           
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  ----------------------------------
        //  0      7       0     this  Lcom/sun/xml/xsom/util/NameGetter;
        //  0      7       1     decl  Lcom/sun/xml/xsom/XSAttributeDecl;
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
        //     0: lmul           
        //     1: fdiv           
        //     2: ineg           
        //     3: aconst_null    
        //     4: nop            
        //     5: lload_2        
        //     6: dadd           
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  ----------------------------------
        //  0      7       0     this  Lcom/sun/xml/xsom/util/NameGetter;
        //  0      7       1     type  Lcom/sun/xml/xsom/XSComplexType;
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
        //     0: fneg           
        //     1: fconst_1       
        //     2: nop            
        //     3: lrem           
        //     4: nop            
        //     5: frem           
        //     6: fconst_1       
        //    LocalVariableTable:
        //  Start  Length  Slot  Name    Signature
        //  -----  ------  ----  ------  ----------------------------------
        //  0      7       0     this    Lcom/sun/xml/xsom/util/NameGetter;
        //  0      7       1     schema  Lcom/sun/xml/xsom/XSSchema;
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
        //     0: dadd           
        //     1: nop            
        //     2: isub           
        //     3: fconst_1       
        //     4: nop            
        //     5: saload         
        //     6: nop            
        //    LocalVariableTable:
        //  Start  Length  Slot  Name   Signature
        //  -----  ------  ----  -----  ----------------------------------
        //  0      7       0     this   Lcom/sun/xml/xsom/util/NameGetter;
        //  0      7       1     facet  Lcom/sun/xml/xsom/XSFacet;
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
        //     0: lneg           
        //     1: fdiv           
        //     2: laload         
        //     3: ishl           
        //     4: ldiv           
        //     5: idiv           
        //     6: laload         
        //    LocalVariableTable:
        //  Start  Length  Slot  Name        Signature
        //  -----  ------  ----  ----------  ----------------------------------
        //  0      7       0     this        Lcom/sun/xml/xsom/util/NameGetter;
        //  0      7       1     simpleType  Lcom/sun/xml/xsom/XSSimpleType;
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
        //     0: astore_1        /* particle */
        //     1: fmul           
        //     2: ladd           
        //     3: fneg           
        //     4: ladd           
        //     5: laload         
        //     6: idiv           
        //    LocalVariableTable:
        //  Start  Length  Slot  Name      Signature
        //  -----  ------  ----  --------  ----------------------------------
        //  0      7       0     this      Lcom/sun/xml/xsom/util/NameGetter;
        //  0      7       1     particle  Lcom/sun/xml/xsom/XSParticle;
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
        //     0: ladd           
        //     1: fneg           
        //     2: ladd           
        //     3: laload         
        //     4: lneg           
        //     5: ineg           
        //     6: lmul           
        //    LocalVariableTable:
        //  Start  Length  Slot  Name   Signature
        //  -----  ------  ----  -----  ----------------------------------
        //  0      7       0     this   Lcom/sun/xml/xsom/util/NameGetter;
        //  0      7       1     empty  Lcom/sun/xml/xsom/XSContentType;
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
        //     0: lneg           
        //     1: frem           
        //     2: dadd           
        //     3: lsub           
        //     4: lstore_3       
        //     5: lneg           
        //     6: fdiv           
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  ----------------------------------
        //  0      7       0     this  Lcom/sun/xml/xsom/util/NameGetter;
        //  0      7       1     wc    Lcom/sun/xml/xsom/XSWildcard;
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
        //     0: fdiv           
        //     1: isub           
        //     2: idiv           
        //     3: lsub           
        //     4: istore_0        /* this */
        //     5: aconst_null    
        //     6: nop            
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  -----------------------------------
        //  0      7       0     this  Lcom/sun/xml/xsom/util/NameGetter;
        //  0      7       1     decl  Lcom/sun/xml/xsom/XSModelGroupDecl;
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
        // Could not show original bytecode, likely due to the same error.
        // 
        // The error that occurred was:
        // 
        // com.strobel.assembler.metadata.MethodBodyParseException: An error occurred while parsing the bytecode of method 'com/sun/xml/xsom/util/NameGetter.modelGroup:(Lcom/sun/xml/xsom/XSModelGroup;)Ljava/lang/Object;'.
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
        // Caused by: java.lang.ClassCastException: com.strobel.assembler.ir.ConstantPool$TypeInfoEntry cannot be cast to com.strobel.assembler.ir.ConstantPool$FieldReferenceEntry
        //     at com.strobel.assembler.metadata.ClassFileReader$Scope.lookupField(ClassFileReader.java:1233)
        //     at com.strobel.assembler.metadata.MethodReader.readBodyCore(MethodReader.java:214)
        //     at com.strobel.assembler.metadata.MethodReader.readBody(MethodReader.java:62)
        //     ... 16 more
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public Object elementDecl(final XSElementDecl decl) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: iconst_m1      
        //     1: nop            
        //     2: aconst_null    
        //     3: nop            
        //     4: nop            
        //     5: nop            
        //     6: fconst_2       
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  ----------------------------------
        //  0      7       0     this  Lcom/sun/xml/xsom/util/NameGetter;
        //  0      7       1     decl  Lcom/sun/xml/xsom/XSElementDecl;
        // 
        // The error that occurred was:
        // 
        // java.lang.NullPointerException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public Object notation(final XSNotation n) {
        // 
        // This method could not be decompiled.
        // 
        // Could not show original bytecode, likely due to the same error.
        // 
        // The error that occurred was:
        // 
        // com.strobel.assembler.metadata.MethodBodyParseException: An error occurred while parsing the bytecode of method 'com/sun/xml/xsom/util/NameGetter.notation:(Lcom/sun/xml/xsom/XSNotation;)Ljava/lang/Object;'.
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
        // Caused by: java.lang.ClassCastException: com.strobel.assembler.ir.ConstantPool$MethodReferenceEntry cannot be cast to com.strobel.assembler.ir.ConstantPool$ConstantEntry
        //     at com.strobel.assembler.ir.ConstantPool.lookupConstant(ConstantPool.java:120)
        //     at com.strobel.assembler.metadata.ClassFileReader$Scope.lookupConstant(ClassFileReader.java:1319)
        //     at com.strobel.assembler.metadata.MethodReader.readBodyCore(MethodReader.java:286)
        //     at com.strobel.assembler.metadata.MethodReader.readBody(MethodReader.java:62)
        //     ... 16 more
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    private String localize(final String key) {
        // 
        // This method could not be decompiled.
        // 
        // Could not show original bytecode, likely due to the same error.
        // 
        // The error that occurred was:
        // 
        // com.strobel.assembler.metadata.MethodBodyParseException: An error occurred while parsing the bytecode of method 'com/sun/xml/xsom/util/NameGetter.localize:(Ljava/lang/String;)Ljava/lang/String;'.
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
        // Caused by: java.lang.ClassCastException: com.strobel.assembler.ir.ConstantPool$FieldReferenceEntry cannot be cast to com.strobel.assembler.ir.ConstantPool$ConstantEntry
        //     at com.strobel.assembler.ir.ConstantPool.lookupConstant(ConstantPool.java:120)
        //     at com.strobel.assembler.metadata.ClassFileReader$Scope.lookupConstant(ClassFileReader.java:1319)
        //     at com.strobel.assembler.metadata.MethodReader.readBodyCore(MethodReader.java:286)
        //     at com.strobel.assembler.metadata.MethodReader.readBody(MethodReader.java:62)
        //     ... 16 more
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    static {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: aconst_null    
        //     1: nop            
        //     2: aconst_null    
        //     3: nop            
        //     4: bastore        
        //     5: nop            
        //     6: castore        
        //     7: nop            
        //     8: aconst_null    
        //     9: nop            
        //    10: lstore          0
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
}
