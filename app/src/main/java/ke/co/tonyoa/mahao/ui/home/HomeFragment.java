package ke.co.tonyoa.mahao.ui.home;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

import ke.co.tonyoa.mahao.R;
import ke.co.tonyoa.mahao.app.api.APIResponse;
import ke.co.tonyoa.mahao.app.api.responses.Property;
import ke.co.tonyoa.mahao.app.enums.FeedbackType;
import ke.co.tonyoa.mahao.app.interfaces.OnItemClickListener;
import ke.co.tonyoa.mahao.app.navigation.BaseFragment;
import ke.co.tonyoa.mahao.app.utils.SnapScrollListener;
import ke.co.tonyoa.mahao.app.utils.ViewUtils;
import ke.co.tonyoa.mahao.databinding.FragmentHomeBinding;
import ke.co.tonyoa.mahao.databinding.LayoutSomeHousesBinding;
import ke.co.tonyoa.mahao.ui.main.MainFragmentDirections;
import ke.co.tonyoa.mahao.ui.main.MainViewModel;
import ke.co.tonyoa.mahao.ui.properties.PropertyAdapter;

public class HomeFragment extends BaseFragment {

    public static final String LISTENER = "LISTENER";
    private FragmentHomeBinding mFragmentHomeBinding;
    private HomeViewModel mHomeViewModel;
    private MainViewModel mMainViewModel;

    private PropertyAdapter mPropertyAdapterRecommended;
    private PropertyAdapter mPropertyAdapterNearby;
    private PropertyAdapter mPropertyAdapterLatest;
    private PropertyAdapter mPropertyAdapterPopular;
    private PropertyAdapter mPropertyAdapterFavorite;
    private PropertyAdapter mPropertyAdapterPersonal;
    private final OnItemClickListener<Property> mPropertyOnItemClickListener = (property, position) -> {
        mHomeViewModel.addFeedback(property.getId(), FeedbackType.CLICK);
        navigate(MainFragmentDirections.actionNavigationMainToSinglePropertyFragment(property));
    };
    private final OnItemClickListener<Property> mPropertyOnReadClickListener = (property, position) -> {
        mHomeViewModel.addFeedback(property.getId(), FeedbackType.READ);
    };
    private OnShowMoreClickListener mOnShowMoreClickListener;
    private ActivityResultLauncher<String> mPermissionResultLauncher;
    private ActivityResultLauncher<Intent> mLocationEnableLauncher;


    public static HomeFragment newInstance(OnShowMoreClickListener onShowMoreClickListener){
        HomeFragment homeFragment = new HomeFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(LISTENER, onShowMoreClickListener);
        homeFragment.setArguments(bundle);
        return homeFragment;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments()!=null){
            mOnShowMoreClickListener = (OnShowMoreClickListener) getArguments().getSerializable(LISTENER);
        }
        mHomeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        mMainViewModel = new ViewModelProvider(requireParentFragment()).get(MainViewModel.class);

        mPermissionResultLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), result -> {
            if (result!=null && result){
                setupCurrentLocation();
            }
            else {
                Snackbar.make(mFragmentHomeBinding.coordinatorLayoutHome,
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

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        mFragmentHomeBinding = FragmentHomeBinding.inflate(inflater, container, false);
        mFragmentHomeBinding.imageViewHomeProfile.setOnClickListener(v->{
            mMainViewModel.setSelectedPosition(3);
        });

        mHomeViewModel.getName().observe(getViewLifecycleOwner(), name->{
            if (name!=null){
                mFragmentHomeBinding.textViewHomeHello.setText(getString(R.string.hello_s, name));
            }
        });
        mHomeViewModel.getProfilePicture().observe(getViewLifecycleOwner(), profilePicture->{
            Glide.with(requireContext())
                    .load(profilePicture)
                    .placeholder(R.drawable.ic_baseline_person_24)
                    .error(R.drawable.ic_baseline_person_24)
                    .transform(new CircleCrop())
                    .into(mFragmentHomeBinding.imageViewHomeProfile);
        });

        mFragmentHomeBinding.layoutSomeHousesRecommended.textViewSomeHousesTitle.setText(R.string.recommended);
        mFragmentHomeBinding.layoutSomeHousesNearby.textViewSomeHousesTitle.setText(R.string.nearby);
        mFragmentHomeBinding.layoutSomeHousesLatest.textViewSomeHousesTitle.setText(R.string.latest);
        mFragmentHomeBinding.layoutSomeHousesPopular.textViewSomeHousesTitle.setText(R.string.popular);
        mFragmentHomeBinding.layoutSomeHousesFavorite.textViewSomeHousesTitle.setText(R.string.favorite);
        mFragmentHomeBinding.layoutSomeHousesPersonal.textViewSomeHousesTitle.setText(R.string.personal);

        mFragmentHomeBinding.layoutSomeHousesRecommended.textViewSomeHousesShowMore.setOnClickListener(v->{
            if (mOnShowMoreClickListener!=null){
                mOnShowMoreClickListener.onShowMore(1);
            }
        });
        mFragmentHomeBinding.layoutSomeHousesNearby.textViewSomeHousesShowMore.setOnClickListener(v->{
            if (mOnShowMoreClickListener!=null){
                mOnShowMoreClickListener.onShowMore(2);
            }
        });
        mFragmentHomeBinding.layoutSomeHousesLatest.textViewSomeHousesShowMore.setOnClickListener(v->{
            if (mOnShowMoreClickListener!=null){
                mOnShowMoreClickListener.onShowMore(3);
            }
        });
        mFragmentHomeBinding.layoutSomeHousesPopular.textViewSomeHousesShowMore.setOnClickListener(v->{
            if (mOnShowMoreClickListener!=null){
                mOnShowMoreClickListener.onShowMore(4);
            }
        });
        mFragmentHomeBinding.layoutSomeHousesFavorite.textViewSomeHousesShowMore.setOnClickListener(v->{
            if (mOnShowMoreClickListener!=null){
                mOnShowMoreClickListener.onShowMore(5);
            }
        });
        mFragmentHomeBinding.layoutSomeHousesPersonal.textViewSomeHousesShowMore.setOnClickListener(v->{
            if (mOnShowMoreClickListener!=null){
                mOnShowMoreClickListener.onShowMore(6);
            }
        });

        setupAdapters();
        setupRecyclerViews();
        fetchData();
        mFragmentHomeBinding.getRoot().setOnRefreshListener(this::fetchData);

        setupCurrentLocation();
        return mFragmentHomeBinding.getRoot();
    }

    private void fetchData(){
        fetchSingleRow(mFragmentHomeBinding.layoutSomeHousesRecommended, mHomeViewModel.getRecommendedProperties(),
                mPropertyAdapterRecommended);
        fetchSingleRow(mFragmentHomeBinding.layoutSomeHousesNearby, mHomeViewModel.getNearbyProperties(),
                mPropertyAdapterNearby);
        fetchSingleRow(mFragmentHomeBinding.layoutSomeHousesLatest, mHomeViewModel.getLatestProperties(),
                mPropertyAdapterLatest);
        fetchSingleRow(mFragmentHomeBinding.layoutSomeHousesPopular, mHomeViewModel.getPopularProperties(),
                mPropertyAdapterPopular);
        fetchSingleRow(mFragmentHomeBinding.layoutSomeHousesFavorite, mHomeViewModel.getFavoriteProperties(),
                mPropertyAdapterFavorite);
        fetchSingleRow(mFragmentHomeBinding.layoutSomeHousesPersonal, mHomeViewModel.getPersonalProperties(),
                mPropertyAdapterPersonal);
        mFragmentHomeBinding.getRoot().setRefreshing(false);
    }

    private void fetchSingleRow(LayoutSomeHousesBinding layoutSomeHousesBinding, LiveData<APIResponse<List<Property>>> apiResponseLiveData,
                                PropertyAdapter propertyAdapter){
        LottieAnimationView loadingView = layoutSomeHousesBinding.animationViewSomeHousesLoading;
        List<RecyclerView> enabledViews = Arrays.asList(layoutSomeHousesBinding.recyclerViewSomeHouses);
        ViewUtils.load(loadingView, enabledViews, true);
        apiResponseLiveData.observe(getViewLifecycleOwner(), listAPIResponse -> {
            ViewUtils.load(loadingView, enabledViews, false);
            int propertyCount = 0;
            if (listAPIResponse!=null && listAPIResponse.isSuccessful()){
                List<Property> properties = listAPIResponse.body();
                propertyAdapter.submitList(properties);
                if (properties!=null) {
                    propertyCount = properties.size();

                    //Add tabs
                    layoutSomeHousesBinding.tabLayoutSomeHouses.removeAllTabs();
                    for (int index = 0; index<propertyCount; index++) {
                        TabLayout.Tab newTab = layoutSomeHousesBinding.tabLayoutSomeHouses.newTab();
                        layoutSomeHousesBinding.tabLayoutSomeHouses.addTab(newTab);
                    }
                }
            }
            else {
                Toast.makeText(requireContext(),
                        (listAPIResponse==null || listAPIResponse.errorMessage(requireContext())==null)?
                                getString(R.string.unknown_error):
                                listAPIResponse.errorMessage(requireContext()),
                        Toast.LENGTH_SHORT).show();
                propertyCount = propertyAdapter.getItemCount();
            }

            layoutSomeHousesBinding.getRoot().setVisibility(propertyCount>0?View.VISIBLE:View.GONE);
        });
    }

    private void setupAdapters(){
        OnFavoriteClickListener onRecommendedLikeListener = new OnFavoriteClickListener(mPropertyAdapterRecommended);
        mPropertyAdapterRecommended = new PropertyAdapter(PropertyAdapter.ListType.HORIZONTAL_PROPERTY,
                requireContext(), mPropertyOnItemClickListener,
                onRecommendedLikeListener, mPropertyOnReadClickListener);
        onRecommendedLikeListener.setPropertyAdapter(mPropertyAdapterRecommended);
        OnFavoriteClickListener onNearbyLikeListener = new OnFavoriteClickListener(mPropertyAdapterNearby);
        mPropertyAdapterNearby = new PropertyAdapter(PropertyAdapter.ListType.HORIZONTAL_PROPERTY,
                requireContext(), mPropertyOnItemClickListener,
                onNearbyLikeListener, mPropertyOnReadClickListener);
        onNearbyLikeListener.setPropertyAdapter(mPropertyAdapterNearby);
        OnFavoriteClickListener onLatestPropertyLikeListener = new OnFavoriteClickListener(mPropertyAdapterLatest);
        mPropertyAdapterLatest = new PropertyAdapter(PropertyAdapter.ListType.HORIZONTAL_PROPERTY,
                requireContext(), mPropertyOnItemClickListener,
                onLatestPropertyLikeListener, mPropertyOnReadClickListener);
        onLatestPropertyLikeListener.setPropertyAdapter(mPropertyAdapterLatest);
        OnFavoriteClickListener onPopularLikeListener = new OnFavoriteClickListener(mPropertyAdapterPopular);
        mPropertyAdapterPopular = new PropertyAdapter(PropertyAdapter.ListType.HORIZONTAL_PROPERTY,
                requireContext(), mPropertyOnItemClickListener,
                onPopularLikeListener, mPropertyOnReadClickListener);
        onPopularLikeListener.setPropertyAdapter(mPropertyAdapterPopular);
        OnFavoriteClickListener onFavoriteLikeListener = new OnFavoriteClickListener(mPropertyAdapterFavorite);
        mPropertyAdapterFavorite = new PropertyAdapter(PropertyAdapter.ListType.HORIZONTAL_PROPERTY,
                requireContext(), mPropertyOnItemClickListener,
                onFavoriteLikeListener, mPropertyOnReadClickListener);
        onFavoriteLikeListener.setPropertyAdapter(mPropertyAdapterFavorite);
        OnFavoriteClickListener onPersonalLikeListener = new OnFavoriteClickListener(mPropertyAdapterPersonal);
        mPropertyAdapterPersonal = new PropertyAdapter(PropertyAdapter.ListType.HORIZONTAL_PROPERTY,
                requireContext(), mPropertyOnItemClickListener,
                onPersonalLikeListener, mPropertyOnReadClickListener);
        onPersonalLikeListener.setPropertyAdapter(mPropertyAdapterPersonal);
    }


    private void setupRecyclerViews(){
        setupRecyclerView(mPropertyAdapterRecommended,
                mFragmentHomeBinding.layoutSomeHousesRecommended.recyclerViewSomeHouses,
                mFragmentHomeBinding.layoutSomeHousesRecommended.tabLayoutSomeHouses);
        setupRecyclerView(mPropertyAdapterNearby,
                mFragmentHomeBinding.layoutSomeHousesNearby.recyclerViewSomeHouses,
                mFragmentHomeBinding.layoutSomeHousesNearby.tabLayoutSomeHouses);
        setupRecyclerView(mPropertyAdapterLatest,
                mFragmentHomeBinding.layoutSomeHousesLatest.recyclerViewSomeHouses,
                mFragmentHomeBinding.layoutSomeHousesLatest.tabLayoutSomeHouses);
        setupRecyclerView(mPropertyAdapterPopular,
                mFragmentHomeBinding.layoutSomeHousesPopular.recyclerViewSomeHouses,
                mFragmentHomeBinding.layoutSomeHousesPopular.tabLayoutSomeHouses);
        setupRecyclerView(mPropertyAdapterFavorite,
                mFragmentHomeBinding.layoutSomeHousesFavorite.recyclerViewSomeHouses,
                mFragmentHomeBinding.layoutSomeHousesFavorite.tabLayoutSomeHouses);
        setupRecyclerView(mPropertyAdapterPersonal,
                mFragmentHomeBinding.layoutSomeHousesPersonal.recyclerViewSomeHouses,
                mFragmentHomeBinding.layoutSomeHousesPersonal.tabLayoutSomeHouses);

    }

    private void setupRecyclerView(PropertyAdapter propertyAdapter, RecyclerView recyclerView, TabLayout tabLayout){
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        recyclerView.setAdapter(propertyAdapter);

        PagerSnapHelper pagerSnapHelper = new PagerSnapHelper();
        pagerSnapHelper.attachToRecyclerView(recyclerView);
        recyclerView.addOnScrollListener(new SnapScrollListener(pagerSnapHelper,
                SnapScrollListener.Behavior.NOTIFY_ON_SCROLL,
                position -> {
                    tabLayout.selectTab(tabLayout.getTabAt(position));
                }));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                recyclerView.scrollToPosition(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    class OnFavoriteClickListener implements OnItemClickListener<Property>{

        private PropertyAdapter mPropertyAdapter;

        public OnFavoriteClickListener(PropertyAdapter propertyAdapter){
            mPropertyAdapter = propertyAdapter;
        }

        @Override
        public void onItemClick(Property property, int position) {
            if (property.getIsFavorite()) {
                mHomeViewModel.addFavorite(property.getId()).observe(getViewLifecycleOwner(), favoriteResponseAPIResponse -> {
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
                mHomeViewModel.removeFavorite(property.getId()).observe(getViewLifecycleOwner(), favoriteResponseAPIResponse -> {
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
        }

        public void setPropertyAdapter(PropertyAdapter propertyAdapter) {
            mPropertyAdapter = propertyAdapter;
        }
    }

    private void setupCurrentLocation(){
        // Use fields to define the data types to return.
        List<Place.Field> placeFields = Arrays.asList(Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG);

        // Use the builder to create a FindCurrentPlaceRequest.
        FindCurrentPlaceRequest request = FindCurrentPlaceRequest.newInstance(placeFields);

        // Call findCurrentPlace and handle the response (first check that the user has granted permission).
        if (ContextCompat.checkSelfPermission(requireContext(), ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (!ViewUtils.isLocationEnabled(requireContext())){
                Snackbar.make(mFragmentHomeBinding.coordinatorLayoutHome,
                        R.string.enable_your_location, Snackbar.LENGTH_INDEFINITE)
                        .setAction(R.string.enable, v -> {
                            Intent intent=new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            mLocationEnableLauncher.launch(intent);
                        })
                        .show();
                return;
            }

            mHomeViewModel.listenToLocationUpdates(location -> {
            }, 1000*60, 10);
        }
        else {
            mPermissionResultLauncher.launch(ACCESS_FINE_LOCATION);
        }

    }

    public interface OnShowMoreClickListener extends Serializable {
        void onShowMore(int position);
    }
}