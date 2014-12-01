/**
 * 
 */
package com.magicflix.goog.builders;

/**
 * @author bhimesh
 *
 */
public interface IJsonBuilder {

	public <T> T getEntityForJson(String json, Class<T> entity);

	public <T> String getJsonForEntity(IJsonEntity<T> entity);
}
