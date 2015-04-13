package com.magikflix.kurio.app.api.requests;

import com.magikflix.kurio.api.data.AbstractDataRequest;

public class FavourireVideoRequest extends AbstractDataRequest{

//	public String[] favs ;
//	public String[] unFavs ;
//	public String userId;
	public String token;
	public String videoId;
	public boolean isFavorite;

}
