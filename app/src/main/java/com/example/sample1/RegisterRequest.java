package com.example.sample1;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class RegisterRequest extends StringRequest {

    final static private String URL = "http://test2021.dothome.co.kr/UserRegister.php";
    private Map<String, String> maps;

    public RegisterRequest(String userID, String userPassword, String userGender, String userRescueGrade, String userEmail, String userPhoneNumber, String userBirth, Response.Listener<String> listener){
        super(Method.POST, URL, listener, null);
        maps = new HashMap<>();
        maps.put("userID", userID);
        maps.put("userPassword", userPassword);
        maps.put("userGender", userGender);
        maps.put("userRescueGrade", userRescueGrade);
        maps.put("userEmail", userEmail);
        maps.put("userPhoneNumber", userPhoneNumber);
        maps.put("userBirth", userBirth);
    }

    @Override
    public  Map<String, String> getParams() throws AuthFailureError{
        return maps;
    }
}