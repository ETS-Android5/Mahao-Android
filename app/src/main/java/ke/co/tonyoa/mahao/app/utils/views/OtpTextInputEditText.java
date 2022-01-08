package ke.co.tonyoa.mahao.app.utils.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputConnectionWrapper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.textfield.TextInputEditText;

public class OtpTextInputEditText extends TextInputEditText {

    private OnBackspaceClickedListener mOnBackspaceClickedListener;

    public OtpTextInputEditText(@NonNull Context context) {
        super(context);
    }

    public OtpTextInputEditText(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public OtpTextInputEditText(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public InputConnection onCreateInputConnection(@NonNull EditorInfo outAttrs) {
        return new OtpInputConnection(super.onCreateInputConnection(outAttrs),
                true);
    }

    public OnBackspaceClickedListener getOnBackspaceClickedListener() {
        return mOnBackspaceClickedListener;
    }

    public void setOnBackspaceClickedListener(OnBackspaceClickedListener onBackspaceClickedListener) {
        mOnBackspaceClickedListener = onBackspaceClickedListener;
    }

    private class OtpInputConnection extends InputConnectionWrapper {

        public OtpInputConnection(InputConnection target, boolean mutable) {
            super(target, mutable);
        }

        @Override
        public boolean sendKeyEvent(KeyEvent event) {
            if (event.getAction() == KeyEvent.ACTION_DOWN
                    && event.getKeyCode() == KeyEvent.KEYCODE_DEL) {
                if (mOnBackspaceClickedListener!=null)
                    mOnBackspaceClickedListener.onBackspaceClicked();
                // Un-comment if you wish to cancel the backspace:
                // return false;
            }
            return super.sendKeyEvent(event);
        }


        @Override
        public boolean deleteSurroundingText(int beforeLength, int afterLength) {
            // magic: in latest Android, deleteSurroundingText(1, 0) will be called for backspace
            if (beforeLength == 1 && afterLength == 0) {
                // backspace
                return sendKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL))
                        && sendKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_DEL));
            }

            return super.deleteSurroundingText(beforeLength, afterLength);
        }
    }

    interface OnBackspaceClickedListener{
        void onBackspaceClicked();
    }

}
