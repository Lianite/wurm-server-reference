// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.generator;

import com.sun.codemodel.JMethod;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JAssignmentTarget;
import com.sun.codemodel.JExpr;
import java.util.Set;
import com.sun.msv.grammar.NameClassVisitor;
import java.util.HashSet;
import com.sun.msv.grammar.SimpleNameClass;
import com.sun.msv.grammar.NameClass;
import com.sun.msv.grammar.NameClassAndExpression;
import com.sun.codemodel.JVar;
import com.sun.codemodel.JBlock;
import com.sun.codemodel.JType;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JExpression;

public abstract class XmlNameStoreAlgorithm
{
    public abstract JExpression getNamespaceURI();
    
    public abstract JExpression getLocalPart();
    
    public abstract JType getType(final JCodeModel p0);
    
    public abstract void onNameUnmarshalled(final JCodeModel p0, final JBlock p1, final JVar p2, final JVar p3);
    
    public abstract void populate(final ClassContext p0);
    
    public static XmlNameStoreAlgorithm get(final NameClassAndExpression item) {
        return get(item.getNameClass());
    }
    
    public static XmlNameStoreAlgorithm get(final NameClass nc) {
        if (nc instanceof SimpleNameClass) {
            return (XmlNameStoreAlgorithm)new Simple((SimpleNameClass)nc, (XmlNameStoreAlgorithm$1)null);
        }
        final Set namespaces = new HashSet();
        nc.simplify().visit((NameClassVisitor)new XmlNameStoreAlgorithm$1(namespaces));
        if (namespaces.size() == 1) {
            return (XmlNameStoreAlgorithm)new UniqueNamespace((String)namespaces.iterator().next(), (XmlNameStoreAlgorithm$1)null);
        }
        return (XmlNameStoreAlgorithm)Any.theInstance;
    }
    
    private static class Simple extends XmlNameStoreAlgorithm
    {
        private final SimpleNameClass snc;
        
        private Simple(final SimpleNameClass _snc) {
            this.snc = _snc;
        }
        
        public JExpression getLocalPart() {
            return JExpr.lit(this.snc.localName);
        }
        
        public JExpression getNamespaceURI() {
            return JExpr.lit(this.snc.namespaceURI);
        }
        
        public void onNameUnmarshalled(final JCodeModel codeModel, final JBlock block, final JVar $uri, final JVar $localName) {
        }
        
        public void populate(final ClassContext target) {
        }
        
        public JType getType(final JCodeModel codeModel) {
            return null;
        }
    }
    
    private static class UniqueNamespace extends XmlNameStoreAlgorithm
    {
        private final String namespaceURI;
        
        private UniqueNamespace(final String _nsUri) {
            this.namespaceURI = _nsUri;
        }
        
        public JExpression getLocalPart() {
            return JExpr.ref("_XmlName");
        }
        
        public JExpression getNamespaceURI() {
            return JExpr.lit(this.namespaceURI);
        }
        
        public void onNameUnmarshalled(final JCodeModel codeModel, final JBlock block, final JVar $uri, final JVar $localName) {
            block.assign(JExpr.ref("_XmlName"), $localName);
        }
        
        public void populate(final ClassContext target) {
            final JDefinedClass impl = target.implClass;
            final JCodeModel codeModel = impl.owner();
            final JClass string = codeModel.ref((XmlNameStoreAlgorithm.class$java$lang$String == null) ? (XmlNameStoreAlgorithm.class$java$lang$String = XmlNameStoreAlgorithm.class$("java.lang.String")) : XmlNameStoreAlgorithm.class$java$lang$String);
            final JVar $name = impl.field(2, string, "_XmlName");
            final MethodWriter writer = target.createMethodWriter();
            writer.declareMethod((JType)string, "getXmlName").body()._return($name);
            final JMethod setter = writer.declareMethod((JType)codeModel.VOID, "setXmlName");
            final JVar $arg = writer.addParameter((JType)string, "newLocalName");
            setter.body().assign($name, $arg);
        }
        
        public JType getType(final JCodeModel codeModel) {
            return codeModel.ref((XmlNameStoreAlgorithm.class$java$lang$String == null) ? (XmlNameStoreAlgorithm.class$java$lang$String = XmlNameStoreAlgorithm.class$("java.lang.String")) : XmlNameStoreAlgorithm.class$java$lang$String);
        }
    }
    
    private static class Any extends XmlNameStoreAlgorithm
    {
        private static final Any theInstance;
        
        public JExpression getLocalPart() {
            return JExpr.ref("_XmlName").invoke("getLocalPart");
        }
        
        public JExpression getNamespaceURI() {
            return JExpr.ref("_XmlName").invoke("getNamespaceURI");
        }
        
        public void onNameUnmarshalled(final JCodeModel codeModel, final JBlock block, final JVar $uri, final JVar $localName) {
            block.assign(JExpr.ref("_XmlName"), JExpr._new(codeModel.ref((XmlNameStoreAlgorithm.class$javax$xml$namespace$QName == null) ? (XmlNameStoreAlgorithm.class$javax$xml$namespace$QName = XmlNameStoreAlgorithm.class$("javax.xml.namespace.QName")) : XmlNameStoreAlgorithm.class$javax$xml$namespace$QName)).arg($uri).arg($localName));
        }
        
        public void populate(final ClassContext target) {
            final JDefinedClass impl = target.implClass;
            final JCodeModel codeModel = impl.owner();
            final JClass qname = codeModel.ref((XmlNameStoreAlgorithm.class$javax$xml$namespace$QName == null) ? (XmlNameStoreAlgorithm.class$javax$xml$namespace$QName = XmlNameStoreAlgorithm.class$("javax.xml.namespace.QName")) : XmlNameStoreAlgorithm.class$javax$xml$namespace$QName);
            final JVar $name = impl.field(2, qname, "_XmlName");
            final MethodWriter helper = target.createMethodWriter();
            helper.declareMethod((JType)qname, "getXmlName").body()._return($name);
            final JMethod setter = helper.declareMethod((JType)codeModel.VOID, "setXmlName");
            final JVar $arg = helper.addParameter((JType)qname, "newLocalName");
            setter.body().assign($name, $arg);
        }
        
        public JType getType(final JCodeModel codeModel) {
            return codeModel.ref((XmlNameStoreAlgorithm.class$javax$xml$namespace$QName == null) ? (XmlNameStoreAlgorithm.class$javax$xml$namespace$QName = XmlNameStoreAlgorithm.class$("javax.xml.namespace.QName")) : XmlNameStoreAlgorithm.class$javax$xml$namespace$QName);
        }
        
        static {
            Any.theInstance = new Any();
        }
    }
}
