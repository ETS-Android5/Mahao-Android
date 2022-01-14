package ke.co.tonyoa.mahao.app.utils;
// From https://medium.com/over-engineering/detecting-snap-changes-with-androids-recyclerview-snaphelper-9e9f5e95c424


import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

public class SnapScrollListener extends RecyclerView.OnScrollListener {

    public enum Behavior {
        NOTIFY_ON_SCROLL,
        NOTIFY_ON_SCROLL_STATE_IDLE
    }

    private SnapHelper mSnapHelper;
    private Behavior mBehavior;
    private OnSnapPositionChangeListener mOnSnapPositionChangeListener;
    private int mSnapPosition = RecyclerView.NO_POSITION;

    public SnapScrollListener(SnapHelper snapHelper, Behavior behavior,
                              OnSnapPositionChangeListener onSnapPositionChangeListener) {
        mSnapHelper = snapHelper;
        mBehavior = behavior;
        mOnSnapPositionChangeListener = onSnapPositionChangeListener;
    }

    @Override
    public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);
        if (mBehavior == Behavior.NOTIFY_ON_SCROLL) {
            maybeNotifySnapPositionChange(recyclerView);
        }
    }

    private void maybeNotifySnapPositionChange(RecyclerView recyclerView) {
        int snapPosition = getSnapPosition(recyclerView);
        boolean snapPositionChanged = this.mSnapPosition != snapPosition;
        if (snapPositionChanged) {
            if (mOnSnapPositionChangeListener!=null){
                mOnSnapPositionChangeListener.onSnapPositionChange(snapPosition);
            }
            this.mSnapPosition = snapPosition;
        }
    }

    private int getSnapPosition(RecyclerView recyclerView){
        RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        if (layoutManager==null){
             return RecyclerView.NO_POSITION;
        }
        View actualSnapView = mSnapHelper.findSnapView(layoutManager);
        if (actualSnapView == null)
            return RecyclerView.NO_POSITION;

        return layoutManager.getPosition(actualSnapView);
    }

    public interface OnSnapPositionChangeListener {
        void onSnapPositionChange(int position);
    }
}