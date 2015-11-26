package org.i3xx.util.rna.engine;

import java.util.Iterator;

import org.i3xx.util.rna.core.IBrick;

public class DbQuery {
	
	private static IDbQuery[] query = new IDbQuery[1];
	
	public static void setDbQuery(IDbQuery query) {
		DbQuery.query[0] = query;
	}
	
	public static IBrick pick(String stmt) {
		return query[0].pick(stmt);
	}
	
	public static Iterator<IBrick> select(String stmt) {
		return query[0].select(stmt);
	}
	
}
