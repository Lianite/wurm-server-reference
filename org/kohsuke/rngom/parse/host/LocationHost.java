// 
// Decompiled by Procyon v0.5.30
// 

package org.kohsuke.rngom.parse.host;

import org.kohsuke.rngom.ast.om.Location;

final class LocationHost implements Location
{
    final Location lhs;
    final Location rhs;
    
    LocationHost(final Location lhs, final Location rhs) {
        this.lhs = lhs;
        this.rhs = rhs;
    }
}
