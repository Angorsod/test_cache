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
		Random rng = new Random();
		
		ObjectsCacheFileImpl<Long, CachedObject> fileCache = new ObjectsCacheFileImpl<>(1000, CacheStrategy.LFU);
		ObjectsCache<Long, CachedObject> cache = new ObjectsCacheMemoryImpl<>(fileCache, 5, CacheStrategy.LFU);
		
		System.out.println("Setting maxSize for in-memory cache.");
		cache.setMaxSize(10);
		System.out.println("Current in-memory cache maxSize: " + cache.getMaxSize());
		System.out.println("Setting in-memory cache strategy to LRU.");
		cache.setStrategy(CacheStrategy.LRU);

		System.out.println("Fill memCache.maxSize elements into cache.");
		for (i = 0; i < cache.getMaxSize(); i++) {
			int id = i; // rng.nextInt(1000);
			testObj = new CachedObject(id, "Alex" + id, 44);
			cache.put((long)id, testObj);
		}

		System.out.println("Random read 100 times.");
		for (i = 0; i < 100; i++) {
			int id = rng.nextInt(10);
			testObj = cache.get((long)id);
		}

		System.out.println("Fill 1000 elements into cache.");
		for (i = cache.getMaxSize(); i < 1000; i++) {
			int id = i; // rng.nextInt(1000);
			testObj = new CachedObject(id, "Alex" + id, 44);
			cache.put((long)id, testObj);
		}

		System.out.println("Random read 50 times.");
		for (i = 0; i < 50; i++) {
			int id = rng.nextInt(2000);
			testObj = cache.get((long)id);
			System.out.println(testObj == null ? "id = " + id : testObj.toString());
		}

		fileCache.clear();
		fileCache.close();
    }
    
}
