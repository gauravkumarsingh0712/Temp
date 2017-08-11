package com.ncsavault.alabamavault.network;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.JsonObject;
import com.ncsavault.alabamavault.globalconstants.GlobalConstants;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


/**
 * Created by gauravkumar.singh on 27/04/16.
 */
public class GETJsonObjectRequest extends JsonObjectRequest
{

    public GETJsonObjectRequest(String url, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener)
    {
        super ( url, listener,errorListener);
        System.out.println(">>>> URL :"+url);
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {

        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Content-Type", "application/json");
        headers.put("Accept", "application/json");
        headers.put("appID", String.valueOf(GlobalConstants.APP_ID));
        headers.put("appVersion", GlobalConstants.APP_VERSION);
        headers.put("deviceType", GlobalConstants.DEVICE_TYPE);

        return headers;
    }

}

