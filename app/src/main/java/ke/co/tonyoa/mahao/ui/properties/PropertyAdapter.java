package ke.co.tonyoa.mahao.ui.properties;

import android.content.Context;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import ke.co.tonyoa.mahao.R;
import ke.co.tonyoa.mahao.app.api.responses.Property;
import ke.co.tonyoa.mahao.app.interfaces.Bindable;
import ke.co.tonyoa.mahao.app.interfaces.ListItemable;
import ke.co.tonyoa.mahao.app.interfaces.OnItemClickListener;
import ke.co.tonyoa.mahao.app.utils.ViewUtils;
import ke.co.tonyoa.mahao.databinding.ItemHorizontalPropertyBinding;
import ke.co.tonyoa.mahao.databinding.ItemVerticalPropertyBinding;

public class PropertyAdapter extends ListAdapter<Property, PropertyAdapter.PropertyViewHolder> implements ListItemable<Property> {

    public enum ListType{
        HORIZONTAL_PROPERTY,
        VERTICAL_PROPERTY
    }

    private final ListType mListType;
    private final Context mContext;
    private final OnItemClickListener<Property> mOnPropertyClickListener;
    private final OnItemClickListener<Property> mOnPropertyLikeListener;
    private final OnItemClickListener<Property> mOnPropertyReadListener;

    public PropertyAdapter(ListType listType, Context context,
                           OnItemClickListener<Property> onPropertyClickListener,
                           OnItemClickListener<Property> onPropertyLikeListener,
                           OnItemClickListener<Property> onPropertyReadListener) {
        super(new DiffUtil.ItemCallback<Property>() {
            @Override
            public boolean areItemsTheSame(@NonNull Property oldItem, @NonNull Property newItem) {
                return Objects.equals(oldItem.getId(), newItem.getId());
            }

            @Override
            public boolean areContentsTheSame(@NonNull Property oldItem, @NonNull Property newItem) {
                return oldItem.equals(newItem);
            }
        });
        mListType = listType;
        mContext = context;
        mOnPropertyClickListener = onPropertyClickListener;
        mOnPropertyLikeListener = onPropertyLikeListener;
        mOnPropertyReadListener = onPropertyReadListener;
    }


