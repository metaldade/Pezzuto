package com.pezzuto.pezzuto;


import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.ArraySet;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.github.jorgecastilloprz.FABProgressCircle;
import com.github.jorgecastilloprz.listeners.FABProgressListener;
import com.pezzuto.pezzuto.items.Event;
import com.pezzuto.pezzuto.items.Product;
import com.pezzuto.pezzuto.items.Promprod;
import com.pezzuto.pezzuto.listeners.OnFragmentInteractionListener;
import com.pezzuto.pezzuto.requests.RequestsUtils;
import com.pezzuto.pezzuto.ui.StickyHeaderFragment;
import com.pezzuto.pezzuto.ui.UiUtils;
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
    private TextView emptyState;
    private HashMap<String,Integer> categories = new HashMap<>();
    private Menu search_menu;
    private Toolbar toolbar;
    private BottomBar bottomBar;
    private RadioGroup radioChoice;
    //type of fragments
    public static final String PROMOTION_DETAIL = "promotion_detail";
    public static final String PRODUCT_DETAIL = "product_detail";
    public static final String PROMOTIONS = "promotions";
    public static final String PRODUCTS = "products";
    public static final String EVENTS = "events";
    public static final String PROD_FILTER = "prod_filter";
    public static final String PROD_SEARCH = "prod_search";

    public static final int FIRST_RUN_CODE = 0;

    //Activity requests
    public static final int IS_CART_EMPTY= 1;

    private boolean isSearchVisible;
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
        categories.put("In primo piano",0);
        //Dove caricare i fragment
        FrameLayout contentContainer = (FrameLayout) findViewById(R.id.contentContainer);
        fab = (FloatingActionButton)  findViewById(R.id.cartFab);
        swipe = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        radioChoice = (RadioGroup) findViewById(R.id.radioChoice);
        swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                lastFragment.refresh();
            }
        });

        //categories request
        RequestsUtils.sendRequest(this,RequestsUtils.CATEGORIE,RequestsUtils.NO_FILTER,null,parseCatResponse);
        emptyState = (TextView) findViewById(R.id.emptyIcon);
        progressCircle = (FABProgressCircle) findViewById(R.id.fabProgressCircle);
        progressCircle.attachListener(this);

        //First Run Check
        if (SharedUtils.isFirstRun(this)) {
            Intent intent = new Intent(this, FirstRunActivity.class);
            startActivityForResult(intent,FIRST_RUN_CODE);
        }

        handleIntent(getIntent());




        // get the bottom sheet view
        llBottomSheet = (FrameLayout) findViewById(R.id.bottomsheet);


