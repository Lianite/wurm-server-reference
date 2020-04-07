// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.xml.bind.v2.runtime.unmarshaller;

import javax.xml.namespace.QName;
import java.util.Collection;
import javax.xml.bind.JAXBElement;
import org.xml.sax.helpers.LocatorImpl;
import com.sun.xml.bind.api.AccessorException;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;
import java.util.Iterator;
import java.util.concurrent.Callable;
import javax.xml.bind.ValidationEventLocator;
import javax.xml.bind.helpers.ValidationEventImpl;
import org.xml.sax.Locator;
import com.sun.istack.SAXParseException2;
import javax.xml.bind.ValidationEvent;
import java.lang.reflect.InvocationTargetException;
import com.sun.xml.bind.v2.ClassFactory;
import javax.xml.bind.UnmarshalException;
import java.lang.reflect.Method;
import org.xml.sax.SAXException;
import com.sun.xml.bind.v2.runtime.JAXBContextImpl;
import java.util.HashMap;
import java.util.Map;
import com.sun.istack.Nullable;
import com.sun.xml.bind.api.ClassResolver;
import com.sun.xml.bind.unmarshaller.InfosetScanner;
import com.sun.xml.bind.v2.runtime.AssociationMap;
import com.sun.xml.bind.IDResolver;
import com.sun.xml.bind.v2.runtime.JaxBeanInfo;
import com.sun.istack.NotNull;
import org.xml.sax.ErrorHandler;
import javax.xml.bind.ValidationEventHandler;
import javax.xml.namespace.NamespaceContext;
import com.sun.xml.bind.v2.runtime.Coordinator;

public final class UnmarshallingContext extends Coordinator implements NamespaceContext, ValidationEventHandler, ErrorHandler, XmlVisitor, TextPredictor
{
    private final State root;
    private State current;
    private static final LocatorEx DUMMY_INSTANCE;
    @NotNull
    private LocatorEx locator;
    private Object result;
    private JaxBeanInfo expectedType;
    private IDResolver idResolver;
    private boolean isUnmarshalInProgress;
    private boolean aborted;
    public final UnmarshallerImpl parent;
    private final AssociationMap assoc;
    private boolean isInplaceMode;
    private InfosetScanner scanner;
    private Object currentElement;
    private NamespaceContext environmentNamespaceContext;
    @Nullable
    public ClassResolver classResolver;
    private final Map<Class, Factory> factories;
    private Patcher[] patchers;
    private int patchersLen;
    private String[] nsBind;
    private int nsLen;
    private Scope[] scopes;
    private int scopeTop;
    private static final Loader DEFAULT_ROOT_LOADER;
    private static final Loader EXPECTED_TYPE_ROOT_LOADER;
    
    public UnmarshallingContext(final UnmarshallerImpl _parent, final AssociationMap assoc) {
        this.locator = UnmarshallingContext.DUMMY_INSTANCE;
        this.isUnmarshalInProgress = true;
        this.aborted = false;
        this.factories = new HashMap<Class, Factory>();
        this.patchers = null;
        this.patchersLen = 0;
        this.nsBind = new String[16];
        this.nsLen = 0;
        this.scopes = new Scope[16];
        this.scopeTop = 0;
        for (int i = 0; i < this.scopes.length; ++i) {
            this.scopes[i] = new Scope(this);
        }
        this.parent = _parent;
        this.assoc = assoc;
        final State state = new State((State)null);
        this.current = state;
        this.root = state;
        this.allocateMoreStates();
    }
    
    public void reset(final InfosetScanner scanner, final boolean isInplaceMode, final JaxBeanInfo expectedType, final IDResolver idResolver) {
        this.scanner = scanner;
        this.isInplaceMode = isInplaceMode;
        this.expectedType = expectedType;
        this.idResolver = idResolver;
    }
    
    public JAXBContextImpl getJAXBContext() {
        return this.parent.context;
    }
    
    public State getCurrentState() {
        return this.current;
    }
    
    public Loader selectRootLoader(final State state, final TagName tag) throws SAXException {
        try {
            final Loader l = this.getJAXBContext().selectRootLoader(state, tag);
            if (l != null) {
                return l;
            }
            if (this.classResolver != null) {
                final Class<?> clazz = this.classResolver.resolveElementName(tag.uri, tag.local);
                if (clazz != null) {
                    final JAXBContextImpl enhanced = this.getJAXBContext().createAugmented(clazz);
                    final JaxBeanInfo<?> bi = enhanced.getBeanInfo(clazz);
                    return bi.getLoader(enhanced, true);
                }
            }
        }
        catch (RuntimeException e) {
            throw e;
        }
        catch (Exception e2) {
            this.handleError(e2);
        }
        return null;
    }
    
