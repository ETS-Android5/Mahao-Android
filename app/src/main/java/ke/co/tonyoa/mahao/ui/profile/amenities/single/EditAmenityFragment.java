package ke.co.tonyoa.mahao.ui.profile.amenities.single;

import static ke.co.tonyoa.mahao.ui.profile.amenities.single.SingleAmenityFragment.AMENITY_EXTRA;

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
import ke.co.tonyoa.mahao.app.api.responses.Amenity;
import ke.co.tonyoa.mahao.app.interfaces.OnSaveListener;
import ke.co.tonyoa.mahao.app.utils.ViewUtils;
import ke.co.tonyoa.mahao.databinding.FragmentEditAmenityBinding;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;

public class EditAmenityFragment extends Fragment {


    private Amenity mAmenity;
    private FragmentEditAmenityBinding mFragmentEditAmenityBinding;
    private EditAmenityViewModel mEditAmenityViewModel;
    private SingleAmenityViewModel mSingleAmenityViewModel;
    private OnSaveListener<Amenity> mOnSaveListener;
    private ImagePicker mImagePicker;

    public EditAmenityFragment() {
        // Required empty public constructor
    }

    public static EditAmenityFragment newInstance(Amenity amenity) {
        EditAmenityFragment fragment = new EditAmenityFragment();
        Bundle args = new Bundle();
        args.putSerializable(AMENITY_EXTRA, amenity);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mAmenity = (Amenity) getArguments().getSerializable(AMENITY_EXTRA);
        }
        mEditAmenityViewModel = new ViewModelProvider(this).get(EditAmenityViewModel.class);
        mSingleAmenityViewModel = new ViewModelProvider(requireParentFragment()).get(SingleAmenityViewModel.class);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (requireParentFragment() instanceof OnSaveListener){
            mOnSaveListener = (OnSaveListener<Amenity>)requireParentFragment();
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mFragmentEditAmenityBinding = FragmentEditAmenityBinding.inflate(inflater, container, false);
        mImagePicker = new ImagePicker(this);

        if (mAmenity != null) {
            Glide.with(requireContext())
                    .load(mAmenity.getIcon())
                    .placeholder(R.drawable.ic_home_black_24dp)
                    .error(R.drawable.ic_home_black_24dp)
                    .into(mFragmentEditAmenityBinding.imageViewEditAmenityAmenity);
            mFragmentEditAmenityBinding.textInputEditTextEditAmenityAmenity.setText(mAmenity.getTitle());
        }

        mFragmentEditAmenityBinding.buttonEditAmenitySave.setOnClickListener(v->{
            String title = ViewUtils.getText(mFragmentEditAmenityBinding.textInputEditTextEditAmenityAmenity);
            if (ViewUtils.isEmptyAndRequired(mFragmentEditAmenityBinding.textInputEditTextEditAmenityAmenity)){
                return;
            }

            List<View> enabledViews = Arrays.asList(mFragmentEditAmenityBinding.imageViewEditAmenityAmenity,
                    mFragmentEditAmenityBinding.textInputEditTextEditAmenityAmenity, mFragmentEditAmenityBinding.buttonEditAmenitySave);
            ViewUtils.load(mFragmentEditAmenityBinding.linearLayoutEditAmenityLoading, enabledViews, true);
            mSingleAmenityViewModel.saveAmenity(mAmenity==null?null:mAmenity.getId(),
                    title, "Some Description", mEditAmenityViewModel.getThumbnailUri().getValue()).observe(getViewLifecycleOwner(), loginResponseAPIResponse -> {
                ViewUtils.load(mFragmentEditAmenityBinding.linearLayoutEditAmenityLoading, enabledViews, false);
                if (loginResponseAPIResponse!=null && loginResponseAPIResponse.isSuccessful()){
                    Toast.makeText(requireContext(), R.string.amenity_created_successfully, Toast.LENGTH_SHORT).show();
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

        mFragmentEditAmenityBinding.imageViewEditAmenityAmenity.setOnClickListener(v->{
            pickOrTakeImage();
        });

        return mFragmentEditAmenityBinding.getRoot();
    }

    private void pickOrTakeImage(){
        Function1<ImageResult<? extends Uri>, Unit> imageCallback = imageResult -> {
            if (imageResult instanceof ImageResult.Success) {
                Uri uri = ((ImageResult.Success<Uri>) imageResult).getValue();
                Glide.with(requireContext())
                        .load(uri)
                        .placeholder(R.drawable.ic_home_black_24dp)
                        .error(R.drawable.ic_home_black_24dp)
                        .into(mFragmentEditAmenityBinding.imageViewEditAmenityAmenity);
                mFragmentEditAmenityBinding.textViewEdictAmenityNoThumbnail.setVisibility(View.GONE);
                mEditAmenityViewModel.setThumbnailUri(uri);
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