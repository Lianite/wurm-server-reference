// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.javaws.jnl;

import java.util.Properties;
import com.sun.javaws.Globals;
import com.sun.deploy.config.Config;
import com.sun.javaws.util.GeneralUtil;
import java.util.Collection;
import java.util.Arrays;
import java.util.ArrayList;
import com.sun.javaws.exceptions.BadFieldException;
import java.io.IOException;
import java.net.URL;
import com.sun.deploy.xml.XMLNode;
import com.sun.deploy.util.Trace;
import com.sun.deploy.util.TraceLevel;
import com.sun.deploy.util.URLUtil;
import com.sun.javaws.exceptions.MissingFieldException;
import com.sun.deploy.xml.BadTokenException;
import com.sun.deploy.xml.XMLParser;
import com.sun.javaws.exceptions.JNLParseException;
import com.sun.deploy.xml.XMLEncoding;

public class XMLFormat
{
    public static LaunchDesc parse(final byte[] array) throws IOException, BadFieldException, MissingFieldException, JNLParseException {
        String decodeXML;
        try {
            decodeXML = XMLEncoding.decodeXML(array);
        }
        catch (Exception ex) {
            throw new JNLParseException(null, ex, "exception determining encoding of jnlp file", 0);
        }
        XMLNode parse;
        try {
            parse = new XMLParser(decodeXML).parse();
        }
        catch (BadTokenException ex2) {
            throw new JNLParseException(decodeXML, (Exception)ex2, "wrong kind of token found", ex2.getLine());
        }
        catch (Exception ex3) {
            throw new JNLParseException(decodeXML, ex3, "exception parsing jnlp file", 0);
        }
        ApplicationDesc buildApplicationDesc = null;
        AppletDesc buildAppletDesc = null;
        LibraryDesc buildLibraryDesc = null;
        InstallerDesc buildInstallerDesc = null;
        final String s = null;
        if (parse.getName().equals("player") || parse.getName().equals("viewer")) {
            return LaunchDescFactory.buildInternalLaunchDesc(parse.getName(), decodeXML, XMLUtils.getAttribute(parse, null, "tab"));
        }
        if (!parse.getName().equals("jnlp")) {
            throw new MissingFieldException(decodeXML, "<jnlp>");
        }
        final String attribute = XMLUtils.getAttribute(parse, "", "spec", "1.0+");
        final String attribute2 = XMLUtils.getAttribute(parse, "", "version");
        final URL pathURL = URLUtil.asPathURL(XMLUtils.getAttributeURL(decodeXML, parse, "", "codebase"));
        final URL attributeURL = XMLUtils.getAttributeURL(decodeXML, pathURL, parse, "", "href");
        int n = 0;
        if (XMLUtils.isElementPath(parse, "<security><all-permissions>")) {
            n = 1;
        }
        else if (XMLUtils.isElementPath(parse, "<security><j2ee-application-client-permissions>")) {
            n = 2;
        }
        int n2;
        if (XMLUtils.isElementPath(parse, "<application-desc>")) {
            n2 = 1;
            buildApplicationDesc = buildApplicationDesc(decodeXML, parse);
        }
        else if (XMLUtils.isElementPath(parse, "<component-desc>")) {
            n2 = 3;
            buildLibraryDesc = buildLibraryDesc(decodeXML, parse);
        }
        else if (XMLUtils.isElementPath(parse, "<installer-desc>")) {
            n2 = 4;
            buildInstallerDesc = buildInstallerDesc(decodeXML, pathURL, parse);
        }
        else {
            if (!XMLUtils.isElementPath(parse, "<applet-desc>")) {
                throw new MissingFieldException(decodeXML, "<jnlp>(<application-desc>|<applet-desc>|<installer-desc>|<component-desc>)");
            }
            n2 = 2;
            buildAppletDesc = buildAppletDesc(decodeXML, pathURL, parse);
        }
        final LaunchDesc launchDesc = new LaunchDesc(attribute, pathURL, attributeURL, attribute2, buildInformationDesc(decodeXML, pathURL, parse), n, buildResourcesDesc(decodeXML, pathURL, parse, false), n2, buildApplicationDesc, buildAppletDesc, buildLibraryDesc, buildInstallerDesc, s, decodeXML, array);
        Trace.println("returning LaunchDesc from XMLFormat.parse():\n" + launchDesc, TraceLevel.TEMP);
        return launchDesc;
    }
    
