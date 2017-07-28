package com.pezzuto.pezzuto;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.dd.CircularProgressButton;
import com.pezzuto.pezzuto.items.Event;
import com.pezzuto.pezzuto.listeners.OnFragmentInteractionListener;
import com.pezzuto.pezzuto.requests.RequestsUtils;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * create an instance of this fragment.
 */
public class EventInfoFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private EditText ragioneSociale, pIVA, email, note;
    private TextInputLayout emailLayout, ragioneSocialeLayout, pIVALayout;
    private NumberPicker np;
    Event event;
    private SharedPreferences sharedPref;
    private SharedPreferences.Editor editor;
    private CircularProgressButton participateButton;
    // TODO: Rename and change types of parameters
    private OnFragmentInteractionListener mListener;

    public EventInfoFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment EventInfoFragment.
     */

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
SharedPreferences shre;
    SharedPreferences.Editor edit;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =  inflater.inflate(R.layout.fragment_event_info, container, false);
        ragioneSociale = (EditText) v.findViewById(R.id.ragione_sociale);
        pIVA = (EditText) v.findViewById(R.id.partita_iva);
        email = (EditText) v.findViewById(R.id.email);
        note = (EditText) v.findViewById(R.id.note);
        np = (NumberPicker) v.findViewById(R.id.np);
        participateButton = (CircularProgressButton) v.findViewById(R.id.participateButton);
        emailLayout = (TextInputLayout) v.findViewById(R.id.layout_email);
        ragioneSocialeLayout = (TextInputLayout) v.findViewById(R.id.layout_ragione_sociale);
        pIVALayout = (TextInputLayout) v.findViewById(R.id.layout_partita_iva);
        ragioneSociale.addTextChangedListener(new InfoTextWatcher(ragioneSociale));
        email.addTextChangedListener(new InfoTextWatcher(email));
        pIVA.addTextChangedListener(new InfoTextWatcher(pIVA));
        participateButton.setIndeterminateProgressMode(true);
        participateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (participateButton.getProgress() == -1) {
                    participateButton.setProgress(0);
                    return;
                }
                else if (participateButton.getProgress() == 100) {
                    mListener.getBottomSheetBehavior().setState(BottomSheetBehavior.STATE_HIDDEN);
                    return;
                }
                submitForm();
            }
        });
        shre = getContext().getSharedPreferences(Statics.SHARED_PREF,Context.MODE_PRIVATE);
        edit = shre.edit();
        event = mListener.getSelectedEvent();
        np.setMinValue(1);
        np.setMaxValue(100);
        //Set shared preferences
        sharedPref = getContext().getSharedPreferences(
                "pezzuto", Context.MODE_PRIVATE);
        editor = sharedPref.edit();

        //Load info
        if (sharedPref.contains("ragione_sociale")) ragioneSociale.setText(sharedPref.getString("ragione_sociale",""));
        if (sharedPref.contains("partita_iva")) pIVA.setText(sharedPref.getString("partita_iva",""));
        if (sharedPref.contains("email")) email.setText(sharedPref.getString("email",""));
        return v;
    }
    private boolean validateRagioneSociale() {
        if (ragioneSociale.getText().toString().trim().isEmpty()) {
            ragioneSocialeLayout.setError(getString(R.string.err_msg_ragione_sociale));
            requestFocus(ragioneSociale);
            return false;
        } else {
            ragioneSocialeLayout.setErrorEnabled(false);
        }

        return true;
    }
    private boolean validatePIVA() {
        if (pIVA.getText().toString().trim().isEmpty()) {
            pIVALayout.setError(getString(R.string.err_msg_piva_empty));
            requestFocus(pIVA);
            return false;
        }
        else if (pIVA.getText().toString().trim().length() < 11) {
            Log.d(""+pIVA.getText().toString().trim().length(),pIVA.getText().toString().trim());
            pIVALayout.setError(getString(R.string.err_msg_piva_characters));
            requestFocus(pIVA);
            return false;
        }
        else {
            pIVALayout.setErrorEnabled(false);
        }

        return true;
    }
    private void submitForm() {
        if (!validateRagioneSociale()) {
            return;
        }

        if (!validateEmail()) {
            return;
        }

        if (!validatePIVA()) {
            return;
        }
        participateEvent();
    }
    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    private boolean validateEmail() {
        String e = email.getText().toString().trim();

        if (e.isEmpty() || !isValidEmail(e)) {
            emailLayout.setError(getString(R.string.err_msg_email));
            requestFocus(email);
            return false;
        } else {
            emailLayout.setErrorEnabled(false);
        }

        return true;
    }
    private static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
    private class InfoTextWatcher implements TextWatcher {

        private View view;

        private InfoTextWatcher(View view) {
            this.view = view;
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void afterTextChanged(Editable editable) {
            switch (view.getId()) {
                case R.id.ragione_sociale:
                    validateRagioneSociale();
                    editor.putString("ragione_sociale",ragioneSociale.getText().toString().trim());
                    break;
                case R.id.email:
                    validateEmail();
                    editor.putString("email",email.getText().toString().trim());
                    break;
                case R.id.partita_iva:
                    validatePIVA();
                    editor.putString("partita_iva",pIVA.getText().toString().trim());
                    break;
            }
        }
    }
    public void participateEvent() {
       final  SharedPreferences shre = getContext().getSharedPreferences(Statics.SHARED_PREF,Context.MODE_PRIVATE);
       final int id = mListener.getSelectedEvent().getId();
        participateButton.setProgress(50);
        Log.d("json",createEventJSON().toString());
       RequestsUtils.sendEventRequest(getContext(), createEventJSON(),
               new Response.Listener<String>() {
                   @Override
                   public void onResponse(String response) {
                        participateButton.setProgress(100);
                        SharedUtils.addEvent(getContext(),id, np.getValue());
                       if (shre.getBoolean("eventsInCalendar",true))
                           openCalendarCheckDialog();
                   }
               }, new Response.ErrorListener() {
                   @Override
                   public void onErrorResponse(VolleyError error) {
                        participateButton.setProgress(-1);
                       error.printStackTrace();
                   }
               }, true);
    }
    public JSONObject createEventJSON() {
        JSONObject request = new JSONObject();
        try{
            JSONObject cliente = new JSONObject();
            cliente.put("ragione_sociale", ragioneSociale.getText().toString().trim());
            cliente.put("partita_iva",pIVA.getText().toString().trim());
            cliente.put("email",email.getText().toString().trim());
            cliente.put("partecipanti",np.getValue());
            JSONObject event = new JSONObject();

            event.put("id",mListener.getSelectedEvent().getId());
            event.put("nome",mListener.getSelectedEvent().getName());

            request.put("cliente",cliente);
            request.put("evento",event);
        }
        catch(JSONException ex) {}
        return request;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        }
        else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }
    public void openCalendarCheckDialog() {
        new AlertDialog.Builder(getContext())
                .setTitle("")
                .setMessage("Vuoi aggiungere questo evento al tuo calendario?")
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (ContextCompat.checkSelfPermission(getActivity(),
                                Manifest.permission.WRITE_CALENDAR) != PackageManager.PERMISSION_GRANTED)
                            requestPermissions(
                                    new String[]{Manifest.permission.WRITE_CALENDAR},
                                    Statics.CALENDAR_PERMISSION);

                        else {
                            pushAppointmentsToCalender();
                            endParticipation();
                        }
                    }
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                endParticipation();
            }
        }).create().show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case Statics.CALENDAR_PERMISSION: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    pushAppointmentsToCalender();
                }
                else {
                    edit.putBoolean("eventsInCalendar",false);
                    edit.commit();
                }
                endParticipation();
            }
        }
    }
