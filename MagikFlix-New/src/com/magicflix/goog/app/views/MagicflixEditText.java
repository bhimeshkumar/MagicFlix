package com.magicflix.goog.app.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.EditText;

import com.magicflix.goog.R;

public class MagicflixEditText extends EditText {

	public MagicflixEditText(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(attrs);
	}
	
	public MagicflixEditText(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(attrs);
		
	}
	
	public MagicflixEditText(Context context) {
		super(context);
		init(null);
	}
	
	private void init(AttributeSet attrs) {
		if (attrs!=null) {
			 TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.MflixTextView);
			 String fontName = a.getString(R.styleable.MflixTextView_fontName);
			 if (fontName!=null) {
				 Typeface myTypeface = Typeface.createFromAsset(getContext().getAssets(), fontName);
				 setTypeface(myTypeface);
			 }
			 a.recycle();
		}
	}

}

