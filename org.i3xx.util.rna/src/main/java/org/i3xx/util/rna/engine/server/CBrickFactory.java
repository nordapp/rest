package org.i3xx.util.rna.engine.server;

import java.lang.reflect.Constructor;

import org.i3xx.util.rna.core.IBrick;
import org.i3xx.util.rna.core.IllegalParameterException;


public class CBrickFactory {

	public static final int VERIFY_ON = 1;
	public static final int VERIFY_OFF = 0;

	private static CBrickFactory m_Element;

	private CBrickFactory(){
		m_Element = null;
	}

	/**
	 *	Erstell-Konstruktor (Singleton)
	 */
	public static CBrickFactory Element(){
		if(m_Element==null)
			m_Element = new CBrickFactory();
		return m_Element;
	}

	/**
	 *	Erstellt die entsprechende Klasse, �bergeben wird der Klassenschl�ssel
	 *	(alle Parameter werden �ber den Schl�ssel aus class.conf entnommen)
	 *
	 * @see CBrickFactory: factory(name, classname, id)
	 */
	public IBrick factory(String classname, Long id) throws Exception {

		String name="generic";
		//Fehlerpr�fung
		if(classname.equals(""))
			throw new IllegalParameterException("Key for class ist empty");
		//Knoten erstellen
		return factory(name, classname, id);
	}

	/**
	 *	Erstellt die entsprechende Klasse
	 *
     * @param id The id of the object.
     *           IBrick.ID_CREATEID veranlasst dir Erstellung einer neuen Object-ID,
     *           IBrick.ID_TEMPNODE setzt eine tempor�re Object-ID (immer 1),
     *           alle anderen ID's werden in das Object geschrieben (transient: 2e16 <= x < 2e31,
     *           persistent: x <= -2e31 oder x >= 2e31).
     * @param classname The classname (type) of the object.
     * @param name The name or description of the object.           
	 * @return The new Brick.
	 * @throws Exception
	 */
	public IBrick factory(String name, String classname, Long id) throws Exception {

		Class<?> cClass=null;
		Constructor<?> cConstructor=null;
		IBrick cBrick=null;

		//Fehlerpr�fung
		if(classname.equals(""))
			throw new IllegalParameterException("Key for class ist empty");
		if(name.equals(""))
			throw new IllegalParameterException("Name is empty");

		//Knoten erstellen
		cClass = Class.forName(IBrick.createdef);
		cConstructor = cClass.getConstructor(new Class[]{String.class, String.class, Long.class});
		cBrick=(IBrick)cConstructor.newInstance(new Object[]{name, classname, id});
		
		return cBrick;
	}
}
