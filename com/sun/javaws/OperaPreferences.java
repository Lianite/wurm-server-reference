// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.javaws;

import java.util.NoSuchElementException;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.ListIterator;
import java.util.Iterator;
import java.io.Writer;
import java.io.PrintWriter;
import java.io.OutputStreamWriter;
import java.io.OutputStream;
import java.io.IOException;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.InputStream;
import java.util.ArrayList;

public class OperaPreferences
{
    private static final String OPERA_ENCODING = "UTF-8";
    private static final char OPEN_BRACKET = '[';
    private static final char CLOSE_BRACKET = ']';
    private static final char SEPARATOR = '=';
    private static final int DEFAULT_SIZE = 16384;
    private static final int DEFAULT_SECTION_COUNT = 20;
    private ArrayList sections;
    
    public void load(final InputStream inputStream) throws IOException {
        final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"), 16384);
        String substring = "";
        for (String s = bufferedReader.readLine(); s != null; s = bufferedReader.readLine()) {
            if (s.length() > 0) {
                if (s.charAt(0) == '[') {
                    substring = s.substring(1, s.indexOf(93));
                }
                else {
                    String substring2 = null;
                    final int index = s.indexOf(61);
                    String substring3;
                    if (index >= 0) {
                        substring3 = s.substring(0, index);
                        substring2 = s.substring(index + 1);
                    }
                    else {
                        substring3 = s;
                    }
                    this.put(substring, substring3, substring2);
                }
            }
        }
    }
    
    public void store(final OutputStream outputStream) throws IOException {
        new PrintWriter(new OutputStreamWriter(outputStream, "UTF-8"), true).println(this.toString());
    }
    
    public boolean containsSection(final String s) {
        return this.indexOf(s) >= 0;
    }
    
    public boolean containsKey(final String s, final String s2) {
        final int index = this.indexOf(s);
        return index >= 0 && ((PreferenceSection)this.sections.get(index)).contains(s2);
    }
    
    public String get(final String s, final String s2) {
        final int index = this.indexOf(s);
        final PreferenceSection.PreferenceEntry preferenceEntry = (index < 0) ? null : this.sections.get(index).get(s2);
        return (preferenceEntry == null) ? null : preferenceEntry.getValue();
    }
    
    public String put(final String s, final String s2, final String s3) {
        final int index = this.indexOf(s);
        PreferenceSection preferenceSection;
        if (index < 0) {
            preferenceSection = new PreferenceSection(s);
            this.sections.add(preferenceSection);
        }
        else {
            preferenceSection = this.sections.get(index);
        }
        return preferenceSection.put(s2, s3);
    }
    
    public PreferenceSection remove(final String s) {
        final int index = this.indexOf(s);
        return (index < 0) ? null : ((PreferenceSection)this.sections.remove(index));
    }
    
    public String remove(final String s, final String s2) {
        final int index = this.indexOf(s);
        return (index < 0) ? null : ((PreferenceSection)this.sections.get(index)).remove(s2);
    }
    
    public Iterator iterator(final String s) {
        final int index = this.indexOf(s);
        return (index < 0) ? new PreferenceSection(s).iterator() : ((PreferenceSection)this.sections.get(index)).iterator();
    }
    
    public Iterator iterator() {
        return new OperaPreferencesIterator();
    }
    
    public boolean equals(final Object o) {
        boolean b = false;
        if (o instanceof OperaPreferences) {
            final OperaPreferences operaPreferences = (OperaPreferences)o;
            final ListIterator listIterator = this.sections.listIterator();
            final ListIterator listIterator2 = operaPreferences.sections.listIterator();
            while (listIterator.hasNext() && listIterator2.hasNext()) {
                if (!listIterator.next().equals(listIterator2.next())) {
                    return b;
                }
            }
            if (!listIterator.hasNext() && !listIterator2.hasNext()) {
                b = true;
            }
        }
        return b;
    }
    
    public int hashCode() {
        return this.sections.hashCode();
    }
    
    public String toString() {
        final StringBuffer sb = new StringBuffer();
        final ListIterator<PreferenceSection> listIterator = (ListIterator<PreferenceSection>)this.sections.listIterator();
        while (listIterator.hasNext()) {
            sb.append(listIterator.next());
        }
        return sb.toString();
    }
    
    public OperaPreferences() {
        this.sections = null;
        this.sections = new ArrayList(20);
    }
    
    private int indexOf(final String s) {
        int n = 0;
        int n2 = -1;
        final ListIterator listIterator = this.sections.listIterator();
        while (listIterator.hasNext()) {
            final PreferenceSection preferenceSection = listIterator.next();
            if (preferenceSection != null && preferenceSection.getName().equalsIgnoreCase(s)) {
                n2 = n;
                break;
            }
            ++n;
        }
        return n2;
    }
    
    private class PreferenceSection
    {
        private String name;
        private HashMap entries;
        private volatile int modified;
        private PreferenceEntry start;
        private PreferenceEntry end;
        
        public String getName() {
            return this.name;
        }
        
        public boolean contains(final String s) {
            return this.entries.containsKey(s);
        }
        
        public String put(final String s, final String value) {
            final PreferenceEntry preferenceEntry = this.entries.get(s);
            String value2 = null;
            if (preferenceEntry == null) {
                final PreferenceEntry end = new PreferenceEntry(s, value);
                if (this.end == null) {
                    this.start = end;
                    this.end = end;
                }
                else {
                    this.end.add(end);
                    this.end = end;
                }
                this.entries.put(end.getKey(), end);
                ++this.modified;
            }
            else {
                value2 = preferenceEntry.getValue();
                preferenceEntry.setValue(value);
            }
            return value2;
        }
        
        public PreferenceEntry get(final String s) {
            return this.entries.get(s);
        }
        
