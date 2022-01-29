package ke.co.tonyoa.mahao.ui.profile;

import static ke.co.tonyoa.mahao.app.utils.DialogUtils.getStandardRatingDialogBuilder;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.codemybrainsout.ratingdialog.RatingDialog;
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity;

import java.util.Arrays;

import ke.co.tonyoa.mahao.BuildConfig;
import ke.co.tonyoa.mahao.R;
import ke.co.tonyoa.mahao.app.navigation.BaseFragment;
import ke.co.tonyoa.mahao.databinding.FragmentProfileBinding;
import ke.co.tonyoa.mahao.databinding.FragmentViewUserBinding;
import ke.co.tonyoa.mahao.ui.main.MainFragmentDirections;

public class ProfileFragment extends BaseFragment {

    private FragmentProfileBinding mFragmentProfileBinding;
    private ProfileViewModel mProfileViewModel;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mProfileViewModel = new ViewModelProvider(this).get(ProfileViewModel.class);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mFragmentProfileBinding = FragmentProfileBinding.inflate(inflater, container, false);

        mFragmentProfileBinding.layoutViewUserProfile.layoutToolbar.getRoot().setVisibility(View.GONE);
        FragmentViewUserBinding layoutViewUserProfile = mFragmentProfileBinding.layoutViewUserProfile;
        mProfileViewModel.getName().observe(getViewLifecycleOwner(), name->{
            if (name!=null){
                layoutViewUserProfile.textViewUserName.setText(name);
            }
        });
        mProfileViewModel.getEmail().observe(getViewLifecycleOwner(), email->{
            if (email!=null){
                layoutViewUserProfile.textViewUserEmail.setText(email);
            }
        });
        mProfileViewModel.getPhone().observe(getViewLifecycleOwner(), phone->{
            if (phone!=null){
                layoutViewUserProfile.textViewUserPhone.setText(phone);
            }
        });
        mProfileViewModel.getProfilePicture().observe(getViewLifecycleOwner(), profilePicture->{
            Glide.with(requireContext())
                    .load(profilePicture)
                    .placeholder(R.drawable.ic_baseline_person_24)
                    .error(R.drawable.ic_baseline_person_24)
                    .transform(new CircleCrop())
                    .into(layoutViewUserProfile.imageViewUser);

        });
        mFragmentProfileBinding.layoutViewUserProfile.imageViewUser.setOnClickListener(v->{
            navigate(MainFragmentDirections.actionNavigationMainToEditProfileFragment());
        });

        boolean isAdmin = mProfileViewModel.isAdmin();
        for (View view: Arrays.asList(mFragmentProfileBinding.linearLayoutProfileUsers, mFragmentProfileBinding.linearLayoutProfileCategories,
                mFragmentProfileBinding.linearLayoutProfileAmenities, mFragmentProfileBinding.linearLayoutProfileDummy,
                mFragmentProfileBinding.linearLayoutProfileFeedback)){
            view.setVisibility(isAdmin?View.VISIBLE:View.GONE);
        }
        mFragmentProfileBinding.linearLayoutProfileUsers.setOnClickListener(v->{
            navigate(MainFragmentDirections.actionNavigationMainToUsersListFragment());
        });
        mFragmentProfileBinding.linearLayoutProfileCategories.setOnClickListener(v->{
            navigate(MainFragmentDirections.actionNavigationMainToCategoriesListFragment());
        });
        mFragmentProfileBinding.linearLayoutProfileAmenities.setOnClickListener(v->{
            navigate(MainFragmentDirections.actionNavigationMainToAmenitiesListFragment());
        });
        mFragmentProfileBinding.buttonProfileLogout.setOnClickListener(v->{
            mProfileViewModel.logOut();
        });
        mFragmentProfileBinding.linearLayoutProfileDummy.setOnClickListener(v->{
            mProfileViewModel.createDummyData();
        });
        mFragmentProfileBinding.linearLayoutProfileFeedback.setOnClickListener(v->{
            mProfileViewModel.createFeedbacks();
        });

        mFragmentProfileBinding.linearLayoutMorePrivacyPolicy.setOnClickListener(v->{
            navigate(MainFragmentDirections.actionNavigationMainToPolicyFragment("file:///android_res/raw/mahao_privacy_termly.html",
                    R.string.privacy_policy));
        });
        mFragmentProfileBinding.linearLayoutMoreTerms.setOnClickListener(v->{
            navigate(MainFragmentDirections.actionNavigationMainToPolicyFragment("file:///android_res/raw/mahao_terms.html",
                    R.string.terms_and_conditions));
        });
        mFragmentProfileBinding.linearLayoutMoreAbout.setOnClickListener(v->{
            navigate(MainFragmentDirections.actionNavigationMainToPolicyFragment("file:///android_res/raw/mahao_about.html",
                    R.string.about_mahao));
        });
        mFragmentProfileBinding.linearLayoutMoreShare.setOnClickListener(v->{
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name));
            String shareMessage= getString(R.string.share_message, BuildConfig.APPLICATION_ID);
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
            startActivity(Intent.createChooser(shareIntent, "Share via"));
        });
        mFragmentProfileBinding.linearLayoutMoreRate.setOnClickListener(v->{
            RatingDialog ratingDialog = getStandardRatingDialogBuilder(requireContext()).build();
            ratingDialog.show();
        });
        mFragmentProfileBinding.linearLayoutMoreLicenses.setOnClickListener(v->{
            startActivity(new Intent(requireContext(), OssLicensesMenuActivity.class));
        });

        return mFragmentProfileBinding.getRoot();
    }
}