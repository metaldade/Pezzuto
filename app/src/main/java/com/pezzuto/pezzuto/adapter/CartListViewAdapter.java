package com.pezzuto.pezzuto.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.media.Image;
import android.support.v4.util.ArraySet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.pezzuto.pezzuto.MainActivity;
import com.pezzuto.pezzuto.R;
import com.pezzuto.pezzuto.Statics;
import com.pezzuto.pezzuto.items.Product;

import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * Created by dade on 24/03/17.
 */

public class CartListViewAdapter extends ArrayAdapter<Product> {
    private SharedPreferences shre;
    SharedPreferences.Editor edit;
    public CartListViewAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
    }

    public CartListViewAdapter(Context context, int resource, List<Product> items) {
        super(context, resource, items);
        shre = context.getSharedPreferences(Statics.SHARED_PREF+"-cart",Context.MODE_PRIVATE);
        edit = shre.edit();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;

        if (v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            v = vi.inflate(R.layout.cart_list_item, null);
        }

        final Product p = getItem(position);

        if (p != null) {
            TextView title = (TextView) v.findViewById(R.id.title);
            final TextView quantity = (TextView) v.findViewById(R.id.quantity);
            TextView price = (TextView) v.findViewById(R.id.price);
            ImageButton remove = (ImageButton) v.findViewById(R.id.remove);

            if (title != null) {
                title.setText(p.getTitle());
            }

            if (price != null) {
                price.setText("Prezzo: "+String.format(Locale.ITALY,"%.2f",p.getPrice()));
            }

            if (quantity != null) {
                quantity.setText("Quantità: "+p.getQuantity());
            }
            if (remove != null) {
                remove.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        removeFromCart(p,quantity);
                    }
                });
            }
        }

        return v;
    }
    public void removeFromCart(Product p,TextView quantity) {
        p.remove();
        if (p.getQuantity() == 0) {
            Set<String> products = shre.getStringSet("products", new ArraySet<String>());
            products.remove(""+p.getId());
            edit.putStringSet("products",products);
            edit.remove(p.getId()+"_title");
            edit.remove(p.getId()+"_price");
            edit.remove(p.getId()+"_quantity");

            edit.apply();
            remove(p);
        }
        else {
            edit.putInt(p.getId()+"_quantity",p.getQuantity());
            edit.apply();
            quantity.setText("Quantità: "+p.getQuantity());
        }
    }
}
