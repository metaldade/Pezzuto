package com.pezzuto.pezzuto.adapter;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.pezzuto.pezzuto.R;
import com.pezzuto.pezzuto.items.Product;

import java.util.List;
import java.util.Locale;

/**
 * Created by dade on 24/03/17.
 */

public class BuyProductListViewAdapter extends ArrayAdapter<Product> {
    public BuyProductListViewAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
    }

    public BuyProductListViewAdapter(Context context, int resource, List<Product> items) {
        super(context, resource, items);
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
        final TextView textViewQuantity = (TextView) v.findViewById(R.id.quantity);
        ImageButton add = (ImageButton) v.findViewById(R.id.add);
        ImageButton remove = (ImageButton) v.findViewById(R.id.remove);
        refreshQuantity(textViewQuantity,p.getQuantity());
        title.setText(p.getTitle());
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                p.add();
                refreshQuantity(textViewQuantity,p.getQuantity());
            }
        });
        remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                p.remove();
                refreshQuantity(textViewQuantity,p.getQuantity());
            }
        });

        return v;
    }
    private void refreshQuantity(TextView v, int quantity) {
        v.setText("Quantità: "+quantity);
    }
}
