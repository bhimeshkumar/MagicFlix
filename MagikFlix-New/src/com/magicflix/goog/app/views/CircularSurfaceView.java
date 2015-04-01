package com.magicflix.goog.app.views;

import com.magicflix.goog.utils.MLogger;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.view.SurfaceView;

public class CircularSurfaceView   extends SurfaceView{


	public CircularSurfaceView(Context context) {
		super(context);
	}

	public CircularSurfaceView(Context context, AttributeSet attrs) {
		super(context, attrs);		
	}

	public CircularSurfaceView(Context context, AttributeSet attrs, int defStyle) {
		this(context, attrs);		
	}

	@Override
	protected void dispatchDraw(Canvas canvas){         

		super.dispatchDraw(canvas);     
		MLogger.logInfo("SurfaceView", "dispatchDraw");
		Path circ = new Path();
		circ.addCircle(canvas.getWidth() / 2, canvas.getWidth() / 2, canvas.getWidth() / 2, Path.Direction.CW);
		Paint paint = new Paint();
		paint.setAntiAlias(true);
		this.setZOrderOnTop(true);
		paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
		canvas.drawPath(circ, paint);
	}

}
