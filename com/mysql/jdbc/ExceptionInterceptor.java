// 
// Decompiled by Procyon v0.5.30
// 

package com.mysql.jdbc;

import java.sql.SQLException;

public interface ExceptionInterceptor extends Extension
{
    SQLException interceptException(final SQLException p0);
}
