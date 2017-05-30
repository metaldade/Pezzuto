package com.pezzuto.pezzuto;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by dade on 24/05/17.
 */

public class LaravelObjRequest extends StringRequest {
    private JSONObject body;
    public LaravelObjRequest(int type, String url, JSONObject body, Response.Listener<String> acc,Response.ErrorListener error) {
        super(type,url,acc,error);
        this.body = body;
    }
    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        Map<String, String> headersSys = super.getHeaders();
        Map<String, String> headers = new HashMap<String, String>();
        headersSys.remove("Content-Type");
        headers.put("Content-Type", "application/json");
        headers.putAll(headersSys);
        return headers;
    }
    @Override
    public String getBodyContentType() {
        return "application/json; charset=utf-8";
    }

    @Override
    public byte[] getBody() throws AuthFailureError {
        byte[] bytes;
        try {
           bytes = body.toString().getBytes("utf-8");
        } catch (UnsupportedEncodingException uee) {
            VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", body.toString(), "utf-8");
            return null;
        }
        return bytes;
    }

    @Override
    protected Response<String> parseNetworkResponse(NetworkResponse response) {
        String responseString = "";
        if (response != null) {
            responseString = String.valueOf(response.statusCode);

        }
        return Response.success(responseString, HttpHeaderParser.parseCacheHeaders(response));
    }
}
