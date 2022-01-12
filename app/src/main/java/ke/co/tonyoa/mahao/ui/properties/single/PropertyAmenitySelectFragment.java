package ke.co.tonyoa.mahao.ui.properties.single;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ke.co.tonyoa.mahao.R;
import ke.co.tonyoa.mahao.app.api.responses.Amenity;
import ke.co.tonyoa.mahao.app.interfaces.OnSaveListener;
import ke.co.tonyoa.mahao.app.utils.ViewUtils;
import ke.co.tonyoa.mahao.databinding.FragmentPropertyAmenitySelectBinding;

public class PropertyAmenitySelectFragment extends BottomSheetDialogFragment {

    private static final String PROPERTY_ID_EXTRA = "PROPERTY_ID_EXTRA";
    private static final String PROPERTY_AMENITIES_EXTRA = "PROPERTY_AMENITIES_EXTRA";
    public static final String LISTENER = "listener";

    private int mPropertyId;
    private ArrayList<Amenity> mPropertyAmenities;
    private FragmentPropertyAmenitySelectBinding mFragmentPropertyAmenitySelectBinding;
    private PropertyAmenityAdapter mPropertyAmenityAdapter;
    private PropertyAmenitySelectViewModel mPropertyAmenitySelectViewModel;
    private LinearLayout mLoadingView;
    private List<View> mEnabledViews;
    private OnPropertyAmenitiesChangedListener mOnPropertyAmenitiesChangedListener;

    public PropertyAmenitySelectFragment() {
        // Required empty public constructor
    }

    public static void show(FragmentManager fragmentManager, int propertyId, ArrayList<Amenity> propertyAmenities,
                            OnPropertyAmenitiesChangedListener onPropertyAmenitiesChangedListener){
        newInstance(propertyId, propertyAmenities, onPropertyAmenitiesChangedListener).show(fragmentManager, "property_amenities");
    }

