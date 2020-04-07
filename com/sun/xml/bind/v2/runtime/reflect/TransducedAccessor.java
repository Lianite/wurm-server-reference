// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.xml.bind.v2.runtime.reflect;

import java.util.concurrent.Callable;
import com.sun.xml.bind.v2.runtime.unmarshaller.Patcher;
import com.sun.xml.bind.v2.runtime.unmarshaller.LocatorEx;
import com.sun.istack.SAXException2;
import com.sun.xml.bind.WhiteSpaceProcessor;
import com.sun.xml.bind.v2.runtime.unmarshaller.UnmarshallingContext;
import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import com.sun.xml.bind.v2.runtime.Name;
import com.sun.xml.bind.v2.model.runtime.RuntimePropertyInfo;
import com.sun.xml.bind.v2.runtime.reflect.opt.OptimizedTransducedAccessorFactory;
import com.sun.xml.bind.v2.model.core.ID;
import com.sun.xml.bind.v2.runtime.Transducer;
import com.sun.xml.bind.v2.model.core.Adapter;
import java.lang.reflect.Type;
import com.sun.xml.bind.v2.model.nav.Navigator;
import com.sun.xml.bind.v2.model.impl.RuntimeModelBuilder;
import com.sun.xml.bind.v2.model.runtime.RuntimeNonElementRef;
import com.sun.xml.bind.v2.runtime.JAXBContextImpl;
import com.sun.istack.Nullable;
import com.sun.istack.NotNull;
import org.xml.sax.SAXException;
import com.sun.xml.bind.api.AccessorException;
import com.sun.xml.bind.v2.runtime.XMLSerializer;

public abstract class TransducedAccessor<BeanT>
{
    public boolean useNamespace() {
        return false;
    }
    
    public void declareNamespace(final BeanT o, final XMLSerializer w) throws AccessorException, SAXException {
    }
    
    @Nullable
    public abstract CharSequence print(@NotNull final BeanT p0) throws AccessorException, SAXException;
    
    public abstract void parse(final BeanT p0, final CharSequence p1) throws AccessorException, SAXException;
    
    public abstract boolean hasValue(final BeanT p0) throws AccessorException;
    
    public static <T> TransducedAccessor<T> get(final JAXBContextImpl context, final RuntimeNonElementRef ref) {
        final Transducer xducer = RuntimeModelBuilder.createTransducer(ref);
        final RuntimePropertyInfo prop = ref.getSource();
        if (prop.isCollection()) {
            return (TransducedAccessor<T>)new ListTransducedAccessorImpl(xducer, prop.getAccessor(), Lister.create(Navigator.REFLECTION.erasure(prop.getRawType()), prop.id(), (Adapter<Type, Class>)prop.getAdapter()));
        }
        if (prop.id() == ID.IDREF) {
            return (TransducedAccessor<T>)new IDREFTransducedAccessorImpl(prop.getAccessor());
        }
        if (xducer.isDefault() && !context.fastBoot) {
            final TransducedAccessor xa = OptimizedTransducedAccessorFactory.get(prop);
            if (xa != null) {
                return (TransducedAccessor<T>)xa;
            }
        }
        if (xducer.useNamespace()) {
            return (TransducedAccessor<T>)new CompositeContextDependentTransducedAccessorImpl(context, xducer, prop.getAccessor());
        }
        return (TransducedAccessor<T>)new CompositeTransducedAccessorImpl(context, xducer, prop.getAccessor());
    }
    
    public abstract void writeLeafElement(final XMLSerializer p0, final Name p1, final BeanT p2, final String p3) throws SAXException, AccessorException, IOException, XMLStreamException;
    
    public abstract void writeText(final XMLSerializer p0, final BeanT p1, final String p2) throws AccessorException, SAXException, IOException, XMLStreamException;
    
    static class CompositeContextDependentTransducedAccessorImpl<BeanT, ValueT> extends CompositeTransducedAccessorImpl<BeanT, ValueT>
    {
        public CompositeContextDependentTransducedAccessorImpl(final JAXBContextImpl context, final Transducer<ValueT> xducer, final Accessor<BeanT, ValueT> acc) {
            super(context, xducer, acc);
            assert xducer.useNamespace();
        }
        
        public boolean useNamespace() {
            return true;
        }
        
        public void declareNamespace(final BeanT bean, final XMLSerializer w) throws AccessorException {
            final ValueT o = this.acc.get(bean);
            if (o != null) {
                this.xducer.declareNamespace(o, w);
            }
        }
        
