// 
// Decompiled by Procyon v0.5.30
// 

package winterwell.jtwitter;

import winterwell.json.JSONObject;
import java.util.ArrayList;
import winterwell.json.JSONException;
import winterwell.json.JSONArray;
import java.util.Collections;
import java.util.List;
import java.net.URI;
import java.util.Date;
import java.io.Serializable;

public final class User implements Serializable
{
    private static final long serialVersionUID = 1L;
    public final Date createdAt;
    public final String description;
    public final int favoritesCount;
    private final Boolean followedByYou;
    public int followersCount;
    private final Boolean followingYou;
    public final boolean followRequestSent;
    public final int friendsCount;
    public final Long id;
    String lang;
    public final int listedCount;
    public final String location;
    public final String name;
    public final boolean notifications;
    private Place place;
    public final String profileBackgroundColor;
    public final URI profileBackgroundImageUrl;
    public final boolean profileBackgroundTile;
    public URI profileImageUrl;
    public final String profileLinkColor;
    public final String profileSidebarBorderColor;
    public final String profileSidebarFillColor;
    public final String profileTextColor;
    public final boolean protectedUser;
    public final String screenName;
    public final Status status;
    public final int statusesCount;
    public final String timezone;
    public final double timezoneOffSet;
    public final boolean verified;
    public final URI website;
    
    static List<User> getUsers(final String json) throws TwitterException {
        if (json.trim().equals("")) {
            return Collections.emptyList();
        }
        try {
            final JSONArray arr = new JSONArray(json);
            return getUsers2(arr);
        }
        catch (JSONException e) {
            throw new TwitterException.Parsing(json, e);
        }
    }
    
    static List<User> getUsers2(final JSONArray arr) throws JSONException {
        final List<User> users = new ArrayList<User>();
        for (int i = 0; i < arr.length(); ++i) {
            final JSONObject obj = arr.getJSONObject(i);
            final User u = new User(obj, null);
            users.add(u);
        }
        return users;
    }
    
    public String getLang() {
        return this.lang;
    }
    
    User(final JSONObject obj, final Status status) throws TwitterException {
        try {
            this.id = obj.getLong("id");
            this.name = InternalUtils.unencode(InternalUtils.jsonGet("name", obj));
            final String sn = InternalUtils.jsonGet("screen_name", obj);
            this.screenName = (Twitter.CASE_SENSITIVE_SCREENNAMES ? sn : sn.toLowerCase());
            final Object _locn = Status.jsonGetLocn(obj);
            this.location = ((_locn == null) ? null : _locn.toString());
            if (_locn instanceof Place) {
                this.place = (Place)_locn;
            }
            this.lang = InternalUtils.jsonGet("lang", obj);
            this.description = InternalUtils.unencode(InternalUtils.jsonGet("description", obj));
            String img = InternalUtils.jsonGet("profile_image_url", obj);
            this.profileImageUrl = ((img == null) ? null : InternalUtils.URI(img));
            final String url = InternalUtils.jsonGet("url", obj);
            this.website = ((url == null) ? null : InternalUtils.URI(url));
            this.protectedUser = obj.optBoolean("protected");
            this.followersCount = obj.optInt("followers_count");
            this.profileBackgroundColor = InternalUtils.jsonGet("profile_background_color", obj);
            this.profileLinkColor = InternalUtils.jsonGet("profile_link_color", obj);
            this.profileTextColor = InternalUtils.jsonGet("profile_text_color", obj);
            this.profileSidebarFillColor = InternalUtils.jsonGet("profile_sidebar_fill_color", obj);
            this.profileSidebarBorderColor = InternalUtils.jsonGet("profile_sidebar_border_color", obj);
            this.friendsCount = obj.optInt("friends_count");
            final String c = InternalUtils.jsonGet("created_at", obj);
            this.createdAt = ((c == null) ? null : InternalUtils.parseDate(c));
            this.favoritesCount = obj.optInt("favourites_count");
            final String utcOffSet = InternalUtils.jsonGet("utc_offset", obj);
            this.timezoneOffSet = ((utcOffSet == null) ? 0.0 : Double.parseDouble(utcOffSet));
            this.timezone = InternalUtils.jsonGet("time_zone", obj);
            img = InternalUtils.jsonGet("profile_background_image_url", obj);
            this.profileBackgroundImageUrl = ((img == null) ? null : InternalUtils.URI(img));
            this.profileBackgroundTile = obj.optBoolean("profile_background_tile");
            this.statusesCount = obj.optInt("statuses_count");
            this.notifications = obj.optBoolean("notifications");
            this.verified = obj.optBoolean("verified");
            final Object _cons = obj.opt("connections");
            if (_cons instanceof JSONArray) {
                final JSONArray cons = (JSONArray)_cons;
                boolean _following = false;
                boolean _followedBy = false;
                boolean _followRequested = false;
                for (int i = 0, n = cons.length(); i < n; ++i) {
                    final String ci = cons.getString(i);
                    if ("following".equals(ci)) {
                        _following = true;
                    }
                    else if ("followed_by".equals(ci)) {
                        _followedBy = true;
                    }
                    else if ("following_requested".equals(ci)) {
                        _followRequested = true;
                    }
                }
                this.followedByYou = _following;
                this.followingYou = _followedBy;
                this.followRequestSent = _followRequested;
            }
            else {
                this.followedByYou = InternalUtils.getOptBoolean(obj, "following");
                this.followingYou = InternalUtils.getOptBoolean(obj, "followed_by");
                this.followRequestSent = obj.optBoolean("follow_request_sent");
            }
            this.listedCount = obj.optInt("listed_count", -1);
            if (status == null) {
                final JSONObject s = obj.optJSONObject("status");
                this.status = ((s == null) ? null : new Status(s, this));
            }
            else {
                this.status = status;
            }
        }
        catch (JSONException e) {
            throw new TwitterException.Parsing(String.valueOf(obj), e);
        }
        catch (NullPointerException e2) {
            throw new TwitterException(e2 + " from <" + obj + ">, <" + status + ">\n\t" + e2.getStackTrace()[0] + "\n\t" + e2.getStackTrace()[1]);
        }
    }
    
