package com.pezzuto.pezzuto;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.pezzuto.pezzuto.requests.RequestsUtils;
import com.pezzuto.pezzuto.ui.GraphicUtils;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ContactsActivity extends AppCompatActivity {
    ListView phones;
    ListView emails;
    ListView geos;
    ListView orari;
    Button conditions;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);
        setTitle("Contatti");
        phones = (ListView) findViewById(R.id.phones);
        emails = (ListView) findViewById(R.id.emails);
        geos = (ListView) findViewById(R.id.geos);
        conditions = (Button) findViewById(R.id.conditions);
        orari = (ListView) findViewById(R.id.orari);

        SimpleAdapter adapterGeos = new SimpleAdapter(this, fillGeos(),
                R.layout.base_list_item,
                new String[] {"content", "type"},
                new int[] {R.id.text1,
                        R.id.text2});
        geos.setAdapter(adapterGeos);
        geos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    Uri gmmIntentUri = Uri.parse("geo:0,0?q=Viale della Repubblica 43, Lecce");
                    Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                    mapIntent.setPackage("com.google.android.apps.maps");
                    startActivity(mapIntent);
                }
            }
        });

        SimpleAdapter adapterPhones = new SimpleAdapter(this, fillPhones(),
                R.layout.base_list_item,
                new String[] {"content", "type"},
                new int[] {R.id.text1,
                        R.id.text2});

        phones.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) dial("0832240041");
            }
        });
        phones.setAdapter(adapterPhones);

        SimpleAdapter adapterEmails = new SimpleAdapter(this, fillEmails(),
                R.layout.base_list_item,
                new String[] {"content", "type"},
                new int[] {R.id.text1,
                        R.id.text2});
        emails.setAdapter(adapterEmails);
        emails.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        sendMail("info@pezzuto.it");
                        break;
                    case 1:
                        sendMail("ordinipezzuto@gmail.com");
                        break;
                    case 2:
                        sendMail("amministrazione@pezzuto.it");
                        break;
                    case 3:
                        sendMail("pezzutosrl@pec.pezzuto.it");
                        break;
                    default:
                        break;
                }
            }
        });
        setAdapterOrari();

        GraphicUtils.setListViewHeightBasedOnChildren(phones,0);
        GraphicUtils.setListViewHeightBasedOnChildren(emails,0);
        GraphicUtils.setListViewHeightBasedOnChildren(geos,0);
        GraphicUtils.setListViewHeightBasedOnChildren(orari,0);
        conditions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadURL("http://www.pezzuto.it/condizioni-vendita");
            }
        });
    }
    public void dial(String num) {
        Uri call = Uri.parse("tel:" + num);
        Intent surf = new Intent(Intent.ACTION_DIAL, call);
        startActivity(surf);
    }
    public void setAdapterOrari() {
        SimpleAdapter adapterOrari = new SimpleAdapter(this, fillOrari(),
                R.layout.base_list_item,
                new String[] {"content", "type"},
                new int[] {R.id.text1,
                        R.id.text2});
        orari.setAdapter(adapterOrari);
    }
    public List<Map<String, String>> fillPhones() {

        List<Map<String, String>> data = new ArrayList<Map<String, String>>();

        Map<String, String> datum1 = new HashMap<String, String>(2);
        datum1.put("type","Telefono");
        datum1.put("content","0832/240041" );

        Map<String, String> datum2 = new HashMap<String, String>(2);
        datum2.put("type","FAX");
        datum2.put("content", "0832/256057");
        data.add(datum1);
        data.add(datum2);
        return data;
    }
    public List<Map<String, String>> fillGeos() {

        List<Map<String, String>> data = new ArrayList<Map<String, String>>();


        Map<String, String> datum1 = new HashMap<String, String>(2);
        datum1.put("type","Sede");
        datum1.put("content", "Viale della Repubblica 43, Lecce");
        data.add(datum1);
        return data;
    }
    public List<Map<String, String>> fillEmails() {

        List<Map<String, String>> data = new ArrayList<Map<String, String>>();

        Map<String, String> datum1 = new HashMap<String, String>(2);
        datum1.put("type","Generale");
        datum1.put("content","info@pezzuto.it");

        Map<String, String> datum2 = new HashMap<String, String>(2);
        datum2.put("type","Ordini");
        datum2.put("content", "ordinipezzuto@gmail.com");

        Map<String, String> datum3 = new HashMap<String, String>(2);
        datum3.put("type","Amministrazione");
        datum3.put("content", "amministrazione@pezzuto.it");

        Map<String, String> datum4 = new HashMap<String, String>(2);
        datum4.put("type","PEC");
        datum4.put("content", "pezzutosrl@pec.pezzuto.it");

        data.add(datum1);
        data.add(datum2);
        data.add(datum3);
        data.add(datum4);
        return data;
    }
    public List<Map<String, String>> fillOrari() {
        SharedPreferences shre = getSharedPreferences(Statics.SHARED_PREF, Context.MODE_PRIVATE);
        if (shre.getString("orarioShowroomInfra","").equals("")) {
            RequestsUtils.sendOrariRequest(this,
                    new Response.Listener<JSONArray>() {
                        @Override
                        public void onResponse(JSONArray response) {
                            try {
                                SharedUtils.saveOrari(getApplicationContext(),response);
                                setAdapterOrari();
                            }
                            catch(JSONException ex) { ex.printStackTrace(); }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(getApplicationContext(),"Errore nel server, impossibile caricare gli orari", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
        List<Map<String, String>> data = new ArrayList<Map<String, String>>();

        Map<String, String> datum1 = new HashMap<String, String>(2);
        datum1.put("type","Showroom (Lun-Ven)");
        datum1.put("content",shre.getString("orarioShowroomInfra","8:00 - 13:00 / 16:00 - 19:00"));

        Map<String, String> datum2 = new HashMap<String, String>(2);
        datum2.put("type","Showroom (Sab)");
        datum2.put("content", shre.getString("orarioShowroomWE","8:00 - 12:00"));

        Map<String, String> datum3 = new HashMap<String, String>(2);
        datum3.put("type","Uffici (Lun-Ven)");
        datum3.put("content", shre.getString("orarioUfficiInfra","8:00 - 13:00 / 16:00 - 19:00"));

        Map<String, String> datum4 = new HashMap<String, String>(2);
        datum4.put("type","Uffici (Sab)");
        datum4.put("content", shre.getString("orarioUfficiWE","8:00 - 12:00"));

        Map<String, String> datum5 = new HashMap<String, String>(2);
        datum5.put("type","Magazzino (Lun-Ven)");
        datum5.put("content", shre.getString("orarioMagazzinoInfra","8:00 - 19:00"));

        Map<String, String> datum6 = new HashMap<String, String>(2);
        datum6.put("type","Magazzino (Sab)");
        datum6.put("content", shre.getString("orarioMagazzinoWE","8:00 - 12:00"));

        data.add(datum1);
        data.add(datum2);
        data.add(datum3);
        data.add(datum4);
        data.add(datum5);
        data.add(datum6);
        return data;
    }

    public void sendMail(String mail) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("message/rfc822");
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{ mail});

        startActivity(Intent.createChooser(intent, "Send Email"));
    }
    public void loadURL(String url) {
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);
    }
}
