/**
 * 
 */
package com.wiley.testcache;

import java.util.Random;

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
		// fill the cache
		for (i = 0; i < objCache.getMaxSize(); i++) {
			testObj = new CachedObject(i, "Alex" + i, 44);
			objCache.put(i, testObj);
			assertEquals(objCache.get(i), testObj);
		}
		// random read 100 times
		Random rng = new Random();
		for (i = 0; i < 100; i++) {
			int id = rng.nextInt(objCache.getMaxSize());
			try {
				Thread.sleep(2);
			} catch (Exception e) {}
			assertNotNull(objCache.get(id));
		}
		for (i = objCache.getMaxSize() / 2 - 1; i >= 0; i--) {
			int id = i + objCache.getMaxSize();
			testObj = new CachedObject(id, "Alex" + id, 44);
			objCache.put(id, testObj);
			assertEquals(objCache.get(id), testObj);
		}
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
