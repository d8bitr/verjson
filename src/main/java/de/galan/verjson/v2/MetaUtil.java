package de.galan.verjson.v2;

import com.google.gson.JsonElement;


/**
 * Access to the predefined serialized MetaWrapper fields
 * 
 * @author daniel
 */
public class MetaUtil {

	public static long getVersion(JsonElement element) {
		return element.getAsJsonObject().get(MetaWrapper.ID_VERSION).getAsLong();
	}


	public static void setVersion(JsonElement element, long version) {
		element.getAsJsonObject().addProperty(MetaWrapper.ID_VERSION, version);
	}


	public static String getNamespace(JsonElement element) {
		JsonElement elementNs = element.getAsJsonObject().get(MetaWrapper.ID_NAMESPACE);
		return (elementNs == null) ? null : elementNs.getAsString();
	}


	public static JsonElement getData(JsonElement element) {
		return element.getAsJsonObject().get(MetaWrapper.ID_DATA);
	}

}
