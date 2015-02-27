package com.magicflix.goog.app.views;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Typeface;
import android.widget.SeekBar;

public class  SeekBarWithText extends SeekBar {

	  private static final int textMargin = 6;
	  private static final int leftPlusRightTextMargins = textMargin + textMargin;
	  private static final int maxFontSize = 18;
	  private static final int minFontSize = 10;

	  protected String overlayText;
	  protected Paint textPaint;

	  public SeekBarWithText(Context context) {
	    super(context);
	    Resources resources = getResources();

	    //Set up drawn text attributes here
	    textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
	    textPaint.setTypeface(Typeface.DEFAULT_BOLD);
	    textPaint.setTextAlign(Align.LEFT);
	  }

	  //This attempts to ensure that the text fits inside your SeekBar on a resize
	  @Override
	  protected void onSizeChanged(int w, int h, int oldw, int oldh) {
	    super.onSizeChanged(w, h, oldw, oldh);
	   // setFontSmallEnoughToFit(w - leftPlusRightTextMargins);
	  }

	  //Finds the largest text size that will fit
//	  protected void setFontSmallEnoughToFit(int width) {
//	    int textSize = maxTextSize;
//	    textPaint.setTextSize(textSize);
//	    while((textPaint.measureText(sampleText) > width) && (textSize > minTextSize)) {
//	      textSize--;
//	      textPaint.setTextSize(textSize);
//	    }
//	  }

	  //Clients use this to change the displayed text
	  public void setOverlayText(String text) {
	    this.overlayText = text;
	    invalidate();
	  }

	  //Draws the text onto the SeekBar
	  @Override
	  protected synchronized void onDraw(Canvas canvas) {
	    //Draw everything else (i.e., the usual SeekBar) first
	    super.onDraw(canvas);

	    //No text, no problem
	    if(overlayText.length() == 0) {
	      return;
	    }

	    canvas.save();

	    //Here are a few parameters that could be useful in calculating where to put the text
	    int width = this.getWidth() - leftPlusRightTextMargins;
	    int height = this.getHeight();

	    //A somewhat fat finger takes up about seven digits of space 
	    // on each side of the thumb; YFMV
	    int fatFingerThumbHangover = (int) textPaint.measureText("1234567");

	    float textWidth = textPaint.measureText(overlayText);

	    int progress = this.getProgress();
	    int maxProgress = this.getMax();
	    double percentProgress = (double) progress / (double) maxProgress;
	    int textHeight = (int) (Math.abs(textPaint.ascent()) + textPaint.descent() + 1);

	    int thumbOffset = this.getThumbOffset();

	    //These are measured from the point textMargin in from the left of the SeekBarWithText view.
	    int middleOfThumbControl = (int) ((double) width * percentProgress); 
	    int spaceToLeftOfFatFinger = middleOfThumbControl - fatFingerThumbHangover;
	    int spaceToRightOfFatFinger = (width - middleOfThumbControl) - fatFingerThumbHangover; 

	    int spaceToLeftOfThumbControl = middleOfThumbControl - thumbOffset;
	    int spaceToRightOfThumbControl = (width - middleOfThumbControl) - thumbOffset; 

	    int bottomPadding = this.getPaddingBottom();
	    int topPadding = this.getPaddingTop();

	    //Here you will use the above and possibly other information to decide where you would 
	    // like to draw the text.  One policy might be to draw it on the extreme right when the thumb
	    // is left of center, and on the extreme left when the thumb is right of center.  These
	    // methods will receive any parameters from the above calculations that you need to
	    // implement your own policy.
	   // x = myMethodToSetXPosition();
	    //y = myMethodToSetYPosition();

	    //Finally, just draw the text on top of the SeekBar
	    canvas.drawText(overlayText, 0, 0, textPaint);

	    canvas.restore();
	  }
	}