package org.i3xx.util.rna.core;


public interface IProxy {
    
	public static final String classdef = "cproxy";
	
    void setReference(IBrick brick);
    IBrick getReference();
    long lastReference();
    
    byte getByte() throws NumberFormatException;
    short getShort() throws NumberFormatException;
    int getInt() throws NumberFormatException;
    long getLong() throws NumberFormatException;
    float getFloat() throws NumberFormatException;
    double getDouble() throws NumberFormatException;
    String getString();
    
    void setByte(byte value);
    void setShort(short value);
    void setInt(int value);
    void setLong(long value);
    void setFloat(float value);
    void setDouble(double value);
    void setString(String value);
    
    boolean isEqual(byte value);
    boolean isEqual(short value);
    boolean isEqual(int value);
    boolean isEqual(long value);
    boolean isEqual(float value);
    boolean isEqual(double value);
    boolean isEqual(String value);
    
    void setField(String field);
    String getField();
    void addProxy();
    void removeProxy();
    void update();
}
