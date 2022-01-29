package ke.co.tonyoa.mahao.ui.auth.forgot;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ke.co.tonyoa.mahao.R;
import ke.co.tonyoa.mahao.app.navigation.BaseFragment;
import ke.co.tonyoa.mahao.app.utils.ViewUtils;
import ke.co.tonyoa.mahao.databinding.FragmentNewPasswordBinding;

public class NewPasswordFragment extends BaseFragment {

    private String mToken;
    private FragmentNewPasswordBinding mFragmentNewPasswordBinding;
    private NewPasswordViewModel mNewPasswordViewModel;
    private List<View> mEnabledViews;

    public NewPasswordFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mToken = getArguments().getString("token");
        }
        mNewPasswordViewModel = new ViewModelProvider(this).get(NewPasswordViewModel.class);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mFragmentNewPasswordBinding = FragmentNewPasswordBinding.inflate(inflater,
                container, false);
        // setToolbar(mFragmentNewPasswordBinding.layoutToolbar.materialToolbarLayoutToolbar);
        setTitle(getString(R.string.register));

        mEnabledViews = new ArrayList<>(Arrays.asList(mFragmentNewPasswordBinding.layoutToolbar.materialToolbarLayoutToolbar,
                mFragmentNewPasswordBinding.textInputEditTextNewPasswordPassword,
                mFragmentNewPasswordBinding.textInputEditTextNewPasswordConfirmPassword,
                mFragmentNewPasswordBinding.buttonNewPasswordConfirm));

        mFragmentNewPasswordBinding.buttonNewPasswordConfirm.setOnClickListener(v->{
            String password = ViewUtils.getText(mFragmentNewPasswordBinding.textInputEditTextNewPasswordPassword);
            String confirmPassword = ViewUtils.getText(mFragmentNewPasswordBinding.textInputEditTextNewPasswordConfirmPassword);
            if (ViewUtils.isEmptyAndRequired(mFragmentNewPasswordBinding.textInputEditTextNewPasswordPassword)){
                return;
            }
            if (ViewUtils.isEmptyAndRequired(mFragmentNewPasswordBinding.textInputEditTextNewPasswordConfirmPassword)){
                return;
            }
            if (password.length()<6){
                mFragmentNewPasswordBinding.textInputEditTextNewPasswordPassword.setError(getString(R.string.password_too_short));
                return;
            }
            if (!password.equals(confirmPassword)){
                mFragmentNewPasswordBinding.textInputEditTextNewPasswordPassword.setError(getString(R.string.passwords_need_to_match));
                mFragmentNewPasswordBinding.textInputEditTextNewPasswordConfirmPassword.setText("");
                return;
            }

            ViewUtils.load(mFragmentNewPasswordBinding.linearLayoutNewPasswordLoading, mEnabledViews, true);
            mNewPasswordViewModel.resetPassword(mToken, password).observe(getViewLifecycleOwner(), loginResponseAPIResponse -> {
                ViewUtils.load(mFragmentNewPasswordBinding.linearLayoutNewPasswordLoading, mEnabledViews, false);
                if (loginResponseAPIResponse!=null && loginResponseAPIResponse.isSuccessful()){
                    Toast.makeText(requireContext(), R.string.password_reset_successfully, Toast.LENGTH_SHORT).show();
                    mNewPasswordViewModel.logout();
                    navigateBack();
                }
                else {
                    Toast.makeText(requireContext(),
                            (loginResponseAPIResponse==null || loginResponseAPIResponse.errorMessage(requireContext())==null)?
                                    getString(R.string.unknown_error):
                                    loginResponseAPIResponse.errorMessage(requireContext()),
                            Toast.LENGTH_SHORT).show();
                }
            });
        });

        return mFragmentNewPasswordBinding.getRoot();
    }
}