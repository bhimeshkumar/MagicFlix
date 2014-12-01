package com.magikflix.kurio.builders;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import com.magikflix.kurio.utils.MLogger;

/**
 * @author Bhimesh
 * 
 */
public class CommonXmlBuilder {

	public <T> List<T> getEntityForXml(String xmlDoc, Class<T> entity) {
		List<T> results = new ArrayList<T>();
		Serializer xmlSerializer = new Persister();
		try {
			T result = xmlSerializer.read(entity, xmlDoc);
			if (result != null) results.add(result);
			return results;
		}
		catch (Exception e) {
			MLogger.logError(MLogger.LOG_TAG, String.format("%s  ", this.getClass().getName()), e);
		}

		return null;
	}

	public <T> String getXmlForEntity(IXmlEntity<T> entity) {
		Serializer xmlSerializer = new Persister();
		StringWriter stringWriter = new StringWriter();
		try {
			xmlSerializer.write(entity, stringWriter);
			return stringWriter.toString();
		}
		catch (Exception e) {
			MLogger.logError(MLogger.LOG_TAG, String.format("%s  ", this.getClass().getName()), e);
		}
		return null;
	}
}
