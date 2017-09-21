package com.pezzuto.pezzuto.ui;

import android.app.Dialog;
import android.content.Context;
import android.support.design.widget.BottomSheetBehavior;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

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
    public static void createCodeRequestDoneDialog(Context context, String email) {
       createDialog(context,"La richiesta del tuo codice azienda è stata inviata con successo.\nIl nostro staff risponderà il prima possibile con un'email all'indirizzo "
               + email);
    }
    public static void createCodeRequestSentDialog(Context context) {
        createDialog(context,"La richiesta del tuo codice azienda è già stata inviata. Il nostro staff ti risponderà il prima possibile.");
    }
    public static void createDialog(Context context, String message) {
        // custom dialog
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_order_done);
        TextView content = (TextView) dialog.findViewById(R.id.content);
        content.setText(message);
        Button done = (Button) dialog.findViewById(R.id.buttonDone);
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        // if button is clicked, close the custom dialog
        dialog.show();
    }
}
