package ke.co.tonyoa.mahao.ui.properties.single;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;
import java.util.List;

import ke.co.tonyoa.mahao.R;
import ke.co.tonyoa.mahao.app.api.responses.Amenity;
import ke.co.tonyoa.mahao.databinding.FragmentPropertyAmenitySelectBinding;

public class PropertyAmenitySelectFragment extends BottomSheetDialogFragment {

    private static final String PROPERTY_ID_EXTRA = "PROPERTY_ID_EXTRA";
    private static final String PROPERTY_AMENITIES_EXTRA = "PROPERTY_AMENITIES_EXTRA";

    private int mPropertyId;
    private ArrayList<Amenity> mAllAmenities;
    private ArrayList<Amenity> mPropertyAmenities;
    private FragmentPropertyAmenitySelectBinding mFragmentPropertyAmenitySelectBinding;
    private PropertyAmenityAdapter mPropertyAmenityAdapter;
    private PropertyAmenitySelectViewModel mPropertyAmenitySelectViewModel;

    public PropertyAmenitySelectFragment() {
        // Required empty public constructor
    }

    public static PropertyAmenitySelectFragment newInstance(int propertyId,ArrayList<Amenity> propertyAmenities) {
        PropertyAmenitySelectFragment fragment = new PropertyAmenitySelectFragment();
        Bundle args = new Bundle();
        args.putInt(PROPERTY_ID_EXTRA, propertyId);
        args.putParcelableArrayList(PROPERTY_AMENITIES_EXTRA, propertyAmenities);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mPropertyId = getArguments().getInt(PROPERTY_ID_EXTRA);
            mPropertyAmenities = getArguments().getParcelableArrayList(PROPERTY_AMENITIES_EXTRA);
        }
        mPropertyAmenitySelectViewModel = new ViewModelProvider(this).get(PropertyAmenitySelectViewModel.class);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mFragmentPropertyAmenitySelectBinding = FragmentPropertyAmenitySelectBinding.inflate(inflater, container, false);

        mPropertyAmenityAdapter = new PropertyAmenityAdapter(requireContext(), null, mPropertyAmenities);
        mPropertyAmenityAdapter.submitList(mAllAmenities);
        mFragmentPropertyAmenitySelectBinding.recyclerViewSelectAmenities.setLayoutManager(new GridLayoutManager(requireContext(), 3));
        mFragmentPropertyAmenitySelectBinding.recyclerViewSelectAmenities.setAdapter(mPropertyAmenityAdapter);
        
        mPropertyAmenitySelectViewModel.getAmenities().observe(getViewLifecycleOwner(), listAPIResponse -> {
            if (listAPIResponse!=null && listAPIResponse.isSuccessful()){
                List<Amenity> amenities = listAPIResponse.body();
                mPropertyAmenityAdapter.submitList(amenities);
            }
            else {
                Toast.makeText(requireContext(),
                        (listAPIResponse==null || listAPIResponse.errorMessage(requireContext())==null)?
                                getString(R.string.unknown_error):
                                listAPIResponse.errorMessage(requireContext()),
                        Toast.LENGTH_SHORT).show();
            }
        });
        
        mFragmentPropertyAmenitySelectBinding.buttonSelectAmenitiesCancel.setOnClickListener(v->{
            dismiss();
        });
        mFragmentPropertyAmenitySelectBinding.buttonSelectAmenitiesDone.setOnClickListener(v->{
            mPropertyAmenitySelectViewModel.modifyAmenities(mPropertyId, mPropertyAmenityAdapter.getAddedAmenities(),
                    mPropertyAmenityAdapter.getRemovedAmenities()).observe(getViewLifecycleOwner(), modifyAmenitiesResponseAPIResponse -> {
                        if (modifyAmenitiesResponseAPIResponse!=null && modifyAmenitiesResponseAPIResponse.isSuccessful()){
                            Toast.makeText(requireContext(), "Amenities updated", Toast.LENGTH_SHORT).show();
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
}