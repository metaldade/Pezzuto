package com.pezzuto.pezzuto;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.GetChars;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.dd.CircularProgressButton;
import com.pezzuto.pezzuto.listeners.OnFirstRunInteractionListener;
import com.pezzuto.pezzuto.requests.RequestsUtils;
import com.pezzuto.pezzuto.ui.GraphicUtils;
import com.pezzuto.pezzuto.ui.UiUtils;

import org.json.JSONException;
import org.json.JSONObject;


public class ClientAuthenticationFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

    private SharedPreferences shre;
    SharedPreferences.Editor editor;
    private ScrollView auth;
    private RelativeLayout choice;
    private Button installatore, privato;
    LinearLayout installatoreBox, privatoBox;
    private CircularProgressButton login, codiceAzienda;
    private EditText ragioneSociale, pIVA, email, code;
    private TextInputLayout emailLayout, ragioneSocialeLayout, pIVALayout, codeLayout;
    OnFirstRunInteractionListener mListener;
    public ClientAuthenticationFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ClientAuthenticationFragment.
     */
    // TODO: Rename and change types and number of parameters
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =  inflater.inflate(R.layout.fragment_client_authentication, container, false);
        shre =  getContext().getSharedPreferences(Statics.SHARED_PREF, Context.MODE_PRIVATE);
        editor = shre.edit();

        installatore = (Button) v.findViewById(R.id.installatore);
        privato = (Button) v.findViewById(R.id.privato);
        installatoreBox = (LinearLayout) v.findViewById(R.id.installatoreBox);
        privatoBox = (LinearLayout) v.findViewById(R.id.privatoBox);
        auth = (ScrollView) v.findViewById(R.id.auth);
        choice = (RelativeLayout) v.findViewById(R.id.choice);

        ragioneSociale = (EditText) v.findViewById(R.id.ragione_sociale);
        ragioneSocialeLayout = (TextInputLayout) v.findViewById(R.id.layout_ragione_sociale);
        pIVA = (EditText) v.findViewById(R.id.partita_iva);
        pIVALayout = (TextInputLayout) v.findViewById(R.id.layout_partita_iva);
        email = (EditText) v.findViewById(R.id.email);
        code = (EditText) v.findViewById(R.id.code);
        codeLayout = (TextInputLayout) v.findViewById(R.id.layout_code);
        emailLayout = (TextInputLayout) v.findViewById(R.id.layout_email);
        ragioneSociale.addTextChangedListener(new InfoTextWatcher(ragioneSociale));
        email.addTextChangedListener(new InfoTextWatcher(email));
        pIVA.addTextChangedListener(new InfoTextWatcher(pIVA));
        Button close = (Button) v.findViewById(R.id.close);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GraphicUtils.setVisibleWithFading(getContext(),choice,true);
                GraphicUtils.setVisibleWithFading(getContext(),auth,false);
            }
        });
        login = (CircularProgressButton) v.findViewById(R.id.login);
        codiceAzienda = (CircularProgressButton) v.findViewById(R.id.codiceAzienda);
        codiceAzienda.setIndeterminateProgressMode(true);
        codiceAzienda.setBackgroundColor(Color.WHITE);
        login.setIndeterminateProgressMode(true);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (login.getProgress() == -1) login.setProgress(0);
                else submitForm();
            }
        });
        installatore.setOnClickListener(installatoreListener);
        privato.setOnClickListener(privatoListener);
        installatoreBox.setOnClickListener(installatoreListener);
        privatoBox.setOnClickListener(privatoListener);
        codiceAzienda.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SharedUtils.isCodeRequestSent(getContext())) UiUtils.createCodeRequestSentDialog(getContext());
                else submitCodeRequest();
            }
        });
        return v;
    }
    private  View.OnClickListener installatoreListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            GraphicUtils.setVisibleWithFading(getContext(),choice,false);
            GraphicUtils.setVisibleWithFading(getContext(),auth,true);
        }
    };
    private  View.OnClickListener privatoListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            SharedUtils.noMoreFirstRun(getContext());
            SharedUtils.setPrivateMember(getContext(),true);
            getActivity().finish();
        }
    };
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
    private boolean validateCode() {
        if (code.getText().toString().trim().isEmpty()) {
            codeLayout.setError(getString(R.string.err_msg_code_empty));
            requestFocus(code);
            return false;
        }
        else return true;
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
        if (!validateCode()) {
            return;
        }
        login.setProgress(50);
        RequestsUtils.sendCodeRequest(getContext(), code.getText().toString(), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                SharedUtils.setPrivateMember(getContext(), false);
                login.setProgress(100);
                editor.commit();
                editor.apply();
                Toast.makeText(getContext(),"Benvenuto!",Toast.LENGTH_SHORT).show();
                SharedUtils.noMoreFirstRun(getContext());
                getActivity().finish();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                login.setProgress(-1);
                if (error.networkResponse.statusCode == 400)  Toast.makeText(getContext(),"Codice errato",Toast.LENGTH_SHORT).show();
                else Toast.makeText(getContext(),"Errore di connessione, prego riprovare",Toast.LENGTH_SHORT).show();
            }
        });
        //SharedUtils.setPrivateMember(getContext(),false);


    }
    private void submitCodeRequest() {
        if (!validateRagioneSociale()) {
            return;
        }

        if (!validateEmail()) {
            return;
        }

        if (!validatePIVA()) {
            return;
        }
        codiceAzienda.setProgress(50);
        RequestsUtils.obtainCodeRequest(getContext(), createJSONCodeRequest(), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                codiceAzienda.setProgress(0);
                SharedUtils.setCodeRequestSent(getContext());
                editor.commit();
                editor.apply();
                UiUtils.createCodeRequestDoneDialog(getContext(),email.getText().toString());
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                codiceAzienda.setProgress(0);
                Toast.makeText(getContext(),"Errore di connessione, prego riprovare",Toast.LENGTH_SHORT).show();
            }
        });
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
                case R.id.code:
                    validateCode();
                    break;
            }
        }
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFirstRunInteractionListener) {
            mListener = (OnFirstRunInteractionListener) context;
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
private JSONObject createJSONCodeRequest() {
    JSONObject request = new JSONObject();
    try {
        JSONObject cliente = new JSONObject();
        cliente.put("ragione_sociale",ragioneSociale.getText());
        cliente.put("partita_iva",pIVA.getText());
        cliente.put("email",email.getText());
        request.put("cliente",cliente);
    }
    catch (JSONException ex) {
        ex.printStackTrace();
    }
    return request;
}
}
