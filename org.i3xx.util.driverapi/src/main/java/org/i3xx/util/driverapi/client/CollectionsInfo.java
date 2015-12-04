package org.i3xx.util.driverapi.client;

import java.lang.reflect.Type;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

/**
 * JSON Deserializer
 * 
 * @author Stefan
 *
 */
public class CollectionsInfo {
	
	/**
	 * Parses the collection info from StatisticImpl
	 * {protocol}://{server}/dba/{session}/statistic?type=collections
	 * 
	 * @param json
	 * @return
	 */
	public static Info[] parse(String json) {
		
		GsonBuilder gsonBuilder = new GsonBuilder();
		gsonBuilder.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES);
		gsonBuilder.registerTypeAdapter(Info.class, new CollectionDeserializer());
		Gson gson = gsonBuilder.create();
		Info[] info = gson.fromJson(json, Info[].class);
		return info;
	}
	
	/**
	 * The statistic info
	 * @author Stefan
	 *
	 */
	public static class Info {
		public String type = null;
		public int size = 0;
		public int cost = 0;
	}/*CLASS*/
	
	/**
	 * The JSON Deserializer
	 * @author Stefan
	 *
	 */
	private static class CollectionDeserializer implements JsonDeserializer<Info> {

		@Override
		public Info deserialize(JsonElement json, Type type,
				JsonDeserializationContext context) throws JsonParseException {
			
			JsonObject jobj = (JsonObject)json;
			
			Info info = new Info();
			info.type = jobj.get("type").getAsString();
			info.size = jobj.get("size").getAsInt();
			info.cost = jobj.get("cost").getAsInt();
			
			return info;
		}
		
	}/*CLASS*/

}
