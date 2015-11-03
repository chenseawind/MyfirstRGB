package com.example.myfirstrgb;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.SweepGradient;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

/**
 * Displays a holo-themed color picker.
 * 
 * <p>
 * Use {@link #getColor()} to retrieve the selected color. <br>
 * Use {@link #addSVBar(SVBar)} to add a Saturation/Value Bar. <br>
 * Use {@link #addOpacityBar(OpacityBar)} to add a Opacity Bar.
 * </p>
 */
public class ColorPickerView extends View {
	private Paint mPaint;//æ¸?å?˜è‰²çŽ¯ç”»ç¬”
	private Paint mCenterPaint;//ä¸­é—´åœ†ç”»ç¬”
	private Paint mLinePaint;//åˆ†éš”çº¿ç”»ç¬”
	private Paint mRectPaint;//æ¸?å?˜æ–¹å?—ç”»ç¬”
	
	private Shader rectShader;//æ¸?å?˜æ–¹å?—æ¸?å?˜å›¾åƒ?
	private float rectLeft;//æ¸?å?˜æ–¹å?—å·¦xå??æ ‡
	private float rectTop;//æ¸?å?˜æ–¹å?—å?³xå??æ ‡
	private float rectRight;//æ¸?å?˜æ–¹å?—ä¸Šyå??æ ‡
	private float rectBottom;//æ¸?å?˜æ–¹å?—ä¸‹yå??æ ‡
	private final boolean debug = true;
	private final String TAG = "ColorPicker";
	
	Context context;
	private String title;//æ ‡é¢˜
	private int mInitialColor;//åˆ?å§‹é¢œè‰²
    private OnColorChangedListener mListener;

	private int[] mCircleColors;//æ¸?å?˜è‰²çŽ¯é¢œè‰²
	private int[] mRectColors;//æ¸?å?˜æ–¹å?—é¢œè‰²
	
	private int mHeight;//Viewé«˜
	private int mWidth;//Viewå®½
	private float r;//è‰²çŽ¯å?Šå¾„(paintä¸­éƒ¨)
	private float centerRadius;//ä¸­å¿ƒåœ†å?Šå¾„
	
