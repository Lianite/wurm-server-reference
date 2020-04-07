// 
// Decompiled by Procyon v0.5.30
// 

package org.apache.xml.resolver;

import java.net.MalformedURLException;
import java.util.StringTokenizer;
import java.util.Vector;
import java.io.InputStream;
import java.io.IOException;
import java.util.MissingResourceException;
import java.util.PropertyResourceBundle;
import java.net.URL;
import java.util.ResourceBundle;

public class CatalogManager
{
    private static String pFiles;
    private static String pVerbosity;
    private static String pPrefer;
    private static String pStatic;
    private static String pAllowPI;
    private static String pClassname;
    private static String pIgnoreMissing;
    private static boolean ignoreMissingProperties;
    private static ResourceBundle resources;
    private static String propertyFile;
    private static URL propertyFileURI;
    private static String defaultCatalogFiles;
    private static int defaultVerbosity;
    private static boolean defaultPreferPublic;
    private static boolean defaultStaticCatalog;
    private static boolean defaultOasisXMLCatalogPI;
    private static boolean defaultRelativeCatalogs;
    
    private static synchronized void readProperties() {
        try {
            CatalogManager.propertyFileURI = ((CatalogManager.class$org$apache$xml$resolver$CatalogManager == null) ? (CatalogManager.class$org$apache$xml$resolver$CatalogManager = class$("org.apache.xml.resolver.CatalogManager")) : CatalogManager.class$org$apache$xml$resolver$CatalogManager).getResource("/" + CatalogManager.propertyFile);
            final InputStream resourceAsStream = ((CatalogManager.class$org$apache$xml$resolver$CatalogManager == null) ? (CatalogManager.class$org$apache$xml$resolver$CatalogManager = class$("org.apache.xml.resolver.CatalogManager")) : CatalogManager.class$org$apache$xml$resolver$CatalogManager).getResourceAsStream("/" + CatalogManager.propertyFile);
            if (resourceAsStream == null) {
                if (!CatalogManager.ignoreMissingProperties) {
                    System.err.println("Cannot find " + CatalogManager.propertyFile);
                }
                return;
            }
            CatalogManager.resources = new PropertyResourceBundle(resourceAsStream);
        }
        catch (MissingResourceException ex) {
            if (!CatalogManager.ignoreMissingProperties) {
                System.err.println("Cannot read " + CatalogManager.propertyFile);
            }
        }
        catch (IOException ex2) {
            if (!CatalogManager.ignoreMissingProperties) {
                System.err.println("Failure trying to read " + CatalogManager.propertyFile);
            }
        }
    }
    
    public static void ignoreMissingProperties(final boolean ignoreMissingProperties) {
        CatalogManager.ignoreMissingProperties = ignoreMissingProperties;
    }
    
    public static int verbosity() {
        String s = System.getProperty(CatalogManager.pVerbosity);
        if (s == null) {
            if (CatalogManager.resources == null) {
                readProperties();
            }
            if (CatalogManager.resources == null) {
                return CatalogManager.defaultVerbosity;
            }
            try {
                s = CatalogManager.resources.getString("verbosity");
            }
            catch (MissingResourceException ex) {
                return CatalogManager.defaultVerbosity;
            }
        }
        try {
            return Integer.parseInt(s.trim());
        }
        catch (Exception ex2) {
            System.err.println("Cannot parse verbosity: \"" + s + "\"");
            return CatalogManager.defaultVerbosity;
        }
    }
    
    public static boolean relativeCatalogs() {
        if (CatalogManager.resources == null) {
            readProperties();
        }
        if (CatalogManager.resources == null) {
            return CatalogManager.defaultRelativeCatalogs;
        }
        try {
            final String string = CatalogManager.resources.getString("relative-catalogs");
            return string.equalsIgnoreCase("true") || string.equalsIgnoreCase("yes") || string.equalsIgnoreCase("1");
        }
        catch (MissingResourceException ex) {
            return CatalogManager.defaultRelativeCatalogs;
        }
    }
    
