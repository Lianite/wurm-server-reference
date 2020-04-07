// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.reader.relaxng;

import org.xml.sax.Attributes;
import com.sun.tools.xjc.reader.internalizer.AbstractReferenceFinderImpl;
import org.xml.sax.SAXException;
import org.w3c.dom.Element;
import org.xml.sax.XMLFilter;
import com.sun.tools.xjc.reader.internalizer.DOMForest;
import com.sun.tools.xjc.reader.internalizer.InternalizationLogic;

public class RELAXNGInternalizationLogic implements InternalizationLogic
{
    public RELAXNGInternalizationLogic() {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: nop            
        //     1: nop            
        //     2: lload           this
        //     4: iconst_m1      
        //    LocalVariableTable:
        //  Start  Length  Slot  Name  Signature
        //  -----  ------  ----  ----  --------------------------------------------------------------
        //  0      5       0     this  Lcom/sun/tools/xjc/reader/relaxng/RELAXNGInternalizationLogic;
        // 
        // The error that occurred was:
        // 
        // java.lang.NullPointerException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public XMLFilter createExternalReferenceFinder(final DOMForest parent) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: nop            
        //     1: nop            
        //     2: nop            
        //     3: iconst_3       
        //     4: nop            
        //     5: aconst_null    
        //     6: nop            
        //     7: nop            
        //     8: nop            
        //     9: astore_3       
        //    LocalVariableTable:
        //  Start  Length  Slot  Name    Signature
        //  -----  ------  ----  ------  --------------------------------------------------------------
        //  0      10      0     this    Lcom/sun/tools/xjc/reader/relaxng/RELAXNGInternalizationLogic;
        //  0      10      1     parent  Lcom/sun/tools/xjc/reader/internalizer/DOMForest;
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public boolean checkIfValidTargetNode(final DOMForest parent, final Element bindings, final Element target) throws SAXException {
        -1;
        return (boolean)parent;
    }
    
    public Element refineTarget(final Element target) {
        // 
        // This method could not be decompiled.
        // 
        // Could not show original bytecode, likely due to the same error.
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        //     at com.strobel.assembler.ir.attributes.CodeAttribute.<init>(CodeAttribute.java:60)
        //     at com.strobel.assembler.metadata.MethodDefinition.tryLoadBody(MethodDefinition.java:685)
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
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    private static final class ReferenceFinder extends AbstractReferenceFinderImpl
    {
        ReferenceFinder(final DOMForest parent) {
            super(parent);
        }
        
        protected String findExternalResource(final String nsURI, final String localName, final Attributes atts) {
            if ("http://relaxng.org/ns/structure/1.0".equals(nsURI) && ("include".equals(localName) || "externalRef".equals(localName))) {
                return atts.getValue("href");
            }
            return null;
        }
    }
}
