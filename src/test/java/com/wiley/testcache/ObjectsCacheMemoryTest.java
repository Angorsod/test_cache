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

	private ObjectsCacheMemoryImpl<Long, CachedObject> memCache;

	public ObjectsCacheMemoryTest(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();
		memCache = new ObjectsCacheMemoryImpl<>(null, 10, CacheStrategy.LRU);
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	/**
	 * Test method for {@link com.wiley.testcache.ObjectsCacheMemoryImpl#get(long)}.
	 */
	public void testGet() {
		// ObjectsCache memCache = ObjectsCacheMemoryImpl.getInstance();
		CachedObject testObj = new CachedObject(1, "Alex", 44);
		assertNull(memCache.get(1L));
		memCache.put(1L, testObj);
		assertEquals(memCache.get(1L), testObj);
		memCache.clear();
	}

	/**
	 * Test method for {@link com.wiley.testcache.ObjectsCacheMemoryImpl#put(long, java.lang.Object)}.
	 */
	public void testPut() {
		// ObjectsCache memCache = ObjectsCacheMemoryImpl.getInstance();
		CachedObject testObj = new CachedObject(1, "Alex", 44);
		assertNull(memCache.get(1L));
		memCache.put(1L, testObj);
		assertEquals(memCache.get(1L), testObj);
		memCache.clear();
	}

	/**
	 * Test method for {@link com.wiley.testcache.ObjectsCacheMemoryImpl#invalidate(long)}.
	 */
	public void testInvalidate() {
		// ObjectsCache memCache = ObjectsCacheMemoryImpl.getInstance();
		CachedObject testObj = new CachedObject(1, "Alex", 44);
		assertNull(memCache.get(1L));
		memCache.put(1L, testObj);
		assertEquals(memCache.get(1L), testObj);
		memCache.invalidate(1L);
		assertNull(memCache.get(1L));
		memCache.clear();
	}


	/**
	 * Test method for {@link com.wiley.testcache.TwoLevetObjectCacheImpl#setMaxSize(byte, int)}.
	 */
	public void testSetMaxSize() {
		int i;
		CachedObject testObj;
		memCache.setMaxSize(10);
		memCache.setStrategy(CacheStrategy.LRU);
		Random rng = new Random();
		// fill the cache
		for (i = 0; i < memCache.getMaxSize(); i++) {
			int id = rng.nextInt(100);
			testObj = new CachedObject(id, "Alex" + id, 44);
			memCache.put((long)id, testObj);
			assertEquals(memCache.get((long)id), testObj);
		}
		// random read 100 times
		for (i = 0; i < 100; i++) {
			// int id = rng.nextInt(memCache.getMaxSize());
			int id = rng.nextInt(100);
			try {
				Thread.sleep(2);
			} catch (Exception e) {}
			memCache.get((long)id);
			// assertNotNull(memCache.get(id));
		}
		for (i = memCache.getMaxSize() / 2 - 1; i >= 0; i--) {
			// int id = i + memCache.getMaxSize();
			int id = rng.nextInt(100);
			testObj = new CachedObject(id, "Alex" + id, 44);
			memCache.put((long)id, testObj);
			assertEquals(memCache.get((long)id), testObj);
		}
		for (i = 0; i < 100; i++) {
			int id = rng.nextInt(100);
			try {
				Thread.sleep(2);
			} catch (Exception e) {}
			memCache.get((long)id);
		}
		for (i = 0; i < 3; i++) {
			int id = rng.nextInt(100);
			testObj = new CachedObject(id, "Alex" + id, 44);
			memCache.put((long)id, testObj);
			assertEquals(memCache.get((long)id), testObj);
		}
		((ObjectsCacheMemoryImpl<Long, CachedObject>)memCache).printAllCached();
		assertEquals(memCache.getMaxSize(), memCache.getCount());
		memCache.clear();
	}

//	/**
//	 * Test method for {@link com.wiley.testcache.TwoLevetObjectCacheImpl#setStrategy(byte, com.wiley.testcache.ObjectsCache.CacheStrategy)}.
//	 */
//	public void testSetStrategy() {
//		fail("Not yet implemented");
//	}

}
