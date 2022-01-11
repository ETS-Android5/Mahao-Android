package ke.co.tonyoa.mahao.ui.profile.users.single;

import android.os.Bundle;

import androidx.annotation.NonNull;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;

import ke.co.tonyoa.mahao.R;
import ke.co.tonyoa.mahao.app.api.responses.User;
import ke.co.tonyoa.mahao.app.navigation.BaseFragment;
import ke.co.tonyoa.mahao.databinding.FragmentViewUserBinding;

public class ViewUserFragment extends BaseFragment {

    private User mUser;
    private FragmentViewUserBinding mFragmentViewUserBinding;

    public ViewUserFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if (getArguments() != null) {
            mUser = ViewUserFragmentArgs.fromBundle(getArguments()).getUser();
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mFragmentViewUserBinding = FragmentViewUserBinding.inflate(inflater, container, false);
        setToolbar(mFragmentViewUserBinding.layoutToolbar.materialToolbarLayoutToolbar);
        if (mUser==null){
            setTitle("New User");
        }
        else {
            setTitle(mUser.getFirstName());
        }

        if (mUser!=null){
            Glide.with(requireContext())
                    .load(mUser.getProfilePicture())
                    .placeholder(R.drawable.ic_baseline_person_24)
                    .error(R.drawable.ic_baseline_person_24)
                    .into(mFragmentViewUserBinding.imageViewUser);
            mFragmentViewUserBinding.textViewUserName.setText(mUser.getFirstName()+" "+mUser.getLastName());
            mFragmentViewUserBinding.textViewUserEmail.setText(mUser.getEmail());
            mFragmentViewUserBinding.textViewUserPhone.setText(mUser.getPhone());
        }

        return mFragmentViewUserBinding.getRoot();
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