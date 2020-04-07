// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.javaws.jnl;

import java.net.MalformedURLException;
import java.net.URL;
import com.sun.javaws.exceptions.MissingFieldException;
import com.sun.javaws.exceptions.BadFieldException;
import com.sun.deploy.xml.XMLNode;

public class XMLUtils
{
    public static int getIntAttribute(final String s, final XMLNode xmlNode, final String s2, final String s3, final int n) throws BadFieldException {
        final String attribute = getAttribute(xmlNode, s2, s3);
        if (attribute == null) {
            return n;
        }
        try {
            return Integer.parseInt(attribute);
        }
        catch (NumberFormatException ex) {
            throw new BadFieldException(s, getPathString(xmlNode) + s2 + s3, attribute);
        }
    }
    
    public static int getRequiredIntAttribute(final String s, final XMLNode xmlNode, final String s2, final String s3) throws BadFieldException, MissingFieldException {
        final String attribute = getAttribute(xmlNode, s2, s3);
        if (attribute == null) {
            throw new MissingFieldException(s, getPathString(xmlNode) + s2 + s3);
        }
        try {
            return Integer.parseInt(attribute);
        }
        catch (NumberFormatException ex) {
            throw new BadFieldException(s, getPathString(xmlNode) + s2 + s3, attribute);
        }
    }
    
    public static String getAttribute(final XMLNode xmlNode, final String s, final String s2) {
        return getAttribute(xmlNode, s, s2, null);
    }
    
    public static String getRequiredAttributeEmptyOK(final String s, final XMLNode xmlNode, final String s2, final String s3) throws MissingFieldException {
        String attribute = null;
        final XMLNode elementPath = findElementPath(xmlNode, s2);
        if (elementPath != null) {
            attribute = elementPath.getAttribute(s3);
        }
        if (attribute == null) {
            throw new MissingFieldException(s, getPathString(xmlNode) + s2 + s3);
        }
        return attribute;
    }
    
    public static String getRequiredAttribute(final String s, final XMLNode xmlNode, final String s2, final String s3) throws MissingFieldException {
        final String attribute = getAttribute(xmlNode, s2, s3, null);
        if (attribute == null) {
            throw new MissingFieldException(s, getPathString(xmlNode) + s2 + s3);
        }
        final String trim = attribute.trim();
        return (trim.length() == 0) ? null : trim;
    }
    
    public static String getAttribute(final XMLNode xmlNode, final String s, final String s2, final String s3) {
        final XMLNode elementPath = findElementPath(xmlNode, s);
        if (elementPath == null) {
            return s3;
        }
        final String attribute = elementPath.getAttribute(s2);
        return (attribute == null || attribute.length() == 0) ? s3 : attribute;
    }
    
    public static URL getAttributeURL(final String s, final URL url, final XMLNode xmlNode, final String s2, final String s3) throws BadFieldException {
        final String attribute = getAttribute(xmlNode, s2, s3);
        if (attribute == null) {
            return null;
        }
        try {
            if (attribute.startsWith("jar:")) {
                final int index = attribute.indexOf("!/");
                if (index > 0) {
                    final String substring = attribute.substring(index);
                    final String substring2 = attribute.substring(4, index);
                    return new URL("jar:" + ((url == null) ? new URL(substring2) : new URL(url, substring2)).toString() + substring);
                }
            }
            return (url == null) ? new URL(attribute) : new URL(url, attribute);
        }
        catch (MalformedURLException ex) {
            if (ex.getMessage().indexOf("https") != -1) {
                throw new BadFieldException(s, "<jnlp>", "https");
            }
            throw new BadFieldException(s, getPathString(xmlNode) + s2 + s3, attribute);
        }
    }
    
    public static URL getAttributeURL(final String s, final XMLNode xmlNode, final String s2, final String s3) throws BadFieldException {
        return getAttributeURL(s, null, xmlNode, s2, s3);
    }
    
    public static URL getRequiredURL(final String s, final URL url, final XMLNode xmlNode, final String s2, final String s3) throws BadFieldException, MissingFieldException {
        final URL attributeURL = getAttributeURL(s, url, xmlNode, s2, s3);
        if (attributeURL == null) {
            throw new MissingFieldException(s, getPathString(xmlNode) + s2 + s3);
        }
        return attributeURL;
    }
    
    public static URL getRequiredURL(final String s, final XMLNode xmlNode, final String s2, final String s3) throws BadFieldException, MissingFieldException {
        return getRequiredURL(s, null, xmlNode, s2, s3);
    }
    
    public static boolean isElementPath(final XMLNode xmlNode, final String s) {
        return findElementPath(xmlNode, s) != null;
    }
    
    public static URL getElementURL(final String s, final XMLNode xmlNode, final String s2) throws BadFieldException {
        final String elementContents = getElementContents(xmlNode, s2);
        try {
            return new URL(elementContents);
        }
        catch (MalformedURLException ex) {
            throw new BadFieldException(s, getPathString(xmlNode) + s2, elementContents);
        }
    }
    
