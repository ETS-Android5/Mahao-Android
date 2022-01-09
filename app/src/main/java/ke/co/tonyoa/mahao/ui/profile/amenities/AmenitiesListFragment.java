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
import ke.co.tonyoa.mahao.app.api.responses.Amenity;
import ke.co.tonyoa.mahao.app.navigation.BaseFragment;
import ke.co.tonyoa.mahao.app.utils.ViewUtils;
import ke.co.tonyoa.mahao.databinding.FragmentAmenitiesListBinding;

public class AmenitiesListFragment extends BaseFragment {

    private FragmentAmenitiesListBinding mFragmentAmenitiesListBinding;
    private AmenitiesViewModel mAmenitiesViewModel;
    private AmenityAdapter mAmenityAdapter;

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
        mAmenityAdapter = new AmenityAdapter(requireContext(), (amenity, position) -> {
            navigate(AmenitiesListFragmentDirections.actionAmenitiesListFragmentToSingleAmenityFragment(amenity));
        });
        mFragmentAmenitiesListBinding.recyclerViewAmenitiesList.setLayoutManager(new GridLayoutManager(requireContext(), 3));
        mFragmentAmenitiesListBinding.recyclerViewAmenitiesList.setAdapter(mAmenityAdapter);
        fetchData();
        mFragmentAmenitiesListBinding.swipeRefreshLayoutAmenities.setOnRefreshListener(this::fetchData);

        return mFragmentAmenitiesListBinding.getRoot();
    }

    private void fetchData(){
        LinearLayout loadingView = mFragmentAmenitiesListBinding.linearLayoutAmenitiesListLoading;
        List<RecyclerView> enabledViews = Arrays.asList(mFragmentAmenitiesListBinding.recyclerViewAmenitiesList);
        ViewUtils.load(loadingView, enabledViews, true);
        mAmenitiesViewModel.getAmenities().observe(getViewLifecycleOwner(), listAPIResponse -> {
            ViewUtils.load(loadingView, enabledViews, false);
            int amenitiesCount = 0;
            if (listAPIResponse!=null && listAPIResponse.isSuccessful()){
                List<Amenity> amenities = listAPIResponse.body();
                mAmenityAdapter.submitList(amenities);
                if (amenities!=null)
                    amenitiesCount = amenities.size();
            }
            else {
                Toast.makeText(requireContext(),
                        (listAPIResponse==null || listAPIResponse.errorMessage(requireContext())==null)?
                                getString(R.string.unknown_error):
                                listAPIResponse.errorMessage(requireContext()),
                        Toast.LENGTH_SHORT).show();
                amenitiesCount = mAmenityAdapter.getItemCount();
            }

            if (amenitiesCount>0){
                mFragmentAmenitiesListBinding.linearLayoutAmenitiesListEmpty.setVisibility(View.GONE);
                mFragmentAmenitiesListBinding.recyclerViewAmenitiesList.setVisibility(View.VISIBLE);
            }
            else {
                mFragmentAmenitiesListBinding.linearLayoutAmenitiesListEmpty.setVisibility(View.VISIBLE);
                mFragmentAmenitiesListBinding.recyclerViewAmenitiesList.setVisibility(View.GONE);
            }
            mFragmentAmenitiesListBinding.swipeRefreshLayoutAmenities.setRefreshing(false);
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