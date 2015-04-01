package com.magicflix.goog.app.adapters;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.localytics.android.LocalyticsAmpSession;
import com.magicflix.goog.MagikFlix;
import com.magicflix.goog.R;
import com.magicflix.goog.app.activities.FamilySetUpActivity;
import com.magicflix.goog.app.api.Model.UserProfile;

public class ChildProfileAdapter extends BaseAdapter{

	private static String TAG = CategoryAdapter.class.getName();
	private Activity mContext;
	private String[] mList;
	int m_w, m_h;
	protected Vector<Boolean> selectedStates;
	private LocalyticsAmpSession mLocalyticsSession;
	private Map<String, String> mCategorySelectorEvent ;
	private int enableCategoryWidthHeight,disableCategoryWidthHeight;
	private List<UserProfile> mUserProfileLsit;

	public ChildProfileAdapter(Activity context, List<UserProfile> userProfiles) {
		mUserProfileLsit = userProfiles;
		mCategorySelectorEvent = new HashMap<String, String>();
		mContext = context;
		mLocalyticsSession = ((MagikFlix)mContext.getApplicationContext()).getLocatyticsSession();
		selectedStates = new Vector<Boolean>();
		clearSelectedState();
		selectedStates.set(((MagikFlix)context.getApplication()).getSelectedProfileIndex(), new Boolean(true));
		enableCategoryWidthHeight = (int) mContext.getResources().getDimension(R.dimen.enabledCategoryItemWidthHeight);
		disableCategoryWidthHeight = (int) mContext.getResources().getDimension(R.dimen.disabledCategoryItemWidthHeight);
	}

	@Override
	public int getCount() {
		return mUserProfileLsit.size(); 
	}

	public Object getItem(int position) {

		return position;
	}

