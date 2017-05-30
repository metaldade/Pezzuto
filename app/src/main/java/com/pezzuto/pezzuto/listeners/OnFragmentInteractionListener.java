package com.pezzuto.pezzuto.listeners;

import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.Fragment;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.pezzuto.pezzuto.RefreshableFragment;
import com.pezzuto.pezzuto.items.Product;
import com.pezzuto.pezzuto.items.Promprod;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by dade on 24/03/17.
 */

public interface OnFragmentInteractionListener {
    void launchFragment(RefreshableFragment fragment);
    void launchFragment(String key);
    Promprod getSelectedPromprod();
    void setSelectedPromprod(Promprod p);
    Product getSelectedProduct();
    void setSelectedProduct(Product p);
    void setFabVisible(boolean visible);
    FloatingActionButton getFab();
    void launchSheet();
    void stopRefresh();
    HashMap<String,Integer> getCategories();
    void launchFilterSheet();
    BottomSheetBehavior getBottomSheetBehavior();
    void setPromotionSheetBehaviour();
    void setProductSheetBehaviour();
    void launchProductFragment(int category);
    void launchSheetInfoFragment();
    void startProgress();
    void endProgressSuccessfully();
    void endProgressWithError();
    void setProdFab();
}
