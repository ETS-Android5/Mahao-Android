package ke.co.tonyoa.mahao.ui.profile.amenities.single;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.Arrays;

import ke.co.tonyoa.mahao.R;
import ke.co.tonyoa.mahao.app.api.responses.Amenity;
import ke.co.tonyoa.mahao.app.interfaces.OnSaveListener;
import ke.co.tonyoa.mahao.app.navigation.BaseFragment;
import ke.co.tonyoa.mahao.app.utils.DialogUtils;
import ke.co.tonyoa.mahao.app.utils.ViewUtils;
import ke.co.tonyoa.mahao.databinding.FragmentSingleAmenityBinding;

public class SingleAmenityFragment extends BaseFragment implements OnSaveListener<Amenity> {

    public static final String AMENITY_EXTRA = "AMENITY";

    private Amenity mAmenity;
    private FragmentSingleAmenityBinding mFragmentSingleAmenityBinding;
    private SingleAmenityViewModel mSingleAmenityViewModel;
    private SingleAmenityAdapter mSingleAmenityAdapter;

    public SingleAmenityFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if (getArguments() != null) {
            mAmenity = SingleAmenityFragmentArgs.fromBundle(getArguments()).getAmenity();
        }
        mSingleAmenityViewModel = new ViewModelProvider(this).get(SingleAmenityViewModel.class);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mFragmentSingleAmenityBinding = FragmentSingleAmenityBinding.inflate(inflater, container, false);
        setToolbar(mFragmentSingleAmenityBinding.layoutToolbar.materialToolbarLayoutToolbar);
        if (mAmenity==null){
            setTitle("New Amenity");
        }
        else {
            setTitle(mAmenity.getTitle());
        }

        mFragmentSingleAmenityBinding.viewPagerSingleAmenity.setUserInputEnabled(false);
        mSingleAmenityAdapter = new SingleAmenityFragment.SingleAmenityAdapter(this);
        mFragmentSingleAmenityBinding.viewPagerSingleAmenity.setAdapter(mSingleAmenityAdapter);

        mSingleAmenityViewModel.getSelectedPosition().observe(getViewLifecycleOwner(), integer->{
            mFragmentSingleAmenityBinding.viewPagerSingleAmenity.setCurrentItem(integer);
        });
        mFragmentSingleAmenityBinding.viewPagerSingleAmenity.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                requireActivity().invalidateOptionsMenu();
            }
        });

        return mFragmentSingleAmenityBinding.getRoot();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.edit_delete, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu) {
        super.onPrepareOptionsMenu(menu);
        int currentItem = mFragmentSingleAmenityBinding.viewPagerSingleAmenity.getCurrentItem();
        if (mSingleAmenityAdapter.getItemCount() <= 1 || currentItem != 0) {
            menu.clear();
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == android.R.id.home) {
            if (mSingleAmenityViewModel.getSelectedPosition().getValue() != 0) {
                mSingleAmenityViewModel.setSelectedPosition(0);
            } else {
                navigateUp();
            }
            return true;
        }
        else if (itemId == R.id.action_editDoneDelete_edit) {//Move to the edit fragment
            mSingleAmenityViewModel.setSelectedPosition(1);
            return true;
        }
        else if (itemId == R.id.action_editDoneDelete_delete){
            if (mAmenity!=null){
                DialogUtils.showDialog(requireContext(), requireContext().getString(R.string.delete_s, "Amenity"),
                        requireContext().getString(R.string.delete_prompt_text, "Amenity"),
                        "Cancel", "Delete", (dialog, which) -> {
                            ViewUtils.load(mFragmentSingleAmenityBinding.linearLayoutSingleAmenityLoading,
                                    Arrays.asList(mFragmentSingleAmenityBinding.layoutToolbar.materialToolbarLayoutToolbar),
                                    true);
                            mSingleAmenityViewModel.deleteAmenity(mAmenity.getId()).observe(getViewLifecycleOwner(), amenityAPIResponse -> {
                                ViewUtils.load(mFragmentSingleAmenityBinding.linearLayoutSingleAmenityLoading,
                                        Arrays.asList(mFragmentSingleAmenityBinding.layoutToolbar.materialToolbarLayoutToolbar),
                                        false);
                                if (amenityAPIResponse!=null && amenityAPIResponse.isSuccessful()){
                                    Toast.makeText(requireContext(),  getString(R.string.successfully_deleted_s, "Amenity"), Toast.LENGTH_SHORT).show();
                                    navigateUp();
                                }
                                else {
                                    Toast.makeText(requireContext(),
                                            (amenityAPIResponse==null||amenityAPIResponse.errorMessage(requireContext())==null)?
                                                    getString(R.string.unknown_error):
                                                    amenityAPIResponse.errorMessage(requireContext()),
                                            Toast.LENGTH_SHORT).show();
                                }
                            });
                        });
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSave(Amenity amenity) {
        mAmenity = amenity;
        setTitle(mAmenity.getTitle());
        mSingleAmenityAdapter=new SingleAmenityFragment.SingleAmenityAdapter(this);
        mFragmentSingleAmenityBinding.viewPagerSingleAmenity.setAdapter(mSingleAmenityAdapter);
        mSingleAmenityViewModel.setSelectedPosition(0);
    }

    class SingleAmenityAdapter extends FragmentStateAdapter {

        public SingleAmenityAdapter(@NonNull Fragment fragment) {
            super(fragment);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            if (mAmenity==null){
                return EditAmenityFragment.newInstance(mAmenity);
            }
            else {
                switch (position) {
                    case 0:
                        return ViewAmenityFragment.newInstance(mAmenity);
                    case 1:
                        return EditAmenityFragment.newInstance(mAmenity);
                }
            }
            return null;
        }

        @Override
        public int getItemCount() {
            return mAmenity==null ? 1 : 2;
        }
    }
}