/**
 * 
 */
package com.magicflix.goog.api.data;


/**
 * @author bhimesh
 *
 */
public abstract class AbstractDataRequest {
	
	/**
	 * Uses command patterns to execute the code in the class tht implement IdataRequestDeleegate
	 */
	public transient IDataRequestDelegate requestDelegate;
	
	/**
	 * Communicates to the {@link IDataRequestDelegate} the internal private method That should be executed
	 */
	public transient IDataRequestType requestType;
}
