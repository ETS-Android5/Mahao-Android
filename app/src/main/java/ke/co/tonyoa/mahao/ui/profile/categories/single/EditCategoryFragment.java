package ke.co.tonyoa.mahao.ui.profile.categories.single;

import static ke.co.tonyoa.mahao.ui.profile.categories.single.SingleCategoryFragment.PROPERTY_CATEGORY_EXTRA;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.Arrays;
import java.util.List;

import dev.ronnie.github.imagepicker.ImagePicker;
import dev.ronnie.github.imagepicker.ImageResult;
import ke.co.tonyoa.mahao.R;
import ke.co.tonyoa.mahao.app.api.responses.PropertyCategory;
import ke.co.tonyoa.mahao.app.interfaces.OnSaveListener;
import ke.co.tonyoa.mahao.app.utils.ViewUtils;
import ke.co.tonyoa.mahao.databinding.FragmentEditCategoryBinding;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;

public class EditCategoryFragment extends Fragment {

    private PropertyCategory mPropertyCategory;
    private FragmentEditCategoryBinding mFragmentEditCategoryBinding;
    private EditCategoryViewModel mEditCategoryViewModel;
    private SingleCategoryViewModel mSingleCategoryViewModel;
    private OnSaveListener<PropertyCategory> mOnSaveListener;
    private ImagePicker mImagePicker;

    public EditCategoryFragment() {
        // Required empty public constructor
    }

    public static EditCategoryFragment newInstance(PropertyCategory propertyCategory) {
        EditCategoryFragment fragment = new EditCategoryFragment();
        Bundle args = new Bundle();
        args.putSerializable(PROPERTY_CATEGORY_EXTRA, propertyCategory);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mPropertyCategory = (PropertyCategory) getArguments().getSerializable(PROPERTY_CATEGORY_EXTRA);
        }
        mEditCategoryViewModel = new ViewModelProvider(this).get(EditCategoryViewModel.class);
        mSingleCategoryViewModel = new ViewModelProvider(requireParentFragment()).get(SingleCategoryViewModel.class);
    }


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (requireParentFragment() instanceof OnSaveListener){
            mOnSaveListener = (OnSaveListener<PropertyCategory>)requireParentFragment();
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mFragmentEditCategoryBinding = FragmentEditCategoryBinding.inflate(inflater, container, false);
        mImagePicker = new ImagePicker(this);

        if (mPropertyCategory != null && mEditCategoryViewModel.isInitialLoad()) {
            Glide.with(requireContext())
                    .load(mPropertyCategory.getIcon())
                    .placeholder(R.drawable.ic_home_black_24dp)
                    .error(R.drawable.ic_home_black_24dp)
                    .into(mFragmentEditCategoryBinding.imageViewEditCategoryCategory);
            mFragmentEditCategoryBinding.textInputEditTextEditCategoryCategory.setText(mPropertyCategory.getTitle());
            mEditCategoryViewModel.setInitialLoad(false);
        }
        else if (mEditCategoryViewModel.getThumbnailUri() != null){
            Glide.with(requireContext())
                    .load(mEditCategoryViewModel.getThumbnailUri())
                    .placeholder(R.drawable.ic_home_black_24dp)
                    .error(R.drawable.ic_home_black_24dp)
                    .into(mFragmentEditCategoryBinding.imageViewEditCategoryCategory);
        }

        mFragmentEditCategoryBinding.buttonEditCategorySave.setOnClickListener(v->{
            String title = ViewUtils.getText(mFragmentEditCategoryBinding.textInputEditTextEditCategoryCategory);
            if (ViewUtils.isEmptyAndRequired(mFragmentEditCategoryBinding.textInputEditTextEditCategoryCategory)){
                return;
            }

            List<View> enabledViews = Arrays.asList(mFragmentEditCategoryBinding.imageViewEditCategoryCategory,
                    mFragmentEditCategoryBinding.textInputEditTextEditCategoryCategory, mFragmentEditCategoryBinding.buttonEditCategorySave);
            ViewUtils.load(mFragmentEditCategoryBinding.linearLayoutEditCategoryLoading, enabledViews, true);
            mSingleCategoryViewModel.savePropertyCategory(mPropertyCategory==null?null:mPropertyCategory.getId(),
                    title, "Some Description", mEditCategoryViewModel.getThumbnailUri()).observe(getViewLifecycleOwner(), loginResponseAPIResponse -> {
                ViewUtils.load(mFragmentEditCategoryBinding.linearLayoutEditCategoryLoading, enabledViews, false);
                if (loginResponseAPIResponse!=null && loginResponseAPIResponse.isSuccessful()){
                    Toast.makeText(requireContext(), R.string.category_created_successfully, Toast.LENGTH_SHORT).show();
                    if (mOnSaveListener!=null)
                        mOnSaveListener.onSave(loginResponseAPIResponse.body());
                }
                else {
                    Toast.makeText(requireContext(),
                            (loginResponseAPIResponse==null || loginResponseAPIResponse.errorMessage(requireContext())==null)?
                                    getString(R.string.unknown_error):
                                    loginResponseAPIResponse.errorMessage(requireContext()),
                            Toast.LENGTH_SHORT).show();
                }
            });

        });

        mFragmentEditCategoryBinding.imageViewEditCategoryCategory.setOnClickListener(v->{
            pickOrTakeImage();
        });

        return mFragmentEditCategoryBinding.getRoot();
    }

    private void pickOrTakeImage(){
        Function1<ImageResult<? extends Uri>, Unit> imageCallback = imageResult -> {
            if (imageResult instanceof ImageResult.Success) {
                Uri uri = ((ImageResult.Success<Uri>) imageResult).getValue();
                Glide.with(requireContext())
                        .load(uri)
                        .placeholder(R.drawable.ic_home_black_24dp)
                        .error(R.drawable.ic_home_black_24dp)
                        .into(mFragmentEditCategoryBinding.imageViewEditCategoryCategory);
                mFragmentEditCategoryBinding.textViewEdictCategoryNoThumbnail.setVisibility(View.GONE);
                mEditCategoryViewModel.setThumbnailUri(uri);
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