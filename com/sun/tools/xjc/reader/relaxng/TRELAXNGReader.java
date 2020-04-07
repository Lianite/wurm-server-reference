// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.reader.relaxng;

import java.text.MessageFormat;
import java.util.ResourceBundle;
import org.xml.sax.SAXException;
import org.xml.sax.Attributes;
import com.sun.tools.xjc.reader.annotator.Annotator;
import com.sun.msv.grammar.Expression;
import com.sun.msv.reader.State;
import com.sun.tools.xjc.reader.decorator.Decorator;
import com.sun.tools.xjc.reader.PackageManager;
import com.sun.tools.xjc.reader.PackageTracker;
import com.sun.msv.reader.GrammarReader;
import com.sun.tools.xjc.reader.annotator.AnnotatorControllerImpl;
import com.sun.msv.reader.GrammarReaderController;
import com.sun.tools.xjc.reader.GrammarReaderControllerAdaptor;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.EntityResolver;
import com.sun.tools.xjc.ErrorReceiver;
import com.sun.tools.xjc.grammar.AnnotatedGrammar;
import com.sun.tools.xjc.reader.annotator.AnnotatorController;
import com.sun.tools.xjc.util.CodeModelClassFactory;
import com.sun.tools.xjc.reader.decorator.RoleBasedDecorator;
import com.sun.tools.xjc.reader.StackPackageManager;
import com.sun.tools.xjc.reader.HierarchicalPackageTracker;
import com.sun.msv.reader.trex.ng.RELAXNGReader;

public class TRELAXNGReader extends RELAXNGReader
{
    protected final HierarchicalPackageTracker packageTracker;
    protected final StackPackageManager packageManager;
    private final RoleBasedDecorator decorator;
    protected final CodeModelClassFactory classFactory;
    private final AnnotatorController annController;
    protected final AnnotatedGrammar annGrammar;
    
    public TRELAXNGReader(final ErrorReceiver errorReceiver, final EntityResolver entityResolver, final SAXParserFactory parserFactory, final String defaultPackage) {
        this(new GrammarReaderControllerAdaptor(errorReceiver, entityResolver), parserFactory, defaultPackage);
    }
    
    private TRELAXNGReader(final GrammarReaderControllerAdaptor _controller, final SAXParserFactory parserFactory, String defaultPackage) {
        super((GrammarReaderController)_controller, parserFactory);
        this.packageTracker = new HierarchicalPackageTracker();
        this.annGrammar = new AnnotatedGrammar(this.pool);
        if (defaultPackage == null) {
            defaultPackage = "generated";
        }
        this.packageManager = new StackPackageManager(this.annGrammar.codeModel._package(defaultPackage));
        this.classFactory = new CodeModelClassFactory(_controller);
        this.annController = new AnnotatorControllerImpl((GrammarReader)this, _controller, this.packageTracker);
        this.decorator = new RoleBasedDecorator((GrammarReader)this, _controller, this.annGrammar, this.annController.getNameConverter(), this.packageManager, new DefaultDecorator(this, this.annController.getNameConverter()));
    }
    
    public AnnotatedGrammar getAnnotatedResult() {
        return this.annGrammar;
    }
    
    protected Expression interceptExpression(final State state, Expression exp) {
        exp = super.interceptExpression(state, exp);
        if (this.controller.hadError()) {
            return exp;
        }
        if (exp == null) {
            return exp;
        }
        exp = this.decorator.decorate(state, exp);
        this.packageTracker.associate(exp, this.packageManager.getCurrentPackage());
        return exp;
    }
    
    public void wrapUp() {
        super.wrapUp();
        if (this.controller.hadError()) {
            return;
        }
        this.packageTracker.associate((Expression)this.annGrammar, this.packageManager.getCurrentPackage());
        this.annGrammar.exp = this.grammar.exp;
        Annotator.annotate(this.annGrammar, this.annController);
        this.grammar.exp = this.annGrammar.exp;
    }
    
    public void startElement(final String a, final String b, final String c, final Attributes atts) throws SAXException {
        this.packageManager.startElement(atts);
        super.startElement(a, b, c, atts);
    }
    
    public void endElement(final String a, final String b, final String c) throws SAXException {
        super.endElement(a, b, c);
        this.packageManager.endElement();
    }
    
    protected String localizeMessage(final String propertyName, final Object[] args) {
        String format;
        try {
            format = ResourceBundle.getBundle("com.sun.tools.xjc.reader.relaxng.Messages").getString(propertyName);
        }
        catch (Exception e) {
            try {
                format = ResourceBundle.getBundle("com.sun.tools.xjc.reader.Messages").getString(propertyName);
            }
            catch (Exception ee) {
                return super.localizeMessage(propertyName, args);
            }
        }
        return MessageFormat.format(format, args);
    }
}
