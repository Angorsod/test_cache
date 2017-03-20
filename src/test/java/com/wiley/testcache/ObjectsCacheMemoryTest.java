/**
 * 
 */
package com.wiley.testcache;

import java.util.Random;

import com.wiley.testcache.ObjectsCache.CacheStrategy;

import junit.framework.TestCase;

/**
 * @author angor
 *
 */
public class ObjectsCacheMemoryTest extends TestCase {

	/**
	 * Test method for {@link com.wiley.testcache.ObjectsCacheMemoryImpl#get(long)}.
	 */
	public void testGet() {
		// ObjectsCache objCache = ObjectsCacheMemoryImpl.getInstance();
		ObjectsCache objCache = new ObjectsCacheMemoryImpl();
		CachedObject testObj = new CachedObject(1, "Alex", 44);
		assertNull(objCache.get(1));
		objCache.put(1, testObj);
		assertEquals(objCache.get(1), testObj);
		objCache.clear();
	}

	/**
	 * Test method for {@link com.wiley.testcache.ObjectsCacheMemoryImpl#put(long, java.lang.Object)}.
	 */
	public void testPut() {
		// ObjectsCache objCache = ObjectsCacheMemoryImpl.getInstance();
		ObjectsCache objCache = new ObjectsCacheMemoryImpl();
		CachedObject testObj = new CachedObject(1, "Alex", 44);
		assertNull(objCache.get(1));
		objCache.put(1, testObj);
		assertEquals(objCache.get(1), testObj);
		objCache.clear();
	}

	/**
	 * Test method for {@link com.wiley.testcache.ObjectsCacheMemoryImpl#invalidate(long)}.
	 */
	public void testInvalidate() {
		// ObjectsCache objCache = ObjectsCacheMemoryImpl.getInstance();
		ObjectsCache objCache = new ObjectsCacheMemoryImpl();
		CachedObject testObj = new CachedObject(1, "Alex", 44);
		assertNull(objCache.get(1));
		objCache.put(1, testObj);
		assertEquals(objCache.get(1), testObj);
		objCache.invalidate(1);
		assertNull(objCache.get(1));
		objCache.clear();
	}


	/**
	 * Test method for {@link com.wiley.testcache.TwoLevetObjectCacheImpl#setMaxSize(byte, int)}.
	 */
	public void testSetMaxSize() {
		int i;
		CachedObject testObj;
		ObjectsCache objCache = new ObjectsCacheMemoryImpl();
		objCache.setMaxSize(10);
		objCache.setStrategy(CacheStrategy.LRU);
		Random rng = new Random();
		// fill the cache
		for (i = 0; i < objCache.getMaxSize(); i++) {
			int id = rng.nextInt(100);
			testObj = new CachedObject(id, "Alex" + id, 44);
			objCache.put(id, testObj);
			assertEquals(objCache.get(id), testObj);
		}
		// random read 100 times
		for (i = 0; i < 100; i++) {
			// int id = rng.nextInt(objCache.getMaxSize());
			int id = rng.nextInt(100);
			try {
				Thread.sleep(2);
			} catch (Exception e) {}
			objCache.get(id);
			// assertNotNull(objCache.get(id));
		}
		for (i = objCache.getMaxSize() / 2 - 1; i >= 0; i--) {
			// int id = i + objCache.getMaxSize();
			int id = rng.nextInt(100);
			testObj = new CachedObject(id, "Alex" + id, 44);
			objCache.put(id, testObj);
			assertEquals(objCache.get(id), testObj);
		}
		for (i = 0; i < 100; i++) {
			int id = rng.nextInt(100);
			try {
				Thread.sleep(2);
			} catch (Exception e) {}
			objCache.get(id);
		}
		for (i = 0; i < 3; i++) {
			int id = rng.nextInt(100);
			testObj = new CachedObject(id, "Alex" + id, 44);
			objCache.put(id, testObj);
			assertEquals(objCache.get(id), testObj);
		}
		((ObjectsCacheMemoryImpl)objCache).printAllCached();
		assertEquals(objCache.getMaxSize(), objCache.getCount());
		objCache.clear();
	}


//	/**
//	 * Test method for {@link com.wiley.testcache.TwoLevetObjectCacheImpl#setStrategy(byte, com.wiley.testcache.ObjectsCache.CacheStrategy)}.
//	 */
//	public void testSetStrategy() {
//		fail("Not yet implemented");
//	}

}
