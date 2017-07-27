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
        Set<String> products = shre.getStringSet("products", new ArraySet<String>());
        products.remove(""+p.getId());
        edit.remove(p.getId()+"_title");
        edit.remove(p.getId()+"_price");
        edit.remove(p.getId()+"_code");
        edit.remove(p.getId()+"_quantity");
        edit.remove(p.getId()+"_promotionPrice");
        edit.remove(p.getId()+"_IVA");
        edit.apply();
    }
}
