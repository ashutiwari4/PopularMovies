package ashutosh.nanodegree.beans;

import android.text.Html;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by ashutosh on 16/2/16.
 */
public class SuperModel {

    public static JSONObject createJsonFromSring(String s) {
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(s);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    public static int getInt(JSONObject jsonObject, String key) {
        int value = 0;
        try {
            if (jsonObject != null)
                value = jsonObject.getInt(key);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return value;
    }

    public static float getFloat(JSONObject jsonObject, String key) {
        float value = 0;
        try {
            if (jsonObject != null)
                value = (float) jsonObject.getDouble(key);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return value;
    }

    public static String getString(JSONObject jsonObject, String key) {
        String value = "";
        try {
            if (jsonObject != null)
                if (jsonObject.has(key)) {
                    String temp = Html.fromHtml(jsonObject.getString(key).trim()).toString().trim();
                    value = temp == null ? "" : temp;
                }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return value;
    }

    public static String getString(JSONArray ja, int index) {
        try {
            return ja.getString(index);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static JSONObject getJObject(JSONObject jsonObject, String key) {
        JSONObject jo = null;
        try {
            jo = jsonObject.getJSONObject(key);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jo;
    }

    public static JSONObject getJObject(JSONArray jsonArray, int index) {
        JSONObject jo = null;
        try {
            jo = jsonArray.getJSONObject(index);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jo;
    }


    public static JSONArray getJArray(JSONObject jsonObject, String key) {
        if (jsonObject.has(key)) {
            try {
                return jsonObject.getJSONArray(key);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    protected static boolean getBoolean(JSONObject profile, String key) {
        boolean value = false;
        try {
            if (profile != null)
                value = profile.getBoolean(key);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return value;
    }

}
