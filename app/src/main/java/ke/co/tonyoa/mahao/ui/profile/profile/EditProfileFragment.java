package ke.co.tonyoa.mahao.ui.profile.profile;

import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import dev.ronnie.github.imagepicker.ImagePicker;
import dev.ronnie.github.imagepicker.ImageResult;
import ke.co.tonyoa.mahao.R;
import ke.co.tonyoa.mahao.app.api.responses.User;
import ke.co.tonyoa.mahao.app.navigation.BaseFragment;
import ke.co.tonyoa.mahao.app.utils.ViewUtils;
import ke.co.tonyoa.mahao.databinding.FragmentEditProfileBinding;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;

public class EditProfileFragment extends BaseFragment {

    private FragmentEditProfileBinding mFragmentEditProfileBinding;
    private EditProfileViewModel mEditProfileViewModel;
    private List<View> mEnabledViews;
    private ImagePicker mImagePicker;

    public EditProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mEditProfileViewModel = new ViewModelProvider(this).get(EditProfileViewModel.class);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mFragmentEditProfileBinding = FragmentEditProfileBinding.inflate(inflater, container, false);
        mImagePicker = new ImagePicker(this);
        setToolbar(mFragmentEditProfileBinding.layoutToolbar.materialToolbarLayoutToolbar);
        setTitle(getString(R.string.edit_profile));

        mEnabledViews = new ArrayList<>(Arrays.asList(mFragmentEditProfileBinding.layoutToolbar.materialToolbarLayoutToolbar,
                mFragmentEditProfileBinding.textInputEditTextEditProfileFirstName, mFragmentEditProfileBinding.textInputEditTextEditProfileLastName,
                mFragmentEditProfileBinding.textInputEditTextEditProfilePhone, mFragmentEditProfileBinding.textInputEditTextEditProfileLocation,
                mFragmentEditProfileBinding.buttonEditProfileSave));

        ViewUtils.load(mFragmentEditProfileBinding.linearLayoutEditProfileLoading, mEnabledViews, true);
        mEditProfileViewModel.getProfile().observe(getViewLifecycleOwner(), userAPIResponse -> {
            ViewUtils.load(mFragmentEditProfileBinding.linearLayoutEditProfileLoading, mEnabledViews, false);
            if (userAPIResponse!=null && userAPIResponse.isSuccessful()){
                User user = userAPIResponse.body();
                Glide.with(requireContext())
                        .load(user.getProfilePicture())
                        .placeholder(R.drawable.ic_baseline_person_24)
                        .error(R.drawable.ic_baseline_person_24)
                        .transform(new CircleCrop())
                        .into(mFragmentEditProfileBinding.imageViewEditProfileAvatar);
                mFragmentEditProfileBinding.textInputEditTextEditProfileFirstName.setText(user.getFirstName());
                mFragmentEditProfileBinding.textInputEditTextEditProfileLastName.setText(user.getLastName());
                mFragmentEditProfileBinding.textInputEditTextEditProfileEmail.setText(user.getEmail());
                mFragmentEditProfileBinding.textInputEditTextEditProfilePhone.setText(user.getPhone());
                mFragmentEditProfileBinding.textInputEditTextEditProfileLocation.setText(user.getLocation());
            }
            else {
                Toast.makeText(requireContext(),
                        (userAPIResponse==null || userAPIResponse.errorMessage(requireContext())==null)?
                                getString(R.string.unknown_error):
                                userAPIResponse.errorMessage(requireContext()),
                        Toast.LENGTH_SHORT).show();
            }
        });

