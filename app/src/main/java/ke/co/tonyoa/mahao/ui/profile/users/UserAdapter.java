package ke.co.tonyoa.mahao.ui.profile.users;

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
import ke.co.tonyoa.mahao.app.api.responses.User;
import ke.co.tonyoa.mahao.app.interfaces.OnItemClickListener;
import ke.co.tonyoa.mahao.databinding.ItemUserBinding;

public class UserAdapter extends ListAdapter<User, UserAdapter.UserViewHolder> {

    private Context mContext;
    private OnItemClickListener<User> mOnItemClickListener;

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
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new UserViewHolder(ItemUserBinding.inflate(LayoutInflater.from(mContext), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        holder.bind(position);
    }

    class UserViewHolder extends RecyclerView.ViewHolder{

        private ItemUserBinding mItemUserBinding;

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
            Glide.with(mContext)
                    .load(user.getProfilePicture())
                    .placeholder(R.drawable.ic_baseline_person_24)
                    .error(R.drawable.ic_baseline_person_24)
                    .into(mItemUserBinding.imageViewItemUserProfile);
            mItemUserBinding.textViewItemUserEmail.setText(user.getEmail());
            mItemUserBinding.textViewItemUserName.setText(String.format("%s %s", user.getFirstName(), user.getLastName()));
        }
    }
}
