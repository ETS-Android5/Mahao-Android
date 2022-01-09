package ke.co.tonyoa.mahao.ui.auth.login;

import android.animation.Animator;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavBackStackEntry;

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
import ke.co.tonyoa.mahao.databinding.FragmentLoginBinding;

public class LoginFragment extends BaseFragment {

    public static final String LOGIN_SUCCESSFUL = "LOGIN_SUCCESSFUL";
    private FragmentLoginBinding mFragmentLoginBinding;
    private LoginViewModel mLoginViewModel;
    private SavedStateHandle mSavedStateHandle;
    private List<View> mEnabledViews;


    public LoginFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLoginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mFragmentLoginBinding = FragmentLoginBinding.inflate(inflater, container, false);

        mEnabledViews = new ArrayList<>(Arrays.asList(mFragmentLoginBinding.textInputEditTextLoginEmail,
                mFragmentLoginBinding.textInputEditTextLoginPassword, mFragmentLoginBinding.textViewLoginForgotPassword,
                mFragmentLoginBinding.buttonLoginLogin, mFragmentLoginBinding.buttonLoginRegister));

        NavBackStackEntry previousBackStackEntry = getNavController().getPreviousBackStackEntry();
        if (previousBackStackEntry != null) {
            mSavedStateHandle = previousBackStackEntry.getSavedStateHandle();
            mSavedStateHandle.set(LOGIN_SUCCESSFUL, false);
        }

        mFragmentLoginBinding.buttonLoginRegister.setOnClickListener(v->{
            navigate(LoginFragmentDirections.actionNavigationLoginToNavigationRegister());
        });
        mFragmentLoginBinding.textViewLoginForgotPassword.setOnClickListener(v->{
            navigate(LoginFragmentDirections.actionNavigationLoginToNavigationForgot());
        });

        mFragmentLoginBinding.buttonLoginLogin.setOnClickListener(v->{
            String email= ViewUtils.getText(mFragmentLoginBinding.textInputEditTextLoginEmail);
            String password=ViewUtils.getText(mFragmentLoginBinding.textInputEditTextLoginPassword);

            if (ViewUtils.isEmptyAndRequired(mFragmentLoginBinding.textInputEditTextLoginEmail)){
                return;
            }
            if (!ViewUtils.isEmailValid(email)){
                mFragmentLoginBinding.textInputEditTextLoginEmail.setError(getString(R.string.please_enter_valid_email));
                return;
            }
            if (ViewUtils.isEmptyAndRequired(mFragmentLoginBinding.textInputEditTextLoginPassword)){
                return;
            }
            if (password.length()<6){
                mFragmentLoginBinding.textInputEditTextLoginPassword.setError(getString(R.string.please_enter_valid_password));
                return;
            }


            ViewUtils.load(mFragmentLoginBinding.linearLayoutLoginLoading, mEnabledViews, true);
            mLoginViewModel.login(email, password).observe(getViewLifecycleOwner(), loginResponseAPIResponse -> {
                ViewUtils.load(mFragmentLoginBinding.linearLayoutLoginLoading, mEnabledViews, false);
                if (loginResponseAPIResponse!=null && loginResponseAPIResponse.isSuccessful()){
                    mSavedStateHandle.set(LOGIN_SUCCESSFUL, true);
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

        mFragmentLoginBinding.animationViewLogin.addAnimatorListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mFragmentLoginBinding.animationViewLogin.postDelayed(()->{
                    mFragmentLoginBinding.animationViewLogin.playAnimation();
                }, 10000);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        return mFragmentLoginBinding.getRoot();
    }

}