	@Override
	public long getItemId(int arg0) {
		return 0;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		View rowView = convertView;

		if (rowView == null) {
			LayoutInflater inflater = mContext.getLayoutInflater();
			rowView = inflater.inflate(R.layout.child_profile_list_item, null);
			ViewHolder viewHolder = new ViewHolder();
			viewHolder.movieThumbnailTV = (ImageView) rowView
					.findViewById(R.id.child_profile_thumbnail_iv);
			viewHolder.selectedIV = (ImageView) rowView
					.findViewById(R.id.child_profile_slected_iv);
			viewHolder.category_title_tv = (TextView) rowView
					.findViewById(R.id.child_profile_title_tv);

			//			viewHolder.mOuterCircleLayout = (RelativeLayout) rowView
			//					.findViewById(R.id.category_outline_layout);
			viewHolder.mInnerCircleLayout = (RelativeLayout) rowView
					.findViewById(R.id.child_profile_inline_layout);
			viewHolder.mCheckMarkIV = (ImageView) rowView
					.findViewById(R.id.selection_check_mark);
			rowView.setTag(viewHolder);
		}

		ViewHolder holder = (ViewHolder) rowView.getTag();

		if(mUserProfileLsit.get(position).avatar != null && mUserProfileLsit.get(position).avatar.length() > 0){
			Bitmap myBitmap = BitmapFactory.decodeFile(mUserProfileLsit.get(position).avatar);
			holder.movieThumbnailTV.setImageBitmap(myBitmap);
		}else{
			holder.movieThumbnailTV.setImageURI(Uri.parse(mUserProfileLsit.get(position).deaultAvatar));
		}
		rowView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if(((FamilySetUpActivity)mContext).saveUserProfile(false, false)){
					clearSelectedState();
					//				sendLocalyticsForCategory(position);
					selectedStates.set(position, new Boolean(true));
					((FamilySetUpActivity)mContext).updateUI(position);
					ChildProfileAdapter.this.notifyDataSetChanged();
				}
			}
		});


		if(selectedStates.get(position).booleanValue()){
			holder.selectedIV.setVisibility(View.VISIBLE);
			holder.mCheckMarkIV.setVisibility(View.VISIBLE);
		}else{
			holder.selectedIV.setVisibility(View.GONE);
			holder.mCheckMarkIV.setVisibility(View.GONE);
		}


		//		if(mPlayLists[position].thumbnailUrl != null && mPlayLists[position].thumbnailUrl.length() > 0)
		//			Picasso.with(mContext).load(mPlayLists[position].thumbnailUrl).into(holder.movieThumbnailTV);
		//		else
		//			holder.movieThumbnailTV .setImageDrawable(mContext.getResources().getDrawable(R.drawable.icon_outline_bg));
		//
		//
		//		if (selectedStates.get(position)){
		//			holder.movieThumbnailTV.requestLayout();
		//			holder.mInnerCircleLayout.requestLayout();



		//			if(CompatibilityUtil.isTablet(mContext)){
		//				holder.movieThumbnailTV.requestLayout();
		//				holder.movieThumbnailTV.getLayoutParams().height = enableCategoryWidthHeight;
		//				holder.movieThumbnailTV.getLayoutParams().width = enableCategoryWidthHeight;
		//				holder.mInnerCircleLayout .getLayoutParams().height = enableCategoryWidthHeight;
		//				holder.mInnerCircleLayout .getLayoutParams().width = enableCategoryWidthHeight;
		//			}else{
		//			holder.movieThumbnailTV.requestLayout();
		//			holder.movieThumbnailTV.getLayoutParams().height = enableCategoryWidthHeight;
		//			holder.movieThumbnailTV.getLayoutParams().width = enableCategoryWidthHeight;
		//			holder.mInnerCircleLayout .getLayoutParams().height = enableCategoryWidthHeight;
		//			holder.mInnerCircleLayout .getLayoutParams().width = enableCategoryWidthHeight;
		//			//			}
		//			holder.mInnerCircleLayout.setBackground(mContext.getResources().getDrawable(R.drawable.icon_outline_color_selected));
		//		}
		//		else {
		//			holder.mInnerCircleLayout.requestLayout();
		//			if(CompatibilityUtil.isTablet(mContext)){
		//				holder.movieThumbnailTV.requestLayout();
		//				holder.movieThumbnailTV.getLayoutParams().height = dpToPx(80);
		//				holder.movieThumbnailTV.getLayoutParams().width = dpToPx(80);
		//				holder.mInnerCircleLayout .getLayoutParams().height = dpToPx(80);
		//				holder.mInnerCircleLayout .getLayoutParams().width = dpToPx(80);
		//			}else{
		holder.movieThumbnailTV.requestLayout();
		holder.movieThumbnailTV.getLayoutParams().height = disableCategoryWidthHeight;
		holder.movieThumbnailTV.getLayoutParams().width = disableCategoryWidthHeight;
		holder.mInnerCircleLayout .getLayoutParams().height = disableCategoryWidthHeight;
		holder.mInnerCircleLayout .getLayoutParams().width = disableCategoryWidthHeight;
		//			}

		//			holder.mInnerCircleLayout.setBackground(mContext.getResources().getDrawable(R.drawable.icon_outline_color_unselected));
		//		}
		//		holder.category_title_tv.setVisibility(View.GONE);
		//
		//		holder.category_title_tv.setText(mPlayLists[position].name);
		return rowView;
	}

	public int checkPosition(int position) { 
		if (position >= mList.length) { 
			position = position % mList.length; 
		} 
		return position; 
	} 

	static class ViewHolder {
		public TextView category_title_tv;
		public TextView movieNameTV;
		public ImageView movieThumbnailTV, selectedIV;
		public ImageView mCheckMarkIV;
		public RelativeLayout mInnerCircleLayout;
	}

	private void clearSelectedState () {
		selectedStates.clear();  
		for (int i = 0 ; i < 30; i++) {
			selectedStates.add(new Boolean(false));
		} 
	}

}
