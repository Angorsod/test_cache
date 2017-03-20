/**
 * 
 */
package com.wiley.testcache;

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

//	/**
//	 * Test method for {@link com.wiley.testcache.TwoLevetObjectCacheImpl#setStrategy(byte, com.wiley.testcache.ObjectsCache.CacheStrategy)}.
//	 */
//	public void testSetStrategy() {
//		fail("Not yet implemented");
//	}
//
//	/**
//	 * Test method for {@link com.wiley.testcache.TwoLevetObjectCacheImpl#getStrategy(byte)}.
//	 */
//	public void testGetStrategy() {
//		fail("Not yet implemented");
//	}
//
//	/**
//	 * Test method for {@link com.wiley.testcache.TwoLevetObjectCacheImpl#setMaxSize(byte, int)}.
//	 */
//	public void testSetMaxSize() {
//		fail("Not yet implemented");
//	}
//
//	/**
//	 * Test method for {@link com.wiley.testcache.TwoLevetObjectCacheImpl#getMaxSize(byte)}.
//	 */
//	public void testGetMaxSize() {
//		fail("Not yet implemented");
//	}

}
