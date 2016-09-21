package com.example.damgaard.maptest;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class RegisterRequest extends StringRequest {
    private static final String REGISTER_REQUEST_URL = "http://users-cs.au.dk/tfhj93/App/Register-test.php";
    private Map<String, String> params;

    public RegisterRequest(String name, String studentID, String password, String email, String education, Response.Listener<String> listener) {
        super(Method.POST, REGISTER_REQUEST_URL, listener, null);
        params = new HashMap<>();
        params.put("name", name);
        params.put("studentID", studentID + "");
        params.put("password", password);
        params.put("email", email);
        params.put("education", education);
    }

    @Override
    public Map<String, String> getParams() {
        return params;
    }
}