    private void allocateMoreStates() {
        assert this.current.next == null;
        State s = this.current;
        for (int i = 0; i < 8; ++i) {
            s = new State(s);
        }
    }
    
    public void setFactories(final Object factoryInstances) {
        this.factories.clear();
        if (factoryInstances == null) {
            return;
        }
        if (factoryInstances instanceof Object[]) {
            for (final Object factory : (Object[])factoryInstances) {
                this.addFactory(factory);
            }
        }
        else {
            this.addFactory(factoryInstances);
        }
    }
    
    private void addFactory(final Object factory) {
        for (final Method m : factory.getClass().getMethods()) {
            if (m.getName().startsWith("create")) {
                if (m.getParameterTypes().length <= 0) {
                    final Class type = m.getReturnType();
                    this.factories.put(type, new Factory(factory, m));
                }
            }
        }
    }
    
    public void startDocument(final LocatorEx locator, final NamespaceContext nsContext) throws SAXException {
        if (locator != null) {
            this.locator = locator;
        }
        this.environmentNamespaceContext = nsContext;
        this.result = null;
        this.current = this.root;
        this.patchersLen = 0;
        this.aborted = false;
        this.isUnmarshalInProgress = true;
        this.nsLen = 0;
        this.setThreadAffinity();
        if (this.expectedType != null) {
            this.root.loader = UnmarshallingContext.EXPECTED_TYPE_ROOT_LOADER;
        }
        else {
            this.root.loader = UnmarshallingContext.DEFAULT_ROOT_LOADER;
        }
        this.idResolver.startDocument(this);
    }
    
    public void startElement(final TagName tagName) throws SAXException {
        this.pushCoordinator();
        try {
            this._startElement(tagName);
        }
        finally {
            this.popCoordinator();
        }
    }
    
    private void _startElement(final TagName tagName) throws SAXException {
        if (this.assoc != null) {
            this.currentElement = this.scanner.getCurrentElement();
        }
        final Loader h = this.current.loader;
        this.current.push();
        h.childElement(this.current, tagName);
        assert this.current.loader != null;
        this.current.loader.startElement(this.current, tagName);
    }
    
    public void text(CharSequence pcdata) throws SAXException {
        final State cur = this.current;
        this.pushCoordinator();
        try {
            if (cur.elementDefaultValue != null && pcdata.length() == 0) {
                pcdata = cur.elementDefaultValue;
            }
            cur.loader.text(cur, pcdata);
        }
        finally {
            this.popCoordinator();
        }
    }
    
    public final void endElement(final TagName tagName) throws SAXException {
        this.pushCoordinator();
        try {
            final State child = this.current;
            child.loader.leaveElement(child, tagName);
            Object target = child.target;
            final Receiver recv = child.receiver;
            final Intercepter intercepter = child.intercepter;
            child.pop();
            if (intercepter != null) {
                target = intercepter.intercept(this.current, target);
            }
            if (recv != null) {
                recv.receive(this.current, target);
            }
        }
        finally {
            this.popCoordinator();
        }
    }
    
    public void endDocument() throws SAXException {
        this.runPatchers();
        this.idResolver.endDocument();
        this.isUnmarshalInProgress = false;
        this.currentElement = null;
        this.locator = UnmarshallingContext.DUMMY_INSTANCE;
        this.environmentNamespaceContext = null;
        assert this.root == this.current;
        this.resetThreadAffinity();
    }
    
    @Deprecated
    public boolean expectText() {
        return this.current.loader.expectText;
    }
    
    @Deprecated
    public TextPredictor getPredictor() {
        return this;
    }
    
    public UnmarshallingContext getContext() {
        return this;
    }
    
    public Object getResult() throws UnmarshalException {
        if (this.isUnmarshalInProgress) {
            throw new IllegalStateException();
        }
        if (!this.aborted) {
            return this.result;
        }
        throw new UnmarshalException((String)null);
    }
    