	private boolean downInCircle = true;//æŒ‰åœ¨æ¸?å?˜çŽ¯ä¸Š
	private boolean downInRect;//æŒ‰åœ¨æ¸?å?˜æ–¹å?—ä¸Š
	private boolean highlightCenter;//é«˜äº®
	private boolean highlightCenterLittle;//å¾®äº®
	
//	public ColorPickerView(Context context, int height, int width) {
	public ColorPickerView(Context context, AttributeSet attrs) {
		super(context,attrs);
				
         int height = MainActivity.screenHeigh;
         int width = MainActivity.screenWidth;
//		int height = 400;
//		int width = 530;
		//设置圆形控件显示的位置（高、宽）
		this.mHeight = (int)(height * 0.5);
		this.mWidth = width;
		setMinimumHeight((int)(height*0.5));
		setMinimumWidth(width);
		//æ¸?å?˜è‰²çŽ¯å?‚æ•°
    	mCircleColors = new int[] {0xFFFF0000, 0xFFFF00FF, 0xFF0000FF, 
    			0xFF00FFFF, 0xFF00FF00,0xFFFFFF00, 0xFFFF0000};
    	Shader s = new SweepGradient(0, 0, mCircleColors, null);
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setShader(s);
        mPaint.setStyle(Paint.Style.STROKE);
        //set edge width
        mPaint.setStrokeWidth(150);
        r = width / 2 * 0.7f - mPaint.getStrokeWidth() * 0.5f;
        
        //ä¸­å¿ƒåœ†å?‚æ•°
        mCenterPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mCenterPaint.setColor(mInitialColor);
        mCenterPaint.setStrokeWidth(5);
        centerRadius = (r - mPaint.getStrokeWidth() / 2 ) * 0.7f;
        
        //è¾¹æ¡†å?‚æ•°
       // mLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
       // mLinePaint.setColor(Color.parseColor("#72A1D1"));
       // mLinePaint.setStrokeWidth(4);
        
        //é»‘ç™½æ¸?å?˜å?‚æ•°
        mRectColors = new int[]{0xFF000000, mCenterPaint.getColor(), 0xFFFFFFFF};
        mRectPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mRectPaint.setStrokeWidth(5);
        rectLeft = -r - mPaint.getStrokeWidth() * 0.5f;
        rectTop = r + mPaint.getStrokeWidth() * 0.5f + 15;
        rectRight = r + mPaint.getStrokeWidth() * 0.5f;
        rectBottom = rectTop + 50;
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		//ç§»åŠ¨ä¸­å¿ƒ
        canvas.translate(mWidth / 2, mHeight / 2 - 50);
        //ç”»ä¸­å¿ƒåœ†
        canvas.drawCircle(0, 0, centerRadius,  mCenterPaint);
        //æ˜¯å?¦æ˜¾ç¤ºä¸­å¿ƒåœ†å¤–çš„å°?åœ†çŽ¯
        if (highlightCenter || highlightCenterLittle) {
            int c = mCenterPaint.getColor();
            mCenterPaint.setStyle(Paint.Style.STROKE);
            if(highlightCenter) {
            	mCenterPaint.setAlpha(0xFF);
            }else if(highlightCenterLittle) {
            	mCenterPaint.setAlpha(0x90);
            }
            canvas.drawCircle(0, 0, 
            		centerRadius + mCenterPaint.getStrokeWidth(),  mCenterPaint);
            
            mCenterPaint.setStyle(Paint.Style.FILL);
            mCenterPaint.setColor(c);
        }
        //ç”»è‰²çŽ¯
        canvas.drawOval(new RectF(-r, -r, r, r), mPaint);
        //ç”»é»‘ç™½æ¸?å?˜å?—
        if(downInCircle) {
        	mRectColors[1] = mCenterPaint.getColor();
        }
        //rectShader = new LinearGradient(rectLeft, 0, rectRight, 0, mRectColors, null, Shader.TileMode.MIRROR);
       // mRectPaint.setShader(rectShader);
        //canvas.drawRect(rectLeft, rectTop, rectRight, rectBottom, mRectPaint);
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		float x = event.getX() - mWidth / 2;
        float y = event.getY() - mHeight / 2 + 50;
        boolean inCircle = inColorCircle(x, y, 
        		r + mPaint.getStrokeWidth() / 2, r - mPaint.getStrokeWidth() / 2);
        boolean inCenter = inCenter(x, y, centerRadius);
        boolean inRect = inRect(x, y);
        
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
            	downInCircle = inCircle;
            	downInRect = inRect;
            	highlightCenter = inCenter;
            case MotionEvent.ACTION_MOVE:
            	if(downInCircle && inCircle) {//downæŒ‰åœ¨æ¸?å?˜è‰²çŽ¯å†…, ä¸”moveä¹Ÿåœ¨æ¸?å?˜è‰²çŽ¯å†…
            		float angle = (float) Math.atan2(y, x);
                    float unit = (float) (angle / (2 * Math.PI));
                    if (unit < 0) {
                        unit += 1;
                    }
               		mCenterPaint.setColor(interpCircleColor(mCircleColors, unit));
               		if(debug) Log.v(TAG, "è‰²çŽ¯å†…, å??æ ‡: " + x + "," + y);
            	}
            	if(debug) Log.v(TAG, "[MOVE] é«˜äº®: " + highlightCenter + "å¾®äº®: " + highlightCenterLittle + " ä¸­å¿ƒ: " + inCenter);
            	if((highlightCenter && inCenter) || (highlightCenterLittle && inCenter)) {//ç‚¹å‡»ä¸­å¿ƒåœ†, å½“å‰?ç§»åŠ¨åœ¨ä¸­å¿ƒåœ†
            		highlightCenter = true;
            		highlightCenterLittle = false;
            	} else if(highlightCenter || highlightCenterLittle) {//ç‚¹å‡»åœ¨ä¸­å¿ƒåœ†, å½“å‰?ç§»å‡ºä¸­å¿ƒåœ†
            		highlightCenter = false;
            		highlightCenterLittle = true;
            	} else {
            		highlightCenter = false;
            		highlightCenterLittle = false;
            	}
               	invalidate();
            	break;
            case MotionEvent.ACTION_UP:
            	if(highlightCenter && inCenter) {//ç‚¹å‡»åœ¨ä¸­å¿ƒåœ†, ä¸”å½“å‰?å?¯åŠ¨åœ¨ä¸­å¿ƒåœ†
            		if(mListener != null) {
            			mListener.colorChanged(mCenterPaint.getColor());
            		}
            	}
            	if(downInCircle) {
            		downInCircle = false;
            	}
            	if(downInRect) {
            		downInRect = false;
            	}
            	if(highlightCenter) {
            		highlightCenter = false;
            	}
            	if(highlightCenterLittle) {
            		highlightCenterLittle = false;
            	}
            	invalidate();
                break;
        }
        return true;
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(mWidth, mHeight);
	}

	/**
	 * å??æ ‡æ˜¯å?¦åœ¨è‰²çŽ¯ä¸Š
	 * @param x å??æ ‡
	 * @param y å??æ ‡
	 * @param outRadius è‰²çŽ¯å¤–å?Šå¾„
	 * @param inRadius è‰²çŽ¯å†…å?Šå¾„
	 * @return
	 */
	private boolean inColorCircle(float x, float y, float outRadius, float inRadius) {
		double outCircle = Math.PI * outRadius * outRadius;
		double inCircle = Math.PI * inRadius * inRadius;
		double fingerCircle = Math.PI * (x * x + y * y);
		if(fingerCircle < outCircle && fingerCircle > inCircle) {
			return true;
		}else {
			return false;
		}
	}
	
	/**
	 * å??æ ‡æ˜¯å?¦åœ¨ä¸­å¿ƒåœ†ä¸Š
	 * @param x å??æ ‡
	 * @param y å??æ ‡
	 * @param centerRadius åœ†å?Šå¾„
	 * @return
	 */
	private boolean inCenter(float x, float y, float centerRadius) {
		double centerCircle = Math.PI * centerRadius * centerRadius;
		double fingerCircle = Math.PI * (x * x + y * y);
		if(fingerCircle < centerCircle) {
			return true;
		}else {
			return false;
		}
	}
	
	/**
	 * å??æ ‡æ˜¯å?¦åœ¨æ¸?å?˜è‰²ä¸­
	 * @param x
	 * @param y
	 * @return
	 */
	private boolean inRect(float x, float y) {
		if( x <= rectRight && x >=rectLeft && y <= rectBottom && y >=rectTop) {
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * èŽ·å?–åœ†çŽ¯ä¸Šé¢œè‰²
	 * @param colors
	 * @param unit
	 * @return
	 */
	private int interpCircleColor(int colors[], float unit) {
        if (unit <= 0) {
            return colors[0];
        }
        if (unit >= 1) {
            return colors[colors.length - 1];
        }
        
        float p = unit * (colors.length - 1);
        int i = (int)p;
        p -= i;

        // now p is just the fractional part [0...1) and i is the index
        int c0 = colors[i];
        int c1 = colors[i+1];
        int a = ave(Color.alpha(c0), Color.alpha(c1), p);
        int r = ave(Color.red(c0), Color.red(c1), p);
        int g = ave(Color.green(c0), Color.green(c1), p);
        int b = ave(Color.blue(c0), Color.blue(c1), p);
        int wheel_val=1;
        if((r<256)&&(g<256)&&(b<256))
        {
	        if(b==255)
	        {
	        	if(g==0)
	        	{
	        		wheel_val=27-r/25;
	        	}
	        	else
	        	{
	        		wheel_val=27+g/25;
	        	}
	        }
	        else if(g==255)
	        {
	        	if(r==0)
	        	{
	        		wheel_val=48-b/25;
	        	}
	        	else
	        	{
	        		wheel_val=48+r/25;
	        	}
	        }
	        else if(r==255)
	        {
	        	if(g==0)
	        	{
	        		wheel_val=6+b/25;
	        	}
	        	else
	        	{
	        		wheel_val=69-g/25;
	        		if(wheel_val>63)
	        		{
	        			wheel_val=wheel_val-63;
	        		}
	        	}
	        }
        }
        wheel_val=2*wheel_val+1;
       // if(MainActivity.senddelay_flag==0)
        {
        	ColorPickerActivity.wheel_value=(byte)wheel_val;
        	MainActivity.senddelay_cout=0;
        	MainActivity.senddelay_flag=1;
        	ColorPickerActivity.sendmsg(2);
        }
        ColorPickerActivity.rgbr = r;
		ColorPickerActivity.rgbg = g;
		ColorPickerActivity.rgbb = b;
        return Color.argb(a, r, g, b);
    }
	
	/**
	 * èŽ·å?–æ¸?å?˜å?—ä¸Šé¢œè‰²
	 * @param colors
	 * @param x
	 * @return
	 */
	private int interpRectColor(int colors[], float x) {
		int a, r, g, b, c0, c1;
    	float p;
    	if (x < 0) {
    		c0 = colors[0]; 
    		c1 = colors[1];
    		p = (x + rectRight) / rectRight;
    	} else {
    		c0 = colors[1];
    		c1 = colors[2];
    		p = x / rectRight;
    	}
    	a = ave(Color.alpha(c0), Color.alpha(c1), p);
    	r = ave(Color.red(c0), Color.red(c1), p);
    	g = ave(Color.green(c0), Color.green(c1), p);
    	b = ave(Color.blue(c0), Color.blue(c1), p);
    	return Color.argb(a, r, g, b);
	}
	
	private int ave(int s, int d, float p) {
        return s + Math.round(p * (d - s));
    }


/**
 * å›žè°ƒæŽ¥å?£
 * @author <a href="clarkamx@gmail.com">LynK</a>
 * 
 * Create on 2012-1-6 ä¸Šå?ˆ8:21:05
 *
 */
public interface OnColorChangedListener {
	/**
	 * å›žè°ƒå‡½æ•°
	 * @param color é€‰ä¸­çš„é¢œè‰²
	 */
    void colorChanged(int color);
}

public String getTitle() {
	return title;
}

public void setTitle(String title) {
	this.title = title;
}

public int getmInitialColor() {
	return mInitialColor;
}

public void setmInitialColor(int mInitialColor) {
	this.mInitialColor = mInitialColor;
}

public OnColorChangedListener getmListener() {
	return mListener;
}

public void setmListener(OnColorChangedListener mListener) {
	this.mListener = mListener;
}
}