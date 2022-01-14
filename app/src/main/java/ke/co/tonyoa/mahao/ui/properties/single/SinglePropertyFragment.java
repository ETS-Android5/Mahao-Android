package ke.co.tonyoa.mahao.ui.properties.single;

import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.Arrays;

import ke.co.tonyoa.mahao.R;
import ke.co.tonyoa.mahao.app.api.responses.Property;
import ke.co.tonyoa.mahao.app.interfaces.OnSaveListener;
import ke.co.tonyoa.mahao.app.navigation.BaseFragment;
import ke.co.tonyoa.mahao.app.utils.DialogUtils;
import ke.co.tonyoa.mahao.app.utils.ViewUtils;
import ke.co.tonyoa.mahao.databinding.FragmentSinglePropertyBinding;

public class SinglePropertyFragment extends BaseFragment implements OnSaveListener<Property> {

    public static final String PROPERTY_EXTRA = "PROPERTY_EXTRA";

    private Property mProperty;
    private FragmentSinglePropertyBinding mFragmentSinglePropertyBinding;
    private SinglePropertyViewModel mSinglePropertyViewModel;
    private SinglePropertyFragment.SinglePropertyAdapter mSinglePropertyAdapter;

    public SinglePropertyFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if (getArguments() != null) {
            mProperty = SinglePropertyFragmentArgs.fromBundle(getArguments()).getProperty();
        }
        mSinglePropertyViewModel = new ViewModelProvider(this).get(SinglePropertyViewModel.class);

        // This callback will only be called when MyFragment is at least Started.
        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                if (mSinglePropertyViewModel.getSelectedPosition().getValue() != 0) {
                    mSinglePropertyViewModel.setSelectedPosition(0);
                } else {
                    navigateUp();
                }
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mFragmentSinglePropertyBinding = FragmentSinglePropertyBinding.inflate(inflater, container, false);

        mFragmentSinglePropertyBinding.viewPagerSingleProperty.setUserInputEnabled(false);
        mSinglePropertyAdapter = new SinglePropertyFragment.SinglePropertyAdapter(this);
        mFragmentSinglePropertyBinding.viewPagerSingleProperty.setAdapter(mSinglePropertyAdapter);

