package com.pezzuto.pezzuto;


import android.support.v4.app.Fragment;

/**
 * Created by dade on 18/05/17.
 */

public abstract class RefreshableFragment extends Fragment {
    abstract public void refresh();
    abstract public String getType();
    abstract public boolean hasEmptySet(String type);
}
