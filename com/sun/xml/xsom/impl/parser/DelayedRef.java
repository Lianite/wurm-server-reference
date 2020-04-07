// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.xml.xsom.impl.parser;

import com.sun.xml.xsom.XSAttributeDecl;
import com.sun.xml.xsom.XSAttGroupDecl;
import com.sun.xml.xsom.XSModelGroupDecl;
import com.sun.xml.xsom.XSTerm;
import com.sun.xml.xsom.XSElementDecl;
import com.sun.xml.xsom.XSComplexType;
import com.sun.xml.xsom.XSSimpleType;
import com.sun.xml.xsom.XSType;
import com.sun.xml.xsom.impl.Ref;
import com.sun.xml.xsom.XSDeclaration;
import org.xml.sax.SAXException;
import com.sun.xml.xsom.impl.SchemaImpl;
import org.xml.sax.Locator;
import com.sun.xml.xsom.impl.UName;
import com.sun.xml.xsom.XSSchemaSet;

public abstract class DelayedRef implements Patch
{
    protected final XSSchemaSet schema;
    private PatcherManager manager;
    private UName name;
    private Locator source;
    private Object ref;
    
    DelayedRef(final PatcherManager _manager, final Locator _source, final SchemaImpl _schema, final UName _name) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     1: dsub           
        //     2: lsub           
        //     3: ineg           
        //     4: astore_3        /* _schema */
        //     5: ladd           
        //     6: ldiv           
        //     7: lsub           
        //     8: iconst_1       
        //     9: lload_3         /* _schema */
        //    10: nop            
        //    11: aload           this
        //    13: bipush          0
        //    15: aconst_null    
        //    16: nop            
        //    17: iload_0         /* this */
        //    18: nop            
        //    19: iconst_2       
        //    20: nop            
        //    21: ldc2_w          "manager"
        //    24: nop            
        //    25: aload_3         /* _schema */
        //    26: nop            
        //    27: nop            
        //    28: nop            
        //    29: iconst_m1      
        //    30: nop            
        //    31: iaload         
        //    32: nop            
        //    33: laload         
        //    34: nop            
        //    35: nop            
        //    36: nop            
        //    37: iconst_m1      
        //    38: nop            
        //    39: faload         
        //    40: nop            
        //    41: daload         
        //    42: nop            
        //    43: nop            
        //    44: nop            
        //    45: iconst_m1      
        //    46: nop            
        //    47: aaload         
        //    48: nop            
        //    49: baload         
        //    50: nop            
        //    51: nop            
        //    52: nop            
        //    53: iconst_m1      
        //    54: nop            
        //    55: caload         
        //    56: nop            
        //    57: saload         
        //    58: nop            
        //    LocalVariableTable:
        //  Start  Length  Slot  Name      Signature
        //  -----  ------  ----  --------  ---------------------------------------------
        //  0      59      0     this      Lcom/sun/xml/xsom/impl/parser/DelayedRef;
        //  0      59      1     _manager  Lcom/sun/xml/xsom/impl/parser/PatcherManager;
        //  0      59      2     _source   Lorg/xml/sax/Locator;
        //  0      59      3     _schema   Lcom/sun/xml/xsom/impl/SchemaImpl;
        //  0      59      4     _name     Lcom/sun/xml/xsom/impl/UName;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public void run() throws SAXException {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: nop            
        //     1: astore          this
        //     3: fstore          this
        //     5: astore          this
        //     7: nop            
        //     8: nop            
        //     9: caload         
        //    10: nop            
        //    11: iconst_2       
        //    12: nop            
        //    13: nop            
        //    14: nop            
        //    15: istore_0        /* this */
        //    16: nop            
        //    17: istore_0        /* this */
        //    18: nop            
        //    19: istore_1       
        //    20: nop            
        //    21: nop            
        //    22: nop            
        //    23: nop            
        //    24: nop            
        //    25: istore_0        /* this */
        //    26: nop            
        //    Exceptions:
        //  throws org.xml.sax.SAXException
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  -----------------------------------------
        //  0      27      0     this  Lcom/sun/xml/xsom/impl/parser/DelayedRef;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    protected abstract Object resolveReference(final UName p0);
    
    protected abstract String getErrorProperty();
    
