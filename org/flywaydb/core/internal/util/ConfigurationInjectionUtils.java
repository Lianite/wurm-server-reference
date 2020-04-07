// 
// Decompiled by Procyon v0.5.30
// 

package org.flywaydb.core.internal.util;

import org.flywaydb.core.api.configuration.ConfigurationAware;
import org.flywaydb.core.api.configuration.FlywayConfiguration;

public class ConfigurationInjectionUtils
{
    public static void injectFlywayConfiguration(final Object target, final FlywayConfiguration configuration) {
        if (target instanceof ConfigurationAware) {
            ((ConfigurationAware)target).setFlywayConfiguration(configuration);
        }
    }
}
