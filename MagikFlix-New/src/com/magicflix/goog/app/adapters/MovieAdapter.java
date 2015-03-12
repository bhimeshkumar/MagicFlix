package com.magicflix.goog.app.adapters;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import at.technikum.mti.fancycoverflow.FancyCoverFlow;
import at.technikum.mti.fancycoverflow.FancyCoverFlowAdapter;

import com.magicflix.goog.R;
import com.magicflix.goog.app.api.results.Videos;
import com.magicflix.goog.app.utils.Constants;
import com.magicflix.goog.utils.CompatibilityUtil;
import com.squareup.picasso.Picasso;

public class MovieAdapter extends FancyCoverFlowAdapter{

	private Activity mContext;
	private ArrayList<Videos> mVideosList;

	public MovieAdapter(Activity context,ArrayList<Videos> videosList ) {
		mContext = context;
		mVideosList = videosList;
	}

	@Override
	public int getCount() {
		//return mDocus.length;
		return mVideosList.size(); 
	}

	public Object getItem(int position) {
		
		return mVideosList.get(position);
	}

	@Override
	public long getItemId(int arg0) {
		return 0;
	}

	
	@Override
    public View getCoverFlowItem(int position, View reuseableView, ViewGroup viewGroup) {
		
		  CustomViewGroup customViewGroup = null;

          if (reuseableView != null) {
              customViewGroup = (CustomViewGroup) reuseableView;
          } else {
              customViewGroup = new CustomViewGroup(viewGroup.getContext());
              
              int movieItemWidth = (int) mContext.getResources().getDimension(R.dimen.movieItemWidth);
              
//              if(CompatibilityUtil.isTablet(mContext))
//            	  customViewGroup.setLayoutParams(new FancyCoverFlow.LayoutParams(dpToPx(380), FancyCoverFlow.LayoutParams.MATCH_PARENT));
//              else
            	  customViewGroup.setLayoutParams(new FancyCoverFlow.LayoutParams(dpToPx(movieItemWidth), FancyCoverFlow.LayoutParams.MATCH_PARENT));
          }

          customViewGroup.getDurationTextView().setText(Constants.formatDuration(mVideosList.get(position).duration));
          if(mVideosList.get(position).thumbnailUrl != null)
        	  Picasso.with(mContext).load(mVideosList.get(position).thumbnailUrl).placeholder(mContext.getResources().getDrawable(R.drawable.image_loading_icon)).into(customViewGroup.getImageView());
          else
        	  customViewGroup.getImageView().setBackground(mContext.getResources().getDrawable(R.drawable.image_loading_icon));
//          customViewGroup.getImageView().setImageResource(this.getItem(p));
//          customViewGroup.getTextView().setText(String.format("Item %d", i));

          return customViewGroup;
//		View rowView = reuseableView;
//		 if (rowView == null) {
//		      LayoutInflater inflater = mContext.getLayoutInflater();
//		      rowView = inflater.inflate(R.layout.main_activity_play_list_item, null);
//		      ViewHolder viewHolder = new ViewHolder();
//		      viewHolder.movieThumbnailTV = (ImageView) rowView
//		          .findViewById(R.id.play_list_thumbnail_iv);
//		      rowView.setTag(viewHolder);
//		    }
//		    // fill data
//		    ViewHolder holder = (ViewHolder) rowView.getTag();
//		    Picasso.with(mContext).load(mVideosList.get(position).thumbnailUrl).placeholder(mContext.getResources().getDrawable(R.drawable.image_loading_icon)).into(holder.movieThumbnailTV);
//		return rowView;
		
//		ImageView imageView = null;
//
//        if (reuseableView != null) {
//            imageView = (ImageView) reuseableView;
//        } else {
//            imageView = new ImageView(viewGroup.getContext());
//            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
//            imageView.setLayoutParams(new FancyCoverFlow.LayoutParams(dpToPx(500), dpToPx(200)));
//
//        }
//        imageView.setBackground(mContext.getResources().getDrawable(R.drawable.images_a));
//        Picasso.with(mContext).load(mVideosList.get(position).thumbnailUrl).placeholder(mContext.getResources().getDrawable(R.drawable.image_loading_icon)).into(imageView);
//        return imageView;
		
    }

	
	public int dpToPx(int dp) {
		DisplayMetrics displayMetrics =mContext.getResources().getDisplayMetrics();
		int px = Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));       
		return px;
	}

	public void notifyDataSetChanged() {
		super.notifyDataSetChanged();
		//notifyChanged = true;
	}

	static class ViewHolder {
		public TextView durationTV;
		public ImageView movieThumbnailTV;
	}
	
	 private static class CustomViewGroup extends LinearLayout {

	        // =============================================================================
	        // Child views
	        // =============================================================================

	        private TextView duartionTextView;
	        private ImageView imageView;

//	        private Button button;

	        // =============================================================================
	        // Constructor
	        // =============================================================================

	        private CustomViewGroup(Context context) {
	            super(context);

	            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
			    View  rowView = inflater.inflate(R.layout.movie_list_item, null);
			      ViewHolder viewHolder = new ViewHolder();
			      viewHolder.movieThumbnailTV = (ImageView) rowView
			          .findViewById(R.id.movie_list_thumbnail_iv);
			      viewHolder.durationTV = (TextView) rowView
				          .findViewById(R.id.movie_list_duration_tv);
			      imageView = viewHolder.movieThumbnailTV ;
			      duartionTextView = viewHolder.durationTV;
			      rowView.setTag(viewHolder);
	           

//	            this.textView = new TextView(context);
//	            this.imageView = new ImageView(context);
//	            this.button = new Button(context);

//	            LinearLayout.LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
//	            this.textView.setLayoutParams(layoutParams);
//	            this.imageView.setLayoutParams(layoutParams);
//	            this.button.setLayoutParams(layoutParams);

//	            this.textView.setGravity(Gravity.CENTER);
//
//	            this.imageView.setScaleType(ImageView.ScaleType.FIT_XY);
//	            this.imageView.setAdjustViewBounds(true);

//	            this.button.setText("Goto GitHub");
//	            this.button.setOnClickListener(new OnClickListener() {
//	                @Override
//	                public void onClick(View view) {
//	                    Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("https://davidschreiber.github.com/FancyCoverFlow"));
//	                    view.getContext().startActivity(i);
//	                }
//	            });

//	            this.addView(this.textView);
	            this.addView(rowView);
//	            this.addView(this.button);
	        }

	        // =============================================================================
	        // Getters
	        // =============================================================================

//	        private TextView getTextView() {
//	            return textView;
//	        }

	        private ImageView getImageView() {
	            return imageView;
	        }
	        
	        private TextView getDurationTextView() {
	            return duartionTextView;
	        }
	    }
	 
	 
	 public static int convertPixelsToDp(float px, Context context){
		    Resources resources = context.getResources();
		    DisplayMetrics metrics = resources.getDisplayMetrics();
		    int dp = (int) (px / (metrics.densityDpi / 160f));
		    return dp;
		}


}
