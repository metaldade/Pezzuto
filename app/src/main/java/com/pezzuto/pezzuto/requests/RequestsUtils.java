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
import com.android.volley.toolbox.StringRequest;
import com.pezzuto.pezzuto.LaravelObjRequest;

import org.json.JSONArray;
import org.json.JSONException;
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
    public static String CERCA = "cerca";
    public static String PARTECIPA = "partecipa";
    public static String NO_MORE = "nomore";

    //Filtri
    public static String FILTER_CATEGORIA = "filtro/";
    public static String EVENT_PARTICIPATE = "partecipa";
    public static String NO_FILTER = "no_filter";
    public static String FILTER_PRODOTTI = "";
    public static String NO_OPTIONAL = "";

    //Order types
    public static String PROMOTION_ORDER = "promozioni/ordine";
    public static String PRODUCT_ORDER = "prodotti/ordine";


    //Urls
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
    public static void sendOrderRequest(final Context context, String type, JSONObject order, Response.Listener<String> response, Response.ErrorListener error) {
           LaravelObjRequest request =  new LaravelObjRequest(Request.Method.POST, BASE_URL + type, order, response, error);
        VolleySingleton.getInstance(context).getRequestQueue().add(request);
    }

    public static void sendSearchRequest(final Context context, JSONObject search, Response.Listener<JSONArray> response, Response.ErrorListener error){
        CustomRequest request =  new CustomRequest(Request.Method.POST,BASE_URL+CERCA,search,response,error);
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
    public static void sendEventRequest(final Context context, JSONObject eventRequest,
                                        Response.Listener<String> response, Response.ErrorListener error, boolean isParticipating) {
        LaravelObjRequest request =  new LaravelObjRequest(Request.Method.POST, BASE_URL + EVENTI+"/"+(isParticipating ? PARTECIPA : NO_MORE), eventRequest, response, error);
        VolleySingleton.getInstance(context).getRequestQueue().add(request);
    }
    public static void sendCodeRequest(final Context context, String code, Response.Listener<String> response, Response.ErrorListener error) {
        JSONObject reqJSON = new JSONObject();
        try {
            reqJSON.put("codice",code);
        }
        catch (JSONException ex) { ex.printStackTrace(); }
        LaravelObjRequest request =  new LaravelObjRequest(Request.Method.POST, BASE_URL + "codice", reqJSON, response, error);
        VolleySingleton.getInstance(context).getRequestQueue().add(request);
    }
    public static void sendOrariRequest(final Context context, Response.Listener<JSONArray> response, Response.ErrorListener error) {
        JsonArrayRequest request = new JsonArrayRequest
                (Request.Method.GET,
                        BASE_URL + "orari",
                        null, response,error);
        VolleySingleton.getInstance(context).getRequestQueue().add(request);
    }
}
