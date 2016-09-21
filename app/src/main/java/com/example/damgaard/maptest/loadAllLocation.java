package com.example.damgaard.maptest;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Damgaard on 16-09-2016.
 */
public class loadAllLocation extends StringRequest {
    private static final String LOGIN_REQUEST_URL = "http://users-cs.au.dk/tfhj93/App/loadAllLocation.php";
    private Map<String, String> params;

    public loadAllLocation(Response.Listener<String> listener) {
        super(Method.POST, LOGIN_REQUEST_URL, listener, null);
    }

    @Override
    public Map<String, String> getParams() {
        return params;
    }
}
