/**
 * 
 */
package com.magikflix.kurio.api.data;


/**
 * 
 * Add this interface to a concrete implementation of {@code AbstractBuilder} so
 * that the builder can be invoked by {@code DataApiAsyncTask}
 * 
 * @author bhimesh
 * 
 */
public interface IDataRequestDelegate {
	/**
	 * Classes implementing this are typically Builder Classes
	 */
	
	public <T> DataResult<T> execute(AbstractDataRequest dataRequest);
	

}
