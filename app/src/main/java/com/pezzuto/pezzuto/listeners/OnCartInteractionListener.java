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
    void endProgressWithError();
    void restoreCartButton();
}
