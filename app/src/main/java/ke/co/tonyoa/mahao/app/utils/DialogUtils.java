package ke.co.tonyoa.mahao.app.utils;

import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.util.DisplayMetrics;

import androidx.appcompat.app.AlertDialog;

import com.codemybrainsout.ratingdialog.RatingDialog;

import ke.co.tonyoa.mahao.R;

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

    public static RatingDialog.Builder getStandardRatingDialogBuilder(Context context) {
        return new RatingDialog.Builder(context)
                .threshold(3.5f)
                .onRatingBarFormSumbit(new RatingDialog.Builder.RatingDialogFormListener() {
                    @Override
                    public void onFormSubmitted(String feedback) {
                        composeEmail(context, new String[]{"mahaoorg@gmail.com"},
                                "Mahao Report", feedback);
                    }
                });
    }

    public static void composeEmail(Context context, String[] addresses, String subject, String body) {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:"));
        intent.putExtra(Intent.EXTRA_EMAIL, addresses);
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(Intent.EXTRA_TEXT, body);
        if (intent.resolveActivity(context.getPackageManager()) != null) {
            Intent.createChooser(intent, context.getString(R.string.send_email));
        }
    }

}