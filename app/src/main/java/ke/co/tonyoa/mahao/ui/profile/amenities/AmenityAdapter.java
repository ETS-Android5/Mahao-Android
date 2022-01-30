package ke.co.tonyoa.mahao.ui.profile.amenities;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;

import java.util.Objects;

import ke.co.tonyoa.mahao.app.api.responses.Amenity;
import ke.co.tonyoa.mahao.app.interfaces.OnItemClickListener;
import ke.co.tonyoa.mahao.databinding.ItemAmenityBinding;

public class AmenityAdapter extends ListAdapter<Amenity, AmenityAdapterPaged.AmenityViewHolder> {

    private final Context mContext;
    private final OnItemClickListener<Amenity> mOnItemClickListener;

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
    public AmenityAdapterPaged.AmenityViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        return new AmenityAdapterPaged.AmenityViewHolder(mContext, ItemAmenityBinding.inflate(inflater, parent, false),
                mOnItemClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull AmenityAdapterPaged.AmenityViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

}
