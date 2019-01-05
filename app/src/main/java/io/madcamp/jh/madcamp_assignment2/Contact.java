package io.madcamp.jh.madcamp_assignment2;

import android.util.Log;

import org.json.*;

public class Contact {
    public String _id;
    public String name;
    public String phoneNumber;

    public Contact(String name, String phoneNumber) {
        this(null, name, phoneNumber);
    }

    public Contact(String _id, String name, String phoneNumber) {
        this._id = _id;
        this.name = name;
        this.phoneNumber = phoneNumber;
    }

    public JSONObject toJSONObject(boolean includeId) {
        try {
            JSONObject obj = new JSONObject();
            if (includeId && _id != null) {
                obj.put("_id", _id);
            }
            obj.put("name", name);
            obj.put("phone", phoneNumber);
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

    public void loadFromJSON(JSONObject obj) {
        try {
            if(obj.has("_id"))
                _id = obj.getString("_id");
            if(obj.has("name"))
                name = obj.getString("name");
            if(obj.has("phone"))
                phoneNumber = obj.getString("phone");
        } catch(JSONException e) {
        }
    }

    public static Contact fromJSON(JSONObject obj) {
        Contact contact = new Contact(null, null, null);
        contact.loadFromJSON(obj);
        return contact;
    }
}
