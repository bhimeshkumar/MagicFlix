package com.magicflix.goog.app.api.results;

import java.io.Serializable;

public class Videos  implements Serializable{
	
	/**
	 * 
	 */
	
	
	private static final long serialVersionUID = -3371978325534565535L;
	public String videoId;
	public String title;
	public String source;
	public String thumbnailUrl;
	public String streamUrl;
	public String isDownloadable;
	public int duration;
	public int order;
	public String category;
	public Float score;
	
//	public String id ;
//	public String tl ;
//	public String ct ;
//	public String tb;
//	public String src ;
//	public boolean isFeatured;
//	public boolean isShowCased;
//	public boolean isNew;
//	public int key;
//	public int cat_order_id;
//	public int min_age;
//	public int max_age;
//	public int order_id;
//	public int cc;
	public boolean isRecent;
	public boolean isFavorite;
}
