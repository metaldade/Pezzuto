package com.pezzuto.pezzuto;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.util.TypedValue;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pezzuto.pezzuto.items.Event;
import com.pezzuto.pezzuto.listeners.OnFragmentInteractionListener;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.Date;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFragmentInteractionListener} interface
 * to handle interaction events.
 * create an instance of this fragment.
 */
public class EventDetailFragment extends RefreshableFragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private OnFragmentInteractionListener mListener;
    TextView title, description,date;
    ImageView image;
    Button askInfo;
    Event event;
    RelativeLayout eventLayout;
    public EventDetailFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment EventDetailFragment.
     */


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =  inflater.inflate(R.layout.fragment_event_detail, container, false);
        askInfo = (Button) v.findViewById(R.id.askInfo);
        title = (TextView) v.findViewById(R.id.title);
        image = (ImageView) v.findViewById(R.id.image);
        date = (TextView) v.findViewById(R.id.date);
        description = (TextView) v.findViewById(R.id.description);
        eventLayout = (RelativeLayout) v.findViewById(R.id.event_detail_layout);
        fill();

        return v;
    }
    public void fill() {
        event = mListener.getSelectedEvent();
        title.setText(event.getName());
        description.setText(event.getDescription());
        date.setText(Statics.getFormattedEventDate(event));
        if (event.getImage().equals("null")) {
            image.setVisibility(View.GONE);
        }
        else Statics.loadImage(getContext(), event.getImage(),image);
        insertParticipateButton(false);
        Log.d("image",event.getImage());
    }
    public void insertParticipateButton(boolean withImage) {
        //create button
        Button b = new Button(getContext());
        b.setBackgroundResource(R.drawable.participate_selector);

        RelativeLayout.LayoutParams rl = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        if (!withImage) rl.addRule(RelativeLayout.BELOW, R.id.description);
        if (withImage) rl.addRule(RelativeLayout.BELOW,R.id.image);
        b.setCompoundDrawablesWithIntrinsicBounds( R.drawable.ic_event_white_48px, 0, 0, 0);
        b.setText("Partecipa");
        b.setTextColor(ContextCompat.getColor(getContext(),R.color.colorAccent));
        b.setId(View.generateViewId());
        b.setLayoutParams(rl);
        eventLayout.addView(b);

        RelativeLayout.LayoutParams params2 = (RelativeLayout.LayoutParams) askInfo.getLayoutParams();
        params2.removeRule(RelativeLayout.BELOW);
        if (!withImage) params2.addRule(RelativeLayout.BELOW,b.getId());
        if (withImage) params2.addRule(RelativeLayout.BELOW,R.id.description);
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }
    public void refresh() {}
    public String getType() { return ""; }
    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
}
