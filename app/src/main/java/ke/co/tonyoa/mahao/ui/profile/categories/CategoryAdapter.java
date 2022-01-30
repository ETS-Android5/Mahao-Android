package ke.co.tonyoa.mahao.ui.profile.categories;

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
import ke.co.tonyoa.mahao.app.api.responses.PropertyCategory;
import ke.co.tonyoa.mahao.app.interfaces.OnItemClickListener;
import ke.co.tonyoa.mahao.app.paging.RepoDataSource;
import ke.co.tonyoa.mahao.databinding.ItemCategoryBinding;
import ke.co.tonyoa.mahao.databinding.ItemLoadingBinding;
import ke.co.tonyoa.mahao.ui.common.LoadingViewHolder;

public class CategoryAdapter extends PagedListAdapter<PropertyCategory, RecyclerView.ViewHolder> {

    private static final int TYPE_LOAD = 1;
    private static final int TYPE_CATEGORY = 2;

    private final Context mContext;
    private final OnItemClickListener<PropertyCategory> mOnItemClickListener;
    private Integer mLoadState;

    public CategoryAdapter(Context context, OnItemClickListener<PropertyCategory> onItemClickListener) {
        super(new DiffUtil.ItemCallback<PropertyCategory>() {
            @Override
            public boolean areItemsTheSame(@NonNull PropertyCategory oldItem, @NonNull PropertyCategory newItem) {
                return Objects.equals(oldItem.getId(), newItem.getId());
            }

            @Override
            public boolean areContentsTheSame(@NonNull PropertyCategory oldItem, @NonNull PropertyCategory newItem) {
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
        if (viewType == TYPE_CATEGORY) {
            return new CategoryViewHolder(ItemCategoryBinding.inflate(LayoutInflater.from(mContext), parent, false));
        }
        else {
            return new LoadingViewHolder(ItemLoadingBinding.inflate(inflater, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof CategoryViewHolder) {
            ((CategoryViewHolder)holder).bind(position);
        }
    }


    @Override
    public int getItemViewType(int position) {
        //IMPORTANT: When to show a loading spinner
        //if((we are at the last position of previous page)  &&  (the loading state is ONGOING))
        if (position == getItemCount()-1  && mLoadState != null && mLoadState.equals(RepoDataSource.LOADING_ONGOING))
            return TYPE_LOAD;
        else
            return TYPE_CATEGORY;
    }

    public void setLoadState(int loadState) {
        mLoadState = loadState;
        // Remove the loading progressbar
        notifyItemChanged(getItemCount()-1);
    }

    class CategoryViewHolder extends RecyclerView.ViewHolder{

        private final ItemCategoryBinding mItemCategoryBinding;

        public CategoryViewHolder(ItemCategoryBinding itemCategoryBinding) {
            super(itemCategoryBinding.getRoot());
            mItemCategoryBinding = itemCategoryBinding;
            itemView.setOnClickListener(v->{
                if (mOnItemClickListener!=null){
                    PropertyCategory propertyCategory = getItem(getAdapterPosition());
                    mOnItemClickListener.onItemClick(propertyCategory, getAdapterPosition());
                }
            });
        }

        public void bind(int position){
            PropertyCategory propertyCategory = getItem(position);
            if (propertyCategory == null)
                return;
            Glide.with(mContext)
                    .load(propertyCategory.getIcon())
                    .placeholder(R.drawable.ic_home_black_24dp)
                    .error(R.drawable.ic_home_black_24dp)
                    .into(mItemCategoryBinding.imageViewItemCategoryIcon);
            mItemCategoryBinding.textViewItemCategoryTitle.setText(propertyCategory.getTitle());
        }
    }
}
