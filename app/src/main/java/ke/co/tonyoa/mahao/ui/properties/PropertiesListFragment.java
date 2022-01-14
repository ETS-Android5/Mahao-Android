package ke.co.tonyoa.mahao.ui.properties;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;


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
        PropertiesListViewModelFactory propertiesListViewModelFactory = new PropertiesListViewModelFactory(requireActivity().getApplication(), mPropertyListType);
        mPropertiesListViewModel = new ViewModelProvider(this, propertiesListViewModelFactory).get(PropertiesListViewModel.class);
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
}