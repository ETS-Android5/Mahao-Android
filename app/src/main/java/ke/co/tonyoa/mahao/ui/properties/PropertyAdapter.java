package ke.co.tonyoa.mahao.ui.properties;

import android.content.Context;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import ke.co.tonyoa.mahao.R;
import ke.co.tonyoa.mahao.app.api.responses.Property;
import ke.co.tonyoa.mahao.app.interfaces.Bindable;
import ke.co.tonyoa.mahao.app.interfaces.OnItemClickListener;
import ke.co.tonyoa.mahao.app.utils.ViewUtils;
import ke.co.tonyoa.mahao.databinding.ItemHorizontalPropertyBinding;
import ke.co.tonyoa.mahao.databinding.ItemVerticalPropertyBinding;

public class PropertyAdapter extends ListAdapter<Property, PropertyAdapter.PropertyViewHolder> {

    public enum ListType{
        HORIZONTAL_PROPERTY,
        VERTICAL_PROPERTY
    }

    private final ListType mListType;
    private int mRecyclerViewWidth;
    private final Context mContext;
    private final OnItemClickListener<Property> mOnPropertyClickListener;
    private final OnItemClickListener<Property> mOnPropertyLikeListener;

    public PropertyAdapter(ListType listType, int recyclerViewWidth, Context context,
                           OnItemClickListener<Property> onPropertyClickListener,
                              OnItemClickListener<Property> onPropertyLikeListener) {
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
        mRecyclerViewWidth = recyclerViewWidth;
        mContext = context;
        mOnPropertyClickListener = onPropertyClickListener;
        mOnPropertyLikeListener = onPropertyLikeListener;
    }


    @NonNull
    @Override
    public PropertyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        if (mListType == ListType.HORIZONTAL_PROPERTY){
            //Adjust to partially show next and previous items
            ItemHorizontalPropertyBinding itemHorizontalPropertyBinding = ItemHorizontalPropertyBinding.inflate(inflater, parent, false);
            int width = mRecyclerViewWidth;
            ViewGroup.LayoutParams params = itemHorizontalPropertyBinding.getRoot().getLayoutParams();
            //params.width = (int)(width * 0.8);
            //itemHorizontalPropertyBinding.getRoot().setLayoutParams(params);
            return new HorizontalPropertyViewHolder(itemHorizontalPropertyBinding);
        }
        else {
            return new VerticalPropertyViewHolder(ItemVerticalPropertyBinding.inflate(inflater, parent, false));
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

    abstract static class PropertyViewHolder extends RecyclerView.ViewHolder implements Bindable{
        public PropertyViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    class HorizontalPropertyViewHolder extends PropertyViewHolder{

        @NonNull
        private final ItemHorizontalPropertyBinding mItemHorizontalPropertyBinding;

        public HorizontalPropertyViewHolder(ItemHorizontalPropertyBinding itemHorizontalPropertyBinding) {
            super(itemHorizontalPropertyBinding.getRoot());
            mItemHorizontalPropertyBinding = itemHorizontalPropertyBinding;
            mItemHorizontalPropertyBinding.getRoot().setOnClickListener(v->{
                if (mOnPropertyClickListener!=null){
                    int position = getAdapterPosition();
                    Property property = getItem(position);
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
                    Property property = getItem(position);
                    // Make the favorite status to one opposite of the previous status
                    property.setIsFavorite(!property.getIsFavorite());
                    displayFavorite(property, true);
                    mOnPropertyLikeListener.onItemClick(property, position);
                }
            });
        }

        @Override
        public void bind(int position) {
            Property property = getItem(position);
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
            Property property = getItem(position);
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

    class VerticalPropertyViewHolder extends PropertyViewHolder{

        @NonNull
        private final ItemVerticalPropertyBinding mItemVerticalPropertyBinding;

        public VerticalPropertyViewHolder(ItemVerticalPropertyBinding itemVerticalPropertyBinding) {
            super(itemVerticalPropertyBinding.getRoot());
            mItemVerticalPropertyBinding = itemVerticalPropertyBinding;
            mItemVerticalPropertyBinding.getRoot().setOnClickListener(v->{
                if (mOnPropertyClickListener!=null){
                    int position = getAdapterPosition();
                    Property property = getItem(position);
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
                    Property property = getItem(position);
                    // Make the favorite status to one opposite of the previous status
                    property.setIsFavorite(!property.getIsFavorite());
                    displayFavorite(property, true);
                    mOnPropertyLikeListener.onItemClick(property, position);
                }
            });
        }

        @Override
        public void bind(int position) {
            Property property = getItem(position);
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
            Property property = getItem(position);
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