    protected final Object _get() {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: nop            
        //     1: lstore_1       
        //     2: nop            
        //     3: fconst_0       
        //     4: nop            
        //     5: lstore_2       
        //     6: nop            
        //     7: bipush          0
        //     9: lstore_3       
        //    10: nop            
        //    11: iload           this
        //    13: fstore_0        /* this */
        //    14: nop            
        //    15: iload_0         /* this */
        //    16: nop            
        //    17: fstore_1       
        //    18: nop            
        //    19: astore          this
        //    21: nop            
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  -----------------------------------------
        //  0      22      0     this  Lcom/sun/xml/xsom/impl/parser/DelayedRef;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    private void resolve() throws SAXException {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: nop            
        //     1: lload           42
        //     3: getfield        com/sun/xml/xsom/impl/parser/DelayedRef.ref:Ljava/lang/Object;
        //     6: ifnonnull       19
        //     9: new             Ljava/lang/InternalError;
        //    12: dup            
        //    13: ldc             "unresolved reference"
        //    15: invokespecial   java/lang/InternalError.<init>:(Ljava/lang/String;)V
        //    18: athrow         
        //    19: aload_0         /* this */
        //    20: getfield        com/sun/xml/xsom/impl/parser/DelayedRef.ref:Ljava/lang/Object;
        //    23: areturn        
        //    24: nop            
        //    25: nop            
        //    26: nop            
        //    27: iconst_m1      
        //    28: nop            
        //    29: dstore          this
        //    31: nop            
        //    32: nop            
        //    33: lconst_1       
        //    34: nop            
        //    35: iconst_m1      
        //    36: nop            
        //    37: nop            
        //    38: nop            
        //    39: dastore        
        //    40: nop            
        //    41: sipush          83
        //    44: nop            
        //    45: astore          this
        //    Exceptions:
        //  throws org.xml.sax.SAXException
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  -----------------------------------------
        //  0      47      0     this  Lcom/sun/xml/xsom/impl/parser/DelayedRef;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public void redefine(final XSDeclaration d) {
        // 
        // This method could not be decompiled.
        // 
        // Could not show original bytecode, likely due to the same error.
        // 
        // The error that occurred was:
        // 
        // com.strobel.assembler.metadata.MethodBodyParseException: An error occurred while parsing the bytecode of method 'com/sun/xml/xsom/impl/parser/DelayedRef.redefine:(Lcom/sun/xml/xsom/XSDeclaration;)V'.
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
    
    public static class Type extends DelayedRef implements Ref.Type
    {
        public Type(final PatcherManager manager, final Locator loc, final SchemaImpl schema, final UName name) {
            super(manager, loc, schema, name);
        }
        
        protected Object resolveReference(final UName name) {
            final Object o = super.schema.getSimpleType(name.getNamespaceURI(), name.getName());
            if (o != null) {
                return o;
            }
            return super.schema.getComplexType(name.getNamespaceURI(), name.getName());
        }
        
        protected String getErrorProperty() {
            return "UndefinedType";
        }
        
        public XSType getType() {
            return (XSType)super._get();
        }
    }
    
    public static class SimpleType extends DelayedRef implements Ref.SimpleType
    {
        public SimpleType(final PatcherManager manager, final Locator loc, final SchemaImpl schema, final UName name) {
            super(manager, loc, schema, name);
        }
        
        public XSSimpleType getType() {
            return (XSSimpleType)this._get();
        }
        
        protected Object resolveReference(final UName name) {
            return super.schema.getSimpleType(name.getNamespaceURI(), name.getName());
        }
        
        protected String getErrorProperty() {
            return "UndefinedSimpleType";
        }
    }
    
    public static class ComplexType extends DelayedRef implements Ref.ComplexType
    {
        public ComplexType(final PatcherManager manager, final Locator loc, final SchemaImpl schema, final UName name) {
            super(manager, loc, schema, name);
        }
        
        protected Object resolveReference(final UName name) {
            return super.schema.getComplexType(name.getNamespaceURI(), name.getName());
        }
        
        protected String getErrorProperty() {
            return "UndefinedCompplexType";
        }
        
        public XSComplexType getType() {
            return (XSComplexType)super._get();
        }
    }
    
    public static class Element extends DelayedRef implements Ref.Element
    {
        public Element(final PatcherManager manager, final Locator loc, final SchemaImpl schema, final UName name) {
            super(manager, loc, schema, name);
        }
        
        protected Object resolveReference(final UName name) {
            return super.schema.getElementDecl(name.getNamespaceURI(), name.getName());
        }
        
        protected String getErrorProperty() {
            return "UndefinedElement";
        }
        
        public XSElementDecl get() {
            return (XSElementDecl)super._get();
        }
        
        public XSTerm getTerm() {
            return this.get();
        }
    }
    
    public static class ModelGroup extends DelayedRef implements Ref.Term
    {
        public ModelGroup(final PatcherManager manager, final Locator loc, final SchemaImpl schema, final UName name) {
            super(manager, loc, schema, name);
        }
        
        protected Object resolveReference(final UName name) {
            return super.schema.getModelGroupDecl(name.getNamespaceURI(), name.getName());
        }
        
        protected String getErrorProperty() {
            return "UndefinedModelGroup";
        }
        
        public XSModelGroupDecl get() {
            return (XSModelGroupDecl)super._get();
        }
        
        public XSTerm getTerm() {
            return this.get();
        }
    }
    
    public static class AttGroup extends DelayedRef implements Ref.AttGroup
    {
        public AttGroup(final PatcherManager manager, final Locator loc, final SchemaImpl schema, final UName name) {
            super(manager, loc, schema, name);
        }
        
        protected Object resolveReference(final UName name) {
            return super.schema.getAttGroupDecl(name.getNamespaceURI(), name.getName());
        }
        
        protected String getErrorProperty() {
            return "UndefinedAttributeGroup";
        }
        
        public XSAttGroupDecl get() {
            return (XSAttGroupDecl)super._get();
        }
    }
    
    public static class Attribute extends DelayedRef implements Ref.Attribute
    {
        public Attribute(final PatcherManager manager, final Locator loc, final SchemaImpl schema, final UName name) {
            super(manager, loc, schema, name);
        }
        
        protected Object resolveReference(final UName name) {
            return super.schema.getAttributeDecl(name.getNamespaceURI(), name.getName());
        }
        
        protected String getErrorProperty() {
            return "UndefinedAttribute";
        }
        
        public XSAttributeDecl getAttribute() {
            return (XSAttributeDecl)super._get();
        }
    }
}
