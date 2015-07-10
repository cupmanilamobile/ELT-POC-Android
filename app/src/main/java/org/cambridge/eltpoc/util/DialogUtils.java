package org.cambridge.eltpoc.util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

/**
 * Created by etorres on 7/6/15.
 */
public class DialogUtils {
    public interface OnOptionSelectedListener {
        void onOptionSelected();
    }

    public static void createDialog(Context context, String title, String message) {
        AlertDialog alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }

    public static void createOptionDialog(Context context, String title, String message,
                                          String okTitle, String cancelTitle,
                                          final OnOptionSelectedListener onOptionSelectedListener) {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
        alertDialog.setPositiveButton(okTitle, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                onOptionSelectedListener.onOptionSelected();
                dialog.dismiss();
            }
        });
        alertDialog.setNegativeButton(cancelTitle, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alertDialog.show();
    }
}
