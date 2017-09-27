package com.pezzuto.pezzuto;


import android.graphics.Bitmap;
import android.support.v4.app.Fragment;

import com.pezzuto.pezzuto.items.PezzutoObject;

/**
 * Created by dade on 18/05/17.
 */

public abstract class RefreshableFragment extends Fragment {
    abstract public void refresh();
    abstract public String getType();
    abstract public PezzutoObject getRelatedObject();
    abstract public Bitmap getImageBitmap();
    abstract public boolean hasEmptySet(String type);

}
