package com.pezzuto.pezzuto.adapter;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.pezzuto.pezzuto.R;
import com.pezzuto.pezzuto.SharedUtils;
import com.pezzuto.pezzuto.Statics;
import com.pezzuto.pezzuto.items.Product;

import java.util.List;
import java.util.Locale;

/**
 * Created by dade on 24/03/17.
 */

public class CategoriesListViewAdapter extends ArrayAdapter<String> {
    private Context context;
    private String selectedCategory = "In primo piano";
    private String[] items;
    public CategoriesListViewAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
    }

    public CategoriesListViewAdapter(Context context, int resource, String[] items, String selectedCategory) {
        super(context, resource, items);
        this.context = context;
        this.items = items;
        this.selectedCategory = selectedCategory;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            v = vi.inflate(R.layout.category_list_item, null);
        }
        TextView t1 = (TextView) v.findViewById(R.id.text1);
        ImageView done = (ImageView) v.findViewById(R.id.done);
        t1.setText(items[position]);
        if (selectedCategory.equals(items[position])) {
            t1.setTypeface(Typeface.DEFAULT_BOLD);
            done.setVisibility(View.VISIBLE);
        }
        else {
            t1.setTypeface(Typeface.DEFAULT);
            done.setVisibility(View.GONE);
        }
        return v;
    }
    public void setSelectedCategory(String category) {
        selectedCategory = category;
    }
}
