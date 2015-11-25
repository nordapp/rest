package org.i3xx.util.rna.core;

public interface IRole {
	
	/* positive Rechte */
	public static final int RIGHT_ALL = 0x0000FFFF; /* Vollzugriff */
	public static final int RIGHT_READ = 0x1; /* Lesen */
	public static final int RIGHT_WRITE = 0x2; /* Schreiben */
	public static final int RIGHT_CREATE = 0x4; /* Erstellen */
	public static final int RIGHT_DELETE = 0x8; /* L�schen */
	public static final int RIGHT_LIST = 0x10; /* Kindelemente lesen */
	public static final int RIGHT_ADD = 0x20; /* Kindelement hinzuf�gen */
	public static final int RIGHT_REMOVE = 0x40; /* Kindelement entfernen */
	public static final int RIGHT_READFIELD = 0x80; /* Feld lesen */
	public static final int RIGHT_WRITEFIELD = 0x100; /* Feld schreiben */
	public static final int RIGHT_EXPORT = 0x200; /* Weitergeben (E-Mail) */
	public static final int RIGHT_SEARCH = 0x400; /* Suchen */
	public static final int RIGHT_QUERY = 0x800; /* Listen abfragen (Cursor) */
	public static final int RIGHT_READRIGHT = 0x1000; /* Berechtigung lesen */
	public static final int RIGHT_CHANGERIGHT = 0x2000; /* Berechtigung �ndern */
	public static final int RIGHT_TAKEOVER = 0x4000; /* Besitzrechte �bernehmen */
	public static final int RIGHT_RECURSION = 0x8000; /* Rechte an Nachfahren �bertragen */
	
	/* negative Rechte */
	public static final int iRIGHT_ALL = 0xFFFF0000; /* Kein Zugriff */
	public static final int iRIGHT_READ = 0x10000; /* Lesen */
	public static final int iRIGHT_WRITE = 0x20000; /* Schreiben */
	public static final int iRIGHT_CREATE = 0x40000; /* Erstellen */
	public static final int iRIGHT_DELETE = 0x80000; /* L�schen */
	public static final int iRIGHT_LIST = 0x100000; /* Kindelemente lesen */
	public static final int iRIGHT_ADD = 0x200000; /* Kindelement hinzuf�gen */
	public static final int iRIGHT_REMOVE = 0x400000; /* Kindelement entfernen */
	public static final int iRIGHT_READFIELD = 0x800000; /* Feld lesen */
	public static final int iRIGHT_WRITEFIELD = 0x1000000; /* Feld schreiben */
	public static final int iRIGHT_EXPORT = 0x2000000; /* Weitergeben (E-Mail) */
	public static final int iRIGHT_SEARCH = 0x4000000; /* Suchen */
	public static final int iRIGHT_QUERY = 0x8000000; /* Listen abfragen (Cursor) */
	public static final int iRIGHT_READRIGHT = 0x10000000; /* Berechtigung lesen */
	public static final int iRIGHT_CHANGERIGHT = 0x20000000; /* Berechtigung �ndern */
	public static final int iRIGHT_TAKEOVER = 0x40000000; /* Besitzrechte �bernehmen */
	public static final int iRIGHT_RECURSION = 0x80000000; /* Rechte an Nachfahren �bertragen */
	
	public static final int NOT_FOUND = 0;
	public static final int ALLOWED = -1;
	public static final int FORBIDDEN = 1;
}
