package org.i3xx.util.ramdisk;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.i3xx.util.ramdisk.test.FileList;
import org.i3xx.util.ramdisk.test.Worker;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class FileThreadTest {

	@Before
	public void setUp() throws Exception {
		MountPoint.mount();
	}

	@After
	public void tearDown() throws Exception {
		MountPoint.unmount();
	}

	@Test
	public void testA() {
		
		List<FileList> words = new ArrayList<FileList>();
		
		for(int i=0;i<1;i++) {
			FileList list = new FileList();
			Worker work = new Worker(list, 1);
			Thread t = new Thread(work);
			t.start();
			
			words.add(list);
		}
		
		//wait until all threads have finished their work
		for(;;) {
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {}
			
			int n = 0;
			for(int i=0;i<words.size();i++) {
				n += words.get(i).size();
			}
			
			if(n==0)
				break;
		}//for
		
		Counter counter = new Counter();
		watchA(new File(MountPoint.getRoot()), counter);
		
		//990 nodes + root
		assertEquals( counter.size(), 991);
	}

	@Test
	public void testB() {
		
		List<FileList> words = new ArrayList<FileList>();
		
		for(int i=0;i<1;i++) {
			FileList list = new FileList();
			Worker work = new Worker(list, 1);
			Thread t = new Thread(work);
			t.start();
			
			words.add(list);
		}
		
		//wait until all threads have finished their work
		for(;;) {
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {}
			
			int n = 0;
			for(int i=0;i<words.size();i++) {
				n += words.get(i).size();
			}
			
			if(n==0)
				break;
		}//for
		
		Counter counter = new Counter();
		watchB(new File(MountPoint.getRoot()), counter);
		
		//990 nodes + root
		assertEquals( counter.size(), 991);
	}

	@Test
	public void testC() {
		
		List<FileList> words = new ArrayList<FileList>();
		
		FileList list = new FileList();
		words.add(list);
		
		for(int i=0;i<5;i++) {
			Worker work = new Worker(list, 1);
			Thread t = new Thread(work);
			t.start();
		}
		
		//wait until all threads have finished their work
		for(;;) {
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {}
			
			int n = 0;
			for(int i=0;i<words.size();i++) {
				n += words.get(i).size();
			}
			
			if(n==0)
				break;
		}//for
		
		Counter counter = new Counter();
		watchB(new File(MountPoint.getRoot()), counter);
		
		//990 nodes + root
		assertEquals( counter.size(), 991);
	}
	
	/**
	 * @param file The file to watch
	 * @param counter
	 */
	private void watchA(File file, Counter counter) {
		counter.count(file);
		
		File[] list = file.listFiles();
		if(list==null)
			return;
		
		for(File f : list) {
			watchA(f, counter);
		}
	}
	
	/**
	 * @param file The file to watch
	 * @param counter
	 */
	private void watchB(File file, Counter counter) {
		counter.count(file);
		
		String path = file.getAbsolutePath();
		String[] list = file.list();
		if(list==null)
			return;
		
		for(String s : list) {
			File f = new File(path+"/"+s);
			watchB(f, counter);
		}
	}
	
	private class Counter {
		
		Set<String> counter;
		
		public Counter() {
			counter = new HashSet<String>();
		}
		public int size() {
			return counter.size();
		}
		public void count(File file) {
			if(file.exists())
				counter.add(file.getName());
		}
	}//class

}
