package ke.co.tonyoa.mahao.ui.properties;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.paging.PagedListAdapter;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Objects;

import ke.co.tonyoa.mahao.app.api.responses.Property;
import ke.co.tonyoa.mahao.app.interfaces.ListItemable;
import ke.co.tonyoa.mahao.app.interfaces.OnItemClickListener;
import ke.co.tonyoa.mahao.app.paging.RepoDataSource;
import ke.co.tonyoa.mahao.databinding.ItemHorizontalPropertyBinding;
import ke.co.tonyoa.mahao.databinding.ItemLoadingBinding;
import ke.co.tonyoa.mahao.databinding.ItemVerticalPropertyBinding;
import ke.co.tonyoa.mahao.ui.common.LoadingViewHolder;

public class PropertyAdapterPaged extends PagedListAdapter<Property, RecyclerView.ViewHolder> implements ListItemable<Property> {

    private static final int TYPE_LOAD = 1;
    private static final int TYPE_PROPERTY = 2;

    private final PropertyAdapter.ListType mListType;
    private final Context mContext;
    private final OnItemClickListener<Property> mOnPropertyClickListener;
    private final OnItemClickListener<Property> mOnPropertyLikeListener;
    private final OnItemClickListener<Property> mOnPropertyReadListener;
    private Integer mLoadState;

    public PropertyAdapterPaged(PropertyAdapter.ListType listType, Context context,
                                OnItemClickListener<Property> onPropertyClickListener,
                                OnItemClickListener<Property> onPropertyLikeListener,
                                OnItemClickListener<Property> onPropertyReadListener) {
        super(new DiffUtil.ItemCallback<Property>() {
            @Override
            public boolean areItemsTheSame(@NonNull Property oldItem, @NonNull Property newItem) {
                return Objects.equals(oldItem.getId(), newItem.getId());
            }

            @Override
            public boolean areContentsTheSame(@NonNull Property oldItem, @NonNull Property newItem) {
                return oldItem.equals(newItem);
            }
        });
        mListType = listType;
        mContext = context;
        mOnPropertyClickListener = onPropertyClickListener;
        mOnPropertyLikeListener = onPropertyLikeListener;
        mOnPropertyReadListener = onPropertyReadListener;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        if (mListType == PropertyAdapter.ListType.HORIZONTAL_PROPERTY){
            return new PropertyAdapter.HorizontalPropertyViewHolder(ItemHorizontalPropertyBinding.inflate(inflater, parent, false),
                    mOnPropertyClickListener, mOnPropertyLikeListener, mContext, this);
        }
        else {
            if (viewType == TYPE_PROPERTY) {
                return new PropertyAdapter.VerticalPropertyViewHolder(ItemVerticalPropertyBinding.inflate(inflater, parent, false),
                        mOnPropertyClickListener, mOnPropertyLikeListener, mContext, this);
            }
            else {
                return new LoadingViewHolder(ItemLoadingBinding.inflate(inflater, parent, false));
            }
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof PropertyAdapter.PropertyViewHolder) {
            ((PropertyAdapter.PropertyViewHolder)holder).bind(position);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position, @NonNull List<Object> payloads) {
        super.onBindViewHolder(holder, position, payloads);
        if (holder instanceof PropertyAdapter.PropertyViewHolder) {
            if (payloads.size() > 0) {
                ((PropertyAdapter.PropertyViewHolder)holder).like(position);
            } else {
                onBindViewHolder(holder, position);
            }
        }
        else {
            onBindViewHolder(holder, position);
        }
    }

    @Override
    public int getItemViewType(int position) {
        //IMPORTANT: When to show a loading spinner
        //if((we are at the last position of previous page)  &&  (the loading state is ONGOING)) and
        // it is a vertical list
        if (position == getItemCount()-1  && mLoadState != null &&
                mLoadState.equals(RepoDataSource.LOADING_ONGOING) && mListType == PropertyAdapter.ListType.VERTICAL_PROPERTY)
            return TYPE_LOAD;
        else
            return TYPE_PROPERTY;
    }

    public void setLoadState(int loadState) {
        mLoadState = loadState;
        // Remove the loading progressbar
        notifyItemChanged(getItemCount()-1);
    }

    @Override
    public Property getListItem(int position) {
        return getItem(position);
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        RecyclerView.LayoutManager manager = recyclerView.getLayoutManager();
        if(manager instanceof LinearLayoutManager && getItemCount() > 0) {
            LinearLayoutManager llm = (LinearLayoutManager) manager;
            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                }

                @Override
                public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    int visiblePosition = llm.findFirstCompletelyVisibleItemPosition();
                    if(visiblePosition > -1) {
                        if (mOnPropertyReadListener!=null){
                            mOnPropertyReadListener.onItemClick(getItem(visiblePosition), visiblePosition);
                        }
                    }
                }
            });
        }
    }

}
