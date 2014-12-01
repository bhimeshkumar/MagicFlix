package com.magikflix.kurio.app.api.results;

public class VideoResult {
	
	public AppConfig appConfig;
	public AppData appData;
	public UserParent userParent;
	public Playlists[] playlists;
	public Videos[] videos;
	
	public String name;
	public Categories[] categories;
	public String[] ProductIDs;
	public Favorites[] Favorites;
	public String category;
	public boolean isInAppActive;
	public boolean isSubscribed;
	public int data_version;
	public int balance;
}
