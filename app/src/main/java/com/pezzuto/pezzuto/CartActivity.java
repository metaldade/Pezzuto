package com.pezzuto.pezzuto;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.util.ArraySet;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.dd.CircularProgressButton;
import com.pezzuto.pezzuto.adapter.CartListViewAdapter;
import com.pezzuto.pezzuto.adapter.PromotionListViewAdapter;
import com.pezzuto.pezzuto.items.Product;
import com.pezzuto.pezzuto.listeners.OnCartInteractionListener;
import com.pezzuto.pezzuto.ui.GraphicUtils;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class CartActivity extends AppCompatActivity implements OnCartInteractionListener {
    Button order;
    CircularProgressButton orderButton;
    int state = 1;
    BottomBuyFragment bottomBuyFragment = null;
    Menu menu;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        TextView emptyIcon = (TextView) findViewById(R.id.emptyIcon);
        if (SharedUtils.isCartEmpty(getApplicationContext())) {
            emptyIcon.setVisibility(View.VISIBLE);
        }
        else loadCartFragment();
        order = (Button) findViewById(R.id.order);
        orderButton = (CircularProgressButton) findViewById(R.id.orderButton);
        orderButton.setText("Avanti");
        orderButton.setIndeterminateProgressMode(true);
        setListeners();
    }
    public void loadCartFragment() {
        bottomBuyFragment = BottomBuyFragment.newInstance("cart");
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.contentContainer, bottomBuyFragment).commit();
    }
    public void setListeners() {
        orderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SharedUtils.isCartEmpty(getApplicationContext())) Toast.makeText(getApplicationContext(), "Inserisci almeno un elemento nel carrello per poter procedere",Toast.LENGTH_SHORT).show();
                else if (state == 1) {
                    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                    ft.replace(R.id.contentContainer, CustomerInfoFragment.newInstance("cart")).commit();
                    orderButton.setText("Ordina");
                    menu.findItem(R.id.edit).setVisible(false);
                    state = 2;
                }
            }
        });
    }

    public void startProgress() {
        orderButton.setProgress(50);
    }
    public void endProgressSuccessfully() {
        orderButton.setProgress(100);
    }
    public void endProgressWithError() {
        Toast.makeText(this,"Errore, prego riprovare.",Toast.LENGTH_SHORT).show();
        orderButton.setProgress(-1);
    }
    public CircularProgressButton getCartButton() { return orderButton; }

    @Override
    public void onBackPressed() {
        if (state == 1 && !SharedUtils.isCartEmpty(getApplicationContext()) && bottomBuyFragment.isModifying()) {
            restoreBaseState();
        }
        else if (state == 2) {
            bottomBuyFragment = BottomBuyFragment.newInstance("cart");
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.contentContainer, bottomBuyFragment).commit();
            state = 1;
            orderButton.setText("Avanti");
            setListeners();
        }
        else super.onBackPressed();
    }
    public void restoreBaseState() {
        bottomBuyFragment.undoRemoving();
        bottomBuyFragment.goModify(false);
        menu.findItem(R.id.edit).setIcon(R.drawable.ic_edit);
        menu.findItem(R.id.remove).setVisible(false);
    }
    public void restoreCartButton() {
        orderButton.setProgress(0);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.modify, menu);
        menu.findItem(R.id.remove).setVisible(false);
        this.menu = menu;
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.edit:
                if (bottomBuyFragment != null && bottomBuyFragment.isModifying()) {
                      restoreBaseState();
                    menu.findItem(R.id.edit).setIcon(R.drawable.ic_edit);
                }
                else if (SharedUtils.isCartEmpty(getApplicationContext())) Toast.makeText(this,"Non ci sono elementi nel carrello",Toast.LENGTH_SHORT).show();
                else {
                    bottomBuyFragment.goModify(true);
                    menu.findItem(R.id.edit).setIcon(R.drawable.ic_close);
                    menu.findItem(R.id.remove).setVisible(true);
                }
                return super.onOptionsItemSelected(item);
            case R.id.remove:
                if (!bottomBuyFragment.isAtLeastOneChecked()) Toast.makeText(this,"Selezionare almeno un elemento da eliminare", Toast.LENGTH_SHORT).show();
                else {
                    bottomBuyFragment.removeSelected();
                    restoreBaseState();
                }
                return super.onOptionsItemSelected(item);
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
