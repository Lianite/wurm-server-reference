// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.runtime;

import java.io.InputStream;
import com.sun.xml.bind.GrammarImpl;
import java.io.ObjectInputStream;
import javax.xml.bind.JAXBException;
import com.sun.xml.bind.Messages;
import com.sun.msv.grammar.Grammar;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import javax.xml.namespace.QName;
import java.util.Map;

public class GrammarInfoImpl implements GrammarInfo
{
    private final Map rootTagMap;
    private final Class objectFactoryClass;
    private final Map defaultImplementationMap;
    private final ClassLoader classLoader;
    
    public GrammarInfoImpl(final Map _rootTagMap, final Map _defaultImplementationMap, final Class _objectFactoryClass) {
        this.rootTagMap = _rootTagMap;
        this.defaultImplementationMap = _defaultImplementationMap;
        this.objectFactoryClass = _objectFactoryClass;
        this.classLoader = this.objectFactoryClass.getClassLoader();
    }
    
    private final Class lookupRootMap(final String nsUri, final String localName) {
        QName qn = new QName(nsUri, localName);
        if (this.rootTagMap.containsKey(qn)) {
            return this.rootTagMap.get(qn);
        }
        qn = new QName(nsUri, "*");
        if (this.rootTagMap.containsKey(qn)) {
            return this.rootTagMap.get(qn);
        }
        qn = new QName("*", "*");
        return this.rootTagMap.get(qn);
    }
    
    public final Class getRootElement(final String namespaceUri, final String localName) {
        final Class intfCls = this.lookupRootMap(namespaceUri, localName);
        if (intfCls == null) {
            return null;
        }
        return this.getDefaultImplementation(intfCls);
    }
    
    public final UnmarshallingEventHandler createUnmarshaller(final String namespaceUri, final String localName, final UnmarshallingContext context) {
        final Class impl = this.getRootElement(namespaceUri, localName);
        if (impl == null) {
            return null;
        }
        try {
            return impl.newInstance().createUnmarshaller(context);
        }
        catch (InstantiationException e) {
            throw new InstantiationError(e.toString());
        }
        catch (IllegalAccessException e2) {
            throw new IllegalAccessError(e2.toString());
        }
    }
    
    public final String[] getProbePoints() {
        final List r = new ArrayList();
        for (final QName qn : this.rootTagMap.keySet()) {
            r.add(qn.getNamespaceURI());
            r.add(qn.getLocalPart());
        }
        return r.toArray(new String[r.size()]);
    }
    
    public final boolean recognize(final String nsUri, final String localName) {
        return this.lookupRootMap(nsUri, localName) != null;
    }
    
    public final Class getDefaultImplementation(final Class javaContentInterface) {
        try {
            return Class.forName(this.defaultImplementationMap.get(javaContentInterface), true, this.classLoader);
        }
        catch (ClassNotFoundException e) {
            throw new NoClassDefFoundError(e.toString());
        }
    }
    
    public final Grammar getGrammar() throws JAXBException {
        try {
            final InputStream is = this.objectFactoryClass.getResourceAsStream("bgm.ser");
            if (is == null) {
                String name = this.objectFactoryClass.getName();
                final int idx = name.lastIndexOf(46);
                name = '/' + name.substring(0, idx + 1).replace('.', '/') + "bgm.ser";
                throw new JAXBException(Messages.format("GrammarInfo.NoBGM", (Object)name));
            }
            final ObjectInputStream ois = new ObjectInputStream(is);
            final GrammarImpl g = (GrammarImpl)ois.readObject();
            ois.close();
            g.connect(new Grammar[] { g });
            return (Grammar)g;
        }
        catch (Exception e) {
            throw new JAXBException(Messages.format("GrammarInfo.UnableToReadBGM"), e);
        }
    }
    
    public XMLSerializable castToXMLSerializable(final Object o) {
        if (o instanceof XMLSerializable) {
            return (XMLSerializable)o;
        }
        return null;
    }
    
    public ValidatableObject castToValidatableObject(final Object o) {
        if (o instanceof ValidatableObject) {
            return (ValidatableObject)o;
        }
        return null;
    }
}
