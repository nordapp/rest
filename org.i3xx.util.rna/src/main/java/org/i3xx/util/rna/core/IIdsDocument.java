package org.i3xx.util.rna.core;
import java.util.NoSuchElementException;

public interface IIdsDocument extends Cloneable
{
	public static final String classdef = "org.i3xx.util.rna.impl.IdsDocument";
	
    long ID();
    void ID(long id);
    Object clone() throws CloneNotSupportedException;
    void delete();
    int createVersion();
    void destroy(int i) throws NoSuchElementException;
    int last();
    int length(int i) throws NoSuchElementException;
    long TS(int i) throws NoSuchElementException;
    String mimetype(int i) throws NoSuchElementException;
    void mimetype(String m, int i) throws NoSuchElementException;
    IIdsStore get(int i) throws NoSuchElementException;
}
