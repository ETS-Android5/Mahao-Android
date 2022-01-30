package ke.co.tonyoa.mahao.ui.profile.amenities;

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
import ke.co.tonyoa.mahao.databinding.FragmentAmenitiesListBinding;

public class AmenitiesListFragment extends BaseFragment {

    private FragmentAmenitiesListBinding mFragmentAmenitiesListBinding;
    private AmenitiesViewModel mAmenitiesViewModel;
    private AmenityAdapterPaged mAmenityAdapterPaged;

    public AmenitiesListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mAmenitiesViewModel = new ViewModelProvider(this).get(AmenitiesViewModel.class);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mFragmentAmenitiesListBinding = FragmentAmenitiesListBinding.inflate(inflater, container, false);
        setToolbar(mFragmentAmenitiesListBinding.layoutToolbar.materialToolbarLayoutToolbar);
        setTitle(getString(R.string.amenities));

        mFragmentAmenitiesListBinding.floatingActionButtonAmenitiesListAdd.setOnClickListener(v->{
            navigate(AmenitiesListFragmentDirections.actionAmenitiesListFragmentToSingleAmenityFragment(null));
        });
        mAmenityAdapterPaged = new AmenityAdapterPaged(requireContext(), (amenity, position) -> {
            navigate(AmenitiesListFragmentDirections.actionAmenitiesListFragmentToSingleAmenityFragment(amenity));
        });
        mFragmentAmenitiesListBinding.recyclerViewAmenitiesList.setLayoutManager(new GridLayoutManager(requireContext(), 3));
        mFragmentAmenitiesListBinding.recyclerViewAmenitiesList.setAdapter(mAmenityAdapterPaged);
        fetchData();
        mFragmentAmenitiesListBinding.swipeRefreshLayoutAmenities.setOnRefreshListener(()->mAmenitiesViewModel.invalidateList());

        return mFragmentAmenitiesListBinding.getRoot();
    }

    private void fetchData(){
        LinearLayout loadingView = mFragmentAmenitiesListBinding.linearLayoutAmenitiesListLoading;
        List<RecyclerView> enabledViews = Arrays.asList(mFragmentAmenitiesListBinding.recyclerViewAmenitiesList);
        ViewUtils.load(loadingView, enabledViews, true);
        mAmenitiesViewModel.getAmenities().observe(getViewLifecycleOwner(), userPagedList ->{
            mAmenityAdapterPaged.submitList(userPagedList);
            if (userPagedList.getLoadedCount()>0){
                mFragmentAmenitiesListBinding.recyclerViewAmenitiesList.setVisibility(View.VISIBLE);
                mFragmentAmenitiesListBinding.linearLayoutAmenitiesListEmpty.setVisibility(View.GONE);
            }
            else {
                mFragmentAmenitiesListBinding.recyclerViewAmenitiesList.setVisibility(View.GONE);
                mFragmentAmenitiesListBinding.linearLayoutAmenitiesListEmpty.setVisibility(View.VISIBLE);
            }
        });
        mAmenitiesViewModel.getLoadState().observe(getViewLifecycleOwner(), loadState->{
            if (loadState != RepoDataSource.LOADING_ONGOING){
                mFragmentAmenitiesListBinding.swipeRefreshLayoutAmenities.setRefreshing(false);
                mFragmentAmenitiesListBinding.linearProgressIndicatorAmenitiesList.setVisibility(View.GONE);
            }
            else {
                mFragmentAmenitiesListBinding.linearProgressIndicatorAmenitiesList.setVisibility(View.VISIBLE);
            }

            // No items loaded, and it is loading
            ViewUtils.load(loadingView, enabledViews, mAmenityAdapterPaged.getItemCount() == 0 && loadState == RepoDataSource.LOADING_ONGOING);
            mAmenityAdapterPaged.setLoadState(loadState);
        });
        mAmenitiesViewModel.getErrors().observe(getViewLifecycleOwner(), error->{
            if (error != null) {
                // Show any errors other than one showing no next page found
                if (!error.equalsIgnoreCase("Not Found") || mAmenityAdapterPaged.getItemCount() == 0)
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