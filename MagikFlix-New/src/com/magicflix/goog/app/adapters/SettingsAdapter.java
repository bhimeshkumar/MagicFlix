package com.magicflix.goog.app.adapters;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.magicflix.goog.R;
import com.magicflix.goog.app.activities.BaseActivity;
import com.magicflix.goog.app.sectionlistview.EntryItem;
import com.magicflix.goog.app.sectionlistview.Item;
import com.magicflix.goog.app.sectionlistview.SectionItem;

public class SettingsAdapter extends ArrayAdapter<Item> {

	private Activity mContext;
	private ArrayList<Item> items;
	private LayoutInflater mLayoutInflater;
	private String mTimerValue;
    private int mSelectedPosition = 1;
	public SettingsAdapter(Activity context,ArrayList<Item> items) {
		super(context,0, items);
		this.mContext = context;
		this.items = items;
		mLayoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}


	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View vrootView = convertView;

		final Item ilistItem = items.get(position);
		if (ilistItem != null) {
			if(ilistItem.isSection()){
				SectionItem sectionItem = (SectionItem)ilistItem;
				vrootView = mLayoutInflater.inflate(R.layout.navigation_drawer_list_heaader, null);

				vrootView.setOnClickListener(null);
				vrootView.setOnLongClickListener(null);
				vrootView.setLongClickable(false);

				final TextView sectionView = (TextView) vrootView.findViewById(R.id.list_item_header_tv);
				sectionView.setText(sectionItem.getTitle());
				sectionView.setTypeface(((BaseActivity)mContext).getMagikFlixFontBold());

			}else{
				EntryItem entryItem = (EntryItem)ilistItem;
				vrootView = mLayoutInflater.inflate(R.layout.settings_list_item, null);
				final TextView title = (TextView)vrootView.findViewById(R.id.settings_list_item_title);
				title.setTypeface(((BaseActivity)mContext).getMagikFlixFontRegular());
				if (title != null) {
					title.setText(entryItem.title);
				}
				 if(position == 1){
					title.setTypeface(Typeface.DEFAULT_BOLD);
					title.setText(mTimerValue);
					title.setTypeface(((BaseActivity)mContext).getMagikFlixFontBold());
				}
				else if(position == 2){
					buildSetLimitTextView(title);
					title.setTypeface(((BaseActivity)mContext).getMagikFlixFontBold());
				}else{
					title.setTypeface(((BaseActivity)mContext).getMagikFlixFontRegular());
				}
				 title.setTextColor(mContext.getResources().getColor(R.color.white));
			}
		}
		
		return vrootView;
	}
	
	private void buildSetLimitTextView(TextView textView){   
	    LinearLayout.LayoutParams paramsExample = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT,1.0f);
	    textView.setGravity(Gravity.CENTER);
//	    textView.setBackgroundResource(R.drawable.btn_age_setter);
	    paramsExample.setMargins(0, 0, 25, 0);
	    textView.setPadding(10, 10, 10, 10);
	    textView.setTextSize(18);
	    textView.setTypeface(Typeface.DEFAULT_BOLD);
	    textView.setLayoutParams(paramsExample);
	}
	
	@Override
	public void notifyDataSetChanged()
	{
		super.notifyDataSetChanged();
	}
	
	public void setTimerValue(String timerValue){
		mTimerValue = timerValue;
	}
	
	
}