        public void writeLeafElement(final XMLSerializer w, final Name tagName, final BeanT o, final String fieldName) throws SAXException, AccessorException, IOException, XMLStreamException {
            w.startElement(tagName, null);
            this.declareNamespace(o, w);
            w.endNamespaceDecls(null);
            w.endAttributes();
            this.xducer.writeText(w, this.acc.get(o), fieldName);
            w.endElement();
        }
    }
    
    static class CompositeTransducedAccessorImpl<BeanT, ValueT> extends TransducedAccessor<BeanT>
    {
        protected final Transducer<ValueT> xducer;
        protected final Accessor<BeanT, ValueT> acc;
        
        public CompositeTransducedAccessorImpl(final JAXBContextImpl context, final Transducer<ValueT> xducer, final Accessor<BeanT, ValueT> acc) {
            this.xducer = xducer;
            this.acc = acc.optimize(context);
        }
        
        public CharSequence print(final BeanT bean) throws AccessorException {
            final ValueT o = this.acc.get(bean);
            if (o == null) {
                return null;
            }
            return this.xducer.print(o);
        }
        
        public void parse(final BeanT bean, final CharSequence lexical) throws AccessorException, SAXException {
            this.acc.set(bean, this.xducer.parse(lexical));
        }
        
        public boolean hasValue(final BeanT bean) throws AccessorException {
            return this.acc.getUnadapted(bean) != null;
        }
        
        public void writeLeafElement(final XMLSerializer w, final Name tagName, final BeanT o, final String fieldName) throws SAXException, AccessorException, IOException, XMLStreamException {
            this.xducer.writeLeafElement(w, tagName, this.acc.get(o), fieldName);
        }
        
        public void writeText(final XMLSerializer w, final BeanT o, final String fieldName) throws AccessorException, SAXException, IOException, XMLStreamException {
            this.xducer.writeText(w, this.acc.get(o), fieldName);
        }
    }
    
    private static final class IDREFTransducedAccessorImpl<BeanT, TargetT> extends DefaultTransducedAccessor<BeanT>
    {
        private final Accessor<BeanT, TargetT> acc;
        private final Class<TargetT> targetType;
        
        public IDREFTransducedAccessorImpl(final Accessor<BeanT, TargetT> acc) {
            this.acc = acc;
            this.targetType = acc.getValueType();
        }
        
        public String print(final BeanT bean) throws AccessorException, SAXException {
            final TargetT target = this.acc.get(bean);
            if (target == null) {
                return null;
            }
            final XMLSerializer w = XMLSerializer.getInstance();
            try {
                final String id = w.grammar.getBeanInfo(target, true).getId(target, w);
                if (id == null) {
                    w.errorMissingId(target);
                }
                return id;
            }
            catch (JAXBException e) {
                w.reportError(null, e);
                return null;
            }
        }
        
        private void assign(final BeanT bean, final TargetT t, final UnmarshallingContext context) throws AccessorException {
            if (!this.targetType.isInstance(t)) {
                context.handleError(Messages.UNASSIGNABLE_TYPE.format(this.targetType, t.getClass()));
            }
            else {
                this.acc.set(bean, t);
            }
        }
        
        public void parse(final BeanT bean, final CharSequence lexical) throws AccessorException, SAXException {
            final String idref = WhiteSpaceProcessor.trim(lexical).toString();
            final UnmarshallingContext context = UnmarshallingContext.getInstance();
            final Callable callable = context.getObjectFromId(idref, this.acc.valueType);
            if (callable == null) {
                context.errorUnresolvedIDREF(bean, idref, context.getLocator());
                return;
            }
            TargetT t;
            try {
                t = callable.call();
            }
            catch (SAXException e) {
                throw e;
            }
            catch (RuntimeException e2) {
                throw e2;
            }
            catch (Exception e3) {
                throw new SAXException2(e3);
            }
            if (t != null) {
                this.assign(bean, t, context);
            }
            else {
                final LocatorEx loc = new LocatorEx.Snapshot(context.getLocator());
                context.addPatcher(new Patcher() {
                    public void run() throws SAXException {
                        try {
                            final TargetT t = callable.call();
                            if (t == null) {
                                context.errorUnresolvedIDREF(bean, idref, loc);
                            }
                            else {
                                IDREFTransducedAccessorImpl.this.assign(bean, t, context);
                            }
                        }
                        catch (AccessorException e) {
                            context.handleError(e);
                        }
                        catch (SAXException e2) {
                            throw e2;
                        }
                        catch (RuntimeException e3) {
                            throw e3;
                        }
                        catch (Exception e4) {
                            throw new SAXException2(e4);
                        }
                    }
                });
            }
        }
        
        public boolean hasValue(final BeanT bean) throws AccessorException {
            return this.acc.get(bean) != null;
        }
    }
}
