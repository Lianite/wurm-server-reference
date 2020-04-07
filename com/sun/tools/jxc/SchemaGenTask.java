// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.jxc;

import java.util.Map;
import com.sun.tools.jxc.apt.SchemaGenerator;
import org.apache.tools.ant.BuildException;
import java.util.HashMap;
import com.sun.mirror.apt.AnnotationProcessorFactory;
import org.apache.tools.ant.types.Commandline;
import java.util.ArrayList;
import java.io.File;
import java.util.List;

public class SchemaGenTask extends AptBasedTask
{
    private final List schemas;
    private File episode;
    
    public SchemaGenTask() {
        this.schemas = new ArrayList();
    }
    
    protected void setupCommandlineSwitches(final Commandline cmd) {
        cmd.createArgument().setValue("-nocompile");
    }
    
    protected String getCompilationMessage() {
        return "Generating schema from ";
    }
    
    protected String getFailedMessage() {
        return "schema generation failed";
    }
    
    public Schema createSchema() {
        final Schema s = new Schema();
        this.schemas.add(s);
        return s;
    }
    
    public void setEpisode(final File f) {
        this.episode = f;
    }
    
    protected AnnotationProcessorFactory createFactory() {
        final Map m = new HashMap();
        for (int i = 0; i < this.schemas.size(); ++i) {
            final Schema schema = this.schemas.get(i);
            if (m.containsKey(schema.namespace)) {
                throw new BuildException("the same namespace is specified twice");
            }
            m.put(schema.namespace, schema.file);
        }
        final SchemaGenerator r = new SchemaGenerator(m);
        if (this.episode != null) {
            r.setEpisodeFile(this.episode);
        }
        return (AnnotationProcessorFactory)r;
    }
    
    public class Schema
    {
        private String namespace;
        private File file;
        
        public void setNamespace(final String namespace) {
            this.namespace = namespace;
        }
        
        public void setFile(final String fileName) {
            File dest = SchemaGenTask.this.getDestdir();
            if (dest == null) {
                dest = SchemaGenTask.this.getProject().getBaseDir();
            }
            this.file = new File(dest, fileName);
        }
    }
}
