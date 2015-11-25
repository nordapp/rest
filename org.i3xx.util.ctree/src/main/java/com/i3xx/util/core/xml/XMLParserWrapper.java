package com.i3xx.util.core.xml;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

import org.xml.sax.SAXException;
import org.xml.sax.InputSource;
import org.w3c.dom.Document;

public abstract class XMLParserWrapper
{
    public abstract void setXML( String in );
    public abstract void setXML( Reader in);
    public abstract void setXML( InputStream in);
    public abstract void setXML( InputSource is );

    public abstract Document getDocument() throws IOException, SAXException;

    public abstract void process() throws IOException, SAXException;
}
