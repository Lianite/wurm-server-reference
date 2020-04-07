// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.binding.xml;

import org.fourthline.cling.model.types.CustomDatatype;
import org.fourthline.cling.model.types.Datatype;
import org.fourthline.cling.binding.staging.MutableAllowedValueRange;
import org.fourthline.cling.model.meta.StateVariableEventDetails;
import org.fourthline.cling.model.meta.ActionArgument;
import java.util.Locale;
import org.fourthline.cling.binding.staging.MutableActionArgument;
import org.xml.sax.SAXException;
import java.util.List;
import org.fourthline.cling.binding.staging.MutableStateVariable;
import org.fourthline.cling.binding.staging.MutableAction;
import java.util.ArrayList;
import org.xml.sax.Attributes;
import org.fourthline.cling.model.ValidationException;
import org.fourthline.cling.model.meta.Device;
import java.io.Reader;
import org.xml.sax.InputSource;
import java.io.StringReader;
import org.fourthline.cling.binding.staging.MutableService;
import org.seamless.xml.SAXParser;
import org.fourthline.cling.model.meta.Service;
import java.util.logging.Logger;

public class UDA10ServiceDescriptorBinderSAXImpl extends UDA10ServiceDescriptorBinderImpl
{
    private static Logger log;
    
    @Override
    public <S extends Service> S describe(final S undescribedService, final String descriptorXml) throws DescriptorBindingException, ValidationException {
        if (descriptorXml == null || descriptorXml.length() == 0) {
            throw new DescriptorBindingException("Null or empty descriptor");
        }
        try {
            UDA10ServiceDescriptorBinderSAXImpl.log.fine("Reading service from XML descriptor");
            final SAXParser parser = new SAXParser();
            final MutableService descriptor = new MutableService();
            this.hydrateBasic(descriptor, undescribedService);
            new RootHandler(descriptor, parser);
            parser.parse(new InputSource(new StringReader(descriptorXml.trim())));
            return (S)descriptor.build(undescribedService.getDevice());
        }
        catch (ValidationException ex) {
            throw ex;
        }
        catch (Exception ex2) {
            throw new DescriptorBindingException("Could not parse service descriptor: " + ex2.toString(), ex2);
        }
    }
    
    static {
        UDA10ServiceDescriptorBinderSAXImpl.log = Logger.getLogger(ServiceDescriptorBinder.class.getName());
    }
    
    protected static class RootHandler extends ServiceDescriptorHandler<MutableService>
    {
        public RootHandler(final MutableService instance, final SAXParser parser) {
            super(instance, parser);
        }
        
        @Override
        public void startElement(final Descriptor.Service.ELEMENT element, final Attributes attributes) throws SAXException {
            if (element.equals(ActionListHandler.EL)) {
                final List<MutableAction> actions = new ArrayList<MutableAction>();
                this.getInstance().actions = actions;
                new ActionListHandler(actions, this);
            }
            if (element.equals(StateVariableListHandler.EL)) {
                final List<MutableStateVariable> stateVariables = new ArrayList<MutableStateVariable>();
                this.getInstance().stateVariables = stateVariables;
                new StateVariableListHandler(stateVariables, this);
            }
        }
    }
    
    protected static class ActionListHandler extends ServiceDescriptorHandler<List<MutableAction>>
    {
        public static final Descriptor.Service.ELEMENT EL;
        
        public ActionListHandler(final List<MutableAction> instance, final ServiceDescriptorHandler parent) {
            super(instance, parent);
        }
        
        @Override
        public void startElement(final Descriptor.Service.ELEMENT element, final Attributes attributes) throws SAXException {
            if (element.equals(ActionHandler.EL)) {
                final MutableAction action = new MutableAction();
                this.getInstance().add(action);
                new ActionHandler(action, this);
            }
        }
        
        @Override
        public boolean isLastElement(final Descriptor.Service.ELEMENT element) {
            return element.equals(ActionListHandler.EL);
        }
        
        static {
            EL = Descriptor.Service.ELEMENT.actionList;
        }
    }
    
    protected static class ActionHandler extends ServiceDescriptorHandler<MutableAction>
    {
        public static final Descriptor.Service.ELEMENT EL;
        
        public ActionHandler(final MutableAction instance, final ServiceDescriptorHandler parent) {
            super(instance, parent);
        }
        
        @Override
        public void startElement(final Descriptor.Service.ELEMENT element, final Attributes attributes) throws SAXException {
            if (element.equals(ActionArgumentListHandler.EL)) {
                final List<MutableActionArgument> arguments = new ArrayList<MutableActionArgument>();
                this.getInstance().arguments = arguments;
                new ActionArgumentListHandler(arguments, this);
            }
        }
        
        @Override
        public void endElement(final Descriptor.Service.ELEMENT element) throws SAXException {
            switch (element) {
                case name: {
                    this.getInstance().name = this.getCharacters();
                    break;
                }
            }
        }
        
