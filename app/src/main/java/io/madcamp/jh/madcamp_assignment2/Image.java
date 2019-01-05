package io.madcamp.jh.madcamp_assignment2;

import android.net.Uri;

import com.google.android.gms.maps.model.LatLng;

import org.json.*;

public class Image {
    public String _id;
    public String name;
    public LatLng latLng;
    public int good, bad;

    public Uri uri;

    public Image() {
        _id = null;
        name = null;
        latLng = null;
        good = 0;
        bad = 0;
        uri = null;
    }

    public JSONObject toJSONObject(boolean includeId) {
        try {
            JSONObject obj = new JSONObject();
            if (includeId) {
                obj.put("_id", _id);
            } else {
                _id = null;
            }
            obj.put("name", name);
            JSONObject ll = new JSONObject();
            ll.put("latitude", latLng.latitude);
            ll.put("longitude", latLng.longitude);
            obj.put("latLng", ll);
            obj.put("good", good);
            obj.put("bad", bad);
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
        Image image = new Image();
        try {
            image._id = obj.getString("_id");
        } catch(JSONException e) {
            image._id = null;
        }
        try {
            image.name = obj.getString("name");
            JSONObject ll = obj.getJSONObject("latLng");
            image.latLng = new LatLng(ll.getDouble("latitude"), ll.getDouble("longitude"));
            image.good = obj.getInt("good");
            image.bad = obj.getInt("bad");
            image.uri = Uri.parse(obj.getString("url"));
        } catch(JSONException e) {
            return null;
        }
        return image;
    }
}