    private static InformationDesc combineInformationDesc(final InformationDesc informationDesc, final InformationDesc informationDesc2) {
        if (informationDesc == null) {
            return informationDesc2;
        }
        if (informationDesc2 == null) {
            return informationDesc;
        }
        final String s = (informationDesc.getTitle() != null) ? informationDesc.getTitle() : informationDesc2.getTitle();
        final String s2 = (informationDesc.getVendor() != null) ? informationDesc.getVendor() : informationDesc2.getVendor();
        final URL url = (informationDesc.getHome() != null) ? informationDesc.getHome() : informationDesc2.getHome();
        final String[] array = new String[4];
        for (int i = 0; i < array.length; ++i) {
            array[i] = ((informationDesc.getDescription(i) != null) ? informationDesc.getDescription(i) : informationDesc2.getDescription(i));
        }
        final ArrayList list = new ArrayList();
        if (informationDesc2.getIcons() != null) {
            list.addAll(Arrays.asList(informationDesc2.getIcons()));
        }
        if (informationDesc.getIcons() != null) {
            list.addAll(Arrays.asList(informationDesc.getIcons()));
        }
        return new InformationDesc(s, s2, url, array, list.toArray(new IconDesc[list.size()]), (informationDesc.getShortcut() != null) ? informationDesc.getShortcut() : informationDesc2.getShortcut(), (RContentDesc[])addArrays(informationDesc.getRelatedContent(), informationDesc2.getRelatedContent()), (AssociationDesc[])addArrays(informationDesc.getAssociations(), informationDesc2.getAssociations()), informationDesc.supportsOfflineOperation() || informationDesc.supportsOfflineOperation());
    }
    
    private static InformationDesc buildInformationDesc(final String s, final URL url, final XMLNode xmlNode) throws MissingFieldException, BadFieldException {
        final ArrayList<InformationDesc> list = new ArrayList<InformationDesc>();
        XMLUtils.visitElements(xmlNode, "<information>", new XMLUtils.ElementVisitor() {
            private final /* synthetic */ ArrayList val$list = list;
            
            public void visitElement(final XMLNode xmlNode) throws BadFieldException, MissingFieldException {
                GeneralUtil.getStringList(XMLUtils.getAttribute(xmlNode, "", "locale"));
                final String[] stringList = GeneralUtil.getStringList(XMLUtils.getAttribute(xmlNode, "", "os", null));
                final String[] stringList2 = GeneralUtil.getStringList(XMLUtils.getAttribute(xmlNode, "", "arch", null));
                final String[] stringList3 = GeneralUtil.getStringList(XMLUtils.getAttribute(xmlNode, "", "locale", null));
                final String[] stringList4 = GeneralUtil.getStringList(XMLUtils.getAttribute(xmlNode, "", "platform", null));
                if (GeneralUtil.prefixMatchStringList(stringList, Config.getOSName()) && GeneralUtil.prefixMatchStringList(stringList2, Config.getOSArch()) && GeneralUtil.prefixMatchStringList(stringList4, Config.getOSPlatform()) && XMLFormat.matchDefaultLocale(stringList3)) {
                    this.val$list.add(new InformationDesc(XMLUtils.getElementContents(xmlNode, "<title>"), XMLUtils.getElementContents(xmlNode, "<vendor>"), XMLUtils.getAttributeURL(s, url, xmlNode, "<homepage>", "href"), new String[] { XMLUtils.getElementContentsWithAttribute(xmlNode, "<description>", "kind", "", null), XMLUtils.getElementContentsWithAttribute(xmlNode, "<description>", "kind", "short", null), XMLUtils.getElementContentsWithAttribute(xmlNode, "<description>", "kind", "one-line", null), XMLUtils.getElementContentsWithAttribute(xmlNode, "<description>", "kind", "tooltip", null) }, getIconDescs(s, url, xmlNode), getShortcutDesc(xmlNode), getRContentDescs(s, url, xmlNode), getAssociationDesc(s, xmlNode), XMLUtils.isElementPath(xmlNode, "<offline-allowed>")));
                }
            }
        });
        InformationDesc combineInformationDesc = new InformationDesc(null, null, null, null, null, null, null, null, false);
        for (int i = 0; i < list.size(); ++i) {
            combineInformationDesc = combineInformationDesc(list.get(i), combineInformationDesc);
        }
        if (combineInformationDesc.getTitle() == null) {
            throw new MissingFieldException(s, "<jnlp><information><title>");
        }
        if (combineInformationDesc.getVendor() == null) {
            throw new MissingFieldException(s, "<jnlp><information><vendor>");
        }
        return combineInformationDesc;
    }
    
