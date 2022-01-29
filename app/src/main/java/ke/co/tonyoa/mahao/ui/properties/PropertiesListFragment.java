package ke.co.tonyoa.mahao.ui.properties;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;


import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import java.util.Arrays;
import java.util.List;

import ke.co.tonyoa.mahao.R;
import ke.co.tonyoa.mahao.app.api.responses.Property;
import ke.co.tonyoa.mahao.app.enums.FeedbackType;
import ke.co.tonyoa.mahao.app.interfaces.OnItemClickListener;
import ke.co.tonyoa.mahao.app.navigation.BaseFragment;
import ke.co.tonyoa.mahao.app.utils.ViewUtils;
import ke.co.tonyoa.mahao.databinding.FragmentPropertiesListBinding;
import ke.co.tonyoa.mahao.ui.main.MainFragmentDirections;

public class PropertiesListFragment extends BaseFragment {

    private static final String PROPERTY_LIST_ARG = "property_list";

    public enum PropertyListType {
        ALL,
        RECOMMENDED,
        NEARBY,
        LATEST,
        POPULAR,
        FAVORITE,
        PERSONAL
    }

    private FragmentPropertiesListBinding mFragmentPropertiesListBinding;
    private PropertiesListViewModel mPropertiesListViewModel;
    private PropertyListType mPropertyListType;
    private PropertyAdapter mPropertyAdapter;
    private ActivityResultLauncher<String> mPermissionResultLauncher;
    private ActivityResultLauncher<Intent> mLocationEnableLauncher;

    public static PropertiesListFragment newInstance(PropertyListType propertyListType){
        Bundle bundle = new Bundle();
        bundle.putSerializable(PROPERTY_LIST_ARG, propertyListType);
        PropertiesListFragment propertiesListFragment = new PropertiesListFragment();
        propertiesListFragment.setArguments(bundle);
        return propertiesListFragment;
    }

