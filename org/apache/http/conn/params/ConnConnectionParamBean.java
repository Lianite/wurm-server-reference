// 
// Decompiled by Procyon v0.5.30
// 

package org.apache.http.conn.params;

import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpAbstractParamBean;

@Deprecated
public class ConnConnectionParamBean extends HttpAbstractParamBean
{
    public ConnConnectionParamBean(final HttpParams params) {
        super(params);
    }
    
    public void setMaxStatusLineGarbage(final int maxStatusLineGarbage) {
        this.params.setIntParameter("http.connection.max-status-line-garbage", maxStatusLineGarbage);
    }
}
