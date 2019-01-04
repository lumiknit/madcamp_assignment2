package io.madcamp.jh.madcamp_assignment2;

import org.json.*;

public class Contact {
    public String _id;
    public String name;
    public String phoneNumber;

    public Contact(String _id, String name, String phoneNumber) {
        this._id = _id;
        this.name = name;
        this.phoneNumber = phoneNumber;
    }

    public JSONObject toJSONObject(boolean includeId) {
        try {
            JSONObject obj = new JSONObject();
            if (includeId) {
                obj.put("_id", _id);
            }
            obj.put("name", name);
            obj.put("phoneNumber", phoneNumber);
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

    public static Contact fromJSON(JSONObject obj) {
        Contact contact = new Contact(null, null, null);
        try {
            contact._id = obj.getString("_id");

        } catch(JSONException e) {
            contact._id = null;
        }
        try {
            contact.name = obj.getString("name");
            contact.phoneNumber = obj.getString("phoneNumber");
        } catch(JSONException e) {
            return null;
        }
        return contact;
    }
}
