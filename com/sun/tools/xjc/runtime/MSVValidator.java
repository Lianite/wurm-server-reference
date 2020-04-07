// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.runtime;

import com.sun.msv.verifier.regexp.StringToken;
import org.relaxng.datatype.Datatype;
import com.sun.xml.bind.marshaller.IdentifiableObject;
import com.sun.xml.bind.serializer.AbortSerializationException;
import javax.xml.bind.ValidationEvent;
import com.sun.xml.bind.JAXBAssertionError;
import com.sun.xml.bind.validator.Messages;
import com.sun.xml.bind.RIElement;
import com.sun.xml.bind.JAXBObject;
import com.sun.msv.util.DatatypeRef;
import com.sun.xml.bind.serializer.Util;
import org.xml.sax.Attributes;
import com.sun.msv.util.StartTagInfo;
import com.sun.msv.util.StringRef;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;
import com.sun.msv.util.LightStack;
import com.sun.msv.verifier.Acceptor;
import com.sun.msv.grammar.IDContextProvider2;

public class MSVValidator implements XMLSerializer, IDContextProvider2
{
    private Acceptor acceptor;
    private final ValidationContext context;
    private final ValidatableObject target;
    final DefaultJAXBContextImpl jaxbContext;
    private final LightStack stack;
    private StringBuffer buf;
    private String attNamespaceUri;
    private String attLocalName;
    private boolean insideAttribute;
    private String currentElementUri;
    private String currentElementLocalName;
    private static final AttributesImpl emptyAttributes;
    public static final String DUMMY_ELEMENT_NS = "http://java.sun.com/jaxb/xjc/dummy-elements";
    
    public NamespaceContext2 getNamespaceContext() {
        return (NamespaceContext2)this.context.getNamespaceContext();
    }
    
    private MSVValidator(final DefaultJAXBContextImpl _jaxbCtx, final ValidationContext _ctxt, final ValidatableObject vo) {
        this.stack = new LightStack();
        this.buf = new StringBuffer();
        this.jaxbContext = _jaxbCtx;
        this.acceptor = vo.createRawValidator().createAcceptor();
        this.context = _ctxt;
        this.target = vo;
    }
    
    public static void validate(final DefaultJAXBContextImpl jaxbCtx, final ValidationContext context, final ValidatableObject vo) throws SAXException {
        try {
            new MSVValidator(jaxbCtx, context, vo)._validate();
        }
        catch (RuntimeException e) {
            context.reportEvent(vo, (Exception)e);
        }
    }
    
    private void _validate() throws SAXException {
        this.context.getNamespaceContext().startElement();
        this.target.serializeURIs((XMLSerializer)this);
        this.endNamespaceDecls();
        this.target.serializeAttributes((XMLSerializer)this);
        this.endAttributes();
        this.target.serializeBody((XMLSerializer)this);
        this.writePendingText();
        this.context.getNamespaceContext().endElement();
        if (!this.acceptor.isAcceptState((StringRef)null)) {
            final StringRef ref = new StringRef();
            this.acceptor.isAcceptState(ref);
            this.context.reportEvent(this.target, ref.str);
        }
    }
    
    public void endNamespaceDecls() throws SAXException {
        this.context.getNamespaceContext().endNamespaceDecls();
    }
    
    public void endAttributes() throws SAXException {
        if (!this.acceptor.onEndAttributes((StartTagInfo)null, (StringRef)null)) {
            final StringRef ref = new StringRef();
            final StartTagInfo sti = new StartTagInfo(this.currentElementUri, this.currentElementLocalName, this.currentElementLocalName, (Attributes)MSVValidator.emptyAttributes, (IDContextProvider2)this);
            this.acceptor.onEndAttributes(sti, ref);
            this.context.reportEvent(this.target, ref.str);
        }
    }
    
    public final void text(final String text, final String fieldName) throws SAXException {
        if (text == null) {
            this.reportMissingObjectError(fieldName);
            return;
        }
        if (this.buf.length() != 0) {
            this.buf.append(' ');
        }
        this.buf.append(text);
    }
    
    public void reportMissingObjectError(final String fieldName) throws SAXException {
        this.reportError(Util.createMissingObjectError((Object)this.target, fieldName));
    }
    
    public void startAttribute(final String uri, final String local) {
        this.attNamespaceUri = uri;
        this.attLocalName = local;
        this.insideAttribute = true;
    }
    
    public void endAttribute() throws SAXException {
        this.insideAttribute = false;
        if (!this.acceptor.onAttribute2(this.attNamespaceUri, this.attLocalName, this.attLocalName, this.buf.toString(), (IDContextProvider2)this, (StringRef)null, (DatatypeRef)null)) {
            final StringRef ref = new StringRef();
            this.acceptor.onAttribute2(this.attNamespaceUri, this.attLocalName, this.attLocalName, this.buf.toString(), (IDContextProvider2)this, ref, (DatatypeRef)null);
            this.context.reportEvent(this.target, ref.str);
        }
        this.buf = new StringBuffer();
    }
    
