package org.i3xx.util.rna.engine.server;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import org.i3xx.util.rna.core.ConcurrentAccessException;
import org.i3xx.util.rna.core.IBrick;
import org.i3xx.util.rna.core.IDocument;
import org.i3xx.util.rna.core.IIdsDocument;


/**
 * Uses a reflection factory to access the package
 * 
 * @author Stefan
 *
 */
public class RNAFactory {

	/**
	 * @param brick
	 * @param timestamp
	 * @param mimetype
	 * @param content
	 * @throws ConcurrentAccessException
	 * @throws ClassNotFoundException
	 * @throws SecurityException
	 * @throws NoSuchMethodException
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public static final void createIdsStore(IBrick brick, long timestamp, String mimetype, List<byte[]> content) throws ConcurrentAccessException, ClassNotFoundException, SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException {
		//com.i3xx.ob.proc.db.ActualPlacement.createIdsStore(brick, timestamp, mimetype, content);
		
		Class<?> fClass = Class.forName("com.i3xx.ob.proc.db.ActualPlacement");
		Class<?>[] paramTypes = new Class[]{IBrick.class, Long.TYPE, String.class, List.class};
		Method method = fClass.getMethod("createIdsStore", paramTypes);
		
		method.invoke(fClass, new Object[]{brick, timestamp, mimetype, content});
	}
	
	/**
	 * @param brick
	 * @param stmt
	 * @throws ClassNotFoundException
	 * @throws SecurityException
	 * @throws NoSuchMethodException
	 * @throws IllegalArgumentException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public static final void setDocument(IBrick brick, String stmt) throws ClassNotFoundException, SecurityException, NoSuchMethodException, IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException {
		IIdsDocument doc = null;
		//doc = new com.i3xx.ob.proc.db.IdsDocumentCPAspect(stmt);
		
		Class<?> cClass = Class.forName("com.i3xx.ob.proc.db.IdsDocumentCPAspect");
		Constructor<?> cConstructor = cClass.getConstructor(new Class[]{String.class});;
		doc = (IIdsDocument)cConstructor.newInstance(new Object[]{stmt});
		
		IDocument document = (IDocument)brick;
		document.setDocument(doc);
	}
	
	/**
	 * @param doc
	 * @return
	 * @throws ClassNotFoundException
	 * @throws SecurityException
	 * @throws NoSuchMethodException
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public static final String referenceToAffiliatedDatabase(IIdsDocument doc) throws ClassNotFoundException, SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException {
		//return com.i3xx.ob.proc.db.ActualPlacement.referenceToAffiliatedDatabase( doc );
		
		Class<?> fClass = Class.forName("com.i3xx.ob.proc.db.ActualPlacement");
		Class<?>[] paramTypes = new Class[]{IIdsDocument.class};
		Method method = fClass.getMethod("referenceToAffiliatedDatabase", paramTypes);
		
		return (String)method.invoke(fClass, new Object[]{doc});
	}
	
}
