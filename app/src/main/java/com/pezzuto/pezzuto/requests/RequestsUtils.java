package com.pezzuto.pezzuto.requests;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.pezzuto.pezzuto.LaravelObjRequest;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by dade on 28/04/17.
 */

public class RequestsUtils {

    //Tipi query base
    public static String PROMOZIONI = "promozioni";
    public static String PRODOTTI = "prodotti";
    public static String CATEGORIE = "categorie";
    public static String EVENTI = "eventi";

    //Filtri
    public static String FILTER_CATEGORIA = "filtro/";
    public static String EVENT_PARTICIPATE = "partecipa";
    public static String NO_FILTER = "no_filter";
    public static String FILTER_PRODOTTI = "";
    public static String NO_OPTIONAL = "";

    //Order types
    public static String PROMOTION_ORDER = "promozioni/ordine";
    public static String PRODUCT_ORDER = "prodotti/ordine";


    private static String BASE_URL = "http://api.pezzuto.net/pezzuto/public/api/";
    public static String BASE_STORAGE_URL = "http://api.pezzuto.net/pezzuto/storage/app/";

    public static void sendRequest(Context context, String TYPE, String FILTER, String optional, Response.Listener<JSONArray> responseListener) {
        if (optional == null) optional = "";
        JsonArrayRequest jsArrRequest;
        if (FILTER.equals(EVENT_PARTICIPATE)) {
            jsArrRequest = new JsonArrayRequest
                    (Request.Method.GET,
                            BASE_URL+EVENTI+"/"+optional+"/"+FILTER ,
                            null, responseListener, new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            // TODO Auto-generated method stub

                        }
                    });
        }
        else if (FILTER.equals(NO_FILTER)) {
            jsArrRequest = new JsonArrayRequest
                    (Request.Method.GET,
                            BASE_URL + TYPE + optional,
                            null, responseListener, new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            // TODO Auto-generated method stub

                        }
                    });
        }
        else {
            jsArrRequest = new JsonArrayRequest
                    (Request.Method.GET,
                            BASE_URL + TYPE +"/"+FILTER+optional,
                            null, responseListener, new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            // TODO Auto-generated method stub

                        }
                    });
            Log.d("link",BASE_URL + TYPE +"/"+FILTER+optional );
        }
// Access the RequestQueue through your singleton class.
        VolleySingleton.getInstance(context).getRequestQueue().add(jsArrRequest);
    }
    public static void sendOrderRequest(final Context context, String type, JSONObject order, Response.Listener<String> response) {
           LaravelObjRequest request =  new LaravelObjRequest(Request.Method.POST, BASE_URL + type, order, response, new Response.ErrorListener() {
               @Override
               public void onErrorResponse(VolleyError error) {
                   error.printStackTrace();
                   Toast.makeText(context,"error: "+error.getLocalizedMessage(),Toast.LENGTH_SHORT).show();
               }
           });
        VolleySingleton.getInstance(context).getRequestQueue().add(request);
    }
    public static void sendProductRequest(final Context context, int id, Response.Listener<JSONObject> response){
        JsonObjectRequest request =  new JsonObjectRequest(BASE_URL+PRODOTTI+"/"+id,
               null,response,new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                // TODO Auto-generated method stub

            }
        });
        VolleySingleton.getInstance(context).getRequestQueue().add(request);
    }
}
