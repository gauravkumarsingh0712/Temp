package com.ncsavault.alabamavault.network;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.ncsavault.alabamavault.controllers.AppController;
import com.ncsavault.alabamavault.models.LocalModel;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


/**
 * Created by ashishkumarpatel on 27/04/16.
 */
public class PUTJsonRequest extends JsonObjectRequest {
    LocalModel localModel = null;

    public PUTJsonRequest(String url, String jsonData, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener) {

        super(Method.PUT, url, jsonData, listener, errorListener);
        System.out.println(">>>> URL :"+url);
        System.out.println(">>>> DATA : "+jsonData);

        localModel = AppController.getInstance().getModelFacade().getLocalModel();
    }


    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {

        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Content-Type", "application/json; charset=utf-8");
        headers.put("Accept", "application/json; charset=utf-8");

//        if (localModel.getToken() != null) {
//
//            headers.put("Authorization", "Bearer " + localModel.getToken());
//        }

        return headers;
    }

}
