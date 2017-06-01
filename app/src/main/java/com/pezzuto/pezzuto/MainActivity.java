package com.pezzuto.pezzuto;


import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.android.volley.Response;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.github.jorgecastilloprz.FABProgressCircle;
import com.github.jorgecastilloprz.listeners.FABProgressListener;
import com.pezzuto.pezzuto.items.Event;
import com.pezzuto.pezzuto.items.Product;
import com.pezzuto.pezzuto.items.Promprod;
import com.pezzuto.pezzuto.listeners.OnFragmentInteractionListener;
import com.pezzuto.pezzuto.requests.RequestsUtils;
import com.pezzuto.pezzuto.ui.StickyHeaderFragment;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabReselectListener;
import com.roughike.bottombar.OnTabSelectListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity implements OnFragmentInteractionListener,FABProgressListener {
    private Promprod selected;
    private Product prodSelected;
    private Event eventSelected;
    private FloatingActionButton fab;
    private BottomSheetBehavior bottomSheetBehavior;
    private FrameLayout llBottomSheet;
    private boolean firstAccessProm = true;
    private boolean firstAccessProd = true;
    private boolean firstAccessEvent = true;
    private FABProgressCircle progressCircle;
    private SwipeRefreshLayout swipe;
    private RefreshableFragment lastFragment;
    private HashMap<String,Integer> categories = new HashMap<>();

    //type of fragments
    public static final String PROMOTION_DETAIL = "promotion_detail";
    public static final String PRODUCT_DETAIL = "product_detail";

    Response.Listener<JSONArray> parseCatResponse = new Response.Listener<JSONArray>() {
        @Override
        public void onResponse(JSONArray response) {
            try {
                for (int i = 0; i < response.length(); i++) {
                    String cat = ((JSONObject) response.get(i)).getString("nome");
                    Integer id = ((JSONObject) response.get(i)).getInt("id");
                    categories.put(cat,id);
                }
            }
            catch (JSONException e) { e.printStackTrace(); }
        }
    };
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Dove caricare i fragment
        FrameLayout contentContainer = (FrameLayout) findViewById(R.id.contentContainer);
        fab = (FloatingActionButton)  findViewById(R.id.cartFab);
        swipe = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                lastFragment.refresh();
            }
        });
        //categories request
        RequestsUtils.sendRequest(this,RequestsUtils.CATEGORIE,RequestsUtils.NO_FILTER,null,parseCatResponse);

        progressCircle = (FABProgressCircle) findViewById(R.id.fabProgressCircle);
        progressCircle.attachListener(this);

        // get the bottom sheet view
        llBottomSheet = (FrameLayout) findViewById(R.id.bottomsheet);

