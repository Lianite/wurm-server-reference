// 
// Decompiled by Procyon v0.5.30
// 

package winterwell.jtwitter;

import java.util.ArrayList;
import winterwell.json.JSONException;
import java.util.Iterator;
import winterwell.json.JSONArray;
import winterwell.json.JSONObject;
import com.winterwell.jgeoplanet.Location;
import java.util.List;
import com.winterwell.jgeoplanet.BoundingBox;
import java.io.Serializable;
import com.winterwell.jgeoplanet.IPlace;

public class Place implements IPlace, Serializable
{
    private static final long serialVersionUID = 1L;
    private BoundingBox boundingBox;
    private String country;
    private String countryCode;
    private List<Location> geometry;
    private String id;
    private String name;
    private String type;
    private Place parent;
    
    @Override
    public IPlace getParent() {
        return this.parent;
    }
    
    public Place(final JSONObject _place) throws JSONException {
        this.id = InternalUtils.jsonGet("id", _place);
        if (this.id == null) {
            this.id = InternalUtils.jsonGet("woeid", _place);
        }
        this.type = InternalUtils.jsonGet("place_type", _place);
        this.name = InternalUtils.jsonGet("full_name", _place);
        if (this.name == null) {
            this.name = InternalUtils.jsonGet("name", _place);
        }
        this.countryCode = InternalUtils.jsonGet("country_code", _place);
        this.country = InternalUtils.jsonGet("country", _place);
        Object _parent = _place.opt("contained_within");
        if (_parent instanceof JSONArray) {
            final JSONArray pa = (JSONArray)_parent;
            _parent = ((pa.length() == 0) ? null : pa.get(0));
        }
        if (_parent != null) {
            this.parent = new Place((JSONObject)_parent);
        }
        final Object bbox = _place.opt("bounding_box");
        if (bbox instanceof JSONObject) {
            final List<Location> bb = this.parseCoords((JSONObject)bbox);
            double n = -90.0;
            double e = -180.0;
            double s = 90.0;
            double w = 180.0;
            for (final Location ll : bb) {
                n = Math.max(ll.latitude, n);
                s = Math.min(ll.latitude, s);
                e = Math.max(ll.longitude, e);
                w = Math.min(ll.longitude, w);
            }
            this.boundingBox = new BoundingBox(new Location(n, e), new Location(s, w));
        }
        final Object geo = _place.opt("geometry");
        if (geo instanceof JSONObject) {
            this.geometry = this.parseCoords((JSONObject)geo);
        }
    }
    
    @Override
    public BoundingBox getBoundingBox() {
        return this.boundingBox;
    }
    
    public String getCountryCode() {
        return this.countryCode;
    }
    
    @Override
    public String getCountryName() {
        return this.country;
    }
    
    public List<Location> getGeometry() {
        return this.geometry;
    }
    
    public String getId() {
        return this.id;
    }
    
    public String getInfoUrl() {
        return "http://api.twitter.com/1/geo/id/" + this.id + ".json";
    }
    
    @Override
    public String getName() {
        return this.name;
    }
    
    @Override
    public String getType() {
        return this.type;
    }
    
    private List<Location> parseCoords(final JSONObject bbox) throws JSONException {
        JSONArray coords = bbox.getJSONArray("coordinates");
        coords = coords.getJSONArray(0);
        final List<Location> coordinates = new ArrayList<Location>();
        for (int i = 0, n = coords.length(); i < n; ++i) {
            final JSONArray pt = coords.getJSONArray(i);
            final Location x = new Location(pt.getDouble(1), pt.getDouble(0));
            coordinates.add(x);
        }
        return coordinates;
    }
    
    @Override
    public String toString() {
        return this.getName();
    }
    
    @Override
    public Location getCentroid() {
        if (this.boundingBox == null) {
            return null;
        }
        return this.boundingBox.getCenter();
    }
    
    @Override
    public String getUID() {
        return String.valueOf(this.id) + "@twitter";
    }
}
