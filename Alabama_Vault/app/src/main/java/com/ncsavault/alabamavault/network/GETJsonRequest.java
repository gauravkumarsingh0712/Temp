package com.ncsavault.alabamavault.network;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonArrayRequest;
import com.ncsavault.alabamavault.globalconstants.GlobalConstants;
import org.json.JSONArray;
import java.util.HashMap;
import java.util.Map;


/**
 * Created by gauravkumar.singh on 27/04/16.
 */
public class GETJsonRequest extends JsonArrayRequest
{

    public GETJsonRequest(String url, Response.Listener<JSONArray> listener, Response.ErrorListener errorListener)
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

