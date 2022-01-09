package ke.co.tonyoa.mahao.app.utils;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.AttrRes;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.Chip;

import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ViewUtils {

    private ViewUtils(){

    }

    public static <T extends Collection<View>> void setEmptyView(T items, RecyclerView recyclerView, View emptyView){
        if (items==null || items.size()==0){
            recyclerView.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
        }
        else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
        }
    }

    public static int getThemeColor(Context context, @AttrRes int colorAttribute) {
        TypedValue typedValue = new TypedValue();
        TypedArray typedArray = context.obtainStyledAttributes(typedValue.data, new int[]{colorAttribute});
        int color = typedArray.getColor(0, 0);
        typedArray.recycle();
        return color;
    }

    public static<T extends TextView> void clearText(@NonNull List<T> views){
        for (TextView textView:views){
            textView.setText("");
        }
    }

    public static<T extends TextView> String getStringFromTextView(@NonNull T textView){
        return textView.getText().toString().trim();
    }

    public static<T extends TextView> boolean isEmptyOrNull(@NonNull T textView){
        return getStringFromTextView(textView).trim().isEmpty();
    }

    public static<T extends TextView> boolean isEmptyOrNull(String text){
        return text==null || text.trim().isEmpty();
    }

    public static<T extends TextView> void clearErrors(@NonNull List<T> textViews){
        for (T t:textViews){
            t.setError(null);
        }
    }

    public static<T extends TextView> boolean isEmptyAndRequired(T t){
        if (isEmptyOrNull(t)){
            setRequiredError(t);
            return true;
        }
        return false;
    }

    public static<T extends TextView> void setRequiredError(T t){
        t.setError("Required");
    }

    public static<T extends TextView> String getText(@NonNull T textView){
        return textView.getText()==null?"":textView.getText().toString().trim();
    }

    public static boolean isEmailValid(CharSequence email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public static boolean isKenyanPhoneValid(String inputPhoneNumber){
        Pattern pattern = Pattern.compile("^(\\+254|0)([17][0-9]|[1][0-1]){1}[0-9]{1}[0-9]{6}$");
        Matcher matcher = pattern.matcher(inputPhoneNumber);
        if (matcher.matches()) {
            String validPhoneNumber = "254" + matcher.group(1);
        }
        return matcher.matches();
    }


    public static void selectChip(Chip selectedChip, List<Chip> allChips){
        selectedChip.setSelected(true);
        selectedChip.setChecked(true);
        for (Chip chip:allChips){
            if (chip!=selectedChip){
                chip.setSelected(false);
                chip.setChecked(false);
            }
        }
    }

    public static <T extends View> void load(View loadingView, List<T> enabledViews, boolean isLoading){
        loadingView.setVisibility(isLoading?View.VISIBLE:View.GONE);
        for (View view:enabledViews){
            view.setEnabled(!isLoading);
        }
    }


    public static void setFragmentTitle(String title, Toolbar toolbar){
        toolbar.setTitle(title);
    }

    public abstract static class TextValidator implements TextWatcher{

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            validate(editable.toString());
        }

        public abstract void validate(CharSequence text);
    }

}
