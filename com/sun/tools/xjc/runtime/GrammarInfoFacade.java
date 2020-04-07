// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.runtime;

import com.sun.msv.grammar.Expression;
import com.sun.msv.grammar.ExpressionPool;
import com.sun.xml.bind.GrammarImpl;
import com.sun.xml.bind.ProxyGroup;
import javax.xml.bind.JAXBContext;
import java.util.StringTokenizer;
import com.sun.xml.bind.Messages;
import java.util.ArrayList;
import javax.xml.bind.JAXBException;
import com.sun.msv.grammar.Grammar;

class GrammarInfoFacade implements GrammarInfo
{
    private GrammarInfo[] grammarInfos;
    private Grammar bgm;
    
    public GrammarInfoFacade(final GrammarInfo[] items) throws JAXBException {
        this.grammarInfos = null;
        this.bgm = null;
        this.grammarInfos = items;
        this.detectRootElementCollisions(this.getProbePoints());
    }
    
    public UnmarshallingEventHandler createUnmarshaller(final String namespaceUri, final String localName, final UnmarshallingContext context) {
        for (int i = 0; i < this.grammarInfos.length; ++i) {
            final UnmarshallingEventHandler ueh = this.grammarInfos[i].createUnmarshaller(namespaceUri, localName, context);
            if (ueh != null) {
                return ueh;
            }
        }
        return null;
    }
    
    public Class getRootElement(final String namespaceUri, final String localName) {
        for (int i = 0; i < this.grammarInfos.length; ++i) {
            final Class c = this.grammarInfos[i].getRootElement(namespaceUri, localName);
            if (c != null) {
                return c;
            }
        }
        return null;
    }
    
    public boolean recognize(final String nsUri, final String localName) {
        for (int i = 0; i < this.grammarInfos.length; ++i) {
            if (this.grammarInfos[i].recognize(nsUri, localName)) {
                return true;
            }
        }
        return false;
    }
    
    public String[] getProbePoints() {
        final ArrayList probePointList = new ArrayList();
        for (int i = 0; i < this.grammarInfos.length; ++i) {
            final String[] points = this.grammarInfos[i].getProbePoints();
            for (int j = 0; j < points.length; ++j) {
                probePointList.add(points[j]);
            }
        }
        return probePointList.toArray(new String[probePointList.size()]);
    }
    
    private void detectRootElementCollisions(final String[] points) throws JAXBException {
        for (int i = 0; i < points.length; i += 2) {
            boolean elementFound = false;
            for (int j = this.grammarInfos.length - 1; j >= 0; --j) {
                if (this.grammarInfos[j].recognize(points[i], points[i + 1])) {
                    if (elementFound) {
                        throw new JAXBException(Messages.format("GrammarInfoFacade.CollisionDetected", (Object)points[i], (Object)points[i + 1]));
                    }
                    elementFound = true;
                }
            }
        }
    }
    
