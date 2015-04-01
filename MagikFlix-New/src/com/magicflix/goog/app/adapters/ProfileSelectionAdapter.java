package com.magicflix.goog.app.adapters;

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

import com.magicflix.goog.MagikFlix;
import com.magicflix.goog.R;
import com.magicflix.goog.app.activities.FamilySetUpActivity;
import com.magicflix.goog.app.api.Model.UserProfile;
import com.magicflix.goog.app.utils.Constants;

public class ProfileSelectionAdapter extends BaseAdapter{

	//private static String TAG = ProfileSelectionAdapter.class.getName();
	private Activity mContext;
	protected Vector<Boolean> selectedStates;
	private List<UserProfile> mUserProfileLsit;

	public ProfileSelectionAdapter(Activity context, List<UserProfile> userProfiles) {
		mUserProfileLsit = userProfiles;
		mContext = context;
		selectedStates = new Vector<Boolean>();
		clearSelectedState();
		selectedStates.set(((MagikFlix)context.getApplication()).getSelectedProfileIndex(), new Boolean(true));
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
			rowView = inflater.inflate(R.layout.profile_selection_list_item, null);
			ViewHolder viewHolder = new ViewHolder();
			viewHolder.mProfileIV = (ImageView) rowView
					.findViewById(R.id.profile_screen_profile_iv);
			viewHolder.mOuterLayout = (RelativeLayout) rowView
					.findViewById(R.id.profile_selection_circle_layout);
			viewHolder.mInnerLayout = (RelativeLayout) rowView
					.findViewById(R.id.profile_selected_circle_layout);
			viewHolder.mProfileNameTV = (TextView) rowView
					.findViewById(R.id.profile_name_tv);
			
			rowView.setTag(viewHolder);
		}

		ViewHolder holder = (ViewHolder) rowView.getTag();

		if(mUserProfileLsit.get(position).avatar != null && mUserProfileLsit.get(position).avatar.length() > 0){
			Bitmap myBitmap = BitmapFactory.decodeFile(mUserProfileLsit.get(position).avatar);
			holder.mProfileIV.setImageBitmap(myBitmap);
		}else{
			holder.mProfileIV.setImageURI(Uri.parse(mUserProfileLsit.get(position).deaultAvatar));
		}
		 UserProfile userProfile = mUserProfileLsit.get(position);
		 if(userProfile.childName != null && userProfile.childName.length() > 0){
			 holder.mProfileNameTV.setText(userProfile.childName);
		 }else{
			 holder.mProfileNameTV.setText("Child Name");
		 }
		
		rowView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				clearSelectedState();
				MagikFlix app = (MagikFlix)mContext.getApplication();
				app.setSelectedProfileIndex(position);
				ProfileSelectionAdapter.this.notifyDataSetChanged();
				Constants.IS_PROFILE_SELECTION_CHANGED = true;
				mContext.finish();
			}
		});


		if(selectedStates.get(position).booleanValue()){
			holder.mInnerLayout.setVisibility(View.VISIBLE);
		}else{
			holder.mInnerLayout.setVisibility(View.GONE);
		}


		return rowView;
	}


	static class ViewHolder {
		public ImageView mProfileIV;
		public RelativeLayout mOuterLayout,mInnerLayout;
		public TextView mProfileNameTV;
	}

	private void clearSelectedState () {
		selectedStates.clear();  
		for (int i = 0 ; i < 30; i++) {
			selectedStates.add(new Boolean(false));
		} 
	}




}
