package com.cos.jogger.activities;

/**
 * Created by admin1 on 10/29/2015.
 */

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.widget.ImageView;

public class RotateZoomImageView extends ImageView {
    private ScaleGestureDetector mScaleDetector;
    private Matrix mImageMatrix;
    /* Last Rotation Angle */
    private int mLastAngle = 0;
    /* Pivot Point for Transforms */
    private int mPivotX, mPivotY;

    // We can be in one of these 3 states
    static final int NONE = 0;
    static final int DRAG = 1;
    static final int ZOOM = 2;
    int mode = NONE;

    // Remember some things for zooming
    Matrix savedMatrix = new Matrix();
    PointF start = new PointF();

    public RotateZoomImageView(Context context) {
        super(context);
        init(context);
    }

    public RotateZoomImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public RotateZoomImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
        mScaleDetector = new ScaleGestureDetector(context, mScaleListener);

        setScaleType(ScaleType.MATRIX);
        mImageMatrix = new Matrix();
    }

    /*
     * Use onSizeChanged() to calculate values based on the view's size.
     * The view has no size during init(), so we must wait for this
     * callback.
     */
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        if (w != oldw || h != oldh) {
            //Shift the image to the center of the view
            int translateX = (w - getDrawable().getIntrinsicWidth()) / 2;
            int translateY = (h - getDrawable().getIntrinsicHeight()) / 2;
            mImageMatrix.setTranslate(translateX, translateY);
            setImageMatrix(mImageMatrix);
            //Get the center point for future scale and rotate transforms
            mPivotX = w / 2;
            mPivotY = h / 2;
        }
    }

    private ScaleGestureDetector.SimpleOnScaleGestureListener mScaleListener = new ScaleGestureDetector.SimpleOnScaleGestureListener() {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            // ScaleGestureDetector calculates a scale factor based on whether
            // the fingers are moving apart or together
            float scaleFactor = detector.getScaleFactor();
            //Pass that factor to a scale for the image
            mImageMatrix.postScale(scaleFactor, scaleFactor, mPivotX, mPivotY);
            setImageMatrix(mImageMatrix);

            return true;
        }
    };

    /*
     * Operate on two-finger events to rotate the image.
     * This method calculates the change in angle between the
     * pointers and rotates the image accordingly.  As the user
     * rotates their fingers, the image will follow.
     */
    private boolean doRotationEvent(MotionEvent event) {
        //Calculate the angle between the two fingers
        mScaleDetector.onTouchEvent(event);
        float deltaX = event.getX(0) - event.getX(1);
        float deltaY = event.getY(0) - event.getY(1);
        double radians = Math.atan(deltaY / deltaX);
        //Convert to degrees
        int degrees = (int)(radians * 180 / Math.PI);

        /*
         * Must use getActionMasked() for switching to pick up pointer events.
         * These events have the pointer index encoded in them so the return
         * from getAction() won't match the exact action constant.
         */
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_POINTER_DOWN:
            case MotionEvent.ACTION_POINTER_UP:
                //Mark the initial angle
                mLastAngle = degrees;
                break;
            case MotionEvent.ACTION_MOVE:
                // ATAN returns a converted value between -90deg and +90deg
                // which creates a point when two fingers are vertical where the
                // angle flips sign.  We handle this case by rotating a small amount
                // (5 degrees) in the direction we were traveling
                if ((degrees - mLastAngle) > 45) {
                    //Going CCW across the boundary
                    mImageMatrix.postRotate(-5, mPivotX, mPivotY);
                } else if ((degrees - mLastAngle) < -45) {
                    //Going CW across the boundary
                    mImageMatrix.postRotate(5, mPivotX, mPivotY);
                } else {
                    //Normal rotation, rotate the difference
                    mImageMatrix.postRotate(degrees - mLastAngle, mPivotX, mPivotY);
                }
                //Post the rotation to the image
                setImageMatrix(mImageMatrix);
                //Save the current angle
                mLastAngle = degrees;
                break;
        }

        return true;
    }

    public boolean move(MotionEvent event) {

        float scale;

        // Handle touch events here...
        switch (event.getAction() & MotionEvent.ACTION_MASK) {

            case MotionEvent.ACTION_DOWN: //first finger down only
                savedMatrix.set(mImageMatrix);
                start.set(event.getX(), event.getY());
                mode = DRAG;
                break;
            case MotionEvent.ACTION_UP: //first finger lifted
            case MotionEvent.ACTION_POINTER_UP: //second finger lifted
                mode = NONE;
                break;
            case MotionEvent.ACTION_POINTER_DOWN: //second finger down
                break;

            case MotionEvent.ACTION_MOVE:
                if (mode == DRAG)
                { //movement of first finger
                    mImageMatrix.set(savedMatrix);
                    if (this.getLeft() >= -392)
                    {
                        mImageMatrix.postTranslate(event.getX() - start.x, event.getY() - start.y);
                    }
                }
                break;
        }

        // Perform the transformation
        setImageMatrix(mImageMatrix);

        return true; // indicate event was handled
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        /*if (event.getAction() == MotionEvent.ACTION_DOWN) {
            // We don't care about this event directly, but we declare
            // interest so we can get later multi-touch events.
            return true;
        }*/


        switch (event.getPointerCount()) {
            case 3:
                // With three fingers down, zoom the image
                // using the ScaleGestureDetector
                return mScaleDetector.onTouchEvent(event);
            case 2:
                // With two fingers down, rotate the image
                // following the fingers
                return doRotationEvent(event);
            case 1:
                return move(event);
            default:
                //Ignore this event
                return super.onTouchEvent(event);
        }
    }

    public Bitmap getBitmap(){
        Bitmap original = ((BitmapDrawable) this.getDrawable()).getBitmap();
        Bitmap result = Bitmap.createBitmap(original, 0, 0, original.getWidth(), original.getHeight(), mImageMatrix, true);
        return result;

    }
}
