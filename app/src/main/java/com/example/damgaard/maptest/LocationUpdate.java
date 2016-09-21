package com.example.damgaard.maptest;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class LocationUpdate extends StringRequest {
    private static final String REGISTER_REQUEST_URL = "http://users-cs.au.dk/tfhj93/App/locationRegisterUpdateTest.php";
    private Map<String, String> params;

    public LocationUpdate(String latitude, String longitude, String userID, Response.Listener<String> listener) {
        super(Method.POST, REGISTER_REQUEST_URL, listener, null);
        params = new HashMap<>();
        params.put("latitude", latitude);
        params.put("longitude", longitude);
        params.put("userID", userID);
    }

    @Override
    public Map<String, String> getParams() {
        return params;
    }
}