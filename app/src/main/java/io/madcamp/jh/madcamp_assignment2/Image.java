package io.madcamp.jh.madcamp_assignment2;

import android.net.Uri;

import com.google.android.gms.maps.model.LatLng;

import org.json.*;

import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;

public class Image {
    public String _id;
    public String fb_id;
    public String tag;
    public String name;
    public long date;
    public LatLng latLng;
    public int like;
    public Uri uri;

    public Image() {
        _id = null;
        fb_id = null;
        name = null;
        date = 0;
        tag = null;
        latLng = null;
        like = 0;
        uri = null;
    }

    public JSONObject toJSONObject(boolean includeId) {
        try {
            JSONObject obj = new JSONObject();
            if (includeId && _id != null) {
                obj.put("_id", _id);
            } else {
                _id = null;
            }
            if(fb_id != null)
                obj.put("fb_id", fb_id);
            if(name != null)
                obj.put("name", name);
            obj.put("date", date);
            if(latLng != null) {
                obj.put("lat", latLng.latitude);
                obj.put("lng", latLng.longitude);
            }
            obj.put("like", like);
            return obj;
        } catch(JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String toJSON(boolean includeId) {
        JSONObject obj = toJSONObject(includeId);
        if(obj != null) {
            return obj.toString();
        } else {
            return null;
        }
    }

    public static Image fromJSON(JSONObject obj) {
        return (new Image()).loadFromJSON(obj);
    }

    public Image loadFromJSON(JSONObject obj) {
        try {
            if(obj.has("_id"))
                _id = obj.getString("_id");
            if(obj.has("fb_id"))
                fb_id = obj.getString("fb_id");
            if(obj.has("name"))
                name = obj.getString("name");
            if(obj.has("date"))
                date = obj.getLong("date");
            if(obj.has("lat") && obj.has("lng"))
                latLng = new LatLng(obj.getDouble("lat"), obj.getDouble("lng"));
            if(obj.has("like"))
                like = obj.getInt("like");
            if(obj.has("uri"))
                uri = Uri.parse(obj.getString("url"));
        } catch(JSONException e) {
            return null;
        }
        return this;
    }

    public String getDateAsString() {
        return new SimpleDateFormat("yy.MM.dd HH:mm:ss").format(new Date(date));
    }

    public String getLikeAsString() {
        String s = "마음에 들었다냥";
        if(like > 0) s += "(" + like + ")";
        return s;
    }

    public void updateTag() {
        tag = getDateAsString();
        if(name != null) {
            tag = name + ", " + tag;
        }
    }


    public static Comparator<Image> cmpDate = new Comparator<Image>() {
        @Override
        public int compare(Image o1, Image o2) {
            return new Long(o2.date).compareTo(o1.date);
        }
    };

    public static Comparator<Image> cmpLike = new Comparator<Image>() {
        @Override
        public int compare(Image o1, Image o2) {
            if(o1.like == o2.like) {
                return cmpDate.compare(o1, o2);
            } else {
                return new Integer(o2.like).compareTo(o1.like);
            }
        }
    };
}
