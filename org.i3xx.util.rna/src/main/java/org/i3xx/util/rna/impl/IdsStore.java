package org.i3xx.util.rna.impl;

import java.util.ArrayList;
import java.util.List;

import org.i3xx.util.rna.core.ConcurrentAccessException;
import org.i3xx.util.rna.core.IIdsStore;

public class IdsStore implements IIdsStore, Cloneable
{
    private boolean _write;             /* read/write-lock */

    private List<byte[]> content;
    private String mimetype;
    private int length;
    private long timestamp;

    public IdsStore()
    {
        _write = true;

        length = 0;
        content = new ArrayList<byte[]>();
        mimetype = "";
        timestamp = System.currentTimeMillis();
    }

    protected IdsStore(long timestamp)
    {
    	this();
    	
        this.timestamp = timestamp;
    }

    public Object clone() throws CloneNotSupportedException
    {
        if ( _write )
        {
            throw new CloneNotSupportedException();
        }

        try {
            IIdsStore store = new IdsStore();
            int i=0;
            for ( i=0; i<content.size(); i++ )
            {
                store.persist(persist(i));
            }
            store.mimetype(mimetype());
            store.fix();

            return store;

        }catch(ConcurrentAccessException e){
            throw new CloneNotSupportedException(e.toString());
        }
    }

    public void delete()
    {
        content.clear();
        length = 0;
    }

    public byte[] persist(int index) throws ConcurrentAccessException
    {
        if ( _write )
        {
            throw new ConcurrentAccessException();
        }

        // Der Kopiervorgang ist notwendig um die Daten korrekt
        // auszulesen.
        byte[] persist = (byte[])content.get(index);
        byte[] cbuf = new byte[persist.length];
        System.arraycopy(persist, 0, cbuf, 0, persist.length);

        return cbuf;
    }

    public void persist(byte[] cbuf) throws ConcurrentAccessException
    {
        if ( ! _write )
        {
            throw new ConcurrentAccessException();
        }

        if (cbuf == null)
        {
            throw new NullPointerException();
        }

        byte[] persist = new byte[cbuf.length];
        System.arraycopy(cbuf, 0, persist, 0, cbuf.length);
        content.add(persist);

        length += cbuf.length;
    }

    public void fix()
    {
    	((ArrayList<byte[]>)content).trimToSize();
        _write = false;
    }

    public void destroy()
    {
        delete();
        _write = true;
    }

    public void length(int value)
    {
        length = value;
    }

    public int length()
    {
        return length;
    }

    public int size()
    {
        return content.size();
    }

    /**
     *  Zeitstempel (timestamp)
     */
    public long TS()
    {
        return timestamp;
    }

    /**
     *  Dokumententyp gem. MIME
     */
    public String mimetype()
    {
        return mimetype;
    }

    /**
     *  Dokumententyp gem. MIME
     */
    public void mimetype(String mimetype)
    {
        this.mimetype = mimetype;
    }

}