    private static Object[] addArrays(final Object[] array, final Object[] array2) {
        if (array == null) {
            return array2;
        }
        if (array2 == null) {
            return array;
        }
        final ArrayList<Object> list = new ArrayList<Object>();
        int i = 0;
        while (i < array.length) {
            list.add(array[i++]);
        }
        int j = 0;
        while (j < array2.length) {
            list.add(array2[j++]);
        }
        return list.toArray(array);
    }
    
    public static boolean matchDefaultLocale(final String[] array) {
        return GeneralUtil.matchLocale(array, Globals.getDefaultLocale());
    }
    
    static final ResourcesDesc buildResourcesDesc(final String s, final URL url, final XMLNode xmlNode, final boolean b) throws MissingFieldException, BadFieldException {
        final ResourcesDesc resourcesDesc = new ResourcesDesc();
        XMLUtils.visitElements(xmlNode, "<resources>", new XMLUtils.ElementVisitor() {
            public void visitElement(final XMLNode xmlNode) throws MissingFieldException, BadFieldException {
                final String[] stringList = GeneralUtil.getStringList(XMLUtils.getAttribute(xmlNode, "", "os", null));
                final String[] stringList2 = GeneralUtil.getStringList(XMLUtils.getAttribute(xmlNode, "", "arch", null));
                final String[] stringList3 = GeneralUtil.getStringList(XMLUtils.getAttribute(xmlNode, "", "locale", null));
                if (GeneralUtil.prefixMatchStringList(stringList, Config.getOSName()) && GeneralUtil.prefixMatchStringList(stringList2, Config.getOSArch()) && XMLFormat.matchDefaultLocale(stringList3)) {
                    XMLUtils.visitChildrenElements(xmlNode, new XMLUtils.ElementVisitor() {
                        public void visitElement(final XMLNode xmlNode) throws MissingFieldException, BadFieldException {
                            handleResourceElement(s, url, xmlNode, resourcesDesc, b);
                        }
                    });
                }
            }
        });
        return resourcesDesc.isEmpty() ? null : resourcesDesc;
    }
    
    private static IconDesc[] getIconDescs(final String s, final URL url, final XMLNode xmlNode) throws MissingFieldException, BadFieldException {
        final ArrayList list = new ArrayList();
        XMLUtils.visitElements(xmlNode, "<icon>", new XMLUtils.ElementVisitor() {
            public void visitElement(final XMLNode xmlNode) throws MissingFieldException, BadFieldException {
                final String attribute = XMLUtils.getAttribute(xmlNode, "", "kind", "");
                final URL requiredURL = XMLUtils.getRequiredURL(s, url, xmlNode, "", "href");
                final String attribute2 = XMLUtils.getAttribute(xmlNode, "", "version", null);
                final int intAttribute = XMLUtils.getIntAttribute(s, xmlNode, "", "height", 0);
                final int intAttribute2 = XMLUtils.getIntAttribute(s, xmlNode, "", "width", 0);
                final int intAttribute3 = XMLUtils.getIntAttribute(s, xmlNode, "", "depth", 0);
                int n = 0;
                if (attribute.equals("selected")) {
                    n = 1;
                }
                else if (attribute.equals("disabled")) {
                    n = 2;
                }
                else if (attribute.equals("rollover")) {
                    n = 3;
                }
                else if (attribute.equals("splash")) {
                    n = 4;
                }
                list.add(new IconDesc(requiredURL, attribute2, intAttribute, intAttribute2, intAttribute3, n));
            }
        });
        return list.toArray(new IconDesc[list.size()]);
    }
    
    private static ShortcutDesc getShortcutDesc(final XMLNode xmlNode) throws MissingFieldException, BadFieldException {
        final ArrayList<ShortcutDesc> list = new ArrayList<ShortcutDesc>();
        XMLUtils.visitElements(xmlNode, "<shortcut>", new XMLUtils.ElementVisitor() {
            private final /* synthetic */ ArrayList val$shortcuts = list;
            
            public void visitElement(final XMLNode xmlNode) throws MissingFieldException, BadFieldException {
                this.val$shortcuts.add(new ShortcutDesc(XMLUtils.getAttribute(xmlNode, "", "online", "true").equalsIgnoreCase("true"), XMLUtils.isElementPath(xmlNode, "<desktop>"), XMLUtils.isElementPath(xmlNode, "<menu>"), XMLUtils.getAttribute(xmlNode, "<menu>", "submenu")));
            }
        });
        if (list.size() > 0) {
            return list.get(0);
        }
        return null;
    }
    
