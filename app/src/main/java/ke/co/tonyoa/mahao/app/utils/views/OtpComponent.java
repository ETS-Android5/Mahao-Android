package ke.co.tonyoa.mahao.app.utils.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import com.google.android.material.textfield.TextInputEditText;

import ke.co.tonyoa.mahao.R;

public class OtpComponent extends LinearLayout {

    private TextInputEditText[] mTextInputEditTexts;
    private String mOtp;
    private int mLength;
    private int mTextSize;
    private final TextWatcher mTextWatcher= new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String otp = mOtp;
            if (otp == null)
                otp = "";
            if (s.length() > 0) {
                otp = otp + s.toString().trim();
            } else {
                //If two or more characters
                if (otp.length() > 1) {
                    otp = otp.substring(0, otp.length() - 1);
                }
                //If one or less characters
                else {
                    otp = "";
                }
            }
            int index;
            //When character is added
            if (otp.length() < mLength) {
                index = otp.length();
                focusView(index);
            }

            mOtp = otp;
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };

    public OtpComponent(Context context) {
        this(context, null);
    }

    public OtpComponent(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public OtpComponent(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public OtpComponent(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, new int[]{defStyleAttr});
        mOtp = typedArray.getString(R.styleable.OtpComponent_otp);
        mLength = typedArray.getInt(R.styleable.OtpComponent_length, 4);
        mTextSize = typedArray.getInt(R.styleable.OtpComponent_android_textSize,
                context.getResources().getDimensionPixelSize(R.dimen.text_14));

        typedArray.recycle();
        init();
    }

    private void init(){
        setOrientation(HORIZONTAL);

        setViews();
    }

    private void setViews(){
        setLength(mLength);
        setTextSize(mTextSize);
        setOtp(mOtp);
    }

    public void setLength(int length){
        mLength=length;

        removeAllViews();
        mTextInputEditTexts=new TextInputEditText[mLength];
        LayoutParams layoutParams=new LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1);
        int margin4=getContext().getResources().getDimensionPixelSize(R.dimen.size_4);
        layoutParams.setMargins(margin4, 0, margin4, 0);
        for (int index=0; index<mLength; index++){
            OtpTextInputEditText textInputEditText = new OtpTextInputEditText(getContext());
            textInputEditText.setLayoutParams(layoutParams);
            textInputEditText.setGravity(Gravity.CENTER_HORIZONTAL);
            textInputEditText.setTextSize(mTextSize);
            textInputEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
            textInputEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(1)});
            final  int finalIndex=index;
            textInputEditText.setOnBackspaceClickedListener(() -> {
                int current=finalIndex;
                Editable text = textInputEditText.getText();
                if (current>0 && text!=null && text.length()==0)
                    current=current-1;
                focusView(current);
            });
            textInputEditText.addTextChangedListener(mTextWatcher);

            mTextInputEditTexts[index]=textInputEditText;
            addView(textInputEditText);
        }
    }

    private void focusView(int index){
        if (index>=mTextInputEditTexts.length)
            index=mTextInputEditTexts.length-1;
        else if (index<0)
            index=0;

        mTextInputEditTexts[index].requestFocus();
    }

    public void setOtp(String otp){
        mOtp=otp;

        int lastText=-1;
        for (int index=0; index<mLength; index++){
            String text;
            if (mOtp==null || mOtp.length()<=index){
                text="";
            }
            else {
                text=mOtp.charAt(index)+"";
                lastText=index;
            }
            TextInputEditText textInputEditText = mTextInputEditTexts[index];
            textInputEditText.removeTextChangedListener(mTextWatcher);
            textInputEditText.setText(text);
            textInputEditText.setSelection(text.length());
            textInputEditText.addTextChangedListener(mTextWatcher);
        }
        focusView((mOtp==null||mOtp.trim().length()==0)?0:lastText+1);
    }

    public void setTextSize(int textSize){
        mTextSize = textSize;

        for (TextInputEditText textInputEditText:mTextInputEditTexts){
            textInputEditText.setTextSize(mTextSize);
        }
    }

    public int getLength(){
        return mLength;
    }

    public int getTextSize(){
        return mTextSize;
    }

    public String getOtp(){
        return mOtp;
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        for (TextInputEditText textInputEditText:mTextInputEditTexts){
            textInputEditText.setEnabled(enabled);
        }
    }

    @Nullable
    @Override
    protected Parcelable onSaveInstanceState() {
        Parcelable parcelable = super.onSaveInstanceState();
        Bundle bundle=new Bundle();
        bundle.putString("otp", mOtp);
        bundle.putInt("length", mLength);
        bundle.putInt("textSize", mTextSize);
        bundle.putParcelable("super", parcelable);
        return bundle;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle){
            Bundle bundle=(Bundle) state;
            mOtp = bundle.getString("otp");
            mLength = bundle.getInt("length");
            mTextSize = bundle.getInt("textSize");
            super.onRestoreInstanceState(bundle.getParcelable("super"));
            setViews();
        }
        else {
            super.onRestoreInstanceState(state);
        }
    }
}
