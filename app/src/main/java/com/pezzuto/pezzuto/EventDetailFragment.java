package com.pezzuto.pezzuto;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
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
import android.widget.Toast;

import com.android.volley.Response;
import com.pezzuto.pezzuto.items.Event;
import com.pezzuto.pezzuto.listeners.OnFragmentInteractionListener;
import com.pezzuto.pezzuto.requests.RequestsUtils;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.Calendar;
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
    //Shre MACROS
    public static final String EVENTS_IN_CALENDAR = "eventsInCalendar";

    private OnFragmentInteractionListener mListener;
    TextView title, description,date;
    ImageView image;
    Button askInfo;
    Event event;
    RelativeLayout eventLayout;
    SharedPreferences shre;
    SharedPreferences.Editor edit;
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
        shre = getContext().getSharedPreferences(Statics.SHARED_PREF,Context.MODE_PRIVATE);
        edit = shre.edit();
        return v;
    }
    public void openCalendarCheckDialog() {
        new AlertDialog.Builder(getContext())
                .setTitle("")
                .setMessage("Vuoi aggiungere questo evento al tuo calendario?")
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        requestPermissions(
                                new String[]{Manifest.permission.WRITE_CALENDAR},
                                Statics.CALENDAR_PERMISSION);
                    }
                }).setNegativeButton("No", null).create().show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case Statics.CALENDAR_PERMISSION: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    addScheduleToCalendar(event.getName(),event.getPlace(),event.getBriefDescription());
                }
                else {
                    edit.putBoolean("eventsInCalendar",false);
                    edit.commit();
                }
            }
        }
    }

    public Uri addScheduleToCalendar(String title,String place,String description) {

        long calID = 3;
        long startMillis = 0;
        long endMillis = 0;
        Calendar beginTime = Calendar.getInstance();
        beginTime.set(2016, 07, 22, 17, 30);
        startMillis = beginTime.getTimeInMillis();
        Calendar endTime = Calendar.getInstance();
        endTime.set(2016, 07, 22, 18, 45);
        endMillis = endTime.getTimeInMillis();

        ContentValues values = new ContentValues();
        ContentResolver cr = getContext().getContentResolver();

        values.put(CalendarContract.Events.EVENT_LOCATION, place);
        values.put(CalendarContract.Events.DTSTART, startMillis);
        values.put(CalendarContract.Events.DTEND, endMillis);
        values.put(CalendarContract.Events.TITLE, title);
        values.put(CalendarContract.Events.DESCRIPTION, description);
        values.put(CalendarContract.Events.CALENDAR_ID, calID);
        values.put(CalendarContract.Events.EVENT_TIMEZONE, "UTC/GMT +2:00");
        Uri uri;
        if (Build.VERSION.SDK_INT >= 8) {
            uri = Uri.parse("content://com.android.calendar/events");
        } else {
            uri = Uri.parse("content://calendar/events");
        }
        Uri l_uri = ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.WRITE_CALENDAR) == PackageManager.PERMISSION_GRANTED ? cr.insert(CalendarContract.Events.CONTENT_URI, values) : null;

        return l_uri;

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
        if (!withImage) {
            rl.addRule(RelativeLayout.BELOW, R.id.description);
            rl.setMargins(18,0,0,0);
        }
        if (withImage) rl.addRule(RelativeLayout.BELOW,R.id.image);

        b.setCompoundDrawablesWithIntrinsicBounds( R.drawable.ic_event_white_48px, 0, 0, 0);
        b.setText("Partecipa");
        b.setTextColor(ContextCompat.getColor(getContext(),R.color.colorAccent));
        b.setId(View.generateViewId());
        b.setLayoutParams(rl);
        eventLayout.addView(b);

        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RequestsUtils.participateEventRequest(getContext(), event.getId(), new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(getContext(),"Evento correttamente prenotato",Toast.LENGTH_SHORT).show();
                        if (shre.getBoolean("eventsInCalendar",true))
                            openCalendarCheckDialog();
                    }
                });
            }
        });
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
    public boolean hasEmptySet(String type) { return false; }
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
