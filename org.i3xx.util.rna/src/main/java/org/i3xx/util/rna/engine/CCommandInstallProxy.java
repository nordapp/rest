package org.i3xx.util.rna.engine;

import org.i3xx.util.rna.core.CCommand;
import org.i3xx.util.rna.core.IBrick;
import org.i3xx.util.rna.core.IProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CCommandInstallProxy extends CCommand
{
	private static final long serialVersionUID = 1L;
	private static final Logger logger = LoggerFactory.getLogger(CCommandInstallProxy.class);
	
	public static final int NO_2ND_ENDPOINT = 1;
	
	private IBrick target; //Zu referenzierender Brick anstelle von stmt
	private IProxy proxy;
	@SuppressWarnings("unused")
	private int version;
	private String feld; // Feld das zu referenzieren ist.
	
	/* Suchbegriffe für die zu referenzierende Klasse.
	Beginnend bei stmts[0] wird der Erste genommen der ein Ergebnis liefert */
	private String[] stmts;
	
	/**
	 * Caution, the policy to create a direct link may change.
	 * This is a proxy link with no 2nd endpoint. There may be
	 * another policy with 2 specified endpoints.
	 * 
	 * @param feld
	 * @param version The version of the link
	 * @param target The target brick
	 * @since 10.12.2009
	 */
	public CCommandInstallProxy(String feld, IBrick target, int version) {
		this( feld, new String[]{} );
		
		this.target = target;
		this.version = version;
	}
	
	/**
	 * @param feld
	 * @param stmt The query to search the target brick
	 */
	public CCommandInstallProxy(String feld, String stmt) {
		this( feld, new String[]{stmt} );
	}
	
	/**
	 * @param feld
	 * @param stmts The queries to search the target brick
	 */
	public CCommandInstallProxy( String feld, String[] stmts ) {
		proxy = null;
		target = null;
		this.feld = feld;
		this.stmts = stmts;
	}

	public void execute(IBrick brick) {
		try {
			if ( stmts != null ) {
				int i=0;
				IBrick b = target;
				while ( ( i < stmts.length ) && ( b == null ) ) {
					b = DbQuery.pick(stmts[i++]);
				}
				
				if(b!=null){
					proxy = b.getProxy(feld);
					String st = (i>0) ? stmts[--i] : "no query (brick)";	
					logger.debug("Install proxy statement:{}, id:{}, field:{}, text:{}", st,b.ID(),((IBrick)proxy).ID(),proxy.getField(),proxy.getString());
				}else{
					String stmt = "";
					for (i=0; i<stmts.length; i++)
						stmt += "|" + stmts[i];
					logger.debug("Install proxy statement:{}, field:{}, text:{}", stmt, feld);
				}
			}else{
				logger.debug("Install proxy field:{}", feld);
			}
		}catch (Exception e){
			logger.debug("Install proxy", e);
		}
	}

	public IProxy getProxy() {
		return proxy;
	}
}