    public static Vector catalogFiles() {
        String s = System.getProperty(CatalogManager.pFiles);
        boolean b = false;
        if (s == null) {
            if (CatalogManager.resources == null) {
                readProperties();
            }
            if (CatalogManager.resources != null) {
                try {
                    s = CatalogManager.resources.getString("catalogs");
                    b = true;
                }
                catch (MissingResourceException ex) {
                    System.err.println(CatalogManager.propertyFile + ": catalogs not found.");
                    s = null;
                }
            }
        }
        if (s == null) {
            s = CatalogManager.defaultCatalogFiles;
        }
        final StringTokenizer stringTokenizer = new StringTokenizer(s, ";");
        final Vector<String> vector = new Vector<String>();
        while (stringTokenizer.hasMoreTokens()) {
            String s2 = stringTokenizer.nextToken();
            if (b && !relativeCatalogs()) {
                try {
                    s2 = new URL(CatalogManager.propertyFileURI, s2).toString();
                }
                catch (MalformedURLException ex2) {}
            }
            vector.add(s2);
        }
        return vector;
    }
    
    public static boolean preferPublic() {
        String s = System.getProperty(CatalogManager.pPrefer);
        if (s == null) {
            if (CatalogManager.resources == null) {
                readProperties();
            }
            if (CatalogManager.resources == null) {
                return CatalogManager.defaultPreferPublic;
            }
            try {
                s = CatalogManager.resources.getString("prefer");
            }
            catch (MissingResourceException ex) {
                return CatalogManager.defaultPreferPublic;
            }
        }
        if (s == null) {
            return CatalogManager.defaultPreferPublic;
        }
        return s.equalsIgnoreCase("public");
    }
    
    public static boolean staticCatalog() {
        String s = System.getProperty(CatalogManager.pStatic);
        if (s == null) {
            if (CatalogManager.resources == null) {
                readProperties();
            }
            if (CatalogManager.resources == null) {
                return CatalogManager.defaultStaticCatalog;
            }
            try {
                s = CatalogManager.resources.getString("static-catalog");
            }
            catch (MissingResourceException ex) {
                return CatalogManager.defaultStaticCatalog;
            }
        }
        if (s == null) {
            return CatalogManager.defaultStaticCatalog;
        }
        return s.equalsIgnoreCase("true") || s.equalsIgnoreCase("yes") || s.equalsIgnoreCase("1");
    }
    
    public static boolean allowOasisXMLCatalogPI() {
        String s = System.getProperty(CatalogManager.pAllowPI);
        if (s == null) {
            if (CatalogManager.resources == null) {
                readProperties();
            }
            if (CatalogManager.resources == null) {
                return CatalogManager.defaultOasisXMLCatalogPI;
            }
            try {
                s = CatalogManager.resources.getString("allow-oasis-xml-catalog-pi");
            }
            catch (MissingResourceException ex) {
                return CatalogManager.defaultOasisXMLCatalogPI;
            }
        }
        if (s == null) {
            return CatalogManager.defaultOasisXMLCatalogPI;
        }
        return s.equalsIgnoreCase("true") || s.equalsIgnoreCase("yes") || s.equalsIgnoreCase("1");
    }
    
    public static String catalogClassName() {
        final String property = System.getProperty(CatalogManager.pClassname);
        if (property == null) {
            if (CatalogManager.resources == null) {
                readProperties();
            }
            if (CatalogManager.resources == null) {
                return null;
            }
            try {
                return CatalogManager.resources.getString("catalog-class-name");
            }
            catch (MissingResourceException ex) {
                return null;
            }
        }
        return property;
    }
    
    static {
        CatalogManager.pFiles = "xml.catalog.files";
        CatalogManager.pVerbosity = "xml.catalog.verbosity";
        CatalogManager.pPrefer = "xml.catalog.prefer";
        CatalogManager.pStatic = "xml.catalog.staticCatalog";
        CatalogManager.pAllowPI = "xml.catalog.allowPI";
        CatalogManager.pClassname = "xml.catalog.className";
        CatalogManager.pIgnoreMissing = "xml.catalog.ignoreMissing";
        CatalogManager.ignoreMissingProperties = (System.getProperty(CatalogManager.pIgnoreMissing) != null || System.getProperty(CatalogManager.pFiles) != null);
        CatalogManager.propertyFile = "CatalogManager.properties";
        CatalogManager.propertyFileURI = null;
        CatalogManager.defaultCatalogFiles = "./xcatalog";
        CatalogManager.defaultVerbosity = 1;
        CatalogManager.defaultPreferPublic = true;
        CatalogManager.defaultStaticCatalog = true;
        CatalogManager.defaultOasisXMLCatalogPI = true;
        CatalogManager.defaultRelativeCatalogs = true;
    }
}
