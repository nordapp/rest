package org.i3xx.util.ramdisk.test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class FileList {
	
	public static final String[] COLORS = new String[]{
		"white", "gray", "yellow", "pink", "red", "green", "blue", "brown", "orange", "violet", "black"
	};
	
	public static final String[] THINGS = new String[]{
		"ball", "house", "star", "door", "car", "bicycle", "table", "chair", "disc"
	};
	
	public static final String[] NUMBERS = new String[]{
		"one", "two", "three", "four", "five", "six", "seven", "eight", "nine", "zero"
	};
	
	private final List<String> list;
	
	private final Random rand;
	
	public FileList() {
		
		//11 * 9 * 10 = 990
		list = new ArrayList<String>();
		rand = new Random();
		
		for(int h=0;h<COLORS.length;h++) {
			for(int i=0;i<THINGS.length;i++){
				for(int j=0;j<NUMBERS.length;j++){
					list.add(COLORS[h]+"_"+THINGS[i]+NUMBERS[j]);
				}//for
			}//for
		}//for
		
	}
	
	/**
	 * @return
	 */
	public synchronized String getWord() {
		if(list.isEmpty())
			return null;
		
		int r = rand.nextInt(list.size());
		return list.remove(r);
	}
	
	/**
	 * @return
	 */
	public synchronized int size() {
		return list.size();
	}
	
}
