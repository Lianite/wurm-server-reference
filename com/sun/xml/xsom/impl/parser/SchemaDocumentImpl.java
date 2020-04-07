// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.xml.xsom.impl.parser;

import com.sun.xml.xsom.XSSchema;
import java.util.Iterator;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import com.sun.xml.xsom.impl.SchemaImpl;
import com.sun.xml.xsom.parser.SchemaDocument;

public final class SchemaDocumentImpl implements SchemaDocument
{
    private final SchemaImpl schema;
    private final String schemaDocumentURI;
    final Set<SchemaDocumentImpl> references;
    final Set<SchemaDocumentImpl> referers;
    
    protected SchemaDocumentImpl(final SchemaImpl schema, final String _schemaDocumentURI) {
        this.references = new HashSet<SchemaDocumentImpl>();
        this.referers = new HashSet<SchemaDocumentImpl>();
        this.schema = schema;
        this.schemaDocumentURI = _schemaDocumentURI;
    }
    
    public String getSystemId() {
        return this.schemaDocumentURI;
    }
    
    public String getTargetNamespace() {
        return this.schema.getTargetNamespace();
    }
    
    public SchemaImpl getSchema() {
        return this.schema;
    }
    
    public Set<SchemaDocument> getReferencedDocuments() {
        return Collections.unmodifiableSet((Set<? extends SchemaDocument>)this.references);
    }
    
    public Set<SchemaDocument> getIncludedDocuments() {
        return this.getImportedDocuments(this.getTargetNamespace());
    }
    
    public Set<SchemaDocument> getImportedDocuments(final String targetNamespace) {
        if (targetNamespace == null) {
            throw new IllegalArgumentException();
        }
        final Set<SchemaDocument> r = new HashSet<SchemaDocument>();
        for (final SchemaDocumentImpl doc : this.references) {
            if (doc.getTargetNamespace().equals(targetNamespace)) {
                r.add(doc);
            }
        }
        return Collections.unmodifiableSet((Set<? extends SchemaDocument>)r);
    }
    
    public boolean includes(final SchemaDocument doc) {
        return this.references.contains(doc) && doc.getSchema() == this.schema;
    }
    
    public boolean imports(final SchemaDocument doc) {
        return this.references.contains(doc) && doc.getSchema() != this.schema;
    }
    
    public Set<SchemaDocument> getReferers() {
        return Collections.unmodifiableSet((Set<? extends SchemaDocument>)this.referers);
    }
    
    public boolean equals(final Object o) {
        final SchemaDocumentImpl rhs = (SchemaDocumentImpl)o;
        if (this.schemaDocumentURI == null || rhs.schemaDocumentURI == null) {
            return this == rhs;
        }
        return this.schemaDocumentURI.equals(rhs.schemaDocumentURI) && this.schema == rhs.schema;
    }
    
    public int hashCode() {
        if (this.schemaDocumentURI == null) {
            return super.hashCode();
        }
        return this.schemaDocumentURI.hashCode() ^ this.schema.hashCode();
    }
}
