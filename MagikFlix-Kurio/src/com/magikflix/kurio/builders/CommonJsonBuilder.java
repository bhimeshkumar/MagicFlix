package com.magikflix.kurio.builders;


/**
 * @author Bhimesh
 *
 */

import com.google.gson.Gson;
import com.magikflix.kurio.utils.MLogger;

public class CommonJsonBuilder {
	public <T> T getEntityForJson(String json, Class<T> entity) {
		try {
			return new Gson().fromJson(json, entity);
		}
		catch (Exception e) {
			MLogger.logError(MLogger.LOG_TAG, String.format("%s  ", this.getClass().getName()), e);
	
		}
		return null;
	}

	public <T> String getJsonForEntity(IJsonEntity<T> entity) {
		try {
			return new Gson().toJson(entity);
		}
		catch (Exception e) {
			MLogger.logError(MLogger.LOG_TAG, String.format("%s  ", this.getClass().getName()), e);
		}
		return null;
	}

}
