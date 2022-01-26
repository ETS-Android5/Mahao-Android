package ke.co.tonyoa.mahao.ui.properties.single;

import static ke.co.tonyoa.mahao.ui.properties.single.SinglePropertyFragment.PROPERTY_EXTRA;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavBackStackEntry;

import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.Arrays;
import java.util.List;

import dev.ronnie.github.imagepicker.ImagePicker;
import dev.ronnie.github.imagepicker.ImageResult;
import ke.co.tonyoa.mahao.R;
import ke.co.tonyoa.mahao.app.api.responses.Property;
import ke.co.tonyoa.mahao.app.api.responses.PropertyCategory;
import ke.co.tonyoa.mahao.app.interfaces.OnSaveListener;
import ke.co.tonyoa.mahao.app.navigation.BaseFragment;
import ke.co.tonyoa.mahao.app.utils.ViewUtils;
import ke.co.tonyoa.mahao.databinding.FragmentEditPropertyBinding;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;

public class EditPropertyFragment extends BaseFragment {

    private Property mProperty;
    private FragmentEditPropertyBinding mFragmentEditPropertyBinding;
    private EditPropertyViewModel mEditPropertyViewModel;
    private SinglePropertyViewModel mSinglePropertyViewModel;
    private OnSaveListener<Property> mOnSaveListener;
    private ImagePicker mImagePicker;
    private ArrayAdapter<PropertyCategory> mPropertyCategoryArrayAdapter;

    public EditPropertyFragment() {
        // Required empty public constructor
    }

