package ke.co.tonyoa.mahao.ui.profile.amenities;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.Objects;

import ke.co.tonyoa.mahao.R;
import ke.co.tonyoa.mahao.app.api.responses.Amenity;
import ke.co.tonyoa.mahao.app.interfaces.OnItemClickListener;
import ke.co.tonyoa.mahao.databinding.ItemAmenityBinding;

public class AmenityAdapter extends ListAdapter<Amenity, AmenityAdapter.AmenityViewHolder> {

    private Context mContext;
    private OnItemClickListener<Amenity> mOnItemClickListener;

    public AmenityAdapter(Context context, OnItemClickListener<Amenity> onItemClickListener) {
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

    class AmenityViewHolder extends RecyclerView.ViewHolder{

        private ItemAmenityBinding mItemAmenityBinding;

        public AmenityViewHolder(ItemAmenityBinding itemAmenityBinding) {
            super(itemAmenityBinding.getRoot());
            mItemAmenityBinding = itemAmenityBinding;
            itemView.setOnClickListener(v->{
                if (mOnItemClickListener!=null){
                    Amenity amenity = getItem(getAdapterPosition());
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
        }
    }
}
