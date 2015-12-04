package org.i3xx.util.driverapi.client;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * JSON Deserializer
 * 
 * @author Stefan
 *
 */
public class RootsInfo {
	
	/**
	 * Parses the collection info from StatisticImpl
	 * {protocol}://{server}/dba/{session}/statistic?type=roots
	 * 
	 * @param json
	 * @return
	 */
	public static String[] parse(String json) {
		
		GsonBuilder gsonBuilder = new GsonBuilder();
		gsonBuilder.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES);
		Gson gson = gsonBuilder.create();
		String[] list = gson.fromJson(json, String[].class);
		return list;
	}

}