    public static PropertyAmenitySelectFragment newInstance(int propertyId,ArrayList<Amenity> propertyAmenities,
                                                            OnPropertyAmenitiesChangedListener onPropertyAmenitiesChangedListener) {
        PropertyAmenitySelectFragment fragment = new PropertyAmenitySelectFragment();
        Bundle args = new Bundle();
        args.putInt(PROPERTY_ID_EXTRA, propertyId);
        args.putParcelableArrayList(PROPERTY_AMENITIES_EXTRA, propertyAmenities);
        args.putSerializable(LISTENER, onPropertyAmenitiesChangedListener);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mPropertyId = getArguments().getInt(PROPERTY_ID_EXTRA);
            mPropertyAmenities = getArguments().getParcelableArrayList(PROPERTY_AMENITIES_EXTRA);
            mOnPropertyAmenitiesChangedListener = (OnPropertyAmenitiesChangedListener) getArguments().getSerializable(LISTENER);
        }
        mPropertyAmenitySelectViewModel = new ViewModelProvider(this).get(PropertyAmenitySelectViewModel.class);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (requireParentFragment() instanceof OnSaveListener){
            mOnPropertyAmenitiesChangedListener = (OnPropertyAmenitiesChangedListener)requireParentFragment();
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mFragmentPropertyAmenitySelectBinding = FragmentPropertyAmenitySelectBinding.inflate(inflater, container, false);

        mPropertyAmenityAdapter = new PropertyAmenityAdapter(requireContext(), null, mPropertyAmenities);
        mFragmentPropertyAmenitySelectBinding.recyclerViewSelectAmenities.setLayoutManager(new GridLayoutManager(requireContext(), 3));
        mFragmentPropertyAmenitySelectBinding.recyclerViewSelectAmenities.setAdapter(mPropertyAmenityAdapter);

        mLoadingView = mFragmentPropertyAmenitySelectBinding.linearLayoutSelectAmenitiesLoading;
        mEnabledViews = Arrays.asList(mFragmentPropertyAmenitySelectBinding.recyclerViewSelectAmenities,
                mFragmentPropertyAmenitySelectBinding.buttonSelectAmenitiesDone, mFragmentPropertyAmenitySelectBinding.buttonSelectAmenitiesCancel);

        fetchData();
        
        mFragmentPropertyAmenitySelectBinding.buttonSelectAmenitiesCancel.setOnClickListener(v->{
            dismiss();
        });
        mFragmentPropertyAmenitySelectBinding.buttonSelectAmenitiesDone.setOnClickListener(v->{
            ViewUtils.load(mLoadingView, mEnabledViews, true);
            List<Amenity> addedAmenities = mPropertyAmenityAdapter.getAddedAmenities();
            List<Integer> addedAmenitiesIds = new ArrayList<>();
            for (Amenity amenity:addedAmenities){
                addedAmenitiesIds.add(amenity.getId());
            }
            List<Amenity> removedAmenities = mPropertyAmenityAdapter.getRemovedAmenities();
            List<Integer> removedAmenitiesIds = new ArrayList<>();
            for (Amenity amenity:removedAmenities){
                removedAmenitiesIds.add(amenity.getId());
            }
            mPropertyAmenitySelectViewModel.modifyAmenities(mPropertyId, addedAmenitiesIds,
                    removedAmenitiesIds).observe(getViewLifecycleOwner(), modifyAmenitiesResponseAPIResponse -> {
                ViewUtils.load(mLoadingView, mEnabledViews, false);
                if (modifyAmenitiesResponseAPIResponse!=null && modifyAmenitiesResponseAPIResponse.isSuccessful()){
                            Toast.makeText(requireContext(), "Amenities updated", Toast.LENGTH_SHORT).show();
                            if (mOnPropertyAmenitiesChangedListener!=null)
                                mOnPropertyAmenitiesChangedListener.onPropertyAmenitiesChanged(addedAmenities, removedAmenities);
                            dismiss();
                        }
                        else {
                            Toast.makeText(requireContext(),
                                    (modifyAmenitiesResponseAPIResponse==null || modifyAmenitiesResponseAPIResponse.errorMessage(requireContext())==null)?
                                            getString(R.string.unknown_error):
                                            modifyAmenitiesResponseAPIResponse.errorMessage(requireContext()),
                                    Toast.LENGTH_SHORT).show();
                        }
            });
        });
        
        return mFragmentPropertyAmenitySelectBinding.getRoot();
    }

    private void fetchData(){
        ViewUtils.load(mLoadingView, mEnabledViews, true);
        mPropertyAmenitySelectViewModel.getAmenities().observe(getViewLifecycleOwner(), listAPIResponse -> {
            ViewUtils.load(mLoadingView, mEnabledViews, false);
            int amenityCount = 0;
            if (listAPIResponse!=null && listAPIResponse.isSuccessful()){
                List<Amenity> amenities = listAPIResponse.body();
                mPropertyAmenityAdapter.submitList(amenities);
                if (amenities!=null)
                    amenityCount = amenities.size();
            }
            else {
                Toast.makeText(requireContext(),
                        (listAPIResponse==null || listAPIResponse.errorMessage(requireContext())==null)?
                                getString(R.string.unknown_error):
                                listAPIResponse.errorMessage(requireContext()),
                        Toast.LENGTH_SHORT).show();
                amenityCount = mPropertyAmenityAdapter.getItemCount();
            }

            if (amenityCount>0){
                mFragmentPropertyAmenitySelectBinding.linearLayoutSelectAmenitiesEmpty.setVisibility(View.GONE);
                mFragmentPropertyAmenitySelectBinding.recyclerViewSelectAmenities.setVisibility(View.VISIBLE);
            }
            else {
                mFragmentPropertyAmenitySelectBinding.linearLayoutSelectAmenitiesEmpty.setVisibility(View.VISIBLE);
                mFragmentPropertyAmenitySelectBinding.recyclerViewSelectAmenities.setVisibility(View.GONE);
            }
        });
    }

    interface OnPropertyAmenitiesChangedListener extends Serializable {
        void onPropertyAmenitiesChanged(List<Amenity> added, List<Amenity> removed);
    }
}