// init the bottom sheet behavior
        bottomSheetBehavior = BottomSheetBehavior.from(llBottomSheet);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);


        bottomBar = (BottomBar) findViewById(R.id.bottomBar);
        bottomBar.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelected(@IdRes int tabId) {
                setTitle("Promozioni");
                if (!firstAccessProm) search_menu.findItem(R.id.search).collapseActionView();
                if (tabId == R.id.tab_promozioni) {
                    isSearchVisible = false;
                    if (firstAccessProm) {
                        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                        RefreshableFragment f = StickyHeaderFragment.newInstance(PROMOTIONS);
                        ft.replace(R.id.contentContainer,f , PROMOTIONS).commit();
                        lastFragment = f;
                        ft.addToBackStack(null);
                        firstAccessProm = false;
                    }
                    else {
                        launchFragment(PROMOTIONS);
                        setFabVisible(false);
                    }
                }
                if (tabId == R.id.tab_prodotti) {
                    setTitle("Prodotti");
                    isSearchVisible = true;
                    if (firstAccessProd) {
                        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                        RefreshableFragment f = StickyHeaderFragment.newInstance(PRODUCTS);
                        ft.replace(R.id.contentContainer, f, PRODUCTS).commit();
                        lastFragment = f;
                        ft.addToBackStack(null);
                        firstAccessProd = false;
                    }
                    else {
                        launchFragment(PRODUCTS);
                        setProdFab();
                    }
                }
                if (tabId == R.id.tab_eventi) {
                    setTitle("Eventi");
                    isSearchVisible = false;
                    if (firstAccessEvent) {
                        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                        RefreshableFragment f = StickyHeaderFragment.newInstance(EVENTS);
                        ft.replace(R.id.contentContainer, f, EVENTS).commit();
                        lastFragment = f;
                        ft.addToBackStack(null);
                        firstAccessEvent = false;
                    }
                    else {
                        launchFragment(EVENTS);
                        setFabVisible(false);
                    }
                }
            }
        });
        bottomBar.setOnTabReselectListener(new OnTabReselectListener() {
            @Override
            public void onTabReSelected(@IdRes int tabId) {
                if (tabId == R.id.tab_promozioni) {
                    launchFragment(PROMOTIONS);
                }
                if (tabId == R.id.tab_prodotti) {
                    launchFragment(PRODUCTS);
                }
                if (tabId == R.id.tab_eventi) {
                    launchFragment(EVENTS);
                }
            }
        });
    }
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        handleIntent(intent);
    }
    private void handleIntent(Intent intent) {

        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            int radioId = radioChoice.getCheckedRadioButtonId();
            String scope = radioId == R.id.radioName ? "nome" : "marca";
            Log.d("prova",scope);
            doSearch(scope,query);
        }
    }
    public void doSearch(String scope, String query) {
        search_menu.findItem(R.id.search).collapseActionView();
        removeEmptyState();
        launchProductFragment(scope,query);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.cart, menu);
        inflater.inflate(R.menu.search_menu, menu);
        inflater.inflate(R.menu.contacts, menu);
        search_menu = menu;
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        checkCartIcon();
        final SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    setToolbarVisible(false);
                    setFabVisible(true);
                    setBottomBarVisible(true);
                }
                else {
                    setToolbarVisible(true);
                    setFabVisible(false);
                    setBottomBarVisible(false);
                }
            }
        });
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(false);
        setSearchVisible(isSearchVisible);
        return true;
    }
    public void checkCartIcon() {
        if (lastFragment.getType().equals(PROMOTIONS) || lastFragment.getType().equals(EVENTS)) search_menu.findItem(R.id.cartMenu).setVisible(false);
        else if (isCartEmpty()) search_menu.findItem(R.id.cartMenu).setIcon(R.drawable.ic_cart_empty);
        else search_menu.findItem(R.id.cartMenu).setIcon(R.drawable.ic_cart);
    }
    public void hideCartMenu() {
        search_menu.findItem(R.id.cartMenu).setVisible(false);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.cartMenu:
                Intent intent = new Intent(this,CartActivity.class);
                startActivityForResult(intent, IS_CART_EMPTY);
                return true;
            case R.id.contactsMenu:
                Intent intent2 = new Intent(this,ContactsActivity.class);
                startActivity(intent2);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == IS_CART_EMPTY) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                search_menu.findItem(R.id.cartMenu).setIcon(R.drawable.ic_cart_empty);
                if (lastFragment.getType().equals(PRODUCT_DETAIL)) fab.setIcon(R.drawable.ic_add_shopping_cart);
            }
            else search_menu.findItem(R.id.cartMenu).setIcon(R.drawable.ic_cart);
        }
        if (requestCode == FIRST_RUN_CODE) {
            if (resultCode == FirstRunActivity.CLOSE_APP) finish();
        }
    }
    private void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public void setBottomBarVisible(boolean isVisible) {
        if (isVisible) {
            bottomBar.setVisibility(View.VISIBLE);
        }
        else {
            bottomBar.setVisibility(View.GONE);
        }
    }
    public void setSearchVisible(boolean isVisible) {
        if (isVisible) search_menu.findItem(R.id.search).setVisible(true);
        else search_menu.findItem(R.id.search).setVisible(false);
    }
    public void setProdFab() {
        fab.setIcon(R.drawable.ic_filter);
        setFabVisible(true);
        setProductSheetBehaviour();
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                search_menu.findItem(R.id.search).collapseActionView();
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
        if (tag.equals(PRODUCTS)) isSearchVisible = true;
        else isSearchVisible = false;
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.contentContainer, fragment, tag).commit();
        ft.addToBackStack(null);
        lastFragment = fragment;
    }
    public void launchFragment(String key) {
        if (key.equals(PRODUCTS)) isSearchVisible = true;
        else isSearchVisible = false;
        if (key.equals(PRODUCTS)) {
            setProductSheetBehaviour();
            setProdFab();
        }
        else if (key.equals(PROMOTIONS)) {
            setPromotionSheetBehaviour();
            setFabVisible(false);
        }
        RefreshableFragment f = (RefreshableFragment) getSupportFragmentManager().findFragmentByTag(key);
        if (f.hasEmptySet(key)) setEmptyState(key);
        else removeEmptyState();
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
        ft.replace(R.id.contentContainer, f, PRODUCTS).commit();
        ft.addToBackStack(null);
        lastFragment = f;
    }
    public void refreshCurrentFragment() {
        lastFragment.refresh();
    }
    public void launchProductFragment(String scope, String query) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        RefreshableFragment f = StickyHeaderFragment.newProdInstance(scope,query);
        ft.replace(R.id.contentContainer, f, PRODUCTS).commit();
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
    public void setToolbarVisible(boolean isVisible) {
       Animation animationFadeIn = AnimationUtils.loadAnimation(this, R.anim.fadein);
       Animation animationFadeOut = AnimationUtils.loadAnimation(this, R.anim.fadeout);
        animationFadeOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                toolbar.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        if (isVisible) {
            toolbar.setVisibility(View.VISIBLE);
            toolbar.startAnimation(animationFadeIn);
        }
        else {
            toolbar.startAnimation(animationFadeOut);

        }
    }
    public void removeSearchMenuVisibility() {
        isSearchVisible = false;
    }

    public FloatingActionButton getFab() { return fab; }
    public void launchSheet() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.bottomsheet, BottomBuyFragment.newInstance("promotion")).commit();
        fab.setIcon(R.drawable.ic_arrow);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
    }
    public void launchSheetInfoFragment() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.bottomsheet, CustomerInfoFragment.newInstance("promotion")).commit();
        fab.setIcon(R.drawable.ic_cart);
    }
    public void launchEventInfoFragment() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.bottomsheet, new EventInfoFragment()).commit();
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
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
        else if (swipe.isRefreshing()) stopRefresh();
        else if (lastFragment.getType().equals(PROMOTION_DETAIL)) {
            launchFragment(PROMOTIONS);
        }
        else if (lastFragment.getType().equals(PRODUCT_DETAIL)) {
            launchFragment(PRODUCTS);
        }
        else {
            finish();
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
                if (newState == BottomSheetBehavior.STATE_HIDDEN ||
                        newState == BottomSheetBehavior.STATE_COLLAPSED) setFabVisible(true);
                else setFabVisible(false);
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
            }

        });
    }
    public void setPromotionSheetBehaviour() {
        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
               // if (newState == BottomSheetBehavior.STATE_HIDDEN ||
                   //     newState == BottomSheetBehavior.STATE_COLLAPSED) fab.setIcon(R.drawable.ic_cart);
              /*  if (newState == BottomSheetBehavior.STATE_DRAGGING || newState == BottomSheetBehavior.STATE_SETTLING) {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                }*/
                if (newState == BottomSheetBehavior.STATE_HIDDEN ||
                        newState == BottomSheetBehavior.STATE_COLLAPSED) setFabVisible(true);
                else setFabVisible(false);
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
    public void setEventSheetBehaviour() {
        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
              /*  if (newState == BottomSheetBehavior.STATE_DRAGGING || newState == BottomSheetBehavior.STATE_SETTLING
                        || newState == BottomSheetBehavior.STATE_COLLAPSED) {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                }*/
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });
    }
    View.OnClickListener goOnListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            goOnProm();
        }
    };
    public void goOnProm(){
        if (isAlmostOneSelected(selected.getProducts()))
            launchSheetInfoFragment();
        else Toast.makeText(getApplicationContext(),"Scegli almeno un prodotto",Toast.LENGTH_SHORT).show();
    }
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
        UiUtils.createOrderDoneDialog(this);
    }
    public void setEmptyState(String type) {
        emptyState.setVisibility(View.VISIBLE);
        switch (type) {
            case PROMOTIONS:
                emptyState.setCompoundDrawablesWithIntrinsicBounds(0,R.drawable.ic_grade_black_24px,0,0);
                emptyState.setText("Nessuna promozione attiva");
                break;
            case PRODUCTS:
                emptyState.setCompoundDrawablesWithIntrinsicBounds(0,R.drawable.ic_local_offer_black_24px,0,0);
                emptyState.setText("Nessun prodotto trovato");
                break;
            case EVENTS:
                emptyState.setCompoundDrawablesWithIntrinsicBounds(0,R.drawable.ic_event_black_24px,0,0);
                emptyState.setText("Nessun evento attivo");
                break;
            case PROD_FILTER:
                emptyState.setCompoundDrawablesWithIntrinsicBounds(0,R.drawable.ic_filter_black,0,0);

                emptyState.setText("Nessun prodotto trovato in questa categoria");
            case PROD_SEARCH:
                emptyState.setCompoundDrawablesWithIntrinsicBounds(0,R.drawable.ic_search_black,0,0);
                emptyState.setText("La ricerca non ha prodotto nessun risultato");
                break;
            default:
                emptyState.setVisibility(View.GONE);
        }
    }
    public void removeEmptyState() {
        emptyState.setVisibility(View.GONE);
    }
    public void collapseSearchView() {
        search_menu.findItem(R.id.search).collapseActionView();
    }
    public void disableSwipeRefresh() {
        swipe.setEnabled(false);
    }
    public void enableSwipeRefresh() {
        swipe.setEnabled(true);
    }
    public boolean isCartEmpty() {
        SharedPreferences shre = getSharedPreferences(Statics.SHARED_PREF+"-cart", Context.MODE_PRIVATE);
        return shre.getStringSet("products", new ArraySet<String>()).isEmpty();
    }
    public void setCartIconFull() {
        search_menu.findItem(R.id.cartMenu).setIcon(R.drawable.ic_cart);
    }
    public void setCartIconEmpty() {
        search_menu.findItem(R.id.cartMenu).setIcon(R.drawable.ic_cart_empty);
    }
}
