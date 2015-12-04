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
public class IndexInfo {
	
	/**
	 * Parses the collection info from StatisticImpl
	 * {protocol}://{server}/dba/{session}/statistic?type=
	 * 
	 * @param json
	 * @return
	 */
	public static Info[] parse(String json) {
		
		GsonBuilder gsonBuilder = new GsonBuilder();
		gsonBuilder.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES);
		gsonBuilder.registerTypeAdapter(Info.class, new CollectionDeserializer());
		gsonBuilder.registerTypeAdapter(Statistic.class, new StatisticDeserializer());
		Gson gson = gsonBuilder.create();
		Info[] info = gson.fromJson(json, Info[].class);
		return info;
	}
	
	/**
	 * The collection info
	 * @author Stefan
	 *
	 */
	public static class Info {
		public String collection = null;
		public String path = null;
		public String field = null;
		public boolean unique = false;
		public boolean ordered = false;
		public boolean indexed = false;
		public Statistic statistic = null;
	}/*CLASS*/
	
	/**
	 * The statistic info
	 * @author Stefan
	 *
	 */
	public static class Statistic {
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
			info.collection = jobj.get("collection").getAsString();
			info.path = jobj.get("path").getAsString();
			info.field = jobj.get("field").getAsString();
			info.unique = jobj.get("unique").getAsBoolean();
			info.ordered = jobj.get("ordered").getAsBoolean();
			info.indexed = jobj.get("indexed").getAsBoolean();
			
			info.statistic = (Statistic)context.deserialize(jobj.get("statistic").getAsJsonObject(), Statistic.class);
			
			return info;
		}
		
	}/*CLASS*/
	
	/**
	 * The JSON Deserializer
	 * @author Stefan
	 *
	 */
	private static class StatisticDeserializer implements JsonDeserializer<Statistic> {

		@Override
		public Statistic deserialize(JsonElement json, Type type,
				JsonDeserializationContext context) throws JsonParseException {
			
			JsonObject jobj = (JsonObject)json;
			
			Statistic stat = new Statistic();
			stat.size = jobj.get("size").getAsInt();
			stat.cost = jobj.get("cost").getAsInt();
			
			return stat;
		}
		
	}/*CLASS*/
}