    public static EditPropertyFragment newInstance(Property property) {
        EditPropertyFragment fragment = new EditPropertyFragment();
        Bundle args = new Bundle();
        args.putSerializable(PROPERTY_EXTRA, property);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mProperty = (Property) getArguments().getSerializable(PROPERTY_EXTRA);
        }
        EditPropertyViewModelFactory editPropertyViewModelFactory = new EditPropertyViewModelFactory(requireActivity().getApplication(),
                mProperty);
        mEditPropertyViewModel = new ViewModelProvider(this, editPropertyViewModelFactory).get(EditPropertyViewModel.class);
        mSinglePropertyViewModel = new ViewModelProvider(requireParentFragment()).get(SinglePropertyViewModel.class);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (requireParentFragment() instanceof OnSaveListener){
            mOnSaveListener = (OnSaveListener<Property>)requireParentFragment();
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mFragmentEditPropertyBinding = FragmentEditPropertyBinding.inflate(inflater, container, false);
        mImagePicker = new ImagePicker(this);

        mEditPropertyViewModel.getProperty().observe(getViewLifecycleOwner(), property -> {
            if (property!=null){
                Glide.with(requireContext())
                        .load(property.getFeatureImage())
                        .placeholder(R.drawable.ic_home_black_24dp)
                        .error(R.drawable.ic_home_black_24dp)
                        .into(mFragmentEditPropertyBinding.imageViewEditPropertyFeatureImage);
                mFragmentEditPropertyBinding.textInputEditTextEditPropertyName.setText(property.getTitle());
                PropertyCategory propertyCategory = property.getPropertyCategory();
                if (propertyCategory!=null) {
                    mFragmentEditPropertyBinding.autoCompleteTextViewEditPropertyCategory.setText(propertyCategory.getTitle());
                }
                mEditPropertyViewModel.setSelectedPropertyCategory(propertyCategory);
                mFragmentEditPropertyBinding.textInputEditTextEditPropertyLocation.setText(property.getLocationName());
                mFragmentEditPropertyBinding.textInputEditTextEditPropertyBeds.setText(property.getNumBed()==null?null:property.getNumBed()+"");
                mFragmentEditPropertyBinding.textInputEditTextEditPropertyBaths.setText(property.getNumBath()==null?null:property.getNumBath()+"");
                mFragmentEditPropertyBinding.textInputEditTextEditPropertyPrice.setText(property.getPrice()==null?null:property.getPrice()+"");
                mFragmentEditPropertyBinding.textInputEditTextEditPropertyDescription.setText(property.getDescription());
            }
        });

        mFragmentEditPropertyBinding.autoCompleteTextViewEditPropertyCategory.setInputType(InputType.TYPE_NULL);
        mFragmentEditPropertyBinding.autoCompleteTextViewEditPropertyCategory.setOnClickListener(v->{
            mFragmentEditPropertyBinding.autoCompleteTextViewEditPropertyCategory.setText("");
            mFragmentEditPropertyBinding.autoCompleteTextViewEditPropertyCategory.showDropDown();
        });
        mFragmentEditPropertyBinding.autoCompleteTextViewEditPropertyCategory.setOnItemClickListener((parent, view, position, id) -> {
            if (position>=0) {
                mEditPropertyViewModel.setSelectedPropertyCategory(mPropertyCategoryArrayAdapter.getItem(position));
            }
        });

        mFragmentEditPropertyBinding.autoCompleteTextViewEditPropertyCategory.setOnFocusChangeListener((v, isFocused)->{
            if (isFocused) {
                mFragmentEditPropertyBinding.autoCompleteTextViewEditPropertyCategory.performClick();
            }
        });

        mEditPropertyViewModel.getPropertyCategories().observe(getViewLifecycleOwner(), listAPIResponse -> {
            if (listAPIResponse!=null && listAPIResponse.isSuccessful()){
                mPropertyCategoryArrayAdapter = new ArrayAdapter<>(requireContext(),
                        android.R.layout.simple_dropdown_item_1line, android.R.id.text1, listAPIResponse.body());
                mFragmentEditPropertyBinding.autoCompleteTextViewEditPropertyCategory.setAdapter(mPropertyCategoryArrayAdapter);
            }
        });

        mFragmentEditPropertyBinding.buttonEditPropertySave.setOnClickListener(v->{
            String title = ViewUtils.getText(mFragmentEditPropertyBinding.textInputEditTextEditPropertyName);
            String category = ViewUtils.getText(mFragmentEditPropertyBinding.autoCompleteTextViewEditPropertyCategory);
            String location = ViewUtils.getText(mFragmentEditPropertyBinding.textInputEditTextEditPropertyLocation);
            String bed = ViewUtils.getText(mFragmentEditPropertyBinding.textInputEditTextEditPropertyBeds);
            String bath = ViewUtils.getText(mFragmentEditPropertyBinding.textInputEditTextEditPropertyBaths);
            String price = ViewUtils.getText(mFragmentEditPropertyBinding.textInputEditTextEditPropertyPrice);
            String description = ViewUtils.getText(mFragmentEditPropertyBinding.textInputEditTextEditPropertyDescription);
            Uri uri = mEditPropertyViewModel.getThumbnailUri();
            String url = null;
            PropertyCategory propertyCategory = mEditPropertyViewModel.getSelectedPropertyCategory();
            if (mProperty!=null)
                url = mProperty.getFeatureImage();

            if (ViewUtils.isEmptyAndRequired(mFragmentEditPropertyBinding.textInputEditTextEditPropertyName)){
                return;
            }
            if (ViewUtils.isEmptyAndRequired(mFragmentEditPropertyBinding.autoCompleteTextViewEditPropertyCategory) || propertyCategory==null){
                return;
            }
            if (ViewUtils.isEmptyAndRequired(mFragmentEditPropertyBinding.textInputEditTextEditPropertyLocation)){
                return;
            }
            if (ViewUtils.isEmptyAndRequired(mFragmentEditPropertyBinding.textInputEditTextEditPropertyBeds)){
                return;
            }
            if (ViewUtils.isEmptyAndRequired(mFragmentEditPropertyBinding.textInputEditTextEditPropertyBaths)){
                return;
            }
            if (ViewUtils.isEmptyAndRequired(mFragmentEditPropertyBinding.textInputEditTextEditPropertyPrice)){
                return;
            }
            if (ViewUtils.isEmptyAndRequired(mFragmentEditPropertyBinding.textInputEditTextEditPropertyDescription)){
                return;
            }
            if (uri==null && url==null){
                Toast.makeText(requireContext(), "Please select an image", Toast.LENGTH_SHORT).show();
                pickOrTakeImage();
                return;
            }

            String locationName = mEditPropertyViewModel.getLocationName();
            float lat = 0, lng = 0;
            if (mEditPropertyViewModel.getCoordinates()!=null){
                lat = (float) mEditPropertyViewModel.getCoordinates().latitude;
                lng = (float) mEditPropertyViewModel.getCoordinates().longitude;
            }
            else if (mProperty!=null){
                locationName = mProperty.getLocationName();
                lat = mProperty.getLocation().get(0);
                lng = mProperty.getLocation().get(1);
            }

            List<View> enabledViews = Arrays.asList(mFragmentEditPropertyBinding.imageViewEditPropertyFeatureImage,
                    mFragmentEditPropertyBinding.textInputEditTextEditPropertyName, mFragmentEditPropertyBinding.autoCompleteTextViewEditPropertyCategory,
                    mFragmentEditPropertyBinding.textInputEditTextEditPropertyLocation, mFragmentEditPropertyBinding.textInputEditTextEditPropertyBeds,
                    mFragmentEditPropertyBinding.textInputEditTextEditPropertyBaths, mFragmentEditPropertyBinding.textInputEditTextEditPropertyPrice,
                    mFragmentEditPropertyBinding.textInputEditTextEditPropertyDescription, mFragmentEditPropertyBinding.buttonEditPropertySave);
            ViewUtils.load(mFragmentEditPropertyBinding.linearLayoutEditPropertyLoading, enabledViews, true);
            mSinglePropertyViewModel.saveProperty(mProperty==null?null:mProperty.getId(),
                    mEditPropertyViewModel.getThumbnailUri(), propertyCategory.getId(),
                    title, description, Integer.parseInt(bed), Integer.parseInt(bath), locationName, Float.parseFloat(price),
                    lat, lng, mProperty == null || mProperty.getIsEnabled(),
                    mProperty==null || mProperty.getIsVerified()).observe(getViewLifecycleOwner(), propertyAPIResponse -> {
                ViewUtils.load(mFragmentEditPropertyBinding.linearLayoutEditPropertyLoading, enabledViews, false);
                if (propertyAPIResponse!=null && propertyAPIResponse.isSuccessful()){
                    Toast.makeText(requireContext(), R.string.category_created_successfully, Toast.LENGTH_SHORT).show();
                    if (mOnSaveListener!=null)
                        mOnSaveListener.onSave(propertyAPIResponse.body());
                }
                else {
                    Toast.makeText(requireContext(),
                            (propertyAPIResponse==null || propertyAPIResponse.errorMessage(requireContext())==null)?
                                    getString(R.string.unknown_error):
                                    propertyAPIResponse.errorMessage(requireContext()),
                            Toast.LENGTH_SHORT).show();
                }
            });

        });

        mFragmentEditPropertyBinding.imageViewEditPropertyFeatureImage.setOnClickListener(v->{
            pickOrTakeImage();
        });

        NavBackStackEntry currentBackStackEntry = getNavController().getCurrentBackStackEntry();
        if (currentBackStackEntry!=null) {
            MutableLiveData<Object> locationLiveData = currentBackStackEntry.getSavedStateHandle().getLiveData("location");
            locationLiveData.observe(getViewLifecycleOwner(), result->{
                if (result instanceof PickLocationFragment.LocationWithLatLng){
                    PickLocationFragment.LocationWithLatLng locationWithLatLng = (PickLocationFragment.LocationWithLatLng) result;
                    String locationName = locationWithLatLng.getLocation();
                    mEditPropertyViewModel.setLocation(locationName, locationWithLatLng.getLatLng());
                    mFragmentEditPropertyBinding.textInputEditTextEditPropertyLocation.setText(locationName);
                }
            });
        }

        mFragmentEditPropertyBinding.textInputEditTextEditPropertyLocation.setInputType(InputType.TYPE_NULL);
        mFragmentEditPropertyBinding.textInputEditTextEditPropertyLocation.setOnClickListener(v->{
            navigate(SinglePropertyFragmentDirections.actionSinglePropertyFragmentToPickLocationFragment(mProperty==null?null:mProperty.getLocationName(),
                    mProperty==null?null: new float[]{mProperty.getLocation().get(0), mProperty.getLocation().get(1)}));
        });
        mFragmentEditPropertyBinding.textInputEditTextEditPropertyLocation.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus){
                mFragmentEditPropertyBinding.textInputEditTextEditPropertyLocation.performClick();
            }
        });

        return mFragmentEditPropertyBinding.getRoot();
    }

    private void pickOrTakeImage(){
        Function1<ImageResult<? extends Uri>, Unit> imageCallback = imageResult -> {
            if (imageResult instanceof ImageResult.Success) {
                Uri uri = ((ImageResult.Success<Uri>) imageResult).getValue();
                Glide.with(requireContext())
                        .load(uri)
                        .placeholder(R.drawable.ic_home_black_24dp)
                        .error(R.drawable.ic_home_black_24dp)
                        .into(mFragmentEditPropertyBinding.imageViewEditPropertyFeatureImage);
                mFragmentEditPropertyBinding.textViewEditPropertyNoThumbnail.setVisibility(View.GONE);
                mEditPropertyViewModel.setThumbnailUri(uri);
            } else {
                String errorString = ((ImageResult.Failure) imageResult).getErrorString();
                Toast.makeText(requireContext(), errorString, Toast.LENGTH_LONG).show();
            }
            return null;
        };


        new AlertDialog.Builder(requireContext())
                .setItems(new String[]{"Gallery", "Camera"}, (dialog, which) -> {
                    if (which==0){
                        mImagePicker.pickFromStorage(imageCallback);
                    }
                    else {
                        mImagePicker.takeFromCamera(imageCallback);
                    }
                }).setTitle("Image Source")
                .create()
                .show();

    }


}