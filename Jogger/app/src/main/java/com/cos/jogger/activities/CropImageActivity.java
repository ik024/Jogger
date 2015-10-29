package com.cos.jogger.activities;

import android.graphics.Point;
import android.graphics.PointF;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.cos.jogger.R;
import com.cos.jogger.utils.Util;
import com.edmodo.cropper.CropImageView;

public class CropImageActivity extends AppCompatActivity{

    private static final String TAG = CropImageActivity.class.getSimpleName();
    // These matrices will be used to move and zoom image

    CropImageView cropImageView;
    RotateZoomImageView imageview;
    FrameLayout root;
    View cropView;
    Button cropBtn;
    boolean cropMode = false;
    ImageView mover, expander;
    PointF startMover = new PointF();
    PointF startExpander = new PointF();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop_image);
        root = (FrameLayout) findViewById(R.id.root);
        cropView = (View)findViewById(R.id.crop_view);
        imageview  = (RotateZoomImageView) findViewById(R.id.imageview);
        mover  = (ImageView) findViewById(R.id.mover);
        expander  = (ImageView) findViewById(R.id.expander);
       // cropBtn = (Button) findViewById(R.id.cropBtn);

        cropImageView = (CropImageView) findViewById(R.id.CropImageView);
        cropImageView.setAspectRatio(1, 1);
        //cropImageView.setFixedAspectRatio(true);
        cropImageView.setGuidelines(2);

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;

        cropView.getLayoutParams().width = (int) (width - Util.dipToPixels(this,50));
        cropView.getLayoutParams().height = (int) (height - Util.dipToPixels(this, 50));

       /* cropBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!cropMode) {
                    cropMode = true;
                    imageview.setVisibility(View.GONE);
                    cropImageView.setVisibility(View.VISIBLE);
                    cropImageView.setImageBitmap(imageview.getBitmap());
                    cropBtn.setText("Exit Crop Mode");
                } else {
                    cropMode = false;
                    imageview.setVisibility(View.VISIBLE);
                    cropImageView.setVisibility(View.GONE);
                    cropBtn.setText("Enter Crop Mode");
                }
            }
        });*/


        mover.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction() & MotionEvent.ACTION_MASK) {

                    case MotionEvent.ACTION_DOWN:
                        startMover.set(event.getX(), event.getY());
                        break;
                    case MotionEvent.ACTION_MOVE:

                        cropView.setX(cropView.getX() + (event.getX() - startMover.x));
                        cropView.setY(cropView.getY() + (event.getY() - startMover.y));

                        mover.setX(mover.getX() + (event.getX() - startMover.x));
                        mover.setY(mover.getY() + (event.getY() - startMover.y));

                        expander.setX(expander.getX() +( event.getX() - startMover.x ));
                        expander.setY(expander.getY() + (event.getY() - startMover.y));

                        break;
                    default:
                        break;
                }

                return true;
            }
        });

        expander.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction() & MotionEvent.ACTION_MASK) {

                    case MotionEvent.ACTION_DOWN:
                        startExpander.set(event.getX(), event.getY());
                        break;
                    case MotionEvent.ACTION_MOVE:

                        cropView.getLayoutParams().width = (int) (cropView.getLayoutParams().width + (event.getX() - startExpander.x));
                        cropView.getLayoutParams().height = (int) (cropView.getLayoutParams().height + (event.getY() - startExpander.y));
                        cropView.requestLayout();

                        mover.setX(mover.getX() + (event.getX() - startExpander.x));
                        mover.setY(mover.getY() + (event.getY() - startExpander.y));

                        expander.setX(expander.getX() + (event.getX() - startExpander.x));
                        expander.setY(expander.getY() + (event.getY() - startExpander.y));

                        break;
                    default:
                        break;
                }

                return true;
            }
        });
    }
}
