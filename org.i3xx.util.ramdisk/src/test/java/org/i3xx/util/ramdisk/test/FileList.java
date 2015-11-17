package org.i3xx.util.ramdisk.test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
	
	public FileList() {
		
		//11 * 9 * 10 * 10 * 10 = 990000
		list = new ArrayList<String>();
		
		for(int h=0;h<COLORS.length;h++) {
			for(int i=0;i<THINGS.length;i++){
				for(int j=0;j<NUMBERS.length;j++){
					for(int k=0;k<NUMBERS.length;k++){
						for(int m=0;m<NUMBERS.length;m++){
							for(int n=0;n<NUMBERS.length;n++){
								list.add(COLORS[h]+"_"+THINGS[i]+"_"+NUMBERS[j]+NUMBERS[k]+NUMBERS[m]+NUMBERS[n]);
							}//for
						}//for
					}//for
				}//for
			}//for
		}//for
		
		Collections.shuffle(list);
	}
	
	private FileList(boolean f) {
		list = new ArrayList<String>();
	}
	
	/**
	 * @return
	 */
	public synchronized List<String> copy() {
		List<String> n = new ArrayList<String>();
		n.addAll(list);
		
		return n;
	}
	
	/**
	 * @param num
	 * @return
	 */
	public synchronized FileList[] spread(int num) {
		FileList[] resl = new FileList[num];
		for(int i=0;i<resl.length;i++) {
			resl[i] = new FileList(true);
		}
		
		int n = 0;
		while(n<list.size()) {
			for(int i=0;i<resl.length;i++) {
				resl[i].list.add( list.get(n++) );
				
				if(n==list.size())
					break;
			}//for
		}//while
		
		return resl;
	}
	
	/**
	 * @return
	 */
	public synchronized String getWord() {
		if(list.isEmpty())
			return null;
			
		return list.remove(0);
	}
	
	/**
	 * @return
	 */
	public synchronized int size() {
		return list.size();
	}
	
}
