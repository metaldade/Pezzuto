package com.pezzuto.pezzuto.adapter;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.pezzuto.pezzuto.R;
import com.pezzuto.pezzuto.items.Product;
import com.pezzuto.pezzuto.items.Promotion;

import java.util.List;
import java.util.Locale;

/**
 * Created by dade on 24/03/17.
 */

public class PromotionListViewAdapter extends ArrayAdapter<Product> {
    public PromotionListViewAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
    }

    public PromotionListViewAdapter(Context context, int resource, List<Product> items) {
        super(context, resource, items);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;

        if (v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            v = vi.inflate(R.layout.promotion_list_item, null);
        }

        Product p = getItem(position);

        if (p != null) {
            TextView title = (TextView) v.findViewById(R.id.title);
            TextView oldPrice = (TextView) v.findViewById(R.id.oldPrice);
            TextView price = (TextView) v.findViewById(R.id.price);
            oldPrice.setPaintFlags(oldPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

            if (title != null) {
                title.setText(p.getTitle());
            }

            if (oldPrice != null) {
                oldPrice.setText(String.format(Locale.ITALY,"%.2f",p.getPrice()));
            }

            if (price != null) {
                price.setText(String.format (Locale.ITALY,"%.2f", p.getPromotionPrice()));
            }
        }

        return v;
    }
}
