package ke.co.tonyoa.mahao.ui.profile.users;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.paging.PagedListAdapter;
import androidx.recyclerview.widget.DiffUtil;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;

import java.util.Objects;

import ke.co.tonyoa.mahao.R;
import ke.co.tonyoa.mahao.app.api.responses.User;
import ke.co.tonyoa.mahao.app.interfaces.OnItemClickListener;
import ke.co.tonyoa.mahao.app.paging.RepoDataSource;
import ke.co.tonyoa.mahao.databinding.ItemLoadingBinding;
import ke.co.tonyoa.mahao.databinding.ItemUserBinding;
import ke.co.tonyoa.mahao.ui.common.LoadingViewHolder;

public class UserAdapter extends PagedListAdapter<User, RecyclerView.ViewHolder> {

    private static final int TYPE_LOAD = 1;
    private static final int TYPE_USER = 2;

    private final Context mContext;
    private final OnItemClickListener<User> mOnItemClickListener;
    private Integer mLoadState;

    public UserAdapter(Context context, OnItemClickListener<User> onItemClickListener) {
        super(new DiffUtil.ItemCallback<User>() {
            @Override
            public boolean areItemsTheSame(@NonNull User oldItem, @NonNull User newItem) {
                return Objects.equals(oldItem.getId(), newItem.getId());
            }

            @Override
            public boolean areContentsTheSame(@NonNull User oldItem, @NonNull User newItem) {
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
        if (viewType == TYPE_USER) {
            return new UserViewHolder(ItemUserBinding.inflate(LayoutInflater.from(mContext), parent,
                    false));
        }
        else {
            return new LoadingViewHolder(ItemLoadingBinding.inflate(inflater, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof UserViewHolder) {
            ((UserViewHolder)holder).bind(position);
        }
    }


    @Override
    public int getItemViewType(int position) {
        //IMPORTANT: When to show a loading spinner
        //if((we are at the last position of previous page)  &&  (the loading state is ONGOING))
        if (position == getItemCount()-1  && mLoadState != null && mLoadState.equals(RepoDataSource.LOADING_ONGOING))
            return TYPE_LOAD;
        else
            return TYPE_USER;
    }

    public void setLoadState(int loadState) {
        mLoadState = loadState;
        // Remove the loading progressbar
        notifyItemChanged(getItemCount()-1);
    }


    class UserViewHolder extends RecyclerView.ViewHolder{

        private final ItemUserBinding mItemUserBinding;

        public UserViewHolder(ItemUserBinding itemUserBinding) {
            super(itemUserBinding.getRoot());
            mItemUserBinding = itemUserBinding;
            itemView.setOnClickListener(v->{
                if (mOnItemClickListener!=null){
                    User user = getItem(getAdapterPosition());
                    mOnItemClickListener.onItemClick(user, getAdapterPosition());
                }
            });
        }

        public void bind(int position){
            User user = getItem(position);
            if (user == null)
                return;
            Glide.with(mContext)
                    .load(user.getProfilePicture())
                    .placeholder(R.drawable.ic_baseline_person_24)
                    .error(R.drawable.ic_baseline_person_24)
                    .transform(new CircleCrop())
                    .into(mItemUserBinding.imageViewItemUserProfile);
            mItemUserBinding.textViewItemUserEmail.setText(user.getEmail());
            mItemUserBinding.textViewItemUserName.setText(String.format("%s %s", user.getFirstName(), user.getLastName()));
        }
    }
}
