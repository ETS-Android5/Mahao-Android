package ke.co.tonyoa.mahao.ui.properties.single;

import static ke.co.tonyoa.mahao.ui.home.HomeViewModel.DEFAULT_COORDINATES;
import static ke.co.tonyoa.mahao.ui.properties.single.SinglePropertyFragment.PROPERTY_EXTRA;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
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
import ke.co.tonyoa.mahao.app.utils.ViewUtils;
import ke.co.tonyoa.mahao.databinding.FragmentEditPropertyBinding;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;

public class EditPropertyFragment extends Fragment {

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
        mEditPropertyViewModel = new ViewModelProvider(this).get(EditPropertyViewModel.class);
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

        if (mProperty != null) {
            Glide.with(requireContext())
                    .load(mProperty.getFeatureImage())
                    .placeholder(R.drawable.ic_home_black_24dp)
                    .error(R.drawable.ic_home_black_24dp)
                    .into(mFragmentEditPropertyBinding.imageViewEditPropertyFeatureImage);
            mFragmentEditPropertyBinding.textInputEditTextEditPropertyName.setText(mProperty.getTitle());
            mFragmentEditPropertyBinding.autoCompleteTextViewEditPropertyCategory.setText(mProperty.getPropertyCategory().getTitle());
            mEditPropertyViewModel.setSelectedPropertyCategory(mProperty.getPropertyCategory());
            mFragmentEditPropertyBinding.textInputEditTextEditPropertyLocation.setText(mProperty.getLocationName());
            mFragmentEditPropertyBinding.textInputEditTextEditPropertyBeds.setText(mProperty.getNumBed()+"");
            mFragmentEditPropertyBinding.textInputEditTextEditPropertyBaths.setText(mProperty.getNumBath()+"");
            mFragmentEditPropertyBinding.textInputEditTextEditPropertyPrice.setText(mProperty.getPrice()+"");
            mFragmentEditPropertyBinding.textInputEditTextEditPropertyDescription.setText(mProperty.getDescription());
        }

        mFragmentEditPropertyBinding.autoCompleteTextViewEditPropertyCategory.setInputType(InputType.TYPE_NULL);
        mFragmentEditPropertyBinding.autoCompleteTextViewEditPropertyCategory.setOnClickListener(v->{
            mFragmentEditPropertyBinding.autoCompleteTextViewEditPropertyCategory.setText("");
            mFragmentEditPropertyBinding.autoCompleteTextViewEditPropertyCategory.showDropDown();
        });
        mFragmentEditPropertyBinding.autoCompleteTextViewEditPropertyCategory.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position>=0) {
                    mEditPropertyViewModel.setSelectedPropertyCategory(mPropertyCategoryArrayAdapter.getItem(position));
                }
            }
        });

        mFragmentEditPropertyBinding.autoCompleteTextViewEditPropertyCategory.setOnDismissListener(new AutoCompleteTextView.OnDismissListener() {
            @Override
            public void onDismiss() {
                PropertyCategory propertyCategory = mEditPropertyViewModel.getSelectedPropertyCategory().getValue();
                if (propertyCategory!=null) {
                    mFragmentEditPropertyBinding.autoCompleteTextViewEditPropertyCategory.setText(propertyCategory.getTitle());
                }
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
            Uri uri = mEditPropertyViewModel.getThumbnailUri().getValue();
            String url = null;
            PropertyCategory propertyCategory = mEditPropertyViewModel.getSelectedPropertyCategory().getValue();
            if (mProperty!=null)
                url = mProperty.getFeatureImage();

            Log.e("Property Category", "Selected property category is null "+(propertyCategory==null));
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

            List<View> enabledViews = Arrays.asList(mFragmentEditPropertyBinding.imageViewEditPropertyFeatureImage,
                    mFragmentEditPropertyBinding.textInputEditTextEditPropertyName, mFragmentEditPropertyBinding.autoCompleteTextViewEditPropertyCategory,
                    mFragmentEditPropertyBinding.textInputEditTextEditPropertyLocation, mFragmentEditPropertyBinding.textInputEditTextEditPropertyBeds,
                    mFragmentEditPropertyBinding.textInputEditTextEditPropertyBaths, mFragmentEditPropertyBinding.textInputEditTextEditPropertyPrice,
                    mFragmentEditPropertyBinding.textInputEditTextEditPropertyDescription, mFragmentEditPropertyBinding.buttonEditPropertySave);
            ViewUtils.load(mFragmentEditPropertyBinding.linearLayoutEditPropertyLoading, enabledViews, true);
            mSinglePropertyViewModel.saveProperty(mProperty==null?null:mProperty.getId(),
                    mEditPropertyViewModel.getThumbnailUri().getValue(), propertyCategory.getId(),
                    title, description, Integer.parseInt(bed), Integer.parseInt(bath), location, Float.parseFloat(price),
                    (float) DEFAULT_COORDINATES.latitude, (float) DEFAULT_COORDINATES.longitude,
                    mProperty == null || mProperty.getIsEnabled(),
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