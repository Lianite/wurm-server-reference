// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.reader.xmlschema.parser;

import com.sun.xml.bind.JAXBAssertionError;
import org.xml.sax.SAXParseException;
import org.xml.sax.SAXException;
import org.xml.sax.Attributes;
import org.xml.sax.ErrorHandler;
import org.xml.sax.Locator;
import org.xml.sax.helpers.XMLFilterImpl;

public class ProhibitedFeaturesFilter extends XMLFilterImpl
{
    private Locator locator;
    private ErrorHandler errorHandler;
    private boolean strict;
    private static final int REPORT_DISABLED_IN_STRICT_MODE = 1;
    private static final int REPORT_RESTRICTED = 2;
    private static final int REPORT_WARN = 3;
    private static final int REPORT_UNSUPPORTED_ERROR = 4;
    
    public ProhibitedFeaturesFilter(final ErrorHandler eh, final boolean strict) {
        this.locator = null;
        this.errorHandler = null;
        this.strict = true;
        this.errorHandler = eh;
        this.strict = strict;
    }
    
    public void startElement(final String uri, final String localName, final String qName, final Attributes atts) throws SAXException {
        if (this.strict && localName.equals("any") && "skip".equals(atts.getValue("processContents"))) {
            this.report(3, "ProhibitedFeaturesFilter.ProcessContentsAttrOfAny", this.locator);
        }
        else if (localName.equals("anyAttribute")) {
            this.report(2, this.strict ? "ProhibitedFeaturesFilter.AnyAttr" : "ProhibitedFeaturesFilter.AnyAttrWarning", this.locator);
        }
        else if (localName.equals("complexType")) {
            if (atts.getValue("block") != null && !parseComplexTypeBlockAttr(atts.getValue("block"))) {
                this.report(3, "ProhibitedFeaturesFilter.BlockAttrOfComplexType", this.locator);
            }
            if (atts.getValue("final") != null) {
                this.report(3, "ProhibitedFeaturesFilter.FinalAttrOfComplexType", this.locator);
            }
        }
        else if (localName.equals("element")) {
            if (atts.getValue("abstract") != null && parsedBooleanTrue(atts.getValue("abstract"))) {
                this.report(1, "ProhibitedFeaturesFilter.AbstractAttrOfElement", this.locator);
            }
            if (atts.getValue("substitutionGroup") != null && !atts.getValue("substitutionGroup").trim().equals("")) {
                this.report(1, "ProhibitedFeaturesFilter.SubstitutionGroupAttrOfElement", this.locator);
            }
            if (atts.getValue("final") != null) {
                this.report(3, "ProhibitedFeaturesFilter.FinalAttrOfElement", this.locator);
            }
            if (atts.getValue("block") != null && !parseElementBlockAttr(atts.getValue("block"))) {
                this.report(3, "ProhibitedFeaturesFilter.BlockAttrOfElement", this.locator);
            }
        }
        else if (localName.equals("key")) {
            this.report(2, this.strict ? "ProhibitedFeaturesFilter.Key" : "ProhibitedFeaturesFilter.KeyWarning", this.locator);
        }
        else if (localName.equals("keyref")) {
            this.report(2, this.strict ? "ProhibitedFeaturesFilter.Keyref" : "ProhibitedFeaturesFilter.KeyrefWarning", this.locator);
        }
        else if (localName.equals("notation")) {
            this.report(2, this.strict ? "ProhibitedFeaturesFilter.Notation" : "ProhibitedFeaturesFilter.NotationWarning", this.locator);
        }
        else if (localName.equals("unique")) {
            this.report(2, this.strict ? "ProhibitedFeaturesFilter.Unique" : "ProhibitedFeaturesFilter.UniqueWarning", this.locator);
        }
        else if (localName.equals("redefine")) {
            this.report(4, "ProhibitedFeaturesFilter.Redefine", this.locator);
        }
        else if (localName.equals("schema")) {
            if (atts.getValue("blockDefault") != null && !atts.getValue("blockDefault").equals("#all")) {
                this.report(3, "ProhibitedFeaturesFilter.BlockDefaultAttrOfSchema", this.locator);
            }
            if (atts.getValue("finalDefault") != null) {
                this.report(3, "ProhibitedFeaturesFilter.FinalDefaultAttrOfSchema", this.locator);
            }
            if (atts.getValue("http://java.sun.com/xml/ns/jaxb", "extensionBindingPrefixes") != null) {
                this.report(1, "ProhibitedFeaturesFilter.ExtensionBindingPrefixesOfSchema", this.locator);
            }
        }
        super.startElement(uri, localName, qName, atts);
    }
    
    public void setDocumentLocator(final Locator locator) {
        super.setDocumentLocator(locator);
        this.locator = locator;
    }
    
    private void report(int type, final String msg, final Locator loc) throws SAXException {
        SAXParseException spe = null;
        if (type == 2 && !this.strict) {
            type = 3;
        }
        if (type == 1 && !this.strict) {
            return;
        }
        switch (type) {
            case 1:
            case 2: {
                spe = new SAXParseException(Messages.format("ProhibitedFeaturesFilter.StrictModePrefix") + "\n\t" + Messages.format(msg), loc);
                this.errorHandler.error(spe);
                throw spe;
            }
            case 3: {
                spe = new SAXParseException(Messages.format("ProhibitedFeaturesFilter.WarningPrefix") + " " + Messages.format(msg), loc);
                this.errorHandler.warning(spe);
            }
            case 4: {
                spe = new SAXParseException(Messages.format("ProhibitedFeaturesFilter.UnsupportedPrefix") + " " + Messages.format(msg), loc);
                this.errorHandler.error(spe);
                throw spe;
            }
            default: {
                throw new JAXBAssertionError();
            }
        }
    }
    
    private static boolean parsedBooleanTrue(final String lexicalBoolean) throws SAXParseException {
        return lexicalBoolean.equals("true") || lexicalBoolean.equals("1");
    }
    
    private static boolean parseElementBlockAttr(final String lexicalBlock) {
        return lexicalBlock.equals("#all") || (lexicalBlock.indexOf("restriction") != -1 && lexicalBlock.indexOf("extension") != -1 && lexicalBlock.indexOf("substitution") != -1);
    }
    
    private static boolean parseComplexTypeBlockAttr(final String lexicalBlock) {
        return lexicalBlock.equals("#all") || (lexicalBlock.indexOf("restriction") != -1 && lexicalBlock.indexOf("extension") != -1);
    }
}
