package com.pezzuto.pezzuto;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.util.ArraySet;
import android.widget.Toast;

import com.pezzuto.pezzuto.items.Product;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by dade on 22/07/17.
 */

public class SharedUtils {
    public static List<Product> getProductsFromCart(Context context) {
        SharedPreferences shre = context.getSharedPreferences(Statics.SHARED_PREF+"-cart", Context.MODE_PRIVATE);
        List<Product> prods = new ArrayList<>();
        Set<String> products = shre.getStringSet("products", new ArraySet<String>());
        for (String id : products) {
            prods.add(new Product(
                            Integer.parseInt(id),
                            shre.getString(id+"_title",""),
                            shre.getString(id+"_code",""),
                            (double) shre.getFloat(id+"_price",0),
                            (double) shre.getFloat(id+"_promotionPrice",0),
                            shre.getInt(id+"_IVA",0),
                            shre.getInt(id+"_quantity",0)
                    )
            );
        }
        return prods;
    }
    public static void addQuantity(SharedPreferences shre, int id) {
        SharedPreferences.Editor edit = shre.edit();
        int quantity = shre.getInt(id+"_quantity",0);
        edit.putInt(id+"_quantity",quantity+1);
        edit.apply();
    }
    public static void removeQuantity(SharedPreferences shre, int id) {
        SharedPreferences.Editor edit = shre.edit();
        int quantity = shre.getInt(id+"_quantity",0);
        edit.putInt(id+"_quantity",quantity-1);
        edit.apply();
    }
    public static void addToCart(Context context, Product p) {
        SharedPreferences shre = context.getSharedPreferences(Statics.SHARED_PREF+"-cart", Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = shre.edit();
        Set<String> products = shre.getStringSet("products", new ArraySet<String>());
        products.add(""+p.getId());
        edit.putStringSet("products",products);
        edit.putString(p.getId()+"_title",p.getTitle());
        edit.putFloat(p.getId()+"_promotionPrice",(float) p.getPromotionPrice());
        edit.putInt(p.getId()+"_IVA",p.getIVA());
        edit.putFloat(p.getId()+"_price",(float) p.getPrice());
        edit.putInt(p.getId()+"_quantity",1);
        edit.putString(p.getId()+"_code",p.getCode());
        edit.apply();
    }
    public static boolean isCartEmpty(Context context) {
        SharedPreferences shre = context.getSharedPreferences(Statics.SHARED_PREF+"-cart", Context.MODE_PRIVATE);
        Set<String> products = shre.getStringSet("products", new ArraySet<String>());
        return products.isEmpty();
    }
    public static void removeFromCart(Context context, Product p) {
        SharedPreferences shre = context.getSharedPreferences(Statics.SHARED_PREF+"-cart", Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = shre.edit();
        removeFromCart(context,p.getId());
        edit.apply();
    }
    public static void removeFromCart(Context context, int id) {
        SharedPreferences shre = context.getSharedPreferences(Statics.SHARED_PREF+"-cart", Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = shre.edit();
        Set<String> products = shre.getStringSet("products", new ArraySet<String>());
        products.remove(""+id);
        edit.remove(id+"_title");
        edit.remove(id+"_price");
        edit.remove(id+"_code");
        edit.remove(id+"_quantity");
        edit.remove(id+"_promotionPrice");
        edit.remove(id+"_IVA");
        edit.apply();
    }
    public static void emptyCart(Context context) {
        SharedPreferences shre = context.getSharedPreferences(Statics.SHARED_PREF+"-cart", Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = shre.edit();
        Set<String> products = shre.getStringSet("products", new ArraySet<String>());
        Set<String> prodClone = new ArraySet<>();
        prodClone.addAll(products);
        for (String id : prodClone) {
            removeFromCart(context,Integer.parseInt(id));
        }
        //edit.putStringSet("products", new ArraySet<String>());
    }
    public static void addEvent(Context context, int idEvent, int participants) {
        SharedPreferences shre = context.getSharedPreferences(Statics.SHARED_PREF+"-events", Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = shre.edit();
        Set<String> events = shre.getStringSet("events", new ArraySet<String>());
        events.add(""+idEvent);
        edit.putStringSet("events",events);
        edit.putInt(idEvent+"_participants",participants);
        edit.apply();
    }
    public static boolean removeEvent(Context context, int idEvent) {
        SharedPreferences shre = context.getSharedPreferences(Statics.SHARED_PREF+"-events", Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = shre.edit();
        Set<String> events = shre.getStringSet("events", new ArraySet<String>());
        if (events.contains(""+idEvent)) {
            events.remove(""+idEvent);
            edit.remove(idEvent+"_participants");
            edit.putStringSet("events",events);
            edit.apply();
            return true;
        }
        else return false;
    }
    public static boolean isParticipating(Context context, int idEvent) {
        SharedPreferences shre = context.getSharedPreferences(Statics.SHARED_PREF+"-events", Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = shre.edit();
        Set<String> events = shre.getStringSet("events", new ArraySet<String>());
        if (events.contains(""+idEvent)) return true;
        else return false;
    }
    public static int getParticipants(Context context, int idEvent) {
        SharedPreferences shre = context.getSharedPreferences(Statics.SHARED_PREF+"-events", Context.MODE_PRIVATE);
        return shre.getInt(idEvent+"_participants",0);
    }
    public static boolean isFirstRun(Context context) {
        SharedPreferences shre = context.getSharedPreferences(Statics.SHARED_PREF+"-cart", Context.MODE_PRIVATE);
        return shre.getBoolean("first_run",true);
    }
    public static void noMoreFirstRun(Context context) {
        SharedPreferences shre = context.getSharedPreferences(Statics.SHARED_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = shre.edit();
        edit.putBoolean("first_run", false);
        edit.apply();
    }
    public static boolean isPrivateMember(Context context) {
        SharedPreferences shre = context.getSharedPreferences(Statics.SHARED_PREF, Context.MODE_PRIVATE);
        return shre.getBoolean("private_member",true);
    }
    public static void setPrivateMember(Context context, boolean isPrivate) {
        SharedPreferences shre = context.getSharedPreferences(Statics.SHARED_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = shre.edit();
        edit.putBoolean("private_member",isPrivate);
        edit.apply();
    }
}