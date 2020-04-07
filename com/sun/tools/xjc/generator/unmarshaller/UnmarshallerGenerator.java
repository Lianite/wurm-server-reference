// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.generator.unmarshaller;

import com.sun.msv.grammar.ChoiceNameClass;
import com.sun.msv.grammar.DifferenceNameClass;
import com.sun.msv.grammar.NotNameClass;
import com.sun.msv.grammar.AnyNameClass;
import com.sun.msv.grammar.NamespaceNameClass;
import com.sun.msv.grammar.SimpleNameClass;
import java.util.HashSet;
import java.util.Set;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JType;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JDefinedClass;
import com.sun.tools.xjc.generator.StaticMapGenerator;
import com.sun.codemodel.JExpression;
import com.sun.codemodel.JVar;
import com.sun.msv.grammar.ExpressionVisitorVoid;
import com.sun.codemodel.JPackage;
import com.sun.msv.util.StringPair;
import com.sun.tools.xjc.generator.PackageContext;
import com.sun.msv.grammar.NameClassVisitor;
import com.sun.msv.grammar.NameClass;
import java.util.Map;
import com.sun.tools.xjc.grammar.ClassItem;
import java.util.HashMap;
import com.sun.tools.xjc.generator.unmarshaller.automaton.Automaton;
import com.sun.tools.xjc.generator.GeneratorContext;
import com.sun.codemodel.JCodeModel;
import com.sun.tools.xjc.grammar.AnnotatedGrammar;
import com.sun.tools.xjc.Options;

public class UnmarshallerGenerator
{
    final Options options;
    final AnnotatedGrammar grammar;
    final JCodeModel codeModel;
    final GeneratorContext context;
    final boolean trace;
    
    public static Automaton[] generate(final AnnotatedGrammar grammar, final GeneratorContext context, final Options opt) {
        return new UnmarshallerGenerator(grammar, context, opt)._generate();
    }
    
    private Automaton[] _generate() {
        final ClassItem[] cis = this.grammar.getClasses();
        final Automaton[] automata = new Automaton[cis.length];
        final Map automataDic = new HashMap();
        for (int i = 0; i < cis.length; ++i) {
            automata[i] = new Automaton(this.context.getClassContext(cis[i]));
            automataDic.put(cis[i], automata[i]);
        }
        for (int i = 0; i < automata.length; ++i) {
            AutomatonBuilder.build(automata[i], this.context, automataDic);
        }
        if (this.options.debugMode && this.options.verbose) {
            for (int i = 0; i < cis.length; ++i) {
                System.out.println(cis[i].getType().fullName());
                System.out.println("nullable: " + automata[i].isNullable());
                System.out.println();
            }
        }
        for (int i = 0; i < automata.length; ++i) {
            new PerClassGenerator(this, automata[i]).generate();
        }
        this.generateGrammarInfoImpl();
        return automata;
    }
    
    private void generateGrammarInfoImpl() {
        final PackageContext[] pcs = this.context.getAllPackageContexts();
        for (int i = 0; i < pcs.length; ++i) {
            final RootMapBuilder rmb = new RootMapBuilder(pcs[i].rootTagMap, pcs[i].objectFactory);
            final Map roots = this.getRootMap(pcs[i]._package);
            final ClassItem[] classes = (ClassItem[])roots.keySet().toArray(new ClassItem[roots.size()]);
            final NameClass[] nameClasses = (NameClass[])roots.values().toArray(new NameClass[roots.size()]);
            final ProbePointBuilder ppb = new ProbePointBuilder((UnmarshallerGenerator$1)null);
            for (int j = 0; j < nameClasses.length; ++j) {
                nameClasses[j].visit((NameClassVisitor)ppb);
            }
            final StringPair[] probePoints = ppb.getResult();
            for (int k = 0; k < probePoints.length; ++k) {
                int l;
                for (l = 0; l < nameClasses.length; ++l) {
                    if (nameClasses[l].accepts(probePoints[k])) {
                        rmb.add(probePoints[k], classes[l]);
                        break;
                    }
                }
                if (l == nameClasses.length) {
                    rmb.add(probePoints[k], (ClassItem)null);
                }
            }
        }
    }
    
    private Map getRootMap(final JPackage currentPackage) {
        final Map roots = new HashMap();
        this.grammar.getTopLevel().visit((ExpressionVisitorVoid)new UnmarshallerGenerator$1(this, currentPackage, roots));
        return roots;
    }
    
    UnmarshallerGenerator(final AnnotatedGrammar _grammar, final GeneratorContext _context, final Options _opt) {
        this.options = _opt;
        this.trace = this.options.traceUnmarshaller;
        this.grammar = _grammar;
        this.codeModel = this.grammar.codeModel;
        this.context = _context;
    }
    
    protected JExpression generateNameClassTest(final NameClass nc, final JVar $uri, final JVar $local) {
        return (JExpression)nc.visit((NameClassVisitor)new UnmarshallerGenerator$2(this, $local, $uri));
    }
    
    private static class RootMapBuilder extends StaticMapGenerator
    {
        private final JDefinedClass objectFactory;
        private final JCodeModel codeModel;
        
        RootMapBuilder(final JVar $rootTagMap, final JDefinedClass _objectFactory) {
            super($rootTagMap, _objectFactory.init());
            this.objectFactory = _objectFactory;
            this.codeModel = this.objectFactory.owner();
        }
        
        protected JMethod createNewMethod(final int uniqueId) {
            return this.objectFactory.method(20, this.codeModel.VOID, "__initRootMap" + uniqueId);
        }
        
        public void add(final StringPair tagName, final ClassItem value) {
            super.add(JExpr._new(this.codeModel.ref((UnmarshallerGenerator.class$javax$xml$namespace$QName == null) ? (UnmarshallerGenerator.class$javax$xml$namespace$QName = UnmarshallerGenerator.class$("javax.xml.namespace.QName")) : UnmarshallerGenerator.class$javax$xml$namespace$QName)).arg(JExpr.lit(tagName.namespaceURI)).arg(JExpr.lit(tagName.localName)), (value != null) ? value.getTypeAsDefined().dotclass() : JExpr._null());
        }
    }
    
    private static class ProbePointBuilder implements NameClassVisitor
    {
        private final Set probePoints;
        
        private ProbePointBuilder() {
            this.probePoints = new HashSet();
        }
        
        public StringPair[] getResult() {
            return this.probePoints.toArray(new StringPair[this.probePoints.size()]);
        }
        
        public Object onSimple(final SimpleNameClass nc) {
            this.probePoints.add(nc.toStringPair());
            return null;
        }
        
        public Object onNsName(final NamespaceNameClass nc) {
            this.probePoints.add(new StringPair(nc.namespaceURI, "*"));
            return null;
        }
        
        public Object onAnyName(final AnyNameClass nc) {
            this.probePoints.add(new StringPair("*", "*"));
            return null;
        }
        
        public Object onNot(final NotNameClass nc) {
            nc.child.visit((NameClassVisitor)this);
            return null;
        }
        
        public Object onDifference(final DifferenceNameClass nc) {
            nc.nc1.visit((NameClassVisitor)this);
            nc.nc2.visit((NameClassVisitor)this);
            return null;
        }
        
        public Object onChoice(final ChoiceNameClass nc) {
            nc.nc1.visit((NameClassVisitor)this);
            nc.nc2.visit((NameClassVisitor)this);
            return null;
        }
    }
}
