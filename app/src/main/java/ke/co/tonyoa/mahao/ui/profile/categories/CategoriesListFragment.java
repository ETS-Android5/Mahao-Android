package ke.co.tonyoa.mahao.ui.profile.categories;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.Arrays;
import java.util.List;

import ke.co.tonyoa.mahao.R;
import ke.co.tonyoa.mahao.app.navigation.BaseFragment;
import ke.co.tonyoa.mahao.app.paging.RepoDataSource;
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
        mFragmentCategoriesListBinding.swipeRefreshLayoutCategories.setOnRefreshListener(()-> mCategoriesListViewModel.invalidateList());

        return mFragmentCategoriesListBinding.getRoot();
    }

    private void fetchData(){
        LinearLayout loadingView = mFragmentCategoriesListBinding.linearLayoutCategoriesListLoading;
        List<RecyclerView> enabledViews = Arrays.asList(mFragmentCategoriesListBinding.recyclerViewCategoriesList);
        ViewUtils.load(loadingView, enabledViews, true);
        mCategoriesListViewModel.getPropertyCategories().observe(getViewLifecycleOwner(), categoriesPagedList ->{
            mCategoryAdapter.submitList(categoriesPagedList);
            if (categoriesPagedList.getLoadedCount()>0){
                mFragmentCategoriesListBinding.recyclerViewCategoriesList.setVisibility(View.VISIBLE);
                mFragmentCategoriesListBinding.linearLayoutCategoriesListEmpty.setVisibility(View.GONE);
            }
            else {
                mFragmentCategoriesListBinding.recyclerViewCategoriesList.setVisibility(View.GONE);
                mFragmentCategoriesListBinding.linearLayoutCategoriesListEmpty.setVisibility(View.VISIBLE);
            }
        });
        mCategoriesListViewModel.getLoadState().observe(getViewLifecycleOwner(), loadState->{
            if (loadState != RepoDataSource.LOADING_ONGOING){
                mFragmentCategoriesListBinding.swipeRefreshLayoutCategories.setRefreshing(false);
                mFragmentCategoriesListBinding.linearProgressIndicatorCategoriesList.setVisibility(View.GONE);
            }
            else {
                mFragmentCategoriesListBinding.linearProgressIndicatorCategoriesList.setVisibility(View.VISIBLE);
            }

            // No items loaded, and it is loading
            ViewUtils.load(loadingView, enabledViews, mCategoryAdapter.getItemCount() == 0 && loadState == RepoDataSource.LOADING_ONGOING);
            mCategoryAdapter.setLoadState(loadState);
        });
        mCategoriesListViewModel.getErrors().observe(getViewLifecycleOwner(), error->{
            if (error != null) {
                // Show any errors other than one showing no next page found
                if (!error.equalsIgnoreCase("Not Found") || mCategoryAdapter.getItemCount() == 0)
                    Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
            }
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