        mFragmentEditProfileBinding.buttonEditProfileSave.setOnClickListener(v->{
            String firstName = ViewUtils.getText(mFragmentEditProfileBinding.textInputEditTextEditProfileFirstName);
            String lastName = ViewUtils.getText(mFragmentEditProfileBinding.textInputEditTextEditProfileLastName);
            String email = ViewUtils.getText(mFragmentEditProfileBinding.textInputEditTextEditProfileEmail);
            String phone = ViewUtils.getText(mFragmentEditProfileBinding.textInputEditTextEditProfilePhone);
            String location = ViewUtils.getText(mFragmentEditProfileBinding.textInputEditTextEditProfileLocation);

            if (ViewUtils.isEmptyAndRequired(mFragmentEditProfileBinding.textInputEditTextEditProfileFirstName)){
                return;
            }
            if (ViewUtils.isEmptyAndRequired(mFragmentEditProfileBinding.textInputEditTextEditProfileLastName)){
                return;
            }
            if (ViewUtils.isEmptyAndRequired(mFragmentEditProfileBinding.textInputEditTextEditProfileEmail)){
                return;
            }
            if (!ViewUtils.isEmailValid(email)){
                mFragmentEditProfileBinding.textInputEditTextEditProfileEmail.setError(getString(R.string.please_enter_valid_email));
                return;
            }
            if (ViewUtils.isEmptyAndRequired(mFragmentEditProfileBinding.textInputEditTextEditProfilePhone)){
                return;
            }
            if (!ViewUtils.isKenyanPhoneValid(phone)){
                mFragmentEditProfileBinding.textInputEditTextEditProfilePhone.setError(getString(R.string.enter_valid_phone));
                return;
            }
            if (ViewUtils.isEmptyAndRequired(mFragmentEditProfileBinding.textInputEditTextEditProfileLocation)){
                return;
            }

            ViewUtils.load(mFragmentEditProfileBinding.linearLayoutEditProfileLoading, mEnabledViews, true);
            mEditProfileViewModel.updateProfile(firstName, lastName, phone, location).observe(getViewLifecycleOwner(), userAPIResponse -> {
                ViewUtils.load(mFragmentEditProfileBinding.linearLayoutEditProfileLoading, mEnabledViews, false);
                if (userAPIResponse !=null && userAPIResponse.isSuccessful()){
                    Toast.makeText(requireContext(), R.string.profile_updated_successfully, Toast.LENGTH_SHORT).show();
                    navigateBack();
                }
                else {
                    Toast.makeText(requireContext(),
                            (userAPIResponse ==null || userAPIResponse.errorMessage(requireContext())==null)?
                                    getString(R.string.unknown_error):
                                    userAPIResponse.errorMessage(requireContext()),
                            Toast.LENGTH_SHORT).show();
                }
            });

        });
        mFragmentEditProfileBinding.imageViewEditProfileAvatar.setOnClickListener(v-> pickOrTakeImage());

        return mFragmentEditProfileBinding.getRoot();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId()==android.R.id.home){
            navigateUp();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void pickOrTakeImage(){
        Function1<ImageResult<? extends Uri>, Unit> imageCallback = imageResult -> {
            if (imageResult instanceof ImageResult.Success) {
                Uri uri = ((ImageResult.Success<Uri>) imageResult).getValue();
                Glide.with(requireContext())
                        .load(uri)
                        .placeholder(R.drawable.ic_home_black_24dp)
                        .error(R.drawable.ic_home_black_24dp)
                        .transform(new CircleCrop())
                        .into(mFragmentEditProfileBinding.imageViewEditProfileAvatar);
                mFragmentEditProfileBinding.animationViewEditProfileLoadingProfilePicture.setVisibility(View.VISIBLE);
                mEditProfileViewModel.updateProfilePicture(uri).observe(getViewLifecycleOwner(), userAPIResponse -> {
                    mFragmentEditProfileBinding.animationViewEditProfileLoadingProfilePicture.setVisibility(View.GONE);
                    if (userAPIResponse == null || !userAPIResponse.isSuccessful()) {
                        Toast.makeText(requireContext(),
                                (userAPIResponse ==null || userAPIResponse.errorMessage(requireContext())==null)?
                                        getString(R.string.unknown_error):
                                        userAPIResponse.errorMessage(requireContext()),
                                Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                String errorString = ((ImageResult.Failure) imageResult).getErrorString();
                Toast.makeText(requireContext(), errorString, Toast.LENGTH_LONG).show();
            }
            return null;
        };


        new AlertDialog.Builder(requireContext())
                .setItems(new String[]{"Gallery", "Camera"}, (dialog, which) -> {
                    if (which==0){
                        mImagePicker.pickFromStorage(imageCallback);
                    }
                    else {
                        mImagePicker.takeFromCamera(imageCallback);
                    }
                }).setTitle("Image Source")
                .create()
                .show();

    }
}