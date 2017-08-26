package com.pezzuto.pezzuto.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.TextView;

import com.pezzuto.pezzuto.BottomBuyFragment;
import com.pezzuto.pezzuto.CartActivity;
import com.pezzuto.pezzuto.MainActivity;
import com.pezzuto.pezzuto.R;
import com.pezzuto.pezzuto.SharedUtils;
import com.pezzuto.pezzuto.Statics;
import com.pezzuto.pezzuto.items.Product;
import com.pezzuto.pezzuto.listeners.OnCartInteractionListener;

import java.util.List;
import java.util.Locale;

/**
 * Created by dade on 24/03/17.
 */

public class BuyProductListViewAdapter extends ArrayAdapter<Product> {
    List<Product> prods;
    TextView total;
    TextView imponibile;
    TextView iva;
    String type;
    OnCartInteractionListener mListener;
    public BuyProductListViewAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
    }

    public BuyProductListViewAdapter(Context context, int resource, List<Product> items,
                                     TextView imponibile, TextView iva, TextView total, String type) {
        super(context, resource, items);
        this.total = total;
        this.iva = iva;
        this.imponibile = imponibile;
        prods = items;
        this.type = type;
    }
    public BuyProductListViewAdapter(Context context, int resource, List<Product> items,
                                     TextView imponibile, TextView iva, TextView total, String type, OnCartInteractionListener mListener) {
        super(context, resource, items);
        this.total = total;
        this.iva = iva;
        this.imponibile = imponibile;
        prods = items;
        this.type = type;
        this.mListener = mListener;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;

        if (v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            v = vi.inflate(R.layout.promotion_buy_list_item, null);
        }

        final Product p = getItem(position);

        TextView title = (TextView) v.findViewById(R.id.title);
        final TextView price = (TextView) v.findViewById(R.id.price);
        final TextView textViewQuantity = (TextView) v.findViewById(R.id.quantity);
        final ImageButton delete = (ImageButton) v.findViewById(R.id.removeItem);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedUtils.removeFromCart(getContext(),p);
                prods.remove(p);
                notifyDataSetChanged();
                refreshTotal();
                mListener.adjustListView();
            }
        });

        ImageButton add = (ImageButton) v.findViewById(R.id.add);
        ImageButton remove = (ImageButton) v.findViewById(R.id.remove);
        title.setText(p.getTitle());

        //handle modifying state
        if (p.isModifying()) {
            add.setVisibility(View.GONE);
            remove.setVisibility(View.GONE);
            delete.setVisibility(View.VISIBLE);
        }
        else {
            add.setVisibility(View.VISIBLE);
            remove.setVisibility(View.VISIBLE);
            delete.setVisibility(View.GONE);
        }
        //Refresh quantities
        refreshQuantity(textViewQuantity,p.getQuantity());
        refreshQuantity(price,p);

        //Set and and remove listeners
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                p.add();
                refreshQuantity(price,p);
                SharedUtils.addQuantity(getContext().getSharedPreferences(Statics.SHARED_PREF+"-cart", Context.MODE_PRIVATE),p.getId());
                refreshQuantity(textViewQuantity,p.getQuantity());
                refreshTotal();
            }
        });
        remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (type.equals("cart") && p.getQuantity() > 1) {
                    p.remove();
                    SharedUtils.removeQuantity(getContext().getSharedPreferences(Statics.SHARED_PREF+"-cart", Context.MODE_PRIVATE),p.getId());
                }
                else if (type.equals("promotion")) p.remove();
                refreshQuantity(price,p);
                refreshQuantity(textViewQuantity,p.getQuantity());
                refreshTotal();
            }
        });
        refreshTotal();
        return v;
    }
    public void refreshQuantity(TextView v, Product p) {
        double finalPrice =  Statics.getFinalPrice(getContext(),p)*p.getQuantity();
        v.setText(String.format("%.2f",
                finalPrice)+"€");
    }
    private void refreshQuantity(TextView v, int quantity) {
        v.setText("Quantità: "+quantity);
    }
    public void refreshTotal() {
        double num_imp = 0;
        double num_iva = 0;

        for (Product p : prods) {
            double finalPrice =  Statics.getFinalPrice(getContext(),p)*p.getQuantity();

            num_imp += finalPrice;
            num_iva += finalPrice*p.getIVA()/100;
        }
        imponibile.setText(String.format("%.2f",num_imp)+"€");
        iva.setText("+"+String.format("%.2f",num_iva)+"€");
        total.setText(String.format("%.2f",num_iva+num_imp)+"€");
    }

}
