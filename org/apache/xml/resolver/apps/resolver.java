// 
// Decompiled by Procyon v0.5.30
// 

package org.apache.xml.resolver.apps;

import java.io.IOException;
import java.io.FileNotFoundException;
import org.apache.xml.resolver.tools.CatalogResolver;
import java.net.MalformedURLException;
import java.net.URL;
import org.apache.xml.resolver.Catalog;
import org.apache.xml.resolver.helpers.Debug;
import java.util.Vector;

public class resolver
{
    public static void main(final String[] array) throws FileNotFoundException, IOException {
        final Vector<String> vector = new Vector<String>();
        int n = 0;
        String s = null;
        String s2 = null;
        String s3 = null;
        String string = null;
        String s4 = null;
        boolean b = false;
        for (int i = 0; i < array.length; ++i) {
            if (array[i].equals("-c")) {
                ++i;
                vector.add(array[i]);
            }
            else if (array[i].equals("-p")) {
                ++i;
                s3 = array[i];
            }
            else if (array[i].equals("-s")) {
                ++i;
                string = array[i];
            }
            else if (array[i].equals("-n")) {
                ++i;
                s2 = array[i];
            }
            else if (array[i].equals("-u")) {
                ++i;
                s4 = array[i];
            }
            else if (array[i].equals("-a")) {
                b = true;
            }
            else if (array[i].equals("-d")) {
                ++i;
                final String s5 = array[i];
                try {
                    final int int1 = Integer.parseInt(s5);
                    if (int1 >= 0) {
                        Debug.setDebug(int1);
                    }
                }
                catch (Exception ex) {}
            }
            else {
                s = array[i];
            }
        }
        if (s == null) {
            usage();
        }
        if (s.equalsIgnoreCase("doctype")) {
            n = Catalog.DOCTYPE;
            if (s3 == null && string == null) {
                System.out.println("DOCTYPE requires public or system identifier.");
                usage();
            }
        }
        else if (s.equalsIgnoreCase("document")) {
            n = Catalog.DOCUMENT;
        }
        else if (s.equalsIgnoreCase("entity")) {
            n = Catalog.ENTITY;
            if (s3 == null && string == null && s2 == null) {
                System.out.println("ENTITY requires name or public or system identifier.");
                usage();
            }
        }
        else if (s.equalsIgnoreCase("notation")) {
            n = Catalog.NOTATION;
            if (s3 == null && string == null && s2 == null) {
                System.out.println("NOTATION requires name or public or system identifier.");
                usage();
            }
        }
        else if (s.equalsIgnoreCase("public")) {
            n = Catalog.PUBLIC;
            if (s3 == null) {
                System.out.println("PUBLIC requires public identifier.");
                usage();
            }
        }
        else if (s.equalsIgnoreCase("system")) {
            n = Catalog.SYSTEM;
            if (string == null) {
                System.out.println("SYSTEM requires system identifier.");
                usage();
            }
        }
        else if (s.equalsIgnoreCase("uri")) {
            n = Catalog.URI;
            if (s4 == null) {
                System.out.println("URI requires a uri.");
                usage();
            }
        }
        else {
            System.out.println(s + " is not a recognized keyword.");
            usage();
        }
        if (b) {
            URL url;
            try {
                final String property = System.getProperty("user.dir");
                property.replace('\\', '/');
                url = new URL("file:///" + property + "/basename");
            }
            catch (MalformedURLException ex2) {
                final String property2 = System.getProperty("user.dir");
                property2.replace('\\', '/');
                Debug.message(1, "Malformed URL on cwd", property2);
                url = null;
            }
            try {
                string = new URL(url, string).toString();
            }
            catch (MalformedURLException ex3) {
                try {
                    final URL url2 = new URL("file:///" + string);
                }
                catch (MalformedURLException ex4) {
                    Debug.message(1, "Malformed URL on system id", string);
                }
            }
        }
        final Catalog catalog = new CatalogResolver().getCatalog();
        for (int j = 0; j < vector.size(); ++j) {
            catalog.parseCatalog(vector.elementAt(j));
        }
        String s6 = null;
        if (n == Catalog.DOCTYPE) {
            System.out.println("Resolve DOCTYPE (name, publicid, systemid):");
            if (s2 != null) {
                System.out.println("       name: " + s2);
            }
            if (s3 != null) {
                System.out.println("  public id: " + s3);
            }
            if (string != null) {
                System.out.println("  system id: " + string);
            }
            if (s4 != null) {
                System.out.println("        uri: " + s4);
            }
            s6 = catalog.resolveDoctype(s2, s3, string);
        }
        else if (n == Catalog.DOCUMENT) {
            System.out.println("Resolve DOCUMENT ():");
            s6 = catalog.resolveDocument();
        }
        else if (n == Catalog.ENTITY) {
            System.out.println("Resolve ENTITY (name, publicid, systemid):");
            if (s2 != null) {
                System.out.println("       name: " + s2);
            }
            if (s3 != null) {
                System.out.println("  public id: " + s3);
            }
            if (string != null) {
                System.out.println("  system id: " + string);
            }
            s6 = catalog.resolveEntity(s2, s3, string);
        }
        else if (n == Catalog.NOTATION) {
            System.out.println("Resolve NOTATION (name, publicid, systemid):");
            if (s2 != null) {
                System.out.println("       name: " + s2);
            }
            if (s3 != null) {
                System.out.println("  public id: " + s3);
            }
            if (string != null) {
                System.out.println("  system id: " + string);
            }
            s6 = catalog.resolveNotation(s2, s3, string);
        }
        else if (n == Catalog.PUBLIC) {
            System.out.println("Resolve PUBLIC (publicid, systemid):");
            if (s3 != null) {
                System.out.println("  public id: " + s3);
            }
            if (string != null) {
                System.out.println("  system id: " + string);
            }
            s6 = catalog.resolvePublic(s3, string);
        }
        else if (n == Catalog.SYSTEM) {
            System.out.println("Resolve SYSTEM (systemid):");
            if (string != null) {
                System.out.println("  system id: " + string);
            }
            s6 = catalog.resolveSystem(string);
        }
        else if (n == Catalog.URI) {
            System.out.println("Resolve URI (uri):");
            if (s4 != null) {
                System.out.println("        uri: " + s4);
            }
            s6 = catalog.resolveURI(s4);
        }
        else {
            System.out.println("resType is wrong!? This can't happen!");
            usage();
        }
        System.out.println("Result: " + s6);
    }
    
    public static void usage() {
        System.out.println("Usage: resolver [options] keyword");
        System.out.println("");
        System.out.println("Where:");
        System.out.println("");
        System.out.println("-c catalogfile  Loads a particular catalog file.");
        System.out.println("-n name         Sets the name.");
        System.out.println("-p publicId     Sets the public identifier.");
        System.out.println("-s systemId     Sets the system identifier.");
        System.out.println("-a              Makes the system URI absolute before resolution");
        System.out.println("-u uri          Sets the URI.");
        System.out.println("-d integer      Set the debug level.");
        System.out.println("keyword         Identifies the type of resolution to perform:");
        System.out.println("                doctype, document, entity, notation, public, system,");
        System.out.println("                or uri.");
        System.exit(1);
    }
}
