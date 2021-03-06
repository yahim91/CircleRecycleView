package me.rotatingrecyclerview;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by mihai on 7/16/16.
 */
public class ScrollListener extends RecyclerView.OnScrollListener {

    private static final String TAG = "ScrollListener";

    public int getViewPosition(int i, int count) {
        if (i == largestView) {
            return count - 1;
        } else if (i > largestView) {
            return count - 1 - (i - largestView);
        } else {
            return i;
        }
    }

    private int largestView;

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);
        int itemSize = recyclerView.getWidth()/3;
        int centerX = recyclerView.getWidth()/2;
        Log.d(TAG, "width: " + recyclerView.getWidth());
        int begin = itemSize;

        int max = 0;

        for (int i = 0; i < recyclerView.getChildCount(); i++) {
            View view = recyclerView.getChildAt(i);
            float childCenterX = view.getX() + view.getWidth()/2;
            Log.d(TAG, String.format("%d centerX: %d childCenterX %f", i,  centerX, childCenterX));
            MainActivity.Adapter.ViewHolder holder = (MainActivity.Adapter.ViewHolder) recyclerView.getChildViewHolder(view);
            ViewGroup.LayoutParams params = view.getLayoutParams();
            float percent = 1F - Math.abs(childCenterX - centerX)/itemSize;
            if (percent < 0F) {
                percent = 0F;
            } else if (percent > 1F) {
                percent = 1F;
            }

            float ratio = 1F + (percent * 0.5F);
            params.height = (int)(itemSize * ratio);
            params.width = (int)(itemSize * ratio);
            holder.offset = (params.width - itemSize)/2;
            view.requestLayout();

            if (holder.offset > max) {
                max = holder.offset;
                largestView = i;
            }

            Log.d(TAG, String.format("percent %d %f", i, percent));

            Log.d(TAG, String.format("offset %d", holder.offset));
        }
        recyclerView.invalidateItemDecorations();

    }

    @Override
    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        super.onScrollStateChanged(recyclerView, newState);

        Log.d(TAG, "State changed: " + newState);
    }


}
