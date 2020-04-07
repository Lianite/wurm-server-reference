// 
// Decompiled by Procyon v0.5.30
// 

package org.seamless.util.jpa;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class HibernateUtil
{
    public static final Configuration configuration;
    public static final SessionFactory sessionFactory;
    
    public static Configuration getConfiguration() {
        return HibernateUtil.configuration;
    }
    
    public static SessionFactory getSessionFactory() {
        return HibernateUtil.sessionFactory;
    }
    
    static {
        try {
            configuration = new Configuration().configure();
            sessionFactory = HibernateUtil.configuration.buildSessionFactory();
        }
        catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }
}
