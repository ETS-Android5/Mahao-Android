package ke.co.tonyoa.mahao.ui.auth.register;

import android.os.Bundle;

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
import ke.co.tonyoa.mahao.databinding.FragmentRegisterBinding;

public class RegisterFragment extends BaseFragment {

    private FragmentRegisterBinding mFragmentRegisterBinding;
    private RegisterViewModel mRegisterViewModel;
    private List<View> mEnabledViews;

    public RegisterFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mRegisterViewModel = new ViewModelProvider(this).get(RegisterViewModel.class);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mFragmentRegisterBinding = FragmentRegisterBinding.inflate(inflater, container, false);
        setToolbar(mFragmentRegisterBinding.layoutToolbar.materialToolbarLayoutToolbar);
        setTitle(getString(R.string.register));

        mEnabledViews = new ArrayList<>(Arrays.asList(mFragmentRegisterBinding.layoutToolbar.materialToolbarLayoutToolbar,
                mFragmentRegisterBinding.textInputEditTextRegisterFirstName, mFragmentRegisterBinding.textInputEditTextRegisterLastName,
                mFragmentRegisterBinding.textInputEditTextRegisterEmail, mFragmentRegisterBinding.textInputEditTextRegisterPhone,
                mFragmentRegisterBinding.textInputEditTextRegisterLocation, mFragmentRegisterBinding.textInputEditTextRegisterPassword,
                mFragmentRegisterBinding.textInputEditTextRegisterConfirmPassword, mFragmentRegisterBinding.checkboxRegisterTerms,
                mFragmentRegisterBinding.buttonRegisterCreate, mFragmentRegisterBinding.textViewRegisterLogin));

        mFragmentRegisterBinding.checkboxRegisterTerms.setOnCheckedChangeListener((v, isChecked)->{
            mFragmentRegisterBinding.buttonRegisterCreate.setEnabled(isChecked);
        });
        mFragmentRegisterBinding.textViewRegisterTerms.setOnClickListener(v->{
            navigate(RegisterFragmentDirections.actionNavigationRegisterToPolicyFragment("file:///android_res/raw/mahao_terms.html",
                    R.string.terms_and_conditions));
        });
        mFragmentRegisterBinding.textViewRegisterLogin.setOnClickListener(v->{
            navigateBack();
        });

        mFragmentRegisterBinding.buttonRegisterCreate.setOnClickListener(v->{
            String firstName = ViewUtils.getText(mFragmentRegisterBinding.textInputEditTextRegisterFirstName);
            String lastName = ViewUtils.getText(mFragmentRegisterBinding.textInputEditTextRegisterLastName);
            String email = ViewUtils.getText(mFragmentRegisterBinding.textInputEditTextRegisterEmail);
            String phone = ViewUtils.getText(mFragmentRegisterBinding.textInputEditTextRegisterPhone);
            String location = ViewUtils.getText(mFragmentRegisterBinding.textInputEditTextRegisterLocation);
            String password = ViewUtils.getText(mFragmentRegisterBinding.textInputEditTextRegisterPassword);
            String confirmPassword = ViewUtils.getText(mFragmentRegisterBinding.textInputEditTextRegisterConfirmPassword);

            if (ViewUtils.isEmptyAndRequired(mFragmentRegisterBinding.textInputEditTextRegisterFirstName)){
                return;
            }
            if (ViewUtils.isEmptyAndRequired(mFragmentRegisterBinding.textInputEditTextRegisterLastName)){
                return;
            }
            if (ViewUtils.isEmptyAndRequired(mFragmentRegisterBinding.textInputEditTextRegisterEmail)){
                return;
            }
            if (!ViewUtils.isEmailValid(email)){
                mFragmentRegisterBinding.textInputEditTextRegisterEmail.setError(getString(R.string.please_enter_valid_email));
                return;
            }
            if (ViewUtils.isEmptyAndRequired(mFragmentRegisterBinding.textInputEditTextRegisterPhone)){
                return;
            }
            if (!ViewUtils.isKenyanPhoneValid(phone)){
                mFragmentRegisterBinding.textInputEditTextRegisterPhone.setError(getString(R.string.enter_valid_phone));
                return;
            }
            if (ViewUtils.isEmptyAndRequired(mFragmentRegisterBinding.textInputEditTextRegisterLocation)){
                return;
            }
            if (ViewUtils.isEmptyAndRequired(mFragmentRegisterBinding.textInputEditTextRegisterPassword)){
                return;
            }
            if (ViewUtils.isEmptyAndRequired(mFragmentRegisterBinding.textInputEditTextRegisterConfirmPassword)){
                return;
            }
            if (password.length()<6){
                mFragmentRegisterBinding.textInputEditTextRegisterPassword.setError(getString(R.string.password_too_short));
                return;
            }
            if (!password.equals(confirmPassword)){
                mFragmentRegisterBinding.textInputEditTextRegisterPassword.setError(getString(R.string.passwords_need_to_match));
                mFragmentRegisterBinding.textInputEditTextRegisterConfirmPassword.setText("");
                return;
            }

            ViewUtils.load(mFragmentRegisterBinding.linearLayoutRegisterLoading, mEnabledViews, true);
            mRegisterViewModel.register(firstName, lastName, email, phone, location, password).observe(getViewLifecycleOwner(), loginResponseAPIResponse -> {
                ViewUtils.load(mFragmentRegisterBinding.linearLayoutRegisterLoading, mEnabledViews, false);
                if (loginResponseAPIResponse!=null && loginResponseAPIResponse.isSuccessful()){
                    Toast.makeText(requireContext(), R.string.account_created_successfully, Toast.LENGTH_SHORT).show();
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
        return mFragmentRegisterBinding.getRoot();
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