package ke.co.tonyoa.mahao.app.utils;

import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.DisplayMetrics;

import androidx.appcompat.app.AlertDialog;

public class DialogUtils {

    public static void expandDialogWidth(Context context, Dialog dialog, float widthPercent, float heightPercent) {
        if (context!=null && dialog!=null) {
            DisplayMetrics metrics = context.getResources().getDisplayMetrics();
            int width = metrics.widthPixels;
            int height = metrics.heightPixels;
            dialog.getWindow().setLayout(widthPercent==WRAP_CONTENT?WRAP_CONTENT:Math.round(widthPercent*width),
                    heightPercent==WRAP_CONTENT?WRAP_CONTENT:Math.round(heightPercent*height));
        }
    }

    public static void showDialog(Context context, String title, String message, String negativeButtonText, String positiveButtonText, DialogInterface.OnClickListener onDeleteClickListener){
        new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setNegativeButton(negativeButtonText, (dialog, which) -> dialog.dismiss())
                .setPositiveButton(positiveButtonText, (dialog, which) -> {
                    if (onDeleteClickListener!=null)
                        onDeleteClickListener.onClick(dialog, which);
                })
                .setCancelable(false)
                .create()
                .show();
    }

}