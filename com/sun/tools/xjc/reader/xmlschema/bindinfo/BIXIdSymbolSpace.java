// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.reader.xmlschema.bindinfo;

import com.sun.tools.xjc.grammar.id.SymbolSpace;
import com.sun.tools.xjc.grammar.id.IDREFTransducer;
import com.sun.tools.xjc.grammar.xducer.Transducer;
import org.xml.sax.Locator;
import javax.xml.namespace.QName;

public class BIXIdSymbolSpace extends AbstractDeclarationImpl
{
    private final String name;
    public static final QName NAME;
    
    public BIXIdSymbolSpace(final Locator _loc, final String _name) {
        super(_loc);
        this.name = _name;
    }
    
    public Transducer makeTransducer(final Transducer core) {
        this.markAsAcknowledged();
        final SymbolSpace ss = this.getBuilder().grammar.getSymbolSpace(this.name);
        if (core.isID()) {
            return (Transducer)new BIXIdSymbolSpace$1(this, core, ss);
        }
        return new IDREFTransducer(this.getBuilder().grammar.codeModel, ss, true);
    }
    
    public QName getName() {
        return BIXIdSymbolSpace.NAME;
    }
    
    static {
        BIXIdSymbolSpace.NAME = new QName("http://java.sun.com/xml/ns/jaxb/xjc", "idSymbolSpace");
    }
}