    private static AssociationDesc[] getAssociationDesc(final String s, final XMLNode xmlNode) throws MissingFieldException, BadFieldException {
        final ArrayList list = new ArrayList();
        XMLUtils.visitElements(xmlNode, "<association>", new XMLUtils.ElementVisitor() {
            public void visitElement(final XMLNode xmlNode) throws MissingFieldException, BadFieldException {
                list.add(new AssociationDesc(XMLUtils.getRequiredAttribute(s, xmlNode, "", "extensions"), XMLUtils.getRequiredAttribute(s, xmlNode, "", "mime-type")));
            }
        });
        return list.toArray(new AssociationDesc[list.size()]);
    }
    
    private static RContentDesc[] getRContentDescs(final String s, final URL url, final XMLNode xmlNode) throws MissingFieldException, BadFieldException {
        final ArrayList list = new ArrayList();
        XMLUtils.visitElements(xmlNode, "<related-content>", new XMLUtils.ElementVisitor() {
            public void visitElement(final XMLNode xmlNode) throws MissingFieldException, BadFieldException {
                list.add(new RContentDesc(XMLUtils.getRequiredURL(s, url, xmlNode, "", "href"), XMLUtils.getElementContents(xmlNode, "<title>"), XMLUtils.getElementContents(xmlNode, "<description>"), XMLUtils.getAttributeURL(s, url, xmlNode, "<icon>", "href")));
            }
        });
        return list.toArray(new RContentDesc[list.size()]);
    }
    
    private static void handleResourceElement(final String s, final URL url, final XMLNode xmlNode, final ResourcesDesc resourcesDesc, final boolean b) throws MissingFieldException, BadFieldException {
        final String name = xmlNode.getName();
        if (name.equals("jar") || name.equals("nativelib")) {
            final URL requiredURL = XMLUtils.getRequiredURL(s, url, xmlNode, "", "href");
            final String attribute = XMLUtils.getAttribute(xmlNode, "", "version", null);
            final String attribute2 = XMLUtils.getAttribute(xmlNode, "", "download");
            final String attribute3 = XMLUtils.getAttribute(xmlNode, "", "main");
            final String attribute4 = XMLUtils.getAttribute(xmlNode, "", "part");
            final int intAttribute = XMLUtils.getIntAttribute(s, xmlNode, "", "size", 0);
            final boolean equals = name.equals("nativelib");
            boolean b2 = false;
            boolean b3 = false;
            if ("lazy".equalsIgnoreCase(attribute2)) {
                b2 = true;
            }
            if ("true".equalsIgnoreCase(attribute3)) {
                b3 = true;
            }
            resourcesDesc.addResource(new JARDesc(requiredURL, attribute, b2, b3, equals, attribute4, intAttribute, resourcesDesc));
        }
        else if (name.equals("property")) {
            resourcesDesc.addResource(new PropertyDesc(XMLUtils.getRequiredAttribute(s, xmlNode, "", "name"), XMLUtils.getRequiredAttributeEmptyOK(s, xmlNode, "", "value")));
        }
        else if (name.equals("package")) {
            resourcesDesc.addResource(new PackageDesc(XMLUtils.getRequiredAttribute(s, xmlNode, "", "name"), XMLUtils.getRequiredAttribute(s, xmlNode, "", "part"), "true".equals(XMLUtils.getAttribute(xmlNode, "", "recursive", "false"))));
        }
        else if (name.equals("extension")) {
            resourcesDesc.addResource(new ExtensionDesc(XMLUtils.getAttribute(xmlNode, "", "name"), XMLUtils.getRequiredURL(s, url, xmlNode, "", "href"), XMLUtils.getAttribute(xmlNode, "", "version", null), getExtDownloadDescs(s, xmlNode)));
        }
        else if (name.equals("j2se") && !b) {
            resourcesDesc.addResource(new JREDesc(XMLUtils.getRequiredAttribute(s, xmlNode, "", "version"), GeneralUtil.heapValToLong(XMLUtils.getAttribute(xmlNode, "", "initial-heap-size")), GeneralUtil.heapValToLong(XMLUtils.getAttribute(xmlNode, "", "max-heap-size")), XMLUtils.getAttribute(xmlNode, "", "java-vm-args"), XMLUtils.getAttributeURL(s, url, xmlNode, "", "href"), buildResourcesDesc(s, url, xmlNode, true)));
        }
    }
    
