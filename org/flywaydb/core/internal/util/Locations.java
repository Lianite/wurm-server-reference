// 
// Decompiled by Procyon v0.5.30
// 

package org.flywaydb.core.internal.util;

import org.flywaydb.core.internal.util.logging.LogFactory;
import java.util.Iterator;
import java.util.Collections;
import java.util.ArrayList;
import java.util.List;
import org.flywaydb.core.internal.util.logging.Log;

public class Locations
{
    private static final Log LOG;
    private final List<Location> locations;
    
    public Locations(final String... rawLocations) {
        this.locations = new ArrayList<Location>();
        final List<Location> normalizedLocations = new ArrayList<Location>();
        for (final String rawLocation : rawLocations) {
            normalizedLocations.add(new Location(rawLocation));
        }
        Collections.sort(normalizedLocations);
        for (final Location normalizedLocation : normalizedLocations) {
            if (this.locations.contains(normalizedLocation)) {
                Locations.LOG.warn("Discarding duplicate location '" + normalizedLocation + "'");
            }
            else {
                final Location parentLocation = this.getParentLocationIfExists(normalizedLocation, this.locations);
                if (parentLocation != null) {
                    Locations.LOG.warn("Discarding location '" + normalizedLocation + "' as it is a sublocation of '" + parentLocation + "'");
                }
                else {
                    this.locations.add(normalizedLocation);
                }
            }
        }
    }
    
    public List<Location> getLocations() {
        return this.locations;
    }
    
    private Location getParentLocationIfExists(final Location location, final List<Location> finalLocations) {
        for (final Location finalLocation : finalLocations) {
            if (finalLocation.isParentOf(location)) {
                return finalLocation;
            }
        }
        return null;
    }
    
    static {
        LOG = LogFactory.getLog(Locations.class);
    }
}