    static GrammarInfo createGrammarInfoFacade(final String contextPath, final ClassLoader classLoader) throws JAXBException {
        String version = null;
        final ArrayList gis = new ArrayList();
        final StringTokenizer st = new StringTokenizer(contextPath, ":;");
        while (st.hasMoreTokens()) {
            final String targetPackage = st.nextToken();
            final String objectFactoryName = targetPackage + ".ObjectFactory";
            try {
                final JAXBContext c = (JAXBContext)Class.forName(objectFactoryName, true, classLoader).newInstance();
                if (version == null) {
                    version = getVersion(c);
                }
                else if (!version.equals(getVersion(c))) {
                    throw new JAXBException(Messages.format("GrammarInfoFacade.IncompatibleVersion", new Object[] { version, c.getClass().getName(), getVersion(c) }));
                }
                Object grammarInfo = c.getClass().getField("grammarInfo").get(null);
                grammarInfo = ProxyGroup.blindWrap(grammarInfo, (GrammarInfoFacade.class$com$sun$tools$xjc$runtime$GrammarInfo == null) ? (GrammarInfoFacade.class$com$sun$tools$xjc$runtime$GrammarInfo = class$("com.sun.tools.xjc.runtime.GrammarInfo")) : GrammarInfoFacade.class$com$sun$tools$xjc$runtime$GrammarInfo, new Class[] { (GrammarInfoFacade.class$com$sun$tools$xjc$runtime$GrammarInfo == null) ? (GrammarInfoFacade.class$com$sun$tools$xjc$runtime$GrammarInfo = class$("com.sun.tools.xjc.runtime.GrammarInfo")) : GrammarInfoFacade.class$com$sun$tools$xjc$runtime$GrammarInfo, (GrammarInfoFacade.class$com$sun$tools$xjc$runtime$UnmarshallingContext == null) ? (GrammarInfoFacade.class$com$sun$tools$xjc$runtime$UnmarshallingContext = class$("com.sun.tools.xjc.runtime.UnmarshallingContext")) : GrammarInfoFacade.class$com$sun$tools$xjc$runtime$UnmarshallingContext, (GrammarInfoFacade.class$com$sun$tools$xjc$runtime$UnmarshallingEventHandler == null) ? (GrammarInfoFacade.class$com$sun$tools$xjc$runtime$UnmarshallingEventHandler = class$("com.sun.tools.xjc.runtime.UnmarshallingEventHandler")) : GrammarInfoFacade.class$com$sun$tools$xjc$runtime$UnmarshallingEventHandler, (GrammarInfoFacade.class$com$sun$tools$xjc$runtime$XMLSerializer == null) ? (GrammarInfoFacade.class$com$sun$tools$xjc$runtime$XMLSerializer = class$("com.sun.tools.xjc.runtime.XMLSerializer")) : GrammarInfoFacade.class$com$sun$tools$xjc$runtime$XMLSerializer, (GrammarInfoFacade.class$com$sun$tools$xjc$runtime$XMLSerializable == null) ? (GrammarInfoFacade.class$com$sun$tools$xjc$runtime$XMLSerializable = class$("com.sun.tools.xjc.runtime.XMLSerializable")) : GrammarInfoFacade.class$com$sun$tools$xjc$runtime$XMLSerializable, (GrammarInfoFacade.class$com$sun$tools$xjc$runtime$NamespaceContext2 == null) ? (GrammarInfoFacade.class$com$sun$tools$xjc$runtime$NamespaceContext2 = class$("com.sun.tools.xjc.runtime.NamespaceContext2")) : GrammarInfoFacade.class$com$sun$tools$xjc$runtime$NamespaceContext2, (GrammarInfoFacade.class$com$sun$tools$xjc$runtime$ValidatableObject == null) ? (GrammarInfoFacade.class$com$sun$tools$xjc$runtime$ValidatableObject = class$("com.sun.tools.xjc.runtime.ValidatableObject")) : GrammarInfoFacade.class$com$sun$tools$xjc$runtime$ValidatableObject });
                gis.add(grammarInfo);
            }
            catch (ClassNotFoundException e) {
                throw new NoClassDefFoundError(e.getMessage());
            }
            catch (Exception e2) {
                throw new JAXBException(e2);
            }
        }
        if (gis.size() == 1) {
            return gis.get(0);
        }
        return (GrammarInfo)new GrammarInfoFacade((GrammarInfo[])gis.toArray(new GrammarInfo[gis.size()]));
    }
    
    private static String getVersion(final JAXBContext c) throws JAXBException {
        try {
            final Class jaxbBersionClass = (Class)c.getClass().getField("version").get(null);
            return (String)jaxbBersionClass.getField("version").get(null);
        }
        catch (Throwable t) {
            return null;
        }
    }
    
    public Class getDefaultImplementation(final Class javaContentInterface) {
        for (int i = 0; i < this.grammarInfos.length; ++i) {
            final Class c = this.grammarInfos[i].getDefaultImplementation(javaContentInterface);
            if (c != null) {
                return c;
            }
        }
        return null;
    }
    
    public Grammar getGrammar() throws JAXBException {
        if (this.bgm == null) {
            final Grammar[] grammars = new Grammar[this.grammarInfos.length];
            for (int i = 0; i < this.grammarInfos.length; ++i) {
                grammars[i] = this.grammarInfos[i].getGrammar();
            }
            for (int i = 0; i < this.grammarInfos.length; ++i) {
                if (grammars[i] instanceof GrammarImpl) {
                    ((GrammarImpl)grammars[i]).connect(grammars);
                }
            }
            for (int i = 0; i < this.grammarInfos.length; ++i) {
                final Grammar n = grammars[i];
                if (this.bgm == null) {
                    this.bgm = n;
                }
                else {
                    this.bgm = this.union(this.bgm, n);
                }
            }
        }
        return this.bgm;
    }
    
    private Grammar union(final Grammar g1, final Grammar g2) {
        final ExpressionPool pool = g1.getPool();
        final Expression top = pool.createChoice(g1.getTopLevel(), g2.getTopLevel());
        return (Grammar)new GrammarInfoFacade$1(this, pool, top);
    }
    
    public XMLSerializable castToXMLSerializable(final Object o) {
        XMLSerializable result = null;
        for (int i = 0; i < this.grammarInfos.length; ++i) {
            result = this.grammarInfos[i].castToXMLSerializable(o);
            if (result != null) {
                return result;
            }
        }
        return null;
    }
    
    public ValidatableObject castToValidatableObject(final Object o) {
        ValidatableObject result = null;
        for (int i = 0; i < this.grammarInfos.length; ++i) {
            result = this.grammarInfos[i].castToValidatableObject(o);
            if (result != null) {
                return result;
            }
        }
        return null;
    }
}