    private static ExtDownloadDesc[] getExtDownloadDescs(final String s, final XMLNode xmlNode) throws BadFieldException, MissingFieldException {
        final ArrayList list = new ArrayList();
        XMLUtils.visitElements(xmlNode, "<ext-download>", new XMLUtils.ElementVisitor() {
            public void visitElement(final XMLNode xmlNode) throws MissingFieldException {
                list.add(new ExtDownloadDesc(XMLUtils.getRequiredAttribute(s, xmlNode, "", "ext-part"), XMLUtils.getAttribute(xmlNode, "", "part"), "lazy".equals(XMLUtils.getAttribute(xmlNode, "", "download", "eager"))));
            }
        });
        return list.toArray(new ExtDownloadDesc[list.size()]);
    }
    
    private static ApplicationDesc buildApplicationDesc(final String s, final XMLNode xmlNode) throws MissingFieldException, BadFieldException {
        final String attribute = XMLUtils.getAttribute(xmlNode, "<application-desc>", "main-class");
        final ArrayList list = new ArrayList();
        XMLUtils.visitElements(xmlNode, "<application-desc><argument>", new XMLUtils.ElementVisitor() {
            public void visitElement(final XMLNode xmlNode) throws MissingFieldException, BadFieldException {
                final String elementContents = XMLUtils.getElementContents(xmlNode, "", null);
                if (elementContents == null) {
                    throw new BadFieldException(s, XMLUtils.getPathString(xmlNode), "");
                }
                list.add(elementContents);
            }
        });
        return new ApplicationDesc(attribute, list.toArray(new String[list.size()]));
    }
    
    private static LibraryDesc buildLibraryDesc(final String s, final XMLNode xmlNode) throws MissingFieldException, BadFieldException {
        return new LibraryDesc(XMLUtils.getAttribute(xmlNode, "<component-desc>", "unique-id"));
    }
    
    private static InstallerDesc buildInstallerDesc(final String s, final URL url, final XMLNode xmlNode) throws MissingFieldException, BadFieldException {
        return new InstallerDesc(XMLUtils.getAttribute(xmlNode, "<installer-desc>", "main-class"));
    }
    
    private static AppletDesc buildAppletDesc(final String s, final URL url, final XMLNode xmlNode) throws MissingFieldException, BadFieldException {
        final String requiredAttribute = XMLUtils.getRequiredAttribute(s, xmlNode, "<applet-desc>", "main-class");
        final String requiredAttribute2 = XMLUtils.getRequiredAttribute(s, xmlNode, "<applet-desc>", "name");
        final URL attributeURL = XMLUtils.getAttributeURL(s, url, xmlNode, "<applet-desc>", "documentbase");
        final int requiredIntAttribute = XMLUtils.getRequiredIntAttribute(s, xmlNode, "<applet-desc>", "width");
        final int requiredIntAttribute2 = XMLUtils.getRequiredIntAttribute(s, xmlNode, "<applet-desc>", "height");
        if (requiredIntAttribute <= 0) {
            throw new BadFieldException(s, XMLUtils.getPathString(xmlNode) + "<applet-desc>width", new Integer(requiredIntAttribute).toString());
        }
        if (requiredIntAttribute2 <= 0) {
            throw new BadFieldException(s, XMLUtils.getPathString(xmlNode) + "<applet-desc>height", new Integer(requiredIntAttribute2).toString());
        }
        final Properties properties = new Properties();
        XMLUtils.visitElements(xmlNode, "<applet-desc><param>", new XMLUtils.ElementVisitor() {
            public void visitElement(final XMLNode xmlNode) throws MissingFieldException, BadFieldException {
                properties.setProperty(XMLUtils.getRequiredAttribute(s, xmlNode, "", "name"), XMLUtils.getRequiredAttributeEmptyOK(s, xmlNode, "", "value"));
            }
        });
        return new AppletDesc(requiredAttribute2, requiredAttribute, attributeURL, requiredIntAttribute, requiredIntAttribute2, properties);
    }
}
