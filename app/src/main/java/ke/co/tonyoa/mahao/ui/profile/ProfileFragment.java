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

import ke.co.tonyoa.mahao.R;
import ke.co.tonyoa.mahao.databinding.FragmentProfileBinding;
import ke.co.tonyoa.mahao.databinding.FragmentViewUserBinding;

public class ProfileFragment extends Fragment {

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

        mFragmentProfileBinding.linearLayoutProfileUsers.setOnClickListener(v->{
            //TODO: Navigate to users
        });
        mFragmentProfileBinding.linearLayoutProfileCategories.setOnClickListener(v->{
            //TODO: Navigate to categories
        });
        mFragmentProfileBinding.linearLayoutProfileAmenities.setOnClickListener(v->{
            //TODO: Navigate to amenities
        });
        mFragmentProfileBinding.buttonProfileLogout.setOnClickListener(v->{
            mProfileViewModel.logOut();
        });

        return mFragmentProfileBinding.getRoot();
    }
}