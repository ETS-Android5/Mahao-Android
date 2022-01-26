package ke.co.tonyoa.mahao.ui.properties;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.io.Serializable;
import java.util.Objects;

import ke.co.tonyoa.mahao.app.MahaoApplication;

public class PropertiesViewModel extends AndroidViewModel {

    private final MutableLiveData<Integer> mSelectedPosition=new MutableLiveData<>();
    private final MutableLiveData<FilterAndSort> mFilterAndSort=new MutableLiveData<>();

    public PropertiesViewModel(@NonNull Application application) {
        super(application);
        MahaoApplication.getInstance(application).getApplicationComponent().inject(this);
        setSort(new SortBy(SortColumn.TIME,  SortOrder.DESC));
        mSelectedPosition.setValue(1);
    }

    public void setSelectedPosition(int position){
        mSelectedPosition.postValue(position);
    }

    public LiveData<Integer> getSelectedPosition(){
        return mSelectedPosition;
    }

    public void setFilter(FilterPropertiesFragment.PropertyFilter propertyFilter){
        FilterAndSort filterAndSort = mFilterAndSort.getValue();
        if (filterAndSort == null)
            filterAndSort = new FilterAndSort(propertyFilter, null);
        else {
            filterAndSort.setPropertyFilter(propertyFilter);
        }
        mFilterAndSort.postValue(filterAndSort);
    }

    public void setQuery(String query){
        if (query == null || query.trim().isEmpty())
            query = null;
        FilterAndSort filterAndSort = mFilterAndSort.getValue();
        FilterPropertiesFragment.PropertyFilter propertyFilter = null;
        if (filterAndSort == null) {
            filterAndSort = new FilterAndSort(null, null);
        }
        else {
            propertyFilter = filterAndSort.getPropertyFilter();
        }
        if (propertyFilter == null){
            propertyFilter = new FilterPropertiesFragment.PropertyFilter();
        }
        String currentQuery = propertyFilter.getQuery();
        if (Objects.equals(currentQuery, query))
            return;
        propertyFilter.setQuery(query);
        filterAndSort.setPropertyFilter(propertyFilter);
        mFilterAndSort.postValue(filterAndSort);
    }

    public void setSort(SortBy sortBy){
        FilterAndSort filterAndSort = mFilterAndSort.getValue();
        if (filterAndSort == null)
            filterAndSort = new FilterAndSort(null, sortBy);
        else {
            SortBy currentSort = filterAndSort.getSortBy();
            // Not changed
            if (currentSort.getSortColumn() == sortBy.getSortColumn() &&
                    currentSort.getSortOrder() == sortBy.getSortOrder()){
                return;
            }
            filterAndSort.setSortBy(sortBy);
        }
        mFilterAndSort.postValue(filterAndSort);
    }

    public LiveData<FilterAndSort> getFilterAndSort() {
        return mFilterAndSort;
    }

    public static class FilterAndSort {
        private FilterPropertiesFragment.PropertyFilter mPropertyFilter;
        private SortBy mSortBy;

        public FilterAndSort(FilterPropertiesFragment.PropertyFilter propertyFilter, SortBy sortBy) {
            mPropertyFilter = propertyFilter;
            mSortBy = sortBy;
        }

        public FilterPropertiesFragment.PropertyFilter getPropertyFilter() {
            return mPropertyFilter;
        }

        public void setPropertyFilter(FilterPropertiesFragment.PropertyFilter propertyFilter) {
            mPropertyFilter = propertyFilter;
        }

        public SortBy getSortBy() {
            return mSortBy;
        }

        public void setSortBy(SortBy sortBy) {
            mSortBy = sortBy;
        }
    }

    public static class SortBy implements Serializable {
        private SortColumn mSortColumn;
        private SortOrder mSortOrder;

        public SortBy(SortColumn sortColumn, SortOrder sortOrder) {
            mSortColumn = sortColumn;
            mSortOrder = sortOrder;
        }

        public SortColumn getSortColumn() {
            return mSortColumn;
        }

        public void setSortColumn(SortColumn sortColumn) {
            mSortColumn = sortColumn;
        }

        public SortOrder getSortOrder() {
            return mSortOrder;
        }

        public void setSortOrder(SortOrder sortOrder) {
            mSortOrder = sortOrder;
        }
    }

    enum SortColumn{
        TIME,
        PRICE,
        DISTANCE
    }

    enum SortOrder{
        ASC,
        DESC
    }
}
