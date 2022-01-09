package ke.co.tonyoa.mahao.ui.profile.categories.single;

import static ke.co.tonyoa.mahao.ui.profile.categories.single.SingleCategoryFragment.PROPERTY_CATEGORY_EXTRA;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;

import ke.co.tonyoa.mahao.R;
import ke.co.tonyoa.mahao.app.api.responses.PropertyCategory;
import ke.co.tonyoa.mahao.databinding.FragmentViewCategoryBinding;

public class ViewCategoryFragment extends Fragment {

    private PropertyCategory mPropertyCategory;
    private FragmentViewCategoryBinding mFragmentViewCategoryBinding;

    public ViewCategoryFragment() {
        // Required empty public constructor
    }

    public static ViewCategoryFragment newInstance(PropertyCategory propertyCategory) {
        ViewCategoryFragment fragment = new ViewCategoryFragment();
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

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mFragmentViewCategoryBinding = FragmentViewCategoryBinding.inflate(inflater, container, false);

        if (mPropertyCategory != null) {
            Glide.with(requireContext())
                    .load(mPropertyCategory.getIcon())
                    .placeholder(R.drawable.ic_home_black_24dp)
                    .error(R.drawable.ic_home_black_24dp)
                    .into(mFragmentViewCategoryBinding.imageViewViewCategoryIcon);
            mFragmentViewCategoryBinding.textViewViewCategoryName.setText(mPropertyCategory.getTitle());
        }

        return mFragmentViewCategoryBinding.getRoot();
    }
}