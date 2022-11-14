package com.aikucun.akapp.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

import com.aswife.ui.MaskImageView;

/**
 * @Parameter: TODO
 * @Function: 圆形头像
 */
public class RoundImageView extends MaskImageView {
    private BitmapShader mBitmapShader;
    private Paint mBitmapPaint = new Paint();
    private RectF mRoundRect=new RectF();

    public RoundImageView(Context context) {
        super(context);
        initObjectAttribute();
    }


    public RoundImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RoundImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        initObjectAttribute();
    }

    @Override
    public void setImageResource(int resId) {
        super.setImageResource(resId);
        createBitmapShader();
    }

    @Override
    public void setImageBitmap(Bitmap bm) {
        super.setImageBitmap(bm);
//		createBitmapShader();
    }

    @Override
    public void setImageDrawable(Drawable drawable) {
        super.setImageDrawable(drawable);
        createBitmapShader();
    }

    private void initObjectAttribute()
    {
        mBitmapPaint.setAntiAlias(true);
//		if(getScaleType() != ScaleType.CENTER_CROP)
//		{
//			setScaleType(ScaleType.CENTER_CROP);
//		}
    }

    private void createBitmapShader()
    {
        mBitmapShader = null;
        Drawable mDrawable = getDrawable();
        if (mDrawable == null)
        {
            return;
        }

        if(mDrawable instanceof BitmapDrawable)
        {
            BitmapDrawable bd = (BitmapDrawable) mDrawable;
            Bitmap bitmap = bd.getBitmap();
            mBitmapShader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        }
        else //if Drawable instanceof NinePathDrawable ，the code below is bad , because a view reference two bitmap ( one in NinePath , other is here)
        {
            int w = mDrawable.getIntrinsicWidth();
            int h = mDrawable.getIntrinsicHeight();

            Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            mDrawable.setBounds(0, 0, w, h);
            mDrawable.draw(canvas);
            mBitmapShader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mRoundRect.set(0,0,getMeasuredWidth(),getMeasuredHeight());
    }

    @Override
    protected void onDraw(Canvas canvas) {

        Drawable mDrawable = getDrawable();
        if (null == mDrawable || null == mBitmapShader)
        {
            return;
        }
        Matrix mDrawMatrix= getImageMatrix();
        if(null == mDrawMatrix)
        {
            mDrawMatrix =new Matrix();
        }
        mBitmapShader.setLocalMatrix(mDrawMatrix);
        mBitmapPaint.setShader(mBitmapShader);
        canvas.drawCircle(getWidth()/2, getHeight()/2, Math.min(getWidth()/2, getHeight()/2), mBitmapPaint);
    }
}
