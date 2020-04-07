// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.binding.xml;

import java.util.regex.Matcher;
import java.util.Locale;
import java.util.regex.Pattern;
import org.seamless.xml.ParserException;
import org.xml.sax.SAXParseException;
import org.fourthline.cling.model.ValidationException;
import org.seamless.xml.XmlPullParserUtils;
import org.seamless.util.Exceptions;
import org.fourthline.cling.model.meta.Device;
import java.util.logging.Logger;

public class RecoveringUDA10DeviceDescriptorBinderImpl extends UDA10DeviceDescriptorBinderImpl
{
    private static Logger log;
    
    @Override
    public <D extends Device> D describe(final D undescribedDevice, String descriptorXml) throws DescriptorBindingException, ValidationException {
        D device = null;
        try {
            try {
                if (descriptorXml != null) {
                    descriptorXml = descriptorXml.trim();
                }
                device = super.describe(undescribedDevice, descriptorXml);
                return device;
            }
            catch (DescriptorBindingException ex) {
                RecoveringUDA10DeviceDescriptorBinderImpl.log.warning("Regular parsing failed: " + Exceptions.unwrap(ex).getMessage());
                final DescriptorBindingException originalException = ex;
                String fixedXml = this.fixGarbageLeadingChars(descriptorXml);
                if (fixedXml != null) {
                    try {
                        device = super.describe(undescribedDevice, fixedXml);
                        return device;
                    }
                    catch (DescriptorBindingException ex2) {
                        RecoveringUDA10DeviceDescriptorBinderImpl.log.warning("Removing leading garbage didn't work: " + Exceptions.unwrap(ex2).getMessage());
                    }
                }
                fixedXml = this.fixGarbageTrailingChars(descriptorXml, originalException);
                if (fixedXml != null) {
                    try {
                        device = super.describe(undescribedDevice, fixedXml);
                        return device;
                    }
                    catch (DescriptorBindingException ex2) {
                        RecoveringUDA10DeviceDescriptorBinderImpl.log.warning("Removing trailing garbage didn't work: " + Exceptions.unwrap(ex2).getMessage());
                    }
                }
                DescriptorBindingException lastException = originalException;
                fixedXml = descriptorXml;
                int retryCount = 0;
                while (retryCount < 5) {
                    fixedXml = this.fixMissingNamespaces(fixedXml, lastException);
                    if (fixedXml != null) {
                        try {
                            device = super.describe(undescribedDevice, fixedXml);
                            return device;
                        }
                        catch (DescriptorBindingException ex3) {
                            RecoveringUDA10DeviceDescriptorBinderImpl.log.warning("Fixing namespace prefix didn't work: " + Exceptions.unwrap(ex3).getMessage());
                            lastException = ex3;
                            ++retryCount;
                            continue;
                        }
                        break;
                    }
                    break;
                }
                fixedXml = XmlPullParserUtils.fixXMLEntities(descriptorXml);
                if (!fixedXml.equals(descriptorXml)) {
                    try {
                        device = super.describe(undescribedDevice, fixedXml);
                        return device;
                    }
                    catch (DescriptorBindingException ex4) {
                        RecoveringUDA10DeviceDescriptorBinderImpl.log.warning("Fixing XML entities didn't work: " + Exceptions.unwrap(ex4).getMessage());
                    }
                }
                this.handleInvalidDescriptor(descriptorXml, originalException);
            }
        }
        catch (ValidationException ex5) {
            device = this.handleInvalidDevice(descriptorXml, device, ex5);
            if (device != null) {
                return device;
            }
        }
        throw new IllegalStateException("No device produced, did you swallow exceptions in your subclass?");
    }
    
    private String fixGarbageLeadingChars(final String descriptorXml) {
        final int index = descriptorXml.indexOf("<?xml");
        if (index == -1) {
            return descriptorXml;
        }
        return descriptorXml.substring(index);
    }
    
    protected String fixGarbageTrailingChars(final String descriptorXml, final DescriptorBindingException ex) {
        final int index = descriptorXml.indexOf("</root>");
        if (index == -1) {
            RecoveringUDA10DeviceDescriptorBinderImpl.log.warning("No closing </root> element in descriptor");
            return null;
        }
        if (descriptorXml.length() != index + "</root>".length()) {
            RecoveringUDA10DeviceDescriptorBinderImpl.log.warning("Detected garbage characters after <root> node, removing");
            return descriptorXml.substring(0, index) + "</root>";
        }
        return null;
    }
    
    protected String fixMissingNamespaces(final String descriptorXml, final DescriptorBindingException ex) {
        final Throwable cause = ex.getCause();
        if (!(cause instanceof SAXParseException) && !(cause instanceof ParserException)) {
            return null;
        }
        final String message = cause.getMessage();
        if (message == null) {
            return null;
        }
        Pattern pattern = Pattern.compile("The prefix \"(.*)\" for element");
        Matcher matcher = pattern.matcher(message);
        if (!matcher.find() || matcher.groupCount() != 1) {
            pattern = Pattern.compile("undefined prefix: ([^ ]*)");
            matcher = pattern.matcher(message);
            if (!matcher.find() || matcher.groupCount() != 1) {
                return null;
            }
        }
        final String missingNS = matcher.group(1);
        RecoveringUDA10DeviceDescriptorBinderImpl.log.warning("Fixing missing namespace declaration for: " + missingNS);
        pattern = Pattern.compile("<root([^>]*)");
        matcher = pattern.matcher(descriptorXml);
        if (!matcher.find() || matcher.groupCount() != 1) {
            RecoveringUDA10DeviceDescriptorBinderImpl.log.fine("Could not find <root> element attributes");
            return null;
        }
        final String rootAttributes = matcher.group(1);
        RecoveringUDA10DeviceDescriptorBinderImpl.log.fine("Preserving existing <root> element attributes/namespace declarations: " + matcher.group(0));
        pattern = Pattern.compile("<root[^>]*>(.*)</root>", 32);
        matcher = pattern.matcher(descriptorXml);
        if (!matcher.find() || matcher.groupCount() != 1) {
            RecoveringUDA10DeviceDescriptorBinderImpl.log.fine("Could not extract body of <root> element");
            return null;
        }
        final String rootBody = matcher.group(1);
        return "<?xml version=\"1.0\" encoding=\"UTF-8\" ?><root " + String.format(Locale.ROOT, "xmlns:%s=\"urn:schemas-dlna-org:device-1-0\"", missingNS) + rootAttributes + ">" + rootBody + "</root>";
    }
    
    protected void handleInvalidDescriptor(final String xml, final DescriptorBindingException exception) throws DescriptorBindingException {
        throw exception;
    }
    
    protected <D extends Device> D handleInvalidDevice(final String xml, final D device, final ValidationException exception) throws ValidationException {
        throw exception;
    }
    
    static {
        RecoveringUDA10DeviceDescriptorBinderImpl.log = Logger.getLogger(RecoveringUDA10DeviceDescriptorBinderImpl.class.getName());
    }
}