    public Object createInstance(final Class<?> clazz) throws SAXException {
        if (!this.factories.isEmpty()) {
            final Factory factory = this.factories.get(clazz);
            if (factory != null) {
                return factory.createInstance();
            }
        }
        return ClassFactory.create(clazz);
    }
    
    public Object createInstance(final JaxBeanInfo beanInfo) throws SAXException {
        if (!this.factories.isEmpty()) {
            final Factory factory = this.factories.get(beanInfo.jaxbType);
            if (factory != null) {
                return factory.createInstance();
            }
        }
        try {
            return beanInfo.createInstance(this);
        }
        catch (IllegalAccessException e) {
            Loader.reportError("Unable to create an instance of " + beanInfo.jaxbType.getName(), e, false);
        }
        catch (InvocationTargetException e2) {
            Loader.reportError("Unable to create an instance of " + beanInfo.jaxbType.getName(), e2, false);
        }
        catch (InstantiationException e3) {
            Loader.reportError("Unable to create an instance of " + beanInfo.jaxbType.getName(), e3, false);
        }
        return null;
    }
    
    public void handleEvent(final ValidationEvent event, final boolean canRecover) throws SAXException {
        final ValidationEventHandler eventHandler = this.parent.getEventHandler();
        final boolean recover = eventHandler.handleEvent(event);
        if (!recover) {
            this.aborted = true;
        }
        if (!canRecover || !recover) {
            throw new SAXParseException2(event.getMessage(), this.locator, new UnmarshalException(event.getMessage(), event.getLinkedException()));
        }
    }
    
    public boolean handleEvent(final ValidationEvent event) {
        try {
            final boolean recover = this.parent.getEventHandler().handleEvent(event);
            if (!recover) {
                this.aborted = true;
            }
            return recover;
        }
        catch (RuntimeException re) {
            return false;
        }
    }
    
    public void handleError(final Exception e) throws SAXException {
        this.handleError(e, true);
    }
    
    public void handleError(final Exception e, final boolean canRecover) throws SAXException {
        this.handleEvent(new ValidationEventImpl(1, e.getMessage(), this.locator.getLocation(), e), canRecover);
    }
    
    public void handleError(final String msg) {
        this.handleEvent(new ValidationEventImpl(1, msg, this.locator.getLocation()));
    }
    
    protected ValidationEventLocator getLocation() {
        return this.locator.getLocation();
    }
    
    public LocatorEx getLocator() {
        return this.locator;
    }
    
    public void errorUnresolvedIDREF(final Object bean, final String idref, final LocatorEx loc) throws SAXException {
        this.handleEvent(new ValidationEventImpl(1, Messages.UNRESOLVED_IDREF.format(idref), loc.getLocation()), true);
    }
    
    public void addPatcher(final Patcher job) {
        if (this.patchers == null) {
            this.patchers = new Patcher[32];
        }
        if (this.patchers.length == this.patchersLen) {
            final Patcher[] buf = new Patcher[this.patchersLen * 2];
            System.arraycopy(this.patchers, 0, buf, 0, this.patchersLen);
            this.patchers = buf;
        }
        this.patchers[this.patchersLen++] = job;
    }
    
    private void runPatchers() throws SAXException {
        if (this.patchers != null) {
            for (int i = 0; i < this.patchersLen; ++i) {
                this.patchers[i].run();
                this.patchers[i] = null;
            }
        }
    }
    
    public String addToIdTable(final String id) throws SAXException {
        Object o = this.current.target;
        if (o == null) {
            o = this.current.prev.target;
        }
        this.idResolver.bind(id, o);
        return id;
    }
    
    public Callable getObjectFromId(final String id, final Class targetType) throws SAXException {
        return this.idResolver.resolve(id, targetType);
    }
    
    public void startPrefixMapping(final String prefix, final String uri) {
        if (this.nsBind.length == this.nsLen) {
            final String[] n = new String[this.nsLen * 2];
            System.arraycopy(this.nsBind, 0, n, 0, this.nsLen);
            this.nsBind = n;
        }
        this.nsBind[this.nsLen++] = prefix;
        this.nsBind[this.nsLen++] = uri;
    }
    
    public void endPrefixMapping(final String prefix) {
        this.nsLen -= 2;
    }
    
