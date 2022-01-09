package ke.co.tonyoa.mahao.ui.profile.amenities.single;

import static ke.co.tonyoa.mahao.ui.profile.amenities.single.SingleAmenityFragment.AMENITY_EXTRA;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;

import ke.co.tonyoa.mahao.R;
import ke.co.tonyoa.mahao.app.api.responses.Amenity;
import ke.co.tonyoa.mahao.databinding.FragmentViewAmenityBinding;

public class ViewAmenityFragment extends Fragment {

    private Amenity mAmenity;
    private FragmentViewAmenityBinding mFragmentViewAmenityBinding;

    public ViewAmenityFragment() {
        // Required empty public constructor
    }

    public static ViewAmenityFragment newInstance(Amenity amenity) {
        ViewAmenityFragment fragment = new ViewAmenityFragment();
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mFragmentViewAmenityBinding = FragmentViewAmenityBinding.inflate(inflater, container, false);

        if (mAmenity != null) {
            Glide.with(requireContext())
                    .load(mAmenity.getIcon())
                    .placeholder(R.drawable.ic_home_black_24dp)
                    .error(R.drawable.ic_home_black_24dp)
                    .into(mFragmentViewAmenityBinding.imageViewViewAmenityIcon);
            mFragmentViewAmenityBinding.textViewViewAmenityAmenity.setText(mAmenity.getTitle());
        }

        return mFragmentViewAmenityBinding.getRoot();
    }
}