    @NonNull
    @Override
    public PropertyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        if (mListType == ListType.HORIZONTAL_PROPERTY){
            return new HorizontalPropertyViewHolder(ItemHorizontalPropertyBinding.inflate(inflater, parent, false),
                    mOnPropertyClickListener, mOnPropertyLikeListener, mContext, this);
        }
        else {
            return new VerticalPropertyViewHolder(ItemVerticalPropertyBinding.inflate(inflater, parent, false),
                    mOnPropertyClickListener, mOnPropertyLikeListener, mContext, this);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull PropertyViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public void onBindViewHolder(@NonNull PropertyViewHolder holder, int position, @NonNull List<Object> payloads) {
        super.onBindViewHolder(holder, position, payloads);
        if (payloads.size()>0){
            holder.like(position);
        }
        else {
            onBindViewHolder(holder, position);
        }
    }

    @Override
    public Property getListItem(int position) {
        return getItem(position);
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        RecyclerView.LayoutManager manager = recyclerView.getLayoutManager();
        if(manager instanceof LinearLayoutManager && getItemCount() > 0) {
            LinearLayoutManager llm = (LinearLayoutManager) manager;
            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                }

                @Override
                public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    int visiblePosition = llm.findFirstCompletelyVisibleItemPosition();
                    if(visiblePosition > -1) {
                        if (mOnPropertyReadListener!=null){
                            mOnPropertyReadListener.onItemClick(getItem(visiblePosition), visiblePosition);
                        }
                    }
                }
            });
        }
    }


    public abstract static class PropertyViewHolder extends RecyclerView.ViewHolder implements Bindable{
        protected final OnItemClickListener<Property> mOnPropertyClickListener;
        protected final OnItemClickListener<Property> mOnPropertyLikeListener;
        protected final Context mContext;
        protected final ListItemable<Property> mListItemable;

        public PropertyViewHolder(@NonNull View itemView, OnItemClickListener<Property> onPropertyClickListener,
                                  OnItemClickListener<Property> onPropertyLikeListener, Context context,
                                  ListItemable<Property> listItemable) {
            super(itemView);
            mOnPropertyClickListener = onPropertyClickListener;
            mOnPropertyLikeListener = onPropertyLikeListener;
            mContext = context;
            mListItemable = listItemable;
        }
    }

    public static class HorizontalPropertyViewHolder extends PropertyViewHolder{

        @NonNull
        private final ItemHorizontalPropertyBinding mItemHorizontalPropertyBinding;

        public HorizontalPropertyViewHolder(ItemHorizontalPropertyBinding itemHorizontalPropertyBinding,
                                            OnItemClickListener<Property> onPropertyClickListener,
                                            OnItemClickListener<Property> onPropertyLikeListener, Context context,
                                            ListItemable<Property> listItemable) {
            super(itemHorizontalPropertyBinding.getRoot(), onPropertyClickListener, onPropertyLikeListener,
                    context, listItemable);
            mItemHorizontalPropertyBinding = itemHorizontalPropertyBinding;
            mItemHorizontalPropertyBinding.getRoot().setOnClickListener(v->{
                if (mOnPropertyClickListener!=null){
                    int position = getAdapterPosition();
                    Property property = listItemable.getListItem(position);
                    mOnPropertyClickListener.onItemClick(property, position);
                }
            });
            mItemHorizontalPropertyBinding.animationViewItemHorizontalPropertyLike.setOnClickListener(v->{
                if (mOnPropertyLikeListener!=null){
                    ViewUtils.load(mItemHorizontalPropertyBinding.animationViewItemHorizontalPropertyLoading,
                            Arrays.asList(mItemHorizontalPropertyBinding.animationViewItemHorizontalPropertyLike),
                            true);
                    mItemHorizontalPropertyBinding.animationViewItemHorizontalPropertyLoading.playAnimation();
                    
                    int position = getAdapterPosition();
                    Property property = listItemable.getListItem(position);
                    // Make the favorite status to one opposite of the previous status
                    property.setIsFavorite(!property.getIsFavorite());
                    displayFavorite(property, true);
                    mOnPropertyLikeListener.onItemClick(property, position);
                }
            });
        }

        @Override
        public void bind(int position) {
            Property property = mListItemable.getListItem(position);
            if (property == null)
                return;
            Glide.with(mContext)
                    .load(property.getFeatureImage())
                    .placeholder(R.drawable.ic_home_black_24dp)
                    .error(R.drawable.ic_home_black_24dp)
                    .into(mItemHorizontalPropertyBinding.imageViewItemHorizontalPropertyFeature);
            
            mItemHorizontalPropertyBinding.textViewItemHorizontalPropertyLocation.setText(property.getLocationName());
            displayFavorite(property, false);
            mItemHorizontalPropertyBinding.imageViewItemHorizontalPropertyVerified.setVisibility(property.getIsVerified()?
                    View.VISIBLE:View.GONE);
            mItemHorizontalPropertyBinding.textViewItemHorizontalPropertyTitle.setText(property.getTitle());
            mItemHorizontalPropertyBinding.textViewItemHorizontalPropertyPrice.setText(mContext.getString(R.string.kes_f_month, property.getPrice()));
            mItemHorizontalPropertyBinding.textViewItemHorizontalPropertyBeds.setText(mContext.getString(R.string.d_beds, property.getNumBed()));
            mItemHorizontalPropertyBinding.textViewItemHorizontalPropertyBaths.setText(mContext.getString(R.string.d_baths, property.getNumBath()));
        }

        @Override
        public void like(int position) {
            Property property = mListItemable.getListItem(position);
            boolean isCurrentFavorite = mItemHorizontalPropertyBinding.animationViewItemHorizontalPropertyLike.getProgress()==1;
            // Only change state if it is different
            if (property.getIsFavorite()!=isCurrentFavorite){
                displayFavorite(property, true);
            }
            ViewUtils.load(mItemHorizontalPropertyBinding.animationViewItemHorizontalPropertyLoading,
                    Arrays.asList(mItemHorizontalPropertyBinding.animationViewItemHorizontalPropertyLike),
                    false);
        }

        public void displayFavorite(Property property, boolean click){
            if (click) {
                if (property.getIsFavorite()) {
                    mItemHorizontalPropertyBinding.animationViewItemHorizontalPropertyLike.setProgress(0);
                    mItemHorizontalPropertyBinding.animationViewItemHorizontalPropertyLike.setSpeed(1);
                } else {
                    mItemHorizontalPropertyBinding.animationViewItemHorizontalPropertyLike.setProgress(1);
                    mItemHorizontalPropertyBinding.animationViewItemHorizontalPropertyLike.setSpeed(-1);
                }
                mItemHorizontalPropertyBinding.animationViewItemHorizontalPropertyLike.playAnimation();
            }
            else {
                mItemHorizontalPropertyBinding.animationViewItemHorizontalPropertyLike.setProgress(property.getIsFavorite()?1:0);
            }
        }
    }

    public static class VerticalPropertyViewHolder extends PropertyViewHolder{

        @NonNull
        private final ItemVerticalPropertyBinding mItemVerticalPropertyBinding;

        public VerticalPropertyViewHolder(ItemVerticalPropertyBinding itemVerticalPropertyBinding,
                                          OnItemClickListener<Property> onPropertyClickListener,
                                          OnItemClickListener<Property> onPropertyLikeListener, Context context,
                                          ListItemable<Property> listItemable) {
            super(itemVerticalPropertyBinding.getRoot(), onPropertyClickListener, onPropertyLikeListener,
                    context, listItemable);
            mItemVerticalPropertyBinding = itemVerticalPropertyBinding;
            mItemVerticalPropertyBinding.getRoot().setOnClickListener(v->{
                if (mOnPropertyClickListener!=null){
                    int position = getAdapterPosition();
                    Property property = listItemable.getListItem(position);
                    mOnPropertyClickListener.onItemClick(property, position);
                }
            });
            mItemVerticalPropertyBinding.animationViewItemVerticalPropertyLike.setOnClickListener(v->{
                if (mOnPropertyLikeListener!=null){
                    ViewUtils.load(mItemVerticalPropertyBinding.animationViewItemVerticalPropertyLoading,
                            Arrays.asList(mItemVerticalPropertyBinding.animationViewItemVerticalPropertyLike),
                            true);
                    mItemVerticalPropertyBinding.animationViewItemVerticalPropertyLike.playAnimation();

                    int position = getAdapterPosition();
                    Property property = listItemable.getListItem(position);
                    // Make the favorite status to one opposite of the previous status
                    property.setIsFavorite(!property.getIsFavorite());
                    displayFavorite(property, true);
                    mOnPropertyLikeListener.onItemClick(property, position);
                }
            });
        }

        @Override
        public void bind(int position) {
            Property property = mListItemable.getListItem(position);
            if (property == null)
                return;
            Glide.with(mContext)
                    .load(property.getFeatureImage())
                    .placeholder(R.drawable.ic_home_black_24dp)
                    .error(R.drawable.ic_home_black_24dp)
                    .into(mItemVerticalPropertyBinding.imageViewItemVerticalPropertyFeature);

            mItemVerticalPropertyBinding.textViewItemVerticalPropertyLocation.setText(property.getLocationName());
            displayFavorite(property, false);
            mItemVerticalPropertyBinding.imageViewItemVerticalPropertyVerified.setVisibility(property.getIsVerified()?
                    View.VISIBLE:View.GONE);
            mItemVerticalPropertyBinding.textViewItemVerticalPropertyTitle.setText(property.getTitle());
            mItemVerticalPropertyBinding.textViewItemVerticalPropertyPrice.setText(mContext.getString(R.string.kes_f_month, property.getPrice()));
            mItemVerticalPropertyBinding.textViewItemVerticalPropertyBeds.setText(mContext.getString(R.string.d_beds, property.getNumBed()));
            mItemVerticalPropertyBinding.textViewItemVerticalPropertyBaths.setText(mContext.getString(R.string.d_baths, property.getNumBath()));
            mItemVerticalPropertyBinding.textViewItemVerticalPropertyAmenities.setText(mContext.getString(R.string.d_amens, property.getPropertyAmenities().size()));
        }

        @Override
        public void like(int position) {
            Property property = mListItemable.getListItem(position);
            boolean isCurrentFavorite = mItemVerticalPropertyBinding.animationViewItemVerticalPropertyLike.getProgress()==1;
            // Only change state if it is different
            if (property.getIsFavorite()!=isCurrentFavorite){
                displayFavorite(property, true);
            }
            ViewUtils.load(mItemVerticalPropertyBinding.animationViewItemVerticalPropertyLoading,
                    Arrays.asList(mItemVerticalPropertyBinding.animationViewItemVerticalPropertyLike),
                    false);
        }

        public void displayFavorite(Property property, boolean click){
            if (click) {
                if (property.getIsFavorite()) {
                    mItemVerticalPropertyBinding.animationViewItemVerticalPropertyLike.setProgress(0);
                    mItemVerticalPropertyBinding.animationViewItemVerticalPropertyLike.setSpeed(1);
                } else {
                    mItemVerticalPropertyBinding.animationViewItemVerticalPropertyLike.setProgress(1);
                    mItemVerticalPropertyBinding.animationViewItemVerticalPropertyLike.setSpeed(-1);
                }
                mItemVerticalPropertyBinding.animationViewItemVerticalPropertyLike.playAnimation();
            }
            else {
                mItemVerticalPropertyBinding.animationViewItemVerticalPropertyLike.setProgress(property.getIsFavorite()?1:0);
            }
        }
    }
}
