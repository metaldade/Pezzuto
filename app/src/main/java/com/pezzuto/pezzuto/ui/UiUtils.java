package com.pezzuto.pezzuto.ui;

import android.app.Dialog;
import android.content.Context;
import android.support.design.widget.BottomSheetBehavior;
import android.view.View;
import android.widget.Button;

import com.pezzuto.pezzuto.R;
import com.pezzuto.pezzuto.listeners.OnFragmentInteractionListener;

/**
 * Created by dade on 19/06/17.
 */

public class UiUtils {
    public static void createOrderDoneDialog(Context context, final OnFragmentInteractionListener mListener) {
        // custom dialog
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_order_done);


        Button done = (Button) dialog.findViewById(R.id.buttonDone);
        // if button is clicked, close the custom dialog
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (mListener != null) mListener.getBottomSheetBehavior().setState(BottomSheetBehavior.STATE_HIDDEN);
            }
        });

        dialog.show();
    }
        public static void createOrderDoneDialog(Context context) {
            createOrderDoneDialog(context,null);
        }
}
