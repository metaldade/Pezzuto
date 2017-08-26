package com.pezzuto.pezzuto;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.pezzuto.pezzuto.ui.GraphicUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ContactsActivity extends AppCompatActivity {
    ListView phones;
    ListView emails;
    ListView geos;
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
        GraphicUtils.setListViewHeightBasedOnChildren(phones,0);
        GraphicUtils.setListViewHeightBasedOnChildren(emails,0);
        GraphicUtils.setListViewHeightBasedOnChildren(geos,0);

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