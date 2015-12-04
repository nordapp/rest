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

public class AppState {
	
	/**
	 * Parses the collection info
	 * 
	 * @param json
	 * @return
	 */
	public static State parse(String json) {
		
		GsonBuilder gsonBuilder = new GsonBuilder();
		gsonBuilder.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES);
		gsonBuilder.registerTypeAdapter(State.class, new StateDeserializer());
		Gson gson = gsonBuilder.create();
		State state = gson.fromJson(json, State.class);
		return state;
	}
	
	/**
	 * The collection info
	 * @author Stefan
	 *
	 */
	public static class State {
		public int state = 0;
	}/*CLASS*/
	
	/**
	 * The JSON Deserializer
	 * @author Stefan
	 *
	 */
	private static class StateDeserializer implements JsonDeserializer<State> {

		@Override
		public State deserialize(JsonElement json, Type type,
				JsonDeserializationContext context) throws JsonParseException {
			
			JsonObject jobj = (JsonObject)json;
			
			State state = new State();
			state.state = jobj.get("app-state").getAsInt();
			
			return state;
		}
		
	}/*CLASS*/

}
