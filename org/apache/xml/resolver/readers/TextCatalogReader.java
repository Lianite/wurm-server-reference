// 
// Decompiled by Procyon v0.5.30
// 

package org.apache.xml.resolver.readers;

import org.apache.xml.resolver.CatalogException;
import java.util.Vector;
import org.apache.xml.resolver.CatalogEntry;
import java.io.IOException;
import java.net.URLConnection;
import java.io.FileNotFoundException;
import org.apache.xml.resolver.helpers.Debug;
import java.net.MalformedURLException;
import java.net.URL;
import org.apache.xml.resolver.Catalog;
import java.util.Stack;
import java.io.InputStream;

public class TextCatalogReader implements CatalogReader
{
    protected InputStream catfile;
    protected int[] stack;
    protected Stack tokenStack;
    protected int top;
    protected boolean caseSensitive;
    
    public TextCatalogReader() {
        this.catfile = null;
        this.stack = new int[3];
        this.tokenStack = new Stack();
        this.top = -1;
        this.caseSensitive = false;
    }
    
    public void setCaseSensitive(final boolean caseSensitive) {
        this.caseSensitive = caseSensitive;
    }
    
    public boolean getCaseSensitive() {
        return this.caseSensitive;
    }
    
    public void readCatalog(final Catalog catalog, final String s) throws MalformedURLException, IOException {
        URL url;
        try {
            url = new URL(s);
        }
        catch (MalformedURLException ex) {
            url = new URL("file:///" + s);
        }
        final URLConnection openConnection = url.openConnection();
        try {
            this.readCatalog(catalog, openConnection.getInputStream());
        }
        catch (FileNotFoundException ex2) {
            Debug.message(1, "Failed to load catalog, file not found", url.toString());
        }
    }
    
    public void readCatalog(final Catalog catalog, final InputStream catfile) throws MalformedURLException, IOException {
        this.catfile = catfile;
        if (this.catfile == null) {
            return;
        }
        Vector<String> vector = null;
        while (true) {
            final String nextToken = this.nextToken();
            if (nextToken == null) {
                break;
            }
            String upperCase;
            if (this.caseSensitive) {
                upperCase = nextToken;
            }
            else {
                upperCase = nextToken.toUpperCase();
            }
            try {
                final int entryArgCount = CatalogEntry.getEntryArgCount(CatalogEntry.getEntryType(upperCase));
                final Vector<String> vector2 = new Vector<String>();
                if (vector != null) {
                    catalog.unknownEntry(vector);
                    vector = null;
                }
                for (int i = 0; i < entryArgCount; ++i) {
                    vector2.addElement(this.nextToken());
                }
                catalog.addEntry(new CatalogEntry(upperCase, (Vector)vector2));
            }
            catch (CatalogException ex) {
                if (ex.getExceptionType() == 3) {
                    if (vector == null) {
                        vector = new Vector<String>();
                    }
                    vector.addElement(nextToken);
                }
                else {
                    if (ex.getExceptionType() != 2) {
                        continue;
                    }
                    Debug.message(1, "Invalid catalog entry", nextToken);
                    vector = null;
                }
            }
        }
        if (vector != null) {
            catalog.unknownEntry(vector);
        }
        this.catfile.close();
        this.catfile = null;
    }
    
    protected void finalize() {
        if (this.catfile != null) {
            try {
                this.catfile.close();
            }
            catch (IOException ex) {}
        }
        this.catfile = null;
    }
    
    protected String nextToken() throws IOException {
        String s = "";
        if (!this.tokenStack.empty()) {
            return this.tokenStack.pop();
        }
        while (true) {
            int i = this.catfile.read();
            while (i <= 32) {
                i = this.catfile.read();
                if (i < 0) {
                    return null;
                }
            }
            final int read = this.catfile.read();
            if (read < 0) {
                return null;
            }
            if (i == 45 && read == 45) {
                for (int n = 32, n2 = this.nextChar(); n != 45 || n2 != 45; n = n2, n2 = this.nextChar()) {}
            }
            else {
                this.stack[++this.top] = read;
                this.stack[++this.top] = i;
                int j = this.nextChar();
                if (j == 34 || j == 39) {
                    int nextChar;
                    while ((nextChar = this.nextChar()) != j) {
                        s = s.concat(new String(new char[] { (char)nextChar }));
                    }
                    return s;
                }
                while (j > 32) {
                    final int nextChar2 = this.nextChar();
                    if (j == 45 && nextChar2 == 45) {
                        this.stack[++this.top] = j;
                        this.stack[++this.top] = nextChar2;
                        return s;
                    }
                    s = s.concat(new String(new char[] { (char)j }));
                    j = nextChar2;
                }
                return s;
            }
        }
    }
    
    protected int nextChar() throws IOException {
        if (this.top < 0) {
            return this.catfile.read();
        }
        return this.stack[this.top--];
    }
}