    public static String getPathString(final XMLNode xmlNode) {
        return (xmlNode == null || !xmlNode.isElement()) ? "" : (getPathString(xmlNode.getParent()) + "<" + xmlNode.getName() + ">");
    }
    
    public static String getElementContentsWithAttribute(final XMLNode xmlNode, final String s, final String s2, final String s3, final String s4) throws BadFieldException, MissingFieldException {
        final XMLNode elementWithAttribute = getElementWithAttribute(xmlNode, s, s2, s3);
        if (elementWithAttribute == null) {
            return s4;
        }
        return getElementContents(elementWithAttribute, "", s4);
    }
    
    public static URL getAttributeURLWithAttribute(final String s, final XMLNode xmlNode, final String s2, final String s3, final String s4, final String s5, final URL url) throws BadFieldException, MissingFieldException {
        final XMLNode elementWithAttribute = getElementWithAttribute(xmlNode, s2, s3, s4);
        if (elementWithAttribute == null) {
            return url;
        }
        final URL attributeURL = getAttributeURL(s, elementWithAttribute, "", s5);
        if (attributeURL == null) {
            return url;
        }
        return attributeURL;
    }
    
    public static XMLNode getElementWithAttribute(final XMLNode xmlNode, final String s, final String s2, final String s3) throws BadFieldException, MissingFieldException {
        final XMLNode[] array = { null };
        visitElements(xmlNode, s, new ElementVisitor() {
            public void visitElement(final XMLNode xmlNode) throws BadFieldException, MissingFieldException {
                if (array[0] == null && xmlNode.getAttribute(s2).equals(s3)) {
                    array[0] = xmlNode;
                }
            }
        });
        return array[0];
    }
    
    public static String getElementContents(final XMLNode xmlNode, final String s) {
        return getElementContents(xmlNode, s, null);
    }
    
    public static String getElementContents(final XMLNode xmlNode, final String s, final String s2) {
        final XMLNode elementPath = findElementPath(xmlNode, s);
        if (elementPath == null) {
            return s2;
        }
        final XMLNode nested = elementPath.getNested();
        if (nested != null && !nested.isElement()) {
            return nested.getName();
        }
        return s2;
    }
    
    public static XMLNode findElementPath(final XMLNode xmlNode, final String s) {
        if (xmlNode == null) {
            return null;
        }
        if (s == null || s.length() == 0) {
            return xmlNode;
        }
        final int index = s.indexOf(62);
        if (s.charAt(0) != '<') {
            throw new IllegalArgumentException("bad path. Missing begin tag");
        }
        if (index == -1) {
            throw new IllegalArgumentException("bad path. Missing end tag");
        }
        return findElementPath(findChildElement(xmlNode, s.substring(1, index)), s.substring(index + 1));
    }
    
    public static XMLNode findChildElement(final XMLNode xmlNode, final String s) {
        for (XMLNode xmlNode2 = xmlNode.getNested(); xmlNode2 != null; xmlNode2 = xmlNode2.getNext()) {
            if (xmlNode2.isElement() && xmlNode2.getName().equals(s)) {
                return xmlNode2;
            }
        }
        return null;
    }
    
    public static void visitElements(final XMLNode xmlNode, final String s, final ElementVisitor elementVisitor) throws BadFieldException, MissingFieldException {
        final int lastIndex = s.lastIndexOf(60);
        if (lastIndex == -1) {
            throw new IllegalArgumentException("bad path. Must contain atleast one tag");
        }
        if (s.length() == 0 || s.charAt(s.length() - 1) != '>') {
            throw new IllegalArgumentException("bad path. Must end with a >");
        }
        final String substring = s.substring(0, lastIndex);
        final String substring2 = s.substring(lastIndex + 1, s.length() - 1);
        final XMLNode elementPath = findElementPath(xmlNode, substring);
        if (elementPath == null) {
            return;
        }
        for (XMLNode xmlNode2 = elementPath.getNested(); xmlNode2 != null; xmlNode2 = xmlNode2.getNext()) {
            if (xmlNode2.isElement() && xmlNode2.getName().equals(substring2)) {
                elementVisitor.visitElement(xmlNode2);
            }
        }
    }
    
    public static void visitChildrenElements(final XMLNode xmlNode, final ElementVisitor elementVisitor) throws BadFieldException, MissingFieldException {
        for (XMLNode xmlNode2 = xmlNode.getNested(); xmlNode2 != null; xmlNode2 = xmlNode2.getNext()) {
            if (xmlNode2.isElement()) {
                elementVisitor.visitElement(xmlNode2);
            }
        }
    }
    
    public abstract static class ElementVisitor
    {
        public abstract void visitElement(final XMLNode p0) throws BadFieldException, MissingFieldException;
    }
}