    private String resolveNamespacePrefix(final String prefix) {
        if (prefix.equals("xml")) {
            return "http://www.w3.org/XML/1998/namespace";
        }
        for (int i = this.nsLen - 2; i >= 0; i -= 2) {
            if (prefix.equals(this.nsBind[i])) {
                return this.nsBind[i + 1];
            }
        }
        if (this.environmentNamespaceContext != null) {
            return this.environmentNamespaceContext.getNamespaceURI(prefix.intern());
        }
        if (prefix.equals("")) {
            return "";
        }
        return null;
    }
    
    public String[] getNewlyDeclaredPrefixes() {
        return this.getPrefixList(this.current.prev.numNsDecl);
    }
    
    public String[] getAllDeclaredPrefixes() {
        return this.getPrefixList(0);
    }
    
    private String[] getPrefixList(final int startIndex) {
        final int size = (this.current.numNsDecl - startIndex) / 2;
        final String[] r = new String[size];
        for (int i = 0; i < r.length; ++i) {
            r[i] = this.nsBind[startIndex + i * 2];
        }
        return r;
    }
    
    public Iterator<String> getPrefixes(final String uri) {
        return Collections.unmodifiableList((List<? extends String>)this.getAllPrefixesInList(uri)).iterator();
    }
    
    private List<String> getAllPrefixesInList(final String uri) {
        final List<String> a = new ArrayList<String>();
        if (uri == null) {
            throw new IllegalArgumentException();
        }
        if (uri.equals("http://www.w3.org/XML/1998/namespace")) {
            a.add("xml");
            return a;
        }
        if (uri.equals("http://www.w3.org/2000/xmlns/")) {
            a.add("xmlns");
            return a;
        }
        for (int i = this.nsLen - 2; i >= 0; i -= 2) {
            if (uri.equals(this.nsBind[i + 1]) && this.getNamespaceURI(this.nsBind[i]).equals(this.nsBind[i + 1])) {
                a.add(this.nsBind[i]);
            }
        }
        return a;
    }
    
    public String getPrefix(final String uri) {
        if (uri == null) {
            throw new IllegalArgumentException();
        }
        if (uri.equals("http://www.w3.org/XML/1998/namespace")) {
            return "xml";
        }
        if (uri.equals("http://www.w3.org/2000/xmlns/")) {
            return "xmlns";
        }
        for (int i = this.nsLen - 2; i >= 0; i -= 2) {
            if (uri.equals(this.nsBind[i + 1]) && this.getNamespaceURI(this.nsBind[i]).equals(this.nsBind[i + 1])) {
                return this.nsBind[i];
            }
        }
        if (this.environmentNamespaceContext != null) {
            return this.environmentNamespaceContext.getPrefix(uri);
        }
        return null;
    }
    
    public String getNamespaceURI(final String prefix) {
        if (prefix == null) {
            throw new IllegalArgumentException();
        }
        if (prefix.equals("xmlns")) {
            return "http://www.w3.org/2000/xmlns/";
        }
        return this.resolveNamespacePrefix(prefix);
    }
    
    public void startScope(final int frameSize) {
        this.scopeTop += frameSize;
        if (this.scopeTop >= this.scopes.length) {
            final Scope[] s = new Scope[Math.max(this.scopeTop + 1, this.scopes.length * 2)];
            System.arraycopy(this.scopes, 0, s, 0, this.scopes.length);
            for (int i = this.scopes.length; i < s.length; ++i) {
                s[i] = new Scope(this);
            }
            this.scopes = s;
        }
    }
    
    public void endScope(int frameSize) throws SAXException {
        try {
            while (frameSize > 0) {
                this.scopes[this.scopeTop].finish();
                --frameSize;
                --this.scopeTop;
            }
        }
        catch (AccessorException e) {
            this.handleError(e);
            while (frameSize > 0) {
                this.scopes[this.scopeTop--] = new Scope(this);
                --frameSize;
            }
        }
    }
    
    public Scope getScope(final int offset) {
        return this.scopes[this.scopeTop - offset];
    }
    
    public void recordInnerPeer(final Object innerPeer) {
        if (this.assoc != null) {
            this.assoc.addInner(this.currentElement, innerPeer);
        }
    }
    
    public Object getInnerPeer() {
        if (this.assoc != null && this.isInplaceMode) {
            return this.assoc.getInnerPeer(this.currentElement);
        }
        return null;
    }
    
    public void recordOuterPeer(final Object outerPeer) {
        if (this.assoc != null) {
            this.assoc.addOuter(this.currentElement, outerPeer);
        }
    }
    
