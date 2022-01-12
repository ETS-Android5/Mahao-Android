package ke.co.tonyoa.mahao.ui.properties.single;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import ke.co.tonyoa.mahao.R;
import ke.co.tonyoa.mahao.databinding.ItemFullPhotoBinding;

public class FullPhotoAdapter extends ListAdapter<String, FullPhotoAdapter.FullPhotoViewHolder> {


    private Context mContext;

    public FullPhotoAdapter(Context context) {
        super(new DiffUtil.ItemCallback<String>() {
            @Override
            public boolean areItemsTheSame(@NonNull String oldItem, @NonNull String newItem) {
                return oldItem.equals(newItem);
            }

            @Override
            public boolean areContentsTheSame(@NonNull String oldItem, @NonNull String newItem) {
                return oldItem.equals(newItem);
            }
        });
        mContext = context;
    }

    @NonNull
    @Override
    public FullPhotoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new FullPhotoViewHolder(ItemFullPhotoBinding.inflate(LayoutInflater.from(mContext), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull FullPhotoViewHolder holder, int position) {
        holder.bind(position);
    }

    class FullPhotoViewHolder extends RecyclerView.ViewHolder{

        private final ItemFullPhotoBinding mItemFullPhotoBinding;

        public FullPhotoViewHolder(@NonNull ItemFullPhotoBinding itemFullPhotoBinding) {
            super(itemFullPhotoBinding.getRoot());
            mItemFullPhotoBinding = itemFullPhotoBinding;
        }

        public void bind(int position){
            String propertyPhoto = getItem(position);
            Glide.with(mContext)
                    .load(propertyPhoto)
                    .placeholder(R.drawable.ic_home_black_24dp)
                    .error(R.drawable.ic_home_black_24dp)
                    .into(mItemFullPhotoBinding.touchImageViewItemFullPhoto);
        }
    }
}
