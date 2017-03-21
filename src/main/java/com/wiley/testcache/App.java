package com.wiley.testcache;

import java.util.Random;

import com.wiley.testcache.ObjectsCache.CacheStrategy;

/**
 * Hello world!
 *
 */
public class App {
	
    public static void main( String[] args ) {
		int i;
		CachedObject testObj;
		ObjectsCache<Long, CachedObject> objCache = new ObjectsCacheFileImpl<>(100, CacheStrategy.LFU);
//		objCache.setMaxSize(10);
//		objCache.setStrategy(CacheStrategy.LRU);
		Random rng = new Random();
		// fill the cache
		for (i = 0; i < objCache.getMaxSize(); i++) {
			int id = i; // rng.nextInt(1000);
			testObj = new CachedObject(id, "Alex" + id, 44);
			objCache.put((long)id, testObj);
		}
		// random read 1000 times
		for (i = 0; i < 10; i++) {
			int id = rng.nextInt(100);
//			try {
//				Thread.sleep(2);
//			} catch (Exception e) {}
			testObj = objCache.get((long)id);
			System.out.println(testObj == null ? "id = " + id : testObj.toString());
		}
//		for (i = objCache.getMaxSize() / 2 - 1; i >= 0; i--) {
//			int id = rng.nextInt(100);
//			testObj = new CachedObject(id, "Alex" + id, 44);
//			objCache.put((long)id, testObj);
//		}
//		for (i = 0; i < 100; i++) {
//			int id = rng.nextInt(100);
//			try {
//				Thread.sleep(2);
//			} catch (Exception e) {}
//			objCache.get((long)id);
//		}
//		for (i = 0; i < 3; i++) {
//			int id = rng.nextInt(100);
//			testObj = new CachedObject(id, "Alex" + id, 44);
//			objCache.put((long)id, testObj);
//		}
//		((ObjectsCacheMemoryImpl<Long, CachedObject>)objCache).printAllCached();
		objCache.clear();
    }
    
}