// init the bottom sheet behavior
        bottomSheetBehavior = BottomSheetBehavior.from(llBottomSheet);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

        BottomBar bottomBar = (BottomBar) findViewById(R.id.bottomBar);
        bottomBar.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelected(@IdRes int tabId) {
                if (tabId == R.id.tab_promozioni) {
                    if (firstAccessProm) {
                        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                        RefreshableFragment f = StickyHeaderFragment.newInstance("promotions");
                        ft.replace(R.id.contentContainer,f , "promotions").commit();
                        lastFragment = f;
                        ft.addToBackStack(null);
                        firstAccessProm = false;
                    }
                    else {
                        launchFragment("promotions");
                        setFabVisible(false);
                    }
                }
                if (tabId == R.id.tab_prodotti) {
                    if (firstAccessProd) {
                        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                        RefreshableFragment f = StickyHeaderFragment.newInstance("products");
                        ft.replace(R.id.contentContainer, f, "products").commit();
                        lastFragment = f;
                        ft.addToBackStack(null);
                        firstAccessProd = false;
                    }
                    else {
                        launchFragment("products");
                        setProdFab();
                    }
                }
                if (tabId == R.id.tab_eventi) {
                    if (firstAccessEvent) {
                        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                        RefreshableFragment f = StickyHeaderFragment.newInstance("events");
                        ft.replace(R.id.contentContainer, f, "events").commit();
                        lastFragment = f;
                        ft.addToBackStack(null);
                        firstAccessEvent = false;
                    }
                    else {
                        launchFragment("events");
                        setFabVisible(false);
                    }
                }
            }
        });
        bottomBar.setOnTabReselectListener(new OnTabReselectListener() {
            @Override
            public void onTabReSelected(@IdRes int tabId) {
                if (tabId == R.id.tab_promozioni) {
                    launchFragment("promotions");
                }
            }
        });
    }
    public void setProdFab() {
        fab.setIcon(R.drawable.ic_filter);
        setFabVisible(true);
        setProductSheetBehaviour();
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchFilterSheet();

            }
        });
    }
    public void launchFragment(RefreshableFragment fragment) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.contentContainer, fragment).commit();
        ft.addToBackStack(null);
        lastFragment = fragment;
    }
    public void launchFragment(RefreshableFragment fragment, String tag) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.contentContainer, fragment, tag).commit();
        ft.addToBackStack(null);
        lastFragment = fragment;
    }
    public void launchFragment(String key) {
        if (key.equals("products")) {
            setProductSheetBehaviour();
            setProdFab();
        }
        else if (key.equals("promotions")) {
            setPromotionSheetBehaviour();
            setFabVisible(false);
        }
        RefreshableFragment f = (RefreshableFragment) getSupportFragmentManager().findFragmentByTag(key);
        launchFragment(f);
        lastFragment = f;
    }
    public void startProgress() {
        progressCircle.show();
    }
    public void endProgressSuccessfully() {
        progressCircle.beginFinalAnimation();
    }
    public void endProgressWithError() {
        progressCircle.hide();
    }
    public void launchProductFragment(int category) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        RefreshableFragment f = StickyHeaderFragment.newProdInstance(category);
        ft.replace(R.id.contentContainer, f, "products").commit();
        ft.addToBackStack(null);
        lastFragment = f;
    }

    public void setSelectedPromprod(Promprod p) {
        selected = p;
    }
    public void setSelectedEvent(Event e) {
        eventSelected = e;
    }
    public Product getSelectedProduct() {
        return prodSelected;
    }
    public Promprod getSelectedPromprod() {
        return selected;
    }
    public Event getSelectedEvent() {
        return eventSelected;
    }
    public void setSelectedProduct(Product p) {
        prodSelected = p;
    }
    public void setFabVisible(boolean visible) {
        if (visible) {
            progressCircle.setVisibility(View.VISIBLE);
            progressCircle.animate().scaleX(1).scaleY(1).setDuration(300).start();
        }
        else {
            progressCircle.animate().scaleX(0).scaleY(0).setDuration(300).start();
        }
    }

    public FloatingActionButton getFab() { return fab; }
    public void launchSheet() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.bottomsheet, new BottomBuyFragment()).commit();
        fab.setIcon(R.drawable.ic_arrow);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
    }
    public void launchSheetInfoFragment() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.bottomsheet, new CustomerInfoFragment()).commit();
        fab.setIcon(R.drawable.ic_cart);
    }
    public void launchFilterSheet() {

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.bottomsheet, new BottomFilterFragment()).commit();
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
    }
    @Override
    public void onBackPressed()
    {
        if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED || bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_DRAGGING )
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        else if (lastFragment.getType().equals(PROMOTION_DETAIL)) {
            launchFragment("promotions");
        }
        else if (lastFragment.getType().equals(PRODUCT_DETAIL)) {
            launchFragment("products");
        }
        else if (getFragmentManager().getBackStackEntryCount() > 0) {
            getFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
    }
    public void stopRefresh() {
        swipe.setRefreshing(false);
    }
    public HashMap<String,Integer> getCategories() {
        return categories;
    }
    public BottomSheetBehavior getBottomSheetBehavior() { return bottomSheetBehavior; }
    public void setProductSheetBehaviour() {
        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                fab.animate().scaleX(1 - slideOffset).scaleY(1 - slideOffset).setDuration(0).start();
            }
        });
    }
    public void setPromotionSheetBehaviour() {
        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_HIDDEN ||
                        newState == BottomSheetBehavior.STATE_COLLAPSED) fab.setIcon(R.drawable.ic_cart);
                if (newState == BottomSheetBehavior.STATE_DRAGGING) {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                if (slideOffset < 0.5) {
                    fab.setIcon(R.drawable.ic_cart);
                    fab.setOnClickListener(sheetListener);
                }
                else {
                    fab.setIcon(R.drawable.ic_arrow);
                    fab.setOnClickListener(goOnListener);
                }
            }
        });
    }
    View.OnClickListener goOnListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (isAlmostOneSelected(selected.getProducts()))
                launchSheetInfoFragment();
            else Toast.makeText(getApplicationContext(),"Scegli almeno un prodotto",Toast.LENGTH_SHORT).show();
        }
    };
    View.OnClickListener sheetListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
                launchSheet();
        }
    };
    public static boolean isAlmostOneSelected(List<Product> products) {
        for (Product p : products) {
            if (p.getQuantity() > 0) return true;
        }
        return false;
    }
    private void restorePromoFAB() {
        fab.setIcon(R.drawable.ic_cart);
        fab.setColorNormal(R.color.colorAccent);
    }
    @Override public void onFABProgressAnimationEnd() {
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
    }
}
