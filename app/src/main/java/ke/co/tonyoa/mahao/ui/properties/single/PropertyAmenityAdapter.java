package ke.co.tonyoa.mahao.ui.properties.single;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import ke.co.tonyoa.mahao.R;
import ke.co.tonyoa.mahao.app.api.responses.Amenity;
import ke.co.tonyoa.mahao.app.interfaces.OnItemClickListener;
import ke.co.tonyoa.mahao.databinding.ItemAmenityBinding;

public class PropertyAmenityAdapter extends ListAdapter<Amenity, PropertyAmenityAdapter.AmenityViewHolder> {

    private Context mContext;
    private OnItemClickListener<Amenity> mOnItemClickListener;
    private List<Amenity> mPropertyAmenities;
    private List<Integer> mAddedAmenities = new ArrayList<>();
    private List<Integer> mRemovedAmenities = new ArrayList<>();

    public PropertyAmenityAdapter(Context context, OnItemClickListener<Amenity> onItemClickListener,
                                  List<Amenity> propertyAmenities) {
        super(new DiffUtil.ItemCallback<Amenity>() {
            @Override
            public boolean areItemsTheSame(@NonNull Amenity oldItem, @NonNull Amenity newItem) {
                return Objects.equals(oldItem.getId(), newItem.getId());
            }

            @Override
            public boolean areContentsTheSame(@NonNull Amenity oldItem, @NonNull Amenity newItem) {
                return oldItem.equals(newItem);
            }
        });
        mContext = context;
        mOnItemClickListener = onItemClickListener;
        mPropertyAmenities = propertyAmenities;
    }

    @NonNull
    @Override
    public AmenityViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new AmenityViewHolder(ItemAmenityBinding.inflate(LayoutInflater.from(mContext), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull AmenityViewHolder holder, int position) {
        holder.bind(position);
    }

    public List<Integer> getAddedAmenities() {
        return mAddedAmenities;
    }

    public void setAddedAmenities(List<Integer> addedAmenities) {
        mAddedAmenities = addedAmenities;
    }

    public List<Integer> getRemovedAmenities() {
        return mRemovedAmenities;
    }

    public void setRemovedAmenities(List<Integer> removedAmenities) {
        mRemovedAmenities = removedAmenities;
    }

    public List<Amenity> getPropertyAmenities() {
        return mPropertyAmenities;
    }

    public void setPropertyAmenities(List<Amenity> propertyAmenities) {
        mPropertyAmenities = propertyAmenities;
    }

    class AmenityViewHolder extends RecyclerView.ViewHolder{

        private ItemAmenityBinding mItemAmenityBinding;

        public AmenityViewHolder(ItemAmenityBinding itemAmenityBinding) {
            super(itemAmenityBinding.getRoot());
            mItemAmenityBinding = itemAmenityBinding;
            itemView.setOnClickListener(v->{
                Amenity amenity = getItem(getAdapterPosition());
                boolean isChecked = mItemAmenityBinding.imageViewItemAmenityAddRemove.getVisibility()==View.VISIBLE;
                if (isChecked){
                    mPropertyAmenities.remove(amenity);
                    mRemovedAmenities.add(amenity.getId());
                }
                else {
                    mPropertyAmenities.add(amenity);
                    mAddedAmenities.add(amenity.getId());
                }
                notifyItemChanged(getAdapterPosition());
                if (mOnItemClickListener!=null){
                    mOnItemClickListener.onItemClick(amenity, getAdapterPosition());
                }
            });
        }

        public void bind(int position){
            Amenity amenity = getItem(position);
            Glide.with(mContext)
                    .load(amenity.getIcon())
                    .placeholder(R.drawable.ic_home_black_24dp)
                    .error(R.drawable.ic_home_black_24dp)
                    .into(mItemAmenityBinding.imageViewItemAmenityIcon);
            mItemAmenityBinding.textViewItemAmenityTitle.setText(amenity.getTitle());
            if (mPropertyAmenities.contains(amenity)){
                mItemAmenityBinding.imageViewItemAmenityAddRemove.setVisibility(View.VISIBLE);
            }
            else {
                mItemAmenityBinding.imageViewItemAmenityAddRemove.setVisibility(View.GONE);
            }
        }
    }
}
