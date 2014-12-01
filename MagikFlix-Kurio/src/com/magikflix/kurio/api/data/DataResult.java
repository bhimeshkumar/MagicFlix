package com.magikflix.kurio.api.data;

import java.util.List;

/**
 * @author bhimesh
 */

public class DataResult<T> {
	public boolean successful;
	public int statusCode;
	public T entity;
	public List<T> entities;

}
