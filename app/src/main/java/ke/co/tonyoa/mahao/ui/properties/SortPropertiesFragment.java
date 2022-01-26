package ke.co.tonyoa.mahao.ui.properties;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavBackStackEntry;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import ke.co.tonyoa.mahao.R;
import ke.co.tonyoa.mahao.databinding.FragmentSortPropertiesBinding;

public class SortPropertiesFragment extends BottomSheetDialogFragment {

    private PropertiesViewModel.SortBy mSortBy;
    private FragmentSortPropertiesBinding mFragmentSortPropertiesBinding;
    private SortPropertiesViewModel mSortPropertiesViewModel;

    public SortPropertiesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mSortBy = SortPropertiesFragmentArgs.fromBundle(getArguments()).getSortBy();
        }
        SortPropertiesViewModelFactory sortPropertiesViewModelFactory = new SortPropertiesViewModelFactory(mSortBy);
        mSortPropertiesViewModel = new ViewModelProvider(this, sortPropertiesViewModelFactory).get(SortPropertiesViewModel.class);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mFragmentSortPropertiesBinding = FragmentSortPropertiesBinding.inflate(inflater, container, false);

        PropertiesViewModel.SortColumn sortColumn = mSortPropertiesViewModel.getSortColumn();
        if (sortColumn != null){
            RadioButton radioButton = null;
            switch (sortColumn){
                case DISTANCE:
                    radioButton = mFragmentSortPropertiesBinding.radioButtonSortDistance;
                    break;
                case PRICE:
                    radioButton = mFragmentSortPropertiesBinding.radioButtonSortPrice;
                    break;
                case TIME:
                    radioButton = mFragmentSortPropertiesBinding.radioButtonSortTime;
                    break;
            }
            if (radioButton != null){
                radioButton.setChecked(true);
            }
        }
        PropertiesViewModel.SortOrder sortOrder = mSortPropertiesViewModel.getSortOrder();
        if (sortOrder != null){
            RadioButton radioButton = null;
            switch (sortOrder){
                case ASC:
                    radioButton = mFragmentSortPropertiesBinding.radioButtonSortAscending;
                    break;
                case DESC:
                    radioButton = mFragmentSortPropertiesBinding.radioButtonSortDescending;
                    break;
            }
            if (radioButton != null) {
                radioButton.setChecked(true);
            }
        }

        mFragmentSortPropertiesBinding.buttonSortPropertiesCancel.setOnClickListener(v->{
            dismiss();
        });
        mFragmentSortPropertiesBinding.buttonSortPropertiesDone.setOnClickListener(v->{
            saveSortOrder();
            PropertiesViewModel.SortBy sortBy = new PropertiesViewModel.SortBy(mSortPropertiesViewModel.getSortColumn(),
                    mSortPropertiesViewModel.getSortOrder());
            NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_activity_main);
            NavBackStackEntry previousBackStackEntry = navController.getPreviousBackStackEntry();
            if (previousBackStackEntry != null) {
                previousBackStackEntry.getSavedStateHandle().set("order", sortBy);
            }
            dismiss();
        });

        return mFragmentSortPropertiesBinding.getRoot();
    }

    private void saveSortOrder(){
        PropertiesViewModel.SortColumn sortColumn = null;
        if (mFragmentSortPropertiesBinding.radioButtonSortDistance.isChecked()){
            sortColumn = PropertiesViewModel.SortColumn.DISTANCE;
        }
        else if (mFragmentSortPropertiesBinding.radioButtonSortPrice.isChecked()){
            sortColumn = PropertiesViewModel.SortColumn.PRICE;
        }
        else if (mFragmentSortPropertiesBinding.radioButtonSortTime.isChecked()){
            sortColumn = PropertiesViewModel.SortColumn.TIME;
        }
        mSortPropertiesViewModel.setSortColumn(sortColumn);

        PropertiesViewModel.SortOrder sortOrder = null;
        if (mFragmentSortPropertiesBinding.radioButtonSortAscending.isChecked()){
            sortOrder = PropertiesViewModel.SortOrder.ASC;
        }
        else if (mFragmentSortPropertiesBinding.radioButtonSortDescending.isChecked()){
            sortOrder = PropertiesViewModel.SortOrder.DESC;
        }
        mSortPropertiesViewModel.setSortOrder(sortOrder);
    }

    @Override
    public void onPause() {
        super.onPause();
        saveSortOrder();
    }
}