        public String remove(final String s) {
            final PreferenceEntry preferenceEntry = this.entries.get(s);
            String value = null;
            if (preferenceEntry != null) {
                value = preferenceEntry.getValue();
                this.removeEntry(preferenceEntry);
            }
            return value;
        }
        
        public Iterator iterator() {
            return new PreferenceEntryIterator(this.start);
        }
        
        public boolean equals(final Object o) {
            boolean b = false;
            if (o instanceof PreferenceSection) {
                final PreferenceSection preferenceSection = (PreferenceSection)o;
                if (this.name == preferenceSection.name || (this.name != null && this.name.equals(preferenceSection.name))) {
                    final Iterator iterator = this.iterator();
                    final Iterator iterator2 = preferenceSection.iterator();
                    while (iterator.hasNext() && iterator2.hasNext()) {
                        if (!iterator.next().equals(iterator2.next())) {
                            return b;
                        }
                    }
                    if (!iterator.hasNext() && !iterator2.hasNext()) {
                        b = true;
                    }
                }
            }
            return b;
        }
        
        public int hashCode() {
            return this.entries.hashCode();
        }
        
        public String toString() {
            final StringBuffer sb = new StringBuffer(this.entries.size() * 80);
            if (this.name != null && this.name.length() > 0) {
                sb.append('[').append(this.name).append(']').append(System.getProperty("line.separator"));
            }
            final Iterator iterator = this.iterator();
            while (iterator.hasNext()) {
                sb.append(iterator.next()).append(System.getProperty("line.separator"));
            }
            sb.append(System.getProperty("line.separator"));
            return sb.toString();
        }
        
        public PreferenceSection(final String name) {
            this.name = name;
            this.entries = new HashMap();
            this.modified = 0;
            this.start = null;
            this.end = null;
        }
        
        private void removeEntry(final PreferenceEntry preferenceEntry) {
            if (preferenceEntry == this.start) {
                this.start = preferenceEntry.getNext();
            }
            if (preferenceEntry == this.end) {
                this.end = preferenceEntry.getPrevious();
            }
            preferenceEntry.remove();
            this.entries.remove(preferenceEntry.getKey());
            ++this.modified;
        }
        
        private class PreferenceEntry
        {
            private final String key;
            private String value;
            private PreferenceEntry previous;
            private PreferenceEntry next;
            
            public String getKey() {
                return this.key;
            }
            
            public String getValue() {
                return this.value;
            }
            
            public void setValue(final String value) {
                this.value = value;
            }
            
            public void add(final PreferenceEntry next) {
                if (this.next != null) {
                    this.next.add(next);
                }
                else {
                    this.next = next;
                    next.previous = this;
                }
            }
            
            public void remove() {
                if (this.previous != null) {
                    this.previous.next = this.next;
                }
                if (this.next != null) {
                    this.next.previous = this.previous;
                }
                this.previous = null;
                this.next = null;
            }
            
            public PreferenceEntry getPrevious() {
                return this.previous;
            }
            
            public PreferenceEntry getNext() {
                return this.next;
            }
            
            public boolean equals(final Object o) {
                boolean b = false;
                if (o instanceof PreferenceEntry) {
                    final PreferenceEntry preferenceEntry = (PreferenceEntry)o;
                    final String key = this.getKey();
                    final String key2 = preferenceEntry.getKey();
                    if (key == key2 || (key != null && key.equals(key2))) {
                        final String value = this.getValue();
                        final String value2 = preferenceEntry.getValue();
                        if (value == value2 || (value != null && value.equals(value2))) {
                            b = true;
                        }
                    }
                }
                return b;
            }
            
            public int hashCode() {
                return (this.key == null) ? 0 : this.key.hashCode();
            }
            
            public String toString() {
                final StringBuffer sb = new StringBuffer(((this.key == null) ? 0 : this.key.length()) + ((this.value == null) ? 0 : this.value.length()) + 1);
                if (this.key != null && this.value != null) {
                    sb.append(this.key).append('=').append(this.value);
                }
                else if (this.key != null) {
                    sb.append(this.key);
                }
                else if (this.value != null) {
                    sb.append(this.value);
                }
                return sb.toString();
            }
            
            public PreferenceEntry(final String key, final String value) {
                this.key = key;
                this.value = value;
                this.previous = null;
                this.next = null;
            }
        }
        
        private class PreferenceEntryIterator implements Iterator
        {
            private PreferenceEntry next;
            private PreferenceEntry current;
            private int expectedModified;
            
            public boolean hasNext() {
                return this.next != null;
            }
            
            public Object next() {
                if (PreferenceSection.this.modified != this.expectedModified) {
                    throw new ConcurrentModificationException();
                }
                if (this.next == null) {
                    throw new NoSuchElementException();
                }
                this.current = this.next;
                this.next = this.next.getNext();
                return this.current;
            }
            
            public void remove() {
                if (this.current == null) {
                    throw new IllegalStateException();
                }
                if (PreferenceSection.this.modified != this.expectedModified) {
                    throw new ConcurrentModificationException();
                }
                PreferenceSection.this.removeEntry(this.current);
                this.current = null;
                this.expectedModified = PreferenceSection.this.modified;
            }
            
            public PreferenceEntryIterator(final PreferenceEntry next) {
                this.next = next;
                this.current = null;
                this.expectedModified = PreferenceSection.this.modified;
            }
        }
    }
    
    private class OperaPreferencesIterator implements Iterator
    {
        private Iterator i;
        
        public boolean hasNext() {
            return this.i.hasNext();
        }
        
        public Object next() {
            return this.i.next().getName();
        }
        
        public void remove() {
            this.i.remove();
        }
        
        public OperaPreferencesIterator() {
            this.i = OperaPreferences.this.sections.listIterator();
        }
    }
}
