package ke.co.tonyoa.mahao.ui.properties;

import androidx.lifecycle.ViewModel;

public class SortPropertiesViewModel extends ViewModel {

    private PropertiesViewModel.SortColumn mSortColumn;
    private PropertiesViewModel.SortOrder mSortOrder;

    public SortPropertiesViewModel(PropertiesViewModel.SortBy sortBy){
        mSortColumn = sortBy.getSortColumn();
        mSortOrder = sortBy.getSortOrder();
    }

    public PropertiesViewModel.SortColumn getSortColumn() {
        return mSortColumn;
    }

    public void setSortColumn(PropertiesViewModel.SortColumn sortColumn) {
        mSortColumn = sortColumn;
    }

    public PropertiesViewModel.SortOrder getSortOrder() {
        return mSortOrder;
    }

    public void setSortOrder(PropertiesViewModel.SortOrder sortOrder) {
        mSortOrder = sortOrder;
    }
}