public void endParticipation() {
    mListener.refreshCurrentFragment();
    mListener.getBottomSheetBehavior().setState(BottomSheetBehavior.STATE_HIDDEN);
}
    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
    public long pushAppointmentsToCalender() {
        /***************** Event: note(without alert) *******************/

        String eventUriString = "content://com.android.calendar/events";
        ContentValues eventValues = new ContentValues();
        eventValues.put("calendar_id", 1); // id, We need to choose from
        // our mobile for primary
        // its 1
        eventValues.put("title", event.getName());
        eventValues.put("description", event.getBriefDescription());
        eventValues.put("eventLocation", event.getPlace());
        long startDate = event.getStartDate().getTime();
        long endDate;
        if (event.getEndDate() == null) endDate = startDate + 1000 * 60 * 60* 24; // For all day
        else endDate = event.getEndDate().getTime();

        eventValues.put("dtstart", startDate);
        eventValues.put("dtend", endDate);

        // values.put("allDay", 1); //If it is bithday alarm or such
        // kind (which should remind me for whole day) 0 for false, 1
        // for true
        eventValues.put("eventStatus", 1); // This information is
        // sufficient for most
        // entries tentative (0),
        // confirmed (1) or canceled
        // (2):
        eventValues.put("eventTimezone", "UTC/GMT +2:00");
   /*Comment below visibility and transparency  column to avoid java.lang.IllegalArgumentException column visibility is invalid error */

    /*eventValues.put("visibility", 3); // visibility to default (0),
                                        // confidential (1), private
                                        // (2), or public (3):
    eventValues.put("transparency", 0); // You can control whether
                                        // an event consumes time
                                        // opaque (0) or transparent
                                        // (1).
      */
        eventValues.put("hasAlarm", 0); // 0 for false, 1 for true

        Uri eventUri = getContext().getContentResolver().insert(Uri.parse(eventUriString), eventValues);
        long eventID = Long.parseLong(eventUri.getLastPathSegment());



        /***************** Event: Meeting(without alert) Adding Attendies to the meeting *******************/


        Toast.makeText(getContext(),"Evento correttamente inserito nel calendario",Toast.LENGTH_SHORT).show();
        return eventID;

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
