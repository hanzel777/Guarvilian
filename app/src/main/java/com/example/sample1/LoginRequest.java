package com.example.sample1;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class LoginRequest extends StringRequest {

    final static private String URL = "http://test2021.dothome.co.kr/UserLogin.php";
    private Map<String, String> maps;

    public LoginRequest(String userID, String userPassword, String userRescueGrade, Response.Listener<String> listener){
        super(Method.POST, URL, listener, null);
        maps = new HashMap<>();
        maps.put("userID", userID);
        maps.put("userPassword", userPassword);
        maps.put("userRescueGrade", userRescueGrade);
    }

    @Override
    public  Map<String, String> getParams() throws AuthFailureError {
        return maps;
    }
}