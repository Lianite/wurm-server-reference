// 
// Decompiled by Procyon v0.5.30
// 

package winterwell.jtwitter.ecosystem;

import java.text.ParseException;
import winterwell.json.JSONException;
import java.util.Iterator;
import java.util.Map;
import java.util.List;
import java.util.Collections;
import winterwell.json.JSONObject;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class TwitterCounterStats
{
    public final String screenName;
    public final Date dateUpdated;
    public final int followDays;
    public final double avgGrowth;
    public final long rank;
    public final ArrayList<DateValue> data;
    static final SimpleDateFormat format;
    static final SimpleDateFormat duformat;
    public final String website;
    
    static {
        format = new SimpleDateFormat("'date'yyyy-MM-dd");
        duformat = new SimpleDateFormat("yyyy-MM-dd");
    }
    
    @Override
    public String toString() {
        if (this.data.isEmpty()) {
            return "TwitterCounterStats[@" + this.screenName + " no data]";
        }
        final Date s = this.data.get(0).date;
        final Date e = this.data.get(this.data.size() - 1).date;
        return "TwitterCounterStats[@" + this.screenName + " " + this.data.size() + " pts from " + s + " to " + e + "]";
    }
    
    TwitterCounterStats(final JSONObject jo) throws JSONException, ParseException {
        this.screenName = jo.getString("username");
        this.dateUpdated = TwitterCounterStats.duformat.parse(jo.getString("date_updated"));
        this.followDays = jo.getInt("follow_days");
        this.avgGrowth = jo.getDouble("average_growth");
        this.website = jo.optString("url");
        this.rank = jo.getLong("rank");
        final Map<String, ?> perdate = jo.getJSONObject("followersperdate").getMap();
        this.data = new ArrayList<DateValue>(perdate.size());
        for (final String key : perdate.keySet()) {
            final Date date = TwitterCounterStats.format.parse(key);
            final int v = (int)perdate.get(key);
            this.data.add(new DateValue(date, v));
        }
        Collections.sort(this.data);
    }
    
    public static final class DateValue implements Comparable<DateValue>
    {
        public final int value;
        public final Date date;
        
        DateValue(final Date date, final int v) {
            this.date = date;
            this.value = v;
        }
        
        @Override
        public String toString() {
            return this.date + ": " + this.value;
        }
        
        @Override
        public int compareTo(final DateValue o) {
            return this.date.compareTo(o.date);
        }
    }
}
