package com.magikflix.kurio.app.adapters;

import java.util.List;
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

import com.magikflix.kurio.MagikFlix;
import com.magikflix.kurio.R;
import com.magikflix.kurio.app.activities.FamilySetUpActivity;
import com.magikflix.kurio.app.api.Model.UserProfile;

public class ChildProfileAdapter extends BaseAdapter{

	//private static String TAG = CategoryAdapter.class.getName();
	private Activity mContext;
	private String[] mList;
	int m_w, m_h;
	protected Vector<Boolean> selectedStates;
	private int disableCategoryWidthHeight;
	private List<UserProfile> mUserProfileLsit;

	public ChildProfileAdapter(Activity context, List<UserProfile> userProfiles) {
		mUserProfileLsit = userProfiles;
		mContext = context;
		selectedStates = new Vector<Boolean>();
		clearSelectedState();
		selectedStates.set(((MagikFlix)context.getApplication()).getSelectedProfileIndex(), new Boolean(true));
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
			viewHolder.movieThumbnailTV = (ImageView) rowView.findViewById(R.id.child_profile_thumbnail_iv);
			viewHolder.selectedIV = (ImageView) rowView.findViewById(R.id.child_profile_slected_iv);
			viewHolder.category_title_tv = (TextView) rowView.findViewById(R.id.child_profile_title_tv);
			viewHolder.mInnerCircleLayout = (RelativeLayout) rowView.findViewById(R.id.child_profile_inline_layout);
			viewHolder.mCheckMarkIV = (ImageView) rowView.findViewById(R.id.selection_check_mark);
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
		holder.movieThumbnailTV.requestLayout();
		holder.movieThumbnailTV.getLayoutParams().height = disableCategoryWidthHeight;
		holder.movieThumbnailTV.getLayoutParams().width = disableCategoryWidthHeight;
		holder.mInnerCircleLayout .getLayoutParams().height = disableCategoryWidthHeight;
		holder.mInnerCircleLayout .getLayoutParams().width = disableCategoryWidthHeight;
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
