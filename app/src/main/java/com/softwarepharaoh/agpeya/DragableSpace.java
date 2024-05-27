package com.softwarepharaoh.agpeya;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.Scroller;

public class DragableSpace extends ViewGroup {
    private static final int SNAP_VELOCITY = 300;
    // private static final int TOUCH_STATE_REST = 0;
    // private static final int TOUCH_STATE_SCROLLING = 1;
    private float lastMotionY;
    private int mCurrentScreen = 0;
    private float mLastMotionX;
    private int mScrollX = 0;
    private final Scroller mScroller;
    private int mTouchSlop = 0;
    private int mTouchState = 0;
    private VelocityTracker mVelocityTracker;

//    public interface onViewChangedEvent {
//        void onViewChange(int i);
//    }

    public DragableSpace(Context context) {
        super(context);
        this.mScroller = new Scroller(context);
        this.mTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
        setLayoutParams(new ViewGroup.LayoutParams(-2, -1));
    }

    public boolean onInterceptTouchEvent(MotionEvent ev) {
        boolean yMoved;
        boolean xMoved;
        int action = ev.getAction();
        if (action == 2 && this.mTouchState != 0) {
            return true;
        }
        float x = ev.getX();
        float y = ev.getY();
        int allowedDistance = 50;
        switch (action) {
            case 0:
                this.mLastMotionX = x;
                this.lastMotionY = y;
                this.mTouchState = this.mScroller.isFinished() ? 0 : 1;
                break;
            case 1:
            case 3:
                this.mTouchState = 0;
                break;
            case 2:
                int xDiff = (int) Math.abs(x - this.mLastMotionX);
                yMoved = ((int) Math.abs(y - this.lastMotionY)) < allowedDistance;
                xMoved = xDiff > this.mTouchSlop;
                if (yMoved && xMoved) {
                    this.mTouchState = 1;
                    break;
                }
        }
        if (this.mTouchState == 0) {
            return false;
        }
        return true;
    }

    public boolean onTouchEvent(MotionEvent event) {
        int availableToScroll;
        if (this.mVelocityTracker == null) {
            this.mVelocityTracker = VelocityTracker.obtain();
        }
        this.mVelocityTracker.addMovement(event);
        int action = event.getAction();
        float x = event.getX();
        switch (action) {
            case 0:
                if (!this.mScroller.isFinished()) {
                    this.mScroller.abortAnimation();
                }
                this.mLastMotionX = x;
                break;
            case 1:
                VelocityTracker velocityTracker = this.mVelocityTracker;
                velocityTracker.computeCurrentVelocity(1000);
                int velocityX = (int) velocityTracker.getXVelocity();
                if (velocityX > SNAP_VELOCITY && this.mCurrentScreen > 0) {
                    snapToScreen(this.mCurrentScreen - 1);
                } else if (velocityX >= -300 || this.mCurrentScreen >= getChildCount() - 1) {
                    snapToDestination();
                } else {
                    snapToScreen(this.mCurrentScreen + 1);
                }
                if (this.mVelocityTracker != null) {
                    this.mVelocityTracker.recycle();
                    this.mVelocityTracker = null;
                }
                this.mTouchState = 0;
                break;
            case 2:
                int deltaX = (int) (this.mLastMotionX - x);
                this.mLastMotionX = x;
                if (deltaX >= 0) {
                    if (deltaX > 0 && (availableToScroll = (getChildAt(getChildCount() - 1).getRight() - this.mScrollX) - getWidth()) > 0) {
                        scrollBy(Math.min(availableToScroll, deltaX), 0);
                        break;
                    }
                } else if (this.mScrollX > 0) {
                    scrollBy(Math.max(-this.mScrollX, deltaX), 0);
                    break;
                }
                break;
            case 3:
                this.mTouchState = 0;
                break;
        }
        this.mScrollX = getScrollX();
        return true;
    }

    private void snapToDestination() {
        int screenWidth = getWidth();
        snapToScreen((this.mScrollX + (screenWidth / 2)) / screenWidth);
    }

    public void snapToScreen(int whichScreen) {
        this.mCurrentScreen = whichScreen;
        int delta = (whichScreen * getWidth()) - this.mScrollX;
        this.mScroller.startScroll(this.mScrollX, 0, delta, 0, Math.abs(delta) * 2);
        invalidate();
    }

//    public void setToScreen(int whichScreen) {
//        this.mCurrentScreen = whichScreen;
//        this.mScroller.startScroll(this.mScrollX, 0, (whichScreen * getWidth()) - this.mScrollX, 0, 1);
//        invalidate();
//    }

    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int childLeft = 0;
        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            // if (child.getVisibility() != 8) {
            if (child.getVisibility() != View.GONE) {
                int childWidth = child.getMeasuredWidth();
                child.layout(childLeft, 0, childLeft + childWidth, child.getMeasuredHeight());
                childLeft += childWidth;
            }
        }
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = View.MeasureSpec.getSize(widthMeasureSpec);
        // if (View.MeasureSpec.getMode(widthMeasureSpec) != 1073741824) {
        if (MeasureSpec.getMode(widthMeasureSpec) != MeasureSpec.EXACTLY) {
            throw new IllegalStateException("error mode.");
        //} else if (MeasureSpec.getMode(heightMeasureSpec) != 1073741824) {
        } else if (MeasureSpec.getMode(heightMeasureSpec) != MeasureSpec.EXACTLY) {
            throw new IllegalStateException("error mode.");
        } else {
            int count = getChildCount();
            for (int i = 0; i < count; i++) {
                getChildAt(i).measure(widthMeasureSpec, heightMeasureSpec);
            }
            scrollTo(this.mCurrentScreen * width, 0);
        }
    }

    public void computeScroll() {
        if (this.mScroller.computeScrollOffset()) {
            this.mScrollX = this.mScroller.getCurrX();
            scrollTo(this.mScrollX, 0);
            postInvalidate();
        }
    }

    protected Parcelable onSaveInstanceState() {
        SavedState state = new SavedState(super.onSaveInstanceState());
        state.currentScreen = this.mCurrentScreen;
        return state;
    }

    protected void onRestoreInstanceState(Parcelable state) {
        SavedState savedState = (SavedState) state;
        super.onRestoreInstanceState(savedState.getSuperState());
        if (savedState.currentScreen != -1) {
            this.mCurrentScreen = savedState.currentScreen;
        }
    }

    public void setCurrentScreen(int screen) {
        this.mCurrentScreen = screen;
    }

    public int getCurrentScreen() {
        return this.mCurrentScreen;
    }

    public static class SavedState extends View.BaseSavedState {
        public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator<SavedState>() {
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in, (SavedState) null);
            }

            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
        int currentScreen;

        SavedState(Parcelable superState) {
            super(superState);
            this.currentScreen = -1;
        }

        SavedState(Parcel parcel, SavedState savedState) {
            this(parcel);
        }

        private SavedState(Parcel in) {
            super(in);
            this.currentScreen = -1;
            this.currentScreen = in.readInt();
        }

        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeInt(this.currentScreen);
        }
    }
}
