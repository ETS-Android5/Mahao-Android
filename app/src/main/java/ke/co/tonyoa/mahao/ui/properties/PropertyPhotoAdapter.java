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

import ke.co.tonyoa.mahao.R;
import ke.co.tonyoa.mahao.app.api.responses.PropertyPhoto;
import ke.co.tonyoa.mahao.app.interfaces.OnItemClickListener;
import ke.co.tonyoa.mahao.databinding.ItemGalleryBinding;

public class PropertyPhotoAdapter extends ListAdapter<PropertyPhoto, PropertyPhotoAdapter.PropertyPhotoViewHolder> {


    private Context mContext;
    private OnItemClickListener<PropertyPhoto> mPropertyPhotoOnItemClickListener;
    private OnItemClickListener<PropertyPhoto> mPropertyPhotoOnDeleteClickListener;
    private boolean mEditable;

    public PropertyPhotoAdapter(Context context, OnItemClickListener<PropertyPhoto> propertyPhotoOnItemClickListener,
                                OnItemClickListener<PropertyPhoto> propertyPhotoOnDeleteClickListener, boolean editable) {
        super(new DiffUtil.ItemCallback<PropertyPhoto>() {
            @Override
            public boolean areItemsTheSame(@NonNull PropertyPhoto oldItem, @NonNull PropertyPhoto newItem) {
                return oldItem.getId().equals(newItem.getId());
            }

            @Override
            public boolean areContentsTheSame(@NonNull PropertyPhoto oldItem, @NonNull PropertyPhoto newItem) {
                return oldItem.equals(newItem);
            }
        });
        mContext = context;
        mPropertyPhotoOnItemClickListener = propertyPhotoOnItemClickListener;
        mPropertyPhotoOnDeleteClickListener = propertyPhotoOnDeleteClickListener;
        mEditable = editable;
    }

    @NonNull
    @Override
    public PropertyPhotoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new PropertyPhotoViewHolder(ItemGalleryBinding.inflate(LayoutInflater.from(mContext), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull PropertyPhotoViewHolder holder, int position) {
        holder.bind(position);
    }

    class PropertyPhotoViewHolder extends RecyclerView.ViewHolder{

        private final ItemGalleryBinding mItemGalleryBinding;

        public PropertyPhotoViewHolder(@NonNull ItemGalleryBinding itemGalleryBinding) {
            super(itemGalleryBinding.getRoot());
            mItemGalleryBinding = itemGalleryBinding;
            mItemGalleryBinding.getRoot().setOnClickListener(v->{
                PropertyPhoto propertyPhoto = getItem(getAdapterPosition());
                if (mPropertyPhotoOnItemClickListener!=null)
                    mPropertyPhotoOnItemClickListener.onItemClick(propertyPhoto, getAdapterPosition());
            });
            mItemGalleryBinding.imageButtonItemGalleryDelete.setOnClickListener(v->{
                PropertyPhoto propertyPhoto = getItem(getAdapterPosition());
                if (mPropertyPhotoOnDeleteClickListener !=null)
                    mPropertyPhotoOnDeleteClickListener.onItemClick(propertyPhoto, getAdapterPosition());
            });
        }

        public void bind(int position){
            PropertyPhoto propertyPhoto = getItem(position);
            Glide.with(mContext)
                    .load(propertyPhoto.getPhoto())
                    .placeholder(R.drawable.ic_home_black_24dp)
                    .error(R.drawable.ic_home_black_24dp)
                    .into(mItemGalleryBinding.imageViewItemGallery);
            mItemGalleryBinding.imageButtonItemGalleryDelete.setVisibility(mEditable? View.VISIBLE:View.GONE);
        }
    }
}
