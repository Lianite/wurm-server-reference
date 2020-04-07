// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.properties;

import java.io.InputStream;
import java.io.IOException;
import java.util.Properties;
import java.net.URL;
import java.util.HashMap;
import java.util.logging.Logger;

public enum PropertiesRepository
{
    INSTANCE;
    
    private static final Logger logger;
    private static final HashMap<URL, Properties> propertiesHashMap;
    
    public static PropertiesRepository getInstance() {
        return PropertiesRepository.INSTANCE;
    }
    
    Properties getProperties(final URL file) {
        if (PropertiesRepository.propertiesHashMap.containsKey(file)) {
            return PropertiesRepository.propertiesHashMap.get(file);
        }
        final Properties properties = new Properties();
        PropertiesRepository.propertiesHashMap.put(file, properties);
        try (final InputStream is = file.openStream()) {
            properties.load(is);
        }
        catch (IOException e) {
            PropertiesRepository.logger.warning("Unable to open properties file " + file.toString());
        }
        return properties;
    }
    
    public String getValueFor(final URL file, final String key) {
        return this.getProperties(file).getProperty(key);
    }
    
    static {
        logger = Logger.getLogger(PropertiesRepository.class.getName());
        propertiesHashMap = new HashMap<URL, Properties>();
    }
}
