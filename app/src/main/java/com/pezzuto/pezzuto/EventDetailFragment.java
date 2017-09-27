package com.pezzuto.pezzuto;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
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
import com.android.volley.VolleyError;
import com.pezzuto.pezzuto.items.Event;
import com.pezzuto.pezzuto.items.PezzutoObject;
import com.pezzuto.pezzuto.listeners.OnFragmentInteractionListener;
import com.pezzuto.pezzuto.requests.RequestsUtils;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;
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
    Button b;
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
        mListener.disableSwipeRefresh();
        mListener.setImageLoading(true);
        fill();
        shre = getContext().getSharedPreferences(Statics.SHARED_PREF,Context.MODE_PRIVATE);
        edit = shre.edit();
        mListener.setEventSheetBehaviour();
        return v;
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
    public PezzutoObject getRelatedObject() {
        return event;
    }
    public Bitmap getImageBitmap() {
        return ((BitmapDrawable) image.getDrawable()).getBitmap();
    }
    public void fill() {
        event = mListener.getSelectedEvent();
        askInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               Statics.sendMail(getContext(),"eventipezzuto@gmail.com",event.getName());
            }
        });
        title.setText(event.getName());
        description.setText(event.getDescription());
        date.setText(Statics.getFormattedEventDate(event));
        if (event.getImage().equals("null")) {
            image.setVisibility(View.GONE);
            if (!SharedUtils.isPrivateMember(getContext())) insertParticipateButton(false);
            mListener.setImageLoading(false);
        }
        else {
            Statics.loadImage(getContext(), event.getImage(), image, new Callback() {
                @Override
                public void onSuccess() {
                    mListener.setImageLoading(false);
                }

                @Override
                public void onError() {

                }
            });
            image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getContext(),ImageViewerActivity.class);
                    intent.putExtra("image",event.getImage());
                    startActivity(intent);
                }
            });

            if (!SharedUtils.isPrivateMember(getContext())) insertParticipateButton(true);
        }

        Log.d("image",event.getImage());
    }
    public ColorStateList getParticipateColorStateList() {
        int[][] states = new int[][] {
                new int[] { android.R.attr.state_enabled}, // enabled
                new int[] {-android.R.attr.state_enabled}, // disabled
                new int[] {-android.R.attr.state_checked}, // unchecked
                new int[] { android.R.attr.state_pressed}  // pressed
        };

        int[] colors = new int[] {
                Color.parseColor("#e30613"),
                Color.parseColor("#e30613"),
                Color.parseColor("#e30613"),
                Color.WHITE
        };

        return new ColorStateList(states, colors);
    }
    public ColorStateList getNotParticipateColorStateList() {
        int[][] states = new int[][] {
                new int[] { android.R.attr.state_enabled}, // enabled
                new int[] {-android.R.attr.state_enabled}, // disabled
                new int[] {-android.R.attr.state_checked}, // unchecked
                new int[] { android.R.attr.state_pressed}  // pressed
        };

        int[] colors = new int[] {
                Color.WHITE,
                Color.WHITE,
                Color.WHITE,
                Color.parseColor("#e30613")
        };

        return new ColorStateList(states, colors);
    }
    public void insertParticipateButton(boolean withImage) {
        //create button
        b = new Button(getContext());
            checkParticipateButton();

        RelativeLayout.LayoutParams rl = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        if (!withImage) {
            rl.addRule(RelativeLayout.BELOW, R.id.description);
            rl.setMargins(0,36,0,0);
        }
        if (withImage) {
            rl.addRule(RelativeLayout.BELOW,R.id.image);

        }
        b.setId(View.generateViewId());
        b.setLayoutParams(rl);
        eventLayout.addView(b);

        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SharedUtils.isParticipating(getContext(),mListener.getSelectedEvent().getId())) {
                    RequestsUtils.sendEventRequest(getContext(), createNoMoreJSON(), new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            setParticipateButton(b);
                            SharedUtils.removeEvent(getContext(),mListener.getSelectedEvent().getId());
                            Toast.makeText(getContext(),"Prenotazione correttamente eliminata",Toast.LENGTH_SHORT).show();
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(getContext(),"Errore, prego riprovare.",Toast.LENGTH_SHORT).show();
                        }
                    }, false);
                }
                else mListener.launchEventInfoFragment();
                /*RequestsUtils.participateEventRequest(getContext(), event.getId(), new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(getContext(),"Evento correttamente prenotato",Toast.LENGTH_SHORT).show();
                        if (shre.getBoolean("eventsInCalendar",true))
                            openCalendarCheckDialog();
                    }
                });*/
            }
        });
        RelativeLayout.LayoutParams params2 = (RelativeLayout.LayoutParams) askInfo.getLayoutParams();
        params2.removeRule(RelativeLayout.BELOW);
        if (!withImage) params2.addRule(RelativeLayout.BELOW,b.getId());
        if (withImage) {
            params2.addRule(RelativeLayout.BELOW,R.id.description);
            RelativeLayout.LayoutParams paramsDesc = (RelativeLayout.LayoutParams) description.getLayoutParams();
            paramsDesc.addRule(RelativeLayout.BELOW,b.getId());
        }

    }
    public JSONObject createNoMoreJSON() {
        JSONObject request = new JSONObject();
        try {
            JSONObject event = new JSONObject();
            event.put("id",mListener.getSelectedEvent().getId());
            event.put("partecipanti",SharedUtils.getParticipants(getContext(),mListener.getSelectedEvent().getId()));
            request.put("evento", event);

        }
        catch (JSONException e) {}
        return request;
    }
    private void setParticipateButton(Button b) {
        b.setBackgroundResource(R.drawable.participate_selector);
        b.setCompoundDrawablesWithIntrinsicBounds( R.drawable.ic_event_white_48px, 0, 0, 0);
        b.setTextColor(getParticipateColorStateList());
        b.setText("Partecipa");
    }
    public void setNotParticipateButton(Button b) {
        b.setBackgroundResource(R.drawable.not_participate_selector);
        b.setCompoundDrawablesWithIntrinsicBounds( R.drawable.ic_event_white, 0, 0, 0);
        b.setTextColor(getNotParticipateColorStateList());
        b.setText("Non partecipare più");
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
    public void refresh() {
        checkParticipateButton();
    }
    public void checkParticipateButton() {
        if (SharedUtils.isParticipating(getContext(),mListener.getSelectedEvent().getId()))
            setNotParticipateButton(b);

        else
            setParticipateButton(b);
    }
    public String getType() { return MainActivity.EVENT_DETAIL; }
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
