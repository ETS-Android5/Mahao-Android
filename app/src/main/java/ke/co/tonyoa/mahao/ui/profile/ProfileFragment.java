package ke.co.tonyoa.mahao.ui.profile;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;

import java.util.Arrays;

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

        boolean isAdmin = mProfileViewModel.isAdmin();
        for (View view: Arrays.asList(mFragmentProfileBinding.linearLayoutProfileUsers, mFragmentProfileBinding.linearLayoutProfileCategories,
                mFragmentProfileBinding.linearLayoutProfileAmenities)){
            view.setVisibility(isAdmin?View.VISIBLE:View.GONE);
        }
        mFragmentProfileBinding.linearLayoutProfileUsers.setOnClickListener(v->{
            //TODO: Navigate to users
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

        return mFragmentProfileBinding.getRoot();
    }
}