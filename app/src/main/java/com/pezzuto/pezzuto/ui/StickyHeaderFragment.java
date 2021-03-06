/*
 * Copyright 2014 Eduardo Barrenechea
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.pezzuto.pezzuto.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.pezzuto.pezzuto.MainActivity;
import com.pezzuto.pezzuto.ParseUtils;
import com.pezzuto.pezzuto.PromotionDetailFragment;
import com.pezzuto.pezzuto.R;
import com.pezzuto.pezzuto.SharedUtils;
import com.pezzuto.pezzuto.Statics;
import com.pezzuto.pezzuto.adapter.StickyEventAdapter;
import com.pezzuto.pezzuto.adapter.StickyProdAdapter;
import com.pezzuto.pezzuto.adapter.StickyTestAdapter;
import com.pezzuto.pezzuto.items.Event;
import com.pezzuto.pezzuto.items.PezzutoObject;
import com.pezzuto.pezzuto.items.Product;
import com.pezzuto.pezzuto.items.Promprod;
import com.pezzuto.pezzuto.listeners.OnFragmentInteractionListener;
import com.pezzuto.pezzuto.requests.RequestsUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.TreeMap;

import ca.barrenechea.widget.recyclerview.decoration.StickyHeaderDecoration;

public class StickyHeaderFragment extends BaseDecorationFragment implements RecyclerView.OnItemTouchListener {

    private StickyHeaderDecoration decor;
    private static int NO_FILTER = -1;
    private OnFragmentInteractionListener mListener;
    private List<Promprod> promprods = new ArrayList<>();
    private List<Product> products = new ArrayList<>();
    private List<Event> events = new ArrayList<>();
    private List<Product> prods = new ArrayList<>();
    private TreeMap<String,List<Promprod>> catPromprods = new TreeMap<>();
    private StickyTestAdapter adapterProm;
    private ArrayList<String> categories;
    private StickyProdAdapter adapterProd;
    private StickyEventAdapter adapterEvent;
    private String scope, query;
    private String type;
    private int category;
    @Override
    protected void setAdapterAndDecor(RecyclerView list) {

        adapterProm = new StickyTestAdapter(this.getActivity(),promprods,mListener);
        adapterProd = new StickyProdAdapter(this.getActivity(),prods,mListener);
        adapterEvent = new StickyEventAdapter(this.getActivity(),events,mListener);
        if (type.equals(MainActivity.PROMOTIONS)) {
            decor = new StickyHeaderDecoration(adapterProm);
            getActivity().setTitle("Promozioni");
            list.setAdapter(adapterProm);
        }
        else if (type.equals(MainActivity.PRODUCTS)) {
            decor = new StickyHeaderDecoration(adapterProd);
            getActivity().setTitle("Prodotti");
            list.setAdapter(adapterProd);
        }
        else if (type.equals(MainActivity.EVENTS)) {
            decor = new StickyHeaderDecoration(adapterEvent);
            getActivity().setTitle("Eventi");
            list.setAdapter(adapterEvent);
        }
        setHasOptionsMenu(true);
        list.addItemDecoration(decor, 1);
        list.addOnItemTouchListener(this);
    }
    public void sendPromotionRequest() {
        RequestsUtils.sendRequest(getContext(),RequestsUtils.PROMOZIONI,RequestsUtils.NO_FILTER,null,parsePromResponse);
    }
    public void sendProductRequest() {
        RequestsUtils.sendRequest(getContext(),RequestsUtils.PRODOTTI,RequestsUtils.NO_FILTER,"",parseProdResponse);
    }
    public void sendFilteredProductRequest() {
        if (category == 0) sendProductRequest();
        else RequestsUtils.sendRequest(getContext(),RequestsUtils.PRODOTTI,RequestsUtils.FILTER_CATEGORIA,""+category,parseProdFilter);
    }
    public void sendEventRequest() {
        RequestsUtils.sendRequest(getContext(), RequestsUtils.EVENTI, RequestsUtils.NO_FILTER, null, parseEventResponse);
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        catPromprods.clear();
        promprods.clear();
        prods.clear();
        products.clear();
        checkType();
    }
    public void checkType() {
        Bundle args = getArguments();
        type = args.getString("type");
        category = args.getInt("category",NO_FILTER);
        scope = args.getString("scope",null);
        query = args.getString("query",null);
        if (type.equals(MainActivity.PROMOTIONS)) {
            sendPromotionRequest();
            getActivity().setTitle("Promozioni");
            mListener.setFabVisible(false);
        }
        else if (type.equals(MainActivity.PRODUCTS)) {
            if (category != NO_FILTER) {
                sendFilteredProductRequest();
            }
            else if (scope != null) {
                sendSearchProdRequest(scope,query);
            }
            else {
                sendProductRequest();
            }
            getActivity().setTitle("Prodotti");
            mListener.setProdFab();
        }
        else if (type.equals(MainActivity.EVENTS)) {
            getActivity().setTitle("Eventi");
            mListener.setFabVisible(false);
            sendEventRequest();
        }
    }

    public void refresh() {
        if (type.equals(MainActivity.PROMOTIONS)) {
            sendPromotionRequest();

        }
        else if (type.equals(MainActivity.PRODUCTS)) {
            if (category != NO_FILTER) {
                sendFilteredProductRequest();
            } else {
                sendProductRequest();
            }
        }
        if (type.equals(MainActivity.EVENTS)) {
            sendEventRequest();

        }
    }
    public static StickyHeaderFragment newInstance(String type) {
        StickyHeaderFragment f = new StickyHeaderFragment();
        // Supply index input as an argument.
        Bundle args = new Bundle();
        args.putString("type", type);
        f.setArguments(args);
        return f;
    }

    final Response.Listener<JSONArray> parseEventResponse = new Response.Listener<JSONArray>() {
        @Override
        public void onResponse(JSONArray response) {
            try {
                JSONArray eventsResp = response.getJSONObject(0).getJSONArray("eventi");
                events.clear();
                if (response.length() == 0) mListener.setEmptyState(MainActivity.EVENTS);
                else mListener.removeEmptyState();
              for (int i = 0; i < eventsResp.length(); i++) {
                    JSONObject event = eventsResp.getJSONObject(i);
                    Event e = parseEvent(event);
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(e.getStartDate());
                    String month = cal.getDisplayName(Calendar.MONTH,Calendar.LONG,Locale.ITALIAN);
                    e.setMonth(month);
                    events.add(e);
                }
                adapterEvent.notifyDataSetChanged();
                mListener.stopRefresh();
                SharedUtils.saveOrari(getContext(),response.getJSONObject(1).getJSONArray("orari"));
            }
            catch(JSONException e) { e.printStackTrace(); }
        }
    };

    public Event parseEvent(JSONObject event) throws JSONException {
     Event e = new Event(event.getInt("id"),
             event.getString("nome"),
             event.getString("descrizione_completa"),
             event.getString("descrizione_corta"),
             Statics.parseSimpleDate(event.getString("data_inizio")),
             event.getString("data_fine") == null ? null : Statics.parseSimpleDate(event.getString("data_fine")),
             event.getString("luogo"),
             event.getInt("partecipanti"),
             event.getString("img")
             );
        return e;
    }
    //filter by category
    public static StickyHeaderFragment newProdInstance(int category) {
        StickyHeaderFragment f = new StickyHeaderFragment();
        // Supply index input as an argument.
        Bundle args = new Bundle();
        //category filter
        args.putInt("category", category);
        args.putString("type", "products");
        f.setArguments(args);
        return f;
    }
    public static StickyHeaderFragment newProdInstance(String scope, String query) {
        StickyHeaderFragment f = new StickyHeaderFragment();
        // Supply index input as an argument.
        Bundle args = new Bundle();
        //category filter
        args.putString("scope", scope);
        args.putString("query", query);
        args.putString("type", "products");
        f.setArguments(args);
        return f;
    }
    Response.Listener<JSONArray> parsePromResponse = new Response.Listener<JSONArray>() {
        @Override
        public void onResponse(JSONArray response) {
            promprods.clear();
            prods.clear();
            catPromprods.clear();
            mListener.stopRefresh();
            try {
                JSONArray arr = ((JSONObject) response.get(0)).getJSONArray("promozioni");
                if (arr.length() == 0) mListener.setEmptyState(MainActivity.PROMOTIONS);
                else mListener.removeEmptyState();
                promprods.addAll(ParseUtils.parsePromotions(arr));
                adapterProm.notifyDataSetChanged();

            } catch (JSONException e) { e.printStackTrace(); }
        }
    };
    Response.Listener<JSONArray> parseProdResponse = new Response.Listener<JSONArray>() {
        @Override
        public void onResponse(JSONArray response) {
            prods.clear();
            mListener.stopRefresh();
            prods.clear();
            try {
                JSONArray arr = ((JSONObject) response.get(0)).getJSONArray("prodotti");
                if (arr.length() == 0) mListener.setEmptyState(MainActivity.PRODUCTS);
                else mListener.removeEmptyState();
                products = ParseUtils.parseProducts(arr);
            }
            catch (JSONException e) { e.printStackTrace(); }
            //get featured
            for (Product p : products) {
                if (p.isFeatured()) {
                    p.setLabel("In primo piano");
                    prods.add(p);
                }
            }

            //category division
            TreeMap<String,List<Product>> catProducts = new TreeMap<>();
            for (Product p : products) {
                if (p.isFeatured()) continue;
                String category = p.getCategory();
                List<Product> pros = new ArrayList<>();
                if (catProducts.containsKey(category)) pros = catProducts.get(category);
                pros.add(p);
                p.setLabel(category);
                catProducts.put(category,pros);
            }

            //insert in adapter
            for (String cat : catProducts.keySet())
                for (Product p : catProducts.get(cat)) {
                    prods.add(p);
                }

            adapterProd.notifyDataSetChanged();
        }


        };
    Response.Listener<JSONArray> parseProdFilter = new Response.Listener<JSONArray>() {
        @Override
        public void onResponse(JSONArray response) {
            prods.clear();
            mListener.stopRefresh();
            try {
                prods.clear();
                String category = response.getJSONObject(0).getJSONArray("prodotti").getJSONObject(0)
                .getJSONObject("categoria").getString("nome");
                Log.d("category",category);
                JSONArray products = response.getJSONObject(0).getJSONArray("prodotti");
                if (products.length() == 0) mListener.setEmptyState(MainActivity.PROD_FILTER);
                else mListener.removeEmptyState();
                List<Product> prodsList = ParseUtils.parseProducts(products);
                for (Product p : prodsList) {
                    p.setLabel(category);
                    p.setCategory(category);
                }
                prods.clear();
                prods.addAll(prodsList);
                adapterProd.notifyDataSetChanged();
            } catch (JSONException e) { e.printStackTrace(); }
        }
    };

public PezzutoObject getRelatedObject() {
    return null;
}
public Bitmap getImageBitmap() {
    return null;
}
public void sendSearchProdRequest(String scope, String query) {
    RequestsUtils.sendSearchRequest(getContext(), createSearchJSON(scope, query), new Response.Listener<JSONArray>() {
        @Override
        public void onResponse(JSONArray response) {
            Log.d("response",response.toString());
            try {
                JSONArray pr = response.getJSONObject(0).getJSONArray("prodotti");
                prods.clear();
                List<Product> products = ParseUtils.parseProducts(pr);
                if (pr.length() == 0) mListener.setEmptyState(MainActivity.PROD_SEARCH);
                //category division
                TreeMap<String,List<Product>> catProducts = new TreeMap<>();
                for (Product p : products) {
                    String category = p.getCategory();
                    List<Product> pros = new ArrayList<>();
                    if (catProducts.containsKey(category)) pros = catProducts.get(category);
                    pros.add(p);
                    p.setLabel(category);
                    catProducts.put(category,pros);
                }

                //insert in adapter
                for (String cat : catProducts.keySet())
                    for (Product p : catProducts.get(cat)) {
                        prods.add(p);
                    }


                adapterProd.notifyDataSetChanged();
            }
            catch (JSONException ex) {
                ex.printStackTrace();
            }
        }
    }, new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            error.printStackTrace();
        }
    });
}
    public JSONObject createSearchJSON(String scope, String query) {
        JSONObject request = new JSONObject();
        try {
            JSONObject ricerca = new JSONObject();
            ricerca.put("scope", scope);
            ricerca.put("query",query);
            request.put("ricerca",ricerca);
        }

        catch (JSONException ex ) {}
        return request;
    }

    @Override
    public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
        // really bad click detection just for demonstration purposes
        // it will not allow the list to scroll if the swipe motion starts
        // on top of a header
        View v = rv.findChildViewUnder(e.getX(), e.getY());

        return v == null;
//        return rv.findChildViewUnder(e.getX(), e.getY()) != null;
    }

    @Override
    public void onTouchEvent(RecyclerView rv, MotionEvent e) {
        // only use the "UP" motion event, discard all others
        if (e.getAction() != MotionEvent.ACTION_UP) {
            return;
        }

        // find the header that was clicked
        View view = decor.findHeaderViewUnder(e.getX(), e.getY());

    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
        // do nothing
    }
    public String getType() { return type; }
    public boolean hasEmptySet(String type) {
        switch (type) {
            case MainActivity.PROMOTIONS:
                return promprods.isEmpty();
            case MainActivity.PRODUCTS:
                return prods.isEmpty();
            case MainActivity.EVENTS:
                return events.isEmpty();
            default:
              return false;
        }
    }
}
