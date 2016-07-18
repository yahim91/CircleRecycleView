package me.rotatingrecyclerview.snappy;

/**
 * Created by mihai on 7/18/2016.
 */
public interface ISnappyLayoutManager {

    /**
     * @param velocityX
     * @param velocityY
     * @param childSize
     * @return the resultant position from a fling of the given velocity.
     */
    int getPositionForVelocity(int velocityX, int velocityY, int childSize);

    /**
     * @return the position this list must scroll to to fix a state where the
     * views are not snapped to grid.
     */
    int getFixScrollPos();

}