    public Object getOuterPeer() {
        if (this.assoc != null && this.isInplaceMode) {
            return this.assoc.getOuterPeer(this.currentElement);
        }
        return null;
    }
    
    public String getXMIMEContentType() {
        final Object t = this.current.target;
        if (t == null) {
            return null;
        }
        return this.getJAXBContext().getXMIMEContentType(t);
    }
    
    public static UnmarshallingContext getInstance() {
        return (UnmarshallingContext)Coordinator._getInstance();
    }
    
    static {
        final LocatorImpl loc = new LocatorImpl();
        loc.setPublicId(null);
        loc.setSystemId(null);
        loc.setLineNumber(-1);
        loc.setColumnNumber(-1);
        DUMMY_INSTANCE = new LocatorExWrapper(loc);
        DEFAULT_ROOT_LOADER = new DefaultRootLoader();
        EXPECTED_TYPE_ROOT_LOADER = new ExpectedTypeRootLoader();
    }
    
    public final class State
    {
        public Loader loader;
        public Receiver receiver;
        public Intercepter intercepter;
        public Object target;
        public Object backup;
        private int numNsDecl;
        public String elementDefaultValue;
        public final State prev;
        private State next;
        
        public UnmarshallingContext getContext() {
            return UnmarshallingContext.this;
        }
        
        private State(final State prev) {
            this.prev = prev;
            if (prev != null) {
                prev.next = this;
            }
        }
        
        private void push() {
            if (this.next == null) {
                UnmarshallingContext.this.allocateMoreStates();
            }
            final State n = this.next;
            n.numNsDecl = UnmarshallingContext.this.nsLen;
            UnmarshallingContext.this.current = n;
        }
        
        private void pop() {
            assert this.prev != null;
            this.loader = null;
            this.receiver = null;
            this.intercepter = null;
            this.elementDefaultValue = null;
            this.target = null;
            UnmarshallingContext.this.current = this.prev;
        }
    }
    
    private static class Factory
    {
        private final Object factorInstance;
        private final Method method;
        
        public Factory(final Object factorInstance, final Method method) {
            this.factorInstance = factorInstance;
            this.method = method;
        }
        
        public Object createInstance() throws SAXException {
            try {
                return this.method.invoke(this.factorInstance, new Object[0]);
            }
            catch (IllegalAccessException e) {
                UnmarshallingContext.getInstance().handleError(e, false);
            }
            catch (InvocationTargetException e2) {
                UnmarshallingContext.getInstance().handleError(e2, false);
            }
            return null;
        }
    }
    
    private static final class DefaultRootLoader extends Loader implements Receiver
    {
        public void childElement(final State state, final TagName ea) throws SAXException {
            final Loader loader = state.getContext().selectRootLoader(state, ea);
            if (loader != null) {
                state.loader = loader;
                state.receiver = this;
                return;
            }
            final JaxBeanInfo beanInfo = XsiTypeLoader.parseXsiType(state, ea, null);
            if (beanInfo == null) {
                this.reportUnexpectedChildElement(ea, false);
                return;
            }
            state.loader = beanInfo.getLoader(null, false);
            state.prev.backup = new JAXBElement(ea.createQName(), Object.class, null);
            state.receiver = this;
        }
        
        public Collection<QName> getExpectedChildElements() {
            return UnmarshallingContext.getInstance().getJAXBContext().getValidRootNames();
        }
        
        public void receive(final State state, Object o) {
            if (state.backup != null) {
                ((JAXBElement)state.backup).setValue(o);
                o = state.backup;
            }
            state.getContext().result = o;
        }
    }
    
    private static final class ExpectedTypeRootLoader extends Loader implements Receiver
    {
        public void childElement(final State state, final TagName ea) {
            final UnmarshallingContext context = state.getContext();
            final QName qn = new QName(ea.uri, ea.local);
            state.prev.target = new JAXBElement(qn, (Class<Object>)context.expectedType.jaxbType, null, null);
            state.receiver = this;
            state.loader = new XsiNilLoader(context.expectedType.getLoader(null, true));
        }
        
        public void receive(final State state, final Object o) {
            final JAXBElement e = (JAXBElement)state.target;
            e.setValue(o);
            state.getContext().recordOuterPeer(e);
            state.getContext().result = e;
        }
    }
}