        mSinglePropertyViewModel.getSelectedPosition().observe(getViewLifecycleOwner(), integer->{
            mFragmentSinglePropertyBinding.viewPagerSingleProperty.setCurrentItem(integer);
        });
        mFragmentSinglePropertyBinding.viewPagerSingleProperty.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                requireActivity().invalidateOptionsMenu();
            }
        });

        return mFragmentSinglePropertyBinding.getRoot();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == android.R.id.home) {
            if (mSinglePropertyViewModel.getSelectedPosition().getValue() != 0) {
                mSinglePropertyViewModel.setSelectedPosition(0);
            } else {
                navigateUp();
            }
            return true;
        }
        else if (itemId == R.id.action_editDoneDelete_edit) {//Move to the edit fragment
            mSinglePropertyViewModel.setSelectedPosition(1);
            return true;
        }
        else if (itemId == R.id.action_editDoneDelete_delete){
            if (mProperty!=null){
                DialogUtils.showDialog(requireContext(), requireContext().getString(R.string.delete_s, "Property"),
                        requireContext().getString(R.string.delete_prompt_text, "Property"),
                        "Cancel", "Delete", (dialog, which) -> {
                            ViewUtils.load(mFragmentSinglePropertyBinding.linearLayoutSinglePropertyLoading,
                                    Arrays.asList(mFragmentSinglePropertyBinding.layoutToolbar.materialToolbarLayoutToolbar),
                                    true);
                            mSinglePropertyViewModel.deleteProperty(mProperty.getId()).observe(getViewLifecycleOwner(), propertyAPIResponse -> {
                                ViewUtils.load(mFragmentSinglePropertyBinding.linearLayoutSinglePropertyLoading,
                                        Arrays.asList(mFragmentSinglePropertyBinding.layoutToolbar.materialToolbarLayoutToolbar),
                                        false);
                                if (propertyAPIResponse!=null && propertyAPIResponse.isSuccessful()){
                                    Toast.makeText(requireContext(),  getString(R.string.successfully_deleted_s, "Property"), Toast.LENGTH_SHORT).show();
                                    navigateUp();
                                }
                                else {
                                    Toast.makeText(requireContext(),
                                            (propertyAPIResponse==null||propertyAPIResponse.errorMessage(requireContext())==null)?
                                                    getString(R.string.unknown_error):
                                                    propertyAPIResponse.errorMessage(requireContext()),
                                            Toast.LENGTH_SHORT).show();
                                }
                            });
                        });
            }
            return true;
        }
        else if (itemId == R.id.action_editDoneDelete_verify){
            if (mProperty != null){
                boolean verified = item.getTitle().equals(getString(R.string.verify));
                ViewUtils.load(mFragmentSinglePropertyBinding.linearLayoutSinglePropertyLoading,
                        Arrays.asList(mFragmentSinglePropertyBinding.layoutToolbar.materialToolbarLayoutToolbar),
                        true);
                mSinglePropertyViewModel.saveProperty(mProperty.getId(), null, mProperty.getPropertyCategoryId(),
                        mProperty.getTitle(), mProperty.getDescription(), mProperty.getNumBed(), mProperty.getNumBath(),
                        mProperty.getLocationName(), mProperty.getPrice(), mProperty.getLocation().get(0),
                        mProperty.getLocation().get(1), mProperty.getIsEnabled(), verified)
                        .observe(getViewLifecycleOwner(), propertyAPIResponse -> {
                            ViewUtils.load(mFragmentSinglePropertyBinding.linearLayoutSinglePropertyLoading,
                                    Arrays.asList(mFragmentSinglePropertyBinding.layoutToolbar.materialToolbarLayoutToolbar),
                                    false);
                            if (propertyAPIResponse!=null && propertyAPIResponse.isSuccessful()){
                                onSave(propertyAPIResponse.body());
                            }
                            else {
                                Toast.makeText(requireContext(),
                                        (propertyAPIResponse==null||propertyAPIResponse.errorMessage(requireContext())==null)?
                                                getString(R.string.unknown_error):
                                                propertyAPIResponse.errorMessage(requireContext()),
                                        Toast.LENGTH_SHORT).show();
                            }
                        });

            }
            return true;
        }
        else if (itemId == R.id.action_editDoneDelete_enable){
            if (mProperty != null){
                boolean enabled = item.getTitle().equals(getString(R.string.enable));
                ViewUtils.load(mFragmentSinglePropertyBinding.linearLayoutSinglePropertyLoading,
                        Arrays.asList(mFragmentSinglePropertyBinding.layoutToolbar.materialToolbarLayoutToolbar),
                        true);
                mSinglePropertyViewModel.saveProperty(mProperty.getId(), null, mProperty.getPropertyCategoryId(),
                        mProperty.getTitle(), mProperty.getDescription(), mProperty.getNumBed(), mProperty.getNumBath(),
                        mProperty.getLocationName(), mProperty.getPrice(), mProperty.getLocation().get(0),
                        mProperty.getLocation().get(1), enabled, mProperty.getIsVerified())
                        .observe(getViewLifecycleOwner(), propertyAPIResponse -> {
                            ViewUtils.load(mFragmentSinglePropertyBinding.linearLayoutSinglePropertyLoading,
                                    Arrays.asList(mFragmentSinglePropertyBinding.layoutToolbar.materialToolbarLayoutToolbar),
                                    false);
                            if (propertyAPIResponse!=null && propertyAPIResponse.isSuccessful()){
                                onSave(propertyAPIResponse.body());
                            }
                            else {
                                Toast.makeText(requireContext(),
                                        (propertyAPIResponse==null||propertyAPIResponse.errorMessage(requireContext())==null)?
                                                getString(R.string.unknown_error):
                                                propertyAPIResponse.errorMessage(requireContext()),
                                        Toast.LENGTH_SHORT).show();
                            }
                        });

            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSave(Property property) {
        mProperty = property;
        setTitle(mProperty.getTitle());
        mSinglePropertyAdapter=new SinglePropertyFragment.SinglePropertyAdapter(this);
        mFragmentSinglePropertyBinding.viewPagerSingleProperty.setAdapter(mSinglePropertyAdapter);

        mSinglePropertyViewModel.setSelectedPosition(0);
        requireActivity().invalidateOptionsMenu();
    }

    class SinglePropertyAdapter extends FragmentStateAdapter {

        public SinglePropertyAdapter(@NonNull Fragment fragment) {
            super(fragment);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            if (mProperty==null){
                return EditPropertyFragment.newInstance(mProperty);
            }
            else {
                switch (position) {
                    case 0:
                        return ViewPropertyFragment.newInstance(mProperty);
                    case 1:
                        return EditPropertyFragment.newInstance(mProperty);
                }
            }
            return null;
        }

        @Override
        public int getItemCount() {
            return mProperty==null ? 1 : 2;
        }
    }
}