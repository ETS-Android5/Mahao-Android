package ke.co.tonyoa.mahao.ui.properties.single;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Arrays;
import java.util.List;

import ke.co.tonyoa.mahao.app.api.responses.PropertyPhoto;
import ke.co.tonyoa.mahao.app.navigation.BaseFragment;
import ke.co.tonyoa.mahao.databinding.FragmentFullPhotoBinding;

public class FullPhotoFragment extends BaseFragment {

    private FragmentFullPhotoBinding mFragmentFullPhotoBinding;
    private List<String> mPropertyPhotos;
    private String mSelected;

    public FullPhotoFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            FullPhotoFragmentArgs fullPhotoFragmentArgs = FullPhotoFragmentArgs.fromBundle(getArguments());
            String[] photos = fullPhotoFragmentArgs.getPhotos();
            mPropertyPhotos = Arrays.asList(photos);
            mSelected = fullPhotoFragmentArgs.getSelected();
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mFragmentFullPhotoBinding = FragmentFullPhotoBinding.inflate(inflater, container, false);
        FullPhotoAdapter fullPhotoAdapter = new FullPhotoAdapter(requireContext());
        fullPhotoAdapter.submitList(mPropertyPhotos);
        mFragmentFullPhotoBinding.recyclerViewFullPhoto.setLayoutManager(new LinearLayoutManager(requireContext(),
                RecyclerView.HORIZONTAL, false));
        mFragmentFullPhotoBinding.recyclerViewFullPhoto.setAdapter(fullPhotoAdapter);
        new PagerSnapHelper().attachToRecyclerView(mFragmentFullPhotoBinding.recyclerViewFullPhoto);
        mFragmentFullPhotoBinding.recyclerViewFullPhoto.scrollToPosition(mPropertyPhotos.indexOf(mSelected));
        mFragmentFullPhotoBinding.imageButtonFullPhotoCancel.setOnClickListener(v->{
            navigateBack();
        });

        return mFragmentFullPhotoBinding.getRoot();
    }
}