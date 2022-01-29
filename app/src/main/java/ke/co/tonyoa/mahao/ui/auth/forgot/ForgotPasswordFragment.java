package ke.co.tonyoa.mahao.ui.auth.forgot;

import android.animation.Animator;
import android.os.Bundle;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ke.co.tonyoa.mahao.R;
import ke.co.tonyoa.mahao.app.navigation.BaseFragment;
import ke.co.tonyoa.mahao.app.utils.ViewUtils;
import ke.co.tonyoa.mahao.databinding.FragmentForgotPasswordBinding;

public class ForgotPasswordFragment extends BaseFragment {

    private FragmentForgotPasswordBinding mFragmentForgotPasswordBinding;
    private ForgotPasswordViewModel mForgotPasswordViewModel;
    private List<View> mEnabledViews;

    public ForgotPasswordFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mForgotPasswordViewModel = new ViewModelProvider(this).get(ForgotPasswordViewModel.class);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mFragmentForgotPasswordBinding = FragmentForgotPasswordBinding.inflate(inflater, container, false);
        setToolbar(mFragmentForgotPasswordBinding.layoutToolbar.materialToolbarLayoutToolbar);
        setTitle(getString(R.string.recover_password));

        mEnabledViews = new ArrayList<>(Arrays.asList(mFragmentForgotPasswordBinding.layoutToolbar.materialToolbarLayoutToolbar,
                mFragmentForgotPasswordBinding.textInputEditTextForgotPasswordEmail, mFragmentForgotPasswordBinding.buttonForgotPasswordConfirm));

        mFragmentForgotPasswordBinding.buttonForgotPasswordConfirm.setOnClickListener(v->{
            String email = ViewUtils.getText(mFragmentForgotPasswordBinding.textInputEditTextForgotPasswordEmail);

            if (ViewUtils.isEmptyAndRequired(mFragmentForgotPasswordBinding.textInputEditTextForgotPasswordEmail)){
                return;
            }
            if (!ViewUtils.isEmailValid(email)){
                mFragmentForgotPasswordBinding.textInputEditTextForgotPasswordEmail.setError(getString(R.string.please_enter_valid_email));
                return;
            }

            ViewUtils.load(mFragmentForgotPasswordBinding.linearLayoutForgotLoading, mEnabledViews, true);
            mForgotPasswordViewModel.recoverPassword(email).observe(getViewLifecycleOwner(), forgotPasswordResponseAPIResponse -> {
                if (forgotPasswordResponseAPIResponse !=null && forgotPasswordResponseAPIResponse.isSuccessful()){
                    Toast.makeText(requireContext(), getString(R.string.password_recovery_email_sent,
                            email), Toast.LENGTH_LONG).show();
                    mFragmentForgotPasswordBinding.animationViewForgotLoading.setAnimation(R.raw.email_send);
                    mFragmentForgotPasswordBinding.animationViewForgotLoading.setRepeatCount(1);
                    mFragmentForgotPasswordBinding.animationViewForgotLoading.addAnimatorListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animation) {
                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            navigateBack();
                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {

                        }

                        @Override
                        public void onAnimationRepeat(Animator animation) {

                        }
                    });
                    mFragmentForgotPasswordBinding.animationViewForgotLoading.playAnimation();
                }
                else {
                    ViewUtils.load(mFragmentForgotPasswordBinding.linearLayoutForgotLoading, mEnabledViews, false);
                    Toast.makeText(requireContext(),
                            (forgotPasswordResponseAPIResponse ==null || forgotPasswordResponseAPIResponse.errorMessage(requireContext())==null)?
                                    getString(R.string.unknown_error):
                                    forgotPasswordResponseAPIResponse.errorMessage(requireContext()),
                            Toast.LENGTH_SHORT).show();
                }
            });

        });
        return mFragmentForgotPasswordBinding.getRoot();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId()==android.R.id.home){
            navigateUp();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}