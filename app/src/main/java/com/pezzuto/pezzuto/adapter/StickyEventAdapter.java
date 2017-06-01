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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pezzuto.pezzuto.EventDetailFragment;
import com.pezzuto.pezzuto.ProductDetailFragment;
import com.pezzuto.pezzuto.R;
import com.pezzuto.pezzuto.Statics;
import com.pezzuto.pezzuto.items.Event;
import com.pezzuto.pezzuto.items.Product;
import com.pezzuto.pezzuto.listeners.OnFragmentInteractionListener;

import java.util.List;
import java.util.Locale;

import ca.barrenechea.widget.recyclerview.decoration.StickyHeaderAdapter;

public class StickyEventAdapter extends RecyclerView.Adapter<StickyEventAdapter.ViewHolder> implements
        StickyHeaderAdapter<StickyEventAdapter.HeaderHolder> {
    private LayoutInflater mInflater;
    private List<Event> events;
    OnFragmentInteractionListener listener;
    Context context;
    public StickyEventAdapter(Context context, List<Event> events, OnFragmentInteractionListener listener) {
        this.context = context;
        this.events = events;
        this.listener = listener;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = mInflater.inflate(R.layout.event_list_item, viewGroup, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        final Event e = events.get(i);
        viewHolder.event_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.setSelectedEvent(e);
                listener.launchFragment(new EventDetailFragment());
            }
        });
        viewHolder.title.setText(e.getName());
        viewHolder.description.setText(e.getBriefDescription());
        viewHolder.date.setText(Statics.getFormattedEventDate(e));
    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    @Override
    public long getHeaderId(int position) {
        if (position > 0) {
            if (!events.get(position-1).getMonth().equals(events.get(position).getMonth())) return (long) position;
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
              viewholder.header.setText(events.get(position).getMonth());
    }


    static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView title;
        public TextView description;
        public TextView date;
        public LinearLayout event_layout;

        public ViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.title);
            description = (TextView) itemView.findViewById(R.id.description);
            date = (TextView) itemView.findViewById(R.id.date);
            event_layout = (LinearLayout) itemView.findViewById(R.id.event_layout);
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
