package com.pezzuto.pezzuto.listeners;

import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.Fragment;
import android.view.Menu;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.pezzuto.pezzuto.RefreshableFragment;
import com.pezzuto.pezzuto.items.Event;
import com.pezzuto.pezzuto.items.Product;
import com.pezzuto.pezzuto.items.Promprod;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by dade on 24/03/17.
 */

public interface OnFragmentInteractionListener {
    //set actual element
    void setSelectedPromprod(Promprod p);
    void setSelectedProduct(Product p);
    void setSelectedEvent(Event p);

    //get actual element
    Promprod getSelectedPromprod();
    Product getSelectedProduct();
    Event getSelectedEvent();

    //refreshing
    void stopRefresh();
    void disableSwipeRefresh();
    void enableSwipeRefresh();
    void refreshCurrentFragment();

    HashMap<String,Integer> getCategories();

    //sheets
    void launchSheet();
    void launchFilterSheet();
    void launchEventInfoFragment();
    void setPromotionSheetBehaviour();
    void setProductSheetBehaviour();
    void setEventSheetBehaviour();
    BottomSheetBehavior getBottomSheetBehavior();

    //fragments
    void launchFragment(RefreshableFragment fragment);
    void launchFragment(String key);
    void launchProductFragment(int category);
    void launchSheetInfoFragment();

    //Fab progress
    void startProgress();
    void endProgressSuccessfully();
    void endProgressWithError();

    //fabs
    FloatingActionButton getFab();
    void setFabVisible(boolean visible);
    void setProdFab();
    void setEmptyState(String type);
    void removeEmptyState();

    //Menu
    void setSearchVisible(boolean isVisible);
    void setCartIconFull();
    void setCartIconEmpty();
    void removeSearchMenuVisibility();

    void collapseSearchView();

    void goOnProm();

    void hideCartMenu();
}
