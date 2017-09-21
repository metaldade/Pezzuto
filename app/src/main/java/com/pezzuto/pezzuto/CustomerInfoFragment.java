package com.pezzuto.pezzuto;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.BottomSheetBehavior;
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
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.dd.CircularProgressButton;
import com.pezzuto.pezzuto.items.Product;
import com.pezzuto.pezzuto.items.Promprod;
import com.pezzuto.pezzuto.listeners.OnCartInteractionListener;
import com.pezzuto.pezzuto.listeners.OnFragmentInteractionListener;
import com.pezzuto.pezzuto.requests.RequestsUtils;
import com.pezzuto.pezzuto.ui.StickyHeaderFragment;
import com.pezzuto.pezzuto.ui.UiUtils;

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
    private OnCartInteractionListener cartListener;
    private EditText ragioneSociale, pIVA, email, note, cognome,phone;
    private TextInputLayout emailLayout, ragioneSocialeLayout, pIVALayout, cognomeLayout, phoneLayout;
    private SharedPreferences sharedPref;
    private SharedPreferences.Editor editor;
    private LinearLayout linearCustomer;
    private CircularProgressButton orderButton;
    private boolean isPrivate;
    String[] tipiConsegna = {"Ritiro in negozio","Spedizione"};
    Spinner spinner;
    String type;
    public CustomerInfoFragment() {
        // Required empty public constructor
    }
    public static CustomerInfoFragment newInstance(String type) {
        CustomerInfoFragment f = new CustomerInfoFragment();
        // Supply index input as an argument.
        Bundle args = new Bundle();
        args.putString("type", type);
        f.setArguments(args);
        return f;
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
        Bundle args = getArguments();
        type = args.getString("type");
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
        isPrivate = SharedUtils.isPrivateMember(getContext());
        sharedPref = getContext().getSharedPreferences(
                "pezzuto", Context.MODE_PRIVATE);
        editor = sharedPref.edit();
        isPrivate = SharedUtils.isPrivateMember(getContext());
        ragioneSociale = (EditText) v.findViewById(R.id.ragione_sociale);
        pIVA = (EditText) v.findViewById(R.id.partita_iva);
        email = (EditText) v.findViewById(R.id.email);
        note = (EditText) v.findViewById(R.id.note);
        cognome = (EditText) v.findViewById(R.id.cognome);
        phone = (EditText) v.findViewById(R.id.phone);
        emailLayout = (TextInputLayout) v.findViewById(R.id.layout_email);
        cognomeLayout = (TextInputLayout) v.findViewById(R.id.layout_cognome);
        phoneLayout = (TextInputLayout) v.findViewById(R.id.layout_phone);

        //set order button
        orderButton = (CircularProgressButton) v.findViewById(R.id.orderButton);
        if (type.equals("promotion")) {
            orderButton.setVisibility(View.VISIBLE);
            orderButton.setIndeterminateProgressMode(true);
            orderButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int progress = orderButton.getProgress();
                    if (progress == -1) orderButton.setProgress(0);
                    submitForm();
                }
            });
        }

        ragioneSocialeLayout = (TextInputLayout) v.findViewById(R.id.layout_ragione_sociale);
        pIVALayout = (TextInputLayout) v.findViewById(R.id.layout_partita_iva);
        ragioneSociale.addTextChangedListener(new InfoTextWatcher(ragioneSociale));
        email.addTextChangedListener(new InfoTextWatcher(email));
        pIVA.addTextChangedListener(new InfoTextWatcher(pIVA));
        cognome.addTextChangedListener(new InfoTextWatcher(cognome));
        phone.addTextChangedListener(new InfoTextWatcher(phone));
        linearCustomer = (LinearLayout) v.findViewById(R.id.linearCustomer);
        if (type.equals("promotion")) mListener.getFab().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitForm();
            }
        });
        else cartListener.getCartButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int progress = cartListener.getCartButton().getProgress();
                if (progress == -1 || progress == 100) {
                    cartListener.restoreCartButton();

                    return;
                }
                submitForm();
            }
        });
        if (SharedUtils.isPrivateMember(getContext())) {
            pIVALayout.setVisibility(View.GONE);
            cognomeLayout.setVisibility(View.VISIBLE);
            phoneLayout.setVisibility(View.VISIBLE);
            ragioneSocialeLayout.setHint("Nome");
        }
        else {
            cognomeLayout.setVisibility(View.GONE);
            phoneLayout.setVisibility(View.GONE);
            pIVALayout.setVisibility(View.VISIBLE);
            ragioneSocialeLayout.setHint("Ragione Sociale");
        }

        if (sharedPref.contains("ragione_sociale")) ragioneSociale.setText(sharedPref.getString("ragione_sociale",""));
        if (sharedPref.contains("partita_iva")) pIVA.setText(sharedPref.getString("partita_iva",""));
        if (sharedPref.contains("email")) email.setText(sharedPref.getString("email",""));
        if (sharedPref.contains("cognome")) cognome.setText(sharedPref.getString("cognome",""));
        if (sharedPref.contains("phone")) phone.setText(sharedPref.getString("phone",""));

        return v;
    }



    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        }
        else if (context instanceof OnCartInteractionListener) {
            cartListener = (OnCartInteractionListener) context;
        }
        else {
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
            if (SharedUtils.isPrivateMember(getContext())) ragioneSocialeLayout.setError(getString(R.string.err_msg_nome));
            else ragioneSocialeLayout.setError(getString(R.string.err_msg_ragione_sociale));
            requestFocus(ragioneSociale);
            return false;
        } else {
            ragioneSocialeLayout.setErrorEnabled(false);
        }

        return true;
    }
    private boolean validateCognome() {
        if (cognome.getText().toString().trim().isEmpty()) {
            cognomeLayout.setError(getString(R.string.err_msg_cognome));
            requestFocus(cognome);
            return false;
        } else {
            cognomeLayout.setErrorEnabled(false);
        }
        return true;
    }
    private boolean validatePhone() {
        if (phone.getText().toString().trim().isEmpty()) {
            phoneLayout.setError(getString(R.string.err_msg_phone));
            requestFocus(phone);
            return false;
        } else {
            phoneLayout.setErrorEnabled(false);
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

        if (!isPrivate && !validatePIVA()) {
            return;
        }
        if (!isPrivate && !validatePhone()) {
            return;
        }
        if (isPrivate && !validateCognome()) {
            return;
        }
        editor.apply();
        sendOrder();
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
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
            //getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
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
                case R.id.cognome:
                    validateCognome();
                    editor.putString("cognome",cognome.getText().toString());
                    break;
                case R.id.phone:
                    validatePhone();
                    editor.putString("phone",phone.getText().toString().trim());
                    break;
            }
        }
    }
    public void setInvisible() {
        linearCustomer.setVisibility(View.GONE);
    }
    public void sendOrder(){
        JSONObject order = createJSONOrder();
        if(type.equals("promotion")) orderButton.setProgress(50);
        if (type.equals("cart")) cartListener.startProgress();
        RequestsUtils.sendOrderRequest(getContext(), type.equals("cart") ? RequestsUtils.PRODUCT_ORDER : RequestsUtils.PROMOTION_ORDER, order, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                       if (type.equals("promotion")) {
                           orderButton.setProgress(100);
                           UiUtils.createOrderDoneDialog(getContext(),mListener);
                       }
                       if (type.equals("cart")) {
                           cartListener.endOrderSuccessfully();
                       }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (type.equals("promotion")) {
                            orderButton.setProgress(-1);
                            //mListener.endProgressWithError();
                        }
                        if (type.equals("cart")) cartListener.endProgressWithError();
                        Log.d("error",""+error.getLocalizedMessage());
                    }
                });
    }
    public JSONObject createJSONOrder() {
        JSONObject request = new JSONObject();
        try {
            JSONObject cliente = new JSONObject();
            if (SharedUtils.isPrivateMember(getContext())) {
                cliente.put("tipo","privato");
                cliente.put("nome", ragioneSociale.getText().toString().trim());
                cliente.put("cognome",cognome.getText().toString().trim());
                cliente.put("telefono",phone.getText().toString().trim());
            }
            else {
                cliente.put("tipo","partita_iva");
                cliente.put("ragione_sociale", ragioneSociale.getText().toString().trim());
                cliente.put("partita_iva",pIVA.getText().toString().trim());
            }
            cliente.put("email",email.getText().toString().trim());
            cliente.put("tipo_consegna",spinner.getSelectedItemPosition() == 0 ? "Ritiro in negozio" : "Spedizione");
            cliente.put("note",note.getText().toString().trim());

            JSONArray prodotti = new JSONArray();
            List<Product> productsToInsert = type.equals("cart") ? SharedUtils.getProductsFromCart(getContext()) : mListener.getSelectedPromprod().getProducts();
            for (Product p : productsToInsert) {
                if (p.getQuantity() > 0) {
                    JSONObject product = new JSONObject();
                    product.put("id",p.getId());
                    product.put("codice",p.getCode());
                    product.put("nome",p.getTitle());
                    product.put("quantita",""+p.getQuantity());
                    if (type.equals("promotion")) product.put("prezzo_promozione",String.format(Locale.ITALY,"%.3f",
                            Statics.getFinalPrice(getContext(),p)));
                    if (type.equals("cart")) {
                        product.put("prezzo",String.format(Locale.ITALY,"%.3f",Statics.getFinalPrice(getContext(),p)));
                    }
                    product.put("iva",""+p.getIVA());
                    prodotti.put(product);
                }
            }
            if (type.equals("promotion")) {
                JSONObject promozione = new JSONObject();
                promozione.put("id", mListener.getSelectedPromprod().getId());
                promozione.put("nome", mListener.getSelectedPromprod().getTitle());
                promozione.put("prodotti", prodotti);
                request.put("promozione",promozione);
            }
            request.put("cliente",cliente);
            if (type.equals("cart")) {
                JSONObject items = new JSONObject();
                items.put("prodotti",prodotti);
                request.put("carrello",items);
            }

        }
        catch (JSONException e) { e.printStackTrace(); }
        Log.d("request",request.toString());

        return request;
    }
}
