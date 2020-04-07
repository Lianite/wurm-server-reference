// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.reader;

import org.xml.sax.Attributes;
import com.sun.codemodel.JPackage;
import java.util.Stack;
import com.sun.codemodel.JCodeModel;

public class StackPackageManager implements PackageManager
{
    private final JCodeModel codeModel;
    private final Stack stack;
    
    public StackPackageManager(final JPackage pkg) {
        this.stack = new Stack();
        this.codeModel = pkg.owner();
        this.stack.push(pkg);
    }
    
    public final JPackage getCurrentPackage() {
        return this.stack.peek();
    }
    
    public final void startElement(final Attributes atts) {
        if (atts.getIndex("http://java.sun.com/xml/ns/jaxb", "package") != -1) {
            final String name = atts.getValue("http://java.sun.com/xml/ns/jaxb", "package");
            this.stack.push(this.codeModel._package(name));
        }
        else {
            this.stack.push(this.stack.peek());
        }
    }
    
    public final void endElement() {
        this.stack.pop();
    }
}
