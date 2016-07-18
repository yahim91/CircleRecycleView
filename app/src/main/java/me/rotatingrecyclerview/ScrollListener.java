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

        // 0 1 2 3 4 5 6 7 8

        //   8 7 6 5 4 3 2 1
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
        Log.d(TAG, "width: " + recyclerView.getWidth());
        int begin = itemSize;

        int max = 0;

        for (int i = 0; i < recyclerView.getChildCount(); i++) {
            View view = recyclerView.getChildAt(i);
            Log.d(TAG, "X: " + i + " " +  view.getX());
            MainActivity.Adapter.ViewHolder holder = (MainActivity.Adapter.ViewHolder) recyclerView.getChildViewHolder(view);
            ViewGroup.LayoutParams params = view.getLayoutParams();
            float percent = 1F - (Math.abs(view.getX() - begin))/itemSize;
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
    }


}