    public PropertiesListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments()!=null){
            mPropertyListType = (PropertyListType) getArguments().getSerializable(PROPERTY_LIST_ARG);
        }
        PropertiesViewModel propertiesViewModel = new ViewModelProvider(requireParentFragment()).get(PropertiesViewModel.class);
        PropertiesListViewModelFactory propertiesListViewModelFactory = new PropertiesListViewModelFactory(requireActivity().getApplication(),
                mPropertyListType, propertiesViewModel.getFilterAndSort());
        mPropertiesListViewModel = new ViewModelProvider(this, propertiesListViewModelFactory).get(PropertiesListViewModel.class);

        mPermissionResultLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), result -> {
            if (result!=null && result){
                setupCurrentLocation();
            }
            else {
                Snackbar.make(mFragmentPropertiesListBinding.recyclerViewPropertiesList,
                        "Grant Location Permission for correct functionality",
                        BaseTransientBottomBar.LENGTH_INDEFINITE).setAction("Grant", v -> {
                    mPermissionResultLauncher.launch(ACCESS_FINE_LOCATION);
                }).show();
            }
        });
        mLocationEnableLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            setupCurrentLocation();
        });
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mFragmentPropertiesListBinding = FragmentPropertiesListBinding.inflate(inflater, container, false);

        OnItemClickListener<Property> onItemReadListener = null;
        if (mPropertyListType!=PropertyListType.ALL && mPropertyListType!=PropertyListType.PERSONAL){
            onItemReadListener = new OnItemClickListener<Property>() {
                @Override
                public void onItemClick(Property property, int position) {
                    mPropertiesListViewModel.addFeedback(property.getId(), FeedbackType.READ);
                }
            };
        }
        mPropertyAdapter = new PropertyAdapter(PropertyAdapter.ListType.VERTICAL_PROPERTY,
                mFragmentPropertiesListBinding.recyclerViewPropertiesList.getWidth(), requireContext(),
                (property, position) -> {
                    navigate(MainFragmentDirections.actionNavigationMainToSinglePropertyFragment(property));
                }, (property, position) -> {
                    if (property.getIsFavorite()) {
                        mPropertiesListViewModel.addFavorite(property.getId()).observe(getViewLifecycleOwner(), favoriteResponseAPIResponse -> {
                            if (favoriteResponseAPIResponse != null && favoriteResponseAPIResponse.isSuccessful()) {
                                property.setIsFavorite(true);
                            } else {
                                Toast.makeText(requireContext(),
                                        (favoriteResponseAPIResponse == null || favoriteResponseAPIResponse.errorMessage(requireContext()) == null) ?
                                                getString(R.string.unknown_error) :
                                                favoriteResponseAPIResponse.errorMessage(requireContext()),
                                        Toast.LENGTH_SHORT).show();
                                property.setIsFavorite(false);
                            }
                            mPropertyAdapter.notifyItemChanged(position, Arrays.asList("like"));
                        });
                    }
                    else {
                        mPropertiesListViewModel.removeFavorite(property.getId()).observe(getViewLifecycleOwner(), favoriteResponseAPIResponse -> {
                            if (favoriteResponseAPIResponse != null && favoriteResponseAPIResponse.isSuccessful()) {
                                property.setIsFavorite(false);
                            } else {
                                Toast.makeText(requireContext(),
                                        (favoriteResponseAPIResponse == null || favoriteResponseAPIResponse.errorMessage(requireContext()) == null) ?
                                                getString(R.string.unknown_error) :
                                                favoriteResponseAPIResponse.errorMessage(requireContext()),
                                        Toast.LENGTH_SHORT).show();
                                property.setIsFavorite(true);
                            }
                            mPropertyAdapter.notifyItemChanged(position, Arrays.asList("like"));
                        });
                    }
                }, onItemReadListener);
        mFragmentPropertiesListBinding.recyclerViewPropertiesList.setLayoutManager(new LinearLayoutManager(requireContext(),
                LinearLayoutManager.VERTICAL, false));
        mFragmentPropertiesListBinding.recyclerViewPropertiesList.setAdapter(mPropertyAdapter);
        fetchData();
        mFragmentPropertiesListBinding.getRoot().setOnRefreshListener(this::fetchData);

        return mFragmentPropertiesListBinding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mPropertyListType == PropertyListType.ALL || mPropertyListType == PropertyListType.NEARBY){
            setupCurrentLocation();
        }
    }

    private void fetchData(){
        LinearLayout loadingView = mFragmentPropertiesListBinding.linearLayoutPropertiesListLoading;
        List<RecyclerView> enabledViews = Arrays.asList(mFragmentPropertiesListBinding.recyclerViewPropertiesList);
        ViewUtils.load(loadingView, enabledViews, true);
        mPropertiesListViewModel.getPropertiesLiveData().observe(getViewLifecycleOwner(), listAPIResponse -> {
            ViewUtils.load(loadingView, enabledViews, false);
            int propertyCount = 0;
            if (listAPIResponse!=null && listAPIResponse.isSuccessful()){
                List<Property> properties = listAPIResponse.body();
                mPropertyAdapter.submitList(properties);
                if (properties!=null)
                    propertyCount = properties.size();
            }
            else {
                Toast.makeText(requireContext(),
                        (listAPIResponse==null || listAPIResponse.errorMessage(requireContext())==null)?
                                getString(R.string.unknown_error):
                                listAPIResponse.errorMessage(requireContext()),
                        Toast.LENGTH_SHORT).show();
                propertyCount = mPropertyAdapter.getItemCount();
            }

            if (propertyCount>0){
                mFragmentPropertiesListBinding.linearLayoutPropertiesListEmpty.setVisibility(View.GONE);
                mFragmentPropertiesListBinding.recyclerViewPropertiesList.setVisibility(View.VISIBLE);
            }
            else {
                mFragmentPropertiesListBinding.linearLayoutPropertiesListEmpty.setVisibility(View.VISIBLE);
                mFragmentPropertiesListBinding.recyclerViewPropertiesList.setVisibility(View.GONE);
            }
            mFragmentPropertiesListBinding.getRoot().setRefreshing(false);
        });
    }

    private void setupCurrentLocation(){
        // Use fields to define the data types to return.
        List<Place.Field> placeFields = Arrays.asList(Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG);

        // Use the builder to create a FindCurrentPlaceRequest.
        FindCurrentPlaceRequest request = FindCurrentPlaceRequest.newInstance(placeFields);

        // Call findCurrentPlace and handle the response (first check that the user has granted permission).
        if (ContextCompat.checkSelfPermission(requireContext(), ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (!ViewUtils.isLocationEnabled(requireContext())){
                Snackbar.make(mFragmentPropertiesListBinding.recyclerViewPropertiesList,
                        R.string.enable_your_location, Snackbar.LENGTH_INDEFINITE)
                        .setAction(R.string.enable, v -> {
                            Intent intent=new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            mLocationEnableLauncher.launch(intent);
                        })
                        .show();
                return;
            }

            mPropertiesListViewModel.listenToLocationUpdates(location -> {
            }, 1000*60, 10);
        }
        else {
            mPermissionResultLauncher.launch(ACCESS_FINE_LOCATION);
        }

    }
}