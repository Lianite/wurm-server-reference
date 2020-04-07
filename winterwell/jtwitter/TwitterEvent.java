// 
// Decompiled by Procyon v0.5.30
// 

package winterwell.jtwitter;

import winterwell.json.JSONException;
import winterwell.json.JSONObject;
import java.util.Date;

public class TwitterEvent
{
    public final Date createdAt;
    public final User source;
    public final User target;
    private Object targetObject;
    public final String type;
    
    TwitterEvent(final Date createdAt, final User source, final String type, final User target, final Object targetObject) {
        this.createdAt = createdAt;
        this.source = source;
        this.type = type;
        this.target = target;
        this.targetObject = targetObject;
    }
    
    public TwitterEvent(final JSONObject jo, final Twitter jtwit) throws JSONException {
        this.type = jo.getString("event");
        this.target = new User(jo.getJSONObject("target"), null);
        this.source = new User(jo.getJSONObject("source"), null);
        this.createdAt = InternalUtils.parseDate(jo.getString("created_at"));
        final JSONObject to = jo.optJSONObject("target_object");
        if (to == null) {
            return;
        }
        if (to.has("member_count")) {
            this.targetObject = new TwitterList(to, jtwit);
            return;
        }
        try {
            this.targetObject = new Status(to, null);
        }
        catch (Exception ex) {
            this.targetObject = to;
        }
    }
    
    public Date getCreatedAt() {
        return this.createdAt;
    }
    
    public User getSource() {
        return this.source;
    }
    
    public User getTarget() {
        return this.target;
    }
    
    public Object getTargetObject() {
        return this.targetObject;
    }
    
    public String getType() {
        return this.type;
    }
    
    public boolean is(final String type) {
        return this.type.equals(type);
    }
    
    @Override
    public String toString() {
        return this.source + " " + this.type + " " + this.target + " " + this.getTargetObject();
    }
    
    public interface Type
    {
        public static final String ADDED_TO_LIST = "list_member_added";
        public static final String FAVORITE = "favorite";
        public static final String FOLLOW = "follow";
        public static final String LIST_CREATED = "list_created";
        public static final String REMOVED_FROM_LIST = "list_member_removed";
        public static final String UNFAVORITE = "unfavorite";
        public static final String USER_UPDATE = "user_update";
    }
}
