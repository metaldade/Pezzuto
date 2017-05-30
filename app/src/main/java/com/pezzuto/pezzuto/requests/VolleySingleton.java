package com.pezzuto.pezzuto.requests;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by generoso on 06/10/16.
 */

public class VolleySingleton {

    private RequestQueue _requestQueue;
    private static VolleySingleton _instance = null;

    private VolleySingleton(Context context){
        this._requestQueue = Volley.newRequestQueue(context);
    }

    public static VolleySingleton getInstance(Context context){
        if(_instance==null)
            _instance = new VolleySingleton(context);
        return  _instance;
    }

    public RequestQueue getRequestQueue(){
        return _requestQueue;
    }

}
