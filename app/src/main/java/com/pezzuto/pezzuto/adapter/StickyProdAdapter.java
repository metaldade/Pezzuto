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

package com.pezzuto.pezzuto.adapter;

import android.content.Context;
import android.media.Image;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pezzuto.pezzuto.ProductDetailFragment;
import com.pezzuto.pezzuto.PromotionDetailFragment;
import com.pezzuto.pezzuto.R;
import com.pezzuto.pezzuto.Statics;
import com.pezzuto.pezzuto.items.Product;
import com.pezzuto.pezzuto.items.Promprod;
import com.pezzuto.pezzuto.listeners.OnFragmentInteractionListener;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Locale;

import ca.barrenechea.widget.recyclerview.decoration.StickyHeaderAdapter;

public class StickyProdAdapter extends RecyclerView.Adapter<StickyProdAdapter.ViewHolder> implements
        StickyHeaderAdapter<StickyProdAdapter.HeaderHolder> {
    private LayoutInflater mInflater;
    private List<Product> products;
    OnFragmentInteractionListener listener;
    Context context;
    public StickyProdAdapter(Context context, List<Product> products, OnFragmentInteractionListener listener) {
        this.context = context;
        this.products = products;
        this.listener = listener;
        mInflater = LayoutInflater.from(context);

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = mInflater.inflate(R.layout.product_list_item, viewGroup, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        final Product p = products.get(i);
        viewHolder.product_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.setSelectedProduct(p);
                listener.launchFragment(new ProductDetailFragment());

            }
        });
        viewHolder.title.setText(p.getTitle());
        viewHolder.category.setText(p.getCategory());
        viewHolder.price.setText(String.format(Locale.ITALY,"%.2f",p.getPrice())+" €");
        Statics.loadImage(context,p.getThumbnail(),viewHolder.image);
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    @Override
    public long getHeaderId(int position) {
        if (position > 0) {
            if (!products.get(position-1).getLabel().equals(products.get(position).getLabel())) return (long) position;
            else return 0;
        }
        else return 0;
    }

    @Override
    public HeaderHolder onCreateHeaderViewHolder(ViewGroup parent) {
        final View view = mInflater.inflate(R.layout.list_header, parent, false);
        return new HeaderHolder(view);
    }

    @Override
    public void onBindHeaderViewHolder(HeaderHolder viewholder, int position) {
              viewholder.header.setText(products.get(position).getLabel());
    }


    static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView title;
        public TextView category;
        public TextView price;
        public ImageView image;
        public RelativeLayout product_layout;

        public ViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.title);
            category = (TextView) itemView.findViewById(R.id.category);
            price = (TextView) itemView.findViewById(R.id.price);
            product_layout = (RelativeLayout) itemView.findViewById(R.id.product_layout);
            image = (ImageView) itemView.findViewById(R.id.image);
        }
    }

    static class HeaderHolder extends RecyclerView.ViewHolder {
        public TextView header;

        public HeaderHolder(View itemView) {
            super(itemView);

            header = (TextView) itemView;
        }
    }
}
