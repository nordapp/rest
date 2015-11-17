package org.i3xx.util.ramdisk;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.i3xx.util.ramdisk.test.FileList;
import org.i3xx.util.ramdisk.test.Worker;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
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

	/**
	 * One thread work, test result by file list
	 * 
	 * listFiles()
	 */
	@Ignore
	public void testA() {
		
		List<FileList> words = new ArrayList<FileList>();
		
		for(int i=0;i<1;i++) {
			FileList list = new FileList();
			Worker work = new Worker(list, 1, 0);
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

	/**
	 * One thread work, test result by path list.
	 * 
	 * Note: A sleep needs 0.7ms minimum to awake.
	 * 
	 * list()
	 */
	@Ignore
	public void testB() {
		
		List<FileList> words = new ArrayList<FileList>();
		
		for(int i=0;i<1;i++) {
			FileList list = new FileList();
			Worker work = new Worker(list, 0, 700);
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
		List<Worker> workers = new ArrayList<Worker>();
		
		FileList list = new FileList();
		List<String> ref = list.copy();
		FileList[] sprList = list.spread(40);
		
		for(int i=0;i<sprList.length;i++) {
			words.add(sprList[i]);
		}
		
		long start = System.currentTimeMillis();
		
		int k=0;
		for(int i=0;i<40;i++) {
			k=k<3?k+1:0;
			Worker work = new Worker(sprList[i], 0, 0);
			workers.add(work);
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
			
			if(n==0) {
				break;
			}
		}//for
		
		long end = System.currentTimeMillis();
		System.out.println(end-start+" - "+ref.size());
		
		//Assume the list is empty
		//assertEquals(list.size(), 0);
		
		//Assume each worker has finished
		for(Worker w : workers) {
			assertFalse( w.getCont() );
		}
		
		Counter counter = new Counter();
		watchB(new File(MountPoint.getRoot()), counter);
		
		//990 nodes + root
		assertEquals( counter.size(), ref.size()+1);
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
			else
				System.err.println("Counter is missing ::>"+file.getAbsolutePath());
		}
	}//class

}
