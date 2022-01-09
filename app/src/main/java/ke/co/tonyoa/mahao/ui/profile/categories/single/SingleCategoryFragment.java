package ke.co.tonyoa.mahao.ui.profile.categories.single;

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
import ke.co.tonyoa.mahao.app.api.responses.PropertyCategory;
import ke.co.tonyoa.mahao.app.interfaces.OnSaveListener;
import ke.co.tonyoa.mahao.app.navigation.BaseFragment;
import ke.co.tonyoa.mahao.app.utils.DialogUtils;
import ke.co.tonyoa.mahao.app.utils.ViewUtils;
import ke.co.tonyoa.mahao.databinding.FragmentSingleCategoryBinding;

public class SingleCategoryFragment extends BaseFragment implements OnSaveListener<PropertyCategory> {
    public static final String PROPERTY_CATEGORY_EXTRA = "PROPERTY_CATEGORY";

    private PropertyCategory mPropertyCategory;
    private FragmentSingleCategoryBinding mFragmentSingleCategoryBinding;
    private SingleCategoryViewModel mSingleCategoryViewModel;
    private SingleCategoryAdapter mSingleCategoryAdapter;

    public SingleCategoryFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if (getArguments() != null) {
            mPropertyCategory = SingleCategoryFragmentArgs.fromBundle(getArguments()).getCategory();
        }
        mSingleCategoryViewModel = new ViewModelProvider(this).get(SingleCategoryViewModel.class);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mFragmentSingleCategoryBinding = FragmentSingleCategoryBinding.inflate(inflater, container, false);
        setToolbar(mFragmentSingleCategoryBinding.layoutToolbar.materialToolbarLayoutToolbar);
        if (mPropertyCategory==null){
            setTitle("New Category");
        }
        else {
            setTitle(mPropertyCategory.getTitle());
        }

        mFragmentSingleCategoryBinding.viewPagerSingleCategory.setUserInputEnabled(false);
        mSingleCategoryAdapter = new SingleCategoryAdapter(this);
        mFragmentSingleCategoryBinding.viewPagerSingleCategory.setAdapter(mSingleCategoryAdapter);

        mSingleCategoryViewModel.getSelectedPosition().observe(getViewLifecycleOwner(), integer->{
            mFragmentSingleCategoryBinding.viewPagerSingleCategory.setCurrentItem(integer);
        });
        mFragmentSingleCategoryBinding.viewPagerSingleCategory.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                requireActivity().invalidateOptionsMenu();
            }
        });

        return mFragmentSingleCategoryBinding.getRoot();
    }


    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.edit_delete, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu) {
        super.onPrepareOptionsMenu(menu);
        int currentItem = mFragmentSingleCategoryBinding.viewPagerSingleCategory.getCurrentItem();
        if (mSingleCategoryAdapter.getItemCount() <= 1 || currentItem != 0) {
            menu.clear();
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == android.R.id.home) {
            if (mSingleCategoryViewModel.getSelectedPosition().getValue() != 0) {
                mSingleCategoryViewModel.setSelectedPosition(0);
            } else {
                navigateUp();
            }
            return true;
        }
        else if (itemId == R.id.action_editDoneDelete_edit) {//Move to the edit fragment
            mSingleCategoryViewModel.setSelectedPosition(1);
            return true;
        }
        else if (itemId == R.id.action_editDoneDelete_delete){
            if (mPropertyCategory!=null){
                DialogUtils.showDialog(requireContext(), requireContext().getString(R.string.delete_s, "Category"),
                        requireContext().getString(R.string.delete_prompt_text, "Category"),
                        "Cancel", "Delete", (dialog, which) -> {
                            ViewUtils.load(mFragmentSingleCategoryBinding.linearLayoutSingleCategoryLoading,
                                    Arrays.asList(mFragmentSingleCategoryBinding.layoutToolbar.materialToolbarLayoutToolbar),
                                    true);
                            mSingleCategoryViewModel.deletePropertyCategory(mPropertyCategory.getId()).observe(getViewLifecycleOwner(), propertyCategoryAPIResponse -> {
                                ViewUtils.load(mFragmentSingleCategoryBinding.linearLayoutSingleCategoryLoading,
                                        Arrays.asList(mFragmentSingleCategoryBinding.layoutToolbar.materialToolbarLayoutToolbar),
                                        false);
                                if (propertyCategoryAPIResponse!=null && propertyCategoryAPIResponse.isSuccessful()){
                                    Toast.makeText(requireContext(),  getString(R.string.successfully_deleted_s, "Category"), Toast.LENGTH_SHORT).show();
                                    navigateUp();
                                }
                                else {
                                    Toast.makeText(requireContext(),
                                            (propertyCategoryAPIResponse==null||propertyCategoryAPIResponse.errorMessage(requireContext())==null)?
                                                    getString(R.string.unknown_error):
                                                    propertyCategoryAPIResponse.errorMessage(requireContext()),
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
    public void onSave(PropertyCategory propertyCategory) {
        mPropertyCategory = propertyCategory;
        setTitle(mPropertyCategory.getTitle());
        mSingleCategoryAdapter=new SingleCategoryAdapter(this);
        mFragmentSingleCategoryBinding.viewPagerSingleCategory.setAdapter(mSingleCategoryAdapter);

        mSingleCategoryViewModel.setSelectedPosition(0);
    }

    class SingleCategoryAdapter extends FragmentStateAdapter{

        public SingleCategoryAdapter(@NonNull Fragment fragment) {
            super(fragment);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            if (mPropertyCategory==null){
                return EditCategoryFragment.newInstance(mPropertyCategory);
            }
            else {
                switch (position) {
                    case 0:
                        return ViewCategoryFragment.newInstance(mPropertyCategory);
                    case 1:
                        return EditCategoryFragment.newInstance(mPropertyCategory);
                }
            }
            return null;
        }

        @Override
        public int getItemCount() {
            return mPropertyCategory==null ? 1 : 2;
        }
    }
}