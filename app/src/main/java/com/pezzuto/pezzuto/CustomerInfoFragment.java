package com.pezzuto.pezzuto;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.widget.TextViewCompat;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Response;
import com.pezzuto.pezzuto.items.Product;
import com.pezzuto.pezzuto.items.Promprod;
import com.pezzuto.pezzuto.listeners.OnFragmentInteractionListener;
import com.pezzuto.pezzuto.requests.RequestsUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class CustomerInfoFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

    private OnFragmentInteractionListener mListener;
    private EditText ragioneSociale, pIVA, email, note;
    private TextInputLayout emailLayout, ragioneSocialeLayout, pIVALayout;
    private SharedPreferences sharedPref;
    private SharedPreferences.Editor editor;
    String[] tipiConsegna = {"Ritiro in negozio","Spedizione"};
    Spinner spinner;
    public CustomerInfoFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment CustomerInfoFragment.
     */

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =  inflater.inflate(R.layout.fragment_customer_info, container, false);
        spinner = (Spinner) v.findViewById(R.id.tipo_consegna);
// Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.tipi_consegna, android.R.layout.simple_spinner_item);
// Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
        spinner.setAdapter(adapter);

        sharedPref = getContext().getSharedPreferences(
                "pezzuto", Context.MODE_PRIVATE);
        editor = sharedPref.edit();

        ragioneSociale = (EditText) v.findViewById(R.id.ragione_sociale);
        pIVA = (EditText) v.findViewById(R.id.partita_iva);
        email = (EditText) v.findViewById(R.id.email);
        note = (EditText) v.findViewById(R.id.note);
        emailLayout = (TextInputLayout) v.findViewById(R.id.layout_email);
        ragioneSocialeLayout = (TextInputLayout) v.findViewById(R.id.layout_ragione_sociale);
        pIVALayout = (TextInputLayout) v.findViewById(R.id.layout_partita_iva);
        ragioneSociale.addTextChangedListener(new InfoTextWatcher(ragioneSociale));
        email.addTextChangedListener(new InfoTextWatcher(email));
        pIVA.addTextChangedListener(new InfoTextWatcher(pIVA));
        mListener.getFab().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitForm();
            }
        });

        if (sharedPref.contains("ragione_sociale")) ragioneSociale.setText(sharedPref.getString("ragione_sociale",""));
        if (sharedPref.contains("partita_iva")) pIVA.setText(sharedPref.getString("partita_iva",""));
        if (sharedPref.contains("email")) email.setText(sharedPref.getString("email",""));

        return v;
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
        editor.apply();
        editor.commit();
        sendOrder();
        Toast.makeText(getContext(), "Thank You!", Toast.LENGTH_SHORT).show();
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
    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
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
    public void sendOrder(){
        JSONObject order = createJSONOrder();
        mListener.startProgress();
        RequestsUtils.sendOrderRequest(getContext(), RequestsUtils.PROMOTION_ORDER, order, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                mListener.endProgressSuccessfully();
            }
        });
    }
    public JSONObject createJSONOrder() {
        JSONObject request = new JSONObject();
        try {
            JSONObject cliente = new JSONObject();
            cliente.put("ragione_sociale", ragioneSociale.getText().toString().trim());
            cliente.put("partita_iva",pIVA.getText().toString().trim());
            cliente.put("email",email.getText().toString().trim());
            cliente.put("tipo_consegna",spinner.getSelectedItemPosition() == 0 ? "Ritiro in negozio" : "Spedizione");
            cliente.put("note",note.getText().toString().trim());
            JSONObject promozione = new JSONObject();

            Promprod selectedPromprod = mListener.getSelectedPromprod();
            promozione.put("id",selectedPromprod.getId());
            promozione.put("nome",selectedPromprod.getTitle());

            JSONArray prodotti = new JSONArray();
            for (Product p : selectedPromprod.getProducts()) {
                if (p.getQuantity() > 0) {
                    JSONObject product = new JSONObject();
                    product.put("id",p.getId());
                    product.put("codice",p.getCode());
                    product.put("nome",p.getTitle());
                    product.put("quantita",""+p.getQuantity());
                    product.put("prezzo_promozione",String.format(Locale.ITALY,"%.2f",p.getPromotionPrice()));
                    prodotti.put(product);
                }
            }
            promozione.put("prodotti",prodotti);
            request.put("cliente",cliente);
            request.put("promozione",promozione);

        }
        catch (JSONException e) { e.printStackTrace(); }
        Log.d("request",request.toString());

        return request;
    }
}