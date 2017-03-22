package com.wiley.testcache;

import com.wiley.testcache.ObjectsCache.CacheStrategy;

import junit.framework.TestCase;

public class ObjectsCacheFileTest extends TestCase {

	private ObjectsCacheFileImpl<Long, CachedObject> fileCache;

	public ObjectsCacheFileTest(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();
		fileCache = new ObjectsCacheFileImpl<>(1000, CacheStrategy.LRU);
	}

	protected void tearDown() throws Exception {
		super.tearDown();
		fileCache.close();
	}

	public void testGet() {
		CachedObject testObj = new CachedObject(1, "Alex", 44);
		assertNull(fileCache.get(1L));
		fileCache.put(1L, testObj);
		assertEquals(fileCache.get(1L), testObj);
		fileCache.clear();
	}

	public void testPut() {
		CachedObject testObj = new CachedObject(1, "Alex", 44);
		assertNull(fileCache.get(1L));
		fileCache.put(1L, testObj);
		assertEquals(fileCache.get(1L), testObj);
		fileCache.clear();
	}

	public void testInvalidate() {
		CachedObject testObj = new CachedObject(1, "Alex", 44);
		assertNull(fileCache.get(1L));
		fileCache.put(1L, testObj);
		assertEquals(fileCache.get(1L), testObj);
		fileCache.invalidate(1L);
		assertNull(fileCache.get(1L));
		fileCache.clear();
	}

//	public void testClear() {
//		fail("Not yet implemented");
//	}
//
//	public void testGetCount() {
//		fail("Not yet implemented");
//	}
//
//	public void testSetStrategy() {
//		fail("Not yet implemented");
//	}
//
//	public void testSetMaxSize() {
//		fail("Not yet implemented");
//	}

}
