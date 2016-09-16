package com.littleones.fragenrondell;

        import android.content.Context;
        import android.graphics.Canvas;
        import android.util.AttributeSet;
        import android.util.DisplayMetrics;
        import android.util.Log;
        import android.view.Display;
        import android.view.ViewGroup;
        import android.widget.RelativeLayout;

public class ScalingRelativeLayout extends RelativeLayout {
    int baseWidth;
    int baseHeight;
    boolean alreadyScaled;
    float scale;
    int expectedWidth;
    int expectedHeight;


    public ScalingRelativeLayout(Context context) {
        super(context);

        Log.d("notcloud.view", "ScalingRelativeLayout: width=" + this.getWidth() + ", height=" + this.getHeight());
        this.alreadyScaled = false;
    }

    public ScalingRelativeLayout(Context context, AttributeSet attributes) {
        super(context, attributes);

        Log.d("notcloud.view", "ScalingRelativeLayout: attrib - width=" + this.getWidth() + ", height=" + this.getHeight());
        this.alreadyScaled = false;

    }

    public void onFinishInflate() {
        Log.d("notcloud.view", "ScalingRelativeLayout::onFinishInflate: 1 width=" + this.getWidth() + ", height=" + this.getHeight());
        //this.setMinimumWidth(Activity_Main.screenWidth);
        //this.setMinimumHeight(Activity_Main.screenHeight);
        // Do an initial measurement of this layout with no major restrictions on size.
        // This will allow us to figure out what the original desired width and height are.
        this.measure(1000, 1000); // Adjust this up if necessary.
        this.baseWidth = this.getMeasuredWidth();
        this.baseHeight = this.getMeasuredHeight();

        this.scale = Math.min(Activity_Main.screenWidth*1.f/this.baseWidth,1.f);

        Log.d("notcloud.view", "ScalingRelativeLayout::onFinishInflate: base width=" + this.getMeasuredWidth() + ", height=" + this.getMeasuredHeight());
        Log.d("notcloud.view", "ScalingRelativeLayout::onFinishInflate: 2 width=" + this.getWidth() + ", height=" + this.getHeight());

        Log.d("notcloud.view", "ScalingRelativeLayout::onFinishInflate: alreadyScaled=" + this.alreadyScaled);
        Log.d("notcloud.view", "ScalingRelativeLayout::onFinishInflate: scale=" + this.scale);
        if(!this.alreadyScaled && this.scale > 0.f) {

            Scale.scaleChildren((RelativeLayout)this, this.scale);
        }
        super.onFinishInflate();
    }


    public void draw(Canvas canvas) {
        // Get the current width and height.
        int width = this.getWidth();
        int height = this.getHeight();

        Log.d("notcloud.view", "canvas: " + width + " " + height);
        Log.d("notcloud.view", "base: " + this.baseWidth + " " + this.baseHeight);

        // Figure out if we need to scale the layout.
        // We may need to scale if:
        //    1. We haven't scaled it before.
        //    2. The width has changed.
        //    3. The height has changed.
        if(!this.alreadyScaled || width != this.expectedWidth || height != this.expectedHeight) {
            // Figure out the x-scaling.
            float xScale = (float)width / this.baseWidth;
            if(this.alreadyScaled && width != this.expectedWidth) {
                xScale = (float)width / this.expectedWidth;
            }
            // Figure out the y-scaling.
            float yScale = (float)height / this.baseHeight;
            if(this.alreadyScaled && height != this.expectedHeight) {
                yScale = (float)height / this.expectedHeight;
            }

            // Scale the layout.
            this.scale = Math.min(xScale, yScale);
            Log.d("notcloud.view", "ScalingRelativeLayout::onLayout: Scaling with " + this.scale + "!");
            Scale.scaleViewAndChildren((RelativeLayout)this, this.scale, 0);

            // Mark that we've already scaled this layout, and what
            // the width and height were when we did so.
            this.alreadyScaled = true;
            this.expectedWidth = width;
            this.expectedHeight = height;

            // Finally, return.
            return;
        }

        super.draw(canvas);
    }
}