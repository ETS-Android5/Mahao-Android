package ke.co.tonyoa.mahao.ui.home;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;

import java.util.Arrays;
import java.util.List;

import ke.co.tonyoa.mahao.R;
import ke.co.tonyoa.mahao.app.api.APIResponse;
import ke.co.tonyoa.mahao.app.api.responses.Property;
import ke.co.tonyoa.mahao.app.interfaces.OnItemClickListener;
import ke.co.tonyoa.mahao.app.navigation.BaseFragment;
import ke.co.tonyoa.mahao.app.utils.ViewUtils;
import ke.co.tonyoa.mahao.databinding.FragmentHomeBinding;
import ke.co.tonyoa.mahao.databinding.LayoutSomeHousesBinding;
import ke.co.tonyoa.mahao.ui.main.MainViewModel;
import ke.co.tonyoa.mahao.ui.properties.PropertyAdapter;

public class HomeFragment extends BaseFragment {

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
        //TODO: Navigate to single property fragment
        //TODO:
    };



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mHomeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        mMainViewModel = new ViewModelProvider(requireParentFragment()).get(MainViewModel.class);
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        mFragmentHomeBinding = FragmentHomeBinding.inflate(inflater, container, false);
        mFragmentHomeBinding.imageViewHomeProfile.setOnClickListener(v->{
            mMainViewModel.setSelectedPosition(2);
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

        setupAdapters();
        setupRecyclerViews();
        fetchData();
        mFragmentHomeBinding.getRoot().setOnRefreshListener(this::fetchData);

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
                if (properties!=null)
                    propertyCount = properties.size();
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
                mFragmentHomeBinding.layoutSomeHousesRecommended.recyclerViewSomeHouses.getWidth(),
                requireContext(), mPropertyOnItemClickListener,
                onRecommendedLikeListener);
        onRecommendedLikeListener.setPropertyAdapter(mPropertyAdapterRecommended);
        OnFavoriteClickListener onNearbyLikeListener = new OnFavoriteClickListener(mPropertyAdapterNearby);
        mPropertyAdapterNearby = new PropertyAdapter(PropertyAdapter.ListType.HORIZONTAL_PROPERTY,
                mFragmentHomeBinding.layoutSomeHousesNearby.recyclerViewSomeHouses.getWidth(),
                requireContext(), mPropertyOnItemClickListener,
                onNearbyLikeListener);
        onNearbyLikeListener.setPropertyAdapter(mPropertyAdapterNearby);
        OnFavoriteClickListener onLatestPropertyLikeListener = new OnFavoriteClickListener(mPropertyAdapterLatest);
        mPropertyAdapterLatest = new PropertyAdapter(PropertyAdapter.ListType.HORIZONTAL_PROPERTY,
                mFragmentHomeBinding.layoutSomeHousesLatest.recyclerViewSomeHouses.getWidth(),
                requireContext(), mPropertyOnItemClickListener,
                onLatestPropertyLikeListener);
        onLatestPropertyLikeListener.setPropertyAdapter(mPropertyAdapterLatest);
        OnFavoriteClickListener onPopularLikeListener = new OnFavoriteClickListener(mPropertyAdapterPopular);
        mPropertyAdapterPopular = new PropertyAdapter(PropertyAdapter.ListType.HORIZONTAL_PROPERTY,
                mFragmentHomeBinding.layoutSomeHousesPopular.recyclerViewSomeHouses.getWidth(),
                requireContext(), mPropertyOnItemClickListener,
                onPopularLikeListener);
        onPopularLikeListener.setPropertyAdapter(mPropertyAdapterPopular);
        OnFavoriteClickListener onFavoriteLikeListener = new OnFavoriteClickListener(mPropertyAdapterFavorite);
        mPropertyAdapterFavorite = new PropertyAdapter(PropertyAdapter.ListType.HORIZONTAL_PROPERTY,
                mFragmentHomeBinding.layoutSomeHousesFavorite.recyclerViewSomeHouses.getWidth(),
                requireContext(), mPropertyOnItemClickListener,
                onFavoriteLikeListener);
        onFavoriteLikeListener.setPropertyAdapter(mPropertyAdapterFavorite);
        OnFavoriteClickListener onPersonalLikeListener = new OnFavoriteClickListener(mPropertyAdapterPersonal);
        mPropertyAdapterPersonal = new PropertyAdapter(PropertyAdapter.ListType.HORIZONTAL_PROPERTY,
                mFragmentHomeBinding.layoutSomeHousesPersonal.recyclerViewSomeHouses.getWidth(),
                requireContext(), mPropertyOnItemClickListener,
                onPersonalLikeListener);
        onPersonalLikeListener.setPropertyAdapter(mPropertyAdapterPersonal);
    }


    private void setupRecyclerViews(){
        setupRecyclerView(mPropertyAdapterRecommended, mFragmentHomeBinding.layoutSomeHousesRecommended.recyclerViewSomeHouses);
        setupRecyclerView(mPropertyAdapterNearby, mFragmentHomeBinding.layoutSomeHousesNearby.recyclerViewSomeHouses);
        setupRecyclerView(mPropertyAdapterLatest, mFragmentHomeBinding.layoutSomeHousesLatest.recyclerViewSomeHouses);
        setupRecyclerView(mPropertyAdapterPopular, mFragmentHomeBinding.layoutSomeHousesPopular.recyclerViewSomeHouses);
        setupRecyclerView(mPropertyAdapterFavorite, mFragmentHomeBinding.layoutSomeHousesFavorite.recyclerViewSomeHouses);
        setupRecyclerView(mPropertyAdapterPersonal, mFragmentHomeBinding.layoutSomeHousesPersonal.recyclerViewSomeHouses);

    }

    private void setupRecyclerView(PropertyAdapter propertyAdapter, RecyclerView recyclerView){
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        recyclerView.setAdapter(propertyAdapter);
        new PagerSnapHelper().attachToRecyclerView(recyclerView);
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
}