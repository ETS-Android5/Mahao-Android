package ke.co.tonyoa.mahao.ui.profile.categories;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.Arrays;
import java.util.List;

import ke.co.tonyoa.mahao.R;
import ke.co.tonyoa.mahao.app.api.responses.PropertyCategory;
import ke.co.tonyoa.mahao.app.navigation.BaseFragment;
import ke.co.tonyoa.mahao.app.utils.ViewUtils;
import ke.co.tonyoa.mahao.databinding.FragmentCategoriesListBinding;

public class CategoriesListFragment extends BaseFragment {

    private FragmentCategoriesListBinding mFragmentCategoriesListBinding;
    private CategoriesListViewModel mCategoriesListViewModel;
    private CategoryAdapter mCategoryAdapter;

    public CategoriesListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mCategoriesListViewModel = new ViewModelProvider(this).get(CategoriesListViewModel.class);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mFragmentCategoriesListBinding = FragmentCategoriesListBinding.inflate(inflater, container, false);
        setToolbar(mFragmentCategoriesListBinding.layoutToolbar.materialToolbarLayoutToolbar);
        setTitle(getString(R.string.categories));

        mFragmentCategoriesListBinding.floatingActionButtonCategoriesListAdd.setOnClickListener(v->{
            navigate(CategoriesListFragmentDirections.actionCategoriesListFragmentToSingleCategoryFragment(null));
        });
        mCategoryAdapter = new CategoryAdapter(requireContext(), (propertyCategory, position) -> {
            navigate(CategoriesListFragmentDirections.actionCategoriesListFragmentToSingleCategoryFragment(propertyCategory));
        });
        mFragmentCategoriesListBinding.recyclerViewCategoriesList.setLayoutManager(new GridLayoutManager(requireContext(), 3));
        mFragmentCategoriesListBinding.recyclerViewCategoriesList.setAdapter(mCategoryAdapter);
        fetchData();
        mFragmentCategoriesListBinding.swipeRefreshLayoutCategories.setOnRefreshListener(this::fetchData);

        return mFragmentCategoriesListBinding.getRoot();
    }

    private void fetchData(){
        LinearLayout loadingView = mFragmentCategoriesListBinding.linearLayoutCategoriesListLoading;
        List<RecyclerView> enabledViews = Arrays.asList(mFragmentCategoriesListBinding.recyclerViewCategoriesList);
        ViewUtils.load(loadingView, enabledViews, true);
        mCategoriesListViewModel.getPropertyCategories().observe(getViewLifecycleOwner(), listAPIResponse -> {
            ViewUtils.load(loadingView, enabledViews, false);
            int propertyCount = 0;
            if (listAPIResponse!=null && listAPIResponse.isSuccessful()){
                List<PropertyCategory> propertyCategories = listAPIResponse.body();
                mCategoryAdapter.submitList(propertyCategories);
                if (propertyCategories!=null)
                    propertyCount = propertyCategories.size();
            }
            else {
                Toast.makeText(requireContext(),
                        (listAPIResponse==null || listAPIResponse.errorMessage(requireContext())==null)?
                                getString(R.string.unknown_error):
                                listAPIResponse.errorMessage(requireContext()),
                        Toast.LENGTH_SHORT).show();
                propertyCount = mCategoryAdapter.getItemCount();
            }

            if (propertyCount>0){
                mFragmentCategoriesListBinding.linearLayoutCategoriesListEmpty.setVisibility(View.GONE);
                mFragmentCategoriesListBinding.recyclerViewCategoriesList.setVisibility(View.VISIBLE);
            }
            else {
                mFragmentCategoriesListBinding.linearLayoutCategoriesListEmpty.setVisibility(View.VISIBLE);
                mFragmentCategoriesListBinding.recyclerViewCategoriesList.setVisibility(View.GONE);
            }
            mFragmentCategoriesListBinding.swipeRefreshLayoutCategories.setRefreshing(false);
        });
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