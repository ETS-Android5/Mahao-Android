package ke.co.tonyoa.mahao.ui.profile.amenities;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.paging.PagedListAdapter;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.Objects;

import ke.co.tonyoa.mahao.R;
import ke.co.tonyoa.mahao.app.api.responses.Amenity;
import ke.co.tonyoa.mahao.app.interfaces.ListItemable;
import ke.co.tonyoa.mahao.app.interfaces.OnItemClickListener;
import ke.co.tonyoa.mahao.app.paging.RepoDataSource;
import ke.co.tonyoa.mahao.databinding.ItemAmenityBinding;
import ke.co.tonyoa.mahao.databinding.ItemLoadingBinding;
import ke.co.tonyoa.mahao.ui.common.LoadingViewHolder;

public class AmenityAdapterPaged extends PagedListAdapter<Amenity, RecyclerView.ViewHolder> implements
        ListItemable<Amenity> {

    private static final int TYPE_LOAD = 1;
    private static final int TYPE_AMENITY = 2;

    private final Context mContext;
    private final OnItemClickListener<Amenity> mOnItemClickListener;
    private Integer mLoadState;

    public AmenityAdapterPaged(Context context, OnItemClickListener<Amenity> onItemClickListener) {
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
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        if (viewType == TYPE_AMENITY) {
            return new AmenityViewHolder(mContext, ItemAmenityBinding.inflate(inflater, parent, false),
                    mOnItemClickListener, this);
        }
        else {
            return new LoadingViewHolder(ItemLoadingBinding.inflate(inflater, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof AmenityViewHolder) {
            ((AmenityViewHolder)holder).bind(getItem(position));
        }
    }


    @Override
    public int getItemViewType(int position) {
        //IMPORTANT: When to show a loading spinner
        //if((we are at the last position of previous page)  &&  (the loading state is ONGOING))
        if (position == getItemCount()-1  && mLoadState != null && mLoadState.equals(RepoDataSource.LOADING_ONGOING))
            return TYPE_LOAD;
        else
            return TYPE_AMENITY;
    }

    public void setLoadState(int loadState) {
        mLoadState = loadState;
        // Remove the loading progressbar
        notifyItemChanged(getItemCount()-1);
    }

    @Override
    public Amenity getListItem(int position) {
        return getItem(position);
    }


    public static class AmenityViewHolder extends RecyclerView.ViewHolder{

        private final Context mContext;
        private final ItemAmenityBinding mItemAmenityBinding;
        private final OnItemClickListener<Amenity> mOnItemClickListener;
        private ListItemable<Amenity> mListItemable;

        public AmenityViewHolder(Context context, ItemAmenityBinding itemAmenityBinding,
                                 OnItemClickListener<Amenity> onItemClickListener,
                                 ListItemable<Amenity> listItemable) {
            super(itemAmenityBinding.getRoot());
            mContext = context;
            mItemAmenityBinding = itemAmenityBinding;
            mOnItemClickListener = onItemClickListener;
            mListItemable = listItemable;
            itemView.setOnClickListener(v->{
                if (mOnItemClickListener !=null){
                    mOnItemClickListener.onItemClick(mListItemable.getListItem(getAdapterPosition()),
                            getAdapterPosition());
                }
            });
        }

        public void bind(Amenity amenity){
            if (amenity == null)
                return;
            Glide.with(mContext)
                    .load(amenity.getIcon())
                    .placeholder(R.drawable.ic_home_black_24dp)
                    .error(R.drawable.ic_home_black_24dp)
                    .into(mItemAmenityBinding.imageViewItemAmenityIcon);
            mItemAmenityBinding.textViewItemAmenityTitle.setText(amenity.getTitle());
        }
    }
}