    private void writePendingText() throws SAXException {
        if (!this.acceptor.onText2(this.buf.toString(), (IDContextProvider2)this, (StringRef)null, (DatatypeRef)null)) {
            final StringRef ref = new StringRef();
            this.acceptor.onText2(this.buf.toString(), (IDContextProvider2)this, ref, (DatatypeRef)null);
            this.context.reportEvent(this.target, ref.str);
        }
        if (this.buf.length() > 1024) {
            this.buf = new StringBuffer();
        }
        else {
            this.buf.setLength(0);
        }
    }
    
    public void startElement(final String uri, final String local) throws SAXException {
        this.writePendingText();
        this.context.getNamespaceContext().startElement();
        this.stack.push((Object)this.acceptor);
        final StartTagInfo sti = new StartTagInfo(uri, local, local, (Attributes)MSVValidator.emptyAttributes, (IDContextProvider2)this);
        Acceptor child = this.acceptor.createChildAcceptor(sti, (StringRef)null);
        if (child == null) {
            final StringRef ref = new StringRef();
            child = this.acceptor.createChildAcceptor(sti, ref);
            this.context.reportEvent(this.target, ref.str);
        }
        this.currentElementUri = uri;
        this.currentElementLocalName = local;
        this.acceptor = child;
    }
    
    public void endElement() throws SAXException {
        this.writePendingText();
        if (!this.acceptor.isAcceptState((StringRef)null)) {
            final StringRef ref = new StringRef();
            this.acceptor.isAcceptState(ref);
            this.context.reportEvent(this.target, ref.str);
        }
        final Acceptor child = this.acceptor;
        this.acceptor = (Acceptor)this.stack.pop();
        if (!this.acceptor.stepForward(child, (StringRef)null)) {
            final StringRef ref2 = new StringRef();
            this.acceptor.stepForward(child, ref2);
            this.context.reportEvent(this.target, ref2.str);
        }
        this.context.getNamespaceContext().endElement();
    }
    
    public void childAsAttributes(final JAXBObject o, final String fieldName) throws SAXException {
    }
    
    public void childAsURIs(final JAXBObject o, final String fieldName) throws SAXException {
    }
    
    public void childAsBody(final JAXBObject o, final String fieldName) throws SAXException {
        final ValidatableObject vo = this.jaxbContext.getGrammarInfo().castToValidatableObject((Object)o);
        if (vo == null) {
            this.reportMissingObjectError(fieldName);
            return;
        }
        if (this.insideAttribute) {
            this.childAsAttributeBody(vo, fieldName);
        }
        else {
            this.childAsElementBody(o, vo);
        }
    }
    
    private void childAsElementBody(final Object o, final ValidatableObject vo) throws SAXException {
        String intfName = vo.getPrimaryInterface().getName();
        intfName = intfName.replace('$', '.');
        final StartTagInfo sti = new StartTagInfo("http://java.sun.com/jaxb/xjc/dummy-elements", intfName, intfName, (Attributes)MSVValidator.emptyAttributes, (IDContextProvider2)this);
        Acceptor child = this.acceptor.createChildAcceptor(sti, (StringRef)null);
        if (child == null) {
            final StringRef ref = new StringRef();
            child = this.acceptor.createChildAcceptor(sti, ref);
            this.context.reportEvent(this.target, ref.str);
        }
        if (o instanceof RIElement) {
            final RIElement rie = (RIElement)o;
            if (!child.onAttribute2(rie.____jaxb_ri____getNamespaceURI(), rie.____jaxb_ri____getLocalName(), rie.____jaxb_ri____getLocalName(), "", (IDContextProvider2)null, (StringRef)null, (DatatypeRef)null)) {
                this.context.reportEvent(this.target, Messages.format("MSVValidator.IncorrectChildForWildcard", (Object)rie.____jaxb_ri____getNamespaceURI(), (Object)rie.____jaxb_ri____getLocalName()));
            }
        }
        child.onEndAttributes(sti, (StringRef)null);
        if (!this.acceptor.stepForward(child, (StringRef)null)) {
            throw new JAXBAssertionError();
        }
        this.context.validate(vo);
    }
    
    private void childAsAttributeBody(final ValidatableObject vo, final String fieldName) throws SAXException {
        this.text("\u0000" + vo.getPrimaryInterface().getName(), fieldName);
        this.context.validate(vo);
    }
    
    public void reportError(final ValidationEvent e) throws AbortSerializationException {
        this.context.reportEvent(this.target, e);
    }
    
    public String onID(final IdentifiableObject owner, final String value) throws SAXException {
        return this.context.onID((XMLSerializable)this.target, value);
    }
    
    public String onIDREF(final IdentifiableObject value) throws SAXException {
        return this.context.onIDREF((XMLSerializable)this.target, value.____jaxb____getId());
    }
    
    public String getBaseUri() {
        return null;
    }
    
    public boolean isUnparsedEntity(final String entityName) {
        return true;
    }
    
    public boolean isNotation(final String notation) {
        return true;
    }
    
    public void onID(final Datatype dt, final StringToken s) {
    }
    
    public String resolveNamespacePrefix(final String prefix) {
        return this.context.getNamespaceContext().getNamespaceURI(prefix);
    }
    
    static {
        emptyAttributes = new AttributesImpl();
    }
}