    public User(final String screenName) {
        this(screenName, null);
    }
    
    User(String screenName, final Number id) {
        this.id = ((id == null) ? null : id.longValue());
        this.name = null;
        if (screenName != null && !Twitter.CASE_SENSITIVE_SCREENNAMES) {
            screenName = screenName.toLowerCase();
        }
        this.screenName = screenName;
        this.status = null;
        this.location = null;
        this.description = null;
        this.profileImageUrl = null;
        this.website = null;
        this.protectedUser = false;
        this.followersCount = 0;
        this.profileBackgroundColor = null;
        this.profileLinkColor = null;
        this.profileTextColor = null;
        this.profileSidebarFillColor = null;
        this.profileSidebarBorderColor = null;
        this.friendsCount = 0;
        this.createdAt = null;
        this.favoritesCount = 0;
        this.timezoneOffSet = -1.0;
        this.timezone = null;
        this.profileBackgroundImageUrl = null;
        this.profileBackgroundTile = false;
        this.statusesCount = 0;
        this.notifications = false;
        this.verified = false;
        this.followedByYou = null;
        this.followingYou = null;
        this.followRequestSent = false;
        this.listedCount = -1;
    }
    
    @Override
    public boolean equals(final Object other) {
        if (this == other) {
            return true;
        }
        if (other.getClass() != User.class) {
            return false;
        }
        final User ou = (User)other;
        if (this.screenName != null && ou.screenName != null) {
            return this.screenName.equals(ou.screenName);
        }
        return this.id != null && ou.id != null && this.id == ou.id;
    }
    
    public Date getCreatedAt() {
        return this.createdAt;
    }
    
    public String getDescription() {
        return this.description;
    }
    
    public int getFavoritesCount() {
        return this.favoritesCount;
    }
    
    public int getFollowersCount() {
        return this.followersCount;
    }
    
    public int getFriendsCount() {
        return this.friendsCount;
    }
    
    public Long getId() {
        return this.id;
    }
    
    public String getLocation() {
        return this.location;
    }
    
    public String getName() {
        return this.name;
    }
    
    public Place getPlace() {
        return this.place;
    }
    
    public String getProfileBackgroundColor() {
        return this.profileBackgroundColor;
    }
    
    public URI getProfileBackgroundImageUrl() {
        return this.profileBackgroundImageUrl;
    }
    
    public URI getProfileImageUrl() {
        return this.profileImageUrl;
    }
    
    public String getProfileLinkColor() {
        return this.profileLinkColor;
    }
    
    public String getProfileSidebarBorderColor() {
        return this.profileSidebarBorderColor;
    }
    
    public String getProfileSidebarFillColor() {
        return this.profileSidebarFillColor;
    }
    
    public String getProfileTextColor() {
        return this.profileTextColor;
    }
    
    public boolean getProtectedUser() {
        return this.protectedUser;
    }
    
    public String getScreenName() {
        return this.screenName;
    }
    
    public Status getStatus() {
        return this.status;
    }
    
    public int getStatusesCount() {
        return this.statusesCount;
    }
    
    public String getTimezone() {
        return this.timezone;
    }
    
    public double getTimezoneOffSet() {
        return this.timezoneOffSet;
    }
    
    public URI getWebsite() {
        return this.website;
    }
    
    @Override
    public int hashCode() {
        return this.screenName.hashCode();
    }
    
    public boolean isDummyObject() {
        return this.name == null;
    }
    
    public Boolean isFollowedByYou() {
        return this.followedByYou;
    }
    
    public Boolean isFollowingYou() {
        return this.followingYou;
    }
    
    public boolean isNotifications() {
        return this.notifications;
    }
    
    public boolean isProfileBackgroundTile() {
        return this.profileBackgroundTile;
    }
    
    public boolean isProtectedUser() {
        return this.protectedUser;
    }
    
    public boolean isVerified() {
        return this.verified;
    }
    
    @Override
    public String toString() {
        return this.screenName;
    }
}
