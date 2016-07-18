package me.rotatingrecyclerview.snappy;

import android.content.Context;
import android.graphics.PointF;
import android.hardware.SensorManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSmoothScroller;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewConfiguration;

/**
 * Created by mihai on 7/18/2016.
 */
public class SnappyLinearLayoutManager extends LinearLayoutManager implements ISnappyLayoutManager {
    // These variables are from android.widget.Scroller, which is used, via ScrollerCompat, by
    // Recycler View. The scrolling distance calculation logic originates from the same place. Want
    // to use their variables so as to approximate the look of normal Android scrolling.
    // Find the Scroller fling implementation in android.widget.Scroller.fling().
    private static final float INFLEXION = 0.35f; // Tension lines cross at (INFLEXION, 1)
    private static float DECELERATION_RATE = (float) (Math.log(0.78) / Math.log(0.9));
    private float mFlingFriction = ViewConfiguration.getScrollFriction();

    private float mPhysicalCoeff;

    public SnappyLinearLayoutManager(Context context) {
        super(context);

        mPhysicalCoeff = computeDeceleration(context, 0.84F);
    }

    public SnappyLinearLayoutManager(Context context, int orientation, boolean reverseLayout) {
        super(context, orientation, reverseLayout);
        mPhysicalCoeff = computeDeceleration(context, 0.84F);
    }

    private float computeDeceleration(Context context, float friction) {
        return SensorManager.GRAVITY_EARTH // g (m/s^2)
                * 39.37F // inches per meter
                // pixels per inch. 160 is the "default" dpi, i.e. one dip is one pixel on a 160 dpi
                // screen
                * context.getResources().getDisplayMetrics().density * 160.0f * friction;
    }

    @Override
    public int getPositionForVelocity(int velocityX, int velocityY, int childSize) {
        if (getChildCount() == 0) {
            return 0;
        }

        if (canScrollHorizontally()) {
            return calcPosForVelocity(velocityX, getChildAt(0).getLeft(), getChildAt(0).getWidth(),
                    getPosition(getChildAt(0)));
        } else {
            return calcPosForVelocity(velocityY, getChildAt(0).getTop(), getChildAt(0).getHeight(),
                    getPosition(getChildAt(0)));
        }
    }

    private int calcPosForVelocity(int velocity, int scrollPos, int childSize, int currPos) {
        final double dist = getSplineFlingDistance(velocity);

        final double tempScroll = scrollPos + (velocity > 0 ? dist : -dist);

        /*if (velocity < 0) {
            // Not sure if I need to lower bound this here.
            if (Math.abs(dist) < childSize && Math.abs(dist) > childSize/2) {
                Log.d("next", "" + Math.max(currPos - 1, 0));
                return Math.max(currPos - 1, 0);
            } else {
                return (int) Math.max(currPos - dist/ childSize + 1, 0);
            }
        } else {
            if (Math.abs(dist) < childSize && Math.abs(dist) > childSize/2) {
                return currPos + 1;
            } else {
                return (int) (currPos + (dist / childSize));
            }
        }*/

        Log.d("Snappy", String.format("%d %d", currPos, velocity));
        if (velocity < 0) {
            return Math.max(currPos, 0);
        } else {
            return currPos + 1;
        }
    }

    @Override
    public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state, int position) {
        final LinearSmoothScroller linearSmoothScroller =
                new LinearSmoothScroller(recyclerView.getContext()) {

                    // I want a behavior where the scrolling always snaps to the beginning of
                    // the list. Snapping to end is also trivial given the default implementation.
                    // If you need a different behavior, you may need to override more
                    // of the LinearSmoothScrolling methods.
                    protected int getHorizontalSnapPreference() {
                        return SNAP_TO_START;
                    }

                    protected int getVerticalSnapPreference() {
                        return SNAP_TO_START;
                    }

                    @Override
                    public PointF computeScrollVectorForPosition(int targetPosition) {
                        return SnappyLinearLayoutManager.this
                                .computeScrollVectorForPosition(targetPosition);
                    }
                };
        linearSmoothScroller.setTargetPosition(position);
        startSmoothScroll(linearSmoothScroller);
    }

    private double getSplineFlingDistance(double velocity) {
        final double l = getSplineDeceleration(velocity);
        final double decelMinusOne = DECELERATION_RATE - 1.0;
        return mFlingFriction * mPhysicalCoeff
                * Math.exp(DECELERATION_RATE / decelMinusOne * l);
    }

    private double getSplineDeceleration(double velocity) {
        return Math.log(INFLEXION * Math.abs(velocity)
                / (mFlingFriction * mPhysicalCoeff));
    }

    /**
     * This implementation obviously doesn't take into account the direction of the
     * that preceded it, but there is no easy way to get that information without more
     * hacking than I was willing to put into it.
     */
    @Override
    public int getFixScrollPos() {
        if (this.getChildCount() == 0) {
            return 0;
        }

        final View child = getChildAt(0);
        final int childPos = getPosition(child);

        if (getOrientation() == HORIZONTAL
                && Math.abs(child.getLeft()) > child.getMeasuredWidth() / 2) {
            Log.d("onTouch", String.format("no fling left: %d", child.getLeft()));
            // Scrolled first view more than halfway offscreen
            return childPos + 1;
        } else if (getOrientation() == VERTICAL
                && Math.abs(child.getTop()) > child.getMeasuredWidth() / 2) {
            // Scrolled first view more than halfway offscreen
            return childPos + 1;
        }
        return childPos;
    }

}
