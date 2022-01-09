package ke.co.tonyoa.mahao.ui.profile.categories;

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
import ke.co.tonyoa.mahao.app.api.responses.PropertyCategory;
import ke.co.tonyoa.mahao.app.interfaces.OnItemClickListener;
import ke.co.tonyoa.mahao.databinding.ItemCategoryBinding;

public class CategoryAdapter extends ListAdapter<PropertyCategory, CategoryAdapter.CategoryViewHolder> {

    private Context mContext;
    private OnItemClickListener<PropertyCategory> mOnItemClickListener;

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
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CategoryViewHolder(ItemCategoryBinding.inflate(LayoutInflater.from(mContext), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        holder.bind(position);
    }

    class CategoryViewHolder extends RecyclerView.ViewHolder{

        private ItemCategoryBinding mItemCategoryBinding;

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
            Glide.with(mContext)
                    .load(propertyCategory.getIcon())
                    .placeholder(R.drawable.ic_home_black_24dp)
                    .error(R.drawable.ic_home_black_24dp)
                    .into(mItemCategoryBinding.imageViewItemCategoryIcon);
            mItemCategoryBinding.textViewItemCategoryTitle.setText(propertyCategory.getTitle());
        }
    }
}
