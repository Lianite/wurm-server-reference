// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.jxc.apt;

import com.sun.tools.xjc.api.J2SJAXBModel;
import java.util.Iterator;
import java.util.List;
import com.sun.tools.xjc.api.ErrorListener;
import java.io.OutputStream;
import javax.xml.transform.stream.StreamResult;
import com.sun.mirror.apt.Filer;
import java.io.FileOutputStream;
import javax.xml.transform.Result;
import java.io.IOException;
import javax.xml.bind.SchemaOutputResolver;
import com.sun.tools.xjc.api.XJC;
import com.sun.mirror.declaration.ClassDeclaration;
import com.sun.mirror.declaration.TypeDeclaration;
import com.sun.tools.xjc.api.Reference;
import java.util.ArrayList;
import com.sun.mirror.apt.AnnotationProcessor;
import com.sun.mirror.apt.AnnotationProcessorEnvironment;
import com.sun.mirror.declaration.AnnotationTypeDeclaration;
import java.util.Set;
import java.util.Arrays;
import java.util.Collections;
import java.util.Collection;
import java.util.HashMap;
import java.io.File;
import java.util.Map;
import com.sun.mirror.apt.AnnotationProcessorFactory;

public class SchemaGenerator implements AnnotationProcessorFactory
{
    private final Map<String, File> schemaLocations;
    private File episodeFile;
    
    public SchemaGenerator() {
        this.schemaLocations = new HashMap<String, File>();
    }
    
    public SchemaGenerator(final Map<String, File> m) {
        (this.schemaLocations = new HashMap<String, File>()).putAll(m);
    }
    
    public void setEpisodeFile(final File episodeFile) {
        this.episodeFile = episodeFile;
    }
    
    public Collection<String> supportedOptions() {
        return (Collection<String>)Collections.emptyList();
    }
    
    public Collection<String> supportedAnnotationTypes() {
        return Arrays.asList("*");
    }
    
    public AnnotationProcessor getProcessorFor(final Set<AnnotationTypeDeclaration> atds, final AnnotationProcessorEnvironment env) {
        return (AnnotationProcessor)new AnnotationProcessor() {
            final ErrorReceiverImpl errorListener = new ErrorReceiverImpl(env);
            
            public void process() {
                final List<Reference> decls = new ArrayList<Reference>();
                for (final TypeDeclaration d : env.getTypeDeclarations()) {
                    if (d instanceof ClassDeclaration) {
                        decls.add(new Reference(d, env));
                    }
                }
                final J2SJAXBModel model = XJC.createJavaCompiler().bind(decls, Collections.emptyMap(), null, env);
                if (model == null) {
                    return;
                }
                try {
                    model.generateSchema(new SchemaOutputResolver() {
                        public Result createOutput(final String namespaceUri, final String suggestedFileName) throws IOException {
                            File file;
                            OutputStream out;
                            if (SchemaGenerator.this.schemaLocations.containsKey(namespaceUri)) {
                                file = SchemaGenerator.this.schemaLocations.get(namespaceUri);
                                if (file == null) {
                                    return null;
                                }
                                out = new FileOutputStream(file);
                            }
                            else {
                                file = new File(suggestedFileName);
                                out = env.getFiler().createBinaryFile(Filer.Location.CLASS_TREE, "", file);
                                file = file.getAbsoluteFile();
                            }
                            final StreamResult ss = new StreamResult(out);
                            env.getMessager().printNotice("Writing " + file);
                            ss.setSystemId(file.toURL().toExternalForm());
                            return ss;
                        }
                    }, this.errorListener);
                    if (SchemaGenerator.this.episodeFile != null) {
                        env.getMessager().printNotice("Writing " + SchemaGenerator.this.episodeFile);
                        model.generateEpisodeFile(new StreamResult(SchemaGenerator.this.episodeFile));
                    }
                }
                catch (IOException e) {
                    this.errorListener.error(e.getMessage(), e);
                }
            }
        };
    }
}
