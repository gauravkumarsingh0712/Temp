package com.ncsavault.alabamavault.network;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.ncsavault.alabamavault.globalconstants.GlobalConstants;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;


/**
 * Created by gauravkumar.singh on 27/04/16.
 */
public class POSTJsonRequest extends JsonObjectRequest {

    public POSTJsonRequest(String url, String jsonData, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener) {

        super(Method.POST, url, jsonData, listener, errorListener);
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