        @Override
        public boolean isLastElement(final Descriptor.Service.ELEMENT element) {
            return element.equals(ActionHandler.EL);
        }
        
        static {
            EL = Descriptor.Service.ELEMENT.action;
        }
    }
    
    protected static class ActionArgumentListHandler extends ServiceDescriptorHandler<List<MutableActionArgument>>
    {
        public static final Descriptor.Service.ELEMENT EL;
        
        public ActionArgumentListHandler(final List<MutableActionArgument> instance, final ServiceDescriptorHandler parent) {
            super(instance, parent);
        }
        
        @Override
        public void startElement(final Descriptor.Service.ELEMENT element, final Attributes attributes) throws SAXException {
            if (element.equals(ActionArgumentHandler.EL)) {
                final MutableActionArgument argument = new MutableActionArgument();
                this.getInstance().add(argument);
                new ActionArgumentHandler(argument, this);
            }
        }
        
        @Override
        public boolean isLastElement(final Descriptor.Service.ELEMENT element) {
            return element.equals(ActionArgumentListHandler.EL);
        }
        
        static {
            EL = Descriptor.Service.ELEMENT.argumentList;
        }
    }
    
    protected static class ActionArgumentHandler extends ServiceDescriptorHandler<MutableActionArgument>
    {
        public static final Descriptor.Service.ELEMENT EL;
        
        public ActionArgumentHandler(final MutableActionArgument instance, final ServiceDescriptorHandler parent) {
            super(instance, parent);
        }
        
        @Override
        public void endElement(final Descriptor.Service.ELEMENT element) throws SAXException {
            switch (element) {
                case name: {
                    this.getInstance().name = this.getCharacters();
                    break;
                }
                case direction: {
                    final String directionString = this.getCharacters();
                    try {
                        this.getInstance().direction = ActionArgument.Direction.valueOf(directionString.toUpperCase(Locale.ROOT));
                    }
                    catch (IllegalArgumentException ex) {
                        UDA10ServiceDescriptorBinderSAXImpl.log.warning("UPnP specification violation: Invalid action argument direction, assuming 'IN': " + directionString);
                        this.getInstance().direction = ActionArgument.Direction.IN;
                    }
                    break;
                }
                case relatedStateVariable: {
                    this.getInstance().relatedStateVariable = this.getCharacters();
                    break;
                }
                case retval: {
                    this.getInstance().retval = true;
                    break;
                }
            }
        }
        
        @Override
        public boolean isLastElement(final Descriptor.Service.ELEMENT element) {
            return element.equals(ActionArgumentHandler.EL);
        }
        
        static {
            EL = Descriptor.Service.ELEMENT.argument;
        }
    }
    
    protected static class StateVariableListHandler extends ServiceDescriptorHandler<List<MutableStateVariable>>
    {
        public static final Descriptor.Service.ELEMENT EL;
        
        public StateVariableListHandler(final List<MutableStateVariable> instance, final ServiceDescriptorHandler parent) {
            super(instance, parent);
        }
        
        @Override
        public void startElement(final Descriptor.Service.ELEMENT element, final Attributes attributes) throws SAXException {
            if (element.equals(StateVariableHandler.EL)) {
                final MutableStateVariable stateVariable = new MutableStateVariable();
                final String sendEventsAttributeValue = attributes.getValue(Descriptor.Service.ATTRIBUTE.sendEvents.toString());
                stateVariable.eventDetails = new StateVariableEventDetails(sendEventsAttributeValue != null && sendEventsAttributeValue.toUpperCase(Locale.ROOT).equals("YES"));
                this.getInstance().add(stateVariable);
                new StateVariableHandler(stateVariable, this);
            }
        }
        
        @Override
        public boolean isLastElement(final Descriptor.Service.ELEMENT element) {
            return element.equals(StateVariableListHandler.EL);
        }
        
        static {
            EL = Descriptor.Service.ELEMENT.serviceStateTable;
        }
    }
    
    protected static class StateVariableHandler extends ServiceDescriptorHandler<MutableStateVariable>
    {
        public static final Descriptor.Service.ELEMENT EL;
        
        public StateVariableHandler(final MutableStateVariable instance, final ServiceDescriptorHandler parent) {
            super(instance, parent);
        }
        
        @Override
        public void startElement(final Descriptor.Service.ELEMENT element, final Attributes attributes) throws SAXException {
            if (element.equals(AllowedValueListHandler.EL)) {
                final List<String> allowedValues = new ArrayList<String>();
                this.getInstance().allowedValues = allowedValues;
                new AllowedValueListHandler(allowedValues, this);
            }
            if (element.equals(AllowedValueRangeHandler.EL)) {
                final MutableAllowedValueRange allowedValueRange = new MutableAllowedValueRange();
                this.getInstance().allowedValueRange = allowedValueRange;
                new AllowedValueRangeHandler(allowedValueRange, this);
            }
        }
        
