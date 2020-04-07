// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.addon.episode;

import com.sun.xml.xsom.XSModelGroup;
import com.sun.xml.xsom.XSModelGroupDecl;
import com.sun.xml.xsom.XSWildcard;
import com.sun.xml.xsom.XSContentType;
import com.sun.xml.xsom.XSParticle;
import com.sun.xml.xsom.XSXPath;
import com.sun.xml.xsom.XSIdentityConstraint;
import com.sun.xml.xsom.XSNotation;
import com.sun.xml.xsom.XSFacet;
import com.sun.xml.xsom.XSAttributeUse;
import com.sun.xml.xsom.XSAttributeDecl;
import com.sun.xml.xsom.XSAttGroupDecl;
import com.sun.xml.xsom.XSAnnotation;
import com.sun.xml.xsom.XSElementDecl;
import com.sun.xml.xsom.XSSimpleType;
import com.sun.xml.xsom.XSComplexType;
import org.xml.sax.SAXException;
import java.io.OutputStream;
import com.sun.xml.xsom.XSComponent;
import java.util.Iterator;
import org.xml.sax.Locator;
import org.xml.sax.SAXParseException;
import java.util.Map;
import com.sun.xml.txw2.output.XmlSerializer;
import com.sun.xml.txw2.TXW;
import com.sun.xml.txw2.output.StreamSerializer;
import com.sun.xml.bind.v2.schemagen.episode.Bindings;
import java.io.FileOutputStream;
import java.util.ArrayList;
import com.sun.xml.xsom.XSDeclaration;
import com.sun.tools.xjc.outline.ClassOutline;
import java.util.List;
import com.sun.xml.xsom.XSSchema;
import java.util.HashMap;
import org.xml.sax.ErrorHandler;
import com.sun.tools.xjc.outline.Outline;
import java.io.IOException;
import com.sun.tools.xjc.BadCommandLineException;
import com.sun.tools.xjc.Options;
import com.sun.xml.xsom.visitor.XSFunction;
import java.io.File;
import com.sun.tools.xjc.Plugin;

public class PluginImpl extends Plugin
{
    private File episodeFile;
    private static final XSFunction<String> SCD;
    
    public String getOptionName() {
        return "episode";
    }
    
    public String getUsage() {
        return "  -episode <FILE>    :  generate the episode file for separate compilation";
    }
    
    public int parseArgument(final Options opt, final String[] args, int i) throws BadCommandLineException, IOException {
        if (args[i].equals("-episode")) {
            this.episodeFile = new File(opt.requireArgument("-episode", args, ++i));
            return 2;
        }
        return 0;
    }
    
    public boolean run(final Outline model, final Options opt, final ErrorHandler errorHandler) throws SAXException {
        try {
            final Map<XSSchema, List<ClassOutline>> perSchema = new HashMap<XSSchema, List<ClassOutline>>();
            boolean hasComponentInNoNamespace = false;
            for (final ClassOutline co : model.getClasses()) {
                final XSComponent sc = co.target.getSchemaComponent();
                if (sc == null) {
                    continue;
                }
                if (!(sc instanceof XSDeclaration)) {
                    continue;
                }
                final XSDeclaration decl = (XSDeclaration)sc;
                if (decl.isLocal()) {
                    continue;
                }
                List<ClassOutline> list = perSchema.get(decl.getOwnerSchema());
                if (list == null) {
                    list = new ArrayList<ClassOutline>();
                    perSchema.put(decl.getOwnerSchema(), list);
                }
                list.add(co);
                if (!decl.getTargetNamespace().equals("")) {
                    continue;
                }
                hasComponentInNoNamespace = true;
            }
            final OutputStream os = new FileOutputStream(this.episodeFile);
            final Bindings bindings = TXW.create(Bindings.class, new StreamSerializer(os, "UTF-8"));
            if (hasComponentInNoNamespace) {
                bindings._namespace("http://java.sun.com/xml/ns/jaxb", "jaxb");
            }
            else {
                bindings._namespace("http://java.sun.com/xml/ns/jaxb", "");
            }
            bindings.version("2.1");
            bindings._comment("\n\n" + opt.getPrologComment() + "\n  ");
            for (final Map.Entry<XSSchema, List<ClassOutline>> e : perSchema.entrySet()) {
                final Bindings group = bindings.bindings();
                final String tns = e.getKey().getTargetNamespace();
                if (!tns.equals("")) {
                    group._namespace(tns, "tns");
                }
                group.scd("x-schema::" + (tns.equals("") ? "" : "tns"));
                group.schemaBindings().map(false);
                for (final ClassOutline co2 : e.getValue()) {
                    final Bindings child = group.bindings();
                    child.scd(co2.target.getSchemaComponent().apply(PluginImpl.SCD));
                    child.klass().ref(co2.implClass.fullName());
                }
                group.commit(true);
            }
            bindings.commit();
            return true;
        }
        catch (IOException e2) {
            errorHandler.error(new SAXParseException("Failed to write to " + this.episodeFile, null, e2));
            return false;
        }
    }
    
    static {
        SCD = new XSFunction<String>() {
            private String name(final XSDeclaration decl) {
                if (decl.getTargetNamespace().equals("")) {
                    return decl.getName();
                }
                return "tns:" + decl.getName();
            }
            
            public String complexType(final XSComplexType type) {
                return "~" + this.name(type);
            }
            
            public String simpleType(final XSSimpleType simpleType) {
                return "~" + this.name(simpleType);
            }
            
            public String elementDecl(final XSElementDecl decl) {
                return this.name(decl);
            }
            
            public String annotation(final XSAnnotation ann) {
                throw new UnsupportedOperationException();
            }
            
            public String attGroupDecl(final XSAttGroupDecl decl) {
                throw new UnsupportedOperationException();
            }
            
            public String attributeDecl(final XSAttributeDecl decl) {
                throw new UnsupportedOperationException();
            }
            
            public String attributeUse(final XSAttributeUse use) {
                throw new UnsupportedOperationException();
            }
            
            public String schema(final XSSchema schema) {
                throw new UnsupportedOperationException();
            }
            
            public String facet(final XSFacet facet) {
                throw new UnsupportedOperationException();
            }
            
            public String notation(final XSNotation notation) {
                throw new UnsupportedOperationException();
            }
            
            public String identityConstraint(final XSIdentityConstraint decl) {
                throw new UnsupportedOperationException();
            }
            
            public String xpath(final XSXPath xpath) {
                throw new UnsupportedOperationException();
            }
            
            public String particle(final XSParticle particle) {
                throw new UnsupportedOperationException();
            }
            
            public String empty(final XSContentType empty) {
                throw new UnsupportedOperationException();
            }
            
            public String wildcard(final XSWildcard wc) {
                throw new UnsupportedOperationException();
            }
            
            public String modelGroupDecl(final XSModelGroupDecl decl) {
                throw new UnsupportedOperationException();
            }
            
            public String modelGroup(final XSModelGroup group) {
                throw new UnsupportedOperationException();
            }
        };
    }
}
