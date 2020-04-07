// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.reader.decorator;

import com.sun.xml.bind.JAXBAssertionError;
import com.sun.msv.grammar.NameClass;
import org.xml.sax.SAXParseException;
import com.sun.msv.reader.trex.DefineState;
import com.sun.msv.reader.ExpressionState;
import com.sun.msv.grammar.SimpleNameClass;
import com.sun.msv.grammar.NameClassAndExpression;
import org.xml.sax.Locator;
import com.sun.msv.grammar.Expression;
import com.sun.msv.reader.State;
import com.sun.msv.util.StartTagInfo;
import com.sun.tools.xjc.reader.NameConverter;
import com.sun.codemodel.JCodeModel;
import com.sun.tools.xjc.grammar.AnnotatedGrammar;
import com.sun.msv.reader.GrammarReader;

public abstract class DecoratorImpl implements Decorator
{
    protected final GrammarReader reader;
    protected final AnnotatedGrammar grammar;
    protected final JCodeModel codeModel;
    protected final NameConverter nameConverter;
    
    protected DecoratorImpl(final GrammarReader _reader, final AnnotatedGrammar _grammar, final NameConverter nc) {
        this.reader = _reader;
        this.grammar = _grammar;
        this.codeModel = this.grammar.codeModel;
        this.nameConverter = nc;
    }
    
    protected final String getAttribute(final StartTagInfo tag, final String attName) {
        return tag.getAttribute("http://java.sun.com/xml/ns/jaxb", attName);
    }
    
    protected final String getAttribute(final StartTagInfo tag, final String attName, final String defaultValue) {
        final String r = this.getAttribute(tag, attName);
        if (r != null) {
            return r;
        }
        return defaultValue;
    }
    
    protected final String decideName(final State state, final Expression exp, final String role, final String suffix, final Locator loc) {
        final StartTagInfo tag = state.getStartTag();
        String name = this.getAttribute(tag, "name");
        if (name == null) {
            name = tag.getAttribute("name");
            if (name != null) {
                name = this.xmlNameToJavaName(role, name + suffix);
            }
        }
        if (name == null && exp instanceof NameClassAndExpression) {
            final NameClass nc = ((NameClassAndExpression)exp).getNameClass();
            if (nc instanceof SimpleNameClass) {
                name = this.xmlNameToJavaName(role, ((SimpleNameClass)nc).localName + suffix);
            }
        }
        if (name != null) {
            return name;
        }
        if (state.getParentState() instanceof ExpressionState || state.getParentState() instanceof DefineState) {
            return this.decideName(state.getParentState(), exp, role, suffix, loc);
        }
        this.reader.controller.error(new SAXParseException(Messages.format("NameNeeded"), loc));
        return "DUMMY";
    }
    
    private final String xmlNameToJavaName(final String role, final String name) {
        if (role.equals("field")) {
            return this.nameConverter.toPropertyName(name);
        }
        if (role.equals("interface")) {
            return this.nameConverter.toInterfaceName(name);
        }
        if (role.equals("class")) {
            return this.nameConverter.toClassName(name);
        }
        throw new JAXBAssertionError(role);
    }
}