        @Override
        public void endElement(final Descriptor.Service.ELEMENT element) throws SAXException {
            switch (element) {
                case name: {
                    this.getInstance().name = this.getCharacters();
                    break;
                }
                case dataType: {
                    final String dtName = this.getCharacters();
                    final Datatype.Builtin builtin = Datatype.Builtin.getByDescriptorName(dtName);
                    this.getInstance().dataType = ((builtin != null) ? builtin.getDatatype() : new CustomDatatype(dtName));
                    break;
                }
                case defaultValue: {
                    this.getInstance().defaultValue = this.getCharacters();
                    break;
                }
            }
        }
        
        @Override
        public boolean isLastElement(final Descriptor.Service.ELEMENT element) {
            return element.equals(StateVariableHandler.EL);
        }
        
        static {
            EL = Descriptor.Service.ELEMENT.stateVariable;
        }
    }
    
    protected static class AllowedValueListHandler extends ServiceDescriptorHandler<List<String>>
    {
        public static final Descriptor.Service.ELEMENT EL;
        
        public AllowedValueListHandler(final List<String> instance, final ServiceDescriptorHandler parent) {
            super(instance, parent);
        }
        
        @Override
        public void endElement(final Descriptor.Service.ELEMENT element) throws SAXException {
            switch (element) {
                case allowedValue: {
                    this.getInstance().add(this.getCharacters());
                    break;
                }
            }
        }
        
        @Override
        public boolean isLastElement(final Descriptor.Service.ELEMENT element) {
            return element.equals(AllowedValueListHandler.EL);
        }
        
        static {
            EL = Descriptor.Service.ELEMENT.allowedValueList;
        }
    }
    
    protected static class AllowedValueRangeHandler extends ServiceDescriptorHandler<MutableAllowedValueRange>
    {
        public static final Descriptor.Service.ELEMENT EL;
        
        public AllowedValueRangeHandler(final MutableAllowedValueRange instance, final ServiceDescriptorHandler parent) {
            super(instance, parent);
        }
        
        @Override
        public void endElement(final Descriptor.Service.ELEMENT element) throws SAXException {
            try {
                switch (element) {
                    case minimum: {
                        this.getInstance().minimum = Long.valueOf(this.getCharacters());
                        break;
                    }
                    case maximum: {
                        this.getInstance().maximum = Long.valueOf(this.getCharacters());
                        break;
                    }
                    case step: {
                        this.getInstance().step = Long.valueOf(this.getCharacters());
                        break;
                    }
                }
            }
            catch (Exception ex) {}
        }
        
        @Override
        public boolean isLastElement(final Descriptor.Service.ELEMENT element) {
            return element.equals(AllowedValueRangeHandler.EL);
        }
        
        static {
            EL = Descriptor.Service.ELEMENT.allowedValueRange;
        }
    }
    
    protected static class ServiceDescriptorHandler<I> extends SAXParser.Handler<I>
    {
        public ServiceDescriptorHandler(final I instance) {
            super(instance);
        }
        
        public ServiceDescriptorHandler(final I instance, final SAXParser parser) {
            super(instance, parser);
        }
        
        public ServiceDescriptorHandler(final I instance, final ServiceDescriptorHandler parent) {
            super(instance, parent);
        }
        
        public ServiceDescriptorHandler(final I instance, final SAXParser parser, final ServiceDescriptorHandler parent) {
            super(instance, parser, parent);
        }
        
        @Override
        public void startElement(final String uri, final String localName, final String qName, final Attributes attributes) throws SAXException {
            super.startElement(uri, localName, qName, attributes);
            final Descriptor.Service.ELEMENT el = Descriptor.Service.ELEMENT.valueOrNullOf(localName);
            if (el == null) {
                return;
            }
            this.startElement(el, attributes);
        }
        
        @Override
        public void endElement(final String uri, final String localName, final String qName) throws SAXException {
            super.endElement(uri, localName, qName);
            final Descriptor.Service.ELEMENT el = Descriptor.Service.ELEMENT.valueOrNullOf(localName);
            if (el == null) {
                return;
            }
            this.endElement(el);
        }
        
        @Override
        protected boolean isLastElement(final String uri, final String localName, final String qName) {
            final Descriptor.Service.ELEMENT el = Descriptor.Service.ELEMENT.valueOrNullOf(localName);
            return el != null && this.isLastElement(el);
        }
        
        public void startElement(final Descriptor.Service.ELEMENT element, final Attributes attributes) throws SAXException {
        }
        
        public void endElement(final Descriptor.Service.ELEMENT element) throws SAXException {
        }
        
        public boolean isLastElement(final Descriptor.Service.ELEMENT element) {
            return false;
        }
    }
}
