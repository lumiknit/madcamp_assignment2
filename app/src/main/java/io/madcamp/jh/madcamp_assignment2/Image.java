package io.madcamp.jh.madcamp_assignment2;

import android.net.Uri;

import com.google.android.gms.maps.model.LatLng;

import org.json.*;

public class Image {
    public String _id;
    public String fb_id;
    public String tag;
    public LatLng latLng;
    public int good, bad;
    public Uri uri;

    public Image() {
        _id = null;
        fb_id = null;
        tag = null;
        latLng = null;
        good = 0;
        bad = 0;
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
                obj.put("name", fb_id);
            if(latLng != null) {
                obj.put("lat", latLng.latitude);
                obj.put("lng", latLng.longitude);
            }
//            obj.put("good", good);
//            obj.put("bad", bad);
//            if(uri != null)
//                obj.put("uri", uri.toString());
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
            if(obj.has("lat") && obj.has("lng"))
                latLng = new LatLng(obj.getDouble("lat"), obj.getDouble("lng"));
            if(obj.has("good"))
                good = obj.getInt("good");
            if(obj.has("bad"))
                bad = obj.getInt("bad");
            if(obj.has("uri"))
                uri = Uri.parse(obj.getString("url"));
        } catch(JSONException e) {
            return null;
        }
        return this;
    }
}
