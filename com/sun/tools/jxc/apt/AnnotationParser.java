// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.jxc.apt;

import javax.xml.bind.SchemaOutputResolver;
import com.sun.tools.xjc.api.J2SJAXBModel;
import com.sun.tools.xjc.api.Reference;
import java.util.Iterator;
import org.xml.sax.SAXException;
import java.io.IOException;
import com.sun.tools.xjc.api.ErrorListener;
import java.util.Collections;
import com.sun.tools.xjc.api.XJC;
import org.xml.sax.ErrorHandler;
import com.sun.mirror.declaration.TypeDeclaration;
import java.util.Collection;
import com.sun.tools.jxc.ConfigReader;
import org.xml.sax.Locator;
import java.util.StringTokenizer;
import java.io.File;
import java.util.Map;
import com.sun.mirror.declaration.AnnotationTypeDeclaration;
import java.util.Set;
import com.sun.tools.xjc.ErrorReceiver;
import com.sun.mirror.apt.AnnotationProcessorEnvironment;
import com.sun.mirror.apt.AnnotationProcessor;

final class AnnotationParser implements AnnotationProcessor
{
    private final AnnotationProcessorEnvironment env;
    private ErrorReceiver errorListener;
    
    public AnnotationProcessorEnvironment getEnv() {
        return this.env;
    }
    
    AnnotationParser(final Set<AnnotationTypeDeclaration> atds, final AnnotationProcessorEnvironment env) {
        this.env = env;
        this.errorListener = new ErrorReceiverImpl(env.getMessager(), env.getOptions().containsKey("-Ajaxb.debug"));
    }
    
    public void process() {
        for (final Map.Entry<String, String> me : this.env.getOptions().entrySet()) {
            final String key = me.getKey();
            if (key.startsWith("-Ajaxb.config=")) {
                final String value = key.substring("-Ajaxb.config".length() + 1);
                final StringTokenizer st = new StringTokenizer(value, File.pathSeparator);
                if (!st.hasMoreTokens()) {
                    this.errorListener.error(null, Messages.OPERAND_MISSING.format("-Ajaxb.config"));
                }
                else {
                    while (st.hasMoreTokens()) {
                        final File configFile = new File(st.nextToken());
                        if (!configFile.exists()) {
                            this.errorListener.error(null, Messages.NON_EXISTENT_FILE.format(new Object[0]));
                        }
                        else {
                            try {
                                final ConfigReader configReader = new ConfigReader(this.env, this.env.getTypeDeclarations(), configFile, this.errorListener);
                                final Collection<Reference> classesToBeIncluded = configReader.getClassesToBeIncluded();
                                final J2SJAXBModel model = XJC.createJavaCompiler().bind(classesToBeIncluded, Collections.emptyMap(), null, this.env);
                                final SchemaOutputResolver schemaOutputResolver = configReader.getSchemaOutputResolver();
                                model.generateSchema(schemaOutputResolver, this.errorListener);
                            }
                            catch (IOException e) {
                                this.errorListener.error(e.getMessage(), e);
                            }
                            catch (SAXException ex) {}
                        }
                    }
                }
            }
        }
    }
}
