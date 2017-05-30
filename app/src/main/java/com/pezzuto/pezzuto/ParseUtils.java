package com.pezzuto.pezzuto;

import android.util.Log;

import com.pezzuto.pezzuto.items.Product;
import com.pezzuto.pezzuto.items.Promprod;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

/**
 * Created by dade on 18/05/17.
 */

public class ParseUtils {
    public static Product parseProduct(JSONObject obj) throws JSONException {
        Product p = new Product(obj.getInt("id"),
                obj.getString("codice"),
                obj.has("categoria") ? obj.getJSONObject("categoria").getString("nome") : "",
                obj.getString("nome"),
                obj.getString("marca"),
                obj.getDouble("prezzo"),
                obj.getString("unita_di_misura"),
                obj.getString("thumbnail"),
                obj.getString("img"),
                obj.getString("descrizione"),
                obj.getInt("featured") == 1,
                obj.getInt("id_iva") == 1 ? 10 : 22
        );
        if (obj.has("pivot")) p.setPromotionPrice(obj.getJSONObject("pivot").getDouble("prezzo_promozione"));
        return p;
    }
    public static List<Product> parseProducts(JSONArray arr) throws JSONException {
        List<Product> products = new ArrayList<>();
        for (int i = 0; i < arr.length(); i++) {
            JSONObject obj = (JSONObject) arr.get(i);
            Product p = parseProduct(obj);
            products.add(p);
        }
        return products;
    }
    public static List<Promprod> parsePromotions(JSONArray arr) throws JSONException {
        TreeMap<String,List<Promprod>> catPromprods = new TreeMap<>();
        List<Promprod> promprods = new ArrayList<>();
        for (int i = 0; i < arr.length(); i++) {
            JSONObject obj = (JSONObject) arr.get(i);
            String category = obj.getJSONObject("categoria").getString("nome");
            Promprod p = parsePromotion(obj);
            p.setLabel(category);
            Log.d("asd",obj.getString("nome"));

            p.setProducts(ParseUtils.parseProducts(obj.getJSONArray("prodotti")));

            //Add element in the same category list
            List<Promprod> pros;
            if (catPromprods.containsKey(category)) pros = catPromprods.get(category);
            else pros = new ArrayList<>();
            pros.add(p);
            catPromprods.put(category,pros);
        }
        for (String cat : catPromprods.keySet())
            for (Promprod p : catPromprods.get(cat)) {
                Log.d("ss",p.getCategory());
                promprods.add(p);
            }
            return promprods;
    }
    public static Promprod parsePromotion(JSONObject obj) throws JSONException {
        Promprod p = new Promprod(obj.getInt("id"),
                obj.getJSONObject("categoria").getString("nome"),
                obj.getString("nome"),
                obj.getString("descrizione"),
                obj.getString("img"),
                Statics.parseSimpleDate(obj.getString("valida_dal")),
                Statics.parseSimpleDate(obj.getString("valida_al")),
                obj.getInt("esaurimento_scorte") == 0
        );
        return p;
    }

}
