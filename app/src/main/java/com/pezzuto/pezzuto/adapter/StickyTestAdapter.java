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
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pezzuto.pezzuto.MainActivity;
import com.pezzuto.pezzuto.PromotionDetailFragment;
import com.pezzuto.pezzuto.R;
import com.pezzuto.pezzuto.RefreshableFragment;
import com.pezzuto.pezzuto.Statics;
import com.pezzuto.pezzuto.items.Product;
import com.pezzuto.pezzuto.items.Promprod;
import com.pezzuto.pezzuto.listeners.OnFragmentInteractionListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import ca.barrenechea.widget.recyclerview.decoration.StickyHeaderAdapter;
import ca.barrenechea.widget.recyclerview.decoration.StickyHeaderDecoration;

public class StickyTestAdapter extends RecyclerView.Adapter<StickyTestAdapter.ViewHolder> implements
        StickyHeaderAdapter<StickyTestAdapter.HeaderHolder> {
    private LayoutInflater mInflater;
    private List<Promprod> promprods;
    OnFragmentInteractionListener listener;
    Context context;
    public StickyTestAdapter(Context context, List<Promprod> promprods, OnFragmentInteractionListener listener) {
        this.context = context;
        this.promprods = promprods;
        this.listener = listener;
        mInflater = LayoutInflater.from(context);

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = mInflater.inflate(R.layout.promprod_list_item, viewGroup, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        final Promprod p = promprods.get(i);
        viewHolder.promprod_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.setSelectedPromprod(p);
                RefreshableFragment f = new PromotionDetailFragment();
                listener.launchFragment(f,MainActivity.PROMOTION_DETAIL);
            }
        });
        viewHolder.title.setText(p.getTitle());
        viewHolder.description.setText(p.getDescription());
        viewHolder.validity.setText("Valida dal "+ Statics.getSimpleDate(p.getValidaDal())+
                " al "+Statics.getSimpleDate(p.getValidaAl())+(p.isEsaurimento() ? " o esaurimento scorte" : ""));
    }

    @Override
    public int getItemCount() {
        return promprods.size();
    }

    @Override
    public long getHeaderId(int position) {
        if (position > 0) {
            if (!promprods.get(position-1).getLabel().equals(promprods.get(position).getLabel())) return (long) position;
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
              viewholder.header.setText(promprods.get(position).getLabel());
    }


    static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView title;
        public TextView description;
        public TextView validity;
        public RelativeLayout promprod_layout;

        public ViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.title);
            description = (TextView) itemView.findViewById(R.id.description);
            validity = (TextView) itemView.findViewById(R.id.validity);
            promprod_layout = (RelativeLayout) itemView.findViewById(R.id.promprod_item);
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
