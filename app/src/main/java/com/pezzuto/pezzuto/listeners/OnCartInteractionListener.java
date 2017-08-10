package com.pezzuto.pezzuto.listeners;

import android.widget.Button;

import com.dd.CircularProgressButton;

/**
 * Created by dade on 19/06/17.
 */

public interface OnCartInteractionListener {
    CircularProgressButton getCartButton();
    void startProgress();
    void endProgressSuccessfully();
    void endOrderSuccessfully();
    void goEmptyState(int state);
    void endProgressWithError();
    void restoreCartButton();
    void adjustListView();
}
