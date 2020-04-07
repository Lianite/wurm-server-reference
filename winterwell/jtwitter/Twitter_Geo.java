// 
// Decompiled by Procyon v0.5.30
// 

package winterwell.jtwitter;

import com.winterwell.jgeoplanet.IPlace;
import com.winterwell.jgeoplanet.MFloat;
import winterwell.json.JSONArray;
import winterwell.json.JSONException;
import java.util.ArrayList;
import winterwell.json.JSONObject;
import java.util.Map;
import java.util.List;
import com.winterwell.jgeoplanet.IGeoCode;

public class Twitter_Geo implements IGeoCode
{
    private double accuracy;
    private final Twitter jtwit;
    
    Twitter_Geo(final Twitter jtwit) {
        assert jtwit != null;
        this.jtwit = jtwit;
    }
    
    public List geoSearch(final double latitude, final double longitude) {
        throw new RuntimeException();
    }
    
    public List<Place> geoSearch(final String query) {
        final String url = String.valueOf(this.jtwit.TWITTER_URL) + "/geo/search.json";
        final Map vars = InternalUtils.asMap("query", query);
        if (this.accuracy != 0.0) {
            vars.put("accuracy", String.valueOf(this.accuracy));
        }
        final boolean auth = InternalUtils.authoriseIn11(this.jtwit);
        final String json = this.jtwit.getHttpClient().getPage(url, vars, auth);
        try {
            final JSONObject jo = new JSONObject(json);
            final JSONObject jo2 = jo.getJSONObject("result");
            final JSONArray arr = jo2.getJSONArray("places");
            final List places = new ArrayList(arr.length());
            for (int i = 0; i < arr.length(); ++i) {
                final JSONObject _place = arr.getJSONObject(i);
                final Place place = new Place(_place);
                places.add(place);
            }
            return (List<Place>)places;
        }
        catch (JSONException e) {
            throw new TwitterException.Parsing(json, e);
        }
    }
    
    public List geoSearchByIP(final String ipAddress) {
        throw new RuntimeException();
    }
    
    public List<Place> getTrendRegions() {
        final String json = this.jtwit.getHttpClient().getPage(String.valueOf(this.jtwit.TWITTER_URL) + "/trends/available.json", null, false);
        try {
            final JSONArray json2 = new JSONArray(json);
            final List<Place> trends = new ArrayList<Place>();
            for (int i = 0; i < json2.length(); ++i) {
                final JSONObject ti = json2.getJSONObject(i);
                final Place place = new Place(ti);
                trends.add(place);
            }
            return trends;
        }
        catch (JSONException e) {
            throw new TwitterException.Parsing(json, e);
        }
    }
    
    public void setAccuracy(final double metres) {
        this.accuracy = metres;
    }
    
    @Override
    public IPlace getPlace(final String locationDescription, final MFloat confidence) {
        final List<Place> places = this.geoSearch(locationDescription);
        if (places.size() == 0) {
            return null;
        }
        if (places.size() == 1) {
            if (confidence != null) {
                confidence.value = 0.8f;
            }
            return places.get(0);
        }
        return InternalUtils.prefer(places, "city", confidence, 0.8f);
    }
}
